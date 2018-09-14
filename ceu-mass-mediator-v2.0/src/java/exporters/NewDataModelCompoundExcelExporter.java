/*
 * NewCMM_Class.java
 *
 * Created on 24-may-2018, 17:39:52
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package exporters;

import LCMS.CompoundLCMS;
import compound.Compound;
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
import pathway.Pathway;

/**
 * Class for generate excel from compounds in the new data model
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 24-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class NewDataModelCompoundExcelExporter extends ExcelExporter {

    /**
     * Creates a new instance of NewCMM_Class
     */
    public NewDataModelCompoundExcelExporter() {
        super("ceumass_compounds.xls");
        super.setNumberOfColumns(30);
        this.setFileName("ceumass_compounds.xls");
    }

    /**
     * Loads the information of the element into the sheet. 
     * Compound comes from a MS search
     *
     * @param element
     * @param sheet
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT.
     */
    @Override
    protected void generateCompoundDataLCMS(Object element, HSSFSheet sheet, int flag) {
        CompoundLCMS compound = (CompoundLCMS) element;
        //compound.Compound compound = (compound.Compound) element;
        // show pathways always
        compound.setBoolShowPathways(true);
        setRow(sheet.createRow(rowNumber++));
        DecimalFormat twoDForm = new DecimalFormat("#.####");

        CellType cellTypeNumeric = CellType.NUMERIC;
        CellType cellTypeString = CellType.STRING;
        //int cellTypeNumeric = HSSFCell.CELL_TYPE_NUMERIC;//CELL_TYPE_NUMERIC;
        //int cellTypeString = HSSFCell.CELL_TYPE_STRING;//CELL_TYPE_NUMERIC;
        HSSFCell cell = getRow().createCell(CompoundColumns.EXPERIMENTAL_MASS.getnColumn(), cellTypeNumeric);
        if (compound.getEM() > 0) {
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.getMassIntroduced()).replace(",", ".")));
        } else {
            cell.setCellValue("--------");
        }

        if (flag == 1) {
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME.getnColumn(), cellTypeNumeric);
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.getRT()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn(), cellTypeString);
            cell.setCellValue(compound.getCas_id());

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn(), cellTypeNumeric);
            cell.setCellValue(compound.getCompound_id());

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn(), cellTypeNumeric);
            //cell.setCellValue(tc.getMolecularWeight());
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.getMass()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn(), cellTypeNumeric);
            cell.setCellValue(compound.getIncrementPPM());

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn(), cellTypeString);
            cell.getCellStyle().setWrapText(true);
            cell.setCellValue(compound.getCompound_name());

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn(), cellTypeString);
            cell.setCellValue(compound.getFormula());

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn(), cellTypeString);
            cell.setCellValue(compound.getAdduct());

            Hyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getKegg_id());
            linkKegg.setAddress(compound.getCompoundKeggWebPage());
            cell.setHyperlink(linkKegg);

            HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getHmdb_id());
            linkHMDB.setAddress(compound.getCompoundHMDBWebPage());
            cell.setHyperlink(linkHMDB);

            HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getLm_id());
            linkLM.setAddress(compound.getCompoundLMWebPage());
            cell.setHyperlink(linkLM);

            HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getMetlin_id());
            linkMetlin.setAddress(compound.getCompoundMetlinWebPage());
            cell.setHyperlink(linkMetlin);

            HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn(), cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getPc_id());
            linkPC.setAddress(compound.getCompoundPubChemWebPage());
            cell.setHyperlink(linkPC);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(compound.getIonizationScore() >= 0 ? Float.toString(compound.getIonizationScore()) : "N/A");

            if (compound.getIonizationScore() >= 0F) {
                if (compound.getIonizationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getIonizationScore() >= 0.5F && compound.getIonizationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getIonizationScore() >= 1F && compound.getIonizationScore() < 1.5F) {
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
            cell.setCellValue(compound.getRTscore() >= 0 ? Float.toString(compound.getRTscore()) : "N/A");

            if (compound.getRTscore()>= 0F) {
                if (compound.getRTscore() < 0.55F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getRTscore() >= 0.5F && compound.getRTscore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getRTscore() >= 1F && compound.getRTscore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(compound.getAdductRelationScore() >= 0 ? Float.toString(compound.getAdductRelationScore()) : "N/A");

            if (compound.getAdductRelationScore() >= 0F) {
                if (compound.getAdductRelationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getAdductRelationScore() >= 0.5F && compound.getAdductRelationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getAdductRelationScore() >= 1F && compound.getAdductRelationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn(), cellTypeNumeric);
            cell.setCellValue(compound.getFinalScore() >= 0 ? Float.toString(compound.getFinalScore()) : "N/A");

            if (compound.getFinalScore() >= 0F) {
                if (compound.getFinalScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getFinalScore() >= 0.5F && compound.getFinalScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getFinalScore() >= 1F && compound.getFinalScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }
            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn(), cellTypeString);
            cell.setCellValue(compound.getInChIKey());
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn(), cellTypeString);
            cell.setCellValue(compound.getSmiles());

            generateRestOfMultipleValues(compound, CompoundColumns.PATHWAYS.getnColumn());
        } else {
            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getCas_id());

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(compound.getCompound_id());

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn() - 1, cellTypeNumeric);
            //cell.setCellValue(tc.getMolecularWeight());
            cell.setCellValue(Double.valueOf(twoDForm.format(compound.getMass()).replace(",", ".")));

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(compound.getIncrementPPM());

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getCompound_name());

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getFormula());

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getAdduct());

            HSSFHyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getKegg_id());
            linkKegg.setAddress(compound.getCompoundKeggWebPage());
            cell.setHyperlink(linkKegg);

            HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getHmdb_id());
            linkHMDB.setAddress(compound.getCompoundHMDBWebPage());
            cell.setHyperlink(linkHMDB);

            HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getLm_id());
            linkLM.setAddress(compound.getCompoundLMWebPage());
            cell.setHyperlink(linkLM);

            HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getMetlin_id());
            linkMetlin.setAddress(compound.getCompoundMetlinWebPage());
            cell.setHyperlink(linkMetlin);

            HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn() - 1, cellTypeString);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(compound.getPc_id());
            linkPC.setAddress(compound.getCompoundPubChemWebPage());
            cell.setHyperlink(linkPC);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(compound.getIonizationScore() >= 0 ? Float.toString(compound.getIonizationScore()) : "N/A");

            if (compound.getIonizationScore() >= 0F) {
                if (compound.getIonizationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getIonizationScore() >= 0.5F && compound.getIonizationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getIonizationScore() >= 1F && compound.getIonizationScore() < 1.5F) {
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
            cell.setCellValue(compound.getRTscore()>= 0 ? Float.toString(compound.getRTscore()) : "N/A");

            if (compound.getRTscore() >= 0F) {
                if (compound.getRTscore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getRTscore() >= 0.5F && compound.getRTscore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getRTscore() >= 1F && compound.getRTscore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(compound.getAdductRelationScore() >= 0 ? Float.toString(compound.getAdductRelationScore()) : "N/A");

            if (compound.getAdductRelationScore() >= 0F) {
                if (compound.getAdductRelationScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getAdductRelationScore() >= 0.5F && compound.getAdductRelationScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getAdductRelationScore() >= 1F && compound.getAdductRelationScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn() - 1, cellTypeNumeric);
            cell.setCellValue(compound.getFinalScore() >= 0 ? Float.toString(compound.getFinalScore()) : "N/A");

            if (compound.getFinalScore() >= 0F) {
                if (compound.getFinalScore() < 0.5F) {
                    cell.setCellStyle(getNot_expected_style());
                } else if (compound.getFinalScore() >= 0.5F && compound.getFinalScore() < 1F) {
                    cell.setCellStyle(getNot_probable_style());
                } else if (compound.getFinalScore() >= 1F && compound.getFinalScore() < 1.5F) {
                    cell.setCellStyle(getProbable_style());
                } else {
                    cell.setCellStyle(getExpected_style());
                }
            }

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getInChIKey());
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn() - 1, cellTypeString);
            cell.setCellValue(compound.getSmiles());

            generateRestOfMultipleValues(compound, CompoundColumns.PATHWAYS.getnColumn() - 1);
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
        Compound compound = (Compound) element;
        // show pathways always
        compound.setBoolShowPathways(true);
        setRow(sheet.createRow(rowNumber++));

        DecimalFormat twoDForm = new DecimalFormat("#.####");

        CellType cellTypeNumeric = CellType.NUMERIC;
        CellType cellTypeString = CellType.STRING;
        //int cellTypeNumeric = HSSFCell.CELL_TYPE_NUMERIC;//CELL_TYPE_NUMERIC;
        //int cellTypeString = HSSFCell.CELL_TYPE_STRING;//CELL_TYPE_NUMERIC;
        HSSFCell cell;

        cell = getRow().createCell(CompoundColumnsBrowseSearch.CAS.getnColumn(), cellTypeString);
        cell.setCellValue(compound.getCas_id());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.COMPOUND_ID.getnColumn(), cellTypeNumeric);
        cell.setCellValue(compound.getCompound_id());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.MOLECULAR_WEIGHT.getnColumn(), cellTypeNumeric);
        //cell.setCellValue(tc.getMolecularWeight());
        cell.setCellValue(Double.valueOf(twoDForm.format(compound.getMass()).replace(",", ".")));

        cell = getRow().createCell(CompoundColumnsBrowseSearch.NAME.getnColumn(), cellTypeString);
        cell.getCellStyle().setWrapText(true);
        cell.setCellValue(compound.getCompound_name());

        cell = getRow().createCell(CompoundColumnsBrowseSearch.FORMULA.getnColumn(), cellTypeString);
        cell.setCellValue(compound.getFormula());

        HSSFHyperlink linkHMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.HMDB.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(compound.getHmdb_id());
        linkHMDB.setAddress(compound.getCompoundHMDBWebPage());
        cell.setHyperlink(linkHMDB);

        
        HSSFHyperlink linkMetlin = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.METLIN.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(compound.getMetlin_id());
        linkMetlin.setAddress(compound.getCompoundMetlinWebPage());
        cell.setHyperlink(linkMetlin);

        
        HSSFHyperlink linkLM = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.LIPIDMAPS.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(compound.getLm_id());
        linkLM.setAddress(compound.getCompoundLMWebPage());
        cell.setHyperlink(linkLM);
        
        Hyperlink linkKegg = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.KEGG.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(compound.getKegg_id());
        linkKegg.setAddress(compound.getCompoundKeggWebPage());
        cell.setHyperlink(linkKegg);

        HSSFHyperlink linkPC = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.PUBCHEM.getnColumn(), cellTypeString);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(compound.getPc_id());
        linkPC.setAddress(compound.getCompoundPubChemWebPage());
        cell.setHyperlink(linkPC);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.INCHIKEY.getnColumn(), cellTypeString);
        cell.setCellValue(compound.getInChIKey());
        
        cell = getRow().createCell(CompoundColumnsBrowseSearch.SMILES.getnColumn(), cellTypeString);
        cell.setCellValue(compound.getSmiles());

        generateRestOfMultipleValues(compound, CompoundColumnsBrowseSearch.PATHWAYS.getnColumn());
    }

    /**
     * Shows the mass of the element that is being inserted
     *
     * @param element
     */
    @Override
    protected void showProcessCompoundProgress(Object element) {
        Compound ec = (Compound) element;
//        System.out.println("Inserting empirical compound with mass " + ec.getExperimentalMass());
    }

    private void generateRestOfMultipleValues(Compound compound, int columnPathway) {
        Iterator<Pathway> kp = compound.getPathways().iterator();
        //int cellType = HSSFCell.CELL_TYPE_STRING;
        if (kp.hasNext()) {
            int count = 0;
            //setRow(sheet.createRow(rowNumber++));

            while (kp.hasNext()) {
                Pathway p = kp.next();
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
    private void insertPathway(Pathway p, int count, int columnPathway) {
        HSSFCell cell = getRow().createCell(columnPathway + count, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(p.getPathwayName());

        Hyperlink link = ch.createHyperlink(HyperlinkType.URL);
        link.setAddress(p.getPathwayWebPage());
        cell.setHyperlink(link);
    }
}
