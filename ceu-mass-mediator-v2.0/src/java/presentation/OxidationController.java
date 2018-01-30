package presentation;

import exporters.CompoundExcelExporter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import presentation.paginationHelpers.MyPaginationHelper;
import presentation.paginationHelpers.PaginationHelper;
import utilities.Cadena;
import static utilities.Constantes.TOLERANCE_MODE_INICITAL_VALUE;
import utilities.ConstantesForOxidation;
import utilities.OxidationLists;
import utilities.PatternFinder;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;

/**
 * Controller (Bean) of the application for the oxidation feature
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/10/2017
 */
@ManagedBean(name = "oxidationController")
@SessionScoped
public class OxidationController implements Serializable {

    private static final long serialVersionUID = 1L;

    private MyPaginationHelper pagination;
    private String queryInputParentIonMass;
    private String queryInputFattyAcidMass1;
    private String queryInputFattyAcidMass2;

    private String inputToleranceForFA;
    private String inputModeToleranceForFA;
    private String inputToleranceForPI;
    private String inputModeToleranceForPI;
    private final String ionMode;

    private Double queryParentIonMass;
    private List<Double> queryFattyAcidMasses;

    private List<String> oxidations;
    private List<SelectItem> oxidationsCandidates;

    private List<String> databasesForPISearch;
    private List<SelectItem> DBcandidates;

    // NOT USED
    // private boolean thereAreTheoreticalCompounds;
    private List<OxidizedTheoreticalCompound> oxidizedCompoundsList;

    @EJB
    private presentation.TheoreticalCompoundsFacade ejbFacade;

    public OxidationController() {
        this.inputToleranceForFA = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForFA = TOLERANCE_MODE_INICITAL_VALUE;
        this.inputToleranceForPI = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForPI = TOLERANCE_MODE_INICITAL_VALUE;
        //String version = FacesContext.class.getPackage().getImplementationVersion();
        //System.out.println("\n\n  VERSION DE JSF: " + version + "\n\n");
        this.oxidizedCompoundsList = null;
        this.DBcandidates = new LinkedList<SelectItem>();
        this.DBcandidates.add(new SelectItem("all", "All"));
        for (String db : OxidationLists.LISTDB) {
            this.DBcandidates.add(new SelectItem(db, db));
        }
        this.databasesForPISearch = new LinkedList<String>();
        this.databasesForPISearch.add("all");

        this.oxidationsCandidates = new LinkedList<SelectItem>();
        this.ionMode = "negative";
        this.oxidations = new LinkedList<String>();
        this.oxidations.add("allOxidations");
        this.queryInputParentIonMass = "";
        this.queryInputFattyAcidMass1 = "";
        this.queryInputFattyAcidMass2 = "";
    }
    
    /**
     * Method to initialize the possible oxidations on long chain oxidation mode
     */
    public void loadLCOxidations() {
        this.oxidationsCandidates.clear();
        this.oxidationsCandidates.add(new SelectItem("allOxidations", "All"));
        for (String oxidation : OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES) {
            this.oxidationsCandidates.add(new SelectItem(oxidation, oxidation));
        }
    }
    
    /**
     * Method to initialize the possible oxidations on short chain oxidation mode
     */
    public void loadSCOxidations() {
        this.oxidationsCandidates.clear();
        this.oxidationsCandidates.add(new SelectItem("allOxidations", "All"));
        for (String oxidation : OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES) {
            this.oxidationsCandidates.add(new SelectItem(oxidation, oxidation));
        }
    }

    /**
     * Method to set Demo Masses and Tolerances (LC oxidized FA m/z, SC non-oxidized m/z and Parent ion m/z).
     */
    public void setLCDemoMasses() {
        this.setQueryInputParentIonMass(ConstantesForOxidation.LC_PARENTIONDEMOMASS);
        this.setQueryInputFattyAcidMass1(ConstantesForOxidation.LC_OX_FATTYACIDDEMOMASS);
        this.setQueryInputFattyAcidMass2(ConstantesForOxidation.LC_NONOX_FATTYACIDDEMOMASS);
        this.inputToleranceForFA = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForFA = TOLERANCE_MODE_INICITAL_VALUE;
        this.inputToleranceForPI = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForPI = TOLERANCE_MODE_INICITAL_VALUE;
    }
    
