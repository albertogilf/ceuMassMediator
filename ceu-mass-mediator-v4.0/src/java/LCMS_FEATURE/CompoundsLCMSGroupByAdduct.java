/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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
    private final Map<Double, Double> CS;
    private final String adduct;
    private final double EMMode;
    private int ionizationMode;
    private List<CompoundLCMS> compounds;

    public CompoundsLCMSGroupByAdduct(double EM, String adduct, int EMMode, int ionizationMode) {
        this(EM, 0d, new TreeMap<Double, Double>(), adduct, EMMode, ionizationMode, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, String adduct, int EMMode, int ionizationMode) {
        this(EM, RT, new TreeMap<Double, Double>(), adduct, EMMode, ionizationMode, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, Map<Double, Double> CS,
            String adduct, int EMMode, int ionizationMode) {
        this(EM, 0d, CS, adduct, EMMode, ionizationMode, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, Map<Double, Double> CS,
            String adduct, int EMMode, int ionizationMode) {
        this(EM, RT, CS, adduct, EMMode, ionizationMode, new LinkedList<CompoundLCMS>());
    }

    public CompoundsLCMSGroupByAdduct(double EM, double RT, Map<Double, Double> CS,
            String adduct, int EMMode, int ionizationMode, List<CompoundLCMS> compounds) {
        this.EM = EM;
        this.RT = RT;
        this.CS = CS;
        this.EMMode = EMMode;
        this.ionizationMode = ionizationMode;
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
        if (!Objects.equals(this.EM,other.EM)) {
            return false;
        }
        if (!Objects.equals(this.RT,other.RT)){
            return false;
        }
        if (!Objects.equals(this.adduct,other.adduct)){
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

    public Map<Double, Double> getCS() {
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
        } else {
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

    

    @Override
    public String toString() {

        return "Compounds group by adduct with RT: " + this.RT + " and EM: " + this.EM + " adduct: " + this.adduct;
    }

    public String titleMessage() {
        return "Compounds grouped by adduct " + this.adduct;
    }

}
