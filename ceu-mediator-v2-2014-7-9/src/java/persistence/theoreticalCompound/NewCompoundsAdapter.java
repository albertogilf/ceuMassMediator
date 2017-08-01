package persistence.theoreticalCompound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import persistence.NewCompounds;
import persistence.NewPathways;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
@XmlRootElement
public class NewCompoundsAdapter extends TheoreticalCompoundsAdapter implements Serializable {

    private static final long serialVersionUID = 1L;
    
    NewCompounds newCompounds;

    /**
     * Constructor of NewCompoundsAdapter
     *
     * @param nc
     * @param em
     * @param rt
     * @param masstoSearch
     * @param adduct
     * @param adductAutoDetected
     */
    public NewCompoundsAdapter(NewCompounds nc, 
            Double em, 
            Double rt, 
            Double masstoSearch, 
            String adduct, 
            String adductAutoDetected) {
        super(em,rt,adduct);
        this.newCompounds = nc;
        if (this.newCompounds == null) {
            super.setPPMIncrement(0d, 0d);
            if (masstoSearch == null) {
                this.boolAdductAutoDetected = true;
                this.adductAutoDetected = adductAutoDetected;
            }
        } else {
            super.setPPMIncrement(masstoSearch, (Double) this.newCompounds.getMass());
        }
        
        this.AdductAutoDetectedString = " because we detected the adduct based on the composite spectrum. Look results for"
                + " adduct: " + this.adductAutoDetected;
    }
    

    /**
     * Constructor of NewCompoundsAdapter
     *
     * @param nc
     * @param em
     * @param rt
     * @param masstoSearch
     * @param adduct
     * @param adductAutoDetected
     * @param isSignificative
     */
    public NewCompoundsAdapter(NewCompounds nc, 
            Double em, 
            Double rt, 
            Double masstoSearch, 
            String adduct, 
            String adductAutoDetected, 
            Boolean isSignificative) {
        super(em,rt,adduct,isSignificative);
        this.newCompounds = nc;
        if (this.newCompounds == null) {
            super.setPPMIncrement(0d, 0d);
            if (masstoSearch == null) {
                this.boolAdductAutoDetected = true;
                this.adductAutoDetected = adductAutoDetected;
            }
        } else {
            super.setPPMIncrement(masstoSearch, (Double) this.newCompounds.getMass());
        }
        this.AdductAutoDetectedString = " because we detected the adduct based on the composite spectrum. Look results for"
                + " adduct: " + this.adductAutoDetected;
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
    public Integer getIdentifier() {
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
            if (this.boolAdductAutoDetected) {
                return "No compounds found for experimental mass " + this.experimentalMass + " and adduct: "
                        + this.adduct + this.AdductAutoDetectedString;
            } else {
                return "No compounds found for experimental mass " + this.experimentalMass + " and adduct: "
                        + this.adduct;
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
            return new ArrayList<NewPathways>();
        } else {
            return this.newCompounds.getNewPathwaysCollection();
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
     * @return the reference of HMDB compound
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
     * @return the link of the HMDB compound webpage
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
     * @return the reference of HMDB compound
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
     * @return the link of the HMDB compound webpage
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
     * @return the reference of HMDB compound
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
     * @return the link of the HMDB compound webpage
     */
    @Override
    public String getPCWebPage() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.obtainPCWebPage();
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
}
