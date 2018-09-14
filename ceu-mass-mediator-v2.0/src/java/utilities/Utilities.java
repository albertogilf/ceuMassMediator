/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.List;
import oxidation.CompoundFA;
import persistence.oxidizedTheoreticalCompound.FACompound;
import static utilities.ConstantesForOxidation.H_WEIGHT;
import static utilities.ConstantesForOxidation.PROTON_WEIGHT;
import static utilities.ConstantesForOxidation.PCHEAD_WEIGHT;
import static utilities.OxidationLists.MAPOXIDATIONS;

/**
 *
 * @author alberto
 */
public final class Utilities {

    private Utilities() {

    }

    private final static double MDA_TO_DA = 0.001d;
    private final static double PPM_TO_DA = 0.000001d;

    /**
     * Calculate the MZ if the user has introduced the neutral mass based on a
     * protonated or deprotonated calcuation
     *
     * @param inputMass
     * @param massesMode
     * @param ionizationMode
     * @return
     */
    public static Double calculateMZFromNeutralMass(Double inputMass, int massesMode, int ionizationMode) {
        Double mzInputMass = inputMass;
        if (massesMode == 0 && ionizationMode == 1) {
            mzInputMass = inputMass + Constantes.PROTON_WEIGHT;
        } else if (massesMode == 0 && ionizationMode == 2) {
            mzInputMass = inputMass - Constantes.PROTON_WEIGHT;
        }
        return mzInputMass;
    }

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
    public static Double calculateTheoreticalExperimentalMassOfPCJPA(List<FACompound> FAs, String adductType) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (FACompound FA : FAs) {
            theoreticalMass = theoreticalMass + calculateTheoreticalMassOfOxidizedFAJPA(FA) - H_WEIGHT;
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
     * @param adductType
     * @return
     */
    public static Double calculateTheoreticalExperimentalMassOfPC(List<CompoundFA> FAs, String adductType) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (CompoundFA FA : FAs) {
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
    public static Double calculateTheoreticalNeutralMassOfPCJPA(List<FACompound> FAs) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (FACompound FA : FAs) {
            theoreticalMass = theoreticalMass + calculateTheoreticalMassOfOxidizedFAJPA(FA) - H_WEIGHT;
        }
        return theoreticalMass;
    }

    /**
     * Calculates the theoretical Experimental Mass of a Phosphocholine
     * depending on the FAs and the adduct type
     *
     * @param FAs
     * @return
     */
    public static Double calculateTheoreticalNeutralMassOfPC(List<CompoundFA> FAs) {
        double theoreticalMass;
        theoreticalMass = ConstantesForOxidation.PCHEAD_WEIGHT;
        for (CompoundFA FA : FAs) {
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
    public static String createNameOfPCJPA(List<FACompound> FAs) {
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
     * Calculates the theoretical Name for PC
     *
     * @param FAs
     * @return
     */
    public static String createNameOfPC(List<CompoundFA> FAs) {
        String PCName;
        PCName = "PC(";
        for (CompoundFA FA : FAs) {
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
    public static String createFormulaOfPCJPA(List<FACompound> FAs) {
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
     * Calculates the Formula for PC
     *
     * @param FAs
     * @return
     */
    public static String createFormulaOfPC(List<CompoundFA> FAs) {
        String PCFormula;
        int numDoubleBonds = 0;
        int numCarbons;
        numCarbons = 0;
        int numFAs = 0;
        for (CompoundFA FA : FAs) {
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
    public static Double calculateTheoreticalMassOfOxidizedFAJPA(FACompound FA) {
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
    public static Double calculateTheoreticalMassOfOxidizedFA(CompoundFA FA) {
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
        if(ParentIonNeutralMass == null || FAEM1 == null)
        {
            return null;
        }
        double FAEM2;
        double neutralMassFAEM1 = FAEM1 + PROTON_WEIGHT - H_WEIGHT;
        // Parent Ion Neutral mass - Mass of the PC Head - FA of Oxidized FA
        FAEM2 = ParentIonNeutralMass - PCHEAD_WEIGHT - neutralMassFAEM1;
        return FAEM2;
    }

    /**
     * Create a SQL condition between () from a list of Strings
     *
     * @param list
     * @return the SQL conition for Strings
     */
    public static String createSQLInStringFromListStrings(List<String> list) {
        String sqlCondition = "";
        if (!list.isEmpty()) {
            sqlCondition = "(";
        }
        for (String element : list) {
            sqlCondition = "'" + element + "',";
        }
        if (!list.isEmpty()) {
            sqlCondition = sqlCondition.substring(0, sqlCondition.length() - 1) + ")";
        }

        return sqlCondition;
    }
    
    /**
     * Calculate the delta to search based on the mass, the tolerance Mode and
     * the tolerance
     *
     * @param massToSearch Mass to search to calculate delta based on the
     * tolerance
     * @param toleranceMode 0 (ppm) or 1 (mDa)
     * @param tolerance Tolerance value
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double calculateDeltaPPM(Double massToSearch, Integer toleranceMode,
            Double tolerance) {
        Double delta;
        String toleranceModeString = DataFromInterfacesUtilities.toleranceTypeToString(toleranceMode);
        return calculateDeltaPPM(massToSearch,toleranceModeString,tolerance);
    }

    /**
     * Calculate the delta to search based on the mass, the tolerance Mode and
     * the tolerance
     *
     * @param massToSearch Mass to search to calculate delta based on the
     * tolerance
     * @param toleranceMode ppm or Mda
     * @param tolerance Tolerance value
     *
     * @return the mass difference within the tolerance respecting to the
     * massToSearch
     */
    public static Double calculateDeltaPPM(Double massToSearch, String toleranceMode,
            Double tolerance) {
        Double delta;
        switch (toleranceMode) {
            // Case mDa
            case "mDa":
                delta = tolerance * MDA_TO_DA;
                break;
            // Case ppm
            case "ppm":
                delta = massToSearch * tolerance * PPM_TO_DA;
                break;
            case "Da":
                delta = tolerance;
                break;
            default:
                delta = massToSearch * tolerance * PPM_TO_DA;
                break;
        }
        return delta;
    }

    public static String escapeSQLForREGEXP(String value) {
        value = value.replace("\\","\\\\");
        return value;
    }

    /**
     * Calculate the theoretical monoisotopic weight
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
