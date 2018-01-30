package exporters;

import exporters.compoundsColumns.CompoundColumns;
import exporters.compoundsColumns.PathwayColumns;
import java.text.DecimalFormat;
import java.util.Iterator;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Hyperlink;
import persistence.NewPathways;
import persistence.theoreticalCompound.TheoreticalCompounds;
import utilities.Constantes;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 3.1, 17/02/2016
 */
public class CompoundExcelExporter extends ExcelExporter {

    public CompoundExcelExporter() {
        super("ceumass_compounds.xls");
        super.setNumberOfColumns(30);
        this.setFileName("ceumass_compounds.xls");
    }

    /**
     * Loads the information of the element into the sheet
     *
     * @param element
     * @param sheet
     * @param flag
     */
    @Override
    protected void generateCompoundData(Object element, HSSFSheet sheet, int flag) {
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
        if(tc.getExperimentalMass()>0)
        {
        cell.setCellValue(Double.valueOf(twoDForm.format(tc.getExperimentalMass()).replace(",", ".")));
        }
        else
        {
            cell.setCellValue("--------");
        }
        
        if (flag == 1) {
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME.getnColumn(), cellTypeNumeric);
            cell.setCellValue(Double.valueOf(twoDForm.format(tc.getRetentionTime()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn(), cellTypeString);
            cell.setCellValue(tc.getCasId());

            cell = getRow().createCell(CompoundColumns.IDENTIFIER.getnColumn(), cellTypeNumeric);
            cell.setCellValue(tc.getIdentifier());

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

            Iterator<NewPathways> np = tc.getPathways().iterator();
            generateRestOfMultipleValues(tc, sheet, np, flag);

        } else {
            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, cellTypeString);
            cell.setCellValue(tc.getCasId());

            cell = getRow().createCell(CompoundColumns.IDENTIFIER.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(tc.getIdentifier());

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

            Iterator<NewPathways> np = tc.getPathways().iterator();
            generateRestOfMultipleValues(tc, sheet, np, flag);

        }
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

    /**
     * Generate the headline
     *
     * @param flag
     */
    @Override
    protected void generateCompoundHeadLine(int flag) {
        HSSFCell cell = getRow().createCell(CompoundColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.EXPERIMENTAL_MASS_HEADER);
        if (flag == 1) {
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.RT_HEADER);
            cell = getRow().createCell(CompoundColumns.CAS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.CAS_HEADER);

            cell = getRow().createCell(CompoundColumns.IDENTIFIER.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.IDENTIFIER_HEADER);

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PPM_INCREMENT_HEADER);

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.MOL_WEIGHT_HEADER);

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.NAME_HEADER);

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.FORMULA_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.ADDUCT_HEADER);

            HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.KEGG_HEADER);
            linkKeggDB.setAddress(Constantes.WEB_KEGG);
            cell.setHyperlink(linkKeggDB);

            HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.HMDB_HEADER);
            linkHMDBDB.setAddress(Constantes.WEB_HMDB);
            cell.setHyperlink(linkHMDBDB);

            HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.LIPIDMAPS_HEADER);
            linkLMDB.setAddress(Constantes.WEB_LIPID_MAPS);
            cell.setHyperlink(linkLMDB);

            HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.METLIN_HEADER);
            linkMETLINDB.setAddress(Constantes.WEB_METLIN);
            cell.setHyperlink(linkMETLINDB);

            HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.PUBHCEMICAL_HEADER);
            linkPCDB.setAddress(Constantes.WEB_PUBCHEMICHAL);
            cell.setHyperlink(linkPCDB);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.IONIZATION_SCORE_HEADER);

            // DELETED TEMPORALY
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PRECEDENCE_SCORE_HEADER);
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.RETENTION_TIME_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.ADDUCT_RELATION_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.FINAL_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.INCHIKEY_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PATHWAYS_HEADER);

        } else {

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.CAS_HEADER);

            cell = getRow().createCell(CompoundColumns.IDENTIFIER.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.IDENTIFIER_HEADER);

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PPM_INCREMENT_HEADER);

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.MOL_WEIGHT_HEADER);

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.NAME_HEADER);

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.FORMULA_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.ADDUCT_HEADER);

            HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.KEGG_HEADER);
            linkKeggDB.setAddress(Constantes.WEB_KEGG);
            cell.setHyperlink(linkKeggDB);

            HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.HMDB_HEADER);
            linkHMDBDB.setAddress(Constantes.WEB_HMDB);
            cell.setHyperlink(linkHMDBDB);

            HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.LIPIDMAPS_HEADER);
            linkLMDB.setAddress(Constantes.WEB_LIPID_MAPS);
            cell.setHyperlink(linkLMDB);

            HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.METLIN_HEADER);
            linkMETLINDB.setAddress(Constantes.WEB_METLIN);
            cell.setHyperlink(linkMETLINDB);

            HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constantes.PUBHCEMICAL_HEADER);
            linkPCDB.setAddress(Constantes.WEB_PUBCHEMICHAL);
            cell.setHyperlink(linkPCDB);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.IONIZATION_SCORE_HEADER);

            // Deleted temporaly
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PRECEDENCE_SCORE_HEADER);
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.RETENTION_TIME_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.ADDUCT_RELATION_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.FINAL_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.INCHIKEY_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PATHWAYS_HEADER);

        }
    }

    /**
     *
     * @param element
     * @param sheet
     * @param numRow
     * @return the number of rows generated
     */
    @Override
    public int generatePathwayFileData(Object element, HSSFSheet sheet, int numRow) {
        int numRows = 0;
        importers.Pathway pathway = (importers.Pathway) element;

        HSSFRow rowPathway = sheet.createRow(numRow);

        DecimalFormat twoDForm = new DecimalFormat("#.####");

        HSSFHyperlink linkPathway = ch.createHyperlink(HyperlinkType.URL);

        HSSFCell cell = rowPathway.createCell(PathwayColumns.PATHWAYS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(pathway.name);
        linkPathway.setAddress(pathway.hyperl.getAddress());
        cell.setHyperlink(linkPathway);

        // Loop for compounds which are present in the pathway
        for (importers.CompoundForPathway compound : pathway.compounds) {
            cell = rowPathway.createCell(PathwayColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.NUMERIC);

            cell.setCellValue(Double.valueOf(twoDForm.format(compound.expmass.replace(",", "."))));

            cell = rowPathway.createCell(PathwayColumns.RETENTION_TIME.getnColumn(), CellType.NUMERIC);
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.retentionTime.replace(",", "."))));

            cell = rowPathway.createCell(PathwayColumns.CAS.getnColumn(), CellType.STRING);
            cell.setCellValue(compound.casId);

            cell = rowPathway.createCell(PathwayColumns.IDENTIFIER.getnColumn(), CellType.NUMERIC);
            cell.setCellValue(compound.identifier);

            cell = rowPathway.createCell(PathwayColumns.INCREMENT_PPM.getnColumn(), CellType.NUMERIC);
            cell.setCellValue(compound.ppmError);

            cell = rowPathway.createCell(PathwayColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.NUMERIC);
            //cell.setCellValue(tc.getMolecularWeight());
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.molecularWeight.replace(",", "."))));

            cell = rowPathway.createCell(PathwayColumns.NAME.getnColumn(), CellType.STRING);
            cell.setCellValue(compound.name);

            cell = rowPathway.createCell(PathwayColumns.FORMULA.getnColumn(), CellType.STRING);
            cell.setCellValue(compound.formula);

            cell = rowPathway.createCell(PathwayColumns.ADDUCT.getnColumn(), CellType.STRING);
            cell.setCellValue(compound.adduct);

            HSSFHyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
            cell = rowPathway.createCell(PathwayColumns.KEGG.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.keggCompound);
            String keggWebPage = Constantes.WEB_COMPUESTO_KEGG + compound.keggCompound;
            linkKegg.setAddress(keggWebPage);
            cell.setHyperlink(linkKegg);

            HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = rowPathway.createCell(PathwayColumns.HMDB.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.HMDBCompound);
            String HMDBWebPage = Constantes.WEB_COMPUESTO_HMDB + compound.HMDBCompound;
            linkKegg.setAddress(HMDBWebPage);
            cell.setHyperlink(linkHMDB);

            HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
            cell = rowPathway.createCell(PathwayColumns.LIPIDMAPS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.LMCompound);
            String LMWebPage = Constantes.WEB_COMPUESTO_LM + compound.LMCompound;
            linkKegg.setAddress(LMWebPage);
            cell.setHyperlink(linkLM);

            HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
            cell = rowPathway.createCell(PathwayColumns.METLIN.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.metlinCompound);
            String metlinWebPage = Constantes.WEB_COMPUESTO_METLIN + compound.metlinCompound;
            linkKegg.setAddress(metlinWebPage);
            cell.setHyperlink(linkMetlin);

            HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
            cell = rowPathway.createCell(PathwayColumns.PUBCHEM.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.PCCompound);
            String PCWebPage = Constantes.WEB_COMPUESTO_PUBCHEMICHAL + compound.PCCompound;
            linkKegg.setAddress(PCWebPage);
            cell.setHyperlink(linkPC);

            cell = rowPathway.createCell(PathwayColumns.INCHIKEY.getnColumn(), CellType.STRING);
            cell.setCellValue(compound.inchikey);

            numRows++;

            rowPathway = sheet.createRow(numRow + numRows);
        }
        return numRows;
    }

    /**
     * Shows the mass of the element that is being inserted
     *
     * @param element
     */
    @Override
    public void showProcessPathwayProgress(Object element) {
        importers.Pathway ec = (importers.Pathway) element;
//        System.out.println("Inserting pathway with name " + ec.name);
    }

    @Override
    public void generatePathwayFileHeadLine(HSSFSheet sheetPathway, int rowNumber) {

        HSSFRow rowHeaderPathway = sheetPathway.createRow(rowNumber);
        HSSFCell cell = rowHeaderPathway.createCell(PathwayColumns.PATHWAYS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.PATHWAYS_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.EXPERIMENTAL_MASS_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.RETENTION_TIME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.RT_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.CAS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.CAS_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.IDENTIFIER.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.IDENTIFIER_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.INCREMENT_PPM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.PPM_INCREMENT_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.MOL_WEIGHT_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.NAME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.NAME_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.FORMULA.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.FORMULA_HEADER);

        cell = rowHeaderPathway.createCell(PathwayColumns.ADDUCT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.ADDUCT_HEADER);

        HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
        cell = rowHeaderPathway.createCell(PathwayColumns.KEGG.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.KEGG_HEADER);
        linkKeggDB.setAddress(Constantes.WEB_KEGG);
        cell.setHyperlink(linkKeggDB);

        HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
        cell = rowHeaderPathway.createCell(PathwayColumns.HMDB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.HMDB_HEADER);
        linkHMDBDB.setAddress(Constantes.WEB_HMDB);
        cell.setHyperlink(linkHMDBDB);

        HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = rowHeaderPathway.createCell(PathwayColumns.LIPIDMAPS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.LIPIDMAPS_HEADER);
        linkLMDB.setAddress(Constantes.WEB_LIPID_MAPS);
        cell.setHyperlink(linkLMDB);

        HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
        cell = rowHeaderPathway.createCell(PathwayColumns.METLIN.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.METLIN_HEADER);
        linkMETLINDB.setAddress(Constantes.WEB_METLIN);
        cell.setHyperlink(linkMETLINDB);

        HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
        cell = rowHeaderPathway.createCell(PathwayColumns.PUBCHEM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.PUBHCEMICAL_HEADER);
        linkPCDB.setAddress(Constantes.WEB_PUBCHEMICHAL);
        cell.setHyperlink(linkPCDB);

        cell = rowHeaderPathway.createCell(PathwayColumns.INCHIKEY.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.INCHIKEY_HEADER);

    }

}
