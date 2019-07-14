package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class OxidationSearchResults {
  
    private List<OxidationCompound> results;

    public OxidationSearchResults() {
        this.results = new ArrayList();
    }

    public OxidationSearchResults(List<OxidationCompound> compounds) {
        this();
        this.results.addAll(compounds);
    }

    public void addCompound(OxidationCompound compound) {
        this.results.add(compound);
    }  
}
