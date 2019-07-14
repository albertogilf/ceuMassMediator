/*
 * CEMSCompoundComparer.java
 *
 * Created on 29-may-2019, 0:21:39
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparer for CEMS Compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 29-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSProductIonComparer implements Comparator<CEMSProductIon>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * This method compares the Compounds, with the Id If both are null = 0; If
     * the first is null= 1 or if the second is null, the result is -1.
     *
     * @param ceMS1. ceMSCompound 1
     * @param ceMS2. ceMSCompound 1
     * @return return a result identifying if CasId of o1 is greater than casId
     * of o2
     */
    @Override
    public int compare(CEMSProductIon ceMS1, CEMSProductIon ceMS2) {

        int result = ceMS1.getIncrementPPM() - ceMS2.getIncrementPPM();
        if(result == 0)
        {
            result = ceMS1.getErrorRMT() - ceMS2.getErrorRMT();
        }

        return result;
    }
}
