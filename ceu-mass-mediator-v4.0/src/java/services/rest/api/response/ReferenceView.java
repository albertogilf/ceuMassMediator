package services.rest.api.response;

import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ReferenceView extends View {


    private static final String title = "Reference";

    private final Integer reference_id;
    private final String reference_text;
    private final String doi;
    private final String link;

    private final List<Link> compounds;
    private final List<Link> organisms;



    public ReferenceView(Integer reference_id, String reference_text,
                         String doi, String link, String created,
                         String last_updated, List<Link> compounds, List<Link> organisms) {
        super(created, last_updated);
        this.reference_id = reference_id;
        this.reference_text = reference_text;
        this.doi = doi;
        this.link = link;
        this.compounds = compounds;
        this.organisms = organisms;
    }



    public Integer getReference_id() {
        return reference_id;
    }



    public String getReference_text() {
        return reference_text;
    }



    public String getDoi() {
        return doi;
    }



    public String getLink() {
        return link;
    }



    @Override
    public String getTitle() {
        return this.title;
    }



    public List<Link> getCompounds() {
        return compounds;
    }



    public List<Link> getOrganisms() {
        return organisms;
    }

}

