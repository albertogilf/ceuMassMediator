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
@Table(name = "compounds_fahfa")
public class NewCompoundsFahfa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Column(name = "fahfa_id")
    private int fahfaId;

    @Column(name = "oh_position")
    private int ohPosition;

    public NewCompoundsFahfa() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getFahfaId() {
        return fahfaId;
    }

    public void setFahfaId(int fahfaId) {
        this.fahfaId = fahfaId;
    }
    public int getOhPosition() {
        return this.ohPosition;
    }

    public void setOhPosition(int ohPosition) {
        this.ohPosition = ohPosition;
    }

}
