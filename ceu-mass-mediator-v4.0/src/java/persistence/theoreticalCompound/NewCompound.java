package persistence.theoreticalCompound;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlRootElement;
import persistence.NewCompounds;
import persistence.NewPathways;
import utilities.Utilities;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
@XmlRootElement
public class NewCompound extends TheoreticalCompoundsAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    final NewCompounds newCompounds;

    /**
     * Constructor of NewCompoundsAdapter
     *
     * @param nc
     * @param em
     * @param rt
     * @param masstoSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param hypothesisId
     */
    public NewCompound(NewCompounds nc,
            Double em,
            Double rt,
            Double masstoSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            Integer hypothesisId) {
        super(em, rt, adduct, isAdductAutoDetected, adductAutoDetected, hypothesisId);
        this.newCompounds = nc;
        if (this.newCompounds == null) {
            this.incrementPPM = 0;
        } else {
            this.incrementPPM = Utilities.calculatePPMIncrement(masstoSearch, (Double) this.newCompounds.getMass());
        }
    }

    /**
     * Constructor of NewCompoundsAdapter
     *
     * @param nc
     * @param em
     * @param rt
     * @param masstoSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificative
     * @param hypothesisId
     */
    public NewCompound(NewCompounds nc,
            Double em,
            Double rt,
            Double masstoSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            boolean isSignificative,
            Integer hypothesisId) {
        super(em, rt, adduct, isAdductAutoDetected, adductAutoDetected, isSignificative, hypothesisId);
        this.newCompounds = nc;
        if (this.newCompounds == null || masstoSearch == null) {
            this.incrementPPM = 0;
        } else {
            this.incrementPPM = Utilities.calculatePPMIncrement(masstoSearch, (Double) this.newCompounds.getMass());
        }
    }

    @Override
    public String getCasId() {
        // Cas Id obtained when the compounds were accesed by web page
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getCasId();
        }
    }

    /**
     * @return the formula
     */
    @Override
    public String getFormula() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getFormula();
        }
    }

    /**
     * @return the identifier of the compound
     */
    @Override
    public Integer getCompoundId() {
        if (this.newCompounds == null) {
            return 0;
        } else {
            return this.newCompounds.getCompoundId();
        }
    }

    /**
     * @return the name of the compound
     */
    @Override
    public String getName() {
        if (this.newCompounds == null) {
            if (super.isBoolAdductAutoDetected()) {
                return "No compounds found for experimental mass " + super.getExperimentalMass() + " and adduct: "
                        + super.getAdduct() + super.getAdductAutoDetectedString();
            } else {
                return "No compounds found for experimental mass " + super.getExperimentalMass() + " and adduct: "
                        + super.getAdduct();
            }
        } else {
            return this.newCompounds.getCompoundName();
        }
    }

    /**
     * @return the molecular weight of the compound
     */
    @Override
    public Double getMolecularWeight() {
        // System.out.println("Double: " + (Double) newCompounds.getMass() + " double: " + newCompounds.getMass());
        if (this.newCompounds == null) {
            return 0d;
        } else {
            return (Double) this.newCompounds.getMass();
        }
    }

    /**
     * Return an empty list because it is mandatory to implement every method
     * defined in the interfaz TheoreticalCompounds
     *
     * @return an empty list
     */
    @Override
    public Collection getPathways() {
        if (this.newCompounds == null || !this.isBoolShowPathways()) {
            return new LinkedList<NewPathways>();
        } else {
            return this.newCompounds.getNewPathwaysCollection();
        }
    }
    
    /**
     * @return the link of the kegg compound webpage
     */
    @Override
    public String getCMMWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainCMMWebPage();
        }
    }
    
    

    /**
     * @return the reference of kegg compound
     */
    @Override
    public String getKeggCompound() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getKeggId();
        }
    }

    /**
     * @return the link of the kegg compound webpage
     */
    @Override
    public String getKeggWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainKeggWebPage();
        }
    }
    
    

    /**
     * @return the reference of HMDB compound
     */
    @Override
    public String getHMDBCompound() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getHmdbId();
        }
    }

    /**
     * @return the link of the HMDB compound webpage
     */
    @Override
    public String getHMDBWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainHMDBWebPage();
        }
    }

    /**
     * @return the reference of Metlin compound
     */
    @Override
    public String getMetlinCompound() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getMetlinId();
        }
    }

    /**
     * @return the link of the Metlin compound webpage
     */
    @Override
    public String getMetlinWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainMetlinWebPage();
        }
    }

    /**
     * @return the reference of LipidMaps compound
     */
    @Override
    public String getLMCompound() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getLmId();
        }
    }

    /**
     * @return the link of the LipidMaps compound webpage
     */
    @Override
    public String getLMWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainLMWebPage();
        }
    }

    /**
     * @return the reference of PubChem compound
     */
    @Override
    public String getPCCompound() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getPcId();
        }
    }

    /**
     * @return the link of the PubChem compound webpage
     */
    @Override
    public String getPCWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainPCWebPage();
        }
    }
    
    
    /**
     * @return the reference of Knapsack compound
     */
    @Override
    public String getKnapsackId() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getKnapsackId();
        }
    }

    /**
     * @return the link of the Knapsack compound webpage
     */
    @Override
    public String getKnapsackWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainKnapsackWebPage();
        }
    }
    
    /**
     * @return the reference of NP ATlas compound
     */
    @Override
    public String getNpAtlasId() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getNPAtlasId();
        }
    }

    /**
     * @return the link of the NP ATlas compound webpage
     */
    @Override
    public String getNpAtlasWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainNPAtlasWebPage();
        }
    }

    @Override
    public boolean areTherePathways() {
        if (this.newCompounds == null) {
            return false;
        } else if (this.newCompounds.getNewPathwaysCollection() == null || this.newCompounds.getNewPathwaysCollection().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

// Attributes for InChIKey
    @Override
    public String getInChIKey() {
        if (this.newCompounds == null) {
            return "";
        } else if (this.newCompounds.getInChiKey() == null || this.newCompounds.getInChiKey().equals("")) {
            return "";
        } else {
            return this.newCompounds.getInChiKey();
        }
    }

    @Override
    public String getSMILES() {
        if (this.newCompounds == null) {
            return "";
        } else if (this.newCompounds.getSmiles() == null || this.newCompounds.getSmiles().equals("")) {
            return "";
        } else {
            return this.newCompounds.getSmiles();
        }
    }

    @Override
    public String getMineWebPage() {
        return "";
    }

    @Override
    public String getMineId() {
        return null;
    }

// Attributes from Lipids Clasification
    @Override
    public String getCategory() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getCategory();
        }
    }

    @Override
    public String getMainClass() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getMainClass();
        }
    }

    @Override
    public String getSubClass() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getSubClass();
        }
    }

    @Override
    public int getCarbons() {
        if (this.newCompounds == null) {
            return -1;
        } else {
            return this.newCompounds.getCarbons();
        }
    }

    @Override
    public int getDoubleBonds() {
        if (this.newCompounds == null) {
            return -1;
        } else {
            return this.newCompounds.getDoubleBonds();
        }
    }

    @Override
    public String getLipidType() {
        return this.newCompounds.getLipidType();
    }

    @Override
    public String toString() {
        return "NewCompound{" + "newCompounds=" + newCompounds + '}'
                + "\n KBSystemProlog: " + super.getKbSystemResults();
    }

}
