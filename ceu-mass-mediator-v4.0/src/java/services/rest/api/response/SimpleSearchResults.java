package services.rest.api.response;

import java.util.*;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class SimpleSearchResults {

    private List<Compound> results;

    public SimpleSearchResults() {
        this.results = new ArrayList();
    }

    public SimpleSearchResults(List<Compound> compounds) {
        this();
        this.results.addAll(compounds);
    }

    public void addCompound(Compound compound) {
        this.results.add(compound);
    }
}
