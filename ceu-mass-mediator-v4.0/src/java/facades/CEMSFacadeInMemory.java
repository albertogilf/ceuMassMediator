/*
 * CEMSFacadeInMemory.java
 *
 * Created on 02-ene-2020, 20:39:19
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package facades;

import CEMS.CEMSAnnotation;
import CEMS.CEMSAnnotationFragment;
import CEMS.CEMSAnnotationsGroupByAdduct;
import CEMS.CEMSCompound;
import CEMS.CEMSFeature;
import CEMS.CEMSFragment;
import List.NoDuplicatesList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.AdductProcessing;
import utilities.Utilities;

/**
 * Class to perform queries over the CE COMPOUNDS in memory
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 02-ene-2020
 *
 * @author Alberto Gil de la Fuente
 */
public final class CEMSFacadeInMemory {

    /**
     * Search the CEMSAnnotations for the mzs and MTs
     *
     * @param dbcandidates DB containing the CEMS Compounds
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param MTs
     * @param MTTolerance
     * @param MTToleranceMode
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @return
     */
    public static List<CEMSFeature> getCECompoundsFromMassesToleranceAndMTs(
            Set<CEMSCompound> dbcandidates,
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> MTs,
            Integer MTTolerance,
            String MTToleranceMode,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts) {
        Double expRMT = null;
        Double expEffMob = null;
        Integer bge = null;

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();

        Set<CEMSCompound> filteredDBCandidates = filterIntegerFormulaTypeCEMSCompounds(dbcandidates, chemAlphabet);

        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        //start the query
        Integer numMzs = mzs.size();
        Set<CEMSFragment> experimentalFragments = new TreeSet<>();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons = new TreeSet<>();

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expMT;
            try {
                expMT = MTs.get(i);
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expMT = null;
            }

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);

            setAnnotationsGroupByAdduct = new TreeSet<>();
            Set<CEMSCompound> filteredMTDBCandidates = filterMTCEMSCompounds(filteredDBCandidates, expMT, (double) MTTolerance, MTToleranceMode);

            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                Set<CEMSCompound> finalMZDBCandidates = filterMassesCEMSCompounds(filteredMTDBCandidates, massToSearch, (double) mzTolerance, mzToleranceMode);

                CEMSAnnotationsForOneAdduct = new TreeSet<>();

                //end = System.currentTimeMillis();
                //System.out.println("Time executing query: "+(end-start));
                //JDBCcounter = JDBCcounter + (end - start);
                CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromCompounds(massToSearch, adduct, expEffMob, expMT, expRMT,
                        experimentalFragments, finalMZDBCandidates);

