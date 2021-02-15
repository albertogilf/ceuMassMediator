package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentsGroupMzEff extends FragmentsGroupMz {


    private double input_effmob;



    public FragmentsGroupMzEff(double input_mz, double input_effmob) {
        super(input_mz);
        this.input_effmob = input_effmob;
    }

}

