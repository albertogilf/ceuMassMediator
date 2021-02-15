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

import exceptions.bufferTemperatureException;
import java.io.Serializable;
import static utilities.CEMSLists.getBufferFromBufferTempList;
import static utilities.CEMSLists.getTemperatureFromBufferTempList;

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
public class CEMSEffMobExperiment extends CEMSExperimentAdapter implements Serializable {

    private final Integer effMobTolerance;
    private final String effMobTolerance_type;

    /**
     *
     * @param mzTolerance
     * @param mzTolerance_type
     * @param ionizationMode
     * @param polarity
     * @param bufferTemperature 0: formic acid / 20ºC 1: acetic acid / 25ºC
     * @param capillary_voltage
     * @param capillary_length
     * @param effMobTolerance
     * @param effMobTolerance_type
     * @throws exceptions.bufferTemperatureException
     */
    public CEMSEffMobExperiment(Integer mzTolerance, Integer mzTolerance_type,
            Integer ionizationMode, Integer polarity,
            Integer bufferTemperature,
            Integer capillary_voltage, Integer capillary_length,
            Integer effMobTolerance, String effMobTolerance_type) throws bufferTemperatureException {
        this(mzTolerance, mzTolerance_type,
                ionizationMode, polarity,
                getBufferFromBufferTempList(bufferTemperature), getTemperatureFromBufferTempList(bufferTemperature),
                capillary_voltage, capillary_length,
                effMobTolerance, effMobTolerance_type);
    }

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
     * @param effMobTolerance
     * @param effMobTolerance_type
     */
    public CEMSEffMobExperiment(Integer mzTolerance, int mzTolerance_type, Integer ionizationMode, Integer polarity,
            Integer buffer, Integer temperature,
            Integer capillary_voltage, Integer capillary_length,
            Integer effMobTolerance, String effMobTolerance_type) {
        super(mzTolerance, mzTolerance_type, ionizationMode, polarity, buffer, temperature, capillary_voltage, capillary_length);
        this.effMobTolerance = effMobTolerance;
        this.effMobTolerance_type = effMobTolerance_type;
    }

    public Integer getEffMobTolerance() {
        return effMobTolerance;
    }

    public String getEffMobTolerance_type() {
        return effMobTolerance_type;
    }

}
