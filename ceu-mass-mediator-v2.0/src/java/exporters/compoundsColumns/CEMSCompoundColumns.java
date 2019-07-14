/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exporters.compoundsColumns;

/**
 *
 * @author Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente
 */
public enum CEMSCompoundColumns {
    EXPERIMENTAL_MASS(0),
    RMT(1),
    COMPOUND_ID(2),
    ADDUCT(3),
    NAME(4),
    FORMULA(5),
    MOLECULAR_WEIGHT(6),
    INCREMENT_PPM(7),
    STANDARD_RMT(8),
    ERROR_RMT(9),
    CAS(10),
    KEGG(11),
    HMDB(12),
    LIPIDMAPS(13),
    METLIN(14),
    PUBCHEM(15),
    INCHIKEY(16),
    SMILES(17),
    PATHWAYS(18);
    //FRAGMENTS(18), 
    //PATHWAYS(19);

    private int nColumn;

    CEMSCompoundColumns(int nColumn) {
        this.nColumn = nColumn;
    }

    /**
     * @return the nColumn
     */
    public int getnColumn() {
        return nColumn;
    }

    /**
     * @param nColumn the nColumn to set
     */
    private void setnColumn(int nColumn) {
        this.nColumn = nColumn;
    }

}
