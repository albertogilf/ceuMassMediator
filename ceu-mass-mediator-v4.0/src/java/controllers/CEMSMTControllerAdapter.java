package controllers;

import CEMS.CEMSCompound;
import exceptions.bufferTemperatureException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import utilities.CEMSLists;
import static utilities.CEMSLists.getBufferFromBufferTempList;
import static utilities.CEMSLists.getTemperatureFromBufferTempList;

/**
 * abstract parent controller for the distinct searches in CEMS (EffMob, RT, RMT
 * and targeted).
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2019
 */
public abstract class CEMSMTControllerAdapter extends CEMSControllerAdapter implements Serializable {

    // variables to calculate the MT 
    private Integer capillary_voltage; // capillary_voltage in V from 1 to 300000 (300 kV)
    private Integer capillary_length; // Length in mm from 1 to 10000 (10 m)
    private Integer IDMarker1; // Compound ID of the marker 1
    private Double markerTime1; // Migration or Relative Migration Time of the marker 1
    private Integer IDMarker2; // Compound ID of the marker 2
    private Double markerTime2; // Migration or Relative Migration Time of the marker 2

    private List<SelectItem> bgeCandidates;
    private List<SelectItem> bgeExpCandidates;
    private final List<SelectItem> bgeFormic20CandidatesPositiveDirect;
    private final List<SelectItem> bgeFormic20CandidatesNegativeInverse;
    private final List<SelectItem> bgeAcetic25CandidatesPositiveDirect;
    private final List<SelectItem> bgeAcetic25CandidatesNegativeInverse;
    private Map<Integer, CEMSCompound> dbCandidates;
    private Map<Integer, CEMSCompound> dbFormic20CandidatesPositiveDirect;
    private Map<Integer, CEMSCompound> dbFormic20CandidatesNegativeInverse;
    private Map<Integer, CEMSCompound> dbAcetic25CandidatesPositiveDirect;
    private Map<Integer, CEMSCompound> dbAcetic25CandidatesNegativeInverse;

    public CEMSMTControllerAdapter() {
        super();

        this.dbFormic20CandidatesPositiveDirect = fillDBsByExpCond(1, 20, 1, 1);
        this.dbFormic20CandidatesNegativeInverse = fillDBsByExpCond(1, 20, 2, 2);
        this.dbAcetic25CandidatesPositiveDirect = fillDBsByExpCond(2, 25, 1, 1);
        this.dbAcetic25CandidatesNegativeInverse = fillDBsByExpCond(2, 25, 2, 2);

        this.bgeFormic20CandidatesPositiveDirect = getSelectItemsFromCompounds(this.dbFormic20CandidatesPositiveDirect);
        this.bgeFormic20CandidatesNegativeInverse = getSelectItemsFromCompounds(this.dbFormic20CandidatesNegativeInverse);
        this.bgeAcetic25CandidatesPositiveDirect = getSelectItemsFromCompounds(this.dbAcetic25CandidatesPositiveDirect);
        this.bgeAcetic25CandidatesNegativeInverse = getSelectItemsFromCompounds(this.dbAcetic25CandidatesNegativeInverse);

        this.bgeCandidates = this.bgeFormic20CandidatesPositiveDirect;
        this.bgeExpCandidates = CEMSLists.BGEEXPCANDIDATES;
        this.dbCandidates = this.dbFormic20CandidatesPositiveDirect;
        this.capillary_length = 1000; // mm
        this.capillary_voltage = 30; // kvoltios
    }
    
    /**
     * Clear the form of CEMS MT
     */
    @Override
    public void clearForm() {
        super.clearForm();
        this.setMarkerTime1(null);
        this.setMarkerTime2(null);
    }

    @Override
    protected void setDemoMasses() {
        super.setDemoMasses();
        this.capillary_length = 1000; // mm
        this.capillary_voltage = 30; // kvoltios
        if (super.getBufferTemperatureCode().equals(1)) {
            this.IDMarker1 = 180838;
            this.markerTime1 = 14.24;
            this.IDMarker2 = 91854;
            this.markerTime2 = 25.29;
        } else if (super.getBufferTemperatureCode().equals(2)) {
            this.IDMarker1 = 125813;
            this.IDMarker2 = 73414;
            this.markerTime1 = null;
            this.markerTime2 = null;
        }

    }

    public Integer getCapillary_voltage() {
        return capillary_voltage;
    }

    public void setCapillary_voltage(Integer capillary_voltage) {
        this.capillary_voltage = capillary_voltage;
    }

    public Integer getCapillary_length() {
        return capillary_length;
    }

    public void setCapillary_length(Integer capillary_length) {
        this.capillary_length = capillary_length;
    }

    public Integer getIDMarker1() {
        return IDMarker1;
    }

    public void setIDMarker1(Integer IDMarker1) {
        this.IDMarker1 = IDMarker1;
    }

    public Double getMarkerTime1() {
        return markerTime1;
    }

    public void setMarkerTime1(Double markerTime1) {
        this.markerTime1 = markerTime1;
    }

    public Integer getIDMarker2() {
        return IDMarker2;
    }

    public void setIDMarker2(Integer IDMarker2) {
        this.IDMarker2 = IDMarker2;
    }

    public Double getMarkerTime2() {
        return markerTime2;
    }

    public void setMarkerTime2(Double markerTime2) {
        this.markerTime2 = markerTime2;
    }

