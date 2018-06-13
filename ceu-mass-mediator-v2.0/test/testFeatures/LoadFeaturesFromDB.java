/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFeatures;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.EMComparator;
import LCMS.ExperimentGroupByRT;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import LCMS.RTComparator;
import List.NoDuplicatesList;
import facades.MSFacade;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import msms.MSMSCompound;
import msms.Peak;
import static testFeatures.LoadFeaturesfromStrings.loadFeaturesFromExperiment;
import utilities.AdductProcessing;
import static utilities.AdductProcessing.*;
import utilities.DataFromInterfacesUtilities;
import utilities.FeaturesRTProcessing;

/**
 *
 * @author María Class to test. 18/05/2018
 */
public class LoadFeaturesFromDB {

    public static void main(String[] args) {
        MSFacade msfacade = new MSFacade();
        Double myTolerance = 50d;
        String experimentalMasses = "501.007276"//M+H
                + "\n522.989218"//M+Na
                + "\n538.963158"//M+K
                + "\n343.963158"//M+K
                + "\n306.007276"//M+H
                + "\n112.0505"
                + "\n47.0133"
                + "\n201.00728"
                + "\n182.9968";// M+H

        String retentionTimes = "18.842525"
                + "\n18.842525"
                + "\n18.842525"
                + "\n18.842525"
                + "\n18.852442"
                + "\n18.852442"
                + "\n22.345"
                + "\n22.345"
                + "\n22.345";
        String compositeSpectra = "(400.3432, 307034.88)(401.34576, 73205.016)(402.3504, 15871.166)(403.35446, 2379.5325)(404.3498, 525.92053)"
                + "\n(422.32336, 1562.7301)(423.3237, 564.0795)(424.33255, 292.2923)"
                + "\n(422.32336, 1562.7301)(423.3237, 564.0795)(424.33255, 292.2923)"
                + "\n(470.5, 1832.6085)(471.432, 468.8131)"
                + "\n(485.29334, 3261.4624)(486.29334, 1148.7113)"
                + "\n(570.3551, 15104.281)(571.3582, 5156.9814)(572.36224, 1203.8275)"
                + "\n(80.0003, 15104.281)"
                + "(573.3646, 101.370476)(592.33673, 1409.5024)(593.3393, 543.91895)(594.3383, 262.465)"
                + "\n(201.00728, 15104.281)( 222.989218, 5156.9814), (238.963158 1203.8275)"
                + "\n (180,1300)";
        /*
        Feature f = new Feature (399.3367, 0, CS, true, compositeSpectra, true, new LinkedList(), 0);
        f.addAnnotationGroupByAdduct(annotationsGroupByAdduct);
         */
        // 1. Create features (will be taken from and experiment in real process
        List<Feature> features = loadFeaturesFromExperiment(experimentalMasses, retentionTimes, compositeSpectra);

        if (features.isEmpty()) {
            System.out.println("Features null");
        } else {
            System.out.println("features size: " + features.size());
        }
        //2. Group features by rt
        //Experiment attributes
        List<Feature> significantFeatures = features;
        List<Feature> allFeatures = features;
        //      List <FeaturesGroupByRT> featuresGroupByRt is created within Experiment constructor

        int tolerance = 10;
        int tolerance_type = 0;
        int chemicalAlphabet = 1;
        int modifier = 0;
        int metabolitesType = 0;
        int chemAlphabet = 4;
        List<Integer> databases = new LinkedList<>();
        databases.add(1);
        int inputMassesMode = 1;
        String ionMode = "positive";
        int ionizationMode = 0;
        //String massesMode = "mz";

        //attributes needed for querying
        //List<FeaturesGroupByRT> featuresGroups_byRT, Double tolerance, String toleranceMode, 
        //    List<String> adducts, String ionMode, List <String> databases, int metabolitesType, MSFacade msfacade
        String toleranceMode = "mDa";

        Integer toleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(toleranceMode);

        List<String> adducts;
        adducts = AdductProcessing.getAllAdducts(ionMode);

        Collections.sort(features, new RTComparator());
        // 2. Group features by RT (is within experiment constructor)
        ExperimentGroupByRT experimentGroupByRT;
        experimentGroupByRT = new ExperimentGroupByRT(significantFeatures, allFeatures, true, tolerance,
                tolerance_type, chemicalAlphabet, modifier,
                metabolitesType, databases, inputMassesMode, ionizationMode, adducts);

        // 3. Detect adduct from CS
        List<FeaturesGroupByRT> featuresGroups_byRT = experimentGroupByRT.getAllFeaturesGroupByRT();
        System.out.println("out " + featuresGroups_byRT.size());
        for (FeaturesGroupByRT featureGroup : featuresGroups_byRT) {
            Collections.sort(featureGroup.getFeatures(), new EMComparator());
        }
        settingAdductsPredictedFromCS(featuresGroups_byRT, ionMode, adducts);

        // 4. Detect adduct from relation among features
        settingRelationshipAmongFeatures(featuresGroups_byRT, adducts, ionMode);

        //5. For each feature obtain the list of compounds group by adduct  
        setCompoundsGroupByAdduct(featuresGroups_byRT, myTolerance, toleranceModeAsInt, adducts, ionMode, 
                databases, metabolitesType, chemAlphabet, msfacade);
        //printResults(featuresGroups_byRT);

        //check if the unidentified adducts belongs to fragments
        settingFragments(featuresGroups_byRT, msfacade);

        //print results:
        printResults(featuresGroups_byRT);

    }

