/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * CompoundsLCMSGroupByAdduct. Represents a set of compounds grouped by: -
 * retention time (indirectly) since they come from features grouped by rt -
 * Adducts (directly) They aren't grouped by experimental mass since it exits
 * the possibility of having the same adduct with different masses (it doesn't
 * really happens) - Since it doent's really happens they are also grouped
 * indirectly by mass (EM)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class CompoundsLCMSGroupByAdduct {

    private final double EM;
    private final double RT;
    private final Map<Double, Integer> CS;
    private final String adduct;
    private List<CompoundLCMS> compounds;

    public CompoundsLCMSGroupByAdduct(double EM, String adduct) {
        this(EM, 0d, new LinkedHashMap<Double, Integer>(), adduct, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, String adduct) {
        this(EM, RT, new LinkedHashMap<Double, Integer>(), adduct, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, Map<Double, Integer> CS,
            String adduct) {
        this(EM, 0d, CS, adduct, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, Map<Double, Integer> CS,
            String adduct) {
        this(EM, RT, CS, adduct, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, Map<Double, Integer> CS,
            String adduct, List<CompoundLCMS> compounds) {
        this.EM = EM;
        this.RT = RT;
        this.CS = CS;
        this.adduct = adduct;
        this.compounds = compounds;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.EM) ^ (Double.doubleToLongBits(this.EM) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.RT) ^ (Double.doubleToLongBits(this.RT) >>> 32));
        hash = 37 * hash + Objects.hashCode(this.adduct);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CompoundsLCMSGroupByAdduct other = (CompoundsLCMSGroupByAdduct) obj;
        if (Double.doubleToLongBits(this.EM) != Double.doubleToLongBits(other.EM)) {
            return false;
        }
        if (Double.doubleToLongBits(this.RT) != Double.doubleToLongBits(other.RT)) {
            return false;
        }
        if (!Objects.equals(this.adduct, other.adduct)) {
            return false;
        }
        return true;
    }

    
    public double getEM() {
        return this.EM;
    }

    public double getRT() {
        return this.RT;
    }

    public Map<Double, Integer> getCS() {
        return this.CS;
    }

    public String getAdduct() {
        return this.adduct;
    }

    public List<CompoundLCMS> getCompounds() {
        return this.compounds;
    }

    public void addAnnotation(CompoundLCMS annotation) {
        compounds.add(annotation);
    }

    public int getNumberAnnotations() {
        if (this.compounds != null) {
            return compounds.size();
        }
        else {
            return 0;
        }
    }

    public boolean isEmpty() {
        return compounds.isEmpty();
    }

    public boolean isThereTheoreticalCompounds() {
        return getNumberAnnotations() > 0;
    }

    public void setCompounds(List<CompoundLCMS> compounds) {
        this.compounds = compounds;
    }

    
    public void deleteAnnotation(CompoundLCMS annotation) {
        this.compounds.remove(annotation);
    }

    public void deleteAllAnnotations() {
        this.compounds.clear();
    }

    /**
     * Get title for the presentation
     *
     * @return
     */
    public String getTitleMessage() {
        String titleMessage;
        titleMessage = "Adduct: " + this.adduct;

        if (!isThereTheoreticalCompounds()) {
            titleMessage = "No results for " + titleMessage;
        } else {
            titleMessage = titleMessage + " -> " + getNumberAnnotations();
        }
        return titleMessage;
    }

    @Override
    public String toString() {
        /*
        Iterator it= this.compounds.iterator();
        if(this.compounds.isEmpty()){return "";}
        String toreturn="";
        while(it.hasNext())
        {
            toreturn+=((CompoundLCMS)it.next()).toString()+"\n";
        }
        
        return "        COMPOUNDS GROUPED BY ADDUCT "+this.adduct+" (rt: "+this.RT+" em: "+this.EM+")\n"+toreturn;
         */
        return "Compounds group by adduct with RT: " + this.RT + " and EM: " + this.EM + " adduct: " + this.adduct;
    }

    public String titleMessage() {
        return "Compounds grouped by adduct " + this.adduct;
    }

}