    /**
     * Method to set Demo Masses and Tolerances (LC non-oxidized FA m/z and Parent ion m/z).
     */
    public void setSCDemoMasses() {
        this.setQueryInputParentIonMass(ConstantesForOxidation.SC_PARENTIONDEMOMASS);
        this.setQueryInputFattyAcidMass1(ConstantesForOxidation.SC_NONOX_FATTYACIDDEMOMASS);
        this.inputToleranceForFA = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForFA = TOLERANCE_MODE_INICITAL_VALUE;
        this.inputToleranceForPI = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForPI = TOLERANCE_MODE_INICITAL_VALUE;
    }

    /**
     * Method to clean the input formulary where the queryParentIonMass are
     * written
     */
    public void clearForm() {
        this.queryInputParentIonMass = "";
        this.queryInputFattyAcidMass1 = "";
        this.queryInputFattyAcidMass2 = "";
        this.inputToleranceForFA = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForFA = TOLERANCE_MODE_INICITAL_VALUE;
        this.inputToleranceForPI = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        this.inputModeToleranceForPI = TOLERANCE_MODE_INICITAL_VALUE;
        /*
        this.oxidations.clear();
        this.oxidations.add("allOxidations");
         */
        this.oxidizedCompoundsList = null;
    }

    /**
     * TODO Method that permits to create a excel from the current results.
     */
    /*
    public void exportToExcel() {
        // All pages display the number of input compounds defined in ITEMS_PER_PAGE_IN_EXCEL

        if (pagination == null) {
            pagination = new MyPaginationHelper(ITEMS_PER_PAGE_IN_EXCEL,
                    items.size());
        }
        CompoundExcelExporter compoundExcelExporter = new CompoundExcelExporter();
        compoundExcelExporter.generateWholeExcelCompound(items, pagination);
    }
     */
    /**
     *
     */
    public void submit() {
        System.out.println("Working on it..");
    }

    /**
     * Method to reset all the items that are instantiated in the class Reset
     * all the compounds obtained by the mediator.
     */
    public void submitLCOxidationCompounds() {
        this.oxidizedCompoundsList = null;
        int numFattyAcidInputMasses;
        this.queryInputParentIonMass = this.queryInputParentIonMass.replace(",", ".");
        this.queryInputFattyAcidMass1 = this.queryInputFattyAcidMass1.replace(",", ".");
        this.queryInputFattyAcidMass2 = this.queryInputFattyAcidMass2.replace(",", ".");
        List<Double> fattyAcidMassesAux; // auxiliar List for input Masses
        fattyAcidMassesAux = new ArrayList<Double>();
        //Method returns an ArrayList because it is acceded by index
        if (org.apache.commons.lang3.math.NumberUtils.isNumber(this.queryInputFattyAcidMass1)) {
            fattyAcidMassesAux.add(Cadena.extractFirstDouble(this.queryInputFattyAcidMass1));
        }

        if (org.apache.commons.lang3.math.NumberUtils.isNumber(this.queryInputFattyAcidMass2)) {
            fattyAcidMassesAux.add(Cadena.extractFirstDouble(this.queryInputFattyAcidMass2));
        }
        // System.out.println("INPUT: " + queryInputParentIonMass + " \n ARRAY: " + massesAux);

        this.setQueryFattyAcidMasses(fattyAcidMassesAux);
        numFattyAcidInputMasses = fattyAcidMassesAux.size();
        // System.out.println("INPUT: " + queryInputParentIonMasses + " \n ARRAY: " + massesAux);

        this.setQueryParentIonMass(Cadena.extractFirstDouble(this.queryInputParentIonMass));

        // Change for ALL
        pagination = new MyPaginationHelper(ConstantesForOxidation.ITEMS_PER_PAGE_IN_EXCEL, 1);
        processLCOxidationCompounds();
    }

