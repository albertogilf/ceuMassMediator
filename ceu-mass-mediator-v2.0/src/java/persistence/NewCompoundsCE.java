/*
 * NewCompoundsCE.java
 *
 * Created on 27-may-2019, 19:18:38
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * JPA class for CEMS compounds
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 27-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
@Entity
@Table(name = "compound_ce_experimental_properties")
@NamedQueries({
    @NamedQuery(name = "NewCompoundsCE.findAll", query = "SELECT ncce FROM NewCompoundsCE ncce")})
public class NewCompoundsCE implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ce_ms_id")
    private int cemsId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = false, insertable = false, updatable = false)
    private NewCompounds nc;

    @OneToMany(mappedBy = "ncce", cascade = CascadeType.ALL)
    private List<CEProductIon> ceProductIons;

    @Column(name = "ionization_mode")
    private int ionizationMode;

    @Column(name = "polarity")
    private int polarity;
    /*
    @Column(name = "theoretical_mz")
    private Double theoreticalMZ;
     */
    @Column(name = "ce_sample_type")
    private int ceSampleType;

    @Column(name = "buffer")
    private int buffer;

    @Column(name = "bge")
    private int bge;

    @Column(name = "relative_MT")
    private Double RMT;

    public int getCemsId() {
        return cemsId;
    }

    public void setCemsId(int cemsId) {
        this.cemsId = cemsId;
    }

    public int getIonizationMode() {
        return ionizationMode;
    }

    public void setIonizationMode(int ionizationMode) {
        this.ionizationMode = ionizationMode;
    }

    public int getPolarity() {
        return polarity;
    }

    public void setPolarity(int polarity) {
        this.polarity = polarity;
    }

    /*
    public Double getTheoreticalMZ() {
        return theoreticalMZ;
    }

    public void setTheoreticalMZ(Double theoreticalMZ) {
        this.theoreticalMZ = theoreticalMZ;
    }
     */
    public int getCeSampleType() {
        return ceSampleType;
    }

    public void setCeSampleType(int ceSampleType) {
        this.ceSampleType = ceSampleType;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getBge() {
        return bge;
    }

    public void setBge(int bge) {
        this.bge = bge;
    }

    public Double getRMT() {
        return RMT;
    }

    public NewCompounds getNc() {
        return nc;
    }

    public void setNc(NewCompounds nc) {
        this.nc = nc;
    }

    public void setRMT(Double RMT) {
        this.RMT = RMT;
    }

    public List<CEProductIon> getCeProductIons() {
        return ceProductIons;
    }

    public void setCeProductIons(List<CEProductIon> ceProductIons) {
        this.ceProductIons = ceProductIons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof NewCompoundsCE)) {
            return false;
        }
        return cemsId != 0 && cemsId == ((NewCompoundsCE) o).getCemsId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.cemsId);
    }

    @Override
    public String toString() {
        return "CE Compound: " + cemsId + ", ion mode: " + ionizationMode
                + ", ceSampleType: " + ceSampleType
                + ", buffer: " + buffer + ", bge: " + bge + ", relative_MT: " + RMT;
        //+ " NC: " + super.toString();
    }

    public void addCEProductIon(CEProductIon ceProductIon) {
        ceProductIons.add(ceProductIon);
        ceProductIon.setNcce(this);
    }

    public void removeComment(CEProductIon ceProductIon) {
        ceProductIons.remove(ceProductIon);
        ceProductIon.setNcce(null);
    }
}
