package persistence.adapterFactories;

import persistence.NewCompoundsGen;
import persistence.theoreticalCompound.NewCompoundsGenAdapter;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Implementation of TheoreticalCompoundsFactory for compounds
   * @author: Andrés Esteban. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public class NewCompoundsGenAdapterFactory implements TheoreticalCompoundFactory {
    
    @Override
    public TheoreticalCompounds construct(Object mc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            String adductAutoDetected) {
        return new NewCompoundsGenAdapter((NewCompoundsGen) mc, mass, rt, massToSearch, adduct, adductAutoDetected);
    }
    
    @Override
    public TheoreticalCompounds construct(Object mc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            String adductAutoDetected, 
            Boolean isSignificative) {
        return new NewCompoundsGenAdapter((NewCompoundsGen) mc, mass, rt, massToSearch, adduct, adductAutoDetected, isSignificative);
    }
    
}
