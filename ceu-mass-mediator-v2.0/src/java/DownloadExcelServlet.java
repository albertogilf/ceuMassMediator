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
import utilities.Constantes;

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
public class DownloadExcelServlet extends HttpServlet implements Constantes {

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
            WritableSheet nuevoSheet = workbook.getSheet(0);

            WritableCellFormat we = new WritableCellFormat();
            WritableFont fwe = new WritableFont(WritableFont.ARIAL);
            fwe.setBoldStyle(WritableFont.BOLD);
            fwe.setPointSize(10);
            we.setFont(fwe);
            int row = 1;
            Label EtiquetaInicial = new Label(0, 0, PATHWAYEXCELFILETITLE, we);
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
                Column4 = new Label(PathwayColumns.IDENTIFIER.getnColumn(), row, IDENTIFIER_HEADER, we);
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
                Column3 = new Label(PathwayColumns.IDENTIFIER.getnColumn() - 1, row, IDENTIFIER_HEADER, we);
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
            nuevoSheet.addCell(EtiquetaInicial);
            nuevoSheet.addCell(Column1);
            nuevoSheet.addCell(Column2);
            nuevoSheet.addCell(Column3);
            nuevoSheet.addCell(Column4);
            nuevoSheet.addCell(Column5);
            nuevoSheet.addCell(Column6);
            nuevoSheet.addCell(Column7);
            nuevoSheet.addCell(Column8);
            nuevoSheet.addCell(Column9);
            nuevoSheet.addCell(Column10);
            nuevoSheet.addCell(Column11);
            nuevoSheet.addCell(Column12);
            nuevoSheet.addCell(Column13);
            nuevoSheet.addCell(Column14);
            nuevoSheet.addCell(Column15);
            if (flag == 1) {
                nuevoSheet.addCell(Column16);
            }

            CellView autoSizeCellView = new CellView();
            autoSizeCellView.setAutosize(true);

            // tamaños
            nuevoSheet.setColumnView(0, autoSizeCellView);
            nuevoSheet.setColumnView(1, autoSizeCellView);
            nuevoSheet.setColumnView(2, autoSizeCellView);
            nuevoSheet.setColumnView(3, autoSizeCellView);
            nuevoSheet.setColumnView(4, autoSizeCellView);
            nuevoSheet.setColumnView(5, autoSizeCellView);
            nuevoSheet.setColumnView(6, autoSizeCellView);
            nuevoSheet.setColumnView(7, autoSizeCellView);
            nuevoSheet.setColumnView(8, autoSizeCellView);
            nuevoSheet.setColumnView(9, autoSizeCellView);
            nuevoSheet.setColumnView(10, autoSizeCellView);
            nuevoSheet.setColumnView(11, autoSizeCellView);
            nuevoSheet.setColumnView(12, autoSizeCellView);
            nuevoSheet.setColumnView(13, autoSizeCellView);
            nuevoSheet.setColumnView(14, autoSizeCellView);
            nuevoSheet.setColumnView(15, autoSizeCellView);
            if (flag == 1) {
                nuevoSheet.setColumnView(16, autoSizeCellView);
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
                nuevoSheet.addHyperlink(hl);

                // compounds
                List<importers.CompoundForPathway> compounds = pathway.getCompounds();
                for (importers.CompoundForPathway compound : compounds) {

                    compound.toExcel(nuevoSheet, row, flag);
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
                "attachment; filename=" + "ceumass_pathways.xls");

        int octet;
        while ((octet = in.read()) != -1) {
            out.write(octet);
        }

        in.close();
        out.close();

        //borramos el fichero para no guardar inecesarios
        new File(fileName).delete();
    }

    /*
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ExcelExporter exporter = new CompoundExcelExporter();
        String fileName = UPLOADPATH + File.separator + "ExcelDownload" + (new Random()).nextInt() + ".xls";
        try {

            // Create the workbook
            HSSFWorkbook workbookPathway;
            workbookPathway = new HSSFWorkbook();

//Get the workbook instance for XLS file 
            PathwayPageConstructor pageC = (PathwayPageConstructor) request.getSession().getAttribute("pagepathway");

            List<Pathway> pathwayCompounds = pageC.listPathways;

            workbookPathway = exporter.generateWholeExcelPathway(fileName, pathwayCompounds);
//            System.out.println(workbookPathway);
            System.out.println(" Pathway Excel exported");

            ServletOutputStream out = response.getOutputStream();

            response.setContentType("application/vnd.ms-excel");
            response.addHeader("content-disposition",
                    "attachment; filename=" + "ceumass_pathways.xls");
            
            int octet;
            while ((octet = in.read()) != -1) {
                out.write(octet);
            }
             
            workbookPathway.write(out);
            out.close();

            //borramos el fichero para no guardar inecesarios
            new File(fileName).delete();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        // download
    }
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
