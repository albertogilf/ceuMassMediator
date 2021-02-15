
package importers;

import exporters.compoundsColumns.PathwayColumns;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.write.Label;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import static utilities.Constants.*;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class CompoundForPathway implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public String name;
    public List<Pathway> pathways;
    public String expmass;
    public String casId;
    public String compound_id;
    public String formula;
    public String adduct;
    public String molecularWeight;
    public String ppmError;
    public String retentionTime;
    public String keggCompound;
    public String HMDBCompound;
    public String LMCompound;
    public String metlinCompound;
    public String PCCompound;
    public String ChebiCompound;
    public String inchikey;
    /**
     * Constructor
     *
     * @param info
     * @param columnsTable
     */
    public CompoundForPathway(List<String> info, Map<String, Integer> columnsTable) {
        pathways = new ArrayList<>();
        
        try {
            expmass = info.get(columnsTable.get(EXPERIMENTAL_MASS_HEADER));
        } catch (Exception e) {
            expmass = "0";
        }
        try {
            casId = info.get(columnsTable.get(CAS_HEADER));
        } catch (Exception e) {
            casId = "";
        }
        try {
            compound_id = info.get(columnsTable.get(COMPOUND_ID_HEADER));
            // System.out.println("Identifier: " + identifier);
        } catch (Exception e) {
            compound_id = "";
        }
        try {
            molecularWeight = info.get(columnsTable.get(MOL_WEIGHT_HEADER));
            // System.out.println("MOL WEIGHT: " + molecularWeight);
        } catch (Exception e) {
            molecularWeight = "0";
        }

        // TODO Calculate Error here taking in account the adducts
        try {
            ppmError = info.get(columnsTable.get(PPM_INCREMENT_HEADER));
            // System.out.println("PPM ERROR: " + ppmError);
        } catch (Exception e) {
            ppmError = "NA";
        }

        try {

            name = info.get(columnsTable.get(NAME_HEADER));
        } catch (Exception e) {
            name = "";
        }
        try {
            formula = info.get(columnsTable.get(FORMULA_HEADER));
        } catch (Exception e) {
            formula = "";
        }
        try {
            adduct = info.get(columnsTable.get(ADDUCT_HEADER));
        } catch (Exception e) {
            adduct = "0";
        }
        try {
            retentionTime = info.get(columnsTable.get(RT_HEADER));
        } catch (Exception e) {
            retentionTime = "0";
        }
        try {
            keggCompound = info.get(columnsTable.get(KEGG_HEADER));
        } catch (Exception e) {
            keggCompound = "";
        }
        try {
            HMDBCompound = info.get(columnsTable.get(HMDB_HEADER));
        } catch (Exception e) {
            HMDBCompound = "";
        }
        try {
            LMCompound = info.get(columnsTable.get(LIPIDMAPS_HEADER));
        } catch (Exception e) {
            LMCompound = "";
        }
        try {
            metlinCompound = info.get(columnsTable.get(METLIN_HEADER));
        } catch (Exception e) {
            metlinCompound = "";
        }
        try {
            PCCompound = info.get(columnsTable.get(PUBHCEMICAL_HEADER));
        } catch (Exception e) {
            PCCompound = "";
        }
        
        try {
            ChebiCompound = info.get(columnsTable.get(CHEBI_HEADER));
        } catch (Exception e) {
            ChebiCompound = "";
        }
        
        try {
            inchikey = info.get(columnsTable.get(INCHIKEY_HEADER));
        } catch (Exception e) {
            inchikey = "";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pathway> getPathways() {
        return pathways;
    }

    public void setPathways(List<Pathway> Pathways) {
        this.pathways = Pathways;
    }

    public String getExpmass() {
        return expmass;
    }

    public void setExpmass(String expmass) {
        this.expmass = expmass;
    }

    public String getCasId() {
        return casId;
    }

    public void setCasId(String casId) {
        this.casId = casId;
    }

    public String getIdentifier() {
        return compound_id;
    }

    public void setIdentifier(String identifier) {
        this.compound_id = identifier;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public String getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(String molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public String getPpmError() {
        return ppmError;
    }

    public void setPpmError(String ppmError) {
        this.ppmError = ppmError;
    }

    public String getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(String retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getKeggCompound() {
        return keggCompound;
    }

    public void setKeggCompound(String keggCompound) {
        this.keggCompound = keggCompound;
    }

    public String keggCompoundWebPage() {
        return WEB_COMPOUND_KEGG + this.keggCompound;
    }

    public String getHMDBCompound() {
        return HMDBCompound;
    }

    public void setHMDBCompound(String HMDBCompound) {
        this.HMDBCompound = HMDBCompound;
    }

    public String HMDBCompoundWebPage() {
        return WEB_COMPOUND_HMDB + this.HMDBCompound;
    }

    public String getLMCompound() {
        return LMCompound;
    }

    public void setLMCompound(String LMCompound) {
        this.LMCompound = LMCompound;
    }

    public String LMCompoundWebPage() {
        return WEB_COMPOUND_LM + this.LMCompound;
    }

    public String getMetlinCompound() {
        return metlinCompound;
    }

    public void setMetlinCompound(String metlinCompound) {
        this.metlinCompound = metlinCompound;
    }

    public String metlinCompoundWebPage() {
        return WEB_COMPOUND_METLIN + this.metlinCompound;
    }

    public String getPCCompound() {
        return PCCompound;
    }

    public void setPCCompound(String PCCompound) {
        this.PCCompound = PCCompound;
    }

    public String PCCompoundWebPage() {
        return WEB_COMPOUND_PUBCHEMICHAL + this.PCCompound;
    }
    
    public String ChebiCompoundWebPage() {
        return WEB_COMPOUND_CHEBI + this.PCCompound;
    }

    public String getInchikey() {
        return inchikey;
    }

    public void setInchikey(String inchikey) {
        this.inchikey = inchikey;
    }
    

    /**
     *
     * @param pathway
     */
    public void addPathway(Pathway pathway) {
        this.pathways.add(pathway);
    }

    /**
     * Writes in a excel row all the items of the compound
     *
     * @param sheet
     * @param row
     * @param flag
     * @throws WriteException
     * @throws MalformedURLException
     */
    public void toExcel(WritableSheet sheet, int row, int flag) throws WriteException, MalformedURLException {

        Label doubleLabel = new Label(PathwayColumns.EXPERIMENTAL_MASS.getnColumn(), row, this.expmass);
        sheet.addCell(doubleLabel);

        if (flag == 1) {
            doubleLabel = new Label(PathwayColumns.RETENTION_TIME.getnColumn(), row, this.retentionTime);
            sheet.addCell(doubleLabel);

            Label intLabel = new Label(PathwayColumns.COMPOUND_ID.getnColumn(), row, this.compound_id);
            // System.out.println("Identifier: " + this.identifier + " Content:" + intLabel.getContents());
            sheet.addCell(intLabel);
            
            Label stringLabel = new Label(PathwayColumns.ADDUCT.getnColumn(), row, this.adduct);
            sheet.addCell(stringLabel);

            doubleLabel = new Label(PathwayColumns.INCREMENT_PPM.getnColumn(), row, this.ppmError);
            // System.out.println("ppmError: " + this.ppmError + " Content:" + intLabel.getContents());
            sheet.addCell(doubleLabel);
            
            doubleLabel = new Label(PathwayColumns.MOLECULAR_WEIGHT.getnColumn(), row, this.molecularWeight);
            // System.out.println("Identifier: " + this.molecularWeight + " Content:" + doubleLabel.getContents());
            sheet.addCell(doubleLabel);
            
            stringLabel = new Label(PathwayColumns.NAME.getnColumn(), row, this.name);
            sheet.addCell(stringLabel);
            
            stringLabel = new Label(PathwayColumns.FORMULA.getnColumn(), row, this.formula);
            sheet.addCell(stringLabel);

            stringLabel = new Label(PathwayColumns.CAS.getnColumn(), row, this.casId);
            sheet.addCell(stringLabel);

            if (!this.keggCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.KEGG.getnColumn(), row, new URL(WEB_COMPOUND_KEGG + this.keggCompound));
                hl.setDescription(this.keggCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.KEGG.getnColumn(), row, this.keggCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.HMDBCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.HMDB.getnColumn(), row, new URL(WEB_COMPOUND_HMDB + this.HMDBCompound));
                hl.setDescription(this.HMDBCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.HMDB.getnColumn(), row, this.HMDBCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.LMCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.LIPIDMAPS.getnColumn(), row, new URL(WEB_COMPOUND_LM + this.LMCompound));
                hl.setDescription(this.LMCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.LIPIDMAPS.getnColumn(), row, this.LMCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.metlinCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.METLIN.getnColumn(), row, new URL(WEB_COMPOUND_METLIN + this.LMCompound));
                hl.setDescription(this.metlinCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.METLIN.getnColumn(), row, this.metlinCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.PCCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.PUBCHEM.getnColumn(), row, new URL(WEB_COMPOUND_PUBCHEMICHAL + this.PCCompound));
                hl.setDescription(this.PCCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.PUBCHEM.getnColumn(), row, this.metlinCompound);
                sheet.addCell(stringLabel);
            }
            
            stringLabel = new Label(PathwayColumns.INCHIKEY.getnColumn(), row, this.inchikey);
            sheet.addCell(stringLabel);
            
            
        } // Default -> Flag = 0
        else {

            Label intLabel = new Label(PathwayColumns.COMPOUND_ID.getnColumn() - 1, row, this.compound_id);
            // System.out.println("Identifier: " + this.identifier + " Content:" + intLabel.getContents());
            sheet.addCell(intLabel);
            
            Label stringLabel = new Label(PathwayColumns.ADDUCT.getnColumn() - 1, row, this.adduct);
            sheet.addCell(stringLabel);

            doubleLabel = new Label(PathwayColumns.INCREMENT_PPM.getnColumn() - 1, row, this.ppmError);
            // System.out.println("ppmError: " + this.ppmError + " Content:" + intLabel.getContents());
            sheet.addCell(doubleLabel);
            
            doubleLabel = new Label(PathwayColumns.MOLECULAR_WEIGHT.getnColumn() - 1, row, this.molecularWeight);
            // System.out.println("Identifier: " + this.molecularWeight + " Content:" + doubleLabel.getContents());
            sheet.addCell(doubleLabel);
            
            stringLabel = new Label(PathwayColumns.NAME.getnColumn() - 1, row, this.name);
            sheet.addCell(stringLabel);
            
            stringLabel = new Label(PathwayColumns.FORMULA.getnColumn() - 1, row, this.formula);
            sheet.addCell(stringLabel);

            stringLabel = new Label(PathwayColumns.CAS.getnColumn() - 1, row, this.casId);
            sheet.addCell(stringLabel);

            if (!this.keggCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.KEGG.getnColumn() - 1, row, new URL(WEB_COMPOUND_KEGG + this.keggCompound));
                hl.setDescription(this.keggCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.KEGG.getnColumn() - 1, row, this.keggCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.HMDBCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.HMDB.getnColumn() - 1, row, new URL(WEB_COMPOUND_HMDB + this.HMDBCompound));
                hl.setDescription(this.HMDBCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.HMDB.getnColumn() - 1, row, this.HMDBCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.LMCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.LIPIDMAPS.getnColumn() - 1, row, new URL(WEB_COMPOUND_LM + this.LMCompound));
                hl.setDescription(this.LMCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.LIPIDMAPS.getnColumn() - 1, row, this.LMCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.metlinCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.METLIN.getnColumn() - 1, row, new URL(WEB_COMPOUND_METLIN + this.LMCompound));
                hl.setDescription(this.metlinCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.METLIN.getnColumn() - 1, row, this.metlinCompound);
                sheet.addCell(stringLabel);
            }
            if (!this.PCCompound.equals("")) {
                WritableHyperlink hl = new WritableHyperlink(PathwayColumns.PUBCHEM.getnColumn() - 1, row, new URL(WEB_COMPOUND_PUBCHEMICHAL + this.PCCompound));
                hl.setDescription(this.PCCompound);
                sheet.addHyperlink(hl);
            } else {
                stringLabel = new Label(PathwayColumns.PUBCHEM.getnColumn() - 1, row, this.metlinCompound);
                sheet.addCell(stringLabel);
            }
            
            stringLabel = new Label(PathwayColumns.INCHIKEY.getnColumn() - 1, row, this.inchikey);
            sheet.addCell(stringLabel);

        }

    }

    /**
     * Creates the structure of the items of the compunds in html format Less
     * job on application server
     *
     * @return String with all the columns
     */
    public String toHtml() {
        String html = "";
        html = html.concat("<td>" + this.expmass + "</td>");
        html = html.concat("<td>" + this.retentionTime + "</td>");
        html = html.concat("<td>" + this.compound_id + "</td>");
        html = html.concat("<td>" + this.adduct + "</td>");
        html = html.concat("<td>" + this.ppmError + "</td>");
        html = html.concat("<td>" + this.molecularWeight + "</td>");
        html = html.concat("<td>" + this.name.replaceAll("\n", "<br/>") + "</td>");
        html = html.concat("<td>" + this.formula + "</td>");
        html = html.concat("<td>" + this.casId + "</td>");
        html = html.concat("<td>" + keggToHtml() + "</td>");
        html = html.concat("<td>" + hmdbToHtml() + "</td>");
        html = html.concat("<td>" + lmToHtml() + "</td>");
        html = html.concat("<td>" + metlinToHtml() + "</td>");
        html = html.concat("<td>" + pcToHtml() + "</td>");
        html = html.concat("<td>" + this.inchikey + "</td>");

        return html;
    }

    /**
     * Creates the hyperlink of the compound to keggCompound web
     *
     * @return the hyperlink
     */
    public String keggToHtml() {
        String s = "";
        if (!this.keggCompound.equals("")) {
            s = "<a href='" + WEB_COMPOUND_KEGG + this.keggCompound + "'>"
                    + this.keggCompound + "</a>";
        }
        return s;
    }

    /**
     * Creates the hyperlink of the compound to HMDBCompound web
     *
     * @return the hyperlink
     */
    public String hmdbToHtml() {
        String s = "";
        if (!this.HMDBCompound.equals("")) {
            s = "<a href='" + WEB_COMPOUND_HMDB + this.HMDBCompound + "'>"
                    + this.HMDBCompound + "</a>";
        }
        return s;
    }

    /**
     * Creates the hyperlink of the compound to LMCompound web
     *
     * @return the hyperlink
     */
    public String lmToHtml() {
        String s = "";
        if (!this.LMCompound.equals("")) {
            s = "<a href='" + WEB_COMPOUND_LM + this.LMCompound + "'>"
                    + this.LMCompound + "</a>";
        }
        return s;
    }

    /**
     * Creates the hyperlink of the compound to metlinCompound web
     *
     * @return the hyperlink
     */
    public String metlinToHtml() {
        String s = "";
        if (!this.metlinCompound.equals("")) {
            s = "<a href='" + WEB_COMPOUND_METLIN + this.metlinCompound + "'>"
                    + this.metlinCompound + "</a>";
        }
        return s;
    }

    /**
     * Creates the hyperlink of the compound to pubChem web
     *
     * @return the hyperlink
     */
    public String pcToHtml() {
        String s = "";
        if (!this.PCCompound.equals("")) {
            s = "<a href='" + WEB_COMPOUND_PUBCHEMICHAL + this.PCCompound + "'>"
                    + this.PCCompound + "</a>";
        }
        return s;
    }

}
