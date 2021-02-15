package services.rest.api.request;

import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class SimpleBatchQuery {

    private List<Double> masses;
    private double tolerance;
    private ToleranceMode tolerance_mode;
    private List<Database> databases;
    private MetabolitesType metabolites_type;
    private MassesMode masses_mode;
    private IonMode ion_mode;
    private List<Adducts> adducts;

    public SimpleBatchQuery(List<Double> masses,
            double tolerance, ToleranceMode tolerance_mode,
            List<Database> databases,
            MetabolitesType metabolites_type,
            MassesMode masses_mode, IonMode ion_mode,
            List<Adducts> adducts) {
        this(masses, tolerance, tolerance_mode, metabolites_type, masses_mode, ion_mode);
        this.databases = databases;
        this.adducts = adducts;
    }

    public SimpleBatchQuery(List<Double> masses,
            double tolerance, ToleranceMode tolerance_mode,
            MetabolitesType metabolites_type,
            MassesMode masses_mode, IonMode ion_mode) {
        this.masses = masses;
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.databases = new ArrayList();
        this.metabolites_type = metabolites_type;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = new ArrayList();
    }

    public List<Double> getMasses() {
        return masses;
    }

    public double getTolerance() {
        return tolerance;
    }

    public ToleranceMode getTolerance_mode() {
        return tolerance_mode;
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

    public void addDatabase(Database database) {
        this.databases.add(database);
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

        // DEMO DATA - BATCH ADVANCED SEARCH V1
        Double[] massesArray = {
            399.3367, 421.31686, 315.2424, 337.2234, 280.2402,
            287.2104, 495.3352, 517.3172, 547.3635, 571.3644,
            569.3479, 567.3328, 589.3148, 481.3167, 477.2858,
            499.2672, 501.286, 525.2853, 547.2667, 511.3265,
            533.3079, 539.3571, 356.2923, 129.1515, 281.2722,
            282.2563, 264.2458, 256.2406, 255.2571, 648.5165,
            646.5048, 672.5205, 694.5019, 425.3504, 267.0966,
            183.0882, 174.1109, 584.263, 194.0804, 161.1059,
            362.2094, 113.059, 155.0689, 165.078, 430.3773
        };

        List<Double> masses = Arrays.asList(massesArray);

        SimpleBatchQuery demoData = new SimpleBatchQuery(
                masses,
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

        System.out.println("Query - DEMO DATA - Batch Simple Search");
        System.out.println(result);

        AdvancedSearchQuery received = gson.fromJson(result, AdvancedSearchQuery.class);

        System.out.println("Result: " + gson.toJson(received).equals(result));

        System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        for (Adducts pa : received.getAdducts()) {
            System.out.println(pa + " " + pa.getClass().getTypeName());
        }

        // DEMO DATA - BATCH ADVANCED SEARCH - V2 - LCMS
        System.out.println("Query - DEMO DATA - LCMS Batch Advanced Search");

        final Double[] massesArray2 = {192.0743, 301.1798, 146.4819, 90.0219, 187.0};

        final Double[] retention_timesArray2 = {18.842525, 8.425, 18.842525, 18.842525, 8.425};

        masses = Arrays.asList(massesArray2);

        demoData = new SimpleBatchQuery(
                masses,
                10.0,
                ToleranceMode.PPM,
                MetabolitesType.AWPEPTIDES,
                MassesMode.MZ,
                IonMode.POSITIVE);

        demoData.addDatabase(Database.ALLWMINE);
        demoData.addAdduct(Adducts.PMpH);
        demoData.addAdduct(Adducts.PMpNA);

        result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Batch Simple Search v2 - LCMS");
        System.out.println(result);

        received = gson.fromJson(result, AdvancedSearchQuery.class);
        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
