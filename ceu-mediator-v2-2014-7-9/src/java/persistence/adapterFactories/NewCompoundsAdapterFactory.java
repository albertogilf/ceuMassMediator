package persistence.adapterFactories;

import persistence.NewCompounds;
import persistence.theoreticalCompound.NewCompoundsAdapter;
import persistence.theoreticalCompound.TheoreticalCompounds;

  /**
   * Implementation of TheoreticalCompoundsFactory for compounds.
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public class NewCompoundsAdapterFactory implements TheoreticalCompoundFactory {

    @Override
    public TheoreticalCompounds construct(Object mc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            String adductAutoDetected) {
        return new NewCompoundsAdapter((NewCompounds) mc, mass, rt, massToSearch, adduct, adductAutoDetected);
    }
    
    @Override
    public TheoreticalCompounds construct(Object mc, 
            Double mass, 
            Double rt, 
            Double massToSearch, 
            String adduct, 
            String adductAutoDetected, 
            Boolean isSignificative) {
        return new NewCompoundsAdapter((NewCompounds) mc, mass, rt, massToSearch, adduct, adductAutoDetected, isSignificative);
    }
    
}
