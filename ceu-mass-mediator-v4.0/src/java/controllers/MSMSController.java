package controllers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import msms.MSMSCompound;
import static utilities.Constants.*;
import msms.Msms;
import msms.ScoreComparator;
import msms.Peak;
import facades.MSMSFacade;
import java.util.Collections;
import javax.annotation.PreDestroy;

/**
 * Controller (Bean) of the application
 * 
 *
 * @author Maria Postigo, Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 09/04/2018
 */
@ManagedBean(name = "MSMSController")
@SessionScoped
public class MSMSController implements Serializable {
// TODO ViewScoped until I check how to destroy sessions.
    private static final long serialVersionUID = 1L;
    private String precursorIonMass;
    private String inputPeaks;
    private String precursorIonTolerance;
    private String mzTolerance;
    private int ionizationMode;
    private int ionizationVoltage;
    private String ionizationVoltageLevel;
    private String precursorIonToleranceMode;
    private String mzToleranceMode;
    // List of candidates is within msms, attribute annotations
    private Msms msms;
    private List<Peak> itemsPeaks;
    private String queryPeaks;

    private List<SelectItem> spectraTypeCandidates;
    private List<String> spectraType;

    // Object to 
    private final MSMSFacade msmsFacade;

    // List of compounds for the result
    private List<MSMSCompound> itemsMSCompound;

    public MSMSController() {
        this.mzToleranceMode = TOLERANCE_MODE_DEFAULT_VALUE;
        this.precursorIonToleranceMode = TOLERANCE_MODE_DEFAULT_VALUE;
        this.ionizationMode = 1;
        this.ionizationVoltage = 10;
        this.ionizationVoltageLevel = "low";
        this.spectraType = new LinkedList<>();
        this.spectraType.add("experimental");
        this.spectraTypeCandidates = new LinkedList<>();
        this.spectraTypeCandidates.add(new SelectItem("experimental", "Experimental"));
        this.spectraTypeCandidates.add(new SelectItem("predicted", "Predicted"));
        this.msmsFacade = new MSMSFacade();
        //System.out.println("CONNECTING MSMS");
    }

    public int getSpectraTypeAsInt() {

        if (this.spectraType.contains("experimental") && this.spectraType.contains("predicted")) {
            return 2;

        } else if (this.spectraType.contains("predicted")) {
            return 1;
        } else if (this.spectraType.contains("experimental")) {
            return 0;
        }
        return -1;
    }

    public List<SelectItem> getSpectraTypeCandidates() {
        return spectraTypeCandidates;
    }

    public void setSpectraTypeCandidates(List<SelectItem> predictedCandidates) {
        this.spectraTypeCandidates = predictedCandidates;
    }

    public List<String> getSpectraType() {
        return spectraType;
    }

    public void setSpectraType(List<String> predicted) {
        this.spectraType = predicted;
    }

    public Msms getMsms() {
        return msms;
    }

    public void setMsms(Msms msms) {
        this.msms = msms;
    }

    public String getQueryPeaks() {
        return queryPeaks;
    }

    public void setQueryPeaks(String queryPeaks) {
        this.queryPeaks = queryPeaks;
    }

    public boolean isThereInputPeaks() {
        if (getInputPeaks() != null) {
            return this.itemsPeaks.size() > 0;
        }
        return false;

    }

    public String getInputPeaks() {
        return inputPeaks;
    }

    public void setInputPeaks(String inputPeaks) {
        this.inputPeaks = inputPeaks;
    }

    public String getPrecursorIonTolerance() {
        return precursorIonTolerance;
    }

    public void setPrecursorIonTolerance(String precursorIonTolerance) {
        this.precursorIonTolerance = precursorIonTolerance;
    }

