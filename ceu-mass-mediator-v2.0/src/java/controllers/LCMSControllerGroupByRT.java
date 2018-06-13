package controllers;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.ExperimentGroupByRT;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import LCMS.RTComparator;
import List.NoDuplicatesList;
import exporters.NewDataModelCompoundExcelExporter;
import facades.MSFacade;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import utilities.Cadena;
import utilities.DataFromInterfacesUtilities;
import utilities.FeaturesRTProcessing;

/**
 * Controller (Bean) of the application for LC/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2018
 */
@SessionScoped
@Named("LCMSControllerGroupByRT")
public class LCMSControllerGroupByRT extends LCMSControllerAdapter {

    private List<FeaturesGroupByRT> allFeaturesGroupByRT;
    private List<FeaturesGroupByRT> significativeFeaturesGroupedByRT;
    private ExperimentGroupByRT experimentGroupByRT;

    public LCMSControllerGroupByRT() {
        super();
        this.allFeaturesGroupByRT = new NoDuplicatesList<>();
    }

    /**
     * Method that permits to create a excel from the current results. Flag
     * indicates if the excel generated contains RT field or not. 1 yes 0 no
     * // TODO ALBERTO EXPORT TO EXCEL THE POSSIBLE FRAGMENTS
     * @param flag
     */
    @Override
    public void exportToExcel(int flag) {
        // Only export to Excel no significative compounds
        List<CompoundLCMS> compoundsLCMS = this.experimentGroupByRT.getCompoundsLCMS();

        NewDataModelCompoundExcelExporter compoundExcelExporter = new NewDataModelCompoundExcelExporter();
        compoundExcelExporter.generateWholeExcelCompound(compoundsLCMS, flag);
    }

    /**
     * Submit compounds in advanced mode.
     */
    @Override
    public void submitLCMSAdvancedSearch() {
        this.allFeaturesGroupByRT.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        if (getAllInputMasses().equals("")) {
            setAllFeatures(false);
            //Method returns an ArrayList because it is acceded by index
            massesAux = Cadena.extractDoubles(getQueryInputMasses());
            List<Double> massesMZFromNeutral = new LinkedList<>();

            for (double mass : massesAux) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
                massesMZFromNeutral.add(mass);
            }

            this.setQueryMasses(massesMZFromNeutral);
            numInputMasses = massesAux.size();
            List<Double> retentionTimes = Cadena.getListOfDoubles(getQueryInputRetentionTimes(), numInputMasses);
            this.setQueryRetentionTimes(retentionTimes);
            List<Map<Double, Integer>> compositeSpectra = getListOfCompositeSpectra(getQueryInputCompositeSpectra(), numInputMasses);
            this.setQueryCompositeSpectrum(compositeSpectra);

            List<Feature> significantFeatures;
            significantFeatures = loadFeaturesFromExperiment(massesMZFromNeutral,
                    retentionTimes, compositeSpectra, false);
            Collections.sort(significantFeatures, new RTComparator());

            processGroupedCompoundsAdvanced(significantFeatures, significantFeatures);

            //TODO MARIA REMOVE AFTER TESTING
            printResults(this.allFeaturesGroupByRT);

        } else {
            // IGUAL QUE SI FUESEN TODAS SIGNIFICATIVAS DE MOMENTO. 

            setAllFeatures(true);
            // significant features
            massesAux = Cadena.extractDoubles(getQueryInputMasses());
            List<Double> massesMZFromNeutral = new LinkedList<>();

            for (double mass : massesAux) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
                massesMZFromNeutral.add(mass);
            }

            this.setQueryMasses(massesMZFromNeutral);
            numInputMasses = massesAux.size();
            List<Double> retentionTimes = Cadena.getListOfDoubles(getQueryInputRetentionTimes(), numInputMasses);
            this.setQueryRetentionTimes(retentionTimes);
            List<Map<Double, Integer>> compositeSpectra = getListOfCompositeSpectra(getQueryInputCompositeSpectra(), numInputMasses);
            this.setQueryCompositeSpectrum(compositeSpectra);

            List<Feature> significantFeatures;
            significantFeatures = loadFeaturesFromExperiment(massesMZFromNeutral,
                    retentionTimes, compositeSpectra, false);
            Collections.sort(significantFeatures, new RTComparator());

