package services.rest.api.response;

import java.util.LinkedList;
import java.util.List;
import services.rest.api.request.PDCompound;
import services.rest.api.request.PathWayExt;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class PDPathWaysExt extends PathWayExt {

    private List<PDCompound> compounds_found;
    private List<PDCompound> compounds_not_found;

    public PDPathWaysExt(String name, String uri, String identifier,
                         List<PDCompound> compounds_found,
                         List<PDCompound> compounds_not_found) {
        super(name, uri, identifier);
        this.compounds_found = compounds_found;
        this.compounds_not_found = compounds_not_found;
    }

    public PDPathWaysExt(String name, String uri, String identifier) {
        this(name, uri, identifier, new LinkedList<PDCompound>(), new LinkedList<PDCompound>());
    }

    public PDPathWaysExt(PathWayExt pathway) {
        this(pathway.getName(), pathway.getUri(), pathway.getIdentifier());
    }

    public PDPathWaysExt(PathWayExt pathway,
                         List<PDCompound> compounds_found,
                         List<PDCompound> compounds_not_found) {
        this(pathway.getName(), pathway.getUri(), pathway.getIdentifier(), compounds_found, compounds_not_found);
    }

    public void addCompoundFound(PDCompound compound) {
        this.compounds_found.add(compound);
    }

    public void addCompoundNotFound(PDCompound compound) {
        this.compounds_not_found.add(compound);
    }
}
