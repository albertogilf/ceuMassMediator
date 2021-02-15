package persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

 /**
   * JPA definition for compounds from kegg
   * @author: San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */

@Entity
@Table(name="compounds_kegg")
public class NewCompoundsKegg implements Serializable {
     private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;
    
    @Size(max = 20)
    @NotNull
    @Column(name = "kegg_id")
    private String keggId;

    public NewCompoundsKegg() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getKeggId() {
        return keggId;
    }

    public void setKeggId(String keggId) {
        this.keggId = keggId;
    }
    
    
}
