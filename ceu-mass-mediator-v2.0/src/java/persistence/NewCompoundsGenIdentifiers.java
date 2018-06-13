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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
   * JPA definition for Generated compound identifiers
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 05/05/2017
   */
@Entity
@Table(name = "compound_gen_identifiers")
@NamedQueries({
    @NamedQuery(name = "NewCompoundsGenIdentifiers.findAll", query = "SELECT ncgi FROM NewCompoundsGenIdentifiers ncgi"),
    @NamedQuery(name = "NewCompoundsGenIdentifiers.findInchiKeyByCompoundIdGen", query = "SELECT ncgi.inchiKey FROM NewCompoundsGenIdentifiers ncgi WHERE ncgi.compoundId = :compoundId"),
    @NamedQuery(name = "NewCompoundsGenIdentifiers.findGenCompoundIDByInchiKey", query = "SELECT ncgi.compoundId FROM NewCompoundsGenIdentifiers ncgi WHERE ncgi.inchiKey = :inchiKey")})
public class NewCompoundsGenIdentifiers implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Size(max = 50)
    @Column(name = "inchi_key")
    private String inchiKey;
    
    @NotNull
    @Size(max = 1200)
    @Column(name = "smiles")
    private String smiles;

    public NewCompoundsGenIdentifiers() {
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getInchiKey() {
        return inchiKey;
    }

    public void setInchiKey(String inchiKey) {
        this.inchiKey = inchiKey;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }
    
    
}
