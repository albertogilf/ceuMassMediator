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
     *
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
     *
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
     * @param chemAlphabet -- 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4
     * ALL, 5 ALLD
     *
     * @return
     */
    public static String addFilterIntegerFormulaTypeJPA(String aliasName, String query,
            Integer chemAlphabet) {
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
     * Fill the query to add filter of the ce Eff mob conditions
     *
     * @param aliasName
     * @param query
     * @param CEEffMobConditionsCode 1-16 (depending on the buffer, temperature,
     * ion mode and polarity)
     *
     * @return
     */
    public static String addFilterCEEffMobConditionsJDBC(String aliasName, String query,
            Integer CEEffMobConditionsCode) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".ce_exp_prop_id = " + CEEffMobConditionsCode + " and ";
        return newQuery;
    }

    /**
     * Fill the query to add filter of the ce Eff mob conditions
     *
     * @param aliasName
     * @param query
     * @param bge_compound_id compound id of the bge
     *
     * @return
     */
    public static String addFilterExpPropMetaBGEJDBC(String aliasName, String query,
            Integer bge_compound_id) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".bge_compound_id = " + bge_compound_id + " and ";
        return newQuery;
    }

    /**
     * Fill the query to add filter of the ce sample type (standard or plasma)
     *
     * @param aliasName
     * @param query
     * @param sampleType compound id of the bge
     *
     * @return
     */
    public static String addFilterExpPropMetaSampleTypeJDBC(String aliasName, String query,
            Integer sampleType) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".ce_sample_type = " + sampleType + " and ";
        return newQuery;
    }

    /**
     * Fill the query to add filter of the CE EFF MOB present
     *
     * @param aliasName
     * @param query
     *
     * @return
     */
    public static String addFilterCEEffMobConditionsNotnullJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".eff_mobility is not null and ";
        return newQuery;
    }

    /**
     * Fill the query to add filter from the formula type
     *
     * @param aliasName
     * @param query
     * @param chemAlphabet -- 0 CHNOPS, 1 CHNOPSD, 2 CHNOPSCL, 3 CHNOPSCLD, 4
     * ALL, 5 ALLD
     *
     * @return
     */
    public static String addFilterIntegerFormulaTypeJDBC(String aliasName, String query,
            Integer chemAlphabet) {
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
     *
     * @return
     */
    public static String addFilterDatabasesJPA(String query, List<String> databases) {
        String newQuery = query;
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin")
                && databases.contains("In-house") && databases.contains("Aspergillus")
                && databases.contains("FAHFA Lipids")))) {
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
                newQuery = newQuery + " LEFT JOIN NewCompoundsMetlin cm on nc.compoundId=cm.compoundId ";
            }
            if (databases.contains("In-house")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsInHouse cih on nc.compoundId=cih.compoundId ";
            }
            if (databases.contains("Aspergillus")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsAspergillus c_asp on nc.compoundId=c_asp.compoundId ";
            }
            if (databases.contains("FAHFA Lipids")) {
                newQuery = newQuery + " LEFT JOIN NewCompoundsFahfa c_fahfa on nc.compoundId=c_fahfa.compoundId ";
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
            if (databases.contains("In-house")) {
                newQuery = newQuery + "cih.inHouseId is not null or ";
            }
            if (databases.contains("Aspergillus")) {
                newQuery = newQuery + "c_asp.aspergillusId is not null or ";
            }
            if (databases.contains("FAHFA Lipids")) {
                newQuery = newQuery + "c_fahfa.compoundId is not null or ";
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
     * Creates a String with the beginning the sql for the searches of compounds
     *
     * @return
     */
    public static String createStartSQLCompoundWithoutDBIds() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status ";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
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
     * Creates a String with the beginning the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewWithDBIdsNoTable() {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.cas_id, c.charge_type, c.charge_number, "
                + "c.formula_type_int, c.compound_type, c.compound_status, "
                + "c.kegg_id, c.lm_id, c.hmdb_id, c.agilent_id, c.pc_id, c.chebi_id, c.in_house_id, "
                + "c.knapsack_id, c.npatlas_id, "
                + "c.aspergillus_id, c.fahfa_id, "
                + "c.oh_position,"
                + "c.mesh_nomenclature, c.iupac_classification, c.aspergillus_web_name, "
                + "c.inchi, c.inchi_key, c.smiles, "
                + "c.lipid_type, c.num_chains, c.number_carbons, c.double_bonds, "
                + "c.category, c.main_class, c.sub_class, c.class_level4 ";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations
     *
     * @return the sql query
     */
    public static String searchForCompoundInCompoundView(int compoundId) {
        String newQuery = "SELECT c.compound_id, c.mass, c.formula, c.compound_name, "
                + "c.cas_id, c.charge_type, c.charge_number, "
                + "c.kegg_id, c.lm_id, c.hmdb_id, c.agilent_id, c.pc_id, c.chebi_id, "
                + "c.aspergillus_id,"
                + "c.oh_position,"
                + "c.mesh_nomenclature, c.iupac_classification, c.aspergillus_web_name, "
                + "c.inchi, c.inchi_key, c.smiles, "
                + "c.num_chains, c.number_carbons, c.double_bonds, "
                + "c.category, c.main_class, c.sub_class, c.class_level4,c.created, c.last_updated, c.logP, "
                + "c.knapsack_id, c.biological_activity, c.npatlas_id ";

        newQuery = newQuery + "FROM compounds_view c WHERE c.compound_id = " + compoundId;

        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * reactions using the view for the compounds, which contains information
     * about the 1:N relations
     *
     * @return the sql query
     */
    public static String searchForCompoundsReactions(int compoundId) {
        String newQuery = "SELECT crk.reaction_id ";

        newQuery = newQuery + "FROM compounds_reactions_kegg crk WHERE crk.compound_id = " + compoundId;

        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * reactions using the view for the compounds, which contains information
     * about the 1:N relations
     *
     * @return the sql query
     */
    public static String searchForClassyfireNode(String nodeId) {
        String newQuery = "SELECT ccd.node_name, cc.node_id, cc.kingdom, cc.super_class, "
                + "cc.main_class, cc.sub_class, cc.direct_parent, cc.level_7, "
                + "cc.level_8, cc.level_9, cc.level_10, cc.level_11, cc.created, "
                + "cc.last_updated ";
        newQuery = newQuery + "FROM classyfire_classification cc inner join "
                + "classyfire_classification_dictionary ccd on cc.node_id=ccd.node_id "
                + "WHERE cc.node_id=\"" + nodeId + "\"";

        return newQuery;
    }

    public static String searchForTerm(int termId) {
        String newQuery = "SELECT  ontology_term_id, term, definition, external_id, "
                + "external_source, ontology_comment, curator, parent_id, "
                + "ontology_level, created, last_updated ";
        newQuery = newQuery + "FROM ontology_terms WHERE ontology_term_id=" + termId;

        return newQuery;
    }

    public static String searchForReference(int referenceId) {
        String newQuery = "SELECT r.reference_id, r.reference_text, r.doi, r.link, r.created, r.last_updated ";
        newQuery = newQuery + "from reference r where r.reference_id=" + referenceId;

        return newQuery;
    }

    public static String searchforCompoundTerm(int compoundId) {
        String newQuery = "SELECT ot.ontology_term_id, ot.term ";
        newQuery = newQuery + "FROM compound_ontology_terms cot inner join ontology_terms ot "
                + "on cot.ontology_term_id=ot.ontology_term_id WHERE cot.compound_id=" + compoundId;
        return newQuery;
    }

    public static String searchforCompoundReferences(int compoundId) {
        String newQuery = "SELECT r.reference_text, r.doi, r.link ";
        newQuery = newQuery + "FROM compound_reference cr inner join reference r "
                + "on cr.reference_id=r.reference_id WHERE cr.compound_id=" + compoundId;
        return newQuery;
    }

    public static String searchforCompoundOrganisms(int compoundId) {
        String newQuery = "SELECT o.organism_id, o.organism_name "
                + "FROM compound_organism oo inner join organism o on oo.organism_id=o.organism_id "
                + "WHERE oo.compound_id=" + compoundId;
        return newQuery;
    }

    public static String searchForReferenceOrganisms(int referenceId) {
        String newQuery = "SELECT o.organism_id, o.organism_name "
                + "FROM organism_reference oref inner join organism o on oref.organism_id=o.organism_id  "
                + "WHERE oref.reference_id=" + referenceId;
        return newQuery;
    }

    public static String searchForReferenceCompounds(int referenceId) {
        String newQuery = "SELECT c.compound_id, c.compound_name, cr.reference_id "
                + "FROM compound_reference cr inner join compounds c on cr.compound_id=c.compound_id "
                + "WHERE cr.reference_id=" + referenceId;
        return newQuery;
    }

    public static String searchForOrganism(int organismId) {
        String newQuery = "SELECT o.organism_id, o.organism_name, o.organism_level,"
                + " o.parent_id, o.created, o.last_updated "
                + "FROM organism o WHERE o.organism_id=" + organismId;
        return newQuery;
    }

    public static String searchforOrganismReferences(int organismId) {
        String newQuery = "SELECT r.reference_text, r.doi, r.link "
                + "FROM organism_reference oo inner join reference r "
                + "on oo.reference_id=r.reference_id WHERE oo.organism_id=" + organismId;
        return newQuery;
    }

    public static String searchforOrganismCompounds(int organismId) {
        String newQuery = "select distinct c.compound_id, compound_name "
                + "from compound_organism co inner join compounds c "
                + "on c.compound_id = co.compound_id inner join "
                + "organism o on o.organism_id=co.organism_id "
                + "where co.organism_id=" + organismId + " or parent_id=" + organismId;

        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * reactions using the view for the compounds, which contains information
     * about the 1:N relations
     *
     * @return the sql query
     */
    public static String searchForCompoundClassyfire(int compoundId) {
        String newQuery = "SELECT cc.node_id, ccd.node_name ";
        newQuery = newQuery + "FROM compound_classyfire_classification ccc "
                + "inner join classyfire_classification cc on ccc.node_id=cc.node_id "
                + "left join classyfire_classification_dictionary ccd on cc.node_id=ccd.node_id "
                + "WHERE ccc.compound_id=" + compoundId;

        return newQuery;
    }

    /**
     * add the attributes of the ce_eff_mob table
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewCEMSAtrributes(String aliasCEEffMobTable) {
        String newQuery = createSQLCompoundViewWithDBIdsNoTable();
        newQuery = newQuery + ", " + aliasCEEffMobTable + ".cembio_id, "
                + aliasCEEffMobTable + ".eff_mobility, "
                + aliasCEEffMobTable + ".ce_exp_prop_id ";
        return newQuery;
    }

    /**
     * add the attributes of the corrsponding ce_eff_mob view
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewCEMSEXPMetaDataAtrributes(String aliasCEEffMobTable) {
        String newQuery = createSQLCompoundViewCEMSAtrributes(aliasCEEffMobTable);
        newQuery = newQuery + ", ce_exp_prop_md.bge_compound_id, ce_exp_prop_md.relative_MT";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations from the compounds view table
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewWithDBIds() {
        String newQuery = createSQLCompoundViewWithDBIdsNoTable();
        newQuery = newQuery + " FROM compounds_view c ";
        return newQuery;
    }

    /**
     * Creates a SQL STRING WITH THE QUERY PERFORMED OVER THE CE PRODUCT IONS
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLProductIonsJDBC(String aliasCEEffMobTable) {
        String newQuery = "SELECT cepi.ce_product_ion_id, cepi.ion_source_voltage, cepi.ce_product_ion_mz, "
                + "cepi.ce_product_ion_intensity, cepi.ce_transformation_type, "
                + "cepi.ce_product_ion_name, cepi.ce_eff_mob_id, "
                + "cepi.compound_id_own, " + aliasCEEffMobTable + ".eff_mobility, "
                + aliasCEEffMobTable + ".cembio_id, " + aliasCEEffMobTable + ".ce_compound_id ";
        newQuery = newQuery + "FROM compound_ce_product_ion cepi "
                + " inner join " + aliasCEEffMobTable + " " + aliasCEEffMobTable
                + " on cepi.ce_eff_mob_id = " + aliasCEEffMobTable + ".ce_eff_mob_id ";
        return newQuery;
    }

    /**
     * Creates a SQL Query over the experimental mz in the metadata table
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLProductIonsEXPRMTJDBC(String aliasCEEffMobTable) {
        String newQuery = "SELECT cepi.ce_product_ion_id, cepi.ion_source_voltage, cepi.ce_product_ion_mz, "
                + "cepi.ce_product_ion_intensity, cepi.ce_transformation_type, "
                + "cepi.ce_product_ion_name, cepi.ce_eff_mob_id, "
                + "cepi.compound_id_own, " + aliasCEEffMobTable + ".eff_mobility, "
                + aliasCEEffMobTable + ".cembio_id, " + aliasCEEffMobTable + ".ce_compound_id, "
                + "ce_exp_prop_md.bge_compound_id, ce_exp_prop_md.relative_MT ";
        newQuery = newQuery + "FROM compound_ce_product_ion cepi "
                + "inner join " + aliasCEEffMobTable + " " + aliasCEEffMobTable
                + " on cepi.ce_eff_mob_id = " + aliasCEEffMobTable + ".ce_eff_mob_id "
                + "inner join ce_experimental_properties_metadata ce_exp_prop_md "
                + "on " + aliasCEEffMobTable + ".ce_eff_mob_id=ce_exp_prop_md.ce_eff_mob_id";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
     * using the view for the compounds, which contains information about the
     * 1:1 relations and with the where condition
     *
     * @return the sql query
     */
    public static String createSQLCompoundViewWithDBIdsWhereCondition() {
        String newQuery = createSQLCompoundViewWithDBIds();
        newQuery = newQuery + " WHERE ";
        return newQuery;
    }

    /**
     * Joins the table of compounds view with the corresponding name of the view
     * of ce_eff_mob
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLInnerJoinCE_eff_mob(String aliasCEEffMobTable) {
        String newQuery = createSQLCompoundViewCEMSAtrributes(aliasCEEffMobTable);
        newQuery = newQuery + " FROM compounds_view c inner join " + aliasCEEffMobTable
                + " " + aliasCEEffMobTable + " on "
                + "c.compound_id=" + aliasCEEffMobTable + ".ce_compound_id ";
        return newQuery;
    }

    /**
     * Joins the table of compounds view with the corresponding view of
     * ce_eff_mob
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLInnerJoinCE_exp_prop_metadata(String aliasCEEffMobTable) {
        String newQuery = createSQLCompoundViewCEMSEXPMetaDataAtrributes(aliasCEEffMobTable);
        newQuery = newQuery + " FROM compounds_view c inner join " + aliasCEEffMobTable + " "
                + aliasCEEffMobTable + " on c.compound_id= " + aliasCEEffMobTable + ".ce_compound_id "
                + " inner join ce_experimental_properties_metadata ce_exp_prop_md on "
                + aliasCEEffMobTable + ".ce_eff_mob_id=ce_exp_prop_md.ce_eff_mob_id ";
        return newQuery;
    }

    /**
     * Returns the JDBC query to obtain the CEMS compound by the compound ID
     *
     * @param aliasCEEffMobTable
     *
     * @return the sql query
     */
    public static String createSQLInnerJoinCE_eff_mobByCompoundID(String aliasCEEffMobTable) {
        String newQuery = createSQLInnerJoinCE_eff_mob(aliasCEEffMobTable);
        newQuery = newQuery + " WHERE " + aliasCEEffMobTable + ".ce_compound_id = ?";
        return newQuery;
    }

    /**
     * Creates a String with the beginning the sql for the searches of compounds
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
     * Creates a String with the beginning the sql for the searches of compounds
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
    public static String createSQLForCompoundIdAndMassFromMainClass_Carbons_DoubleBonds() {
        String sql = "SELECT distinct c.compound_id,c.mass FROM compounds c "
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
                + "c.charge_type, c.charge_number, c.formula_type_int, c.compound_type, c.compound_status, "
                + "c.oh_position, "
                + "c.mesh_nomenclature, c.iupac_classification, c.aspergillus_web_name, ";
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
     *
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
     *
     * @return
     */
    public static String addIDsDatabasesJDBC(String query) {
        String newQuery = query;
        // Add all the links from the databases
        newQuery = newQuery + "c.kegg_id, ";
        newQuery = newQuery + "c.hmdb_id, ";
        newQuery = newQuery + "c.lm_id, ";
        newQuery = newQuery + "c.agilent_id, ";
        newQuery = newQuery + "c.in_house_id, ";
        newQuery = newQuery + "c.aspergillus_id, ";
        newQuery = newQuery + "c.fahfa_id, ";
        newQuery = newQuery + "c.pc_id, ";
        newQuery = newQuery + "c.chebi_id, ";
        newQuery = newQuery + "c.knapsack_id, ";
        newQuery = newQuery + "c.npatlas_id ";

        newQuery = newQuery + "FROM compounds_view c ";
        return newQuery;
    }

    /**
     * Fill the query to select only compounds from the database present in the
     * list of databases
     *
     * @deprecated
     * @param query
     * @param databases
     *
     * @return
     */
    public static String addFilterDatabasesJDBCString(String query, List<String> databases) {
        String newQuery = query;
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin")
                && databases.contains("In-house") && databases.contains("Aspergillus")
                && databases.contains("FAHFA Lipids")))) {
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
            if (databases.contains("In-house")) {
                newQuery = newQuery + " cih.compound_id is not null or ";
            }
            if (databases.contains("Aspergillus")) {
                newQuery = newQuery + " c_asp.compound_id is not null or ";
            }
            if (databases.contains("FAHFA Lipids")) {
                newQuery = newQuery + " c_fahfa.compound_id is not null or ";
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
     *
     * @return
     */
    public static String addFilterDatabasesJDBC(String query, List<Integer> databases) {
        String newQuery = query;
        if (!(databases.contains(MAPDATABASES.get("HMDB"))
                && databases.contains(MAPDATABASES.get("LipidMaps"))
                && databases.contains(MAPDATABASES.get("Metlin"))
                && databases.contains(MAPDATABASES.get("Kegg"))
                && databases.contains(MAPDATABASES.get("In-house"))
                && databases.contains(MAPDATABASES.get("Aspergillus"))
                && databases.contains(MAPDATABASES.get("FAHFA Lipids")))) {
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
            if (databases.contains(MAPDATABASES.get("In-house"))) {
                newQuery = newQuery + " cih.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("Aspergillus"))) {
                newQuery = newQuery + " c_asp.compound_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("FAHFA Lipids"))) {
                newQuery = newQuery + " c_fahfa.compound_id is not null or ";
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
     *
     * @return
     */
    public static String addFilterDatabasesCompoundsViewJDBC(String query, List<Integer> databases) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        if (!(databases.contains(MAPDATABASES.get("HMDB"))
                && databases.contains(MAPDATABASES.get("LipidMaps"))
                && databases.contains(MAPDATABASES.get("Metlin"))
                && databases.contains(MAPDATABASES.get("Kegg"))
                && databases.contains(MAPDATABASES.get("In-house"))
                && databases.contains(MAPDATABASES.get("Aspergillus"))
                && databases.contains(MAPDATABASES.get("FAHFA Lipids")))) {
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
            if (databases.contains(MAPDATABASES.get("In-house"))) {
                newQuery = newQuery + " in_house_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("Aspergillus"))) {
                newQuery = newQuery + " aspergillus_id is not null or ";
            }
            if (databases.contains(MAPDATABASES.get("FAHFA Lipids"))) {
                newQuery = newQuery + " fahfa_id is not null or ";
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
     *
     * @return
     */
    public static String addFilterMetabolitesTypeJPA(String query, String metabolitesType) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
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
     *
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
     *
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
     *
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
     *
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
     *
     * @return
     */
    public static String addFilterMassesProductIons(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        newQuery = newQuery + "(" + aliasName + ".ce_product_ion_mz >= ? and "
                + aliasName + ".ce_product_ion_mz <= ? ) and ";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     *
     * @return
     */
    public static String addOrderByMassesJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        newQuery = newQuery + "ABS(" + aliasName + ".mass - ? )";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     *
     * @return
     */
    public static String orderByCompoundId(String aliasName, String query) {
        String newQuery = query;
        if (newQuery.toLowerCase().endsWith("and ")) {
            // Add databases depending on the user selection
            newQuery = newQuery.substring(0, query.length() - 4);
        }
        if (!newQuery.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = newQuery + " order by ";
        } else {
            newQuery = newQuery + ", ";
        }
        newQuery = newQuery + aliasName + ".compound_id";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the masses
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     *
     * @return
     */
    public static String addOrderByMassesProductIonsJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        newQuery = newQuery + "ABS(" + aliasName + ".ce_product_ion_mz - ? )";
        return newQuery;
    }

    /**
     * Fill the query to add filter for transformation Type of CE MS Product
     * Ions
     *
     * @param aliasName
     * @param query
     * @param transformationTypes Types of transformation between single (') or
     * double quotes (")
     *
     * @return
     */
    public static String addFilterTransformationTypeJDBC(String aliasName,
            String query, String transformationTypes) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + aliasName + ".ce_transformation_type in (" + transformationTypes + ") ";

        return newQuery;
    }

    // TODO REMOVE IT
    // START WITH OLD CE MS METHODS
    /**
     * Fill the query to add filter for transformation Type of CE MS Product
     * Ions
     *
     * @param aliasName
     * @param query
     * @param transformationTypes Types of transformation between single (') or
     * double quotes (")
     *
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
     * @param bge
     *
     * @return
     */
    public static String addFilterBGECEJPA(String aliasName, String query, int bge) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        newQuery = newQuery + "(" + aliasName + ".bge = " + bge + ") and ";

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
     *
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
        Double delta = Utilities.calculateDeltaPercentage(RMTToSearch, RMTToleranceMode, RMTTolerance);

        Double low = RMTToSearch - delta;
        Double high = RMTToSearch + delta;

        newQuery = newQuery + "(" + aliasName + ".RMT >= " + low + " and "
                + aliasName + ".RMT <= " + high + ") and ";

        return newQuery;
    }

    /**
     * Fill the query to add the filter of the effective mobilities in CE MS
     *
     * @param aliasName
     * @param query
     * @param effMob
     * @param effMobTolerance
     *
     * @return
     */
    public static String addFilterEeffMobsCEJPA(String aliasName, String query,
            Double effMob, Double effMobTolerance) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }

        // TODO CREATE A NON-LINEAR ALGORITHM TO SET THE TOLERANCE
        Double delta = effMob * effMobTolerance / 100;

        Double low = effMob - delta;
        Double high = effMob + delta;

        newQuery = newQuery + "(" + aliasName + ".effMobility >= " + low + " and "
                + aliasName + ".effMobility <= " + high + ") and ";

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
     *
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
     *
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
     *
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
        newQuery = newQuery + "ABS(" + aliasName + ".RMT - " + RMTToSearch + ")";
        return newQuery;
    }

    /**
     * Fill the query to add the order by clause for eff Mob
     *
     * @param aliasName
     * @param query
     * @param effMob
     * @param effMobTolerance
     *
     * @return
     */
    public static String addOrderByEffMobJPA(String aliasName, String query,
            Double effMob, Double effMobTolerance) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        newQuery = newQuery + "ABS(" + aliasName + ".effMobility - " + effMob + ")";
        return newQuery;
    }

    /**
     * Fill the query to add the filter for the cemsId
     *
     * @param aliasName
     * @param query
     *
     * @return
     */
    public static String addFilterEffMobsJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        newQuery = newQuery + "(" + aliasName + ".eff_mobility >= ? and "
                + aliasName + ".eff_mobility <= ? ) and ";

        return newQuery;
    }

    /**
     * Fill the query to add the filter for the cemsId
     *
     * @param aliasName
     * @param query
     *
     * @return
     */
    public static String addFilterRMTsJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains(" where ")) {
            // Add databases depending on the user selection
            newQuery = query + " WHERE ";
        } else {
            newQuery = query;
        }
        newQuery = newQuery + "(" + aliasName + ".relative_MT >= ? and "
                + aliasName + ".relative_MT <= ? ) and ";

        return newQuery;
    }

    /**
     * Fill the query to add the filter from the effective mobilities
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     *
     * @return
     */
    public static String addOrderByEffMobsJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        newQuery = newQuery + "ABS(" + aliasName + ".eff_mobility - ? )";
        return newQuery;
    }

    /**
     * Fill the query to add the filter from the effective mobilities
     *
     * @param aliasName alias of the table compounds
     * @param query previous SQL query built
     *
     * @return
     */
    public static String addOrderByRMTsJDBC(String aliasName, String query) {
        String newQuery;
        if (!query.toLowerCase().contains("order by")) {
            // Add databases depending on the user selection
            newQuery = query + " order by ";
        } else {
            newQuery = query + ", ";
        }
        newQuery = newQuery + "ABS(" + aliasName + ".relative_MT - ? )";
        return newQuery;
    }

}
