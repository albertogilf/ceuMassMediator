/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.util.List;
import static utilities.Constants.WEB_COMPOUND_CMM;
import static utilities.Constants.WEB_COMPOUND_HMDB;

/**
 * MSMS Compounds from the standards, mainly from databases.
 *
 * @author Maria Postigo. Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 17/04/2018
 */
public class MSMSCompound extends Msms {

    // Attributes from the db
    private final int msms_id;
    private final int compound_id;
    private final String compound_name;
    private final String hmdb_id;
    private final String compound_formula;
    private final double compound_mass;

    // Calculated attributes
    private double score;

    /**
     * Creates a MSMS Compound from database. It calls the super constructor and
     * initialize variables with a score.
     *
     * @param ionizationMode
     * @param voltage
     * @param voltageLevel
     * @param peaks
     * @param spectraType
     * @param msms_id
     * @param compound_id
     * @param hmdb_id
     * @param compound_name
     * @param compound_formula
     * @param compound_mass
     * @param score
     *
     */
    public MSMSCompound(int ionizationMode, int voltage, String voltageLevel, List<Peak> peaks, int spectraType,
            int msms_id, int compound_id, String hmdb_id, String compound_name, String compound_formula,
            double compound_mass, double score) {
        super(ionizationMode, voltage, voltageLevel, peaks, spectraType);
        this.compound_id = compound_id;
        this.msms_id = msms_id;
        this.hmdb_id = hmdb_id;
        this.compound_name = compound_name;
        this.compound_formula = compound_formula;
        this.compound_mass = compound_mass;
        this.parentIonNeutralMass = this.compound_mass;
        this.parentIonMZ = utilities.AdductProcessing.calculateMZFromHAdduct(ionizationMode, this.parentIonMZ);
        this.score = score;

    }

    /**
     * Creates a MSMS Compound from database. It calls the super constructor and
     * initialize variables without a score.
     *
     * @param ionizationMode
     * @param voltage
     * @param voltageLevel
     * @param peaks
     * @param spectraType
     * @param msms_id
     * @param compound_id
     * @param hmdb_id
     * @param compound_name
     * @param compound_formula
     * @param compound_mass
     */
    public MSMSCompound(
            int ionizationMode, int voltage, String voltageLevel, List<Peak> peaks, int spectraType,
            int msms_id, int compound_id, String hmdb_id, String compound_name, String compound_formula, double compound_mass) {
        super(ionizationMode, voltage, voltageLevel, peaks, spectraType);
        this.compound_id = compound_id;
        this.msms_id = msms_id;
        this.hmdb_id = hmdb_id;
        this.compound_name = compound_name;
        this.compound_formula = compound_formula;
        this.compound_mass = compound_mass;
        this.parentIonNeutralMass = this.compound_mass;
        this.parentIonMZ = utilities.AdductProcessing.calculateMZFromHAdduct(ionizationMode, this.parentIonMZ);
    }

    public MSMSCompound(int msms_id, int compound_id, String hmdb_id, int ionizationMode, int voltage, String voltageLevel, List<Peak> peaks, int spectraType) {
        super(ionizationMode, voltage, voltageLevel, peaks, spectraType);
        this.msms_id = msms_id;
        this.compound_id = compound_id;
        this.hmdb_id = hmdb_id;
        this.compound_name = "";
        this.compound_formula = "";
        this.compound_mass = 0;
    }

    public String getHMDB_ID() {
        return this.hmdb_id;
    }

    public int getMsms_id() {
        return msms_id;
    }

    public int getCompound_id() {
        return compound_id;
    }

    public String getCompound_name() {
        return compound_name;
    }

    public String getCompound_formula() {
        return compound_formula;
    }

    public double getCompound_mass() {
        return compound_mass;
    }

    public String getRoundedDouble(Double doubleToRound) {
        return String.format("%.4f", doubleToRound).replace(",", ".");
        // return new DecimalFormat(".#####").format(doubleToRound);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    
    public String getCMMWebPage() {
        if (this.compound_id == 0) {
            return "";
        }
        try {
            String compoundIdString = Integer.toString(this.compound_id);
            return WEB_COMPOUND_CMM + compoundIdString;
        } catch (NumberFormatException ex) {
            return "";
        }
    }
    
    public String getHMDBWebPage() {
        if (this.hmdb_id == null) {
            return "";
        } else {
            return WEB_COMPOUND_HMDB + hmdb_id;
        }
    }

    @Override
    public String toString() {
        return "id: " + this.compound_id + " HMDB id: " + this.hmdb_id + " name: "
                + this.compound_name + " formula: " + this.compound_formula
                + " mass: " + this.compound_mass + " Score: " + this.score;
    }

}
