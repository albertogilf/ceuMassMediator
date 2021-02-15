package controllers;

import LCMS_FEATURE.Feature;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import utilities.Cadena;
import static utilities.Constants.*;
import utilities.AdductProcessing;
import utilities.DataFromInterfacesUtilities;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import utilities.FeaturesRTProcessing;

/**
 * Controller (Bean) of the application for LC/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2018
 */
public abstract class LCMSControllerAdapter extends MSController implements Serializable {

    private String queryInputRetentionTimes;
    private String queryInputCompositeSpectra;
    private String allmzMasses;
    private String allInputRetentionTimes;
    private String allInputCompositeSpectra;

    private String modifier;
    private List<SelectItem> modifierCandidates;
    private List<String> databases;
    private final List<SelectItem> DBcandidates;
    private String metabolitesType;
    private final List<SelectItem> metabolitesTypecandidates;

    private List<Double> queryRetentionTimes;
    private List<Map<Double, Double>> queryCompositeSpectrum;
    private List<Boolean> isSignificativeCompound;

    private boolean isAllFeatures;

    public LCMSControllerAdapter() {
        super();
        //String version = FacesContext.class.getPackage().getImplementationVersion();
        //System.out.println("\n\n  VERSION DE JSF: " + version + "\n\n");
        this.DBcandidates = new LinkedList<>();
        this.DBcandidates.add(new SelectItem("AllWM", "All except MINE"));
        this.DBcandidates.add(new SelectItem("All", "All (Including In Silico Compounds)"));
        MAPDATABASES.entrySet().forEach((e) -> {
            this.DBcandidates.add(new SelectItem(e.getKey(), (String) e.getKey()));
        });
        this.databases = new LinkedList<>();
        this.databases.add("AllWM");

        // MODIFIERS
        this.modifierCandidates = new LinkedList<>();
        DataFromInterfacesUtilities.MODIFIERS.entrySet().forEach((e) -> {
            this.modifierCandidates.add(new SelectItem(e.getKey(), (String) e.getKey()));
        });
        this.modifier = "None";

        this.metabolitesTypecandidates = new LinkedList<>();
        DataFromInterfacesUtilities.METABOLITESTYPES.entrySet().forEach((e) -> {
            this.metabolitesTypecandidates.add(new SelectItem(e.getKey(), (String) e.getKey()));
        });
        this.metabolitesType = "All except peptides";

        this.queryInputRetentionTimes = "";
        this.queryInputCompositeSpectra = "";
        this.allmzMasses = "";
        this.allInputRetentionTimes = "";
        this.allInputCompositeSpectra = "";
    }

    /**
     * This method is used to load a list of inputmzs declared in the class
     * Constants
     */
    public void setDemoMass() {
        super.setDemoParameters();
        this.setQueryInputMasses(ONEDEMOMASS);
    }

    /**
     * This method is used to load a list of inputmzs declared in the class
     * Constants
     */
    public void setAdvancedDemoMass() {
        super.setDemoParameters();
        this.setQueryInputMasses(ONEDEMOMASS);
        this.setQueryInputRetentionTimes(ONERETENTIONTIME);
        this.setQueryInputCompositeSpectra(ONECOMPOSITESPECTRUM);

    }

    /**
     * This method is used to load a list of inputmzs declared in the class
     * Constants
     */
    public void setDemoMasses() {
        super.setDemoParameters();
        this.setQueryInputMasses(NEWDEMOMASSES);
    }

    /**
     * This method is used to load a list of inputmzs declared in the class
     * Constants
     */
    public void setAdvancedDemoMasses() {
        super.setDemoParameters();
        this.setQueryInputMasses(NEWDEMOMASSES);
        this.setQueryInputRetentionTimes(NEWDEMORETENTIONTIME);
        this.setQueryInputCompositeSpectra(NEWDEMOSPECTRUM);
    }

    /**
     * Method to clean the input formulary where the inputmzs are written
     */
    @Override
    public void clearForm() {
        super.clearForm();
        this.queryInputRetentionTimes = "";
        this.queryInputCompositeSpectra = "";
        this.allmzMasses = "";
        this.allInputRetentionTimes = "";
        this.allInputCompositeSpectra = "";

        resetItems();
    }

