package controllers;

import CEMS.CEMSExperiment;
import CEMS.CEMSFeature;
import exporters.NewDataModelCompoundExcelExporter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import utilities.AdductsLists;
import static utilities.AdductsLists.DEFAULT_ADDUCTS_POSITIVE;
import utilities.Cadena;
import utilities.DataFromInterfacesUtilities;
import static utilities.Constants.RMT_TOLERANCE_MODE_INITIAL_VALUE;
import static utilities.Constants.TOLERANCE_MODE_INITIAL_VALUE;
import static utilities.Constants.RMT_TOLERANCE_INITIAL_VALUE;
import static utilities.Constants.TOLERANCE_INITIAL_VALUE;
import utilities.Constants;

/**
 * Controller (Bean) of the application for CE/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2018
 */
@SessionScoped
@Named("CEMSController")
public class CEMSController implements Serializable {

    private CEMSExperiment cemsExperiment;

    private String queryInputMasses;
    private List<Double> inputmzs;
    private String queryInputMigrationTimes;
    private List<Double> inputrmts;
    private String queryInputGroupedSignals;
    private List<Map<Double, Double>> inputGroupedSignals;

    private boolean absoluteMTTimes;
    private boolean referenceMethionineSulfone;
    private boolean referenceParacetamol;

    private String inputmzTolerance;
    private String inputmzModeTolerance;
    private Double mzTolerance;
    private String inputrmtTolerance;
    private String inputrmtModeTolerance;
    private Double rmtTolerance;

    private String bufferInput;
    private int buffer;
    private int matrix; // 1: standard, 2 plasma, etc..
    private int capilar_length; // default: 100
    private int bge; // default: 1 -> methionine sulfone
    private int temperature; // default: 20

    private String chemAlphabet;
    private boolean includeDeuterium;

    private int massesMode;
    private int ionMode;
    private List<SelectItem> ionizationModeCandidates;
    private List<String> adducts;
    private List<SelectItem> adductsCandidates;
    // to Improve efficiency. They are assigned to adductsCandidates
    private final List<SelectItem> positiveCandidates;
    private final List<SelectItem> negativeCandidates;
    private final List<SelectItem> neutralCandidates;

    @EJB
    private facades.TheoreticalCompoundsFacade ejbFacade;

    public CEMSController() {

        this.inputmzTolerance = TOLERANCE_INITIAL_VALUE;
        this.inputmzModeTolerance = TOLERANCE_MODE_INITIAL_VALUE;
        this.inputrmtTolerance = RMT_TOLERANCE_INITIAL_VALUE;
        this.inputrmtModeTolerance = RMT_TOLERANCE_MODE_INITIAL_VALUE;

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

        this.massesMode = 1;
        this.ionizationModeCandidates = AdductsLists.LISTIONIZEDMODES;
        this.ionMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts = new LinkedList<>();
        this.adducts.addAll(AdductsLists.DEFAULT_ADDUCTS_POSITIVE);
        this.queryInputMasses = "";
        this.queryInputMigrationTimes = "";
        this.queryInputGroupedSignals = "";
        this.absoluteMTTimes = false;
        referenceMethionineSulfone = false;
        referenceParacetamol = false;

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.bufferInput = "1";
        this.buffer = 1;
    }

    /**
     * Method that permits to create a excel from the current results of CEMS
     * experiment.
     *
     */
    public void exportToExcel() {
        List<CEMSFeature> cemsFeatures = getCEMSFeatures();
        if (cemsFeatures != null && !cemsFeatures.isEmpty()) {
            int flag = 3;
            // excel from CMM Compound Browse Search
            NewDataModelCompoundExcelExporter compoundExcelExporter
                    = new NewDataModelCompoundExcelExporter(flag);

            compoundExcelExporter.generateWholeExcelCompound(this.cemsExperiment.getCEMSFeatures(), flag);
        }
    }

