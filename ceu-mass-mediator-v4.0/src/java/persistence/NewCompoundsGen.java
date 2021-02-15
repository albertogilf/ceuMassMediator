package persistence;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JPA definition for pathways
 *
 * @author aesteban. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity
@Table(name = "compounds_gen")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewCompoundsGen.findAll", query = "SELECT nc FROM NewCompoundsGen nc")
    ,
    @NamedQuery(name = "NewCompoundsGen.findByCompoundId", query = "SELECT nc FROM NewCompoundsGen nc WHERE nc.compoundId = :compoundId")
    ,
    @NamedQuery(name = "NewCompoundsGen.findByMineId", query = "SELECT nc FROM NewCompoundsGen nc WHERE nc.mineId = :mineId")
    ,
    @NamedQuery(name = "NewCompoundsGen.findByExactMass", query = "SELECT nc FROM NewCompoundsGen nc WHERE nc.mass = :mass")
    ,
    @NamedQuery(name = "NewCompoundsGen.findByFormula", query = "SELECT nc FROM NewCompoundsGen nc WHERE nc.formula = :formula")})
public class NewCompoundsGen implements Serializable {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "compound_id", unique = true, nullable = true, insertable = false, updatable = false)
    private NewCompoundsGenIdentifiers ncgIdentifier;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    @Size(max = 20)
    @Column(name = "MINE_id")
    private String mineId;

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
    @Column(name = "formula_type")
    private String formulaType;

    @Column(name = "formula_type_int")
    private int formulaTypeInt;

    @Column(name = "charge_type")
    private int chargeType;

    @Column(name = "charge_number")
    private int chargeNumber;

    @Column(name = "np_likeness")
    private double npLikeness;

    @Column(name = "logP")
    private double logP;

    public NewCompoundsGen() {
    }

    public NewCompoundsGen(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getMineId() {
        return mineId;
    }

    public void setMineId(String mineId) {
        this.mineId = mineId;
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

    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }

    public int getFormulaTypeInt() {
        return formulaTypeInt;
    }

    public void setFormulaTypeInt(int formulaTypeInt) {
        this.formulaTypeInt = formulaTypeInt;
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

    public double getNpLikeness() {
        return npLikeness;
    }

    public void setNpLikeness(double npLikeness) {
        this.npLikeness = npLikeness;
    }

    public double getLogP() {
        return logP;
    }

    public void setLogP(double logP) {
        this.logP = logP;
    }

    public String obtainMineWebPage() {
        return "http://minedatabase.mcs.anl.gov/#/acompound" + mineId + "/overview";
    }

    public NewCompoundsGenIdentifiers getNcgIdentifier() {
        return ncgIdentifier;
    }

    public void setNcgIdentifier(NewCompoundsGenIdentifiers ncgIdentifier) {
        this.ncgIdentifier = ncgIdentifier;
    }

    public String getInChiKey() {
        if (this.ncgIdentifier == null) {
            return "";

        } else {
            return this.ncgIdentifier.getInchiKey();
        }
    }

    public void setInChiKey(String inChiKey) {
        this.ncgIdentifier.setInchiKey(inChiKey);
    }

    public String getSmiles() {
        if (this.ncgIdentifier == null) {
            return "";

        } else {
            return this.ncgIdentifier.getSmiles();
        }
    }

    public void setSmiles(String smiles) {
        this.ncgIdentifier.setSmiles(smiles);
    }

}
