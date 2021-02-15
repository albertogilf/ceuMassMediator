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
public enum CompoundColumnsBrowseSearch {

    COMPOUND_ID(0),
    MOLECULAR_WEIGHT(1),
    NAME(2),
    FORMULA(3),
    CAS(4),
    KEGG(5),
    HMDB(6),
    LIPIDMAPS(7),
    METLIN(8),
    PUBCHEM(9),
    INCHIKEY(10),
    SMILES(11),
    PATHWAYS(12);

    private int nColumn;

    CompoundColumnsBrowseSearch(int nColumn) {
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
