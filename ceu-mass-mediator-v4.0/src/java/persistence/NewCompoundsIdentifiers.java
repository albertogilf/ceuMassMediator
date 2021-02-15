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
   * JPA definition for compound identifiers
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 05/05/2017
   */
@Entity
@Table(name = "compound_identifiers")
@NamedQueries({
    @NamedQuery(name = "NewCompoundsIdentifiers.findAll", query = "SELECT nci FROM NewCompoundsIdentifiers nci"),
    @NamedQuery(name = "NewCompoundsIdentifiers.findInchiKeyByCompoundId", query = "SELECT nci.inchiKey FROM NewCompoundsIdentifiers nci WHERE nci.compoundId = :compoundId"),
    @NamedQuery(name = "NewCompoundsIdentifiers.findCompoundIDByInchiKey", query = "SELECT nci.compoundId FROM NewCompoundsIdentifiers nci WHERE nci.inchiKey = :inchiKey")})
public class NewCompoundsIdentifiers implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "compound_id")
    private int compoundId;

    @NotNull
    @Size(max = 50)
    @Column(name = "inchi_key")
    private String inchiKey;
    
    @Size(max = 1200)
    @Column(name = "smiles")
    private String smiles;

    public NewCompoundsIdentifiers() {
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