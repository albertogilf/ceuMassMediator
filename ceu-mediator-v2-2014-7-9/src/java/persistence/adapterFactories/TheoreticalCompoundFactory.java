package persistence.adapterFactories;

import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 * Interface to define the methods to construct each compound
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 * @param <T> class of the Theoretical Compound Factory
 */
public interface TheoreticalCompoundFactory<T> {

    
    
    /**
     *
     * @param kc
     * @param mass
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param adductAutoDetected
     * @return
     */
    public TheoreticalCompounds construct(T kc, Double mass, Double rt, Double massToSearch, String adduct, String adductAutoDetected);

    /**
     *
     * @param kc
     * @param mass
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param adductAutoDetected
     * @param isSignificative
     * @return
     */
    public TheoreticalCompounds construct(T kc, Double mass, Double rt, Double massToSearch, String adduct, String adductAutoDetected, Boolean isSignificative);
    

}
