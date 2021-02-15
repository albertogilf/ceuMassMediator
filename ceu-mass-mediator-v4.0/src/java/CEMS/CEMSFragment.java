/*
 * CEMSFragment.java
 *
 * Created on 29-may-2019, 20:45:39
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.util.Objects;

/**
 * CEMS Fragment (fragments, adducts and other alterations) over the
 * compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 29-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSFragment implements Comparable<CEMSFragment> {

    private Double effMob;
    private Double MT;
    private Double RMT;

    private final Integer ion_source_voltage;
    private final Double mz;
    private final Double intensity;
    private final String transformation_type;
    private final String name;
    private final CEMSCompound precursorIon;

    /**
     * Constructor of ceFragment
     *
     * @param effMob
     * @param MT
     * @param ion_source_voltage
     * @param mz
     * @param RMT
     * @param intensity
     * @param transformation_type
     * @param precursorIon
     * @param name
     */
    public CEMSFragment(Double effMob, Double MT, Double RMT, Integer ion_source_voltage, Double mz, Double intensity,
            String transformation_type, String name, CEMSCompound precursorIon) {

        this.effMob = effMob;
        this.MT = MT;
        this.RMT = RMT;
        this.ion_source_voltage = ion_source_voltage;
        this.mz = mz;
        this.intensity = intensity;
        this.transformation_type = transformation_type;
        this.name = name;
        this.precursorIon = precursorIon;

    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int result = 1;
        result = prime + (int) Math.round(this.mz);
        result = prime * result + this.precursorIon.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!Objects.equals(getClass(), obj.getClass())) {
            return false;
        }
        final CEMSFragment other = (CEMSFragment) obj;

        if (this.mz != null && other.getMz() != null && Math.abs(this.mz - other.getMz()) < 1d) {
            if (this.intensity != null && other.getIntensity() != null && Math.abs(this.intensity - other.getIntensity()) > 10d) {
                return false;
            } else {
                if (Objects.equals(this.ion_source_voltage, other.getIon_source_voltage())) {
                    if (this.ion_source_voltage != null && other.getIon_source_voltage() != null
                            && Math.abs(this.ion_source_voltage - other.getIon_source_voltage()) > 25d) {
                        return false;
                    }
                    if (this.precursorIon != null && other.getPrecursorIon() != null
                            && this.precursorIon != other.getPrecursorIon()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CEMSFragment{" + "effMob=" + effMob + ", MT=" + MT + ", RMT=" + RMT + ", voltage=" + ion_source_voltage
                + ", mz=" + mz + ", intensity=" + intensity + ", transformation_type=" + transformation_type
                + ", name=" + name + ", precursorIon=" + precursorIon + '}';
    }

    public Double getEffMob() {
        return effMob;
    }

    public Double getMT() {
        return MT;
    }

    public Double getRMT() {
        return RMT;
    }

    public void setEffMob(Double effMob) {
        this.effMob = effMob;
    }

    public void setMT(Double MT) {
        this.MT = MT;
    }

    public void setRMT(Double RMT) {
        this.RMT = RMT;
    }

    public Integer getIon_source_voltage() {
        return ion_source_voltage;
    }

    public Double getMz() {
        return mz;
    }

    public Double getIntensity() {
        return intensity;
    }

    public String getTransformation_type() {
        return transformation_type;
    }

    public String getName() {
        return name;
    }

    public CEMSCompound getPrecursorIon() {
        return precursorIon;
    }

    @Override
    public int compareTo(CEMSFragment o) {
        int result = (int) Math.round(this.mz) - (int) Math.round(o.mz);
        if (result == 0) {
            if (this.intensity != null && o.intensity != null) {
                result = (int) Math.round(this.intensity) - (int) Math.round(o.intensity);
                if (result == 0) {
                    if (this.ion_source_voltage != null && o.ion_source_voltage != null) {
                        result = this.ion_source_voltage - o.ion_source_voltage;
                        if (result == 0) {
                            if (this.precursorIon != null && o.precursorIon != null) {
                                result = this.precursorIon.getCompound_id() - o.precursorIon.getCompound_id();
                            } else if (this.precursorIon != null) {
                                return 1;
                            } else if (o.precursorIon != null) {
                                return -1;
                            }
                        }
                    } else if (this.ion_source_voltage != null) {
                        return 1;
                    } else if (o.ion_source_voltage != null) {
                        return -1;
                    }
                }
            } else {
                if (this.ion_source_voltage != null && o.ion_source_voltage != null) {
                    result = this.ion_source_voltage - o.ion_source_voltage;
                }
            }
        }
        return result;
    }

    public static String roundToFourDecimals(Double doubleToRound) {
        if (doubleToRound == null) {
            return "--";
        }
        return String.format("%.4f", doubleToRound).replace(",", ".");
    }
}
