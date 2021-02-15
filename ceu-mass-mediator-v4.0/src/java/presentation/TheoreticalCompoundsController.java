package presentation;

import com.google.gson.Gson;
import controllers.InterfaceValidators;
import facades.TheoreticalCompoundsFacade;
import exporters.CompoundExcelExporter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import model.kbsystem.KBSystemResult;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import utilities.Cadena;
import utilities.AdductsLists;
import static utilities.Constants.*;
import ruleengine.ConfigFilter;
import ruleengine.RuleProcessor;
import static services.rest.external.KBSystemQuery.*;
import static utilities.AdductsLists.DEFAULT_ADDUCTS_POSITIVE;
import utilities.DataFromInterfacesUtilities;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import static utilities.kbsystem.Formatter.assignResultsToTheoreticalCompounds;
import static utilities.kbsystem.Formatter.FromDomCompoundToKBSystemRequestStringJSON;
import utilities.Constants;

/**
 * Controller (Bean) of the application
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@ManagedBean(name = "theoreticalCompoundsController")
@SessionScoped
public class TheoreticalCompoundsController implements Serializable, Controller {

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

    private String massesMode;
    private int ionMode;
    private List<SelectItem> ionizationModeCandidates;
    private List<String> adducts;
    private List<SelectItem> adductsCandidates;
    // to Improve efficiency. They are assigned to adductsCandidates
    private final List<SelectItem> positiveCandidates;
    private final List<SelectItem> negativeCandidates;
    private final List<SelectItem> neutralCandidates;

    // Declared as a variable because JSF needs that even Framework marks as not used.
    private int numAdducts;

    private List<Double> queryMasses;
    private List<Double> queryRetentionTimes;
    private List<Map<Double, Double>> queryCompositeSpectrum;
    private List<Boolean> isSignificativeCompound;

    private List<TheoreticalCompoundsGroup> itemsGrouped;
    private List<TheoreticalCompoundsGroup> itemsGroupedWithoutSignificative;
    private List<TheoreticalCompounds> items;
    private boolean allCompounds;

    private int maxNumberOfRTScoresApplied;

    // For FDR
    private double fdr;

    @EJB
    private facades.TheoreticalCompoundsFacade ejbFacade;

    public TheoreticalCompoundsController() {
        this.inputTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;
        //String version = FacesContext.class.getPackage().getImplementationVersion();
        //System.out.println("\n\n  VERSION DE JSF: " + version + "\n\n");
        this.items = null;
        this.itemsGrouped = new LinkedList<>();
        this.itemsGroupedWithoutSignificative = new LinkedList<>();
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
        this.massesMode = "neutral";
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
        this.maxNumberOfRTScoresApplied = 0;
        this.includeDeuterium = false;
        this.fdr = 0;
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
     */
    public void setDemoMass() {
        this.setQueryInputMasses(ONEDEMOMASS);
        this.ionMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
        //System.out.println(demoMasses);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
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
        //System.out.println(demoMasses);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
     */
    public void setDemoMasses() {
        this.setQueryInputMasses(NEWDEMOMASSES);
        this.ionMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
        //System.out.println(demoMasses);
    }

    /**
     * This method is used to load a list of queryMasses declared in the class
     * Constants
     */
    public void setAdvancedDemoMasses() {
        this.setQueryInputMasses(NEWDEMOMASSES);
        this.setQueryInputRetentionTimes(NEWDEMORETENTIONTIME);
        this.setQueryInputCompositeSpectra(NEWDEMOSPECTRUM);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.setMassesMode("mz");
    }

    /**
     * This method is used to load the demo data for the KB System Constants
     */
    public void setAdvancedDemoMassesForKBSystem() {
        this.setQueryInputMasses(NEWDEMOMZSFORKBSYSTEM);
        this.setQueryInputRetentionTimes(NEWDEMORETENTIONTIMEFORKBSYSTEM);
        this.setQueryInputCompositeSpectra(NEWDEMOSPECTRUMFORKBSYSTEM);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.setMassesMode("mz");
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
        this.inputTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        
        this.databases.clear();
        this.databases.add("AllWM");
        /*this.massesMode = "neutral";
        this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
        this.ionMode = "neutral";
        this.adductsCandidates = neutralCandidates;
        this.adducts.clear();
        this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
         */
        resetItems();
    }
    
    public void clearFormForAspergillus() {
        this.queryInputMasses = "";
        this.queryInputRetentionTimes = "";
        this.queryInputCompositeSpectra = "";
        this.allInputMasses = "";
        this.allInputRetentionTimes = "";
        this.allInputCompositeSpectra = "";
        this.inputTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
        this.databases.clear();
        this.databases.add("Aspergillus");
        /*this.massesMode = "neutral";
        this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
        this.ionMode = "neutral";
        this.adductsCandidates = neutralCandidates;
        this.adducts.clear();
        this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
         */
        resetItems();
    }

    private void resetItems() {
        this.items = null;
        this.itemsGrouped.clear();
        this.itemsGroupedWithoutSignificative.clear();
    }

    /**
     * Method that permits to create a excel from the current results. Flag
     * indicates if the excel generated contains RT field or not. 1 yes 0 no
     *
     * @param flag
     */
    public void exportToExcel(int flag) {
        // Only export to Excel no significative compounds
        if (this.items != null && !this.items.isEmpty()) {

            List<TheoreticalCompounds> itemsWithoutSignificative = new LinkedList<>();

            for (TheoreticalCompounds tc : this.items) {
                if (tc.isSignificativeCompound()) {
                    itemsWithoutSignificative.add(tc);
                }
            }
            CompoundExcelExporter compoundExcelExporter = new CompoundExcelExporter(flag);
            compoundExcelExporter.generateWholeExcelCompound(itemsWithoutSignificative, flag);
        }
    }

    /**
     * Submit compounds in advanced mode.
     */
    public void submitCompoundsAdvanced() {
        this.items = null;
        this.itemsGrouped.clear();
        this.itemsGroupedWithoutSignificative.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        List<Double> retAux; // Auxiliar List for Retention Times
        List<Map<Double, Double>> spectrumAux;  // Auxiliar List for Composite Spectra processed
        List<Boolean> isSignifativeCompoundAux;
        // CHANGE THE INPUT ELEMENTS FOR THE ALL INPUT ELEMENTS
        List<Double> allMassesAux;
        List<Double> allRetAux;
        List<Map<Double, Double>> AllSpectrumAux;
        if (this.allInputMasses.equals("")) {
            this.allCompounds = false;
            //Method returns an ArrayList because it is acceded by index
            massesAux = Cadena.extractDoubles(this.queryInputMasses);
            numInputMasses = massesAux.size();
            retAux = Cadena.getListOfDoubles(this.queryInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = new ArrayList<>(Collections.nCopies(numInputMasses, true));
            spectrumAux = getListOfCompositeSpectra(this.queryInputCompositeSpectra, numInputMasses);
            // System.out.println("INPUT: " + queryInputMasses + " \n ARRAY: " + massesAux);
            // System.out.println("INPUT RETENTION TIME: " + this.queryInputRetentionTimes);
            // System.out.println("INPUT COMPOSITE SPECTRUM: " + this.queryInputCompositeSpectra);
            //System.out.println("SIMPLE SEARCH");
            //System.out.println("Sign Compounds: "+ isSignifativeCompoundAux.toString() + " size: " + isSignifativeCompoundAux.size());
            this.setQueryMasses(massesAux);
            this.setQueryRetentionTimes(retAux);
            this.setQueryCompositeSpectrum(spectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        } else {
            this.allCompounds = true;
            Set<String> KeysSignificativeCompounds; // Set for save the keys with <Mass>_<RT> of significative compounds
            KeysSignificativeCompounds = Cadena.generateSetOfSignificativeCompounds(this.queryInputMasses, this.queryInputRetentionTimes);

            //Method returns an ArrayList because it is acceded by index
            allMassesAux = Cadena.extractDoubles(this.allInputMasses);
            numInputMasses = allMassesAux.size();
            allRetAux = Cadena.getListOfDoubles(this.allInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = Cadena.fillIsSignificativeCompound(this.allInputMasses,
                    this.allInputRetentionTimes,
                    numInputMasses,
                    KeysSignificativeCompounds);
            //System.out.println("allInputMasses: " + allInputMasses.toString());
            //System.out.println("allRT: " + allInputRetentionTimes.toString());
            //System.out.println("ADVANCED SEARCH");
            //System.out.println("KEYS: " + KeysSignificativeCompounds.toString());
            //System.out.println("Significatives Compounds: " + isSignifativeCompoundAux.toString()+ " size: " + isSignifativeCompoundAux.size());

            AllSpectrumAux = getListOfCompositeSpectra(this.allInputCompositeSpectra, numInputMasses);

            this.setQueryMasses(allMassesAux);
            this.setQueryRetentionTimes(allRetAux);
            this.setQueryCompositeSpectrum(AllSpectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        }

        processGroupedCompoundsAdvanced();
        calculateScores();
        getOnlySignificativeCompounds();

    }
    
    /**
     * Submit compounds in advanced mode.
     */
    public void submitCompoundsAspergillus() {
        this.items = null;
        this.itemsGrouped.clear();
        this.itemsGroupedWithoutSignificative.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        List<Double> retAux; // Auxiliar List for Retention Times
        List<Map<Double, Double>> spectrumAux;  // Auxiliar List for Composite Spectra processed
        List<Boolean> isSignifativeCompoundAux;
        // CHANGE THE INPUT ELEMENTS FOR THE ALL INPUT ELEMENTS
        List<Double> allMassesAux;
        List<Double> allRetAux;
        List<Map<Double, Double>> AllSpectrumAux;
        if (this.allInputMasses.equals("")) {
            this.allCompounds = false;
            //Method returns an ArrayList because it is acceded by index
            massesAux = Cadena.extractDoubles(this.queryInputMasses);
            numInputMasses = massesAux.size();
            retAux = Cadena.getListOfDoubles(this.queryInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = new ArrayList<>(Collections.nCopies(numInputMasses, true));
            spectrumAux = getListOfCompositeSpectra(this.queryInputCompositeSpectra, numInputMasses);
            // System.out.println("INPUT: " + queryInputMasses + " \n ARRAY: " + massesAux);
            // System.out.println("INPUT RETENTION TIME: " + this.queryInputRetentionTimes);
            // System.out.println("INPUT COMPOSITE SPECTRUM: " + this.queryInputCompositeSpectra);
            //System.out.println("SIMPLE SEARCH");
            //System.out.println("Sign Compounds: "+ isSignifativeCompoundAux.toString() + " size: " + isSignifativeCompoundAux.size());
            this.setQueryMasses(massesAux);
            this.setQueryRetentionTimes(retAux);
            this.setQueryCompositeSpectrum(spectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        } else {
            this.allCompounds = true;
            Set<String> KeysSignificativeCompounds; // Set for save the keys with <Mass>_<RT> of significative compounds
            KeysSignificativeCompounds = Cadena.generateSetOfSignificativeCompounds(this.queryInputMasses, this.queryInputRetentionTimes);

            //Method returns an ArrayList because it is acceded by index
            allMassesAux = Cadena.extractDoubles(this.allInputMasses);
            numInputMasses = allMassesAux.size();
            allRetAux = Cadena.getListOfDoubles(this.allInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = Cadena.fillIsSignificativeCompound(this.allInputMasses,
                    this.allInputRetentionTimes,
                    numInputMasses,
                    KeysSignificativeCompounds);
            //System.out.println("allInputMasses: " + allInputMasses.toString());
            //System.out.println("allRT: " + allInputRetentionTimes.toString());
            //System.out.println("ADVANCED SEARCH");
            //System.out.println("KEYS: " + KeysSignificativeCompounds.toString());
            //System.out.println("Significatives Compounds: " + isSignifativeCompoundAux.toString()+ " size: " + isSignifativeCompoundAux.size());

            AllSpectrumAux = getListOfCompositeSpectra(this.allInputCompositeSpectra, numInputMasses);

            this.setQueryMasses(allMassesAux);
            this.setQueryRetentionTimes(allRetAux);
            this.setQueryCompositeSpectrum(AllSpectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        }

        processGroupedCompoundsAdvanced();
        calculateScores();
        getOnlySignificativeCompounds();

    }
    

    /**
     * Submit Compounds in Simple mode.
     */
    public void submitCompoundsSimple() {
        this.items = null;
        this.itemsGrouped.clear();
        this.itemsGroupedWithoutSignificative.clear();
        List<Double> massesAux; // auxiliar List for input Masses

        this.allCompounds = false;
        //Method returns an ArrayList because it is acceded by index
        massesAux = Cadena.extractDoubles(this.queryInputMasses);
        // System.out.println("INPUT: " + queryInputMasses + " \n ARRAY: " + massesAux);
        this.setQueryMasses(massesAux);

        processGroupedCompoundsSimple();
        this.itemsGroupedWithoutSignificative = this.itemsGrouped;
    }

    /**
     * Submit compounds in advanced mode.
     */
    public void submitCompoundsAdvancedKBSystem() {
        this.items = null;
        this.itemsGrouped.clear();
        this.itemsGroupedWithoutSignificative.clear();
        List<Double> massesAux; // auxiliar List for input Masses
        int numInputMasses;
        List<Double> retAux; // Auxiliar List for Retention Times
        List<Map<Double, Double>> spectrumAux;  // Auxiliar List for Composite Spectra processed
        List<Boolean> isSignifativeCompoundAux;
        // CHANGE THE INPUT ELEMENTS FOR THE ALL INPUT ELEMENTS
        List<Double> allMassesAux;
        List<Double> allRetAux;
        List<Map<Double, Double>> AllSpectrumAux;
        if (this.allInputMasses.equals("")) {
            this.allCompounds = false;
            //Method returns an ArrayList because it is acceded by index
            massesAux = Cadena.extractDoubles(this.queryInputMasses);
            numInputMasses = massesAux.size();
            retAux = Cadena.getListOfDoubles(this.queryInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = new ArrayList<>(Collections.nCopies(numInputMasses, true));
            spectrumAux = getListOfCompositeSpectra(this.queryInputCompositeSpectra, numInputMasses);
            // System.out.println("INPUT: " + queryInputMasses + " \n ARRAY: " + massesAux);
            // System.out.println("INPUT RETENTION TIME: " + this.queryInputRetentionTimes);
            // System.out.println("INPUT COMPOSITE SPECTRUM: " + this.queryInputCompositeSpectra);
            //System.out.println("SIMPLE SEARCH");
            //System.out.println("Sign Compounds: "+ isSignifativeCompoundAux.toString() + " size: " + isSignifativeCompoundAux.size());
            this.setQueryMasses(massesAux);
            this.setQueryRetentionTimes(retAux);
            this.setQueryCompositeSpectrum(spectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        } else {
            this.allCompounds = true;
            Set<String> KeysSignificativeCompounds; // Set for save the keys with <Mass>_<RT> of significative compounds
            KeysSignificativeCompounds = Cadena.generateSetOfSignificativeCompounds(this.queryInputMasses, this.queryInputRetentionTimes);

            //Method returns an ArrayList because it is acceded by index
            allMassesAux = Cadena.extractDoubles(this.allInputMasses);
            numInputMasses = allMassesAux.size();
            allRetAux = Cadena.getListOfDoubles(this.allInputRetentionTimes, numInputMasses);
            isSignifativeCompoundAux = Cadena.fillIsSignificativeCompound(this.allInputMasses,
                    this.allInputRetentionTimes,
                    numInputMasses,
                    KeysSignificativeCompounds);
            //System.out.println("allInputMasses: " + allInputMasses.toString());
            //System.out.println("allRT: " + allInputRetentionTimes.toString());
            //System.out.println("ADVANCED SEARCH");
            //System.out.println("KEYS: " + KeysSignificativeCompounds.toString());
            //System.out.println("Significatives Compounds: " + isSignifativeCompoundAux.toString()+ " size: " + isSignifativeCompoundAux.size());

            AllSpectrumAux = getListOfCompositeSpectra(this.allInputCompositeSpectra, numInputMasses);

            this.setQueryMasses(allMassesAux);
            this.setQueryRetentionTimes(allRetAux);
            this.setQueryCompositeSpectrum(AllSpectrumAux);
            this.setIsSignificativeCompound(isSignifativeCompoundAux);
        }

        processCompoundsAdvancedKBSystem();
        getOnlySignificativeCompounds();

    }

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

    private TheoreticalCompoundsFacade getFacade() {
        return this.ejbFacade;
    }

    @Override
    /**
     * Getter of Items. If Items is null, the method is going to create a new
     * instance
     *
     */
    public List<TheoreticalCompounds> getItems() {
        System.out.println("\nGET ITEMS CALLED -> \n");
        return this.items;
    }

    @Override
    /**
     * Getter of ItemsGrouped. If ItemsGrouped is null, the method is going to
     * create a new instance
     *
     */
    public List<TheoreticalCompoundsGroup> getItemsGrouped() {
        return this.itemsGrouped;
    }

    @Override
    /**
     * Getter of getItemsGroupedWithoutSignificative. If ItemsGrouped is null,
     * the method is going to create a new instance
     *
     */
    public List<TheoreticalCompoundsGroup> getItemsGroupedWithoutSignificative() {
        return itemsGroupedWithoutSignificative;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<TheoreticalCompounds> items) {
        this.items = items;
    }

    /**
     * @param itemsGrouped the items to set
     */
    public void setItemsGrouped(List<TheoreticalCompoundsGroup> itemsGrouped) {
        this.itemsGrouped = itemsGrouped;
    }

    /**
     * @param itemsGroupedWithoutSignificative the items to set
     */
    public void setItemsGroupedWithoutSignificative(List<TheoreticalCompoundsGroup> itemsGroupedWithoutSignificative) {
        this.itemsGroupedWithoutSignificative = itemsGroupedWithoutSignificative;
    }

    /**
     * @return the thereAreTheoreticalCompounds
     */
    public boolean isThereTheoreticalCompounds() {
        return getQueryMasses().size() > 0;
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

    public String getMassesMode() {
        return this.massesMode;
    }

    public void setMassesMode(String massesMode) {
        switch (massesMode) {
            case "neutral":
                this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
                this.ionMode = 0;
                this.adductsCandidates = neutralCandidates;
                this.adducts.clear();
                this.adducts.add(DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL);
                break;
            // If there is not any of these 3 (It should not occur) The assigned mode is neutral
            case "mz":
                // By default, positive
                this.ionizationModeCandidates = AdductsLists.LISTIONIZEDMODES;
                this.ionMode = 1;
                this.adductsCandidates = positiveCandidates;
                this.adducts.clear();
                this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
                break;
            /*
            case Constants.NAME_FOR_RECALCULATED:
                this.ionizationModeCandidates = AdductsLists.LISTIONIZEDMODES;
                this.ionMode = "positive";
                this.adductsCandidates = positiveCandidates;
                this.adducts.clear();
                this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
                break;
             */
            default:
                this.ionizationModeCandidates = AdductsLists.LISTNEUTRALMODES;
                break;
        }
        // System.out.println("CHANGED TO: "+massesMode);
        // System.out.println("IONMODE: " + ionMode);
        // System.out.println("ADDUCTS: " + adducts);
        this.massesMode = massesMode;
    }

    /**
     * @return The ionization mode
     */
    public int getIonMode() {
        // System.out.println("ION MODE RETURNED: " + ionMode);
        return this.ionMode;
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
    public boolean isAllCompounds() {
        return allCompounds;
    }

    /**
     * @param allCompounds user introduced significant and non significant
     * compounds
     */
    public void setAllCompounds(boolean allCompounds) {
        this.allCompounds = allCompounds;
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

    private List<Double> getListOfFirstDataSpectrum(String input, int numInputMasses) {
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

    private List<Map<Double, Double>> getListOfCompositeSpectra(String input, int numInputMasses) {
        List<Map<Double, Double>> spectrumAux;
        if (!input.equals("")) {
            spectrumAux = Cadena.extractDataSpectrum(input);
            // If there is no time for all queryMasses, fill with 0
            for (int i = spectrumAux.size(); i < numInputMasses; i++) {
                spectrumAux.add(new TreeMap<Double, Double>());
            }
        } else {
            spectrumAux = new ArrayList<Map<Double, Double>>();
            // If there is no time for all queryMasses, fill with 0
            for (int i = 0; i < numInputMasses; i++) {
                spectrumAux.add(new TreeMap<Double, Double>());
            }

        }
        return spectrumAux;
    }

    public String showMessageForNeutralMasses() {
        if (this.massesMode.equals(Constants.NAME_FOR_RECALCULATED) && (this.ionMode == 1 || this.ionMode == 2)) {
            return "calculation of new m/z from neutral mass based on selected adducts";
        } else {
            return "";
        }
    }
    
    private void processGroupedCompoundsAspergillus() {

        // Time measurement
        // long startTime = System.currentTimeMillis();
        String chemAlphabetWithDeuterium = DataFromInterfacesUtilities.getChemAlphabet(this.chemAlphabet, this.includeDeuterium);
        int chemAlphabetForSearch = DataFromInterfacesUtilities.getIntChemAlphabet(chemAlphabetWithDeuterium);
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            this.items = this.ejbFacade.findCompoundsAdvanced(
                    this.queryMasses,
                    this.queryRetentionTimes,
                    this.queryCompositeSpectrum,
                    this.isSignificativeCompound,
                    this.inputModeTolerance,
                    Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium,
                    this.ionMode,
                    this.massesMode,
                    this.adducts,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
            this.fdr = this.ejbFacade.getFDR(this.queryMasses, this.inputModeTolerance, Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium, this.ionMode, this.massesMode, this.databases, this.metabolitesType);
            // Time measurement
            //long compoundsFromDBTime = System.currentTimeMillis() - startTime;
            processAllRules();
            /*
            // Time measurement
            long DroolsTime = System.currentTimeMillis() - startTime;
            System.out.println("TIMES OF EXECUTION: "
                    + "\ncompoundsFromDBTime: " + compoundsFromDBTime
                    + "\nDroolsTime: "
                    + (DroolsTime - compoundsFromDBTime)
                    + " Accumulated" + DroolsTime);
             */

 /* System.out.println("\n \n GROUP OF compounds:" + itemsGrouped.toString());
            for (TheoreticalCompoundsGroup group : itemsGrouped) {
                System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
                System.out.println("Number of elemens: ");
                for (TheoreticalCompounds tc : group.getTheoreticalCompounds()) {
                    System.out.println("Compound: " + tc.getIdentifier());
                }
            }
             */
        }
    }

    private void processGroupedCompoundsAdvanced() {

        // Time measurement
        // long startTime = System.currentTimeMillis();
        String chemAlphabetWithDeuterium = DataFromInterfacesUtilities.getChemAlphabet(this.chemAlphabet, this.includeDeuterium);
        int chemAlphabetForSearch = DataFromInterfacesUtilities.getIntChemAlphabet(chemAlphabetWithDeuterium);
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            this.items = this.ejbFacade.findCompoundsAdvanced(
                    this.queryMasses,
                    this.queryRetentionTimes,
                    this.queryCompositeSpectrum,
                    this.isSignificativeCompound,
                    this.inputModeTolerance,
                    Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium,
                    this.ionMode,
                    this.massesMode,
                    this.adducts,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
            this.fdr = this.ejbFacade.getFDR(this.queryMasses, this.inputModeTolerance, Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium, this.ionMode, this.massesMode, this.databases, this.metabolitesType);
            // Time measurement
            //long compoundsFromDBTime = System.currentTimeMillis() - startTime;
            processAllRules();
            /*
            // Time measurement
            long DroolsTime = System.currentTimeMillis() - startTime;
            System.out.println("TIMES OF EXECUTION: "
                    + "\ncompoundsFromDBTime: " + compoundsFromDBTime
                    + "\nDroolsTime: "
                    + (DroolsTime - compoundsFromDBTime)
                    + " Accumulated" + DroolsTime);
             */

 /* System.out.println("\n \n GROUP OF compounds:" + itemsGrouped.toString());
            for (TheoreticalCompoundsGroup group : itemsGrouped) {
                System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
                System.out.println("Number of elemens: ");
                for (TheoreticalCompounds tc : group.getTheoreticalCompounds()) {
                    System.out.println("Compound: " + tc.getIdentifier());
                }
            }
             */
        }
    }

    private void processCompoundsAdvancedKBSystem() {

        String chemAlphabetWithDeuterium = DataFromInterfacesUtilities.getChemAlphabet(this.chemAlphabet, this.includeDeuterium);
        // Time measurement
        long startTime = System.currentTimeMillis();
        int chemAlphabetForSearch = DataFromInterfacesUtilities.getIntChemAlphabet(chemAlphabetWithDeuterium);
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            this.items = this.ejbFacade.findCompoundsAdvanced(
                    this.queryMasses,
                    this.queryRetentionTimes,
                    this.queryCompositeSpectrum,
                    this.isSignificativeCompound,
                    this.inputModeTolerance,
                    Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium,
                    this.ionMode,
                    this.massesMode,
                    this.adducts,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
            this.fdr = this.ejbFacade.getFDR(this.queryMasses, this.inputModeTolerance, Double.parseDouble(this.inputTolerance),
                    chemAlphabetWithDeuterium, this.ionMode, this.massesMode, this.databases, this.metabolitesType);

            // Time measurement
            //long compoundsFromDBTime = System.currentTimeMillis() - startTime;
            // List<KBSystemCompound> listKBCompounds = FromDomCompoundToKBSystemCompounds(this.items);
            // System.out.println("LIST DE KB SYSTEM COMPOUNDS: " + listKBCompounds);
            String queryJSON = FromDomCompoundToKBSystemRequestStringJSON(this.ionMode, this.items);
            // Time measurement
            // long fromDomCompoundToKBSystemCompoundsJSONTime = System.currentTimeMillis() - startTime;
            //System.out.println("queryJSON: " + queryJSON);
            String resultJSON = KBRequestFromString(queryJSON);
            // Time measurement
            //long requestTime = System.currentTimeMillis() - startTime;
            //System.out.println("RESULTS JSON: " + resultJSON);
            Gson gson = new Gson();
            KBSystemResult[] kbsystemResults = gson.fromJson(resultJSON, KBSystemResult[].class);

            Arrays.sort(kbsystemResults);
//            Arrays.stream(kbsystemResults).forEach( res -> {
//                System.out.println(res);
//            });
            assignResultsToTheoreticalCompounds(this.items, kbsystemResults);
            /*
            // Time measurement
            long fromJSONToDomCompoundTime = System.currentTimeMillis() - startTime;
            System.out.println("TIMES OF EXECUTION: "
                    + "\ncompoundsFromDBTime: " + compoundsFromDBTime
                    + "\nfromDomCompoundToKBSystemCompoundsJSONTime: "
                    + (fromDomCompoundToKBSystemCompoundsJSONTime - compoundsFromDBTime)
                    + " Accumulated" + fromDomCompoundToKBSystemCompoundsJSONTime
                    + "\nrequestTime: "
                    + (requestTime - fromDomCompoundToKBSystemCompoundsJSONTime)
                    + " Accumulated" + requestTime
                    + "\nfromJSONToDomCompoundTime: "
                    + (fromJSONToDomCompoundTime - requestTime)
                    + " Accumulated" + fromJSONToDomCompoundTime);
             */
        }
    }

    private void processGroupedCompoundsSimple() {
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            this.items = this.ejbFacade.findCompoundsSimple(
                    this.queryMasses,
                    this.inputModeTolerance,
                    Double.parseDouble(this.inputTolerance),
                    this.ionMode,
                    this.massesMode,
                    this.adducts,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
            this.fdr = this.ejbFacade.getFDR(this.queryMasses, this.inputModeTolerance, Double.parseDouble(this.inputTolerance),
                    this.ionMode, this.massesMode, this.databases, this.metabolitesType);

            processIonizationRules();

            /*System.out.println("\n \n GROUP OF compounds:" + itemsGrouped.toString());
            for (TheoreticalCompoundsGroup group : itemsGrouped) {
                System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
                System.out.println("Number of elemens: ");
                for (TheoreticalCompounds tc : group.getTheoreticalCompounds()) {
                    System.out.println("Compound: " + tc.getIdentifier());
                }
            }
             */
        }
    }

    private void processAllRules() {
        // Drools.
        // Creates configFilter with ionization mode.
        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(this.modifier);
        configFilter.setIonMode(this.ionMode);
        configFilter.setAllCompounds(this.allCompounds);

        //KieContainer kContainer = RuleProcessor.getContainer("TheoreticalCompounds");
        // Execute rules.
        //RuleProcessor.processRulesTC(this.allFeatures, configFilter, kContainer);
        RuleProcessor.processRulesTC(this.items, configFilter);

    }

    private void processIonizationRules() {
        // Drools.
        // Creates configFilter with ionization mode.
        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(this.modifier);
        configFilter.setIonMode(this.ionMode);
        configFilter.setAllCompounds(this.allCompounds);

        //KieContainer kContainer = RuleProcessor.getContainer("TheoreticalCompounds");
        // Execute rules.
        //RuleProcessor.processSimpleSearchTC(this.items, configFilter, kContainer);
        RuleProcessor.processSimpleSearchTC(this.items, configFilter);

        // Write tags for colors
        this.items.forEach((tc) -> {
            tc.createColorIonizationScore();
        });
    }

    private void getOnlySignificativeCompounds() {
        if (this.itemsGroupedWithoutSignificative == null) {
            this.itemsGroupedWithoutSignificative = new LinkedList<>();
        } else if (this.itemsGroupedWithoutSignificative.isEmpty()) {
        } else {
            this.itemsGroupedWithoutSignificative = new LinkedList<>();
        }
        int i = 0;
        int localNumAdducts = getNumAdducts();
        for (boolean isSignificative : this.isSignificativeCompound) {
            if (isSignificative) {
                for (int j = 0; j < localNumAdducts; j++) {
                    int indexTC = i * localNumAdducts + j;
                    this.itemsGroupedWithoutSignificative.add(this.itemsGrouped.get(indexTC));
                }
            }
            i++;
        }
    }

    private void calculateMaxNumberOfRTScores() {
        for (TheoreticalCompoundsGroup tcg : this.itemsGrouped) {
            for (Object o : tcg.getTheoreticalCompounds()) {
                TheoreticalCompounds tc = (TheoreticalCompounds) o;
                if (tc.getNumberRTScores() > this.maxNumberOfRTScoresApplied) {
                    this.maxNumberOfRTScoresApplied = tc.getNumberRTScores();
                }
            }
        }
        //System.out.println("MAX NUMBER OF RT APPLIED: " + this.maxNumberOfRTScoresApplied);
    }

    private void calculateScores() {
        calculateMaxNumberOfRTScores();
        for (TheoreticalCompoundsGroup tcg : this.itemsGrouped) {
            for (Object o : tcg.getTheoreticalCompounds()) {
                TheoreticalCompounds tc = (TheoreticalCompounds) o;
                tc.calculateRetentionTimeScore();
                tc.calculateFinalScore(this.maxNumberOfRTScoresApplied);
                // Create tags for colours
                tc.createColorIonizationScore();
                tc.createColorAdductRelationScore();
                //
                tc.createColorRetentionTimeScore();
                tc.createColorFinalScore();
            }
        }
    }

    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
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

    public static String roundToFourDecimals(Double doubleToRound) {
        if (doubleToRound == null) {
            return "--";
        }
        return String.format("%.4f", doubleToRound).replace(",", ".");
    }
}
