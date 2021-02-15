/*
 * CEMSAnnotation.java
 *
 * Created on 30-dic-2019, 16:56:10
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

import java.util.Objects;
import static utilities.Utilities.calculatePPMIncrement;
import static utilities.Utilities.calculatePercentageError;

/**
 * Annotations for CEMS Compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 5.0.0.0 30-dic-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSAnnotationFragment extends CEMSFragment implements CEMSAnnotationAdapter {

    private final Double exp_mz;
    private final Double exp_MT;
    private final Double exp_RMT;
    private final Double exp_effMob;
    private final Integer errorMZ;
    private final Integer errorMT;
    private final Integer errorRMT;
    private final Integer errorEffMob;

    private final String colorFoundInStandard;

    /**
     *
     * @param exp_mz
     * @param exp_MT
     * @param exp_RMT
     * @param exp_effMob
     * @param effMob
     * @param MT
     * @param RMT
     * @param ion_source_voltage
     * @param mz
     * @param intensity
     * @param transformation_type
     * @param name
     * @param precursorIon
     */
    public CEMSAnnotationFragment(Double exp_mz, Double exp_effMob, Double exp_MT, Double exp_RMT,
            Double effMob, Double MT, Double RMT, Integer ion_source_voltage, Double mz, Double intensity,
            String transformation_type, String name, CEMSCompound precursorIon, boolean foundInStandard) {
        super(effMob, MT, RMT, ion_source_voltage, mz, intensity, transformation_type, name,
                precursorIon);
        this.exp_mz = exp_mz;
        this.exp_MT = exp_MT;
        this.exp_RMT = exp_RMT;
        this.exp_effMob = exp_effMob;
        if (this.exp_mz != null && super.getMz() != null) {
            this.errorMZ = calculatePPMIncrement(this.exp_mz, super.getMz());
        } else {
            this.errorMZ = null;
        }
        if (this.exp_MT != null && super.getMT() != null) {
            this.errorMT = calculatePercentageError(this.exp_MT, super.getMT());
        } else {
            this.errorMT = null;
        }
        if (this.exp_RMT != null && super.getRMT() != null) {
            this.errorRMT = calculatePercentageError(this.exp_RMT, super.getRMT());
        } else {
            this.errorRMT = null;
        }
        if (this.exp_effMob != null && super.getEffMob() != null) {
            this.errorEffMob = calculatePercentageError(this.exp_effMob, super.getEffMob());
        } else {
            this.errorEffMob = null;
        }
        if (foundInStandard) {
            this.colorFoundInStandard = "row_EXPECTED";
        } else {
            this.colorFoundInStandard = "";
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.exp_mz);
        hash = 37 * hash + Objects.hashCode(this.exp_MT);
        hash = 37 * hash + Objects.hashCode(this.exp_RMT);
        hash = 37 * hash + Objects.hashCode(this.exp_effMob);
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
        final CEMSAnnotationFragment other = (CEMSAnnotationFragment) obj;
        if (!Objects.equals(this.exp_mz, other.exp_mz)) {
            return false;
        }
        if (!Objects.equals(this.exp_MT, other.exp_MT)) {
            return false;
        }
        if (!Objects.equals(this.exp_RMT, other.exp_RMT)) {
            return false;
        }
        if (!Objects.equals(this.exp_effMob, other.exp_effMob)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CEMSAnnotationFragment{" + "exp_mz=" + exp_mz + ", exp_MT=" + exp_MT + ", exp_RMT=" + exp_RMT
                + ", exp_effMob=" + exp_effMob + ", errorMZ=" + errorMZ + ", errorMT=" + errorMT + ", errorRMT="
                + errorRMT + ", errorEffMob=" + errorEffMob + '}';
    }

    @Override
    public Integer getErrorMT() {
        return errorMT;
    }

    @Override
    public Double getExp_mz() {
        return exp_mz;
    }

    @Override
    public Double getExp_MT() {
        return exp_MT;
    }
    
    @Override
    public String getAdduct() {
        return "";
    }

    @Override
    public Double getExp_RMT() {
        return exp_RMT;
    }

    @Override
    public Double getExp_effMob() {
        return exp_effMob;
    }

    @Override
    public Integer getErrorMZ() {
        return errorMZ;
    }

    @Override
    public Integer getErrorRMT() {
        return errorRMT;
    }

    @Override
    public Integer getErrorEffMob() {
        return errorEffMob;
    }

    public String getColorFoundInStandard() {
        return colorFoundInStandard;
    }

}
