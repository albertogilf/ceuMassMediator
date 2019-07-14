package services.rest.api.response;

import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class OxidationCompound {
    
    private final Double oxidized_FA_mass;
    private final Double non_oxidized_FA_mass;
    private final Double precursor_mass;
    private final String adduct_type;
    private final String oxidation_type;
    private final String name;
    private final String formula;
    private final Double molecular_weight;
    private final int error_ppm;
    private final Double mz_precursor_positive_M_H;
    private final List<Double> neutral_losses_positive;
    private final Double mz_precursor_negative_M_HCOO;
    private final List<Double> neutral_losses_negative;
    private final List<Annotation> putative_annotations_oxidized;
    private final List<Annotation> putative_annotations_non_oxidized;

    public OxidationCompound(Double oxidized_FA_mass, Double non_oxidized_FA_mass,
            Double precursor_mass, String adduct_type, String oxidation_type,
            String name, String formula, Double molecular_weight,
            int error_ppm,
            Double mz_precursor_positive_M_H, List<Double> neutral_losses_positive,
            Double mz_precursor_negative_M_HCOO, List<Double> neutral_losses_negative,
            List<Annotation> putative_annotations_oxidized,
            List<Annotation> putative_annotations_non_oxidized) {
        this.oxidized_FA_mass = oxidized_FA_mass;
        this.non_oxidized_FA_mass = non_oxidized_FA_mass;
        this.precursor_mass = precursor_mass;
        this.adduct_type = adduct_type;
        this.oxidation_type = oxidation_type;
        this.name = name;
        this.formula = formula;
        this.molecular_weight = molecular_weight;
        this.error_ppm = error_ppm;
        this.mz_precursor_positive_M_H = mz_precursor_positive_M_H;
        this.neutral_losses_positive = neutral_losses_positive;
        this.mz_precursor_negative_M_HCOO = mz_precursor_negative_M_HCOO;
        this.neutral_losses_negative = neutral_losses_negative;
        this.putative_annotations_oxidized = putative_annotations_oxidized;
        this.putative_annotations_non_oxidized = putative_annotations_non_oxidized;
    }

    public Double getOxidized_FA_mass() {
        return oxidized_FA_mass;
    }

    public Double getNon_oxidized_FA_mass() {
        return non_oxidized_FA_mass;
    }

    public Double getPrecursor_mass() {
        return precursor_mass;
    }

    public String getAdduct_type() {
        return adduct_type;
    }

    public String getOxidation_type() {
        return oxidation_type;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public Double isMolecular_weight() {
        return molecular_weight;
    }

    public int getError_ppm() {
        return error_ppm;
    }

    public Double getMolecular_weight() {
        return molecular_weight;
    }

    public Double getMz_precursor_positive_M_H() {
        return mz_precursor_positive_M_H;
    }

    public List<Double> getNeutral_losses_positive() {
        return neutral_losses_positive;
    }

    public Double getMz_precursor_negative_M_HCOO() {
        return mz_precursor_negative_M_HCOO;
    }

    public List<Double> getNeutral_losss_negative() {
        return neutral_losses_negative;
    }

    public List<Annotation> getPutative_annotations_oxidized() {
        return putative_annotations_oxidized;
    }

    public List<Annotation> getPutative_annotations_non_oxidized() {
        return putative_annotations_non_oxidized;
    }

    
     
}
