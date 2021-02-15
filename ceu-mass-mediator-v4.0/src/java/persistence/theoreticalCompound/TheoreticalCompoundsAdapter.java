package persistence.theoreticalCompound;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.kbsystem.KBSystemResult;
import static utilities.Constants.MIN_RETENTION_TIME_SCORE;

/**
 * Abstract class to work TheoreticalCompounds
 *
 * @author alberto
 * @version: 3.1, 17/02/2016
 */
public abstract class TheoreticalCompoundsAdapter implements TheoreticalCompounds {

    private final Double experimentalMass;
    private final Double retentionTime;
    private final String adduct;
    protected Integer incrementPPM;
    final boolean significativeCompound;
    private boolean boolShowPathways;

    private float ionizationScore;
    private float adductRelationScore;
    // Deleted temporaly
    //private int okPrecedenceScore;
    //private int nokPrecedenceScore;

    private int totalNumberScores;
    private float retentionTimeScore;
    // Weight of retention Time Score depends only on the number of other 
    // keys EM_RT which have been taking into account.
    // Also, it is needed to create a method for normalize scores of each 
    // key EM_RT (KEY, STRING)
    private Map<String, List<Boolean>> mapRTRules;
    private final String myKey;

    private float finalScore;

    private final String adductAutoDetected;
    private final String AdductAutoDetectedString;
    private final boolean boolAdductAutoDetected;

    private String colorIonizationScore;
    private String colorAdductRelationScore;
    //public String colorPrecedenceScore;
    private String colorRetentionTimeScore;
    private String colorFinalScore;

    private List<KBSystemResult> kbSystemResults;
    private final Integer hypothesisId;

    /**
     *
     * @param em
     * @param rt
     * @param adduct
     * @param booladductAutoDetected
     * @param adductAutoDetected
     * @param hypothesisId
     */
    public TheoreticalCompoundsAdapter(Double em, Double rt, String adduct,
            boolean booladductAutoDetected, String adductAutoDetected, Integer hypothesisId) {
        this.experimentalMass = em;
        this.retentionTime = rt;
        this.adduct = adduct;
        this.significativeCompound = true;
        this.boolAdductAutoDetected = booladductAutoDetected;
        this.adductAutoDetected = adductAutoDetected;
        this.AdductAutoDetectedString = " because we detected the adduct based on the composite spectrum. Look results for"
                + " adduct: " + this.adductAutoDetected;

        this.boolShowPathways = false;

        //this.nokPrecedenceScore = 0;
        //this.okPrecedenceScore = 0;
        // INITIALIZE to -1F to dont print when no need
        this.ionizationScore = -1F;
        this.adductRelationScore = -1F;
        this.retentionTimeScore = -1F;
        this.finalScore = -1F;
        this.mapRTRules = new LinkedHashMap<String, List<Boolean>>();
        this.myKey = em.toString() + "_" + rt.toString();
        this.hypothesisId = hypothesisId;
        this.kbSystemResults = new LinkedList<>();
    }

    /**
     *
     * @param em
     * @param rt
     * @param adduct
     * @param booladductAutoDetected
     * @param adductAutoDetected
     * @param isSignificative
     * @param hypothesisId
     */
    public TheoreticalCompoundsAdapter(Double em, Double rt, String adduct,
            boolean booladductAutoDetected, String adductAutoDetected, boolean isSignificative,
            Integer hypothesisId) {
        this.experimentalMass = em;
        this.retentionTime = rt;
        this.adduct = adduct;
        this.significativeCompound = isSignificative;

        this.boolAdductAutoDetected = booladductAutoDetected;
        this.adductAutoDetected = adductAutoDetected;
        this.AdductAutoDetectedString = " because we detected the adduct based on the composite spectrum. Look results for"
                + " adduct: " + this.adductAutoDetected;

        this.boolShowPathways = false;
        //this.nokPrecedenceScore = 0;
        //this.okPrecedenceScore = 0;
        // INITIALIZE to -1F to dont print when no need
        this.ionizationScore = -1F;
        this.adductRelationScore = -1F;
        this.retentionTimeScore = -1F;
        this.finalScore = -1F;
        this.mapRTRules = new LinkedHashMap<String, List<Boolean>>();
        this.myKey = em.toString() + "_" + rt.toString();

        this.hypothesisId = hypothesisId;
        this.kbSystemResults = new LinkedList<>();
    }

