/*
 * CEProductIon.java
 *
 * Created on 28-may-2019, 12:06:18
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */

package persistence;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * JPA class for CEMS product ions (fragments and adducts)
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 27-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
@Entity
@Table(name = "compound_ce_product_ion")
@NamedQueries({
    @NamedQuery(name = "CEProductIon.findAll", query = "SELECT ccepi FROM CEProductIon ccepi")})
public class CEProductIon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ce_product_ion_id")
    private int ceProductIonId;

    // Compound_id_own
    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ce_ms_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsCE ncce;

    @Column(name = "voltage")
    private int voltage;

    @Column(name = "ce_product_ion_mz")
    private Double mz;

    @Column(name = "ce_product_ion_intensity")
    private Double intensity;

    @Column(name = "ce_transformation_type")
    private String transformationType;

    @Column(name = "ce_product_ion_name")
    private String ceProductIonName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id_own", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompounds nc_own;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof CEProductIon)) {
            return false;
        }
        return ceProductIonId != 0 && ceProductIonId == ((CEProductIon) o).getCeProductIonId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.ceProductIonId);
    }
    
    @Override
    public String toString() {
        return "Product ion: " + ceProductIonId + ", ncce";
    }

    public int getCeProductIonId() {
        return ceProductIonId;
    }

    public void setCeProductIonId(int ceProductIonId) {
        this.ceProductIonId = ceProductIonId;
    }

    public NewCompoundsCE getNcce() {
        return ncce;
    }

    public void setNcce(NewCompoundsCE ncce) {
        this.ncce = ncce;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public Double getMz() {
        return mz;
    }

    public void setMz(Double mz) {
        this.mz = mz;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public String getTransformationType() {
        return transformationType;
    }

    public void setTransformationType(String transformationType) {
        this.transformationType = transformationType;
    }

    public String getCeProductIonName() {
        return ceProductIonName;
    }

    public void setCeProductIonName(String ceProductIonName) {
        this.ceProductIonName = ceProductIonName;
    }

    public NewCompounds getNc_own() {
        return nc_own;
    }

    public void setNc_own(NewCompounds nc_own) {
        this.nc_own = nc_own;
    }

    
}
