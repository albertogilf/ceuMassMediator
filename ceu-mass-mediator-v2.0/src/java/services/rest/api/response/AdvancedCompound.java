package services.rest.api.response;

import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class AdvancedCompound extends Compound {

    private final double RT;
    private final float adductRelationScore;
    private final float RTscore;

    public AdvancedCompound(int identifier, double EM, double RT, String name,
                            String formula, String adduct,
                            double molecular_weight, int error_ppm,
                            float ionizationScore, float adductRelationScore,
                            float RTscore, float finalScore, String cas,
                            String kegg_compound, String kegg_uri,
                            String hmdb_compound,
                            String hmdb_uri, String lipidmaps_compound,
                            String lipidmaps_uri,
                            String metlin_compound, String metlin_uri,
                            String pubchem_compound,
                            String pubchem_uri, String inChiKey,
                            List<PathWay> pathways) {
        super(identifier, EM, name, formula, adduct, molecular_weight, error_ppm, ionizationScore, finalScore, cas,
              kegg_compound, kegg_uri, hmdb_compound, hmdb_uri, lipidmaps_compound, lipidmaps_uri,
              metlin_compound, metlin_uri, pubchem_compound, pubchem_uri, inChiKey, pathways);
        this.RT = RT;
        this.adductRelationScore = adductRelationScore;
        this.RTscore = RTscore;
    }

    @Override
    public String toString() {
        String toString = Integer.toString(this.getIdentifier()) + "_"
                          + Double.toString(this.RT) + "_" + Double.toString(this.RT);
        return toString;
    }

    public double getRT() {
        return RT;
    }

    public float getAdductRelationScore() {
        return adductRelationScore;
    }

    public float getRTscore() {
        return RTscore;
    }

}
