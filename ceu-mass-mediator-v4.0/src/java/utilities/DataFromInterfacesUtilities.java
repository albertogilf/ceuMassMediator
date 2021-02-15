/*
 * DataFromInterfacesUtilities.java
 *
 * Created on 28-may-2018, 8:27:03
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities;

import controllers.BrowseSearchControllerJDBC;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

/**
 * Method to use data structures and maps to fill the experiment from the user
 * interface
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 28-may-2018
 *
 * @author Alberto Gil de la Fuente, María Postigo
 */
public final class DataFromInterfacesUtilities {

    private DataFromInterfacesUtilities() {

    }

    public static String ALLADDUCTS_NEUTRAL = "allNeutral";
    public static String ALLADDUCTS_POSITIVE = "allPositives";
    public static String ALLADDUCTS_NEGATIVE = "allNegatives";

    public static final List<SelectItem> CHEMALPHABETLIST;

    static {
        List<SelectItem> CHEMALPHABETLISTTMP = new LinkedList<>();
        CHEMALPHABETLISTTMP.add(new SelectItem("ALL", "All"));
        CHEMALPHABETLISTTMP.add(new SelectItem("CHNOPS", "CHNOPS"));
        CHEMALPHABETLISTTMP.add(new SelectItem("CHNOPSCL", "CHNOPS + Cl"));
        CHEMALPHABETLIST = Collections.unmodifiableList(CHEMALPHABETLISTTMP);
    }
    
    public static final Map<String, Integer> MAPCHEMALPHABET;

    static {
        Map<String, Integer> mapChemAlphabetTMP = new LinkedHashMap<>();
        mapChemAlphabetTMP.put("CHNOPS", 0);
        mapChemAlphabetTMP.put("CHNOPSD", 1);
        mapChemAlphabetTMP.put("CHNOPSCL", 2);
        mapChemAlphabetTMP.put("CHNOPSCLD", 3);
        mapChemAlphabetTMP.put("ALL", 4);
        mapChemAlphabetTMP.put("ALLD", 5);
        MAPCHEMALPHABET = Collections.unmodifiableMap(mapChemAlphabetTMP);
    }

    public static final Map<String, Integer> MAPDATABASES;

    static {
        Map<String, Integer> mapDatabasesTMP = new LinkedHashMap<>();
        mapDatabasesTMP.put("HMDB", 1);
        mapDatabasesTMP.put("LipidMaps", 2);
        mapDatabasesTMP.put("Metlin", 3);
        mapDatabasesTMP.put("Kegg", 4);
        mapDatabasesTMP.put("In-house", 5);
        mapDatabasesTMP.put("Aspergillus", 6);
        mapDatabasesTMP.put("FAHFA Lipids", 7);
        mapDatabasesTMP.put("MINE (Only In Silico Compounds)", 8);
        MAPDATABASES = Collections.unmodifiableMap(mapDatabasesTMP);
    }

    public static final Map<String, Integer> MODIFIERS;

    static {
        // If change, see the method to return as Integer
        Map<String, Integer> modifiersTMP = new LinkedHashMap<>();
        modifiersTMP.put("None", 0);
        modifiersTMP.put("NH3", 1);
        modifiersTMP.put("HCOO", 2);
        modifiersTMP.put("CH3COO", 3);
        modifiersTMP.put("HCOONH3", 4);
        modifiersTMP.put("CH3COONH3", 5);
        MODIFIERS = Collections.unmodifiableMap(modifiersTMP);
    }

    public static final Map<String, Integer> METABOLITESTYPES;

    static {
        Map<String, Integer> metabolitesTypesTMP = new LinkedHashMap<>();
        metabolitesTypesTMP.put("All except peptides", 0); //0
        metabolitesTypesTMP.put("Only lipids", 1); // 1
        metabolitesTypesTMP.put("All including peptides", 2); // 2
        METABOLITESTYPES = Collections.unmodifiableMap(metabolitesTypesTMP);
    }

    /**
     * Return the String for the chemAlphabet (CHNOPS, CHNOPSD, CHNOPSCL,
     * CHNOPSCLD, ALL or ALLD depending on the inclusion of deuterium). TODO
     * CHANGE FOR INT
     *
     * @param inputChemAlphabet
     * @param includeDeuterium
     * @return
     */
    public static String getChemAlphabet(String inputChemAlphabet, Boolean includeDeuterium) {
        String chemAlphabet = inputChemAlphabet;
        if (includeDeuterium) {
            chemAlphabet = chemAlphabet + "D";
        }
        return chemAlphabet;
    }

