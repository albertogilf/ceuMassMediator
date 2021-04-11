/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

import java.util.LinkedList;
import java.util.List;
import pathway.Pathway;
import static utilities.Constants.WEB_COMPOUND_ASPERGILLUS;
import static utilities.Constants.WEB_COMPOUND_CHEBI;
import static utilities.Constants.WEB_COMPOUND_MINE_START;
import static utilities.Constants.WEB_COMPOUND_MINE_SUFFIX;
import static utilities.Constants.WEB_COMPOUND_KEGG;
import static utilities.Constants.WEB_COMPOUND_METLIN;
import static utilities.Constants.WEB_COMPOUND_LM;
import static utilities.Constants.WEB_COMPOUND_HMDB;
import static utilities.Constants.WEB_COMPOUND_KNAPSACK;
import static utilities.Constants.WEB_COMPOUND_NPATLAS;
import static utilities.Constants.WEB_COMPOUND_PUBCHEMICHAL;

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
    private final String aspergillus_id;
    private final String mesh_nomenclature;
    private final String iupac_classification;
    private final String aspergillus_web_name;
    private final Integer fahfa_id;
    private final Integer oh_position;
    private final Integer pc_id;
    private final Integer chebi_id;
    private final Integer MINE_id;
    private final String knapsack_id;
    private final Integer npatlas_id;

    private Boolean boolShowPathways;

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
     * @param chebi_id
     * @param MINE_id
     * @param knapsack_id
     * @param npatlas_id
     * @param aspergillus_id
     * @param mesh_nomenclature
     * @param iupac_classification
     * @param aspergillus_web_name
     * @param fahfa_id
     * @param oh_position
     * @param structure
     * @param lm_classification
     * @param classyfire_classification
     * @param lipids_classification
     * @param pathways
     *
     */
    public CMMCompound(Integer compound_id, double mass, String formula, String compound_name,
            String cas_id, Integer formula_type, Integer compound_type, Integer compound_status,
            Integer charge_type, Integer charge_number,
            String lm_id, String kegg_id, String hmdb_id, String metlin_id, String in_house_id, Integer pc_id, Integer chebi_id, Integer MINE_id,
            String knapsack_id, Integer npatlas_id,
            String aspergillus_id, String mesh_nomenclature, String iupac_classification, String aspergillus_web_name,
            Integer fahfa_id, Integer oh_position,
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
        this.aspergillus_id = aspergillus_id;
        this.mesh_nomenclature = mesh_nomenclature;
        this.iupac_classification = iupac_classification;
        this.aspergillus_web_name = aspergillus_web_name;
        this.fahfa_id = fahfa_id;
        this.oh_position = oh_position;
        this.pc_id = pc_id;
        this.chebi_id = chebi_id;
        this.MINE_id = MINE_id;
        this.lm_id = lm_id;
        this.knapsack_id = knapsack_id;
        this.npatlas_id = npatlas_id;

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
        return WEB_COMPOUND_LM + getLm_id();
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
        return WEB_COMPOUND_KEGG + getKegg_id();
    }

    public String getAspergillus_id() {
        if (this.aspergillus_id == null) {
            return "";
        }
        return this.aspergillus_id;
    }

    public String getMesh_nomenclature() {
        if (this.mesh_nomenclature == null) {
            return "";
        }
        return this.mesh_nomenclature;
    }

    public String getIupac_classification() {
        if (this.iupac_classification == null) {
            return "";
        }
        return this.iupac_classification;
    }

    public String getAspergillus_web_name() {
        if (this.aspergillus_web_name == null) {
            return "";
        }
        return this.aspergillus_web_name;
    }

    public String getCompoundAspergillusWebPage() {
        if (null == this.aspergillus_web_name || this.aspergillus_web_name.equals("")) {
            return "";
        }
        return WEB_COMPOUND_ASPERGILLUS + getAspergillus_web_name();
    }

    public Integer getFahfa_id() {
        return fahfa_id;
    }

    public Integer getOh_position() {
        return oh_position;
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
        return WEB_COMPOUND_HMDB + getHmdb_id();
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
        return WEB_COMPOUND_METLIN + getMetlin_id();
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
        return Integer.toString(this.pc_id);
    }

    public String getCompoundPubChemWebPage() {
        if (null == this.pc_id || this.pc_id.equals("")) {
            return "";
        }
        return WEB_COMPOUND_PUBCHEMICHAL + getPc_id();
    }

    public String getChebi_id() {
        if (this.chebi_id == null) {
            return "";
        }
        return Integer.toString(this.chebi_id);
    }

    public String getCompoundChebiWebPage() {
        if (null == this.chebi_id || this.chebi_id.equals("")) {
            return "";
        }
        return WEB_COMPOUND_CHEBI + getChebi_id();
    }

    public String getKnapsack_id() {
        if (this.knapsack_id == null) {
            return "";
        }
        return this.knapsack_id;
    }

    public String getCompoundKnapsackWebPage() {
        if (null == this.knapsack_id) {
            return "";
        }
        return WEB_COMPOUND_KNAPSACK + getKnapsack_id();
    }

    public String getNpatlas_id() {
        if (this.npatlas_id == null) {
            return "";
        }
        return Integer.toString(this.npatlas_id);
    }

    public String getCompoundNpatlasWebPage() {
        if (null == this.npatlas_id) {
            return "";
        }
        return WEB_COMPOUND_NPATLAS + getNpatlas_id();
    }

    public String getMINE_id() {
        if (this.MINE_id == null) {
            return "";
        }
        return Integer.toString(this.MINE_id);
    }

    public String getCompoundMINEWebPage() {
        if (null == this.MINE_id || this.MINE_id.equals("")) {
            return "";
        }
        return WEB_COMPOUND_MINE_START + getMINE_id() + WEB_COMPOUND_MINE_SUFFIX;
    }

    public Boolean getBoolShowPathways() {
        return this.boolShowPathways;
    }

    public void setBoolShowPathways(Boolean boolShowPathways) {
        this.boolShowPathways = boolShowPathways;
    }

    public void exchangeBoolShowPathways() {
        this.boolShowPathways = !this.boolShowPathways;
    }

    public Boolean showPathways() {
        return this.getBoolShowPathways();
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
