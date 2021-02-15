
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

/**
 * JPA definition for Compounds LM Classification
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
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

    @Size(max = 20)
    @Column(name = "lipid_type")
    private String lipidType;
    
    @Size(max = 2)
    @Column(name = "num_chains")
    private int numChains;
    
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

    public String getLipidType() {
        return lipidType;
    }

    public void setLipidType(String lipidType) {
        this.lipidType = lipidType;
    }

    public int getNumChains() {
        return numChains;
    }

    public void setNumChains(int numChains) {
        this.numChains = numChains;
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
