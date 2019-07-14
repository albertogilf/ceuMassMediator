/*
 * CEMSAdapter.java
 *
 * Created on 29-may-2019, 20:52:00
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import persistence.NewPathways;

/**
 * CEMSAdapter to create the common fields for product and precursor ions
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 29-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public abstract class CEMSAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Double, Boolean> fragmentsFound;
    private final List<Double> listFragmentsFound;
    private final List<Double> listFragmentsNotFound;
    private final Integer incrementPPM;
    private final Integer errorRMT;
    private boolean boolShowPathways;

    public CEMSAdapter(Integer incrementPPM, Integer errorRMT, Map<Double, Boolean> fragmentsFound) {
        this.incrementPPM = incrementPPM;
        this.errorRMT = errorRMT;
        this.boolShowPathways = false;
        this.fragmentsFound = fragmentsFound;
        this.listFragmentsFound = new LinkedList();
        this.listFragmentsNotFound = new LinkedList();
        for (Map.Entry<Double, Boolean> fragment : fragmentsFound.entrySet()) {
            if (fragment.getValue() == true) {
                listFragmentsFound.add(fragment.getKey());
            } else {
                listFragmentsNotFound.add(fragment.getKey());
            }
        }
    }

    public Integer getIncrementPPM() {
        return incrementPPM;
    }

    public Integer getErrorRMT() {
        return errorRMT;
    }

    public Map<Double, Boolean> getFragmentsFound() {
        return fragmentsFound;
    }

    public List<Double> getListFragmentsFound() {
        return listFragmentsFound;
    }

    public List<Double> getListFragmentsNotFound() {
        return listFragmentsNotFound;
    }

    /**
     *
     * @return the pathways only if the show pathways is established by 1
     */
    public abstract List<NewPathways> getPathways();

    public abstract boolean areTherePathways();

    public boolean areThereFragments() {
        return !(this.fragmentsFound == null
                || this.fragmentsFound.isEmpty());
    }

    public boolean isBoolShowPathways() {
        return this.boolShowPathways;
    }

    public void setBoolShowPathways(boolean boolShowPathways) {
        this.boolShowPathways = boolShowPathways;
    }

    public void exchangeBoolShowPathways() {
        this.boolShowPathways = !this.boolShowPathways;
    }

    public String roundToFourDecimals(Double doubleToRound) {
        return String.format("%.4f", doubleToRound).replace(",", ".");
        // return new DecimalFormat(".#####").format(doubleToRound);
    }

}
