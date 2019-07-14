/*
 * CEMSCompoundsGroupByAdduct.java
 *
 * Created on 28-may-2019, 23:38:35
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Class that contains a group of CEMSCompounds and the corresponding adduct
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 28-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSCompoundsGroupByAdduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String adduct;
    private final List<CEMSCompound> annotationsCEMS;

    public CEMSCompoundsGroupByAdduct(String adduct, List<CEMSCompound> annotationsCEMS) {
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
        final CEMSCompoundsGroupByAdduct other = (CEMSCompoundsGroupByAdduct) obj;
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

    public List<CEMSCompound> getAnnotationsCEMS() {
        return annotationsCEMS;
    }
    
    public boolean isEmpty() {
        return this.annotationsCEMS == null ? true : this.annotationsCEMS.isEmpty();
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

}
