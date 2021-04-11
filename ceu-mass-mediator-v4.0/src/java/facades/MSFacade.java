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

import CEMS.*;
import CEMS.enums.CE_Exp_properties;
import static CEMS.enums.CE_Exp_properties.CE_EXP_PROP_ENUM.fromIntegerCode;
import DBManager.DBManager;
import DBManager.QueryConstructor;
import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.Feature;
import List.NoDuplicatesList;
import compound.*;
import exceptions.bufferTemperatureException;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import msms.MSMSCompound;
import msms.Peak;
import pathway.Pathway;
import services.rest.api.response.*;
import utilities.*;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import static utilities.Utilities.escapeSQLForREGEXP;

/**
 * MSFacade. Middleware to connect the CMM LCMS application with the database It
 * interacts with the database of CMM from the project
 *
 * @version $Revision: 1.1.1.2 $
 * @since Build 4.1 30-abr-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class MSFacade implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DBManager DBM = new DBManager();
    protected final Connection conn;

    public MSFacade() {
        this.conn = MSFacade.DBM.connect();
    }

    public void disconnect() {
        MSFacade.DBM.disconnect();
    }

    private List<String> getDataForConnection() {
        List<String> dataToConnect = new LinkedList<>();
        try {

            File file = new File("C:\\Users\\ceu\\Desktop\\fichero.txt");

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
     *
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
     *
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
        sql = QueryConstructor.createSQLCompoundViewWithDBIdsWhereCondition();
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
            prepSt.setDouble(3, massToSearch);
            //start = System.currentTimeMillis();
            rs = prepSt.executeQuery();
            //end = System.currentTimeMillis();
            //System.out.println("Time executing query: "+(end-start));
            //JDBCcounter = JDBCcounter + (end - start);

            while (rs.next()) {
                //start = System.currentTimeMillis();

                int compound_id = rs.getInt("compound_id");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
                String name = rs.getString("compound_name");
                String cas_id = rs.getString("cas_id");
                int charge_type = rs.getInt("charge_type");
                int charge_number = rs.getInt("charge_number");
                int formula_type_int = rs.getInt("formula_type_int");
                int compound_type = rs.getInt("compound_type");
                int compound_status = rs.getInt("compound_status");
                String kegg_id = rs.getString("kegg_id");
                String lm_id = rs.getString("lm_id");
                String hmdb_id = rs.getString("hmdb_id");
                String metlin_id = rs.getString("agilent_id");
                Integer pc_id = rs.getInt("pc_id");
                Integer chebi_id = rs.getInt("chebi_id");
                String knapsack_id = rs.getString("knapsack_id");
                Integer npatlas_id = rs.getInt("npatlas_id");
                String in_house_id = rs.getString("in_house_id");
                String aspergillus_id = rs.getString("aspergillus_id");
                Integer fahfa_id = rs.wasNull() ? null : rs.getInt("fahfa_id");
                Integer oh_position = rs.wasNull() ? null : rs.getInt("oh_position");

                String mesh_nomenclature = rs.getString("mesh_nomenclature");
                String iupac_classification = rs.getString("iupac_classification");
                String aspergillus_web_name = rs.getString("aspergillus_web_name");
                Integer MINE_id = null;
                // Variables for Structure
                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");

                // Variables forLipid Classification
                String lipid_type = rs.getString("lipid_type");
                Integer num_chains = rs.wasNull() ? null : rs.getInt("num_chains");
                Integer number_carbons = rs.wasNull() ? null : rs.getInt("number_carbons");
                Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");

                // Variables for LM Classification
                String category = rs.getString("category");
                String main_class = rs.getString("main_class");
                String sub_class = rs.getString("sub_class");
                String class_level4 = rs.getString("class_level4");

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
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
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
     *
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
            prepSt.setDouble(3, massToSearch);
            rs = prepSt.executeQuery();

            while (rs.next()) {

                int compound_id = rs.getInt("compound_id");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
                String name = rs.getString("compound_name");
                String cas_id = null;
                int charge_type = rs.getInt("charge_type");
                int charge_number = rs.getInt("charge_number");
                int formula_type_int = rs.getInt("formula_type_int");
                int compound_type = 0;
                int compound_status = 3;
                Double np_likeness = rs.getDouble("np_likeness");
                np_likeness = rs.wasNull() ? null : np_likeness;
                Integer mine_id = rs.getInt("MINE_id");
                String kegg_id = null;
                String hmdb_id = null;
                String lm_id = null;
                String agilent_id = null;
                String in_house_id = null;
                String aspergillus_id = null;
                String mesh_nomenclature = null;
                String iupac_classification = null;
                String aspergillus_web_name = null;
                Integer fahfa_id = null;
                Integer oh_position = null;
                Integer pc_id = null;
                Integer chebi_id = null;
                String knapsack_id = null;
                Integer npatlas_id = null;
                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");

                LM_Classification lm_classification = null;
                Lipids_Classification lipids_classification = null;
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = null;
                Structure structure = new Structure(inchi, inchi_key, smiles);

                CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct,
                        EMMode, ionizationMode, isSignificative,
                        compound_id, mass, formula, name, cas_id, formula_type_int, compound_type,
                        compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, chebi_id, mine_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
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
     *
     * @return
     */
    public CMMCompound getCompoundFromExperiment(int compound_id) {
        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        sql = QueryConstructor.createSQLCompoundViewWithDBIdsWhereCondition();
        sql = sql + " c.compound_id = ?";
        CMMCompound compound;
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();

            compound_id = rs.getInt("compound_id");
            double mass = rs.getDouble("mass");
            String formula = rs.getString("formula");
            String name = rs.getString("compound_name");
            String cas_id = rs.getString("cas_id");
            int charge_type = rs.getInt("charge_type");
            int charge_number = rs.getInt("charge_number");
            int formula_type_int = rs.getInt("formula_type_int");
            int compound_type = rs.getInt("compound_type");
            int compound_status = rs.getInt("compound_status");
            String kegg_id = rs.getString("kegg_id");
            String lm_id = rs.getString("lm_id");
            String hmdb_id = rs.getString("hmdb_id");
            String metlin_id = rs.getString("agilent_id");
            Integer pc_id = rs.getInt("pc_id");
            Integer chebi_id = rs.getInt("chebi_id");
            String knapsack_id = rs.getString("knapsack_id");
            Integer npatlas_id = rs.getInt("npatlas_id");
            String in_house_id = rs.getString("in_house_id");
            String aspergillus_id = rs.getString("aspergillus_id");
            Integer fahfa_id = rs.wasNull() ? null : rs.getInt("fahfa_id");
            Integer oh_position = rs.wasNull() ? null : rs.getInt("oh_position");

            String mesh_nomenclature = rs.getString("mesh_nomenclature");
            String iupac_classification = rs.getString("iupac_classification");
            String aspergillus_web_name = rs.getString("aspergillus_web_name");
            Integer MINE_id = null;
            // Variables for Structure
            String inchi = rs.getString("inchi");
            String inchi_key = rs.getString("inchi_key");
            String smiles = rs.getString("smiles");

            // Variables forLipid Classification
            String lipid_type = rs.getString("lipid_type");
            Integer num_chains = rs.wasNull() ? null : rs.getInt("num_chains");
            Integer number_carbons = rs.wasNull() ? null : rs.getInt("number_carbons");
            Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");

            // Variables for LM Classification
            String category = rs.getString("category");
            String main_class = rs.getString("main_class");
            String sub_class = rs.getString("sub_class");
            String class_level4 = rs.getString("class_level4");

            Structure structure = new Structure(inchi, inchi_key, smiles);
            Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);
            LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);
            //To null since its not used so is not filled yet
            List<Classyfire_Classification> classyfire_classifications = null;
            List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

            compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                    compound_type, compound_status, charge_type, charge_number,
                    lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                    aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                    fahfa_id, oh_position,
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
     *
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
                category = rs.getString("category");
                main_class = rs.getString("main_class");
                sub_class = rs.getString("sub_class");
                class_level4 = rs.getString("class_level4");

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
     *
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
                String lipidtype = rs.getString("lipid_type");
                Integer num_chains = rs.wasNull() ? null : rs.getInt("num_chains");
                Integer number_carbons = rs.wasNull() ? null : rs.getInt("number_carbons");
                Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");

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
     *
     * @return List of chain object
     */
    protected List<Chain> getListChainsByCompound_id(int compound_id) {
        String sql = "SELECT c.chain_id, c.num_carbons, c.double_bonds, c.oxidation, c.mass, c.formula "
                + "FROM chains c INNER JOIN compound_chain cc "
                + "on c.chain_id=cc.chain_id "
                + "WHERE cc.compound_id=?";
        PreparedStatement prepSt;
        ResultSet rs;
        List<Chain> chains = new LinkedList<>();

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int chain_id = rs.getInt("chain_id");
                Integer num_carbons = rs.wasNull() ? null : rs.getInt("num_carbons");
                Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");
                String oxidation = rs.getString("oxidation");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
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
     * return a list of product ions for the compound id
     *
     * @param compound_id
     * @param MT
     * @param RMT
     *
     * @return List of product ions object
     */
    protected CEMSCompound getPrecursorIonByCompound_id(int compound_id, Double MT, Double RMT) {
        String sql = "SELECT cepi.ce_product_ion_id, cepi.ion_source_voltage, cepi.ce_transformation_type, "
                + "cepi.compound_id_own, cepi.ce_product_ion_mz, cepi.ce_product_ion_name, "
                + "cepi.ce_product_ion_intensity, ce_eff_mob.eff_mobility, ce_eff_mob.cembio_id "
                + "FROM compound_ce_product_ion cepi INNER JOIN ce_eff_mob ce_eff_mob "
                + "on cepi.ce_eff_mob_id = ce_eff_mob.ce_eff_mob_id "
                + "INNER JOIN compounds_view c "
                + "on ce_eff_mob.ce_compound_id= c.compound_id "
                + "WHERE c.compound_id= ?";
        PreparedStatement prepSt;
        ResultSet rs;
        CEMSCompound cemscompound;
        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setInt(1, compound_id);
            rs = prepSt.executeQuery();
            cemscompound = getCEMSCompoundFromRS(rs);
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return cemscompound;
    }

    /**
     * return a list of product ions for the compound id
     *
     * @param compound_id
     * @param MT
     * @param RMT
     * @param ionSourceVoltage
     *
     * @return List of product ions object
     */
    protected Set<CEMSFragment> getProductIonsByCompound_id(Integer compound_id, Double MT, Double RMT, Integer ionSourceVoltage) {
        String sql = "SELECT cepi.ce_product_ion_id, cepi.ion_source_voltage, cepi.ce_transformation_type, "
                + "cepi.compound_id_own, cepi.ce_product_ion_mz, cepi.ce_product_ion_name, "
                + "cepi.ce_product_ion_intensity, ce_eff_mob.eff_mobility, ce_eff_mob.cembio_id "
                + "FROM compound_ce_product_ion cepi INNER JOIN ce_eff_mob ce_eff_mob "
                + "on cepi.ce_eff_mob_id = ce_eff_mob.ce_eff_mob_id "
                + "INNER JOIN compounds_view c "
                + "on ce_eff_mob.ce_compound_id= c.compound_id "
                + "WHERE c.compound_id= ? ";
        String sqlWithVoltage = sql;
        if (ionSourceVoltage != null) {
            sqlWithVoltage = sqlWithVoltage + "and cepi.ion_source_voltage = ?";
        }
        PreparedStatement prepSt1;
        ResultSet rs1;
        Set<CEMSFragment> productIons;
        try {
            prepSt1 = conn.prepareStatement(sqlWithVoltage);
            prepSt1.setInt(1, compound_id);
            if (ionSourceVoltage != null) {
                prepSt1.setInt(2, ionSourceVoltage);
            }
            rs1 = prepSt1.executeQuery();
            productIons = getProductIonFromRS(rs1, MT, RMT);
            if (ionSourceVoltage != null && productIons.isEmpty()) {
                prepSt1 = conn.prepareStatement(sql);
                prepSt1.setInt(1, compound_id);
                rs1 = prepSt1.executeQuery();
                productIons = getProductIonFromRS(rs1, MT, RMT);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return productIons;
    }

    /**
     * return a list of pathways from the database. If the compound_id is not
     * present, return null
     *
     * @param compound_id
     *
     * @return List of pathways object
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
                int pathway_id = rs.getInt("pathway_id");
                String pathway_map = rs.getString("pathway_map");
                String pathway_name = rs.getString("pathway_name");
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
     *
     * @return
     */
    public List<CMMCompound> getCompoundsByPathway_id(int pathway_id) {

        String sql = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status, "
                + "c.kegg_id, c.lm_id, c.hmdb_id, c.agilent_id, c.pc_id, c.chebi_id, c.in_house_id, "
                + "c.kanpsack_id, c.npatlas_id, "
                + "c.aspergillus_id, c.fahfa_id, "
                + "c.oh_position,"
                + "c.mesh_nomenclature, c.iupac_classification, c.aspergillus_web_name, "
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
            int compound_id = rs.getInt("compound_id");
            double mass = rs.getDouble("mass");
            String formula = rs.getString("formula");
            String name = rs.getString("compound_name");
            String cas_id = rs.getString("cas_id");
            int charge_type = rs.getInt("charge_type");
            int charge_number = rs.getInt("charge_number");
            int formula_type_int = rs.getInt("formula_type_int");
            int compound_type = rs.getInt("compound_type");
            int compound_status = rs.getInt("compound_status");
            String kegg_id = rs.getString("kegg_id");
            String lm_id = rs.getString("lm_id");
            String hmdb_id = rs.getString("hmdb_id");
            String metlin_id = rs.getString("agilent_id");
            Integer pc_id = rs.getInt("pc_id");
            Integer chebi_id = rs.getInt("chebi_id");
            String knapsack_id = rs.getString("knapsack_id");
            Integer npatlas_id = rs.getInt("npatlas_id");
            String in_house_id = rs.getString("in_house_id");
            String aspergillus_id = rs.getString("aspergillus_id");
            Integer fahfa_id = rs.wasNull() ? null : rs.getInt("fahfa_id");
            Integer oh_position = rs.wasNull() ? null : rs.getInt("oh_position");

            String mesh_nomenclature = rs.getString("mesh_nomenclature");
            String iupac_classification = rs.getString("iupac_classification");
            String aspergillus_web_name = rs.getString("aspergillus_web_name");
            Integer MINE_id = null;
            // Variables for Structure
            String inchi = rs.getString("inchi");
            String inchi_key = rs.getString("inchi_key");
            String smiles = rs.getString("smiles");

            // Variables forLipid Classification
            String lipid_type = rs.getString("lipid_type");
            Integer num_chains = rs.wasNull() ? null : rs.getInt("num_chains");
            Integer number_carbons = rs.wasNull() ? null : rs.getInt("number_carbons");
            Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");

            // Variables for LM Classification
            String category = rs.getString("category");
            String main_class = rs.getString("main_class");
            String sub_class = rs.getString("sub_class");
            String class_level4 = rs.getString("class_level4");

            Structure structure = new Structure(inchi, inchi_key, smiles);

            Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);

            LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);

            List<Classyfire_Classification> classyfire_classifications = getClassyfire_ClassificationByCompound_id(compound_id);

            List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

            compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int, compound_type,
                    compound_status, charge_type, charge_number,
                    lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                    aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                    fahfa_id, oh_position,
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
     *
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
                String kingdom = rs.getString("kingdom");
                String super_class = rs.getString("super_class");
                String main_class = rs.getString("main_class");
                String sub_class = rs.getString("sub_class");
                String direct_parent = rs.getString("direct_parent");
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
     *
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
                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");
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
     *
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
                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");
                structure = new Structure(inchi, inchi_key, smiles);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return structure;
    }

    /**
     * Return the number of peaks that has an mz within an specified window and
     * from a specific compound.
     *
     * @param compound_id
     * @param peak_mass
     * @param tolerance
     *
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
     * Get the corresponding msms of a compound with id compound_id and ionMode.
     *
     * @param compound_id
     * @param ionMode
     *
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
     *
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

        sql = QueryConstructor.createSQLCompoundViewWithDBIdsWhereCondition();
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
                int compound_id = rs.getInt("compound_id");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
                String name = rs.getString("compound_name");
                String cas_id = rs.getString("cas_id");
                int charge_type = rs.getInt("charge_type");
                int charge_number = rs.getInt("charge_number");
                int formula_type_int = rs.getInt("formula_type_int");
                int compound_type = rs.getInt("compound_type");
                int compound_status = rs.getInt("compound_status");
                String kegg_id = rs.getString("kegg_id");
                String lm_id = rs.getString("lm_id");
                String hmdb_id = rs.getString("hmdb_id");
                String metlin_id = rs.getString("agilent_id");
                Integer pc_id = rs.getInt("pc_id");
                Integer chebi_id = rs.getInt("chebi_id");
                String knapsack_id = rs.getString("knapsack_id");
                Integer npatlas_id = rs.getInt("npatlas_id");
                String in_house_id = rs.getString("in_house_id");
                String aspergillus_id = rs.getString("aspergillus_id");
                Integer fahfa_id = rs.wasNull() ? null : rs.getInt("fahfa_id");
                Integer oh_position = rs.wasNull() ? null : rs.getInt("oh_position");

                String mesh_nomenclature = rs.getString("mesh_nomenclature");
                String iupac_classification = rs.getString("iupac_classification");
                String aspergillus_web_name = rs.getString("aspergillus_web_name");
                Integer MINE_id = null;
                // Variables for Structure
                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");

                // Variables forLipid Classification
                String lipid_type = rs.getString("lipid_type");
                Integer num_chains = rs.wasNull() ? null : rs.getInt("num_chains");
                Integer number_carbons = rs.wasNull() ? null : rs.getInt("number_carbons");
                Integer double_bonds = rs.wasNull() ? null : rs.getInt("double_bonds");

                // Variables for LM Classification
                String category = rs.getString("category");
                String main_class = rs.getString("main_class");
                String sub_class = rs.getString("sub_class");
                String class_level4 = rs.getString("class_level4");
                Structure structure = new Structure(inchi, inchi_key, smiles);

                Lipids_Classification lipids_classification = new Lipids_Classification(lipid_type, num_chains, number_carbons, double_bonds);

                LM_Classification lm_classification = new LM_Classification(category, main_class, sub_class, class_level4);

                //List<Classyfire_Classification> classyfire_classifications = getClassyfire_ClassificationByCompound_id(compound_id);
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);

                compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                        compound_type, compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
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
     *
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
                int compound_id = rs.getInt("compound_id");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
                String name = rs.getString("compound_name");
                String cas_id = null;
                int charge_type = rs.getInt("charge_type");
                int charge_number = rs.getInt("charge_number");
                int formula_type_int = rs.getInt("formula_type_int");
                int compound_type = 0;
                int compound_status = 3;
                Double np_likeness = rs.getDouble("np_likeness"); // NOT USED YET!
                np_likeness = rs.wasNull() ? null : np_likeness;
                Integer mine_id = rs.getInt("MINE_id");
                String kegg_id = null;
                String hmdb_id = null;
                String lm_id = null;
                String agilent_id = null;
                String in_house_id = null;
                String aspergillus_id = null;
                String mesh_nomenclature = null;
                Integer fahfa_id = null;
                Integer oh_position = null;
                String iupac_classification = null;
                String aspergillus_web_name = null;
                Integer pc_id = null;
                Integer chebi_id = null;
                String knapsack_id = null;
                Integer npatlas_id = null;

                String inchi = rs.getString("inchi");
                String inchi_key = rs.getString("inchi_key");
                String smiles = rs.getString("smiles");

                LM_Classification lm_classification = null;
                Lipids_Classification lipids_classification = null;
                List<Classyfire_Classification> classyfire_classifications = null;
                List<Pathway> pathways = null;
                Structure structure = new Structure(inchi, inchi_key, smiles);
                compound = new CMMCompound(compound_id, mass, formula, name, cas_id, formula_type_int,
                        compound_type, compound_status, charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, chebi_id, mine_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
                        structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                compoundsList.add(compound);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);

        }

        return compoundsList;
    }

    protected List<Peak> getPeaksFromMsms_id(int msms_id) {
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
                Double intensity = rs.getDouble("intensity");
                intensity = rs.wasNull() ? null : intensity;
                double mz = rs.getDouble("mz");
                mz = rs.wasNull() ? null : mz;
                peak = new Peak(mz, intensity, msms_id);
                peaks.add(peak);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return peaks;
    }

    /**
     * Method to find the names and compoundIds of the compounds present in the
     * experimental properties specified
     *
     * @param buffer 1: formic; 2: acetic
     * @param temperature 20; 25
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param includeAllESIModes
     *
     * @return
     *
     * @throws bufferTemperatureException
     */
    public Map<Integer, CEMSCompound> getCEMSCompoundsFromExperimentalConditions(
            Integer buffer, Integer temperature, Integer ionMode, Integer polarity,
            Boolean includeAllESIModes)
            throws bufferTemperatureException {
        // LinkedHashMap to preserve the order of the compounds
        Map<Integer, CEMSCompound> CEMSCompounds = new LinkedHashMap<>();
        CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = null;
        try {
            CE_Exp_properties.CE_BUFFER_ENUM ce_buffer = CE_Exp_properties.CE_BUFFER_ENUM.fromBufferCode(buffer);
            ce_exp_prop = CE_Exp_properties.CE_EXP_PROP_ENUM.fromBufferTemperatureIonModeAndPolarity(ce_buffer, temperature, ionMode, polarity);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        Integer codeExpProp = ce_exp_prop.getCode();

        ResultSet rs;
        PreparedStatement prepSt;
        String aliasCompoundsTable = "c";
        String aliasCEEffMobTable = ce_exp_prop.getViewName(includeAllESIModes);
        String queryCECompoundsExperimentalConditions = QueryConstructor.createSQLInnerJoinCE_eff_mob(aliasCEEffMobTable);
        if (!includeAllESIModes) {
            queryCECompoundsExperimentalConditions
                    = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, queryCECompoundsExperimentalConditions, ce_exp_prop.getCode());
            queryCECompoundsExperimentalConditions
                    = QueryConstructor.addFilterCEEffMobConditionsNotnullJDBC(aliasCEEffMobTable, queryCECompoundsExperimentalConditions);
        }

        queryCECompoundsExperimentalConditions
                = QueryConstructor.orderByCompoundId(aliasCompoundsTable, queryCECompoundsExperimentalConditions);

        try {
            prepSt = conn.prepareStatement(queryCECompoundsExperimentalConditions);
            rs = prepSt.executeQuery();
            CEMSCompounds = getCEMSCompoundsFromRS(ce_exp_prop, rs);
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return CEMSCompounds;
    }

    /**
     * Method to find the annotations for a CE MS Search using effective
     * mobilities.
     *
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param effMobs
     * @param effMobTolerance
     * @param effMobToleranceMode
     * @param buffer 1: formic; 2: acetic
     * @param temperature 20; 25
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @param includeAllESIModes
     *
     * @return
     *
     * @throws exceptions.bufferTemperatureException
     */
    public List<CEMSFeature> getCEAnnotationsFromMassesToleranceAndEffMobs(
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> effMobs,
            Integer effMobTolerance,
            String effMobToleranceMode,
            Integer buffer,
            Integer temperature,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts,
            Boolean includeAllESIModes) throws bufferTemperatureException {
        Double absMZTolerance;
        Double absEffMobTolerance;
        Integer ionModeForCEMSConditions;
        if (massesMode == 0) {
            ionModeForCEMSConditions = 1;
        } else {
            ionModeForCEMSConditions = ionMode;
        }

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();
        CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = null;
        try {
            CE_Exp_properties.CE_BUFFER_ENUM ce_buffer = CE_Exp_properties.CE_BUFFER_ENUM.fromBufferCode(buffer);
            ce_exp_prop = CE_Exp_properties.CE_EXP_PROP_ENUM.fromBufferTemperatureIonModeAndPolarity(ce_buffer, temperature, ionModeForCEMSConditions, polarity);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTableCompoundsPrecursorIons = "c";
        String aliasCEEffMobTable = ce_exp_prop.getViewName(includeAllESIModes);
        String aliasTableProductIons = "cepi";

        String startQueryPrecursorIons;
        String middleQueryPrecursorIons;
        String finalQueryPrecursorIons;

        String startQueryProductIons;
        String middleQueryProductIons;
        String finalQueryProductIons;
        String transformationType = "'Fragment'";
        //start the query
        startQueryPrecursorIons = QueryConstructor.createSQLInnerJoinCE_eff_mob(aliasCEEffMobTable);
        if (!includeAllESIModes) {
            startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        }
        startQueryPrecursorIons = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTableCompoundsPrecursorIons, startQueryPrecursorIons, chemAlphabet);

        startQueryProductIons = QueryConstructor.createSQLProductIonsJDBC(aliasCEEffMobTable);

        System.out.println("Eff Mob Query Precursor and Product Ions");
        Integer numMzs = mzs.size();
        Set<CEMSFragment> experimentalFragments = new TreeSet<>();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons;

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expEffMob;
            try {
                expEffMob = effMobs.get(i);
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expEffMob = null;
            }
            Double expRMT = null;
            Double expMT = null;
            Integer bge = null;
            Double RMT = null;
            Double MT = null;

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);
            setAnnotationsGroupByAdduct = new TreeSet<>();
            middleQueryPrecursorIons = QueryConstructor.addFilterEffMobsJDBC(aliasCEEffMobTable,
                    startQueryPrecursorIons);
            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                finalQueryPrecursorIons = QueryConstructor.addFilterMassesJDBC(aliasTableCompoundsPrecursorIons, middleQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByMassesJDBC(aliasTableCompoundsPrecursorIons, finalQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByEffMobsJDBC(aliasCEEffMobTable, finalQueryPrecursorIons);
                CEMSAnnotationsForOneAdduct = new TreeSet<>();
                try {
                    absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                    absEffMobTolerance = Utilities.calculateDeltaPercentage(expEffMob, effMobToleranceMode, (double) effMobTolerance);
                    prepSt = conn.prepareStatement(finalQueryPrecursorIons);
                    prepSt.setDouble(1, (expEffMob - absEffMobTolerance));
                    prepSt.setDouble(2, (expEffMob + absEffMobTolerance));
                    prepSt.setDouble(3, (massToSearch - absMZTolerance));
                    prepSt.setDouble(4, (massToSearch + absMZTolerance));
                    prepSt.setDouble(5, massToSearch);
                    prepSt.setDouble(6, expEffMob);
                    //start = System.currentTimeMillis();
                    rs = prepSt.executeQuery();
                    //end = System.currentTimeMillis();
                    //System.out.println("Time executing query: "+(end-start));
                    //JDBCcounter = JDBCcounter + (end - start);
                    CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromRS(inputMass, adduct,
                            expEffMob, expMT, expRMT, experimentalFragments, ce_exp_prop, rs, null);
                    if (CEMSAnnotationsForOneAdduct.size() > 0) {
                        CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                                adduct, CEMSAnnotationsForOneAdduct);
                        setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            CEMSAnnotationsProductIons = new TreeSet<>();
            middleQueryProductIons = QueryConstructor.addFilterEffMobsJDBC(aliasCEEffMobTable,
                    startQueryProductIons);
            finalQueryProductIons = QueryConstructor.addFilterMassesProductIons(aliasTableProductIons, middleQueryProductIons);
            finalQueryProductIons = QueryConstructor.addFilterTransformationTypeJDBC(aliasTableProductIons, finalQueryProductIons, transformationType);
            finalQueryProductIons = QueryConstructor.addOrderByMassesProductIonsJDBC(aliasTableProductIons, finalQueryProductIons);
            finalQueryProductIons = QueryConstructor.addOrderByEffMobsJDBC(aliasCEEffMobTable, finalQueryProductIons);

            double massToSearch;
            if (massesMode == 0) {
                massToSearch = mzInputMass + Constants.PROTON_WEIGHT;
            } else {
                massToSearch = mzInputMass;
            }

            try {
                absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                absEffMobTolerance = Utilities.calculateDeltaPercentage(expEffMob, effMobToleranceMode, (double) effMobTolerance);
                prepSt = conn.prepareStatement(finalQueryProductIons);
                prepSt.setDouble(1, (expEffMob - absEffMobTolerance));
                prepSt.setDouble(2, (expEffMob + absEffMobTolerance));
                prepSt.setDouble(3, (massToSearch - absMZTolerance));
                prepSt.setDouble(4, (massToSearch + absMZTolerance));
                prepSt.setDouble(5, massToSearch);
                prepSt.setDouble(6, expEffMob);
                //start = System.currentTimeMillis();
                rs = prepSt.executeQuery();
                //end = System.currentTimeMillis();
                //System.out.println("Time executing query: "+(end-start));
                //JDBCcounter = JDBCcounter + (end - start);

                CEMSAnnotationsProductIons = getCEMSAnnotationsProductIonFromRS(massToSearch, expEffMob, expMT, expRMT, rs);

            } catch (SQLException ex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            }

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge,
                    experimentalFragments, ionModeForCEMSConditions, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    /**
     * Method to find the annotations for a CE MS Search using effective
     * mobilities.
     *
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param RMTs
     * @param RMTTolerance
     * @param RMTToleranceMode
     * @param buffer 1: formic; 2: acetic
     * @param temperature 20; 25
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @param bge_compound_id compound id of the bge
     * @param includeAllESIModes
     *
     * @return
     *
     * @throws exceptions.bufferTemperatureException
     */
    public List<CEMSFeature> getCEAnnotationsFromMassesToleranceAndExpRMTs(
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> RMTs,
            Integer RMTTolerance,
            String RMTToleranceMode,
            Integer buffer,
            Integer temperature,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts,
            Integer bge_compound_id,
            Boolean includeAllESIModes) throws bufferTemperatureException {
        Double absMZTolerance;
        Double absRMTTolerance;

        Integer ionModeForCEMSConditions;
        if (massesMode == 0) {
            ionModeForCEMSConditions = 1;
        } else {
            ionModeForCEMSConditions = ionMode;
        }

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();
        CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = null;
        try {
            CE_Exp_properties.CE_BUFFER_ENUM ce_buffer = CE_Exp_properties.CE_BUFFER_ENUM.fromBufferCode(buffer);
            ce_exp_prop = CE_Exp_properties.CE_EXP_PROP_ENUM.fromBufferTemperatureIonModeAndPolarity(ce_buffer, temperature, ionModeForCEMSConditions, polarity);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTableCompoundsPrecursorIons = "c";
        String aliasCEEffMobTable = "ce_eff_mob";
        String aliasCEExpPropMetaTable = "ce_exp_prop_md";
        String aliasTableProductIons = "cepi";

        String startQueryPrecursorIons;
        String middleQueryPrecursorIons;
        String finalQueryPrecursorIons;

        String startQueryProductIons;
        String middleQueryProductIons;
        String finalQueryProductIons;
        String transformationType = "'Fragment'";
        Integer sampleType = 1;
        //start the query
        startQueryPrecursorIons = QueryConstructor.createSQLInnerJoinCE_exp_prop_metadata(aliasCEEffMobTable);
        if (!includeAllESIModes) {
            startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        }
        startQueryPrecursorIons = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTableCompoundsPrecursorIons, startQueryPrecursorIons, chemAlphabet);
        startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        startQueryPrecursorIons = QueryConstructor.addFilterExpPropMetaSampleTypeJDBC(aliasCEExpPropMetaTable, startQueryPrecursorIons, sampleType);
        startQueryPrecursorIons = QueryConstructor.addFilterExpPropMetaBGEJDBC(aliasCEExpPropMetaTable, startQueryPrecursorIons, bge_compound_id);

        startQueryProductIons = QueryConstructor.createSQLProductIonsEXPRMTJDBC(aliasCEEffMobTable);
        startQueryProductIons = QueryConstructor.addFilterExpPropMetaSampleTypeJDBC(aliasCEExpPropMetaTable, startQueryProductIons, sampleType);
        startQueryProductIons = QueryConstructor.addFilterExpPropMetaBGEJDBC(aliasCEExpPropMetaTable, startQueryProductIons, bge_compound_id);

        System.out.println("Experimental RMT Precursor and Product Ions");
        Integer numMzs = mzs.size();
        Set<CEMSFragment> experimentalFragments = new TreeSet<>();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons;

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expRMT;
            try {
                expRMT = RMTs.get(i);
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expRMT = null;
            }
            Double expEffMob = null;
            Double expMT = null;

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);
            setAnnotationsGroupByAdduct = new TreeSet<>();
            middleQueryPrecursorIons = QueryConstructor.addFilterRMTsJDBC(aliasCEExpPropMetaTable,
                    startQueryPrecursorIons);
            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                finalQueryPrecursorIons = QueryConstructor.addFilterMassesJDBC(aliasTableCompoundsPrecursorIons, middleQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByMassesJDBC(aliasTableCompoundsPrecursorIons, finalQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByRMTsJDBC(aliasCEExpPropMetaTable, finalQueryPrecursorIons);
                CEMSAnnotationsForOneAdduct = new TreeSet<>();
                try {
                    absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                    absRMTTolerance = Utilities.calculateDeltaPercentage(expRMT, RMTToleranceMode, (double) RMTTolerance);
                    prepSt = conn.prepareStatement(finalQueryPrecursorIons);
                    prepSt.setDouble(1, (expRMT - absRMTTolerance));
                    prepSt.setDouble(2, (expRMT + absRMTTolerance));
                    prepSt.setDouble(3, (massToSearch - absMZTolerance));
                    prepSt.setDouble(4, (massToSearch + absMZTolerance));
                    prepSt.setDouble(5, massToSearch);
                    prepSt.setDouble(6, expRMT);
                    //start = System.currentTimeMillis();
                    rs = prepSt.executeQuery();
                    //end = System.currentTimeMillis();
                    //System.out.println("Time executing query: "+(end-start));
                    //JDBCcounter = JDBCcounter + (end - start);
                    CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromRS(inputMass, adduct,
                            expEffMob, expMT, expRMT, experimentalFragments, ce_exp_prop, rs, null);

                } catch (SQLException ex) {
                    Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (CEMSAnnotationsForOneAdduct.size() > 0) {
                    CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                            adduct, CEMSAnnotationsForOneAdduct);
                    setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                }
            }

            CEMSAnnotationsProductIons = new TreeSet<>();
            middleQueryProductIons = QueryConstructor.addFilterRMTsJDBC(aliasCEExpPropMetaTable,
                    startQueryProductIons);
            finalQueryProductIons = QueryConstructor.addFilterMassesProductIons(aliasTableProductIons, middleQueryProductIons);
            finalQueryProductIons = QueryConstructor.addFilterTransformationTypeJDBC(aliasTableProductIons, finalQueryProductIons, transformationType);
            finalQueryProductIons = QueryConstructor.addOrderByMassesProductIonsJDBC(aliasTableProductIons, finalQueryProductIons);
            finalQueryProductIons = QueryConstructor.addOrderByRMTsJDBC(aliasCEExpPropMetaTable, finalQueryProductIons);

            double massToSearch;
            if (massesMode == 0) {
                massToSearch = mzInputMass + Constants.PROTON_WEIGHT;
            } else {
                massToSearch = mzInputMass;
            }

            try {
                absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                absRMTTolerance = Utilities.calculateDeltaPercentage(expRMT, RMTToleranceMode, (double) RMTTolerance);
                prepSt = conn.prepareStatement(finalQueryProductIons);
                prepSt.setDouble(1, (expRMT - absRMTTolerance));
                prepSt.setDouble(2, (expRMT + absRMTTolerance));
                prepSt.setDouble(3, (massToSearch - absMZTolerance));
                prepSt.setDouble(4, (massToSearch + absMZTolerance));
                prepSt.setDouble(5, massToSearch);
                prepSt.setDouble(6, expRMT);
                //start = System.currentTimeMillis();
                rs = prepSt.executeQuery();
                //end = System.currentTimeMillis();
                //System.out.println("Time executing query: "+(end-start));
                //JDBCcounter = JDBCcounter + (end - start);

                CEMSAnnotationsProductIons = getCEMSAnnotationsProductIonFromRS(massToSearch, expEffMob, expMT, expRMT, rs);

            } catch (SQLException ex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            }

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge_compound_id,
                    experimentalFragments, ionModeForCEMSConditions, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    /**
     * Method to find the annotations for a CE MS Search using effective
     * mobilities.
     *
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param RMTs
     * @param RMTTolerance
     * @param RMTToleranceMode
     * @param expFragments
     * @param buffer 1: formic; 2: acetic
     * @param temperature 20; 25
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @param bge_compound_id compound id of the bge
     * @param ionSourceVoltage
     * @param includeAllESIModes
     *
     * @return
     *
     * @throws exceptions.bufferTemperatureException
     */
    public List<CEMSFeature> getCEAnnotationsFromMassesToleranceExpRMTsAndFragments(
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> RMTs,
            Integer RMTTolerance,
            String RMTToleranceMode,
            List<Set<CEMSFragment>> expFragments,
            Integer buffer,
            Integer temperature,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts,
            Integer bge_compound_id,
            Integer ionSourceVoltage,
            Boolean includeAllESIModes) throws bufferTemperatureException {
        Double absMZTolerance;
        Double absRMTTolerance;

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();
        CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = null;
        Integer ionModeForCEMSConditions;
        if (massesMode == 0) {
            ionModeForCEMSConditions = 1;
        } else {
            ionModeForCEMSConditions = ionMode;
        }
        try {
            CE_Exp_properties.CE_BUFFER_ENUM ce_buffer = CE_Exp_properties.CE_BUFFER_ENUM.fromBufferCode(buffer);
            ce_exp_prop = CE_Exp_properties.CE_EXP_PROP_ENUM.fromBufferTemperatureIonModeAndPolarity(ce_buffer, temperature, ionModeForCEMSConditions, polarity);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTableCompoundsPrecursorIons = "c";
        String aliasCEEffMobTable = "ce_eff_mob";
        String aliasCEExpPropMetaTable = "ce_exp_prop_md";

        String startQueryPrecursorIons;
        String middleQueryPrecursorIons;
        String finalQueryPrecursorIons;

        Integer sampleType = 1;
        //start the query
        startQueryPrecursorIons = QueryConstructor.createSQLInnerJoinCE_exp_prop_metadata(aliasCEEffMobTable);
        if (!includeAllESIModes) {
            startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        }
        startQueryPrecursorIons = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTableCompoundsPrecursorIons, startQueryPrecursorIons, chemAlphabet);
        startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        startQueryPrecursorIons = QueryConstructor.addFilterExpPropMetaSampleTypeJDBC(aliasCEExpPropMetaTable, startQueryPrecursorIons, sampleType);
        startQueryPrecursorIons = QueryConstructor.addFilterExpPropMetaBGEJDBC(aliasCEExpPropMetaTable, startQueryPrecursorIons, bge_compound_id);
        System.out.println("Targeted Experimental RMTs");
        Integer numMzs = mzs.size();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons;

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expRMT;
            Set<CEMSFragment> experimentalFragments;
            try {
                expRMT = RMTs.get(i);
                experimentalFragments = expFragments.get(i);
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expRMT = null;
                experimentalFragments = new TreeSet<>();
            }
            Double expEffMob = null;
            Double expMT = null;

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);
            setAnnotationsGroupByAdduct = new TreeSet<>();
            middleQueryPrecursorIons = QueryConstructor.addFilterRMTsJDBC(aliasCEExpPropMetaTable,
                    startQueryPrecursorIons);
            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                finalQueryPrecursorIons = QueryConstructor.addFilterMassesJDBC(aliasTableCompoundsPrecursorIons, middleQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByMassesJDBC(aliasTableCompoundsPrecursorIons, finalQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByRMTsJDBC(aliasCEExpPropMetaTable, finalQueryPrecursorIons);
                CEMSAnnotationsForOneAdduct = new TreeSet<>();
                try {
                    absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                    absRMTTolerance = Utilities.calculateDeltaPercentage(expRMT, RMTToleranceMode, (double) RMTTolerance);
                    prepSt = conn.prepareStatement(finalQueryPrecursorIons);
                    prepSt.setDouble(1, (expRMT - absRMTTolerance));
                    prepSt.setDouble(2, (expRMT + absRMTTolerance));
                    prepSt.setDouble(3, (massToSearch - absMZTolerance));
                    prepSt.setDouble(4, (massToSearch + absMZTolerance));
                    prepSt.setDouble(5, massToSearch);
                    prepSt.setDouble(6, expRMT);
                    //start = System.currentTimeMillis();
                    rs = prepSt.executeQuery();
                    //end = System.currentTimeMillis();
                    //System.out.println("Time executing query: "+(end-start));
                    //JDBCcounter = JDBCcounter + (end - start);
                    CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromRS(inputMass, adduct,
                            expEffMob, expMT, expRMT, experimentalFragments, ce_exp_prop, rs, ionSourceVoltage);

                } catch (SQLException ex) {
                    Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (CEMSAnnotationsForOneAdduct.size() > 0) {
                    CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                            adduct, CEMSAnnotationsForOneAdduct);
                    setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                }
            }
            CEMSAnnotationsProductIons = new TreeSet<>();

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge_compound_id,
                    experimentalFragments, ionModeForCEMSConditions, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    /**
     * Method to find the annotations for a CE MS Search using effective
     * mobilities.
     *
     * @param mzs
     * @param mzTolerance
     * @param mzToleranceMode
     * @param effMobs
     * @param effMobTolerance
     * @param effMobToleranceMode
     * @param expFragments
     * @param buffer 1: formic; 2: acetic
     * @param temperature 20; 25
     * @param chemAlphabet 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4 ALL,
     * 5 ALLD
     * @param includeDeuterium
     * @param massesMode 1: neutral masses; 2: mzs
     * @param ionMode 1: positive; 2: negative
     * @param polarity 1: direct; 2: reverse
     * @param adducts
     * @param bge_compound_id compound id of the bge
     * @param ionSourceVoltage
     * @param includeAllESIModes
     *
     * @return
     *
     * @throws exceptions.bufferTemperatureException
     */
    public List<CEMSFeature> getCEAnnotationsFromMassesToleranceEffMobsAndFragments(
            List<Double> mzs,
            Integer mzTolerance,
            Integer mzToleranceMode,
            List<Double> effMobs,
            Integer effMobTolerance,
            String effMobToleranceMode,
            List<Set<CEMSFragment>> expFragments,
            Integer buffer,
            Integer temperature,
            Integer chemAlphabet,
            Boolean includeDeuterium,
            Integer massesMode,
            Integer ionMode,
            Integer polarity,
            List<String> adducts,
            Integer bge_compound_id,
            Integer ionSourceVoltage,
            Boolean includeAllESIModes) throws bufferTemperatureException {
        Double absMZTolerance;
        Double absEffMobTolerance;

        List<CEMSFeature> cemsfeatures = new NoDuplicatesList<>();
        CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = null;
        Integer ionModeForCEMSConditions;
        if (massesMode == 0) {
            ionModeForCEMSConditions = 1;
        } else {
            ionModeForCEMSConditions = ionMode;
        }
        try {
            CE_Exp_properties.CE_BUFFER_ENUM ce_buffer = CE_Exp_properties.CE_BUFFER_ENUM.fromBufferCode(buffer);
            ce_exp_prop = CE_Exp_properties.CE_EXP_PROP_ENUM.fromBufferTemperatureIonModeAndPolarity(ce_buffer, temperature, ionModeForCEMSConditions, polarity);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        Map<String, String> provisionalMap;
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTableCompoundsPrecursorIons = "c";
        String aliasCEEffMobTable = ce_exp_prop.getViewName(includeAllESIModes);
        String aliasTableProductIons = "cepi";

        String startQueryPrecursorIons;
        String middleQueryPrecursorIons;
        String finalQueryPrecursorIons;

        //start the query
        startQueryPrecursorIons = QueryConstructor.createSQLInnerJoinCE_eff_mob(aliasCEEffMobTable);
        if (!includeAllESIModes) {
            startQueryPrecursorIons = QueryConstructor.addFilterCEEffMobConditionsJDBC(aliasCEEffMobTable, startQueryPrecursorIons, ce_exp_prop.getCode());
        }
        startQueryPrecursorIons = QueryConstructor.addFilterIntegerFormulaTypeJDBC(aliasTableCompoundsPrecursorIons, startQueryPrecursorIons, chemAlphabet);

        System.out.println("Eff Mob Query Precursor and Product Ions");
        Integer numMzs = mzs.size();
        Set<CEMSAnnotationsGroupByAdduct> setAnnotationsGroupByAdduct;
        Set<CEMSAnnotation> CEMSAnnotationsForOneAdduct;
        Set<CEMSAnnotationFragment> CEMSAnnotationsProductIons;

        for (int i = 0; i < numMzs; i++) {
            Double inputMass = mzs.get(i);
            Double expEffMob;
            Set<CEMSFragment> experimentalFragments;
            try {
                expEffMob = effMobs.get(i);
                experimentalFragments = expFragments.get(i);
            } catch (Exception exceptIndex) {
                Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, exceptIndex);
                expEffMob = null;
                experimentalFragments = new TreeSet<>();
            }
            Double expRMT = null;
            Double expMT = null;
            Integer bge = null;

            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesMode, ionMode);
            setAnnotationsGroupByAdduct = new TreeSet<>();
            middleQueryPrecursorIons = QueryConstructor.addFilterEffMobsJDBC(aliasCEEffMobTable,
                    startQueryPrecursorIons);
            for (String adduct : adducts) {
                double massToSearch = AdductProcessing.getMassToSearch(mzInputMass, adduct, ionMode);
                finalQueryPrecursorIons = QueryConstructor.addFilterMassesJDBC(aliasTableCompoundsPrecursorIons, middleQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByMassesJDBC(aliasTableCompoundsPrecursorIons, finalQueryPrecursorIons);
                finalQueryPrecursorIons = QueryConstructor.addOrderByEffMobsJDBC(aliasCEEffMobTable, finalQueryPrecursorIons);
                CEMSAnnotationsForOneAdduct = new TreeSet<>();
                try {
                    absMZTolerance = Utilities.calculateDeltaPPM(massToSearch, mzToleranceMode, (double) mzTolerance);
                    absEffMobTolerance = Utilities.calculateDeltaPercentage(expEffMob, effMobToleranceMode, (double) effMobTolerance);
                    prepSt = conn.prepareStatement(finalQueryPrecursorIons);
                    prepSt.setDouble(1, (expEffMob - absEffMobTolerance));
                    prepSt.setDouble(2, (expEffMob + absEffMobTolerance));
                    prepSt.setDouble(3, (massToSearch - absMZTolerance));
                    prepSt.setDouble(4, (massToSearch + absMZTolerance));
                    prepSt.setDouble(5, massToSearch);
                    prepSt.setDouble(6, expEffMob);
                    //start = System.currentTimeMillis();
                    rs = prepSt.executeQuery();
                    //end = System.currentTimeMillis();
                    //System.out.println("Time executing query: "+(end-start));
                    //JDBCcounter = JDBCcounter + (end - start);
                    CEMSAnnotationsForOneAdduct = getCEMSAnnotationsFromRS(inputMass, adduct,
                            expEffMob, expMT, expRMT, experimentalFragments, ce_exp_prop, rs, ionSourceVoltage);

                } catch (SQLException ex) {
                    Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (CEMSAnnotationsForOneAdduct.size() > 0) {
                    CEMSAnnotationsGroupByAdduct cemsAnnotationsGroupByAdduct = new CEMSAnnotationsGroupByAdduct(
                            adduct, CEMSAnnotationsForOneAdduct);
                    setAnnotationsGroupByAdduct.add(cemsAnnotationsGroupByAdduct);
                }
            }

            CEMSAnnotationsProductIons = new TreeSet<>();

            CEMSFeature cemsFeature = new CEMSFeature(inputMass, expEffMob, expMT, expRMT, bge,
                    experimentalFragments, ionModeForCEMSConditions, polarity,
                    setAnnotationsGroupByAdduct, CEMSAnnotationsProductIons);
            cemsfeatures.add(cemsFeature);
        }
        return cemsfeatures;
    }

    private Map<Integer, CEMSCompound> getCEMSCompoundsFromRS(CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop,
            ResultSet resultSetAnnotations) {
        Map<Integer, CEMSCompound> CEMSCompoundsFromResultSet = new LinkedHashMap<>();
        try {
            while (resultSetAnnotations.next()) {

                Double effMob = null;
                Double MT = null;
                Double RMT = null;
                int compound_id = resultSetAnnotations.getInt("compound_id");
                double mass = resultSetAnnotations.getDouble("mass");
                String formula = resultSetAnnotations.getString("formula");
                String name = resultSetAnnotations.getString("compound_name");
                String cas_id = resultSetAnnotations.getString("cas_id");
                int formula_type = resultSetAnnotations.getInt("formula_type_int");
                int compound_type = resultSetAnnotations.getInt("compound_type");
                int compound_status = resultSetAnnotations.getInt("compound_status");
                int charge_type = resultSetAnnotations.getInt("charge_type");
                int charge_number = resultSetAnnotations.getInt("charge_number");
                String kegg_id = resultSetAnnotations.getString("kegg_id");
                String lm_id = resultSetAnnotations.getString("lm_id");
                String hmdb_id = resultSetAnnotations.getString("hmdb_id");
                String metlin_id = resultSetAnnotations.getString("agilent_id");
                Integer pc_id = resultSetAnnotations.getInt("pc_id");
                Integer chebi_id = resultSetAnnotations.getInt("chebi_id");
                String knapsack_id = resultSetAnnotations.getString("knapsack_id");
                Integer npatlas_id = resultSetAnnotations.getInt("npatlas_id");
                String in_house_id = resultSetAnnotations.getString("in_house_id");
                String aspergillus_id = resultSetAnnotations.getString("aspergillus_id");
                Integer fahfa_id = resultSetAnnotations.wasNull() ? null : resultSetAnnotations.getInt("fahfa_id");
                Integer oh_position = resultSetAnnotations.wasNull() ? null : resultSetAnnotations.getInt("oh_position");
                String mesh_nomenclature = resultSetAnnotations.getString("mesh_nomenclature");
                String iupac_classification = resultSetAnnotations.getString("iupac_classification");
                String aspergillus_web_name = resultSetAnnotations.getString("aspergillus_web_name");
                Integer MINE_id = null;
                int cembio_id = resultSetAnnotations.getInt("cembio_id");
                try {
                    effMob = resultSetAnnotations.getDouble("eff_mobility");
                    effMob = resultSetAnnotations.wasNull() ? null : effMob;
                } catch (SQLException ex) {
                }
                try {
                    RMT = resultSetAnnotations.getDouble("relative_MT");
                    RMT = resultSetAnnotations.wasNull() ? null : RMT;
                } catch (SQLException ex) {
                }

                Set<CEMSFragment> productIons = getProductIonsByCompound_id(compound_id, null, null, null);

                // Variables for Structure
                String inchi = resultSetAnnotations.getString("inchi");
                String inchi_key = resultSetAnnotations.getString("inchi_key");
                String smiles = resultSetAnnotations.getString("smiles");
                Structure structure = new Structure(inchi, inchi_key, smiles);
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);
                CEMSCompound cemsCompound = new CEMSCompound(ce_exp_prop, effMob, MT, RMT, productIons,
                        compound_id, mass, formula, name, cas_id, formula_type, compound_type, compound_status,
                        charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
                        structure, pathways);
                CEMSCompoundsFromResultSet.put(compound_id, cemsCompound);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return CEMSCompoundsFromResultSet;
    }

    private Set<CEMSAnnotation> getCEMSAnnotationsFromRS(Double massToSearch,
            String adduct, Double expEffMob, Double expMT, Double expRMT,
            Set<CEMSFragment> experimentalFragments,
            CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop,
            ResultSet resultSetAnnotations, Integer ionSourceVoltage) {
        Set<CEMSAnnotation> CEMSAnnotationsFromResultSet = new TreeSet<>();
        try {
            while (resultSetAnnotations.next()) {
                Double effMob = null;
                Double MT = null;
                Double RMT = null;
                int compound_id = resultSetAnnotations.getInt("compound_id");
                double mass = resultSetAnnotations.getDouble("mass");
                String formula = resultSetAnnotations.getString("formula");
                String name = resultSetAnnotations.getString("compound_name");
                String cas_id = resultSetAnnotations.getString("cas_id");
                int formula_type = resultSetAnnotations.getInt("formula_type_int");
                int compound_type = resultSetAnnotations.getInt("compound_type");
                int compound_status = resultSetAnnotations.getInt("compound_status");
                int charge_type = resultSetAnnotations.getInt("charge_type");
                int charge_number = resultSetAnnotations.getInt("charge_number");
                String kegg_id = resultSetAnnotations.getString("kegg_id");
                String lm_id = resultSetAnnotations.getString("lm_id");
                String hmdb_id = resultSetAnnotations.getString("hmdb_id");
                String metlin_id = resultSetAnnotations.getString("agilent_id");
                Integer pc_id = resultSetAnnotations.getInt("pc_id");
                Integer chebi_id = resultSetAnnotations.getInt("chebi_id");
                String knapsack_id = resultSetAnnotations.getString("knapsack_id");
                Integer npatlas_id = resultSetAnnotations.getInt("npatlas_id");
                String in_house_id = resultSetAnnotations.getString("in_house_id");
                String aspergillus_id = resultSetAnnotations.getString("aspergillus_id");
                Integer fahfa_id = resultSetAnnotations.wasNull() ? null : resultSetAnnotations.getInt("fahfa_id");
                Integer oh_position = resultSetAnnotations.wasNull() ? null : resultSetAnnotations.getInt("oh_position");
                String mesh_nomenclature = resultSetAnnotations.getString("mesh_nomenclature");
                String iupac_classification = resultSetAnnotations.getString("iupac_classification");
                String aspergillus_web_name = resultSetAnnotations.getString("aspergillus_web_name");

                Integer MINE_id = null;
                int cembio_id = resultSetAnnotations.getInt("cembio_id");
                try {
                    effMob = resultSetAnnotations.getDouble("eff_mobility");
                } catch (SQLException ex) {
                }
                try {
                    RMT = resultSetAnnotations.getDouble("relative_MT");
                } catch (SQLException ex) {
                }
                Set<CEMSFragment> productIons = getProductIonsByCompound_id(compound_id, MT, RMT, ionSourceVoltage);

                // Variables for Structure
                String inchi = resultSetAnnotations.getString("inchi");
                String inchi_key = resultSetAnnotations.getString("inchi_key");
                String smiles = resultSetAnnotations.getString("smiles");
                Structure structure = new Structure(inchi, inchi_key, smiles);
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);
                CEMSAnnotation cemsAnnotation = new CEMSAnnotation(massToSearch, adduct, expEffMob, expMT, expRMT,
                        experimentalFragments, ce_exp_prop, effMob, MT, RMT, productIons,
                        compound_id, mass, formula, name, cas_id, formula_type, compound_type, compound_status,
                        charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
                        structure, pathways, true);
                CEMSAnnotationsFromResultSet.add(cemsAnnotation);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return CEMSAnnotationsFromResultSet;
    }

    private CEMSCompound getCEMSCompoundFromRS(ResultSet resultSet) {
        CEMSCompound CEMSCompoundFromRS = null;
        try {
            while (resultSet.next()) {
                Double effMob = null;
                Double MT = null;
                Double RMT = null;
                int compound_id = resultSet.getInt("compound_id");
                double mass = resultSet.getDouble("mass");
                String formula = resultSet.getString("formula");
                String name = resultSet.getString("compound_name");
                String cas_id = resultSet.getString("cas_id");
                int formula_type = resultSet.getInt("formula_type_int");
                int compound_type = resultSet.getInt("compound_type");
                int compound_status = resultSet.getInt("compound_status");
                int charge_type = resultSet.getInt("charge_type");
                int charge_number = resultSet.getInt("charge_number");
                String kegg_id = resultSet.getString("kegg_id");
                String lm_id = resultSet.getString("lm_id");
                String hmdb_id = resultSet.getString("hmdb_id");
                String metlin_id = resultSet.getString("agilent_id");
                Integer pc_id = resultSet.getInt("pc_id");
                Integer chebi_id = resultSet.getInt("chebi_id");
                String knapsack_id = resultSet.getString("knapsack_id");
                Integer npatlas_id = resultSet.getInt("npatlas_id");
                String in_house_id = resultSet.getString("in_house_id");
                String aspergillus_id = resultSet.getString("aspergillus_id");
                Integer fahfa_id = resultSet.wasNull() ? null : resultSet.getInt("fahfa_id");
                Integer oh_position = resultSet.wasNull() ? null : resultSet.getInt("oh_position");
                String mesh_nomenclature = resultSet.getString("mesh_nomenclature");
                String iupac_classification = resultSet.getString("iupac_classification");
                String aspergillus_web_name = resultSet.getString("aspergillus_web_name");

                Integer MINE_id = null;
                int cembio_id = resultSet.getInt("cembio_id");
                try {
                    effMob = resultSet.getDouble("eff_mobility");
                    effMob = resultSet.wasNull() ? null : effMob;
                } catch (SQLException ex) {
                }
                try {
                    RMT = resultSet.getDouble("relative_MT");
                    RMT = resultSet.wasNull() ? null : RMT;
                } catch (SQLException ex) {
                }
                Set<CEMSFragment> productIons = getProductIonsByCompound_id(compound_id, MT, RMT, null);

                // Variables for Structure
                String inchi = resultSet.getString("inchi");
                String inchi_key = resultSet.getString("inchi_key");
                String smiles = resultSet.getString("smiles");
                Structure structure = new Structure(inchi, inchi_key, smiles);
                List<Pathway> pathways = getPathwaysByCompound_id(compound_id);
                CE_Exp_properties.CE_EXP_PROP_ENUM ce_exp_prop = fromIntegerCode(resultSet.getInt("ce_exp_prop_id"));
                CEMSCompoundFromRS = new CEMSCompound(ce_exp_prop, effMob, MT, RMT, productIons,
                        compound_id, mass, formula, name, cas_id, formula_type, compound_type, compound_status,
                        charge_type, charge_number,
                        lm_id, kegg_id, hmdb_id, metlin_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                        aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                        fahfa_id, oh_position,
                        structure, pathways);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (bufferTemperatureException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return CEMSCompoundFromRS;
    }

    private Set<CEMSFragment> getProductIonFromRS(ResultSet resultSetAnnotations, Double MT, Double RMT) {
        Set<CEMSFragment> CEMSProductIonsFromResultSet = new TreeSet<>();

        try {
            while (resultSetAnnotations.next()) {

                Integer compound_id_own = resultSetAnnotations.getInt("compound_id_own");
                compound_id_own = resultSetAnnotations.wasNull() ? null : compound_id_own;
                Double mass = resultSetAnnotations.getDouble("ce_product_ion_mz");
                String name = resultSetAnnotations.getString("ce_product_ion_name");
                String transformation_type = resultSetAnnotations.getString("ce_transformation_type");
                Integer ion_source_voltage = resultSetAnnotations.getInt("ion_source_voltage");
                ion_source_voltage = resultSetAnnotations.wasNull() ? null : ion_source_voltage;
                Double ce_product_ion_intensity = resultSetAnnotations.getDouble("ce_product_ion_intensity");
                ce_product_ion_intensity = resultSetAnnotations.wasNull() ? null : ce_product_ion_intensity;
                Double effMob = resultSetAnnotations.getDouble("eff_mobility");
                effMob = resultSetAnnotations.wasNull() ? null : effMob;
                Integer cembio_id = resultSetAnnotations.getInt("cembio_id");
                CEMSCompound precursorIon = null;

                // Variables for Structure
                CEMSFragment cemsProductIon = new CEMSFragment(
                        effMob, MT, RMT, ion_source_voltage, mass, ce_product_ion_intensity, transformation_type, name, precursorIon);
                CEMSProductIonsFromResultSet.add(cemsProductIon);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return CEMSProductIonsFromResultSet;
    }

    private Set<CEMSAnnotationFragment> getCEMSAnnotationsProductIonFromRS(Double massToSearch, Double expEffMob,
            Double expMT, Double expRMT, ResultSet resultSetAnnotations) {
        Set<CEMSAnnotationFragment> CEMSProductIonAnnotationsFromResultSet = new TreeSet<>();

        try {
            while (resultSetAnnotations.next()) {
                Double effMob = null;
                Double MT = null;
                Double RMT = null;
                int compound_id_own = resultSetAnnotations.getInt("compound_id_own");
                double mass = resultSetAnnotations.getDouble("ce_product_ion_mz");
                String name = resultSetAnnotations.getString("ce_product_ion_name");
                String transformation_type = resultSetAnnotations.getString("ce_transformation_type");
                int ion_source_voltage = resultSetAnnotations.getInt("ion_source_voltage");
                double ce_product_ion_intensity = resultSetAnnotations.getDouble("ce_product_ion_intensity");
                int cembio_id = resultSetAnnotations.getInt("cembio_id");
                int compound_id_precursor_ion = resultSetAnnotations.getInt("ce_compound_id");
                try {
                    effMob = resultSetAnnotations.getDouble("eff_mobility");
                    effMob = resultSetAnnotations.wasNull() ? null : effMob;
                } catch (SQLException ex) {
                }
                try {
                    RMT = resultSetAnnotations.getDouble("relative_MT");
                    RMT = resultSetAnnotations.wasNull() ? null : RMT;
                } catch (SQLException ex) {
                }

                CEMSCompound precursorIon = null;
                String aliasCEEffMobTable = "ce_eff_mob";
                String queryPrecursorIons = QueryConstructor.createSQLInnerJoinCE_eff_mobByCompoundID(aliasCEEffMobTable);
                PreparedStatement prepSt = conn.prepareStatement(queryPrecursorIons);
                prepSt.setInt(1, compound_id_precursor_ion);
                ResultSet rs = prepSt.executeQuery();
                precursorIon = getCEMSCompoundFromRS(rs);

                // Variables for Structure
                CEMSAnnotationFragment cemsAnnotationProductIon = new CEMSAnnotationFragment(massToSearch, expEffMob, expMT, expRMT,
                        effMob, MT, RMT, ion_source_voltage, mass, ce_product_ion_intensity, transformation_type, name, precursorIon, true);
                CEMSProductIonAnnotationsFromResultSet.add(cemsAnnotationProductIon);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return CEMSProductIonAnnotationsFromResultSet;
    }

    private List<Link> getCompoundReactions(int compoundId) {
        String sqlreactions;
        ResultSet rs;
        PreparedStatement prepSt;
        sqlreactions = QueryConstructor.searchForCompoundsReactions(compoundId);

        List<Link> reactions = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sqlreactions);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                reactions.add(new Link(getString(rs, "reaction_id"), getString(rs, "reaction_id")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return reactions;
    }

    public CompoundView getCompound(int compoundId) {

        CompoundView cv = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForCompoundInCompoundView(compoundId);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                Integer identifier = getInt(rs, "compound_id");
                String cas_id = getString(rs, "cas_id");
                String name = getString(rs, "compound_name");
                String formula = getString(rs, "formula");
                Double mass = getDouble(rs, "mass");
                Integer charge_type = getInt(rs, "charge_type");
                Integer charge_number = getInt(rs, "charge_number");

                Double logP = getDouble(rs, "logP");

                String kegg_id = getString(rs, "kegg_id");
                String lm_id = getString(rs, "lm_id");
                String hmdb_id = getString(rs, "hmdb_id");
                String agilent_id = getString(rs, "agilent_id");

                Integer pc_id = getInt(rs, "pc_id");
                Integer chebi_id = getInt(rs, "chebi_id");
                Integer aspergillus_id = getInt(rs, "aspergillus_id");

                Integer oh_position = getInt(rs, "oh_position");

                String mesh_nomenclature = getString(rs, "mesh_nomenclature");
                String iupac_classification = getString(rs, "iupac_classification");
                String aspergillus_web_name = getString(rs, "aspergillus_web_name");

                String inchi = getString(rs, "inchi");
                String inchi_key = getString(rs, "inchi_key");
                String smiles = getString(rs, "smiles");

                Integer num_chains = getInt(rs, "num_chains");
                Integer number_carbons = getInt(rs, "number_carbons");
                Integer double_bonds = getInt(rs, "double_bonds");

                String category = getString(rs, "category");
                String main_class = getString(rs, "main_class");
                String sub_class = getString(rs, "sub_class");
                String class_level4 = getString(rs, "class_level4");

                java.util.Date created = getDate(rs, "created");
                java.util.Date last_updated = getDate(rs, "last_updated");

                String knapsack_id = getString(rs, "knapsack_id");

                String biological_activity = getString(rs, "biological_activity");

                Integer npatlas_id = getInt(rs, "npatlas_id");

                //List<Pathway> pathways = getPathwaysByCompound_id(compound_id);
                cv = new CompoundView(identifier, cas_id, name,
                        formula, mass, charge_type,
                        charge_number,
                        logP, kegg_id,
                        lm_id, hmdb_id, agilent_id,
                        pc_id, chebi_id,
                        aspergillus_id, oh_position,
                        mesh_nomenclature, iupac_classification,
                        aspergillus_web_name, inchi, inchi_key,
                        smiles, num_chains,
                        number_carbons, double_bonds, category,
                        main_class, sub_class, class_level4,
                        created.toString(), last_updated.toString(),
                        knapsack_id, npatlas_id, biological_activity,
                        getCompoundReactions(identifier),
                        getCompoundClassyfire(identifier),
                        getCompoundOntology(identifier),
                        getCompoundReferences(identifier),
                        getCompoundOrganisms(identifier)
                );

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return cv;
    }

    private List<Link> getCompoundClassyfire(int compoundId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForCompoundClassyfire(compoundId);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                links.add(new Link(ClassyfireNodeView.obtainNumberId(getString(rs, "node_id")), getString(rs, "node_name")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    private List<Link> getCompoundOntology(int compoundId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchforCompoundTerm(compoundId);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                links.add(new Link(getString(rs, "ontology_term_id"), getString(rs, "term")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    private List<Link> getCompoundOrganisms(int compoundId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchforCompoundOrganisms(compoundId);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {

                links.add(new Link(getInt(rs, "organism_id").toString(),
                        getString(rs, "organism_name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    private List<Link> getCompoundReferences(int compoundId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchforCompoundReferences(compoundId);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                String reference_text = getString(rs, "reference_text");
                String doi = getString(rs, "doi");
                String doip = (doi != null && !doi.isEmpty()) ? " -- [ doi: " + doi + "]" : "";
                reference_text = (reference_text != null) ? reference_text + doip : doi;

                links.add(new Link(getString(rs, "link"), reference_text));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    public ClassyfireNodeView getClassyfireNode(String nodeid) {

        ClassyfireNodeView cn = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForClassyfireNode(nodeid);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                String node_name = getString(rs, "node_name");
                String node_id = getString(rs, "node_id");
                String kingdom = getString(rs, "kingdom");
                String super_class = getString(rs, "super_class");
                String main_class = getString(rs, "main_class");
                String sub_class = getString(rs, "sub_class");
                String direct_parent = getString(rs, "direct_parent");
                String level_7 = getString(rs, "level_7");
                String level_8 = getString(rs, "level_8");
                String level_9 = getString(rs, "level_9");
                String level_10 = getString(rs, "level_10");
                String level_11 = getString(rs, "level_11");
                String created = getString(rs, "created");
                String last_updated = getString(rs, "last_updated");

                cn = new ClassyfireNodeView(node_name, node_id, kingdom, super_class,
                        main_class, sub_class, direct_parent, level_7,
                        level_8, level_9, level_10, level_11,
                        created, last_updated);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return cn;
    }

    public TermView getOntologyTerm(Integer termId) {
        TermView t = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForTerm(termId);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                Integer ontology_term_id = getInt(rs, "ontology_term_id");
                String term = getString(rs, "term");
                String definition = getString(rs, "definition");
                String external_id = getString(rs, "external_id");
                String external_source = getString(rs, "external_source");
                String ontology_comment = getString(rs, "ontology_comment");
                String curator = getString(rs, "curator");
                Integer parent_id = getInt(rs, "parent_id");
                Integer ontology_level = getInt(rs, "ontology_level");
                String created = getString(rs, "created");
                String last_updated = getString(rs, "last_updated");

                t = new TermView(ontology_term_id,
                        term,
                        definition,
                        external_id,
                        external_source,
                        ontology_comment,
                        curator,
                        parent_id,
                        ontology_level,
                        created,
                        last_updated);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }

    public ReferenceView getReference(Integer referenceId) {
        ReferenceView r = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForReference(referenceId);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                Integer reference_id = getInt(rs, "reference_id");
                String reference_text = getString(rs, "reference_text");
                String doi = getString(rs, "doi");
                String link = getString(rs, "link");
                String created = getString(rs, "created");
                String last_updated = getString(rs, "last_updated");

                r = new ReferenceView(reference_id,
                        reference_text,
                        doi,
                        link,
                        created,
                        last_updated,
                        this.getReferenceCompounds(reference_id),
                        this.getReferenceOrganisms(reference_id)
                );

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    private List<Link> getReferenceCompounds(int reference_id) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForReferenceCompounds(reference_id);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                links.add(new Link(getString(rs, "compound_id"), getString(rs, "compound_name")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    private List<Link> getReferenceOrganisms(int reference_id) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForReferenceOrganisms(reference_id);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                links.add(new Link(getString(rs, "organism_id"), getString(rs, "organism_name")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    public OrganismView getOrganism(Integer organismId) {
        OrganismView o = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForOrganism(organismId);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                Integer organism_id = getInt(rs, "organism_id");
                String organism_name = getString(rs, "organism_name");
                Integer organism_level = getInt(rs, "organism_level");
                Integer parent_id = getInt(rs, "parent_id");
                String created = getString(rs, "created");
                String last_updated = getString(rs, "last_updated");

                o = new OrganismView(organism_id,
                        organism_name,
                        organism_level,
                        parent_id,
                        created,
                        last_updated,
                        this.getOrganismHierarchy(organism_id,
                                new ArrayList<NumberedLink>()),
                        this.getOrganismCompounds(organism_id),
                        this.getOrganismReferences(organism_id)
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return o;
    }

    public OrganismView getReducedOrganism(Integer organismId) {
        OrganismView o = null;

        // Search in the database
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchForOrganism(organismId);

        try {

            //Search the component.
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                Integer organism_id = getInt(rs, "organism_id");
                String organism_name = getString(rs, "organism_name");
                Integer organism_level = getInt(rs, "organism_level");
                Integer parent_id = getInt(rs, "parent_id");

                o = new OrganismView(organism_id,
                        organism_name,
                        organism_level,
                        parent_id,
                        null,
                        null,
                        null,
                        null,
                        null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return o;
    }

    private List<NumberedLink> getOrganismHierarchy(int organismId, ArrayList<NumberedLink> hierarchy) {
        // 5 (subspecie) -> 4 (specie) -> 3(Genus) -> 2 (Family) -> 1 (Kingdom)
        OrganismView org = getReducedOrganism(organismId);
        hierarchy.add(new NumberedLink(org.getOrganism_id().toString(),
                org.getOrganism_name(),
                org.getOrganism_level()));
        if (org.getOrganism_level() != 1) {
            return this.getOrganismHierarchy(org.getParent_id(), hierarchy);
        } else {
            return hierarchy;
        }
    }

    private List<Link> getOrganismReferences(int organismId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchforOrganismReferences(organismId);

        List<Link> links = new ArrayList();

        try {

            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                String reference_text = getString(rs, "reference_text");
                String doi = getString(rs, "doi");
                String doip = (doi != null && !doi.isEmpty()) ? " -- [ doi: " + doi + "]" : "";
                reference_text = (reference_text != null) ? reference_text + doip : doi;

                links.add(new Link(getString(rs, "link"), reference_text));
            }

        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    private List<Link> getOrganismCompounds(int organismId) {
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;

        sql = QueryConstructor.searchforOrganismCompounds(organismId);

        List<Link> links = new ArrayList();

        try {
            prepSt = conn.prepareStatement(sql);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                links.add(new Link(getInt(rs, "compound_id").toString(),
                        getString(rs, "compound_name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return links;
    }

    /**
     * Deals with null values in that database String field.
     *
     * @param rs the result value to be queried
     * @param field A String typed field
     *
     * @return
     *
     * @throws SQLException
     */
    private String getString(ResultSet rs, String field) throws SQLException {
        return (rs.getObject(field) != null) ? rs.getString(field) : null;
    }

    /**
     * Deals with null values in that database Integer field.
     *
     * @param rs the result value to be queried
     * @param field An Integer typed field
     *
     * @return
     *
     * @throws SQLException
     */
    private Integer getInt(ResultSet rs, String field) throws SQLException {
        return (rs.getObject(field) != null) ? rs.getInt(field) : null;
    }

    /**
     * Deals with null values in that database Double field.
     *
     * @param rs the result value to be queried
     * @param field A Double typed field
     *
     * @return
     *
     * @throws SQLException
     */
    private Double getDouble(ResultSet rs, String field) throws SQLException {
        return (rs.getObject(field) != null) ? rs.getDouble(field) : null;
    }

    /**
     * Deals with null values in that database Data field.
     *
     * @param rs the result value to be queried
     * @param field A Data typed field
     *
     * @return
     *
     * @throws SQLException
     */
    private java.util.Date getDate(ResultSet rs, String field) throws SQLException {
        return (rs.getObject(field) != null) ? rs.getDate(field) : null;
    }

}
