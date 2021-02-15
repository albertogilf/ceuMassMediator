package controllers;

import CEMS.CEMSExperimentAdapter;
import CEMS.CEMSFeature;
import exporters.NewDataModelCompoundExcelExporter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.model.SelectItem;
import utilities.CEMSLists;
import utilities.Cadena;
import utilities.Constants;

/**
 * abstract parent controller for the distinct searches in CEMS (EffMob, RT, RMT
 * and targeted).
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2019
 */
public abstract class CEMSControllerAdapter extends MSController implements Serializable {

    protected CEMSExperimentAdapter cemsExperiment;

    private Integer bufferTemperatureCode; // 1: formic / 20 ºC, 2: acetic 25

    private Integer polarity; // default: 1 -> direct for positive ionization mode 
    // default 2 -> inverse for negative ionization mode

    private final List<SelectItem> bufferCandidates;
    private final List<SelectItem> polarityCandidates;

    protected Boolean allowOppositeESIMode;

    public CEMSControllerAdapter() {
        super();

        this.bufferCandidates = CEMSLists.BUFFERTEMPERATURELIST;
        this.polarityCandidates = CEMSLists.POLARITYLIST;

        this.polarity = 1; // direct

        this.bufferTemperatureCode = 1; // 1: formic acid / 20ºC 2: acetic acid / 25ºC

        this.allowOppositeESIMode = true;

    }

    /**
     * Method that permits to create a excel from the current results of CEMS
     * experiment. 3 for CEMS Untargeted search 4 for CEMS Targeted searches
     *
     * @param flag
     */
    public void exportToExcel(Integer flag) {
        List<CEMSFeature> cemsFeatures = getCEMSFeatures();
        if (cemsFeatures != null && !cemsFeatures.isEmpty()) {
            // excel from CMM Compound Browse Search
            NewDataModelCompoundExcelExporter compoundExcelExporter
                    = new NewDataModelCompoundExcelExporter(flag);

            compoundExcelExporter.generateWholeExcelCompound(this.cemsExperiment.getCEMSFeatures(), flag);
        }
    }

    /**
     * It reads a list of MZs form a String introduced in queryInputMasses and
     * saves them in inputmzs.
     *
     * @return the number of masses read
     */
    public Integer readInputMZs() {
        List<Double> mzsAux; // auxiliar List for input Masses
        Integer numInputMZs;
        //Method returns an ArrayList because it is acceded by index
        mzsAux = Cadena.extractDoubles(getQueryInputMasses());
        numInputMZs = mzsAux.size();
        this.setInputmzs(mzsAux);
        return numInputMZs;
    }

    public CEMSExperimentAdapter getCemsExperiment() {
        if (this.cemsExperiment == null) {

        }
        return this.cemsExperiment;
    }

    public void setCemsExperiment(CEMSExperimentAdapter cemsExperiment) {
        this.cemsExperiment = cemsExperiment;
    }

    protected void setDemoMasses() {
        super.setDemoParameters();
        this.setQueryInputMasses(Constants.CEDEMOMASSES);
        this.chemAlphabet = "CHNOPS";
        this.includeDeuterium = false;
    }

    public Integer getPolarity() {
        return polarity;
    }

    public void setPolarity(Integer polarity) {
        this.polarity = polarity;
    }

    public void setBufferTemperatureCode(Integer bufferTemperatureCode) {
        this.bufferTemperatureCode = bufferTemperatureCode;
    }

    public Integer getBufferTemperatureCode() {
        return bufferTemperatureCode;
    }

    public List<SelectItem> getBufferCandidates() {
        return bufferCandidates;
    }

    public List<SelectItem> getPolarityCandidates() {
        return polarityCandidates;
    }

    public Boolean getAllowOppositeESIMode() {
        return allowOppositeESIMode;
    }

    public void setAllowOppositeESIMode(Boolean allowOppositeESIMode) {
        this.allowOppositeESIMode = allowOppositeESIMode;
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

}
