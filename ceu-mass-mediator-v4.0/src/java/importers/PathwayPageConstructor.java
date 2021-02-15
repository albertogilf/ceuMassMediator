package importers;

import facades.PathwaysFacade;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import static utilities.Constants.*;
import utilities.Constants;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class PathwayPageConstructor implements Serializable {

    private static final long serialVersionUID = 1L;

    // public List<Compound> rowList;
    public HSSFSheet theSheet;
    public int linea;
    public Map<String, Integer> columnsTable;
    public int flag;

    public List<Pathway> listPathways;
    // public Map<String, Integer> pathwaysTable;

    private int numRows;
    private int numHeaderColumns;

    public List<Pathway> getListPathways() {
        return listPathways;
    }

    public void setListPathways(List<Pathway> listPathways) {
        this.listPathways = listPathways;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumHeaderColumns() {
        return numHeaderColumns;
    }

    public void setNumHeaderColumns(int numHeaderColumns) {
        this.numHeaderColumns = numHeaderColumns;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private int loadHeaders() {
        Iterator<Row> rowIterator = theSheet.iterator();
        // Title row
        rowIterator.next();
        // Header Row
        Row headerRow = (Row) rowIterator.next();
        numHeaderColumns = headerRow.getPhysicalNumberOfCells();
        //System.out.println("\n Number of columns in header: " + numHeaderColumns);
        columnsTable = new <String, Integer> HashMap();
        int i = 0;
        int j = 0;
        while (j < numHeaderColumns) {
            Cell cell;
            cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                //System.out.println("\n: " + i + ": " + cell.getStringCellValue());
                columnsTable.put(cell.getStringCellValue(), j);
                j++;
            }
            i++;
        }
        if (!columnsTable.containsKey("Pathways")) {
            // Pathways column is not present
            return 4;
        } else if (columnsTable.get(Constants.PATHWAYS_HEADER) != numHeaderColumns - 1) {
            // Bad structure even Pathways column is present
            // Pathways column is not the last one
            return 8;
        }
        if (columnsTable.get(RT_HEADER) == null) {
            flag = 0;
        } else {
            flag = 1;
        }
        return 0;
    }

    private void loadPathways() {

        // Initialize Array of Pathways
        listPathways = new LinkedList<>();

        Iterator<Row> rowIterator = theSheet.iterator();
        // Skip title row and header rows
        rowIterator.next();
        rowIterator.next();

        // Create a list of Pathways
        // List<Hyperlink> listaTrueHyperlinks;
        // listaTrueHyperlinks = new LinkedList();
        List<String> pathwayCodes = new LinkedList();
        int countRow = 2;
        // System.out.println("NUM HEADER COLUMNS: " + numHeaderColumns);
        while (rowIterator.hasNext()) {
            Row row = (Row) rowIterator.next();
            int numColumns = row.getPhysicalNumberOfCells();
            // System.out.println("\n Row " + (countRow + 1) + " of " + numRows);
            // System.out.println("NUM COLUMNS: " + numColumns);
            if (numColumns >= numHeaderColumns) {
                List<String> valuesList = new ArrayList();
                for (int column = 0; column < numHeaderColumns - 1; column++) {
                    Cell cell;
                    cell = row.getCell(column);
                    String valueColumn;
                    // System.out.println("column" + column + " of " + (numHeaderColumns - 2));
                    try {
                        int cellType;
                        cellType = cell.getCellType();
                        switch (cellType) {
                            case 0:
                                if (column == columnsTable.get(COMPOUND_ID_HEADER)
                                        || (columnsTable.containsKey(PPM_INCREMENT_HEADER)
                                        && column == columnsTable.get(PPM_INCREMENT_HEADER))) {
                                    valueColumn = "" + (int) cell.getNumericCellValue();
                                } else {
                                    valueColumn = "" + cell.getNumericCellValue();
                                }
                                break;
                            case 1:
                                valueColumn = cell.getStringCellValue();
                                break;
                            default:
                                valueColumn = "";
                                break;
                        }
                    } catch (NullPointerException ex) {
                        valueColumn = "";
                    }
                    // System.out.println("CELL: " + valueColumn);
                    valuesList.add(valueColumn);
                    // System.out.println("\n Column " + column + " of " + (numHeaderColumns - 2) + ": " + valueColumn);
                }

                importers.CompoundForPathway comp = new CompoundForPathway(valuesList, columnsTable);
                //System.out.println("COMPOUND: " + comp.getIdentifier() + " INCHI: " + comp.getInchikey());

                for (int column = numColumns - 1; column >= numHeaderColumns - 1; column--) {
                    Cell cell;
                    cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    // System.out.println("\n Column " + column + " of " + numColumns + ": " + cell.getStringCellValue());
                    Hyperlink partialLink;
                    partialLink = cell.getHyperlink();
                    String pathwayCode = partialLink.getAddress();
                    pathwayCode = pathwayCode.substring(WEB_PATHWAY_KEGG.length());
                    String pathwayName = cell.getStringCellValue();
                    //System.out.println("\n: pathway: " + column + ": " + pathwayName
                    //       + "  address: " + partialLink.getAddress());
                    if (!pathwayCode.equals("") && !pathwayCodes.contains(pathwayCode)) {
                        // listaTrueHyperlinks.add(partialLink);
                        pathwayCodes.add(pathwayCode);
                        Pathway pathway = new Pathway(pathwayCode, pathwayName, partialLink);
                        // System.out.println("PathwayCode: " + pathwayCode);
                        pathway.compounds.add(comp);
                        listPathways.add(pathway);
                    } else if (!pathwayName.equals("")) {
                        Pathway pathway = getPathway(pathwayCode);
                        comp.addPathway(pathway);
                        pathway.compounds.add(comp);
                    }
                }
                // rowList.add(comp);
                countRow++;
            } else {
                // System.out.println("\n\n LESS COLUMNS THAN GENERAL COMPOUNDS \n\n");
            }

            // Sort the compounds in the pathway
            for (Pathway pathway : listPathways) {
                pathway.sortCompounds();
            }
        }
    }

    /**
     * The main function. It start loading the excel file, loading the columns
     * and pathway and act according two formats ceumass_compounds: (the
     * original one) that means that more or less, each pathway is going to be a
     * compound ceumass_pathway: Means that the first pathway is going to be a
     * pathway with the respective compounds (one or more rows) ErrorCodes: 12
     * File not read 8 Bad Structure 4 Bad structure 0 Correct read
     *
     * @param filename name of the file
     * @return error code or status ok (0)
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    public int start(String filename) throws SQLException, NamingException {
        // 
        int errorCode;
        try {
            // Empezamos abriendo el fichero

            FileInputStream file = new FileInputStream(new File(Constants.UPLOADPATH + File.separator + filename));

//Get the workbook instance for XLS file 
            HSSFWorkbook workbook = new HSSFWorkbook(file);

//Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            theSheet = sheet;
            // rowList = new ArrayList();
            numRows = theSheet.getPhysicalNumberOfRows();
            errorCode = loadHeaders();
            if (errorCode != 0) {
                // System.out.println("Bad structure: Column Header: " + columnsTable.get(Constants.PATHWAYS_HEADER)
                //        + " NUm Header Columns: " + numHeaderColumns);
                // loadPathways();
                // Collections.sort(listPathways);
                // Collections.reverse(listPathways);
                theSheet = null;
            } else {
                // System.out.println("Well Structure -> Column Header: " + columnsTable.get(Constants.PATHWAYS_HEADER)
                //        + " NUm Header Columns: " + numHeaderColumns);
                loadPathways();
                //String dsName = "java:comp/env/jdbc/testConnection";
                //DBManager dbm = new DBManager(dsName);
                PathwaysFacade pathwaysFacade = new PathwaysFacade();
                //
                //dbm.printPaths(listPathways);
                List<Pathway> sortedPathways = pathwaysFacade.orderByMaximum(listPathways);
                listPathways = sortedPathways;
                //dbm.printPaths(listPathways);

                // End Pathway Analysis

                /* PRINT COLLECTION OF PATHWAYS
                for(Pathway pathway : listPathways)
                {
                    System.out.println("\n pathway: " + pathway.name + " address: " + pathway.hyperl.getAddress());
                    for(CompoundForPathway compound : pathway.compounds)
                    {
                        System.out.println("\n id: " + compound.Identifier);
                    }
                    
                }
                 */
                pathwaysFacade.disconnect();
            }
            return errorCode;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PathwayPageConstructor.class.getName()).log(Level.SEVERE, null, ex);
            return 16;
        } catch (IOException ex) {
            Logger.getLogger(PathwayPageConstructor.class.getName()).log(Level.SEVERE, null, ex);
            return 20;
        }
    }

    private Pathway getPathway(String pathwayCode) {
        for (Pathway pathway : listPathways) {
            if (pathway.getCode().equals(pathwayCode)) {
                return pathway;
            }
        }
        return null;
    }

    /**
     * @return the number of pathways
     */
    public int numPathways() {
        return getListPathways().size();
    }

    /**
     * @return true if there are pathways and false is there is not
     */
    public boolean isTherePathways() {
        return getListPathways().size() > 0;
    }

    public boolean isThereFlag() {
        return flag != 0;
    }
}
