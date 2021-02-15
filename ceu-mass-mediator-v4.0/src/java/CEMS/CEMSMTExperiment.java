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
public class CEMSMTExperiment extends CEMSExperimentAdapter implements Serializable {

    private final Integer mtTolerance;
    private final Integer mtTolerance_type;

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
     * @param mtTolerance
     * @param mtTolerance_type
     */
    public CEMSMTExperiment(Integer mzTolerance, int mzTolerance_type, int ionizationMode, int polarity,
            int buffer, int temperature,
            int capillary_voltage, int capillary_length,
            Integer mtTolerance, int mtTolerance_type) {
        super(mtTolerance, mtTolerance_type, ionizationMode, polarity, buffer, temperature, capillary_voltage, capillary_length);
        this.mtTolerance = mtTolerance;
        this.mtTolerance_type = mtTolerance_type;
    }

    public Integer getMtTolerance() {
        return mtTolerance;
    }

    public int getMtTolerance_type() {
        return mtTolerance_type;
    }

}
