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
@Table(name = "compounds_in_house")
public class NewCompoundsInHouse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Size(max = 20)
    @Column(name = "in_house_id")
    private String inHouseId;

    @NotNull
    @Size(max = 20)
    @Column(name = "source_data")
    private String sourceData;

    @NotNull
    @Size(max = 200)
    @Column(name = "description")
    private String description;

    public NewCompoundsInHouse() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getInHouseId() {
        return inHouseId;
    }

    public void setInHouseId(String inHouseId) {
        this.inHouseId = inHouseId;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
