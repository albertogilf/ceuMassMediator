package controllers;

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.Experiment;
import LCMS_FEATURE.Feature;
import List.NoDuplicatesList;
import exporters.NewDataModelCompoundExcelExporter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;
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
@Named("LCMSController")
public class LCMSController extends LCMSControllerAdapter {

    private List<Feature> allFeatures;
    private List<Feature> significativeFeatures;
    private Experiment experiment;

    public LCMSController() {
        super();
        this.allFeatures = new NoDuplicatesList();
    }

    /**
     * Method that permits to create a excel from the current results. Flag
     * indicates if the excel generated contains RT field or not. 1 yes 0 no
     *
     * @param flag
     */
    @Override
    public void exportToExcel(int flag) {
        // Only export to Excel no significative compounds

        List<CompoundLCMS> compoundsLCMS = this.experiment.getCompoundsLCMS();

        NewDataModelCompoundExcelExporter compoundExcelExporter
                = new NewDataModelCompoundExcelExporter(flag);
        compoundExcelExporter.generateWholeExcelCompound(compoundsLCMS, flag);

    }

    /**
     * Submit compounds in advanced mode.
     */
    @Override
    public void submitLCMSAdvancedSearch() {
        this.allFeatures.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        massesAux = Cadena.extractDoubles(getQueryInputMasses());
        List<Double> massesMZFromNeutral = new LinkedList<>();

        for (double mass : massesAux) {
            mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
            massesMZFromNeutral.add(mass);
        }

        this.setInputmzs(massesMZFromNeutral);
        numInputMasses = massesMZFromNeutral.size();
        List<Double> retentionTimes = Cadena.getListOfDoubles(getQueryInputRetentionTimes(), numInputMasses);
        this.setQueryRetentionTimes(retentionTimes);
        List<Map<Double, Double>> compositeSpectra = Cadena.getListOfCompositeSpectra(getQueryInputCompositeSpectra(), numInputMasses);
        this.setQueryCompositeSpectrum(compositeSpectra);

        List<Feature> significantFeatures;
        significantFeatures = loadFeaturesFromExperiment(massesMZFromNeutral,
                retentionTimes, compositeSpectra, true);

        if (getAllmzMasses().equals("")) {
            setIsAllFeatures(false);
            processCompoundsAdvanced(significantFeatures, significantFeatures);
            this.significativeFeatures = this.allFeatures;

        } else {
            setIsAllFeatures(true);
            //all features
            List<Double> allMassesAux = Cadena.extractDoubles(this.getAllmzMasses());
            List<Double> allMassesMZFromNeutral = new LinkedList<>();
            for (double mass : allMassesAux) {
                mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
                allMassesMZFromNeutral.add(mass);
            }
            this.setInputmzs(allMassesMZFromNeutral);
            int numAllInputMasses = allMassesMZFromNeutral.size();
            List<Double> allretentionTimes = Cadena.getListOfDoubles(getAllInputRetentionTimes(), numAllInputMasses);
            this.setQueryRetentionTimes(allretentionTimes);
            List<Map<Double, Double>> allcompositeSpectra;
            allcompositeSpectra = Cadena.getListOfCompositeSpectra(getAllInputCompositeSpectra(), numAllInputMasses);
            this.setQueryCompositeSpectrum(allcompositeSpectra);

            List<Feature> allFeatures;
            allFeatures = loadFeaturesFromExperiment(allMassesMZFromNeutral,
                    allretentionTimes,
                    allcompositeSpectra, true);

            FeaturesRTProcessing.setSignificantFeatures(significantFeatures, allFeatures);
            processCompoundsAdvanced(significantFeatures, allFeatures);
        }
    }

    /**
     * Submit Compounds in Simple mode.
     */
    @Override
    public void submitLCMSSimpleSearch() {
        //System.out.println("Entering submit");
        this.allFeatures.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        setIsAllFeatures(false);
        //Method returns an ArrayList because it is acceded by index
        massesAux = Cadena.extractDoubles(getQueryInputMasses());
        List<Double> massesMZFromNeutral = new LinkedList<>();

        for (double mass : massesAux) {
            mass = utilities.Utilities.calculateMZFromNeutralMass(mass, getMassesMode(), getIonMode());
            massesMZFromNeutral.add(mass);
        }

        this.setInputmzs(massesMZFromNeutral);
        numInputMasses = massesAux.size();
        List<Double> retentionTimes = Cadena.getListOfDoubles(getQueryInputRetentionTimes(), numInputMasses);
        this.setQueryRetentionTimes(retentionTimes);
        List<Map<Double, Double>> compositeSpectra = Cadena.getListOfCompositeSpectra(getQueryInputCompositeSpectra(), numInputMasses);
        this.setQueryCompositeSpectrum(compositeSpectra);

        List<Feature> features;
        features = loadFeaturesFromExperiment(massesMZFromNeutral,
                retentionTimes, compositeSpectra, true);

        processCompoundsSimple(features);

        this.significativeFeatures = this.allFeatures;
    }

