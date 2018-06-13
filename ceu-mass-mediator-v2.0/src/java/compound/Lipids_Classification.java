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
    private final int numberChains;
    private final int numberDoubleBounds;
    private final int numberCarbons;
    private final List<Chain> chains;
    private final List<Compound> compounds;

    /**
     *
     * @param lipidType
     * @param numberChains
     * @param numberDoubleBounds
     * @param numberCarbons
     */
    public Lipids_Classification(String lipidType, int numberChains, int numberDoubleBounds,
            int numberCarbons) {
        this(lipidType, numberChains, numberDoubleBounds,
                numberCarbons, new LinkedList<Chain>(), new LinkedList<Compound>());
    }
    
    /**
     *
     * @param lipidType
     * @param numberChains
     * @param numberDoubleBounds
     * @param numberCarbons
     * @param chains
     */
    public Lipids_Classification(String lipidType, int numberChains, int numberDoubleBounds,
            int numberCarbons, List<Chain> chains) {
        this(lipidType, numberChains, numberDoubleBounds,
                numberCarbons, chains, new LinkedList<Compound>());
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
    public Lipids_Classification(String lipidType, int numberChains, int numberDoubleBounds,
            int numberCarbons, List<Chain> chains, List<Compound> compounds) {
        this.lipidType = lipidType;
        this.numberChains = numberChains;
        this.numberDoubleBounds = numberDoubleBounds;
        this.numberCarbons = numberCarbons;
        this.chains = chains;
        this.compounds = compounds;
    }

    public String getLipidType() {
        return lipidType;
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

    public List<Compound> getCompounds() {
        return compounds;
    }

}
