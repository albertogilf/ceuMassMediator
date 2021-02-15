package exporters;

import exporters.compoundsColumns.CEMSCompoundColumns;
import exporters.compoundsColumns.CompoundColumns;
import exporters.compoundsColumns.CompoundColumnsBrowseSearch;
import java.io.FileOutputStream;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
//import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import presentation.paginationHelpers.MyPaginationHelper;
import utilities.Downloader;
import presentation.paginationHelpers.PaginationHelper;
import static utilities.Constants.ITEMS_PER_PAGE_IN_EXCEL;
import utilities.Constants;

/**
 * Abstract Class for exporting the compounds into excel file
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
public abstract class ExcelExporter {

    private String initialTitle;
    private String fileName;
    private String realFileName;
    private FileOutputStream file;
    protected short rowNumber;
    private HSSFWorkbook book;
    HSSFCreationHelper ch;
    private HSSFRow row;
    //private HSSFCell cell;
    private int numberOfColumns;
    private HSSFCellStyle hlink_style;
    private HSSFFont hlink_font;
    private HSSFCellStyle adjustedLetter;
    private HSSFCellStyle headline_style;
    private HSSFFont headline_font;
    private DataFormat numerical_format;
    private HSSFCellStyle numerical_style;
    private HSSFCellStyle numerical_bold_style;
    private int currentSheetNumber;

    private HSSFFont score_font;
    private HSSFCellStyle expected_style;
    private HSSFCellStyle probable_style;
    private HSSFCellStyle not_probable_style;
    private HSSFCellStyle not_expected_style;

    private PaginationHelper ph;

    /**
     * Constructor
     *
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds
     */
    public ExcelExporter(int flag) {
        this.setFileName(flag);
        setInitialTitle(flag);
        FacesContext fc = FacesContext.getCurrentInstance();
        //El external context es el application context de JSP.
        ExternalContext ec = fc.getExternalContext();
        realFileName = ec.getRealPath("resources/" + fileName);

        // creates bppl and creationhelper
        this.book = new HSSFWorkbook();
        this.ch = book.getCreationHelper();
        setStyle();
    }

    private void setStyle() {
        // Crea el estilo hlink_style

        hlink_style = book.createCellStyle();
        hlink_font = book.createFont();
        hlink_font.setUnderline(HSSFFont.U_SINGLE);
        hlink_font.setColor(HSSFColor.BLUE.index);
        hlink_style.setFont(hlink_font);

        // Crea el estilo headline
        headline_style = book.createCellStyle();
        headline_font = book.createFont();
        headline_font.setBold(true);
        headline_style.setFont(headline_font);

        // Crea estilo numérico con cuatro cifras decimales
        numerical_format = book.createDataFormat();
        numerical_style = book.createCellStyle();
        numerical_style.setDataFormat(numerical_format.getFormat("0.0000"));

        // Crea estilo numérico en negrita con cuatro cifras decimales
        numerical_bold_style = book.createCellStyle();
        numerical_bold_style.setDataFormat(numerical_format.getFormat("0.0000"));
        numerical_bold_style.setFont(headline_font);

        // Crea el estilo adjustedLetter
        adjustedLetter = book.createCellStyle();
        //alineado a la izquierda y justificado vertical
        adjustedLetter.setAlignment(HorizontalAlignment.LEFT);
        adjustedLetter.setVerticalAlignment(VerticalAlignment.JUSTIFY);

        score_font = book.createFont();
        score_font.setBold(true);

        expected_style = book.createCellStyle();
        expected_style.setFont(score_font);
        expected_style.setFillForegroundColor(HSSFColor.LIME.index);
        expected_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        probable_style = book.createCellStyle();
        probable_style.setFont(score_font);
        probable_style.setFillForegroundColor(HSSFColor.YELLOW.index);
        probable_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        not_probable_style = book.createCellStyle();
        not_probable_style.setFont(score_font);
        not_probable_style.setFillForegroundColor(HSSFColor.ORANGE.index);
        not_probable_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        not_expected_style = book.createCellStyle();
        not_expected_style.setFont(score_font);
        not_expected_style.setFillForegroundColor(HSSFColor.RED.index);
        not_expected_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    /**
     *
     * Generates the excel file from the elements passed as an argument in the
     * param elements. Flag indicates the compounds type to create the excel
     * file Flag may be 0: LCMS Compounds without RT, 1: LCMS Compounds with RT,
     * 2: CMMCOmpound (BrowseSearch), 3: CEMSCompounds
     *
     * @param elements
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds Search, 4 for Targeted CE MS Search
     */
    public void generateWholeExcelCompound(List elements, int flag) {
        System.out.println("Exporting into Excel: " + this.fileName);
        this.ph = new MyPaginationHelper(ITEMS_PER_PAGE_IN_EXCEL, elements.size());
        try {
            setFile(new FileOutputStream(getRealFileName()));
            boolean hasNext = true;

            this.setCurrentSheetNumber(0);

            while (hasNext) {
                generateSheetCompounds(elements, flag);

                hasNext = this.ph.isHasNextPage();
                this.ph.nextPage();
                this.setCurrentSheetNumber(this.getCurrentSheetNumber() + 1);
            }
            getBook().write(getFile());
            getFile().close();

            (new Downloader()).download(getFileName(), getRealFileName());
            System.out.println("Excel exported");

        } catch (RuntimeException e) {
            System.out.println("RUNTIME EXCEPTION. FAILED GENERATING EXCEL FILE. ExcelExporter.generateWholeExcelCompound");
        } catch (Exception e) {
            System.out.println("NON RUNTIME EXCEPTION. FAILED GENERATING EXCEL FILE. ExcelExporter.generateWholeExcelCompound");
        }
    }

    /**
     * Generates the sheet of the excel with the list of elements
     *
     * @param elements
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds Search
     */
    public void generateSheetCompounds(List elements, int flag) {
        HSSFSheet sheet = book.createSheet();
        rowNumber = 0;

        // HEADER ROW
        setRow(sheet.createRow(rowNumber++));
        HSSFCell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(getInitialTitle());
        setRow(sheet.createRow(rowNumber++));
        System.out.println("FLAG: " + flag);

        switch (flag) {
            case 0:
            case 1:
                sheet.autoSizeColumn(CompoundColumns.NAME.getnColumn());
                generateCompoundHeadLineLCMS(flag);
                elements.forEach((element) -> {
                    // showProcessCompoundProgress(element);
                    generateCompoundDataLCMS(element, sheet, flag);
                });
                break;
            case 2:
                //sheet.setColumnWidth(CompoundColumnsBrowseSearch.NAME.getnColumn(), 50);
                sheet.autoSizeColumn(CompoundColumnsBrowseSearch.NAME.getnColumn());
                generateCompoundHeadLineBrowse();
                // Generate excel file for browse search
                elements.forEach((element) -> {
                    // showProcessCompoundProgress(element);
                    generateCompoundDataBrowse(element, sheet);
                });
                break;
            case 3:
                sheet.autoSizeColumn(CompoundColumns.NAME.getnColumn());
                generateCompoundHeadLineCEMS();
                elements.forEach((element) -> {
                    // showProcessCompoundProgress(element);
                    generateCompoundDataCEMS(element, sheet);
                });
                break;
            case 4:
                sheet.autoSizeColumn(CompoundColumns.NAME.getnColumn());
                generateCompoundHeadLineTargetedCEMS();
                for (Object element : elements) {
                    // showProcessCompoundProgress(element);
                    generateCompoundDataTargetedCEMS(element, sheet);
                }
                break;
            default:
                break;
        }

        //Align cell size to text size
        for (int j = 0; j < getNumberOfColumns(); j++) {
            sheet.autoSizeColumn((short) j);
        }
    }

    /**
     * Generate the headline if flag is 0 there is no RT, if flag is 1 there is
     * RT
     *
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds
     */
    protected void generateCompoundHeadLineLCMS(int flag) {
        HSSFCell cell = getRow().createCell(CompoundColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXPERIMENTAL_MASS_HEADER);
        if (flag == 1) {
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.RT_HEADER);
            cell = getRow().createCell(CompoundColumns.CAS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.CAS_HEADER);

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.COMPOUND_ID_HEADER);

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PPM_INCREMENT_HEADER);

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.MOL_WEIGHT_HEADER);

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.NAME_HEADER);

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.FORMULA_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.ADDUCT_HEADER);

            HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.KEGG_HEADER);
            linkKeggDB.setAddress(Constants.WEB_KEGG);
            cell.setHyperlink(linkKeggDB);

            HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.HMDB_HEADER);
            linkHMDBDB.setAddress(Constants.WEB_HMDB);
            cell.setHyperlink(linkHMDBDB);

            HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.LIPIDMAPS_HEADER);
            linkLMDB.setAddress(Constants.WEB_LIPID_MAPS);
            cell.setHyperlink(linkLMDB);

            HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.METLIN_HEADER);
            linkMETLINDB.setAddress(Constants.WEB_METLIN);
            cell.setHyperlink(linkMETLINDB);

            HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.PUBHCEMICAL_HEADER);
            linkPCDB.setAddress(Constants.WEB_PUBCHEMICHAL);
            cell.setHyperlink(linkPCDB);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.IONIZATION_SCORE_HEADER);

            // DELETED TEMPORALY
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PRECEDENCE_SCORE_HEADER);
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.RETENTION_TIME_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.ADDUCT_RELATION_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.FINAL_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.INCHIKEY_HEADER);

            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.SMILES_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PATHWAYS_HEADER);

        } else {

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.CAS_HEADER);

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.COMPOUND_ID_HEADER);

            cell = getRow().createCell(CompoundColumns.INCREMENT_PPM.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PPM_INCREMENT_HEADER);

            cell = getRow().createCell(CompoundColumns.MOLECULAR_WEIGHT.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.MOL_WEIGHT_HEADER);

            cell = getRow().createCell(CompoundColumns.NAME.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.NAME_HEADER);

            cell = getRow().createCell(CompoundColumns.FORMULA.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.FORMULA_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.ADDUCT_HEADER);

            HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.KEGG.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.KEGG_HEADER);
            linkKeggDB.setAddress(Constants.WEB_KEGG);
            cell.setHyperlink(linkKeggDB);

            HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.HMDB.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.HMDB_HEADER);
            linkHMDBDB.setAddress(Constants.WEB_HMDB);
            cell.setHyperlink(linkHMDBDB);

            HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.LIPIDMAPS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.LIPIDMAPS_HEADER);
            linkLMDB.setAddress(Constants.WEB_LIPID_MAPS);
            cell.setHyperlink(linkLMDB);

            HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.METLIN.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.METLIN_HEADER);
            linkMETLINDB.setAddress(Constants.WEB_METLIN);
            cell.setHyperlink(linkMETLINDB);

            HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
            cell = getRow().createCell(CompoundColumns.PUBCHEM.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(Constants.PUBHCEMICAL_HEADER);
            linkPCDB.setAddress(Constants.WEB_PUBCHEMICHAL);
            cell.setHyperlink(linkPCDB);

            cell = getRow().createCell(CompoundColumns.IONIZATION_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.IONIZATION_SCORE_HEADER);

            // Deleted temporaly
            /*
            cell = getRow().createCell(CompoundColumns.PRECEDENCE_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PRECEDENCE_SCORE_HEADER);
             */
            cell = getRow().createCell(CompoundColumns.RETENTION_TIME_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.RETENTION_TIME_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.ADDUCT_RELATION_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.ADDUCT_RELATION_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.FINAL_SCORE.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.FINAL_SCORE_HEADER);

            cell = getRow().createCell(CompoundColumns.INCHIKEY.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.INCHIKEY_HEADER);

            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.SMILES_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constants.PATHWAYS_HEADER);

        }
    }

    /**
     * Generate the headline for CEMS Compounds
     *
     */
    protected void generateCompoundHeadLineCEMS() {
        HSSFCell cell = getRow().createCell(CEMSCompoundColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXPERIMENTAL_MASS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_EFFMOB_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_RMT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_MT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.COMPOUND_ID.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.COMPOUND_ID_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ADDUCT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.ADDUCT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.NAME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.NAME_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.FORMULA.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.FORMULA_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.MOL_WEIGHT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_PPM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.PPM_INCREMENT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_EFFMOB_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EFFMOB_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_RMT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.RMT_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_MT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.MT_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.CAS.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.CAS_HEADER);

        HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.KEGG.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.KEGG_HEADER);
        linkKeggDB.setAddress(Constants.WEB_KEGG);
        cell.setHyperlink(linkKeggDB);

        HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.HMDB.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.HMDB_HEADER);
        linkHMDBDB.setAddress(Constants.WEB_HMDB);
        cell.setHyperlink(linkHMDBDB);

        HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.LIPIDMAPS.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.LIPIDMAPS_HEADER);
        linkLMDB.setAddress(Constants.WEB_LIPID_MAPS);
        cell.setHyperlink(linkLMDB);

        HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.METLIN.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.METLIN_HEADER);
        linkMETLINDB.setAddress(Constants.WEB_METLIN);
        cell.setHyperlink(linkMETLINDB);

        HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.PUBCHEM.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.PUBHCEMICAL_HEADER);
        linkPCDB.setAddress(Constants.WEB_PUBCHEMICHAL);
        cell.setHyperlink(linkPCDB);

        HSSFHyperlink linkCHEBIDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.CHEBI.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.CHEBI_HEADER);
        linkPCDB.setAddress(Constants.WEB_CHEBI);
        cell.setHyperlink(linkCHEBIDB);

        cell = getRow().createCell(CEMSCompoundColumns.INCHIKEY.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.INCHIKEY_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.SMILES.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.SMILES_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.PATHWAYS.getnColumn() - 4, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.PATHWAYS_HEADER);

    }

    /**
     * Generate the headline for CEMS Compounds
     *
     */
    protected void generateCompoundHeadLineTargetedCEMS() {
        HSSFCell cell = getRow().createCell(CEMSCompoundColumns.EXPERIMENTAL_MASS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXPERIMENTAL_MASS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_EFFMOB_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_RMT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXP_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXP_MT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.COMPOUND_ID.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.COMPOUND_ID_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ADDUCT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.ADDUCT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.NAME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.NAME_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.FORMULA.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.FORMULA_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.MOL_WEIGHT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_PPM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.PPM_INCREMENT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_EFFMOB_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_EFFMOB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EFFMOB_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_RMT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_RMT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.RMT_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.STANDARD_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.STANDARD_MT_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.ERROR_MT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.MT_ERROR_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.EXPERIMENTAL_FRAGMENTS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.EXPERIMENTAL_FRAGMENTS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.FOUND_FRAGMENTS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.FOUND_FRAGMENTS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.NOT_FOUND_THEORETICAL_FRAGMENTS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.NOT_FOUND_THEORETICAL_FRAGMENTS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.NOT_FOUND_EXPERIMENTAL_FRAGMENTS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.NOT_FOUND_EXPERIMENTAL_FRAGMENTS_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.CAS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.CAS_HEADER);

        HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.KEGG.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.KEGG_HEADER);
        linkKeggDB.setAddress(Constants.WEB_KEGG);
        cell.setHyperlink(linkKeggDB);

        HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.HMDB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.HMDB_HEADER);
        linkHMDBDB.setAddress(Constants.WEB_HMDB);
        cell.setHyperlink(linkHMDBDB);

        HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.LIPIDMAPS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.LIPIDMAPS_HEADER);
        linkLMDB.setAddress(Constants.WEB_LIPID_MAPS);
        cell.setHyperlink(linkLMDB);

        HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.METLIN.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.METLIN_HEADER);
        linkMETLINDB.setAddress(Constants.WEB_METLIN);
        cell.setHyperlink(linkMETLINDB);

        HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.PUBCHEM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.PUBHCEMICAL_HEADER);
        linkPCDB.setAddress(Constants.WEB_PUBCHEMICHAL);
        cell.setHyperlink(linkPCDB);

        HSSFHyperlink linkCHEBIDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CEMSCompoundColumns.CHEBI.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.CHEBI_HEADER);
        linkPCDB.setAddress(Constants.WEB_CHEBI);
        cell.setHyperlink(linkCHEBIDB);

        cell = getRow().createCell(CEMSCompoundColumns.INCHIKEY.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.INCHIKEY_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.SMILES.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.SMILES_HEADER);

        cell = getRow().createCell(CEMSCompoundColumns.PATHWAYS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.PATHWAYS_HEADER);

    }

    /**
     * Generate the headline for browse Searches
     *
     */
    protected void generateCompoundHeadLineBrowse() {
        HSSFCell cell;
        cell = getRow().createCell(CompoundColumnsBrowseSearch.CAS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.CAS_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.COMPOUND_ID.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.COMPOUND_ID_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.MOL_WEIGHT_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.NAME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.NAME_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.FORMULA.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.FORMULA_HEADER);

        HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.KEGG.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.KEGG_HEADER);
        linkKeggDB.setAddress(Constants.WEB_KEGG);
        cell.setHyperlink(linkKeggDB);

        HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.HMDB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.HMDB_HEADER);
        linkHMDBDB.setAddress(Constants.WEB_HMDB);
        cell.setHyperlink(linkHMDBDB);

        HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.LIPIDMAPS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.LIPIDMAPS_HEADER);
        linkLMDB.setAddress(Constants.WEB_LIPID_MAPS);
        cell.setHyperlink(linkLMDB);

        HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.METLIN.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.METLIN_HEADER);
        linkMETLINDB.setAddress(Constants.WEB_METLIN);
        cell.setHyperlink(linkMETLINDB);

        HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.PUBCHEM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constants.PUBHCEMICAL_HEADER);
        linkPCDB.setAddress(Constants.WEB_PUBCHEMICHAL);
        cell.setHyperlink(linkPCDB);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.INCHIKEY.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.INCHIKEY_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.SMILES.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.SMILES_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.PATHWAYS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constants.PATHWAYS_HEADER);
    }

    protected abstract void generateCompoundDataLCMS(Object element, HSSFSheet sheet, int flag);

    protected abstract void generateCompoundDataBrowse(Object element, HSSFSheet sheet);

    protected abstract void generateCompoundDataCEMS(Object element, HSSFSheet sheet);

    protected abstract void generateCompoundDataTargetedCEMS(Object element, HSSFSheet sheet);

    protected abstract void showProcessCompoundProgress(Object element);

    /**
     * @return the initialTitle
     */
    public String getInitialTitle() {
        return initialTitle;
    }

    /**
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds
     */
    public final void setInitialTitle(int flag) {
        switch (flag) {
            case 0:
                this.initialTitle = Constants.TITLECOMPOUNDSEXCEL;
                break;
            case 1:
                this.initialTitle = Constants.TITLEMSCOMPOUNDSEXCEL;
                break;
            case 2:
                this.initialTitle = Constants.TITLELCMSCOMPOUNDSEXCEL;
                break;
            case 3:
                this.initialTitle = Constants.TITLECEMSCOMPOUNDSEXCEL;
                break;
            default:
                this.initialTitle = Constants.TITLECOMPOUNDSEXCEL;
                break;
        }
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public final void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse 3 for
     * CEMSCompounds
     */
    public final void setFileName(int flag) {
        switch (flag) {
            case 0:
                this.fileName = Constants.FILENAMECOMPOUNDSEXCEL;
                break;
            case 1:
                this.fileName = Constants.FILENAMEMSCOMPOUNDSEXCEL;
                break;
            case 2:
                this.fileName = Constants.FILENAMELCMSCOMPOUNDSEXCEL;
                break;
            case 3:
                this.fileName = Constants.FILENAMECEMSCOMPOUNDSEXCEL;
                break;
            case 4:
                this.fileName = Constants.FILENAMETARGETEDCEMSCOMPOUNDSEXCEL;
                break;
            default:
                this.fileName = Constants.FILENAMECOMPOUNDSEXCEL;
                break;
        }
    }

    /**
     * @return the realFileName
     */
    public String getRealFileName() {
        return realFileName;
    }

    /**
     * @param realFileName the realFileName to set
     */
    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    /**
     * @return the file
     */
    public FileOutputStream getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(FileOutputStream file) {
        this.file = file;
    }

    /**
     * @return the book
     */
    public HSSFWorkbook getBook() {
        return book;
    }

    /**
     * @param book the book to set
     */
    public void setBook(HSSFWorkbook book) {
        this.book = book;
    }

    /**
     * @return the row
     */
    public HSSFRow getRow() {
        return row;
    }

    /**
     * @param row the row to set
     */
    public void setRow(HSSFRow row) {
        this.row = row;
    }

    /**
     * @return the numberOfColumns
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * @param numberOfColumns the numberOfColumns to set
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * @return the hlink_style
     */
    public HSSFCellStyle getHlink_style() {
        return hlink_style;
    }

    /**
     * @param hlink_style the hlink_style to set
     */
    public void setHlink_style(HSSFCellStyle hlink_style) {
        this.hlink_style = hlink_style;
    }

    /**
     * @return the hlink_font
     */
    public HSSFFont getHlink_font() {
        return hlink_font;
    }

    /**
     * @param hlink_font the hlink_font to set
     */
    public void setHlink_font(HSSFFont hlink_font) {
        this.hlink_font = hlink_font;
    }

    /**
     * @return the adjustedLetter
     */
    public HSSFCellStyle getAdjustedLetter() {
        return adjustedLetter;
    }

    /**
     * @param adjustedLetter the adjustedLetter to set
     */
    public void setAdjustedLetter(HSSFCellStyle adjustedLetter) {
        this.adjustedLetter = adjustedLetter;
    }

    /**
     * @return the headline_style
     */
    public HSSFCellStyle getHeadline_style() {
        return headline_style;
    }

    /**
     * @param headline_style the headline_style to set
     */
    public void setHeadline_style(HSSFCellStyle headline_style) {
        this.headline_style = headline_style;
    }

    /**
     * @return the headline_font
     */
    public HSSFFont getHeadline_font() {
        return headline_font;
    }

    /**
     * @param headline_font the headline_font to set
     */
    public void setHeadline_font(HSSFFont headline_font) {
        this.headline_font = headline_font;
    }

    /**
     * @return the numerical_format
     */
    public DataFormat getNumerical_format() {
        return numerical_format;
    }

    /**
     * @param numerical_format the numerical_format to set
     */
    public void setNumerical_format(DataFormat numerical_format) {
        this.numerical_format = numerical_format;
    }

    /**
     * @return the numerical_style
     */
    public HSSFCellStyle getNumerical_style() {
        return numerical_style;
    }

    /**
     * @param numerical_style the numerical_style to set
     */
    public void setNumerical_style(HSSFCellStyle numerical_style) {
        this.numerical_style = numerical_style;
    }

    /**
     * @return the numerical_bold_style
     */
    public HSSFCellStyle getNumerical_bold_style() {
        return numerical_bold_style;
    }

    /**
     * @param numerical_bold_style the numerical_bold_style to set
     */
    public void setNumerical_bold_style(HSSFCellStyle numerical_bold_style) {
        this.numerical_bold_style = numerical_bold_style;
    }

    /**
     * @return the currentSheetNumber
     */
    public int getCurrentSheetNumber() {
        return currentSheetNumber;
    }

    /**
     * @param currentSheetNumber the currentSheetNumber to set
     */
    public void setCurrentSheetNumber(int currentSheetNumber) {
        this.currentSheetNumber = currentSheetNumber;
    }

    /**
     * @return the expected_style
     */
    public HSSFCellStyle getExpected_style() {
        return expected_style;
    }

    /**
     * @param expected_style the expected_style to set
     */
    public void setExpected_style(HSSFCellStyle expected_style) {
        this.expected_style = expected_style;
    }

    /**
     * @return the probable
     */
    public HSSFCellStyle getProbable_style() {
        return probable_style;
    }

    /**
     * @param probable_style the probable to set
     */
    public void setProbable_style(HSSFCellStyle probable_style) {
        this.probable_style = probable_style;
    }

    /**
     * @return the probable
     */
    public HSSFCellStyle getNot_probable_style() {
        return this.not_probable_style;
    }

    /**
     * @param not_probable_style the probable to set
     */
    public void setNot_probable_style(HSSFCellStyle not_probable_style) {
        this.not_probable_style = not_probable_style;
    }

    /**
     * @return the not_expected_style
     */
    public HSSFCellStyle getNot_expected_style() {
        return not_expected_style;
    }

    /**
     * @param not_expected_style the not_expected_style to set
     */
    public void setNot_expected_style(HSSFCellStyle not_expected_style) {
        this.not_expected_style = not_expected_style;
    }

}
