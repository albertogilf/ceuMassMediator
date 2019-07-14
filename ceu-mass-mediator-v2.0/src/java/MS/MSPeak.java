/*
 * MSPeak.java
 *
 * Created on 15-dic-2018, 13:51:48
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package MS;

/**
 * Refactorization to build a more precise model that represents better the 
 * metabolomics data
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 15-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class MSPeak {

    private final double EM;
    private final int intensity;
    private final int EMMode; // 0 neutral (protonated or deprotonated), 1 m/z
    private final int ionization_mode; // 0 neutral, 1 positive, 2 negative

    /**
     * Creates a new instance of MSPeak; if the intensity is not provided, it is
     * assigned to -1.
     *
     * @param EM Experimental Mass of the MSPeak
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionization_mode 0 neutral, 1 positive, 2 negative
     */
    public MSPeak(double EM, int EMMode, int ionization_mode) {
        this(EM, -1, EMMode, ionization_mode);
    }

    /**
     * Creates a new instance of MSPeak
     *
     * @param EM Experimental Mass of the MSPeak
     * @param intensity Intensity of the MSPeak
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionization_mode 0 neutral, 1 positive, 2 negative
     */
    public MSPeak(double EM, int intensity, int EMMode, int ionization_mode) {
        this.EM = EM;
        this.intensity = intensity;
        this.EMMode = EMMode;
        this.ionization_mode = ionization_mode;
    }

    public double getEM() {
        return EM;
    }

    public double getIntensity() {
        return intensity;
    }

    public int getEMMode() {
        return EMMode;
    }

    public int getIonization_mode() {
        return ionization_mode;
    }

}
