package exporters;

import exporters.compoundsColumns.CompoundColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import persistence.NewPathways;
import persistence.theoreticalCompound.TheoreticalCompounds;
import utilities.Downloader;
import presentation.paginationHelpers.PaginationHelper;
import utilities.Constantes;

/**
 * Abstract Class for exporting the compounds into excel file
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
public abstract class ExcelExporter<T> {

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
        book = new HSSFWorkbook();
        ch = book.getCreationHelper();

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
     * param flag
     *
     * @param elements
     * @param ph. Pagination Helper
     * @param flag
     */
    public void generateWholeExcelCompound(List elements, PaginationHelper ph, int flag) {
        System.out.println("Exporting into Excel");

        try {
            setElFichero(new FileOutputStream(getRealFileName()));
            boolean hasNext = true;

            this.setCurrentSheetNumber(0);

            while (hasNext) {
                System.out.println("Generating sheet " + this.getCurrentSheetNumber());
                generateSheetCompound(elements, flag);

                hasNext = ph.isHasNextPage();
                ph.nextPage();
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
     * @param flag
     */
    public void generateSheetCompound(List elements, int flag) //AQUÍ, CLASE GENÉRICA
    {
        HSSFSheet sheet = book.createSheet();
        rowNumber = 0;

        // HEADER ROW
        setRow(sheet.createRow(rowNumber++));
        HSSFCell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(getHeadline_style());
        cell.setCellValue(getInitialTitle());
        setRow(sheet.createRow(rowNumber++));
        System.out.println("FLAG: " + flag);
        generateCompoundHeadLine(flag);
        //sheet.setColumnWidth(CompoundColumns.NAME.getnColumn(), 50);
        sheet.autoSizeColumn(CompoundColumns.NAME.getnColumn());
        //Se insertan los datos
        Iterator it = elements.iterator();

        while (it.hasNext()) {
            Object element = it.next();
            // showProcessCompoundProgress(element);
            generateCompoundData(element, sheet, flag);
            //setRow(sheet.createRow(rowNumber++)); // Se deja un espacio entre
            // cada compuesto empírico
        }

        //Se alinea el tamaño de las celdas al tamaño del texto
        for (int j = 0; j < getNumberOfColumns(); j++) {
            sheet.autoSizeColumn((short) j);
        }
    }

    public HSSFWorkbook generateWholeExcelPathway(String fout, List elements) {
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

            //Se alinea el tamaño de las celdas al tamaño del texto
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

    protected abstract void generateCompoundData(Object element, HSSFSheet sheet, int flag);

    protected abstract void showProcessCompoundProgress(Object element);

    protected abstract void generateCompoundHeadLine(int flag);

    public abstract void generatePathwayFileHeadLine(HSSFSheet sheet, int row);

    public abstract int generatePathwayFileData(Object element, HSSFSheet sheet, int row);

    public abstract void showProcessPathwayProgress(Object element);

    protected void generateRestOfMultipleValues(TheoreticalCompounds tc,
            HSSFSheet sheet, Iterator kp, int flag) {

        //int cellType = HSSFCell.CELL_TYPE_STRING;
        if (kp.hasNext()) {
            int count = 0;
            //setRow(sheet.createRow(rowNumber++));

            while (kp.hasNext()) {
                NewPathways p = ((Iterator<NewPathways>) kp).next();
                insertPathway(p, count, flag);
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
    protected void insertPathway(NewPathways p, int count, int flag) {
        if (flag == 1) {
            HSSFCell cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn() + count, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(p.getPathwayName());

            Hyperlink link = ch.createHyperlink(HyperlinkType.URL);
            link.setAddress(p.obtainPathwayWebPage());
            cell.setHyperlink(link);
        } else {
            HSSFCell cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn() + count - 1, CellType.STRING);
            cell.setCellStyle(getHlink_style());
            cell.setCellValue(p.getPathwayName());

            Hyperlink link = ch.createHyperlink(HyperlinkType.URL);
            link.setAddress(p.obtainPathwayWebPage());
            cell.setHyperlink(link);
        }
    }

    /**
     * Inserts into the cell a link of the kegg compunds
     *
     * @param p. The KeggPathway
     */
    /*
    protected void insertPathway(NewPathways p)
    {
        HSSFCell cell = getRow().createCell(CompoundColumns.PATHWAYS.getnColumn());
        cell.setCellStyle(getHlink_style());
        cell.setCellValue(p.getPathwayName());
        HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
        link.setAddress(p.obtainCompoundWebPage());
        cell.setHyperlink(link);
    }
     */
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
    public void setFileName(String fileName) {
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
