package controllers;

import LCMS_FEATURE.Feature;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import facades.MSFacade;
import java.util.TreeMap;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import utilities.Cadena;
import utilities.AdductsLists;
import static utilities.Constants.*;
import utilities.AdductProcessing;
import static utilities.AdductsLists.DEFAULT_ADDUCTS_POSITIVE;
import utilities.DataFromInterfacesUtilities;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import utilities.FeaturesRTProcessing;
import utilities.Constants;

/**
 * Controller (Bean) of the application for LC/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2018
 */
public abstract class LCMSControllerAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String queryInputMasses;
    private String queryInputRetentionTimes;
    private String queryInputCompositeSpectra;
    private String allInputMasses;
    private String allInputRetentionTimes;
    private String allInputCompositeSpectra;

    private String inputTolerance;
    private String inputModeTolerance;
    private String chemAlphabet;
    private boolean includeDeuterium;
    private String modifier;
    private List<SelectItem> modifierCandidates;
    private List<String> databases;
    private final List<SelectItem> DBcandidates;
    private String metabolitesType;
    private final List<SelectItem> metabolitesTypecandidates;

    private int massesMode;
    private int ionMode;
    private List<SelectItem> ionizationModeCandidates;
    private List<String> adducts;
    private List<SelectItem> adductsCandidates;
    // to Improve efficiency. They are assigned to adductsCandidates
    private final List<SelectItem> positiveCandidates;
    private final List<SelectItem> negativeCandidates;
    private final List<SelectItem> neutralCandidates;

    // Declared as a variable because JSF needs that even Framework marks as not used.
    //private int numAdducts;
    private List<Double> queryMasses;
    private List<Double> queryRetentionTimes;
    private List<Map<Double, Double>> queryCompositeSpectrum;
    private List<Boolean> isSignificativeCompound;

    private boolean isAllFeatures;

    // Declared as a variable because JSF needs that even Framework marks as not used.
    private int numAdducts;

    private final facades.MSFacade msFacade;

    public LCMSControllerAdapter() {
        this.msFacade = new MSFacade();
        this.inputTolerance = TOLERANCE_INITIAL_VALUE;
        this.inputModeTolerance = TOLERANCE_MODE_INITIAL_VALUE;
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

        this.positiveCandidates = new LinkedList<>();
        this.positiveCandidates.add(new SelectItem(DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE, "All"));
        (AdductsLists.MAPMZPOSITIVEADDUCTS).entrySet().forEach((e) -> {
            this.positiveCandidates.add(new SelectItem((String) e.getKey(), (String) e.getKey()));
        });

        this.negativeCandidates = new LinkedList<>();
        this.negativeCandidates.add(new SelectItem(DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE, "All"));
        (AdductsLists.MAPMZNEGATIVEADDUCTS).entrySet().forEach((e) -> {
            this.negativeCandidates.add(new SelectItem((String) e.getKey(), (String) e.getKey()));
        });

        this.neutralCandidates = new LinkedList<>();
        this.neutralCandidates.add(new SelectItem(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL, "All"));
        (AdductsLists.MAPNEUTRALADDUCTS).entrySet().forEach((e) -> {
            this.neutralCandidates.add(new SelectItem((String) e.getKey(), (String) e.getKey()));
        });
        this.massesMode = 0;
        this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
        this.ionMode = 0;
        this.adductsCandidates = neutralCandidates;
        this.adducts = new LinkedList<>();
        this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
        this.queryInputMasses = "";
        this.queryInputRetentionTimes = "";
        this.queryInputCompositeSpectra = "";
        this.allInputMasses = "";
        this.allInputRetentionTimes = "";
        this.allInputCompositeSpectra = "";
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
 Constants
     */
    public void setDemoMass() {
        this.setQueryInputMasses(ONEDEMOMASS);
        this.ionMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
 Constants
     */
    public void setAdvancedDemoMass() {
        this.setQueryInputMasses(ONEDEMOMASS);
        this.setQueryInputRetentionTimes(ONERETENTIONTIME);
        this.setQueryInputCompositeSpectra(ONECOMPOSITESPECTRUM);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.ionMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
 Constants
     */
    public void setDemoMasses() {
        this.setQueryInputMasses(NEWDEMOMASSES);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
 Constants
     */
    public void setAdvancedDemoMasses() {
        this.setQueryInputMasses(NEWDEMOMASSES);
        this.setQueryInputRetentionTimes(NEWDEMORETENTIONTIME);
        this.setQueryInputCompositeSpectra(NEWDEMOSPECTRUM);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.ionMode = 1;
        this.massesMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
        //System.out.println(demoMasses);
    }

    /**
     * Method to clean the input formulary where the queryMasses are written
     */
    public void clearForm() {
        this.queryInputMasses = "";
        this.queryInputRetentionTimes = "";
        this.queryInputCompositeSpectra = "";
        this.allInputMasses = "";
        this.allInputRetentionTimes = "";
        this.allInputCompositeSpectra = "";
        this.inputTolerance = TOLERANCE_INITIAL_VALUE;
        this.inputModeTolerance = TOLERANCE_MODE_INITIAL_VALUE;

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        /*this.massesMode = "neutral";
        this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
        this.ionMode = "neutral";
        this.adductsCandidates = neutralCandidates;
        this.adducts.clear();
        this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
         */
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

    /**
     * @return the queryInputMasses
     */
    public String getQueryInputMasses() {
        return this.queryInputMasses;
    }

    /**
     * Catches the input text in the formulary on the web of the queryMasses and
     * obtains the list of them in order.
     *
     * @param queryInputMasses the queryInputMasses to set
     */
    public void setQueryInputMasses(String queryInputMasses) {
        this.queryInputMasses = queryInputMasses;
    }

    public String getQueryInputRetentionTimes() {
        return queryInputRetentionTimes;
    }

    public void setQueryInputRetentionTimes(String queryInputRetentionTimes) {
        this.queryInputRetentionTimes = queryInputRetentionTimes;
    }

    public String getAllInputMasses() {
        return allInputMasses;
    }

    public void setAllInputMasses(String allInputMasses) {
        this.allInputMasses = allInputMasses;
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

    /**
     * @return the inputTolerance
     */
    public String getInputTolerance() {
        return this.inputTolerance;
    }

    /**
     * @param inputTolerance the inputTolerance to set
     */
    public void setInputTolerance(String inputTolerance) {
        this.inputTolerance = inputTolerance;
    }

    /**
     * @return the inputModeTolerance
     */
    public String getInputModeTolerance() {
        return inputModeTolerance;
    }

    /**
     * @param inputModeTolerance the inputModeTolerance to set
     */
    public void setInputModeTolerance(String inputModeTolerance) {
        this.inputModeTolerance = inputModeTolerance;
    }

    /**
     * @return the queryMasses
     */
    public List<Double> getQueryMasses() {
        return this.queryMasses;
    }

    /**
     * @param queryMasses the masses introduced to set
     */
    public void setQueryMasses(List<Double> queryMasses) {
        this.queryMasses = queryMasses;
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
     * @return the isThereInputMasses
     */
    public boolean isThereInputMasses() {
        if (getQueryMasses() != null) {
            return getQueryMasses().size() > 0;
        }
        return false;
    }

    public String getChemAlphabet() {
        return chemAlphabet;
    }

    public void setChemAlphabet(String chemAlphabet) {
        this.chemAlphabet = chemAlphabet;
    }

    public boolean isIncludeDeuterium() {
        return this.includeDeuterium;
    }

    public void setIncludeDeuterium(boolean includeDeuterium) {
        this.includeDeuterium = includeDeuterium;
    }

    public int getMassesMode() {
        return this.massesMode;
    }

    public void setMassesMode(int massesMode) {
        switch (massesMode) {
            // Neutral
            case 0:
                this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
                this.ionMode = 0;
                this.adductsCandidates = neutralCandidates;
                this.adducts.clear();
                this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
                break;
            // If there is not any of these 3 (It should not occur) The assigned mode is neutral
            // Case mz
            case 1:
                // By default, positive
                this.ionizationModeCandidates = AdductsLists.LISTIONIZEDMODES;
                this.ionMode = 1;
                this.adductsCandidates = positiveCandidates;
                this.adducts.clear();
                this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
                break;
            default:
                this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
                break;
        }
        // System.out.println("CHANGED TO: "+massesMode);
        // System.out.println("IONMODE: " + this.ionMode);
        // System.out.println("ADDUCTS: " + this.adducts);
        this.massesMode = massesMode;
    }

    /**
     * @return The ionization mode
     */
    public int getIonMode() {
        // System.out.println("ION MODE RETURNED: " + ionMode);
        return ionMode;
    }

    /**
     * Set the ionization mode
     *
     * @param ionMode
     */
    public void setIonMode(int ionMode) {
        switch (ionMode) {
            case 1:
                this.ionMode = ionMode;
                this.adductsCandidates = positiveCandidates;
                this.adducts.clear();
                this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
                break;
            case 2:
                this.ionMode = ionMode;
                this.adductsCandidates = negativeCandidates;
                this.adducts.clear();
                this.adducts.addAll(AdductsLists.DEFAULT_ADDUCTS_NEGATIVE);
                break;
            // If there is not any of these 3 (It should not occur) The assigned mode is neutral
            case 0:
                this.ionMode = ionMode;
                this.adductsCandidates = neutralCandidates;
                this.adducts.clear();
                this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
                break;
            default:
                this.ionMode = 0;
                this.adductsCandidates = neutralCandidates;
                this.adducts.clear();
                this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
                break;
        }
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
     * @return the queryMasses
     */
    public List<String> getAdducts() {
        return adducts;
    }

    /**
     * Set the adducts to the object
     *
     * @param adducts the adducts to search
     */
    public void setAdducts(List<String> adducts) {
        this.adducts = adducts;
        /*
        for(String s : adducts)
        {
            System.out.println("\n \n ADUCTO: " + s + "\n \n");
        }
         */
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

    /**
     *
     * @return the number of adducts
     */
    public int getNumAdducts() {
        if (adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE)) {
            // Double check
            if (this.ionMode != 1) {
                System.out.println("\nSomething is wrong in the search form "
                        + "Adducts contains positive and ion mode is: " + ionMode);
            }
            return AdductsLists.MAPMZPOSITIVEADDUCTS.size();
        } else if (adducts.contains(DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE)) {
            if (this.ionMode != 2) {
                System.out.println("\nSomething is wrong in the search form "
                        + "Adducts contains negative and ion mode is: " + ionMode);
            }
            return AdductsLists.MAPMZPOSITIVEADDUCTS.size();
        } else if (this.ionMode == 0) {
            return 1;
        }

        // By default, paginate with number of adducts
        return adducts.size();
    }

    public List<SelectItem> getAdductsCandidates() {
        return this.adductsCandidates;
    }

    public void setAdductsCandidates(List<SelectItem> adductsCandidates) {
        this.adductsCandidates = adductsCandidates;
    }

    public List<SelectItem> getDBcandidates() {
        return this.DBcandidates;
    }

    public List<SelectItem> getIonizationModeCandidates() {
        // System.out.println("GET CANDIDATES: "+ ionizationModeCandidates);
        return ionizationModeCandidates;
    }

    public void setIonizationModeCandidates(List<SelectItem> ionizationModeCandidates) {
        this.ionizationModeCandidates = ionizationModeCandidates;
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

            // If there is no time for all queryMasses, fill with 0
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

    

    public MSFacade getMsFacade() {
        return msFacade;
    }

    public String showMessageForNeutralMasses() {
        if (this.massesMode == 0 && (this.ionMode == 1 || this.ionMode == 2)) {
            return "calculation of new m/z from neutral mass based on selected adducts";
        } else {
            return "";
        }
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
        adductsFiltered = AdductProcessing.FilterAdductsFromInterface(this.adducts, this.ionMode);
        Integer toleranceMode;
        List<Integer> databasesInt;
        Integer metabolitesTypeInt;
        
        tolerance = Double.parseDouble(this.inputTolerance);
        toleranceMode = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputModeTolerance);
        databasesInt = DataFromInterfacesUtilities.getDatabasesAsInt(this.databases);
        metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(this.metabolitesType);
        int chemAlphabetAsInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.chemAlphabet);
        

        featuresFromInputData = FeaturesRTProcessing.loadFeatures(experimentalMasses, retentionTimes,
                compositeSpectra, searchAnnotationsInDatabase, this.massesMode, this.ionMode, adductsFiltered,
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

    public String getMINEWebPage() {
        return Constants.WEB_MINE;
    }

    public List<SelectItem> getPositiveCandidates() {
        return positiveCandidates;
    }

    public List<SelectItem> getNegativeCandidates() {
        return negativeCandidates;
    }

    public List<SelectItem> getNeutralCandidates() {
        return neutralCandidates;
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateInputTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputTolerance(arg0, arg1, arg2);
    }

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
