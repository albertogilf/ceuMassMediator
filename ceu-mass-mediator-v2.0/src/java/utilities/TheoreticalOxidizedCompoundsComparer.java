/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.Serializable;
import java.util.Comparator;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.0, 08/09/2017
 */
public class TheoreticalOxidizedCompoundsComparer implements Comparator<OxidizedTheoreticalCompound>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * This method compares the Compounds, with the Id If both are null = 0; If
     * the first is null= 1 or if the second is null, the result is -1.
     *
     * @param o1. TheoreticalCompounds
     * @param o2. TheoreticalCompounds
     * @return return a result identifying if CasId of o1 is greater than casId
     * of o2
     */
    @Override
    public int compare(OxidizedTheoreticalCompound o1, OxidizedTheoreticalCompound o2) {
        Double theoreticalWeightForO1 = o1.getTheoreticalPIMolecularWeight();
        Double theoreticalWeightForO2 = o2.getTheoreticalPIMolecularWeight();
        final Double massDifferenceForO2 = Math.abs(theoreticalWeightForO2 - o2.getNeutralMassPI());
        final Double massDifferenceForO1 = Math.abs(theoreticalWeightForO1 - o1.getNeutralMassPI());
        int result = (int) ((massDifferenceForO1 - massDifferenceForO2) * 10000000);

        return result;

    }

}
