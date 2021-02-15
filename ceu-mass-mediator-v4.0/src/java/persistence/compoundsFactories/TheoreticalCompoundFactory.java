package persistence.compoundsFactories;

import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 * Interface to define the methods to construct each compound
 *
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 * @param <T> class of the Theoretical Compound Factory
 */
public interface TheoreticalCompoundFactory<T> {

    /**
     *
     * @param nc
     * @param mass
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param hypothesisId
     * @return
     */
    public TheoreticalCompounds construct(T nc, Double mass, Double rt, Double massToSearch,
            String adduct, boolean isAdductAutoDetected, String adductAutoDetected, Integer hypothesisId);

    /**
     *
     * @param nc
     * @param mass
     * @param rt
     * @param massToSearch
     * @param adduct
     * @param isAdductAutoDetected
     * @param adductAutoDetected
     * @param isSignificative
     * @param hypothesisId
     * @return
     */
    public TheoreticalCompounds construct(T nc, Double mass, Double rt, Double massToSearch, String adduct,
            boolean isAdductAutoDetected, String adductAutoDetected, Boolean isSignificative, Integer hypothesisId);

}
