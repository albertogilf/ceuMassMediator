package presentation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import static utilities.Constantes.TOLERANCE_MODE_INICITAL_VALUE;
import utilities.ConstantesForOxidation;
import static utilities.ConstantsForQA.*;
import utilities.OxidationLists;

/**
 * Controller (Bean) of the application for the oxidation feature
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 29/11/2017
 */
@ManagedBean(name = "qualityAssuranceController")
@SessionScoped
public class QualityAssuranceController implements Serializable {

    private static final long serialVersionUID = 1L;

    // 5 for < 10E4
    // 4 for 10E4-10E5
    // 3 for 10E5-10E6
    // 2 for 10E6-10E7
    // 1 for 10E7-10E8
    // 0 for > 10E8
    private Integer avgMSIntensity;

    private List<SelectItem> overallMSMSIntensityCandidates;
    // 2 for best
    // 1 for middle range
    // 0 for worse
    private Integer overallMSMSIntensity;

    // 2 for < 5 %
    // 1 for 5-20%
    // 0 for > 20 %
    private Integer noisePercentage;

    // 2 for > 5 scans
    // 1 for 3-5 scans
    // 0 for < 3 scans
    // 2 for > 2 samples
    private Integer numScans;

    // Not used with buttons!!
    private Integer numSamples;

    // 2 for no co-ellution
    // 1 for co-ellution with known compound.
    // 0 for co-ellution with unknown. 
    private Integer coElution;
    // 2 for no cross-talk
    // 1 for small cross-talk
    // 0 for big cross-talk
    private Integer crossTalk;

    private Double finalScore;
    private String stringFinalScore;
    private String tagFinalScore;
    private String colorFinalScore;

    // PROPERTY: overallMSMSIntensityInput
    private UIInput avgMSIntensityInput;
    private UIInput overallMSMSIntensityInput;

    public QualityAssuranceController() {
        this.avgMSIntensity = 2;
        this.overallMSMSIntensityCandidates = LISTMSMSINT_10E6;
        this.overallMSMSIntensity = 2;
        this.noisePercentage = null;
        this.numScans = null;
        this.numSamples = null;
        this.coElution = null;
        this.crossTalk = null;
        this.finalScore = null;
        this.stringFinalScore = "";
        this.tagFinalScore = "";
        this.colorFinalScore = "";
    }

    public Integer getAvgMSIntensity() {
        return this.avgMSIntensity;
    }

    public void setAvgMSIntensity(Integer avgMSIntensity) {
        switch (avgMSIntensity) {
            case 5:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E3;
                break;
            // If there is not any of these 3 (It should not occur) The assigned mode is neutral
            case 4:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E4;
                break;
            case 3:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E5;
                break;
            case 2:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E6;
                break;
            case 1:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E7;
                break;
            case 0:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E7;
                break;
            default:
                this.overallMSMSIntensityCandidates = LISTMSMSINT_10E6;
                this.avgMSIntensity = 2;
                return;
        }
        this.avgMSIntensity = avgMSIntensity;
    }

    private boolean isValidAvgMSIntensity() {
        return this.avgMSIntensity != null && this.avgMSIntensity <= 5 && this.avgMSIntensity >= 0;
    }

    public List<SelectItem> getOverallMSMSIntensityCandidates() {
        return overallMSMSIntensityCandidates;
    }

    public void setOverallMSMSIntensityCandidates(List<SelectItem> overallMSMSIntensityCandidates) {
        this.overallMSMSIntensityCandidates = overallMSMSIntensityCandidates;
    }

    public Integer getOverallMSMSIntensity() {
        return this.overallMSMSIntensity;
    }

    public void setOverallMSMSIntensity(Integer overallMSMSIntensity) {
        this.overallMSMSIntensity = overallMSMSIntensity;
    }

    private boolean isValidOverallMSMSIntensity() {
        return this.overallMSMSIntensity != null && this.overallMSMSIntensity <= 2 && this.overallMSMSIntensity >= 0;
    }

