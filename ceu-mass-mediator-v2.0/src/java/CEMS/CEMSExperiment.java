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
 * several features (tuples of mz, MT or RMT, ionization_mode, buffer, capilar
 * length, buffer and temperature)
 *
 * It also saves the tolerance usedto search for annotations
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSExperiment implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private List<CEMSFeature> CEMSFeatures;

    private final Double tolerance;
    private final int tolerance_type;

    private final int ionizationMode; // 0:neutral, 1: positive 2: negative
    private final int buffer; // 1: formico 1 molar (1M) al 10% de methanol, 2: acetic acid 10%
    private final int matrix;  // 1: standard, 2: plasma, ..Still to do
    private final int capilar_length; // capilar length in cm (100)
    private final int bge; // 1: methionine sulfone, 2: paracetamol
    private final int temperature; //temperature in celsius degrees (default 20)

    public CEMSExperiment(Double tolerance, int tolerance_type, int ionizationMode, int buffer) {
        this(tolerance, tolerance_type, ionizationMode, buffer, 0, 100, 1, 20);
    }

    public CEMSExperiment(Double tolerance, int tolerance_type, int ionizationMode, int buffer,
            int matrix, int capilar_length, int bge, int temperature) {
        this.tolerance = tolerance;
        this.tolerance_type = tolerance_type;
        this.ionizationMode = ionizationMode;
        this.matrix = matrix;
        this.buffer = buffer;
        this.capilar_length = capilar_length;
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

    public int getCapilar_length() {
        return capilar_length;
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
