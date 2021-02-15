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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Features coming from CE MS experiments
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSFeature implements Serializable, Comparable<CEMSFeature> {

    private static final long serialVersionUID = 1L;

    // Description of the CEMS features
    private final Double input_mz;

    // For MT
    private final Double input_MT;

    // FOR RMT
    private final Double input_RMT;
    private final Integer bge; // If RMT is used, what was the bge?

    // For Eff Mob
    private final Double input_eff_mob;

    private final Set<CEMSFragment> experimentalFragments;
    private final Integer ionization_mode; // 0 neutral, 1 positive, 2 negative
    private final Integer polarity; // 1 direct, 2 reverse
    private final Set<CEMSAnnotationsGroupByAdduct> annotationsCEMSGroupByAdduct;
    private final Set<CEMSAnnotationFragment> annotationsFragmentsCEMS;

    /**
     *
     * @param input_mz
     * @param input_MT
     * @param input_RMT
     * @param eff_mob
     * @param bge
     * @param experimentalFragments
     * @param ionization_mode
     * @param annotationsCEMSGroupByAdduct
     * @param polarity
     * @param annotationsFragmentsCEMS
     */
    public CEMSFeature(Double input_mz, Double eff_mob, Double input_MT, Double input_RMT, Integer bge,
            Set<CEMSFragment> experimentalFragments, Integer ionization_mode, Integer polarity,
            Set<CEMSAnnotationsGroupByAdduct> annotationsCEMSGroupByAdduct,
            Set<CEMSAnnotationFragment> annotationsFragmentsCEMS) {
        this.input_mz = input_mz;
        this.input_MT = input_MT;
        this.input_RMT = input_RMT;
        this.bge = bge;
        this.input_eff_mob = eff_mob;
        if (experimentalFragments == null) {
            this.experimentalFragments = new TreeSet<CEMSFragment>();
        } else {
            this.experimentalFragments = experimentalFragments;
        }
        this.ionization_mode = ionization_mode;
        this.polarity = polarity;
        if (annotationsCEMSGroupByAdduct == null) {
            this.annotationsCEMSGroupByAdduct = new TreeSet<>();
        } else {
            this.annotationsCEMSGroupByAdduct = annotationsCEMSGroupByAdduct;
        }
        if (annotationsFragmentsCEMS == null) {
            this.annotationsFragmentsCEMS = new TreeSet<>();
        } else {
            this.annotationsFragmentsCEMS = annotationsFragmentsCEMS;
        }
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
        if (!Objects.equals(this.input_mz, other.input_mz)) {
            return false;
        }
        if (this.input_eff_mob != null && other.input_eff_mob != null) {
            return Objects.equals(this.input_eff_mob, other.input_eff_mob);
        }
        if (this.input_RMT != null && other.input_RMT != null) {
            if (Objects.equals(this.bge, other.bge)) {
                return Objects.equals(this.input_RMT, other.input_RMT);
            }
        }
        if (this.input_MT != null && other.input_MT != null) {
            return Objects.equals(this.input_MT, other.input_MT);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.input_mz);
        hash += 97 * hash + Objects.hashCode(this.input_RMT);
        return hash;
    }

    @Override
    public String toString() {
        return "CEMSFeature{" + "exp_mz=" + input_mz + ", exp_MT=" + input_MT
                + ", exp_RMT=" + input_RMT + ", eff_mob=" + this.input_eff_mob
                + ", ionization_mode=" + ionization_mode
                + ", annotationsCEMSGroupByAdduct=" + annotationsCEMSGroupByAdduct
                + ", annotationsFragmentsCEMS=" + annotationsFragmentsCEMS + '}';
    }

    public Double getInput_mz() {
        return input_mz;
    }

    public Double getInput_MT() {
        return input_MT;
    }

    public Double getInput_RMT() {
        return input_RMT;
    }

    public Integer getBge() {
        return bge;
    }

    public Double getInput_eff_mob() {
        return input_eff_mob;
    }

    public Set<CEMSFragment> getExperimentalFragments() {
        return experimentalFragments;
    }

    public Integer getIonization_mode() {
        return ionization_mode;
    }

    public Integer getPolarity() {
        return polarity;
    }

    public Set<CEMSAnnotationsGroupByAdduct> getAnnotationsCEMSGroupByAdduct() {
        return annotationsCEMSGroupByAdduct;
    }

    public Set<CEMSAnnotationFragment> getAnnotationsFragmentsCEMS() {
        return this.annotationsFragmentsCEMS;
    }

    public void addAnnotationsGroupByAdduct(CEMSAnnotationsGroupByAdduct CEMSAnnotationForOneAdduct) {
        this.annotationsCEMSGroupByAdduct.add(CEMSAnnotationForOneAdduct);
    }

    public void removeAnnotationsGroupByAdduct(CEMSAnnotationsGroupByAdduct CEMSAnnotationForOneAdduct) {
        this.annotationsCEMSGroupByAdduct.remove(CEMSAnnotationForOneAdduct);
    }

    public Integer getTotalNumAnnotations() {
        return this.getNumAnnotationsGrouped() + getNumAnnotationsFragments();
    }

    public Integer getNumAnnotationsGrouped() {
        Integer numAnnotationsPrecursorIons = 0;
        if (this.annotationsCEMSGroupByAdduct != null) {
            numAnnotationsPrecursorIons = this.annotationsCEMSGroupByAdduct.size();
        }
        return numAnnotationsPrecursorIons;
    }

    public Integer getNumAnnotationsFragments() {
        Integer numAnnotationsFragments = 0;
        if (this.annotationsFragmentsCEMS != null) {
            numAnnotationsFragments = this.annotationsFragmentsCEMS.size();
        }
        return numAnnotationsFragments;
    }

    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Compounds found for m/z: " + this.input_mz + "";
        if (this.input_RMT != null && this.input_RMT > 0d) {
            titleMessage = titleMessage + " and RMT: " + this.input_RMT;
        }

        if (this.input_eff_mob != null) {
            titleMessage = titleMessage + " and eff Mob: " + this.input_eff_mob;
        }

        boolean allAnnotationGroupNotEmpty = false;
        Integer sizeAnnotationGroup = 0;
        for (CEMSAnnotationsGroupByAdduct cemsAannotationsGroupedByAdductPartial : this.annotationsCEMSGroupByAdduct) {
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
        return titleMessage;
    }

    /**
     * Get title for the presentation
     *
     * @param adduct
     * @param numAnnotations
     * @return
     */
    public String getTitleMessage(String adduct, Integer numAnnotations) {
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

        String titleMessage = "No compounds found for adduct: " + adduct;
        return titleMessage;
    }

    /**
     * Get the empty title when there are no compounds for a group of
     * annotations
     *
     * @param adduct
     * @return
     */
    public String getEmptyAnnotationMessage() {

        String titleMessage = "No compounds found for m/z: " + input_mz;
        if (this.input_RMT != null && this.input_RMT > 0d) {
            titleMessage = titleMessage + " and RMT: " + this.input_RMT;
        }
        if (this.input_MT != null && this.input_MT > 0d) {
            titleMessage = titleMessage + " and MT: " + this.input_MT;
        }
        if (this.input_eff_mob != null) {
            titleMessage = titleMessage + " and eff Mob: " + this.input_eff_mob;
        }
        return titleMessage;
    }

    /**
     * Get title for the fragments found
     *
     * @return
     */
    public String getTitleMessageForFragments() {
        String titleMessage;
        if (this.annotationsFragmentsCEMS == null || this.annotationsFragmentsCEMS.isEmpty()) {
            titleMessage = getEmptyAnnotationMessageForFragments();
        } else {
            titleMessage = "Fragments found for mz: " + this.input_mz;
            if (this.input_RMT != null && this.input_RMT > 0d) {
                titleMessage = titleMessage + ", RMT: " + this.input_RMT;
            }
            if (this.input_MT != null && this.input_MT > 0d) {
                titleMessage = titleMessage + " and MT: " + this.input_MT;
            }
            if (this.input_eff_mob != null) {
                titleMessage = titleMessage + ", eff Mob: " + this.input_eff_mob;
            }
            titleMessage = titleMessage + " -> " + this.annotationsFragmentsCEMS.size();
        }
        return titleMessage;
    }

    /**
     * Get the empty title when there are no fragments for the MZ and the RMT
     *
     * @return
     */
    public String getEmptyAnnotationMessageForFragments() {

        String titleMessage = "No fragments found for mz: " + this.input_mz;
        if (this.input_RMT != null && this.input_RMT > 0d) {
            titleMessage = titleMessage + ", RMT: " + this.input_RMT;
        }
        if (this.input_MT != null && this.input_MT > 0d) {
            titleMessage = titleMessage + " and MT: " + this.input_MT;
        }
        if (this.input_eff_mob != null) {
            titleMessage = titleMessage + ", eff Mob: " + this.input_eff_mob;
        }

        return titleMessage;
    }

    @Override
    public int compareTo(CEMSFeature o) {
        int result = (int) Math.round(this.input_mz) - (int) Math.round(o.getInput_mz());
        if (result == 0) {
            if (!Objects.equals(this.ionization_mode, o.getIonization_mode())) {
                result = this.ionization_mode - o.getIonization_mode();
            } else if (!Objects.equals(this.polarity, o.getPolarity())) {
                result = this.polarity - o.getPolarity();
            } else if (this.input_eff_mob != null && o.getInput_eff_mob() != null) {
                result = (int) Math.round(this.input_eff_mob) - (int) Math.round(o.getInput_eff_mob());
                if (result == 0 && this.input_RMT != null && o.getInput_RMT() != null) {
                    result = (int) Math.round(this.input_RMT) - (int) Math.round(o.getInput_RMT());
                    if (result == 0 && this.input_MT != null && o.getInput_MT() != null) {
                        result = (int) Math.round(this.input_MT) - (int) Math.round(o.getInput_MT());
                    }
                }
            } else if (o.getInput_eff_mob() == null) {
                result = -1;
            } else if (o.getInput_eff_mob() == null) {
                result = 1;
            } else if (this.input_RMT != null && o.getInput_RMT() != null) {
                result = (int) Math.round(this.input_RMT) - (int) Math.round(o.getInput_RMT());
                if (result == 0 && this.input_MT != null && o.getInput_MT() != null) {
                    result = this.bge - o.getBge();
                    if (result == 0 && this.input_MT != null && o.getInput_MT() != null) {
                        result = (int) Math.round(this.input_MT) - (int) Math.round(o.getInput_MT());
                    }
                }
            } else if (this.input_MT != null && o.getInput_MT() != null) {
                result = (int) Math.round(this.input_MT) - (int) Math.round(o.getInput_MT());
            }
        }
        return result;
    }

}
