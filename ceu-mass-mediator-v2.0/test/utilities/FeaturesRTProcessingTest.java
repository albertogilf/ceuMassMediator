/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.Feature;
import LCMS_FEATURE.FeaturesGroupByRT;
import facades.MSFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author María 606888798
 */
public class FeaturesRTProcessingTest {

    /**
     * Test method that group features by RT
     */
    @Test
    public void groupFeaturesByRT() {
        int massesMode = 1;
        int ionizationMode = 1;
        Feature f1 = new Feature(18d, 1d, true, massesMode, ionizationMode);
        Feature f2 = new Feature(14d, 2d, true, massesMode, ionizationMode);
        Feature f3 = new Feature(10d, 1d, true, massesMode, ionizationMode);
        Feature f8 = new Feature(8d, 0d, true, massesMode, ionizationMode);
        Feature f4 = new Feature(12d, 1d, true, massesMode, ionizationMode);
        Feature f5 = new Feature(13d, 3d, true, massesMode, ionizationMode);
        Feature f6 = new Feature(18d, 2d, true, massesMode, ionizationMode);
        Feature f7 = new Feature(18d, 0d, true, massesMode, ionizationMode);

        List<Feature> features = new LinkedList<>();

        features.add(f1);
        features.add(f2);
        features.add(f3);
        features.add(f8);
        features.add(f4);
        features.add(f5);
        features.add(f6);
        features.add(f7);

        List<Feature> fRT3 = new LinkedList<>();
        fRT3.add(f5);
        FeaturesGroupByRT fRT3Check = new FeaturesGroupByRT(3, fRT3);

        List<Feature> fRT2 = new LinkedList<>();
        fRT2.add(f2);
        fRT2.add(f6);
        FeaturesGroupByRT fRT2Check = new FeaturesGroupByRT(2, fRT2);

        List<Feature> fRT1 = new LinkedList<>();
        fRT1.add(f1);
        fRT1.add(f3);
        fRT1.add(f4);
        FeaturesGroupByRT fRT1Check = new FeaturesGroupByRT(1, fRT1);

        List<Feature> fRT0A = new LinkedList<>();
        fRT0A.add(f7);
        FeaturesGroupByRT fRT0ACheck = new FeaturesGroupByRT(0, fRT0A);

        List<Feature> fRT0B = new LinkedList<>();
        fRT0B.add(f8);
        FeaturesGroupByRT fRT0BCheck = new FeaturesGroupByRT(0, fRT0B);

        List<FeaturesGroupByRT> expResult = new LinkedList<>();
        expResult.add(fRT3Check);
        expResult.add(fRT2Check);
        expResult.add(fRT1Check);
        expResult.add(fRT0ACheck);
        expResult.add(fRT0BCheck);

        List<FeaturesGroupByRT> result = FeaturesRTProcessing.groupFeaturesByRT(features, Constants.RT_WINDOW);

        assertEquals(expResult.size(), result.size());
    }

    @Test
    public void isMassWithinTolerance() {
        double m1 = 9;
        double m2 = 9.7;
        boolean result = FeaturesRTProcessing.isMassWithinTolerance(m1, m2);
        boolean expResult = false;
        assertEquals(expResult, result);
    }

    @Test
    public void setAdductsDetectedFromCS() {

        int massesMode = 1;
        int ionMode = 1;
        Double EM = 281.24765d;
        double RT = 13.4;
        Map<Double, Double> CS = new TreeMap();

        CS.put(561.4858d, 236d);
        CS.put(141.1306, 297d);
        CS.put(281.24765, 8532d);
        CS.put(263.23685, 2734d);
        CS.put(264.24228, 616d);
        CS.put(265.2474, 97d);
        CS.put(303.2296, 3154d);
        CS.put(304.2393, 718d);
        CS.put(305.23438, 272d);

        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+Na");
        adducts.add("M+K");

        Feature feature = new Feature(EM, RT, CS, false, "", true, massesMode, ionMode);
        Feature expFeature = new Feature(EM, RT, CS, false, "", true, massesMode, ionMode);
        expFeature.setIsAdductAutoDetected(true);
        expFeature.setAdductAutoDetected("M+H");
        List<Feature> features = new LinkedList<>();
        features.add(feature);
        FeaturesGroupByRT resultFGBRT = new FeaturesGroupByRT(RT, features);
        List<FeaturesGroupByRT> result = new LinkedList<>();
        result.add(resultFGBRT);

        List<Feature> expFeatures = new LinkedList<>();
        expFeatures.add(expFeature);
        FeaturesGroupByRT expResultFGBRT = new FeaturesGroupByRT(RT, expFeatures);
        List<FeaturesGroupByRT> expResult = new LinkedList<>();
        expResult.add(expResultFGBRT);

        FeaturesRTProcessing.setAdductsDetectedFromCS(result, 1, adducts);

        assertEquals(expResult.get(0).getFeatures().get(0).getAdductAutoDetected(), result.get(0).getFeatures().get(0).getAdductAutoDetected());
    }

