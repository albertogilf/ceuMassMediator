package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentsGroupMzRmt extends FragmentsGroupMz {


    private double input_rmt;



    public FragmentsGroupMzRmt(double input_mz, double input_rmt) {
        super(input_mz);
        this.input_rmt = input_rmt;
    }

}

