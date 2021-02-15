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

import LCMS_FEATURE.EMComparator;
import LCMS_FEATURE.Feature;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import static utilities.Constants.ADDUCT_AUTOMATIC_DETECTION_WINDOW;

/**
 * Class which contains static methods to process algorithms with adducts
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1 10-may-2018
 *
 * @author Alberto Gil de la Fuente. María Postigo. San Pablo-CEU
 */
public class AdductProcessing {

    private AdductProcessing() {

    }

    /**
     * Calculate the neutral mass adding or subtracting the weight of a proton
     * depending on the ionization mode.
     *
     * @param ionizationMode 0 is negative. 1 is positive
     * @param mzMass
     * @return
     */
    public static double calculateNeutralMassFromHAdduct(int ionizationMode, double mzMass) {
        switch (ionizationMode) {
            // positive ionization
            case 1:
                return getMassToSearch(mzMass, "M+H", ionizationMode);
            // negative ionization
            case 2:
                return getMassToSearch(mzMass, "M-H", ionizationMode);

            default:
                return mzMass;
        }
    }

    /**
     * Calculate the m/z mass adding or subtracting the weight of a proton
     * depending on the ionization mode.
     *
     * @param ionizationMode 0 is negative. 1 is positive
     * @param neutralMass
     * @return
     */
    //TODO IMPORTANT
    public static double calculateMZFromHAdduct(int ionizationMode, double neutralMass) {
        switch (ionizationMode) {
            // positive ionization
            case 1:
                return getMassOfAdductFromMonoWeight(neutralMass, "M+H", ionizationMode);
            // negative ionization
            case 2:
                return getMassOfAdductFromMonoWeight(neutralMass, "M-H", ionizationMode);
            default:
                return neutralMass;
        }
    }

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param experimentalMass Experimental mass of the compound
     * @param adduct adduct name (M+H, 2M+H, M+2H, etc..)
     * @param adductValue. numeric value of the adduct (1.0073, etc..)
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double getMassToSearch(Double experimentalMass, String adduct, Double adductValue) {
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedOriginalMass(experimentalMass, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedOriginalMass(experimentalMass, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerOriginalMass(experimentalMass, adductValue, 2);
        } else if (AdductsLists.TRIMER_3.contains(adduct)) {
            massToSearch = getDimmerOriginalMass(experimentalMass, adductValue, 3);
        } else {
            massToSearch = experimentalMass + adductValue;
        }
        return massToSearch;
    }

    /**
     * Calculate the monoisotopic mass based on the adduct mass, without knowing
     * the value of the adduct.
     *
     * @param experimentalMass
     * @param adduct
     * @param ionizationMode
     * @return
     */
    public static Double getMassToSearch(Double experimentalMass, String adduct, int ionizationMode) {
        Double adductValue = getAdductValue(adduct, ionizationMode);
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedOriginalMass(experimentalMass, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedOriginalMass(experimentalMass, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerAdductMass(experimentalMass, adductValue, 2);
        } else if (AdductsLists.TRIMER_3.contains(adduct)) {
            massToSearch = getDimmerAdductMass(experimentalMass, adductValue, 3);
        } else {
            massToSearch = experimentalMass + adductValue;
        }
        return massToSearch;
    }

    private static Double getChargedOriginalMass(double experimentalMass, double adductValue, int charge) {
        double result = experimentalMass;

        result = result + adductValue;
        result = result * charge;

        return result;
    }

    private static Double getDimmerOriginalMass(double experimentalMass, double adductValue, int numberAtoms) {
        double result = experimentalMass;

        result = result + adductValue;
        result = result / numberAtoms;

        return result;
    }

    private static Double getChargedAdductMass(double monoisotopicWeight, double adductValue, int charge) {
        double result = monoisotopicWeight;

        result = result / charge;
        result = result - adductValue;

        return result;
    }

    private static Double getDimmerAdductMass(double monoisotopicWeight, double adductValue, int numberAtoms) {
        double result = monoisotopicWeight;
        result = result * numberAtoms;
        result = result - adductValue;

        return result;
    }

    /**
     * Calculate the adduct Mass based on the monoisotopic weight, without
     * knowing the value of the adduct.
     *
     * @param monoisotopic_weight Experimental mass of the compound
     * @param adduct adduct name (M+H, 2M+H, M+2H, etc..)
     * @param ionizationMode positive, negative or neutral
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double getMassOfAdductFromMonoWeight(Double monoisotopic_weight, String adduct, int ionizationMode) {
        Double adductValue = getAdductValue(adduct, ionizationMode);
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedAdductMass(monoisotopic_weight, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedAdductMass(monoisotopic_weight, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerAdductMass(monoisotopic_weight, adductValue, 2);
        } else if (AdductsLists.TRIMER_3.contains(adduct)) {
            massToSearch = getDimmerAdductMass(monoisotopic_weight, adductValue, 3);
        } else {
            massToSearch = monoisotopic_weight - adductValue;
        }
        return massToSearch;
    }

    /**
     * Calculate the adduct Mass based on the monoisotopic weight
     *
     * @param monoisotopic_weight Experimental mass of the compound
     * @param adduct adduct name (M+H, 2M+H, M+2H, etc..)
     * @param adductValue. numeric value of the adduct (1.0073, etc..)
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double getMassOfAdductFromMonoWeight(Double monoisotopic_weight, String adduct, Double adductValue) {
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedAdductMass(monoisotopic_weight, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedAdductMass(monoisotopic_weight, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerAdductMass(monoisotopic_weight, adductValue, 2);
        } else if (AdductsLists.TRIMER_3.contains(adduct)) {
            massToSearch = getDimmerAdductMass(monoisotopic_weight, adductValue, 3);
        } else {
            massToSearch = monoisotopic_weight - adductValue;
        }
        return massToSearch;
    }

    /**
     * get the string for the ionizationMode from a integer. 0 is negative, 1 is
     * positive any other value is positive by default
     *
     * @param ionizationMode
     * @return
     */
    public static String getStringIonizationModeFromInt(int ionizationMode) {
        String ionMode;
        switch (ionizationMode) {
            case 0:
                ionMode = "negative";
                break;
            case 1:
                ionMode = "positive";
                break;
            default:
                ionMode = "positive";
                break;
        }
        return ionMode;
    }

    /**
     * get the int for the ionizationMode from a String.
     *
     * @param ionizationMode
     * @return 0 if is negative, 1 if is positive 1 by default
     */
    public static int getIntIonizationModeFromString(String ionizationMode) {
        switch (ionizationMode) {
            case "negative":
                return 0;
            case "positive":
                return 1;
            default:
                return 1;
        }
    }

    /**
     * Method to filter the compositeSpectrum and filter the adducts. It is
     * specially useful when looking for adduct and fragment in source
     * relations. The Map compositeSpectrum should be ordered due to their
     * intensity.
     *
     * @param compositeSpectrum // TODO ALBERTO CHANGE IT TO PRIVATE WHEN
     * TESTED!
     * @return
     */
    public static Map<Double, Double> getMonoPeaksFromCS(Map<Double, Double> compositeSpectrum) {
        Map<Double, Double> filteredCS = new TreeMap();
        Double previousPeak = 0d;
        for (Map.Entry<Double, Double> entry : compositeSpectrum.entrySet()) {
            Double mz = entry.getKey();
            Double intensity = entry.getValue();
            if (previousPeak == 0d || Math.abs(mz - previousPeak) > Constants.BIGGEST_ISOTOPE * Constants.PROTON_WEIGHT) {
                filteredCS.put(mz, intensity);
            }
            previousPeak = mz;
        }
        return filteredCS;
    }

    /**
     * Detect the ionization adduct based on the relationships in the Composite
     * Spectrum. The inputMass is handled as m/z
     *
     * @param ionMode 0 neutral, 1 positive 2 negative
     * @param inputMass m/z
     * @param adducts possible adducts to be formed
     * @param compositeSpectrum Signals of CS
     * @return
     */
    public static String detectAdductBasedOnCompositeSpectrum(
            int ionMode,
            Double inputMass,
            List<String> adducts,
            Map<Double, Double> compositeSpectrum) {
        if (compositeSpectrum.isEmpty()) {
            return "";
        }
        Map<Double, Double> filteredCS = getMonoPeaksFromCS(compositeSpectrum);

        String adductDetected = "";
        Map<String, String> mapAdducts;
        double adductDouble;
        double adductDoubleForCheckRelation;
        double massToSearchInCompositeSpectrumForCheckRelation;
        double differenceMassAndPeak;
        List<String> allAdductsForCheckRelation;
        boolean search = false;
        switch (ionMode) {
            case 1: {
                mapAdducts = AdductsLists.MAPMZPOSITIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
                if (ionMode == 1) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + ionMode);
                    return adductDetected;
                }
                break;
            }
            case 2: {
                mapAdducts = AdductsLists.MAPMZNEGATIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
                if (ionMode == 2) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + ionMode);
                }
                break;
            }
            default: {
                mapAdducts = null;
                allAdductsForCheckRelation = null;
            }
        }
        if (search) {

            for (String adductName : adducts) {
                // Hypothesis -> Adduct is adductName
                String adductValue;
                adductValue = mapAdducts.get(adductName);
                if (adductValue == null) {
                    break;
                }
                adductDouble = Double.parseDouble(adductValue);
                Double neutralMassBasedOnAdduct;
                neutralMassBasedOnAdduct = utilities.AdductProcessing.getMassToSearch(inputMass, adductName, adductDouble);

                //System.out.println("HYPOTHESIS -> ADDUCT = " + adductName + " value: " + neutralMassBasedOnAdduct);
                // Hypothesis -> Peak is adductName
                // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                // So now is time to loop the composite spectrum searching the peak
                for (String adductNameForCheckRelation : allAdductsForCheckRelation) {
                    String adductValueForCheckRelation = mapAdducts.get(adductNameForCheckRelation);
                    if (adductValueForCheckRelation == null) {
                        break;
                    }
                    adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                    if (!adductName.equals(adductNameForCheckRelation)) {
                        massToSearchInCompositeSpectrumForCheckRelation
                                = utilities.AdductProcessing.getMassOfAdductFromMonoWeight(neutralMassBasedOnAdduct, adductNameForCheckRelation, adductDoubleForCheckRelation);
                        //System.out.println("  ADDUCT TO SEARCH IN CS: " + adductNameForCheckRelation
                        //        + " VALUE:" + massToSearchInCompositeSpectrumForCheckRelation);
                        // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                        // So now is time to loop the composite spectrum searching the peak
                        for (Double peakInCompositeSpectrum : filteredCS.keySet()) {
//                                    System.out.println("AdductName: " + adductNameForCheckRelation 
//                                            + " value: " + adductValueForCheckRelation + 
//                                            " PEAK COMPOSITE: " + peakInCompositeSpectrum + 
//                                            " SEARCHING PEAK: " + massToSearchInCompositeSpectrumForCheckRelation);
                            differenceMassAndPeak = Math.abs(peakInCompositeSpectrum - massToSearchInCompositeSpectrumForCheckRelation);
//                                    System.out.println("PEAK IN COMPOSITE SPECTRUM: " + peakInCompositeSpectrum + 
//                                            " DIFFERENCE: " + differenceMassAndPeak);
                            if (differenceMassAndPeak < ADDUCT_AUTOMATIC_DETECTION_WINDOW) {
                                adductDetected = adductName;
//                                System.out.println("ADDUCT DETECTED: " + adductDetected + " RELATED TO: " + adductNameForCheckRelation);
                                return adductDetected;
                            }
                        }
                    }
                }
            }
        }
        return adductDetected;
    }

    /**
     * process the provisional map to perform the search. Return provisional Map
     * of adducts
     *
     * @param ionMode Ionization mode (positive, negative or neutral)
     * @return provisionalMap Map to get the value of adducts
     */
    public static Map<String, String> chooseprovisionalMapAdducts(int ionMode) {
        Map<String, String> provisionalMap;
        switch (ionMode) {
            case 1:
                provisionalMap = AdductsLists.MAPMZPOSITIVEADDUCTS;
                break;
            case 2:
                provisionalMap = AdductsLists.MAPMZNEGATIVEADDUCTS;
                break;
            default:
                provisionalMap = AdductsLists.MAPNEUTRALADDUCTS;
                break;
        }
        return provisionalMap;
    }

    /**
     * process the list of adducts to perform the search. Return adducts to
     * search. If the adducts contains all positives or all negatives, it adds
     * all the adducts from the provisionalMap.
     *
     * @param ionMode Ionization mode
     * @param provisionalMap Map to get the value of adducts
     * @param adducts Possible adducts occurred during the experiment
     * @return the list of adducts to search
     */
    public static List<String> chooseAdducts(int ionMode,
            Map<String, String> provisionalMap,
            List<String> adducts) {
        switch (ionMode) {
            case 1:
                if (adducts.isEmpty() || adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE)) {
                    Set set = provisionalMap.keySet();
                    return new LinkedList<>(set);
                }
                break;
            case 2:
                if (adducts.isEmpty() || adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE)) {
                    Set set = provisionalMap.keySet();
                    return new LinkedList<>(set);
                }
                break;
            default:
                Set set = provisionalMap.keySet();
                return new LinkedList<>(set);
        }
        return adducts;

    }

    /**
     * Filter the item for selecting all the adducts from the corresponding
     * ionization mode
     *
     * @param adducts List of adducts that can contain all the adducts from the
     * corresponding ionization mode.
     * @param ionMode Ionization mode
     * @return
     */
    public static List<String> FilterAdductsFromInterface(List<String> adducts, int ionMode) {
        if (adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE)
                || adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE)
                || adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL)
                || adducts.contains("all")) {
            adducts = AdductProcessing.getAllAdducts(ionMode);
        }
        return adducts;
    }

    /**
     * Get all the adducts from the ionization mode
     *
     * @param ionMode Ionization mode
     * @return the list of adducts to search
     */
    public static List<String> getAllAdducts(String ionMode) {
        List<String> adducts;
        switch (ionMode) {
            case "positive":
                adducts = new LinkedList<>(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
                break;
            case "negative":
                adducts = new LinkedList<>(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
                break;
            default:
                adducts = new LinkedList<>(AdductsLists.MAPNEUTRALADDUCTS.keySet());
                break;
        }
        return adducts;
    }

    /**
     * Get all the adducts from the ionization mode
     *
     * @param ionMode Ionization mode (1 positive, 2 negative, 0 neutral.
     * Default neutral)
     * @return the list of adducts to search
     */
    public static List<String> getAllAdducts(int ionMode) {
        List<String> adducts;
        switch (ionMode) {
            case 1:
                adducts = new LinkedList<>(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
                break;
            case 2:
                adducts = new LinkedList<>(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
                break;
            default:
                adducts = new LinkedList<>(AdductsLists.MAPNEUTRALADDUCTS.keySet());
                break;
        }
        return adducts;
    }

    /**
     * Get the value of the adduct named adductName within the ionization mode
     * ionMode
     *
     * @param adductName
     * @param ionMode
     * @return
     */
    private static Double getAdductValue(String adductName, int ionMode) {

        Map<String, String> provisionalMap;
        String adductValue;
        double adductDouble;
        switch (ionMode) {
            case 1:
                provisionalMap = AdductsLists.MAPMZPOSITIVEADDUCTS;
                adductValue = provisionalMap.get(adductName);
                break;
            case 2:
                provisionalMap = AdductsLists.MAPMZNEGATIVEADDUCTS;
                adductValue = provisionalMap.get(adductName);
                break;
            default:
                return 0d;
        }
        adductDouble = Double.parseDouble(adductValue);
        return adductDouble;
    }

    /**
     * Detect the adduct from a feature when in a FGBRT there is one or more
     * features with the adduct autodetected
     *
     * @param experimentalMass
     * @param molecularMass
     * @param ionMode
     * @return
     */
    public static String detectAdductBasedOnAnotherFeatureAdduct(Double experimentalMass, double molecularMass, int ionMode) {

        Map<String, String> mapAdducts;
        String adductDetected = "";
        switch (ionMode) {
            case 1: {
                mapAdducts = AdductsLists.MAPMZPOSITIVEADDUCTS;
                break;
            }
            case 2: {
                mapAdducts = AdductsLists.MAPMZNEGATIVEADDUCTS;
                break;
            }
            default: {
                mapAdducts = null;
            }
        }
        if (mapAdducts == null) {
            return "M";
        }
        List<String> adductNames = new LinkedList(mapAdducts.keySet());
        for (String adductName : adductNames) {
            String adductValue = mapAdducts.get(adductName);
            if (adductValue == null) {
                break;
            }

            double theoreticalAdductMass = getMassOfAdductFromMonoWeight(experimentalMass, adductName, ionMode);
            System.out.println("Evaluating adduct " + adductName + " theoretical Mass: " + theoreticalAdductMass + " vs " + molecularMass);
            double difference = Math.abs((theoreticalAdductMass - molecularMass));
            if (difference < ADDUCT_AUTOMATIC_DETECTION_WINDOW) {
                System.out.println("    ADDUCT DETECTED");
                adductDetected = adductName;
                return adductDetected;
            }

        }
        return adductDetected;
    }

    /**
     * detect adduct based only on the relation of the EM from the features.
     *
     * @param adducts
     * @param features
     * @param ionMode
     */
    public static void detectAdductBasedOnFeaturesRelation(
            List<String> adducts, List<Feature> features, int ionMode) {
        //ex: imputMass=522.98

        String adductDetected;
        Map<String, String> mapAdducts;
        double adductDouble;
        double adductDoubleForCheckRelation;

        List<String> allAdductsForCheckRelation;

        boolean search = false;
        switch (ionMode) {
            case 1: {
                mapAdducts = AdductsLists.MAPMZPOSITIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
                if (ionMode == 1) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "FEATURES GROUP: " + ionMode);
                }
                break;
            }
            case 2: {
                mapAdducts = AdductsLists.MAPMZNEGATIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
                if (ionMode == 2) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "FEATURES GROUP: " + ionMode);
                }
                break;
            }
            default: {
                mapAdducts = null;
                allAdductsForCheckRelation = null;
            }
        }
        if (search) {
            for (Feature featureReference : features) {
                for (String adductName : adducts) {
                    // Hypothesis -> Adduct is adductName
                    Double inputMass = featureReference.getEM();
                    String adductValue;
                    adductValue = mapAdducts.get(adductName);//ej. M+Na
                    if (adductValue == null) {
                        break;
                    }
                    adductDouble = Double.parseDouble(adductValue);//ej.22.98....
                    Double neutralMassBasedOnAdduct;
                    neutralMassBasedOnAdduct = utilities.AdductProcessing.getMassToSearch(inputMass, adductName, adductDouble);//ej.500

                    for (String adductNameForCheckRelation : allAdductsForCheckRelation) {
                        String adductValueForCheckRelation = mapAdducts.get(adductNameForCheckRelation);
                        if (adductValueForCheckRelation == null) {
                            break;
                        }
                        adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                        if (!adductName.equals(adductNameForCheckRelation)) {
                            Double difference = 10d;
                            Double massValueToBeCorrespondantAdduct = neutralMassBasedOnAdduct - adductDoubleForCheckRelation;
                            Double featureMassValue = 0d;
                            for (Feature feature : features) {
                                if (feature.getEM() != inputMass) {
                                    featureMassValue = feature.getEM();
                                    difference = Math.abs(featureMassValue - massValueToBeCorrespondantAdduct);
                                    if (difference < ADDUCT_AUTOMATIC_DETECTION_WINDOW) {
                                        adductDetected = adductName;
                                        if (featureReference.getAdductAutoDetected().equals("") && !featureReference.isIsAdductAutoDetected()) {
                                            featureReference.setAdductAutoDetected(adductDetected);
                                            featureReference.setIsAdductAutoDetected(true);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void establishRelationshipAmongFeaturesFromMultipleAdducts(List<Feature> features, int ionMode) {

        List<Feature> featuresWithAdductAutoDetected = getFeaturesWithAdductAutoDetected(features);
        List<Feature> featuresWithoutAdductAutoDetected = getFeaturesWithoutAdductAutoDetected(features);
        Collections.sort(featuresWithAdductAutoDetected, new EMComparator());
        Collections.sort(featuresWithoutAdductAutoDetected, new EMComparator());

        for (Feature featureWithAdductAutoDetected : featuresWithAdductAutoDetected) {
            Double inputMass = featureWithAdductAutoDetected.getEM();
            String adduct = featureWithAdductAutoDetected.getAdductAutoDetected();
            Double neutralMassToSearch = AdductProcessing.getMassOfAdductFromMonoWeight(inputMass, adduct, ionMode);

            for (Feature feature : featuresWithoutAdductAutoDetected) {

                String adductDetected = detectAdductBasedOnAnotherFeatureAdduct(neutralMassToSearch, feature.getEM(), ionMode);
                if (!adductDetected.equals("") && !feature.isIsAdductAutoDetected()) {
                    feature.setAdductAutoDetected(adductDetected);
                    feature.setIsAdductAutoDetected(true);
                }

            }
        }

    }

    private static List<Feature> getFeaturesWithAdductAutoDetected(List<Feature> features) {
        List<Feature> featuresWithAdductAutoDetected = new LinkedList<>();
        for (Feature feature : features) {
            if (!feature.getAdductAutoDetected().equals("")) {
                featuresWithAdductAutoDetected.add(feature);
            }
        }
        return featuresWithAdductAutoDetected;
    }

    private static List<Feature> getFeaturesWithoutAdductAutoDetected(List<Feature> features) {
        List<Feature> featuresWithAdductAutoDetected = new LinkedList<>();
        for (Feature feature : features) {
            if (feature.getAdductAutoDetected().equals("")) {
                featuresWithAdductAutoDetected.add(feature);
            }
        }
        return featuresWithAdductAutoDetected;
    }

}
