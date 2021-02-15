
package persistence.oxidizedTheoreticalCompound;

import java.util.Collection;
import java.util.List;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.CompoundsGroupByMass;

  /**
   * Interface which defines the groups of theoretical Compounds
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 4.1, 17/02/2016
   */
public interface OxidizedTheoreticalCompound {
    
    Double getOxidizedFAEM();
    Double getNonOxidizedFAEM();
    Double getParentIonEM();
    Double getNeutralMassPI();
    Double getMzPositivePI();
    String getOxidationType();
    String getAdductType();
    
    
    boolean isThereFATheoreticalCompounds();
    
    String getName();
    String getFormula();
    Double getTheoreticalPIMolecularWeight();
    int getPpmIncrement();
    String getTitleMessage();
    
    Integer getNumCarbonsInFAs();
    Integer getNumDoubleBondsInFAs();
    
    FACompound getOxidizedFA();
    FACompound getNonOxidizedFA();
    // Long Chain oxidation or Short Chain oxidation
    int getType();
    boolean isLCOxidation();
    
    Collection<Double> getNeutralLossesPositiveMode();
    Collection<Double> getNeutralLossesNegativeMode();
    boolean areThereNeutralLossesPositiveMode();
    boolean areThereNeutralLossesNegativeMode();
    
    public void setOxidizedCompoundsGroupByMass(CompoundsGroupByMass oxidizedCompoundsGroupByMass);
    CompoundsGroupByMass getOxidizedCompoundsGroupByMass();
    boolean areThereOxidizedAnnotationsForPI();
    
    public void setNonOxidizedCompoundsGroupByMass(CompoundsGroupByMass nonOxidizedCompoundsGroupByMass);
    CompoundsGroupByMass getNonOxidizedCompoundsGroupByMass();
    boolean areThereNonOxidizedAnnotationsForPI();
    
    void exchangeBoolShowNonOxidizedAnnotations();
    
}