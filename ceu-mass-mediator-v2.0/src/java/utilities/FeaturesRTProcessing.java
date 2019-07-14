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

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.CompoundsLCMSGroupByAdduct;
import LCMS_FEATURE.EMComparator;
import LCMS_FEATURE.EMInverseComparator;
import LCMS_FEATURE.Feature;
import LCMS_FEATURE.FeaturesGroupByRT;
import LCMS_FEATURE.RTComparator;
import List.NoDuplicatesList;
import facades.MSFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import msms.MSMSCompound;
import msms.Peak;
import static utilities.Constants.TOLERANCE_SAME_MASS_WITHIN_FEATUREGROUPEDBYRT;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 02-jun-2018
 *
 * @author Alberto Gil de la Fuente. María Postigo. San Pablo-CEU
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
            end = midpoint + RT_window / 2;
            start = midpoint - RT_window / 2;
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
    public static void setAdductsDetectedFromCS(List<FeaturesGroupByRT> featuresGroups_byRT, int ionMode, List<String> adducts) {
        //System.out.println("\n\nPredicting adduct from CS: ");
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            //System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            List<Feature> features = featuresGroup_byRT.getFeatures();
            for (Feature feature : features) {
                String adductAutodetected = AdductProcessing.detectAdductBasedOnCompositeSpectrum(
                        ionMode, feature.getEM(), adducts, feature.getCS());
                if (!adductAutodetected.equals("")) {
                    feature.setAdductAutoDetected(adductAutodetected);
                    feature.setIsAdductAutoDetected(true);
                }
                //System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
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
    public static void setRelationshipAmongFeatures(List<FeaturesGroupByRT> featuresGroups_byRT,
            List<String> adducts, int ionMode) {
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();

            Collections.sort(features, new EMComparator());
            int numberAdductsAutoDetected = getNumberOfAdductsAutodetected(features);
            if (numberAdductsAutoDetected > 1) {
                //PREDICTING ADDUCTS IF MORE THAN ONE ADDUCT IS AUTODETECTED
                AdductProcessing.establishRelationshipAmongFeaturesFromMultipleAdducts(features, ionMode);
                Collections.sort(features, new EMComparator());
            } else if (numberAdductsAutoDetected == 1) {
                //PREDICTING ADDUCT IF ONE ADDUCT IS AUTODETECTED
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
            } else if (numberAdductsAutoDetected == 0) {
                //If no adducts were detected from the composite spectra, detect the by checking the relationship among features
                //  Autodetect adduct by relation between features 
                //PREDICTING ADDUCTS IF NO ADDUCT IS AUTODETECED
                Collections.sort(features, new EMComparator());
                AdductProcessing.detectAdductBasedOnFeaturesRelation(adducts, features, ionMode);
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
    private static double getMolecularMassFromDetectedAdduct(List<Feature> features, int ionMode) {
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
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3;
     * ALL, 4; ALLD, 5
     * @param msfacade
     */
    public static void setAnnotationsGroupByAdduct(Feature feature,
            Double tolerance, Integer toleranceMode, List<String> adducts, int EMMode, int ionizationMode,
            List<Integer> databases, int metabolitesType, int chemAlphabet, MSFacade msfacade) {
        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct;
        if (feature.isIsAdductAutoDetected()) {
            String adductAutoDetected = feature.getAdductAutoDetected();
            compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(
                    feature, adductAutoDetected, tolerance, toleranceMode, EMMode, ionizationMode,
                    databases, metabolitesType, chemAlphabet, msfacade);
            feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
            for (String adduct : adducts) {
                if (!feature.getAdductAutoDetected().equals(adduct)) {
                    compoundsGroupByAdduct
                            = FeaturesRTProcessing.getCompoundsGroupEmptyByAdductFromFeatureAndAdduct(
                                    feature, adduct, EMMode, ionizationMode);
                    feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                }
            }
        } else {
            for (String adduct : adducts) {
                compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(
                        feature, adduct, tolerance, toleranceMode, EMMode, ionizationMode, databases,
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
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3;
     * ALL, 4; ALLD, 5
     * @param msfacade
     */
    public static void setAnnotationsGroupByAdduct(List<FeaturesGroupByRT> featuresGroups_byRT,
            Double tolerance, Integer toleranceMode, List<String> adducts, int EMMode, int ionizationMode,
            List<Integer> databases, int metabolitesType, int chemAlphabet, MSFacade msfacade) {
        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = null;
        CompoundsLCMSGroupByAdduct aux;

        adducts = AdductProcessing.FilterAdductsFromInterface(adducts, ionizationMode);
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            List<Feature> features = featuresGroup_byRT.getFeatures();
            Double auxhypoteticalNeutralMass = -13d;

            for (Feature feature : features) {
                Double hypoteticalNeutralMass = feature.getHypotheticalNeutralMass();
                boolean withinTolerance = FeaturesRTProcessing.isMassWithinTolerance(hypoteticalNeutralMass, auxhypoteticalNeutralMass);
                if (withinTolerance && feature.isIsAdductAutoDetected()) {
                    //same hypothetical neutral mass
                    double EM = feature.getEM();
                    double RT = feature.getRT();
                    Map<Double, Double> CS = feature.getCS();
                    String adduct = feature.getAdductAutoDetected();
                    List<CompoundLCMS> compounds = compoundsGroupByAdduct.getCompounds();
                    aux = new CompoundsLCMSGroupByAdduct(EM, RT, CS, adduct, EMMode, ionizationMode, compounds);
                    feature.getAnnotationsGroupedByAdduct().add(aux);
                }
                auxhypoteticalNeutralMass = feature.getHypotheticalNeutralMass();

                for (String adduct : adducts) {
                    if (!feature.isIsAdductAutoDetected()) {
                        compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct,
                                tolerance, toleranceMode, EMMode, ionizationMode,
                                databases, metabolitesType, chemAlphabet, msfacade);
                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                        long end = System.currentTimeMillis();
                    } else if (feature.getAnnotationsGroupedByAdduct().isEmpty() && feature.isIsAdductAutoDetected()) {
                        adduct = feature.getAdductAutoDetected();
                        compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct,
                                tolerance, toleranceMode, EMMode, ionizationMode,
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
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3;
     * ALL, 4; ALLD, 5
     * @param msFacade
     *
     * @return CompoundsLCMSGroupByAdduct from a specified adduct
     */
    public static CompoundsLCMSGroupByAdduct getCompoundsGroupByAdductFromFeatureAndAdduct(
            Feature feature, String adduct, Double tolerance, Integer toleranceMode, int EMMode,
            int ionizationMode, List<Integer> databases, int metabolitesType, int chemAlphabet, MSFacade msFacade) {
        double EM = feature.getEM();
        Double RT = feature.getRT();
        Map<Double, Double> CS = feature.getCS();
        CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS,
                adduct, EMMode, ionizationMode, new LinkedList<>());
        List<CompoundLCMS> compoundsFromDB;
        compoundsFromDB = msFacade.getCompoundsFromExperiment_byMassAndTolerance(
                feature, EM, adduct, tolerance, toleranceMode, databases,
                metabolitesType, chemAlphabet, EMMode, ionizationMode);
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
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     *
     * @return CompoundsLCMSGroupByAdduct from a specified adduct
     */
    public static CompoundsLCMSGroupByAdduct getCompoundsGroupEmptyByAdductFromFeatureAndAdduct(
            Feature feature, String adduct, int EMMode, int ionizationMode) {
        double EM = feature.getEM();
        Double RT = feature.getRT();
        Map<Double, Double> CS = feature.getCS();
        CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS,
                adduct, EMMode, ionizationMode, new LinkedList<>());
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
     * @param tolerance
     * @param msfacade
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public static void setFragments(List<FeaturesGroupByRT> featuresGroups_byRT,
            double tolerance, MSFacade msfacade, int ionizationMode) {
        //short the features by EM (lower to greater)
        boolean isAnyFeatureWithoutAdductAutodetected = isAnyFeatureWithoutAdductAutodetected(featuresGroups_byRT);
        if (!isAnyFeatureWithoutAdductAutodetected) {
            //if all the features have an adduct autodetected there is not fragment within the group
            return;
        }
        for (FeaturesGroupByRT featuresgroup : featuresGroups_byRT) {
            List<Feature> features = featuresgroup.getFeatures();
            Collections.sort(features, new EMInverseComparator());
            List<Feature> auxiliarFeatures = new LinkedList<>();
            List<CompoundsLCMSGroupByAdduct> compoundsToSearchForMSMS = new LinkedList<>();
            List<Integer> compoundIDs = new LinkedList<>();
            auxiliarFeatures.addAll(features);

            for (Feature smallestFeature : features) {
                //get the feature with lowest EM and check if it is a fragment of any other feature
                //System.out.println("Smallest feature: " + smallestFeature.toString() + " with ion mode " + ionMode);
                auxiliarFeatures.remove(smallestFeature);
                if (auxiliarFeatures.size() > 0) {//SEE ALBERTO
                    //System.out.println("Will be compared against " + auxiliarFeatures.size() + " features");
                    double mzSmallestFeature = smallestFeature.getEM();
                    //System.out.println("neutral mass smalles feature: " + adductSmallestFeatureNM);
                    List<String> adducts = AdductProcessing.getAllAdducts(ionizationMode);
                    for (String adduct : adducts) {
                        //System.out.println("Hypothesis fragment " + adduct);
                        // first hypothesis (M+H), first adduct. 
                        // Second hypothesis (M+Na) seccond adduct, etc..
                        // This is the mass which should appear in the MSMS.
                        double hypotheticalFragmentMass = AdductProcessing.getMassToSearch(mzSmallestFeature, adduct, ionizationMode);
                        //Now hypotetical fragment mass is neutral: so que must become it mz
                        if (ionizationMode == 1) {
                            hypotheticalFragmentMass = hypotheticalFragmentMass + Constants.PROTON_WEIGHT;
                        }
                        if (ionizationMode == 2) {
                            hypotheticalFragmentMass = hypotheticalFragmentMass - Constants.PROTON_WEIGHT;
                        }
                        //System.out.println("Neutral mass "+adductSmallestFeatureNM+" Adduct mass :"+hypotheticalFragmentMass);
                        List<String> parentAdducts;
                        switch (ionizationMode) {
                            case 1:
                                parentAdducts = AdductsLists.MAPPOSITIVEADDUCTFRAGMENT.get(adduct);
                                break;
                            case 2:
                                parentAdducts = AdductsLists.MAPPOSITIVEADDUCTFRAGMENT.get(adduct);
                                break;
                            case 0:
                                parentAdducts = new LinkedList<>();
                                parentAdducts.add(AdductsLists.MAPNEUTRALADDUCTS.get(adduct));
                                break;
                            default:
                                parentAdducts = new LinkedList<>();
                                parentAdducts.add(AdductsLists.MAPNEUTRALADDUCTS.get(adduct));
                                break;
                        }
                        if (parentAdducts != null) {
                            if (auxiliarFeatures.isEmpty()) {
                                return;
                            }
                            for (Feature greaterFeature : auxiliarFeatures) {
                                compoundsToSearchForMSMS = greaterFeature.getAnnotationsGroupedByAdduct();
                                for (CompoundsLCMSGroupByAdduct compoundsGroup : compoundsToSearchForMSMS) {
                                    if (compoundsGroup != null) {
                                        String adductFromTheCompoundLCMS = compoundsGroup.getAdduct();
                                        // Only search if the adduct of the parent ion is in the map<hijo,List<padres>);
                                        if (parentAdducts.contains(adductFromTheCompoundLCMS)) {
                                            //System.out.println("Parent adduct " + adductFromTheCompoundLCMS);
                                            List<CompoundLCMS> compounds = compoundsGroup.getCompounds();
                                            for (CompoundLCMS compound : compounds) {
                                                int id = compound.getCompound_id();
                                                boolean isAPossibleFragment = checkIfIsPossibleFragment(id, hypotheticalFragmentMass, tolerance, msfacade, ionizationMode);

                                                if (isAPossibleFragment) {
                                                    if (!compoundIDs.contains(id)) {
                                                        compoundIDs.add(id);
                                                        smallestFeature.addPossibleParentCompounds(adduct, compound);
                                                        smallestFeature.setPossibleFragment(true);
                                                        //System.out.println("FEATURE WITH EM: " + featureWithoutAdduct.getEM() + " CAN BE A FRAGMENT OF COMPOUND " + compound.getCompound_id());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    compoundIDs = new LinkedList<>();
                }
            }
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
     * compound_id according to a specific adduct and ionizationMode
     *
     * @param compound_id
     * @param tolerance
     * @param msfacade
     * @return
     */
    private static boolean checkIfIsPossibleFragment(int compound_id, double possibleFragmentMass, double tolerance, MSFacade msfacade, int ionMode) {

        List<MSMSCompound> msmsCompounds = msfacade.getMSMSFromCompoundIDandIonMode(compound_id, ionMode);
        for (MSMSCompound msms : msmsCompounds) {
            List<Peak> peaks = msms.getAbsolutePeaks();
            for (Peak p : peaks) {
                //System.out.println(possibleFragmentMass + " vs " + p.getMz());
                if ((possibleFragmentMass - tolerance) <= p.getMz() && p.getMz() <= (possibleFragmentMass + tolerance)) {
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
     * @param massesMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts list of adducts to search
     * @param databases list of databases to search. (HMDB -> 1, LipidMaps -> 2,
     * Metlin -> 3, Kegg ->4, In_house ->5, MINE -> 6)
     * @param toleranceMode 0 (ppm) or 1 (Da)
     * @param tolerance Tolerance value
     * @param metabolitesType 0 all except peptides, 1 for lipids, 2 for all
     * including peptides.
     * @param chemAlphabet CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3;
     * ALL, 4; ALLD, 5
     * @param msFacade Object with a database connection for annotations.
     * @return the features
     */
    public static List<Feature> loadFeatures(List<Double> experimentalMasses, List<Double> retentionTimes,
            List<Map<Double, Double>> compositeSpectra, boolean searchAnnotationsInDatabase, int massesMode,
            int ionizationMode, List<String> adducts, Double tolerance, Integer toleranceMode,
            List<Integer> databases, Integer metabolitesType, int chemAlphabet, MSFacade msFacade) {
        int numInputMasses = experimentalMasses.size();

        List<Boolean> isSignificativeFeatures = new ArrayList<>(Collections.nCopies(numInputMasses, true));
        return loadFeatures(experimentalMasses, retentionTimes, compositeSpectra, isSignificativeFeatures,
                searchAnnotationsInDatabase, massesMode, ionizationMode, adducts, tolerance, toleranceMode,
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
     * @param massesMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts list of adducts to search
     * @param databases list of databases to search. (HMDB -> 1, LipidMaps -> 2,
     * Metlin -> 3, Kegg ->4, In_house ->5, MINE -> 6)
     * @param toleranceMode 0 (ppm) or 1 (Da)
     * @param tolerance Tolerance value
     * @param metabolitesType 0 all except peptides, 1 for lipids, 2 for all
     * including peptides.
     * @param chemAlphabet CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3;
     * ALL, 4; ALLD, 5
     * @param msFacade Object with a database connection for annotations.
     * @return the features
     */
    public static List<Feature> loadFeatures(List<Double> experimentalMasses, List<Double> retentionTimes,
            List<Map<Double, Double>> compositeSpectra, List<Boolean> isSignificativeFeatures,
            boolean searchAnnotationsInDatabase, int massesMode, int ionizationMode, List<String> adducts, Double tolerance,
            Integer toleranceMode, List<Integer> databases, Integer metabolitesType, int chemAlphabet, MSFacade msFacade) {
        List<Feature> featuresFromInputData = new NoDuplicatesList();
        int numInputMasses = experimentalMasses.size();
        int numRTs = retentionTimes.size();
        int numCS = compositeSpectra.size();

        double EM;
        double RT;
        Map<Double, Double> CS;
        boolean isSignificativeFeature;
        for (int i = 0; i < numInputMasses; i++) {
            EM = experimentalMasses.get(i);
            RT = i < numRTs ? retentionTimes.get(i) : 0;
            CS = i < numCS ? compositeSpectra.get(i) : new TreeMap();

            isSignificativeFeature = isSignificativeFeatures.get(i);
            String adductAutoDetected;
            adductAutoDetected = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionizationMode, EM, adducts, CS);
            boolean isAdductAutodetected;
            isAdductAutodetected = !adductAutoDetected.equals("");
            Feature feature = new Feature(EM, RT, CS, isAdductAutodetected, adductAutoDetected, isSignificativeFeature,
                    new LinkedList<>(), massesMode, ionizationMode);
            if (searchAnnotationsInDatabase) {
                FeaturesRTProcessing.setAnnotationsGroupByAdduct(feature, tolerance, toleranceMode, adducts,
                        massesMode, ionizationMode, databases, metabolitesType, chemAlphabet, msFacade);
            }
            featuresFromInputData.add(feature);
        }
        return featuresFromInputData;
    }
}
