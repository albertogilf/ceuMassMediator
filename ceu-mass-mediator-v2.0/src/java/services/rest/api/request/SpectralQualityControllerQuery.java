package services.rest.api.request;

import com.google.gson.Gson;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class SpectralQualityControllerQuery {

    private int averageSignal;
    private int intensity;
    private int noise;
    private int scans;
    private int samples;
    private CoelutionType coelution;
    private CrossTalkType crosstalk;

    public SpectralQualityControllerQuery(int averageSignal, int intensity,
                                          int noise, int scans,
                                          int samples,
                                          CoelutionType coelution,
                                          CrossTalkType crosstalk) {
        this.averageSignal = averageSignal;
        this.intensity = intensity;
        this.noise = noise;
        this.scans = scans;
        this.samples = samples;
        this.coelution = coelution;
        this.crosstalk = crosstalk;
    }

    public int getAverageSignal() {
        return averageSignal;
    }

    public void setAverageSignal(int averageSignal) {
        this.averageSignal = averageSignal;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getNoise() {
        return noise;
    }

    public void setNoise(int noise) {
        this.noise = noise;
    }

    public int getScans() {
        return scans;
    }

    public void setScans(int scans) {
        this.scans = scans;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public CoelutionType getCoelution() {
        return coelution;
    }

    public void setCoelution(CoelutionType coelution) {
        this.coelution = coelution;
    }

    public CrossTalkType getCrosstalk() {
        return crosstalk;
    }

    public void setCrosstalk(CrossTalkType crosstalk) {
        this.crosstalk = crosstalk;
    }

    /**
     * Shows a JSON message for invoking the advance batch service using the
     * predefined demo values.
     *
     * @param args No params needed
     */
    public static void main(String[] args) {

        // DEMO DATA - SPECTRAL QUALITY CONTROLLER
        SpectralQualityControllerQuery demoData = new SpectralQualityControllerQuery(
                100000,
                100000,
                10,
                7,
                1,
                CoelutionType.NOCOELUTION,
                CrossTalkType.NOCROSSTALK);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Spectral Quality Controller");
        System.out.println(result);

        SpectralQualityControllerQuery received = gson.fromJson(result, SpectralQualityControllerQuery.class);

        assert (gson.toJson(received).equals(result));

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
