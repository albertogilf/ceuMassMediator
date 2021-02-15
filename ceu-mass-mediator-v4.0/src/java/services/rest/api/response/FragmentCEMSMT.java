package services.rest.api.response;

import CEMS.CEMSAnnotationFragment;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentCEMSMT extends FragmentCEMS {


    public FragmentCEMSMT(CEMSAnnotationFragment fragment) {
        super(fragment);
        this.standardRMT = fragment.getMT();
        this.errorRMT = fragment.getErrorMT();
    }

}

