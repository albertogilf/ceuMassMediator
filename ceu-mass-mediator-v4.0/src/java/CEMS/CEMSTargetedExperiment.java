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
import java.util.Set;

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
public class CEMSTargetedExperiment extends CEMSRMTExperiment implements Serializable {

    private List<Set<CEMSFragment>> groupedSignals;

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
     * @param rmtTolerance
     * @param rmtTolerance_type
     * @param codeBGE
     * @param groupedSignals
     */
    public CEMSTargetedExperiment(Integer mzTolerance, Integer mzTolerance_type,
            Integer ionizationMode, Integer polarity, Integer buffer, Integer temperature,
            Integer capillary_voltage, Integer capillary_length, Integer rmtTolerance, Integer rmtTolerance_type, 
            Integer codeBGE,
            List<Set<CEMSFragment>> groupedSignals) {
        super(mzTolerance, mzTolerance_type, ionizationMode, polarity, buffer, temperature, capillary_voltage, capillary_length, 
                rmtTolerance, rmtTolerance_type,codeBGE);
        this.groupedSignals = groupedSignals;
    }

    public List<Set<CEMSFragment>> getGroupedSignals() {
        return this.groupedSignals;
    }

    public void setInputGroupedSignals(List<Set<CEMSFragment>> groupedSignals) {
        this.groupedSignals = groupedSignals;
    }

}