            //all features
            List<Double> allMassesAux = Cadena.extractDoubles(this.getAllInputMasses());
            List<Double> allMassesMZFromNeutral = new LinkedList<>();
            for (double mass : allMassesAux) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
                allMassesMZFromNeutral.add(mass);
            }
            this.setQueryMasses(allMassesMZFromNeutral);
            numInputMasses = allMassesMZFromNeutral.size();
            List<Double> allretentionTimes = Cadena.getListOfDoubles(getAllInputRetentionTimes(), numInputMasses);
            this.setQueryRetentionTimes(allretentionTimes);
            List<Map<Double, Integer>> allcompositeSpectra;
            allcompositeSpectra = getListOfCompositeSpectra(getAllInputCompositeSpectra(), numInputMasses);
            this.setQueryCompositeSpectrum(allcompositeSpectra);

            List<Feature> allFeatures;
            allFeatures = loadFeaturesFromExperiment(allMassesMZFromNeutral,
                    allretentionTimes,
                    allcompositeSpectra, false);

            FeaturesRTProcessing.setSignificantFeatures(significantFeatures, allFeatures);

            processGroupedCompoundsAdvanced(significantFeatures, allFeatures);
            //TODO MARIA REMOVE AFTER TESTING
            printResults(this.allFeaturesGroupByRT);
        }
    }

    /**
     * Set the features empty
     */
    @Override
    protected void resetItems() {
        this.allFeaturesGroupByRT.clear();
    }

    public List<FeaturesGroupByRT> getAllFeaturesGroupByRT() {
        return allFeaturesGroupByRT;
    }

    public void setAllFeaturesGroupByRT(List<FeaturesGroupByRT> allFeaturesGroupByRT) {
        this.allFeaturesGroupByRT = allFeaturesGroupByRT;
    }

    public List<FeaturesGroupByRT> getSignificativeFeaturesGroupedByRT() {
        return significativeFeaturesGroupedByRT;
    }

    public void setSignificativeFeaturesGroupedByRT(List<FeaturesGroupByRT> significativeFeaturesGroupedByRT) {
        this.significativeFeaturesGroupedByRT = significativeFeaturesGroupedByRT;
    }

    public ExperimentGroupByRT getExperimentGroupByRT() {
        return experimentGroupByRT;
    }

    public void setExperimentGroupByRT(ExperimentGroupByRT ExperimentGroupByRT) {
        this.experimentGroupByRT = ExperimentGroupByRT;
    }

    /**
     * This method process the data introduced from lcms_advanced_search and
     * lcms_batch_advanced_search. Tranforms data into numerical values when
     * needed Creates the experiment object with the given data (which implies
     * grouping the features by retention time)
     */
    private void processGroupedCompoundsAdvanced(List<Feature> significantFeatures, List<Feature> allFeatures) {
        System.out.println("Entering process advanced GROUPED");

        int tolerance = Integer.parseInt(getInputTolerance());
        boolean isAllFeatures = isAllFeatures();

        List<Integer> databasesAsInt = DataFromInterfacesUtilities.getDatabasesAsInt(getDatabases());
        int metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(getMetabolitesType());
        int inputMassModeAsInt = DataFromInterfacesUtilities.inputMassModeToInteger(getMassesMode());
        int ionizationModeAsInt = DataFromInterfacesUtilities.ionizationModeToInteger(getIonMode());
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(getChemAlphabet());
        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputModeTolerance());
        int modifierInt = DataFromInterfacesUtilities.modifierToInteger(getModifier());

        MSFacade msFacade = getMsFacade();

        List<String> adducts = getAdducts();

        //1. Group features by RT (is within experiment constructor)
        this.experimentGroupByRT = new ExperimentGroupByRT(significantFeatures, allFeatures, isAllFeatures,
                tolerance, toleranceTypeAsInt,
                chemAlphabetInt, modifierInt, metabolitesTypeInt,
                databasesAsInt, inputMassModeAsInt, ionizationModeAsInt, adducts);

        this.allFeaturesGroupByRT = experimentGroupByRT.getAllFeaturesGroupByRT();
        // processing of the groups inside the experiment!
        this.experimentGroupByRT.processGroupedCompoundsAdvanced(msFacade);
        // TODO APPLY RULES (DROOLS)

        getOnlySignificativeFeatures();
    }

    /**
     * Copy the significative features over the corresponding item Usually the
     * user is only interested in the significant ones, but the non-significant
     * provide evidence for the annotation of the non significant ones. COPY
     * ONLY THE SIGNIFICATIVE ONES IN THE CORRESPONDING ITEM
     */
    @Override
    protected void getOnlySignificativeFeatures() {
        this.experimentGroupByRT.calculateSignificantFeatures();
        this.significativeFeaturesGroupedByRT = this.experimentGroupByRT.getSignificantFeaturesGroupedByRT();
    }

    //TODO: remove after testing
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
                        System.out.println("           ---->Fragment from " + parentCompound.getCompound_name() + " id: " + parentCompound.getCompound_id()
                                + " EM: " + parentCompound.getEM() + " RT: " + parentCompound.getRT());
                    }
                } else if (!f.getAnnotationsGroupedByAdduct().isEmpty()) {
                    for (CompoundsLCMSGroupByAdduct cg : f.getAnnotationsGroupedByAdduct()) {
                        if (cg != null) {
                            System.out.println("Compounds size: " + cg.getCompounds().size());
                            System.out.println("        Compounds group by Adduct: ");
                            for (CompoundLCMS c : cg.getCompounds()) {
                                System.out.println("            Compound " + c.getCompound_name() + " with mass " + c.getMass());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void calculateScores() {
        this.experimentGroupByRT.calculateScoresAnnotations();
    }

    @Override
    public void submitLCMSSimpleSearch() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
