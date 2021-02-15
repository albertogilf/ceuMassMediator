package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class TargetedExpRmtSearchResults {


    private List<CompoundsGroupMzSignalRmt> compounds_found;



    public TargetedExpRmtSearchResults() {
        this.compounds_found = new ArrayList();
    }



    public void addCompoundGroup(CompoundsGroupMzSignalRmt compoundGroup) {
        this.compounds_found.add(compoundGroup);
    }

}

