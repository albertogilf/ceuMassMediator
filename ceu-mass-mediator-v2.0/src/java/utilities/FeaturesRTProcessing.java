/*
 * FeaturesRTProcessing.java
 *
 * Created on 02-jun-2018, 16:52:49
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.EMComparator;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import LCMS.RTComparator;
import List.NoDuplicatesList;
import facades.MSFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import msms.MSMSCompound;
import msms.Peak;
import static utilities.Constantes.TOLERANCE_SAME_MASS_WITHIN_FEATUREGROUPEDBYRT;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 02-jun-2018
 *
 * @author Alberto Gil de la Fuente
 */
public final class FeaturesRTProcessing {

    /**
     * Creates a new instance of FeaturesRTProcessing
     */
    private FeaturesRTProcessing() {
    }

    /**
     * This method returns a set of features grouped by retention time. The same
     * feature can't be in the same group (a feature is considered the same if
     * it has the same experimental mass and retention time). Features with RT=0
     * will be alone within a featuresGroupByRT object
     *
     * @param features
     * @param RT_window
     * @return
     */
    public static List<FeaturesGroupByRT> groupFeaturesByRT(List<Feature> features, Double RT_window) {

        if (features == null || features.isEmpty()) {
            return new LinkedList<>();

        }

        List<FeaturesGroupByRT> featuresGroupedByRTList = new NoDuplicatesList();
        // 1. Sort features by RT
        sortFeaturesByRT(features);
        Iterator it = features.iterator();
        double start, end, midpoint;
        Feature feature;
        FeaturesGroupByRT featuresGroupByRTItem;

        feature = (Feature) it.next();
        Boolean isThereMoreFeatures = true;
        while (isThereMoreFeatures) {
            // 2. Determine the window
            // if RT=0 include the feature in its own featuresGroupByRT
            midpoint = feature.getRT();
            end = midpoint + Constantes.RT_WINDOW / 2;
            start = midpoint - Constantes.RT_WINDOW / 2;
            featuresGroupByRTItem = new FeaturesGroupByRT(midpoint);

            if (midpoint != 0d) {
                while (feature.getRT() >= start && feature.getRT() < end) {
                    //System.out.println("Feature em " + feature.getEM() + " rt " + feature.getRT());
                    featuresGroupByRTItem.addFeature(feature);

                    if (it.hasNext()) {
                        feature = (Feature) it.next();
                    } else {
                        isThereMoreFeatures = false;
                        break;
                    }
                }
                featuresGroupedByRTList.add(featuresGroupByRTItem);

            } else {
                // RT = 0, then there is no RT
                featuresGroupByRTItem = new FeaturesGroupByRT(0);
                featuresGroupByRTItem.addFeature(feature);

                //System.out.println("Feature em " + feature.getEM() + " rt " + feature.getRT());
                featuresGroupedByRTList.add(featuresGroupByRTItem);

                if (it.hasNext()) {
                    feature = (Feature) it.next();
                } else {
                    isThereMoreFeatures = false;
                }
            }
        }

        return featuresGroupedByRTList;
    }

    public static List<Feature> getOnlySignificativeFeatures(List<Feature> allFeatures,
            boolean isAllFeatures) {
        List<Feature> significativeFeatures = new LinkedList<>();
        if (isAllFeatures) {
            significativeFeatures = allFeatures;
        } else {
            for (Feature feature : allFeatures) {
                if (feature.isIsSignificativeFeature()) {
                    significativeFeatures.add(feature);
                }
            }
        }
        return significativeFeatures;
    }

    /**
     * Set all the significant features as significant, and the features of
     * allFeatures as significant or not depending if they are included or not
     * in the significant set within a tolerance window.
     *
     * @param significantFeatures
     * @param allFeatures
     */
    public static void setSignificantFeatures(List<Feature> significantFeatures, List<Feature> allFeatures) {

        for (Feature significant : significantFeatures) {
            significant.setIsSignificativeFeature(true);
        }
        for (Feature all : allFeatures) {
            if (significantFeatures.contains(all)) {
                all.setIsSignificativeFeature(true);
            } else {
                all.setIsSignificativeFeature(false);
            }
        }

    }

