package services.rest.api.request;


import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class AdvancedSearchQuery {

    private double mass;
    private double retention_time;
    private List<Spectrum> composite_spectrum;
    private double tolerance;
    private ToleranceMode tolerance_mode;
    private ChemAlphabet chemical_alphabet;
    private boolean deuterium;
    private ModifiersType modifiers_type;
    private List<Database> databases;
    private MetabolitesType metabolites_type;
    private MassesMode masses_mode;
    private IonMode ion_mode;
    private List<Adducts> adducts;

    public AdvancedSearchQuery(double mass, double tolerance,
                               ToleranceMode tolerance_mode,
                               double retention_time,
                               List<Spectrum> composite_spectrum,
                               ChemAlphabet chemical_alphabet,
                               boolean deuterium, ModifiersType modifiers_type,
                               List<Database> databases,
                               MetabolitesType metabolites_type,
                               MassesMode masses_mode, IonMode ion_mode,
                               List<Adducts> adducts) {
        this(mass, tolerance, tolerance_mode, retention_time,
             chemical_alphabet, deuterium, modifiers_type,
             metabolites_type, masses_mode, ion_mode);
        this.composite_spectrum = composite_spectrum;
        this.databases = databases;
        this.adducts = adducts;
    }

    public AdvancedSearchQuery(double mass, double tolerance,
                               ToleranceMode tolerance_mode,
                               double retention_time,
                               ChemAlphabet chemical_alphabet, boolean deuterium,
                               ModifiersType modifiers_type,
                               MetabolitesType metabolites_type,
                               MassesMode masses_mode, IonMode ion_mode) {
        this.mass = mass;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.retention_time = retention_time;
        this.composite_spectrum = new ArrayList();
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.modifiers_type = modifiers_type;
        this.databases = new ArrayList();
        this.metabolites_type = metabolites_type;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = new ArrayList();
    }

    public double getMass() {
        return mass;
    }

    public double getTolerance() {
        return tolerance;
    }

    public ToleranceMode getTolerance_mode() {
        return tolerance_mode;
    }

    public double getRetention_time() {
        return retention_time;
    }

    public List<Spectrum> getComposite_spectrum() {
        return composite_spectrum;
    }

    public ChemAlphabet getChemical_alphabet() {
        return chemical_alphabet;
    }

    public boolean getDeuterium() {
        return deuterium;
    }

    public ModifiersType getModifiers_type() {
        return modifiers_type;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public MetabolitesType getMetabolites_type() {
        return metabolites_type;
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

    public void addSpectrum(Spectrum spectrum) {
        this.composite_spectrum.add(spectrum);
    }

    public void addDatabase(Database database) {
        this.databases.add(database);
    }

    public void addAdduct(Adducts pa) {
        this.adducts.add(pa);
    }

    public static void main(String[] args) {
        AdvancedSearchQuery demoData
                            = new AdvancedSearchQuery(
                        757.5667,
                        10.0,
                        ToleranceMode.PPM,
                        27.755224,
                        ChemAlphabet.CHNOPS,
                        false,
                        ModifiersType.NONE,
                        MetabolitesType.AWPEPTIDES,
                        MassesMode.NEUTRAL,
                        IonMode.POSITIVE);
        demoData.addSpectrum(new Spectrum(758.574, 2504091.8));
        demoData.addSpectrum(new Spectrum(759.57526, 1266287.5));
        demoData.addSpectrum(new Spectrum(760.57806, 351016.47));
        demoData.addSpectrum(new Spectrum(761.57874, 68498.03));
        demoData.addSpectrum(new Spectrum(762.5804, 12906.35));
        demoData.addSpectrum(new Spectrum(780.5511, 45726.906));
        demoData.addSpectrum(new Spectrum(781.5546, 21230.219));
        demoData.addDatabase(Database.ALLWMINE);
        demoData.addAdduct(Adducts.PMpH);
        demoData.addAdduct(Adducts.PMp2H);
        demoData.addAdduct(Adducts.PMpNA);
        demoData.addAdduct(Adducts.PMpK);
        demoData.addAdduct(Adducts.PMpNH4);
        demoData.addAdduct(Adducts.PMpHmH2O);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println(result);

        AdvancedSearchQuery received = gson.fromJson(result, AdvancedSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));

        System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        for (Adducts pa : received.getAdducts()) {
            System.out.println(pa + " " + pa.getClass().getTypeName());
        }
    }
}