    public Integer getNoisePercentage() {
        return this.noisePercentage;
    }

    public void setNoisePercentage(Integer noisePercentage) {
        this.noisePercentage = noisePercentage;
    }

    private boolean isValidNoisePercentage() {
        return this.noisePercentage != null && this.noisePercentage <= 2 && this.noisePercentage >= 0;
    }

    public Integer getNumScans() {
        return this.numScans;
    }

    public void setNumScans(Integer numScans) {
        this.numScans = numScans;
    }

    private boolean isValidNumScans() {
        return this.numScans != null && this.numScans <= 2 && this.numScans >= 0;
    }

    public Integer getNumSamples() {
        return this.numSamples;
    }

    public void setNumSamples(Integer numSamples) {
        this.numSamples = numSamples;
    }

    public Integer getCoElution() {
        return this.coElution;
    }

    public void setCoElution(Integer coElution) {
        this.coElution = coElution;
    }

    private boolean isValidCoElution() {
        return this.coElution != null && this.coElution <= 2 && this.coElution >= 0;
    }

    public Integer getCrossTalk() {
        return this.crossTalk;
    }

    public void setCrossTalk(Integer crossTalk) {
        this.crossTalk = crossTalk;
    }

    private boolean isValidCrossTalk() {
        return this.crossTalk != null && this.crossTalk <= 2 && this.crossTalk >= 0;
    }