    public void setBgeCandidates(List<SelectItem> bgeCandidates) {
        this.bgeCandidates = bgeCandidates;
    }

    public List<SelectItem> getBgeCandidates() {
        chooseListRMTCandidates();
        return bgeCandidates;
    }

    public List<SelectItem> getBgeExpCandidates() {
        chooseListRMTCandidates();
        return bgeExpCandidates;
    }

    public void setBgeExpCandidates(List<SelectItem> bgeExpCandidates) {
        this.bgeExpCandidates = bgeExpCandidates;
    }

    public void setDbCandidates(Map<Integer, CEMSCompound> dbCandidates) {
        this.dbCandidates = dbCandidates;
    }

    public Map<Integer, CEMSCompound> getDbCandidates() {
        return dbCandidates;
    }

    private Map<Integer, CEMSCompound> fillDBsByExpCond(Integer buffer, Integer temperature, Integer ionMode, Integer polarity) {
        Map<Integer, CEMSCompound> cemscompounds = new LinkedHashMap<Integer, CEMSCompound>();
        try {
            cemscompounds = this.msFacade.getCEMSCompoundsFromExperimentalConditions(buffer, temperature, ionMode, polarity, this.allowOppositeESIMode);

        } catch (bufferTemperatureException ex) {
            Logger.getLogger(CEMSRMTController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cemscompounds;
    }

    private List<SelectItem> getSelectItemsFromCompounds(Map<Integer, CEMSCompound> compounds) {
        List<SelectItem> selectItemsCompounds = new LinkedList<>();
        for (Map.Entry<Integer, CEMSCompound> pair : compounds.entrySet()) {
            Integer compoundId = pair.getKey();
            String compoundName = pair.getValue().getCompound_name();
            selectItemsCompounds.add(new SelectItem(compoundId, compoundName));
        }

        return selectItemsCompounds;
    }

    public void chooseListRMTCandidates() {
        try {
            int buffer = getBufferFromBufferTempList(super.getBufferTemperatureCode());
            int temperature = getTemperatureFromBufferTempList(super.getBufferTemperatureCode());
            int provIonMode;
            if (super.getMassesMode() == 0) {
                provIonMode = 1;
            } else {
                provIonMode = super.getIonMode();
            }
            if (buffer == 1 && temperature == 20 && provIonMode == 1 && super.getPolarity() == 1) {
                this.setBgeCandidates(bgeFormic20CandidatesPositiveDirect);
                this.setDbCandidates(dbFormic20CandidatesPositiveDirect);
                this.setBgeExpCandidates(CEMSLists.BGEEXPCANDIDATES);
            } else if (buffer == 1 && temperature == 20 && provIonMode == 2 && super.getPolarity() == 2) {
                this.setBgeCandidates(bgeFormic20CandidatesNegativeInverse);
                this.setDbCandidates(dbFormic20CandidatesNegativeInverse);
                List<SelectItem> list = new LinkedList<SelectItem>();
                list.add(new SelectItem(0, "No compounds available in the database"));
                this.setBgeExpCandidates(list);
            } else if (buffer == 2 && temperature == 25 && provIonMode == 1 && super.getPolarity() == 1) {
                this.setBgeCandidates(bgeAcetic25CandidatesPositiveDirect);
                this.setDbCandidates(dbAcetic25CandidatesPositiveDirect);
                List<SelectItem> list = new LinkedList<SelectItem>();
                list.add(new SelectItem(0, "No compounds available in the database"));
                this.setBgeExpCandidates(list);
            } else if (buffer == 2 && temperature == 25 && provIonMode == 2 && super.getPolarity() == 2) {
                this.setBgeCandidates(bgeAcetic25CandidatesNegativeInverse);
                this.setDbCandidates(dbAcetic25CandidatesNegativeInverse);
                List<SelectItem> list = new LinkedList<SelectItem>();
                list.add(new SelectItem(0, "No compounds available in the database"));
                this.setBgeExpCandidates(list);
            } else {
                List<SelectItem> list = new LinkedList<SelectItem>();
                list.add(new SelectItem(0, "No compounds available in the database"));
                this.setBgeCandidates(list);
                this.setBgeExpCandidates(list);
                Map<Integer, CEMSCompound> mapCompounds = new LinkedHashMap<>();
                this.setDbCandidates(mapCompounds);

            }
            if (this.bgeCandidates.isEmpty()) {
                List<SelectItem> list = new LinkedList<SelectItem>();
                list.add(new SelectItem(0, "No compounds available in the database"));
                this.setBgeCandidates(list);
                this.setBgeExpCandidates(list);
                Map<Integer, CEMSCompound> mapCompounds = new LinkedHashMap<>();
                this.setDbCandidates(mapCompounds);
            }
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(CEMSRMTController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Validates the capillary_voltage to be a number between 1 and 300 Kv
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateInputCapillaryVoltage(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputCapillaryVoltage(arg0, arg1, arg2);
    }

    /**
     * Validates the capillary length to be a number between 1 and 10000 mm (10
     * meters)
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateInputCapillaryLength(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateInputCapillaryLength(arg0, arg1, arg2);
    }

    /**
     * Validates the capillary length to be a number between 1 and 10000 mm (10
     * meters)
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public void validateMarkerTime(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        InterfaceValidators.validateMarkerTime(arg0, arg1, arg2);
    }

}
