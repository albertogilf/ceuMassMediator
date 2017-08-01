package persistence.theoreticalGroup;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import persistence.theoreticalCompound.TheoreticalCompounds;
import utilities.Constantes;

  /**
   * Implementation of TheoreticalCompoundsGroup grouped by experimental mass
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public class CompoundsGroupByMass implements Serializable, TheoreticalCompoundsGroup {
    
    private static final long serialVersionUID = 1L;
    
    Double experimentalMass;
    String adduct;
    List<TheoreticalCompounds> listCompounds;
    boolean isSignificativeCompoundGroup;
    
    public CompoundsGroupByMass(Double experimentalMass, String adduct)
    {
        this.experimentalMass = experimentalMass;
        this.adduct=adduct;
        this.isSignificativeCompoundGroup=true;
        listCompounds = new LinkedList<TheoreticalCompounds>();
    }
    
    public CompoundsGroupByMass(Double experimentalMass, String adduct, boolean isSignificativeCompoundGroup)
    {
        this.experimentalMass = experimentalMass;
        this.adduct=adduct;
        this.isSignificativeCompoundGroup=isSignificativeCompoundGroup;
        listCompounds = new LinkedList<TheoreticalCompounds>();
    }

    /**
     * @return the experimentalMass
     */
    @Override
    public Double getExperimentalMass() {
        return this.experimentalMass;
    }

    /**
     * @param experimentalMass the experimentalMass to set
     */
    @Override
    public void setExperimentalMass(Double experimentalMass) {
        this.experimentalMass = experimentalMass;
    }

    /**
     * @return the adduct
     */
    @Override
    public String getAdduct() {
        return this.adduct;
    }

    /**
     * @param adduct the adduct to set
     */
    @Override
    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }
    
    /**
     * Add the compound to the group of compounds
     * @param compound
     */
    @Override
    public void addCompound(TheoreticalCompounds compound)
    {
        listCompounds.add(compound);
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

    public void setIsSignificativeCompoundGroup(boolean isSignificativeCompoundGroup) {
        this.isSignificativeCompoundGroup = isSignificativeCompoundGroup;
    }
    
    

    /**
     *
     * @return the collection of compounds from the group
     */
    @Override
    public Collection<TheoreticalCompounds> getTheoreticalCompounds() {
        return listCompounds;
    }

    @Override
    public String getKeggWebPage() {
        return Constantes.WEB_KEGG;
    }

    @Override
    public String getHMDBWebPage() {
        return Constantes.WEB_HMDB;
    }

    @Override
    public String getMetlinWebPage() {
        return Constantes.WEB_METLIN;
    }

    @Override
    public String getLMWebPage() {
        return Constantes.WEB_LIPID_MAPS;
    }

    @Override
    public String getPCWebPage() {
        return Constantes.WEB_PUBCHEMICHAL;
    }

    @Override
    public boolean isSignificativeCompoundGroup() {
        return isIsSignificativeCompoundGroup();
    }

}
