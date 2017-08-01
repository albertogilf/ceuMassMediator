package persistence;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import static utilities.Constantes.*;

/**
 * JPA definition for Compounds
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity
@Table(name = "compounds")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewCompounds.findAll", query = "SELECT nc FROM NewCompounds nc"),
    @NamedQuery(name = "NewCompounds.findByCompoundId", query = "SELECT nc FROM NewCompounds nc WHERE nc.compoundId = :compoundId"),
    @NamedQuery(name = "NewCompounds.findByCasId", query = "SELECT nc FROM NewCompounds nc WHERE nc.casId = :casId"),
    @NamedQuery(name = "NewCompounds.findByExactMass", query = "SELECT nc FROM NewCompounds nc WHERE nc.mass = :mass"),
    @NamedQuery(name = "NewCompounds.findByFormula", query = "SELECT nc FROM NewCompounds nc WHERE nc.formula = :formula")})
public class NewCompounds implements Serializable {
    @JoinTable(name = "compounds_pathways", joinColumns = {
        @JoinColumn(name = "compound_id", referencedColumnName = "compound_id")}, inverseJoinColumns = {
        @JoinColumn(name = "pathway_id", referencedColumnName = "pathway_id")})
    @ManyToMany
    private Collection<NewPathways> newPathwaysCollection;

    // Relations to all database compounds
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsKegg ncKegg;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsMetlin ncAgilent;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsLM ncLM;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsHMDB ncHMDB;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsPC ncPC;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsIdentifiers ncIdentifier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewLipidsClassification lipidClass;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    @Size(max = 20)
    @Column(name = "cas_id")
    private String casId;

    @Lob
    @Size(max = 65535)
    @Column(name = "compound_name")
    private String compoundName;

    @Column(name = "mass")
    private double mass;

    @Size(max = 100)
    @Column(name = "formula")
    private String formula;

    @Size(max = 20)
    //@Column(name = "type")
    @Column(name = "formula_type")
    private String formulaType;

    @Column(name = "compound_type")
    private int compoundType;

    public NewCompounds() {
    }

    public NewCompounds(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getCasId() {
        return casId;
    }

    public void setCasId(String casId) {
        this.casId = casId;
    }

    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }

    public int getCompoundType() {
        return compoundType;
    }

    public void setCompoundType(int compoundType) {
        this.compoundType = compoundType;
    }

    public String getCompoundName() {
        return compoundName;
    }

    public void setCompoundName(String compoundName) {
        this.compoundName = compoundName;
    }

    public double getMass() {
        return this.mass;
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

    @XmlTransient
    public Collection<NewPathways> getNewPathwaysCollection() {
        return this.newPathwaysCollection;
    }

    public void setNewPathwaysCollection(Collection<NewPathways> newPathwaysCollection) {
        this.newPathwaysCollection = newPathwaysCollection;
    }

    public NewCompoundsKegg getNcKegg() {
        return ncKegg;
    }

    public void setNcKegg(NewCompoundsKegg ncKegg) {
        this.ncKegg = ncKegg;
    }

    public String getKeggId() {
        if (this.ncKegg == null) {
            return "";

        } else {
            return this.ncKegg.getKeggId();
        }
    }

    public void setKeggId(String keggId) {
        this.ncKegg.setKeggId(keggId);
    }

    public String obtainKeggWebPage() {
        return WEB_COMPUESTO_KEGG + getKeggId();
        //return "";
    }

    public NewCompoundsMetlin getNcAgilent() {
        return ncAgilent;
    }

    public void setNcAgilent(NewCompoundsMetlin ncAgilent) {
        this.ncAgilent = ncAgilent;
    }

    public String getMetlinId() {
        if (this.ncAgilent == null) {
            return "";

        } else {
            return this.ncAgilent.getAgilentId();
        }
    }

    public void setMetlinId(String metlinId) {
        this.ncAgilent.setAgilentId(metlinId);
    }

    public String obtainMetlinWebPage() {
        return WEB_COMPUESTO_METLIN + getMetlinId();
        //return "";
    }

    public NewCompoundsLM getNcLM() {
        return ncLM;
    }

    public void setNcLM(NewCompoundsLM ncLM) {
        this.ncLM = ncLM;
    }

    public String getLmId() {
        if (this.ncLM == null) {
            return "";

        } else {
            return this.ncLM.getLmId();
        }
    }

    public void setLmId(String lmId) {
        this.ncLM.setLmId(lmId);
    }

    public String obtainLMWebPage() {
        return WEB_COMPUESTO_LM + getLmId();
        //return "";
    }

    public NewCompoundsHMDB getNcHMDB() {
        return ncHMDB;
    }

    public void setNcHMDB(NewCompoundsHMDB ncHMDB) {
        this.ncHMDB = ncHMDB;
    }

    public String getHmdbId() {
        if (this.ncHMDB == null) {
            return "";

        } else {
            return this.ncHMDB.getHmdbId();
        }
    }

    public void setHmdbId(String hmdbId) {
        this.ncHMDB.setHmdbId(hmdbId);
    }

    public String obtainHMDBWebPage() {
        return WEB_COMPUESTO_HMDB + getHmdbId();
        //return "";
    }

    public NewCompoundsPC getNcPC() {
        return ncPC;
    }

    public void setNcPC(NewCompoundsPC ncPC) {
        this.ncPC = ncPC;
    }

    public String getPcId() {
        if (this.ncPC == null) {
            return "";

        } else {
            return Integer.toString(this.ncPC.getPcId());
        }
    }

    public void setPcId(String pcId) {
        this.ncPC.setPcId(Integer.parseInt(pcId));
    }

    public NewCompoundsIdentifiers getNcIdentifier() {
        return ncIdentifier;
    }

    public void setNcIdentifier(NewCompoundsIdentifiers ncIdentifier) {
        this.ncIdentifier = ncIdentifier;
    }

    public String getInChiKey() {

        if (this.ncIdentifier == null) {
            return "";

        } else {
            return this.ncIdentifier.getInchiKey();

        }
    }

    public void setInChiKey(String inChiKey) {
        //this.ncIdentifier.setInchiKey(inChiKey);
    }

    public String obtainPCWebPage() {
        return WEB_COMPUESTO_PUBCHEMICHAL + getPcId();
        //return "";
    }

// Attributes from Lipids Clasification
    public NewLipidsClassification getLipidClass() {
        return lipidClass;
    }

    public void setLipidClass(NewLipidsClassification lipidClass) {
        this.lipidClass = lipidClass;
    }

    public String getCategory() {
        if (this.lipidClass == null) {
            return "";
        } else {
            return lipidClass.getCategory();
        }
    }

    public void setCategory(String category) {
        lipidClass.setCategory(category);
    }

    public String getMainClass() {
        if (this.lipidClass == null) {
            return "";
        } else {
            return lipidClass.getMainClass();
        }
    }

    public void setMainClass(String mainClass) {
        this.lipidClass.setMainClass(mainClass);
    }

    public String getSubClass() {
        if (this.lipidClass == null) {
            return "";
        } else {
            return lipidClass.getSubClass();
        }
    }

    public void setSubClass(String subClass) {
        this.lipidClass.setSubClass(subClass);
    }

    public int getCarbons() {
        if (this.lipidClass == null) {
            return -1;
        } else {
            return lipidClass.getCarbons();
        }
    }

    public void setCarbons(int carbons) {
        this.lipidClass.setCarbons(carbons);
    }

    public int getDoubleBonds() {
        if (this.lipidClass == null) {
            return -1;
        } else {
            return lipidClass.getDoubleBonds();
        }
    }

    public void setDoubleBonds(int doubleBonds) {
        this.lipidClass.setDoubleBonds(doubleBonds);
    }

}
