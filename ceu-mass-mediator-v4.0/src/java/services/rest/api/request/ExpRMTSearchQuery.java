package services.rest.api.request;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ExpRMTSearchQuery {


    private final List<Double> masses;
    private final int tolerance;
    private final ToleranceMode tolerance_mode;

    private final List<Double> rmt;
    private final int rmt_tolerance;
    private final CEMSToleranceMode rmt_tolerance_mode;


    private final BufferType buffer;
    private final PolarityType polarity;
    private final RMTReference rmt_reference;
    private final ChemAlphabet chemical_alphabet;
    private final boolean deuterium;

    private final IonModeMS ion_mode;
    private final MassesMode masses_mode;
    private final List<Adducts> adducts;



    public ExpRMTSearchQuery(List<Double> masses,
                             int tolerance,
                             ToleranceMode tolerance_mode,
                             List<Double> relative_migration_times,
                             int exp_rmt_tolerance,
                             CEMSToleranceMode exp_rmt_tolerance_mode,
                             BufferType buffer,
                             PolarityType polarity,
                             RMTReference rmt_reference,
                             ChemAlphabet chemical_alphabet,
                             boolean deuterium,
                             IonModeMS ion_mode,
                             MassesMode masses_mode,
                             List<Adducts> adducts) {
        this.masses = masses;
        this.rmt = relative_migration_times;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.rmt_tolerance = exp_rmt_tolerance;
        this.rmt_tolerance_mode = exp_rmt_tolerance_mode;
        this.buffer = buffer;
        this.polarity = polarity;
        this.rmt_reference = rmt_reference;
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = adducts;
    }



    public List<Double> getRmt() {
        return rmt;
    }



    public int getRmt_tolerance() {
        return rmt_tolerance;
    }



    public CEMSToleranceMode getRmt_tolerance_mode() {
        return rmt_tolerance_mode;
    }



    public RMTReference getRmt_Reference() {
        return rmt_reference;
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



    public BufferType getBuffer() {
        return buffer;
    }



    public PolarityType getPolarity() {
        return polarity;
    }



    public ChemAlphabet getChemical_alphabet() {
        return chemical_alphabet;
    }



    public boolean isDeuterium() {
        return deuterium;
    }



    public MassesMode getMasses_mode() {
        return masses_mode;
    }



    public IonModeMS getIon_mode() {
        return ion_mode;
    }



    public List<Adducts> getAdducts() {
        return adducts;
    }



    public void addAdduct(Adducts pa) {
        this.adducts.add(pa);
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
            0.85, 0.86, 1.07, 0.93, 0.42
        };

        List<Double> masses = Arrays.asList(massesArray);
        List<Double> rmts = Arrays.asList(relative_migration_times);

        Adducts[] adductsArray = {
            Adducts.PMpH, Adducts.PMp2H, Adducts.PMpNA, Adducts.PMpK,
            Adducts.PMpNH4, Adducts.PMpHmH2O
        };

        List<Adducts> adducts = Arrays.asList(adductsArray);

        // DEMO DATA - CEMS Eff MOB SEARCH
        ExpRMTSearchQuery demoData
                          = new ExpRMTSearchQuery(
                        masses,
                        10,
                        ToleranceMode.PPM,
                        rmts,
                        10,
                        CEMSToleranceMode.PERCENTAGE,
                        BufferType.FORMIC,
                        PolarityType.DIRECT,
                        RMTReference.LMETHIONINESULFONE,
                        ChemAlphabet.CHNOPS,
                        false,
                        IonModeMS.POSITIVE,
                        MassesMode.MZ,
                        adducts
                );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - ExpRMTSearch Query");
        System.out.println(result);

        ExpRMTSearchQuery received = gson.fromJson(result, ExpRMTSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }

}

