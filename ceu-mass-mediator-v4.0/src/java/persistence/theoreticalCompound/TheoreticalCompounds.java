package persistence.theoreticalCompound;

import java.util.Collection;
import java.util.List;
import model.kbsystem.KBSystemResult;
import persistence.NewPathways;

/**
 *
 * @author USUARIO
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente
 */
public interface TheoreticalCompounds {
    Double getExperimentalMass();
    //void setExperimentalMass(Double experimentalMass);
    Double getRetentionTime();
    //void setRetentionTime(Double retentionTime);
    String getAdduct();
    //void setAdduct(String adduct);

    String getCasId();
    Integer getCompoundId();
    String getName();
    String getFormula();
    //String getDatabase();
    // String getDatabaseWebPage();
    Double getMolecularWeight();
    //void setPPMIncrement(Double measuredMass, Double theoreticalMass);
    Integer getPPMIncrement();
    
    boolean isBoolAdductAutoDetected();
    String getAdductAutoDetected();
    String getAdductAutoDetectedString();
    
    //Collection<NewPathways> getPathways();
    Collection<NewPathways> getPathways();
    boolean areTherePathways();
    void exchangeBoolShowPathways();
    void setBoolShowPathways(boolean boolShowPathways);
    boolean showPathways();
    String getCMMWebPage();
    String getKeggCompound();
    String getKeggWebPage();
    String getHMDBCompound();
    String getHMDBWebPage();
    String getMetlinCompound();
    String getMetlinWebPage();
    String getLMCompound();
    String getLMWebPage();
    String getPCCompound();
    String getPCWebPage();
    String getMineId();
    String getMineWebPage();
    String getKnapsackId();
    String getKnapsackWebPage();
    String getNpAtlasId();    
    String getNpAtlasWebPage();
    
// Attributes for InChiKey
    String getInChIKey();
    String getSMILES();
    
// Attributes from Lipids Clasification
    
    public String getCategory();
    public String getMainClass();
    public String getSubClass();

    public String getLipidType();
    public int getCarbons();
    public int getDoubleBonds();
    
// Attributes for Scoring of Lipids Clasification
    public float getIonizationScore();
    public void setIonizationScore(float ionizationScore);
    
    public float getAdductRelationScore();
    public void setAdductRelationScore(float adductRelationScore);

    // Score based on precedence depending on the lipid type. 
    // Deleted temporaly
    //public float getPrecedenceScore();
    //public void setPrecedenceScore(boolean value);
    
    public void calculateRetentionTimeScore();
    public float getRetentionTimeScore();
    //public void setRetentionTimeScore(boolean value);
    public void setRetentionTimeScore(Double EM, Double RT, boolean value);
    public int getNumberRTScores();
    
    public void calculateFinalScore(int maxNumberOfRTRulesApplied);
    public float getFinalScore();
    
    public boolean isSignificativeCompound();
    
    // Methods for printing colours 
    
    public String getColorIonizationScore();
    public String getColorAdductRelationScore();
    //public String createPrecedenceScore();
    public String getColorRetentionTimeScore();
    public String getColorFinalScore();
    
    public void createColorIonizationScore();
    public void createColorAdductRelationScore();
    //public void createPrecedenceScore();
    public void createColorRetentionTimeScore();
    public void createColorFinalScore();
    
    // Methods for KBSystem
    public void addKbSystemResult(KBSystemResult KbSystemResult);
    public Integer getHypothesisId();
    public List<KBSystemResult> getKbSystemResults();
    boolean areThereKbSystemResults();
    
}
