/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.oxidizedTheoreticalCompound;

import java.io.Serializable;
import persistence.NewCompounds;
import utilities.Utilities;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.0, 08/09/2017
 */
public class FACompound implements Serializable {

    private static final long serialVersionUID = 1L;
    private final NewCompounds newCompounds;
    private final Double FAEM;
    private final String oxidationType;
    private final Integer ppmIncrement;

    private final int numCarbons;
    private final int numDoubleBonds;
    private final boolean isOxidized;

    /**
     * Constructor of NewCompoundsAdapter
     *
     * @param nc newCompounds from Database (FA)
     * @param oxidizedFAEM m/z of the mass
     * @param masstoSearchForOxidizedFA neutral mass to search depending on the
     * adduct and oxidation type
     * @param oxidationType oxidation type
     */
    public FACompound(NewCompounds nc,
            Double oxidizedFAEM,
            Double masstoSearchForOxidizedFA,
            String oxidationType) {
        this.FAEM = oxidizedFAEM;
        this.newCompounds = nc;
        this.oxidationType = oxidationType;
        this.isOxidized = oxidationType != "";
        if (this.newCompounds == null) {
            this.numCarbons = 0;
            this.numDoubleBonds = 0;
            this.ppmIncrement = 0;
        } else {
            this.numCarbons = this.newCompounds.getCarbons();
            this.numDoubleBonds = this.newCompounds.getDoubleBonds();
            this.ppmIncrement = Utilities.calculatePPMIncrement(masstoSearchForOxidizedFA, (Double) this.newCompounds.getMass());
        }
    }


    public Double getFAEM() {
        return this.FAEM;
    }

    public String getOxidationType() {
        return this.oxidationType;
    }

    public boolean isIsOxidized() {
        return this.isOxidized;
    }
    
    

    public Integer getIdentifier() {
        if (this.newCompounds == null) {
            return 0;
        } else {
            return this.newCompounds.getCompoundId();
        }
    }

    public String getName() {
        String name;
        if (this.newCompounds == null) {

            name = "No Fatty Acids found for experimental mass " + this.FAEM + " and oxidation type: "
                    + this.oxidationType;
        } else {
            name = this.numCarbons + ":" + this.numDoubleBonds;
            //return this.newCompounds.getCompoundName();
        }
        return name;
    }

    public String getFormula() {
        if (this.newCompounds == null) {
            return "";
        } else {
            return this.newCompounds.getFormula();
        }
    }

    public Double getMolecularWeight() {
        if (this.newCompounds == null) {
            return 0d;
        } else {
            return (Double) this.newCompounds.getMass();
        }
    }

    public Integer getPPMIncrement() {
        return this.ppmIncrement;
    }
    
    public Integer calculateHydrogens() {
        int numHydrogens;
        numHydrogens = this.numCarbons * 2 - 2*this.numDoubleBonds;
        return numHydrogens;
    }

    public NewCompounds getNewCompounds() {
        return newCompounds;
    }

    public Integer getPpmIncrement() {
        return ppmIncrement;
    }

    public int getNumCarbons() {
        return numCarbons;
    }

    public int getNumDoubleBonds() {
        return numDoubleBonds;
    }

    @Override
    public String toString() {
        return "FACompound{" + "newCompounds=" + newCompounds + ", FAEM=" + FAEM + ", oxidationType=" + oxidationType + ", ppmIncrement=" + ppmIncrement + ", numCarbons=" + numCarbons + ", numDoubleBonds=" + numDoubleBonds + ", isOxidized=" + isOxidized + '}';
    }
    
    

}