    /**
     * @return the retention Time
     */
    @Override
    public Double getRetentionTime() {
        return this.retentionTime;
    }

    /**
     * @return the experimentalMass
     */
    @Override
    public Double getExperimentalMass() {
        return this.experimentalMass;
    }

    /**
     * @return the adduct
     */
    @Override
    public String getAdduct() {
        return this.adduct;
    }

    /**
     * @return the ionization mode
     */
    /*
    @Override
    public String getIonMode() {
        return this.ionMode;
    }
     */
    /**
     * @param ionMode the ionization mode
     */
    /*
    @Override
    public void setIonMode(String ionMode) {
        this.ionMode = ionMode;
    }
     */
    /**
     * @return the PPM increment
     */
    @Override
    public Integer getPPMIncrement() {
        return this.incrementPPM;
    }

    @Override
    public String getAdductAutoDetected() {
        return this.adductAutoDetected;
    }

    @Override
    public String getAdductAutoDetectedString() {
        return AdductAutoDetectedString;
    }

    @Override
    public boolean isBoolAdductAutoDetected() {
        return boolAdductAutoDetected;
    }

// Attributes for Scoring of Lipids Clasification
    /**
     * Get the Ionization score
     *
     * @return
     */
    @Override
    public float getIonizationScore() {
        return ionizationScore * 2;
    }

