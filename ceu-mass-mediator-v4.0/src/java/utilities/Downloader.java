/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Abstract Class for exporting the compounds into excel file
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
public class Downloader {

    /**
     * Method that allows download the excel file in a computer.
     *
     * @param fileName
     * @param realFileName
     * @throws IOException
     */
    public void download(String fileName, String realFileName) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        String contentType = ec.getMimeType(realFileName);
        // System.out.println("FileName: " + realFileName + " contentType: " + contentType);
        // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.responseReset();

        // Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        ec.setResponseContentType(contentType);
        File file = new File(realFileName);
        long contentLength = file.length();
        // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseContentLength((int) contentLength);
        // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            OutputStream os = ec.getResponseOutputStream();
            // Now you can write the InputStream of the file to the above OutputStream the usual way.
            // ...
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = fis.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            fis.close();
            fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
        }
        catch (IOException ex)
        {
            
        }
        finally{
            if(fis !=null)
            {
                try{
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
