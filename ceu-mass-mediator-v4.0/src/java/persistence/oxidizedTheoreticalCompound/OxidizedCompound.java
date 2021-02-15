/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.oxidizedTheoreticalCompound;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import persistence.theoreticalGroup.CompoundsGroupByMass;
import utilities.ConstantesForOxidation;
import utilities.OxidationLists;
import utilities.Utilities;
import static utilities.OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
import static utilities.OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;

/**
 * Oxidized Compound
 *
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 08/09/2016
 */
public class OxidizedCompound implements OxidizedTheoreticalCompound, Serializable {

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

    private final FACompound oxidizedFA;
    private final FACompound nonOxidizedFA;

    private final List<Double> neutralLossesPositiveMode;
    private final List<Double> neutralLossesNegativeMode;

    CompoundsGroupByMass oxidizedCompoundsGroupByMass;
    CompoundsGroupByMass nonOxidizedCompoundsGroupByMass;

    private boolean boolShowNonOxidizedAnnotations;

    /**
     * Constructor of Oxidized Compound. It contains experimental masses of both
     * fatty acids but also of the precursor (parent ion).
     *
     * @param oxidizedFAEM m/z of the oxidized FA
     * @param nonOxidizedFAEM m/z of the non-oxidized FA
     * @param parentIonEM m/z of the parentIon
     * @param oxidationType oxidation type (O,OH,OH-OH,OOH,COH or COOH)
     * @param adductType
     * @param oxidizedFA
     * @param nonOxidizedFA
     */
    public OxidizedCompound(Double oxidizedFAEM,
            Double nonOxidizedFAEM,
            Double parentIonEM,
            String oxidationType,
            String adductType,
            FACompound oxidizedFA,
            FACompound nonOxidizedFA) {
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

        List FAs = new LinkedList<FACompound>();
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
            this.name = Utilities.createNameOfPCJPA(FAs);
            this.formula = Utilities.createFormulaOfPCJPA(FAs);
            this.theoreticalPIMolecularWeight = Utilities.calculateTheoreticalNeutralMassOfPCJPA(FAs);
            this.theoreticalPIEM = Utilities.calculateTheoreticalExperimentalMassOfPCJPA(FAs, adductType);
            this.ppmIncrement = Utilities.calculatePPMIncrement(this.parentIonEM, this.theoreticalPIEM);

            this.numCarbonsInFAs = this.oxidizedFA.getNumCarbons() + this.nonOxidizedFA.getNumCarbons();
            this.numDoubleBondsInFAs = this.oxidizedFA.getNumDoubleBonds() + this.nonOxidizedFA.getNumDoubleBonds();
            this.neutralLossesPositiveMode = setNeutralLossesPositiveMode(this.oxidationType);
            this.neutralLossesNegativeMode = setNeutralLossesNegativeMode(this.oxidationType);
        }
        boolShowNonOxidizedAnnotations = false;

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

    @Override
    public boolean isLCOxidation() {
        return this.oxidationNumberType == 1;
    }

    @Override
    public Double getOxidizedFAEM() {
        return this.oxidizedFAEM;
    }

    @Override
    public Double getNonOxidizedFAEM() {
        return this.nonOxidizedFAEM;
    }

    @Override
    public Double getParentIonEM() {
        return this.parentIonEM;
    }

    @Override
    public Double getNeutralMassPI() {
        return this.neutralMassPI;
    }

    @Override
    public Double getMzPositivePI() {
        return this.mzPositivePI;
    }

    @Override
    public String getOxidationType() {
        return this.oxidationType;
    }

