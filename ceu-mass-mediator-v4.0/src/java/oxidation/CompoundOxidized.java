/*
 * CompoundOxidized.java
 *
 * Created on 22-may-2018, 19:14:58
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package oxidation;

import compound.CMMCompound;
import exceptions.OxidationTypeException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import utilities.ConstantesForOxidation;
import utilities.OxidationLists;
import static utilities.OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
import static utilities.OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
import utilities.Utilities;

/**
 * Oxidized compounds (PC) formed by two FA
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 22-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class CompoundOxidized implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Double oxidizedFAEM;
    private final Double nonOxidizedFAEM;
    private final Double parentIonEM;
    private final Double neutralMassPI;
    private final Double mzPositivePI;
    private final String oxidationType;
    private final String adductType;

    // Attributes which only apply when there are hits
    private final String name;
    private final String formula;
    private final double theoreticalPIMolecularWeight;
    private final double theoreticalPIEM;
    private final Integer ppmIncrement;

    private final int numCarbonsInFAs;
    private final int numDoubleBondsInFAs;

    // Depends on the oxidation type. 0 for NON oxidized, 1 for LC oxidation, 
    // 2 for SC oxidation
    // -1 for Error
    private final int oxidationNumberType;

    private final CompoundFA oxidizedFA;
    private final CompoundFA nonOxidizedFA;

    private final List<Double> neutralLossesPositiveMode;
    private final List<Double> neutralLossesNegativeMode;

    public List<CMMCompound> oxidizedAnnotations;
    private final List<CMMCompound> nonOxidizedAnnotations;

    private boolean boolShowNonOxidizedAnnotations;

    /**
     * Constructor of Oxidized CMMCompound. It contains experimental masses of
     * both fatty acids but also of the precursor (parent ion).
     *
     * @param oxidizedFAEM m/z of the oxidized FA
     * @param nonOxidizedFAEM m/z of the non-oxidized FA
     * @param parentIonEM m/z of the parentIon
     * @param oxidationType oxidation type (O,OH,OH-OH,OOH,COH or COOH)
     * @param adductType
     * @param oxidizedFA
     * @param nonOxidizedFA
     * @throws exceptions.OxidationTypeException
     */
    public CompoundOxidized(Double oxidizedFAEM,
            Double nonOxidizedFAEM,
            Double parentIonEM,
            String oxidationType,
            String adductType,
            CompoundFA oxidizedFA,
            CompoundFA nonOxidizedFA) throws OxidationTypeException {
        this.oxidizedFAEM = oxidizedFAEM;
        this.parentIonEM = parentIonEM;

        this.oxidationType = oxidationType;
        this.oxidationNumberType = calculateOxidationNumberType(this.oxidationType);
        if (adductType.equals(ConstantesForOxidation.NEG_H_ADDUCT)
                && oxidationType.equals(ConstantesForOxidation.OXIDATIONTYPE_FOR_H_ADDUCT)) {
            this.adductType = adductType;
            this.neutralMassPI = parentIonEM + ConstantesForOxidation.PROTON_WEIGHT;
            this.mzPositivePI = this.neutralMassPI + ConstantesForOxidation.PROTON_WEIGHT;
            if (nonOxidizedFAEM == null) {
                this.nonOxidizedFAEM = Utilities.calculateFAEMFromPIandOtherFAEM(neutralMassPI, oxidizedFAEM);
            } else {
                this.nonOxidizedFAEM = nonOxidizedFAEM;
            }
        } else {
            this.adductType = ConstantesForOxidation.HCOO_ADDUCT;
            this.neutralMassPI = parentIonEM - ConstantesForOxidation.HCOO_WEIGHT;
            this.mzPositivePI = this.neutralMassPI + ConstantesForOxidation.PROTON_WEIGHT;
            if (nonOxidizedFAEM == null) {
                this.nonOxidizedFAEM = Utilities.calculateFAEMFromPIandOtherFAEM(neutralMassPI, oxidizedFAEM);
            } else {
                this.nonOxidizedFAEM = nonOxidizedFAEM;
            }
        }

        this.oxidizedFA = oxidizedFA;
        this.nonOxidizedFA = nonOxidizedFA;

        List<CompoundFA> FAs = new LinkedList<CompoundFA>();
        if (this.oxidizedFA == null || this.nonOxidizedFA == null) {
// data introduced by user is not a PC
            this.name = "";
            this.formula = "";
            this.theoreticalPIMolecularWeight = 0;
            this.theoreticalPIEM = 0;
            this.ppmIncrement = 0;

            this.numCarbonsInFAs = 0;
            this.numDoubleBondsInFAs = 0;
            this.neutralLossesPositiveMode = new LinkedList();
            this.neutralLossesNegativeMode = new LinkedList();
        } else {

            FAs.add(nonOxidizedFA);
            FAs.add(oxidizedFA);
            this.name = Utilities.createNameOfPC(FAs);
            this.formula = Utilities.createFormulaOfPC(FAs, oxidationType);
            this.theoreticalPIMolecularWeight = Utilities.calculateTheoreticalNeutralMassOfPC(FAs);
            this.theoreticalPIEM = Utilities.calculateTheoreticalExperimentalMassOfPC(FAs, adductType);
            this.ppmIncrement = Utilities.calculatePPMIncrement(this.parentIonEM, this.theoreticalPIEM);

            this.numCarbonsInFAs = this.oxidizedFA.getNumCarbons() + this.nonOxidizedFA.getNumCarbons();
            this.numDoubleBondsInFAs = this.oxidizedFA.getNumDoubleBonds() + this.nonOxidizedFA.getNumDoubleBonds();
            this.neutralLossesPositiveMode = setNeutralLossesPositiveMode(this.oxidationType);
            this.neutralLossesNegativeMode = setNeutralLossesNegativeMode(this.oxidationType);
        }
        boolShowNonOxidizedAnnotations = false;
        this.oxidizedAnnotations = new LinkedList();
        this.nonOxidizedAnnotations = new LinkedList();
    }

    private List<Double> setNeutralLossesPositiveMode(String oxidationType) {
        switch (oxidationType) {
            case "O":
                return OxidationLists.LISTNLPOS_O;
            case "OH":
                return OxidationLists.LISTNLPOS_OH;
            case "OH-OH":
                return OxidationLists.LISTNLPOS_OH_OH;
            case "OOH":
                return OxidationLists.LISTNLPOS_OOH;
            case "COH":
                return OxidationLists.LISTNLPOS_COH;
            case "COOH":
                return OxidationLists.LISTNLPOS_COOH;
            default:
                return new LinkedList<Double>();
        }
    }

    private List<Double> setNeutralLossesNegativeMode(String oxidationType) {
        switch (oxidationType) {
            case "O":
                return OxidationLists.LISTNLNEG_O;
            case "OH":
                return OxidationLists.LISTNLNEG_OH;
            case "OH-OH":
                return OxidationLists.LISTNLNEG_OH_OH;
            case "OOH":
                return OxidationLists.LISTNLNEG_OOH;
            case "COH":
                return OxidationLists.LISTNLNEG_COH;
            case "COOH":
                return OxidationLists.LISTNLNEG_COOH;
            default:
                return new LinkedList<Double>();
        }
    }

    private int calculateOxidationNumberType(String oxidationType) {
        if (oxidationType.equals("")) {
            return 0;
        } else if (LIST_LONG_CHAIN_OXIDATION_TYPES.contains(oxidationType)) {
            return 1;
        } else if (LIST_SHORT_CHAIN_OXIDATION_TYPES.contains(oxidationType)) {
            return 2;
        } else {
            return -1;
        }
    }

    public boolean isLCOxidation() {
        return this.oxidationNumberType == 1;
    }

    public Double getOxidizedFAEM() {
        return this.oxidizedFAEM;
    }

    public Double getNonOxidizedFAEM() {
        return this.nonOxidizedFAEM;
    }

    public Double getParentIonEM() {
        return this.parentIonEM;
    }

    public Double getNeutralMassPI() {
        return this.neutralMassPI;
    }

    public Double getMzPositivePI() {
        return this.mzPositivePI;
    }

    public String getOxidationType() {
        return this.oxidationType;
    }

    public String getAdductType() {
        return this.adductType;
    }

    /**
     * Check if there are annotations for the oxidized FA. If there are
     * annotations for it, check if there are annotations for the non oxidized
     * compound. If there is annotations for the non oxidized FA, then return
     * true. Any other case, return false.
     *
     * @return
     */
    public boolean isThereFATheoreticalCompounds() {
        if (this.oxidizedFA != null && this.oxidizedFA.getCompound_id() > 0
                && this.nonOxidizedFA != null && this.nonOxidizedFA.getCompound_id() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the composed name in case
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    public String getFormula() {
        return this.formula;
    }

    public Double getTheoreticalPIMolecularWeight() {
        return this.theoreticalPIMolecularWeight;
    }

    public String getStringMolecularWeight() {
        // Other way to deal with decimals 
        return String.format("%.4f", this.theoreticalPIMolecularWeight).replace(",", ".");
        // return new DecimalFormat(".#####").format(this.theoreticalPIMolecularWeight);
    }

    public int getPpmIncrement() {
        return this.ppmIncrement;
    }

    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Oxidized compound found for oxidized FA: "
                + String.format("%.4f", this.oxidizedFAEM).replace(",", ".") + ", ";
        if (this.nonOxidizedFAEM > 0d) {
            titleMessage = titleMessage + "Non-oxidized FA: "
                    + String.format("%.4f", this.nonOxidizedFAEM).replace(",", ".") + ", ";
        }
        if (this.parentIonEM > 0d) {
            titleMessage = titleMessage + "parent ion: "
                    + String.format("%.4f", this.parentIonEM).replace(",", ".") + ", ";
            titleMessage = titleMessage + "adduct: " + this.adductType + " and ";
            titleMessage = titleMessage + "oxidation: " + this.oxidationType;
        }
        if (!isThereFATheoreticalCompounds()) {
            titleMessage = "No " + titleMessage;
        }
        return titleMessage;

    }

    public Integer getNumCarbonsInFAs() {
        return this.numCarbonsInFAs;
    }

    public Integer getNumDoubleBondsInFAs() {
        return this.numDoubleBondsInFAs;
    }

    public CompoundFA getOxidizedFA() {
        return this.oxidizedFA;
    }

    public CompoundFA getNonOxidizedFA() {
        return this.nonOxidizedFA;
    }

    public int getType() {
        return this.oxidationNumberType;
    }

    public Collection getNeutralLossesPositiveMode() {
        return this.neutralLossesPositiveMode;
    }

    public Collection getNeutralLossesNegativeMode() {
        return this.neutralLossesNegativeMode;
    }

    public boolean areThereNeutralLossesPositiveMode() {
        return !this.neutralLossesPositiveMode.isEmpty();
    }

    public boolean areThereNeutralLossesNegativeMode() {
        return !this.neutralLossesNegativeMode.isEmpty();
    }

    public List<CMMCompound> getOxidizedAnnotations() {
        return this.oxidizedAnnotations;
    }

    public boolean areThereOxidizedAnnotationsForPI() {
        boolean areThereOxidizedAnnotationsForPI;
        areThereOxidizedAnnotationsForPI = this.oxidizedAnnotations != null
                && !this.oxidizedAnnotations.isEmpty();
        return areThereOxidizedAnnotationsForPI;

    }

    public List<CMMCompound> getNonOxidizedAnnotations() {
        if (!isBoolShowNonOxidizedAnnotations()) {
            return new LinkedList();
        } else {
            return this.nonOxidizedAnnotations;
        }
    }

    public boolean areThereNonOxidizedAnnotationsForPI() {
        boolean areThereNonOxidizedAnnotationsForPI;
        areThereNonOxidizedAnnotationsForPI = this.nonOxidizedAnnotations != null
                && !this.nonOxidizedAnnotations.isEmpty();
        return areThereNonOxidizedAnnotationsForPI;
    }

    public void exchangeBoolShowNonOxidizedAnnotations() {
        this.boolShowNonOxidizedAnnotations = !this.boolShowNonOxidizedAnnotations;
    }

    public boolean isBoolShowNonOxidizedAnnotations() {
        return this.boolShowNonOxidizedAnnotations;
    }

    /**
     * add one oxidized annotation over the oxidized compound
     *
     * @param oxidizedAnnotation
     */
    public void addOxidizedAnnotation(CMMCompound oxidizedAnnotation) {
        this.oxidizedAnnotations.add(oxidizedAnnotation);
    }

    /**
     * add one Nonoxidized annotation over the oxidized compound
     *
     * @param nonOxidizedAnnotation
     */
    public void addNonOxidizedCompoundsGroupByMass(CMMCompound nonOxidizedAnnotation) {
        this.nonOxidizedAnnotations.add(nonOxidizedAnnotation);
    }

    public String roundToFourDecimals(Double doubleToRound) {
        if (doubleToRound == null) {
            return "--";
        }
        return String.format("%.4f", doubleToRound).replace(",", ".");
    }
}
