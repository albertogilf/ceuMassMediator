/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author María 606888798
 */
public class MsmsReader {

    private static String filePath = "C:\\Users\\María 606888798\\Desktop\\CMM\\ToTestDifferent.txt";
    private static List<Msms> myMsmsList = new LinkedList<>();

    public static List<Msms> loadMsmsToTest() {
        
        double parentIonMZ=0;
        int ionizationMode=0;
        String ionMode="";
        int voltage=0;
        String voltageLevel="";
        List<Peak> peaks=new LinkedList();
        int spectraType=2;
        //maria borra
        String name="";
        String instrument_type="";
        String adduct="";
        
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader bf = new BufferedReader(fr);
            String line;
            Msms msms = null;
            while ((line = bf.readLine()) != null) {
                //System.out.println(line);CH$NAME:
                if (line.contains("CH$NAME")) {
                    int start = 0;
                    if (line.contains(": ")) {
                        start = line.indexOf(": ") + 2;//position plus the space plus the initial position of the info
                    } else if (line.contains(":")) {
                        start = line.indexOf(":") + 1;
                    }
                    name=line.substring(start);
                }
                if (line.contains("AC$INSTRUMENT_TYPE")) {
                    int start = 0;
                    if (line.contains(": ")) {
                        start = line.indexOf(": ") + 2;//position plus the space plus the initial position of the info
                    } else if (line.contains(":")) {
                        start = line.indexOf(":") + 1;
                    }
                   instrument_type=(line.substring(start));
                }
                if (line.contains("AC$MASS_SPECTROMETRY: ION_MODE")) {
                    int start = 0;
                    if (line.contains("MODE ")) {
                        start = line.indexOf("MODE ") + 5;//position plus the space plus the initial position of the info
                    }
                    ionMode=(line.substring(start));
                }
                if (line.contains("AC$MASS_SPECTROMETRY: COLLISION_ENERGY")) {
                    int start = 0;
                    if (line.contains("ENERGY ")) {
                        start = line.indexOf("ENERGY ") + 7;//position plus the space plus the initial position of the infoAC$MASS_SPECTROMETRY: COLLISION_ENERGY
                    }
                    voltageLevel=(line.substring(start));
                }
                if (line.contains("PRECURSOR_M/Z")) {
                    int start = 0;
                    if (line.contains("Z ")) {
                        start = line.indexOf("Z ") + 2;//position plus the space plus the initial position of the info
                    } else if (line.contains("Z")) {
                        start = line.indexOf("Z") + 1;
                    }
                    parentIonMZ=(Double.parseDouble(line.substring(start)));
                    //System.out.println(msms.getPrecursor());
                }
                if (line.contains("PRECURSOR_TYPE")) {
                    int start = 0;
                    //int end=0;
                    if (line.contains("[")) {
                        start = line.indexOf("[");
                        
                    }
                    adduct=(line.substring(start));
                   // if (adduct.contains(line))
                    //System.out.println(adduct);
                    //System.out.println(line.substring(start));
                }
                if (line.contains("PK$NUM_PEAK")) {
                    int start = 0;
                    if (line.contains(": ")) {
                        start = line.indexOf(": ") + 2;//position plus the space plus the initial position of the info
                    } else if (line.contains(":")) {
                        start = line.indexOf(":") + 1;
                    }
                    
                }
                if (line.contains("PK$PEAK: m/z int. rel.int.")) {
                    peaks= new LinkedList<>();
                        if ((line = bf.readLine()) != null) {
                            if (line.contains("----")) {
                                break;
                            }
                            String[] lineValues = line.split(" ");
                            //for (String lineValue : lineValues) {
                            //   System.out.println("<"+lineValue+">");
                            //}
                            //positions 2 and 3 since there are many spaces when starting
                            //first we have mz
                            //then we have intensity
                            double mz = 0;
                            double intensity = 0;
                            for (int j = 0; j < lineValues.length - 1; j++) {
                                if (!lineValues[j].isEmpty()) {
                                    mz = Double.parseDouble(lineValues[j]);
                                    //System.out.println(mz);
                                    j++;
                                    intensity = Double.parseDouble(lineValues[j]);
                                    //System.out.println(intensity);

                                    j++;
                                }
                            }
                            peaks.add(new Peak(mz, intensity));
                        }
                    

                }
                if(ionMode.equalsIgnoreCase("positive")){ionizationMode=1;}
                if(ionMode.equalsIgnoreCase("negative")){ionizationMode=2;}
                msms= new Msms (parentIonMZ, ionizationMode, voltage,"all", peaks, spectraType);
                msms.setName(name);
                if (line.contains("----")) {
                    //System.out.println("Precursor type:"+msms.getPrecursor_type());
                    myMsmsList.add(msms);
                    //refresh object
                    msms = null;
                }

            }
            return myMsmsList;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MsmsReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MsmsReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

}
