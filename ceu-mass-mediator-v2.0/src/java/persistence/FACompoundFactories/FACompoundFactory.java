/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.FACompoundFactories;

import persistence.NewCompounds;
import persistence.oxidizedTheoreticalCompound.FACompound;

/**
 * Interface to define the methods to construct each compound
 *
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 15/11/2017
 * @param <T> class of the FA Theoretical Compound Factory
 */
public class FACompoundFactory<T> {

    /**
     *
     * @param nc
     * @param oxidizedFAEM
     * @param masstoSearchForOxidizedFA
     * @param oxidationType
     * @return
     */
    public FACompound construct(T nc,
            Double oxidizedFAEM,
            Double masstoSearchForOxidizedFA,
            String oxidationType) {
        return new FACompound((NewCompounds) nc, oxidizedFAEM, masstoSearchForOxidizedFA, oxidationType);
    }
}
