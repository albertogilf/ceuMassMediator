package services.rest.api.response;

import CEMS.CEMSAnnotation;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSExtEff extends AnnotationCEMSExt {


    private double effmob;
    private int error_effmob;



    public AnnotationCEMSExtEff(CEMSAnnotation annotation) {
        super(annotation);
        this.effmob = annotation.getEffMob();
        this.error_effmob = annotation.getErrorEffMob();
    }

}

