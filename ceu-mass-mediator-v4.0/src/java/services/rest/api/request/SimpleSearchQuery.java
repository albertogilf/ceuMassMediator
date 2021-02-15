package services.rest.api.request;

import services.rest.api.request.Database;
import services.rest.api.request.Adducts;
import services.rest.api.request.MetabolitesType;
import services.rest.api.request.MassesMode;
import services.rest.api.request.IonMode;
import services.rest.api.request.ToleranceMode;
import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class SimpleSearchQuery {

    private double mass;
    private double tolerance;
    private ToleranceMode tolerance_mode;
    private IonMode ion_mode;
    private MassesMode masses_mode;
    private MetabolitesType metabolites_type;
    private List<Adducts> adducts;
    private List<Database> databases;

    public SimpleSearchQuery(double mass, double tolerance,
                             ToleranceMode tolerance_mode,
                             MetabolitesType metabolites_type,
                             MassesMode masses_mode, IonMode ion_mode,
                             List<Adducts> adducts,
                             List<Database> databases) {
        this(mass, tolerance, tolerance_mode, metabolites_type, masses_mode,
             ion_mode);
        this.adducts = adducts;
        this.databases = databases;
    }

    public SimpleSearchQuery(double mass, double tolerance,
                             ToleranceMode tolerance_mode,
                             MetabolitesType metabolites_type,
                             MassesMode masses_mode, IonMode ion_mode) {
        this.mass = mass;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.ion_mode = ion_mode;
        this.masses_mode = masses_mode;
        this.metabolites_type = metabolites_type;
        this.adducts = new ArrayList();
        this.databases = new ArrayList();
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

    public IonMode getIon_mode() {
        return ion_mode;
    }

    public MassesMode getMasses_mode() {
        return masses_mode;
    }

    public MetabolitesType getMetabolites_type() {
        return metabolites_type;
    }

    public List<Adducts> getAdducts() {
        return adducts;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void addDatabase(Database database) {
        this.databases.add(database);
    }

    public void addAdduct(Adducts pa) {
        this.adducts.add(pa);
    }

    public static void main(String[] args) {
        SimpleSearchQuery demoData
                          = new SimpleSearchQuery(
                        757.5667,
                        10.0,
                        ToleranceMode.PPM,
                        MetabolitesType.AWPEPTIDES,
                        MassesMode.NEUTRAL,
                        IonMode.POSITIVE);
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

        SimpleSearchQuery received = gson.fromJson(result, SimpleSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));

        System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        for (Adducts pa : received.getAdducts()) {
            System.out.println(pa + " " + pa.getClass().getTypeName());
        }
    }
}
