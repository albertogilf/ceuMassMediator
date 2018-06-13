/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeTest;

import LCMS.Experiment;
import LCMS.ExperimentGroupByRT;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import List.NoDuplicatesList;
import controllers.LCMSController;
import controllers.LCMSControllerAdapter;
import facades.MSFacade;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import javax.faces.model.SelectItem;
import org.junit.Test;
import utilities.AdductsLists;
import utilities.Cadena;
import utilities.Constantes;
import utilities.DataFromInterfacesUtilities;
import utilities.FeaturesRTProcessing;

/**
 *
 * @author María 606888798
 */
public class timeTest {
    /*
    public void generateRandomMasses()
    {
        
        
        
        int limit= 15000;
       
        double maxMass=700;
        List <Double> masses= new LinkedList<>();
        
        for (int i= 0; i<=limit; i++)
        {
            double mass= Math.random()*maxMass;
            //double rt=Math.random()*maxRT;
            //double csDouble=Math.random()*maxCS;
            //int csInt= (int) Math.random()*maxCSInt;
            masses.add(mass);
            System.out.println(mass);
            //retentionTimes.add(rt);
            //Map <Double, Integer> CS= new LinkedHashMap<>();
            //CS.put(csDouble, csInt);
            //compositeSpectra.add(CS);
        }
    }

   
@Test
    public void createFeaturesListAndExperiment () {
        long start = System.currentTimeMillis(); 
        LCMSController controller= new LCMSController();
        String queryMasses = Constantes.NEWDEMOMASSES;
        String queryRetentionTimes = Constantes.NEWDEMORETENTIONTIME;
        String queryCompositeSpectrum = Constantes.NEWDEMOSPECTRUM;

        List<Double> masses = Cadena.extractDoubles(queryMasses);
        int numInputMasses = masses.size();
        List <Double> massesMZFromNeutral= new LinkedList<>();
        for (double mass : masses) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, "m/z", "positive");
                massesMZFromNeutral.add(mass);
            }
        List<Double> retentionTimes = Cadena.getListOfDoubles(queryRetentionTimes, numInputMasses);
        List<Map<Double, Integer>> compositeSpectra = controllers.LCMSControllerAdapter.getListOfCompositeSpectra(queryCompositeSpectrum, numInputMasses);
        List<Feature> significantFeatures;
        significantFeatures = controller.loadFeaturesFromExperiment(massesMZFromNeutral,
                retentionTimes, compositeSpectra, true);
        
        long end = System.currentTimeMillis(); 
        System.out.println("Time create Features List: "+(end-start)+" miliseconds");
        start= end;
        
        
        List <Integer> databases = new LinkedList ();
        databases.add(0);
        databases.add(1);
        databases.add(2);
        List <String> adducts= new LinkedList <>();
        adducts.add("M+H");
        adducts.add("M+K");
        adducts.add("M+Na");
        adducts.add("M+2H");
        adducts.add("M+NH4");
        adducts.add("M+H-H2O");
        adducts.add("M+H+NH4");
        adducts.add("2M+H");
        adducts.add("2M+Na");
        
                
        ExperimentGroupByRT experiment= new ExperimentGroupByRT(significantFeatures, significantFeatures, false,
                10, 0, 0, 0, 0, databases, 0, 1, adducts);
        
        List <FeaturesGroupByRT> allFeaturesGroupByRT= experiment.getAllFeaturesGroupByRT();
        end= System.currentTimeMillis(); 
        System.out.println("Time create Experiment with FeatureGroupBYRT List: "+(end-start)+" miliseconds");
        System.out.println("Time with jdbc: "+MSFacade.JDBCcounter);
        System.out.println("Time creating objects: "+MSFacade.objectLCMSCompoundCounter);
    }
     
 
    @Test 
    public void maxCapacity()
    {
        long start = System.currentTimeMillis(); 
        
        int limit= 150000;
        MSFacade msfacade = new MSFacade();
        List<Double> totalMasses = new LinkedList<>();
        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+2H");
        adducts.add("M+Na");
        adducts.add("M+K");
        adducts.add("M+NH4");
        adducts.add("M+H-H2O");
        List <String> databases= new LinkedList<>();
        databases.add("AllWM");
        List <Integer> databasesInt= DataFromInterfacesUtilities.getDatabasesAsInt(databases);
        int metabolitesType=0;
        double maxMass=700;
        double maxRT=30;
        double maxCS=700;
        int maxCSInt=1000;
        int tolerance= 10;
        List <Double> masses= new LinkedList<>();
        List <Double> retentionTimes= new LinkedList<>();
        List <Map <Double, Integer>> compositeSpectra=new LinkedList<>();
        
        msfacade.JDBCcounter=0;
        msfacade.queryCreation=0;
        msfacade.objectLCMSCompoundCounter=0;
        msfacade.structureCounter=0;
        msfacade.pathwayCounter=0;
        msfacade.LMClassificationCounter=0;
        msfacade.lipidsClassificationCounter=0;
        
        for (int i= 0; i<=limit; i++)
        {
            double mass= Math.random()*maxMass;
            //double rt=Math.random()*maxRT;
            //double csDouble=Math.random()*maxCS;
            //int csInt= (int) Math.random()*maxCSInt;
            masses.add(mass);
            //retentionTimes.add(rt);
            //Map <Double, Integer> CS= new LinkedHashMap<>();
            //CS.put(csDouble, csInt);
            //compositeSpectra.add(CS);
        }
        
        long end = System.currentTimeMillis(); 
        System.out.println("Time creating lists: "+(end-start));
        start= end;
        List <Double> massesMZFromNeutral= new LinkedList();
        LCMSController controller= new LCMSController();
        for (double mass : masses) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, "m/z", "positive");
                massesMZFromNeutral.add(mass);
            }
        end= System.currentTimeMillis();
        System.out.println("Time to neutral: "+(end-start));
        start=end;
         FeaturesRTProcessing.loadFeatures(massesMZFromNeutral, new LinkedList(),
                new LinkedList(), true, "positive", adducts,
                10d, 0, databasesInt, metabolitesType, msfacade);
        end = System.currentTimeMillis(); 
        
        //Advanced:      
        // ---> Create experiment
        //---> Experiment.processcompoundsAdvanced
        System.out.println("Time creating features: "+(end-start)+" miliseconds");
        System.out.println("Time with jdbc: " + msfacade.JDBCcounter);
        System.out.println("Time creating queries: " + msfacade.queryCreation);
        System.out.println("Time creating objects: " + msfacade.objectLCMSCompoundCounter);
        System.out.println("Time creating structures: " + msfacade.structureCounter);
        System.out.println("Time creating pathways: " + msfacade.pathwayCounter);
        System.out.println("Time creating LMClassification: " + msfacade.LMClassificationCounter);
        System.out.println("Time creating lipids_classification: " + msfacade.lipidsClassificationCounter);

    
}*/
     
