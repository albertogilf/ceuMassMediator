/*
 * CEMSAnnotation.java
 *
 * Created on 30-dic-2019, 16:56:10
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import CEMS.enums.CE_Exp_properties.CE_EXP_PROP_ENUM;
import compound.Structure;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import pathway.Pathway;
import static utilities.AdductsLists.MAPMZNEGATIVEADDUCTS;
import static utilities.AdductsLists.MAPMZPOSITIVEADDUCTS;
import static utilities.Utilities.calculatePPMIncrement;
import static utilities.Utilities.calculatePercentageError;

/**
 * Annotations for CEMS Compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 5.0.0.0 30-dic-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSAnnotation extends CEMSCompound implements CEMSAnnotationAdapter {

    private final Double exp_mz;
    private final String adduct;
    private final Double exp_MT;
    private final Double exp_RMT;
    private final Double exp_effMob;
    private final Set<CEMSFragment> experimentalFragments;
    private final Set<CEMSFragment> fragmentsFound;
    private final Set<CEMSFragment> theoreticalFragmentsNotFound;
    private final Set<CEMSFragment> experimentalFragmentsNotFound;
    private final Integer errorMZ;
    private final Integer errorMT;
    private final Integer errorRMT;
    private final Integer errorEffMob;
    private final String colorFoundInStandard;

    /**
     * Constructor of CEMS Annotation
     *
     * @param exp_mz
     * @param adduct
     * @param ce_exp_properties
     * @param exp_MT
     * @param exp_RMT
     * @param experimentalFragments
     * @param exp_effMob
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
     * @param structure
     * @param iupac_classification
     * @param fahfa_id
     * @param oh_position
     * @param pathways
     * @param aspergillus_web_name
     * @param foundInStandard
     */
    public CEMSAnnotation(Double exp_mz, String adduct, Double exp_effMob, Double exp_MT, Double exp_RMT,
            Set<CEMSFragment> experimentalFragments,
            CE_EXP_PROP_ENUM ce_exp_properties,
            Double effMob, Double MT, Double RMT,
            Set<CEMSFragment> fragments,
            Integer compound_id, Double mass, String formula, String compound_name,
            String cas_id, Integer formula_type, Integer compound_type, Integer compound_status,
            Integer charge_type, Integer charge_number,
            String lm_id, String kegg_id, String hmdb_id, String metlin_id, String in_house_id, Integer pc_id, Integer chebi_id, Integer MINE_id,
            String knapsack_id, Integer npatlas_id, 
            String aspergillus_id, String mesh_nomenclature, String iupac_classification, String aspergillus_web_name, 
            Integer fahfa_id, Integer oh_position, 
            Structure structure, List<Pathway> pathways, boolean foundInStandard) {
        super(ce_exp_properties, effMob, MT, RMT, fragments,
                compound_id, mass, formula, compound_name, cas_id, formula_type, compound_type,
                compound_status, charge_type, charge_number, lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id,
                knapsack_id, npatlas_id,
                aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                fahfa_id, oh_position,
                structure, pathways);
        this.exp_mz = exp_mz;
        this.adduct = adduct;
        this.exp_MT = exp_MT;
        this.exp_RMT = exp_RMT;
        this.exp_effMob = exp_effMob;
        if (experimentalFragments == null) {
            this.experimentalFragments = new TreeSet<>();
        } else {
            this.experimentalFragments = experimentalFragments;
        }
        this.fragmentsFound = new TreeSet<>();
        this.theoreticalFragmentsNotFound = new TreeSet<>();
        this.experimentalFragmentsNotFound = new TreeSet<>();
        this.fillFragmentsFound();
        if (this.exp_mz != null && super.getMass() != null) {
            Double adductMZDouble =0d;
            String adductMZString = MAPMZPOSITIVEADDUCTS.get(adduct);
            if(adductMZString == null)
            {
                adductMZString = MAPMZNEGATIVEADDUCTS.get(adduct);
            }
            if(adductMZString != null)
            {
                adductMZDouble = Double.valueOf(adductMZString);
            }
            Double measuredMass = this.exp_mz + adductMZDouble;
            this.errorMZ = calculatePPMIncrement(measuredMass, super.getMass());
        } else {
            this.errorMZ = null;
        }
        if (this.exp_MT != null && super.getMT() != null) {
            this.errorMT = calculatePercentageError(this.exp_MT, super.getMT());
        } else {
            this.errorMT = null;
        }
        if (this.exp_RMT != null && super.getRMT() != null) {
            this.errorRMT = calculatePercentageError(this.exp_RMT, super.getRMT());
        } else {
            this.errorRMT = null;
        }
        if (this.exp_effMob != null && super.getEffMob() != null) {
            this.errorEffMob = calculatePercentageError(this.exp_effMob, super.getEffMob());
        } else {
            this.errorEffMob = null;
        }
        if (foundInStandard) {
            this.colorFoundInStandard = "row_EXPECTED";
        } else {
            this.colorFoundInStandard = "";
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.exp_mz);
        hash = 37 * hash + Objects.hashCode(this.exp_MT);
        hash = 37 * hash + Objects.hashCode(this.exp_RMT);
        hash = 37 * hash + Objects.hashCode(this.exp_effMob);
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
        if (!super.equals(obj)) {
            return false;
        } else {
            final CEMSAnnotation other = (CEMSAnnotation) obj;

            if (!Objects.equals(this.exp_mz, other.exp_mz)) {
                return false;
            }
            if (!Objects.equals(this.exp_MT, other.exp_MT)) {
                return false;
            }
            if (!Objects.equals(this.exp_RMT, other.exp_RMT)) {
                return false;
            }
            if (!Objects.equals(this.exp_effMob, other.exp_effMob)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "CEMSAnnotation{" + "exp_mz=" + exp_mz + ", exp_MT=" + exp_MT + ", exp_RMT=" + exp_RMT
                + ", exp_effMob=" + exp_effMob + ", experimentalFragments=" + experimentalFragments
                + ", listFragmentsFound=" + fragmentsFound + ", listFragmentsNotFound=" + theoreticalFragmentsNotFound
                + ", errorMZ=" + errorMZ + ", errorRMT=" + errorRMT + ", errorEffMob=" + errorEffMob + '}';
    }

    @Override
    public Double getExp_mz() {
        return exp_mz;
    }
    
    @Override
    public String getAdduct() {
        return this.adduct;
    }

    @Override
    public Double getExp_MT() {
        return exp_MT;
    }

    @Override
    public Double getExp_RMT() {
        return exp_RMT;
    }

    @Override
    public Double getExp_effMob() {
        return exp_effMob;
    }

    public Set<CEMSFragment> getExperimentalFragments() {
        return experimentalFragments;
    }

    public Set<CEMSFragment> getFragmentsFound() {
        return fragmentsFound;
    }

    public Set<CEMSFragment> getTheoreticalFragmentsNotFound() {
        return theoreticalFragmentsNotFound;
    }

    public Set<CEMSFragment> getExperimentalFragmentsNotFound() {
        return experimentalFragmentsNotFound;
    }

    @Override
    public Integer getErrorMZ() {
        return errorMZ;
    }

    @Override
    public Integer getErrorMT() {
        return errorMT;
    }

    @Override
    public Integer getErrorRMT() {
        return errorRMT;
    }

    @Override
    public Integer getErrorEffMob() {
        return errorEffMob;
    }

    public boolean areThereExperimentalFragments() {
        return !(this.experimentalFragments == null
                || this.experimentalFragments.isEmpty());
    }

    public void addExperimentalFragment(CEMSFragment cemsFragment) {
        if (!this.experimentalFragments.contains(cemsFragment)) {
            this.experimentalFragments.add(cemsFragment);
            if (super.getFragments().contains(cemsFragment)) {
                this.fragmentsFound.add(cemsFragment);
                this.theoreticalFragmentsNotFound.remove(cemsFragment);
            } else {
                this.experimentalFragmentsNotFound.add(cemsFragment);
            }
        }
    }

    private void fillFragmentsFound() {
        Set<CEMSFragment> experimentalFragmentsTMP = new TreeSet<>(this.experimentalFragments);
        if (super.getFragments() != null) {
            for (CEMSFragment theoreticalFragment : super.getFragments()) {
                Boolean found = false;
                for (CEMSFragment experimentalFragment : experimentalFragmentsTMP) {
                    if (theoreticalFragment.equals(experimentalFragment)) {
                        this.fragmentsFound.add(experimentalFragment);
                        found = true;
                        experimentalFragmentsTMP.remove(experimentalFragment);
                        break;
                    }
                }
                if (!found) {
                    this.theoreticalFragmentsNotFound.add(theoreticalFragment);
                }
            }
        }
        this.experimentalFragmentsNotFound.addAll(experimentalFragmentsTMP);
    }

    @Override
    public int compareTo(CEMSCompound compound) {
        if (compound instanceof CEMSAnnotation) {
            CEMSAnnotation o = (CEMSAnnotation) compound;
            int result = (int) Math.round(this.getErrorMZ()) - (int) Math.round(o.getErrorMZ());
            if (result == 0 && this.getErrorEffMob() != null && o.getErrorEffMob() != null) {
                result = (int) Math.round(this.getErrorEffMob()) - (int) Math.round(o.getErrorEffMob());
                if (result == 0 && this.getErrorRMT() != null && o.getErrorRMT() != null) {
                    result = (int) Math.round(this.getErrorRMT()) - (int) Math.round(o.getErrorRMT());
                    if (result == 0 && this.getErrorMT() != null && o.getErrorMT() != null) {
                        result = (int) Math.round(this.getErrorMT()) - (int) Math.round(o.getErrorMT());
                        if (result == 0) {
                            result = super.getCompound_id() - o.getCompound_id();
                        }
                    }
                }
            } else if (this.getErrorEffMob() == null && o.getErrorEffMob() == null) {
                if (this.getErrorRMT() != null && o.getErrorRMT() != null) {
                    result = (int) Math.round(this.getErrorRMT()) - (int) Math.round(o.getErrorRMT());
                    if (result == 0 && this.getErrorMT() != null && o.getErrorMT() != null) {
                        result = (int) Math.round(this.getErrorMT()) - (int) Math.round(o.getErrorMT());
                        if (result == 0) {
                            result = super.getCompound_id() - o.getCompound_id();
                        }
                    } else if (this.getErrorMT() == null) {
                        return -1;
                    } else if (o.getErrorMT() == null) {
                        return 1;
                    }
                } else if (this.getErrorRMT() == null && o.getErrorRMT() == null) {
                    if (this.getErrorMT() != null && o.getErrorMT() != null) {
                        result = (int) Math.round(this.getErrorMT()) - (int) Math.round(o.getErrorMT());
                        if (result == 0) {
                            result = super.getCompound_id() - o.getCompound_id();
                        }
                    } else if (this.getErrorMT() == null && o.getErrorMT() == null) {
                        result = super.getCompound_id() - o.getCompound_id();
                    } else if (this.getErrorMT() == null) {
                        return -1;
                    } else if (o.getErrorMT() == null) {
                        return 1;
                    }
                } else if (this.getErrorRMT() == null) {
                    return -1;
                } else if (o.getErrorRMT() == null) {
                    return 1;
                }
            } else if (this.getErrorEffMob() == null) {
                return -1;
            } else if (o.getErrorEffMob() == null) {
                return 1;
            }
            return result;
        } else {
            throw new IllegalArgumentException("The CEMSCompound should be an annotation to be comparable");
        }
    }

    public String getColorFoundInStandard() {
        return colorFoundInStandard;
    }
}
