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
        /*
        System.out.println("\n Compound 1: " + o1.getCasId() + "  Compound 2: " + o2.getCasId());
        System.out.println("\n DF Compound 1: " + massDifferenceForO1 + " DF Compound 2: " + massDifferenceForO2 + " RETURN: " + (int)((massDifferenceForO1 - massDifferenceForO2)*10000000));
        if (o1.getCasId() != null && o2.getCasId()!= null && o1.getCasId().equals(o2.getCasId()))
        {
            // System.out.println("\n Compound 1: " + o1.getCasId() + "  Compound 2: " + o2.getCasId());
            return 0;
        }
        else
        {
        */
// return dateComparison == 0 ? a.value.compareTo(b.value) : dateComparison; 
            //return (o1.getCasId() != null && o2.getCasId()!= null && o1.getCasId().equals(o2.getCasId())) == true ? 0 : result +1;
            return result;
//        }
        
        // return (int)((massDifferenceForO1 - massDifferenceForO2)*10000000);
        
/*
        String casId1 = o1.getCasId();
        String casId2 = o2.getCasId();
        
        if (casId1 == null && casId2 == null) return 0;
        else if (casId1==null) return 1;
        else if (casId2==null) return -1;
        else return (casId1.compareToIgnoreCase(casId2));
*/
    }
    
}