                if (CEMSAnnotationsForOneAdduct.size() > 0) {
                    CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                            adduct, CEMSAnnotationsForOneAdduct);
                    setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                }
            }

            CEMSAnnotationsProductIons = getCEMSAnnotationsProductIonFromCompounds(
                    mzInputMass, mzToleranceMode, (double) mzTolerance,
                    expEffMob, expMT, expRMT, experimentalFragments, filteredMTDBCandidates);

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge,
                    experimentalFragments, ionMode, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    /**
     * Search the CEMSAnnotations for the mzs and MTs
     *
     * @param dbcandidates DB containing the CEMS Compounds
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param RMTs
     * @param RMTTolerance
     * @param RMTToleranceMode
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @param bge
     * @param bgeMT
     * @return
     */
    public static List<CEMSFeature> getCECompoundsFromMassesToleranceAndRMTs(
            Set<CEMSCompound> dbcandidates,
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> RMTs,
            Integer RMTTolerance,
            String RMTToleranceMode,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts,
            Integer bge,
            Double bgeMT) {
        Double expMT = null;
        Double expEffMob = null;
        if (bge == null || bgeMT == null) {
            throw new NullPointerException("BGEMT should have a value");
        }

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();

        Set<CEMSCompound> filteredDBCandidates = filterIntegerFormulaTypeCEMSCompounds(dbcandidates, chemAlphabet);

        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        //start the query
        Integer numMzs = mzs.size();
        Set<CEMSFragment> experimentalFragments = new TreeSet<>();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons = new TreeSet<>();

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expRMT;
            try {
                expRMT = RMTs.get(i);
                expMT = expRMT * bgeMT;
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expRMT = null;
            }

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);

            setAnnotationsGroupByAdduct = new TreeSet<>();
            Set<CEMSCompound> filteredMTDBCandidates = filterMTCEMSCompounds(filteredDBCandidates, expMT, (double) RMTTolerance, RMTToleranceMode);

            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                Set<CEMSCompound> finalMZDBCandidates = filterMassesCEMSCompounds(filteredMTDBCandidates, massToSearch, (double) mzTolerance, mzToleranceMode);

                CEMSAnnotationsForOneAdduct = new TreeSet<>();

                //end = System.currentTimeMillis();
                //System.out.println("Time executing query: "+(end-start));
                //JDBCcounter = JDBCcounter + (end - start);
                CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromCompounds(massToSearch, adduct, expEffMob, expMT, expRMT,
                        experimentalFragments, finalMZDBCandidates);
                if (CEMSAnnotationsForOneAdduct.size() > 0) {
                    CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                            adduct, CEMSAnnotationsForOneAdduct);
                    setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                }
            }

            CEMSAnnotationsProductIons = getCEMSAnnotationsProductIonFromCompounds(
                    mzInputMass, mzToleranceMode, (double) mzTolerance,
                    expEffMob, expMT, expRMT, experimentalFragments, filteredMTDBCandidates);

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge,
                    experimentalFragments, ionMode, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    /**
     *
     * @param dbCandidates
     * @param chemAlphabet
     * @return a list with a copy of the compounds from the candidates
     */
    public static Set<CEMSCompound> filterIntegerFormulaTypeCEMSCompounds(Set<CEMSCompound> dbCandidates, Integer chemAlphabet) {
        Set<CEMSCompound> filteredDBCandidates = new TreeSet<>();
        for (CEMSCompound cemscompound : dbCandidates) {
            if (Objects.equals(cemscompound.getFormula_type(), chemAlphabet)) {
                filteredDBCandidates.add(cemscompound);
            }
        }
        return filteredDBCandidates;
    }

    /**
     * Filters the dbcandidates removing from them the ones that are not within
     * the tolerance window based on the MT and the predicted MTs of the
     * dbCandidates
     *
     * @param dbCandidates
     * @param MT
     * @param MTTolerance
     * @param MTToleranceMode percentage or absolute
     * @return a list with a copy of the compounds from the candidates
     */
    public static Set<CEMSCompound> filterMTCEMSCompounds(Set<CEMSCompound> dbCandidates,
            Double MT, Double MTTolerance, String MTToleranceMode) {

        Set<CEMSCompound> filteredDBCandidates = new TreeSet<>();
        Double delta = Utilities.calculateDeltaPercentage(MT, MTToleranceMode, MTTolerance);
        for (CEMSCompound cemscompound : dbCandidates) {
            Double predictedMT = cemscompound.getMT();
            if (Math.abs(predictedMT - MT) < delta) {
                filteredDBCandidates.add(cemscompound);
            }
        }
        return filteredDBCandidates;
    }

    /**
     * Filters the dbcandidates removing from them the ones that are not within
     * the tolerance window of the masses
     *
     * @param dbCandidates
     * @param massToSearch
     * @param mzTolerance
     * @param mzToleranceMode 0 (ppm) or 1 (mDa)
     * @return a list with a copy of the compounds from the candidates
     */
    public static Set<CEMSCompound> filterMassesCEMSCompounds(Set<CEMSCompound> dbCandidates,
            Double massToSearch, Double mzTolerance, Integer mzToleranceMode) {

        Set<CEMSCompound> filteredDBCandidates = new TreeSet<>();
        Double delta = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, mzTolerance);
        for (CEMSCompound cemscompound : dbCandidates) {
            Double mass = cemscompound.getMass();
            if (Math.abs(mass - massToSearch) < delta) {
                filteredDBCandidates.add(cemscompound);
            }
        }
        return filteredDBCandidates;
    }

    private static Set<CEMSAnnotation> getCEMSAnnotationsFromCompounds(Double massToSearch, String adduct,
            Double expEffMob, Double expMT, Double expRMT, Set<CEMSFragment> experimentalFragments,
            Set<CEMSCompound> dbcandidates) {
        Set<CEMSAnnotation> CEMSAnnotationsFromFinalDBCandidates = new TreeSet<>();

        for (CEMSCompound cemscompound : dbcandidates) {
            
            Integer pc_id = null;
            try {
                pc_id = Integer.parseInt(cemscompound.getPc_id());
            } catch (NumberFormatException nfe) {

            }
            Integer chebi_id = null;
            try {
                chebi_id = Integer.parseInt(cemscompound.getChebi_id());
            } catch (NumberFormatException nfe) {

            }
            Integer mine_id = null;
            try {
                mine_id = Integer.parseInt(cemscompound.getMINE_id());
            } catch (NumberFormatException nfe) {

            }
            Integer npatlas_id = null;
            try {
                npatlas_id = Integer.parseInt(cemscompound.getNpatlas_id());
            } catch (NumberFormatException nfe) {

            }
            CEMSAnnotation cemsAnnotation = new CEMSAnnotation(massToSearch, adduct, expEffMob, expMT, expRMT,
                    experimentalFragments, cemscompound.getCe_exp_properties(), cemscompound.getEffMob(),
                    cemscompound.getMT(), cemscompound.getRMT(), cemscompound.getFragments(),
                    cemscompound.getCompound_id(), cemscompound.getMass(), cemscompound.getFormula(), cemscompound.getCompound_name(),
                    cemscompound.getCas_id(), cemscompound.getFormula_type(), cemscompound.getCompound_type(), cemscompound.getCompound_status(),
                    cemscompound.getCharge_type(), cemscompound.getCharge_number(), cemscompound.getLm_id(), cemscompound.getKegg_id(),
                    cemscompound.getHmdb_id(), cemscompound.getMetlin_id(), cemscompound.getIn_house_id(), pc_id,
                    chebi_id, mine_id, cemscompound.getKnapsack_id(), npatlas_id,
                    cemscompound.getAspergillus_id(), cemscompound.getMesh_nomenclature(), cemscompound.getIupac_classification(), cemscompound.getAspergillus_web_name(),
                    cemscompound.getFahfa_id(), cemscompound.getOh_position(),
                    cemscompound.getStructure(), cemscompound.getPathways(), false);

            CEMSAnnotationsFromFinalDBCandidates.add(cemsAnnotation);

        }
        return CEMSAnnotationsFromFinalDBCandidates;
    }

    private static Set<CEMSAnnotationFragment> getCEMSAnnotationsProductIonFromCompounds(
            Double massToSearch, Integer mzToleranceMode, Double mzTolerance,
            Double expEffMob, Double expMT, Double expRMT, Set<CEMSFragment> experimentalFragments,
            Set<CEMSCompound> filteredMTDBCandidates) {
        Set<CEMSAnnotationFragment> CEMSAnnotationProductIonsFromFinalDBCandidates = new TreeSet<>();
        Double delta = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, mzTolerance);
        for (CEMSCompound cemscompound : filteredMTDBCandidates) {
            for (CEMSFragment productIon : cemscompound.getFragments()) {
                Double mzProductIon = productIon.getMz();
                String fragment_type = productIon.getTransformation_type().toLowerCase();
                if (Math.abs(mzProductIon - massToSearch) < delta && fragment_type.contains("fragment")) {
                    CEMSAnnotationFragment cemsAnnotationProductIon = new CEMSAnnotationFragment(massToSearch, expEffMob, expMT, expRMT,
                            productIon.getEffMob(), productIon.getMT(), productIon.getRMT(), productIon.getIon_source_voltage(), mzProductIon,
                            productIon.getIntensity(), productIon.getTransformation_type(), productIon.getName(), cemscompound, true);
                    CEMSAnnotationProductIonsFromFinalDBCandidates.add(cemsAnnotationProductIon);
                }

            }

        }
        return CEMSAnnotationProductIonsFromFinalDBCandidates;
    }
}
