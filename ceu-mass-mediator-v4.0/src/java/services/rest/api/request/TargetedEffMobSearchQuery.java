package services.rest.api.request;

import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class TargetedEffMobSearchQuery {


    private final List<Double> masses;
    private final int tolerance;
    private final ToleranceMode tolerance_mode;

    private final List<Double> effective_mobilities;
    private final int eff_mob_tolerance;
    private final CEMSToleranceMode eff_mob_tolerance_mode;

    private final IonSourceVoltage ion_source_voltage;

    private final List<List<Signal>> signals_grouped;

    private final ChemAlphabet chemical_alphabet;
    private final boolean deuterium;

    private final MassesMode masses_mode;

    private final IonMode ion_mode;

    private final List<Adducts> adducts;



    public TargetedEffMobSearchQuery(List<Double> masses, int tolerance, ToleranceMode tolerance_mode, List<Double> effective_mobilities, int eff_mob_tolerance, CEMSToleranceMode eff_mob_tolerance_mode, IonSourceVoltage ion_source_voltage, List<List<Signal>> signals_grouped, ChemAlphabet chemical_alphabet, boolean deuterium, MassesMode masses_mode, IonMode ion_mode, List<Adducts> adducts) {
        this.masses = masses;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.effective_mobilities = effective_mobilities;
        this.eff_mob_tolerance = eff_mob_tolerance;
        this.eff_mob_tolerance_mode = eff_mob_tolerance_mode;
        this.ion_source_voltage = ion_source_voltage;
        this.signals_grouped = signals_grouped;
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



    public List<Double> getEffective_mobilities() {
        return effective_mobilities;
    }



    public int getEff_mob_tolerance() {
        return eff_mob_tolerance;
    }



    public CEMSToleranceMode getEff_mob_tolerance_mode() {
        return eff_mob_tolerance_mode;
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



    public MassesMode getMasses_mode() {
        return masses_mode;
    }



    public IonMode getIon_mode() {
        return ion_mode;
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
        List<Double> masses = Arrays.asList(massesArray);

        Double[] effective_mobilitiesArray = {
            887.0
        };
        List<Double> effective_mobilities = Arrays.asList(effective_mobilitiesArray);

        List<Signal> signal1 = new ArrayList();
        signal1.add(new Signal(188.0711, 6.26));
        signal1.add(new Signal(205.09, 100));
        signal1.add(new Signal(227.08, 0.05));
        signal1.add(new Signal(243.0530, 0.04));
        signal1.add(new Signal(409.187, 3.73));

        List<List<Signal>> signals_grouped = new ArrayList();
        signals_grouped.add(signal1);

        Adducts[] adductsArray = {
            Adducts.PMpH, Adducts.PMp2H, Adducts.PMpNA, Adducts.PMpK,
            Adducts.PMpNH4, Adducts.PMpHmH2O
        };

        List<Adducts> adducts = Arrays.asList(adductsArray);


        // DEMO DATA - CEMS Eff MOB SEARCH
        TargetedEffMobSearchQuery demoData
                                  = new TargetedEffMobSearchQuery(
                        masses,
                        10,
                        ToleranceMode.MDA,
                        effective_mobilities,
                        10,
                        CEMSToleranceMode.PERCENTAGE,
                        IonSourceVoltage._100V,
                        signals_grouped,
                        ChemAlphabet.CHNOPS,
                        false,
                        MassesMode.MZ,
                        IonMode.POSITIVE,
                        adducts
                );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Targeted CE-MS Eff Mob Search Query");
        System.out.println(result);

        TargetedEffMobSearchQuery received = gson.fromJson(result, TargetedEffMobSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));

        //System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        //for (Adducts pa : received.getAdducts()) {
        //    System.out.println(pa + " " + pa.getClass().getTypeName());
        //}
    }

}

