/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.List;
import persistence.oxidizedTheoreticalCompound.FACompound;
import static utilities.ConstantesForOxidation.H_WEIGHT;
import static utilities.ConstantesForOxidation.PROTON_WEIGHT;
import static utilities.ConstantesForOxidation.PCHEAD_WEIGHT;
import static utilities.OxidationLists.MAPOXIDATIONS;

/**
 *
 * @author alberto
 */
public class Utilities {

    /**
     * Set the ppm difference between meassured mass and theoretical mass
     *
     * @param measuredMass Mass meassured by MS
     * @param theoreticalMass Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double measuredMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((measuredMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Calculates the theoretical Mass of a compound based on the formula
     *
     * @param formula
     * @return
     */
    public static Double calculateTheoreticalMass(String formula) {
        double theoreticalMass = 0d;
        // TODO A PATTERN FINDER FOR CALCULATING THE ELEMENTS AND THE FORMULA

        return theoreticalMass;
    }

    /**
     * Calculates the theoretical Experimental Mass of a Phosphocholine
     * depending on the FAs and the adduct type
     *
     * @param FAs
     * @param adductType
     * @return
     */
    public static Double calculateTheoreticalExperimentalMassOfPC(List<FACompound> FAs, String adductType) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (FACompound FA : FAs) {
            theoreticalMass = theoreticalMass + calculateTheoreticalMassOfOxidizedFA(FA) - H_WEIGHT;
        }
        double adductWeight = Double.parseDouble(AdductsLists.MAPMZNEGATIVEADDUCTS.get(adductType));
        theoreticalMass = theoreticalMass - adductWeight;
        return theoreticalMass;
    }

    /**
     * Calculates the theoretical Experimental Mass of a Phosphocholine
     * depending on the FAs and the adduct type
     *
     * @param FAs
     * @return
     */
    public static Double calculateTheoreticalNeutralMassOfPC(List<FACompound> FAs) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (FACompound FA : FAs) {
            theoreticalMass = theoreticalMass + calculateTheoreticalMassOfOxidizedFA(FA) - H_WEIGHT;
        }
        return theoreticalMass;
    }

    /**
     * Calculates the theoretical Name for PC
     *
     * @param FAs
     * @return
     */
    public static String createNameOfPC(List<FACompound> FAs) {
        String PCName;
        PCName = "PC(";
        for (FACompound FA : FAs) {
            if (FA != null && FA.getNumCarbons() > 0) {
                PCName = PCName + FA.getNumCarbons() + ":" + FA.getNumDoubleBonds();
                if (FA.isIsOxidized()) {
                    PCName = PCName + "[" + FA.getOxidationType() + "]";
                }
                PCName = PCName + "/";
            } else {
                PCName = "";
                return PCName;
            }
        }
        PCName = PCName.substring(0, PCName.length() - 1) + ")";
        return PCName;
    }

    /**
     * Calculates the Formula for PC
     *
     * @param FAs
     * @return
     */
    public static String createFormulaOfPC(List<FACompound> FAs) {
        String PCFormula;
        int numDoubleBonds = 0;
        int numCarbons;
        numCarbons = 0;
        int numFAs = 0;
        for (FACompound FA : FAs) {
            if (FA != null && FA.getNumCarbons() > 0) {
                // TODO
                numCarbons = numCarbons + FA.getNumCarbons();
                numDoubleBonds = numDoubleBonds + FA.getNumDoubleBonds();
                numFAs++;
            }
        }
        int numHydrogens = ConstantesForOxidation.PCHEAD_H - numFAs + numCarbons * 2 - 2 * numDoubleBonds;
        numCarbons = numCarbons + ConstantesForOxidation.PCHEAD_C;
        int numOxygens = ConstantesForOxidation.PCHEAD_O + 2 * numFAs;
        PCFormula = "C" + numCarbons + "H" + numHydrogens + "NO" + numOxygens + "P";
        return PCFormula;
    }

    /**
     * Calculates the theoretical Mass of a FA
     *
     * @param FA
     * @return
     */
    public static Double calculateTheoreticalMassOfOxidizedFA(FACompound FA) {
        double theoreticalMass = -1d;
        int numCarbons = FA.getNumCarbons();
        int numDoubleBonds = FA.getNumDoubleBonds();
        String oxidationType = FA.getOxidationType();
        // TODO CHECK WHAT HAPPENS IF THE OXIDATION KEY IS NOT IN THE MAP
        double oxidationWeight;
        if (MAPOXIDATIONS.containsKey(oxidationType)) {
            oxidationWeight = Double.parseDouble(MAPOXIDATIONS.get(oxidationType));
        } else {
            oxidationWeight = 0;
        }
        theoreticalMass = numCarbons * Elements.MAPELEMENTS.get("C")
                + (numCarbons * 2 - 2 * numDoubleBonds) * Elements.MAPELEMENTS.get("H")
                + 2 * Elements.MAPELEMENTS.get("O") - oxidationWeight;
        return theoreticalMass;
    }

    /**
     * Calculates the theoretical Mass of a FA
     *
     * @param FA
     * @return
     */
    public static Double calculateTheoreticalMassOfNonOxidizedFA(FACompound FA) {
        double theoreticalMass;
        int numCarbons = FA.getNumCarbons();
        int numDoubleBonds = FA.getNumDoubleBonds();
        theoreticalMass = numCarbons * Elements.MAPELEMENTS.get("C")
                + (numCarbons * 2 - 2 * numDoubleBonds) * Elements.MAPELEMENTS.get("H")
                + 2 * Elements.MAPELEMENTS.get("O");
        return theoreticalMass;
    }

    /**
     *
     * @param ParentIonNeutralMass
     * @param FAEM1
     * @return
     */
    public static Double calculateFAEMFromPIandOtherFAEM(Double ParentIonNeutralMass, Double FAEM1) {
        double FAEM2;
        double neutralMassFAEM1 = FAEM1 + PROTON_WEIGHT - H_WEIGHT;
        // Parent Ion Neutral mass - Mass of the PC Head - FA of Oxidized FA
        FAEM2 = ParentIonNeutralMass - PCHEAD_WEIGHT - neutralMassFAEM1;
        return FAEM2;
    }

    /**
     * Calculate the theoretical monositopic weight
     *
     * @param FA1EM
     * @param FA2EM
     * @param oxidationType
     * @return
     */
    /*
    public static Double calculateOxidizedTheoreticalMonoisotopicWeight(Double FA1EM, Double FA2EM, String oxidationType) {
        double theoreticalMonoisotopicWeight;
        // Look for weight of oxidation 
        double oxidationWeight = Double.parseDouble(MAPOXIDATIONS.get(oxidationType));
        // 16:0 -> 256.2402302664
        // 20:4 -> 304.2402302664
        // PC HEAD -> 223.0974
        theoreticalMonoisotopicWeight = PCHEAD_WEIGHT + FA1EM + H_WEIGHT + FA2EM + H_WEIGHT - oxidationWeight;
        return theoreticalMonoisotopicWeight;
    }
     */
}
