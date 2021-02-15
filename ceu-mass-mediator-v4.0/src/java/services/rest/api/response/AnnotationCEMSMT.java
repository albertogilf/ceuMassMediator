package services.rest.api.response;

import CEMS.CEMSAnnotation;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSMT extends AnnotationCEMS {


    private double predicted_mt;
    private int error_mt;



    public AnnotationCEMSMT(CEMSAnnotation annotation) {
        super(annotation);
        this.predicted_mt = annotation.getMT();
        this.error_mt = annotation.getErrorMT();
    }

}

