/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporters.compoundsColumns;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public enum PathwayColumns {
    
    
    PATHWAYS(0),
    EXPERIMENTAL_MASS(1),
    RETENTION_TIME(2),
    COMPOUND_ID(3),
    ADDUCT(4),
    INCREMENT_PPM(5),
    MOLECULAR_WEIGHT(6),
    NAME(7),
    FORMULA(8),
    CAS(9),
    KEGG(10),
    HMDB(11),
    LIPIDMAPS(12),
    METLIN(13),
    PUBCHEM(14),
    INCHIKEY(15);
    
    private int nColumn;
    
    PathwayColumns(int nColumn)
    {
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
