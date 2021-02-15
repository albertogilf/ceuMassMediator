package services.rest.api.response;

import CEMS.CEMSAnnotation;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSExtRmt extends AnnotationCEMSExt {


    private double rmt;
    private int error_rmt;



    public AnnotationCEMSExtRmt(CEMSAnnotation annotation) {
        super(annotation);
        this.rmt = annotation.getRMT();
        this.error_rmt = annotation.getErrorRMT();
    }

}