    /**
     * Method that permits to create a excel from the current results. Flag
     * indicates if the excel generated contains RT field or not. 1 yes 0 no
     *
     * @param flag
     */
    public abstract void exportToExcel(int flag);

    /**
     * Submit compounds in advanced mode.
     */
    public abstract void submitLCMSAdvancedSearch();

    /**
     * Submit Compounds in Simple mode.
     */
    public abstract void submitLCMSSimpleSearch();

    protected abstract void resetItems();

    /**
     * @return the queryInputCompositeSpectra
     */
    public String getQueryInputCompositeSpectra() {
        return this.queryInputCompositeSpectra;
    }

    /**
     * Catches the input text in the formulary on the web of the composite
     * Spectrum and obtains the list of them in order.
     *
     * @param queryInputCompositeSpectra the queryInputCompositeSpectra to set
     */
    public void setQueryInputCompositeSpectra(String queryInputCompositeSpectra) {
        this.queryInputCompositeSpectra = queryInputCompositeSpectra;
    }

    public String getQueryInputRetentionTimes() {
        return queryInputRetentionTimes;
    }

    public void setQueryInputRetentionTimes(String queryInputRetentionTimes) {
        this.queryInputRetentionTimes = queryInputRetentionTimes;
    }

    public String getAllmzMasses() {
        return allmzMasses;
    }

    public void setAllmzMasses(String allmzMasses) {
        this.allmzMasses = allmzMasses;
    }

    public String getAllInputRetentionTimes() {
        return allInputRetentionTimes;
    }

    public void setAllInputRetentionTimes(String allInputRetentionTimes) {
        this.allInputRetentionTimes = allInputRetentionTimes;
    }

    public String getAllInputCompositeSpectra() {
        return allInputCompositeSpectra;
    }

    public void setAllInputCompositeSpectra(String allInputCompositeSpectra) {
        this.allInputCompositeSpectra = allInputCompositeSpectra;
    }

    public List<Boolean> getIsSignificativeCompound() {
        return isSignificativeCompound;
    }

    public void setIsSignificativeCompound(List<Boolean> isSignificativeCompound) {
        this.isSignificativeCompound = isSignificativeCompound;
    }

    public List<Double> getQueryRetentionTimes() {
        return queryRetentionTimes;
    }

    /**
     * @param queryRetentionTimes the Retention Times introduced to se
     */
    public void setQueryRetentionTimes(List<Double> queryRetentionTimes) {
        this.queryRetentionTimes = queryRetentionTimes;
    }

    public List<Map<Double, Double>> getQueryCompositeSpectrum() {
        return queryCompositeSpectrum;
    }

    public void setQueryCompositeSpectrum(List<Map<Double, Double>> queryCompositeSpectrum) {
        this.queryCompositeSpectrum = queryCompositeSpectrum;
    }


    /**
     * @return the databases to search
     */
    public List<String> getDatabases() {
        return databases;
    }

    /**
     * Set the databases to the object
     *
     * @param databases the databases to search
     */
    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    /**
     * @return the modifier to search
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * Set the modifiers to the object
     *
     * @param modifier the modifier to search
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * @return the modifiers availables
     */
    public List<SelectItem> getModifierCandidates() {
        return modifierCandidates;
    }

    /**
     * Set the modifiers to the object
     *
     * @param modifierCandidates the modifiers availables
     */
    public void setModifierCandidates(List<SelectItem> modifierCandidates) {
        this.modifierCandidates = modifierCandidates;
    }

    /**
     * @return the metabolites type to search
     */
    public String getMetabolitesType() {
        return this.metabolitesType;
    }

    /**
     * Set the metabolites types for searching to the object
     *
     * @param metabolitesType the modifier to search
     */
    public void setMetabolitesType(String metabolitesType) {
        this.metabolitesType = metabolitesType;
    }

    /**
     * @return the metabolites types availables
     */
    public List<SelectItem> getMetabolitesTypecandidates() {
        return this.metabolitesTypecandidates;
    }

