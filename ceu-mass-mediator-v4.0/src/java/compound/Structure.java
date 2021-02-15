/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

/**
 * Structure. Consists on a class representing the structure of a compound. One
 * compound has one structure (one to one relationship)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Structure {

    private final String inchi;
    private final String inchikey;
    private final String smiles;

    public Structure(String inchi, String inchikey, String smiles) {
        this.inchi = inchi;
        this.inchikey = inchikey;
        this.smiles = smiles;
    }

    public String getInchi() {
        return inchi;
    }

    public String getInchikey() {
        return inchikey;
    }

    public String getSmiles() {
        return smiles;
    }

}