    /**
     * Method to reset all the items that are instantiated in the class Reset
     * all the compounds obtained by the mediator.
     */
    public void submitSCOxidationCompounds() {
        this.oxidizedCompoundsList = null;

        int numFattyAcidInputMasses;
        this.queryInputParentIonMass = this.queryInputParentIonMass.replace(",", ".");
        List<Double> fattyAcidMassesAux; // auxiliar List for input Masses
        fattyAcidMassesAux = new ArrayList<Double>();
        //Method returns an ArrayList because it is acceded by index
        if (org.apache.commons.lang3.math.NumberUtils.isNumber(this.queryInputFattyAcidMass1)) {
            fattyAcidMassesAux.add(Cadena.extractFirstDouble(this.queryInputFattyAcidMass1));
        }

        this.setQueryFattyAcidMasses(fattyAcidMassesAux);
        numFattyAcidInputMasses = fattyAcidMassesAux.size();
        // System.out.println("INPUT: " + queryInputParentIonMasses + " \n ARRAY: " + massesAux);

        this.setQueryParentIonMass(Cadena.extractFirstDouble(this.queryInputParentIonMass));

        // Change for ALL
        //pagination = new MyPaginationHelper(ConstantesForOxidation.ITEMS_PER_PAGE_IN_EXCEL, 1);
        processSCOxidationCompounds();
    }

    public void resetOxidizedCompound() {
        this.oxidizedCompoundsList = null;
    }

    /**
     * @return the queryInputParentIonMass
     */
    public String getQueryInputParentIonMass() {
        return this.queryInputParentIonMass;
    }

    /**
     * Catches the input text in the formulary on the web of the
     * queryParentIonMass and obtains the list of them in order.
     *
     * @param queryInputParentIonMass
     */
    public void setQueryInputParentIonMass(String queryInputParentIonMass) {
        this.queryInputParentIonMass = queryInputParentIonMass;
    }

    public String getQueryInputFattyAcidMass1() {
        return queryInputFattyAcidMass1;
    }

    public void setQueryInputFattyAcidMass1(String queryInputFattyAcidMass1) {
        this.queryInputFattyAcidMass1 = queryInputFattyAcidMass1;
    }

    public String getQueryInputFattyAcidMass2() {
        return queryInputFattyAcidMass2;
    }

    public void setQueryInputFattyAcidMass2(String queryInputFattyAcidMass2) {
        this.queryInputFattyAcidMass2 = queryInputFattyAcidMass2;
    }

    /**
     * @return the inputToleranceForFA
     */
    public String getInputToleranceForFA() {
        return this.inputToleranceForFA;
    }

    /**
     * @param inputToleranceForFA the inputToleranceForFA to set
     */
    public void setInputToleranceForFA(String inputToleranceForFA) {
        this.inputToleranceForFA = inputToleranceForFA;
    }

    /**
     * @return the inputToleranceForFA
     */
    public String getInputToleranceForPI() {
        return this.inputToleranceForPI;
    }

    /**
     * @param inputToleranceForPI the inputToleranceForFA to set
     */
    public void setInputToleranceForPI(String inputToleranceForPI) {
        this.inputToleranceForPI = inputToleranceForPI;
    }

    /**
     * @return the inputModeToleranceForFA
     */
    public String getInputModeToleranceForFA() {
        return this.inputModeToleranceForFA;
    }

    /**
     * @param inputModeToleranceForFA the inputModeToleranceForFA to set
     */
    public void setInputModeToleranceForFA(String inputModeToleranceForFA) {
        this.inputModeToleranceForFA = inputModeToleranceForFA;
    }

    /**
     * @return the inputModeToleranceForFA
     */
    public String getInputModeToleranceForPI() {
        return this.inputModeToleranceForPI;
    }

    /**
     * @param inputModeToleranceForPI the inputModeToleranceForFA to set
     */
    public void setInputModeToleranceForPI(String inputModeToleranceForPI) {
        this.inputModeToleranceForPI = inputModeToleranceForPI;
    }

    /**
     * @return the queryParentIonMass
     */
    public Double getQueryParentIonMass() {
        return this.queryParentIonMass;
    }

    /**
     * @param queryParentIonMass the masses introduced to set
     */
    public void setQueryParentIonMass(Double queryParentIonMass) {
        this.queryParentIonMass = queryParentIonMass;
    }

    public List<Double> getQueryFattyAcidMasses() {
        return this.queryFattyAcidMasses;
    }

    /**
     * @param queryFattyAcidMasses the Retention Times introduced to se
     */
    public void setQueryFattyAcidMasses(List<Double> queryFattyAcidMasses) {
        this.queryFattyAcidMasses = queryFattyAcidMasses;
    }

