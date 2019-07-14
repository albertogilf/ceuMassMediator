/*
 * PathwaysFacade.java
 *
 * Created on 23-abr-2018, 13:29:15
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package facades;

import DBManager.DBManager;
import importers.CompoundForPathway;
import importers.Pathway;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 23-abr-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class PathwaysFacade {

    private static final DBManager DBM = new DBManager();
    private final Connection conn;

    /**
     * Creates a new instance of pathwaysFacade
     */
    public PathwaysFacade() {
        
        this.conn = PathwaysFacade.DBM.connect();
    }
    

    public void disconnect() {
        PathwaysFacade.DBM.disconnect();
    }

//count pathways that have an specific compound
    private Integer countPARTIALpathways(CompoundForPathway compound) {
        Integer n_pathways = -1;
        PreparedStatement stmt = null;
        //Statement stmt = null;
        ResultSet rs = null;
        try {

            //String sql = "SELECT COUNT(*) FROM compounds_pathways WHERE compound_id=" + compound.getIdentifier();
            //stmt = conn.createStatement();
            //rs = stmt.executeQuery(sql);
            String sql = "SELECT COUNT(*) FROM compounds_pathways WHERE compound_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(compound.getIdentifier()));
            rs = stmt.executeQuery();
            if (rs.next()) {
                n_pathways = rs.getInt(1);
                /*System.out.println("\n TOTAL NUMBER OF PATHWAYS THAT CONTAINS " +
                                compound.getName()+": " + n_pathways);*/
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return n_pathways;
    }

    //count the total number of compounds on the pathway
    private Integer countTOTALcompoundsOnAPathway(Pathway pathway) {

        Integer n_compounds = -1;
        //Statement stmt = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            //String sql = "SELECT pathway_id FROM pathways WHERE pathway_map= '" + pathway.getCode() + "'";
            //stmt = conn.createStatement();
            //rs = stmt.executeQuery(sql);
            String sql = "SELECT pathway_id FROM pathways WHERE pathway_map= ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, pathway.getCode());
            rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                rs.close();
                //sql = "SELECT COUNT(*) FROM compounds_pathways WHERE pathway_id=" + id;
                //rs = stmt.executeQuery(sql);

                sql = "SELECT COUNT(*) FROM compounds_pathways WHERE pathway_id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    n_compounds = rs.getInt(1);
                }
            }
            /*System.out.println("\n TOTAL NUMBER OF COMPOUNDS IN " +
                                pathway.getName()+": " + n_compounds);*/
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        return n_compounds;
    }

    private Integer countPARTIALcompoundsThatApear(List<CompoundForPathway> compoundslist) {
        return compoundslist.size();
        //excell
    }

    private boolean compoundExists(String id) {
        boolean exists = true;
        //Statement stmt = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //stmt = conn.createStatement();
            //String sql = "SELECT COUNT(*) FROM compounds WHERE compound_id=" + id;
            //rs = stmt.executeQuery(sql);

            String sql = "SELECT COUNT(*) FROM compounds WHERE compound_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(id));
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    exists = false;
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return exists;

    }
    //normalizing function paquete de Apache Commons Math disponible en http://commons.apache.org/math/
    //http://stackoverflow.com/questions/6353678/calculate-normal-distrubution-using-java
    //usa riemann http://stackoverflow.com/questions/27096743/how-to-calculate-area-under-a-normal-distribution-in-java

    public void printPaths(List<Pathway> listPathways) {

        //each pathway has a list of compounds
        //1) (compounds that appear) / (total compounds of the path)*100
        //2) (total number of pathways) / (number of paths where that compound appears)
        List<CompoundForPathway> compoundsList = new ArrayList<>();

        System.out.println("\n\n\n\n\n");
        for (Pathway pathway : listPathways) {

            //había pensado en sumar los dos criterios de ordenación y ordenar los numeros de mayor a menor. 
            //de esta forma, los paths con números más altos irían primero en la lista ordenada
            //para esto no debería crear una nueva clase? PathOrder que tenga como variales el id del path y el número obtenido para el criterio de ordenación?
            //una vez asignado un número para cada path se establecería el orden. 
            //ejecutaría un foreach con cada función para ir obteniendo los numeros?
            compoundsList = pathway.getCompounds();

            System.out.println("FOR THE PATHWAY " + pathway.getName() + " APPEARS " + pathway.getPercentage() + "% OF DE COMPOUNDS\n"
                    + "---THE MAXIMUM EXCLUSIVITY OF THE PATH IS=" + pathway.getMaxExclusivity() + "\n THE COMPOUNDS THAT APPEAR ARE:");
            TreeSet<String> idSet = new TreeSet<>();

            for (CompoundForPathway compound : compoundsList) {
                if (!idSet.contains(compound.getIdentifier()) && compoundExists(compound.getIdentifier())) {
                    idSet.add(compound.getIdentifier());

                    System.out.println("-----(" + compound.getIdentifier() + ") " + compound.getName());
                }
            }

        }

    }

    //import dbmanager in pathway class and call this method in the setter or the getter??
    private void maxExclusivityInAPath(Pathway pathway) {
        List<CompoundForPathway> compoundList = pathway.getCompounds();
        //excepcion pathway null try catch throw
        double maxExclusivity;
        double exclusivity;
        maxExclusivity = 0;
        //maxExclusivity= (double)(1/(double)countPARTIALpathways(compoundList.get(0)));
        for (CompoundForPathway compound : compoundList) {
            exclusivity = (double) (1 / (double) countPARTIALpathways(compound));
            if (exclusivity > maxExclusivity) {
                maxExclusivity = exclusivity;
            }
        }
        pathway.setMaxExclusivity(maxExclusivity);
    }

    private void setPercentages(List<Pathway> listPathways) {
        double percentage;
        for (Pathway pathway : listPathways) {
            percentage = (double) ((double) countPARTIALcompoundsThatApear(pathway.getCompounds()) / (double) countTOTALcompoundsOnAPathway(pathway)) * 100;
            pathway.setPercentage(percentage);
        }
    }

    private void setPercentagesAndMaxExclusivities(List<Pathway> listPathways) {
        for (Pathway Pathway : listPathways) {
            maxExclusivityInAPath(Pathway);
        }
        setPercentages(listPathways);
    }

    /**
     *
     * @param listPathways
     * @return
     */
    public List<Pathway> orderByMaximum(List<Pathway> listPathways) {
        List<Pathway> orderedList = new ArrayList<>();

        //set the percentages and maxexclusivities
        setPercentagesAndMaxExclusivities(listPathways);

        Set<Pathway> sortedSet = new TreeSet<>(new importers.ComparatorExclusivity());

        sortedSet.addAll(listPathways);

        //System.out.println("sortedsetsize: " + sortedSet.size());
        orderedList.addAll(sortedSet);
        //System.out.println("orderedlistsize: "+ orderedList.size());

        return orderedList;

    }

}
