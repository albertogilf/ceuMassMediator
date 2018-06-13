/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testOxidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import facades.TheoreticalCompoundsFacade;
import utilities.Constantes;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;

/**
 *
 * @author alberto
 */
public class testOxidizedFA {

    
    
    
    
    
    private static TheoreticalCompoundsFacade facade = new TheoreticalCompoundsFacade();
    private static String PATHNAMEFORTESTINGFA = "test/filesForTesting/FA.csv";

    
    private static int isThereAnyOxidizedFA() {
        Double querymzParentIonMass = 0d;
        List<Double> querymzFattyAcidMasses;
        querymzFattyAcidMasses = readDoublesFromFile(PATHNAMEFORTESTINGFA);
        String toleranceModeForFA = "mDa";
        Double toleranceForFA = 10d;
        String toleranceModeForPI = "mDa";
        Double toleranceForPI = 10d;
        String ionMode = "negative";
        List<String> databases = null;
        List<String> possibleOxidations = new LinkedList<String>();
        possibleOxidations.add("allOxidations");
        List<OxidizedTheoreticalCompound> listOxidizedCompoundsGrouped;
        listOxidizedCompoundsGrouped = new LinkedList<OxidizedTheoreticalCompound>();
        OxidizedTheoreticalCompound oxidizedCompound;

        /*oxidizedCompound = facade.findLCOxidizedFA(querymzParentIonMass,
                querymzFattyAcidMasses,
                toleranceModeForFA,
                toleranceForFA,
                toleranceModeForPI,
                toleranceForPI,
                ionMode,
                databases,
                possibleOxidations,
                listOxidizedCompoundsGrouped);
        */
        return 0;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int isThereAnyFA = isThereAnyOxidizedFA();
        System.out.println("DETECTED: " + isThereAnyFA);
    }

    private static List<Double> readDoublesFromFile(String PATH) {
        List<Double> listDoubles = new LinkedList<Double>();

        Scanner scan;
        File file = new File(PATH);
        try {
            scan = new Scanner(file);

            while (scan.hasNextDouble()) {
                Double number = scan.nextDouble();
                //System.out.println(number);
                listDoubles.add(number);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return listDoubles;
    }
}
