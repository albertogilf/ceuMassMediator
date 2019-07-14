package services.rest.api.request;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class PDCompound {

    private int identifier;
    private double EM;
    private double RT;
    private String name;
    private String formula;
    private Adducts adduct;
    private double molecular_weight;
    private int error_ppm;

    private double adductRetentionScore;
    private double ionizationScore;
    private double RTScore;
    private double finalScore;

    private String cas;

    private String kegg_compound;
    private String kegg_uri;

    private String hmdb_compound;
    private String hmdb_uri;

    private String lipidmaps_compound;
    private String lipidmaps_uri;

    private String metlin_compound;
    private String metlin_uri;

    private String pubchem_compound;
    private String pubchem_uri;

    private String inChiKey;

    private List<PathWayExt> pathways;

    public PDCompound(
            int identifier,
            double EM,
            double RT,
            String name,
            String formula,
            Adducts adduct,
            double molecular_weight,
            int error_ppm,
            double adductRetentionScore,
            double ionizationScore,
            double RTScore,
            double finalScore,
            String cas,
            String kegg_compound,
            String kegg_uri,
            String hmdb_compound,
            String hmdb_uri,
            String lipidmaps_compound,
            String lipidmaps_uri,
            String metlin_compound,
            String metlin_uri,
            String pubchem_compound,
            String pubchem_uri,
            String inChiKey) {
        this.identifier = identifier;
        this.EM = EM;
        this.RT = RT;
        this.name = name;
        this.formula = formula;
        this.adduct = adduct;
        this.molecular_weight = molecular_weight;
        this.error_ppm = error_ppm;
        this.adductRetentionScore = adductRetentionScore;
        this.ionizationScore = ionizationScore;
        this.RTScore = RTScore;
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
        this.pathways = new LinkedList<PathWayExt>();
    }

    public PDCompound(int identifier, double EM, double RT, String name,
                      String formula, Adducts adduct, double molecular_weight,
                      int error_ppm, double adductRetentionScore,
                      double ionizationScore, double RTScore, double finalScore,
                      String cas, String kegg_compound, String kegg_uri,
                      String hmdb_compound, String hmdb_uri,
                      String lipidmaps_compound, String lipidmaps_uri,
                      String metlin_compound, String metlin_uri,
                      String pubchem_compound, String pubchem_uri,
                      String inChiKey, List<PathWayExt> pathways) {
        this(identifier, EM, RT, name, formula, adduct, molecular_weight, error_ppm, adductRetentionScore,
             ionizationScore, RTScore, finalScore, cas, kegg_compound, kegg_uri, hmdb_compound, hmdb_uri,
             lipidmaps_compound, lipidmaps_uri, metlin_compound, metlin_uri, pubchem_compound, pubchem_uri,
             inChiKey);
        this.pathways = pathways;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public double getEM() {
        return EM;
    }

    public void setEM(double EM) {
        this.EM = EM;
    }

    public double getRT() {
        return RT;
    }

    public void setRT(double RT) {
        this.RT = RT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Adducts getAdduct() {
        return adduct;
    }

    public void setAdduct(Adducts adduct) {
        this.adduct = adduct;
    }

    public double getMolecular_weight() {
        return molecular_weight;
    }

    public void setMolecular_weight(double molecular_weight) {
        this.molecular_weight = molecular_weight;
    }

    public int getError_ppm() {
        return error_ppm;
    }

    public void setError_ppm(int error_ppm) {
        this.error_ppm = error_ppm;
    }

    public double getAdductRetentionScore() {
        return adductRetentionScore;
    }

    public void setAdductRetentionScore(double adductRetentionScore) {
        this.adductRetentionScore = adductRetentionScore;
    }

    public double getIonizationScore() {
        return ionizationScore;
    }

    public void setIonizationScore(double ionizationScore) {
        this.ionizationScore = ionizationScore;
    }

    public double getRTScore() {
        return RTScore;
    }

    public void setRTScore(double RTScore) {
        this.RTScore = RTScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getKegg_compound() {
        return kegg_compound;
    }

    public void setKegg_compound(String kegg_compound) {
        this.kegg_compound = kegg_compound;
    }

    public String getKegg_uri() {
        return kegg_uri;
    }

    public void setKegg_uri(String kegg_uri) {
        this.kegg_uri = kegg_uri;
    }

    public String getHmdb_compound() {
        return hmdb_compound;
    }

    public void setHmdb_compound(String hmdb_compound) {
        this.hmdb_compound = hmdb_compound;
    }

    public String getHmdb_uri() {
        return hmdb_uri;
    }

    public void setHmdb_uri(String hmdb_uri) {
        this.hmdb_uri = hmdb_uri;
    }

    public String getLipidmaps_compound() {
        return lipidmaps_compound;
    }

    public void setLipidmaps_compound(String lipidmaps_compound) {
        this.lipidmaps_compound = lipidmaps_compound;
    }

    public String getLipidmaps_uri() {
        return lipidmaps_uri;
    }

    public void setLipidmaps_uri(String lipidmaps_uri) {
        this.lipidmaps_uri = lipidmaps_uri;
    }

    public String getMetlin_compound() {
        return metlin_compound;
    }

    public void setMetlin_compound(String metlin_compound) {
        this.metlin_compound = metlin_compound;
    }

    public String getMetlin_uri() {
        return metlin_uri;
    }

    public void setMetlin_uri(String metlin_uri) {
        this.metlin_uri = metlin_uri;
    }

    public String getPubchem_compound() {
        return pubchem_compound;
    }

    public void setPubchem_compound(String pubchem_compound) {
        this.pubchem_compound = pubchem_compound;
    }

    public String getPubchem_uri() {
        return pubchem_uri;
    }

    public void setPubchem_uri(String pubchem_uri) {
        this.pubchem_uri = pubchem_uri;
    }

    public String getInChiKey() {
        return inChiKey;
    }

    public void setInChiKey(String inChiKey) {
        this.inChiKey = inChiKey;
    }

    public List<PathWayExt> getPathways() {
        return pathways;
    }

    public void setPathways(List<PathWayExt> pathways) {
        this.pathways = pathways;
    }

    public void addPathway(PathWayExt pathway) {
        this.pathways.add(pathway);
    }
}
