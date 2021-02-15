package services.rest.api.response;

import CEMS.CEMSAnnotation;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSEff extends AnnotationCEMS {


    private double effmob;
    private int error_effmob;



    public AnnotationCEMSEff(CEMSAnnotation annotation) {
        super(annotation);
        this.effmob = annotation.getEffMob();
        this.error_effmob = annotation.getErrorEffMob();
    }

}

