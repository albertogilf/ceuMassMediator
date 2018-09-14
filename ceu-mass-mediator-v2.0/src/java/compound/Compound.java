/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import pathway.Pathway;
import static utilities.Constantes.WEB_COMPUESTO_HMDB;
import static utilities.Constantes.WEB_COMPUESTO_KEGG;
import static utilities.Constantes.WEB_COMPUESTO_LM;
import static utilities.Constantes.WEB_COMPUESTO_METLIN;
import static utilities.Constantes.WEB_COMPOUND_MINE_START;
import static utilities.Constantes.WEB_COMPOUND_MINE_SUFFIX;
import static utilities.Constantes.WEB_COMPUESTO_PUBCHEMICHAL;

/**
 * Compound. Represents a chemical compound. The data is obtained from de
 * database, not from the user. This is the father class for CompoundLCMS.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Compound implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int compound_id;
    private final double mass;
    private final String formula;
    private final String compound_name;
    private final String cas_id;
    private final int formula_type;
    private final int compound_type;
    private final int compound_status;
    private final int charge_type;
    private final int charge_number;

    private final String lm_id;
    private final String kegg_id;
    private final String hmdb_id;
    private final String metlin_id;
    private final String in_house_id;
    private final String pc_id;
    private final String MINE_id;
    

    private final Structure structure;
    // Several compounds have one LM_Classification
    private final LM_Classification lm_classification;
    // ClassyFire is a web-based application for automated structural classification of chemical entities.
    // Many compounds have many classyfire_classifications (many to many relationship)
    private final List<Classyfire_Classification> classsyfire_classification;
    // Many compounds have one lipids_classidication (duda alberto)
    private final Lipids_Classification lipids_classification;
    // List of pathways in wich the compound is involved. Several compounds can have several pathways (many to many relationship)
    private final List<Pathway> pathways;

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
    public Compound(int compound_id, double mass, String formula, String compound_name,
            String cas_id, int formula_type, int compound_type, int compound_status,
            int charge_type, int charge_number,
            String lm_id, String kegg_id, String hmdb_id, String metlin_id, String in_house_id, String pc_id, String MINE_id,
            Structure structure,
            LM_Classification lm_classification,
            List<Classyfire_Classification> classyfire_classification,
            Lipids_Classification lipids_classification,
            List<Pathway> pathways) {
        this.compound_id = compound_id;
        this.mass = mass;
        this.formula = formula;
        this.compound_name = compound_name;
        this.cas_id = cas_id;
        this.formula_type = formula_type;
        this.compound_type = compound_type;
        this.compound_status = compound_status;
        this.charge_type = charge_type;
        this.charge_number = charge_number;
        //ids
        this.kegg_id = kegg_id;
        this.hmdb_id = hmdb_id;
        this.metlin_id = metlin_id;
        this.in_house_id = in_house_id;
        this.pc_id = pc_id;
        this.MINE_id = MINE_id;
        this.lm_id = lm_id;

        //  Non-primitive objects
        this.lm_classification = lm_classification;
        this.classsyfire_classification = classyfire_classification;
        this.lipids_classification = lipids_classification;
        this.pathways = pathways;
        this.structure = structure;

        // Variables for view
        this.boolShowPathways = false;
    }

    public int getCompound_id() {
        return compound_id;
    }

    public double getMass() {
        return mass;
    }

    public String getFormula() {
        return formula;
    }

    public String getCompound_name() {
        return compound_name;
    }

    public String getCas_id() {
        return cas_id;
    }

    public int getFormula_type() {
        return formula_type;
    }

    public int getCompound_type() {
        return compound_type;
    }

    public int getCompound_status() {
        return compound_status;
    }

    public int getCharge_type() {
        return this.charge_type;
    }

    public int getCharge_number() {
        return this.charge_number;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public LM_Classification getLm_classification() {
        return lm_classification;
    }

    public List<Classyfire_Classification> getClasssyfire_classification() {
        return classsyfire_classification;
    }

    public Lipids_Classification getLipids_classification() {
        return lipids_classification;
    }

    /**
     *
     * @return the pathways 
     */
    public List<Pathway> getPathways() {
        if (this.pathways == null) {
            return new LinkedList<>();
        }
        return pathways;
    }
    
    /**
     *
     * @return the pathways only if the show pathways is established by 1
     */
    public List<Pathway> getPathwaysBool() {
        if (this.pathways == null || !this.boolShowPathways) {
            return new LinkedList<>();
        }
        return pathways;
    }

    public boolean areTherePathways() {
        return !(this.compound_id == 0 || this.pathways == null || this.pathways.isEmpty());
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getInChIKey() {
        if (this.structure == null) {
            return "";
        } else {
            return this.structure.getInchikey();
        }
    }
    
    public String getSmiles() {
        if (this.structure == null) {
            return "";
        } else {
            return this.structure.getSmiles();
        }
    }
    
    // Methods for extracting attributes from objects
    public String getCategory()
    {
        return this.lm_classification!=null ? this.lm_classification.getCategory() : "";
    }
    
    public String getMainClass()
    {
        return this.lm_classification!=null ? this.lm_classification.getMainClass(): "";
    }
    
    public String getSubClass()
    {
        return this.lm_classification!=null ? this.lm_classification.getSubClass() : "";
    }
    
    public int getCarbons(){
        return this.lipids_classification!=null ? this.lipids_classification.getNumberCarbons() : -1;
    }
    
    public int getDoubleBonds(){
        return this.lipids_classification!=null ? this.lipids_classification.getNumberDoubleBounds() : -1;
    }
    
    public int getNumChains(){
        return this.lipids_classification!=null ? this.lipids_classification.getNumberChains() : -1;
    }
}
