package persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * JPA definition for compounds from metlin
 *
 * @author: San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity
//@Table(name = "compounds_metlin")
//Changed because now the compounds from metlin are in the table compounds_agilent
@Table(name = "compounds_agilent")
public class NewCompoundsMetlin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Size(max = 20)
    @Column(name = "agilent_id")
    private String agilentId;

    public NewCompoundsMetlin() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    /*
    public int getMetlinId() {
        return metlinId;
    }

    public void setMetlinId(int metlinId) {
        this.metlinId = metlinId;
    }
     */

    public String getAgilentId() {
        return agilentId;
    }

    public void setAgilentId(String agilentId) {
        this.agilentId = agilentId;
    }

}
