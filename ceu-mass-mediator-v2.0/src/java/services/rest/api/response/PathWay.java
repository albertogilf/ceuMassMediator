package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class PathWay {

    private String name;
    private String uri;

    public PathWay(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
