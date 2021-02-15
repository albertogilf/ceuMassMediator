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
public enum CompoundColumns {

    EXPERIMENTAL_MASS(0),
    RETENTION_TIME(1),
    COMPOUND_ID(2),
    ADDUCT(3),
    INCREMENT_PPM(4),
    MOLECULAR_WEIGHT(5),
    NAME(6),
    FORMULA(7),
    IONIZATION_SCORE(8),
    ADDUCT_RELATION_SCORE(9),
    RETENTION_TIME_SCORE(10),
    FINAL_SCORE(11),
    CAS(12),
    KEGG(13),
    HMDB(14),
    LIPIDMAPS(15),
    METLIN(16),
    PUBCHEM(17),
    INCHIKEY(18),
    SMILES(19),
    PATHWAYS(20);

    private int nColumn;

    CompoundColumns(int nColumn) {
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
