package com.company;

import java.util.HashMap;

public class Compound extends HashMap<String, Integer> {
    private int coefficient = 1;
    public String inputString;
    public Compound(HashMap<String, Integer> entrySet) {
        super(entrySet);
    }

    public Compound() {
        super();
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        if (coefficient == 0) {
            coefficient = 1;
        }
        for (String element : this.keySet()) {
            this.replace(element, (this.get(element) * coefficient) / this.coefficient);
        }
        this.coefficient = coefficient;
    }
}
