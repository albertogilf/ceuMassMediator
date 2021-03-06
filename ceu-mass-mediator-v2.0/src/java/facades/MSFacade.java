/*
 * MSFacade.java
 *
 * Created on 30-abr-2018, 11:21:51
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package facades;

import DBManager.DBManager;
import DBManager.QueryConstructor;
import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.Feature;
import compound.CMMCompound;
import compound.Chain;
import compound.Classyfire_Classification;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import msms.MSMSCompound;
import msms.Peak;
import pathway.Pathway;
import utilities.AdductProcessing;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import utilities.Utilities;
import static utilities.Utilities.escapeSQLForREGEXP;

/**
 * MSFacade. Middleware to connect the CMM LCMS application with the database
 *
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1 30-abr-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class MSFacade implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DBManager DBM = new DBManager();
    private final Connection conn;

    public MSFacade() {
        this.conn = MSFacade.DBM.connect();
    }

    public void disconnect() {
        MSFacade.DBM.disconnect();
    }

    /**
     * Get the compound annotation from the experimental databases. It is
     * obtained by the feature experimental modified according an adduct within
     * a tolerance. Depending on the databases selected, it will search in the
     * corresponding ones applying the filters corresponding to the masses,
     * tolerances, metaboliteTypes and chemAlphabet
     *
     * @param feature
     * @param EM
     * @param adduct
     * @param tolerance
     * @param toleranceMode
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet CHNOPS -> 0, CHNOPSD -> 1, CHNOPSCL -> 2, CHNOPSCLD
     * -> 3, ALL -> 4 , ALLD -> 5
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @return
     */
    public List<CompoundLCMS> getCompoundsFromExperiment_byMassAndTolerance(
            Feature feature, double EM, String adduct, Double tolerance,
            Integer toleranceMode, List<Integer> databases, int metabolitesType,
            int chemAlphabet, int EMMode, int ionizationMode) {
        List<CompoundLCMS> compoundsList;
        // If the search is only in MINE, it goes directly to findRangeGeneratedAdvanced
        if (databases.size() == 1 && databases.contains(MAPDATABASES.get("MINE (Only In Silico Compounds)"))) {
            compoundsList = getInSilicoCompoundsFromExperiment_byMassAndTolerance(
                    feature,
                    EM,
                    adduct,
                    tolerance,
                    toleranceMode,
                    databases,
                    chemAlphabet,
                    EMMode,
                    ionizationMode);
        } else if (databases.size() > 1 && databases.contains(MAPDATABASES.get("MINE (Only In Silico Compounds)"))) {
            compoundsList = getExperimentalCompoundsFromExperiment_byMassAndTolerance(
                    feature,
                    EM,
                    adduct,
                    tolerance,
                    toleranceMode,
                    databases,
                    metabolitesType,
                    chemAlphabet,
                    EMMode,
                    ionizationMode);
            List<CompoundLCMS> InSilicoCompoundsList = getInSilicoCompoundsFromExperiment_byMassAndTolerance(
                    feature,
                    EM,
                    adduct,
                    tolerance,
                    toleranceMode,
                    databases,
                    chemAlphabet,
                    EMMode,
                    ionizationMode);
            compoundsList.addAll(InSilicoCompoundsList);
        } else {
            compoundsList = getExperimentalCompoundsFromExperiment_byMassAndTolerance(
                    feature,
                    EM,
                    adduct,
                    tolerance,
                    toleranceMode,
                    databases,
                    metabolitesType,
                    chemAlphabet,
                    EMMode,
                    ionizationMode);
        }
        return compoundsList;
    }

    /**
     * Get the compound annotation from the experimental databases. It is
     * obtained by the feature experimental modified according an adduct within
     * a tolerance
     *
     * @param feature
     * @param EM
     * @param adduct
     * @param tolerance
     * @param toleranceMode
     * @param databases
     * @param metabolitesType
     * @param chemAlphabet
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @return
     */
    public List<CompoundLCMS> getExperimentalCompoundsFromExperiment_byMassAndTolerance(Feature feature,
            double EM, String adduct, Double tolerance, int toleranceMode,
            List<Integer> databases, int metabolitesType, int chemAlphabet, int EMMode, int ionizationMode) {
        double massToSearch = AdductProcessing.getMassToSearch(EM, adduct, ionizationMode);
        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTable = "c";
        double RT = feature.getRT();
        Map<Double, Double> CS = feature.getCS();
        boolean isSignificative = feature.isIsSignificativeFeature();

        //long start = System.currentTimeMillis();
        sql = QueryConstructor.createSQLCompoundViewWithDBIds();
        sql = QueryConstructor.addFilterDatabasesCompoundsViewJDBC(sql, databases);
        sql = QueryConstructor.addFilterMetabolitesTypeJDBC(sql, metabolitesType);
        sql = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTable, sql, chemAlphabet);
        sql = QueryConstructor.addFilterMassesJDBC(aliasTable, sql);
        sql = QueryConstructor.addOrderByMassesJDBC(aliasTable, sql);

        // System.out.println("SQL: " + sql);
        //long end = System.currentTimeMillis();
        //queryCreation = queryCreation + (end - start);
        List<CompoundLCMS> compounds = new LinkedList<>();

        try {
            tolerance = Utilities.calculateDeltaPPM(massToSearch, toleranceMode, tolerance);
            prepSt = conn.prepareStatement(sql);
            prepSt.setDouble(1, (massToSearch - tolerance));
            prepSt.setDouble(2, (massToSearch + tolerance));
            prepSt.setDouble(3, tolerance);
            //start = System.currentTimeMillis();
            rs = prepSt.executeQuery();
            //end = System.currentTimeMillis();
            //System.out.println("Time executing query: "+(end-start));
            //JDBCcounter = JDBCcounter + (end - start);

            while (rs.next()) {
                //start = System.currentTimeMillis();

                int compound_id = rs.getInt(1);
                double mass = rs.getDouble(2);
                String formula = rs.getString(3);
                String name = rs.getString(4);
                String cas_id = rs.getString(5);
                int charge_type = rs.getInt(6);
                int charge_number = rs.getInt(7);
                int formula_type_int = rs.getInt(8);
                int compound_type = rs.getInt(9);
                int compound_status = rs.getInt(10);
                String kegg_id = rs.getString(11);
                String lm_id = rs.getString(12);
                String hmdb_id = rs.getString(13);
                String metlin_id = rs.getString(14);
                String pc_id = rs.getString(15);
                String in_house_id = rs.getString(16);
                String MINE_id = "";

                // Variables for Structure
                String inchi = rs.getString(17);
                String inchi_key = rs.getString(18);
                String smiles = rs.getString(19);

                // Variables forLipid Classification
                String lipid_type = rs.getString(20);
                Integer num_chains = rs.wasNull() ? null : rs.getInt(21);
                Integer number_carbons = rs.wasNull() ? null : rs.getInt(22);
                Integer double_bonds = rs.wasNull() ? null : rs.getInt(23);

                // Variables for LM Classification
                String category = rs.getString(24);
                String main_class = rs.getString(25);
                String sub_class = rs.getString(26);
                String class_level4 = rs.getString(27);

                //long start2 = System.currentTimeMillis();
                Structure structure = new Structure(inchi, inchi_key, smiles);

                //long end2 = System.currentTimeMillis();
                //structureCounter = structureCounter + (end2 - start2);
                //start2 = System.currentTimeMillis();
                Lipids_Classification lipids_classification;
                if (num_chains == null || number_carbons == null || double_bonds == null) {
                    lipids_classification = null;
                } else {
                    lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);
                }

                //end2 = System.currentTimeMillis();
                //lipidsClassificationCounter = lipidsClassificationCounter + (end2 - start2);
                //start2 = System.currentTimeMillis();
                LM_Classification lm_classification;
                if (category == null) {
                    lm_classification = null;
                } else {
                    lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);
                }

                //end2 = System.currentTimeMillis();
                //LMClassificationCounter = LMClassificationCounter + (end2 - start2);
                //To null since its not used so is not filled yet
                //List<Classyfire_Classification> classyfire_classifications =getClassyfire_ClassificationByCompound_id(compound_id);
                List<Classyfire_Classification> classyfire_classifications = null;

                //start2 = System.currentTimeMillis();
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);
                //end2 = System.currentTimeMillis();
                //pathwayCounter = pathwayCounter + (end2 - start2);

                CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct, 
                        EMMode, ionizationMode, isSignificative,
                        compound_id, mass, formula, name, cas_id, formula_type_int, compound_type,
                        compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                        structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                //end = System.currentTimeMillis();
                //System.out.println("Time creating object: "+(end-start));
                //objectLCMSCompoundCounter = objectLCMSCompoundCounter + (end - start);
                compounds.add(compoundLCMS);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);

        }
        return compounds;
    }

    /**
     * Get the compound annotation from the experimental databases. It is
     * obtained by the feature experimental modified according an adduct within
     * a tolerance
     *
     * @param feature
     * @param EM
     * @param adduct
     * @param tolerance
     * @param toleranceMode
     * @param databases
     * @param chemAlphabet
     * @param EMMode 0 neutral mass (m/z protonated or deprotonated), 1 m/z
     * @param ionizationMode 0 neutral, 1 positive, 2 negative
     * @return
     */
    public List<CompoundLCMS> getInSilicoCompoundsFromExperiment_byMassAndTolerance(Feature feature,
            double EM, String adduct, Double tolerance, Integer toleranceMode,
            List<Integer> databases, int chemAlphabet, int EMMode, int ionizationMode) {
        double massToSearch = AdductProcessing.getMassToSearch(EM, adduct, ionizationMode);
        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTable = "c";
        double RT = feature.getRT();
        Map<Double, Double> CS = feature.getCS();
        boolean isSignificative = feature.isIsSignificativeFeature();

        sql = QueryConstructor.createSQLInSilicoCompoundViewWithDBIds();
        sql = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTable, sql, chemAlphabet);
        sql = QueryConstructor.addFilterMassesJDBC(aliasTable, sql);
        sql = QueryConstructor.addOrderByMassesJDBC(aliasTable, sql);

        List<CompoundLCMS> compounds = new LinkedList<>();

        try {
            tolerance = Utilities.calculateDeltaPPM(massToSearch, toleranceMode, tolerance);
            prepSt = conn.prepareStatement(sql);
            prepSt.setDouble(1, (massToSearch - tolerance));
            prepSt.setDouble(2, (massToSearch + tolerance));
            prepSt.setDouble(3, tolerance);
            rs = prepSt.executeQuery();

            while (rs.next()) {

                int compound_id = rs.getInt(1);
                double mass = rs.getDouble(2);
                String formula = rs.getString(3);
                String name = rs.getString(4);
                String cas_id = null;
                int charge_type = rs.getInt(5);
                int charge_number = rs.getInt(6);
                int formula_type_int = rs.getInt(7);
                int compound_type = 0;
                int compound_status = 3;
                double np_likeness = rs.getDouble(8); // NOT USED YET!
                String mine_id = rs.getString(9);
                String kegg_id = null;
                String hmdb_id = null;
                String lm_id = null;
                String agilent_id = null;
                String in_house_id = null;
                String pc_id = null;
                String inchi = rs.getString(10);
                String inchi_key = rs.getString(11);
                String smiles = rs.getString(12);

                LM_Classification lm_classification = null;
                Lipids_Classification lipids_classification = null;
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = null;
                Structure structure = new Structure(inchi, inchi_key, smiles);

                CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct, 
                        EMMode, ionizationMode, isSignificative,
                        compound_id, mass, formula, name, cas_id, formula_type_int, compound_type,
                        compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, mine_id,
                        structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                compounds.add(compoundLCMS);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);

        }

        return compounds;
    }

    /**
     * return a compound from the database. If the compound_id is not present,
     * return null
     *
     * @param compound_id
     * @return
     */
    public CMMCompound getCompoundFromExperiment(int compound_id) {
        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        sql = QueryConstructor.createSQLCompoundViewWithDBIds();
        sql = sql + " c.compound_id = ?";
        CMMCompound compound;
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();

            compound_id = rs.getInt(1);
            double mass = rs.getDouble(2);
            String formula = rs.getString(3);
            String name = rs.getString(4);
            String cas_id = rs.getString(5);
            int charge_type = rs.getInt(6);
            int charge_number = rs.getInt(7);
            int formula_type_int = rs.getInt(8);
            int compound_type = rs.getInt(9);
            int compound_status = rs.getInt(10);
            String kegg_id = rs.getString(11);
            String lm_id = rs.getString(12);
            String hmdb_id = rs.getString(13);
            String metlin_id = rs.getString(14);
            String pc_id = rs.getString(15);
            String in_house_id = rs.getString(16);
            String MINE_id = "";
            String inchi = rs.getString(17);
            String inchi_key = rs.getString(18);
            String smiles = rs.getString(19);
            String lipid_type = rs.getString(20);
            int num_chains = rs.getInt(21);
            int number_carbons = rs.getInt(22);
            int double_bonds = rs.getInt(23);
            String category = rs.getString(24);
            String main_class = rs.getString(25);
            String sub_class = rs.getString(26);
            String class_level4 = rs.getString(27);

            Structure structure = new Structure(inchi, inchi_key, smiles);
            Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);
            LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);
            //To null since its not used so is not filled yet
            List<Classyfire_Classification> classyfire_classifications = null;
            List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

            compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                    compound_type, compound_status, charge_type, charge_number,
                    lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                    structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return compound;
    }

    /**
     * return a LM_Classification from the database. If the compound_id is not
     * present, return null
     *
     * @param compound_id
     * @return
     */
    protected LM_Classification getLM_ClassificationByCompound_id(int compound_id) {
        String sql = "SELECT category, main_class, sub_class, class_level4 FROM compounds_lm_classification "
                + "WHERE compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        String category, main_class, sub_class, class_level4;
        LM_Classification lm_classification = null;
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            if (rs.next()) {
                category = rs.getString(1);
                main_class = rs.getString(2);
                sub_class = rs.getString(3);
                class_level4 = rs.getString(4);

                lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return lm_classification;
    }

    /**
     * return a lipids_classification from the database. If the compound_id is
     * not present, return null
     *
     * @param compound_id
     * @return
     */
    protected Lipids_Classification getLipids_classificationByCompound_id(int compound_id) {

        String sql = "SELECT lipid_type, num_chains, number_carbons, double_bonds "
                + "FROM compounds_lipids_classification "
                + "WHERE compound_id=?";
        PreparedStatement prepSt;
        ResultSet rs;
        Lipids_Classification lipids_classification = null;

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            if (rs.next()) {
                String lipidtype = rs.getString(1);
                int num_chains = rs.getInt(2);
                int number_carbons = rs.getInt(3);
                int double_bonds = rs.getInt(4);
                List<Chain> chains = getListChainsByCompound_id(compound_id);
                lipids_classification = new Lipids_Classification(lipidtype, num_chains, number_carbons, double_bonds, chains, null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return lipids_classification;
    }

    /**
     * return a list of chains from the database. If the compound_id is not
     * present, return null
     *
     * @param compound_id
     * @return List of chain object
     */
    protected List<Chain> getListChainsByCompound_id(int compound_id) {
        String sql = "SELECT c.chain_id, c.num_carbons, c.double_bonds, c.oxidation, c.mass, c.formula "
                + "FROM chains c INNER JOIN compound_chain cc "
                + "WHERE c.chain_id=cc.chain_id "
                + "AND cc.compound_id=?";
        PreparedStatement prepSt;
        ResultSet rs;
        List<Chain> chains = new LinkedList<>();

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int chain_id = rs.getInt(1);
                int num_carbons = rs.getInt(2);
                int double_bonds = rs.getInt(3);
                String oxidation = rs.getString(4);
                double mass = rs.getDouble(5);
                String formula = rs.getString(6);
                Chain chain = new Chain(chain_id, num_carbons, double_bonds, oxidation, mass, formula);
                chains.add(chain);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return chains;
    }

    /**
     * return a list of pathways from the database. If the compound_id is not
     * present, return null
     *
     * @param compound_id
     * @return List of chain object
     */
    protected List<Pathway> getPathwaysByCompound_id(int compound_id) {
        String sql = "SELECT p.pathway_id, p.pathway_map, p.pathway_name "
                + "FROM pathways p INNER JOIN compounds_pathways cp "
                + "WHERE p.pathway_id=cp.pathway_id "
                + "AND cp.compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        List<Pathway> pathways = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int pathway_id = rs.getInt(1);
                String pathway_map = rs.getString(2);
                String pathway_name = rs.getString(3);
                Pathway pathway = new Pathway(pathway_id, pathway_map, pathway_name, null);
                pathways.add(pathway);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return pathways;
    }

    /**
     * Get a list of compounds related with a given pathway
     *
     * @param pathway_id
     * @return
     */
    public List<CMMCompound> getCompoundsByPathway_id(int pathway_id) {

        String sql = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status, "
                + "c.kegg_id, c.lm_id, c.hmdb_id, c.agilent_id, c.pc_id, c.in_house_id, "
                + "c.inchi, c.inchi_key, c.smiles, "
                + "c.lipid_type, c.num_chains, c.number_carbons, c.double_bonds, "
                + "c.category, c.main_class, c.sub_class, c.class_level4 "
                + "FROM compounds_view c INNER JOIN compounds_pathways ON pathway_id=?";

        List<CMMCompound> compounds = new LinkedList();
        CMMCompound compound;
        PreparedStatement prepSt;
        ResultSet rs;

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, pathway_id);
            rs = prepSt.executeQuery();
            int compound_id = rs.getInt(1);
            double mass = rs.getDouble(2);
            String formula = rs.getString(3);
            String name = rs.getString(4);
            String cas_id = rs.getString(5);
            int charge_type = rs.getInt(6);
            int charge_number = rs.getInt(7);
            int formula_type_int = rs.getInt(8);
            int compound_type = rs.getInt(9);
            int compound_status = rs.getInt(10);
            String kegg_id = rs.getString(11);
            String lm_id = rs.getString(12);
            String hmdb_id = rs.getString(13);
            String metlin_id = rs.getString(14);
            String pc_id = rs.getString(15);
            String in_house_id = rs.getString(16);
            String MINE_id = "";

            // Variables for Structure
            String inchi = rs.getString(17);
            String inchi_key = rs.getString(18);
            String smiles = rs.getString(19);

            // Variables forLipid Classification
            String lipid_type = rs.getString(20);
            int num_chains = rs.getInt(21);
            int number_carbons = rs.getInt(22);
            int double_bonds = rs.getInt(23);

            // Variables for LM Classification
            String category = rs.getString(24);
            String main_class = rs.getString(25);
            String sub_class = rs.getString(26);
            String class_level4 = rs.getString(27);

            Structure structure = new Structure(inchi, inchi_key, smiles);

            Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);

            LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);

            List<Classyfire_Classification> classyfire_classifications = getClassyfire_ClassificationByCompound_id(compound_id);

            List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

            compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int, compound_type,
                    compound_status, charge_type, charge_number,
                    lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                    structure, lm_classification, classyfire_classifications, lipids_classification, pathways);

            compounds.add(compound);

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return compounds;
    }

    /**
     * return a list of Classyfire_Classification from the database. If the
     * compound_id is not present, return null
     *
     * @param compound_id
     * @return List of chain object
     */
    protected List<Classyfire_Classification> getClassyfire_ClassificationByCompound_id(int compound_id) {

        //long start = System.currentTimeMillis();
        String sql = "SELECT cc.kingdom, cc.super_class, cc.main_class, cc.sub_class, cc.direct_parent "
                + "FROM classyfire_classification cc INNER JOIN compound_classyfire_classification ccc "
                + "WHERE cc.node_id=ccc.node_id AND ccc.compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        List<Classyfire_Classification> classyfire_classifications = new LinkedList<>();

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                String kingdom = rs.getString(1);
                String super_class = rs.getString(2);
                String main_class = rs.getString(3);
                String sub_class = rs.getString(4);
                String direct_parent = rs.getString(5);
                Classyfire_Classification classyfire_classification = new Classyfire_Classification(kingdom, super_class, main_class, sub_class, direct_parent);
                classyfire_classifications.add(classyfire_classification);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        //long end = System.currentTimeMillis();
        //classifierClassificationCounter = classifierClassificationCounter + (end - start);
        return classyfire_classifications;
    }

    /**
     * return a structure from the database. If the compound_id is not present,
     * return null
     *
     * @param compound_id
     * @return List of chain object
     */
    protected Structure getStructureByCompound_id(int compound_id) {

        String sql = "SELECT inchi, inchi_key, smiles "
                + "FROM compound_identifiers "
                + "WHERE compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        Structure structure = null;

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            if (rs.next()) {
                String inchi = rs.getString(1);
                String inchi_key = rs.getString(2);
                String smiles = rs.getString(3);
                structure = new Structure(inchi, inchi_key, smiles);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        long end = System.currentTimeMillis();

        return structure;
    }

    /**
     * return a structure from the database. If the compound_id is not present,
     * return null
     *
     * @param compound_id
     * @return List of chain object
     */
    protected Structure getStructureByGenCompound_id(int compound_id) {

        String sql = "SELECT inchi, inchi_key, smiles "
                + "FROM compound_gen_identifiers "
                + "WHERE compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        Structure structure = null;

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            if (rs.next()) {
                String inchi = rs.getString(1);
                String inchi_key = rs.getString(2);
                String smiles = rs.getString(3);
                structure = new Structure(inchi, inchi_key, smiles);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return structure;
    }

    public List<MSMSCompound> getMSMSFromCompoundByMassAndTolerance(double mass, double tolerance) {
        String sql = "SELECT m.ionization_mode, m.voltage, m.voltage_level, m.predicted, "
                + "m.msms_id, m.compound_id, c.compound_name, m.hmdb_id, c.formula, c.mass "
                + "FROM msms m inner join compounds_view c on m.compound_id= m.compound_id=c.compound_id "
                + "where c.mass between ? and ?";
        ResultSet rs;
        PreparedStatement prepSt;
        MSMSCompound compoundmsms;
        List<MSMSCompound> msmsCompounds = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setDouble(1, (mass - tolerance));
            prepSt.setDouble(2, (mass - tolerance));
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                int compound_id = rs.getInt("compound_id");
                String name = rs.getString("compound_name");
                String hmdb_id = rs.getString("hmdb_id");
                String formula = rs.getString("formula");
                double compound_mass = rs.getDouble("mass");
                int ionizationMode = rs.getInt("ionization_mode");
                int spectraType = rs.getInt("predicted");
                int voltage = rs.getInt("voltage");
                String voltage_level = rs.getString("voltage_level");
                List<Peak> peaks = getPeaksFromMsms_id(msms_id);
                compoundmsms = new MSMSCompound(ionizationMode, voltage, voltage_level, peaks, spectraType, msms_id, compound_id, hmdb_id, name, formula, compound_mass);
                msmsCompounds.add(compoundmsms);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msmsCompounds;
    }

    /**
     * Return the number of peaks that has an mz within an specified window and
     * from a specific compound.
     *
     * @param compound_id
     * @param peak_mass
     * @param tolerance
     * @return
     */
    public int countMSMSFromCompoundID(int compound_id, double peak_mass, double tolerance) {
        String sql = "SELECT count(*) AS rowcount FROM msms_peaks join msms where msms.compound_id=? and msms_peaks.mz between ? and ?;";
        ResultSet rs;
        PreparedStatement prepSt;
        int count = 0;
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            prepSt.setDouble(2, peak_mass - tolerance);
            prepSt.setDouble(3, peak_mass + tolerance);
            rs = prepSt.executeQuery();
            rs.next();
            count = rs.getInt("rowcount");

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    /**
     * get the msms from compound with compound id= compounds_id
     *
     * @param compound_id
     * @return
     */
    public List<MSMSCompound> getMSMSFromCompoundID(int compound_id) {
        String sql = "SELECT m.ionization_mode, m.voltage, m.voltage_level, m.predicted, m.msms_id, m.compound_id, m.hmdb_id "
                + "FROM msms as m where m.compound_id=?";
        ResultSet rs;
        PreparedStatement prepSt;
        MSMSCompound compoundmsms;
        List<MSMSCompound> msmsCompounds = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                compound_id = rs.getInt("compound_id");
                String hmdb_id = rs.getString("hmdb_id");
                int ionizationMode = rs.getInt("ionization_mode");
                int spectraType = rs.getInt("predicted");
                int voltage = rs.getInt("voltage");
                String voltage_level = rs.getString("voltage_level");
                List<Peak> peaks = getPeaksFromMsms_id(msms_id);
                compoundmsms = new MSMSCompound(msms_id, compound_id, hmdb_id, ionizationMode, voltage, voltage_level, peaks, spectraType);
                msmsCompounds.add(compoundmsms);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msmsCompounds;
    }

    /**
     * Get the corresponding msms of a compound with id compound_id and ionMode.
     *
     * @param compound_id
     * @param ionMode
     * @return
     */
    public List<MSMSCompound> getMSMSFromCompoundIDandIonMode(int compound_id, int ionMode) {
        String sql = "SELECT m.ionization_mode, m.voltage, m.voltage_level, m.predicted, m.msms_id, m.compound_id, m.hmdb_id "
                + "FROM msms as m where m.compound_id=? and m.ionization_mode=?";
        ResultSet rs;
        PreparedStatement prepSt;
        MSMSCompound compoundmsms;
        List<MSMSCompound> msmsCompounds = new LinkedList<>();
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            prepSt.setInt(2, ionMode);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int msms_id = rs.getInt("msms_id");
                compound_id = rs.getInt("compound_id");
                String hmdb_id = rs.getString("hmdb_id");
                int ionizationMode = rs.getInt("ionization_mode");
                int spectraType = rs.getInt("predicted");
                int voltage = rs.getInt("voltage");
                String voltage_level = rs.getString("voltage_level");
                List<Peak> peaks = getPeaksFromMsms_id(msms_id);
                compoundmsms = new MSMSCompound(msms_id, compound_id, hmdb_id, ionizationMode, voltage, voltage_level, peaks, spectraType);
                msmsCompounds.add(compoundmsms);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msmsCompounds;
    }

    public List<CMMCompound> findCompoundsBrowseSearch(
            String nameQuery,
            boolean exactName,
            String formulaQuery,
            boolean exactFormula,
            List<Integer> databases,
            String metabolitesType) {
        List<CMMCompound> compoundsList;

        // If the search is only in MINE, it goes directly to findRangeGeneratedAdvanced
        if (databases.size() == 1 && databases.contains(MAPDATABASES.get("MINE (Only In Silico Compounds)"))) {
            compoundsList = findInSilicoBrowseSearch(
                    nameQuery,
                    exactName,
                    formulaQuery,
                    exactFormula);
        } else if (databases.size() > 1 && databases.contains(MAPDATABASES.get("MINE (Only In Silico Compounds)"))) {
            compoundsList = findExperimentalCompoundsBrowseSearch(
                    nameQuery,
                    exactName,
                    formulaQuery,
                    exactFormula,
                    databases,
                    metabolitesType);
            List<CMMCompound> InSilicoCompoundsList = findInSilicoBrowseSearch(
                    nameQuery,
                    exactName,
                    formulaQuery,
                    exactFormula);
            compoundsList.addAll(InSilicoCompoundsList);
        } else {
            compoundsList = findExperimentalCompoundsBrowseSearch(
                    nameQuery,
                    exactName,
                    formulaQuery,
                    exactFormula,
                    databases,
                    metabolitesType);
        }
        return compoundsList;
    }

    // Methods for browse search
    /**
     * returns a list of compounds from the databases based on the name and/or
     * formula
     *
     * @param nameQuery
     * @param formulaQuery
     * @param exactName
     * @param exactFormula
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<CMMCompound> findExperimentalCompoundsBrowseSearch(
            String nameQuery,
            boolean exactName,
            String formulaQuery,
            boolean exactFormula,
            List<Integer> databases,
            String metabolitesType) {
        List<CMMCompound> compoundsList = new LinkedList<>();
        //System.out.println("Databases " + databases);

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.createSQLCompoundViewWithDBIds();
// Add databases depending on the user selection
        sql = QueryConstructor.addFilterDatabasesCompoundsViewJDBC(sql, databases);
// Add compound type for search
        sql = QueryConstructor.addFilterMetabolitesTypeJDBC(sql, metabolitesType);

        // Finish the query with the mass condition
        int numberParameters = 0;
        if (!(nameQuery.length() < 3)) {
            numberParameters++;
            if (exactName) {
                sql = sql + " c.compound_name = ?";
            } else {
                sql = sql + " c.compound_name REGEXP ?";
                nameQuery = escapeSQLForREGEXP(nameQuery);
            }
            if (!(formulaQuery.length() < 3)) {
                if (exactFormula) {
                    sql = sql + " and c.formula = ?";
                } else {
                    sql = sql + " and c.formula REGEXP ?";
                    formulaQuery = escapeSQLForREGEXP(formulaQuery);
                }

                numberParameters++;
            }
            //finalQuery = finalQuery + " limit 500";
        } else if (!(formulaQuery.length() < 3)) {
            if (exactFormula) {
                sql = sql + " c.formula = ?";
            } else {
                sql = sql + " c.formula REGEXP ?";
                formulaQuery = escapeSQLForREGEXP(formulaQuery);
            }
            numberParameters++;
        } else {
            // IF there is no filter, return an empty list
            return compoundsList;
        }
        sql = sql + " order by mass, compound_name limit 1000";
        CMMCompound compound;
        try {
            prepSt = conn.prepareStatement(sql);
            switch (numberParameters) {
                case 1:
                    if (!(nameQuery.length() < 3)) {
                        prepSt.setString(1, nameQuery);
                    } else {
                        prepSt.setString(1, formulaQuery);
                    }
                    break;
                case 2:
                    prepSt.setString(1, nameQuery);
                    prepSt.setString(2, formulaQuery);
                    break;
                default:
                    // never arrives here, if there is no parameters, an empty list
                    // is returned before
                    return compoundsList;
            }
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int compound_id = rs.getInt(1);
                double mass = rs.getDouble(2);
                String formula = rs.getString(3);
                String name = rs.getString(4);
                String cas_id = rs.getString(5);
                int charge_type = rs.getInt(6);
                int charge_number = rs.getInt(7);
                int formula_type_int = rs.getInt(8);
                int compound_type = rs.getInt(9);
                int compound_status = rs.getInt(10);
                String kegg_id = rs.getString(11);
                String lm_id = rs.getString(12);
                String hmdb_id = rs.getString(13);
                String metlin_id = rs.getString(14);
                String pc_id = rs.getString(15);
                String in_house_id = rs.getString(16);
                String MINE_id = "";
                String inchi = rs.getString(17);
                String inchi_key = rs.getString(18);
                String smiles = rs.getString(19);
                String lipid_type = rs.getString(20);
                int num_chains = rs.getInt(21);
                int number_carbons = rs.getInt(22);
                int double_bonds = rs.getInt(23);
                String category = rs.getString(24);
                String main_class = rs.getString(25);
                String sub_class = rs.getString(26);
                String class_level4 = rs.getString(27);

                Structure structure = new Structure(inchi, inchi_key, smiles);

                Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);

                LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);

                //List<Classyfire_Classification> classyfire_classifications = getClassyfire_ClassificationByCompound_id(compound_id);
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

                compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                        compound_type, compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, MINE_id,
                        structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                compoundsList.add(compound);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);

        }

        return compoundsList;
    }

    /**
     * returns a list of compounds from the databases based on the name and/or
     * formula
     *
     * @param nameQuery
     * @param formulaQuery
     * @param exactName
     * @param exactFormula
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    private List<CMMCompound> findInSilicoBrowseSearch(
            String nameQuery,
            boolean exactName,
            String formulaQuery,
            boolean exactFormula) {
        List<CMMCompound> compoundsList = new LinkedList<>();

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.createSQLInSilicoCompoundViewWithDBIds();

        // Finish the query with the mass condition
        int numberParameters = 0;
        if (!(nameQuery.length() < 3)) {
            numberParameters++;
            if (exactName) {
                sql = sql + " c.compound_name = ?";
            } else {
                sql = sql + " c.compound_name REGEXP ?";
                nameQuery = escapeSQLForREGEXP(nameQuery);
            }
            if (!(formulaQuery.length() < 3)) {
                if (exactFormula) {
                    sql = sql + " and c.formula = ?";
                } else {
                    sql = sql + " and c.formula REGEXP ?";
                    formulaQuery = escapeSQLForREGEXP(formulaQuery);
                }

                numberParameters++;
            }

            if (exactFormula) {
                sql = sql + " c.formula = ?";
            } else {
                sql = sql + " c.formula REGEXP ?";
                formulaQuery = escapeSQLForREGEXP(formulaQuery);
            }
            numberParameters++;
        } else {
            // IF there is no filter, return an empty list
            return compoundsList;
        }
        sql = sql + " order by mass, compound_name limit 1000";
        CMMCompound compound;
        try {
            prepSt = conn.prepareStatement(sql);
            switch (numberParameters) {
                case 1:
                    if (!(nameQuery.length() < 3)) {
                        prepSt.setString(1, nameQuery);
                    } else {
                        prepSt.setString(1, formulaQuery);
                    }
                    break;
                case 2:
                    prepSt.setString(1, nameQuery);
                    prepSt.setString(2, formulaQuery);
                    break;
                default:
                    // never arrives here, if there is no parameters, an empty list
                    // is returned before
                    return compoundsList;
            }
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int compound_id = rs.getInt(1);
                double mass = rs.getDouble(2);
                String formula = rs.getString(3);
                String name = rs.getString(4);
                String cas_id = null;
                int charge_type = rs.getInt(5);
                int charge_number = rs.getInt(6);
                int formula_type_int = rs.getInt(7);
                int compound_type = 0;
                int compound_status = 3;
                double np_likeness = rs.getDouble(8); // NOT USED YET!
                String mine_id = rs.getString(9);
                String kegg_id = null;
                String hmdb_id = null;
                String lm_id = null;
                String agilent_id = null;
                String in_house_id = null;
                String pc_id = null;
                String inchi = rs.getString(10);
                String inchi_key = rs.getString(11);
                String smiles = rs.getString(12);

                LM_Classification lm_classification = null;
                Lipids_Classification lipids_classification = null;
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = null;
                Structure structure = new Structure(inchi, inchi_key, smiles);
                compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                        compound_type, compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, mine_id,
                        structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                compoundsList.add(compound);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);

        }

        return compoundsList;
    }

    private List<Peak> getPeaksFromMsms_id(int msms_id) {
        String sql = "SELECT * FROM msms_peaks where msms_id=?";
        PreparedStatement prepSt;
        ResultSet rs;
        Peak peak;
        List<Peak> peaks = new LinkedList<>();

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, msms_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                msms_id = rs.getInt("msms_id");
                double intensity = rs.getDouble("intensity");
                double mz = rs.getDouble("mz");
                peak = new Peak(mz, intensity, msms_id);
                peaks.add(peak);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return peaks;
    }

    private List<String> getDataForConnection() {
        List<String> dataToConnect = new LinkedList<>();
        try {

            //File file = new File("C:\\Users\\María 606888798\\Desktop\\fichero.txt");
            File file = new File("C:\\Users\\ceu\\Desktop\\fichero.txt");
            //File file = new File("/home/alberto/Desktop/fichero.txt");

            FileReader fr;
            BufferedReader bf;
            dataToConnect = new LinkedList<>();
            fr = new FileReader(file);
            bf = new BufferedReader(fr);
            String line;
            while ((line = bf.readLine()) != null) {
                dataToConnect.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataToConnect;
    }
}
