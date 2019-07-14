package services.rest.api.response;

import java.util.*;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class AdvancedSearchResults {

    private List<AdvancedCompound> results;

    public AdvancedSearchResults() {
        this.results = new ArrayList();
    }

    public AdvancedSearchResults(List<AdvancedCompound> compounds) {
        this();
        this.results.addAll(compounds);
    }

    public void addCompound(AdvancedCompound compound) {
        this.results.add(compound);
    }
}
