/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package importers;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author: San Pablo-CEU, Maria Postigo Fliquete
 * @version: 4.0, 31/12/2016
 */
public class ComparatorExclusivity implements Serializable, Comparator <Pathway> {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Compares the two specified pathways
     *
     * @param   p1        the first pathway to compare
     * @param   p2        the second pathway to compare
     * @return  the value -1 if p1 has lower incidence than p2 in the compounds list;
     * 1 if p1 has higher incidence than p2 in the compound list; and 0 other case
     * 
     */
    @Override
    public int compare(Pathway p1, Pathway p2) {
        //if(Double.compare(p2.getMaxExclusivity(),p2.getMaxExclusivity())==1){return -1;}
        //else if(Double.compare(p2.getMaxExclusivity(),p2.getMaxExclusivity())==-1){return 1;}
        
        if(p1.getMaxExclusivity()>p2.getMaxExclusivity()){return -1;}
        if(p1.getMaxExclusivity()<p2.getMaxExclusivity()){return 1;}
        //if the exclusivity is the same it should compare the percentages
        //if(Double.compare(p2.getPercentage(),p2.getPercentage())==1){return -1;}
        //else if(Double.compare(p2.getPercentage(),p2.getPercentage())==-1){return 1;}
        
        if(p1.getPercentage()>p2.getPercentage()){return -1;}
        if(p1.getPercentage()<p2.getPercentage()){return 1;}
        else{return 0;}
    }

    
    
}
