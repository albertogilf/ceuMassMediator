package persistence.theoreticalGroup;

import java.util.Collection;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Interface which defines the groups of theoretical Compounds
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public interface TheoreticalCompoundsGroup {
    
    Double getExperimentalMass();
    void setExperimentalMass(Double experimentalMass);
    String getAdduct();
    void setAdduct(String adduct);
    Collection<TheoreticalCompounds> getTheoreticalCompounds();
    void addCompound(TheoreticalCompounds compound);
    // void setTheoreticalCompounds(Collection<TheoreticalCompounds> theoreticalCompounds);
    // To obtain the webpages of the databases
    String getKeggWebPage();
    String getHMDBWebPage();
    String getMetlinWebPage();
    String getLMWebPage();
    String getPCWebPage();
    
    public boolean isSignificativeCompoundGroup();
}
