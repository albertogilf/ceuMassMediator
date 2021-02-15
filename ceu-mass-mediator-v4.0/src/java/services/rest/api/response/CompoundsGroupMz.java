package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMz extends GroupMz {


    private List<AnnotationsGroupByAdduct> annotations;



    public CompoundsGroupMz(double input_mz) {
        super(input_mz);
        this.annotations = new ArrayList();
    }



    public void addGroupByAdduct(AnnotationsGroupByAdduct annotations) {
        this.annotations.add(annotations);
    }

}

