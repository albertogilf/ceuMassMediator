/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import java.io.Serializable;
import static java.lang.Math.abs;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import msms.MSMSCompound;
import msms.Msms;
import msms.Peak;
import msms.ScoreComparator;
import static utilities.Constants.MIN_SCORE_FOR_EUCLIDEAN_DISTANCE;
import utilities.Utilities;

/**
 * Facade for MSMS. It is the middleware between the bean and the database. It
 * contains all the algorithms to search and annotate MSMS Spectra from the
 * user.
 *
 * @author Maria Postigo. Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 15/04/2018
 */
public final class MSMSFacade extends MSFacade implements Serializable {

    private static final double MZEXP = 3d;
    private static final double INTEXP = 0.6d;
    private static final double EUCLIDEAN_EXP_MZ = 4d;
    private static final double EUCLIDEAN_EXP_INTENSITY = 2d;
    
    
    public MSMSFacade() {
        super();
    }


    /**
     * To get the peaks from the user's input string separates each line into mz
     * and intensity
     *
     * @param queryPeaks
     * @return list of object peaks
     */
    public List<Peak> getPeaks(String queryPeaks) {

        List<Peak> peaks = new LinkedList<>();
        String[] p = queryPeaks.split("\n");

        for (String p1 : p) {
            String[] mzi = p1.split(" ");
            //System.out.println(mzi[0]);
            //System.out.println(mzi[1]);
            double mzPeak = Double.parseDouble(mzi[0]);
            double intensityPeak = Double.parseDouble(mzi[1]);
            Peak peak = new Peak(mzPeak, intensityPeak);
            peaks.add(peak);
        }
        return peaks;

    }

    /**
     * Get the msms candidates acording to the precursor ion mass and the
     * tolerance. Tolerance is specified in PPM.
     *
     * @param inputMsms
     * @param precursorIonTolerance
     * @param toleranceType
     * @return a list msms candidates from compound with a mass within the
     * tolerance window
     */
    public List<MSMSCompound> getMsmsCandidates(Msms inputMsms, Double precursorIonTolerance, String toleranceType) {

        //System.out.println("PRECURSOR ION TOLERANCE MODE: " +precursorIonTolerance+" " +toleranceType);
        double massToSearch = inputMsms.getPrecursorIonNeutralMass();
        precursorIonTolerance = Utilities.calculateDeltaPPM(massToSearch, toleranceType, precursorIonTolerance);
        //System.out.println("CONVERTED: "+precursorIonTolerance);

        return getMsmsCandidates(inputMsms, precursorIonTolerance);
    }

