package persistence.compoundsFactories;

import persistence.NewCompounds;
import persistence.theoreticalCompound.NewCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Implementation of TheoreticalCompoundsFactory for compounds.
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public class NewCompoundFactory implements TheoreticalCompoundFactory {

    @Override
    public TheoreticalCompounds construct(Object nc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            boolean isAdductAutoDetected,
            String adductAutoDetected, 
            Integer hypothesisId) {
        return new NewCompound((NewCompounds) nc, mass, rt, massToSearch, adduct, isAdductAutoDetected, adductAutoDetected, hypothesisId);
    }
    
    @Override
    public TheoreticalCompounds construct(Object nc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            boolean isAdductAutoDetected,
            String adductAutoDetected, 
            Boolean isSignificative, 
            Integer hypothesisId) {
        return new NewCompound((NewCompounds) nc, mass, rt, massToSearch, adduct, isAdductAutoDetected, adductAutoDetected, isSignificative, hypothesisId);
    }
    
}
