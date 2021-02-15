
import importers.PathwayPageConstructor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import utilities.Constants;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
@WebServlet("/FileUploadServlet")
@MultipartConfig
public class FileUploadServlet extends HttpServlet implements Constants {

    private final static Logger LOGGER = Logger.getLogger(FileUploadServlet.class.getCanonicalName());

    /**
     * The main statement that should be reflected is that we have to save the
     * client's file in the server and later, read it and do the view.
     *
     *
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Upload the file in our server
        final String path = UPLOADPATH; // Here, the path in the server!
        final Part filePart = request.getPart("uploadFile");
        final String fileName = getFileName(filePart) + (new Random()).nextInt();

        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();
        try {

            out = new FileOutputStream(new File(path + File.separator + fileName));
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            writer.println("New file " + fileName + " created at " + path);
            LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", new Object[]{fileName, path});
            // if the load is complete, we pass to the view. we pass also the name of the file. 

            // constuct the items for the jsf
            PathwayPageConstructor pageC = new PathwayPageConstructor();
            int codeLoad = pageC.start(fileName);

            // go to the page
            if (codeLoad!=0) {
                response.sendRedirect("Error.xhtml");
            } else {
                // add atribute 
                request.getSession().setAttribute("pagepathway", pageC);
                response.sendRedirect("ExcelImporter.xhtml");
            }
        } catch (FileNotFoundException fne) {
            writer.println("You did not specify a file to upload or you are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            writer.println("<br/> ERROR: " + fne.getMessage());

            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});

        } catch (SQLException | NamingException ex) {
            Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
            //borramos
            new File(path + File.separator + fileName).delete();
        }

    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
