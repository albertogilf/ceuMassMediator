package services.rest.api.response;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import services.rest.api.request.PDCompound;
import services.rest.api.request.TestCompounds;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class PathwayDisplayerResults {

    private List<PDPathWaysExt> pathways;

    public PathwayDisplayerResults() {
        this.pathways = new LinkedList<PDPathWaysExt>();
    }

    public PathwayDisplayerResults(List<PDPathWaysExt> pathways) {
        this();
        this.pathways = pathways;
    }

    public List<PDPathWaysExt> getPathways() {
        return pathways;
    }

    public void setPathways(List<PDPathWaysExt> pathways) {
        this.pathways = pathways;
    }

    public void addPathWay(PDPathWaysExt pathway) {
        this.pathways.add(pathway);
    }

    public static void main(String[] args) {
        PathwayDisplayerResults demoData = new PathwayDisplayerResults();

        demoData.addPathWay(
                new PDPathWaysExt(
                        TestCompounds.map05132,
                        new LinkedList<PDCompound>(
                                Arrays.asList(
                                        TestCompounds.larginina
                                )
                        ),
                        new LinkedList<PDCompound>()
                )
        );

        demoData.addPathWay(
                new PDPathWaysExt(
                        TestCompounds.map00472,
                        new LinkedList<PDCompound>(
                                Arrays.asList(
                                        TestCompounds.larginina,
                                        TestCompounds.darginina,
                                        TestCompounds.oxoarginina
                                )
                        ),
                        new LinkedList<PDCompound>()
                )
        );

        demoData.addPathWay(
                new PDPathWaysExt(
                        TestCompounds.map05142,
                        new LinkedList<PDCompound>(
                                Arrays.asList(
                                        TestCompounds.larginina
                                )
                        ),
                        new LinkedList<PDCompound>()
                )
        );

        demoData.addPathWay(
                new PDPathWaysExt(
                        TestCompounds.map05146,
                        new LinkedList<PDCompound>(
                                Arrays.asList(
                                        TestCompounds.larginina
                                )
                        ),
                        new LinkedList<PDCompound>()
                )
        );

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - PathwayDisplayer Response");
        System.out.println(result);

        PathwayDisplayerResults received = gson.fromJson(result, PathwayDisplayerResults.class);

        assert (gson.toJson(received).equals(result));

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
