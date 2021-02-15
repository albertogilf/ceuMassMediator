package persistence.compoundsFactories;

import persistence.NewCompoundsGen;
import persistence.theoreticalCompound.NewCompoundGen;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Implementation of TheoreticalCompoundsFactory for compounds
   * @author: Andrés Esteban. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public class NewCompoundGenFactory implements TheoreticalCompoundFactory {
    
    @Override
    public TheoreticalCompounds construct(Object ncg, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            boolean isAdductAutoDetected,
            String adductAutoDetected, 
            Integer hypothesisId) {
        return new NewCompoundGen((NewCompoundsGen) ncg, mass, rt, massToSearch, 
                adduct, isAdductAutoDetected, adductAutoDetected, hypothesisId);
    }
    
    @Override
    public TheoreticalCompounds construct(Object ncg, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            boolean isAdductAutoDetected,
            String adductAutoDetected, 
            Boolean isSignificative, 
            Integer hypothesisId) {
        return new NewCompoundGen((NewCompoundsGen) ncg, mass, rt, massToSearch, 
                adduct, isAdductAutoDetected, adductAutoDetected, isSignificative, hypothesisId);
    }
    
}
