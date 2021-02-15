/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * MSMS Object. 
 *
 * @author Maria Postigo. Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 15/04/2018
 */
public class Msms {

    //0=negative, 1=positive
    private final int ionizationMode; 
    private final int voltage;
    //low=10-20, med=20-40, high>=40
    private final String voltageLevel;
    private final List<Peak> normalizedPeaks;
    private final List<Peak> absolutePeaks;
    protected double parentIonNeutralMass;
    protected double parentIonMZ;
    private final int spectraType;

    public String getName() {
        return name;
    }
    //maria borra
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    // Putative annotations from database info. 
    private List<MSMSCompound> compounds;
    

    /**
     *
     * @param ionizationMode
     * @param voltage
     * @param voltageLevel
     * @param peaks
     * @param spectraType
     * 
     */
    public Msms(int ionizationMode, int voltage, String voltageLevel, List<Peak> peaks, int spectraType) {
        this.ionizationMode = ionizationMode;
        this.voltage = voltage;
        this.voltageLevel = voltageLevel;
        this.absolutePeaks = peaks;
        this.normalizedPeaks = normalizePeakIntensities();
        this.spectraType = spectraType;

    }

    /**
     *
     * @param parentIonMZ MZ of the parent ion
     * @param ionizationMode
     * @param voltage
     * @param voltageLevel
     * @param peaks
     * @param spectraType
     */
    public Msms(double parentIonMZ, int ionizationMode, int voltage,
            String voltageLevel, List<Peak> peaks, int spectraType) {
        this.parentIonMZ = parentIonMZ;
        this.ionizationMode = ionizationMode;
        //this.parentIonNeutralMass = utilities.AdductProcessing.getMassToSearch(this.parentIonMZ, "M+H", ionizationMode);
        this.parentIonNeutralMass = utilities.AdductProcessing.calculateNeutralMassFromHAdduct(ionizationMode, this.parentIonMZ);
        this.voltage = voltage;
        this.voltageLevel = voltageLevel;
        this.absolutePeaks = peaks;
        this.normalizedPeaks = normalizePeakIntensities();
        this.spectraType = spectraType;


    }

    public int getIonizationModetoint(String ionizationMode) {
        if (ionizationMode.equalsIgnoreCase("positive")) {
            return 1;
        }
        if (ionizationMode.equalsIgnoreCase("negative")) {
            return 0;
        }
        return -1;
    }

    //to normalize the intensities, since they are normalized in the database
    public double getMaxIntensity() {
        Iterator it = this.absolutePeaks.iterator();
        Double maxIntensity = 0d;
        while (it.hasNext()) {
            Peak p = (Peak) it.next();
            maxIntensity = Math.max(maxIntensity, p.getIntensity());
        }
        return maxIntensity;
    }

    private List<Peak> normalizePeakIntensities() {

        List<Peak> normalizedPeaksLocal = new LinkedList<Peak>();
        Double maxIntensity = this.getMaxIntensity();
        if (maxIntensity == 1) {
            Iterator it = this.absolutePeaks.iterator();
            while (it.hasNext()) {
                Peak p = (Peak) it.next();
                double mzPeak = p.getMz();
                double normalizedIntensityPeak = p.getIntensity() * 100;
                int msmsId = p.getMsms_id();
                Peak peakNormalized = new Peak(mzPeak, normalizedIntensityPeak, msmsId);
                normalizedPeaksLocal.add(peakNormalized);
            }
        } else if (maxIntensity != 100) {
            for (Peak p : this.absolutePeaks) {
                double mzPeak = p.getMz();
                double normalizedIntensityPeak = (p.getIntensity() * 100) / maxIntensity;
                int msmsId = p.getMsms_id();
                Peak peakNormalized = new Peak(mzPeak, normalizedIntensityPeak, msmsId);
                normalizedPeaksLocal.add(peakNormalized);
            }
        } else if (maxIntensity == 100) {
            for (Peak p : this.absolutePeaks) {
                double mzPeak = p.getMz();
                double normalizedIntensityPeak = p.getIntensity();
                int msmsId = p.getMsms_id();
                Peak peakNormalized = new Peak(mzPeak, normalizedIntensityPeak, msmsId);
                normalizedPeaksLocal.add(peakNormalized);
            }
        }
        return normalizedPeaksLocal;
    }

    /**
     * Return the list of absolute peaks
     *
     * @return
     */
    public List<Peak> getAbsolutePeaks() {
        return absolutePeaks;
    }


    public double getPrecursorIonNeutralMass() {
        return parentIonNeutralMass;
    }

    public double getPrecursorIonMZ() {
        return parentIonMZ;
    }

    public int getIonizationMode() {
        return ionizationMode;
    }

    public int getVoltage() {
        return voltage;
    }

    public String getVoltageLevel() {
        return voltageLevel;
    }

    public List<Peak> getNormalizedPeaks() {
        return normalizedPeaks;
    }

    public List<MSMSCompound> getCompounds() {
        return compounds;
    }

    public void setCompounds(List<MSMSCompound> compounds) {
        this.compounds = compounds;
    }

    public void addCompoundToCompounds(MSMSCompound compound) {
        this.compounds.add(compound);
    }

    @Override
    public String toString() {
        return "MSMS\nParent ion mass: " + this.parentIonMZ + "\nIonization mode: " + this.ionizationMode + "\nVoltage: " + this.voltageLevel;
    }

    public int getSpectraType() {
        return spectraType;
    }

    protected void setParentIonNeutralMass(double parentIonNeutralMass) {
        this.parentIonNeutralMass = parentIonNeutralMass;
    }

    protected void setParentIonMZ(double parentIonMZ) {
        this.parentIonMZ = parentIonMZ;
    }

    public String getStringSpectraType() {
        if (getSpectraType() == 1) {
            return "Predicted";
        } else if (getSpectraType() == 0) {
            return "Experimental";
        }
        return "N/A";
    }

}
