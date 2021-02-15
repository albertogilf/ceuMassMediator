package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ExpMtSearchResults {


    private List<CompoundsGroupMzMt> compounds_found;
    private List<FragmentsGroupMzMt> fragments;



    public ExpMtSearchResults() {
        this.compounds_found = new ArrayList();
        this.fragments = new ArrayList();
    }



    public void addFragmentGroup(FragmentsGroupMzMt fragmentGroup) {
        this.fragments.add(fragmentGroup);
    }



    public void addCompoundGroup(CompoundsGroupMzMt compoundGroup) {
        this.compounds_found.add(compoundGroup);
    }


}

