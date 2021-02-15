package services.rest.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class TargetedEffMobSearchResults {


    private List<CompoundsGroupMzSignalEff> compounds_found;



    public TargetedEffMobSearchResults() {
        this.compounds_found = new ArrayList();
    }



    public void addCompoundGroup(CompoundsGroupMzSignalEff compoundGroup) {
        this.compounds_found.add(compoundGroup);
    }

}

