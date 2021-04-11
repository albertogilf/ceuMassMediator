package persistence.theoreticalCompound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import model.kbsystem.KBSystemResult;
import persistence.NewCompoundsGen;
import persistence.NewPathways;
import utilities.Utilities;

/**
 *
 * @author: aesteban
 * @version: 4.0, 20/07/2016
 */
@XmlRootElement
public class NewCompoundGen extends TheoreticalCompoundsAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    final NewCompoundsGen newCompoundsGen;

    /**
     * Constructor of NewCompoundsGenAdapter
     *
     * @param ncg
     * @param em
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param hypothesisId
     */
    public NewCompoundGen(NewCompoundsGen ncg,
            Double em,
            Double rt,
            Double massToSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            Integer hypothesisId) {
        super(em, rt, adduct, isAdductAutoDetected, adductAutoDetected, hypothesisId);
        this.newCompoundsGen = ncg;
        if (this.newCompoundsGen == null) {
            this.incrementPPM = 0;
        } else {
            this.incrementPPM = Utilities.calculatePPMIncrement(massToSearch, (Double) this.newCompoundsGen.getMass());
        }
    }

    /**
     * Constructor of NewCompoundsGenAdapter
     *
     * @param ncg
     * @param em
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificative
     * @param hypothesisId
     */
    public NewCompoundGen(NewCompoundsGen ncg,
            Double em,
            Double rt,
            Double massToSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            boolean isSignificative,
            Integer hypothesisId) {
        super(em, rt, adduct, isAdductAutoDetected, adductAutoDetected, isSignificative, hypothesisId);
        this.newCompoundsGen = ncg;
        if (this.newCompoundsGen == null) {
            this.incrementPPM = 0;
        } else {
            this.incrementPPM = Utilities.calculatePPMIncrement(massToSearch, (Double) this.newCompoundsGen.getMass());
        }
    }

    @Override
    public String getCasId() {
        // Cas Id obtained when the compounds were accesed by web page
        return null;
    }

    @Override
    public String getMineId() {
        // Cas Id obtained when the compounds were accesed by web page
        if (this.newCompoundsGen == null) {
            return "";
        } else {
            return newCompoundsGen.getMineId();
        }
    }

    @Override
    public String getMineWebPage() {
        if (this.newCompoundsGen == null) {
            return "";
        } else {
            return newCompoundsGen.obtainMineWebPage();
        }
    }

    /**
     * @return the formula
     */
    @Override
    public String getFormula() {
        if (this.newCompoundsGen == null) {
            return "";
        } else {
            return newCompoundsGen.getFormula();
        }
    }

    /**
     * @return the identifier of the compound
     */
    @Override
    public Integer getCompoundId() {
        if (this.newCompoundsGen == null) {
            return 0;
        } else {
            return newCompoundsGen.getCompoundId();
        }
    }

    /**
     * @return the name of the compound
     */
    @Override
    public String getName() {
        if (this.newCompoundsGen == null) {
            if (super.isBoolAdductAutoDetected()) {
                return "No In Silico compounds found for experimental mass " + super.getExperimentalMass() + " and adduct: "
                        + super.getAdduct() + super.getAdductAutoDetectedString();
            } else {
                return "No In Silico compounds found for experimental mass " + super.getExperimentalMass() + " and adduct: "
                        + super.getAdduct();
            }
        } else {
            return newCompoundsGen.getCompoundName();
        }

    }

    /**
     * @return the molecular weight of the compound
     */
    @Override
    public Double getMolecularWeight() {
        if (this.newCompoundsGen == null) {
            return 0d;
        } else {
            return (Double) newCompoundsGen.getMass();
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
        ArrayList<NewPathways> emptyCollection = new ArrayList<NewPathways>();

        return emptyCollection;
    }

    /**
     * @return the reference of kegg compound
     */
    @Override
    public String getKeggCompound() {
        return "";
    }

    /**
     * @return the link of CMM
     */
    @Override
    public String getCMMWebPage() {
        return "";
    }

    /**
     * @return the link of the kegg compound webpage
     */
    @Override
    public String getKeggWebPage() {
        return "";
    }

    /**
     * @return the reference of HMDB compound
     */
    @Override
    public String getHMDBCompound() {
        return "";
    }

    /**
     * @return the link of the HMDB compound webpage
     */
    @Override
    public String getHMDBWebPage() {
        return "";
    }

    /**
     * @return the reference of METLIN compound
     */
    @Override
    public String getMetlinCompound() {
        return "";
    }

    /**
     * @return the link of the METLIN compound webpage
     */
    @Override
    public String getMetlinWebPage() {
        return "";
    }

    /**
     * @return the reference of LM compound
     */
    @Override
    public String getLMCompound() {
        return "";
    }

    /**
     * @return the link of the LM compound webpage
     */
    @Override
    public String getLMWebPage() {
        return "";
    }

    /**
     * @return the reference of PC compound
     */
    @Override
    public String getPCCompound() {
        return "";
    }

    /**
     * @return the link of the PC compound webpage
     */
    @Override
    public String getPCWebPage() {
        return "";
    }

    /**
     * @return the reference of HMDB compound
     */
    @Override
    public String getKnapsackWebPage() {
        return "";
    }

    /**
     * @return the link of the HMDB compound webpage
     */
    @Override
    public String getKnapsackId() {
        return "";
    }

    /**
     * @return the reference of HMDB compound
     */
    @Override
    public String getNpAtlasWebPage() {
        return "";
    }

    /**
     * @return the link of the HMDB compound webpage
     */
    @Override
    public String getNpAtlasId() {
        return "";
    }

    @Override
    public boolean areTherePathways() {
        return false;
    }

    // Attributes from Lipids Clasification
    @Override
    public String getCategory() {
        return "";
    }

    @Override
    public String getMainClass() {
        return "";
    }

    @Override
    public String getSubClass() {
        return "";
    }

    @Override
    public int getCarbons() {
        return -1;
    }

    @Override
    public int getDoubleBonds() {
        return -1;
    }

    // Attributes for InChIKey
    @Override
    public String getInChIKey() {
        if (this.newCompoundsGen == null) {
            return "";
        } else if (this.newCompoundsGen.getInChiKey() == null) {
            return "";
        } else {
            return this.newCompoundsGen.getInChiKey();
        }
    }

    @Override
    public String getSMILES() {
        if (this.newCompoundsGen == null) {
            return "";
        } else if (this.newCompoundsGen.getSmiles() == null) {
            return "";
        } else {
            return this.newCompoundsGen.getSmiles();
        }
    }

    @Override
    public String getLipidType() {
        return "";
    }

    @Override
    public Integer getHypothesisId() {
        return -1;
    }

    @Override
    public void addKbSystemResult(KBSystemResult KbSystemResult) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
