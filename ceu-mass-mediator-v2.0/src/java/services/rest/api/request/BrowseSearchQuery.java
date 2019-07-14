package services.rest.api.request;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class BrowseSearchQuery {
    private String name;
    private boolean exactName;
    private String formula;
    private boolean exactFormula;
    private List<Database> databases;
    private MetabolitesType metabolites_type;

    public BrowseSearchQuery(String name, boolean exactName,
                             String formula, boolean exactFormula,
                             List<Database> databases,
                             MetabolitesType metabolites_type) {
        this(name, exactName, formula, exactFormula, metabolites_type);
        this.databases = databases;
    }
    
    public BrowseSearchQuery(String name, boolean exactName,
                             String formula, boolean exactFormula,
                             MetabolitesType metabolites_type) {
        this.name = name;
        this.exactName = exactName;
        this.formula = formula;
        this.exactFormula = exactFormula;
        this.databases = new ArrayList<>();
        this.metabolites_type = metabolites_type;
    }

    public String getName() {
        return name;
    }

    public boolean isExactName() {
        return exactName;
    }

    public String getFormula() {
        return formula;
    }

    public boolean isExactFormula() {
        return exactFormula;
    }

    public MetabolitesType getMetabolites_type() {
        return metabolites_type;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void addDatabase(Database database) {
        this.databases.add(database);
    }

    public static void main(String[] args) {
        BrowseSearchQuery demoData = new BrowseSearchQuery(
                                                "choline",
                                                false,
                                                "C5H14NO",
                                                false,
                                                MetabolitesType.AWPEPTIDES);
        demoData.addDatabase(Database.ALLWMINE);
        
        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println(result);

        BrowseSearchQuery received = gson.fromJson(result, BrowseSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
