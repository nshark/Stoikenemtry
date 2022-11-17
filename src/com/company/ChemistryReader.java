package com.company;

import java.util.ArrayList;

public class ChemistryReader {
    public static Compound readInMolecules(String toRead) {
        char[] toReadCharArray = toRead.toCharArray();
        StringBuilder temp = new StringBuilder();
        Compound toReturn = new Compound();
        toReturn.inputString = toRead;
        for (int i = 0; i < toReadCharArray.length; i++) {
            if (Character.isDigit(toReadCharArray[i]) && !temp.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(toReadCharArray[i]);
                for (int j = 1; j + i < toReadCharArray.length; j++) {
                    if (Character.isDigit(toReadCharArray[i + j])) {
                        sb.append(toReadCharArray[i + j]);
                    } else {
                        break;
                    }
                }
                toReturn.put(temp.toString(), Integer.parseInt(sb.toString()));
                temp = new StringBuilder();
            } else {
                if (!temp.isEmpty() && Character.isUpperCase(toReadCharArray[i])) {
                    toReturn.put(temp.toString(), 1);
                    temp = new StringBuilder();
                }
                if (Character.isAlphabetic(toReadCharArray[i])) {
                    temp.append(toReadCharArray[i]);
                }
            }
            if (!temp.isEmpty() && i == toReadCharArray.length - 1) {
                toReturn.put(temp.toString(), 1);
            }
        }
        return toReturn;
    }

    public static void addCompoundsElementCount(Compound initial, Compound toAdd) {
        for (String element : toAdd.keySet()) {
            if (initial.containsKey(element)) {
                initial.replace(element, initial.get(element) + toAdd.get(element));
            } else {
                initial.put(element, toAdd.get(element));
            }
        }
    }

    public static Compound combineCompounds(ArrayList<Compound> list) {
        Compound toReturn = new Compound();
        for (Compound p : list) {
            addCompoundsElementCount(toReturn, p);
        }
        return toReturn;
    }

    public static boolean compareElementCounts(ArrayList<Compound> listA, ArrayList<Compound> listB) {
        return combineCompounds(listA).equals(combineCompounds(listB));
    }

    public static double getMolarMass(Compound c, ArrayList<ArrayList<String>> info) {
        Compound v = new Compound(c);
        double r = 0d;
        v.setCoefficient(1);
        for (String s : v.keySet()) {
            for (ArrayList<String> i : info) {
                if (s.equals(i.get(2))) {
                    r += v.get(s) * Double.parseDouble(i.get(3));
                }
            }
        }
        return r;
    }
}
