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
import persistence.NewLipidsClassification;
import persistence.oxidizedTheoreticalCompound.FACompound;
import persistence.theoreticalCompound.NewCompound;

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
     * Test of calculateTheoreticalExperimentalMassOfPC method, of class Utilities.
     */
    @Test
    public void testCalculateTheoreticalMassOfPC() {
        System.out.println("calculateTheoreticalMassOfPC");
        List<FACompound> FAs = null;
        Double expResult = null;
//        Double result = Utilities.calculateTheoreticalExperimentalMassOfPC(FAs);
//        assertEquals(expResult, result);
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
        Double result = Utilities.calculateTheoreticalMassOfOxidizedFA(FA);
        assertTrue(Math.abs(result - expResult) < 0.001d);
    }
    

    /**
     * Test of calculateTheoreticalMassOfNonOxidizedFA method, of class Utilities.
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
        String result = Utilities.createNameOfPC(FAs);
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
        String result = Utilities.createNameOfPC(FAs);
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
        String result = Utilities.createFormulaOfPC(FAs);
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
        String result = Utilities.createFormulaOfPC(FAs);
        assertEquals(expResult, result);
    }

    private FACompound get16_0NonOxidized(){
        NewCompounds NC;
        NC = new NewCompounds();
        // TODO CHECK THE DB_ID_NUMBER 
        NC.setCompoundId(123205);
        NC.setLipidClass(new NewLipidsClassification());
        NC.setMass(256.2402d);
        NC.setCarbons(16);
        NC.setDoubleBonds(0);
        NC.setCategory("FA");
        NC.setMainClass("FA01");
        NC.setSubClass("FA010S"); 
        FACompound FA = new FACompound(NC, 255.233d, 256.2407, "");
        return FA;
    }
    
    private FACompound get20_4OxidizedOH(){
        NewCompounds NC;
        NC = new NewCompounds();
        // TODO CHECK THE DB_ID_NUMBER 
        NC.setCompoundId(123342);
        NC.setLipidClass(new NewLipidsClassification());
        NC.setMass(304.2402302664d);
        NC.setCarbons(20);
        NC.setDoubleBonds(4);
        NC.setCategory("FA");
        NC.setMainClass("FA01");
        NC.setSubClass("FA010U"); // PG
        FACompound FA = new FACompound(NC, 319.2274, 304.2398, "");
        return FA;
    }
    
}
