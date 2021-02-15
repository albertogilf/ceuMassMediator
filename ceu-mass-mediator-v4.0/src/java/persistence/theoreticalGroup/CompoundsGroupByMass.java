package persistence.theoreticalGroup;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 * Implementation of TheoreticalCompoundsGroup, where compounds are grouped by
 * experimental mass
 *
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
public class CompoundsGroupByMass extends CompoundsGroupAdapter implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String adduct;
    List<TheoreticalCompounds> listCompounds;
    private final boolean isSignificativeCompoundGroup;

    public CompoundsGroupByMass(Double experimentalMass, String adduct) {
        super(experimentalMass, 0d);
        this.adduct = adduct;
        this.isSignificativeCompoundGroup = true;
        listCompounds = new LinkedList<TheoreticalCompounds>();
    }

    public CompoundsGroupByMass(Double experimentalMass, Double retentionTime, String adduct, boolean isSignificativeCompoundGroup) {
        super(experimentalMass, retentionTime);
        this.adduct = adduct;
        this.isSignificativeCompoundGroup = isSignificativeCompoundGroup;
        listCompounds = new LinkedList<TheoreticalCompounds>();
    }

    /**
     * @return the adduct
     */
    public String getAdduct() {
        return this.adduct;
    }

    /**
     * Add the compound to the group of compounds
     *
     * @param compound
     */
    @Override
    public void addCompound(Object compound) {
        listCompounds.add((TheoreticalCompounds) compound);
    }

    public List<TheoreticalCompounds> getListCompounds() {
        return listCompounds;
    }

    public void setListCompounds(List<TheoreticalCompounds> listCompounds) {
        this.listCompounds = listCompounds;
    }

    public boolean isIsSignificativeCompoundGroup() {
        return isSignificativeCompoundGroup;
    }

    /**
     *
     * @return the collection of compounds from the group
     */
    @Override
    public Collection<TheoreticalCompounds> getTheoreticalCompounds() {
        return listCompounds;
    }

    public boolean isSignificativeCompoundGroup() {
        return isIsSignificativeCompoundGroup();
    }

    @Override
    public int getSizeTheoreticalCompoundsGroup() {
        return this.listCompounds.size();
    }

    @Override
    public boolean isThereTheoreticalCompounds() {
        if (this.getSizeTheoreticalCompoundsGroup() == 1 && this.listCompounds.get(0).getCompoundId() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isAutoDetectedAdduct() {
        if (!isThereTheoreticalCompounds()) {
            if (this.listCompounds.get(0).isBoolAdductAutoDetected()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return the message for the title on the presentation
     */
    @Override
    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Metabolites found for mass: " + this.experimentalMass + "";
        if (this.retentionTime > 0d) {
            titleMessage = titleMessage + ", retention time: " + this.retentionTime;
        }
        titleMessage = titleMessage + " and adduct: " + this.adduct;

        if (!isThereTheoreticalCompounds()) {
            titleMessage = "No " + titleMessage;
            if (isAutoDetectedAdduct()) {
                titleMessage = titleMessage + this.listCompounds.get(0).getAdductAutoDetectedString();
            }
        } else {
            titleMessage = titleMessage + " -> " + getSizeTheoreticalCompoundsGroup();
        }
        return titleMessage;
    }

    @Override
    public String toString() {
        return "CompoundsGroupByMass{" + "adduct=" + adduct + ", listCompounds=" + listCompounds + ", isSignificativeCompoundGroup=" + isSignificativeCompoundGroup + '}';
    }

    
}