    private TheoreticalCompoundsFacade getFacade() {
        return this.ejbFacade;
    }

    /**
     * @return if is there theoreticalFattyAcids
     */
    public boolean isThereInputFattyAcids() {
        //System.out.println("CALLING IS THERE INPUT FATTY ACID MASSES");
        //System.out.println("List of FA: " + this.getQueryFattyAcidMasses());
        if (getQueryFattyAcidMasses() != null) {
            return getQueryFattyAcidMasses().size() > 0;
        }
        return false;
    }
    
    /**
     * @return if is there theoreticalFattyAcids
     */
    public boolean isThereTheoreticalOxidizedPCs() {
        if (this.oxidizedCompoundsList != null) {
            return this.oxidizedCompoundsList.size() > 0;
        }
        return false;
    }

    /**
     * @param pagination the pagination to set
     */
    public void setPagination(MyPaginationHelper pagination) {
        this.pagination = pagination;
    }

    /**
     * @return The ionization mode
     */
    public String getIonMode() {
        // System.out.println("ION MODE RETURNED: " + ionMode);
        return ionMode;
    }
    /**
     * @return the databasesForPISearch to search
     */
    public List<String> getDatabasesForPISearch() {
        return databasesForPISearch;
    }

    /**
     * Set the databasesForPISearch to the object
     *
     * @param databasesForPISearch the databasesForPISearch to search
     */
    public void setDatabasesForPISearch(List<String> databasesForPISearch) {
        this.databasesForPISearch = databasesForPISearch;
        /*
        for(String s : databasesForPISearch)
        {
            System.out.println("\n \n Database: " + s + "\n \n");
        }
         */
    }

    /**
     * @return the queryParentIonMass
     */
    public List<String> getOxidations() {
        return oxidations;
    }

    /**
     * Set the oxidations to the object
     *
     * @param oxidations the oxidations to search
     */
    public void setOxidations(List<String> oxidations) {
        this.oxidations = oxidations;
        /*
        for(String s : oxidations)
        {
            System.out.println("\n \n ADUCTO: " + s + "\n \n");
        }
         */
    }

    public List<SelectItem> getOxidationsCandidates() {
        return this.oxidationsCandidates;
    }

    public void setOxidationsCandidates(List<SelectItem> oxidationsCandidates) {
        this.oxidationsCandidates = oxidationsCandidates;
    }

    public List<SelectItem> getDBcandidates() {
        return this.DBcandidates;
    }

    public void setDBcandidates(List<SelectItem> DBcandidates) {
        this.DBcandidates = DBcandidates;
    }

    public List<OxidizedTheoreticalCompound> getOxidizedCompoundsList() {
        return oxidizedCompoundsList;
    }

    /*
    public void setOxidizedCompoundsList(List<OxidizedTheoreticalCompound> oxidizedCompoundsList) {
        this.oxidizedCompoundsList = oxidizedCompoundsList;
    }
    */
    

