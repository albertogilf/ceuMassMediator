package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ExpRmtSearchResults {


    private List<CompoundsGroupMzRmt> compounds_found;
    private List<FragmentsGroupMzRmt> fragments;



    public ExpRmtSearchResults() {
        this.compounds_found = new ArrayList();
        this.fragments = new ArrayList();
    }



    public void addFragmentGroup(FragmentsGroupMzRmt fragmentGroup) {
        this.fragments.add(fragmentGroup);
    }



    public void addCompoundGroup(CompoundsGroupMzRmt compoundGroup) {
        this.compounds_found.add(compoundGroup);
    }


}

