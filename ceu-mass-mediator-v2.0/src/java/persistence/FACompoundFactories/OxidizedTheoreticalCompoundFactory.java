/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.FACompoundFactories;

import persistence.oxidizedTheoreticalCompound.FACompound;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;

/**
 * Interface to define the methods to construct each compound corresponding to
 * oxidized lipids.
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.0, 08/09/2017
 */
public interface OxidizedTheoreticalCompoundFactory {

    /**
     *
     * @param oxidizedFAEM
     * @param nonOxidizedFAEM
     * @param parentIonEM
     * @param oxidationType
     * @param adductType
     * @param oxidizedFA
     * @param nonOxidizedFA
     * @return
     */
    public OxidizedTheoreticalCompound construct(Double oxidizedFAEM,
            Double nonOxidizedFAEM,
            Double parentIonEM,
            String oxidationType,
            String adductType,
            FACompound oxidizedFA,
            FACompound nonOxidizedFA);

}