    private void processLCOxidationCompounds() {
        /*
            System.out.println(" PARENT ION: " + this.queryInputParentIonMass + " double " + this.queryParentIonMass);
            System.out.println(" m/zs of oxidized lipids: " + this.queryInputFattyAcidMass1 + " list: " + this.queryFattyAcidMasses);
            System.out.println(" ion mode: " + this.ionMode
                    + " tolerance for FA: " + this.inputToleranceForFA + this.inputModeToleranceForFA
                    + " tolerance for PI: " + this.inputToleranceForPI + this.inputModeToleranceForFA);
            System.out.println(" DATABASES TO SEARCH: " + this.databasesForPISearch);
            System.out.println(" OXIDATIONS TO SEARCH: " + this.oxidations);
         */

        this.oxidizedCompoundsList = this.ejbFacade.findLCOxidizedFA(this.queryParentIonMass,
                this.queryFattyAcidMasses,
                this.inputModeToleranceForFA,
                Double.parseDouble(this.inputToleranceForFA),
                this.inputModeToleranceForPI,
                Double.parseDouble(this.inputToleranceForPI),
                this.ionMode,
                this.databasesForPISearch,
                this.oxidations
        );

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

    private void processSCOxidationCompounds() {

        /*
            System.out.println(" PARENT ION: " + this.queryInputParentIonMass + " double " + this.queryParentIonMass);
            System.out.println(" m/zs of oxidized lipids: " + this.queryInputFattyAcidMass1 + " list: " + this.queryFattyAcidMasses);
            System.out.println(" ion mode: " + this.ionMode
                    + " tolerance for FA: " + this.inputToleranceForFA + this.inputModeToleranceForFA
                    + " tolerance for PI: " + this.inputToleranceForPI + this.inputModeToleranceForFA);
            System.out.println(" DATABASES TO SEARCH: " + this.databasesForPISearch);
            System.out.println(" OXIDATIONS TO SEARCH: " + this.oxidations);
         */
        this.oxidizedCompoundsList = this.ejbFacade.findSCOxidizedFA(this.queryParentIonMass,
                this.queryFattyAcidMasses,
                this.inputModeToleranceForFA,
                Double.parseDouble(this.inputToleranceForFA),
                this.inputModeToleranceForPI,
                Double.parseDouble(this.inputToleranceForPI),
                this.ionMode,
                this.databasesForPISearch,
                this.oxidations
        );

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

    private void processOxidationCompoundsForTest() {
        /*
            System.out.println(" PARENT ION: " + this.queryInputParentIonMass + " double " + this.queryParentIonMass);
            System.out.println(" m/zs of oxidized lipids: " + this.queryInputFattyAcidMass1 + " list: " + this.queryFattyAcidMasses);
            System.out.println(" ion mode: " + this.ionMode
                    + " tolerance for FA: " + this.inputToleranceForFA + this.inputModeToleranceForFA
                    + " tolerance for PI: " + this.inputToleranceForPI + this.inputModeToleranceForFA);
            System.out.println(" DATABASES TO SEARCH: " + this.databasesForPISearch);
            System.out.println(" OXIDATIONS TO SEARCH: " + this.oxidations);
         */

        String PATHNAMEFORTESTINGFA = "/home/alberto/PHD/mediator/frontEnd/ceu-mediator-v2-2014-7-9/test/filesForTesting/FA.csv";
        List<Double> FAForCheck = PatternFinder.readDoublesFromFile(PATHNAMEFORTESTINGFA);
        this.oxidizedCompoundsList = this.ejbFacade.findLCOxidizedFA(this.queryParentIonMass,
                FAForCheck,
                this.inputModeToleranceForFA,
                Double.parseDouble(this.inputToleranceForFA),
                this.inputModeToleranceForPI,
                Double.parseDouble(this.inputToleranceForPI),
                this.ionMode,
                this.databasesForPISearch,
                this.oxidations
        );

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
        // int inputTol =-1;
        float inputTol = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputTol = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be a number between 0 and 1000"));
        }
        if (inputTol <= 0) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        } else if (inputTol > 1000) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        }
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateParentIonMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float parentIonMass = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            parentIonMass = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be a number between 0 and 10000"));
        }
        if (parentIonMass <= 0) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be between 0 and 10000"));
        } else if (parentIonMass > 10000) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be between 0 and 10000"));
        }
    }

    /**
     * Validates the the Fatty Acid Mass to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateFattyAcidMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float fattyAcidMass = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            fattyAcidMass = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be a number between 0 and 10000"));
        }
        if (fattyAcidMass <= 0) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be between 0 and 10000"));
        } else if (fattyAcidMass > 10000) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be between 0 and 10000"));
        }
    }


    /**
     * Deprecated. Not used Validates the input Tolerance to be a float between
     * 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    /* Commented because it is not useful his use
    public void validateInputMasses(FacesContext arg0, UIComponent arg1, Object arg2)
         throws ValidatorException {
        String queryInputParentIonMass=(String)arg2;
        List<Float> listInput = new LinkedList<Float>();
        try 
        {
            listInput = Cadena.extraerMasas(queryInputParentIonMass);
        }
        catch(NumberFormatException nfe)
        {
            throw new ValidatorException(new FacesMessage("The Masses should be numbers"));
        }
        if(queryInputParentIonMass.contains("[a-zA-Z]+"))
        {
            throw new ValidatorException(new FacesMessage("The Masses should be numbers AA"));
        }
   }
     */
}
