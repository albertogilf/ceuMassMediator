package services.rest.api.response;

import CEMS.CEMSAnnotationFragment;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentCEMSRmt extends FragmentCEMS {


    public FragmentCEMSRmt(CEMSAnnotationFragment fragment) {
        super(fragment);
        this.standardRMT = fragment.getRMT();
        this.errorRMT = fragment.getErrorRMT();
    }

}

