/*
 * CEMSExperimentAdapter.java
 *
 * Created on 22-may-2019, 19:59:23
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.List;
/**
 * Experiment. Consist on the data obtained through an experiment in the
 * laboratory. This data is introduced by the user. An experiment contains
 * several features (tuples of mz, MT or RMT, ionization_mode, buffer, capillary
 * length, buffer and temperature)
 *
 * It also saves the mzTolerance usedto search for annotations
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public abstract class CEMSExperimentAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CEMSFeature> CEMSFeatures;

    private final Integer mzTolerance;
    private final Integer mzTolerance_type;

    private final Integer ionizationMode; // 0:neutral, 1: positive 2: negative
    private final Integer polarity; // 1: direct, 2: inverse
    private final Integer buffer; // 1: formic acid 1 1M methanol 10%, 2: acetic acid 10%
    private final Integer temperature; //temperature in celsius degrees (default 20)

    // variables to calculate the MT or the RMT
    private final Integer capillary_voltage; // capillary_voltage in V (100) // TODO MIN MAX
    private final Integer capillary_length; // capillary length in cm (100) // TODO MIN MAX

    

    /**
     *
     * @param mzTolerance
     * @param mzTolerance_type
     * @param ionizationMode
     * @param polarity
     * @param buffer
     * @param temperature
     * @param capillary_voltage
     * @param capillary_length
     */
    public CEMSExperimentAdapter(Integer mzTolerance, Integer mzTolerance_type,
            Integer ionizationMode, Integer polarity,
            Integer buffer, Integer temperature,
            Integer capillary_voltage, Integer capillary_length) {
        this.mzTolerance = mzTolerance;
        this.mzTolerance_type = mzTolerance_type;
        this.ionizationMode = ionizationMode;
        this.polarity = polarity;
        this.buffer = buffer;
        this.temperature = temperature;
        this.capillary_voltage = capillary_voltage;
        this.capillary_length = capillary_length;
    }

    public Integer getMzTolerance() {
        return mzTolerance;
    }

    public Integer getMzTolerance_type() {
        return mzTolerance_type;
    }

    public Integer getIonizationMode() {
        return ionizationMode;
    }

    public Integer getPolarity() {
        return polarity;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public Integer getCapillary_voltage() {
        return capillary_voltage;
    }

    public Integer getCapillary_length() {
        return capillary_length;
    }

    public List<CEMSFeature> getCEMSFeatures() {
        return CEMSFeatures;
    }

    public void setCEMSFeatures(List<CEMSFeature> CEMSFeatures) {
        this.CEMSFeatures = CEMSFeatures;
    }

}