    /**
     * @return if user introduced significant and non-significant compounds
     */
    public boolean isIsAllFeatures() {
        return isAllFeatures;
    }

    /**
     * @param isAllFeatures user introduced significant and non significant
     * compounds
     */
    public void setIsAllFeatures(boolean isAllFeatures) {
        this.isAllFeatures = isAllFeatures;
    }

    public List<SelectItem> getDBcandidates() {
        return this.DBcandidates;
    }

    /**
     * Get a list of the first m/z from the composite spectra
     *
     * @deprecated
     * @param input
     * @param numInputMasses
     * @return
     */
    protected List<Double> getListOfFirstDataSpectrum(String input, int numInputMasses) {
        List<Double> firstSpectrumAux;
        if (!input.equals("")) {
            //Method returns an ArrayList because it is acceded by index
            firstSpectrumAux = Cadena.extractFirstDataSpectrum(input);

            // If there is no time for all inputmzs, fill with 0
            for (int i = firstSpectrumAux.size(); i < numInputMasses; i++) {
                firstSpectrumAux.add(0d);
            }
        } else {
            firstSpectrumAux = new ArrayList<>();
            for (int i = 0; i < numInputMasses; i++) {
                firstSpectrumAux.add(0d);
            }
        }
        return firstSpectrumAux;
    }


    /**
     * Return a list of features with the experimental masses, retention times
     * and composite spectra introduced as input. The method can search directly
     * the annotations from the database or not depending on
     * searchAnnotationsInDatabase (true or false)
     *
     * @param experimentalMasses
     * @param retentionTimes
     * @param compositeSpectra
     * @param searchAnnotationsInDatabase
     * @return the features
     */
    public List<Feature> loadFeaturesFromExperiment(List<Double> experimentalMasses, List<Double> retentionTimes,
            List<Map<Double, Double>> compositeSpectra, boolean searchAnnotationsInDatabase) {
        List<Feature> featuresFromInputData;
        /*System.out.println("Entering load");
        System.out.println("masses size " + experimentalMasses.size());
        System.out.println("rt size " + retentionTimes.size());
        System.out.println("cs size " + compositeSpectra.size());
         */

        Double tolerance;
        List<String> adductsFiltered;
        adductsFiltered = AdductProcessing.FilterAdductsFromInterface(this.getAdducts(), this.getIonMode());
        Integer toleranceMode;
        List<Integer> databasesInt;
        Integer metabolitesTypeInt;

        tolerance = Double.parseDouble(this.getInputmzTolerance());
        toleranceMode = DataFromInterfacesUtilities.toleranceTypeToInteger(this.getInputmzModeTolerance());
        databasesInt = DataFromInterfacesUtilities.getDatabasesAsInt(this.databases);
        metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(this.metabolitesType);
        int chemAlphabetAsInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.chemAlphabet);

        featuresFromInputData = FeaturesRTProcessing.loadFeatures(experimentalMasses, retentionTimes,
                compositeSpectra, searchAnnotationsInDatabase, this.getMassesMode(), this.getIonMode(), adductsFiltered,
                tolerance, toleranceMode, databasesInt, metabolitesTypeInt, chemAlphabetAsInt, this.msFacade);

        return featuresFromInputData;
    }

    /**
     * Copy the significative features over the corresponding item Usually the
     * user is only interested in the significant ones, but the non-significant
     * provide evidence for the annotation of the non significant ones. COPY
     * ONLY THE SIGNIFICATIVE ONES IN THE CORRESPONDING ITEM
     */
    protected abstract void getOnlySignificativeFeatures();

    /**
     * GET THE MAX NUMBER OF RT RULES APPLIED WITHIN ALL THE ANNOTATIONS.
     */
    protected abstract void calculateScores();

    /**
     * Validates the input single mass to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateInputSingleMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputSingleMass(arg0, arg1, arg2);
    }

    /**
     * Validates the retention Time to be a float between 0 and 1000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateSingleRT(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateSingleRT(arg0, arg1, arg2);
    }

}