    /**
     * Submit search for a CE MS experiment
     *
     */
    public void submitCEMSSearch() {
        List<Double> mzsAux; // auxiliar List for input Masses
        int numInputMZs;
        //Method returns an ArrayList because it is acceded by index
        mzsAux = Cadena.extractDoubles(getQueryInputMasses());

        numInputMZs = mzsAux.size();
        this.setInputmzs(mzsAux);
        List<Double> rmtAux = Cadena.getListOfDoubles(getQueryInputMigrationTimes(), numInputMZs);
        this.setInputrmts(rmtAux);

        processCEMSExperiment();
    }

    /**
     * Submit search for a CE MS experiment
     *
     */
    public void submitTargetedCEMSSearch() {
        List<Double> mzsAux; // auxiliar List for input Masses
        int numInputMZs;
        //Method returns an ArrayList because it is acceded by index
        mzsAux = Cadena.extractDoubles(getQueryInputMasses());

        numInputMZs = mzsAux.size();
        this.setInputmzs(mzsAux);
        List<Double> rmtAux = Cadena.getListOfDoubles(getQueryInputMigrationTimes(), numInputMZs);
        this.setInputrmts(rmtAux);
        // TODO GET THE MAP OF GROUPED SIGNALS
        List<Map<Double, Double>> groupedSignalsAux
                = Cadena.getListOfCompositeSpectra(queryInputGroupedSignals, numInputMZs);
        this.setInputGroupedSignals(groupedSignalsAux);;

        processTargetedCEMSExperiment();
    }

    public CEMSExperiment getCemsExperiment() {
        return cemsExperiment;
    }

    public void setCemsExperiment(CEMSExperiment cemsExperiment) {
        this.cemsExperiment = cemsExperiment;
    }

    /**
     * This method process the data introduced from lcms_advanced_search and
     * lcms_batch_advanced_search. IT tranforms data into numerical values when
     * needed Creates the experiment object with the given data (which implies
     * grouping the features by retention time)
     */
    private void processCEMSExperiment() {
        //System.out.println("Entering process CEMS experiment");

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.chemAlphabet);

        this.mzTolerance = Double.parseDouble(this.inputmzTolerance);
        this.rmtTolerance = Double.parseDouble(this.inputrmtTolerance);
        this.matrix = 2; // 2: plasma

        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        //System.out.println("TOLERANCE: " + this.mzTolerance + " " + toleranceTypeAsInt
        //        + " ionMode: " + this.ionMode + " matrix: " + this.matrix);

        this.cemsExperiment = new CEMSExperiment(this.mzTolerance, toleranceTypeAsInt,
                this.ionMode, this.matrix);

