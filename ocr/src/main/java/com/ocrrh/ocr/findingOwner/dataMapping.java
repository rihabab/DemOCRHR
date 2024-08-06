package com.ocrrh.ocr.findingOwner;


import com.ocrrh.ocr.exceptions.NoNameMachedException;

import java.util.*;

public class dataMapping {

    public static List<String> findMatchingNames(List<List<String>> extractedNames, List<String> databaseNames) throws NoNameMachedException{
        List<String> matchingNames = new ArrayList<>();

        // Convert database names to sets of words for easier comparison, all in lowercase
        List<Set<String>> dbNamesAsSets = new ArrayList<>();
        for (String dbName : databaseNames) {
            Set<String> dbNameSet = new HashSet<>();
            for (String namePart : dbName.split(",")) {
                dbNameSet.add(namePart.trim().toLowerCase());
            }
            dbNamesAsSets.add(dbNameSet);
        }
        boolean a=false;
        for (List<String> extractedName : extractedNames) {
            Set<String> extractedNameSet = new HashSet<>();
            for (String namePart : extractedName) {
                extractedNameSet.add(namePart.trim().toLowerCase());
            }
            for (int i = 0; i < dbNamesAsSets.size(); i++) {
                if (dbNamesAsSets.get(i).equals(extractedNameSet)) {
                    matchingNames.add(databaseNames.get(i));
                    a=true;
                }
            }
        }
        if(!a) throw new NoNameMachedException();
        return matchingNames;
    }

}
