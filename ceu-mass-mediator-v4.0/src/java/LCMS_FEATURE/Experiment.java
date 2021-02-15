/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import List.NoDuplicatesList;
import java.util.LinkedList;
import java.util.List;
//import org.kie.api.runtime.KieContainer;
import ruleengine.ConfigFilter;
import ruleengine.RuleProcessor;
import utilities.FeaturesRTProcessing;
import utilities.RulesProcessing;
import utilities.Constants;

/**
 * Experiment. Consist on the data obtained through an experiment in the
 * laboratory. This data is introduced by the user through lcms_search.xhtml An
 * experiment contains several features (tuples of mz, retention times and CS)
 *
 * @author Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Experiment {

    // TODO PERSIST IN DATABASE WHEN NEEDED
    // The features introduced by the user as significant 
    private List<Feature> significantFeatures;
    // The features introduced by the user as "all", significant and not
    private final List<Feature> allFeatures;

    private boolean isAllFeatures;
    private int maxNumberOfRTScoresApplied;

    private final int tolerance;
    private final int tolerance_type;
    private final int chemicalAlphabet;
    private final int modifier;
    private final int metabolitesType;
    private final List<Integer> databases;
    private final int inputMassesMode;
    private final int ionizationMode; // 0 negative, 1 positive
    private final List<String> adducts;

    private int experimentId;

    /**
     * Creates an experiment with an empty list of features that may be filled
     * later with the distinct methods for adding features.
     *
     * @param tolerance
     * @param tolerance_type
     * @param chemicalAlphabet
     * @param modifier
     * @param metabolitesType
     * @param databases
     * @param inputMassesMode
     * @param ionizationMode
     * @param adducts
     */
    public Experiment(int tolerance, int tolerance_type, int chemicalAlphabet, int modifier, int metabolitesType,
            List<Integer> databases, int inputMassesMode, int ionizationMode, List<String> adducts) {
        this(new NoDuplicatesList(), new NoDuplicatesList(), false,
                tolerance, tolerance_type, chemicalAlphabet, modifier, metabolitesType,
                databases, inputMassesMode, ionizationMode, adducts);
    }

    /**
     * Creates an experiment with the features allFeatures, the significant
     * features significantFeatures, indicating if there is only significant
     * features or there are included all of them (isAllFeatures)
     *
     * @param significantFeatures are the features that the user consider
     * significant
     * @param allFeatures all the features
     * @param isAllFeatures boolean to check if all the features are present
     * (doubt)
     * @param tolerance tolerance for the experimental masses
     * @param tolerance_type 0 (ppm) 1 (mDa)
     * @param chemicalAlphabet indicates which atoms are included for the
     * compound search CHNOPS, 0; CHNOPSD, 1; CHNOPSCL, 2; CHNOPSCLD, 3; ALL, 4;
     * ALLD, 5
     * @param modifier the molecule mixed with the sample to perform the MS.
     * "None", 0; "NH3", 1; "HCOO", 2; "CH3COO", 3; "HCOONH3", 4; "CH3COONH3",
     * 5;
     * @param metabolitesType all except peptides (0), only lipids (1) or all
     * including peptides(2)
     * @param databases databases to perform the search on
     * @param inputMassesMode as neutral (0) or mass charged (m/z) (1)
     * @param ionizationMode neutral (0), positive (1) or negative (2)
     * @param adducts select adducts that may be present in the sample
     */
    public Experiment(List<Feature> significantFeatures, List<Feature> allFeatures, boolean isAllFeatures,
            int tolerance, int tolerance_type, int chemicalAlphabet, int modifier, int metabolitesType,
            List<Integer> databases, int inputMassesMode, int ionizationMode, List<String> adducts) {
        this.significantFeatures = significantFeatures;
        this.allFeatures = allFeatures;
        this.isAllFeatures = isAllFeatures;
        this.tolerance = tolerance;
        this.tolerance_type = tolerance_type;
        this.chemicalAlphabet = chemicalAlphabet;
        this.modifier = modifier;
        this.metabolitesType = metabolitesType;
        this.databases = databases;
        this.inputMassesMode = inputMassesMode;
        this.ionizationMode = ionizationMode;
        this.adducts = adducts;
        this.experimentId = System.identityHashCode(this);
    }

    /**
     *
     * @return the list of significant features from the experiment.
     */
    public List<Feature> getSignificantFeatures() {
        return significantFeatures;
    }

    /**
     * Add a new significant feature to the experiment.
     *
     * @param significantFeature
     */
    public void addSignificantFeature(Feature significantFeature) {
        // it has to be included in significant and allFeatures
        this.significantFeatures.add(significantFeature);
        this.allFeatures.add(significantFeature);
    }

    /**
     * @return the list of significant and non significant features from the
     * experiment.
     */
    public List<Feature> getAllFeatures() {
        return allFeatures;
    }

    /**
     * Add a new non significant feature to the experiment.
     *
     * @param nonSignificantFeature
     */
    public void addNonSignificantFeature(Feature nonSignificantFeature) {
        // it has to be included only in allFeatures
        this.allFeatures.add(nonSignificantFeature);
    }

    public int getTolerance() {
        return tolerance;
    }

    public int getTolerance_type() {
        return tolerance_type;
    }

    public int getChemicalAlphabet() {
        return chemicalAlphabet;
    }

    public int getModifier() {
        return modifier;
    }

    public List<Integer> getDatabases() {
        return databases;
    }

    public int getInputMassesMode() {
        return inputMassesMode;
    }

    public int getIonizationMode() {
        return ionizationMode;
    }

    public List<String> getAdducts() {
        return adducts;
    }

    public int getMetabolitesType() {
        return metabolitesType;
    }

    public boolean isIsAllFeatures() {
        return isAllFeatures;
    }

    public void setIsAllFeatures(boolean isAllFeatures) {
        this.isAllFeatures = isAllFeatures;
    }

    public int getMaxNumberOfRTScoresApplied() {
        return maxNumberOfRTScoresApplied;
    }

    public void setMaxNumberOfRTScoresApplied(int maxNumberOfRTScoresApplied) {
        this.maxNumberOfRTScoresApplied = maxNumberOfRTScoresApplied;
    }

    public String getKeggWebPage() {
        return Constants.WEB_KEGG;
    }

    public String getHMDBWebPage() {
        return Constants.WEB_HMDB;
    }

    public String getMetlinWebPage() {
        return Constants.WEB_METLIN;
    }

    public String getLMWebPage() {
        return Constants.WEB_LIPID_MAPS;
    }

    public String getPCWebPage() {
        return Constants.WEB_PUBCHEMICHAL;
    }

    public String getChebiWebPage() {
        return Constants.WEB_CHEBI;
    }

    public String getMINEWebPage() {
        return Constants.WEB_MINE;
    }

    /**
     * This methods process the features of the experiment from Advanced Search.
     * Rules about ionization, about relation between features and about RT
     * order elution of compounds are applied
     *
     */
    public void processCompoundsAdvanced() {

        System.out.println("Entering process");

        if (this.allFeatures == null) {
            // TODO THROW EXCEPTION. NO EXPERIMENT SHOULD BE EMPTY OF FEATURES
            return;
        }

        // Once we have the features, we have to process their annotations
        processAllRules();
        calculateScoresAnnotations();
    }

    /**
     * This methods process the features of the experiment from Simple Search.
     * It means that only ionization rules will be applied since there is no RT
     * available, as well as CS.
     *
     */
    public void processCompoundsSimple() {

        System.out.println("Entering process");

        if (this.allFeatures == null) {
            // TODO THROW EXCEPTION. NO EXPERIMENT SHOULD BE EMPTY OF FEATURES
            return;
        }

        // Once we have the features, we have to process their annotations
        processIonizationRules();
        calculateScoresAnnotations();
    }

    private void processAllRules() {
        // Drools.
        // Creates configFilter with ionization mode.

        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(this.modifier);
        configFilter.setIonMode(this.ionizationMode);
        configFilter.setAllCompounds(this.isAllFeatures);
        //KieContainer kContainer = RuleProcessor.getContainer(Integer.toString(experimentId));
        // Execute rules.
        //RuleProcessor.processRulesFeatures(this.allFeatures, configFilter, kContainer);
        RuleProcessor.processRulesFeatures(this.allFeatures, configFilter);

    }

    private void processIonizationRules() {
        // Drools.
        // Creates configFilter with ionization mode.
        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(this.modifier);
        configFilter.setIonMode(this.ionizationMode);
        configFilter.setAllCompounds(this.isAllFeatures);
        //KieContainer kContainer = RuleProcessor.getContainer(Integer.toString(experimentId));
        // Execute rules.
        //RuleProcessor.processSimpleSearchFeatures(this.allFeatures, configFilter, kContainer);
        RuleProcessor.processSimpleSearchFeatures(this.allFeatures, configFilter);

    }

    /**
     *
     */
    public void calculateOnlySignificantFeatures() {

        this.significantFeatures = FeaturesRTProcessing.getOnlySignificativeFeatures(
                this.allFeatures, isIsAllFeatures());
    }

    /**
     *
     */
    protected void calculateMaxNumberOfRTScores() {
        int max_numberOfRTScoresApplied;
        max_numberOfRTScoresApplied = RulesProcessing.calculateMaxNumberOfRTScoresFeatures(this.allFeatures);
        setMaxNumberOfRTScoresApplied(max_numberOfRTScoresApplied);
        //System.out.println("MAX NUMBER OF RT APPLIED: " + this.maxNumberOfRTScoresApplied);
    }

    /**
     * Calculate the scores of the annotations based on the rules developed on
     * the compoundLCMS
     */
    public void calculateScoresAnnotations() {
        calculateMaxNumberOfRTScores();
        for (Feature feature : (LinkedList<Feature>) this.allFeatures) {
            for (CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct : feature.getAnnotationsGroupedByAdduct()) {
                for (CompoundLCMS compoundLCMS : compoundsLCMSGroupByAdduct.getCompounds()) {
                    compoundLCMS.calculateFinalScore(getMaxNumberOfRTScoresApplied());
                    // Create tags for colours
                    compoundLCMS.createColorIonizationScore();
                    compoundLCMS.createColorAdductRelationScore();
                    //
                    compoundLCMS.createColorRetentionTimeScore();
                    compoundLCMS.createColorFinalScore();
                }
            }
        }
    }

    /**
     *
     * @return the list of annotations without grouping. Mainly used for
     * exporting to other format where there is no hierarchy (like excel)
     */
    public List<CompoundLCMS> getCompoundsLCMS() {
        List<CompoundLCMS> compoundsLCMS = new LinkedList<>();
        if (this.significantFeatures == null || this.significantFeatures.isEmpty()) {
            calculateOnlySignificantFeatures();
        }
        for (Feature feature : significantFeatures) {
            for (CompoundsLCMSGroupByAdduct annotationsGrouped : feature.getAnnotationsGroupedByAdduct()) {
                for (CompoundLCMS annotation : annotationsGrouped.getCompounds()) {
                    compoundsLCMS.add(annotation);
                }
            }
        }

        return compoundsLCMS;
        //System.out.println("MAX NUMBER OF RT APPLIED: " + this.maxNumberOfRTScoresApplied);
    }

}
