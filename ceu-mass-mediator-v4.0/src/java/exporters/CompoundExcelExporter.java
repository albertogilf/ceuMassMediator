package exporters;

import exporters.compoundsColumns.CompoundColumns;
import exporters.compoundsColumns.CompoundColumnsBrowseSearch;
import java.text.DecimalFormat;
import java.util.Iterator;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Hyperlink;
import persistence.NewPathways;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.1, 17/02/2016
 */
public class CompoundExcelExporter extends ExcelExporter {

    /**
     * Constructor
     *
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 
     * 3 for CEMSCompounds 
     */
    public CompoundExcelExporter(int flag) {
        super(flag);
        super.setNumberOfColumns(40);
    }

    /**
     * Loads the information of the element into the sheet
     *
     * @param element
     * @param sheet
     * @param flag
     */
    @Override
    protected void generateCompoundDataLCMS(Object element, HSSFSheet sheet, int flag) {
        TheoreticalCompounds tc = (TheoreticalCompounds) element;
        // show pathways always
        tc.setBoolShowPathways(true);
        setRow(sheet.createRow(rowNumber++));

        DecimalFormat twoDForm = new DecimalFormat("#.####");

        CellType cellTypeNumeric = CellType.NUMERIC;
        CellType cellTypeString = CellType.STRING;
        //int cellTypeNumeric = HSSFCell.CELL_TYPE_NUMERIC;//CELL_TYPE_NUMERIC;
        //int cellTypeString = HSSFCell.CELL_TYPE_STRING;//CELL_TYPE_NUMERIC;
        HSSFCell cell = getRow().createCell(CompoundColumns.EXPERIMENTAL_MASS.getnColumn(), cellTypeNumeric);
        if (tc.getExperimentalMass() > 0) {
            cell.setCellValue(Double.valueOf(twoDForm.format(tc.getExperimentalMass()).replace(",", ".")));
        } else {
            cell.setCellValue("--------");
        }

        if (flag == 1) {
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME.getnColumn(), cellTypeNumeric);
            cell.setCellValue(Double.valueOf(twoDForm.format(tc.getRetentionTime()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getCasId());

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getCompoundId());

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn(), cellTypeNumeric);
            //cell.setCellValue(tc.getMolecularWeight());
            cell.setCellValue(Double.valueOf(twoDForm.format(tc.getMolecularWeight()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getPPMIncrement());

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn(), cellTypeString);
            cell.getCellStyle().setWrapText(true);
            cell.setCellValue(tc.getName());

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getFormula());

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getAdduct());

            Hyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getKeggCompound());
            linkKegg.setAddress(tc.getKeggWebPage());
            cell.setHyperlink(linkKegg);

            HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getHMDBCompound());
            linkHMDB.setAddress(tc.getHMDBWebPage());
            cell.setHyperlink(linkHMDB);

            HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getLMCompound());
            linkLM.setAddress(tc.getLMWebPage());
            cell.setHyperlink(linkLM);

            HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getMetlinCompound());
            linkMetlin.setAddress(tc.getMetlinWebPage());
            cell.setHyperlink(linkMetlin);

            HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getPCCompound());
            linkPC.setAddress(tc.getPCWebPage());
            cell.setHyperlink(linkPC);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getIonizationScore() >= 0 ? Float.toString(tc.getIonizationScore()) : "N/A");

            if (tc.getIonizationScore() >= 0F) {
                if (tc.getIonizationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getIonizationScore() >= 0.5F && tc.getIonizationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getIonizationScore() >= 1F && tc.getIonizationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            // Deleted temporaly
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getPrecedenceScore() >= 0 ? Float.toString(tc.getPrecedenceScore()) : "N/A");

            if (tc.getPrecedenceScore() >= 0F) {
                if (tc.getPrecedenceScore() < 0.5F) {
                        cell.setCellStyle(getNot_expected_style());
                    } else if (tc.getPrecedenceScore() >= 0.5F && tc.getPrecedenceScore() < 1F) {
                        cell.setCellStyle(getNot_probable_style());
                    } else if (tc.getPrecedenceScore() >= 1F && tc.getPrecedenceScore() < 1.5F) {
                        cell.setCellStyle(getProbable_style());
                    } else {
                        cell.setCellStyle(getExpected_style());
                    }
            }
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getRetentionTimeScore() >= 0 ? Float.toString(tc.getRetentionTimeScore()) : "N/A");

            if (tc.getRetentionTimeScore() >= 0F) {
                if (tc.getRetentionTimeScore() < 0.55F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getRetentionTimeScore() >= 0.5F && tc.getRetentionTimeScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getRetentionTimeScore() >= 1F && tc.getRetentionTimeScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getAdductRelationScore() >= 0 ? Float.toString(tc.getAdductRelationScore()) : "N/A");

            if (tc.getAdductRelationScore() >= 0F) {
                if (tc.getAdductRelationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getAdductRelationScore() >= 0.5F && tc.getAdductRelationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getAdductRelationScore() >= 1F && tc.getAdductRelationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getFinalScore() >= 0 ? Float.toString(tc.getFinalScore()) : "N/A");

            if (tc.getFinalScore() >= 0F) {
                if (tc.getFinalScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getFinalScore() >= 0.5F && tc.getFinalScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getFinalScore() >= 1F && tc.getFinalScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getInChIKey());
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getSMILES());

            generateRestOfMultipleValues(tc, CompoundColumns.PATHWAYS.getnColumn());
        } else {
            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getCasId());

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getCompoundId());

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn() - 1, cellTypeNumeric);
            //cell.setCellValue(tc.getMolecularWeight());
            cell.setCellValue(Double.valueOf(twoDForm.format(tc.getMolecularWeight()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getPPMIncrement());

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getName());

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getFormula());

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getAdduct());

            HSSFHyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getKeggCompound());
            linkKegg.setAddress(tc.getKeggWebPage());
            cell.setHyperlink(linkKegg);

            HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getHMDBCompound());
            linkHMDB.setAddress(tc.getHMDBWebPage());
            cell.setHyperlink(linkHMDB);

            HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getLMCompound());
            linkLM.setAddress(tc.getLMWebPage());
            cell.setHyperlink(linkLM);

            HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getMetlinCompound());
            linkMetlin.setAddress(tc.getMetlinWebPage());
            cell.setHyperlink(linkMetlin);

            HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(tc.getPCCompound());
            linkPC.setAddress(tc.getPCWebPage());
            cell.setHyperlink(linkPC);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getIonizationScore() >= 0 ? Float.toString(tc.getIonizationScore()) : "N/A");

            if (tc.getIonizationScore() >= 0F) {
                if (tc.getIonizationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getIonizationScore() >= 0.5F && tc.getIonizationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getIonizationScore() >= 1F && tc.getIonizationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            // Deleted temporaly
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getPrecedenceScore() >= 0 ? Float.toString(tc.getPrecedenceScore()) : "N/A");

            if (tc.getPrecedenceScore() >= 0F) {
                if (tc.getPrecedenceScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getPrecedenceScore() >= 0.5F && tc.getPrecedenceScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getPrecedenceScore() >= 1F && tc.getPrecedenceScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getRetentionTimeScore() >= 0 ? Float.toString(tc.getRetentionTimeScore()) : "N/A");

            if (tc.getRetentionTimeScore() >= 0F) {
                if (tc.getRetentionTimeScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getRetentionTimeScore() >= 0.5F && tc.getRetentionTimeScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getRetentionTimeScore() >= 1F && tc.getRetentionTimeScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getAdductRelationScore() >= 0 ? Float.toString(tc.getAdductRelationScore()) : "N/A");

            if (tc.getAdductRelationScore() >= 0F) {
                if (tc.getAdductRelationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getAdductRelationScore() >= 0.5F && tc.getAdductRelationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getAdductRelationScore() >= 1F && tc.getAdductRelationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getFinalScore() >= 0 ? Float.toString(tc.getFinalScore()) : "N/A");

            if (tc.getFinalScore() >= 0F) {
                if (tc.getFinalScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (tc.getFinalScore() >= 0.5F && tc.getFinalScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (tc.getFinalScore() >= 1F && tc.getFinalScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getInChIKey());
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getSMILES());

            generateRestOfMultipleValues(tc, CompoundColumns.PATHWAYS.getnColumn() - 1);

        }
    }

    /**
     * Loads the information of the element into the sheet. Compounds comes from
     * the browse Search
     *
     * @param element
     * @param sheet
     */
    @Override
    protected void generateCompoundDataBrowse(Object element, HSSFSheet sheet) {
        TheoreticalCompounds tc = (TheoreticalCompounds) element;
        // show pathways always
        tc.setBoolShowPathways(true);
        setRow(sheet.createRow(rowNumber++));

        DecimalFormat twoDForm = new DecimalFormat("#.####");

        CellType cellTypeNumeric = CellType.NUMERIC;
        CellType cellTypeString = CellType.STRING;
        //int cellTypeNumeric = HSSFCell.CELL_TYPE_NUMERIC;//CELL_TYPE_NUMERIC;
        //int cellTypeString = HSSFCell.CELL_TYPE_STRING;//CELL_TYPE_NUMERIC;
        HSSFCell cell;

        cell = getRow().createCell(CompoundColumnsBrowseSearch.CAS.getnColumn(), cellTypeString);
        cell.setCellValue(tc.getCasId());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.COMPOUND_ID.getnColumn(), cellTypeNumeric);
        cell.setCellValue(tc.getCompoundId());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.MOLECULAR_WEIGHT.getnColumn(), cellTypeNumeric);
        //cell.setCellValue(tc.getMolecularWeight());
        cell.setCellValue(Double.valueOf(twoDForm.format(tc.getMolecularWeight()).replace(",", ".")));

        cell = getRow().createCell(CompoundColumnsBrowseSearch.NAME.getnColumn(), cellTypeString);
        cell.getCellStyle().setWrapText(true);
        cell.setCellValue(tc.getName());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.FORMULA.getnColumn(), cellTypeString);
        cell.setCellValue(tc.getFormula());

        Hyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.KEGG.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(tc.getKeggCompound());
        linkKegg.setAddress(tc.getKeggWebPage());
        cell.setHyperlink(linkKegg);

        HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.HMDB.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(tc.getHMDBCompound());
        linkHMDB.setAddress(tc.getHMDBWebPage());
        cell.setHyperlink(linkHMDB);

        HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.LIPIDMAPS.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(tc.getLMCompound());
        linkLM.setAddress(tc.getLMWebPage());
        cell.setHyperlink(linkLM);

        HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.METLIN.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(tc.getMetlinCompound());
        linkMetlin.setAddress(tc.getMetlinWebPage());
        cell.setHyperlink(linkMetlin);

        HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.PUBCHEM.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(tc.getPCCompound());
        linkPC.setAddress(tc.getPCWebPage());
        cell.setHyperlink(linkPC);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.INCHIKEY.getnColumn(), cellTypeString);
        cell.setCellValue(tc.getInChIKey());
        
        cell = getRow().createCell(CompoundColumnsBrowseSearch.SMILES.getnColumn(), cellTypeString);
        cell.setCellValue(tc.getSMILES());

        generateRestOfMultipleValues(tc, CompoundColumnsBrowseSearch.PATHWAYS.getnColumn());
    }

    /**
     * Shows the mass of the element that is being inserted
     *
     * @param element
     */
    @Override
    protected void showProcessCompoundProgress(Object element) {
        TheoreticalCompounds ec = (TheoreticalCompounds) element;
//        System.out.println("Inserting empirical compound with mass " + ec.getExperimentalMass());
    }

    private void generateRestOfMultipleValues(TheoreticalCompounds tc, int columnPathway) {
        Iterator<NewPathways> kp = tc.getPathways().iterator();
        //int cellType = HSSFCell.CELL_TYPE_STRING;
        if (kp.hasNext()) {
            int count = 0;
            //setRow(sheet.createRow(rowNumber++));

            while (kp.hasNext()) {
                NewPathways p = kp.next();
                insertPathway(p, count, columnPathway);
                count++;
            }
        }
    }

    /**
     * Inserts into the cell a link of the kegg compunds
     *
     * @param p. The KeggPathway
     * @param count
     * @param flag
     */
    private void insertPathway(NewPathways p, int count, int columnPathway) {
        HSSFCell cell = getRow().createCell(columnPathway + count, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(p.getPathwayName());

        Hyperlink link = ch.createHyperlink(HyperlinkType.URL);
        link.setAddress(p.obtainPathwayWebPage());
        cell.setHyperlink(link);
    }

    @Override
    protected void generateCompoundDataCEMS(Object element, HSSFSheet sheet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void generateCompoundDataTargetedCEMS(Object element, HSSFSheet sheet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
