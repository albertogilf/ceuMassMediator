package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;
import services.rest.api.request.Signal;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMzSignalEff extends CompoundsGroupMzSignal {


    private double input_effmob;



    public CompoundsGroupMzSignalEff(double input_mz, double input_effmob) {
        super(input_mz, new ArrayList());
        this.input_effmob = input_effmob;
    }



    public CompoundsGroupMzSignalEff(double input_mz, List<Signal> input_signals, double input_effmob) {
        super(input_mz, input_signals);
        this.input_effmob = input_effmob;
    }

}

