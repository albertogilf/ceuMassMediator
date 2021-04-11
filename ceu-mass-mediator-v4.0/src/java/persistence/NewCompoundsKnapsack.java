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
@Table(name="compounds_knapsack")
public class NewCompoundsKnapsack implements Serializable {
     private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;
    
    @Size(max = 100)
    @NotNull
    @Column(name = "knapsack_id")
    private String knapsackId;

    public NewCompoundsKnapsack() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getKnapsackId() {
        return knapsackId;
    }

    public void setKnapsackId(String knapsackId) {
        this.knapsackId = knapsackId;
    }
    
    
}

