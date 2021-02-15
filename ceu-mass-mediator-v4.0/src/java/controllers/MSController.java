/*
 * MSController.java
 *
 * Created on 11-nov-2019, 23:49:38
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package controllers;

import facades.MSFacade;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import utilities.AdductsLists;
import static utilities.AdductsLists.DEFAULT_ADDUCTS_POSITIVE;
import utilities.Constants;
import utilities.DataFromInterfacesUtilities;
import static utilities.Constants.TOLERANCE_DEFAULT_VALUE;
import static utilities.Constants.TOLERANCE_MODE_DEFAULT_VALUE;

/**
 * MSController handling the common filters for all searches: mz masses,
 * tolerance for mz masses, Metabolites, ChemAlphabet, Masses Mode, Ionization
 * Mode Adducts
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1.0.0 11-nov-2019
 *
 * @author Alberto Gil de la Fuente
 */
public abstract class MSController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String queryInputMasses;
    private List<Double> inputmzs;

    private String inputmzTolerance;
    private String inputmzModeTolerance;
    private Integer mzTolerance;

    private final List<SelectItem> chemAlphabetCandidates;
    protected String chemAlphabet;
    protected Boolean includeDeuterium;

    private Integer massesMode;
    private Integer ionMode;
    private List<SelectItem> ionizationModeCandidates;
    private List<String> adducts;
    private List<SelectItem> adductsCandidates;
    // to Improve efficiency. They are assigned to adductsCandidates
    private final List<SelectItem> positiveCandidates;
    private final List<SelectItem> negativeCandidates;
    private final List<SelectItem> neutralCandidates;

    // Declared as a variable because JSF needs that even Framework marks as not used.
    private int numAdducts;

    protected final facades.MSFacade msFacade;

    /**
     * Creates a new instance of Controller
     */
    public MSController() {
        this.msFacade = new MSFacade();
        this.inputmzTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputmzModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;

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

        this.chemAlphabetCandidates = new LinkedList<>();
        DataFromInterfacesUtilities.CHEMALPHABETLIST.forEach((chemAlph) -> {
            this.chemAlphabetCandidates.add(chemAlph);
        });

        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
    }

    public void clearForm() {
        this.queryInputMasses = "";
        this.inputmzTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputmzModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
    }

    public void setDemoParameters() {

        this.inputmzTolerance = TOLERANCE_DEFAULT_VALUE;
        this.inputmzModeTolerance = TOLERANCE_MODE_DEFAULT_VALUE;
        this.ionMode = 1;
        this.massesMode = 1;
        this.adductsCandidates = positiveCandidates;
        this.adducts.clear();
        this.adducts.addAll(DEFAULT_ADDUCTS_POSITIVE);
    }

    public MSFacade getMsFacade() {
        return msFacade;
    }

    public String getQueryInputMasses() {
        return queryInputMasses;
    }

    public void setQueryInputMasses(String queryInputMasses) {
        this.queryInputMasses = queryInputMasses;
    }

    public List<Double> getInputmzs() {
        return inputmzs;
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

    public void setInputmzs(List<Double> inputmzs) {
        this.inputmzs = inputmzs;
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

    public Integer getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Integer mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public Integer getIonMode() {
        return ionMode;
    }

    /**
     * Set the ionization mode
     *
     * @param ionMode
     */
    public void setIonMode(Integer ionMode) {
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

    public List<SelectItem> getPositiveCandidates() {
        return positiveCandidates;
    }

    public List<SelectItem> getNegativeCandidates() {
        return negativeCandidates;
    }

    public List<SelectItem> getNeutralCandidates() {
        return neutralCandidates;
    }

    public Integer getMassesMode() {
        return this.massesMode;
    }

    public void setMassesMode(Integer massesMode) {
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

    public String getChemAlphabet() {
        return chemAlphabet;
    }

    public void setChemAlphabet(String chemAlphabet) {
        this.chemAlphabet = chemAlphabet;
    }

    public List<SelectItem> getChemAlphabetCandidates() {
        return chemAlphabetCandidates;
    }

    public Boolean getIncludeDeuterium() {
        return this.includeDeuterium;
    }

    public void setIncludeDeuterium(Boolean includeDeuterium) {
        this.includeDeuterium = includeDeuterium;
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

    public String getChebiWebPage() {
        return Constants.WEB_CHEBI;
    }

    public String getMINEWebPage() {
        return Constants.WEB_MINE;
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

}
