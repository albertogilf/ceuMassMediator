package services.rest.api.request;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class OxidationShortSearchQuery {
    
    private double fatty_acid_mz;
    private double fatty_acid_tolerance;
    private ToleranceMode fatty_acid_tolerance_mode;
    private double precursor_mz_negative;
    private double precursor_tolerance;
    private ToleranceMode precursor_tolerance_mode;
    private List<OxidationTypeShort> oxidations;

    public OxidationShortSearchQuery(double fatty_acid_mz,
            double fatty_acid_tolerance, ToleranceMode fatty_acid_tolerance_mode,
            double precursor_mz_negative,
            double precursor_tolerance, ToleranceMode precursor_tolerance_mode) {
        this.fatty_acid_mz = fatty_acid_mz;
        this.fatty_acid_tolerance = fatty_acid_tolerance;
        this.fatty_acid_tolerance_mode = fatty_acid_tolerance_mode;
        this.precursor_mz_negative = precursor_mz_negative;
        this.precursor_tolerance = precursor_tolerance;
        this.precursor_tolerance_mode = precursor_tolerance_mode;
        this.oxidations = new ArrayList<OxidationTypeShort>();
    }

    public OxidationShortSearchQuery(double fatty_acid_mz,
            double fatty_acid_tolerance, ToleranceMode fatty_acid_tolerance_mode,
            double precursor_mz_negative,
            double precursor_tolerance, ToleranceMode precursor_tolerance_mode,
            List<OxidationTypeShort> oxidations) {
        this(fatty_acid_mz,
            fatty_acid_tolerance, fatty_acid_tolerance_mode,
            precursor_mz_negative,
            precursor_tolerance, precursor_tolerance_mode);
        this.oxidations = oxidations;
    }

    public double getFatty_acid_mz() {
        return fatty_acid_mz;
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

    public List<OxidationTypeShort> getOxidations() {
        return oxidations;
    }

    public void addOxidations(OxidationTypeShort type) {
        this.oxidations.add(type);
    }
    
    public static void main(String[] args) {

        OxidationShortSearchQuery demoData
                            = new OxidationShortSearchQuery(
                        255.2330,
                        10,
                        ToleranceMode.PPM,
                        638.3675,
                        10,
                        ToleranceMode.PPM);
        demoData.addOxidations(OxidationTypeShort.ALL);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println(result);

        OxidationShortSearchQuery received = gson.fromJson(result, OxidationShortSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
    
}
