/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import List.NoDuplicatesList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * FeaturesGroupByRT. Consist on a set of features grouped by their retention
 * time (RT). One instance of FeaturesGroupByRT contains 1 or more features.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class FeaturesGroupByRT {

    private final double RT;
    private final List<Feature> features;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.RT) ^ (Double.doubleToLongBits(this.RT) >>> 32));
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
        final FeaturesGroupByRT other = (FeaturesGroupByRT) obj;
        if (!Objects.equals(this.RT, other.RT)) {
            return false;
        }
        if (this.RT == 0) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param RT
     */
    public FeaturesGroupByRT(double RT) {
        this(RT, new NoDuplicatesList());
    }

    /**
     *
     * @param RT
     * @param features
     */
    public FeaturesGroupByRT(double RT, List<Feature> features) {
        this.RT = RT;
        this.features = features;
    }

    public double getRT() {
        return this.RT;
    }

    public List<Feature> getFeatures() {
        return this.features;
    }

    /**
     * Add a feature to the group of features by the RT.
     *
     * @param feature
     */
    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

    // TODO method to search feature based on EM and RT
    /**
     * Search a Feature based on EM and RT and return it to the user.
     *
     * @param EM
     * @param RT
     * @return
     */
    public Feature getFeatureByEMandRT(Double EM, Double RT) {
        return null;
    }

    /**
     * Delete a feature to the group of features by the RT. This feature should
     * be previously searched into the list.
     *
     * @param feature
     */
    public void deleteFeature(Feature feature) {
        this.features.remove(feature);
    }

    @Override
    public String toString() {
        Iterator it = this.features.iterator();
        String toreturn = "";
        while (it.hasNext()) {

            toreturn += ((Feature) it.next()).toString() + "\n";
        }
        return "RETENTION TIME MIDPOINT: " + this.RT + "\n" + toreturn;
    }

    /**
     *
     * @return the number of features in the group of features.
     */
    public Integer getNumFeatures() {
        if (this.features == null) {
            return 0;
        }
        return this.features.size();

    }

    public String getTitleMessage() {
        if (this.RT != 0) {
            return "Features grouped by retention time: " + this.RT;
        }
        return "";
    }
}
