/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import pathway.Pathway;
import compound.Classyfire_Classification;
import compound.CMMCompound;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import utilities.AdductProcessing;
import static utilities.Constants.MIN_RETENTION_TIME_SCORE;
import static utilities.Utilities.calculatePPMIncrement;

/**
 * CompoundLCMS. Represents a chemical compound. This class is the child of
 * CMMCompound. Therefore, it contains the compound's data from the database and
 * the user's input data about the experiment (ex. rt)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class CompoundLCMS extends CMMCompound {

    private final Double EM;
    private final double massIntroduced;
    private final Double RT;
    // Key of EM_RT for scores
    private final String myKey;
    private final Map<Double, Double> CS;
    private final Boolean isSignificative;
    private final String adduct;
    private final Integer EMMode; // 0 neutral (protonated or deprotonated), 1 m/z
    private final Integer ionization_mode; // 0 neutral, 1 positive, 2 negative
    private final Integer incrementPPM;

    private Float ionizationScore;
    private Float adductRelationScore;
    private Float RTscore;
    private Integer totalNumberRTScores;
    // Deleted temporaly
    //private int okPrecedenceScore;
    //private int nokPrecedenceScore;
    // Weight of retention Time Score depends only on the number of other 
    // keys EM_RT which have been taking into account.
    // Also, it is needed to create a method for normalize scores of each 
    // key EM_RT (KEY, STRING)
    // Map of Feature ID and List of boolean applied.
    private Map<String, List<Boolean>> mapRTRules;
    private Float finalScore;

    private String colorIonizationScore;
    private String colorAdductRelationScore;
    //public String colorPrecedenceScore;
    private String colorRTscore;
    private String colorFinalScore;

    /**
     *
     * @param EM
     * @param RT
     * @param CS
     * @param isSignificative
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @param adduct
     * @param compound_id
     * @param compound_mass
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
     * @param fahfa_id
     * @param oh_position
     * @param structure
     * @param aspergillus_web_name
     * @param lm_classification
     * @param classyfire_classification
     * @param lipids_classification
     * @param pathways
     */
    public CompoundLCMS(Double EM, Double RT, Map<Double, Double> CS, String adduct, int EMMode, int ionizationMode,
            Boolean isSignificative, int compound_id, double compound_mass,
            String formula, String compound_name, String cas_id, int formula_type, int compound_type, int compound_status,
            Integer charge_type, Integer charge_number, String lm_id,
            String kegg_id, String hmdb_id, String metlin_id, String in_house_id, Integer pc_id, Integer chebi_id, Integer MINE_id, 
            String knapsack_id, Integer npatlas_id,
            String aspergillus_id, String mesh_nomenclature, String iupac_classification, String aspergillus_web_name,
            Integer fahfa_id, Integer oh_position,
            Structure structure,
            LM_Classification lm_classification, List<Classyfire_Classification> classyfire_classification,
            Lipids_Classification lipids_classification, List<Pathway> pathways) {
        super(compound_id, compound_mass, formula, compound_name, cas_id, formula_type, compound_type, compound_status,
                charge_type, charge_number,
                lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                fahfa_id, oh_position,
                structure, lm_classification, classyfire_classification, lipids_classification, pathways);
        this.EM = EM;
        this.RT = RT;
        this.myKey = EM.toString() + "_" + RT.toString();
        this.CS = CS;
        this.isSignificative = isSignificative;
        this.adduct = adduct;
        this.EMMode = EMMode;
        this.ionization_mode = ionizationMode;
        double massToSearch = AdductProcessing.getMassToSearch(EM, adduct, this.ionization_mode);
        this.incrementPPM = calculatePPMIncrement(massToSearch, compound_mass);

        this.ionizationScore = -1F;
        this.adductRelationScore = -1F;
        this.RTscore = -1F;
        this.finalScore = -1F;
        this.mapRTRules = new LinkedHashMap<String, List<Boolean>>();
        if (this.EMMode == 0) {
            switch (this.ionization_mode) {
                case 1:
                    this.massIntroduced = this.EM - utilities.Constants.PROTON_WEIGHT;
                    break;
                case 2:
                    this.massIntroduced = this.EM + utilities.Constants.PROTON_WEIGHT;
                    break;
                default:
                    this.massIntroduced = this.EM;
                    break;
            }
        } else {
            this.massIntroduced = this.EM;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.EM);
        hash = 59 * hash + Objects.hashCode(this.RT);
        hash = 59 * hash + Objects.hashCode(this.adduct);
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
        final CompoundLCMS other = (CompoundLCMS) obj;
        if (!Objects.equals(this.adduct, other.adduct)) {
            return false;
        }
        if (!Objects.equals(this.EM, other.EM)) {
            return false;
        }
        if (!Objects.equals(this.RT, other.RT)) {
            return false;
        }
        return true;
    }

    public Double getEM() {
        return EM;
    }

    public Double getRT() {
        return RT;
    }

    public Map<Double, Double> getCS() {
        return CS;
    }

    public String getAdduct() {
        return adduct;
    }

    public int getIncrementPPM() {
        return incrementPPM;
    }

    public Boolean isIsSignificative() {
        return isSignificative;
    }

    /**
     * Get the Ionization score
     *
     * @return
     */
    public Float getIonizationScore() {
        return ionizationScore * 2;
    }

    /**
     * Set the Ionization score -2F means that we change the ionization score
     * because detection of pattern and -3F means we decrease the ionization
     * score because lack of adducts when all EM are introduced by the user.
     *
     * @param ionizationScore
     */
    public void setIonizationScore(Float ionizationScore) {
        if (ionizationScore == -2F) {
            if (!(this.ionizationScore == -1F)) {
                this.ionizationScore = 1F;
            }
        } else if (ionizationScore == -3F) {
            {
                if (!(this.ionizationScore == -1F)) {
                    this.ionizationScore = 0.1F;
                }
            }

        } else {
            this.ionizationScore = ionizationScore;
        }
    }

    /**
     * Get the score for the adduct relations score. (M+H, M+Na, M+K and so on)
     *
     * @return
     */
    public Float getAdductRelationScore() {
        return adductRelationScore * 2;
    }

    /**
     * Set the score for the adduct relations score. (M+H, M+Na, M+K and so on)
     *
     * @param adductRelationScore
     */
    public void setAdductRelationScore(Float adductRelationScore) {
        this.adductRelationScore = adductRelationScore;
    }

    // Deleted temporaly
    /**
     * Get the precedence score
     *
     * @return
     */
    /*
    @Override
    public float getPrecedenceScore() {
        float precedenceScore = -1F;
        if ((okPrecedenceScore + nokPrecedenceScore) > 0) {
            precedenceScore = ((float)okPrecedenceScore) / ((float)(okPrecedenceScore + nokPrecedenceScore));
        }
       return precedenceScore;
    }

    /**
     * Set the precedence score.
     * 
     * @param value 
     */
 /*
    @Override
    public void setPrecedenceScore(boolean value) {
        if (value) {
            okPrecedenceScore++;
        } else {
            nokPrecedenceScore++;
        }
    }
     */
    /**
     * Get the retention time score.
     *
     * @return
     */
    public Float getRTscore() {
        return RTscore * 2;
    }

    /**
     *
     * @param EM
     * @param RT
     * @param value
     */
    public void setRTScore(Double EM, Double RT, Boolean value) {

        String key = EM.toString() + "_" + RT.toString();
        // First, check that Scores does not belong to the same EM
        if (!key.equals(this.myKey)) {
            List<Boolean> listScoresForKey;
            if (this.mapRTRules.containsKey(key)) {
                listScoresForKey = this.mapRTRules.get(key);
                listScoresForKey.add(value);
            } else {
                listScoresForKey = new LinkedList<>();
                listScoresForKey.add(value);
                this.mapRTRules.put(key, listScoresForKey);
            }
        } else {
            // TODO 
            // IT MAY BE USED FOR ISOMERS DETECTION!!! IN OTHER FUNCTION
            // SAME EM, SAME RT, SAME CATEGORY, SAME MAIN CLASS, SAME SUB CLASS
            // SAME CARBONS, SAME DOUBLE BOND, SAME START NAME!!!
            // System.out.println("WRONG!");
        }
    }

    public Float getFinalScore() {
        return this.finalScore * 2;
    }

    public void setFinalScore(Float finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getTotalNumberRTScores() {
        return this.totalNumberRTScores;
    }

    public void setTotalNumberRTScores(Integer totalNumberRTScores) {
        this.totalNumberRTScores = totalNumberRTScores;
    }

    @Override
    public String toString() {
        return "            Name: " + super.getCompound_name() + " Retention time: " + this.RT + " Mass: " + super.getMass();
    }

    /**
     * Calculates the RT score
     */
    public void calculateRTScore() {
        //System.out.println("FOR COMPOUND: " + this.myKey);
        this.totalNumberRTScores = this.mapRTRules.size();
        //System.out.println("TOTAL SCORES: " + totalNumberRTScores);
        if (this.totalNumberRTScores > 0) {
            this.RTscore = 0f;
            for (Map.Entry<String, List<Boolean>> entry : this.mapRTRules.entrySet()) {
                String key = entry.getKey();
                //System.out.println(" RULES APPLIED WITH + " + key);
                List<Boolean> relativeScores = entry.getValue();
                int numberRelativeScores = relativeScores.size();
                int relativeScore = 0;
                //System.out.println("SCORES: " + totalRelativeScores + "  " + scores);
                for (Boolean score : relativeScores) {
                    if (score) {
                        relativeScore = relativeScore + 1;
                    }
                }
                relativeScore = relativeScore / numberRelativeScores;
                this.RTscore = this.RTscore + relativeScore;
                //System.out.println("Relative score" + relativeScore);
            }
            this.RTscore = this.RTscore / this.totalNumberRTScores;
            // TO AVOID SCORES 0 DUE TO PRECISSION
            if (this.RTscore < MIN_RETENTION_TIME_SCORE && this.RTscore >= 0F) {
                this.RTscore = MIN_RETENTION_TIME_SCORE;
            }
        }
    }

    /**
     * Get the overall final score. Overall finalScore calculated with a
     * ponderated geometric mean Scores are ponderated with weigth 1 for
     * this.ionizationScore (A); 2 for adductRelationScore (B); (0-2) for
     * RetentionTimeScore (C)
     *
     * @param maxNumberOfRTRulesApplied
     */
    public void calculateFinalScore(int maxNumberOfRTRulesApplied) {

        int thresholdMaxWC = maxNumberOfRTRulesApplied / 2;
        double lnA;
        double lnB;
        double lnC;
        double wA;
        double wB = 0d;
        double wC = 0d;

        double numeratorFinalScore;
        double denominatorFinalScore;
        double finalScore;
        if (Math.abs(this.ionizationScore + 1F) < 0.001f) {
            // There is no ionization score
            // Weight is 0
            lnA = 0d;
            wA = 0d;
        } else {
            // Weight of 1
            lnA = Math.log(this.ionizationScore);
            wA = 1d;
        }

        if (Math.abs(this.adductRelationScore + 1F) < 0.001f) {
            // There is no adductRelationScore score
            // Weight is 0
            lnB = 0d;
            wB = 0d;
        } else {
            // Weight of 2
            lnB = Math.log(this.adductRelationScore);
            wB = 2d;
        }

        if (Math.abs(this.RTscore + 1F) < 0.001F) {
            // There is no ionization score
            // Weight is 0
            lnC = 0d;
            wC = 0d;
        } else if (this.RTscore < MIN_RETENTION_TIME_SCORE) {
            // RetentionTimeScore is = 0;
            // wC is variable. If the number of Relationships with other EM 
            // is above maxNumberOfRTRulesApplied/2, then the weight is maximum weight = 2. 
            // If it is below maxNumberOfRTRulesApplied/2, then Wc is the proportion
            // of this.totalNumberRTScores *2 / (MaxNumberOfRTRulesApplied/2) 
            if (this.totalNumberRTScores >= thresholdMaxWC) {
                // ln(0.05)=-3d
                lnC = -3d;
                wC = 2d;
            } else {
                lnC = -3d;
                wC = 2 * this.totalNumberRTScores / (double) thresholdMaxWC;
            }
        } else {
            lnC = Math.log(this.RTscore);
            // wC is variable. If the number of Relationships with other EM 
            // is above maxNumberOfRTRulesApplied/2, then the weight is maximum weight = 2. 
            // If it is below maxNumberOfRTRulesApplied/2, then Wc is the proportion
            // of this.totalNumberRTScores *2 / (MaxNumberOfRTRulesApplied/2) 
            if (this.totalNumberRTScores > thresholdMaxWC) {
                wC = 2d;
            } else {
                wC = 2 * this.totalNumberRTScores / (double) thresholdMaxWC;
            }
        }
        //System.out.println(this.myKey + " -> WC: " + wC);
        numeratorFinalScore = wA * lnA + wB * lnB + wC * lnC;
        denominatorFinalScore = wA + wB + wC;
        if (Math.abs(denominatorFinalScore) < 0.000001d) {
            this.finalScore = -1F;
        } else {
            finalScore = numeratorFinalScore / denominatorFinalScore;
            finalScore = Math.exp(finalScore);
            this.finalScore = Float.parseFloat(Double.toString(finalScore));
        }
        /*
        if (this.retentionTimeScore <= 0.05F && this.retentionTimeScore > 0F) {
            System.out.println("RT SCORE: " + this.retentionTimeScore
                    + " IONISATION SCORE: " + this.ionizationScore
                    + " ADDUCT SCORE: " + this.adductRelationScore
                    + " num: " + numeratorFinalScore
                    + " den: " + denominatorFinalScore + "  FINAL score: " + this.finalScore);
        }
         */
    }

    public void createColorIonizationScore() {
        //System.out.println("PAINTING: " + this.getIdentifier());
        String nameStyleLabel = "";
        if (this.ionizationScore >= 0F) {
            if (this.ionizationScore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (this.ionizationScore >= 0.25F && this.ionizationScore < 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (this.ionizationScore >= 0.5F && this.ionizationScore < 0.75F) {
                nameStyleLabel = "row_POSSIBLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorIonizationScore = nameStyleLabel;
    }

    public void createColorAdductRelationScore() {
        String nameStyleLabel = "";
        if (this.adductRelationScore >= 0F) {
            if (this.adductRelationScore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (this.adductRelationScore >= 0.25F && this.adductRelationScore < 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (this.adductRelationScore >= 0.5F && this.adductRelationScore < 0.75F) {
                nameStyleLabel = "row_POSSIBLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorAdductRelationScore = nameStyleLabel;
    }

    /*
    @Override
    public void createPrecedenceScore() {
        String nameStyleLabel = "";
        float precedenceScore = getPrecedenceScore();
        if (precedenceScore >= 0F) {
            if (precedenceScore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (precedenceScore >= 0.25F && precedenceScore <= 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (precedenceScore >= 0.5F && precedenceScore < 0.75F) {
                nameStyleLabel = "row_PROBABLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorPrecedenceScore = nameStyleLabel;
    }
     */
    public void createColorRetentionTimeScore() {
        String nameStyleLabel = "";
        if (this.RTscore >= 0F) {
            if (this.RTscore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (this.RTscore >= 0.25F && this.RTscore < 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (this.RTscore >= 0.5F && this.RTscore < 0.75F) {
                nameStyleLabel = "row_POSSIBLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorRTscore = nameStyleLabel;
    }

    public void createColorFinalScore() {
        String nameStyleLabel = "";
        if (this.finalScore >= 0F) {
            if (this.finalScore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (this.finalScore >= 0.25F && this.finalScore < 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (this.finalScore >= 0.5F && this.finalScore < 0.75F) {
                nameStyleLabel = "row_POSSIBLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorFinalScore = nameStyleLabel;
    }

    /**
     *
     * @return
     */
    public String getColorIonizationScore() {
        //System.out.println("ACCESSING PAINT TAG: " + this.colorIonizationScore + " OF: " + this.getIdentifier());
        return this.colorIonizationScore;
    }

    /**
     *
     * @param colorIonizationScore
     */
    public void setColorIonizationScore(String colorIonizationScore) {
        this.colorIonizationScore = colorIonizationScore;
    }

    /**
     *
     * @return
     */
    public String getColorAdductRelationScore() {
        return colorAdductRelationScore;
    }

    /**
     *
     * @param colorAdductRelationScore
     */
    public void setColorAdductRelationScore(String colorAdductRelationScore) {
        this.colorAdductRelationScore = colorAdductRelationScore;
    }

    /**
     *
     * @return
     */
    public String getColorRTscore() {
        return this.colorRTscore;
    }

    /**
     *
     * @param colorRTscore
     */
    public void setColorRTscore(String colorRTscore) {
        this.colorRTscore = colorRTscore;
    }

    /**
     *
     * @return
     */
    public String getColorFinalScore() {
        return colorFinalScore;
    }

    /**
     *
     * @param colorFinalScore
     */
    public void setColorFinalScore(String colorFinalScore) {
        this.colorFinalScore = colorFinalScore;
    }

    public double getMassIntroduced() {
        return massIntroduced;
    }

}
