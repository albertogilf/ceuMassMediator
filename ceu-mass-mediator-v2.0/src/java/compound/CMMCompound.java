/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

import java.util.LinkedList;
import java.util.List;
import pathway.Pathway;
import static utilities.Constants.WEB_COMPUESTO_HMDB;
import static utilities.Constants.WEB_COMPUESTO_KEGG;
import static utilities.Constants.WEB_COMPUESTO_LM;
import static utilities.Constants.WEB_COMPUESTO_METLIN;
import static utilities.Constants.WEB_COMPOUND_MINE_START;
import static utilities.Constants.WEB_COMPOUND_MINE_SUFFIX;
import static utilities.Constants.WEB_COMPUESTO_PUBCHEMICHAL;

/**
 * CMMCompound. Represents a chemical compound. The data is obtained from de
 * database, not from the user. This is the father class for CompoundLCMS.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class CMMCompound extends Compound {

    private final String lm_id;
    private final String kegg_id;
    private final String hmdb_id;
    private final String metlin_id;
    private final String in_house_id;
    private final String pc_id;
    private final String MINE_id;

    private boolean boolShowPathways;

    /**
     *
     * @param compound_id
     * @param mass
     * @param formula
     * @param compound_name
     * @param cas_id
     * @param formula_type
     * @param compound_type
     * @param compound_status
     * @param charge_type
     * @param charge_number
     * @param lm_id
     * @param kegg_id
     * @param hmdb_id
     * @param pc_id
     * @param metlin_id
     * @param in_house_id
     * @param MINE_id
     * @param structure
     * @param lm_classification
     * @param classyfire_classification
     * @param lipids_classification
     * @param pathways
     *
     */
    public CMMCompound(int compound_id, double mass, String formula, String compound_name,
            String cas_id, int formula_type, int compound_type, int compound_status,
            int charge_type, int charge_number,
            String lm_id, String kegg_id, String hmdb_id, String metlin_id, String in_house_id, String pc_id, String MINE_id,
            Structure structure,
            LM_Classification lm_classification,
            List<Classyfire_Classification> classyfire_classification,
            Lipids_Classification lipids_classification,
            List<Pathway> pathways) {
        super(compound_id, mass, formula, compound_name, cas_id, formula_type, compound_type,
                compound_status, charge_type, charge_number, structure, lm_classification,
                classyfire_classification, lipids_classification, pathways);

        //ids
        this.kegg_id = kegg_id;
        this.hmdb_id = hmdb_id;
        this.metlin_id = metlin_id;
        this.in_house_id = in_house_id;
        this.pc_id = pc_id;
        this.MINE_id = MINE_id;
        this.lm_id = lm_id;

        // Variables for view
        this.boolShowPathways = false;
    }

    /**
     *
     * @return the pathways only if the show pathways is established by 1
     */
    public List<Pathway> getPathwaysBool() {
        if (!this.boolShowPathways) {
            return new LinkedList<>();
        } else {
            return this.getPathways();
        }
    }

    public String getLm_id() {
        if (this.lm_id == null) {
            return "";
        }
        return this.lm_id;
    }

    public String getCompoundLMWebPage() {
        if (null == this.lm_id || this.lm_id.equals("")) {
            return "";
        }
        return WEB_COMPUESTO_LM + getLm_id();
    }

    public String getKegg_id() {
        if (this.kegg_id == null) {
            return "";
        }
        return this.kegg_id;
    }

    public String getCompoundKeggWebPage() {
        if (null == this.kegg_id || this.kegg_id.equals("")) {
            return "";
        }
        return WEB_COMPUESTO_KEGG + getKegg_id();
    }

    public String getHmdb_id() {
        if (this.hmdb_id == null) {
            return "";
        }
        return this.hmdb_id;
    }

    public String getCompoundHMDBWebPage() {
        if (null == this.hmdb_id || this.hmdb_id.equals("")) {
            return "";
        }
        return WEB_COMPUESTO_HMDB + getHmdb_id();
    }

    public String getMetlin_id() {
        if (this.metlin_id == null) {
            return "";
        }
        return this.metlin_id;
    }

    public String getCompoundMetlinWebPage() {
        if (null == this.metlin_id || this.metlin_id.equals("")) {
            return "";
        }
        return WEB_COMPUESTO_METLIN + getMetlin_id();
    }

    public String getIn_house_id() {
        if (this.in_house_id == null) {
            return "";
        }
        return this.in_house_id;
    }

    public String getPc_id() {
        if (this.pc_id == null) {
            return "";
        }
        return this.pc_id;
    }

    public String getCompoundPubChemWebPage() {
        if (null == this.pc_id || this.pc_id.equals("")) {
            return "";
        }
        return WEB_COMPUESTO_PUBCHEMICHAL + getPc_id();
    }

    public String getMINE_id() {
        if (this.MINE_id == null) {
            return "";
        }
        return this.MINE_id;
    }

    public String getCompoundMINEWebPage() {
        if (null == this.MINE_id || this.MINE_id.equals("")) {
            return "";
        }
        return WEB_COMPOUND_MINE_START + getMINE_id() + WEB_COMPOUND_MINE_SUFFIX;
    }

    public boolean isBoolShowPathways() {
        return this.boolShowPathways;
    }

    public void setBoolShowPathways(boolean boolShowPathways) {
        this.boolShowPathways = boolShowPathways;
    }

    public void exchangeBoolShowPathways() {
        this.boolShowPathways = !this.boolShowPathways;
    }

    public boolean showPathways() {
        return this.isBoolShowPathways();
    }

    public String roundToFourDecimals(Double doubleToRound) {
        return String.format("%.4f", doubleToRound).replace(",", ".");
        // return new DecimalFormat(".#####").format(doubleToRound);
    }

    @Override
    public String toString() {
        return "Compound{" + "compound_id=" + getCompound_id() + ", mass=" + getMass()
                + ", formula=" + getFormula() + ", compound_name=" + getCompound_name()
                + ", cas_id=" + getCas_id() + ", formula_type=" + getFormula_type()
                + ", compound_type=" + getCompound_type() + ", compound_status=" 
                + getCompound_status() + ", charge_type=" + getCharge_type() + ", charge_number=" 
                + getCharge_number() + ", lm_id=" + lm_id + ", kegg_id=" + kegg_id + ", hmdb_id=" 
                + hmdb_id + ", metlin_id=" + metlin_id + ", in_house_id=" + in_house_id 
                + ", pc_id=" + pc_id + ", MINE_id=" + MINE_id + ", structure=" + getStructure()
                + ", lm_classification=" + getLm_classification() + ", classsyfire_classification=" 
                + getClasssyfire_classification() + ", lipids_classification=" + getLipids_classification()
                + ", pathways=" + getPathways() + ", boolShowPathways=" + boolShowPathways + '}';
    }

}
