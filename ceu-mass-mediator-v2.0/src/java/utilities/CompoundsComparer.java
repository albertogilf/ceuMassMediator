package utilities;

import importers.CompoundForPathway;
import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class CompoundsComparer implements Comparator<CompoundForPathway>, Serializable{

    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(CompoundForPathway o1, CompoundForPathway o2) {
        double molWeightO2= Double.parseDouble(o2.molecularWeight);
        double expMassO2= Double.parseDouble(o2.expmass);
        double molWeightO1= Double.parseDouble(o1.molecularWeight);
        double expMassO1= Double.parseDouble(o1.expmass);
        final double massDifferenceForO2 = Math.abs(molWeightO2- expMassO2);
        final double massDifferenceForO1 = Math.abs(molWeightO1- expMassO1);
        int result = (int)((massDifferenceForO1 - massDifferenceForO2)*10000000);
        return result;
    }
    
}
