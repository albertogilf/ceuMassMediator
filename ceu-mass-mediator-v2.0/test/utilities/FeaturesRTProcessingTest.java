/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import controllers.LCMSControllerAdapter;
import facades.MSFacade;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author María 606888798
 */
public class FeaturesRTProcessingTest {

    /**
     * Test method that grouo features by RT
     */
    @Test
    public void groupFeaturesByRT() {
        Feature f1 = new Feature(18, 1, true, 1);
        Feature f2 = new Feature(14, 2, true, 1);
        Feature f3 = new Feature(10, 1, true, 1);
        Feature f8 = new Feature(8, 0, true, 1);
        Feature f4 = new Feature(12, 1, true, 1);
        Feature f5 = new Feature(13, 3, true, 1);
        Feature f6 = new Feature(18, 2, true, 1);
        Feature f7 = new Feature(18, 0, true, 1);

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

        List<FeaturesGroupByRT> result = FeaturesRTProcessing.groupFeaturesByRT(features, Constantes.RT_WINDOW);

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

        int ionMode = 1;
        Double EM = 281.24765d;
        double RT = 13.4;
        Map<Double, Integer> CS = new LinkedHashMap();

        CS.put(561.4858d, 236);
        CS.put(141.1306, 297);
        CS.put(281.24765, 8532);
        CS.put(263.23685, 2734);
        CS.put(264.24228, 616);
        CS.put(265.2474, 97);
        CS.put(303.2296, 3154);
        CS.put(304.2393, 718);
        CS.put(305.23438, 272);

        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+Na");
        adducts.add("M+K");

        Feature feature = new Feature(EM, RT, CS, false, "", true, ionMode);
        Feature expFeature = new Feature(EM, RT, CS, false, "", true, ionMode);
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

        FeaturesRTProcessing.setAdductsDetectedFromCS(result, "positive", adducts);

        assertEquals(expResult.get(0).getFeatures().get(0).getAdductAutoDetected(), result.get(0).getFeatures().get(0).getAdductAutoDetected());
    }

    @Test
    public void setRelationshipAmongFeatures() {
        List<FeaturesGroupByRT> expFGBRT = new LinkedList<>();
        FeaturesGroupByRT fgbRT = new FeaturesGroupByRT(1);
        Feature f1 = new Feature(501.007276, 18.842525, true, 1);
        Feature f2 = new Feature(522.989218, 18.842525, true, 1);
        Feature f3 = new Feature(538.963158, 18.842525, true, 1);
        Feature f4 = new Feature(343.963158, 18.842525, true, 1);
        Feature f5 = new Feature(306.007276, 18.842525, true, 1);
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
        FeaturesRTProcessing.setRelationshipAmongFeatures(expFGBRT, adducts, "positive");

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
    
  
      @Test
    public void setSignificantFeatures ()
    {
        List <Feature> features= new LinkedList<>();
        List <Feature> allFeatures= new LinkedList<>();
        
        Feature f1 = new Feature(1,2, true, 1);
        Feature f2 = new Feature(3,4, true, 1);
        Feature f3 = new Feature (5,4, true, 1);
        Feature f4 = new Feature(4,8, true, 1);
        Feature f5 = new Feature(3,31, true, 1);
        features.add(f1);
        features.add(f2);
        features.add(f3);
        
        
        
        allFeatures.add(f1);
        allFeatures.add(f2);
        allFeatures.add(f3);
        allFeatures.add(f4);
        allFeatures.add(f5);
        
        
        
       FeaturesRTProcessing.setSignificantFeatures(features, allFeatures);
        
       
        boolean result= allFeatures.get(0).isIsSignificativeFeature();
        
        boolean expResult=true;
        assertEquals(expResult,result);
        result=allFeatures.get(1).isIsSignificativeFeature();
        
        expResult=true;
        assertEquals(expResult,result);
        result=allFeatures.get(2).isIsSignificativeFeature();
       
        expResult=true;
        assertEquals(expResult,result);
        
        
        result=allFeatures.get(3).isIsSignificativeFeature();
        expResult=false;
        assertEquals(expResult,result);
        result=allFeatures.get(4).isIsSignificativeFeature();
        expResult=false;
        assertEquals(expResult,result);
        
        
    }

    
    /*
    @Test
    public void setFragments() {
        
        Feature f1 = new Feature(135.11683, 18.842525, true, 1);
        f1.setAdductAutoDetected("M+H");
        f1.setIsAdductAutoDetected(true);
        Feature f2 = new Feature(95.08607535, 18.842525, true, 1);
        
        
        List<Feature> features = new LinkedList<>();

        features.add(f1);
        features.add(f2);
        MSFacade msfacade= new MSFacade();
        FeaturesGroupByRT fgbrt= new FeaturesGroupByRT(18.842525, features);
        List <FeaturesGroupByRT> lista= new LinkedList <>();
        lista.add(fgbrt);
        
        FeaturesRTProcessing.setFragments(lista, msfacade);
        
        boolean result= f2.isPossibleFragment();
        boolean expResult= true;
        
        assertEquals(expResult, result);

        
    }
     */
}