    public static void printResults(List<FeaturesGroupByRT> featuresGroups_byRT) {
        System.out.println("****FINAL OUTPUT*****");
        System.out.println("Number of features groups: " + featuresGroups_byRT.size());
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            System.out.println("FGBRT RT: " + featuresGroup_byRT.getRT());
            System.out.println("Number of features: " + featuresGroup_byRT.getFeatures().size());
            for (Feature f : featuresGroup_byRT.getFeatures()) {
                System.out.println("    FEATURE EM: " + f.getEM() + " ADDUCT: " + f.getAdductAutoDetected());
                if (f.isPossibleFragment()) {
                    System.out.println("        ---> is a possible fragment.");
                }
                if (!f.getPossibleParentCompounds().isEmpty() || f.isPossibleFragment()) {
                    List<CompoundLCMS> parentCompounds = f.getPossibleParentCompounds();
                    for (CompoundLCMS parentCompound : parentCompounds) {
                        System.out.println("           ---->Fragment from " + parentCompound.getCompound_name() + " id: " + parentCompound.getCompound_id());
                    }

                } else if (!f.getAnnotationsGroupedByAdduct().isEmpty()) {
                    for (CompoundsLCMSGroupByAdduct cg : f.getAnnotationsGroupedByAdduct()) {
                        if (cg != null) {
                            System.out.println("Compounds size: " + cg.getCompounds().size());
                            System.out.println("        Compounds group by Adduct: " + cg.getAdduct());
                            for (CompoundLCMS c : cg.getCompounds()) {
                                System.out.println("            Compound " + c.getCompound_name() + " with mass " + c.getMass());
                            }
                        }
                    }
                }
            }
        }
    }

    public static void setCompoundsGroupByAdduct(
            List<FeaturesGroupByRT> featuresGroups_byRT, Double tolerance, Integer toleranceMode, List<String> adducts,
            String ionMode, List<Integer> databases, int metabolitesType, int chemAlphabet, MSFacade msfacade) {
        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = null;

        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();
            Double auxhypoteticalNeutralMass = 0d;
            for (Feature feature : features) {

                Double hypoteticalNeutralMass = feature.getHypotheticalNeutralMass();
                boolean withinTolerance = isMassWithinTolerance(hypoteticalNeutralMass, auxhypoteticalNeutralMass);
                if (withinTolerance && feature.isIsAdductAutoDetected()) {
                    System.out.println("ENTERING SAME HYPOTETICAL NEUTRAL MASS " + hypoteticalNeutralMass);
                    feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                }
                auxhypoteticalNeutralMass = feature.getHypotheticalNeutralMass();

                for (String adduct : adducts) {
                    //System.out.println(">>>> LIST OF ADDUCTS: "+adduct);
                    // There will be an item CompoundsLCMSGroupByAdduct for each adduct
                    if (!feature.isIsAdductAutoDetected()) {
                        compoundsGroupByAdduct = FeaturesRTProcessing.getCompoundsGroupByAdductFromFeatureAndAdduct(
                                feature, adduct, tolerance, toleranceMode, ionMode,
                                databases, metabolitesType, chemAlphabet, msfacade);
                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                    } else {
                        if (feature.getAnnotationsGroupedByAdduct().isEmpty()) {
                            adduct = feature.getAdductAutoDetected();
                            compoundsGroupByAdduct = FeaturesRTProcessing.getCompoundsGroupByAdductFromFeatureAndAdduct(
                                    feature, adduct, tolerance, toleranceMode, ionMode,
                                    databases, metabolitesType, chemAlphabet, msfacade);
                            feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                        }
                        break;
                    }
                }
                //System.out.println(feature.toString());

            }
        }
    }

    public static void settingAdductsPredictedFromCS(List<FeaturesGroupByRT> featuresGroups_byRT, String ionMode, List<String> adducts) {
        System.out.println("\n\nPredicting adduct from CS: ");
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {

            System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            List<Feature> features = featuresGroup_byRT.getFeatures();
            for (Feature feature : features) {

                String adductAutodetected = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, feature.getEM(), adducts, feature.getCS());
                if (!adductAutodetected.equals("")) {
                    feature.setAdductAutoDetected(adductAutodetected);
                    feature.setIsAdductAutoDetected(true);
                }
                System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
            }
        }
    }

    /**
     * This method obtains the adducts corresponding to the realion between
     * features
     *
     * @param featuresGroups_byRT
     * @param adducts
     * @param ionMode
     */
    public static void settingRelationshipAmongFeatures(List<FeaturesGroupByRT> featuresGroups_byRT, List<String> adducts, String ionMode) {

        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();

            System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            Collections.sort(features, new EMComparator());
            int numberAdductsAutoDetected = isMoreThanOneAdductAutoDetectedByCS(features);
            if (numberAdductsAutoDetected > 1) {
                System.out.println("PREDICTING ADDUCTS IF MORE THAN ONE ADDUCT IS AUTODETECTED");
                //we will update the features so we remove the old ones

                AdductProcessing.establishRelationshipAmongFeaturesFromMultipleAdducts(features, ionMode);
                //Para que las features estén ordenadas en función del compuesto al que corresponden, basta con ordenarlas por masa 
                Collections.sort(features, new EMComparator());
                /*
                for (Feature feature:features)
                {
                    System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
                }
                 */
                //now we add the new ones sorted
                //featuresGroup_byRT.setFeatures(features);
                for (Feature feature : features) {
                    System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
                }
            } else if (numberAdductsAutoDetected == 1) {
                System.out.println("PREDICTING ADDUCT IF ONE ADDUCT IS AUTODETECTED");

                //If the adduct was detected from the Composite Spectra for a feature in the group, use it to detect the rest 
                Double molecularMass = getMolecularMassFromDetectedAdduct(features, ionMode);

                for (Feature feature : features) {
                    String adductName = AdductProcessing.detectAdductBasedOnAnotherFeatureAdduct(molecularMass, feature.getEM(), ionMode);
                    boolean isAdductAutoDetected = feature.isIsAdductAutoDetected();
                    if ((!adductName.equals("")) && (!isAdductAutoDetected)) {
                        feature.setAdductAutoDetected(adductName);
                        feature.setIsAdductAutoDetected(true);
                    }
                }

                //featuresGroup_byRT.setFeatures(features);
                for (Feature feature : features) {
                    System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
                }
            } else if (numberAdductsAutoDetected == 0) {//If no adducts were detected from the composite spectra, detect the by checking the relationship among features
                // 4. Autodetect adduct by relation between features (similar al de ALberto)
                System.out.println("PREDICTING ADDUCTS IF NO ADDUCT IS AUTODETECED");

                Collections.sort(features, new EMComparator());
                AdductProcessing.detectAdductBasedOnFeaturesRelation(adducts, features, ionMode);

                //featuresGroup_byRT.setFeatures(features);
                for (Feature feature : features) {
                    System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
                }
            }

        }
        //return  featuresGroups_byRT;
    }

    private static int isMoreThanOneAdductAutoDetectedByCS(List<Feature> features) {
        int count = 0;
        for (Feature feature : features) {
            if (!feature.getAdductAutoDetected().equals("")) {
                count++;
                if (count > 1) {
                    return count;
                }
            }
        }
        return count;
    }
    //  Method to obtain the molecular mass from a set of features grouped by retention time.

    private static double getMolecularMassFromDetectedAdduct(List<Feature> features, String ionMode) {
        double molecularMass = -1;
        for (Feature feature : features) {
            if (feature.isIsAdductAutoDetected()) {
                String adductName = feature.getAdductAutoDetected();
                double mzMass = feature.getEM();
                molecularMass = getMassOfAdductFromMonoWeight(mzMass, adductName, ionMode);
            }
        }
        return molecularMass;
    }

    private static boolean isMassWithinTolerance(Double hypoteticalNeutralMass, Double auxhypoteticalNeutralMass) {
        if (hypoteticalNeutralMass == 0) {
            return false;
        }
        if (auxhypoteticalNeutralMass == 0) {
            return false;
        }
        return (auxhypoteticalNeutralMass - 0.01 <= hypoteticalNeutralMass && auxhypoteticalNeutralMass + 0.01 > hypoteticalNeutralMass);
    }

    //TODO
    private static void settingFragments(List<FeaturesGroupByRT> featuresGroups_byRT, MSFacade msfacade) {
        double tolerance = 0.1;
        //if there arent features without adduct aoutodetected we suppose that we dont have fragments
        boolean isAnyFeatureWithoutAdductAutodetected = isAnyFeatureWithoutAdductAutodetected(featuresGroups_byRT);
        if (!isAnyFeatureWithoutAdductAutodetected) {
            return;
        }

        double hypotheticalMass = 0, aux = 0;
        List<CompoundsLCMSGroupByAdduct> compoundsToSearchForMSMS;

        //For each feature, check the anotations (dont repeat them)
        for (FeaturesGroupByRT fgbrt : featuresGroups_byRT) {

            List<Feature> features = fgbrt.getFeatures();
            List<Feature> featuresWithAdduct = getFeaturesWithAdduct(features);
            List<Feature> featuresWithoutAdduct = features;
            featuresWithoutAdduct.removeAll(featuresWithAdduct);

            for (Feature featureWithAdduct : featuresWithAdduct) {
                hypotheticalMass = featureWithAdduct.getHypotheticalNeutralMass();
                if (!isMassWithinTolerance(hypotheticalMass, aux)) {
                    compoundsToSearchForMSMS = featureWithAdduct.getAnnotationsGroupedByAdduct();

                    System.out.println("Searching peaks over compounds from mass within " + (hypotheticalMass - tolerance) + " and " + (hypotheticalMass + tolerance));
                    for (Feature featureWithoutAdduct : featuresWithoutAdduct) {
                        System.out.println("Check if feature with mass " + featureWithoutAdduct.getEM() + " is a fragment");
                        for (CompoundsLCMSGroupByAdduct compoundsGroup : compoundsToSearchForMSMS) {
                            System.out.println("entring loop");
                            if (compoundsGroup != null) {
                                System.out.println("entring if");
                                List<CompoundLCMS> compounds = compoundsGroup.getCompounds();
                                System.out.println("Will check fragment compatibility for " + compounds.size() + " compounds");
                                for (CompoundLCMS compound : compounds) {
                                    boolean isAPossibleFragment = checkIfIsPossibleFragment(compound.getCompound_id(), featureWithoutAdduct, tolerance, msfacade);
                                    if (isAPossibleFragment) {

                                        featureWithoutAdduct.getPossibleParentCompounds().add(compound);
                                        featureWithoutAdduct.setPossibleFragment(true);
                                        System.out.println("FEATURE WITH EM: " + featureWithoutAdduct.getEM() + " CAN BE A FRAGMENT OF COMPOUND " + compound.getCompound_id());
                                    }
                                }
                            }

                        }

                    }
                }
                aux = hypotheticalMass;
            }
            features.addAll(featuresWithAdduct);

        }
    }

    private static boolean isAnyFeatureWithoutAdductAutodetected(List<FeaturesGroupByRT> featuresGroups_byRT) {
        for (FeaturesGroupByRT featureGroup : featuresGroups_byRT) {
            List<Feature> features = featureGroup.getFeatures();
            for (Feature feature : features) {
                if (!feature.isIsAdductAutoDetected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    private static boolean checkIfIsPossibleFragment(double hypoteticalNeutralMass, double tolerance, double possibleFragmentMass, MSFacade msfacade) {

        List<MSMSCompound> MsmsCompounds = msfacade.getMSMSFromCompoundByMassAndTolerance(hypoteticalNeutralMass, tolerance);
        for (MSMSCompound msmsCompound : MsmsCompounds) {
            List<Peak> peaks = msmsCompound.getAbsolutePeaks();
            for (Peak peak : peaks) {
                double peakMass = peak.getMz();
                if ((peakMass - 0.01) <= possibleFragmentMass && (peakMass + 0.01) >= possibleFragmentMass) {
                    return true;
                }
            }
        }
        return false;
    }
     */
 /*
    private static List<Double> getMassesToSearchForMSMS(List<FeaturesGroupByRT> featuresGroups_byRT, double tolerance) {

        Double hypotheticalMass, aux = 0d;
        List<Double> massesToSearch = new LinkedList<>();
        for (FeaturesGroupByRT fgbrt : featuresGroups_byRT) {
            List<Feature> features = fgbrt.getFeatures();
            for (Feature feature : features) {
                if (feature.isIsAdductAutoDetected()) {
                    hypotheticalMass = feature.getHypotheticalNeutralMass();
                    if (!isMassWithinTolerance(hypotheticalMass, aux)) {
                        massesToSearch.add(hypotheticalMass);
                    }
                }
                aux = feature.getHypotheticalNeutralMass();

            }
        }
        System.out.println("--------Masses to search in DDBB: " + massesToSearch.size());
        return massesToSearch;
    }
     */
    private static List<Feature> getFeaturesWithAdduct(List<Feature> features) {
        List<Feature> featuresWithAdduct = new NoDuplicatesList();
        for (Feature f : features) {
            if (f.isIsAdductAutoDetected()) {
                featuresWithAdduct.add(f);
            }
        }
        return featuresWithAdduct;
    }

    private static boolean checkIfIsPossibleFragment(int compound_id, Feature featureWithoutAdduct, double tolerance, MSFacade msfacade) {

        System.out.println("Cheking if possible fragment from compound with id " + compound_id);
        double possibleFragmentMass = featureWithoutAdduct.getEM();
        List<MSMSCompound> MsmsCompounds = msfacade.getMSMSFromCompoundID(compound_id);
        System.out.println("Got " + MsmsCompounds.size() + " msms to check.");
        for (MSMSCompound msmsCompound : MsmsCompounds) {
            List<Peak> peaks = msmsCompound.getAbsolutePeaks();
            System.out.println("Msms with " + peaks.size() + " peaks");
            for (Peak peak : peaks) {
                double peakMass = peak.getMz();
                System.out.println("Comparing possible fragment mass: " + possibleFragmentMass + " against peak: " + peak.getMz());
                if ((peakMass - tolerance) <= possibleFragmentMass && (peakMass + tolerance) >= possibleFragmentMass) {
                    System.out.println("!!!! MATCH");
                    featureWithoutAdduct.setPossibleFragment(true);
                    return true;
                }
            }
        }
        return false;

    }

}
