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
    EXP_EFFMOB(1),
    EXP_RMT(2),
    EXP_MT(3),
    COMPOUND_ID(4),
    ADDUCT(5),
    NAME(6),
    FORMULA(7),
    MOLECULAR_WEIGHT(8),
    ERROR_PPM(9),
    STANDARD_EFFMOB(10),
    ERROR_EFFMOB(11),
    STANDARD_RMT(12),
    ERROR_RMT(13),
    STANDARD_MT(14),
    ERROR_MT(15),
    EXPERIMENTAL_FRAGMENTS(16),
    FOUND_FRAGMENTS(17),
    NOT_FOUND_THEORETICAL_FRAGMENTS(18),
    NOT_FOUND_EXPERIMENTAL_FRAGMENTS(19),
    CAS(20),
    KEGG(21),
    HMDB(22),
    LIPIDMAPS(23),
    METLIN(24),
    PUBCHEM(25),
    CHEBI(26),
    INCHIKEY(27),
    SMILES(28),
    PATHWAYS(29);
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