    /**
     * Method which obtains the Significant Features from the list of all
     * Features. If All Features are significant, then it returns all of them.
     *
     * @param allFeaturesGroupByRT
     * @param isAllFeatures
     * @return
     */
    public static List<FeaturesGroupByRT> getOnlySignificativeFeaturesGroupedByRT(List<FeaturesGroupByRT> allFeaturesGroupByRT,
            boolean isAllFeatures) {
        List<FeaturesGroupByRT> significativeFeaturesGroupedByRT = new LinkedList<>();
        if (!isAllFeatures) {
            significativeFeaturesGroupedByRT = allFeaturesGroupByRT;
        } else {
            for (FeaturesGroupByRT featureGroupedByRT : allFeaturesGroupByRT) {
                boolean isThereAnySignificativeFeature = false;
                FeaturesGroupByRT significativeFeaturesGroupByRT = null;
                for (Feature feature : featureGroupedByRT.getFeatures()) {
                    if (feature.isIsSignificativeFeature()) {
                        if (!isThereAnySignificativeFeature) {
                            Double RTFeature = featureGroupedByRT.getRT();
                            significativeFeaturesGroupByRT = new FeaturesGroupByRT(RTFeature);
                            significativeFeaturesGroupByRT.addFeature(feature);
                            isThereAnySignificativeFeature = true;
                        } else {
                            significativeFeaturesGroupByRT.addFeature(feature);
                        }
                    }
                }
                if (isThereAnySignificativeFeature) {
                    significativeFeaturesGroupedByRT.add(significativeFeaturesGroupByRT);
                }
            }
        }
        return significativeFeaturesGroupedByRT;
    }

    /**
     * Sort a list of features by descending RT
     *
     * @param features
     */
    public static void sortFeaturesByRT(List<Feature> features) {
        Collections.sort(features, new RTComparator());
    }

    /**
     * This method check if two masses should be considered the same when they
     * are within the same featuresGroupedByRT. If they have the same neutral
     * mass (due to the adduct), it is not needed to perform different searches.
     * Not for ddbb comparison, used just for comparing among features within
     * the same featuresGroupedByRT. This method supposes an optimization, it
     * avoids to perform the same compounds search over the same mass. (example.
     * F1 (501, M+H) F2 (538, M+K) the mass for both would be 500 so avoid to
     * perform the compound search twice.
     *
     * @param hypoteticalNeutralMass
     * @param auxhypoteticalNeutralMass
     * @return a boolean telling if the masses are considered the same
     */
    public static final boolean isMassWithinTolerance(Double hypoteticalNeutralMass, Double auxhypoteticalNeutralMass) {
        if (hypoteticalNeutralMass == 0) {
            return false;
        }
        if (auxhypoteticalNeutralMass == 0) {
            return false;
        }
        return (auxhypoteticalNeutralMass - TOLERANCE_SAME_MASS_WITHIN_FEATUREGROUPEDBYRT <= hypoteticalNeutralMass
                && auxhypoteticalNeutralMass + TOLERANCE_SAME_MASS_WITHIN_FEATUREGROUPEDBYRT > hypoteticalNeutralMass);
    }

