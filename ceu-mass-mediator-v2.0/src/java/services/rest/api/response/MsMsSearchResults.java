package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class MsMsSearchResults {

    private List<MsMsSearchCompound> results;

    public MsMsSearchResults() {
        this.results = new ArrayList();
    }

    public MsMsSearchResults(List<MsMsSearchCompound> compounds) {
        this();
        this.results.addAll(compounds);
    }

    public void addCompound(MsMsSearchCompound compound) {
        this.results.add(compound);
    }
}
