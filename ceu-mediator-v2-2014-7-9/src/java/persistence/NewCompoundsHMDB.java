package persistence;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * JPA definition for compounds_hmdb
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */

@Entity
@Table(name="compounds_hmdb")
public class NewCompoundsHMDB implements Serializable {
     private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;
    
    @Size(max = 20)
    @NotNull
    @Column(name = "hmdb_id")
    private String hmdbId;

    public NewCompoundsHMDB() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getHmdbId() {
        return hmdbId;
    }

    public void setHmdbId(String hmdbId) {
        this.hmdbId = hmdbId;
    }
    
    
}