    /**
     * Set the value of the attribute "adductAutoDetected" of each feature
     * within the list of FeaturesGroupByRT objects. The value of
     * "adductAutoDetected" is set only if the adduct is autodetected from the
     * feature's composite spectra.
     *
     * @param featuresGroups_byRT
     * @param ionMode positive or negative
     * @param adducts
     */
    public static void setAdductsDetectedFromCS(List<FeaturesGroupByRT> featuresGroups_byRT, String ionMode, List<String> adducts) {
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
     * Set the value of the attribute "adductAutoDetected" of each feature
     * within the list of FeaturesGroupByRT objects. The value of
     * "adductAutoDetected" is set only if the adduct is autodetected from the
     * relationship among features. For example F(EM,RT): F1 (8,501), F2 (8,
     * 520) F3 (8,538)--> F1=M+H, F2=M+Na, F3=M+K from a compound with mass 500.
     *
     * @param featuresGroups_byRT
     * @param adducts
     * @param ionMode
     */
    public static void setRelationshipAmongFeatures(List<FeaturesGroupByRT> featuresGroups_byRT, List<String> adducts, String ionMode) {

        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();

            System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            Collections.sort(features, new EMComparator());
            int numberAdductsAutoDetected = getNumberOfAdductsAutodetected(features);
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
    }

    /**
     * Return de number of features within the features list that have its
     * adduct autodetected
     *
     * @param features
     * @return
     */
    private static int getNumberOfAdductsAutodetected(List<Feature> features) {
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

    /**
     * This method returns the molecular weight of the feature with an adduct
     * autodetected
     *
     * @param features a list of features where just one adduct was autodetected
     * @param ionMode
     * @return the neutral mass of the autodetected adduct (ex. F1(538, M+K)
     * returns 500)
     */
    private static double getMolecularMassFromDetectedAdduct(List<Feature> features, String ionMode) {
        double molecularMass = -1;
        for (Feature feature : features) {
            if (feature.isIsAdductAutoDetected()) {
                String adductName = feature.getAdductAutoDetected();
                double mzMass = feature.getEM();
                molecularMass = AdductProcessing.getMassToSearch(mzMass, adductName, ionMode);
            }
        }
        return molecularMass;
    }

    /**
     * Create the List of compounds group by adduct for the feature feature and
     * all the adducts in the list adducts
     *
     * @param feature
     * @param tolerance
     * @param toleranceMode
     * @param adducts List of adducts to search in.
     * @param ionMode
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet
     * @param msfacade
     */
    public static void setAnnotationsGroupByAdduct(Feature feature,
            Double tolerance, Integer toleranceMode,
            List<String> adducts, String ionMode,
            List<Integer> databases, int metabolitesType,
            int chemAlphabet, MSFacade msfacade) {

        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = null;

        /*
        System.out.println("Tolerance: " + tolerance + " " + toleranceMode);
        System.out.println("adducts: " + adducts);
        System.out.println("Ionization mode: " + ionMode);
        System.out.println("databases: " + databases);
        System.out.println("Metabolites type: " + metabolitesType);
         */
        if (feature.isIsAdductAutoDetected()) {
            String adductAutoDetected = feature.getAdductAutoDetected();
            compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(
                    feature, adductAutoDetected, tolerance, toleranceMode, ionMode, 
                    databases, metabolitesType, chemAlphabet, msfacade);
            feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
            for (String adduct : adducts) {
                // There will be an item CompoundsLCMSGroupByAdduct for each adduct
                if (!feature.getAdductAutoDetected().equals(adduct)) {
                    //System.out.println(">>>> ADDUCT: " + adduct);
                    compoundsGroupByAdduct = FeaturesRTProcessing.getCompoundsGroupEmptyByAdductFromFeatureAndAdduct(feature, adduct);

                    feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                }
            }
        } else {
            for (String adduct : adducts) {
                //System.out.println(">>>> ADDUCT: " + adduct);
                compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(
                        feature, adduct, tolerance, toleranceMode, ionMode, databases, 
                        metabolitesType, chemAlphabet, msfacade);

                feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
            }
        }
    }

    /**
     * Set the annotation over the list of Features Grouped By Adduct
     *
     * @param featuresGroups_byRT
     * @param tolerance
     * @param toleranceMode
     * @param adducts
     * @param ionMode
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet
     * @param msfacade
     */
    public static void setAnnotationsGroupByAdduct(List<FeaturesGroupByRT> featuresGroups_byRT,
            Double tolerance, Integer toleranceMode,
            List<String> adducts, String ionMode,
            List<Integer> databases, int metabolitesType,
            int chemAlphabet, MSFacade msfacade) {
        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = null;
        CompoundsLCMSGroupByAdduct aux = null;

        //System.out.println("Entering setting");
        //System.out.println("Number of FGBRT: " + featuresGroups_byRT.size());
        //System.out.println("Tolerance: " + tolerance + " " + toleranceMode);
        if (adducts.contains("allPositives") || adducts.contains("allNegatives") || adducts.contains("allNeutral")) {
            adducts = AdductProcessing.getAllAdducts(ionMode);
        }
        //System.out.println("adducts: " + adducts);
        //System.out.println("Ionization mode: " + ionMode);
        //System.out.println("databases: " + databases);
        //System.out.println("Metabolites type: " + metabolitesType);

        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();
            //System.out.println("Number of features: " + features.size());
            Double auxhypoteticalNeutralMass = -13d;

            for (Feature feature : features) {

                Double hypoteticalNeutralMass = feature.getHypotheticalNeutralMass();
                boolean withinTolerance = FeaturesRTProcessing.isMassWithinTolerance(hypoteticalNeutralMass, auxhypoteticalNeutralMass);
                if (withinTolerance && feature.isIsAdductAutoDetected()) {
                    //System.out.println("ENTERING SAME HYPOTETICAL NEUTRAL MASS " + hypoteticalNeutralMass);
                    double EM = feature.getEM();
                    double RT = feature.getRT();
                    Map<Double, Integer> CS = feature.getCS();
                    String adduct = feature.getAdductAutoDetected();
                    List<CompoundLCMS> compounds = compoundsGroupByAdduct.getCompounds();
                    aux = new CompoundsLCMSGroupByAdduct(EM, RT, CS, adduct, compounds);
                    feature.getAnnotationsGroupedByAdduct().add(aux);

                }
                auxhypoteticalNeutralMass = feature.getHypotheticalNeutralMass();

                for (String adduct : adducts) {
                    // There will be an item CompoundsLCMSGroupByAdduct for each adduct
                    if (!feature.isIsAdductAutoDetected()) {

                        //System.out.println(">>>> LIST OF ADDUCTS: " + adduct);
                        compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct,
                                tolerance, toleranceMode, ionMode,
                                databases, metabolitesType, chemAlphabet, msfacade);

                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                        long end = System.currentTimeMillis();

                    } else if (feature.getAnnotationsGroupedByAdduct().isEmpty() && feature.isIsAdductAutoDetected()) {
                        adduct = feature.getAdductAutoDetected();
                        compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct,
                                tolerance, toleranceMode, ionMode,
                                databases, metabolitesType, chemAlphabet, msfacade);

                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);

                        break;
                    }
                }
            }
        }
    }

    /**
     * This method searches in the database for a set of compounds that have a
     * mass according to an adduct and the feature EM.
     *
     * @param feature
     * @param adduct
     * @param tolerance
     * @param toleranceMode
     * @param ionMode
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet
     * @param msFacade
     *
     * @return CompoundsLCMSGroupByAdduct from a specified adduct
     */
    public static CompoundsLCMSGroupByAdduct getCompoundsGroupByAdductFromFeatureAndAdduct(
            Feature feature, String adduct,
            Double tolerance, Integer toleranceMode,
            String ionMode, List<Integer> databases,
            int metabolitesType, int chemAlphabet, MSFacade msFacade) {

        // System.out.println("Entering get compounds");
        // proper variables of LCMSCompound: 
        double EM = feature.getEM();
        //double massToSearch = getMassOfAdductFromMonoWeight(EM,adduct,ionMode); DOUBT, WHICH METHOD SHOUD I USE
        //System.out.println("Experimental mass: " + EM);
        double massToSearch = AdductProcessing.getMassToSearch(EM, adduct, ionMode);
        //System.out.println("Monoisotopic mass to search " + massToSearch + " considering adduct " + adduct);
        Double RT = feature.getRT();
        //System.out.println("Retention time: " + RT);
        Map<Double, Integer> CS = feature.getCS();
        CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS,
                adduct, new LinkedList<>());
        // System.out.println("Compounds group by adduct: " + compoundsLCMSGroupByAdduct.toString());

        List<CompoundLCMS> compoundsFromDB;
        compoundsFromDB = msFacade.getCompoundsFromExperiment_byMassAndTolerance(
                feature, massToSearch, adduct, tolerance, toleranceMode, databases, metabolitesType, chemAlphabet);

        compoundsLCMSGroupByAdduct.setCompounds(compoundsFromDB);

        return compoundsLCMSGroupByAdduct;
    }

    /**
     * This method return an empty compoundsLCMSGroupByAdduct for the cases
     * where the adduct is autoDetected and there is no need to search over
     * different adducts.
     *
     * @param feature
     * @param adduct
     *
     * @return CompoundsLCMSGroupByAdduct from a specified adduct
     */
    public static CompoundsLCMSGroupByAdduct getCompoundsGroupEmptyByAdductFromFeatureAndAdduct(
            Feature feature, String adduct) {
        //System.out.println("Entering EMPTY get compounds");
        // proper variables of LCMSCompound: 
        double EM = feature.getEM();
        //double massToSearch = getMassOfAdductFromMonoWeight(EM,adduct,ionMode); DOUBT, WHICH METHOD SHOUD I USE
        // System.out.println("Experimental mass: " + EM);
        Double RT = feature.getRT();
        //System.out.println("Retention time: " + RT);
        Map<Double, Integer> CS = feature.getCS();
        CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS,
                adduct, new LinkedList<>());

        return compoundsLCMSGroupByAdduct;
    }

    /**
     * Set the possibleParentCompounds list for those features without
     * adductAutoDetected. If the feature can be a fragment of a compound the
     * attribute possibleFragment is set to true. Example F(EM, RT, adduct, List
     * <Compounds>): F1 (501, 8, M+H, [a,b,c]), F2 (538, 8, M+K, [a,b,c]) -
     * a,b,c are compounds with molecular weight 500 - F3(278, 8, null, [])
     * check msms from compounds: at an "a" msms we find a peak with mz 278,
     * therefore: F3 is a possible fragment of compound "a".
     *
     * @param featuresGroups_byRT
     * @param msfacade
     */
    public static void setFragments(List<FeaturesGroupByRT> featuresGroups_byRT, MSFacade msfacade) {
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

            List<Integer> compoundIDs = new LinkedList<>();
            List<Feature> features = fgbrt.getFeatures();
            List<Feature> featuresWithAdduct = getFeaturesWithAdductAutoDetected(features);
            List<Feature> featuresWithoutAdduct = features;
            featuresWithoutAdduct.removeAll(featuresWithAdduct);

            for (Feature featureWithAdduct : featuresWithAdduct) {
                hypotheticalMass = featureWithAdduct.getHypotheticalNeutralMass();
                if (!FeaturesRTProcessing.isMassWithinTolerance(hypotheticalMass, aux)) {
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
                                        int id = compound.getCompound_id();

                                        if (!compoundIDs.contains(id)) {
                                            compoundIDs.add(id);
                                            featureWithoutAdduct.getPossibleParentCompounds().add(compound);
                                            featureWithoutAdduct.setPossibleFragment(true);
                                            System.out.println("FEATURE WITH EM: " + featureWithoutAdduct.getEM() + " CAN BE A FRAGMENT OF COMPOUND " + compound.getCompound_id());
                                        }

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

    /**
     * Check if within a list of FeaturesGroupByRT there is one of more features
     * without adductAutoDetected
     *
     * @param featuresGroups_byRT
     * @return
     */
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

    /**
     * Get a list of the features with adductAutoDetected from a list of
     * features.
     *
     * @param features
     * @return
     */
    private static List<Feature> getFeaturesWithAdductAutoDetected(List<Feature> features) {
        List<Feature> featuresWithAdduct = new LinkedList<>();
        for (Feature f : features) {
            if (f.isIsAdductAutoDetected()) {
                featuresWithAdduct.add(f);
            }
        }
        return featuresWithAdduct;
    }

    /**
     * Check if featureWithoutAdduct can be a fragment of compound with id=
     * compound_id
     *
     * @param compound_id
     * @param featureWithoutAdduct
     * @param tolerance
     * @param msfacade
     * @return
     */
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

    /**
     * Return a list of features with the experimental masses, retention times
     * and composite spectra introduced as input. The method can search directly
     * the annotations from the database or not depending on
     * searchAnnotationsInDatabase (true or false)
     *
     * @param experimentalMasses List of experimental masses
     * @param retentionTimes List of retention times
     * @param compositeSpectra List of composite spectrandicates if the feature
     * is significative or not
     * @param searchAnnotationsInDatabase The annotations from the database will
     * be loaded
     * @param ionizationMode positive, negative or neutral
     * @param adducts list of adducts to search
     * @param databases list of databases to search. (HMDB -> 1, LipidMaps -> 2,
     * Metlin -> 3, Kegg ->4, In_house ->5, MINE -> 6)
     * @param toleranceMode 0 (ppm) or 1 (Da)
     * @param tolerance Tolerance value
     * @param metabolitesType 0 all except peptides, 1 for lipids, 2 for all
     * including peptides.
     * @param msFacade Object with a database connection for annotations.
     * @return the features
     */
    public static List<Feature> loadFeatures(List<Double> experimentalMasses, List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectra, boolean searchAnnotationsInDatabase,
            String ionizationMode, List<String> adducts, Double tolerance, Integer toleranceMode,
            List<Integer> databases, Integer metabolitesType, int chemAlphabet, MSFacade msFacade) {
        int numInputMasses = experimentalMasses.size();

        List<Boolean> isSignificativeFeatures = new ArrayList<>(Collections.nCopies(numInputMasses, true));
        return loadFeatures(experimentalMasses, retentionTimes, compositeSpectra, isSignificativeFeatures,
                searchAnnotationsInDatabase, ionizationMode, adducts, tolerance, toleranceMode,
                databases, metabolitesType, chemAlphabet, msFacade);
    }

    /**
     * Return a list of features with the experimental masses, retention times
     * and composite spectra introduced as input. The method can search directly
     * the annotations from the database or not depending on
     * searchAnnotationsInDatabase (true or false)
     *
     * @param experimentalMasses List of experimental masses
     * @param retentionTimes List of retention times
     * @param compositeSpectra List of composite spectra
     * @param isSignificativeFeatures list of boolean that indicates if the
     * feature is significative or not
     * @param searchAnnotationsInDatabase The annotations from the database will
     * be loaded
     * @param ionizationMode positive, negative or neutral
     * @param adducts list of adducts to search
     * @param databases list of databases to search. (HMDB -> 1, LipidMaps -> 2,
     * Metlin -> 3, Kegg ->4, In_house ->5, MINE -> 6)
     * @param toleranceMode 0 (ppm) or 1 (Da)
     * @param tolerance Tolerance value
     * @param metabolitesType 0 all except peptides, 1 for lipids, 2 for all
     * including peptides.
     * @param chemAlphabet
     * @param msFacade Object with a database connection for annotations.
     * @return the features
     */
    public static List<Feature> loadFeatures(List<Double> experimentalMasses, List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectra, List<Boolean> isSignificativeFeatures,
            boolean searchAnnotationsInDatabase, String ionizationMode, List<String> adducts, Double tolerance,
            Integer toleranceMode, List<Integer> databases, Integer metabolitesType, int chemAlphabet, MSFacade msFacade) {
        List<Feature> featuresFromInputData = new NoDuplicatesList();

        int numInputMasses = experimentalMasses.size();
        int numRTs = retentionTimes.size();
        int numCS = compositeSpectra.size();

        double EM;
        double RT;
        Map<Double, Integer> CS;
        boolean isSignificativeFeature;
        int ionizationModeAsInt = DataFromInterfacesUtilities.ionizationModeToInteger(ionizationMode);
        for (int i = 0; i < numInputMasses; i++) {
            EM = experimentalMasses.get(i);
            RT = i < numRTs ? retentionTimes.get(i) : 0;
            CS = i < numCS ? compositeSpectra.get(i) : new LinkedHashMap();

            isSignificativeFeature = isSignificativeFeatures.get(i);
            String adductAutoDetected;
            adductAutoDetected = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionizationMode, EM, adducts, CS);
            boolean isAdductAutodetected;
            isAdductAutodetected = !adductAutoDetected.equals("");
            Feature feature = new Feature(EM, RT, CS, isAdductAutodetected, adductAutoDetected, isSignificativeFeature,
                    new LinkedList<>(), ionizationModeAsInt);

            if (searchAnnotationsInDatabase) {

                FeaturesRTProcessing.setAnnotationsGroupByAdduct(feature, tolerance, toleranceMode, adducts,
                        ionizationMode, databases, metabolitesType, chemAlphabet, msFacade);

            }
            featuresFromInputData.add(feature);

        }
        return featuresFromInputData;
    }
}
