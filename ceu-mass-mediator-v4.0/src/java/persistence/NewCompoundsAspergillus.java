/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * JPA definition for compounds extracted in CEMBIO
 *
 * @author: San Pablo-CEU
 * @version: 4.0, 28/09/2017
 */
@Entity
//@Table(name = "compounds_metlin")
//Changed because now the compounds from metlin are in the table compounds_agilent
@Table(name = "compounds_aspergillus")
public class NewCompoundsAspergillus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Column(name = "aspergillus_id")
    private int aspergillusId;

    @NotNull
    @Size(max = 100)
    @Column(name = "mesh_nomenclature")
    private String meshNomenclature;

    @NotNull
    @Size(max = 100)
    @Column(name = "iupac_classification")
    private String iupacClassification;

    @NotNull
    @Size(max = 100)
    @Column(name = "aspergillus_web_name")
    private String aspergillusWebName;

    public NewCompoundsAspergillus() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getAspergillusId() {
        return aspergillusId;
    }

    public void setAspergillusId(int aspergillusId) {
        this.aspergillusId = aspergillusId;
    }

    public String getMeshNomenclature() {
        return meshNomenclature;
    }

    public void setMeshNomenclature(String meshNomenclature) {
        this.meshNomenclature = meshNomenclature;
    }

    public String getIupacClassification() {
        return iupacClassification;
    }

    public void setIupacClassification(String iupacClassification) {
        this.iupacClassification = iupacClassification;
    }

    public String getAspergillusWebName() {
        return aspergillusWebName;
    }

    public void setAspergillusWebName(String aspergillusWebName) {
        this.aspergillusWebName = aspergillusWebName;
    }

}
