/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeTest;

import facades.MSFacade;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelReader {

    public static final String filename = "C:\\Users\\María 606888798\\Documents\\CEU\\testmaru.csv";

    public static List<Double> getMassesFromCSV() throws IOException {

        FileInputStream fis = null;
        List<Double> masses = new LinkedList<>();

         try {

            File file = new File(filename);
            //File file = new File("C:\\Users\\ceu\\Desktop\\fichero.txt");
            //File file = new File("/home/alberto/Desktop/fichero.txt");

            FileReader fr;
            BufferedReader bf;
            fr = new FileReader(file);
            bf = new BufferedReader(fr);
            String line;
            while ((line = bf.readLine()) != null) {
                System.out.println("LINE: " + line);
                line = line.replace(",", ".");
                masses.add(Double.parseDouble(line));
            }
            fr.close();
            bf.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return masses;
    }

}
