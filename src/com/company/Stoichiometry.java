package com.company;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Stoichiometry implements NestedFor.IAction {
    public ArrayList<Compound> compoundsA;
    public ArrayList<Compound> compoundsB;
    public ArrayList<Compound> allCompound;
    public ArrayList<ArrayList<String>> elementInfo = new ArrayList<>();

    Stoichiometry() {
        //read in the Periodic table from a CSV
        try (BufferedReader br = new BufferedReader(new FileReader("Periodic Table of Elements.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                elementInfo.add(new ArrayList<>(List.of(values)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String compute(String toCompute) {
        StringBuilder sb = new StringBuilder();
        String[] toComputeSides = toCompute.split(">");
        String[] splitToCompoundsA = toComputeSides[0].split("\\+");
        String[] splitToCompoundsB = toComputeSides[1].split("\\+");
        compoundsA = new ArrayList<>();
        compoundsB = new ArrayList<>();
        for (String compound : splitToCompoundsA) {
            compoundsA.add(ChemistryReader.readInMolecules(compound));
        }
        for (String compound : splitToCompoundsB) {
            compoundsB.add(ChemistryReader.readInMolecules(compound));
        }
        //if its already balanced, don't do anything
        if (ChemistryReader.compareElementCounts(compoundsA, compoundsB)) {
            return toCompute;
        }
        Compound sideA = ChemistryReader.combineCompounds(compoundsA);
        Compound sideB = ChemistryReader.combineCompounds(compoundsB);
        allCompound = new ArrayList<>();
        allCompound.addAll(compoundsA);
        allCompound.addAll(compoundsB);
        //dummy checking
        for (String s : sideA.keySet()) {
            if (!sideB.containsKey(s)) {
                throw new RuntimeException(s + " is in sideA, but not in side B");
            }
        }
        for (String s : sideB.keySet()) {
            if (!sideA.containsKey(s)) {
                throw new RuntimeException(s + " is in sideB, but not in side A");
            }
        }
        //check all possible coefficient sets, where each coefficient is greater than 1, and less than 30
        NestedFor nfor = new NestedFor(1, 30, this);
        nfor.nFor(allCompound.size());
        //nicely lay out and build up the string to return
        for (Compound c : compoundsA) {
            if (c.getCoefficient() > 1) {
                sb.append(c.getCoefficient());
                sb.append("*");
            }
            for (String s : c.keySet()) {
                sb.append(s);
                if (c.get(s) / c.getCoefficient() > 1) {
                    sb.append(c.get(s) / c.getCoefficient());
                }
            }
            sb.append("+");
        }
        sb.setCharAt(sb.length() - 1, '>');
        for (Compound c : compoundsB) {
            if (c.getCoefficient() > 1) {
                sb.append(c.getCoefficient());
                sb.append("*");
            }
            for (String s : c.keySet()) {
                sb.append(s);
                if (c.get(s) / c.getCoefficient() > 1) {
                    sb.append(c.get(s) / c.getCoefficient());
                }
            }
            sb.append("+");
        }
        sb.setCharAt(sb.length() - 1, ' ');
        sb.append("\n");
        //get mass ratio between compounds if queried for one
        if (toComputeSides.length >= 3) {
            if(toComputeSides[2].contains("-".subSequence(0,1))) {
                String[] c = toComputeSides[2].split("-");
                double l = ChemistryReader.getMolarMass(allCompound.get(Integer.parseInt(c[0])), elementInfo)
                        / ChemistryReader.getMolarMass(allCompound.get(Integer.parseInt(c[1])), elementInfo);
                System.out.println("Ratio of " + allCompound.get(Integer.parseInt(c[0])).inputString + " to " +
                        allCompound.get(Integer.parseInt(c[1])).inputString + ": " + ((double) Math.round(l * 100d) / 100d));
                if (toComputeSides.length == 4) {
                    //convert grams of first into grams of second required/produced
                    System.out.println("Grams of " + allCompound.get(Integer.parseInt(c[1])).inputString +
                            " produced/required for " + toComputeSides[3] + " grams of " +
                            allCompound.get(Integer.parseInt(c[0])).inputString + ": " +
                            (((double) Math.round((Double.parseDouble(toComputeSides[3])/l*100d))) / 100d));
                }
            }
            else{
                //get the molar mass of a compound
                System.out.println("Molar Mass of " + allCompound.get(Integer.parseInt(toComputeSides[2])).inputString + ": " +
                        ChemistryReader.getMolarMass(allCompound.get(Integer.parseInt(toComputeSides[2])), elementInfo) /
                                allCompound.get(Integer.parseInt(toComputeSides[2])).getCoefficient());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean act(int[] indices) {
        //check if int[] indices is a valid solution
        for (int i = 0; i < indices.length; i++) {
            allCompound.get(i).setCoefficient(indices[i]);
        }
        return ChemistryReader.compareElementCounts(compoundsA, compoundsB);
    }
}

record NestedFor(int lo, int hi, com.company.NestedFor.IAction action) {

    public interface IAction {
        boolean act(int[] indices);
    }

    public void nFor(int depth) {
        n_for(0, new int[0], depth);
    }

    private boolean n_for(int level, int[] indices, int maxLevel) {
        if (level == maxLevel) {
            return action.act(indices);
        } else {
            int newLevel = level + 1;
            int[] newIndices = new int[newLevel];
            System.arraycopy(indices, 0, newIndices, 0, level);
            newIndices[level] = lo;
            while (newIndices[level] < hi) {
                if (n_for(newLevel, newIndices, maxLevel)) {
                    return true;
                }
                ++newIndices[level];
            }
        }
        if (level == 0) {
            throw new RuntimeException("No solution in current range");
        }
        return false;
    }
}