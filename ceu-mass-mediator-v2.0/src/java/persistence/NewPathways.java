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
import static utilities.Constants.WEB_PATHWAY_KEGG;

/**
 * JPA definition for pathways
 *
 * @author: Alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@Entity(name = "NewPathways")
@Table(name = "pathways")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NewPathways.findAll", query = "SELECT np FROM NewPathways np")
    ,
    @NamedQuery(name = "NewPathways.findByPathwayId", query = "SELECT np FROM NewPathways np WHERE np.pathwayId = :pathwayId")})
public class NewPathways implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "pathway_id")
    private int pathwayId;
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "pathway_map")
    private String pathwayMap;
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "pathway_name")
    private String pathwayName;
    // This relation ManyToMany it is not being used in this class..

    @ManyToMany(mappedBy = "newPathwaysCollection")
    private Collection<NewCompounds> newCompoundsCollection;

    /**
     * constructor
     */
    public NewPathways() {
    }

    /**
     * constructor
     *
     * @param pathwayId
     */
    public NewPathways(int pathwayId) {
        this.pathwayId = pathwayId;
    }

    /**
     * Constructor
     *
     * @param pathwayId
     * @param pathwayMap
     */
    public NewPathways(int pathwayId, String pathwayMap) {
        this.pathwayId = pathwayId;
        this.pathwayMap = pathwayMap;
    }

    /**
     * Constructor
     *
     * @param pathwayId
     * @param pathwayMap
     * @param pathwayName
     */
    public NewPathways(int pathwayId, String pathwayMap, String pathwayName) {
        this.pathwayId = pathwayId;
        this.pathwayMap = pathwayMap;
        this.pathwayName = pathwayName;
    }

    /**
     * Obtains the webpage for the URL in Excel File
     *
     * @return
     */
    public String obtainPathwayWebPage() {
        if (this.pathwayId > 0 && !this.pathwayMap.equals("")) {
            return WEB_PATHWAY_KEGG + getPathwayMap();
        } else {
            return "";
        }
    }

    /**
     * @return the PathwayId of the compound
     */
    public int getPathwayId() {
        return pathwayId;
    }

    /**
     * Sets the pathway Id
     *
     * @param pathwayId
     */
    public void setPathwayId(int pathwayId) {
        this.pathwayId = pathwayId;
    }

    /**
     *
     * @return the pathway Name
     */
    public String getPathwayMap() {
        return this.pathwayMap;
    }

    /**
     * Sets the pathway name of a compound
     *
     * @param pathwayMap
     */
    public void setPathwayMap(String pathwayMap) {
        this.pathwayMap = pathwayMap;
    }

    /**
     *
     * @return the pathway Name
     */
    public String getPathwayName() {
        return this.pathwayName;
    }

    /**
     * Sets the pathway name of a compound
     *
     * @param pathwayName
     */
    public void setPathwayName(String pathwayName) {
        this.pathwayName = pathwayName;
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
