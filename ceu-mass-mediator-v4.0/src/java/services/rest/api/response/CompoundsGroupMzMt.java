package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMzMt extends CompoundsGroupMz {


    private double input_mt;



    public CompoundsGroupMzMt(double input_mz, double input_mt) {
        super(input_mz);
        this.input_mt = input_mt;
    }

}

