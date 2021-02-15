package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class Link {


    private final String id;
    private final String name;



    public Link(String id, String name) {
        this.id = id;
        this.name = name;
    }



    public String getId() {
        return id;
    }



    public String getName() {
        return name;
    }

}