    @Override
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
    @Override
    public boolean isThereFATheoreticalCompounds() {
        if (this.oxidizedFA != null) {
            if (this.oxidizedFA.getNewCompounds() != null) {
                if (this.nonOxidizedFA != null) {
                    if (this.nonOxidizedFA.getNewCompounds() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Create the composed name in case
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFormula() {
        return this.formula;
    }

    @Override
    public Double getTheoreticalPIMolecularWeight() {

        return this.theoreticalPIMolecularWeight;
    }

    public String getStringMolecularWeight() {
        // Other way to deal with decimals 
        return String.format("%.4f", this.theoreticalPIMolecularWeight).replace(",", ".");
        // return new DecimalFormat(".#####").format(this.theoreticalPIMolecularWeight);
    }

    @Override
    public int getPpmIncrement() {
        return this.ppmIncrement;
    }

    @Override
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

    @Override
    public Integer getNumCarbonsInFAs() {
        return this.numCarbonsInFAs;
    }

    @Override
    public Integer getNumDoubleBondsInFAs() {
        return this.numDoubleBondsInFAs;
    }

    @Override
    public FACompound getOxidizedFA() {
        return this.oxidizedFA;
    }

    @Override
    public FACompound getNonOxidizedFA() {
        return this.nonOxidizedFA;
    }

    @Override
    public int getType() {
        return this.oxidationNumberType;
    }

    @Override
    public Collection getNeutralLossesPositiveMode() {
        return this.neutralLossesPositiveMode;
    }

    @Override
    public Collection getNeutralLossesNegativeMode() {
        return this.neutralLossesNegativeMode;
    }

    @Override
    public boolean areThereNeutralLossesPositiveMode() {
        return !this.neutralLossesPositiveMode.isEmpty();
    }

    @Override
    public boolean areThereNeutralLossesNegativeMode() {
        return !this.neutralLossesNegativeMode.isEmpty();
    }

    @Override
    public CompoundsGroupByMass getOxidizedCompoundsGroupByMass() {
        return this.oxidizedCompoundsGroupByMass;
    }

    @Override
    public boolean areThereOxidizedAnnotationsForPI() {
        return this.oxidizedCompoundsGroupByMass != null;
    }

    @Override
    public CompoundsGroupByMass getNonOxidizedCompoundsGroupByMass() {
        if (this.nonOxidizedCompoundsGroupByMass == null || !isBoolShowNonOxidizedAnnotations()) {
            return null;
        } else {
            return this.nonOxidizedCompoundsGroupByMass;
        }
    }

    @Override
    public boolean areThereNonOxidizedAnnotationsForPI() {
        return this.nonOxidizedCompoundsGroupByMass != null;
    }

    @Override
    public void exchangeBoolShowNonOxidizedAnnotations() {
        this.boolShowNonOxidizedAnnotations = !this.boolShowNonOxidizedAnnotations;
    }

    public boolean isBoolShowNonOxidizedAnnotations() {
        return this.boolShowNonOxidizedAnnotations;
    }

    /**
     * set the annotations over the oxidized PC
     *
     * @param oxidizedCompoundsGroupByMass
     */
    @Override
    public void setOxidizedCompoundsGroupByMass(CompoundsGroupByMass oxidizedCompoundsGroupByMass) {
        this.oxidizedCompoundsGroupByMass = oxidizedCompoundsGroupByMass;
    }

    /**
     * set the annotations over the non oxidized PC
     *
     * @param nonOxidizedCompoundsGroupByMass
     */
    @Override
    public void setNonOxidizedCompoundsGroupByMass(CompoundsGroupByMass nonOxidizedCompoundsGroupByMass) {
        this.nonOxidizedCompoundsGroupByMass = nonOxidizedCompoundsGroupByMass;
    }

    public String roundToFourDecimals(Double doubleToRound) {
        if (doubleToRound == null) {
            return "--";
        }
        return String.format("%.4f", doubleToRound).replace(",", ".");
    }

    @Override
    public String toString() {
        return "OxidizedCompound{" + "oxidizedFAEM=" + oxidizedFAEM + ", nonOxidizedFAEM=" + nonOxidizedFAEM + ", parentIonEM=" + parentIonEM + ", neutralMassPI=" + neutralMassPI + ", mzPositivePI=" + mzPositivePI + ", oxidationType=" + oxidationType + ", adductType=" + adductType + ", name=" + name + ", formula=" + formula + ", theoreticalPIMolecularWeight=" + theoreticalPIMolecularWeight + ", theoreticalPIEM=" + theoreticalPIEM + ", ppmIncrement=" + ppmIncrement + ", numCarbonsInFAs=" + numCarbonsInFAs + ", numDoubleBondsInFAs=" + numDoubleBondsInFAs + ", oxidationNumberType=" + oxidationNumberType + ", oxidizedFA=" + oxidizedFA + ", nonOxidizedFA=" + nonOxidizedFA + ", neutralLossesPositiveMode=" + neutralLossesPositiveMode + ", neutralLossesNegativeMode=" + neutralLossesNegativeMode + ", oxidizedCompoundsGroupByMass=" + oxidizedCompoundsGroupByMass + ", nonOxidizedCompoundsGroupByMass=" + nonOxidizedCompoundsGroupByMass + ", boolShowNonOxidizedAnnotations=" + boolShowNonOxidizedAnnotations + '}';
    }

}
