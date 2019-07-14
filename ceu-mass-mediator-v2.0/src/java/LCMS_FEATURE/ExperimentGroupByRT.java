/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import List.NoDuplicatesList;
import facades.MSFacade;
import java.util.List;
import utilities.FeaturesRTProcessing;
import utilities.Constants;

/**
 * ExperimentGroupByRT. Consist on the data obtained through an experiment in
 * the laboratory. This data is introduced by the user through
 * lcms_grouped_search.xhtml An experiment contains several features (tuples of
 * mz, retention times and CS)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class ExperimentGroupByRT extends Experiment {

    // TODO PERSIST IN DATABASE WHEN NEEDED
    private List<FeaturesGroupByRT> allFeaturesGroupByRT;
    private List<FeaturesGroupByRT> significantFeaturesGroupedByRT;

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
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts
     * @param RT_window
     */
    public ExperimentGroupByRT(int tolerance, int tolerance_type, int chemicalAlphabet, int modifier, int metabolitesType,
            List<Integer> databases, int inputMassesMode, int ionizationMode, List<String> adducts, Double RT_window) {
        this(new NoDuplicatesList(), new NoDuplicatesList(), false,
                tolerance, tolerance_type, chemicalAlphabet, modifier, metabolitesType,
                databases, inputMassesMode, ionizationMode, adducts, RT_window);
    }

    /**
     * Complete contructor with all the class attributes
     *
     * @param significantFeatures are the features that the user consider
     * significant
     * @param allFeatures all the features
     * @param featuresGroupedByRT features grouped by rentention time within a
     * constant window
     * @param significantFeaturesGroupedByRT significant features grouped by RT
     * @param isAllFeatures boolean to check if all the features are present
     * (doubt)
     * @param tolerance tolerance for the experimental masses
     * @param tolerance_type in ppm or mDa (Da?)
     * @param chemicalAlphabet indicates which atoms are included for the
     * compound search
     * @param modifier the molecule mixed with the sample to perform the MS
     * @param metabolitesType lipids, peptides and/or other
     * @param databases databases to perform the search on
     * @param inputMassesMode as neutral or mass charged (m/z)
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts select adducts that may be present in the sample
     */
    public ExperimentGroupByRT(List<Feature> significantFeatures, List<Feature> allFeatures,
            List<FeaturesGroupByRT> featuresGroupedByRT, List<FeaturesGroupByRT> significantFeaturesGroupedByRT, boolean isAllFeatures,
            int tolerance, int tolerance_type, int chemicalAlphabet, int modifier,
            int metabolitesType, List<Integer> databases, int inputMassesMode, int ionizationMode, List<String> adducts) {
        super(significantFeatures, allFeatures, isAllFeatures, tolerance, tolerance_type, chemicalAlphabet,
                modifier, metabolitesType, databases, inputMassesMode, ionizationMode, adducts);
        this.significantFeaturesGroupedByRT = significantFeaturesGroupedByRT;
        this.allFeaturesGroupByRT = featuresGroupedByRT;
    }

    /**
     * Creates an experiment with the features allFeatures, the significant
     * features significantFeatures, indicating if there is only significant
     * features or there are included all of them (isAllFeatures)
     *
     * @param significantFeatures
     * @param allFeatures
     * @param isAllFeatures
     * @param tolerance
     * @param tolerance_type
     * @param chemicalAlphabet
     * @param modifier
     * @param metabolitesType
     * @param databases
     * @param inputMassesMode
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adducts
     * @param RT_window
     */
    public ExperimentGroupByRT(List<Feature> significantFeatures, List<Feature> allFeatures, boolean isAllFeatures,
            int tolerance, int tolerance_type, int chemicalAlphabet, int modifier, int metabolitesType,
            List<Integer> databases, int inputMassesMode, int ionizationMode, List<String> adducts, Double RT_window) {
        super(significantFeatures, allFeatures, isAllFeatures, tolerance, tolerance_type, chemicalAlphabet,
                modifier, metabolitesType, databases, inputMassesMode, ionizationMode, adducts);
        this.allFeaturesGroupByRT = FeaturesRTProcessing.groupFeaturesByRT(allFeatures, RT_window);

    }

    public List<FeaturesGroupByRT> getSignificantFeaturesGroupedByRT() {
        return this.significantFeaturesGroupedByRT;
    }

    public void setSignificantFeaturesGroupedByRT(List<FeaturesGroupByRT> significantFeaturesGroupedByRT) {
        this.significantFeaturesGroupedByRT = significantFeaturesGroupedByRT;
    }

    public void addFeaturesGroupedByRT(FeaturesGroupByRT featuresGroupedByRT) {
        this.allFeaturesGroupByRT.add(featuresGroupedByRT);
    }

    public List<FeaturesGroupByRT> getAllFeaturesGroupByRT() {
        return allFeaturesGroupByRT;
    }

    public void setAllFeaturesGroupByRT(List<FeaturesGroupByRT> allFeaturesGroupByRT) {
        this.allFeaturesGroupByRT = allFeaturesGroupByRT;
    }

    /**
     * This method process the data introduced from lcms_advanced_search and
     * lcms_batch_advanced_search. Tranforms data into numerical values when
     * needed Creates the experiment object with the given data (which implies
     * grouping the features by retention time)
     *
     * @param msfacade MSFacade for the annotation of the compounds (it contains
     * a database connection)
     */
    public void processGroupedCompoundsAdvanced(MSFacade msfacade) {
        System.out.println("Entering process advanced");

        Integer tolerance = getTolerance();
        double toleranceDouble = tolerance.doubleValue();
        boolean isAllFeatures = isIsAllFeatures();
        Integer toleranceMode = getTolerance_type();

        List<Integer> databasesAsInt = getDatabases();
        int metabolitesTypeInt = getMetabolitesType();
        int massesMode = getInputMassesMode();
        int ionizationMode = getIonizationMode();
        int chemAlphabetInt = getChemicalAlphabet();
        int toleranceTypeAsInt = getTolerance_type();

        List<String> adducts = getAdducts();
        if (this.allFeaturesGroupByRT == null) {
            this.allFeaturesGroupByRT = new NoDuplicatesList();
        }

        //Detect adduct from relation among features
        FeaturesRTProcessing.setRelationshipAmongFeatures(this.allFeaturesGroupByRT, adducts, ionizationMode);

        //For each feature obtain the list of compounds group by adduct  
        FeaturesRTProcessing.setAnnotationsGroupByAdduct(this.allFeaturesGroupByRT, toleranceDouble, toleranceTypeAsInt,
                adducts, massesMode, ionizationMode, databasesAsInt, metabolitesTypeInt, chemAlphabetInt, msfacade);

        //check if the unidentified adducts belongs to fragments
        FeaturesRTProcessing.setFragments(this.allFeaturesGroupByRT,
                Constants.TOLERANCE_FOR_MSMS_PEAKS_POSSIBLE_FRAGMENTS, msfacade, ionizationMode);
    }

    /**
     * This method process the significantFeatures based on all the features
     *
     */
    public void calculateSignificantFeatures() {
        this.significantFeaturesGroupedByRT = FeaturesRTProcessing.getOnlySignificativeFeaturesGroupedByRT(
                this.allFeaturesGroupByRT, isIsAllFeatures());
    }

}
