/*
 * BrowseSearchController.java
 *
 * Created on 24-abr-2018, 20:44:48
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package presentation;

import controllers.InterfaceValidators;
import exporters.CompoundExcelExporter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import persistence.theoreticalCompound.TheoreticalCompounds;
import static utilities.Constants.DEMOFORMULA;
import static utilities.Constants.DEMONAME;
import utilities.DataFromInterfacesUtilities;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;

/**
 * Bean to perform a browse search in CMM TODO
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 24-abr-2018
 *
 * @author Alberto Gil de la Fuente
 */
@ManagedBean(name = "browseSearchController")
@SessionScoped
public class BrowseSearchController implements Serializable {

    private static final long serialVersionUID = 1L;

    //atributtes for browse search (name and formula search)
    private String queryName;
    private String queryFormula;
    private boolean exactName;

    // Atributes for filter
    private List<String> databases;
    private final List<SelectItem> DBcandidates;
    private String metabolitesType;
    private final List<SelectItem> metabolitesTypecandidates;

    private List<TheoreticalCompounds> items;
    @EJB
    private facades.TheoreticalCompoundsFacade ejbFacade;

    /**
     * Creates a new instance of BrowseSearchController
     */
    public BrowseSearchController() {
        this.items = null;

        this.DBcandidates = new LinkedList<>();
        this.DBcandidates.add(new SelectItem("AllWM", "All except MINE"));
        this.DBcandidates.add(new SelectItem("All", "All (Including In Silico Compounds)"));
        for (Map.Entry e : MAPDATABASES.entrySet()) {
            this.DBcandidates.add(new SelectItem(e.getKey(), (String) e.getKey()));
        }
        this.databases = new LinkedList<>();
        this.databases.add("AllWM");

        this.metabolitesTypecandidates = new LinkedList<SelectItem>();
        for (Map.Entry e : DataFromInterfacesUtilities.METABOLITESTYPES.entrySet()) {
            this.metabolitesTypecandidates.add(new SelectItem(e.getKey(), (String) e.getKey()));
        }
        this.metabolitesType = "All except peptides";
    }

    /**
     *
     * @return
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     *
     * @param queryName
     */
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     *
     * @return
     */
    public String getQueryFormula() {
        return queryFormula;
    }

    public boolean isExactName() {
        return exactName;
    }

    public void setExactName(boolean exactName) {
        this.exactName = exactName;
    }

    /**
     *
     * @param queryFormula
     */
    public void setQueryFormula(String queryFormula) {
        this.queryFormula = queryFormula;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public String getMetabolitesType() {
        return metabolitesType;
    }

    public void setMetabolitesType(String metabolitesType) {
        this.metabolitesType = metabolitesType;
    }

    public List<SelectItem> getDBcandidates() {
        return DBcandidates;
    }

    public List<SelectItem> getMetabolitesTypecandidates() {
        return metabolitesTypecandidates;
    }

    /**
     * This method is used to load the demo data for the browse search
     *
     */
    public void setDemoNameFormula() {
        this.setQueryName(DEMONAME);
        this.setQueryFormula(DEMOFORMULA);
    }

    /**
     * Clears the form for Browse Search
     */
    public void clearForm() {
        // clear form for browse search
        this.queryName = "";
        this.queryFormula = "";
        exactName = false;
        resetItems();
    }

    private void resetItems() {
        this.items = null;
    }

    /**
     * Method that permits to create a excel from the current results.
     */
    public void exportToExcel() {
        if (this.items != null && !this.items.isEmpty()) {
            int flag = 0;
            // Compounds come from a browse search so they does not contain information related to LCMS. 

            CompoundExcelExporter compoundExcelExporter = new CompoundExcelExporter(flag);
            compoundExcelExporter.generateWholeExcelCompound(items, 2);
        }
    }

    /**
     * Method to submit a Browse Search
     */
    public void submitBrowseSearch() {
        this.items = null;
        processBrowseSearch();
        //this.itemsGroupedWithoutSignificative = this.itemsGrouped;

    }

    private void processBrowseSearch() {
        this.items = this.ejbFacade.findCompoundsBrowseSearch(
                this.queryName,
                this.queryFormula,
                this.exactName,
                this.databases,
                this.metabolitesType
        );
    }

    public boolean isThereQueryParameters() {
        return this.queryFormula.length() > 3 || this.queryName.length() > 3;
    }

    /**
     * @return the thereAreTheoreticalCompounds
     */
    public boolean isThereTheoreticalCompounds() {
        return items.size() > 0;
    }

    public List<TheoreticalCompounds> getItems() {
        return items;
    }

    /**
     * Validates the retention Time to be a float between 0 and 1000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateNameAndFormula(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateNameAndFormula(arg0, arg1, arg2);
    }

}
