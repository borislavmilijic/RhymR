package at.ac.univie.hci.rhymr;

import java.util.ArrayList;

//This class is used to preserve data in one App session - list of search terms

class ValuePreserver {
    private ValuePreserver() {}

    ArrayList<String> saved_list = new ArrayList<>();

    static ValuePreserver getInstance() {
        if (instance == null) {
            instance = new ValuePreserver();
        }
        return instance;
    }

    private static ValuePreserver instance;
}
