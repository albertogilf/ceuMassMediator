package services.rest.api.request;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class EffMobSearchQuery {


    private final List<Double> masses;
    private final List<Double> effective_mobilities;
    private final int tolerance;
    private final ToleranceMode tolerance_mode;
    private final BufferType buffer;
    private final PolarityType polarity;

    private final int eff_mob_tolerance;
    private final CEMSToleranceMode eff_mob_tolerance_mode;
    private final ChemAlphabet chemical_alphabet;
    private final boolean deuterium;

    private final MassesMode masses_mode;
    private final IonMode ion_mode;
    private final List<Adducts> adducts;



    public EffMobSearchQuery(List<Double> masses,
                             List<Double> effective_mobilities,
                             int tolerance,
                             ToleranceMode tolerance_mode,
                             int eff_mob_tolerance,
                             CEMSToleranceMode eff_mob_tolerance_mode,
                             BufferType buffer,
                             PolarityType polarity,
                             ChemAlphabet chemical_alphabet,
                             boolean deuterium,
                             MassesMode masses_mode,
                             IonMode ion_mode,
                             List<Adducts> adducts) {
        this.masses = masses;
        this.effective_mobilities = effective_mobilities;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.eff_mob_tolerance = eff_mob_tolerance;
        this.eff_mob_tolerance_mode = eff_mob_tolerance_mode;
        this.buffer = buffer;
        this.polarity = polarity;
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = adducts;
    }



    public List<Double> getMasses() {
        return masses;
    }



    public List<Double> getEffective_mobilities() {
        return effective_mobilities;
    }



    public int getTolerance() {
        return tolerance;
    }



    public ToleranceMode getTolerance_mode() {
        return tolerance_mode;
    }



    public CEMSToleranceMode getEff_mob_tolerance_mode() {
        return eff_mob_tolerance_mode;
    }



    public BufferType getBuffer() {
        return buffer;
    }



    public PolarityType getPolarity() {
        return polarity;
    }



    public int getEff_mob_tolerance() {
        return eff_mob_tolerance;
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

        Double[] effective_mobilitiesArray = {
            1174.0, 1060.0, 646.0, 931.0, 3192.0
        };

        List<Double> masses = Arrays.asList(massesArray);
        List<Double> effective_mobilities = Arrays.asList(effective_mobilitiesArray);

        Adducts[] adductsArray = {
            Adducts.PMpH, Adducts.PMp2H, Adducts.PMpNA, Adducts.PMpK,
            Adducts.PMpNH4, Adducts.PMpHmH2O
        };

        List<Adducts> adducts = Arrays.asList(adductsArray);

        // DEMO DATA - CEMS Eff MOB SEARCH
        EffMobSearchQuery demoData
                          = new EffMobSearchQuery(
                        masses,
                        effective_mobilities,
                        10,
                        ToleranceMode.MDA,
                        10,
                        CEMSToleranceMode.PERCENTAGE,
                        BufferType.FORMIC,
                        PolarityType.DIRECT,
                        ChemAlphabet.CHNOPS,
                        false,
                        MassesMode.MZ,
                        IonMode.POSITIVE,
                        adducts
                );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - EffMobSearch Query");
        System.out.println(result);

        EffMobSearchQuery received = gson.fromJson(result, EffMobSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));

        //System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        //for (Adducts pa : received.getAdducts()) {
        //    System.out.println(pa + " " + pa.getClass().getTypeName());
        //}
    }

}