    @Test
    public void maxCapacityFromExcel() {

        int limit = 10;
        MSFacade msfacade = new MSFacade();
        List<Double> totalMasses = new LinkedList<>();
        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+2H");
        adducts.add("M+Na");
        adducts.add("M+K");
        adducts.add("M+NH4");
        adducts.add("M+H-H2O");
        List <String> databases= new LinkedList<>();
        databases.add("AllWM");
        List <Integer> databasesInt= DataFromInterfacesUtilities.getDatabasesAsInt(databases);
        int metabolitesType=0;
        int chemAlphabet=4;

        msfacade.JDBCcounter=0;
        msfacade.queryCreation=0;
        msfacade.objectLCMSCompoundCounter=0;
        msfacade.structureCounter=0;
        msfacade.pathwayCounter=0;
        msfacade.LMClassificationCounter=0;
        msfacade.lipidsClassificationCounter=0;
        //msfacade.classifierClassificationCounter=0;
        
        
        try {
            totalMasses = ExcelReader.getMassesFromCSV();
        } catch (IOException ex) {
            Logger.getLogger(timeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Double> masses = new LinkedList<>();
        for (int i = 0; i < limit; i++) {
            masses.add(totalMasses.get(i));
        }
        
        long start = System.currentTimeMillis();
        List<Double> massesMZFromNeutral = new LinkedList();
        for (double mass : masses) {
            mass = utilities.Utilities.calculateMZFromNeutralMass(mass, "m/z", "positive");
            massesMZFromNeutral.add(mass);
        }
        long end = System.currentTimeMillis();
        System.out.println("Time getting neutral masses: " + (end - start));

        start = end;
        
        FeaturesRTProcessing.loadFeatures(massesMZFromNeutral, new LinkedList(),
                new LinkedList(), true, "positive", adducts,
                10d, 0, databasesInt, metabolitesType, chemAlphabet, msfacade);

        end = System.currentTimeMillis();
        System.out.println("Time creating features: " + (end - start) + " miliseconds");
        System.out.println("Time with jdbc: " + msfacade.JDBCcounter);
        System.out.println("Time creating queries: " + msfacade.queryCreation);
        System.out.println("Time creating objects: " + msfacade.objectLCMSCompoundCounter);
        System.out.println("Time creating structures: " + msfacade.structureCounter);
        System.out.println("Time creating pathways: " + msfacade.pathwayCounter);
        System.out.println("Time creating LMClassification: " + msfacade.LMClassificationCounter);
        System.out.println("Time creating lipids_classification: " + msfacade.lipidsClassificationCounter);
        //System.out.println("Time cerating classyfier_classifications: " + msfacade.classifierClassificationCounter);

    }
    /*
    @Test
    public void measureJDBCTime ()
    {
        long start = System.currentTimeMillis(); 
        LCMSController controller= new LCMSController();
        String queryMasses = Constantes.NEWDEMOMASSES;
        String queryRetentionTimes = Constantes.NEWDEMORETENTIONTIME;
        String queryCompositeSpectrum = Constantes.NEWDEMOSPECTRUM;

        List<Double> masses = Cadena.extractDoubles(queryMasses);
        int numInputMasses = masses.size();
        List <Double> massesMZFromNeutral= new LinkedList<>();
        for (double mass : masses) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, "m/z", "positive");
                massesMZFromNeutral.add(mass);
            }
        List<Double> retentionTimes = Cadena.getListOfDoubles(queryRetentionTimes, numInputMasses);
        List<Map<Double, Integer>> compositeSpectra = controllers.LCMSControllerAdapter.getListOfCompositeSpectra(queryCompositeSpectrum, numInputMasses);
        List<Feature> significantFeatures;
        significantFeatures = controller.loadFeaturesFromExperiment(massesMZFromNeutral,
                retentionTimes, compositeSpectra, true);
        
        long end = System.currentTimeMillis(); 
        System.out.println("Time create Features List: "+(end-start)+" miliseconds");
        start= end;
        
         List <Integer> databases = new LinkedList ();
        databases.add(0);
        databases.add(1);
        databases.add(2);
        List <String> adducts= new LinkedList <>();
        adducts.add("M+H");
        adducts.add("M+K");
        adducts.add("M+Na");
        adducts.add("M+2H");
        adducts.add("M+NH4");
        adducts.add("M+H-H2O");
        adducts.add("M+H+NH4");
        adducts.add("2M+H");
        adducts.add("2M+Na");
        
         ExperimentGroupByRT experiment= new ExperimentGroupByRT(significantFeatures, significantFeatures, false,
                10, 0, 0, 0, 0, databases, 0, 1, adducts);
        
        List <FeaturesGroupByRT> allFeaturesGroupByRT= experiment.getAllFeaturesGroupByRT();
        end= System.currentTimeMillis(); 
        System.out.println("Time create Experiment with FeatureGroupBYRT List: "+(end-start)+" miliseconds");
        
        
        MSFacade msfacade= new MSFacade();
        List <Feature> features= new NoDuplicatesList();
        FeaturesRTProcessing.setAnnotationsGroupByAdduct(allFeaturesGroupByRT,0.1d, 0, adducts, "positive",databases, 0,msfacade);

    }
     */

}
