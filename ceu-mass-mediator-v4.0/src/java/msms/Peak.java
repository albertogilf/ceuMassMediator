/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.util.Objects;

/**
 * Peak Object. 
 *
 * @author Maria Postigo. Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 15/04/2018
 */
public class Peak implements Comparable<Peak> {

    private final Double mz;
    private final Double intensity;
    private final Integer msms_id;

    public Peak(Double mz, Double intensity) {
        this.intensity = intensity;
        this.mz = mz;
        this.msms_id = 0;
    }

    public Peak(Double mz, Double intensity, Integer msmsId) {
        this.intensity = intensity;
        this.mz = mz;
        this.msms_id = msmsId;
    }

    public Integer getMsms_id() {
        return msms_id;
    }



    public Double getIntensity() {
        return intensity;
    }

    public Double getMz() {
        return mz;
    }

    @Override
    public String toString() {

        return " INTENSITY: " + this.intensity + " MASS CHARGE: " + this.mz;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.mz) ^ (Double.doubleToLongBits(this.mz) >>> 32));
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
        final Peak other = (Peak) obj;
        if (!Objects.equals(this.intensity, other.intensity)) {
            return false;
        }
        if (!Objects.equals(this.mz, other.mz)) {
            return false;
        }
        return true;
    }

    //Rewrite compareTo in order to compare the peaks acording to their mz
    //in ascending order to pair the peaks properly
    @Override
    public int compareTo(Peak peak) {
        int result;
        if (this.mz < peak.getMz()) {
            result = -1;
        } else if (this.mz > peak.getMz()) {
            result = 1;
        } else {
            if (this.intensity < peak.getIntensity()) {
                result = -1;
            } else if (this.intensity > peak.getIntensity()) {
                result = 1;
            } else {
                return 0;
            }
        }
        return result;
    }

    /**
     * This method checks if the peaks are similar within the given tolerance in Da
     * 
     * @param libraryPeak is the peak from the candidate obtained through the database
     * @param mzTolerance is the tolerance provide by the user
     * @return true or false according to if the peak is withing the window tolerance
     */
    public boolean peakMatch(Peak libraryPeak, double mzTolerance) {
        
        //return  this.mz<= (libraryPeak.getMz() + mzTolerance) &&  this.mz>= (libraryPeak.getMz() - mzTolerance);
        return  libraryPeak.getMz()<= (this.mz + mzTolerance) &&  libraryPeak.getMz()>= (this.mz - mzTolerance);
    }

    public void setId(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
