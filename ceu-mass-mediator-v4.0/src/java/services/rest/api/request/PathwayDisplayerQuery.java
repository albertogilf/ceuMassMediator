package services.rest.api.request;

import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class PathwayDisplayerQuery {

    private List<PDCompound> compounds;

    public PathwayDisplayerQuery(List<PDCompound> compounds) {
        this.compounds = compounds;
    }

    public PathwayDisplayerQuery() {
        this.compounds = new LinkedList<PDCompound>();
    }

    public List<PDCompound> getCompounds() {
        return compounds;
    }

    public void setCompounds(List<PDCompound> compounds) {
        this.compounds = compounds;
    }

    public void addCompound(PDCompound compound) {
        this.compounds.add(compound);
    }

    public static void main(String[] args) {
        PathwayDisplayerQuery demoData = new PathwayDisplayerQuery();
        demoData.addCompound(TestCompounds.larginina);
        demoData.addCompound(TestCompounds.darginina);
        demoData.addCompound(TestCompounds.oxoarginina);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - PathwayDisplayer Query");
        System.out.println(result);

        PathwayDisplayerQuery received = gson.fromJson(result, PathwayDisplayerQuery.class);

        assert (gson.toJson(received).equals(result));

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