        List<CEMSFeature> cemsFeatures = this.ejbFacade.findCEMSAnnotations(inputmzs, inputrmts,
                inputmzModeTolerance, mzTolerance, inputrmtModeTolerance, rmtTolerance,
                massesMode, ionMode, adducts, chemAlphabetInt);
        this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        //System.out.println("CEMSFEATURES: " + cemsFeatures);
    }

    /**
     * This method process the data introduced from lcms_advanced_search and
     * lcms_batch_advanced_search. IT tranforms data into numerical values when
     * needed Creates the experiment object with the given data (which implies
     * grouping the features by retention time)
     */
    private void processTargetedCEMSExperiment() {
        //System.out.println("Entering process CEMS experiment");

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.chemAlphabet);

        this.mzTolerance = Double.parseDouble(this.inputmzTolerance);
        this.rmtTolerance = Double.parseDouble(this.inputrmtTolerance);
        this.matrix = 2; // 2: plasma

        int toleranceTypeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        //System.out.println("TOLERANCE: " + this.mzTolerance + " " + toleranceTypeAsInt
        //        + " ionMode: " + this.ionMode + " matrix: " + this.matrix);

        // TODO GET VALUES
        this.cemsExperiment = new CEMSExperiment(this.mzTolerance, toleranceTypeAsInt,
                this.ionMode, this.matrix);

        // TODO CHANGE THE SEARCH
        List<CEMSFeature> cemsFeatures = this.ejbFacade.findTargetedCEMSAnnotations(inputmzs, inputrmts,
                inputGroupedSignals, inputmzModeTolerance, mzTolerance, inputrmtModeTolerance, rmtTolerance,
                massesMode, ionMode, adducts, chemAlphabetInt);
        this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        //System.out.println("CEMSFEATURES: " + cemsFeatures);
    }

    /**
     * Method to clean the input formulary where the queryMasses are written
     */
    public void clearForm() {

        this.inputmzTolerance = TOLERANCE_INITIAL_VALUE;
        this.inputmzModeTolerance = TOLERANCE_MODE_INITIAL_VALUE;
        this.inputrmtTolerance = RMT_TOLERANCE_INITIAL_VALUE;
        this.inputrmtModeTolerance = RMT_TOLERANCE_MODE_INITIAL_VALUE;

        this.queryInputMasses = "";
        this.queryInputMigrationTimes = "";
        this.queryInputGroupedSignals = "";

        this.absoluteMTTimes = false;
        this.referenceMethionineSulfone = false;
        this.referenceParacetamol = false;

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
     */
    public void setCEDemoMasses() {
        this.setQueryInputMasses(Constants.CEDEMOMASSES);
        this.setQueryInputMigrationTimes(Constants.CEDEMORMT);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.ionMode = 1;
        this.massesMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
     */
    public void setTargetedCEDemoMasses() {
        this.setQueryInputMasses(Constants.CEDEMOMASSFORTARGETED);
        this.setQueryInputMigrationTimes(Constants.CEDEMORMTFORTARGETED);
        this.setQueryInputGroupedSignals(Constants.CEDEMOGROUPEDSIGNALS);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.ionMode = 1;
        this.massesMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
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
    public String getQueryInputMasses() {
        return queryInputMasses;
    }

    public void setQueryInputMasses(String queryInputMasses) {
        this.queryInputMasses = queryInputMasses;
    }

    public List<Double> getInputmzs() {
        return inputmzs;
    }

    public void setInputmzs(List<Double> inputmzs) {
        this.inputmzs = inputmzs;
    }

    public String getQueryInputMigrationTimes() {
        return queryInputMigrationTimes;
    }

    public void setQueryInputMigrationTimes(String queryInputMigrationTimes) {
        this.queryInputMigrationTimes = queryInputMigrationTimes;
    }

    public List<Double> getInputrmts() {
        return inputrmts;
    }

    public void setInputrmts(List<Double> inputrmts) {
        this.inputrmts = inputrmts;
    }

    public String getQueryInputGroupedSignals() {
        return queryInputGroupedSignals;
    }

    public void setQueryInputGroupedSignals(String queryInputGroupedSignals) {
        this.queryInputGroupedSignals = queryInputGroupedSignals;
    }

    public List<Map<Double, Double>> getInputGroupedSignals() {
        return inputGroupedSignals;
    }

    public void setInputGroupedSignals(List<Map<Double, Double>> inputGroupedSignals) {
        this.inputGroupedSignals = inputGroupedSignals;
    }

    public boolean isAbsoluteMTTimes() {
        return absoluteMTTimes;
    }

    public void setAbsoluteMTTimes(boolean absoluteMTTimes) {
        this.absoluteMTTimes = absoluteMTTimes;
    }

    public boolean isReferenceMethionineSulfone() {
        return referenceMethionineSulfone;
    }

    public void setReferenceMethionineSulfone(boolean referenceMethionineSulfone) {
        this.referenceMethionineSulfone = referenceMethionineSulfone;
    }

    public boolean isReferenceParacetamol() {
        return referenceParacetamol;
    }

    public void setReferenceParacetamol(boolean referenceParacetamol) {
        this.referenceParacetamol = referenceParacetamol;
    }

    public String getInputmzTolerance() {
        return inputmzTolerance;
    }

    public void setInputmzTolerance(String inputmzTolerance) {
        this.inputmzTolerance = inputmzTolerance;
    }

    public String getInputmzModeTolerance() {
        return inputmzModeTolerance;
    }

    public void setInputmzModeTolerance(String inputmzModeTolerance) {
        this.inputmzModeTolerance = inputmzModeTolerance;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public String getInputrmtTolerance() {
        return inputrmtTolerance;
    }

    public void setInputrmtTolerance(String inputrmtTolerance) {
        this.inputrmtTolerance = inputrmtTolerance;
    }

    public String getInputrmtModeTolerance() {
        return inputrmtModeTolerance;
    }

    public void setInputrmtModeTolerance(String inputrmtModeTolerance) {
        this.inputrmtModeTolerance = inputrmtModeTolerance;
    }

    public Double getRmtTolerance() {
        return rmtTolerance;
    }

    public void setRmtTolerance(Double rmtTolerance) {
        this.rmtTolerance = rmtTolerance;
    }

    public int getIonMode() {
        return ionMode;
    }

    public void setIonMode(int ionMode) {
        this.ionMode = ionMode;
    }

    public List<SelectItem> getIonizationModeCandidates() {
        return ionizationModeCandidates;
    }

    public void setIonizationModeCandidates(List<SelectItem> ionizationModeCandidates) {
        this.ionizationModeCandidates = ionizationModeCandidates;
    }

    public List<String> getAdducts() {
        return adducts;
    }

    public void setAdducts(List<String> adducts) {
        this.adducts = adducts;
    }

    public List<SelectItem> getAdductsCandidates() {
        return adductsCandidates;
    }

    public void setAdductsCandidates(List<SelectItem> adductsCandidates) {
        this.adductsCandidates = adductsCandidates;
    }

    public String getBufferInput() {
        return bufferInput;
    }

    public void setBufferInput(String bufferInput) {
        this.bufferInput = bufferInput;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getBuffer() {
        return buffer;
    }

    public int getMatrix() {
        return matrix;
    }

    public void setMatrix(int matrix) {
        this.matrix = matrix;
    }

    public int getCapilar_length() {
        return capilar_length;
    }

    public void setCapilar_length(int capilar_length) {
        this.capilar_length = capilar_length;
    }

    public int getBge() {
        return bge;
    }

    public void setBge(int bge) {
        this.bge = bge;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setChemAlphabet(String chemAlphabet) {
        this.chemAlphabet = chemAlphabet;
    }

    public String getChemAlphabet() {
        return chemAlphabet;
    }

    public void setIncludeDeuterium(boolean includeDeuterium) {
        this.includeDeuterium = includeDeuterium;
    }

    public boolean isIncludeDeuterium() {
        return includeDeuterium;
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

    public int getMassesMode() {
        return this.massesMode;
    }

    public void setMassesMode(int massesMode) {
        switch (massesMode) {
            // Neutral
            case 0:
                this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODESFORCEMS;
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

    public String showMessageForNeutralMasses() {
        if (this.massesMode == 0 && (this.ionMode == 1 || this.ionMode == 2)) {
            return "calculation of new m/z from neutral mass based on selected adducts";
        } else {
            return "";
        }
    }

    /**
     * @return the isThereInputMasses
     */
    public boolean isThereInputMasses() {
        if (getInputmzs() != null) {
            return getInputmzs().size() > 0;
        }
        return false;
    }

    /**
     * @return the list of features from the experiment
     */
    public List<CEMSFeature> getCEMSFeatures() {
        if (this.cemsExperiment.getCEMSFeatures() != null) {
            return cemsExperiment.getCEMSFeatures();
        } else {
            return new LinkedList<CEMSFeature>();
        }
    }

    public void setCEMSFeatures(List<CEMSFeature> cemsFeatures) {
        this.cemsExperiment.setCEMSFeatures(cemsFeatures);
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

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateInputMZTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputTolerance(arg0, arg1, arg2);
    }

}