    /**
     * Set the features empty
     */
    @Override
    protected void resetItems() {
        this.allFeatures.clear();
    }

    public List<Feature> getAllFeatures() {
        return allFeatures;
    }

    public void setAllFeatures(List<Feature> allFeatures) {
        this.allFeatures = allFeatures;
    }

    public List<Feature> getSignificativeFeatures() {
        return significativeFeatures;
    }

    public void setSignificativeFeatures(List<Feature> significativeFeatures) {
        this.significativeFeatures = significativeFeatures;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * This method process the data introduced from lcms_advanced_search and
     * lcms_batch_advanced_search. Tranforms data into numerical values when
     * needed Creates the experiment object with the given data (which implies
     * grouping the features by retention time)
     */
    private void processCompoundsAdvanced(List<Feature> significantFeatures, List<Feature> allFeatures) {
        //System.out.println("Entering process advanced NO GROUPED");

        int tolerance = Integer.parseInt(getInputmzTolerance());
        boolean isAllFeatures = isIsAllFeatures();

        List<Integer> databasesAsInt = DataFromInterfacesUtilities.getDatabasesAsInt(getDatabases());
        int metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(getMetabolitesType());
        int inputMassModeAsInt = getMassesMode();
        int ionMode = getIonMode();
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(getChemAlphabet());
        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        int modifierInt = DataFromInterfacesUtilities.modifierToInteger(getModifier());

        List<String> adducts = getAdducts();

        // Create experiment with Features
        this.experiment = new Experiment(significantFeatures, allFeatures, isAllFeatures,
                tolerance, toleranceTypeAsInt,
                chemAlphabetInt, modifierInt, metabolitesTypeInt,
                databasesAsInt, inputMassModeAsInt, ionMode, adducts);

        this.experiment.processCompoundsAdvanced();
        this.allFeatures = this.experiment.getAllFeatures();
    }

    /**
     * This method process the data introduced from lcms_simple_search and
     * lcms_batch_search. Tranforms data into numerical values when needed
     * Creates the experiment object with the given data (which implies grouping
     * the features by retention time)
     *
     */
    private void processCompoundsSimple(List<Feature> allFeatures) {

        //System.out.println("Entering process process simple search");
        int tolerance = Integer.parseInt(getInputmzTolerance());
        boolean isAllFeatures = isIsAllFeatures();

        List<Integer> databasesAsInt = DataFromInterfacesUtilities.getDatabasesAsInt(getDatabases());
        int metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(getMetabolitesType());
        int inputMassModeAsInt = getMassesMode();
        int ionMode = getIonMode();
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(getChemAlphabet());
        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        int modifierInt = DataFromInterfacesUtilities.modifierToInteger(getModifier());

        List<String> adducts = getAdducts();

        // Create experiment with Features
        this.experiment = new Experiment(allFeatures, allFeatures, isAllFeatures,
                tolerance, toleranceTypeAsInt,
                chemAlphabetInt, modifierInt, metabolitesTypeInt,
                databasesAsInt, inputMassModeAsInt, ionMode, adducts);

        this.experiment.processCompoundsSimple();

        this.allFeatures = this.experiment.getAllFeatures();
    }

    /**
     * Copy the significative features over the corresponding item Usually the
     * user is only interested in the significant ones, but the non-significant
     * provide evidence for the annotation of the non significant ones. COPY
     * ONLY THE SIGNIFICATIVE ONES IN THE CORRESPONDING ITEM
     */
    @Override
    protected void getOnlySignificativeFeatures() {
        this.experiment.calculateOnlySignificantFeatures();
        this.significativeFeatures = this.experiment.getSignificantFeatures();
    }

    @Override
    protected void calculateScores() {
        this.experiment.calculateScoresAnnotations();
    }

    //TODO: remove after testing
    /*
    public static void printResults(List<Feature> features) {
        System.out.println("****FINAL OUTPUT FOR NON GROUPED SEARCH*****");
        for (Feature f : features) {
            System.out.println("    FEATURE EM: " + f.getEM() + " ADDUCT: " + f.getAdductAutoDetected());
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
     */
    
    @PreDestroy
    public void destroy() {
        //System.out.println("DISCONNECTING LCMS SEARCH CONTROLLER");
        super.getMsFacade().disconnect();
    }
}
