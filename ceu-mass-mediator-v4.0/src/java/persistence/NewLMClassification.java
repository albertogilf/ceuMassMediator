
package persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JPA definition for Compounds LM Classification
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity
@Table(name = "compounds_lm_classification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewLMClassification.findAll", query = "SELECT nc FROM NewLMClassification nc"),
    @NamedQuery(name = "NewLMClassification.findByCompoundId", query = "SELECT nc FROM NewLMClassification nc WHERE nc.compoundId = :compoundId")})
public class NewLMClassification implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    @Size(max = 10)
    @Column(name = "category")
    private String category;
        
    @Size(max = 10)
    @Column(name = "main_class")
    private String mainClass;
            
    @Size(max = 10)
    @Column(name = "sub_class")
    private String subClass;
    
    @Size(max = 10)
    @Column(name = "class_level4")
    private String classLevel4;
    
    
    public NewLMClassification() {
    }

    public NewLMClassification(int compoundId) {
        this.compoundId = compoundId;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getSubClass() {
        return subClass;
    }

    public void setSubClass(String subClass) {
        this.subClass = subClass;
    }

    public String getClassLevel4() {
        return classLevel4;
    }

    public void setClassLevel4(String classLevel4) {
        this.classLevel4 = classLevel4;
    }
    
}