    /**
     * Test method that set reltationships among features
     */
    @Test
    public void setRelationshipAmongFeatures() {
        List<FeaturesGroupByRT> expFGBRT = new LinkedList<>();
        FeaturesGroupByRT fgbRT = new FeaturesGroupByRT(1);
        int massesMode = 1;
        int ionizationMode = 1;
        Feature f1 = new Feature(501.007276, 18.842525, true, massesMode, ionizationMode);
        Feature f2 = new Feature(522.989218, 18.842525, true, massesMode, ionizationMode);
        Feature f3 = new Feature(538.963158, 18.842525, true, massesMode, ionizationMode);
        Feature f4 = new Feature(343.963158, 18.842525, true, massesMode, ionizationMode);
        Feature f5 = new Feature(306.007276, 18.842525, true, massesMode, ionizationMode);
        fgbRT.addFeature(f1);
        fgbRT.addFeature(f2);
        fgbRT.addFeature(f3);
        fgbRT.addFeature(f4);
        fgbRT.addFeature(f5);
        expFGBRT.add(fgbRT);
        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+2H");
        adducts.add("M+K");
        adducts.add("M+Na");
        adducts.add("M+H-H2O");
        FeaturesRTProcessing.setRelationshipAmongFeatures(expFGBRT, adducts, 1);

        //features are sorted by experimental mass
        List<Feature> expexted = expFGBRT.get(0).getFeatures();
        assertEquals(expexted.get(0).getAdductAutoDetected(), "M+K");
        assertEquals(expexted.get(1).getAdductAutoDetected(), "M+Na");
        assertEquals(expexted.get(2).getAdductAutoDetected(), "M+H");
        assertEquals(expexted.get(3).getAdductAutoDetected(), "M+K");
        assertEquals(expexted.get(4).getAdductAutoDetected(), "M+H");

        /*
         List <FeaturesGroupByRT> resultFGBRT= new LinkedList<>();
        FeaturesGroupByRT rfgbRT= new FeaturesGroupByRT (1);
        Feature rf1= new Feature (501.007276, 18.842525, true, 1);
        rf1.setAdductAutoDetected("M+H");
        Feature rf2= new Feature (522.989218, 18.842525, true, 1);
        rf2.setAdductAutoDetected("M+Na");
        Feature rf3= new Feature (538.963158, 18.842525, true, 1);
        rf3.setAdductAutoDetected("M+K");
        Feature rf4= new Feature (343.963158, 18.842525, true, 1);
        rf4.setAdductAutoDetected("M+K");
        Feature rf5= new Feature (306.007276, 18.842525, true, 1);
        rf5.setAdductAutoDetected("M+H");
         */
    }

    /**
     * Test method that set significant features
     */
    @Test
    public void setSignificantFeatures() {
        List<Feature> features = new LinkedList<>();
        List<Feature> allFeatures = new LinkedList<>();

        int massesMode = 1;
        int ionizationMode = 1;
        Feature f1 = new Feature(1d, 2d, true, massesMode, ionizationMode);
        Feature f2 = new Feature(3d, 4d, true, massesMode, ionizationMode);
        Feature f3 = new Feature(5d, 4d, true, massesMode, ionizationMode);
        Feature f4 = new Feature(4d, 8d, true, massesMode, ionizationMode);
        Feature f5 = new Feature(3d, 31d, true, massesMode, ionizationMode);
        features.add(f1);
        features.add(f2);
        features.add(f3);

        allFeatures.add(f1);
        allFeatures.add(f2);
        allFeatures.add(f3);
        allFeatures.add(f4);
        allFeatures.add(f5);

        FeaturesRTProcessing.setSignificantFeatures(features, allFeatures);

        boolean result = allFeatures.get(0).isIsSignificativeFeature();

        boolean expResult = true;
        assertEquals(expResult, result);
        result = allFeatures.get(1).isIsSignificativeFeature();

        expResult = true;
        assertEquals(expResult, result);
        result = allFeatures.get(2).isIsSignificativeFeature();

        expResult = true;
        assertEquals(expResult, result);

        result = allFeatures.get(3).isIsSignificativeFeature();
        expResult = false;
        assertEquals(expResult, result);
        result = allFeatures.get(4).isIsSignificativeFeature();
        expResult = false;
        assertEquals(expResult, result);

    }

