/*
 * CEMSFeature.java
 *
 * Created on 22-may-2019, 19:28:15
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Features coming from CE MS experiments
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSFeature implements Serializable {

    private static final long serialVersionUID = 1L;

    // Description of the CEMS features
    private final Double exp_mz;
    private final Double exp_MT;
    private final Double exp_RMT;
    private final Map<Double, Double> fragmentsCEMS;
    // Key of EM_RT for scores
    private final String myKey;
    private final int ionization_mode; // 0 neutral, 1 positive, 2 negative
    private final List<CEMSCompoundsGroupByAdduct> annotationsCEMSGroupByAdduct;
    private final List<CEMSProductIon> annotationsFragmentsCEMS;

    public CEMSFeature(Double exp_mz, Double exp_MT, Double exp_RMT,
            Map<Double, Double> fragmentsCEMS, int ionization_mode,
            List<CEMSCompoundsGroupByAdduct> annotationsCEMSGroupByAdduct,
            List<CEMSProductIon> annotationsFragmentsCEMS) {
        this.exp_mz = exp_mz;
        this.exp_MT = exp_MT;
        this.exp_RMT = exp_RMT;
        this.myKey = exp_mz.toString() + "_" + exp_RMT.toString();
        this.fragmentsCEMS = fragmentsCEMS;
        this.ionization_mode = ionization_mode;
        this.annotationsCEMSGroupByAdduct = annotationsCEMSGroupByAdduct;
        this.annotationsFragmentsCEMS = annotationsFragmentsCEMS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final CEMSFeature other = (CEMSFeature) obj;
        if (!Objects.equals(this.exp_mz, other.exp_mz)) {
            return false;
        }
        return Objects.equals(this.exp_RMT, other.exp_RMT);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.exp_mz);
        hash = 97 * hash + Objects.hashCode(this.exp_RMT);
        return hash;
    }

    @Override
    public String toString() {
        return "CEMSFeature{" + "exp_mz=" + exp_mz + ", exp_MT=" + exp_MT
                + ", exp_RMT=" + exp_RMT + ", myKey=" + myKey
                + ", ionization_mode=" + ionization_mode
                + ", annotationsCEMSGroupByAdduct=" + annotationsCEMSGroupByAdduct
                + ", annotationsFragmentsCEMS=" + annotationsFragmentsCEMS + '}';
    }

    public double getExp_mz() {
        return exp_mz;
    }

    public Double getExp_MT() {
        return exp_MT;
    }

    public Double getExp_RMT() {
        return exp_RMT;
    }

    public Map<Double, Double> getFragmentsCEMS() {
        return fragmentsCEMS;
    }

    public int getIonization_mode() {
        return ionization_mode;
    }

    public List<CEMSCompoundsGroupByAdduct> getAnnotationsCEMSGroupByAdduct() {
        return annotationsCEMSGroupByAdduct;
    }

    public List<CEMSProductIon> getAnnotationsFragmentsCEMS() {
        return annotationsFragmentsCEMS;
    }

    public String getMyKey() {
        return this.myKey;
    }

    public Integer getNumAnnotationsGrouped() {
        int numAnnotationsPrecursorIons = 0;
        if (this.annotationsCEMSGroupByAdduct != null) {
            numAnnotationsPrecursorIons = this.annotationsCEMSGroupByAdduct.size();
        }
        return numAnnotationsPrecursorIons;
    }

    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Compounds found for m/z: " + this.exp_mz + "";
        if (this.exp_RMT != null && this.exp_RMT > 0d) {
            titleMessage = titleMessage + " and RMT: " + this.exp_RMT;
        }

        boolean allAnnotationGroupNotEmpty = false;
        int sizeAnnotationGroup = 0;
        for (CEMSCompoundsGroupByAdduct cemsAannotationsGroupedByAdductPartial : this.annotationsCEMSGroupByAdduct) {
            if (cemsAannotationsGroupedByAdductPartial != null && !cemsAannotationsGroupedByAdductPartial.isEmpty()) {
                allAnnotationGroupNotEmpty = true;
                sizeAnnotationGroup = sizeAnnotationGroup
                        + cemsAannotationsGroupedByAdductPartial.getNumberAnnotations();
            }
        }
        if (this.annotationsFragmentsCEMS != null && !this.annotationsFragmentsCEMS.isEmpty()) {
            allAnnotationGroupNotEmpty = true;
            sizeAnnotationGroup = sizeAnnotationGroup
                    + this.annotationsFragmentsCEMS.size();
        }
        if (!allAnnotationGroupNotEmpty) {
            titleMessage = "No " + titleMessage;
        } else {
            titleMessage = titleMessage + " -> " + sizeAnnotationGroup;
        }
        /*
        if (isAdductAutoDetected) {
            titleMessage = titleMessage + " Adduct detected: " + adductAutoDetected;
        }
         */
// We do not detect automatically the adduct in CE MS
        return titleMessage;
    }

    /**
     * Get title for the presentation
     *
     * @param adduct
     * @param numAnnotations
     * @return
     */
    public String getTitleMessage(String adduct, int numAnnotations) {
        String titleMessage;
        titleMessage = "Compounds found for Adduct: " + adduct;

        if (numAnnotations == 0) {
            titleMessage = getEmptyAnnotationMessage(adduct);
        } else {
            titleMessage = titleMessage + " -> " + numAnnotations;
        }
        return titleMessage;
    }

    /**
     * Get the empty title when there are no compounds for a group of
     * annotations
     *
     * @param adduct
     * @return
     */
    public String getEmptyAnnotationMessage(String adduct) {

        String titleMessage = "No compounds found for mz: " + this.exp_mz + ", RMT: "
                + this.exp_RMT + " and adduct: " + adduct;
        /*
        if (this.isAdductAutoDetected) {
            titleMessage = "No compounds searched. The adduct detected was: " + this.adductAutoDetected + "";
        } else {
            titleMessage = "No compounds found";
        }
         */
        return titleMessage;
    }

    /**
     * Get title for the fragments found
     *
     * @return
     */
    public String getTitleMessageForProductIons() {
        String titleMessage;
        titleMessage = "Fragments found for mz: " + this.exp_mz
                + ", RMT: " + this.exp_RMT + " -> " + this.annotationsFragmentsCEMS.size();
        if (this.annotationsFragmentsCEMS == null || this.annotationsFragmentsCEMS.isEmpty()) {
            titleMessage = getEmptyAnnotationMessageForProductIons();
        }
        return titleMessage;
    }

    /**
     * Get the empty title when there are no product ions for the MZ and the RMT
     *
     * @return
     */
    public String getEmptyAnnotationMessageForProductIons() {

        String titleMessage = "No fragments found for mz: " + this.exp_mz + ", RMT: "
                + this.exp_RMT;
        /*
        if (this.isAdductAutoDetected) {
            titleMessage = "No compounds searched. The adduct detected was: " + this.adductAutoDetected + "";
        } else {
            titleMessage = "No compounds found";
        }
         */
        return titleMessage;
    }

}
