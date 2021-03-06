/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import persistence.NewCompounds;
import persistence.NewLMClassification;
import persistence.oxidizedTheoreticalCompound.FACompound;

/**
 *
 * @author alberto
 */
public class UtilitiesTest {

    public UtilitiesTest() {
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
     * Test of calculatePPMIncrement method, of class Utilities.
     */
    @Test
    public void testCalculatePPMIncrement() {
        System.out.println("calculatePPMIncrement");
        Double measuredMass = 757.5667d;
        Double theoreticalMass = 757.56216d;
        int expResult = 6;
        int result = Utilities.calculatePPMIncrement(measuredMass, theoreticalMass);
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateTheoreticalMass method, of class Utilities.
     */
    @Test
    public void testCalculateTheoreticalMass() {
        System.out.println("calculateTheoreticalMass");
        String formula = "C44H80NO8P";
        Double expResult = 781.5622d;
        Double result = Utilities.calculateTheoreticalMass(formula);
        assertTrue("Result: " + result + " ExpResult: " + expResult, Math.abs(result - expResult) < 0.0001d);
    }

    /**
     * Test of calculateTheoreticalExperimentalMassOfPC method, of class
     * Utilities.
     */
    @Test
    public void testCalculateTheoreticalMassOfPC() {
        System.out.println("calculateTheoreticalMassOfPC");
        // TODO ALBERTO
        String adduct = "M+H";
        List<FACompound> FAs = new LinkedList();
        persistence.NewCompounds nc = new persistence.NewCompounds();
        nc.setCarbons(0);
        
        Double oxidizedFAEM = 0d;
        Double masstoSearchForOxidizedFA = 0d;
        String oxidationType = "";
        FACompound FA1 = new FACompound(nc, oxidizedFAEM, masstoSearchForOxidizedFA, oxidationType);
        
        FAs.add(FA1);
        Double expResult = 0d;
        Double result = Utilities.calculateTheoreticalExperimentalMassOfPCJPA(FAs, adduct);
        System.out.println("");
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateTheoreticalMassOfOxidizedFA method, of class Utilities.
     */
    @Test
    public void testCalculateTheoreticalMassOfOxidizedFA() {
        System.out.println("calculateTheoreticalMassOfOxidizedFA TEST");

        FACompound FA = get20_4OxidizedOH();

        Double expResult = 304.2402302664d;
        Double result = Utilities.calculateTheoreticalMassOfOxidizedFAJPA(FA);
        assertTrue(Math.abs(result - expResult) < 0.001d);
    }

    /**
     * Test of calculateTheoreticalMassOfNonOxidizedFA method, of class
     * Utilities.
     */
    @Test
    public void testCalculateTheoreticalMassOfNonOxidizedFA() {
        System.out.println("calculateTheoreticalMassOfNonOxidizedFA TEST");
        FACompound FA = get16_0NonOxidized();

        Double expResult = 256.2402d;
        Double result = Utilities.calculateTheoreticalMassOfNonOxidizedFA(FA);
        assertTrue(Math.abs(result - expResult) < 0.001d);
    }

    /**
     * Test of createNameOfPC method, of class Utilities.
     */
    @Test
    public void testCreateNameOfPC() {
        System.out.println("createNameOfPC Test");
        FACompound oxidizedFA;
        FACompound nonOxidizedFA;
        oxidizedFA = get20_4OxidizedOH();
        nonOxidizedFA = get16_0NonOxidized();
        List<FACompound> FAs;
        FAs = new LinkedList<>();
        FAs.add(nonOxidizedFA);
        FAs.add(oxidizedFA);
        String expResult = "PC(16:0/20:4)";
        String result = Utilities.createNameOfPCJPA(FAs);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateNameNullOfPC() {
        System.out.println("createNameNullOfPC Test");
        FACompound oxidizedFA;
        FACompound nonOxidizedFA;
        oxidizedFA = null;
        nonOxidizedFA = get16_0NonOxidized();
        List<FACompound> FAs;
        FAs = new LinkedList<>();
        FAs.add(nonOxidizedFA);
        FAs.add(oxidizedFA);
        String expResult = "";
        String result = Utilities.createNameOfPCJPA(FAs);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateFormulaOfPC() {
        System.out.println("createFormulaOfPC Test");
        FACompound oxidizedFA;
        FACompound nonOxidizedFA;
        oxidizedFA = get20_4OxidizedOH();
        nonOxidizedFA = get16_0NonOxidized();
        List<FACompound> FAs;
        FAs = new LinkedList<>();
        FAs.add(nonOxidizedFA);
        FAs.add(oxidizedFA);
        String expResult = "C44H80NO8P";
        String result = Utilities.createFormulaOfPCJPA(FAs);
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateFormula1FAOfPC() {
        System.out.println("createFormulaNullOfPC Test");
        FACompound oxidizedFA;
        FACompound nonOxidizedFA;
        oxidizedFA = null;
        nonOxidizedFA = get16_0NonOxidized();
        List<FACompound> FAs;
        FAs = new LinkedList<>();
        FAs.add(nonOxidizedFA);
        FAs.add(oxidizedFA);
        String expResult = "C24H49NO6P";
        String result = Utilities.createFormulaOfPCJPA(FAs);
        assertEquals(expResult, result);
    }

    private FACompound get16_0NonOxidized() {
        NewCompounds NC;
        NC = new NewCompounds();
        // TODO CHECK THE DB_ID_NUMBER 
        NC.setCompoundId(123205);
        NC.setLMclassification(new NewLMClassification());
        NC.setMass(256.2402d);
        NC.setCarbons(16);
        NC.setDoubleBonds(0);
        NC.setCategory("FA");
        NC.setMainClass("FA01");
        NC.setSubClass("FA010S");
        FACompound FA = new FACompound(NC, 255.233d, 256.2407, "");
        return FA;
    }

    private FACompound get20_4OxidizedOH() {
        NewCompounds NC;
        NC = new NewCompounds();
        // TODO CHECK THE DB_ID_NUMBER 
        NC.setCompoundId(123342);
        NC.setLMclassification(new NewLMClassification());
        NC.setMass(304.2402302664d);
        NC.setCarbons(20);
        NC.setDoubleBonds(4);
        NC.setCategory("FA");
        NC.setMainClass("FA01");
        NC.setSubClass("FA010U"); // PG
        FACompound FA = new FACompound(NC, 319.2274, 304.2398, "");
        return FA;
    }

    /**
     * Test of calculateTheoreticalExperimentalMassOfPC method, of class
     * Utilities.
     */
    @Test
    public void testCalculateTheoreticalExperimentalMassOfPC() {
        System.out.println("calculateTheoreticalExperimentalMassOfPC");
        List<FACompound> FAs = null;
        String adductType = "";
        Double expResult = null;
        Double result = Utilities.calculateTheoreticalExperimentalMassOfPCJPA(FAs, adductType);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateTheoreticalNeutralMassOfPC method, of class Utilities.
     */
    @Test
    public void testCalculateTheoreticalNeutralMassOfPC() {
        System.out.println("calculateTheoreticalNeutralMassOfPC");
        List<FACompound> FAs = null;
        Double expResult = null;
        Double result = Utilities.calculateTheoreticalNeutralMassOfPCJPA(FAs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateFAEMFromPIandOtherFAEM method, of class Utilities.
     */
    @Test
    public void testCalculateFAEMFromPIandOtherFAEM() {
        System.out.println("calculateFAEMFromPIandOtherFAEM");
        Double ParentIonNeutralMass = null;
        Double FAEM1 = null;
        Double expResult = null;
        Double result = Utilities.calculateFAEMFromPIandOtherFAEM(ParentIonNeutralMass, FAEM1);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of createSQLInStringFromListStrings method, of class Utilities.
     */
    @Test
    public void testCreateSQLInStringFromListStrings() {
        System.out.println("createSQLInStringFromListStrings");
        List<String> list = null;
        String expResult = "";
        String result = Utilities.createSQLInStringFromListStrings(list);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateDeltaPPM method, of class Utilities.
     */
    @Test
    public void testCalculateDeltaPPM() {
        System.out.println("calculateDeltaPPM");
        Double massToSearch = null;
        String toleranceMode = "";
        Double tolerance = null;
        Double expResult = null;
        Double result = Utilities.calculateDeltaPPM(massToSearch, toleranceMode, tolerance);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
