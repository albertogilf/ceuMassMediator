package controllers;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import msms.MSMSCompound;
import static utilities.Constantes.*;
import msms.Msms;

import msms.Peak;
import facades.MSMSFacade;

/**
 * Controller (Bean) of the application
 *
 * @author Maria Postigo, Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 09/04/2018
 */
@ManagedBean(name = "MSMSController")
@SessionScoped
public class MSMSController implements Serializable {

    private static final long serialVersionUID = 1L;
    private String parentIonMass;
    private String inputPeaks;
    private String parentIonTolerance;
    private String mzTolerance;
    private String ionizationMode;
    private int ionizationVoltage;
    private String ionizationVoltageLevel;
    private String parentIonToleranceMode;
    private String mzToleranceMode;
    // List of candidates is within msms, attribute annotations
    private Msms msms;
    private List<Peak> itemsPeaks;
    private String queryPeaks;

    private List<SelectItem> spectraTypeCandidates;
    private List<String> spectraType;

    // Object to 
    private MSMSFacade msmsFacade;

    // List of compounds for the result
    private List<MSMSCompound> itemsMSCompound;

    public MSMSController() {
        this.mzToleranceMode = TOLERANCE_MODE_INICITAL_VALUE;
        this.parentIonToleranceMode = TOLERANCE_MODE_INICITAL_VALUE;
        this.ionizationMode = "positive";
        this.ionizationVoltage = 10;
        this.ionizationVoltageLevel = "low";
        this.spectraType = new LinkedList<String>();
        this.spectraType.add("experimental");
        this.spectraTypeCandidates = new LinkedList<SelectItem>();
        this.spectraTypeCandidates.add(new SelectItem("experimental", "Experimental"));
        this.spectraTypeCandidates.add(new SelectItem("predicted", "Predicted"));
        this.msmsFacade = new MSMSFacade();
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

    public String getParentIonTolerance() {
        return parentIonTolerance;
    }

    public void setParentIonTolerance(String parentIonTolerance) {
        this.parentIonTolerance = parentIonTolerance;
    }

    public String getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(String mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public String getIonizationMode() {
        return ionizationMode;
    }

    public void setIonizationMode(String ionizationMode) {
        this.ionizationMode = ionizationMode;
    }

    /**
     *
     * @param ionizationMode
     * @return
     */
    public int getIonizationModeAsIint(String ionizationMode) {
        if (ionizationMode.equalsIgnoreCase("positive")) {
            return 1;
        }
        if (ionizationMode.equalsIgnoreCase("negative")) {
            return 0;
        }
        return -1;
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

    public String getParentIonMass() {
        return parentIonMass;
    }

    public void setParentIonMass(String parentIonMass) {
        this.parentIonMass = parentIonMass;
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

    public String getParentIonToleranceMode() {
        return parentIonToleranceMode;
    }

    public void setParentIonToleranceMode(String parentIonToleranceMode) {
        this.parentIonToleranceMode = parentIonToleranceMode;
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
        this.ionizationMode = "positive";
        ionizationVoltage = 0;
        this.ionizationVoltageLevel = "low";
        this.mzTolerance = "";
        this.parentIonMass = "";
        this.parentIonTolerance = "";
        this.queryPeaks = "";
    }

    /**
     * Submit the data from the web interface to the
     */
    public void submit() {
        List<Peak> peaks = msmsFacade.getPeaks(this.queryPeaks);

        System.out.println("Entering submit");
        //load the peaks

        this.itemsPeaks = peaks;
        /*Iterator it = this.itemsPeaks.iterator();
        while(it.hasNext())
        {
            Peak p = (Peak)it.next();
            System.out.println(p.toString());
        }*/
        //load the input msms
        double parentIonMZ = Double.parseDouble(this.parentIonMass);
        this.msms = new Msms(parentIonMZ, this.getIonizationModeAsIint(ionizationMode),
                this.ionizationVoltage,
                this.ionizationVoltageLevel, peaks, this.getSpectraTypeAsInt());
        //System.out.println("PARENT ION MASS "+this.parentIonMass);

        System.out.println("PREDICTED========" + this.msms.getSpectraType());
        System.out.println("MSMS tostring " + this.msms.toString());
        

        //2. Set the candidates
        // First, search the candidates.
        List<MSMSCompound> listCandidates
                = this.msmsFacade.getMsmsCandidates(this.msms, Double.parseDouble(this.parentIonTolerance), this.parentIonToleranceMode);
        System.out.println("Number of msms candidates: " + listCandidates.size());
        //3. Score the peaks
        // Then score the candidates
        List<MSMSCompound> candidatesScored
                = this.msmsFacade.scoreMatchedPeaks(this.msms, listCandidates, Double.parseDouble(this.mzTolerance), this.mzToleranceMode);
        //System.out.println("Candidates scored at msms controller: "+ this.msms.getAnnotations().getCompound().size());

        //4. Get only the top N matches, we will use top 10
        this.itemsMSCompound = msmsFacade.getTopNMatches(candidatesScored, candidatesScored.size());
        this.msms.setCompounds(this.itemsMSCompound);
        System.out.println("Top number: " + this.itemsMSCompound.size());
        /* Iterator it= this.itemsMSCompound.iterator();
        
        while (it.hasNext())
        {
            System.out.println(((MSMSCompound)it).toString());

        }*/
        //this.msmsFacade.disconnect();

    }

    public void setDemo() {
        this.setQueryPeaks(DEMOPEAKS);
        this.setParentIonMass(DEMOPARENTIONMASS);
        this.setParentIonTolerance(DEMOPARENTIONMASSTOLERANCE);
        this.setParentIonToleranceMode("Da");
        this.setMzTolerance(DEMOMZTOLERANCE);
        this.setMzToleranceMode("Da");
        this.setIonizationMode("positive");
        this.setIonizationVoltage(10);
        this.setIonizationVoltageLevel("low");
        
    }

    public void validateParentIonMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputParent =-1;
        float inputParent = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputParent = Float.parseFloat((String) input);
            //  inputParent = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The parent ion mass should be a number between 0 and 2000"));
        }
        if (inputParent <= 0) {
            throw new ValidatorException(new FacesMessage("The parent ion mass should be between 0 and 2000"));
        } else if (inputParent > 2000) {
            throw new ValidatorException(new FacesMessage("The parent ion mass should be between 0 and 2000"));
        }
    }

    public void validateTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float inputTol = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputTol = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Input tolerance should be a number between 0 and 10000"));
        }
        if (inputTol <= 0) {
            throw new ValidatorException(new FacesMessage("Input tolerance should be between 0 and 10000"));
        } else if (inputTol > 10000) {
            throw new ValidatorException(new FacesMessage("Input tolerance should be between 0 and 10000"));
        }
    }

}
