package services.rest.api.response;

import com.google.gson.Gson;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class SpectralQualityControllerResults {

    private double overallScore;
    private double intensity;
    private double noise;
    private double scans;
    private double coelution;
    private double crosstalk;

    public SpectralQualityControllerResults(double overallScore,
                                            double intensity, double noise,
                                            double scans, double coelution,
                                            double crosstalk) {
        this.overallScore = overallScore;
        this.intensity = intensity;
        this.noise = noise;
        this.scans = scans;
        this.coelution = coelution;
        this.crosstalk = crosstalk;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getNoise() {
        return noise;
    }

    public void setNoise(double noise) {
        this.noise = noise;
    }

    public double getScans() {
        return scans;
    }

    public void setScans(double scans) {
        this.scans = scans;
    }

    public double getCoelution() {
        return coelution;
    }

    public void setCoelution(double coelution) {
        this.coelution = coelution;
    }

    public double getCrosstalk() {
        return crosstalk;
    }

    public void setCrosstalk(double crosstalk) {
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
        SpectralQualityControllerResults demoData = new SpectralQualityControllerResults(
                4.667,
                1,
                0.667,
                1,
                1,
                1);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Spectral Quality Controller - Results");
        System.out.println(result);

        SpectralQualityControllerResults received = gson.fromJson(result, SpectralQualityControllerResults.class);

        assert (gson.toJson(received).equals(result));

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
