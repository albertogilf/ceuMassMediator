/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFacade;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import presentation.TheoreticalCompoundsFacade;
import utilities.Constantes;


/**
 *
 * @author alberto
 */
public class testAutomaticDetection {
    
    static TheoreticalCompoundsFacade facade = new TheoreticalCompoundsFacade();
    
    private static String testDetectionOfAdductH()
    {
        String massesMode = Constantes.NAME_FOR_RECALCULATED;
        String ionMode = "positive";
        double inputMass = 281.24765d;
        List<String> adducts = new LinkedList<String>();
        adducts.add("allPositives");
        Map<Double, Integer> compositeSpectrum = new HashMap<Double, Integer>();
        compositeSpectrum.put(561.4858d, 0);
        compositeSpectrum.put(141.1306d, 0);
        compositeSpectrum.put(281.24765d, 0);
        compositeSpectrum.put(263.23685d, 0);
        compositeSpectrum.put(264.24228d, 0);
        compositeSpectrum.put(265.2474d, 0);
        compositeSpectrum.put(303.2296d, 0);
        compositeSpectrum.put(304.2393d, 0);
        compositeSpectrum.put(305.23438d, 0);
        return facade.testDetectAdductBasedOnCompositeSpectrum(massesMode, ionMode , inputMass, adducts, compositeSpectrum);
    }
    
    private static String testDetectionOfAdductHH2O()
    {
        String massesMode = Constantes.NAME_FOR_RECALCULATED;
        String ionMode = "positive";
        double inputMass = 265.25244d;
        List<String> adducts = new LinkedList<String>();
        adducts.add("allPositives");
        Map<Double, Integer> compositeSpectrum = new HashMap<Double, Integer>();
        compositeSpectrum.put(265.25244d, 0);
        compositeSpectrum.put(266.2552d, 0);
        compositeSpectrum.put(305.24606d, 0);
        compositeSpectrum.put(306.2479d, 0);
        return facade.testDetectAdductBasedOnCompositeSpectrum(massesMode, ionMode , inputMass, adducts, compositeSpectrum);
    }
    
    private static String testDetectionOfAdductMZHH2O()
    {
        String massesMode = "mz";
        String ionMode = "positive";
        double inputMass = 265.25244d;
        List<String> adducts = new LinkedList<String>();
        adducts.add("allPositives");
        Map<Double, Integer> compositeSpectrum = new HashMap<Double, Integer>();
        compositeSpectrum.put(265.25244d, 0);
        compositeSpectrum.put(266.2552d, 0);
        compositeSpectrum.put(305.24606d, 0);
        compositeSpectrum.put(306.2479d, 0);
        return facade.testDetectAdductBasedOnCompositeSpectrum(massesMode, ionMode , inputMass, adducts, compositeSpectrum);
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String detectionH = testDetectionOfAdductH();
        System.out.println("DETECTED: " + detectionH);
        
        String detectionHH2O = testDetectionOfAdductHH2O();
        System.out.println("DETECTED: " + detectionHH2O);
        
        String detectionMZHH2O = testDetectionOfAdductMZHH2O();
        System.out.println("DETECTED: " + detectionMZHH2O);
    }
}
