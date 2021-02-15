package services.rest.api.response;

import CEMS.CEMSAnnotationFragment;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentCEMSEff extends FragmentCEMS {


    public FragmentCEMSEff(CEMSAnnotationFragment fragment) {
        super(fragment);
        this.standardRMT = fragment.getEffMob();
        this.errorRMT = fragment.getErrorEffMob();
    }

}

