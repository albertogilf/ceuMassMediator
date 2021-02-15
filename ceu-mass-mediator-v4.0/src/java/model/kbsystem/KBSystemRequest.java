/*
 * KBSystemRequest.java
 *
 * Created on 18-abr-2019, 18:41:55
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */

package model.kbsystem;

import java.util.List;

/**
 * Request model of the KBSystem
 * 
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1 18-abr-2019
 * 
 * @author Alberto Gil de la Fuente
 */
public class KBSystemRequest {

    final int identifier;
    final String hasIonizationMode;
    final List<KBSystemPutativeAnnotation> hasPutativeAnnotation;
    /**
     * Creates a new instance of KBSystemRequest
     * @param identifier
     * @param ionMode
     * @param putativeAnnotations
     */
    public KBSystemRequest (int identifier, String ionMode, List <KBSystemPutativeAnnotation> putativeAnnotations)
    {
        this.identifier=identifier;
        this.hasIonizationMode=ionMode;
        this.hasPutativeAnnotation=putativeAnnotations;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getHasIonizationMode() {
        return hasIonizationMode;
    }

    public List<KBSystemPutativeAnnotation> getHasPutativeAnnotation() {
        return hasPutativeAnnotation;
    }
    
    @Override
    public String toString() {
        return "id: " + this.identifier + ", ionMode: " + this.hasIonizationMode 
                + ", Annotations: " + this.hasPutativeAnnotation;
    }
}
