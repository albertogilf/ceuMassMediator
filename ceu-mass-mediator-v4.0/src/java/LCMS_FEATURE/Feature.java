/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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

    private final Double EM;
    private final Double massIntroduced;
    private final Double RT;
    private final Map<Double, Double> CS;
    private boolean isSignificativeFeature;
    private boolean isAdductAutoDetected;
    private String adductAutoDetected;
    private final int EMMode; // 0 neutral (protonated or deprotonated), 1 m/z
    private final int ionization_mode; // 0 neutral, 1 positive, 2 negative

    private boolean possibleFragment;

    private Map<String, List<CompoundLCMS>> possibleParentCompounds;
    // private List<CompoundLCMS> possibleParentCompounds = new LinkedList<>();

    /**
     *
     * @param EM
     * @param isSignificativeFeature
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(Double EM, boolean isSignificativeFeature, int EMMode, int ionizationMode) {
        this(EM, 0d, new TreeMap<Double, Double>(), false, "",
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), EMMode, ionizationMode);
    }

    /**
     *
     * @param EM
     * @param RT
     * @param isSignificativeFeature
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(Double EM, Double RT, boolean isSignificativeFeature, 
            int EMMode, int ionizationMode) {
        this(EM, RT, new TreeMap<Double, Double>(), false, "", isSignificativeFeature,
                new LinkedList<CompoundsLCMSGroupByAdduct>(), EMMode, ionizationMode);
    }

    /**
     *
     * @param EM
     * @param CS
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificativeFeature
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(Double EM, Map<Double, Double> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature, int EMMode, int ionizationMode) {
        this(EM, 0d, CS, isAdductAutoDetected, adductAutoDetected,
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), EMMode, ionizationMode);
    }

    /**
     *
     * @param EM
     * @param RT
     * @param CS
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificativeFeature
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(Double EM, Double RT, Map<Double, Double> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature, int EMMode, int ionizationMode) {
        this(EM, RT, CS, isAdductAutoDetected, adductAutoDetected,
                isSignificativeFeature, new LinkedList<CompoundsLCMSGroupByAdduct>(), EMMode, ionizationMode);
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
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     */
    public Feature(Double EM, Double RT, Map<Double, Double> CS, boolean isAdductAutoDetected,
            String adductAutoDetected, boolean isSignificativeFeature,
            List<CompoundsLCMSGroupByAdduct> annotations, int EMMode, int ionizationMode) {
        this.possibleParentCompounds = new LinkedHashMap<>();
        this.EM = EM;
        this.RT = RT;
        this.CS = CS;
        this.ionization_mode = ionizationMode;
        this.EMMode = EMMode;
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
        if (this.EMMode == 0) {
            switch (this.ionization_mode) {
                case 1:
                    this.massIntroduced = this.EM - utilities.Constants.PROTON_WEIGHT;
                    break;
                case 2:
                    this.massIntroduced = this.EM + utilities.Constants.PROTON_WEIGHT;
                    break;
                default:
                    this.massIntroduced = this.EM;
                    break;
            }
        } else {
            this.massIntroduced = this.EM;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.EM) ^ (Double.doubleToLongBits(this.EM) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.RT) ^ (Double.doubleToLongBits(this.RT) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj
    ) {
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
        if (!Objects.equals(this.EM, other.EM)) {
            return false;
        }
        if (!Objects.equals(this.RT, other.RT)) {
            return false;
        }
        return true;
    }

    public Double getEM() {
        return EM;
    }

    public Double getRT() {
        return RT;
    }

    public Map<Double, Double> getCS() {
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

    public Map<String, List<CompoundLCMS>> getPossibleParentCompounds() {
        return possibleParentCompounds;
    }

    public void setPossibleParentCompounds(Map<String, List<CompoundLCMS>> possibleParentCompounds) {
        this.possibleParentCompounds = possibleParentCompounds;
    }

    public void addPossibleParentCompounds(String adduct, CompoundLCMS possibleParentCompounds) {
        List<CompoundLCMS> listCompoundParent = this.possibleParentCompounds.get(adduct);
        if (listCompoundParent == null) {
            listCompoundParent = new LinkedList();
            this.possibleParentCompounds.put(adduct, listCompoundParent);
        }
        listCompoundParent.add(possibleParentCompounds);
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
                    EM, RT, CS, adduct, this.EMMode, this.ionization_mode, annotationsForAdduct);
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
        titleMessage = "Metabolites found for m/z: " + this.massIntroduced + "";
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
     * Get title for the presentation of the possible parents
     *
     * @return
     */
    public String getTitleMessageForParents() {
        String titleMessageForParents;
        titleMessageForParents = "Possible precursor ions found for mass: " + this.massIntroduced + "";
        if (this.RT > 0d) {
            titleMessageForParents = titleMessageForParents + " and retention time: " + this.RT;
        }

        if (!possibleFragment) {
            titleMessageForParents = "No " + titleMessageForParents;
        } else {
            int numPossibleParentCompounds = 0;
            for (Map.Entry<String, List<CompoundLCMS>> entry : this.possibleParentCompounds.entrySet()) {
                numPossibleParentCompounds += entry.getValue().size();
            }

            titleMessageForParents = titleMessageForParents + " -> " + numPossibleParentCompounds;

        }
        return titleMessageForParents;
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public String getEmptyAnnotationMessage(String adduct) {
        String titleMessage;
        if (this.isAdductAutoDetected) {
            titleMessage = "No compounds searched for " + adduct + ". The adduct detected was: " + this.adductAutoDetected + "";
        } else {
            titleMessage = "No compounds found for adduct: " + adduct;
        }

        return titleMessage;
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public List<String> getAdductsPossibleFragment() {
        List<String> adductsPossibleFragment = new LinkedList(this.possibleParentCompounds.keySet());
        return adductsPossibleFragment;
    }

    public Integer getNumAdductsPossibleFragment() {
        List<String> adductsPossibleFragment = new LinkedList(this.possibleParentCompounds.keySet());
        return adductsPossibleFragment.size();
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

        if (isPossibleFragment()) {
            for (Map.Entry<String, List<CompoundLCMS>> entry : this.possibleParentCompounds.entrySet()) {
                String adduct = entry.getKey();
                toreturn += "possible fragments of adduct: " + adduct;
                toreturn += "\n";
                List<CompoundLCMS> possibleParents = entry.getValue();
                for (CompoundLCMS possibleParent : possibleParents) {
                    toreturn += possibleParent.toString() + "\n";
                }
            }
        }
        return "    FEATURE WITH EM: " + this.massIntroduced + " RT: " + this.RT + "\n" + toreturn;
    }

    public boolean isThereTheoreticalCompounds() {
        return annotationsGroupedByAdduct.size() > 0;
    }

    /**
     * Get the hypothetical neutral mass
     *
     * @return
     */
    public Double getHypotheticalNeutralMass() {
        if (this.isAdductAutoDetected) {
            Double hypoteticalNeutralMass;
            hypoteticalNeutralMass = AdductProcessing.getMassToSearch(
                    this.EM, this.adductAutoDetected, this.ionization_mode);
            return hypoteticalNeutralMass;
        } else {
            return 0d;
        }
    }

    public Double getMassIntroduced() {
        return this.massIntroduced;
    }

    public int getEMMode() {
        return this.EMMode;
    }
}