    public Double getFinalScore() {
        return this.finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public String getTagFinalScore() {
        return tagFinalScore;
    }

    public void setTagFinalScore(String tagFinalScore) {
        this.tagFinalScore = tagFinalScore;
    }

    public String getColorFinalScore() {
        return colorFinalScore;
    }

    public void setColorFinalScore(String colorFinalScore) {
        this.colorFinalScore = colorFinalScore;
    }

    public String getStringFinalScore() {
        if (this.finalScore == null) {
            return "";
        } else {
            this.stringFinalScore = this.finalScore.toString();
        }
        return stringFinalScore;
    }

    public void setStringFinalScore(String stringFinalScore) {
        this.stringFinalScore = stringFinalScore;
    }

    public UIInput getOverallMSMSIntensityInput() {
        return overallMSMSIntensityInput;
    }

    public void setOverallMSMSIntensityInput(UIInput newValue) {
        overallMSMSIntensityInput = newValue;
    }

    public UIInput getAvgMSIntensityInput() {
        return avgMSIntensityInput;
    }

    public void setAvgMSIntensityInput(UIInput avgMSIntensityInput) {
        this.avgMSIntensityInput = avgMSIntensityInput;
    }

    public void clearForm() {
        this.avgMSIntensity = 2;
        //this.overallMSMSIntensity = null;
        this.noisePercentage = null;
        this.numScans = null;
        this.numSamples = null;
        this.coElution = null;
        this.crossTalk = null;
        this.finalScore = null;
        this.stringFinalScore = "";
        this.tagFinalScore = "";
        this.colorFinalScore = "";
    }

    public void loadDataExample() {
        this.avgMSIntensity = 2;
        this.overallMSMSIntensity = 1;
        this.noisePercentage = 1;
        this.numScans = 1;
        this.numSamples = 1;
        this.coElution = 2;
        this.crossTalk = 2;
        submitFeatures();
    }

    public void submitFeatures() {
        if (isValidAvgMSIntensity() && isValidOverallMSMSIntensity() && isValidNoisePercentage()
                && isValidNumScans() && isValidCoElution() && isValidCrossTalk()) {
            calculateFinalScore();
            chooseColorAndTag();
        } else {
            this.finalScore = null;
            resetColorAndTag();
        }
    }

    private void calculateFinalScore() {
        // TODO THIS METHOD!
        if (this.coElution == 0) {
            this.finalScore = 0d;
        } else {
            this.finalScore = this.crossTalk.doubleValue() / 2 + this.coElution.doubleValue() / 2
                    + this.numScans.doubleValue() / 2 + this.noisePercentage.doubleValue() / 2;
            if (this.overallMSMSIntensity == 0 && this.noisePercentage == 2) {
                this.finalScore = this.finalScore + 0.5d;
            } else {
                this.finalScore = this.finalScore + this.overallMSMSIntensity.doubleValue() / 2;
            }
        }

    }

    private void chooseColorAndTag() {
        if (this.finalScore != null) {
            if (this.finalScore <= 2d) {
                this.colorFinalScore = "red";
                this.tagFinalScore = "Inadequate";
            } else if (this.finalScore <= 3.5d) {
                this.colorFinalScore = "yellow";
                this.tagFinalScore = "Acceptable";
            } else if (this.finalScore > 3.5d) {
                this.colorFinalScore = "green";
                this.tagFinalScore = "Excellent";
            }
        }
        //System.out.println("TAG: " + this.tagFinalScore + " COLOR: " + this.colorFinalScore);
    }

    private void resetColorAndTag() {
        this.colorFinalScore = "";
        this.tagFinalScore = "Introduce valid data";
    }

/**
 * Validates the average signal to be a double between 0 and 1000000000
 *
 * @param arg0 FacesContext of the form
 * @param arg1 Component of the form
 * @param value Input of the user in the component arg1
 *
 */
public void validateAverageMSSignal(FacesContext arg0, UIComponent arg1, Object value)
            throws ValidatorException {
        Double averageMSSignal = -1d;
        try {
            averageMSSignal = (Double) value;

        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The average signal should be a number between 0 and 1000000000"));
        }
        if (averageMSSignal <= 0) {
            throw new ValidatorException(new FacesMessage("The average signal should be a number between 0 and 1000000000"));
        } else if (averageMSSignal > 1000000000) {
            throw new ValidatorException(new FacesMessage("The average signal should be a number between 0 and 1000000000"));
        }
    }

    /**
     * Validates the overall intensity to be a double between 0 and 1000000000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param value Input of the user in the component arg1
     *
     */
    public void validateOverallIntensity(FacesContext arg0, UIComponent arg1, Object value)
            throws ValidatorException {
        Double averageMSSignal = -1d;
        Double overallMSMSSignal = -1d;
        try {
            averageMSSignal = ((Double) avgMSIntensityInput.getLocalValue());
            overallMSMSSignal = (Double) value;
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The overall intensity should be a number between 0 and 1000000000"));
        }
        if (overallMSMSSignal <= 0) {
            throw new ValidatorException(new FacesMessage("The overall intensity should be a number between 0 and 1000000000"));
        } else if (overallMSMSSignal > 1000000000) {
            throw new ValidatorException(new FacesMessage("The overall intensity should be a number between 0 and 1000000000"));
        }
    }

    /**
     * Validates the noise percentage to be an Integer between 0 and 100
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param value Input of the user in the component arg1
     *
     */
    public void validateNoisePercentage(FacesContext arg0, UIComponent arg1, Object value)
            throws ValidatorException {
        Integer noise = -1;
        try {
            noise = (Integer) value;
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The noise should be a number between 0 and 100"));
        }
        if (noise < 0) {
            throw new ValidatorException(new FacesMessage("The noise should be a number between 0 and 100"));
        } else if (noise > 100) {
            throw new ValidatorException(new FacesMessage("The noise should be a number between 0 and 100"));
        }
    }

    /**
     * Validates the num of scans to be a float between 0 and 100
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param value Input of the user in the component arg1
     *
     */
    public void validateNumScans(FacesContext arg0, UIComponent arg1, Object value)
            throws ValidatorException {
        Integer numScansInput = -1;
        try {
            numScansInput = (Integer) value;
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The number of scans should be a number between 0 and 100"));
        }
        if (numScansInput < 0) {
            throw new ValidatorException(new FacesMessage("The number of scans should be a number between 0 and 100"));
        } else if (numScansInput > 100) {
            throw new ValidatorException(new FacesMessage("The number of scans should be a number between 0 and 100"));
        }
    }

}