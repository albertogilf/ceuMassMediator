/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFeatures;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.EMComparator;
import LCMS.Experiment;
import LCMS.ExperimentGroupByRT;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import LCMS.RTComparator;
import List.NoDuplicatesList;
import compound.Classyfire_Classification;
import compound.Compound;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pathway.Pathway;
import utilities.AdductProcessing;
import utilities.Cadena;
import static utilities.Cadena.extractFirstDouble;
import static utilities.AdductProcessing.getMassOfAdductFromMonoWeight;
import static utilities.AdductProcessing.getMassOfAdductFromMonoWeight;
import static utilities.AdductProcessing.getMassToSearch;

/**
 *
 * @author María 606888798
 *
 */
public class LoadFeaturesfromStrings {

    /**
     * Extract the complete compositeSpectrum introduced in the composite
     * Spectrum inputTextArea.
     *
     * @param chain
     * @return
     */
    static public List<Map<Double, Integer>> extractDataSpectrum(String chain) {
        //ArrayList because it is acceded by index
        List<Map<Double, Integer>> dataSpectrum = new ArrayList<Map<Double, Integer>>();
        for (String line : chain.split("\\n")) {
            dataSpectrum.add(extractOneSpectrum(line));
        }
        return dataSpectrum;
    }

    //Doubt, these methods are from theoretical compoundController and Cadena, should I copy them in a different class?
    private static List<Map<Double, Integer>> getListOfCompositeSpectra(String input, int numInputMasses) {
        List<Map<Double, Integer>> spectrumAux;
        if (!input.equals("")) {
            spectrumAux = extractDataSpectrum(input);
            // If there is no time for all queryMasses, fill with 0
            for (int i = spectrumAux.size(); i < numInputMasses; i++) {
                spectrumAux.add(new LinkedHashMap<Double, Integer>());
            }
        } else {
            spectrumAux = new ArrayList<Map<Double, Integer>>();
            // If there is no time for all queryMasses, fill with 0
            for (int i = 0; i < numInputMasses; i++) {
                spectrumAux.add(new LinkedHashMap<Double, Integer>());
            }

        }
        return spectrumAux;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Integer extractSecondNumberToInteger(String chain) {
        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
        Integer ocurrence = 0;

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(chain);

        if (m.find()) {
            if (m.find()) {
                ocurrence = (int) (Float.parseFloat(m.group()));
            }
        }

        return ocurrence;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Map<Double, Integer> extractOneSpectrum(String chain) {
        Map<Double, Integer> peaks;
        peaks = new LinkedHashMap<Double, Integer>();
        // Two lines. Ugly way for detecting all the cases
        // (\([0-9]+\.[0-9]+\,\s*[0-9]+\.[0-9]+\))|(\([0-9]+\,\s*[0-9]+\.[0-9]+\))
        // |(\([0-9]+\.[0-9]+\,\s*[0-9]+\))|(\([0-9]+\,\s*[0-9]+\))
        // Smart way.
        // (\([0-9]+\.?[0-9]*\,\s*[0-9]+\.?[0-9]*\))
        String peakPattern = "(\\([0-9]+\\.?[0-9]*\\,\\s*[0-9]+\\.?[0-9]*\\))";
        String peak;
        Double peakValue;
        Integer peakIntensity;

        Pattern p = Pattern.compile(peakPattern);

        Matcher m = p.matcher(chain);

        while (m.find()) {
            peak = m.group();
            peakValue = extractFirstDouble(peak);
            peakIntensity = extractSecondNumberToInteger(peak);
            // System.out.println("GROUP: " + peak + " Value: " + peakValue + " INTENSITY: " + peakIntensity);
            peaks.put(peakValue, peakIntensity);
        }
        return peaks;
    }

    /**
     * This methods returns a list of features from tree strings containing a
     * set of experimental mases, retentions times and composites spectra.
     *
     * @param queryExperimentalMasses
     * @param queryRetentionTimes
     * @param queryCompositeSpectra
     * @return
     */
    public static List <Feature> loadFeaturesFromExperiment(String queryExperimentalMasses, String queryRetentionTimes, String queryCompositeSpectra) {

        List <Feature> features = new NoDuplicatesList();
        List<Double> experimentalMasses = Cadena.extractDoubles(queryExperimentalMasses);
        int numInputMasses = experimentalMasses.size();
        List<Double> retentionTimes = Cadena.getListOfDoubles(queryRetentionTimes, numInputMasses);
        List<Map<Double, Integer>> compositeSpectra = getListOfCompositeSpectra(queryCompositeSpectra, numInputMasses);
        List<Boolean> isSignificativeFeatures = new ArrayList<Boolean>(Collections.nCopies(numInputMasses, true));
        int ionizationMode = 1;
        //Doubt, how do I get these values?:
        //      boolean isAdductAutoDetected;
        //      String adductAutoDetected; 
        //      List <CompoundsLCMSGroupByAdduct> compoundsLCMSGroupByAdduct;
        Iterator EMIterator = experimentalMasses.iterator();
        Iterator RTIterator = retentionTimes.iterator();
        Iterator CSIterator = compositeSpectra.iterator();
        Iterator isSiginifcativeIterator = isSignificativeFeatures.iterator();

        for (int i = 0; i < numInputMasses; i++) {
            double EM = (Double) EMIterator.next();
            double RT = (Double) RTIterator.next();
            Map<Double, Integer> CS = (Map<Double, Integer>) CSIterator.next();
            boolean isSignificativeFeature = (boolean) isSiginifcativeIterator.next();

            Feature feature = new Feature(EM, RT, CS, false, "", isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);
            
            features.add(feature);

        }

        return features;
    }

    public static List<Compound> generateCompoundsLCMSForTest() {

        /*
         String inchi="";
        String inchiKey="";
        String smiles = "";
        Structure s1 = new Structure(inchi, inchiKey, smiles);
        String category = "";
        String mainClass = "";
        String subClass = "";
        String class_level4 = "";
        LM_Classification lm_classification = new LM_Classification(category, mainClass, subClass, class_level4);
        List<Classyfire_Classification> classyfire_classification = new LinkedList();
        String lipidType = "";
        Lipids_Classification lipids_classification = new Lipids_Classification(lipidType, 0, 0, 0);
        List<Pathway> pathways = new LinkedList();
        
         */
        String lm_id = "";
        String kegg_id = "";
        String hmdb_id = "";
        String metlin_id = "";
        String in_house_id = "";
        String pc_id = "";
        String MINE_id = "";
        Compound c1 = new Compound(1, 500, "C1", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+K
        Compound c2 = new Compound(1, 500.001, "C2", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+H
        Compound c3 = new Compound(1, 400.99998, "C3", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c4 = new Compound(1, 304.9999, "C4", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+H
        Compound c5 = new Compound(1, 500.00001, "C5", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c6 = new Compound(1, 0.0, "C6", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c7 = new Compound(1, 345.2402 + 17.0032, "C7", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);//M+H-H2O
        Compound c8 = new Compound(1, 672.2424 - 18.033823, "C8", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c9 = new Compound(1, 345.2402 - 18.033823, "C9", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c10 = new Compound(1, 672.2424 - 22.989218, "C10", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);//M+Na
        Compound c11 = new Compound(1, 345.2402 - 18.033823, "C11", "d", "d", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c12 = new Compound(1, 337.2234 - 18.033823, "C12", "", "", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c13 = new Compound(1, 222.2424 - 18.033823, "C13", "e", "e", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+NH4
        Compound c14 = new Compound(1, 280.2402 - 22.989218, "C14", "f", "f", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);// M+Na

        List<Compound> compounds = new LinkedList<>();
        compounds.add(c1);

        compounds.add(c2);
        compounds.add(c3);
        compounds.add(c4);
        compounds.add(c5);
        compounds.add(c6);
        compounds.add(c7);
        compounds.add(c8);
        compounds.add(c9);
        compounds.add(c10);
        compounds.add(c11);
        compounds.add(c12);
        compounds.add(c13);
        compounds.add(c14);

        return compounds;

    }

    private static Feature getCompoundsGroupByAdductFromFeature(Feature feature, String ionMode, String massesMode, List<String> adducts, Double tolerance) {

        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(massesMode, ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);
        Double massToSearch;
        Double mzInputMass = feature.getEM();

        for (String adduct : adducts) {
            Double adductValue;
            String sValueAdduct;
            sValueAdduct = provisionalMap.get(adduct);
            adductValue = Double.parseDouble(sValueAdduct);
            //System.out.println("ADDUCT: " + s);
            massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, adduct, adductValue);
            System.out.println("MASS TO SEARCH: " + massToSearch + " Adduct: " + adduct + " value: " + adductValue + " mass: " + mzInputMass);
            //TODO tolerance in ppm
            CompoundsLCMSGroupByAdduct compoundGroupByAdduct = getCompoundsGroupByAdduct(feature, massToSearch, adduct, tolerance);
            if (compoundGroupByAdduct != null) {
                feature.addAnnotationGroupByAdduct(compoundGroupByAdduct);
            }
        }
        return feature;
    }

    private static List<String> generateAdductsToTest(String[] adductsChain) {
        List<String> adductsToTest = new LinkedList<>();

        for (String adduct : adductsChain) {
            adductsToTest.add(adduct);
        }
        return adductsToTest;
    }

    private static CompoundsLCMSGroupByAdduct getCompoundsGroupByAdduct(Feature feature, Double massToSearch, String adduct, Double tolerance) {
        List<Compound> compoundsFromDB = generateCompoundsLCMSForTest();//this will be transformed in an access to the database
        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = new CompoundsLCMSGroupByAdduct(feature.getEM(), feature.getRT(), feature.getCS(),
                adduct, new LinkedList<CompoundLCMS>());
        for (Compound compound : compoundsFromDB) {
            Double compound_mass = compound.getMass();
            if (compound_mass <= massToSearch + tolerance && compound_mass >= massToSearch - tolerance) {
                int compound_id = compound.getCompound_id();
                //double compound_mass = mass;
                String formula = compound.getFormula();
                String name = compound.getCompound_name();
                String cas_id = compound.getCas_id();
                int formulatype = compound.getFormula_type();
                int compoundtype = compound.getCompound_type();
                int compoundstatus = compound.getCompound_status();
                int chargeType = compound.getCharge_type();
                int chargeNumber = compound.getCharge_number();
                String lm_id = compound.getLm_id();
                String kegg_id = compound.getKegg_id();
                String hmdb_id = compound.getHmdb_id();
                String metlin_id = compound.getMetlin_id();
                String in_house_id = compound.getIn_house_id();
                String pc_id = compound.getPc_id();
                String MINE_id = compound.getMINE_id();
                Structure structure = compound.getStructure();
                LM_Classification lm_classification = compound.getLm_classification();
                List<Classyfire_Classification> classyfire_classification = compound.getClasssyfire_classification();
                Lipids_Classification lipids_classification = compound.getLipids_classification();
                List<Pathway> pathways = compound.getPathways();

                // proper variables of LCMSCompound: 
                Double EM = feature.getEM();
                Double RT = feature.getRT();
                Map<Double, Integer> CS = feature.getCS();
                boolean isSignificative = true;

                CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct, isSignificative,
                        compound_id, compound_mass, formula, name, cas_id, formulatype, compoundtype,
                        compoundstatus, chargeType, chargeNumber,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                        structure, lm_classification, classyfire_classification, lipids_classification, pathways);
                compoundLCMS.setShowPathways(true);
                compoundsGroupByAdduct.addAnnotation(compoundLCMS);
            }
        }
        if (compoundsGroupByAdduct.getCompounds().isEmpty()) {
            return null;
        }
        return compoundsGroupByAdduct;
    }

    public static void main(String[] args) {
//500, 305, 200
        String experimentalMasses = "501.007276"//M+H
                + "\n522.989218"//M+Na
                + "\n538.963158"//M+K
                + "\n343.963158"//M+K
                + "\n306.007276"//M+H
                + "\n297.6745"
                + "\n201.00728"
                + "\n182.9968";// M+H

        String retentionTimes = "18.842525"
                + "\n18.842525"
                + "\n18.842525"
                + "\n18.842525"
                + "\n18.852442"
                + "\n18.852442"
                + "\n22.345"
                + "\n22.345";
        String compositeSpectra = "(400.3432, 307034.88)(401.34576, 73205.016)(402.3504, 15871.166)(403.35446, 2379.5325)(404.3498, 525.92053)"
                + "\n(422.32336, 1562.7301)(423.3237, 564.0795)(424.33255, 292.2923)"
                + "\n(422.32336, 1562.7301)(423.3237, 564.0795)(424.33255, 292.2923)"
                + "\n(470.5, 1832.6085)(471.432, 468.8131)"
                + "\n(485.29334, 3261.4624)(486.29334, 1148.7113)"
                + "\n(570.3551, 15104.281)(571.3582, 5156.9814)(572.36224, 1203.8275)"
                + "(573.3646, 101.370476)(592.33673, 1409.5024)(593.3393, 543.91895)(594.3383, 262.465)"
                + "\n(201.00728, 15104.281)( 222.989218, 5156.9814), (238.963158 1203.8275)"
                + "\n (180,1300)";
        /*
        Feature f = new Feature (399.3367, 0, CS, true, compositeSpectra, true, new LinkedList(), 0);
        f.addAnnotationGroupByAdduct(annotationsGroupByAdduct);
         */
        // 1. Create features (will be taken from and experiment in real process
       List <Feature> features = loadFeaturesFromExperiment(experimentalMasses, retentionTimes, compositeSpectra);

        //Experiment attributes
        List <Feature> significantFeatures = features;
        List <Feature> allFeatures = features;
        //      List <FeaturesGroupByRT> featuresGroupByRt is created within Experiment constructor
        double tol = 0.1;
        int tolerance = 10;
        int tolerance_type = 0;
        int chemicalAlphabet = 1;
        int modifier = 0;
        int metabolitesType = 0;
        List<Integer> databases = new LinkedList<>();
        int inputMassesMode = 1;
        String ionMode = "positive";
        int ionizationMode = 0;
        String massesMode = "mz";

        List<String> adducts;
        adducts = AdductProcessing.getAllAdducts(ionMode);

        Collections.sort(features, new RTComparator());
        // 2. Group features by RT (is within experiment constructor)
        ExperimentGroupByRT experiment;
        experiment = new ExperimentGroupByRT(significantFeatures, allFeatures, true, tolerance, tolerance_type,
                chemicalAlphabet, modifier, metabolitesType, databases, inputMassesMode, ionizationMode, adducts);

        // 3. Autodetect adducts from CS (método de Alberto)
        List<FeaturesGroupByRT> featuresGroups_byRT = experiment.getSignificantFeaturesGroupedByRT();
        System.out.println("\n\nPredicting adduct from CS: ");
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {

            System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            
            features =  featuresGroup_byRT.getFeatures();
            for (Feature feature : features) {

                String adductAutodetected = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode, feature.getEM(), adducts, feature.getCS());
                if (!adductAutodetected.equals("")) {
                    feature.setAdductAutoDetected(adductAutodetected);
                    feature.setIsAdductAutoDetected(true);
                }
                System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
            }
        }
        // 3'. Ver si en un mismo FGBRT hay más de una feature con el aducto auto detectado
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            features = featuresGroup_byRT.getFeatures();

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

            //5. For each feature obtain the list of compounds group by adduct 
            //System.out.println(">>>> For each feature obtain the list of compounds group by adduct ");
            for (Feature feature : features) {
                tol = 0.1; //as a constant in constantes?
                for (String adduct : adducts) {
                    //System.out.println(">>>> LIST OF ADDUCTS: "+adduct);
                    // There will be an item CompoundsLCMSGroupByAdduct for each adduct
                    if (!feature.isIsAdductAutoDetected()) {
                        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct, tol, ionMode);
                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                    } else {
                        adduct = feature.getAdductAutoDetected();
                        CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct, tol, ionMode);
                        feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                        break;
                    }
                }
                //System.out.println(feature.toString());

            }
        }

        //Now I will print everything to check averything is working properly
        System.out.println("****FINAL OUTPUT*****");
        System.out.println("Number of features groups: " + featuresGroups_byRT.size());
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            System.out.println("FGBRT RT: " + featuresGroup_byRT.getRT());
            System.out.println("Number of features: " + featuresGroup_byRT.getFeatures().size());
            for (Feature f : featuresGroup_byRT.getFeatures()) {
                System.out.println("    FEATURE EM: " + f.getEM() + " ADDUCT: " + f.getAdductAutoDetected());
                for (CompoundsLCMSGroupByAdduct cg : f.getAnnotationsGroupedByAdduct()) {
                    if (!cg.getCompounds().isEmpty()) {
                        System.out.println("Compounds size: " + cg.getCompounds().size());
                        System.out.println("        Compounds group by Adduct: " + cg.getAdduct());
                        for (CompoundLCMS c : cg.getCompounds()) {
                            System.out.println("            Compound " + c.getCompound_name() + " with mass " + c.getMass());
                        }
                    }
                }
            }
        }
        /*
        System.out.println("\n\nPredicting adducts from features relations");
        for (FeaturesGroupByRT featuresGroup_byRT : featuresGroups_byRT) {
            System.out.println("Features GROUPED by RT: " + featuresGroup_byRT.getRT());
            features = featuresGroup_byRT.getFeatures();
            //If the adduct was detected from the Composite Spectra for a feature in the group, use it to detect the rest 
            Double molecularMass = getMolecularMassFromDetectedAdduct(features, ionMode);
            Double maximumMassWithinGroup = getMaximunExperimentalMassInFeaturesGroup(features);
            System.out.println("MAXIMUM MASS WITHIN GROUP= "+maximumMassWithinGroup );
            for (Feature feature : features) {
                String adductName = AdductProcessing.detectAdductBasedOnFeaturesData(molecularMass, feature.getEM(), ionMode);
                boolean isAdductAutoDetected = feature.isIsAdductAutoDetected();
                if ((adductName.equals("")) && (!isAdductAutoDetected)) {
                    feature.setAdductAutoDetected(adductName);
                    feature.setIsAdductAutoDetected(true);
                } System.out.println("    Feature with EM: " + feature.getEM() + " and RT: " + feature.getRT() + " adductAutoDetected: " + feature.getAdductAutoDetected());
                
                if (!isAdductAutoDetected) {
                    //. 5 If the adduct wasnt autodetected by the CS or the relationship between features, we proceed to obtain 
                    //the list of compounds grouped by adducts. 

                    //pregunta para alberto, no pueden haber dos features con las misma masa dentro de un mismo grupo no?
                    //asumo que no hasta que se e diga lo contrario
                    if (feature.getEM() == maximumMassWithinGroup) {
                        tol = 0.1; //as a constant in constantes?
                        for (String adduct : adducts) {
                            CompoundsLCMSGroupByAdduct compoundsGroupByAdduct = getCompoundsGroupByAdductFromFeatureAndAdduct(feature, adduct, tol, ionMode);
                            feature.getAnnotationsGroupedByAdduct().add(compoundsGroupByAdduct);
                        }
                        System.out.println(feature.toString());
                    }

                }

                

            }
        }

       
        Iterator it = features.iterator();
        List<Feature> featuresWithCompounds = new LinkedList<>();
        while (it.hasNext()) {
            Feature feature = (Feature) it.next();
            feature = getCompoundsGroupByAdductFromFeature(feature, ionMode, massesMode, adducts, tol);
            
            if (!feature.getAnnotationsGroupedByAdduct().isEmpty()) {
                featuresWithCompounds.add(feature);
            }
        }
        
        
        List<FeaturesGroupByRT> featuresGBRT = experiment.getFeaturesGroupedByRT();
        System.out.println("SIZE: " + featuresGBRT.size());
        it = featuresGBRT.iterator();
        while (it.hasNext()) {
            FeaturesGroupByRT fGBRT = (FeaturesGroupByRT) it.next();
            System.out.println(fGBRT.toString());
        }

         */
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

    public static Double getMaximunExperimentalMassInFeaturesGroup(List<Feature> features) {
        double aux;
        double max = 0;
        for (Feature feature : features) {
            aux = feature.getEM();
            if (aux > max) {
                max = aux;
            }
        }
        return max;
    }

    private static CompoundsLCMSGroupByAdduct getCompoundsGroupByAdductFromFeatureAndAdduct(Feature feature, String adduct,
            Double tol, String ionMode) {
        List<Compound> compoundsFromDB = generateCompoundsLCMSForTest();
        // proper variables of LCMSCompound: 
        double EM = feature.getEM();
        double massToSearch = getMassToSearch(EM, adduct, ionMode);
        Double RT = feature.getRT();
        Map<Double, Integer> CS = feature.getCS();
        boolean isSignificative = true;
        CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS,
                adduct, new LinkedList<>());
        for (Compound compound : compoundsFromDB) {
            int compound_id = compound.getCompound_id();
            double compound_mass = compound.getMass();
            String formula = compound.getFormula();
            String name = compound.getCompound_name();
            String cas_id = compound.getCas_id();
            int formulatype = compound.getFormula_type();
            int compoundtype = compound.getCompound_type();
            int compoundstatus = compound.getCompound_status();
            int chargeType = compound.getCharge_type();
            int chargeNumber = compound.getCharge_number();
            String lm_id = compound.getLm_id();
            String kegg_id = compound.getKegg_id();
            String hmdb_id = compound.getHmdb_id();
            String metlin_id = compound.getMetlin_id();
            String in_house_id = compound.getIn_house_id();
            String pc_id = compound.getPc_id();
            String MINE_id = compound.getMINE_id();
            Structure structure = compound.getStructure();
            LM_Classification lm_classification = compound.getLm_classification();
            List<Classyfire_Classification> classyfire_classification = compound.getClasssyfire_classification();
            Lipids_Classification lipids_classification = compound.getLipids_classification();
            List<Pathway> pathways = compound.getPathways();

            if (massToSearch - tol <= compound_mass && massToSearch + tol >= compound_mass) {

                CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct, isSignificative,
                        compound_id, compound_mass, formula, name, cas_id, formulatype, compoundtype,
                        compoundstatus, chargeType, chargeNumber,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                        structure, lm_classification, classyfire_classification, lipids_classification, pathways);
                compoundLCMS.setShowPathways(true);
                compoundsLCMSGroupByAdduct.addAnnotation(compoundLCMS);
            }

        }
        return compoundsLCMSGroupByAdduct;
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

}
