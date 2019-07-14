/*
 * queryConstructor.java
 *
 * Created on 22-may-2018, 16:33:42
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package DBManager;

import java.util.List;
import utilities.DataFromInterfacesUtilities;
import static utilities.DataFromInterfacesUtilities.MAPDATABASES;
import utilities.Utilities;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 22-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public final class QueryConstructor {

    /**
     * Creates a new instance of queryConstructor
     */
    private QueryConstructor() {

    }

    /**
     * Fill the query to add filter from the formula type
     *
     * @param aliasName
     * @param query
     * @param chemAlphabet
     * @return
     */
    public static String addFilterFormulaTypeJPA(String aliasName, String query, String chemAlphabet) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        switch (chemAlphabet) {
            case "CHNOPS":
                newQuery = newQuery + aliasName + ".formulaType = '" + chemAlphabet + "' and ";
                break;
            case "CHNOPSCL":
                newQuery = newQuery + "(" + aliasName + ".formulaType = '" + chemAlphabet + "' or "
                        + aliasName + ".formulaType = 'CHNOPS') and ";
                break;
            case "CHNOPSD":
                newQuery = newQuery + "(" + aliasName + ".formulaType = '" + chemAlphabet + "' or "
                        + aliasName + ".formulaType = 'CHNOPS') and ";
                break;
            case "CHNOPSCLD":
                newQuery = newQuery + "(" + aliasName + ".formulaType = '" + chemAlphabet + "' or "
                        + aliasName + ".formulaType = 'CHNOPS' or "
                        + aliasName + ".formulaType = 'CHNOPSD' or "
                        + aliasName + ".formulaType = 'CHNOPSCL') and ";
                break;
            default:
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query to add filter from the formula type
     *
     * @param aliasName
     * @param query
     * @param chemAlphabet
     * @return
     */
    public static String addFilterIntegerFormulaTypeJPA(String aliasName, String query, String chemAlphabet) {
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(chemAlphabet);
        return addFilterIntegerFormulaTypeJPA(aliasName, query, chemAlphabetInt);
    }

    /**
     * Fill the query to add filter from the formula type
     *
     * @param aliasName
     * @param query
     * @param chemAlphabet
     * @return
     */
    public static String addFilterIntegerFormulaTypeJPA(String aliasName, String query, int chemAlphabet) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        switch (chemAlphabet) {
            case 0:
                newQuery = newQuery + aliasName + ".formulaTypeInt = 0 and ";
                break;
            case 1:
                newQuery = newQuery + aliasName + ".formulaTypeInt < 2 and ";
                break;
            case 2:
                newQuery = newQuery + "(" + aliasName + ".formulaTypeInt = 0 or "
                        + aliasName + ".formulaTypeInt = 2) and ";
                break;
            case 3:
                newQuery = newQuery + aliasName + ".formulaTypeInt < 4 and ";
                break;
            case 4:
                newQuery = newQuery + aliasName + ".formulaTypeInt < 5 and ";
                break;
            default:
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query to add filter from the formula type
     *
     * @param aliasName
     * @param query
     * @param chemAlphabet
     * @return
     */
    public static String addFilterIntegerFormulaTypeJDBC(String aliasName, String query, int chemAlphabet) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        switch (chemAlphabet) {
            case 0:
                newQuery = newQuery + aliasName + ".formula_type_int = 0 and ";
                break;
            case 1:
                newQuery = newQuery + aliasName + ".formula_type_int < 2 and ";
                break;
            case 2:
                newQuery = newQuery + "(" + aliasName + ".formula_type_int = 0 or "
                        + aliasName + ".formula_type_int = 2) and ";
                break;
            case 3:
                newQuery = newQuery + aliasName + ".formula_type_int < 4 and ";
                break;
            case 4:
                newQuery = newQuery + aliasName + ".formula_type_int < 5 and ";
                break;
            case 5:
                break;
            default:
                newQuery = newQuery + aliasName + ".formula_type_int < 5 and ";
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query with the identifiers from the distinct databases
     *
     *
     * @param query
     * @param databases
     * @return
     */
    public static String addFilterDatabasesJPA(String query, List<String> databases) {
        String newQuery = query;
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin"))
                && databases.contains("in-house"))) {
            if (databases.contains("Kegg")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsKegg ck on nc.compoundId=ck.compoundId ";
            }
            if (databases.contains("HMDB")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsHMDB ch on nc.compoundId=ch.compoundId ";
            }
            if (databases.contains("LipidMaps")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsLM cl on nc.compoundId=cl.compoundId ";
            }
            if (databases.contains("Metlin")) {
                newQuery = newQuery + "LEFT JOIN NewCompoundsMetlin cm on nc.compoundId=cm.compoundId ";
            }
            if (databases.contains("in-house")) {
                newQuery = newQuery + "LEFT JOIN NewCompoundsInHouse cih on nc.compoundId=cih.compoundId ";
            }
            newQuery = newQuery + " WHERE (";
            if (databases.contains("Kegg")) {
                newQuery = newQuery + "ck.keggId is not null or ";
            }
            if (databases.contains("HMDB")) {
                newQuery = newQuery + "ch.hmdbId is not null or ";
            }
            if (databases.contains("LipidMaps")) {
                newQuery = newQuery + "cl.lmId is not null or ";
            }
            if (databases.contains("Metlin")) {
                newQuery = newQuery + "cm.agilentId is not null or ";
            }
            if (databases.contains("in-house")) {
                newQuery = newQuery + "cih.inHouseId is not null or ";
            }

            // delete the last or
            newQuery = newQuery.substring(0, newQuery.length() - 4);
            newQuery = newQuery + ") and ";
        } else {
            // add where condition for all databases
            newQuery = query + " WHERE ";
        }
        return newQuery;
    }

    /**
     * Creates a String with the beggining the sql for the searches of compounds
     *
     * @return
     */
    public static String createStartSQLCompoundWithoutDBIds() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status ";
        return newQuery;
    }

    /**
     * Creates a String with the beggining the sql for the searches of compounds
     *
     * @return
     */
    public static String createStartSQLCompoundWithDBIds() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status, ";
        newQuery = addIDsDatabasesWithWHEREJDBC(newQuery);
        return newQuery;
    }

    /**
     * Creates a String with the beggining the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewWithDBIds() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status, "
                + "c.kegg_id, c.lm_id, c.hmdb_id, c.agilent_id, c.pc_id, c.in_house_id, "
                + "c.inchi, c.inchi_key, c.smiles, "
                + "c.lipid_type, c.num_chains, c.number_carbons, c.double_bonds, "
                + "c.category, c.main_class, c.sub_class, c.class_level4 ";
        newQuery = newQuery + "FROM compounds_view c WHERE ";
        return newQuery;
    }

    /**
     * Creates a String with the beggining the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations
     *
     * @return the sql query
     */
    public static String createSQLInSilicoCompoundViewWithDBIds() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.np_likeness, "
                + "c.MINE_id, "
                + "c.inchi, c.inchi_key, c.smiles ";
        newQuery = newQuery + "FROM compounds_gen_view c WHERE ";
        return newQuery;
    }

    /**
     * Creates a String with the beggining the sql for the searches of compounds
     *
     * @return
     */
    public static String createStartSQLCompoundGen() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.np_likeness, c.MINE_id from compounds_gen c WHERE ";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * The SQL String needs two parameters corresponding to the two classes of
     * the FAs
     *
     *
     * @return
     */
    public static String createStartSQLForFASearch() {
        String sql = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.cas_id, c.charge_type, "
                + "c.charge_number, c.formula_type_int, c.compound_type, c.compound_status "
                + "FROM compounds c INNER JOIN compounds_in_house cih on c.compound_id=cih.compound_id "
                + "INNER JOIN compounds_lm_classification clmc on c.compound_id=clmc.compound_id "
                + "WHERE clmc.sub_class in (?,?) "
                + " and ";
        return sql;
    }

    /**
     * Creates a SQL for obtaining the compound_ids from the sub_class of the
     * compound (lipidMaps Classification), the number of Carbons and the number
     * of doubleBonds of the chain.
     *
     * It contains three parameters for the JDBC sql -> sub_class, carbons and
     * double_bonds
     *
     * @return
     */
    public static String createSQLForCompoundIdFromMainClass_Carbons_DoubleBonds() {
        String sql = "SELECT distinct c.compound_id FROM compounds c "
                + "INNER JOIN compounds_lm_classification clmc on c.compound_id=clmc.compound_id "
                + "INNER JOIN compound_chain c_cha on c.compound_id=c_cha.compound_id "
                + "INNER JOIN chains chains on chains.chain_id=c_cha.chain_id "
                + "WHERE "
                + "clmc.main_class = ? and "
                + "chains.num_carbons = ? and "
                + "chains.double_bonds = ? and " // + "chains.oxidation = ? and "
                ;
        return sql;
    }

    /**
     * Creates a SQL for obtaining the compound_ids from the sub_class of the
     * compound (lipidMaps Classification), the number of Carbons and the number
     * of doubleBonds of the chain.
     *
     * It contains three parameters for the JDBC sql -> sub_class, carbons and
     * double_bonds
     *
     * @return
     */
    public static String createSQLForCompoundIdFromSubClass_Carbons_DoubleBonds() {
        String sql = "SELECT distinct c.compound_id, c.mass FROM compounds c "
                + "INNER JOIN compounds_lm_classification clmc on c.compound_id=clmc.compound_id "
                + "INNER JOIN compound_chain c_cha on c.compound_id=c_cha.compound_id "
                + "INNER JOIN chains chains on chains.chain_id=c_cha.chain_id "
                + "WHERE "
                + "clmc.sub_class = ? and "
                + "chains.num_carbons = ? and "
                + "chains.double_bonds = ? and " // + "chains.oxidation = ? and "
                ;
        return sql;
    }

    /**
     * Creates a SQL for obtaining the compound from the compound_id, the number
     * of Carbons and the number of doubleBonds of the chain. The compound Id
     * has been obtained already before to check the Class of the compound. This
     * method is useful to search compounds with different Chains. Once a list
     * of compound_ids corresponding to the first chain has been obtained, it is
     * possible to search over these compound ids based on the carbons and
     * double bonds from the rest of the chains. It contains three parameters
     * for the JDBC sql -> compound_id, carbons and double_bonds
     *
     * @return
     */
    public static String createSQLForPCCompoundFromCompoundId_Carbons_DoubleBonds() {
        String sql = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.cas_id, "
                + "c.charge_type, c.charge_number, c.formula_type_int, c.compound_type, c.compound_status, ";
        sql = addIDsDatabasesJDBC(sql);
        sql = sql + "INNER JOIN compound_chain c_cha on c.compound_id=c_cha.compound_id "
                + "INNER JOIN chains chains on chains.chain_id=c_cha.chain_id "
                + "WHERE "
                + "c.compound_id = ? and "
                + "chains.num_carbons = ? and "
                + "chains.double_bonds = ? " //+ "chains.oxidation = ? and "
                ;
        return sql;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     *
     * @param query
     * @return
     */
    public static String addIDsDatabasesWithWHEREJDBC(String query) {
        // Add all the links from the databases 
        String newQuery = addIDsDatabasesJDBC(query);
        newQuery = newQuery + " WHERE ";
        return newQuery;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     *
     * @param query
     * @return
     */
    public static String addIDsDatabasesJDBC(String query) {
        String newQuery = query;
        // Add all the links from the databases 
        newQuery = newQuery + "ck.kegg_id, ";
        newQuery = newQuery + "ch.hmdb_id, ";
        newQuery = newQuery + "cl.lm_id, ";
        newQuery = newQuery + "ca.agilent_id, ";
        newQuery = newQuery + "cih.in_house_id, ";
        newQuery = newQuery + "cpc.pc_id ";
        newQuery = newQuery + "FROM compounds c ";
        newQuery = newQuery + "LEFT JOIN compounds_kegg ck on c.compound_id=ck.compound_id ";
        newQuery = newQuery + "LEFT JOIN compounds_hmdb ch on c.compound_id=ch.compound_id ";
        newQuery = newQuery + "LEFT JOIN compounds_lm cl on c.compound_id=cl.compound_id ";
        newQuery = newQuery + "LEFT JOIN compounds_agilent ca on c.compound_id=ca.compound_id ";
        newQuery = newQuery + "LEFT JOIN compounds_in_house cih on c.compound_id=cih.compound_id ";
        newQuery = newQuery + "LEFT JOIN compounds_pc cpc on c.compound_id=cpc.compound_id ";
        return newQuery;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     * @deprecated
     * @param query
     * @param databases
     * @return
     */
    public static String addFilterDatabasesJDBCString(String query, List<String> databases) {
        String newQuery = query;
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin"))
                && databases.contains("in-house"))) {
            newQuery = newQuery + " (";
            if (databases.contains("Kegg")) {
                newQuery = newQuery + " ck.compound_id is not null or ";
            }
            if (databases.contains("HMDB")) {
                newQuery = newQuery + " ch.compound_id is not null or ";
            }
            if (databases.contains("LipidMaps")) {
                newQuery = newQuery + " cl.compound_id is not null or ";
            }
            if (databases.contains("Metlin")) {
                newQuery = newQuery + " ca.compound_id is not null or ";
            }
            if (databases.contains("in-house")) {
                newQuery = newQuery + " cih.compound_id is not null or ";
            }
            // Delete the last or condition
            newQuery = newQuery.substring(0, newQuery.length() - 4);
            newQuery = newQuery + ") and ";
        }
        return newQuery;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     * @param query
     * @param databases
     * @return
     */
    public static String addFilterDatabasesJDBC(String query, List<Integer> databases) {
        String newQuery = query;
        if (!(databases.contains(MAPDATABASES.get("HMDB"))
                && databases.contains(MAPDATABASES.get("LipidMaps"))
                && databases.contains(MAPDATABASES.get("Metlin"))
                && databases.contains(MAPDATABASES.get("Kegg"))
                && databases.contains(MAPDATABASES.get("in-house")))) {
            newQuery = newQuery + " (";
            if (databases.contains(MAPDATABASES.get("Kegg"))) {
                newQuery = newQuery + " ck.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("HMDB"))) {
                newQuery = newQuery + " ch.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("LipidMaps"))) {
                newQuery = newQuery + " cl.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("Metlin"))) {
                newQuery = newQuery + " ca.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("in-house"))) {
                newQuery = newQuery + " cih.compound_id is not null or ";
            }
            // Delete the last or condition
            newQuery = newQuery.substring(0, newQuery.length() - 4);
            newQuery = newQuery + ") and ";
        }
        return newQuery;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     * @param query
     * @param databases
     * @return
     */
    public static String addFilterDatabasesCompoundsViewJDBC(String query, List<Integer> databases) {
        String newQuery = query;
        if (!(databases.contains(MAPDATABASES.get("HMDB"))
                && databases.contains(MAPDATABASES.get("LipidMaps"))
                && databases.contains(MAPDATABASES.get("Metlin"))
                && databases.contains(MAPDATABASES.get("Kegg"))
                && databases.contains(MAPDATABASES.get("in-house")))) {
            newQuery = newQuery + " (";
            if (databases.contains(MAPDATABASES.get("Kegg"))) {
                newQuery = newQuery + " kegg_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("HMDB"))) {
                newQuery = newQuery + " hmdb_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("LipidMaps"))) {
                newQuery = newQuery + " lm_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("Metlin"))) {
                newQuery = newQuery + " agilent_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("in-house"))) {
                newQuery = newQuery + " in_house_id is not null or ";
            }
            // Delete the last or condition
            newQuery = newQuery.substring(0, newQuery.length() - 4);
            newQuery = newQuery + ") and ";
        }
        return newQuery;
    }

    /**
     * Fill the query to add filter from the metabolites Type.
     *
     * @param query
     * @param metabolitesType
     * @return
     */
    public static String addFilterMetabolitesTypeJPA(String query, String metabolitesType) {
        String newQuery = query;
        switch (metabolitesType) {
            // Then search in all compounds in the database
            case "All including peptides":
                break;
            case "Only lipids":
                newQuery = newQuery + "(nc.compoundType = " + "1" + ") and ";
                break;
            default:
                // By default, searches includes all compounds but peptides (2)
                newQuery = newQuery + "(nc.compoundType != " + "2" + ") and ";
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query to add filter from the metabolites Type.
     *
     * @param query
     * @param metabolitesType
     * @return
     */
    public static String addFilterMetabolitesTypeJDBC(String query, String metabolitesType) {
        String newQuery = query;
        switch (metabolitesType) {
            // Then search in all compounds in the database
            case "All including peptides":
                break;
            case "Only lipids":
                newQuery = newQuery + "(c.compound_type = 1) and ";
                break;
            default:
                // By default, searches includes all compounds but peptides (2)
                newQuery = newQuery + "(c.compound_type != 2) and ";
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query to add filter from the metabolites Type.
     *
     * @param query
     * @param metabolitesType
     * @return
     */
    public static String addFilterMetabolitesTypeJDBC(String query, int metabolitesType) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        switch (metabolitesType) {
            // Then search in all compounds in the database
            case 2:
                break;
            case 1:
                newQuery = newQuery + "(c.compound_type = 1) and ";
                break;
            default:
                // By default, searches includes all compounds but peptides (2)
                newQuery = newQuery + "(c.compound_type != 2) and ";
                break;
        }
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName
     * @param query
     * @param massToSearch
     * @param toleranceMode
     * @param tolerance
     * @return
     */
    public static String addFilterMassesJPA(String aliasName, String query, Double massToSearch,
            String toleranceMode, Double tolerance) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        Double delta = Utilities.calculateDeltaPPM(massToSearch, toleranceMode, tolerance);

        Double low = massToSearch - delta;
        Double high = massToSearch + delta;

        newQuery = newQuery + "(" + aliasName + ".mass >= " + low + " and "
                + aliasName + ".mass <= " + high + ")";
        newQuery = newQuery + " order by ABS(" + aliasName + ".mass - " + massToSearch + ")";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     * @return
     */
    public static String addFilterMassesJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        newQuery = newQuery + "(" + aliasName + ".mass >= ? and "
                + aliasName + ".mass <= ? )";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     * @return
     */
    public static String addOrderByMassesJDBC(String aliasName, String query) {
        String newQuery;
        newQuery = query + " order by ABS(" + aliasName + ".mass - ? )";
        return newQuery;
    }

    /**
     * Fill the query to add filter for transformation Type of CE MS Product
     * Ions
     *
     * @param aliasName
     * @param query
     * @param transformationTypes Types of transformation between single (')
     * or double quotes (")
     * @return
     */
    public static String addFilterTransformationTypeJPA(String aliasName,
            String query, String transformationTypes) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".transformationType in (" + transformationTypes + ") and ";

        return newQuery;
    }

    /**
     * Fill the query to add the filter of the Relative migration times in CE MS
     *
     * @param aliasName
     * @param query
     * @param RMTToSearch
     * @param RMTToleranceMode
     * @param RMTTolerance
     * @return
     */
    public static String addFilterRMTSCEJPA(String aliasName, String query, Double RMTToSearch,
            String RMTToleranceMode, Double RMTTolerance) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        Double delta = Utilities.calculateDeltaRMT(RMTToSearch, RMTToleranceMode, RMTTolerance);

        Double low = RMTToSearch - delta;
        Double high = RMTToSearch + delta;

        newQuery = newQuery + "(" + aliasName + ".RMT >= " + low + " and "
                + aliasName + ".RMT <= " + high + ") and ";

        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName
     * @param query
     * @param massToSearch
     * @param toleranceMode
     * @param tolerance
     * @return
     */
    public static String addFilterMassesCEProductIonJPA(String aliasName, String query,
            Double massToSearch,
            String toleranceMode, Double tolerance) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        Double delta = Utilities.calculateDeltaPPM(massToSearch, toleranceMode, tolerance);

        Double low = massToSearch - delta;
        Double high = massToSearch + delta;

        newQuery = newQuery + "(" + aliasName + ".mz >= " + low + " and "
                + aliasName + ".mz <= " + high + ")";
        newQuery = newQuery + " order by ABS(" + aliasName + ".mz - " + massToSearch + ")";
        return newQuery;
    }
    
    /**
     * Fill the query to add the filter for the cemsId
     *
     * @param aliasName
     * @param query
     * @param ceMsId
     * @return
     */
    public static String addFilterCEMSIdProductIonJPA(String aliasName, String query,
            Integer ceMsId) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".cemsId = " + ceMsId + " and";

        return newQuery;
    }
    
    /**
     * Fill the query to add the order by clause for RMT
     *
     * @param aliasName
     * @param query
     * @param RMTToSearch
     * @param RMTToleranceMode
     * @param RMTTolerance
     * @return
     */
    public static String addOrderByRMTJPA(String aliasName, String query, Double RMTToSearch,
            String RMTToleranceMode, Double RMTTolerance) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        
        Double delta = Utilities.calculateDeltaRMT(RMTToSearch, RMTToleranceMode, RMTTolerance);

        Double low = RMTToSearch - delta;
        Double high = RMTToSearch + delta;

        newQuery = newQuery + "ABS(" + aliasName + ".RMT - " + RMTToSearch + ")";
        return newQuery;
    }

}
