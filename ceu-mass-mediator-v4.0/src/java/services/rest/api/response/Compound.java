package services.rest.api.response;

import java.util.List;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class Compound {

    private final int identifier;
    private final double EM;
    private final String name;
    private final String formula;
    private final String adduct;
    private final double molecular_weight;
    private final int error_ppm;
    private final float ionizationScore;
    private final float finalScore;
    private final String cas;
    private final String kegg_compound;
    private final String kegg_uri;

    private final String hmdb_compound;
    private final String hmdb_uri;

    private final String lipidmaps_compound;
    private final String lipidmaps_uri;

    private final String metlin_compound;
    private final String metlin_uri;

    private final String pubchem_compound;
    private final String pubchem_uri;

    private String inChiKey;

    private final List<PathWay> pathways;

    public Compound(int identifier, double EM, String name, String formula,
                    String adduct,
                    double molecular_weight, int error_ppm,
                    float ionizationScore, float finalScore, String cas,
                    String kegg_compound, String kegg_uri, String hmdb_compound,
                    String hmdb_uri, String lipidmaps_compound,
                    String lipidmaps_uri,
                    String metlin_compound, String metlin_uri,
                    String pubchem_compound,
                    String pubchem_uri, String inChiKey, List<PathWay> pathways) {
        this.identifier = identifier;
        this.EM = EM;
        this.name = name;
        this.formula = formula;
        this.adduct = adduct;
        this.molecular_weight = molecular_weight;
        this.error_ppm = error_ppm;
        this.ionizationScore = ionizationScore;
        this.finalScore = finalScore;
        this.cas = cas;
        this.kegg_compound = kegg_compound;
        this.kegg_uri = kegg_uri;
        this.hmdb_compound = hmdb_compound;
        this.hmdb_uri = hmdb_uri;
        this.lipidmaps_compound = lipidmaps_compound;
        this.lipidmaps_uri = lipidmaps_uri;
        this.metlin_compound = metlin_compound;
        this.metlin_uri = metlin_uri;
        this.pubchem_compound = pubchem_compound;
        this.pubchem_uri = pubchem_uri;
        this.inChiKey = inChiKey;
        this.pathways = pathways;
    }

    public int getIdentifier() {
        return identifier;
    }

    public double getEM() {
        return EM;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public String getAdduct() {
        return adduct;
    }

    public double getMolecular_weight() {
        return molecular_weight;
    }

    public int getError_ppm() {
        return error_ppm;
    }

    public float getIonizationScore() {
        return ionizationScore;
    }

    public float getFinalScore() {
        return finalScore;
    }

    public String getCas() {
        return cas;
    }

    public String getKegg_compound() {
        return kegg_compound;
    }

    public String getKegg_uri() {
        return kegg_uri;
    }

    public String getHmdb_compound() {
        return hmdb_compound;
    }

    public String getHmdb_uri() {
        return hmdb_uri;
    }

    public String getLipidmaps_compound() {
        return lipidmaps_compound;
    }

    public String getLipidmaps_uri() {
        return lipidmaps_uri;
    }

    public String getMetlin_compound() {
        return metlin_compound;
    }

    public String getMetlin_uri() {
        return metlin_uri;
    }

    public String getPubchem_compound() {
        return pubchem_compound;
    }

    public String getPubchem_uri() {
        return pubchem_uri;
    }

    public String getInChiKey() {
        return inChiKey;
    }

    public List<PathWay> getPathways() {
        return pathways;
    }

}
