package services.rest.api.response;

import CEMS.CEMSAnnotation;
import java.util.List;
import java.util.stream.Collectors;
import services.rest.api.request.Signal;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMSExt extends AnnotationCEMS {


    private List<Signal> fragmentsFound;
    private List<Signal> ExperimentalfragmentsNotFound;
    private List<Signal> TheoreticalfragmentsNotFound;



    public AnnotationCEMSExt(CEMSAnnotation annotation) {
        super(annotation);
        this.fragmentsFound = annotation.getFragmentsFound().stream().map(element -> new Signal(element.getMz(), element.getIntensity())).collect(Collectors.toList());
        this.ExperimentalfragmentsNotFound = annotation.getExperimentalFragmentsNotFound().stream().map(element -> new Signal(element.getMz(), element.getIntensity())).collect(Collectors.toList());
        this.TheoreticalfragmentsNotFound = annotation.getTheoreticalFragmentsNotFound().stream().map(element -> new Signal(element.getMz(), element.getIntensity())).collect(Collectors.toList());
    }

}

