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
 * JPA definition for Chains
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity(name = "NewLipidsClassificationChains")
@Table(name = "compounds_lipids_classification_chains")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewLipidsClassificationChains.findAll", query = "SELECT nlcc FROM NewLipidsClassificationChains nlcc"),
    @NamedQuery(name = "NewLipidsClassificationChains.findByCompoundId", query = "SELECT nlcc FROM NewLipidsClassification nlcc WHERE nlcc.compoundId = :compoundId")})
public class NewLipidsClassificationChains implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    @Size(max = 10)
    @Column(name = "number_carbons")
    private int carbons;

    @Size(max = 10)
    @Column(name = "double_bonds")
    private int doubleBonds;

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
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
