package persistence.theoreticalGroup;

import java.util.Collection;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Interface which defines the groups of theoretical Compounds
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
 * @param <T>
   */
public interface TheoreticalCompoundsGroup<T> {
    
    Double getExperimentalMass();
    //void setExperimentalMass(Double experimentalMass);
    Double getRetentionTime();
    //void setRetentionTime(Double retentionTime);
    
    Collection<T> getTheoreticalCompounds();
    void addCompound(T compound);
    int getSizeTheoreticalCompoundsGroup();
    boolean isThereTheoreticalCompounds();
    // void setTheoreticalCompounds(Collection<TheoreticalCompounds> theoreticalCompounds);
    // To obtain the webpages of the databases
    String getTitleMessage();
    String getKeggWebPage();
    String getHMDBWebPage();
    String getMetlinWebPage();
    String getLMWebPage();
    String getPCWebPage();
    String getChebiWebPage();
    String getKnapsackWebPage();
    String getNpAtlasWebPage();
}
