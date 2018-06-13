/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utilities.AdductProcessing;
import static utilities.AdductsLists.MAPMZNEGATIVEADDUCTS;
import static utilities.AdductsLists.MAPMZPOSITIVEADDUCTS;

/**
 * Feature. A feature is a tuple of mz, retention time and composite spectra. A
 * feature can represent several compounds.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Feature {

    // Contains a set of possible compounds according to the given mz and rt
    private final List<CompoundsLCMSGroupByAdduct> annotationsGroupedByAdduct;

    private final double EM;
    private final double RT;
    private final Map<Double, Integer> CS;
    private boolean isSignificativeFeature;
    private boolean isAdductAutoDetected;
    private String adductAutoDetected;
    private final int ionization_mode; // 0 neutral, 1 positive, 2 negative

    private boolean possibleFragment;
    private List<CompoundLCMS> possibleParentCompounds = new LinkedList<>();

    /*
    Commented. We do not want to create any feature if it is not explicited 
    indicated if it is significant or not.
    /*
    public Feature(double EM,  int ionizationMode) {
        this(EM, 0d, new LinkedHashMap<Double,Double>(), false, "",
                true, new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);
    }
     */
    /**
     *
     * @param EM
     * @param isSignificativeFeature
     * @param ionizationMode
     */
    public Feature(double EM, boolean isSignificativeFeature, int ionizationMode) {
        this(EM, 0d, new LinkedHashMap<Double, Integer>(), false, "",
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);

    }

    /**
     *
     * @param EM
     * @param RT
     * @param isSignificativeFeature
     * @param ionizationMode
     */
    public Feature(double EM, double RT, boolean isSignificativeFeature, int ionizationMode) {
        this(EM, RT, new LinkedHashMap<Double, Integer>(), false, "", isSignificativeFeature,
                new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);
    }

    /**
     *
     * @param EM
     * @param CS
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificativeFeature
     * @param ionizationMode
     */
    public Feature(double EM, Map<Double, Integer> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature, int ionizationMode) {
        this(EM, 0d, CS, isAdductAutoDetected, adductAutoDetected,
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);
    }

    /**
     *
     * @param EM
     * @param RT
     * @param CS
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificativeFeature
     * @param ionizationMode
     */
    public Feature(double EM, double RT, Map<Double, Integer> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature, int ionizationMode) {
        this(EM, RT, CS, isAdductAutoDetected, adductAutoDetected,
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), ionizationMode);
    }

    /**
     *
     * @param EM
     * @param RT
     * @param CS
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificativeFeature
     * @param annotations
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(double EM, double RT, Map<Double, Integer> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature,
            List<CompoundsLCMSGroupByAdduct> annotations, int ionizationMode) {
        this.EM = EM;
        this.RT = RT;
        this.CS = CS;
        this.ionization_mode = ionizationMode;

        // USE THE FUNCTION IN EXPERIMENT TO TRY TO DETECT THE ADDUCT
        // Check adduct 
        if (checkAdductAutoDetected(isAdductAutoDetected, adductAutoDetected, ionizationMode)) {
            this.isAdductAutoDetected = isAdductAutoDetected;
            this.adductAutoDetected = adductAutoDetected;
        } else {
            this.isAdductAutoDetected = false;
            this.adductAutoDetected = "";
        }
        this.isSignificativeFeature = isSignificativeFeature;
        this.annotationsGroupedByAdduct = annotations;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.EM) ^ (Double.doubleToLongBits(this.EM) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.RT) ^ (Double.doubleToLongBits(this.RT) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if (Double.doubleToLongBits(this.EM) != Double.doubleToLongBits(other.EM)) {
            return false;
        }
        if (Double.doubleToLongBits(this.RT) != Double.doubleToLongBits(other.RT)) {
            return false;
        }
        return true;
    }

    public double getEM() {
        return EM;
    }

    public double getRT() {
        return RT;
    }

    public Map<Double, Integer> getCS() {
        return CS;
    }

    public boolean isIsSignificativeFeature() {
        return isSignificativeFeature;
    }

    public void setIsSignificativeFeature(boolean isSignificativeFeature) {
        this.isSignificativeFeature = isSignificativeFeature;
    }

    public boolean isIsAdductAutoDetected() {
        return isAdductAutoDetected;
    }

    public void setIsAdductAutoDetected(boolean isAdductAutoDetected) {
        this.isAdductAutoDetected = isAdductAutoDetected;
    }

    public String getAdductAutoDetected() {
        return adductAutoDetected;
    }

    public void setAdductAutoDetected(String adductAutoDetected) {
        this.adductAutoDetected = adductAutoDetected;
    }

    public List<CompoundsLCMSGroupByAdduct> getAnnotationsGroupedByAdduct() {
        return annotationsGroupedByAdduct;
    }

    public Integer getNumAnnotationsGroupedByAdduct() {
        return this.annotationsGroupedByAdduct == null ? 0 : annotationsGroupedByAdduct.size();
    }

    public boolean isPossibleFragment() {
        return possibleFragment;
    }

    public void setPossibleFragment(boolean possibleFragment) {
        this.possibleFragment = possibleFragment;
    }

    public List<CompoundLCMS> getPossibleParentCompounds() {
        return possibleParentCompounds;
    }

    public void setPossibleParentCompounds(List<CompoundLCMS> possibleParentCompounds) {
        this.possibleParentCompounds = possibleParentCompounds;
    }

    public int getNumberPossibleParentCompounds() {
        return this.possibleParentCompounds.size();
    }

    /**
     * 0 for negative ionization Mode, 1 for positive ionization Mode
     *
     * @return
     */
    public int getIonization_mode() {
        return ionization_mode;
    }

    private boolean checkAdductAutoDetected(boolean isAdductAutoDetected, String adductAutoDetected,
            int ionizationMode) {
        if ((isAdductAutoDetected && adductAutoDetected.equals(""))
                || (!isAdductAutoDetected && !adductAutoDetected.equals(""))) {
            System.out.println("ERROR DETECTING ADDUCT");
            return false;
        } else if (isAdductAutoDetected) {
            if (ionizationMode == 0) {
                if (MAPMZNEGATIVEADDUCTS.containsKey(adductAutoDetected)) {
                    return true;
                }
            } else if (ionizationMode == 1) {
                if (MAPMZPOSITIVEADDUCTS.containsKey(adductAutoDetected)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * delete the annotation within the feature. TODO Delete Annotation in
     * Database when calling this method. If the database delete works, then
     * deleteAnnotation.
     *
     * @param annotation
     */
    public void deleteAnnotation(CompoundLCMS annotation) {
        this.annotationsGroupedByAdduct.forEach((annotationsGroupedByAdductPartial) -> {
            annotationsGroupedByAdductPartial.deleteAnnotation(annotation);
        });
    }

    /**
     * Search the compound_id in the annotations of the feature and if it is
     * present, it deletes it.
     *
     * @param compoundId
     */
    public void deleteAnnotation(Integer compoundId) {
        outerloop:
        for (CompoundsLCMSGroupByAdduct annotationsGroupedByAdductPartial : this.annotationsGroupedByAdduct) {
            for (CompoundLCMS annotation : annotationsGroupedByAdductPartial.getCompounds()) {
                if (compoundId == annotation.getCompound_id()) {
                    annotationsGroupedByAdductPartial.deleteAnnotation(annotation);
                    break outerloop;
                }
            }
        }
        // It is not possible than the same annotation corresponds to two different adducts
        // within the same feature. 
    }

    /**
     * delete all the annotations corresponding to the adduct @param adduct
     *
     * @param adduct
     */
    public void deleteAnnotationsFromOneAdduct(String adduct) {
        for (CompoundsLCMSGroupByAdduct annotationsGroupedByAdductPartial : this.annotationsGroupedByAdduct) {
            if (annotationsGroupedByAdductPartial.getAdduct() == null ? adduct == null : annotationsGroupedByAdductPartial.getAdduct().equals(adduct)) {
                // this.annotationsGroupedByAdduct.remove(annotationsGroupedByAdductPartial);
                annotationsGroupedByAdductPartial.deleteAllAnnotations();
            }
        }
    }

    /**
     * Add the annotation of the compound_id corresponding to the adduct adduct.
     *
     * @param compound_id
     * @param adduct
     */
    public void addAnnotation(int compound_id, String adduct) {
        // TODO A METHOD TO SEARCH THE COMPOUND IN THE DATABASE,
        //DOUBT, here or at MSFacade?

        // Then check if the annotation is already added in the list of annotations.
        // Then, if it is not added, add the annotation
        CompoundLCMS annotation;
        // this.addAnnotation(annotation,adduct);
        // CREATE A COMPOUNDLCMS WITH THE DATA FROM THE DATABASE (CHECK IT) 
        // AND FROM THE FEATURE (EM, RT, CS, isSignificativeFeature)
        // Then use the method 

    }

    /**
     * Add the annotation annotation corresponding to the adduct adduct.
     *
     * @param annotation
     * @param adduct
     */
    public void addAnnotation(CompoundLCMS annotation, String adduct) {
        // Search the CompoundsLCMSGroupByAdduct corresponding to the adduct
        // Then, add the annotation to the corresponding CompoundsLCMSGroupByAdduct. 
        // USE THE METHOD addAnnotation(CompoundLCMS annotation) from CompoundsLCMSGroupByAdduct
        // If the adduct is not present, then create the group and add the group of adducts
        boolean findAdduct = false;
        for (CompoundsLCMSGroupByAdduct annotationsGroupedByAdductPartial : this.annotationsGroupedByAdduct) {
            if (annotationsGroupedByAdductPartial.getAdduct() == null ? adduct == null : annotationsGroupedByAdductPartial.getAdduct().equals(adduct)) {
                annotationsGroupedByAdductPartial.addAnnotation(annotation);
                findAdduct = true;
            }
        }
        if (!findAdduct) {
            List<CompoundLCMS> annotationsForAdduct = new LinkedList<>();
            annotationsForAdduct.add(annotation);
            CompoundsLCMSGroupByAdduct newGroupCompoundsLCMS = new CompoundsLCMSGroupByAdduct(
                    EM, RT, CS, adduct, annotationsForAdduct);
            this.annotationsGroupedByAdduct.add(newGroupCompoundsLCMS);
        }
    }

    /**
     * Add a Group of annotations grouped by adduct
     *
     * @param annotationsGroupByAdduct
     */
    public void addAnnotationGroupByAdduct(CompoundsLCMSGroupByAdduct annotationsGroupByAdduct) {
        this.annotationsGroupedByAdduct.add(annotationsGroupByAdduct);
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Metabolites found for mass: " + this.EM + "";
        if (this.RT > 0d) {
            titleMessage = titleMessage + " and retention time: " + this.RT;
        }

        boolean allAnnotationGroupNotEmpty = false;
        int sizeAnnotationGroup = 0;
        for (CompoundsLCMSGroupByAdduct annotationsGroupedByAdductPartial : this.annotationsGroupedByAdduct) {
            if (annotationsGroupedByAdductPartial != null && !annotationsGroupedByAdductPartial.isEmpty()) {
                allAnnotationGroupNotEmpty = true;
                sizeAnnotationGroup = sizeAnnotationGroup
                        + annotationsGroupedByAdductPartial.getNumberAnnotations();
            }
        }

        if (!allAnnotationGroupNotEmpty) {
            titleMessage = "No " + titleMessage;
        } else {
            titleMessage = titleMessage + " -> " + sizeAnnotationGroup;
        }

        if (isAdductAutoDetected) {
            titleMessage = titleMessage + " Adduct detected: " + adductAutoDetected;
        }
        return titleMessage;
    }

    /**
     * Get title for the presentation of the possible parents
     *
     * @return
     */
    public String getTitleMessageForParents() {
        String titleMessageForParents;
        titleMessageForParents = "Possible parent ions found for mass: " + this.EM + "";
        if (this.RT > 0d) {
            titleMessageForParents = titleMessageForParents + " and retention time: " + this.RT;
        }

        if (!possibleFragment) {
            titleMessageForParents = "No " + titleMessageForParents;
        } else {
            titleMessageForParents = titleMessageForParents + " -> " + this.possibleParentCompounds.size();
        }
        return titleMessageForParents;
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public String getEmptyAnnotationMessage() {
        String titleMessage;
        if (this.isAdductAutoDetected) {
            titleMessage = "No compounds searched. The adduct detected was: " + this.adductAutoDetected + "";
        } else {
            titleMessage = "No compounds found";
        }

        return titleMessage;
    }

    @Override
    public String toString() {
        Iterator it = this.annotationsGroupedByAdduct.iterator();
        String toreturn = "";
        String aux;
        while (it.hasNext()) {
            aux = ((CompoundsLCMSGroupByAdduct) it.next()).toString();
            if (!aux.equals("")) {
                toreturn += aux + "\n";
            } else {
                toreturn += "";
            }
        }
        return "    FEATURE WITH EM: " + this.EM + " RT: " + this.RT + "\n" + toreturn;
    }

    public boolean isThereTheoreticalCompounds() {
        return annotationsGroupedByAdduct.size() > 0;
    }

    /**
     * Get the hypothetical neutral mass
     *
     * @return
     */
    public double getHypotheticalNeutralMass() {
        String ionMode = AdductProcessing.getStringIonizationModeFromInt(this.ionization_mode);
        if (this.isAdductAutoDetected) {

            double hypoteticalNeutralMass = AdductProcessing.getMassToSearch(this.EM, this.adductAutoDetected, ionMode);
            return hypoteticalNeutralMass;
        } else {
            return 0d;
        }
    }

}
