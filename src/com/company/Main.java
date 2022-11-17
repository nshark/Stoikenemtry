package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        boolean notQuit = true;
        while (notQuit) {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(System.in));
            Stoichiometry st = new Stoichiometry();
            try {
                String v = r.readLine();
                if (v.equals("quit")){
                    notQuit = false;
                }
                else {
                    System.out.println(st.compute(v));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
