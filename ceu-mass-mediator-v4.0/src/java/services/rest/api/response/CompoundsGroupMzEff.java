package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMzEff extends CompoundsGroupMz {


    private double input_effmob;



    public CompoundsGroupMzEff(double input_mz, double input_effmob) {
        super(input_mz);
        this.input_effmob = input_effmob;
    }

}