    /**
     * Set the Ionization score -2F means that we change the ionization score
     * because detection of pattern and -3F means we decrease the ionization
     * score because lack of adducts when all EM are introduced by the user.
     *
     * @param ionizationScore
     */
    @Override
    public void setIonizationScore(float ionizationScore) {
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
    @Override
    public float getAdductRelationScore() {

        return this.adductRelationScore * 2;
    }

    /**
     * Set the score for the adduct relations score. (M+H, M+Na, M+K and so on)
     *
     * @param adductRelationScore
     */
    @Override
    public void setAdductRelationScore(float adductRelationScore) {
        //this.adductRelationScore = this.adductRelationScore + adductRelationScore;
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
 /*
    @Override
    public void calculateRetentionTimeScore() {
        if ((okRetentionTimeScore + nokRetentionTimeScore) > 0) {
            this.retentionTimeScore = ((float) okRetentionTimeScore) / ((float) (okRetentionTimeScore + nokRetentionTimeScore));
        }
    }
     */
    @Override
    public void calculateRetentionTimeScore() {
        //System.out.println("FOR COMPOUND: " + this.myKey);
        this.totalNumberScores = this.mapRTRules.size();
        //System.out.println("TOTAL SCORES: " + totalNumberScores);
        if (this.totalNumberScores > 0) {
            this.retentionTimeScore = 0;
            for (Iterator<Map.Entry<String, List<Boolean>>> it = this.mapRTRules.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, List<Boolean>> entry = it.next();
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
                this.retentionTimeScore = this.retentionTimeScore + relativeScore;
                //System.out.println("Relative score" + relativeScore);
            }
            this.retentionTimeScore = this.retentionTimeScore / this.totalNumberScores;
            if (this.retentionTimeScore < MIN_RETENTION_TIME_SCORE && this.retentionTimeScore >= 0F) {
                this.retentionTimeScore = MIN_RETENTION_TIME_SCORE;
            }
        }
    }

    /**
     * Get the retention time score.
     *
     * @return
     */
    @Override
    public float getRetentionTimeScore() {
        return this.retentionTimeScore * 2;
    }

    /**
     * Set the retention time score. The RT score takes into account that
     * annotations may be from the same EM. For this reason, the different RT
     * scores are saved in a MAP for each EM. After this, the RT Score for each
     * EM will be weighted depending on the number of rules from this EM.
     *
     * @param EM
     * @param RT
     * @param value
     */
    @Override
    public void setRetentionTimeScore(Double EM, Double RT, boolean value) {

        String key = EM.toString() + "_" + RT.toString();
        // First, check that Scores does not belong to the same EM
        if (!key.equals(this.myKey)) {
            List<Boolean> listScoresForKey;
            if (this.mapRTRules.containsKey(key)) {
                listScoresForKey = this.mapRTRules.get(key);
                listScoresForKey.add(value);
            } else {
                listScoresForKey = new LinkedList<Boolean>();
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

    /**
     * Return the maximum number of RT scores (different EM which have applied
     * rules with this Theoretical Compound).
     *
     * @return
     */
    @Override
    public int getNumberRTScores() {
        return this.mapRTRules.size();
    }

    /**
     * Get the overall final score. Overall finalScore calculated with a
     * ponderated geometric mean Scores are ponderated with weigth 1 for
     * this.ionizationScore (A); 2 for adductRelationScore (B); (0-2) for
     * RetentionTimeScore (C)
     *
     * @param maxNumberOfRTRulesApplied
     */
    @Override
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

        if (Math.abs(this.retentionTimeScore + 1F) < 0.001F) {
            // There is no ionization score
            // Weight is 0
            lnC = 0d;
            wC = 0d;
        } else if (this.retentionTimeScore < MIN_RETENTION_TIME_SCORE) {
            // RetentionTimeScore is = 0;
            // wC is variable. If the number of Relationships with other EM 
            // is above maxNumberOfRTRulesApplied/2, then the weight is maximum weight = 2. 
            // If it is below maxNumberOfRTRulesApplied/2, then Wc is the proportion
            // of this.totalNumberScores *2 / (MaxNumberOfRTRulesApplied/2) 
            if (this.totalNumberScores >= thresholdMaxWC) {
                // ln(0.05)=-3d
                lnC = -3d;
                wC = 2d;
            } else {
                lnC = -3d;
                wC = 2 * this.totalNumberScores / (double) thresholdMaxWC;
            }
        } else {
            lnC = Math.log(this.retentionTimeScore);
            // wC is variable. If the number of Relationships with other EM 
            // is above maxNumberOfRTRulesApplied/2, then the weight is maximum weight = 2. 
            // If it is below maxNumberOfRTRulesApplied/2, then Wc is the proportion
            // of this.totalNumberScores *2 / (MaxNumberOfRTRulesApplied/2) 
            if (this.totalNumberScores > thresholdMaxWC) {
                wC = 2d;
            } else {
                wC = 2 * this.totalNumberScores / (double) thresholdMaxWC;
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

    /**
     *
     * @return finalScore
     */
    @Override
    public float getFinalScore() {
        return this.finalScore * 2;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isSignificativeCompound() {
        return this.significativeCompound;
    }

    public boolean getSignificativeCompound() {
        return this.significativeCompound;
    }

    public boolean isBoolShowPathways() {
        return boolShowPathways;
    }

    @Override
    public void setBoolShowPathways(boolean boolShowPathways) {
        this.boolShowPathways = boolShowPathways;
    }

    @Override
    public void exchangeBoolShowPathways() {
        this.boolShowPathways = !this.boolShowPathways;
    }

    @Override
    public boolean showPathways() {
        return isBoolShowPathways();
    }

    @Override
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

    @Override
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
    @Override
    public void createColorRetentionTimeScore() {
        String nameStyleLabel = "";
        if (this.retentionTimeScore >= 0F) {
            if (this.retentionTimeScore < 0.25F) {
                nameStyleLabel = "row_NOT_EXPECTED";
            } else if (this.retentionTimeScore >= 0.25F && this.retentionTimeScore < 0.5F) {
                nameStyleLabel = "row_NOT_PROBABLE";
            } else if (this.retentionTimeScore >= 0.5F && this.retentionTimeScore < 0.75F) {
                nameStyleLabel = "row_POSSIBLE";
            } else {
                nameStyleLabel = "row_EXPECTED";
            }
        }
        this.colorRetentionTimeScore = nameStyleLabel;
    }

    @Override
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
    @Override
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
    @Override
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
    @Override
    public String getColorRetentionTimeScore() {
        return colorRetentionTimeScore;
    }

    /**
     *
     * @param colorRetentionTimeScore
     */
    public void setColorRetentionTimeScore(String colorRetentionTimeScore) {
        this.colorRetentionTimeScore = colorRetentionTimeScore;
    }

    /**
     *
     * @return
     */
    @Override
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

    public String roundToFourDecimals(Double doubleToRound) {
        if (doubleToRound == null) {
            return "--";
        }
        return String.format("%.4f", doubleToRound).replace(",", ".");
    }

    @Override
    public Integer getHypothesisId() {
        return this.hypothesisId;
    }

    @Override
    public List<KBSystemResult> getKbSystemResults() {
        return kbSystemResults;
    }

    @Override
    public void addKbSystemResult(KBSystemResult kbSystemResult) {
        this.kbSystemResults.add(kbSystemResult);
    }

    @Override
    public boolean areThereKbSystemResults() {
        if (this.kbSystemResults == null || this.kbSystemResults.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
