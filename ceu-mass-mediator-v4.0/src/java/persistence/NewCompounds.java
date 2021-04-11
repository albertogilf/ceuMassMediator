package persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import static utilities.Constants.*;

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
    @NamedQuery(name = "NewCompounds.findAll", query = "SELECT nc FROM NewCompounds nc")
    ,
    @NamedQuery(name = "NewCompounds.findByCompoundId", query = "SELECT nc FROM NewCompounds nc WHERE nc.compoundId = :compoundId")
    ,
    @NamedQuery(name = "NewCompounds.findByCasId", query = "SELECT nc FROM NewCompounds nc WHERE nc.casId = :casId")
    ,
    @NamedQuery(name = "NewCompounds.findByExactMass", query = "SELECT nc FROM NewCompounds nc WHERE nc.mass = :mass")
    ,
    @NamedQuery(name = "NewCompounds.findByFormula", query = "SELECT nc FROM NewCompounds nc WHERE nc.formula = :formula")})
public class NewCompounds implements Serializable {

    private static final long serialVersionUID = 1L;

    @JoinTable(name = "compounds_pathways", joinColumns = {
        @JoinColumn(name = "compound_id", referencedColumnName = "compound_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "pathway_id", referencedColumnName = "pathway_id")})
    @ManyToMany
    private Collection<NewPathways> newPathwaysCollection;

    @JoinTable(name = "compound_chain", joinColumns = {
        @JoinColumn(name = "compound_id", referencedColumnName = "compound_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "chain_id", referencedColumnName = "chain_id")})
    @ManyToMany
    private Collection<Chains> chainsCollection;

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
    private NewCompoundsInHouse ncInHouse;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsAspergillus ncAspergillus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsFahfa ncFahfa;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsPC ncPC;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsKnapsack ncKnapsack;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsNPAtlas ncNPAtlas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsIdentifiers ncIdentifier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewLMClassification LMclassification;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewLipidsClassification lipidClassification;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 11)
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

    /*
    @Size(max = 20)
    //@Column(name = "type")
    @Column(name = "formula_type")
    private String formulaType;
     */
    @Column(name = "formula_type_int")
    private int formulaTypeInt;

    @Column(name = "compound_type")
    private int compoundType;

    @Column(name = "charge_type")
    private int chargeType;

    @Column(name = "charge_number")
    private int chargeNumber;

    @Column(name = "compound_status")
    private int compoundStatus;

    @Column(name = "logP")
    private double logP;

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
        if (casId == null) {
            return "";
        }
        return casId;
    }

    public void setCasId(String casId) {
        this.casId = casId;
    }

    /*
    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }
     */
    public int getFormulaTypeInt() {
        return formulaTypeInt;
    }

    public void setFormulaTypeInt(int formulaTypeInt) {
        this.formulaTypeInt = formulaTypeInt;
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
        if (formula == null) {
            return "";
        }
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getChargeType() {
        return chargeType;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    public int getChargeNumber() {
        return chargeNumber;
    }

    public void setChargeNumber(int chargeNumber) {
        this.chargeNumber = chargeNumber;
    }

    public int getCompoundStatus() {
        return compoundStatus;
    }

    public void setCompoundStatus(int compoundStatus) {
        this.compoundStatus = compoundStatus;
    }

    public double getLogP() {
        return logP;
    }

    public void setLogP(double logP) {
        this.logP = logP;
    }

    @XmlTransient
    public Collection<NewPathways> getNewPathwaysCollection() {
        return this.newPathwaysCollection;
    }

    public void setNewPathwaysCollection(Collection<NewPathways> newPathwaysCollection) {
        this.newPathwaysCollection = newPathwaysCollection;
    }

    @XmlTransient
    public Collection<Chains> getChainsCollection() {
        return this.chainsCollection;
    }

    public void setChainsCollection(Collection<Chains> chainsCollection) {
        this.chainsCollection = chainsCollection;
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
        if (getKeggId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_KEGG + getKeggId();
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
        if (getMetlinId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_METLIN + getMetlinId();
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
        if (getLmId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_LM + getLmId();
        //return "";
    }

    public NewCompoundsInHouse getNcInHouse() {
        return ncInHouse;
    }

    public void setNcLM(NewCompoundsInHouse ncInHouse) {
        this.ncInHouse = ncInHouse;
    }

    public String getInHouseId() {
        if (this.ncInHouse == null) {
            return "";

        } else {
            return this.ncInHouse.getInHouseId();
        }
    }

    public void setInHouseId(String ncInHouseId) {
        this.ncInHouse.setInHouseId(ncInHouseId);
    }

    public String obtainInHouseWebPage() {
        return getInHouseId();
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
        if (getHmdbId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_HMDB + getHmdbId();
        //return "";
    }

    public NewCompoundsAspergillus getNcAspergillus() {
        return ncAspergillus;
    }

    public void setNcAspergillus(NewCompoundsAspergillus ncAspergillus) {
        this.ncAspergillus = ncAspergillus;
    }

    public NewCompoundsFahfa getNcFahfa() {
        return ncFahfa;
    }

    public void setNcFahfa(NewCompoundsFahfa ncFahfa) {
        this.ncFahfa = ncFahfa;
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

    public String obtainPCWebPage() {
        if (getPcId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_PUBCHEMICHAL + getPcId();
        //return "";
    }

    public NewCompoundsKnapsack getNcKnapsack() {
        return this.ncKnapsack;
    }

    public void setNcKnapsack(NewCompoundsKnapsack ncKnapsack) {
        this.ncKnapsack = ncKnapsack;
    }

    public String getKnapsackId() {
        if (this.ncKnapsack == null) {
            return "";

        } else {
            return this.ncKnapsack.getKnapsackId();
        }
    }

    public void setKnapsackId(String knapsackId) {
        this.ncKnapsack.setKnapsackId(knapsackId);
    }

    public String obtainKnapsackWebPage() {
        if (getPcId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_KNAPSACK + this.getKnapsackId();
    }

    public NewCompoundsNPAtlas getNcNPAtlas() {
        return this.ncNPAtlas;
    }

    public void setNcNPAtlas(NewCompoundsNPAtlas ncNPAtlas) {
        this.ncNPAtlas = ncNPAtlas;
    }

    public String getNPAtlasId() {
        if (this.ncNPAtlas == null) {
            return "";

        } else {
            return Integer.toString(this.ncNPAtlas.getNpatlasId());
        }
    }

    public void setNPAtlasId(String npAtlasId) {
        this.ncNPAtlas.setNpatlasId(Integer.parseInt(npAtlasId));
    }

    public String obtainNPAtlasWebPage() {
        if (getPcId().equals("")) {
            return "";
        }
        return WEB_COMPOUND_NPATLAS + this.getNPAtlasId();
    }

    public String obtainCMMWebPage() {
        if (this.compoundId == 0) {
            return "";
        }
        try {
            String compoundIdString = Integer.toString(this.compoundId);
            return WEB_COMPOUND_CMM + compoundIdString;
        } catch (NumberFormatException ex) {
            return "";
        }
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

    public String getSmiles() {
        if (this.ncIdentifier == null) {
            return "";
        } else {
            return this.ncIdentifier.getSmiles();
        }
    }

    public void setSmiles(String smiles) {
        //this.ncIdentifier.setSmiles(smiles);
    }

// Attributes from Lipids Clasification
    public NewLMClassification getLMclassification() {
        return LMclassification;
    }

    public void setLMclassification(NewLMClassification LMclassification) {
        this.LMclassification = LMclassification;
    }

    public String getCategory() {
        if (this.LMclassification == null) {
            return "";
        } else {
            return LMclassification.getCategory();
        }
    }

    public void setCategory(String category) {
        LMclassification.setCategory(category);
    }

    public String getMainClass() {
        if (this.LMclassification == null) {
            return "";
        } else {
            return LMclassification.getMainClass();
        }
    }

    public void setMainClass(String mainClass) {
        this.LMclassification.setMainClass(mainClass);
    }

    public String getSubClass() {
        if (this.LMclassification == null) {
            return "";
        } else {
            return LMclassification.getSubClass();
        }
    }

    public void setSubClass(String subClass) {
        this.LMclassification.setSubClass(subClass);
    }

    public String getClassLevel4() {
        if (this.LMclassification == null) {
            return "";
        } else {
            return LMclassification.getClassLevel4();
        }
    }

    public void setClassLevel4(String classLevel4) {
        this.LMclassification.setClassLevel4(classLevel4);
    }

    public NewLipidsClassification getLipidClassification() {
        return lipidClassification;
    }

    public void setLipidClassification(NewLipidsClassification lipidClassification) {
        this.lipidClassification = lipidClassification;
    }

    public int getCarbons() {
        if (this.lipidClassification == null) {
            return -1;
        } else {
            return lipidClassification.getCarbons();
        }
    }

    public void setCarbons(int carbons) {
        this.lipidClassification.setCarbons(carbons);
    }

    public int getDoubleBonds() {
        if (this.lipidClassification == null) {
            return -1;
        } else {
            return lipidClassification.getDoubleBonds();
        }
    }

    public void setDoubleBonds(int doubleBonds) {
        this.lipidClassification.setDoubleBonds(doubleBonds);
    }

    public int getNumChains() {
        if (this.lipidClassification == null) {
            return -1;
        } else {
            return lipidClassification.getNumChains();
        }
    }

    public void setNumChains(int numChains) {
        this.lipidClassification.setNumChains(numChains);
    }

    public String getLipidType() {
        if (this.lipidClassification == null) {
            return "";
        } else {
            return lipidClassification.getLipidType();
        }
    }

    public void setLipidType(String lipidType) {
        this.lipidClassification.setLipidType(lipidType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof NewCompounds)) {
            return false;
        }
        return compoundId != 0 && compoundId == ((NewCompounds) o).getCompoundId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.compoundId);
    }

    @Override
    public String toString() {
        return "NewCompounds{" + "newPathwaysCollection=" + newPathwaysCollection
                + ", chainsCollection=" + chainsCollection + ", ncKegg=" + ncKegg
                + ", ncAgilent=" + ncAgilent + ", ncLM=" + ncLM + ", ncHMDB=" + ncHMDB
                + ", ncInHouse=" + ncInHouse + ", ncPC=" + ncPC + ", ncIdentifier="
                + ncIdentifier + ", LMclassification=" + LMclassification
                + ", lipidClassification=" + lipidClassification + ", compoundId="
                + compoundId + ", casId=" + casId + ", compoundName=" + compoundName
                + ", mass=" + mass + ", formula=" + formula + ", formulaTypeInt="
                + formulaTypeInt + ", compoundType=" + compoundType + ", chargeType="
                + chargeType + ", chargeNumber=" + chargeNumber + ", compoundStatus="
                + compoundStatus + '}';
    }

}
