/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathway;

import compound.CMMCompound;
import java.util.List;
import static utilities.Constants.WEB_PATHWAY_KEGG;

/**
 * Pathway. A metabolic pathway. Several pathways can have several compounds
 * (many to many relationship) The pathways exists independently from the
 * compounds.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Pathway {

    private final int pathway_id;
    private final String pathwayMap;
    private final String pathwayName;
    private final List<CMMCompound> compounds;

    public Pathway(int pathway_id, String pathwayMap, String pathwayName, List<CMMCompound> compounds) {
        this.pathway_id = pathway_id;
        this.pathwayMap = pathwayMap;
        this.pathwayName = pathwayName;
        this.compounds = compounds;
    }

    public int getPathway_id() {
        return pathway_id;
    }

    public String getPathwayMap() {
        return pathwayMap;
    }

    public String getPathwayName() {
        return pathwayName;
    }

    public List<CMMCompound> getCompounds() {
        return compounds;
    }
    
    /**
     * Obtains the webpage for the URL 
     * @return
     */
    public String getPathwayWebPage()
    {
        return WEB_PATHWAY_KEGG+getPathwayMap();
    }

}
