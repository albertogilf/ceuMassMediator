package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;
import services.rest.api.request.Signal;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMzSignalRmt extends CompoundsGroupMzSignal {


    private double input_rmt;



    public CompoundsGroupMzSignalRmt(double input_mz, double input_rmt) {
        super(input_mz, new ArrayList());
        this.input_rmt = input_rmt;
    }



    public CompoundsGroupMzSignalRmt(double input_mz, List<Signal> input_signals, double input_rmt) {
        super(input_mz, input_signals);
        this.input_rmt = input_rmt;
    }

}

