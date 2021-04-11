/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEMS;

import CEMS.enums.CE_Exp_properties.CE_EXP_PROP_ENUM;
import compound.CMMCompound;
import compound.Structure;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import pathway.Pathway;

/**
 * CEMS Compounds
 *
 * @author Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class CEMSCompound extends CMMCompound implements Comparable<CEMSCompound> {

    private Double effMob;
    private Double MT;
    private Double RMT;
    private final CE_EXP_PROP_ENUM ce_exp_properties;
    private final Set<CEMSFragment> fragments;

    /**
     * Constructor of CEMS Compound
     *
     * @param ce_exp_properties
     * @param effMob
     * @param MT
     * @param RMT
     * @param fragments
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
     * @param pathways
     */
    public CEMSCompound(CE_EXP_PROP_ENUM ce_exp_properties,
            Double effMob, Double MT, Double RMT,
            Set<CEMSFragment> fragments,
            Integer compound_id, Double mass, String formula, String compound_name,
            String cas_id, Integer formula_type, Integer compound_type, Integer compound_status,
            Integer charge_type, Integer charge_number,
            String lm_id, String kegg_id, String hmdb_id, String metlin_id, String in_house_id, Integer pc_id, Integer chebi_id, Integer MINE_id, 
            String knapsack_id, Integer npatlas_id,
            String aspergillus_id, String mesh_nomenclature, String iupac_classification, String aspergillus_web_name,
            Integer fahfa_id, Integer oh_position,
            Structure structure, List<Pathway> pathways) {
        // STILL TO DO. INCLUDE CLASSIFICATIONS
        super(compound_id, mass, formula, compound_name, cas_id, formula_type, compound_type,
                compound_status, charge_type, charge_number, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name, fahfa_id, oh_position,
                structure, null, null, null, pathways);
        this.ce_exp_properties = ce_exp_properties;
        this.RMT = RMT;
        this.effMob = effMob;
        this.MT = MT;
        if (fragments == null) {
            this.fragments = new TreeSet<>();
        } else {
            this.fragments = fragments;
        }

    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.ce_exp_properties);
        return hash;
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
        if(super.equals(obj))
        {
            final CEMSCompound other = (CEMSCompound) obj;
            if(this.ce_exp_properties == other.ce_exp_properties)
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(CEMSCompound o) {
        // TODO POSSIBLE PERFORMANCE IMPROVEMENT ORDERING THEM BY MASS
        int result = (int) Math.round(this.getCompound_id()) - (int) Math.round(o.getCompound_id());
        if (result == 0 && this.ce_exp_properties != null && o.ce_exp_properties != null) {
            result = (int) Math.round(this.ce_exp_properties.compareTo(o.ce_exp_properties));
        } else if (this.ce_exp_properties == null && o.ce_exp_properties == null) {
            return 0;
        } else if (this.ce_exp_properties == null) {
            return -1;
        } else if (o.ce_exp_properties == null) {
            return 1;
        }
        return result;
    }

    @Override
    public String toString() {
        return "CEMSCompound{" + "effMob=" + this.effMob + ", MT=" + this.MT + ", RMT=" + this.RMT
                + ", ce_exp_properties=" + this.ce_exp_properties + ", fragments=" + this.fragments + '}';
    }

    public CE_EXP_PROP_ENUM getCe_exp_properties() {
        return ce_exp_properties;
    }

    public Double getEffMob() {
        return effMob;
    }

    public void setEffMob(Double effMob) {
        this.effMob = effMob;
        if(this.fragments != null)
        {
            for (CEMSFragment fragment : this.fragments)
            {
                fragment.setEffMob(effMob);
            }
        }
    }

    public Double getMT() {
        return MT;
    }

    public void setMT(Double MT) {
        this.MT = MT;
        if(this.fragments != null)
        {
            for (CEMSFragment fragment : this.fragments)
            {
                fragment.setMT(MT);
            }
        }
    }

    public Double getRMT() {
        return RMT;
    }

    public void setRMT(Double RMT) {
        this.RMT = RMT;
        if(this.fragments != null)
        {
            for (CEMSFragment fragment : this.fragments)
            {
                fragment.setRMT(RMT);
            }
        }
    }

    public Set<CEMSFragment> getFragments() {
        return this.fragments;
    }

    public boolean areThereFragments() {
        return !(this.fragments == null
                || this.fragments.isEmpty());
    }

    public void addFragment(CEMSFragment fragment) {
        this.fragments.add(fragment);
    }

}