    public String getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(String mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public int getIonizationMode() {
        return ionizationMode;
    }

    public void setIonizationMode(int ionizationMode) {
        this.ionizationMode = ionizationMode;
    }

    public int getIonizationVoltage() {
        return ionizationVoltage;
    }

    public void setIonizationVoltage(int ionizationVoltage) {
        this.ionizationVoltage = ionizationVoltage;
    }

    public void setIonizationVoltage(String ionizationVoltageLevel) {
        switch (ionizationVoltageLevel) {
            case "low":
                setIonizationVoltage(10);
                break;
            case "med":
                setIonizationVoltage(20);
                break;
            case "high":
                setIonizationVoltage(40);
                break;
            case "all":
                setIonizationVoltage(0);
                break;
            default:

        }

    }

    public String getIonizationVoltageLevel() {
        return ionizationVoltageLevel;
    }

    public void setIonizationVoltageLevel(String ionizationVoltageLevel) {
        setIonizationVoltage(ionizationVoltageLevel);
        this.ionizationVoltageLevel = ionizationVoltageLevel;

    }

    public String getPrecursorIonMass() {
        return precursorIonMass;
    }

    public void setPrecursorIonMass(String precursorIonMass) {
        this.precursorIonMass = precursorIonMass;
    }

    public void setItemsPeaks(List<Peak> itemsPeaks) {
        this.itemsPeaks = itemsPeaks;
    }

    public void setItemsMSCompound(List<MSMSCompound> itemsMSCompound) {
        this.itemsMSCompound = itemsMSCompound;
    }

    public List getItemsMSCompound() {
        return this.itemsMSCompound;
    }

    public boolean isThereCandidates() {
        return getItemsMSCompound().size() >= 0;
    }

    public String getPrecursorIonToleranceMode() {
        return precursorIonToleranceMode;
    }

    public void setPrecursorIonToleranceMode(String precursorIonToleranceMode) {
        this.precursorIonToleranceMode = precursorIonToleranceMode;
    }

    public String getMzToleranceMode() {
        return mzToleranceMode;
    }

    public void setMzToleranceMode(String mzToleranceMode) {
        this.mzToleranceMode = mzToleranceMode;
    }

    /**
     * This method clears the form of the MS/MS web interface
     */
    public void clearForm() {
        this.inputPeaks = null;
        this.itemsMSCompound = null;
        this.itemsPeaks = null;
        this.ionizationMode = 1;
        ionizationVoltage = 0;
        this.ionizationVoltageLevel = "low";
        this.mzTolerance = "";
        this.precursorIonMass = "";
        this.precursorIonTolerance = "";
        this.queryPeaks = "";
    }

    /**
     * Submit the data from the web interface to the
     */
    public void submit() {
        List<Peak> peaks = msmsFacade.getPeaks(this.queryPeaks);

        // System.out.println("Entering submit");
        //load the peaks
        this.itemsPeaks = peaks;

        //load the input msms
        double precursorIonMZ = Double.parseDouble(this.precursorIonMass);
        this.msms = new Msms(precursorIonMZ, this.ionizationMode,
                this.ionizationVoltage,
                this.ionizationVoltageLevel, peaks, this.getSpectraTypeAsInt());

        //2. Set the candidates
        // First, search the candidates.
        List<MSMSCompound> listCandidates
                = this.msmsFacade.getMsmsCandidates(this.msms, Double.parseDouble(this.precursorIonTolerance), this.precursorIonToleranceMode);
        //System.out.println("Number of msms candidates: " + listCandidates.size());
        //3. Score the peaks
        // Then score the candidates
        List<MSMSCompound> candidatesScored
                = this.msmsFacade.scoreMatchedPeaks(this.msms, listCandidates, Double.parseDouble(this.mzTolerance), this.mzToleranceMode);

        //4. Get only the top N matches, we will use top 10
        this.itemsMSCompound = msmsFacade.getTopNMatches(candidatesScored, candidatesScored.size());
        this.msms.setCompounds(this.itemsMSCompound);
        Collections.sort(this.itemsMSCompound, new ScoreComparator());
        System.out.println("Top number: " + this.itemsMSCompound.size());

    }

    public void setDemo() {
        this.setQueryPeaks(DEMOPEAKS);
        this.setPrecursorIonMass(DEMOPRECUSRORIONMASS);
        this.setPrecursorIonTolerance(DEMOPRECURSORIONMASSTOLERANCE);
        this.setPrecursorIonToleranceMode("Da");
        this.setMzTolerance(DEMOMZTOLERANCE);
        this.setMzToleranceMode("Da");
        this.setIonizationMode(1);
        this.setIonizationVoltage(10);
        this.setIonizationVoltageLevel("low");
    }

    public void validatePrecursorIonMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validatePrecursorIonMass(arg0, arg1, arg2);
    }

    public void validatePeaks(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {

        InterfaceValidators.validatePeaks(arg0, arg1, arg2);
    }

    public void validateInputMZTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputTolerance(arg0, arg1, arg2);
    }

    @PreDestroy
    public void destroy() {
        //System.out.println("DISCONNECTING MSMS");
        this.msmsFacade.disconnect();
    }
}
