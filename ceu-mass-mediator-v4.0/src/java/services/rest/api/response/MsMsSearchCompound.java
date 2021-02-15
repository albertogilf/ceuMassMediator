package services.rest.api.response;

import utilities.Constants;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class MsMsSearchCompound {
    
    private final String spectral_display_tools;
    private final int identifier;
    private final String hmdb_compound;
    private final String hmdb_uri;
    private final String name;
    private final String formula;
    private final double mass;
    private final double score;

    public MsMsSearchCompound(String spectral_display_tools, int identifier,
            String hmdb_compound,
            String name, String formula, double mass,
            double score) {
        this.spectral_display_tools = spectral_display_tools;
        this.identifier = identifier;
        this.hmdb_compound = hmdb_compound;
        this.hmdb_uri=Constants.WEB_COMPOUND_HMDB + this.hmdb_compound;
        this.name = name;
        this.formula = formula;
        this.mass = mass;
        this.score = score;
    }

    public String getSpectral_display_tools() {
        return spectral_display_tools;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getHmdb_compound() {
        return hmdb_compound;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public double getMass() {
        return mass;
    }

    public double getScore() {
        return score;
    }

    
}
