package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class BrowseSearchResults {

    private List<BrowseCompound> results;

    public BrowseSearchResults() {
        this.results = new ArrayList();
    }

    public BrowseSearchResults(List<BrowseCompound> compounds) {
        this();
        this.results.addAll(compounds);
    }

    public void addCompound(BrowseCompound compound) {
        this.results.add(compound);
    }
}