/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

/**
 * Chain. Chains from a lipid. A lipid_classification can contain several
 * chains.
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Chain {

    private final int chain_id;
    private final int numberCarbons;
    private final int numberDoubleBonds;
    private final String oxidation;
    private final double mass;
    private final String formula;

    public Chain(int chain_id, int numberCarbons, int numberDoubleBonds, String oxidation, double mass, String formula) {
        this.chain_id = chain_id;
        this.numberCarbons = numberCarbons;
        this.numberDoubleBonds = numberDoubleBonds;
        this.oxidation = oxidation;
        this.mass = mass;
        this.formula = formula;
    }

    public int getChain_id() {
        return chain_id;
    }

    public int getNumberCarbons() {
        return numberCarbons;
    }

    public int getNumberDoubleBonds() {
        return numberDoubleBonds;
    }

    public String getOxidation() {
        return oxidation;
    }

    public double getMass() {
        return mass;
    }

    public String getFormula() {
        return formula;
    }

}
