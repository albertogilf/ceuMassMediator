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

/**
 * Experiment. Consist on the data obtained through an experiment in the
 * laboratory. This data is introduced by the user. An experiment contains
 * several features (tuples of mz, MT or RMT, ionization_mode, buffer, capillary
 * length, buffer and temperature)
 *
 * It also saves the tolerance usedto search for annotations
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSRMTExperiment extends CEMSExperimentAdapter implements Serializable {

    private final Integer rmtTolerance;
    private final Integer rmtTolerance_type; // 
    
    private final Integer codeBGE; // Code of the compound to calculate the RMT with

    /**
     *
     * @param mzTolerance: 0-1000
     * @param mzTolerance_type 1: ppm, 2: mDa
     * @param ionizationMode 0: neutral, 1: positive, 2:negative
     * @param polarity
     * @param buffer 1: formic acid, 2: acetic acid
     * @param capillary_voltage
     * @param capillary_length
     * @param temperature
     * @param RMTTolerance
     * @param RMTTolerance_type
     * @param codeBGE
     */
    public CEMSRMTExperiment(Integer mzTolerance, Integer mzTolerance_type, 
            Integer ionizationMode, Integer polarity, 
            Integer buffer, Integer temperature,
            Integer capillary_voltage, Integer capillary_length, 
            Integer RMTTolerance, Integer RMTTolerance_type,
            Integer codeBGE) {
        super(mzTolerance, mzTolerance_type, ionizationMode, polarity, buffer, temperature, capillary_voltage, capillary_length);
        this.rmtTolerance = RMTTolerance;
        this.rmtTolerance_type = RMTTolerance_type;
        this.codeBGE = codeBGE;
    }

    /**
     *
     * @return the code of the BGE to calculate the RMT
     */
    public Integer getCodeBGE() {
        return codeBGE;
    }


    public Integer getRmtTolerance() {
        return rmtTolerance;
    }

    public Integer getRmtTolerance_type() {
        return rmtTolerance_type;
    }

}
