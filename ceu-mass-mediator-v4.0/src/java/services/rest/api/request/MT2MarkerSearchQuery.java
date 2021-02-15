package services.rest.api.request;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class MT2MarkerSearchQuery {


    private final List<Double> masses;
    private final int tolerance;
    private final ToleranceMode tolerance_mode;

    private final List<Double> mt;
    private final int mt_tolerance;
    private final CEMSToleranceMode mt_tolerance_mode;


    private final BufferType buffer;
    private final PolarityType polarity;
    private final RMTReferenceExt marker;
    private final double marker_time;
    private final RMTReferenceExt marker2;
    private final double marker2_time;

    private final ChemAlphabet chemical_alphabet;
    private final boolean deuterium;

    private final IonModeMS ion_mode;
    private final MassesMode masses_mode;
    private final List<Adducts> adducts;



    public MT2MarkerSearchQuery(List<Double> masses,
                                int tolerance,
                                ToleranceMode tolerance_mode,
                                List<Double> migration_times,
                                int mt_tolerance,
                                CEMSToleranceMode mt_tolerance_mode,
                                BufferType buffer,
                                PolarityType polarity,
                                RMTReferenceExt marker,
                                double marker_time,
                                RMTReferenceExt marker2,
                                double marker2_time,
                                ChemAlphabet chemical_alphabet,
                                boolean deuterium,
                                IonModeMS ion_mode,
                                MassesMode masses_mode,
                                List<Adducts> adducts) {
        this.masses = masses;
        this.mt = migration_times;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.mt_tolerance = mt_tolerance;
        this.mt_tolerance_mode = mt_tolerance_mode;
        this.buffer = buffer;
        this.polarity = polarity;
        this.marker = marker;
        this.marker_time = marker_time;
        this.marker2 = marker2;
        this.marker2_time = marker2_time;
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = adducts;
    }



    public List<Double> getMasses() {
        return masses;
    }



    public int getTolerance() {
        return tolerance;
    }



    public ToleranceMode getTolerance_mode() {
        return tolerance_mode;
    }



    public List<Double> getMt() {
        return mt;
    }



    public int getMt_tolerance() {
        return mt_tolerance;
    }



    public CEMSToleranceMode getMt_tolerance_mode() {
        return mt_tolerance_mode;
    }



    public BufferType getBuffer() {
        return buffer;
    }



    public PolarityType getPolarity() {
        return polarity;
    }



    public RMTReferenceExt getMarker() {
        return marker;
    }



    public double getMarker_time() {
        return marker_time;
    }



    public RMTReferenceExt getMarker2() {
        return marker2;
    }



    public double getMarker2_time() {
        return marker2_time;
    }



    public ChemAlphabet getChemical_alphabet() {
        return chemical_alphabet;
    }



    public boolean isDeuterium() {
        return deuterium;
    }



    public IonModeMS getIon_mode() {
        return ion_mode;
    }



    public MassesMode getMasses_mode() {
        return masses_mode;
    }



    public List<Adducts> getAdducts() {
        return adducts;
    }



    /**
     * Shows a JSON message for invoking the advance batch service using the
     * predefined demo values.
     *
     * @param args No params needed
     */
    public static void main(String[] args) {

        // DEMO DATA - CEMS EFF MOB SEARCH
        Double[] massesArray = {
            291.1299, 298.098, 308.094, 316.2488, 55.055
        };

        Double[] relative_migration_times = {
            11.56, 13.65, 15.62, 12.59, 6.99
        };

        List<Double> masses = Arrays.asList(massesArray);
        List<Double> rmts = Arrays.asList(relative_migration_times);

        Adducts[] adductsArray = {
            Adducts.PMpH, Adducts.PMp2H, Adducts.PMpNA, Adducts.PMpK,
            Adducts.PMpNH4, Adducts.PMpHmH2O
        };

        List<Adducts> adducts = Arrays.asList(adductsArray);

        // DEMO DATA - CEMS Eff MOB SEARCH
        MT2MarkerSearchQuery demoData
                             = new MT2MarkerSearchQuery(
                        masses,
                        10,
                        ToleranceMode.PPM,
                        rmts,
                        10,
                        CEMSToleranceMode.PERCENTAGE,
                        BufferType.FORMIC,
                        PolarityType.DIRECT,
                        RMTReferenceExt._LMETHIONINE_SULFONE,
                        14.24,
                        RMTReferenceExt._HIPPURIC_ACID,
                        25.29,
                        ChemAlphabet.CHNOPS,
                        false,
                        IonModeMS.POSITIVE,
                        MassesMode.MZ,
                        adducts
                );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - CE-MS MT 2 Marker Search Query");
        System.out.println(result);

        MT2MarkerSearchQuery received = gson.fromJson(result, MT2MarkerSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }

}

