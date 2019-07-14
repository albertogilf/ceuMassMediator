package services.rest.api.request;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class OxidationLongSearchQuery {
    private double fatty_acid_1_mz;
    private double fatty_acid_2_mz;
    private double fatty_acid_tolerance;
    private ToleranceMode fatty_acid_tolerance_mode;
    private double precursor_mz_negative;
    private double precursor_tolerance;
    private ToleranceMode precursor_tolerance_mode;
    private List<OxidationTypeLong> oxidations;

    public OxidationLongSearchQuery(double fatty_acid_1_mz, double fatty_acid_2_mz,
            double fatty_acid_tolerance, ToleranceMode fatty_acid_tolerance_mode,
            double precursor_mz_negative,
            double precursor_tolerance, ToleranceMode precursor_tolerance_mode) {
        this.fatty_acid_1_mz = fatty_acid_1_mz;
        this.fatty_acid_2_mz = fatty_acid_2_mz;
        this.fatty_acid_tolerance = fatty_acid_tolerance;
        this.fatty_acid_tolerance_mode = fatty_acid_tolerance_mode;
        this.precursor_mz_negative = precursor_mz_negative;
        this.precursor_tolerance = precursor_tolerance;
        this.precursor_tolerance_mode = precursor_tolerance_mode;
        this.oxidations = new ArrayList<OxidationTypeLong>();
    }

    public OxidationLongSearchQuery(double fatty_acid_1_mz, double fatty_acid_2_mz,
            double fatty_acid_tolerance, ToleranceMode fatty_acid_tolerance_mode,
            double precursor_mz_negative,
            double precursor_tolerance, ToleranceMode precursor_tolerance_mode,
            List<OxidationTypeLong> oxidations) {
        this(fatty_acid_1_mz, fatty_acid_2_mz,
            fatty_acid_tolerance, fatty_acid_tolerance_mode,
            precursor_mz_negative,
            precursor_tolerance, precursor_tolerance_mode);
        this.oxidations = oxidations;
    }

    public double getFatty_acid_1_mz() {
        return fatty_acid_1_mz;
    }

    public double getFatty_acid_2_mz() {
        return fatty_acid_2_mz;
    }

    public double getFatty_acid_tolerance() {
        return fatty_acid_tolerance;
    }

    public ToleranceMode getFatty_acid_tolerance_mode() {
        return fatty_acid_tolerance_mode;
    }

    public double getPrecursor_mz_negative() {
        return precursor_mz_negative;
    }
    
    public double getPrecursor_tolerance() {
        return precursor_tolerance;
    }

    public ToleranceMode getPrecursor_tolerance_mode() {
        return precursor_tolerance_mode;
    }

    public List<OxidationTypeLong> getOxidations() {
        return oxidations;
    }

    public void addOxidations(OxidationTypeLong type) {
        this.oxidations.add(type);
    }

    public static void main(String[] args) {

        OxidationLongSearchQuery demoData
                            = new OxidationLongSearchQuery(
                        319.2285,
                        255.2330,
                        10,
                        ToleranceMode.PPM,
                        842.5572,
                        10,
                        ToleranceMode.PPM);
        demoData.addOxidations(OxidationTypeLong.ALL);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println(result);

        OxidationLongSearchQuery received = gson.fromJson(result, OxidationLongSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
