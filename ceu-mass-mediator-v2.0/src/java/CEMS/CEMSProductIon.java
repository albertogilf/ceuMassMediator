/*
 * CEMSProductIon.java
 *
 * Created on 29-may-2019, 20:45:39
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import persistence.CEProductIon;
import persistence.NewCompoundsCE;
import persistence.NewPathways;

/**
 * CEMS Product Ion (fragments, adducts and other alterations) over the
 * compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 29-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSProductIon extends CEMSAdapter {

    private CEProductIon ceProductIon;

    /**
     * Constructor of ceProductIon
     *
     * @param ceProductIon , CEMS ProductIon persisted by a JPA class
     * @param incrementPPM PPM difference between the experimental mz and the
     * theoretical one
     * @param errorRMT
     * @param fragmentsFound
     */
    public CEMSProductIon(CEProductIon ceProductIon, int incrementPPM, int errorRMT,
            Map<Double, Boolean> fragmentsFound) {
        super(incrementPPM, errorRMT, fragmentsFound);
        this.ceProductIon = ceProductIon;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.ceProductIon);
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
        final CEMSProductIon other = (CEMSProductIon) obj;

        return Objects.equals(this.ceProductIon, other.ceProductIon);
    }

    @Override
    public String toString() {
        return "CEMSCompound{" + "ceCompound=" + ceProductIon + ", incrementPPM=" + super.getIncrementPPM() + '}';
    }

    public NewCompoundsCE getCeCompound() {
        // TODO RETURN COMPOUND WHEN THEY ARE LINKED
        return ceProductIon.getNcce();
    }

    public void setCeCompound(NewCompoundsCE ceMSCompound) {
        this.ceProductIon.setNcce(ceMSCompound);
    }

    @Override
    public List<NewPathways> getPathways() {
        List<NewPathways> pathways;
        if (!super.isBoolShowPathways()) {
            pathways = new LinkedList<>();
        } else {
            pathways = new LinkedList(this.getCeCompound().getNc().getNewPathwaysCollection());
        }
        return pathways;
    }

    public CEProductIon getCeProductIon() {
        return ceProductIon;
    }

    public void setCeProductIon(CEProductIon ceProductIon) {
        this.ceProductIon = ceProductIon;
    }

    @Override
    public boolean areTherePathways() {
        return !(this.getCeCompound() == null
                || this.getCeCompound().getNc().getNewPathwaysCollection() == null
                || this.getCeCompound().getNc().getNewPathwaysCollection().isEmpty());
    }

}
