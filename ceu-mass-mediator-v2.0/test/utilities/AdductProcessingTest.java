/*
 * AdductProcessing.java
 *
 * Created on 10-may-2018, 14:46:42
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


/**
 * Test Class which contains static methods to process algorithms with adducts
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1 10-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class AdductProcessingTest {

    // TODO ALBERTO
    public AdductProcessingTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculateNeutralMassFromHAdduct method, of class AdductProcessing.
     */
    @Test
    public void testCalculateNeutralMass() {
        System.out.println("calculateNeutralMass");
        // positive mode
        int ionizationMode = 1;
        double mzMass = 400.3432;
        double expResult = 399.3359;
        double result = AdductProcessing.calculateNeutralMassFromHAdduct(ionizationMode, mzMass);
        assertEquals(expResult, result, 0.0001);
        // negative mode
        ionizationMode = 2;
        mzMass = 399.3359;
        expResult = 400.3432;
        result = AdductProcessing.calculateNeutralMassFromHAdduct(ionizationMode, mzMass);
        assertEquals(expResult, result, 0.0001);

    }

    /**
     * Test of calculateMZFromHAdduct method, of class AdductProcessing.
     */
    @Test
    public void testCalculateMZ() {
        System.out.println("calculateMZ");
        // positive mode
        int ionizationMode = 1;
        double neutralMass = 399.3359;
        double expResult = 400.3432;
        double result = AdductProcessing.calculateMZFromHAdduct(ionizationMode, neutralMass);
        assertEquals(expResult, result, 0.0001);
        // negative mode
        ionizationMode = 2;
        neutralMass = 400.3432;
        expResult = 399.3359;
        result = AdductProcessing.calculateMZFromHAdduct(ionizationMode, neutralMass);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of getMassToSearch method, of class AdductProcessing.
     */
    @Test
    public void testGetMassToSearch() {
        System.out.println("getMassToSearch");
        //System.out.println("exp: " + expResult + " res: " + result);
        // M+H adduct
        Double inputMass = 400.3432;
        String adduct = "M+H";
        Double adductValue = -1.007276d;
        Double expResult = 399.3359;
        Double result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);
        assertEquals(expResult, result, 0.001);
        // M+H+2K adduct
        adduct = "M+H+2K";
        adductValue = -26.3112d;
        expResult = 1122.096;
        result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);

        assertEquals(expResult, result, 0.001);
        // 2M+ACN+H adduct
        adduct = "2M+ACN+H";
        adductValue = -42.033823d;
        expResult = 179.154d;
        result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);
        assertEquals(expResult, result, 0.001);
        // M+Cl adduct
        adduct = "M+Cl";
        adductValue = -34.969402d;
        expResult = 365.3738;
        result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);
        assertEquals(expResult, result, 0.001);
        // 2M+FA-H adduct
        adduct = "2M+FA-H";
        adductValue = -44.998201d;
        expResult = 177.6725d;
        result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);
        assertEquals(expResult, result, 0.001);
        // M-3H adduct
        adduct = "M+3H";
        adductValue = 1.007276d;
        expResult = 1204.05143;
        result = AdductProcessing.getMassToSearch(inputMass, adduct, adductValue);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of getAdductMass method, of class AdductProcessing.
     */
    @Test
    public void testGetAdductMass() {
        System.out.println("getAdductMass");
        //System.out.println("exp: " + expResult + " res: " + result);
        // M+H adduct
        Double monoisotopic_weight = 399.3359d;
        String adduct = "M+H";
        Double adductValue = -1.007276d;
        Double expResult = 400.3432d;
        Double result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.01);
        // M+H+2K adduct
        adduct = "M+H+2K";
        adductValue = -26.3112d;
        monoisotopic_weight = 1122.096;
        result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.01);
        // 2M+ACN+H adduct
        adduct = "2M+ACN+H";
        adductValue = -42.033823d;
        monoisotopic_weight = 179.154d;
        result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.01);
        // M+Cl adduct
        adduct = "M+Cl";
        adductValue = -34.969402d;
        monoisotopic_weight = 365.3738;
        result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.01);
        // 2M+FA-H adduct
        adduct = "2M+FA-H";
        adductValue = -44.998201d;
        monoisotopic_weight = 177.6725d;
        result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.01);
        // M-3H adduct
        adduct = "M+3H";
        adductValue = 1.007276d;
        monoisotopic_weight = 1204.05143;
        result = AdductProcessing.getMassOfAdductFromMonoWeight(monoisotopic_weight, adduct, adductValue);

        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of detectAdductBasedOnCompositeSpectrum method, of class
     * AdductProcessing.
     */
    @Test
    public void testDetectAdductBasedOnCompositeSpectrum() {
        // TODO ALBERTO
        System.out.println("detectAdductBasedOnCompositeSpectrum");
        // positive M+H
        String massesMode = "mz";
        int ionMode = 1;
        Double inputMass = 281.24765d;
        Map<String, String> provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        List<String> adducts = new LinkedList();
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        Map<Double, Double> compositeSpectrum = new TreeMap();

        compositeSpectrum.put(561.4858d, 236d);
        compositeSpectrum.put(141.1306, 297d);
        compositeSpectrum.put(281.24765, 8532d);
        compositeSpectrum.put(263.23685, 2734d);
        compositeSpectrum.put(264.24228, 616d);
        compositeSpectrum.put(265.2474, 97d);
        compositeSpectrum.put(303.2296, 3154d);
        compositeSpectrum.put(304.2393, 718d);
        compositeSpectrum.put(305.23438, 272d);
        String expResult = "M+H";
        String result;
        result = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, inputMass, adducts, compositeSpectrum);
        assertEquals(expResult, result);

        // positive M+H-H2O
        massesMode = "mz";
        ionMode = 1;
        inputMass = 265.25244d;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        compositeSpectrum = new TreeMap();

        compositeSpectrum.put(265.25244, 2643d);
        compositeSpectrum.put(266.2552, 546d);
        compositeSpectrum.put(305.24606, 811d);
        compositeSpectrum.put(306.2479, 286d);

        expResult = "M+H-H2O";
        result = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, inputMass, adducts, compositeSpectrum);
        assertEquals(expResult, result);

    }

    @Test
    public void testDetectionOfAdductH() {
        String massesMode = "mz";
        int ionMode = 1;
        Map<String, String> provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        double inputMass = 281.24765d;
        List<String> adducts = new LinkedList();
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        Map<Double, Double> compositeSpectrum = new TreeMap();
        compositeSpectrum.put(561.4858d, 0d);
        compositeSpectrum.put(563.4868d, 0d);
        compositeSpectrum.put(141.1306d, 0d);
        compositeSpectrum.put(281.24765d, 0d);
        compositeSpectrum.put(263.23685d, 0d);
        compositeSpectrum.put(264.24228d, 0d);
        compositeSpectrum.put(265.2474d, 0d);
        compositeSpectrum.put(303.2296d, 0d);
        compositeSpectrum.put(304.2393d, 0d);
        compositeSpectrum.put(305.23438d, 0d);
        String expResult = "M+H";
        String result;
        result = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, inputMass, adducts, compositeSpectrum);
        assertEquals(expResult, result);

    }

    @Test
    public void testDetectionOfAdductHH2O() {
        String massesMode = "mz";
        int ionMode = 1;
        double inputMass = 265.25244d;
        Map<String, String> provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        List<String> adducts = new LinkedList();
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        Map<Double, Double> compositeSpectrum = new TreeMap();
        compositeSpectrum.put(265.25244d, 0d);
        compositeSpectrum.put(266.2552d, 0d);
        compositeSpectrum.put(305.24606d, 0d);
        compositeSpectrum.put(306.2479d, 0d);
        String expResult = "M+H-H2O";
        String result;
        result = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, inputMass, adducts, compositeSpectrum);
        assertEquals(expResult, result);
    }
    
 

    @Test
    public void testGetMonoPeaksFromCS() {
        Map<Double, Double> compositeSpectrum = new TreeMap();
        compositeSpectrum.put(265.25244d, 0d);
        compositeSpectrum.put(266.2552d, 0d);
        compositeSpectrum.put(305.24606d, 0d);
        compositeSpectrum.put(306.2479d, 0d);
        Map<Double, Double> expResult = new TreeMap();
        expResult.put(265.25244d, 0d);
        expResult.put(305.24606d, 0d);
        Map<Double, Double> result;
        result = AdductProcessing.getMonoPeaksFromCS(compositeSpectrum);
        assertThat(expResult, is(result));
        compositeSpectrum = new TreeMap();
        compositeSpectrum.put(561.4858d, 0d);
        compositeSpectrum.put(563.4868d, 0d);
        compositeSpectrum.put(141.1306d, 0d);
        compositeSpectrum.put(281.24765d, 0d);
        compositeSpectrum.put(263.23685d, 0d);
        compositeSpectrum.put(264.24228d, 0d);
        compositeSpectrum.put(265.2474d, 0d);
        compositeSpectrum.put(303.2296d, 0d);
        compositeSpectrum.put(304.2393d, 0d);
        compositeSpectrum.put(305.23438d, 0d);
        expResult = new TreeMap();
        expResult.put(561.4858d, 0d);
        expResult.put(141.1306d, 0d);
        expResult.put(281.24765d, 0d);
        expResult.put(263.23685d, 0d);
        expResult.put(303.2296d, 0d);
        result = AdductProcessing.getMonoPeaksFromCS(compositeSpectrum);
        assertThat(expResult, is(result));
    }

    /**
     * Test of chooseprovisionalMapAdducts method, of class AdductProcessing.
     */
    @Test
    public void testChooseprovisionalMapAdducts() {
        System.out.println("chooseprovisionalMapAdducts");
        // Positive
        String massesMode = "mz";
        int ionMode = 1;
        Map<String, String> expResult = AdductsLists.MAPMZPOSITIVEADDUCTS;
        Map<String, String> result = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        //System.out.println("result: " + result);
        //System.out.println("expResult: " + expResult);
        assertThat(result, is(expResult));
        // Negative
        ionMode = 2;
        expResult = AdductsLists.MAPMZNEGATIVEADDUCTS;
        result = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        assertThat(result, is(expResult));
        // Neutral
        ionMode = 0;
        expResult = AdductsLists.MAPNEUTRALADDUCTS;
        result = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        assertThat(result, is(expResult));

    }

    /**
     * Test of chooseAdducts method, of class AdductProcessing.
     */
    @Test
    public void testChooseAdducts() {
        System.out.println("chooseAdducts");
        // Positive adducts empty
        String massesMode = "mz";
        int ionMode = 1;
        Map<String, String> provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        List<String> adducts = new LinkedList();
        List<String> expResult = new LinkedList(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
        List<String> result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));
        // Positive adducts all
        ionMode = 1;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE);
        expResult = new LinkedList<String>(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
        result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));
        // Positive adducts 
        ionMode = 1;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        adducts.add("M+H");
        adducts.add("M+K");
        adducts.add("M+Na");
        expResult = new LinkedList<String>(adducts);
        result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));

        // Negative adducts empty
        ionMode = 2;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        expResult = new LinkedList<String>(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
        result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));
        // Positive adducts all
        ionMode = 2;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE);
        expResult = new LinkedList<String>(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
        result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));
        // Positive adducts 
        ionMode = 2;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = new LinkedList();
        adducts.add("M-H");
        adducts.add("M+Cl");
        adducts.add("M-3H");
        expResult = new LinkedList<String>(adducts);
        result = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        assertThat(result, is(expResult));
    }

    /**
     * Test of getAllAdducts method, of class AdductProcessing.
     */
    @Test
    public void testGetAllAdducts() {
        System.out.println("getAllAdducts");
        int ionMode = 1;
        List<String> expResult = new LinkedList(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
        List<String> result = AdductProcessing.getAllAdducts(ionMode);
        assertThat(expResult, is(result));
        ionMode = 2;
        expResult = new LinkedList(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
        result = AdductProcessing.getAllAdducts(ionMode);
        assertThat(expResult, is(result));
        ionMode = 0;
        expResult = new LinkedList(AdductsLists.MAPNEUTRALADDUCTS.keySet());
        result = AdductProcessing.getAllAdducts(ionMode);
        assertThat(expResult, is(result));
    }
     /**
     * Test of getAdductValue method, of class AdductProcessing.
     */
    @Test
    public void testGetAdductValue()
    {
        /*
        System.out.println("getAdductValue");
        String ionMode="positive";
        String adductName="M+Na";
        double expectedResult= -22.989218;
        double result= AdductProcessing.getAdductValue(adductName, ionMode);
        assertThat(expectedResult, is(result));
        ionMode="negative";
        adductName="M+Cl";
        expectedResult= -34.969402;
        result= AdductProcessing.getAdductValue(adductName, ionMode);
        assertThat(expectedResult, is(result));
        ionMode="neutral";
        adductName="M";
        expectedResult= 0;
        result= AdductProcessing.getAdductValue(adductName, ionMode);
        assertThat(expectedResult, is(result));
        */
    }
    
    /**
     * Test of getAdductValue method, of class AdductProcessing.
     */
    
    @Test
    public void testDetectAdductBasedOnFeaturesData()
    {
        System.out.println("detectAdductBasedOnFeaturesData");
        int ionMode = 1;
        double molecularMass= 300;
        double experimentalMass= (300-38.963158);
        String expectedResult= "M+K";
        String result= AdductProcessing.detectAdductBasedOnAnotherFeatureAdduct(experimentalMass, molecularMass, ionMode);
        assertThat(expectedResult, is(result));
        
        ionMode=2;
        molecularMass= 556;
        experimentalMass= (556+1.007276); 
        expectedResult= "M-H";
        result= AdductProcessing.detectAdductBasedOnAnotherFeatureAdduct(experimentalMass, molecularMass, ionMode);
        assertThat(expectedResult, is(result));
        
        ionMode=0;
        molecularMass= 435;
        experimentalMass= 435; 
        expectedResult= "M";
        result= AdductProcessing.detectAdductBasedOnAnotherFeatureAdduct(molecularMass,experimentalMass, ionMode);
        assertThat(expectedResult, is(result));
        
    }

}
