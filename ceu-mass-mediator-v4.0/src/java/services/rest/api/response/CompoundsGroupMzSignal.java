package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;
import services.rest.api.request.Signal;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundsGroupMzSignal extends CompoundsGroupMz {


    private List<Signal> input_signals;



    public CompoundsGroupMzSignal(double input_mz, List<Signal> input_signals) {
        super(input_mz);
        this.input_signals = input_signals;
    }



    public CompoundsGroupMzSignal(double input_mz, double input_rmt) {
        this(input_mz, new ArrayList());
    }



    public void addSignal(Signal signal) {
        this.input_signals.add(signal);
    }



    public void addSignal(double mz, double intensity) {
        this.input_signals.add((new Signal(mz, intensity)));
    }

}

