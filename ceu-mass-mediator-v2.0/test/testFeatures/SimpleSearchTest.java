/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFeatures;

import LCMS.Experiment;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import List.NoDuplicatesList;
import facades.MSFacade;
import java.util.LinkedList;
import java.util.List;
import utilities.DataFromInterfacesUtilities;

/**
 *
 * @author María 606888798
 */
public class SimpleSearchTest {

    public static void main(String[] args) {
        double inputMass = 305;
        String inputMassMode = "m/z";
        int inputMassModeAsInt = DataFromInterfacesUtilities.inputMassModeToInteger(inputMassMode);
        double tolerance = 100;
        String toleranceType = "mDa";
        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(toleranceType);
        List<Integer> databases = new LinkedList<>();
        databases.add(1);
        databases.add(2);
        int metabolitesType = 1;
        int chemAlphabet = 4;
        String ionizationMode = "positive";
        int ionizationModeAsInt = DataFromInterfacesUtilities.ionizationModeToInteger(ionizationMode);
        List<String> adducts = new LinkedList<>();
        adducts.add("M+H");
        adducts.add("M+K");
        adducts.add("M+Na");
        boolean isSignificative = true;
        boolean isAllFeatures = true;
        Feature feature = new Feature(inputMass, isSignificative, ionizationModeAsInt);
        NoDuplicatesList features = new NoDuplicatesList();
        features.add(feature);
        FeaturesGroupByRT featuresGroupByRT = new FeaturesGroupByRT(0, features);
        List<FeaturesGroupByRT> featuresGroupsByRT = new LinkedList<>();
        featuresGroupsByRT.add(featuresGroupByRT);
        int chemicalAphabet = 4; //4 is all
        int modifier = 0;
        Experiment experiment = new Experiment(features, features, isAllFeatures, (int) tolerance,
                toleranceTypeAsInt, chemicalAphabet, modifier, metabolitesType,
                databases, inputMassModeAsInt, ionizationModeAsInt, adducts);

        MSFacade msfacade = new MSFacade();

        LoadFeaturesFromDB.setCompoundsGroupByAdduct(featuresGroupsByRT, tolerance, toleranceTypeAsInt, 
                adducts, ionizationMode, databases, metabolitesType, chemAlphabet, msfacade);

        LoadFeaturesFromDB.printResults(featuresGroupsByRT);
    }

}
