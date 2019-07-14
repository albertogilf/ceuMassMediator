/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

import java.util.LinkedList;
import java.util.List;

/**
 * Lipids_Classification. Lipids Classification. Several compounds have one
 * Lipids_Classification (one to many relationship) Many lipids_classifications
 * have many chains.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Lipids_Classification {

    private final String lipidType;
    private final Integer numberChains;
    private final Integer numberDoubleBounds;
    private final Integer numberCarbons;
    private final List<Chain> chains;
    private final List<CMMCompound> compounds;

    /**
     *
     * @param lipidType
     * @param numberChains
     * @param numberDoubleBounds
     * @param numberCarbons
     */
    public Lipids_Classification(String lipidType, Integer numberChains, 
            Integer numberCarbons, Integer numberDoubleBounds) {
        this(lipidType, numberChains, numberCarbons,
                numberDoubleBounds, new LinkedList<Chain>(), new LinkedList<CMMCompound>());
    }

    /**
     *
     * @param lipidType
     * @param numberChains
     * @param numberDoubleBounds
     * @param numberCarbons
     * @param chains
     */
    public Lipids_Classification(String lipidType, Integer numberChains, Integer numberCarbons,
            Integer numberDoubleBounds, List<Chain> chains) {
        this(lipidType, numberChains, numberCarbons,
                numberDoubleBounds, chains, new LinkedList<CMMCompound>());
    }

    /**
     *
     * @param lipidType
     * @param numberChains
     * @param numberDoubleBounds
     * @param numberCarbons
     * @param chains
     * @param compounds
     */
    public Lipids_Classification(String lipidType, Integer numberChains, Integer numberCarbons,
            Integer numberDoubleBounds, List<Chain> chains, List<CMMCompound> compounds) {
        
        this.lipidType = lipidType == null ? "" : lipidType;
        this.numberChains = numberChains == null ? -1 : numberChains;
        this.numberCarbons = numberCarbons == null ? -1 : numberCarbons;
        this.numberDoubleBounds = numberDoubleBounds == null ? -1 : numberDoubleBounds;
        this.chains = chains == null ? new LinkedList() : chains;
        this.compounds = compounds == null ? new LinkedList() : compounds;
    }

    public String getLipidType() {
        return this.lipidType;
    }

    public int getNumberChains() {
        return numberChains;
    }

    public int getNumberDoubleBounds() {
        return numberDoubleBounds;
    }

    public int getNumberCarbons() {
        return numberCarbons;
    }

    public List<Chain> getChains() {
        return chains;
    }

    public List<CMMCompound> getCompounds() {
        return compounds;
    }

}
