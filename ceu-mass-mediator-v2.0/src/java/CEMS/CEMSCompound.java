/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CEMS;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.NewCompoundsCE;
import persistence.NewPathways;

/**
 * CompoundLCMS. Represents a chemical compound. This class is the child of
 * CMMCompound. Therefore, it contains the compound's data from the database and
 * the user's input data about the experiment (ex. rt)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class CEMSCompound extends CEMSAdapter {

    private NewCompoundsCE ceCompound;

    /**
     * Constructor of CEMSCompound
     *
     * @param ceCompound , CEMS Compound persisted by a JPA class
     * @param incrementPPM PPM difference between the experimental mz and the
     * theoretical one
     * @param errorRMT
     * @param fragmentsFound Map with the experimental fragments and if they are 
     * present in the standard experiment
     */
    public CEMSCompound(NewCompoundsCE ceCompound, int incrementPPM, int errorRMT, 
            Map<Double, Boolean> fragmentsFound) {
        super(incrementPPM, errorRMT, fragmentsFound);
        this.ceCompound = ceCompound;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.ceCompound);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final CEMSCompound other = (CEMSCompound) obj;

        return Objects.equals(this.ceCompound, other.ceCompound);
    }

    @Override
    public String toString() {
        return "CEMSCompound{" + "ceCompound=" + ceCompound + ", incrementPPM=" + super.getIncrementPPM() + '}';
    }

    public NewCompoundsCE getCeCompound() {
        return ceCompound;
    }

    public void setCeCompound(NewCompoundsCE ceCompound) {
        this.ceCompound = ceCompound;
    }

    @Override
    public List<NewPathways> getPathways() {
        List<NewPathways> pathways;
        if (!super.isBoolShowPathways()) {
            pathways = new LinkedList<>();
        } else {
            pathways = new LinkedList(this.ceCompound.getNc().getNewPathwaysCollection());
        }
        return pathways;
    }

    @Override
    public boolean areTherePathways() {
        return !(this.ceCompound == null
                || this.ceCompound.getNc().getNewPathwaysCollection() == null
                || this.ceCompound.getNc().getNewPathwaysCollection().isEmpty());
    }
    
}
