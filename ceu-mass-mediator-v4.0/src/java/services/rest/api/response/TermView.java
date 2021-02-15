package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class TermView extends View {


    private static final String title = "Term";

    private final Integer ontology_term_id;
    private final String term;
    private final String definition;
    private final String external_id;
    private final String external_source;
    private final String ontology_comment;
    private final String curator;
    private final Integer parent_id;
    private final Integer ontology_level;



    public TermView(Integer ontology_term_id, String term, String definition,
                    String external_id, String external_source, String ontology_comment,
                    String curator, Integer parent_id, Integer ontology_level,
                    String created, String last_updated) {
        super(created, last_updated);
        this.ontology_term_id = ontology_term_id;
        this.term = term;
        this.definition = definition;
        this.external_id = external_id;
        this.external_source = external_source;
        this.ontology_comment = ontology_comment;
        this.curator = curator;
        this.parent_id = parent_id;
        this.ontology_level = ontology_level;
    }



    public Integer getOntology_term_id() {
        return ontology_term_id;
    }



    public String getTerm() {
        return term;
    }



    public String getDefinition() {
        return definition;
    }



    public String getExternal_id() {
        return external_id;
    }



    public String getExternal_source() {
        return external_source;
    }



    public String getOntology_comment() {
        return ontology_comment;
    }



    public String getCurator() {
        return curator;
    }



    public Integer getParent_id() {
        return parent_id;
    }



    public Integer getOntology_level() {
        return ontology_level;
    }



    @Override
    public String getTitle() {
        return this.title;
    }

}