    /**
     * Test method that set fragments
     */
    @Test
    public void setFragments() {
        List<Feature> features = new LinkedList<>();
        int massesMode = 1;
        int ionizationMode = 1;
        Feature f1 = new Feature(451.19362653, 2d, true, massesMode, ionizationMode);
        Feature f2 = new Feature(367.2038211, 2d, true, massesMode, ionizationMode);
        Feature f3 = new Feature(291.2687907, 2d, true, massesMode, ionizationMode);
        features.add(f1);
        features.add(f2);
        features.add(f3);

        List<FeaturesGroupByRT> featuresGroups = new LinkedList<>();
        FeaturesGroupByRT group = new FeaturesGroupByRT(2, features);
        featuresGroups.add(group);

        CompoundLCMS c = new CompoundLCMS(
                451.19362653, 2d, null, "M+H", massesMode, ionizationMode, true, 484, 450.19362653, "C20H36O7P2",
                "Geranylgeranyl diphosphate", null, 2, 1, 0, 0, 0,
                "", "", "", "", "", "", "", null, null, null, null, null);
        f1.addAnnotation(c, "M+H");
        double tolerance = Constants.TOLERANCE_FOR_MSMS_PEAKS_POSSIBLE_FRAGMENTS;
        MSFacade msfacade = new MSFacade();

        FeaturesRTProcessing.setFragments(featuresGroups, tolerance, msfacade, 1);

        assertEquals(features.get(0).isPossibleFragment(), true);
        assertEquals(features.get(1).isPossibleFragment(), true);
        assertEquals(features.get(2).isPossibleFragment(), false);

        msfacade.disconnect();

    }

    /**
     * Test method that set fragments in positive mode
     */
    @Test
    public void setFragments2() {
        int massesMode = 1;
        int ionizationMode = 1;
        List<Feature> features = new LinkedList<>();
        Feature f1 = new Feature((191.067068 + Constants.PROTON_WEIGHT), 2d, true, massesMode, ionizationMode);//'91162', '332-80-9', '169.085126611', 'C7H11N3O2', '169.085126611', (M+Na)
        Feature f2 = new Feature(146.481938, 2d, true, massesMode, ionizationMode);//Fragment: 124.5 (M+Na) 146.481938
        Feature f3 = new Feature(90.021938, 2d, true, massesMode, ionizationMode);//Fragment: 68.04 (M+Na) 90,021938
        Feature f4 = new Feature((300.172544634 + Constants.PROTON_WEIGHT), 2d, true, massesMode, ionizationMode);//'39400', NULL, '2-Methoxyestrone', 'C19H24O3', '300.172544634', (none-->all)
        Feature f5 = new Feature(187d, 2d, true, massesMode, ionizationMode);//Fragment: 187 (M+H) 187
        features.add(f1);
        features.add(f2);
        features.add(f3);
        features.add(f4);
        features.add(f5);

        CompoundLCMS c = new CompoundLCMS(191.067068, 2d, null, "M+Na", massesMode, ionizationMode, true,
                91162, 169.085126611, "C19H24O3", "1-Methylhistidine", null, 2, 1, 0, 0, 0,
                "", "", "", "", "", "", "", null, null, null, null, null);
        f1.addAnnotation(c, "M+Na");
        CompoundLCMS c2 = new CompoundLCMS(300.172544634, 2d, null, "M+H", massesMode, ionizationMode, true,
                39400, 300.172544634, "C19H24O3", "2-Methoxyestrone", null, 2, 1, 0, 0, 0,
                "", "", "", "", "", "", "", null, null, null, null, null);
        f4.addAnnotation(c2, "M+H");
        double tolerance = Constants.TOLERANCE_FOR_MSMS_PEAKS_POSSIBLE_FRAGMENTS;
        MSFacade msfacade = new MSFacade();

        List<FeaturesGroupByRT> featuresGroups = new LinkedList<>();
        FeaturesGroupByRT group = new FeaturesGroupByRT(2, features);
        featuresGroups.add(group);

        FeaturesRTProcessing.setFragments(featuresGroups, tolerance, msfacade, 1);

        assertEquals(features.get(0).isPossibleFragment(), true);
        assertEquals(features.get(1).isPossibleFragment(), true);
        assertEquals(features.get(2).isPossibleFragment(), true);
        assertEquals(features.get(3).isPossibleFragment(), false);
        assertEquals(features.get(4).isPossibleFragment(), false);

        for (Feature f : features) {
            System.out.println(f.toString() + " " + f.isPossibleFragment());
        }
        msfacade.disconnect();
    }

}
