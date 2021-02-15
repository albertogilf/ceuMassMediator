import exporters.compoundsColumns.PathwayColumns;
import importers.Pathway;
import importers.PathwayPageConstructor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/*
import exporters.CompoundExcelExporter;
import exporters.ExcelExporter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
 */
import utilities.Constants;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */

/**
 * Servlet to download in excel format the compounds computed by pathway analyzer
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@WebServlet(name = "DownloadExcelServlet", urlPatterns = {"/download"})
public class DownloadExcelServlet extends HttpServlet implements Constants {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if row servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileName = UPLOADPATH + File.separator + "ExcelDownload" + (new Random()).nextInt() + ".xls";
        try {

            PathwayPageConstructor pageC = (PathwayPageConstructor) request.getSession().getAttribute("pagepathway");
            int flag = pageC.flag;
            WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
            workbook.createSheet("Sheet1", 0);
            WritableSheet newSheet = workbook.getSheet(0);

            WritableCellFormat we = new WritableCellFormat();
            WritableFont fwe = new WritableFont(WritableFont.ARIAL);
            fwe.setBoldStyle(WritableFont.BOLD);
            fwe.setPointSize(10);
            we.setFont(fwe);
            int row = 1;
            Label initialLabel = new Label(0, 0, FILENAMEPATHWAYSEXCEL, we);
            Label Column1 = new Label(PathwayColumns.PATHWAYS.getnColumn(), row, PATHWAYS_HEADER, we);
            Label Column2 = new Label(PathwayColumns.EXPERIMENTAL_MASS.getnColumn(), row, EXPERIMENTAL_MASS_HEADER, we);;
            Label Column3;
            Label Column4;
            Label Column5;
            Label Column6;
            Label Column7;
            Label Column8;
            Label Column9;
            Label Column10;
            Label Column11;
            Label Column12;
            Label Column13;
            Label Column14;
            Label Column15;
            Label Column16 = null;
            if (flag == 1) {
                Column3 = new Label(PathwayColumns.RETENTION_TIME.getnColumn(), row, RT_HEADER, we);
                Column4 = new Label(PathwayColumns.COMPOUND_ID.getnColumn(), row, COMPOUND_ID_HEADER, we);
                Column5 = new Label(PathwayColumns.ADDUCT.getnColumn(), row, ADDUCT_HEADER, we);
                Column6 = new Label(PathwayColumns.INCREMENT_PPM.getnColumn(), row, PPM_INCREMENT_HEADER, we);
                Column7 = new Label(PathwayColumns.MOLECULAR_WEIGHT.getnColumn(), row, MOL_WEIGHT_HEADER, we);
                Column8 = new Label(PathwayColumns.NAME.getnColumn(), row, NAME_HEADER, we);
                Column9 = new Label(PathwayColumns.FORMULA.getnColumn(), row, FORMULA_HEADER, we);
                Column10 = new Label(PathwayColumns.CAS.getnColumn(), row, CAS_HEADER, we);
                Column11 = new Label(PathwayColumns.KEGG.getnColumn(), row, KEGG_HEADER, we);
                Column12 = new Label(PathwayColumns.HMDB.getnColumn(), row, HMDB_HEADER, we);
                Column13 = new Label(PathwayColumns.LIPIDMAPS.getnColumn(), row, LIPIDMAPS_HEADER, we);
                Column14 = new Label(PathwayColumns.METLIN.getnColumn(), row, METLIN_HEADER, we);
                Column15 = new Label(PathwayColumns.PUBCHEM.getnColumn(), row, PUBHCEMICAL_HEADER, we);
                Column16 = new Label(PathwayColumns.INCHIKEY.getnColumn(), row, INCHIKEY_HEADER, we);
            } else {
                Column3 = new Label(PathwayColumns.COMPOUND_ID.getnColumn() - 1, row, COMPOUND_ID_HEADER, we);
                Column4 = new Label(PathwayColumns.ADDUCT.getnColumn() - 1, row, ADDUCT_HEADER, we);
                Column5 = new Label(PathwayColumns.INCREMENT_PPM.getnColumn() - 1, row, PPM_INCREMENT_HEADER, we);
                Column6 = new Label(PathwayColumns.MOLECULAR_WEIGHT.getnColumn() - 1, row, MOL_WEIGHT_HEADER, we);
                Column7 = new Label(PathwayColumns.NAME.getnColumn() - 1, row, NAME_HEADER, we);
                Column8 = new Label(PathwayColumns.FORMULA.getnColumn() - 1, row, FORMULA_HEADER, we);
                Column9 = new Label(PathwayColumns.CAS.getnColumn() - 1, row, CAS_HEADER, we);
                Column10 = new Label(PathwayColumns.KEGG.getnColumn() - 1, row, KEGG_HEADER, we);
                Column11 = new Label(PathwayColumns.HMDB.getnColumn() - 1, row, HMDB_HEADER, we);
                Column12 = new Label(PathwayColumns.LIPIDMAPS.getnColumn() - 1, row, LIPIDMAPS_HEADER, we);
                Column13 = new Label(PathwayColumns.METLIN.getnColumn() - 1, row, METLIN_HEADER, we);
                Column14 = new Label(PathwayColumns.PUBCHEM.getnColumn() - 1, row, PUBHCEMICAL_HEADER, we);
                Column15 = new Label(PathwayColumns.INCHIKEY.getnColumn() - 1, row, INCHIKEY_HEADER, we);
            }
            // cabeceras
            newSheet.addCell(initialLabel);
            newSheet.addCell(Column1);
            newSheet.addCell(Column2);
            newSheet.addCell(Column3);
            newSheet.addCell(Column4);
            newSheet.addCell(Column5);
            newSheet.addCell(Column6);
            newSheet.addCell(Column7);
            newSheet.addCell(Column8);
            newSheet.addCell(Column9);
            newSheet.addCell(Column10);
            newSheet.addCell(Column11);
            newSheet.addCell(Column12);
            newSheet.addCell(Column13);
            newSheet.addCell(Column14);
            newSheet.addCell(Column15);
            if (flag == 1) {
                newSheet.addCell(Column16);
            }

            CellView autoSizeCellView = new CellView();
            autoSizeCellView.setAutosize(true);

            // tamaños
            newSheet.setColumnView(0, autoSizeCellView);
            newSheet.setColumnView(1, autoSizeCellView);
            newSheet.setColumnView(2, autoSizeCellView);
            newSheet.setColumnView(3, autoSizeCellView);
            newSheet.setColumnView(4, autoSizeCellView);
            newSheet.setColumnView(5, autoSizeCellView);
            newSheet.setColumnView(6, autoSizeCellView);
            newSheet.setColumnView(7, autoSizeCellView);
            newSheet.setColumnView(8, autoSizeCellView);
            newSheet.setColumnView(9, autoSizeCellView);
            newSheet.setColumnView(10, autoSizeCellView);
            newSheet.setColumnView(11, autoSizeCellView);
            newSheet.setColumnView(12, autoSizeCellView);
            newSheet.setColumnView(13, autoSizeCellView);
            newSheet.setColumnView(14, autoSizeCellView);
            newSheet.setColumnView(15, autoSizeCellView);
            if (flag == 1) {
                newSheet.setColumnView(16, autoSizeCellView);
            }

//Get the workbook instance for XLS file 
            List<Pathway> pathwayCompounds = pageC.listPathways;
            /*
            for (int i=0;i<pathwayCompounds.size();i++) {
                    Pathway pathway=pathwayCompounds.get(i);
                    System.out.println("\n pathway: " + pathway.name + " address: " + pathway.hyperl.getAddress());
                    System.out.println("Num compounds" + pathway.compounds.size());
                    
                    for (CompoundForPathway compound : pathway.compounds) {
                        System.out.println("\n id: " + compound.identifier);
                    }

                }
            Collections.reverse(pathwayCompounds);
            */
            row = 2;

            // los pathways
            for (int i = 0 ; i < pathwayCompounds.size(); i++) {
                importers.Pathway pathway = pathwayCompounds.get(i);
                WritableHyperlink hl = new WritableHyperlink(0, row, new URL(pathway.hyperl.getAddress()));
                hl.setDescription(pathway.name);
                newSheet.addHyperlink(hl);

                // compounds
                List<importers.CompoundForPathway> compounds = pathway.getCompounds();
                for (importers.CompoundForPathway compound : compounds) {

                    compound.toExcel(newSheet, row, flag);
                    row++;
                }
            }

            workbook.write();
            workbook.close();

            System.out.println(" Pathway Excel exported");

        } catch (IOException | IndexOutOfBoundsException | WriteException e) {
            e.printStackTrace(System.err);
        }

        // download
        ServletOutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(fileName);

        response.setContentType("application/vnd.ms-excel");
        response.addHeader("content-disposition",
                "attachment; filename=" + FILENAMEPATHWAYSEXCEL);

        int octet;
        while ((octet = in.read()) != -1) {
            out.write(octet);
        }

        in.close();
        out.close();

        // delete the file in the server once the user has downloaded it
        new File(fileName).delete();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if row servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if row servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns row short description of the servlet.
     *
     * @return row String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
