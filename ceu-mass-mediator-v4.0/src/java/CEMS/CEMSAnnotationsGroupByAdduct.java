/*
 * CEMSAnnotationsGroupByAdduct.java
 *
 * Created on 28-may-2019, 23:38:35
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import static utilities.AdductsLists.MAPMZPOSITIVEADDUCTS;

/**
 * Class that contains a group of CEMSCompounds and the corresponding adduct
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 28-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSAnnotationsGroupByAdduct implements Serializable, Comparable<CEMSAnnotationsGroupByAdduct> {

    private static final long serialVersionUID = 1L;

    private final String adduct;
    private final Set<CEMSAnnotation> annotationsCEMS;

    public CEMSAnnotationsGroupByAdduct(String adduct, Set<CEMSAnnotation> annotationsCEMS) {
        this.adduct = adduct;
        this.annotationsCEMS = annotationsCEMS;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.adduct);
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
        final CEMSAnnotationsGroupByAdduct other = (CEMSAnnotationsGroupByAdduct) obj;
        if (!Objects.equals(this.adduct, other.adduct)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CEMSCompoundsGroupByAdduct{" + "adduct=" + adduct + ", annotationsCEMS=" + annotationsCEMS + '}';
    }

    public String getAdduct() {
        return adduct;
    }

    public Set<CEMSAnnotation> getAnnotationsCEMS() {
        return annotationsCEMS;
    }

    public boolean isEmpty() {
        return this.annotationsCEMS == null ? true : this.annotationsCEMS.isEmpty();
    }

    public void addAnnotation(CEMSAnnotation cemsAnnotation) {
        this.annotationsCEMS.add(cemsAnnotation);
    }

    public void removeAnnotation(CEMSAnnotation cemsAnnotation) {
        this.annotationsCEMS.remove(cemsAnnotation);
    }

    public int getNumberAnnotations() {
        if (this.annotationsCEMS != null) {
            return annotationsCEMS.size();
        } else {
            return 0;
        }
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Adduct: " + this.adduct;

        if (getNumberAnnotations() == 0) {
            titleMessage = "No results for " + titleMessage;
        } else {
            titleMessage = titleMessage + " -> " + getNumberAnnotations();
        }
        return titleMessage;
    }

    @Override
    public int compareTo(CEMSAnnotationsGroupByAdduct o) {
        
        
        if(MAPMZPOSITIVEADDUCTS.containsKey(this.adduct) && MAPMZPOSITIVEADDUCTS.containsKey(o.adduct))
        {
            // TODO ORDER OF CEMSAnnotations
            // CREATE A MAP WITH THE ORDER OF ELEMENTS
        }
        int result = this.adduct.compareTo(o.getAdduct());
        return result;
    }

}
