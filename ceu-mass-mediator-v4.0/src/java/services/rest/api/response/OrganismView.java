package services.rest.api.response;

import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class OrganismView extends View {


    private static final String title = "Organism";

    private final Integer organism_id;
    private final String organism_name;

    private final Integer parent_id;

    private final Integer organism_level;

    private final List<NumberedLink> organism_hierarchy;
    private final List<Link> compounds;
    private final List<Link> references;



    public OrganismView(Integer organism_id, String organism_name,
                        Integer organism_level, Integer parent_id,
                        String created, String last_updated,
                        List<NumberedLink> organism_hierarchy,
                        List<Link> compounds,
                        List<Link> references) {
        super(created, last_updated);
        this.organism_id = organism_id;
        this.organism_name = organism_name;

        this.organism_level = organism_level;
        this.parent_id = parent_id;
        this.organism_hierarchy = organism_hierarchy;
        this.compounds = compounds;
        this.references = references;
    }



    public Integer getOrganism_id() {
        return organism_id;
    }



    public String getOrganism_name() {
        return organism_name;
    }



    public Integer getParent_id() {
        return parent_id;
    }



    public Integer getOrganism_level() {
        return organism_level;
    }



    public List<NumberedLink> getOrganism_hierarchy() {
        return organism_hierarchy;
    }



    public List<Link> getCompounds() {
        return compounds;
    }



    public List<Link> getReferences() {
        return references;
    }



    @Override
    public String getTitle() {
        return this.title;
    }



}

