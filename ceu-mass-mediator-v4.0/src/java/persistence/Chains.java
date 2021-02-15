/*
 * Chains.java
 *
 * Created on 21-mar-2018, 19:40:30
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package persistence;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 21-mar-2018
 *
 * @author Alberto Gil de la Fuente
 */
@Entity(name = "Chains")
@Table(name = "chains")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "chains.findAll", query = "SELECT chain FROM Chains chain")
    ,
    @NamedQuery(name = "chains.findByChainId", query = "SELECT chain FROM Chains chain WHERE chain.chainId = :chainId")})
public class Chains implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of Chains
     */
    public Chains() {

    }

    @ManyToMany(mappedBy = "chainsCollection")
    private Collection<NewCompounds> newCompoundsCollection;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "chain_id")
    private int chainId;

    @Size(max = 10)
    @Column(name = "num_carbons")
    private int carbons;

    @Size(max = 10)
    @Column(name = "double_bonds")
    private int doubleBonds;

    @Size(max = 10)
    @Column(name = "oxidation")
    private String oxidation;

    @Column(name = "mass")
    private double mass;

    @Size(max = 100)
    @Column(name = "formula")
    private String formula;

    public int getChainId() {
        return this.chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
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

    public String getOxidation() {
        return oxidation;
    }

    public void setOxidation(String oxidation) {
        this.oxidation = oxidation;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     *
     * @return the collection of Compounds related to a Pathway
     */
    @XmlTransient
    public Collection<NewCompounds> getNewCompoundsCollection() {
        return this.newCompoundsCollection;
    }

    /**
     * Set the relations between compounds and pathways
     *
     * @param newCompoundsCollection
     */
    public void setKeggCompoundsCollection(Collection<NewCompounds> newCompoundsCollection) {
        this.newCompoundsCollection = newCompoundsCollection;
    }
}
