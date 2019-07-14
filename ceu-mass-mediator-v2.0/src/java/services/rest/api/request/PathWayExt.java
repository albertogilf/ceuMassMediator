package services.rest.api.request;

import services.rest.api.response.PathWay;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class PathWayExt extends PathWay {

    private String identifier;

    public PathWayExt(String name, String uri, String identifier) {
        super(name, uri);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
