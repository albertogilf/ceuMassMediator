package exporters;

import exporters.compoundsColumns.CompoundColumns;
import exporters.compoundsColumns.CompoundColumnsBrowseSearch;
import exporters.compoundsColumns.PathwayColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import utilities.Constantes;
import static utilities.Constantes.ITEMS_PER_PAGE_IN_EXCEL;

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
    private FileOutputStream elFichero;
    protected short rowNumber;
    private HSSFWorkbook book;
    HSSFCreationHelper ch;
    private HSSFRow row;
    //private HSSFCell cell;
    private int numberOfColumns;
    private HSSFCellStyle hlink_style;
    private HSSFFont hlink_font;
    private HSSFCellStyle letrajusta;
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
     * @param fileName
     */
    public ExcelExporter(String fileName) {
        this.initialTitle = Constantes.COMPOUNDSEXCELFILETITLE;
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

        // Crea el estilo letrajusta
        letrajusta = book.createCellStyle();
        //alineado a la izquierda y justificado vertical
        letrajusta.setAlignment(HorizontalAlignment.LEFT);
        letrajusta.setVerticalAlignment(VerticalAlignment.JUSTIFY);

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
     * param elements. Flag indicates if elements contains RT or they do not
     * Flag may be 0 1 or 2
     *
     * @param elements
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse
     * Search
     */
    public void generateWholeExcelCompound(List elements, int flag) {
        System.out.println("Exporting into Excel");
        this.ph = new MyPaginationHelper(ITEMS_PER_PAGE_IN_EXCEL, elements.size()
        );
        try {
            setElFichero(new FileOutputStream(getRealFileName()));
            boolean hasNext = true;

            this.setCurrentSheetNumber(0);

            while (hasNext) {
                generateSheetCompound(elements, flag);

                hasNext = this.ph.isHasNextPage();
                this.ph.nextPage();
                this.setCurrentSheetNumber(this.getCurrentSheetNumber() + 1);
            }
            getBook().write(getElFichero());
            getElFichero().close();

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
     * @param flag 0 for LCMS without RT, 1 for LCMS with RT. 2 for Browse
     * Search
     */
    public void generateSheetCompound(List elements, int flag) {
        HSSFSheet sheet = book.createSheet();
        rowNumber = 0;

        // HEADER ROW
        setRow(sheet.createRow(rowNumber++));
        HSSFCell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(getInitialTitle());
        setRow(sheet.createRow(rowNumber++));
        System.out.println("FLAG: " + flag);

        if (flag == 2) {
            //sheet.setColumnWidth(CompoundColumnsBrowseSearch.NAME.getnColumn(), 50);
            sheet.autoSizeColumn(CompoundColumnsBrowseSearch.NAME.getnColumn());
            generateCompoundHeadLineBrowse();
            // Generate excel file for browse search
            elements.forEach((element) -> {
                // showProcessCompoundProgress(element);
                generateCompoundDataBrowse(element, sheet);
            });

        } else if (flag == 0 || flag == 1) {
            //sheet.setColumnWidth(CompoundColumns.NAME.getnColumn(), 50);
            sheet.autoSizeColumn(CompoundColumns.NAME.getnColumn());
            generateCompoundHeadLineLCMS(flag);
            elements.forEach((element) -> {
                // showProcessCompoundProgress(element);
                generateCompoundDataLCMS(element, sheet, flag);
            });
        }

        //Align cell size to text size
        for (int j = 0; j < getNumberOfColumns(); j++) {
            sheet.autoSizeColumn((short) j);
        }
    }

    /**
     * @deprecated Not used
     * @param fout
     * @param elements
     * @return
     */
    public HSSFWorkbook generatesWholeExcelPathway(String fout, List elements) {
        System.out.println("Exporting compounds sorted by pathways into Excel");

        HSSFWorkbook workbookPathway;
        workbookPathway = new HSSFWorkbook();
        FileOutputStream foutStream = null;
        try {
            foutStream = new FileOutputStream(new File(fout));

            int numberSheet = 0;
            int rowCount = 0;
            boolean hasNext = true;

            HSSFSheet sheetPathway = workbookPathway.createSheet("Compounds from pathways");
            System.out.println("Generating sheet " + numberSheet);
            HSSFRow rowPathway = sheetPathway.createRow(rowCount++);

            HSSFCell cell = rowPathway.createCell(0, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(getInitialTitle());
            generatePathwayFileHeadLine(sheetPathway, rowCount);
            rowCount++;

            Iterator it = elements.iterator();

            while (it.hasNext()) {
                Object element = it.next();
                showProcessPathwayProgress(element);

                int rowsPathway = generatePathwayFileData(element, sheetPathway, rowCount);

                rowCount = rowCount + rowsPathway;
            }

            //Align cell size to text size
            for (int j = 0; j < getNumberOfColumns(); j++) {
                sheetPathway.autoSizeColumn((short) j);
            }

            workbookPathway.write(foutStream);
            // (new Downloader()).download(fout, fout);
            System.out.println(" Pathway Excel exported");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if (foutStream != null) {
                try {
                    foutStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelExporter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return workbookPathway;
    }

    /**
     * Generate the headline if flag is 0 there is no RT, if flag is 1 there is
     * RT
     *
     * @param flag 0 without RT, 1 with RT
     */
    protected void generateCompoundHeadLineLCMS(int flag) {
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

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.COMPOUND_ID_HEADER);

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
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.SMILES_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn(), CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PATHWAYS_HEADER);

        } else {

            cell = getRow().createCell(CompoundColumns.CAS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.CAS_HEADER);

            cell = getRow().createCell(CompoundColumns.COMPOUND_ID.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.COMPOUND_ID_HEADER);

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
            
            cell = getRow().createCell(CompoundColumns.SMILES.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.SMILES_HEADER);

            cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn() - 1, CellType.STRING);
            cell.setCellStyle(getHeadline_style());
            cell.setCellValue(Constantes.PATHWAYS_HEADER);

        }
    }

    /**
     * Generate the headline for browse Searches
     *
     */
    protected void generateCompoundHeadLineBrowse() {
        HSSFCell cell;
        cell = getRow().createCell(CompoundColumnsBrowseSearch.CAS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.CAS_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.COMPOUND_ID.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.COMPOUND_ID_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.MOLECULAR_WEIGHT.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.MOL_WEIGHT_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.NAME.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.NAME_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.FORMULA.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.FORMULA_HEADER);

        HSSFHyperlink linkKeggDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.KEGG.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.KEGG_HEADER);
        linkKeggDB.setAddress(Constantes.WEB_KEGG);
        cell.setHyperlink(linkKeggDB);

        HSSFHyperlink linkHMDBDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.HMDB.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.HMDB_HEADER);
        linkHMDBDB.setAddress(Constantes.WEB_HMDB);
        cell.setHyperlink(linkHMDBDB);

        HSSFHyperlink linkLMDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.LIPIDMAPS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.LIPIDMAPS_HEADER);
        linkLMDB.setAddress(Constantes.WEB_LIPID_MAPS);
        cell.setHyperlink(linkLMDB);

        HSSFHyperlink linkMETLINDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.METLIN.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.METLIN_HEADER);
        linkMETLINDB.setAddress(Constantes.WEB_METLIN);
        cell.setHyperlink(linkMETLINDB);

        HSSFHyperlink linkPCDB = ch.createHyperlink(HyperlinkType.URL);
        cell = getRow().createCell(CompoundColumnsBrowseSearch.PUBCHEM.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(Constantes.PUBHCEMICAL_HEADER);
        linkPCDB.setAddress(Constantes.WEB_PUBCHEMICHAL);
        cell.setHyperlink(linkPCDB);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.INCHIKEY.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.INCHIKEY_HEADER);
                
        cell = getRow().createCell(CompoundColumnsBrowseSearch.SMILES.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.SMILES_HEADER);

        cell = getRow().createCell(CompoundColumnsBrowseSearch.PATHWAYS.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.PATHWAYS_HEADER);

    }

    /**
     * Generate the pathways for exporting after the pathway analysis
     *
     * @deprecated
     * @param element
     * @param sheet
     * @param numRow
     * @return the number of rows generated
     */
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

            cell = rowPathway.createCell(PathwayColumns.COMPOUND_ID.getnColumn(), CellType.NUMERIC);
            cell.setCellValue(compound.compound_id);

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
     * Shows the progress of the pathway exported
     *
     * @deprecated
     * @param element
     */
    public void showProcessPathwayProgress(Object element) {
        importers.Pathway ec = (importers.Pathway) element;
//        System.out.println("Inserting pathway with name " + ec.name);
    }

    /**
     * Generate the header for the pathway analysis being exported
     *
     * @deprecated
     * @param sheetPathway
     * @param rowNumber
     */
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

        cell = rowHeaderPathway.createCell(PathwayColumns.COMPOUND_ID.getnColumn(), CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(Constantes.COMPOUND_ID_HEADER);

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

    protected abstract void generateCompoundDataLCMS(Object element, HSSFSheet sheet, int flag);

    protected abstract void generateCompoundDataBrowse(Object element, HSSFSheet sheet);

    protected abstract void showProcessCompoundProgress(Object element);

    /**
     * @return the initialTitle
     */
    public String getInitialTitle() {
        return initialTitle;
    }

    /**
     * @param initialTitle the initialTitle to set
     */
    public void setInitialTitle(String initialTitle) {
        this.initialTitle = initialTitle;
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
     * @return the elFichero
     */
    public FileOutputStream getElFichero() {
        return elFichero;
    }

    /**
     * @param elFichero the elFichero to set
     */
    public void setElFichero(FileOutputStream elFichero) {
        this.elFichero = elFichero;
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
     * @return the letrajusta
     */
    public HSSFCellStyle getLetrajusta() {
        return letrajusta;
    }

    /**
     * @param letrajusta the letrajusta to set
     */
    public void setLetrajusta(HSSFCellStyle letrajusta) {
        this.letrajusta = letrajusta;
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
