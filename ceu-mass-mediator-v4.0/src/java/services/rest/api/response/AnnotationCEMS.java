package services.rest.api.response;

import CEMS.CEMSAnnotation;
import java.util.ArrayList;
import java.util.List;
import pathway.Pathway;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class AnnotationCEMS {


    private int id;
    private String name;
    private String formula;

    private double molecular_weight; //mass
    private int error_ppm; //errorMZ

    private String cas_id;

    private String hmdb_id;
    private final String hmdb_uri;

    private String metlin_id;
    private final String metlin_uri;

    private String lipidmaps_id;
    private final String lipidmaps_uri;

    private String kegg_id;
    private final String kegg_uri;

    private String pubchem_id;
    private final String pubchem_uri;

    private String chebi_id;
    private final String chebi_uri;

    private List<PathWay> pathways;



    public AnnotationCEMS(CEMSAnnotation annotation) {
        this.id = annotation.getCompound_id();
        this.name = annotation.getCompound_name();
        this.formula = annotation.getFormula();
        this.molecular_weight = annotation.getMass();
        this.error_ppm = annotation.getErrorMZ();
        this.cas_id = annotation.getCas_id();
        this.hmdb_id = annotation.getHmdb_id();
        this.metlin_id = annotation.getMetlin_id();
        this.lipidmaps_id = annotation.getLm_id();
        this.kegg_id = annotation.getKegg_id();
        this.pubchem_id = annotation.getPc_id();
        this.chebi_id = annotation.getChebi_id();
        this.hmdb_uri = annotation.getCompoundHMDBWebPage();
        this.metlin_uri = annotation.getCompoundMetlinWebPage();
        this.lipidmaps_uri = annotation.getCompoundLMWebPage();
        this.kegg_uri = annotation.getCompoundKeggWebPage();
        this.pubchem_uri = annotation.getCompoundPubChemWebPage();
        this.chebi_uri = annotation.getCompoundChebiWebPage();
        this.pathways = new ArrayList<>();
        for (Pathway pathway : annotation.getPathways()) {
            this.pathways.add(new PathWay(pathway.getPathwayName(), pathway.getPathwayWebPage()));
        }
    }

}

