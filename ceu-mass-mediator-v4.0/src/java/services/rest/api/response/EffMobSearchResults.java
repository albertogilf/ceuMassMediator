package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class EffMobSearchResults {



    private List<CompoundsGroupMzEff> compounds_found;
    private List<FragmentsGroupMzEff> fragments;



    public EffMobSearchResults() {
        this.compounds_found = new ArrayList();
        this.fragments = new ArrayList();
    }



    public void addFragmentGroup(FragmentsGroupMzEff fragmentGroup) {
        this.fragments.add(fragmentGroup);
    }



    public void addCompoundGroup(CompoundsGroupMzEff compoundGroup) {
        this.compounds_found.add(compoundGroup);
    }



}

