package persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

  /**
   * JPA definition for compounds from pubchemical
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
@Entity
@Table(name = "compounds_npatlas")
public class NewCompoundsNPAtlas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Column(name = "npatlas_id")
    private int npatlasId;

    public NewCompoundsNPAtlas() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getNpatlasId() {
        return npatlasId;
    }

    public void setNpatlasId(int npatlasId) {
        this.npatlasId = npatlasId;
    }

}
