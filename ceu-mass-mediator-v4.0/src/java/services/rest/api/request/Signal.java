package services.rest.api.request;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class Signal {


    private double mz;
    private double intensity;



    public Signal(double mz, double intensity) {
        this.mz = mz;
        this.intensity = intensity;
    }



    public double getMz() {
        return mz;
    }



    public double getIntensity() {
        return intensity;
    }

}

