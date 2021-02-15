/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import CEMS.CEMSCompound;
import CEMS.CEMSFragment;
import exceptions.OxidationTypeException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import oxidation.CompoundFA;
import persistence.oxidizedTheoreticalCompound.FACompound;
import static utilities.ConstantesForOxidation.H_WEIGHT;
import static utilities.ConstantesForOxidation.PROTON_WEIGHT;
import static utilities.ConstantesForOxidation.PCHEAD_WEIGHT;
import static utilities.OxidationLists.MAPOXIDATIONMZS;

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
            mzInputMass = inputMass + Constants.PROTON_WEIGHT;
        } else if (massesMode == 0 && ionizationMode == 2) {
            mzInputMass = inputMass - Constants.PROTON_WEIGHT;
        }
        return mzInputMass;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param measuredMass Mass measured by MS
     * @param theoreticalMass Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double measuredMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((measuredMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Set the relative percentage difference between measured value and
     * theoretical value
     *
     * @param experimentalRMT RMT in CEMS experiment
     * @param theoreticalRMT RMT in CEMS experiment
     * @return
     */
    public static Integer calculatePercentageError(Double experimentalRMT, Double theoreticalRMT) {
        int RMTError;
        RMTError = (int) Math.round(Math.abs((experimentalRMT - theoreticalRMT) / theoreticalRMT * 100));
        return RMTError;
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
     * @param oxidationType
     * @return
     * @throws exceptions.OxidationTypeException
     */
    public static String createFormulaOfPC(List<CompoundFA> FAs, String oxidationType) throws OxidationTypeException {
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

        int numHydrogensOxidationType = getNumHydrogensOxidationType(oxidationType);
        int NumOxigensOxidationType = getNumOxigensOxidationType(oxidationType);
        int numHydrogens = ConstantesForOxidation.PCHEAD_H - numFAs + numCarbons * 2 - 2 * numDoubleBonds;
        numHydrogens = numHydrogens + numHydrogensOxidationType;
        numCarbons = numCarbons + ConstantesForOxidation.PCHEAD_C;
        int numOxygens = ConstantesForOxidation.PCHEAD_O + 2 * numFAs;
        numOxygens = numOxygens + NumOxigensOxidationType;
        PCFormula = "C" + numCarbons + "H" + numHydrogens + "NO" + numOxygens + "P";
        return PCFormula;
    }

    /**
     * Calculates the number of hydrogens according to the oxidation type
     *
     * @param oxidationType
     * @return
     * @throws exceptions.OxidationTypeException
     */
    public static Integer getNumHydrogensOxidationType(String oxidationType) throws OxidationTypeException {
        if (OxidationLists.MAPOXIDATIONHYDROGENS.containsKey((oxidationType))) {
            return OxidationLists.MAPOXIDATIONHYDROGENS.get(oxidationType);
        } else {
            throw new OxidationTypeException("Oxidation type not recognized");
        }
    }

    /**
     * Calculates the number of oxigens according to the oxidation type
     *
     * @param oxidationType
     * @return
     * @throws exceptions.OxidationTypeException
     */
    public static Integer getNumOxigensOxidationType(String oxidationType) throws OxidationTypeException {
        if (OxidationLists.MAPOXIDATIONOXIGENS.containsKey((oxidationType))) {
            return OxidationLists.MAPOXIDATIONOXIGENS.get(oxidationType);
        } else {
            throw new OxidationTypeException("Oxidation type not recognized");
        }
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
        if (MAPOXIDATIONMZS.containsKey(oxidationType)) {
            oxidationWeight = Double.parseDouble(MAPOXIDATIONMZS.get(oxidationType));
        } else {
            oxidationWeight = 0;
        }
        theoreticalMass = numCarbons * PeriodicTable.MAPELEMENTS.get(Element.C)
                + (numCarbons * 2 - 2 * numDoubleBonds) * PeriodicTable.MAPELEMENTS.get(Element.H)
                + 2 * PeriodicTable.MAPELEMENTS.get(Element.O) - oxidationWeight;
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
        if (MAPOXIDATIONMZS.containsKey(oxidationType)) {
            oxidationWeight = Double.parseDouble(MAPOXIDATIONMZS.get(oxidationType));
        } else {
            oxidationWeight = 0;
        }
        theoreticalMass = numCarbons * PeriodicTable.MAPELEMENTS.get(Element.C)
                + (numCarbons * 2 - 2 * numDoubleBonds) * PeriodicTable.MAPELEMENTS.get(Element.H)
                + 2 * PeriodicTable.MAPELEMENTS.get(Element.O) - oxidationWeight;
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
        theoreticalMass = numCarbons * PeriodicTable.MAPELEMENTS.get(Element.C)
                + (numCarbons * 2 - 2 * numDoubleBonds) * PeriodicTable.MAPELEMENTS.get(Element.H)
                + 2 * PeriodicTable.MAPELEMENTS.get(Element.O);
        return theoreticalMass;
    }

    /**
     *
     * @param ParentIonNeutralMass
     * @param FAEM1
     * @return
     */
    public static Double calculateFAEMFromPIandOtherFAEM(Double ParentIonNeutralMass, Double FAEM1) {
        if (ParentIonNeutralMass == null || FAEM1 == null) {
            return null;
        }
        double FAEM2;
        double neutralMassFAEM1 = FAEM1 + PROTON_WEIGHT - H_WEIGHT;
        // Parent Ion Neutral mass - Mass of the PC Head - FA of Oxidized FA
        FAEM2 = ParentIonNeutralMass - PCHEAD_WEIGHT - neutralMassFAEM1;
        return FAEM2;
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
        String toleranceModeString = DataFromInterfacesUtilities.toleranceTypeToString(toleranceMode);
        return calculateDeltaPPM(massToSearch, toleranceModeString, tolerance);
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

    /**
     * Calculate the delta to search based on the RMT, the RMT Tolerance Mode
     * and the RMT Tolerance
     *
     * @param ValueToSearch Mass RMT search to calculate delta based on the RMT
     * tolerance. If null, the delta will be very high (5000) to search into all
     * masses
     * @param ValueToleranceMode % or abs
     * @param ValueTolerance RMTTolerance value
     *
     * @return the mass difference within the tolerance respecting to the
     * Relative migration time
     */
    public static Double calculateDeltaPercentage(Double ValueToSearch, String ValueToleranceMode,
            Double ValueTolerance) {
        if (ValueToSearch == null) {
            return 5000d;
        }
        Double delta;
        switch (ValueToleranceMode) {
            // Case mDa
            case "percentage":
                delta = ValueToSearch * (ValueTolerance / 100);
                break;
            // Case ppm
            case "absolute":
                delta = ValueTolerance;
                break;
            default:
                delta = ValueTolerance;
                break;
        }
        return delta;
    }

    public static String escapeSQLForREGEXP(String value) {
        value = value.replace("\\", "\\\\");
        return value;
    }

    public static String generateStringFragmentsNointensity(Set<CEMSFragment> ceProductIons) {
        String fragments = "";
        for (CEMSFragment ceProductIon : ceProductIons) {
            Double mz = ceProductIon.getMz();
            fragments = fragments + mz + ", ";
        }
        return (fragments.length() <= 2) ? fragments : fragments.substring(0, fragments.length() - 2);
    }

    public static String generateStringFragments(Set<CEMSFragment> ceProductIons) {
        String fragments = "";
        for (CEMSFragment ceProductIon : ceProductIons) {
            Double mz = ceProductIon.getMz();
            Double intensity = ceProductIon.getIntensity();
            fragments = fragments + "(" + mz + ", " + intensity + "), ";
        }
        return (fragments.length() <= 2) ? fragments : fragments.substring(0, fragments.length() - 2);
        

    }

    /**
     *
     * @param cemsCompounds
     * @param dbmobilities
     * @param IDMarker1
     * @param IDMarker2
     * @return the effective mobility of the marker sent in IDMarker, null if
     * the IDMarker is not present in the database
     */
    public static Double[] getDBMobilitiesFromCEMSCompounds(Map<Integer, CEMSCompound> cemsCompounds,
            float[] dbmobilities, Integer IDMarker1, Integer IDMarker2) {
        Integer i = 0;
        Double[] markerMoblities = new Double[2];
        Boolean IDMarker1Found = false;
        Boolean IDMarker2Found = false;
        for (Map.Entry<Integer, CEMSCompound> pair : cemsCompounds.entrySet()) {
            Integer compoundId = pair.getKey();
            double effMob = pair.getValue().getEffMob();
            if (Objects.equals(compoundId, IDMarker1)) {
                markerMoblities[0] = effMob;
                IDMarker1Found = true;
            }
            if (Objects.equals(compoundId, IDMarker2)) {
                markerMoblities[1] = effMob;
                IDMarker2Found = true;
            }
            dbmobilities[i] = (float) effMob;
            i++;
        }
        if (!IDMarker1Found) {
            markerMoblities[0] = null;
        }
        if (!IDMarker2Found) {
            markerMoblities[1] = null;
        }
        return markerMoblities;
    }

    /**
     * Fill the CEMSCompounds in the map with the predicted MTs. The order of
     * the predicted MTs should be the same than the cemsCompounds
     *
     * @param cemsCompounds
     * @param predictedMTs
     */
    public static void fillMTs(Map<Integer, CEMSCompound> cemsCompounds,
            float[] predictedMTs) {
        Integer i = 0;

        Integer cemsCompoundsSize = cemsCompounds.size();
        Integer dbMobilitiesSize = predictedMTs.length;
        if (dbMobilitiesSize < cemsCompoundsSize) {
            System.out.println("FILLING PREDICTED MTs with smaller size than DB compounds");
        }
        for (Map.Entry<Integer, CEMSCompound> pair : cemsCompounds.entrySet()) {
            Integer compoundId = pair.getKey();
            CEMSCompound cemscompound = pair.getValue();
            double predictedMT = (double) predictedMTs[i];
            cemscompound.setMT(predictedMT);
            i++;
        }
    }

    /**
     * Fill the CEMSCompounds in the map with the predicted MTs. The order of
     * the predicted MTs should be the same than the cemsCompounds
     *
     * @param cemsCompounds
     * @param predictedMTs
     * @param bge
     * @return the predicted MT of the BGE
     */
    public static Double fillRMTs(Map<Integer, CEMSCompound> cemsCompounds,
            float[] predictedMTs, Integer bge) {
        Integer i = 0;
        Integer cemsCompoundsSize = cemsCompounds.size();
        Integer dbMobilitiesSize = predictedMTs.length;
        if (dbMobilitiesSize < cemsCompoundsSize) {
            System.out.println("FILLING PREDICTED MTs with smaller size than DB compounds");
        }
        Double bgeMT = null;
        for (Map.Entry<Integer, CEMSCompound> pair : cemsCompounds.entrySet()) {
            Integer compoundId = pair.getKey();
            if (Objects.equals(compoundId, bge)) {
                CEMSCompound cemscompound = pair.getValue();
                bgeMT = (double) predictedMTs[i];
                i++;
                break;
            }
        }
        if (bgeMT == null) {
            throw new NullPointerException("MT of the BGE not found");
        }
        i = 0;
        for (Map.Entry<Integer, CEMSCompound> pair : cemsCompounds.entrySet()) {
            Integer compoundId = pair.getKey();
            CEMSCompound cemscompound = pair.getValue();
            double predictedMT = (double) predictedMTs[i];
            double predictedRMT = predictedMT / bgeMT;
            cemscompound.setMT(predictedMT);
            cemscompound.setRMT(predictedRMT);
            i++;
        }
        return bgeMT;
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
        double oxidationWeight = Double.parseDouble(MAPOXIDATIONMZS.get(oxidationType));
        // 16:0 -> 256.2402302664
        // 20:4 -> 304.2402302664
        // PC HEAD -> 223.0974
        theoreticalMonoisotopicWeight = PCHEAD_WEIGHT + FA1EM + H_WEIGHT + FA2EM + H_WEIGHT - oxidationWeight;
        return theoreticalMonoisotopicWeight;
    }
     */
}
