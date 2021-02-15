package utilities;

import java.io.Serializable;
import java.util.Comparator;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
   * 
   * @author: San Pablo-CEU, Alberto Gil de la Fuente
   * @version: 3.1, 17/02/2016
   */
public class TheoreticalCompoundsComparer implements Comparator<TheoreticalCompounds>, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    /** This method compares the Compounds, with the Id
     *  If both are null = 0; If the first is null= 1 or if the second is null, 
     *  the result is -1.
     * @param o1. TheoreticalCompounds
     * @param o2. TheoreticalCompounds
     * @return return a result identifying if CasId of o1 is greater than casId of o2
     */
 
    @Override
    public int compare(TheoreticalCompounds o1, TheoreticalCompounds o2) {
        
        final Double massDifferenceForO2 = Math.abs(o2.getMolecularWeight()- o2.getExperimentalMass());
        final Double massDifferenceForO1 = Math.abs(o1.getMolecularWeight()- o1.getExperimentalMass());
        int result = (int)((massDifferenceForO1 - massDifferenceForO2)*10000000);
        
        // System.out.println("\n Compound 1: " + o1.getMolecularWeight() + "  Compound 2: " + o2.getMolecularWeight());
            return result;
    }
    
}
