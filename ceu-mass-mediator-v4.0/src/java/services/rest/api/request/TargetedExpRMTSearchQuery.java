package services.rest.api.request;

import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class TargetedExpRMTSearchQuery {


    private final List<Double> masses;
    private final int tolerance;
    private final ToleranceMode tolerance_mode;

    private final List<Double> rmt;
    private final int rmt_tolerance;
    private final CEMSToleranceMode rmt_tolerance_mode;

    private final RMTReference rmt_reference;

    private final IonSourceVoltage ion_source_voltage;

    private final List<List<Signal>> signals_grouped;

    private final ChemAlphabet chemical_alphabet;
    private final boolean deuterium;

    private final IonModeMS ion_mode;
    private final MassesMode masses_mode;
    private final List<Adducts> adducts;



    public TargetedExpRMTSearchQuery(List<Double> masses, int tolerance, ToleranceMode tolerance_mode, List<Double> rmt, int rmt_tolerance, CEMSToleranceMode rmt_tolerance_mode, RMTReference rmt_reference, IonSourceVoltage ion_source_voltage, List<List<Signal>> signals_grouped, ChemAlphabet chemical_alphabet, boolean deuterium, IonModeMS ion_mode, MassesMode masses_mode, List<Adducts> adducts) {
        this.masses = masses;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.rmt = rmt;
        this.rmt_tolerance = rmt_tolerance;
        this.rmt_tolerance_mode = rmt_tolerance_mode;
        this.rmt_reference = rmt_reference;
        this.ion_source_voltage = ion_source_voltage;
        this.signals_grouped = signals_grouped;
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.ion_mode = ion_mode;
        this.masses_mode = masses_mode;
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



    public List<Double> getRmt() {
        return rmt;
    }



    public int getRmt_tolerance() {
        return rmt_tolerance;
    }



    public CEMSToleranceMode getRmt_tolerance_mode() {
        return rmt_tolerance_mode;
    }



    public RMTReference getRmt_reference() {
        return rmt_reference;
    }



    public IonSourceVoltage getIon_source_voltage() {
        return ion_source_voltage;
    }



    public List<List<Signal>> getSignals_grouped() {
        return signals_grouped;
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
            205.0973
        };

        Double[] relative_migration_times = {
            0.94
        };

        List<Double> masses = Arrays.asList(massesArray);
        List<Double> rmts = Arrays.asList(relative_migration_times);

        Adducts[] adductsArray = {
            Adducts.PMpH, Adducts.PMp2H, Adducts.PMpNA, Adducts.PMpK,
            Adducts.PMpNH4, Adducts.PMpHmH2O
        };

        List<Adducts> adducts = Arrays.asList(adductsArray);

        List<Signal> signal1 = new ArrayList();
        signal1.add(new Signal(188.0711, 6.26));
        signal1.add(new Signal(205.09, 100));
        signal1.add(new Signal(227.08, 0.05));
        signal1.add(new Signal(243.0530, 0.04));
        signal1.add(new Signal(409.187, 3.73));

        List<List<Signal>> signals_grouped = new ArrayList();
        signals_grouped.add(signal1);


        // DEMO DATA - CEMS Eff MOB SEARCH
        TargetedExpRMTSearchQuery demoData
                                  = new TargetedExpRMTSearchQuery(
                        masses,
                        10,
                        ToleranceMode.PPM,
                        rmts,
                        10,
                        CEMSToleranceMode.PERCENTAGE,
                        RMTReference.LMETHIONINESULFONE,
                        IonSourceVoltage._100V,
                        signals_grouped,
                        ChemAlphabet.CHNOPS,
                        false,
                        IonModeMS.POSITIVE,
                        MassesMode.MZ,
                        adducts
                );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Targeted CE-MS Exp RMT Search Query");
        System.out.println(result);

        TargetedExpRMTSearchQuery received = gson.fromJson(result, TargetedExpRMTSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }

}

