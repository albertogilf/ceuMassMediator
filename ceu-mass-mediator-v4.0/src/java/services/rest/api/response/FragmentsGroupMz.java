package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentsGroupMz extends GroupMz {


    private List<FragmentCEMS> fragments;



    public FragmentsGroupMz(double input_mz) {
        super(input_mz);
        this.fragments = new ArrayList();
    }



    public void addFragment(FragmentCEMS fragment) {
        this.fragments.add(fragment);
    }

}