    public static int getIntChemAlphabet(String inputChemAlphabet) {
        int intChemAlphabet = (int) MAPCHEMALPHABET.getOrDefault(inputChemAlphabet, 5);
        return intChemAlphabet;
    }

    public static List<String> getDatabasesString(List<Integer> databases) {
        List<String> databasesString = new LinkedList<>();
        String dbString;
        Map<String, Integer> provisionalMap;
        provisionalMap = MAPDATABASES;
        for (Map.Entry e : provisionalMap.entrySet()) {
            for (int db : databases) {
                if ((int) e.getValue() == db) {
                    dbString = (String) e.getKey();
                    databasesString.add(dbString);
                }
            }
        }
        return databasesString;
    }
    // COMMENT ALL METHODS HERE
    // I HAVE CHANGED IT. ALL or ALLWM ARE NOT DATABASES! IF THE LIST OF 
    // DATABASES CONTAINS ALL or ALLWM THEN

    public static List<Integer> getDatabasesAsInt(List<String> databases) {
        List<Integer> databasesInt = new LinkedList<>();
        if (databases.contains("All")) {
            databasesInt.addAll(MAPDATABASES.values());
            return databasesInt;
        } else if (databases.contains("AllWM")) {
            databasesInt.addAll(MAPDATABASES.values());
            databasesInt.remove(MAPDATABASES.get("MINE (Only In Silico Compounds)"));
            return databasesInt;
        }
        int dbInt;
        Map<String, Integer> provisionalMap;
        provisionalMap = MAPDATABASES;
        for (Map.Entry e : provisionalMap.entrySet()) {
            for (String db : databases) {
                if (e.getKey().equals(db)) {
                    dbInt = (int) e.getValue();
                    databasesInt.add(dbInt);
                }
            }
        }
        return databasesInt;
    }

    public static int toleranceTypeToInteger(String toleranceType) {
        if (null == toleranceType) {
            return 0;
        } else {
            switch (toleranceType) {
                case "ppm":
                    return 0;
                case "mDa":
                    return 1;
                default:
                    return 0;
            }
        }
    }

    public static String toleranceTypeToString(Integer toleranceType) {
        if (null == toleranceType) {
            return "ppm";
        } else {
            switch (toleranceType) {
                case 0:
                    return "ppm";
                case 1:
                    return "mDa";
                default:
                    return "ppm";
            }
        }
    }

    public static int inputMassModeToInteger(String inputMassMode) {
        if (inputMassMode.equals("m/z") || inputMassMode.equals("mz")) {
            return 1;
        }
        return 0;
    }

    public static int ionizationModeToInteger(String ionizationMode) {
        switch (ionizationMode) {
            case "neutral":
                return 0;
            case "positive":
                return 1;
            case "negative":
                return 2;
            default:
                return 0;
        }
    }

    /**
     *
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts list of adducts
     * @return the list of adducts for positive, negative or neutral if the
     * adduct is unknown ("all")
     */
    public static List<String> getValueAllAductsBasedOnIonMode(int ionizationMode, List<String> adducts) {
        if (adducts.contains("all")) {
            List<String> newAdductsList = new LinkedList<>();
            switch (ionizationMode) {
                case 0:
                    newAdductsList.add(ALLADDUCTS_NEUTRAL);
                    return newAdductsList;
                case 1:
                    newAdductsList.add(ALLADDUCTS_POSITIVE);
                    return newAdductsList;
                case 2:
                    newAdductsList.add(ALLADDUCTS_NEGATIVE);
                    return newAdductsList;
                default:
                    newAdductsList.add(ALLADDUCTS_NEUTRAL);
                    return newAdductsList;
            }
        } else {
            return adducts;
        }
    }

    public static String ionizationModeToString(int ionizationMode) {
        switch (ionizationMode) {
            case 0:
                return "neutral";
            case 1:
                return "positive";
            case 2:
                return "negative";
            default:
                return "neutral";
        }
    }

    public static int metabolitesTypeToInteger(String metaboliteType) {
        return (int) METABOLITESTYPES.get(metaboliteType);
    }

    public static int modifierToInteger(String modifier) {
        return (int) MODIFIERS.get(modifier);
    }

    public static int getChemAlphabetAsInt(String chemAlphabet) {

        int caInt = 0;
        Map<String, Integer> provisionalMap;
        provisionalMap = MAPCHEMALPHABET;
        for (Map.Entry e : provisionalMap.entrySet()) {
            if (e.getKey().equals(chemAlphabet)) {
                caInt = (int) e.getValue();
            }
        }
        return caInt;
    }
    
    // STARTING CE DATA INTERFACE 
    

}
