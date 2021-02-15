/*
 * OxidationProcessing.java
 *
 * Created on 23-may-2018, 12:32:02
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */

package utilities;

import java.util.List;

/**
 * Class which contains static methods to process algorithms with adducts
 * 
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 23-may-2018
 * 
 * @author Alberto Gil de la Fuente
 */
public final class OxidationProcessing {

    /**
     * Creates a new instance of OxidationProcessing
     */
    private OxidationProcessing ()
    {
        
    }
    
    /**
     * process the list of LC oxidations to perform the search. Return LC
     * oxidations to search
     *
     * @param oxidations Possible oxidations
     */
    public static List<String> chooseLCOxidations(List<String> oxidations) {
        if (oxidations.isEmpty() || oxidations.get(0).equals("allOxidations")) {
            oxidations = OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
        }
        //System.out.println("DEFAULT -> ADDUCTS: " + adducts);
        return oxidations;
    }

    /**
     * process the list of SC oxidations to perform the search. Return SC
     * oxidations to search
     *
     * @param oxidations Possible oxidations
     */
    public static List<String> chooseSCOxidations(List<String> oxidations) {
        if (oxidations.isEmpty() || oxidations.get(0).equals("allOxidations")) {
            oxidations = OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
        }
        //System.out.println("DEFAULT -> ADDUCTS: " + adducts);
        return oxidations;
    }
}
