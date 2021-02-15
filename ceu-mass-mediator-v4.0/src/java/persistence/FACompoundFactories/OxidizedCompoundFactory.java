/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.FACompoundFactories;

import persistence.oxidizedTheoreticalCompound.FACompound;
import persistence.oxidizedTheoreticalCompound.OxidizedCompound;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.0, 08/09/2017
 */
public class OxidizedCompoundFactory implements OxidizedTheoreticalCompoundFactory {

    @Override
    public OxidizedTheoreticalCompound construct(Double oxidizedFAEM,
            Double nonOxidizedFAEM,
            Double parentIonEM,
            String oxidationType,
            String adductType,
            FACompound oxidizedFA,
            FACompound nonOxidizedFA) {
        return new OxidizedCompound(oxidizedFAEM,
                nonOxidizedFAEM,
                parentIonEM,
                oxidationType,
                adductType,
                oxidizedFA,
                nonOxidizedFA);
    }

}
