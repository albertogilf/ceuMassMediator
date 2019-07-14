
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import utilities.Constants;

/**
 * Servlet to download in excel format the compounds returned by CMM
 * DEPRECATED. Not USED
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
@WebServlet(name = "DownloadCompoundsExcelServlet", urlPatterns = {"/downloadcompounds"})
public class DownloadCompoundsExcelServlet extends HttpServlet implements Constants {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment; filename=ceumass_compounds.xls");
        String fileName = UPLOADPATH + File.separator + "ExcelDownload" + (new Random()).nextInt() + ".xls";
        File file = new File(fileName);
        System.out.println("File:" + file);

    }
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