    /**
     * Get the msms candidates acording to the precursor ion mass and the
     * tolerance. Tolerance is specified in Da.
     *
     * @param inputMsms
     * @param precursorIonTolerance
     * @return a list msms candidates from compound with a mass within the
     * tolerance window
     */
    public List<MSMSCompound> getMsmsCandidates(Msms inputMsms, double precursorIonTolerance) {

        // System.out.println("ENTERING AT SET MSMS CANDIDATES");
        // System.out.println("Tolerance: " + precursorIonTolerance);
        List<MSMSCompound> candidates = new LinkedList<>();
        try {
            double precursorIonNeutralMass = inputMsms.getPrecursorIonNeutralMass();
            // System.out.println("EXPERIMENTAL MASS: " + inputMsms.getPrecursorIonMZ() + " NEUTRAL MASS: " + precursorIonNeutralMass);
            String sql;
            ResultSet rs;
            PreparedStatement prepSt;
            String voltageLevelType = inputMsms.getVoltageLevel().toLowerCase();
            int ionizationMode = inputMsms.getIonizationMode();
            int spectraType = inputMsms.getSpectraType();

            if (!voltageLevelType.equalsIgnoreCase("all")) {
                // If experimental and predicted is selected
                if (inputMsms.getSpectraType() == 2) {
                    sql = "SELECT msms_id, c.hmdb_id, voltage, voltage_level, ionization_mode,"
                            + "msms.compound_id, msms.predicted, "
                            + "c.compound_name, c.formula, c.mass, c.inchi"
                            + " FROM msms msms "
                            + " inner JOIN compounds_view c ON c.compound_id=msms.compound_id "
                            + " WHERE c.mass BETWEEN ? and ?"
                            + " and msms.voltage_level=?"
                            + " and msms.ionization_mode=?";
                    prepSt = conn.prepareStatement(sql);
                    prepSt.setDouble(1, (precursorIonNeutralMass - precursorIonTolerance));
                    prepSt.setDouble(2, (precursorIonNeutralMass + precursorIonTolerance));
                    prepSt.setString(3, voltageLevelType);
                    prepSt.setInt(4, ionizationMode);
                } else {
                    sql = "SELECT msms_id, c.hmdb_id, voltage, voltage_level, ionization_mode,"
                            + "msms.compound_id, msms.predicted, "
                            + "c.compound_name, c.formula, c.mass, c.inchi"
                            + " FROM msms msms "
                            + " inner JOIN compounds_view c ON c.compound_id=msms.compound_id "
                            + " WHERE c.mass BETWEEN ? and ?"
                            + " and msms.voltage_level=?"
                            + " and msms.ionization_mode=?"
                            + " and msms.predicted=?";
                    prepSt = conn.prepareStatement(sql);
                    prepSt.setDouble(1, (precursorIonNeutralMass - precursorIonTolerance));
                    prepSt.setDouble(2, (precursorIonNeutralMass + precursorIonTolerance));
                    prepSt.setString(3, voltageLevelType);
                    prepSt.setInt(4, ionizationMode);
                    prepSt.setInt(5, spectraType);
                }
                // If all the voltages are selected.
            } else {
                // If experimental and predicted is selected
                if (inputMsms.getSpectraType() == 2) {
                    sql = "SELECT msms_id, c.hmdb_id, voltage, voltage_level, ionization_mode,"
                            + "msms.compound_id, msms.predicted, "
                            + "c.compound_name, c.formula, c.mass, c.inchi"
                            + " FROM msms msms "
                            + " inner JOIN compounds_view c ON c.compound_id=msms.compound_id "
                            + " WHERE c.mass BETWEEN ? and ?"
                            + " and msms.ionization_mode=?";
                    prepSt = conn.prepareStatement(sql);
                    prepSt.setDouble(1, (precursorIonNeutralMass - precursorIonTolerance));
                    prepSt.setDouble(2, (precursorIonNeutralMass + precursorIonTolerance));
                    prepSt.setInt(3, ionizationMode);
                } else {
                    sql = "SELECT msms_id, c.hmdb_id, voltage, voltage_level, ionization_mode,"
                            + "msms.compound_id, msms.predicted, "
                            + "c.compound_name, c.formula, c.mass, c.inchi"
                            + " FROM msms msms "
                            + " inner JOIN compounds_view c ON c.compound_id=msms.compound_id "
                            + " WHERE c.mass BETWEEN ? and ?"
                            + " and msms.ionization_mode=?"
                            + " and msms.predicted=?";
                    prepSt = conn.prepareStatement(sql);
                    prepSt.setDouble(1, (precursorIonNeutralMass - precursorIonTolerance));
                    //System.out.println((precursorIonNeutralMass - precursorIonTolerance)+" and "+(precursorIonNeutralMass + precursorIonTolerance));
                    prepSt.setDouble(2, (precursorIonNeutralMass + precursorIonTolerance));
                    prepSt.setInt(3, ionizationMode);
                    //System.out.println("ion mode: "+ionizationMode);
                    prepSt.setInt(4, spectraType);
                    //System.out.println("spectra type: "+spectraType);
                }
            }
            // Precursor ion mass will be between precursor mass - tolerance and precursor mass + tolerance

            rs = prepSt.executeQuery();

            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                String hmdbId = rs.getString("hmdb_id");
                int voltageCandidate = rs.getInt("voltage");
                String voltageLevelCandidate = rs.getString("voltage_level");
                int ionizationModeCandidate = rs.getInt("ionization_mode");
                int compoundId = rs.getInt("compound_id");
                int spectraTypeCandidate = rs.getInt("predicted");
                String compoundName = rs.getString("compound_name");
                String formula = rs.getString("formula");
                double mass = rs.getDouble("mass");
                // not used YET
                String inchi = rs.getString("inchi");

                // Set to each msms its' corresponding peaks
                List<Peak> peaksFromDatabase = getPeaksFromDataBase(msms_id);

                MSMSCompound libraryMsms = new MSMSCompound(
                        ionizationModeCandidate, voltageCandidate,
                        voltageLevelCandidate, peaksFromDatabase, spectraTypeCandidate,
                        msms_id, compoundId, hmdbId, compoundName, formula, mass);

                candidates.add(libraryMsms);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSMSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return candidates;
    }

    /**
     * Get the peaks from the database for the specified msms
     *
     * @param msms_id
     * @return a list of the peaks corresponding to the msms from the database
     * with the specified msms_id
     */
    private List<Peak> getPeaksFromDataBase(int msms_id) {
        String sql = "SELECT msms_peaks.* FROM msms_peaks INNER JOIN msms "
                + "ON msms.msms_id=msms_peaks.msms_id "
                + "WHERE msms_peaks.msms_id= ?";

        List<Peak> libraryPeakList = new LinkedList<>();

        try {
            PreparedStatement prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, msms_id);
            ResultSet rs1 = prepSt.executeQuery();

            while (rs1.next()) {
                double mzPeak = (float) rs1.getDouble("mz");
                double intensityPeak = (float) rs1.getDouble("intensity");
                int msmsIdPeak = rs1.getInt("msms_id");
                Peak libraryPeak = new Peak(mzPeak, intensityPeak, msmsIdPeak);//fresh instance

                libraryPeakList.add(libraryPeak);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSMSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return libraryPeakList;
    }

    /**
     * Scores the candidates obtained from candidatesList for the input spectra
     * in inputMSMS, within a toleranze mzTolerance in ppm
     *
     * @param inputMsms the original MSMS introduced by the user
     * @param candidatesList The cadidates obtained by getMsmsCandidates
     * @param mzTolerance the tolerance for the peaks in Da. (mz)
     * @param toleranceType
     * @return a list of of scored MSMSCompounds
     */
    public List<MSMSCompound> scoreMatchedPeaks(Msms inputMsms, List<MSMSCompound> candidatesList,
            Double mzTolerance, String toleranceType) {
        //System.out.println("INITIAL MZ TOLERANCE: "+mzTolerance+" "+toleranceType);
        double massToSearch = inputMsms.getPrecursorIonNeutralMass();
        mzTolerance = Utilities.calculateDeltaPPM(massToSearch, toleranceType, mzTolerance);
        //System.out.println("CALCULATED: "+mzTolerance);

        return scoreMatchedPeaks(inputMsms, candidatesList, mzTolerance);
    }

    /**
     * Scores the candidates obtained from candidatesList for the input spectra
     * in inputMSMS, within a toleranze mzTolerance in Da
     *
     * @param inputMsms the original MSMS introduced by the user
     * @param candidatesList The cadidates obtained by getMsmsCandidates
     * @param mzTolerance the tolerance for the peaks in Da. (mz)
     * @return a list of of scored MSMSCompounds
     */
    public List<MSMSCompound> scoreMatchedPeaks(Msms inputMsms, List<MSMSCompound> candidatesList, double mzTolerance) {

        Iterator candidates_iterator = candidatesList.iterator();
        List<MSMSCompound> candidatesScored = new LinkedList<>();
        List<Peak> matchedpeaks_candidades;
        List<Peak> matchedpeaks_input;
        //Peaks to normalize:
        List<Peak> allLibraryPeaks;
        //I will compare each input Peak with the library peaks
        // The list of input peaks does not change
        List<Peak> allInputPeaks = inputMsms.getNormalizedPeaks();
        // The compareTo was overwritten in order to sort the peask acording to mz
        Collections.sort(allInputPeaks);
        // Fisrt run the whole list of candidates

        while (candidates_iterator.hasNext()) {
            MSMSCompound msmsToScore = (MSMSCompound) candidates_iterator.next();
            List<Peak> candidatePeaksNormalized = msmsToScore.getNormalizedPeaks();
            allLibraryPeaks = candidatePeaksNormalized;
            Collections.sort(candidatePeaksNormalized);//sorted by mz
            ListIterator inputPeaks_iterator = allInputPeaks.listIterator();
            matchedpeaks_candidades = new LinkedList<>();
            matchedpeaks_input = new LinkedList<>();
            // Run the list of peaks from the input

            while (inputPeaks_iterator.hasNext()) {
                Peak inputPeak = (Peak) inputPeaks_iterator.next();

                // Run the list of peaks from the candidated
                int indexMatch = -1;
                float scoreForPeak = 0f;
                for (Peak match : candidatePeaksNormalized) {

                    if (inputPeak.peakMatch(match, mzTolerance)) {

                        float scoreForMatch;
                        scoreForMatch = euclideanDistanceWeighted(inputPeak, match);
                        if (scoreForMatch > scoreForPeak) {//to get the best match
                            scoreForPeak = scoreForMatch;
                            indexMatch = candidatePeaksNormalized.indexOf(match);
                        }

                    }

                }
                if (indexMatch > -1) {
                    if (!(matchedpeaks_input.contains(inputPeak) || matchedpeaks_candidades.contains(candidatePeaksNormalized.get(indexMatch)))) {
                        matchedpeaks_input.add(inputPeak);
                        matchedpeaks_candidades.add(candidatePeaksNormalized.get(indexMatch));
                    }

                }

            }

            if (!matchedpeaks_candidades.isEmpty()) {

                msmsToScore.setScore(scoreMsmsAsMetFrag(matchedpeaks_candidades, matchedpeaks_input, inputMsms/*, allLibraryPeaks*/));
                //System.out.println("Score: "+msmsToScore.getScore());
                //msmsToScore.setScore(scoreMsmsAsMyCompound(matchedpeaks_candidades,matchedpeaks_input, inputMsms/*, allLibraryPeaks*/));
                //msmsToScore.setScore(scoreMsmsByEuclideanDistance(matchedpeaks_candidades, matchedpeaks_input));
                candidatesScored.add(msmsToScore);
                Collections.sort(candidatesScored, new ScoreComparator());
            }
        }

        return candidatesScored;
    }

    /**
     * Performs a two dimensional weighted dot product between two lists of
     * peaks
     *
     * @param matchedPeaks_candidates The peaks matched from a candidate
     * @param matchedPeaks_input The peaks matched from the input
     * @param mzExp weight for mz
     * @param intExp weight for intensity
     * @return the dot product
     */
    private double dotProduct(List<Peak> matchedPeaks_candidates, List<Peak> matchedPeaks_input, double mzExp, double intExp) {
        Collections.sort(matchedPeaks_candidates);
        Collections.sort(matchedPeaks_input);

        Iterator it_matched = matchedPeaks_candidates.iterator();
        Iterator it_input = matchedPeaks_input.iterator();
        double score = 0;
        while (it_matched.hasNext()) {
            Peak peak_candidate = (Peak) it_matched.next();
            Peak peak_input = (Peak) it_input.next();
            score += Math.pow(peak_candidate.getMz(), mzExp) * Math.pow(peak_input.getMz(), mzExp) + Math.pow(peak_candidate.getIntensity(), intExp) * Math.pow(peak_input.getIntensity(), intExp);
        }
        return score;
    }

    /**
     * Performs a score as MyCompound software
     *
     * @param matchedPeaks_candidates The peaks matched from a candidate
     * @param matchedPeaks_input The peaks matched from the input
     * @param inputMsms The input Msms from the user
     * @return the score
     */
    private double scoreMsmsAsMyCompound(List<Peak> matchedpeaks_candidates, List<Peak> matchedpeaks_input, Msms inputMsms/*, List <Peak> allLibraryPeaks*/) {
        //at my compound an initial weight is obtained from the dot product between the matched peaks (intensities and mz)
        List<Peak> inputPeaks = inputMsms.getNormalizedPeaks();
        double score = dotProduct(matchedpeaks_candidates, matchedpeaks_input, 1, 1);
        double fitScore = score / dotProduct(inputPeaks, inputPeaks, 1, 1);
        return fitScore;
//          List<Peak> allInputPeaks = inputMsms.getNormalizedPeaks();
//        //calculating score (vs input)
//        double numeratorInput= similarityMeasure(matchedInput, matchedLibrary, 1, 1);
//        double denominatorInput=dotProduct(allInputPeaks, allInputPeaks, 1, 1);
//        double A= (numeratorInput/denominatorInput);
//
//        //calculating score (vs library)
//        double numeratorLibrary= similarityMeasure(matchedLibrary,matchedInput, 1, 1);
//        double denominatorLibrary=dotProduct(allLibraryPeaks, allLibraryPeaks, 1, 1);
//        double B=(numeratorLibrary/denominatorLibrary);
//
//        double fitScore= (A+B)/2; //The maximum score is 1

    }

    /**
     * Performs a score as MetFrag software
     *
     * @param matchedPeaks_candidates The peaks matched from a candidate
     * @param matchedPeaks_input The peaks matched from the input
     * @param inputMsms The input Msms from the user
     * @return the score
     */
    private double scoreMsmsAsMetFrag(List<Peak> matchedpeaks_candidates, List<Peak> matchedpeaks_input, Msms inputMsms /* , List <Peak> allLibraryPeaks*/) {
        //at metfrag an initial weight is obtained from the weighted dot product between the matched peaks (intensities and mz)
        List<Peak> inputPeaks = inputMsms.getNormalizedPeaks();
        double score = dotProduct(matchedpeaks_candidates, matchedpeaks_input, MZEXP, INTEXP);
        // System.out.println("Score before normalization: " + score);
        double fitScore = score / dotProduct(inputPeaks, inputPeaks, MZEXP, INTEXP);
//        List<Peak> allInputPeaks = inputMsms.getNormalizedPeaks();
//        //calculating score (vs input)
//        double numeratorInput= similarityMeasure(matchedInput, matchedLibrary, MZEXP, INTEXP);
//
//        double denominatorInput=dotProduct(allInputPeaks, allInputPeaks, MZEXP, INTEXP);
//        double A= (numeratorInput/denominatorInput);
//
//
//        //calculating score (vs library)
//        double numeratorLibrary= similarityMeasure(matchedLibrary,matchedInput, MZEXP, INTEXP);
//
//        double denominatorLibrary=dotProduct(allLibraryPeaks, allLibraryPeaks, MZEXP, INTEXP);
//
//        double B=(numeratorLibrary/denominatorLibrary);
//
//
//        double fitScore= (A+B)/2; //The maximum score is 1

        return fitScore;
    }

    public static double scoreMsmsByEuclideanDistance(List<Peak> matchedpeaks, List<Peak> matchedpeaks_input) {
        double score;
        //the score is preformed as the euclidean distance between the peaks.
        double sumTosqrt = 0d;//euclidean distance berfore squared root
        Collections.sort(matchedpeaks);

        int n = matchedpeaks.size();
        Iterator it = matchedpeaks.iterator();

        Collections.sort(matchedpeaks_input);//sorted by mz
        if (matchedpeaks.size() == matchedpeaks_input.size()) {
            for (Peak input : matchedpeaks_input) {
                while (it.hasNext()) {
                    Peak candidate = (Peak) it.next();

                    double mz = Math.pow(candidate.getMz() - input.getMz(), 2);
                    double inte = Math.pow((candidate.getIntensity() - input.getIntensity()), 2);
                    sumTosqrt += mz + inte;
                    break; //just one match per peak

                }
            }
        } else {
            System.out.println("SOMETHING WENT WRONG");
        }

        score = Math.sqrt(sumTosqrt);

        return 1 / score;
    }

    private static double similarityMeasure(List<Peak> outerPeaks, List<Peak> innerPeaks, double mzExp, double intExp) {
        double mz_score = 0;
        double int_score = 0;
        Collections.sort(outerPeaks);
        Collections.sort(innerPeaks);
        Iterator it_inner = innerPeaks.iterator();
        Iterator it_outer = outerPeaks.iterator();
        double score;
        while (it_inner.hasNext()) {
            Peak libraryPeak = (Peak) it_inner.next();
            Peak inputPeak = (Peak) it_outer.next();

            double mz_outer = inputPeak.getMz();
            double mz_inner = libraryPeak.getMz();
            double int_outer = inputPeak.getIntensity();
            double int_inner = libraryPeak.getIntensity();

            double mzDiff = Math.abs((mz_inner - mz_outer));
            if (mzDiff <= 0.1) {
                mz_score = mz_score + Math.pow((mz_outer * mz_inner), mzExp);
            } else {
                double product = (mz_outer * (mz_outer - Math.abs((mz_outer - mz_inner))));
                mz_score = mz_score + Math.pow(product, mzExp);
            }

            double intUpperLimit = (0.2 * int_outer) + int_outer;
            double intLowerLimit = (0.2 * int_outer) - int_outer;
            if (int_inner > intLowerLimit && int_inner < intUpperLimit) {
                int_score = int_score + Math.pow((int_inner * int_outer), intExp);
            } else {
                double minimun = Math.max((int_outer * int_outer * 0.1), (int_outer * (int_outer - Math.abs((int_outer - int_inner)))));
                int_score = int_score + Math.pow(minimun, intExp);
            }
        }

        score = int_score + mz_score;
        return score;

    }

    private static List<Peak> getMatchFromInput(List<Peak> matchedpeaks, List<Peak> inputPeaks) {
        List<Peak> inputMatch = new LinkedList<>();
        Peak inMatchP = null;
        Iterator itLib = matchedpeaks.iterator();
        Iterator itIn = inputPeaks.iterator();
        Double min = 10d;//por ejemplo
        while (itLib.hasNext()) {
            Peak pLib = (Peak) itLib.next();
            while (itIn.hasNext()) {
                Double diff;
                Peak pIn = (Peak) itIn.next();
                diff = abs(pLib.getMz() - pIn.getMz());
                if (diff < min) {
                    min = diff;
                    inMatchP = pIn;
                }
            }
            if (inMatchP != null) {
                inputMatch.add(inMatchP);
            }
        }
        return inputMatch;
    }

    /**
     * From a list of MSMSCompounds returns a top N according to the best scores
     *
     * @param MsmsList The list of MSMSCompound
     * @param N the top number to return
     * @return a list of MSMSCompounds of size N
     */
    public List<MSMSCompound> getTopNMatches(List<MSMSCompound> MsmsList, int N) //N is the rank of best matches
    {
        Collections.sort(MsmsList, new ScoreComparator());
        List<MSMSCompound> topN = new LinkedList<>();
        List<Integer> topNCompound_id = new LinkedList<>();
        for (MSMSCompound msms : MsmsList) {
            if (N > 0 && msms.getScore() > 0) {
                int n = msms.getCompound_id();
                if (!topNCompound_id.contains(n)) {
                    topNCompound_id.add(n);
                    topN.add(msms);
                    N--;
                }
            }
        }
        normalizeScores(topN);
        return topN;
    }

    /**
     * Measures the inverse of the euclidean distance between a peak from de
     * input and a peak from a candidate.
     *
     * @param inputPeak is a peak from the input peaks introduced bu the user
     * @param match it is a peak that comes from a candidate
     * @return a score of similarity among both peaks
     */
    private float euclideanDistanceWeighted(Peak inputPeak, Peak match) {
        float score;
        double mz_diff = Math.abs(inputPeak.getMz() - match.getMz());
        double intensity_diff = Math.abs(inputPeak.getIntensity() - match.getIntensity());
        double distance = Math.sqrt(Math.pow(mz_diff, EUCLIDEAN_EXP_MZ) + Math.pow(intensity_diff, EUCLIDEAN_EXP_INTENSITY));
        score = (float) ((float) 1 / distance);

        if (MIN_SCORE_FOR_EUCLIDEAN_DISTANCE > score) {
            return 0;
        }
        return score;

    }

    private void normalizeScores(List<MSMSCompound> topN) {
        if (!topN.isEmpty()) {
            double maxScore = topN.get(0).getScore();
            if (maxScore <= 1) {
                return;
            }
            for (MSMSCompound c : topN) {
                double score = c.getScore();
                double score_normalized = score / maxScore;
                c.setScore(score_normalized);
            }
        }
    }
    
    
    public List<MSMSCompound> getMSMSFromCompoundByMassAndTolerance(double mass, double tolerance) {
        String sql = "SELECT m.ionization_mode, m.voltage, m.voltage_level, m.predicted, "
                + "m.msms_id, m.compound_id, c.compound_name, m.hmdb_id, c.formula, c.mass "
                + "FROM msms m inner join compounds_view c on m.compound_id= m.compound_id=c.compound_id "
                + "where c.mass between ? and ?";
        ResultSet rs;
        PreparedStatement prepSt;
        MSMSCompound compoundmsms;
        List<MSMSCompound> msmsCompounds = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setDouble(1, (mass - tolerance));
            prepSt.setDouble(2, (mass + tolerance));
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                int compound_id = rs.getInt("compound_id");
                String name = rs.getString("compound_name");
                String hmdb_id = rs.getString("hmdb_id");
                String formula = rs.getString("formula");
                double compound_mass = rs.getDouble("mass");
                int ionizationMode = rs.getInt("ionization_mode");
                int spectraType = rs.getInt("predicted");
                int voltage = rs.getInt("voltage");
                String voltage_level = rs.getString("voltage_level");
                List<Peak> peaks = super.getPeaksFromMsms_id(msms_id);
                compoundmsms = new MSMSCompound(ionizationMode, voltage, voltage_level, peaks, spectraType, msms_id, compound_id, hmdb_id, name, formula, compound_mass);
                msmsCompounds.add(compoundmsms);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msmsCompounds;
    }
    
    
    /**
     * get the msms from compound with compound id= compounds_id
     *
     * @param compound_id
     * @return
     */
    public List<MSMSCompound> getMSMSFromCompoundID(int compound_id) {
        String sql = "SELECT m.ionization_mode, m.voltage, m.voltage_level, m.predicted, m.msms_id, m.compound_id, m.hmdb_id "
                + "FROM msms as m where m.compound_id=?";
        ResultSet rs;
        PreparedStatement prepSt;
        MSMSCompound compoundmsms;
        List<MSMSCompound> msmsCompounds = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                compound_id = rs.getInt("compound_id");
                String hmdb_id = rs.getString("hmdb_id");
                int ionizationMode = rs.getInt("ionization_mode");
                int spectraType = rs.getInt("predicted");
                int voltage = rs.getInt("voltage");
                String voltage_level = rs.getString("voltage_level");
                List<Peak> peaks = super.getPeaksFromMsms_id(msms_id);
                compoundmsms = new MSMSCompound(msms_id, compound_id, hmdb_id, ionizationMode, voltage, voltage_level, peaks, spectraType);
                msmsCompounds.add(compoundmsms);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msmsCompounds;
    }


}
