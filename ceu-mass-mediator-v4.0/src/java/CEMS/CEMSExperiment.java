/*
 * CEMSExperiment.java
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
 * It also saves the tolerance usedto search for annotations
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSExperiment implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CEMSFeature> CEMSFeatures;

    private final Double tolerance;
    private final Integer tolerance_type;

    private final Integer ionizationMode; // 0:neutral, 1: positive 2: negative
    private final Integer buffer; // 1: formic acid 1 1M methanol 10%, 2: acetic acid 10%
    private final Integer matrix;  // 1: standard, 2: plasma, ..Still to do
    private final Integer capillary_length; // capillary length in cm (100)
    private final Integer bge; // code of the background electrolyte chosen
    private final Integer temperature; //temperature in celsius degrees (default 20)

    public CEMSExperiment(Double tolerance, Integer tolerance_type, Integer ionizationMode,
            Integer buffer) {
        this(tolerance, tolerance_type, ionizationMode, buffer, 0, 100, 1, 20);
    }

    public CEMSExperiment(Double tolerance, Integer tolerance_type, Integer ionizationMode,
            Integer buffer,
            Integer matrix, Integer capillary_length, Integer bge, Integer temperature) {
        this.tolerance = tolerance;
        this.tolerance_type = tolerance_type;
        this.ionizationMode = ionizationMode;
        this.matrix = matrix;
        this.buffer = buffer;
        this.capillary_length = capillary_length;
        this.bge = bge;
        this.temperature = temperature;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public int getTolerance_type() {
        return tolerance_type;
    }

    public int getIonizationMode() {
        return ionizationMode;
    }

    public int getMatrix() {
        return matrix;
    }

    public int getBuffer() {
        return buffer;
    }

    public Integer getCapillary_length() {
        return capillary_length;
    }

    public int getBge() {
        return bge;
    }

    public int getTemperature() {
        return temperature;
    }

    public List<CEMSFeature> getCEMSFeatures() {
        return CEMSFeatures;
    }

    public void setCEMSFeatures(List<CEMSFeature> CEMSFeatures) {
        this.CEMSFeatures = CEMSFeatures;
    }

}
