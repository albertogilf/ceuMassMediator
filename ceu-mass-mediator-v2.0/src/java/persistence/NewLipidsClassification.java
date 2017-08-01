/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "compounds_lipids_classification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewLipidsClassification.findAll", query = "SELECT nc FROM NewLipidsClassification nc"),
    @NamedQuery(name = "NewLipidsClassification.findByCompoundId", query = "SELECT nc FROM NewLipidsClassification nc WHERE nc.compoundId = :compoundId")})
public class NewLipidsClassification implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    @Size(max = 10)
    @Column(name = "category")
    private String category;
        
    @Size(max = 10)
    @Column(name = "main_class")
    private String mainClass;
            
    @Size(max = 10)
    @Column(name = "sub_class")
    private String subClass;
    
    @Size(max = 10)
    @Column(name = "number_carbons")
    private int carbons;
        
    @Size(max = 10)
    @Column(name = "double_bonds")
    private int doubleBonds;
    
    public NewLipidsClassification() {
    }

    public NewLipidsClassification(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getSubClass() {
        return subClass;
    }

    public void setSubClass(String subClass) {
        this.subClass = subClass;
    }

    public int getCarbons() {
        return carbons;
    }
    
    public void setCarbons(int carbons) {
        this.carbons = carbons;
    }
        
    public int getDoubleBonds() {
        return doubleBonds;
    }
    
    public void setDoubleBonds(int doubleBonds) {
        this.doubleBonds = doubleBonds;
    }
    
}