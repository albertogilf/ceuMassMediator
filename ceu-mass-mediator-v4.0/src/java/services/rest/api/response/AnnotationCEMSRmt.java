package services.rest.api.response;

import CEMS.CEMSAnnotation;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSRmt extends AnnotationCEMS {


    private double rmt;
    private int error_rmt;



    public AnnotationCEMSRmt(CEMSAnnotation annotation) {
        super(annotation);
        this.rmt = annotation.getRMT();
        this.error_rmt = annotation.getErrorRMT();
    }

}

