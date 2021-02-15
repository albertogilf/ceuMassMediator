package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public abstract class View {


    private final String created;
    private final String last_updated;



    public View(String created, String last_updated) {
        this.created = created;
        this.last_updated = last_updated;
    }



    public String getCreated() {
        return created;
    }



    public String getLast_updated() {
        return last_updated;
    }



    public abstract String getTitle();

}

