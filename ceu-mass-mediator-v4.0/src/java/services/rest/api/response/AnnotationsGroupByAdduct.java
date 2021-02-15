package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationsGroupByAdduct {


    private String adduct;
    private List<AnnotationCEMS> compounds;



    public AnnotationsGroupByAdduct(String adduct) {
        this.adduct = adduct;
        this.compounds = new ArrayList();
    }



    public void addCompound(AnnotationCEMS annotation) {
        this.compounds.add(annotation);
    }

}

