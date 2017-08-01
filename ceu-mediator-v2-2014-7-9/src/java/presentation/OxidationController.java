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
import utilities.ConstantesForOxidation;
import utilities.OxidationLists;

/**
 * Controller (Bean) of the application
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@ManagedBean(name = "OxidationController")
@SessionScoped
public class OxidationController implements Serializable, Controller {

    private static final long serialVersionUID = 1L;

    private MyPaginationHelper pagination;
    private String queryInputParentIonMasses;
    private String queryInputFattyAcidMasses;

    private String inputTolerance;
    private String ionMode;
    private int flag;

    // Declared as a variable because JSF needs that even Framework marks as not used.
    private int numOxidations;

    private List<Double> queryParentIonMasses;
    private List<Double> queryFattyAcidMasses;

    private List<String> oxidations;
    private List<SelectItem> oxidationsCandidates;

    private List<String> databases;
    private List<SelectItem> DBcandidates;

    // NOT USED
    // private boolean thereAreTheoreticalCompounds;
    private List<TheoreticalCompoundsGroup> itemsGrouped;
    private List<TheoreticalCompounds> items;

    @EJB
    private presentation.TheoreticalCompoundsFacade ejbFacade;

    public OxidationController() {
        this.inputTolerance = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        //String version = FacesContext.class.getPackage().getImplementationVersion();
        //System.out.println("\n\n  VERSION DE JSF: " + version + "\n\n");
        this.items = null;
        this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        this.DBcandidates = new LinkedList<SelectItem>();
        this.DBcandidates.add(new SelectItem("All", "All"));
        for (String db : OxidationLists.LISTDB) {
            this.DBcandidates.add(new SelectItem(db, db));
        }
        this.databases = new LinkedList<String>();
        this.databases.add("All");



        this.oxidationsCandidates = new LinkedList<SelectItem>();
        this.oxidationsCandidates.add(new SelectItem("all", "All"));
        for (Map.Entry e : (OxidationLists.MAPOXIDATIONS).entrySet()) {
            this.oxidationsCandidates.add(new SelectItem((String) e.getKey(), (String) e.getKey()));
        }
        this.ionMode = "negative";
        this.oxidations = new LinkedList<String>();
        this.oxidations.add("all");
        this.queryInputParentIonMasses = "";
        this.queryInputFattyAcidMasses = "";
    }

    /**
     * This method is used to load a list of queryParentIonMasses declared in the class
 Constantes
     */
    public void setDemoMass() {
        this.setQueryInputParentIonMasses(ConstantesForOxidation.PARENTIONDEMOMASS);
        this.setQueryInputFattyAcidMasses(ConstantesForOxidation.FATTYACIDDEMOMASS);
        //System.out.println(demoMasses);
    }

    /**
     * This method is used to load a list of queryParentIonMasses declared in the class
 Constantes
     */
    public void setDemoMasses() {
        // TODO
        //this.setQueryInputParentIonMasses(NEWDEMOMASSES);
        //System.out.println(demoMasses);

    }

    /**
     * This method is used to load a list of queryParentIonMasses declared in the class
 Constantes
     */
    public void setAdvancedDemoMasses() {
        // TODO
        //this.setQueryInputParentIonMasses(NEWDEMOMASSES);
        //this.setQueryInputFattyAcidMasses(NEWDEMORETENTIONTIME);
        //System.out.println(demoMasses);
        // TODO Set All Masses,RTs and Composites

    }

    /**
     * Method to clean the input formulary where the queryParentIonMasses are written
     */
    public void clearForm() {
        this.queryInputParentIonMasses = "";
        this.queryInputFattyAcidMasses = "";
        this.inputTolerance = ConstantesForOxidation.TOLERANCE_INICITAL_VALUE;
        /*
        this.ionMode = "neutral";
        this.oxidations.clear();
        this.oxidations.add("all");
         */
        setItems(null);
        this.itemsGrouped.clear();
    }

    /**
     * TODO
     * Method that permits to create a excel from the current results.
     */
    /*
    public void exportToExcel() {
        // All pages display the number of input compounds defined in ITEMS_PER_PAGE_IN_EXCEL
        // Only export to Excel no significative compounds
        List<TheoreticalCompounds> itemsWithoutSignificative = new LinkedList<TheoreticalCompounds>();

        for (TheoreticalCompounds tc : this.items) {
            if (tc.isSignificativeCompound()) {
                itemsWithoutSignificative.add(tc);
            }
        }
        if (pagination == null) {

            pagination = new MyPaginationHelper(ITEMS_PER_PAGE_IN_EXCEL,
                    itemsWithoutSignificative.size());
        }
        CompoundExcelExporter compoundExcelExporter = new CompoundExcelExporter();
        compoundExcelExporter.generateWholeExcelCompound(itemsWithoutSignificative, pagination, flag);
    }
*/
    
    /**
     * TODO
     * Deprecated. Not used
     * <p>
     * Gets the object MyPaginationHelper pagination. </p>
     * In case that the PaginatorHelper is null, this getter provides a new
     * instance.
     */
    @Override
    public PaginationHelper getPagination() {
        /*
        if (pagination == null) {
            // pagination = new MyPaginationHelper(ITEMS_PER_PAGE, ejbFacade, queryParentIonMasses,queryFattyAcidMasses, inputTolerance);
            pagination = new MyPaginationHelper(ConstantesForOxidation.ITEMS_PER_PAGE_IN_EXCEL,
                    items.size());
        }
        return pagination;
        */
        return null;
    }

    
    
    /**
     * TODO
     */
    public void submit() {
        System.out.println("Working on it..");
    }

    /**
     * Method to reset all the items that are instantiated in the class Reset
     * all the compounds obtained by the mediator.
     */
    public void submitOxidationCompounds() {
        this.items = null;
        this.itemsGrouped.clear();
        int numFattyAcidInputMasses;
        
        List<Double> fattyAcidMassesAux; // auxiliar List for input Masses
        //Method returns an ArrayList because it is acceded by index
        fattyAcidMassesAux = Cadena.extractDoubles(this.queryInputFattyAcidMasses);
        // System.out.println("INPUT: " + queryInputParentIonMasses + " \n ARRAY: " + massesAux);
        this.setQueryFattyAcidMasses(fattyAcidMassesAux);
        numFattyAcidInputMasses = fattyAcidMassesAux.size();
        List<Double> parentIonMassesAux; // auxiliar List for input Masses
        //Method returns an ArrayList because it is acceded by index
        parentIonMassesAux = getListOfDoubles(this.queryInputParentIonMasses, numFattyAcidInputMasses);
        // System.out.println("INPUT: " + queryInputParentIonMasses + " \n ARRAY: " + massesAux);
        this.setQueryParentIonMasses(parentIonMassesAux);
        

        // Change for ALL
        pagination = new MyPaginationHelper(ConstantesForOxidation.ITEMS_PER_PAGE_IN_EXCEL,
                this.queryParentIonMasses.size());
        processOxidationCompounds();
    }

    public void resetItems() {
        this.items = null;
        this.itemsGrouped.clear();
    }

    /**
     * @return the queryInputParentIonMasses
     */
    public String getQueryInputParentIonMasses() {
        return this.queryInputParentIonMasses;
    }

    /**
     * Catches the input text in the formulary on the web of the queryParentIonMasses and
 obtains the list of them in order.
     *
     * @param queryInputParentIonMasses
     */
    public void setQueryInputParentIonMasses(String queryInputParentIonMasses) {
        this.queryInputParentIonMasses = queryInputParentIonMasses;
    }

    public String getQueryInputFattyAcidMasses() {
        return queryInputFattyAcidMasses;
    }

    public void setQueryInputFattyAcidMasses(String queryInputFattyAcidMasses) {
        this.queryInputFattyAcidMasses = queryInputFattyAcidMasses;
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
     * @return the queryParentIonMasses
     */
    public List<Double> getQueryParentIonMasses() {
        return this.queryParentIonMasses;
    }

    /**
     * @param queryParentIonMasses the masses introduced to set
     */
    public void setQueryParentIonMasses(List<Double> queryParentIonMasses) {
        this.queryParentIonMasses = queryParentIonMasses;
    }

    public List<Double> getQueryFattyAcidMasses() {
        return queryFattyAcidMasses;
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
     * Getter of Items. If Items is null, the method is going to create a new
     * instance
     *
     * @return 
     */
    @Override
    public List<TheoreticalCompounds> getItems() {
        System.out.println("\nGET ITEMS CALLED -> \n");
        return this.items;
    }
    

    /** TODO
     * Getter of getItemsGroupedWithoutSignificative. If ItemsGrouped is null,
     * the method is going to create a new instance
     *
     * @return 
     */
        @Override
    public List<TheoreticalCompoundsGroup> getItemsGroupedWithoutSignificative() {
        return null;
    }

    
    /**
     * Getter of ItemsGrouped. If ItemsGrouped is null, the method is going to
     * create a new instance
     *
     * @return 
     */
    @Override
    public List<TheoreticalCompoundsGroup> getItemsGrouped() {
        return itemsGrouped;
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
     * @return the thereAreTheoreticalCompounds
     */
    public boolean isThereTheoreticalCompounds() {
        //return getQueryMasses().size() > 0;
        return false;
    }

    /**
     * @return the isThereInputMasses
     */
    public boolean isThereInputMasses() {
        //if (getQueryMasses() != null) {
        //    return getQueryMasses().size() > 0;
        //}
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
     * Set the ionization mode
     *
     * @param ionMode
     */
    public void setIonMode(String ionMode) {
        this.ionMode = ionMode;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setFlag(String flag) {
        this.flag = Integer.parseInt(flag);
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
        /*
        for(String s : databases)
        {
            System.out.println("\n \n Database: " + s + "\n \n");
        }
         */
    }

    /**
     * @return the queryParentIonMasses
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

    /**
     *
     * @return the number of oxidations
     */
    public int getNumOxidations() {
        if(oxidations.contains("all"))
        {
            return OxidationLists.MAPOXIDATIONS.size();
        }
        // By default, paginate with number of oxidations
        return oxidations.size();
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

    private List<Double> getListOfDoubles(String input, int numInputMasses) {
        List<Double> retAux;
        if (!input.equals("")) {
            retAux = Cadena.extractDoubles(input);
            // If there is no time for all queryParentIonMasses, fill with 0
            for (int i = retAux.size(); i < numInputMasses; i++) {
                retAux.add(0d);
            }
        } else {
            retAux = new ArrayList<Double>();
            for (int i = 0; i < numInputMasses; i++) {
                retAux.add(0d);
            }
        }
        return retAux;
    }


    // TODO
    private void processCompounds() {
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            // TODO METHOD TO SEARCH
            /*
            this.items = this.ejbFacade.findCompoundsAdvanced(new int[]{0, queryParentIonMasses.size() - 1},
                    this.queryParentIonMasses,
                    this.queryFattyAcidMasses,
                    this.queryCompositeSpectrum,
                    this.isSignificativeCompound,
                    Double.parseDouble(this.inputTolerance),
                    this.chemAlphabet,
                    this.ionMode,
                    this.massesMode,
                    this.oxidations,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
*/
        }
    }


    // TODO
    private void processOxidationCompounds() {
        if (this.itemsGrouped == null) {
            this.itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        }
        if (this.items == null || this.itemsGrouped.isEmpty()) {
            // TODO METHOD TO SEARCH COMPOUNDS
            /*
            this.items = this.ejbFacade.findCompoundsSimple(new int[]{0, queryParentIonMasses.size() - 1},
                    this.queryParentIonMasses,
                    Double.parseDouble(this.inputTolerance),
                    this.ionMode,
                    this.massesMode,
                    this.oxidations,
                    this.itemsGrouped,
                    this.databases,
                    this.metabolitesType
            );
*/

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
     * Deprecated
     *
     * @return
     */
    @Override
    public String next() {
        getPagination().nextPage();
        resetItems();
        return "List";
    }

    /**
     * Deprecated
     *
     * @return
     */
    @Override
    public String previous() {
        getPagination().previousPage();
        resetItems();
        return "List";
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
        String queryInputParentIonMasses=(String)arg2;
        List<Float> listInput = new LinkedList<Float>();
        try 
        {
            listInput = Cadena.extraerMasas(queryInputParentIonMasses);
        }
        catch(NumberFormatException nfe)
        {
            throw new ValidatorException(new FacesMessage("The Masses should be numbers"));
        }
        if(queryInputParentIonMasses.contains("[a-zA-Z]+"))
        {
            throw new ValidatorException(new FacesMessage("The Masses should be numbers AA"));
        }
   }
     */
}
