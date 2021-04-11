/*
 * OxidationFacade.java
 *
 * Created on 23-may-2018, 12:21:50
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package facades;

import DBManager.DBManager;
import DBManager.QueryConstructor;
import compound.Classyfire_Classification;
import compound.CMMCompound;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import exceptions.OxidationTypeException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import oxidation.CompoundFA;
import oxidation.CompoundOxidized;
import pathway.Pathway;
import utilities.ConstantesForOxidation;
import static utilities.OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
import static utilities.OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
import utilities.OxidationProcessing;
import utilities.Utilities;
import static utilities.Utilities.calculateFAEMFromPIandOtherFAEM;
import static utilities.OxidationLists.MAPOXIDATIONMZS;

/**
 * OxidationFacade. Middleware to connect the CMM Oxidation application with the
 * database
 *
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1 20-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class OxidationFacade {

    private static final DBManager DBM = new DBManager();
    private final Connection conn;
    private final MSFacade msfacade;

    /**
     * Creates a new instance of OxidationFacade. It creates a connection agains
     * the database
     */
    public OxidationFacade() {
        msfacade = new MSFacade();
        this.conn = OxidationFacade.DBM.connect();
    }

    public void disconnect() {
        OxidationFacade.DBM.disconnect();
        this.msfacade.disconnect();
    }

    /**
     * returns the oxidized compound looking into possible oxidations formed. It
     * searches for the FA masses (queryFattyAcidMasses). Possible oxidations
     * are in the param possibleOxidations and a OxidizedTheoreticalCompound is
     * returned
     *
     * @param parentIonEM
     * @param FAsEM
     * @param toleranceModeForFAs
     * @param toleranceForFAs
     * @param toleranceModeForPI
     * @param toleranceForPI
     * @param ionMode
     * @param databases
     * @param possibleOxidations
     * @return the theoretical LC oxidized compound
     */
    public List<CompoundOxidized> findLCOxidizedFA(
            Double parentIonEM,
            List<Double> FAsEM,
            String toleranceModeForFAs,
            Double toleranceForFAs,
            String toleranceModeForPI,
            Double toleranceForPI,
            String ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<CompoundOxidized> oxidizedPCsList = new LinkedList<>();

        CompoundFA nonOxidizedFA = null;
        CompoundFA oxidizedFA;
        List<String> oxidations = OxidationProcessing.chooseLCOxidations(possibleOxidations);

        int nonOxidizedFAPosition;
        int oxidizedFAPosition = -1;
        Double mzOxidizedFAMass = 0d;
        double mzNONOxidizedFAMass = 0;
        double parentIonNeutralMass = parentIonEM - ConstantesForOxidation.HCOO_WEIGHT;
        // IF THERE IS NOT NON OXIDIZED COMPOUND
// CASE 1. Only Parent ION and Oxidized FA
        if (FAsEM.size() == 1) {
            oxidizedFAPosition = 0;
            mzOxidizedFAMass = FAsEM.get(oxidizedFAPosition);
            mzNONOxidizedFAMass = calculateFAEMFromPIandOtherFAEM(parentIonNeutralMass, mzOxidizedFAMass);
            nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 1);

        } else if (FAsEM.size() == 2) {
// First, identify which is the oxidized FA and which is the non oxidized FA
// First one should be the oxidized one and second one should be non oxidized

// TODO CHECK IF THE FA OF BOTH OXIDIZED COMPOUNDS ARE RELATIONED WITH THE PARENT ION MASS
// IF NOT -> MESSAGE TO THE USER?
            for (int i = FAsEM.size() - 1; i >= 0; i--) {
                Double mzFattyAcidMass = FAsEM.get(i);
                nonOxidizedFA = getNonOxidizedFA(mzFattyAcidMass, 0);
                if (nonOxidizedFA != null) {
                    mzNONOxidizedFAMass = mzFattyAcidMass;
                    nonOxidizedFAPosition = i;
                    if (i == 0) {
                        oxidizedFAPosition = 1;
                        mzOxidizedFAMass = FAsEM.get(oxidizedFAPosition);
                    } else {
                        oxidizedFAPosition = 0;
                        mzOxidizedFAMass = FAsEM.get(oxidizedFAPosition);
                    }
                    break;
                }
            }
        } else if (FAsEM.isEmpty()) {
            // TODO WRITE IN LOG 
            // AND RETURN AN EMPTY LIST
            return oxidizedPCsList;
        } else {
            // TODO WRITE IN LOG
            // There are more FA than 2
            return oxidizedPCsList;
        }

        if (oxidizedFAPosition == 0 || oxidizedFAPosition == 1) {
            Double massToSearchForOxFA;
            String sqlStart;
            String sqlFinal;
            ResultSet rs;
            PreparedStatement prepSt;
            String aliasTable = "c";
            sqlStart = QueryConstructor.createStartSQLForFASearch();
            sqlStart = QueryConstructor.addFilterMassesJDBC(aliasTable, sqlStart);
            sqlFinal = QueryConstructor.addOrderByMassesJDBC(aliasTable, sqlStart);

            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
// TODO TAKE INTO ACCOUNT THAT MORE THAN ONE HYPOTHESIS CAN OCCUR!
                if (LIST_LONG_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    CompoundOxidized oxidizedPC;
// Possible oxidation in long chain
                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONMZS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.PROTON_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;

                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                    Double delta = Utilities.calculateDeltaPPM(massToSearchForOxFA, toleranceModeForFAs, toleranceForFAs);
                    Double low = massToSearchForOxFA - delta;
                    Double high = massToSearchForOxFA + delta;
                    //System.out.println("SQL FOR OXIDIZED: " + sqlFinal);

                    try {
                        prepSt = conn.prepareStatement(sqlFinal);
                        prepSt.setString(1, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(0));
                        prepSt.setString(2, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(1));
                        prepSt.setDouble(3, low);
                        prepSt.setDouble(4, high);
                        prepSt.setDouble(5, massToSearchForOxFA);
                        rs = prepSt.executeQuery();
                        if (rs.next()) {
                            int compound_id = rs.getInt("compound_id");
                            double mass = rs.getDouble("mass");
                            String formula = rs.getString("formula");
                            String name = rs.getString("compound_name");
                            int compound_type = rs.getInt("compound_type");
                            int compound_status = rs.getInt("compound_status");
                            int formula_type_int = rs.getInt("formula_type_int");

                            LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                            Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                            List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                            List<Pathway> pathways = new LinkedList();
                            Structure structure = null;

                            oxidizedFA = new CompoundFA(compound_id, mass, formula, name, formula_type_int,
                                    compound_type, compound_status, structure,
                                    lm_classification, classyfire_classifications, lipids_classification, pathways,
                                    mzOxidizedFAMass, massToSearchForOxFA, oxid);
                        } else {
                            oxidizedFA = null;
                        }
// CREATE PC WITH OXIDIZED AND NON OXIDIZED FA, AND ADD ANNOTATIONS OF 
// OXIDIZED AND NON OXIDIZED COMOPOUNDS IN THIS PC! 
                        try {
                            oxidizedPC = new CompoundOxidized(mzOxidizedFAMass,
                                    mzNONOxidizedFAMass,
                                    parentIonEM,
                                    oxid,
                                    ConstantesForOxidation.HCOO_ADDUCT,
                                    oxidizedFA,
                                    nonOxidizedFA);
                            addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                            addNonOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                            oxidizedPCsList.add(oxidizedPC);
                        } catch (OxidationTypeException oxTypeExc) {
                            Logger.getLogger(OxidationFacade.class.getName()).log(Level.WARNING, null, oxTypeExc);
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(OxidationFacade.class.getName()).log(Level.SEVERE, null, ex);
                        //return null;
                    }
                }
            }
            return oxidizedPCsList;
        } else {
            return oxidizedPCsList;
        }
    }

    /**
     * returns the oxidized compound looking into possible oxidations formed. It
     * searches for the putative (queryFattyAcidMasses) of the non oxidized FA.
     * Possible oxidations are in the param possibleOxidations and a
     * OxidizedTheoreticalCompound is returned Nevertheless, it looks based on
     * the non oxidized FA, and then calculates the mass of the SC oxidized FA
     *
     * @param parentIonEM
     * @param FAsEM
     * @param toleranceModeForFA
     * @param toleranceForFA
     * @param toleranceModeForPI
     * @param toleranceForPI
     * @param ionMode
     * @param databases
     * @param possibleOxidations
     * @return the theoretical SC oxidized compound
     */
    public List<CompoundOxidized> findSCOxidizedFA(
            Double parentIonEM,
            List<Double> FAsEM,
            String toleranceModeForFA,
            Double toleranceForFA,
            String toleranceModeForPI,
            Double toleranceForPI,
            String ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<CompoundOxidized> oxidizedPCsList = new LinkedList<>();

        CompoundFA nonOxidizedFA;
        CompoundFA oxidizedFA;
        List<String> oxidations = OxidationProcessing.chooseSCOxidations(possibleOxidations);
        // FACompoundFactory FAcFactory = new FACompoundFactory();

        int nonOxidizedFAPosition;
        Double mzOxidizedFAMass;
        double mzNONOxidizedFAMass;
        double parentIonNeutralMass;

        // IF THERE IS NOT NON OXIDIZED COMPOUND
// CASE 1. Only Parent ION and Oxidized FA
// There are two hypothesys here. The parent ion EM could be a M+HCOO adduct 
// or a M-H adduct. Take into account both of them
// TODO ISOLATE A METHOD TO SEARCH OXIDIZEDFA and USE THEM IN BOTH METHODS!
        if (FAsEM.size() == 1) {
            parentIonNeutralMass = parentIonEM - ConstantesForOxidation.HCOO_WEIGHT;
            nonOxidizedFAPosition = 0;
            mzNONOxidizedFAMass = FAsEM.get(nonOxidizedFAPosition);
            mzOxidizedFAMass = calculateFAEMFromPIandOtherFAEM(parentIonNeutralMass, mzNONOxidizedFAMass);
            nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 0);

        } else if (FAsEM.size() == 2) {
// Write in log. 
// In short chain oxidation, the SC oxidized FA appears in the low region, which is difficult to see
// Because of the fragments that there are in this region of the cromatogram.
// RETURN AN EMPTY LIST
            return oxidizedPCsList;
        } else if (FAsEM.isEmpty()) {
            // TODO WRITE IN LOG 
            // AND RETURN AN EMPTY LIST
            return oxidizedPCsList;
        } else {
            // TODO WRITE IN LOG
            // There are more FA than 2
            return oxidizedPCsList;
        }

        if (nonOxidizedFAPosition == 0) {

            Double massToSearchForOxFA;
            String sqlStart;
            String sqlFinal;
            ResultSet rs;
            PreparedStatement prepSt;
            String aliasTable = "c";
            sqlStart = QueryConstructor.createStartSQLForFASearch();
            sqlStart = QueryConstructor.addFilterMassesJDBC(aliasTable, sqlStart);
            sqlFinal = QueryConstructor.addOrderByMassesJDBC(aliasTable, sqlStart);
            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
                if (LIST_SHORT_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    CompoundOxidized oxidizedPC;
// Possible oxidation in long chain

                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONMZS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;

                    Double delta = Utilities.calculateDeltaPPM(massToSearchForOxFA, toleranceModeForFA, toleranceForFA);
                    Double low = massToSearchForOxFA - delta;
                    Double high = massToSearchForOxFA + delta;
                    //System.out.println("SQL FOR OXIDIZED: " + sqlFinal);

                    try {
                        prepSt = conn.prepareStatement(sqlFinal);
                        prepSt.setString(1, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(0));
                        prepSt.setString(2, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(1));
                        prepSt.setDouble(3, low);
                        prepSt.setDouble(4, high);
                        prepSt.setDouble(5, massToSearchForOxFA);
                        rs = prepSt.executeQuery();

                        if (rs.next()) {
                            int compound_id = rs.getInt("compound_id");
                            double mass = rs.getDouble("mass");
                            String formula = rs.getString("formula");
                            String name = rs.getString("compound_name");
                            int compound_type = rs.getInt("compound_type");
                            int compound_status = rs.getInt("compound_status");
                            int formula_type_int = rs.getInt("formula_type_int");

                            LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                            Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                            List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                            List<Pathway> pathways = new LinkedList();
                            Structure structure = null;

                            oxidizedFA = new CompoundFA(compound_id, mass, formula, name,
                                    formula_type_int, compound_type, compound_status, structure,
                                    lm_classification, classyfire_classifications, lipids_classification, pathways,
                                    mzOxidizedFAMass, massToSearchForOxFA, oxid);
                            // CREATE PC WITH OXIDIZED AND NON OXIDIZED FA, AND ADD ANNOTATIONS OF 
// OXIDIZED AND NON OXIDIZED COMOPOUNDS IN THIS PC! 
                        } else {
                            oxidizedFA = null;
                        }
                        try {
                            oxidizedPC = new CompoundOxidized(mzOxidizedFAMass,
                                    mzNONOxidizedFAMass,
                                    parentIonEM,
                                    oxid,
                                    ConstantesForOxidation.HCOO_ADDUCT,
                                    oxidizedFA,
                                    nonOxidizedFA);
                            addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                            oxidizedPCsList.add(oxidizedPC);
                        } catch (OxidationTypeException oxTypeExc) {
                            Logger.getLogger(OxidationFacade.class.getName()).log(Level.WARNING, null, oxTypeExc);
                        }
// If the oxidation hypothesis is COOH, then the adduct formed may have been M+HCOO or M-H
// Add the hypothesis of M-H
                        if (oxid.equals(ConstantesForOxidation.OXIDATIONTYPE_FOR_H_ADDUCT)) {

                            parentIonNeutralMass = parentIonEM + ConstantesForOxidation.PROTON_WEIGHT;
                            mzOxidizedFAMass = calculateFAEMFromPIandOtherFAEM(parentIonNeutralMass, mzNONOxidizedFAMass);
                            nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 0);

                            oxidationValue = MAPOXIDATIONMZS.get(oxid);
                            oxidationDouble = Double.parseDouble(oxidationValue);
                            massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                            massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;

                            sqlFinal = QueryConstructor.addOrderByMassesJDBC(aliasTable, sqlStart);
                            delta = Utilities.calculateDeltaPPM(massToSearchForOxFA, toleranceModeForFA, toleranceForFA);
                            low = massToSearchForOxFA - delta;
                            high = massToSearchForOxFA + delta;
                            //System.out.println("SQL FOR OXIDIZED: " + sqlFinal);

                            try {
                                prepSt = conn.prepareStatement(sqlFinal);

                                prepSt.setString(1, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(0));
                                prepSt.setString(2, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(1));
                                prepSt.setDouble(3, low);
                                prepSt.setDouble(4, high);
                                prepSt.setDouble(5, massToSearchForOxFA);
                                rs = prepSt.executeQuery();

                                if (rs.next()) {
                                    int compound_id = rs.getInt("compound_id");
                                    double mass = rs.getDouble("mass");
                                    String formula = rs.getString("formula");
                                    String name = rs.getString("compound_name");
                                    int compound_type = rs.getInt("compound_type");
                                    int compound_status = rs.getInt("compound_status");

                                    LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                                    Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                                    List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                                    List<Pathway> pathways = new LinkedList();
                                    Structure structure = null;

                                    oxidizedFA = new CompoundFA(compound_id, mass, formula, name, 1, compound_type, compound_status,
                                            structure, lm_classification, classyfire_classifications, lipids_classification, pathways,
                                            mzOxidizedFAMass, massToSearchForOxFA, oxid);
                                } else {
                                    oxidizedFA = null;
                                }
// CREATE PC WITH OXIDIZED AND NON OXIDIZED FA, AND ADD ANNOTATIONS OF 
// OXIDIZED AND NON OXIDIZED COMOPOUNDS IN THIS PC! 
                                try {
                                    oxidizedPC = new CompoundOxidized(mzOxidizedFAMass,
                                            mzNONOxidizedFAMass,
                                            parentIonEM,
                                            oxid,
                                            ConstantesForOxidation.NEG_H_ADDUCT,
                                            oxidizedFA,
                                            nonOxidizedFA);
                                    addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                                    oxidizedPCsList.add(oxidizedPC);
                                } catch (OxidationTypeException oxTypeExc) {
                                    Logger.getLogger(OxidationFacade.class.getName()).log(Level.WARNING, null, oxTypeExc);
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(OxidationFacade.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(MSFacade.class.getName()).log(Level.SEVERE, null, ex);
                        //return null;
                    }
                }
            }
            return oxidizedPCsList;
        } else {
            return oxidizedPCsList;
        }
    }

    /**
     * returns if mass querymzFattyAcidMass corresponds to a Fatty Acid based on
     * the tolerance
     *
     * @param querymzFattyAcidMass
     * @param modeQuerymzFattyAcidMass. 0 for experimental mass. 1 for
     * calculated mass
     * @return if mass querymzFattyAcidMass corresponds to a Fatty Acid
     */
    private CompoundFA getNonOxidizedFA(Double querymzFattyAcidMass, int modeQuerymzFattyAcidMass) {
        double toleranceForFA = 10;
        String toleranceModeForFA = "mDa";
        return getNonOxidizedFA(querymzFattyAcidMass, modeQuerymzFattyAcidMass, toleranceForFA, toleranceModeForFA);
    }

    /**
     * returns a CompoundFA if mass querymzNonOxFattyAcid corresponds to a Fatty
     * Acid based on the tolerance
     *
     * @param querymzNonOxFattyAcid
     * @param modeQuerymzNonFattyAcid. 0 for experimental mass. 1 for calculated
     * mass
     * @param toleranceModeForFA
     * @param toleranceForFA
     * @return if mass querymzFattyAcidMass corresponds to a Fatty Acid
     */
    private CompoundFA getNonOxidizedFA(
            Double querymzNonOxFattyAcid,
            int modeQuerymzNonFattyAcid,
            Double toleranceForFA,
            String toleranceModeForFA) {
        CompoundFA compoundFA = null;
        Double massToSearchForNonOxFA;

        if (modeQuerymzNonFattyAcid == 0) {
            massToSearchForNonOxFA = querymzNonOxFattyAcid + ConstantesForOxidation.PROTON_WEIGHT;
        } else {
            massToSearchForNonOxFA = querymzNonOxFattyAcid + ConstantesForOxidation.H_WEIGHT;
        }

        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        String aliasCompoundsTable = "c";
        sql = QueryConstructor.createStartSQLForFASearch();

        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
        sql = QueryConstructor.addFilterMassesJDBC(aliasCompoundsTable, sql);
        sql = QueryConstructor.addOrderByMassesJDBC(aliasCompoundsTable, sql);

        Double delta = Utilities.calculateDeltaPPM(massToSearchForNonOxFA, toleranceModeForFA, toleranceForFA);
        Double low = massToSearchForNonOxFA - delta;
        Double high = massToSearchForNonOxFA + delta;
        //System.out.println("sql FOR NON OXIDIZED: " + sql);

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setString(1, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(0));
            prepSt.setString(2, ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION_LIST.get(1));
            prepSt.setDouble(3, low);
            prepSt.setDouble(4, high);
            prepSt.setDouble(5, massToSearchForNonOxFA);
            rs = prepSt.executeQuery();

            if (rs.next()) {
                int compound_id = rs.getInt("compound_id");
                double mass = rs.getDouble("mass");
                String formula = rs.getString("formula");
                String name = rs.getString("compound_name");
                int compound_type = rs.getInt("compound_type");
                int compound_status = rs.getInt("compound_status");
                int formula_type_int = rs.getInt("formula_type_int");

                LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                List<Pathway> pathways = new LinkedList();
                Structure structure = null;

                compoundFA = new CompoundFA(compound_id, mass, formula, name,
                        formula_type_int, compound_type, compound_status, structure,
                        lm_classification, classyfire_classifications, lipids_classification, pathways,
                        querymzNonOxFattyAcid, massToSearchForNonOxFA, "");

            }
        } catch (SQLException ex) {
            Logger.getLogger(MSFacade.class
                    .getName()).log(Level.SEVERE, null, ex);
            //return null;
        }
        return compoundFA;
    }

    /**
     *
     * Adds to the oxidized compound oxidizedcompound all the annotations in the
     * query results according the mass of the precursor. This method works for
     * long and short chain due to the transformation on the FA also occurs in
     * the oxidized compound
     *
     * @param oxidizedCompound. The compound to add the annotations
     * @param queryParentIonMass queryParentIonMass to be searched
     * @param toleranceModeForPI. Tolerance mode for PI (ppm or mDa)
     * @param toleranceForPI. Value of Tolerance for the search of the precursor
     * @param oxidationType Oxidation Type
     */
    private void addOxidizedAnnotationsOverOxidizedPC(CompoundOxidized oxidizedCompound,
            String toleranceModeForPI,
            Double toleranceForPI) {

        if (!oxidizedCompound.isThereFATheoreticalCompounds()) {
            return;
        }

        double experimentalMassPI;
        experimentalMassPI = oxidizedCompound.getParentIonEM();

        double neutralMassPI;
        neutralMassPI = oxidizedCompound.getNeutralMassPI();
        String oxidationType;
        oxidationType = oxidizedCompound.getOxidationType();

        int numCarbons;
        numCarbons = oxidizedCompound.getNumCarbonsInFAs();
        int doubleBonds;
        doubleBonds = oxidizedCompound.getNumDoubleBondsInFAs();

        int numCarbonsOfOxidizedFA;
        numCarbonsOfOxidizedFA = oxidizedCompound.getOxidizedFA().getNumCarbons();
        int doubleBondsOfOxidizedFA;
        doubleBondsOfOxidizedFA = oxidizedCompound.getOxidizedFA().getNumDoubleBonds();

        int numCarbonsOfNonOxidizedFA;
        numCarbonsOfNonOxidizedFA = oxidizedCompound.getNonOxidizedFA().getNumCarbons();
        int doubleBondsOfNonOxidizedFA;
        doubleBondsOfNonOxidizedFA = oxidizedCompound.getNonOxidizedFA().getNumDoubleBonds();

        String oxidation = oxidizedCompound.getOxidationType();

        Double massToSearch; // Mass to search based on adducts
        // Finish the query with the mass condition

        // set the number of carbons and double bonds for oxidized FA. 
        // The query for Non Oxidized is not needed since the mass already matched
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTable = "c";
        sql = QueryConstructor.createSQLForCompoundIdFromSubClass_Carbons_DoubleBonds();

        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
        } else {
            return;
        }
        sql = QueryConstructor.addFilterMassesJDBC(aliasTable, sql);
        sql = QueryConstructor.addOrderByMassesJDBC(aliasTable, sql);

        Double delta = Utilities.calculateDeltaPPM(massToSearch, toleranceModeForPI, toleranceForPI);
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;
        //System.out.println("SQL FOR OXIDIZED ANNOTATIONS: " + sql);

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setString(1, ConstantesForOxidation.PC_OXIDIZED_LIPIDS_LM_CLASSIFICATION_JDBC);
            prepSt.setInt(2, numCarbonsOfOxidizedFA);
            prepSt.setInt(3, doubleBondsOfOxidizedFA);
            /*
            prepSt.setString(4, oxidation);
            prepSt.setDouble(5, low);
            prepSt.setDouble(6, high);
            prepSt.setDouble(7, massToSearch);
             */
            prepSt.setDouble(4, low);
            prepSt.setDouble(5, high);
            prepSt.setDouble(6, massToSearch);
            rs = prepSt.executeQuery();
            while (rs.next()) {
                int compound_id = rs.getInt("compound_id");
                String sql2;
                ResultSet rs2;
                PreparedStatement prepSt2;
                sql2 = QueryConstructor.createSQLForPCCompoundFromCompoundId_Carbons_DoubleBonds();
                // System.out.println("SQL2" +  sql2);
                prepSt2 = conn.prepareStatement(sql2);
                prepSt2.setInt(1, compound_id);
                prepSt2.setInt(2, numCarbonsOfNonOxidizedFA);
                prepSt2.setInt(3, doubleBondsOfNonOxidizedFA);
                //prepSt2.setString(4, "");
                rs2 = prepSt2.executeQuery();

                if (rs2.next()) {
                    double mass = rs2.getDouble("mass");
                    String formula = rs2.getString("formula");
                    String name = rs2.getString("compound_name");
                    String cas_id = rs2.getString("cas_id");
                    int charge_type = rs2.getInt("charge_type");
                    int charge_number = rs2.getInt("charge_number");
                    int formula_type_int = rs2.getInt("formula_type_int");
                    int compound_type = rs2.getInt("compound_type");
                    int compound_status = rs2.getInt("compound_status");
                    String lm_id = rs2.getString("lm_id");
                    String kegg_id = rs2.getString("kegg_id");
                    String hmdb_id = rs2.getString("hmdb_id");
                    String agilent_id = rs2.getString("agilent_id");
                    String in_house_id = rs2.getString("in_house_id");
                    String aspergillus_id = rs2.getString("aspergillus_id");
                    int fahfa_id = rs2.getInt("fahfa_id");
                    int oh_position = rs2.getInt("oh_position");
                    String mesh_nomenclature = rs2.getString("mesh_nomenclature");
                    String iupac_classification = rs2.getString("iupac_classification");
                    String aspergillus_web_name = rs2.getString("aspergillus_web_name");
                    Integer pc_id = rs2.getInt("pc_id");
                    Integer chebi_id = rs2.getInt("chebi_id");
                    String knapsack_id = rs2.getString("knapsack_id");
                    Integer npatlas_id = rs2.getInt("npatlas_id");
                    Integer MINE_id = null;
                    LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                    Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                    List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                    List<Pathway> pathways = new LinkedList();
                    Structure structure = null;
                    CMMCompound oxidizedAnnotation = new CMMCompound(compound_id, mass, formula, name, cas_id,
                            formula_type_int, compound_type, compound_status, charge_type, charge_number,
                            lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                            aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                            fahfa_id, oh_position,
                            structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                    oxidizedCompound.addOxidizedAnnotation(oxidizedAnnotation);
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQL EXCEPTION ADDING OXIDIZED ANNOTATIONS");
            Logger
                    .getLogger(MSFacade.class
                            .getName()).log(Level.SEVERE, null, ex);
            //return null;
        }
    }

    /**
     *
     *
     * Adds to the oxidized compound oxidizedcompound all the non Oxidizided
     * annotations in the query results according the mass of the precursor.
     * This method works only for long chain oxidation. Short chain oxidized
     * compounds suffer alterations in the structure, so it is not possible to
     * know the non oxidized compounds/mass
     *
     * @param oxidizedCompound. The compound to add the annotations
     * @param queryParentIonMass queryParentIonMass to be searched
     * @param toleranceModeForPI. Tolerance mode for PI (ppm or mDa)
     * @param toleranceForPI. Value of Tolerance for the search of the precursor
     * @param oxidationType Oxidation Type
     */
    private void addNonOxidizedAnnotationsOverOxidizedPC(CompoundOxidized oxidizedCompound,
            String toleranceModeForPI,
            Double toleranceForPI) {
        if (!oxidizedCompound.isThereFATheoreticalCompounds()) {
            return;
        }

        double experimentalMassPI;
        experimentalMassPI = oxidizedCompound.getParentIonEM();

        double neutralMassPI;
        neutralMassPI = oxidizedCompound.getNeutralMassPI();
        String oxidationType;
        oxidationType = oxidizedCompound.getOxidationType();

        int numCarbons;
        numCarbons = oxidizedCompound.getNumCarbonsInFAs();
        int doubleBonds;
        doubleBonds = oxidizedCompound.getNumDoubleBondsInFAs();

        int numCarbonsOfOxidizedFA;
        numCarbonsOfOxidizedFA = oxidizedCompound.getOxidizedFA().getNumCarbons();
        int doubleBondsOfOxidizedFA;
        doubleBondsOfOxidizedFA = oxidizedCompound.getOxidizedFA().getNumDoubleBonds();

        int numCarbonsOfNonOxidizedFA;
        numCarbonsOfNonOxidizedFA = oxidizedCompound.getNonOxidizedFA().getNumCarbons();
        int doubleBondsOfNonOxidizedFA;
        doubleBondsOfNonOxidizedFA = oxidizedCompound.getNonOxidizedFA().getNumDoubleBonds();

        if (!LIST_LONG_CHAIN_OXIDATION_TYPES.contains(oxidationType)) {
            return;
        }
        //System.out.println("ADDING NON OXIDIZED ANNOTATION");

        // set the number of carbons and double bonds for oxidized FA. 
        // The query for Non Oxidized is not needed since the mass already matched
        String sql;
        ResultSet rs;
        PreparedStatement prepSt;
        String aliasTable = "c";
        sql = QueryConstructor.createSQLForCompoundIdAndMassFromMainClass_Carbons_DoubleBonds();

        Double massToSearch; // Mass to search based on adducts

// Finish the query with the mass condition
        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
            // Also include the oxidation
            Double oxidationDouble;
            String oxidationValue;
            oxidationValue = MAPOXIDATIONMZS.get(oxidationType);
            oxidationDouble = Double.parseDouble(oxidationValue);
            massToSearch = massToSearch + oxidationDouble;
        } else {
            return;
        }

        sql = QueryConstructor.addFilterMassesJDBC(aliasTable, sql);
        sql = QueryConstructor.addOrderByMassesJDBC(aliasTable, sql);

        Double delta = Utilities.calculateDeltaPPM(massToSearch, toleranceModeForPI, toleranceForPI);
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;
        //System.out.println("SQL FOR OXIDIZED ANNOTATIONS: " + sql);

        try {
            prepSt = conn.prepareStatement(sql);
            prepSt.setString(1, ConstantesForOxidation.PC_NON_OXIDIZED_LIPIDS_LM_CLASSIFICATION_JDBC);
            prepSt.setInt(2, numCarbonsOfOxidizedFA);
            prepSt.setInt(3, doubleBondsOfOxidizedFA);
            /*
            prepSt.setString(4, oxidation);
            prepSt.setDouble(5, low);
            prepSt.setDouble(6, high);
            prepSt.setDouble(7, massToSearch);
             */
            prepSt.setDouble(4, low);
            prepSt.setDouble(5, high);
            prepSt.setDouble(6, massToSearch);
            rs = prepSt.executeQuery();

            while (rs.next()) {
                int compound_id = rs.getInt("compound_id");
                String sql2;
                ResultSet rs2;
                PreparedStatement prepSt2;
                sql2 = QueryConstructor.createSQLForPCCompoundFromCompoundId_Carbons_DoubleBonds();
                prepSt2 = conn.prepareStatement(sql2);
                prepSt2.setInt(1, compound_id);
                prepSt2.setInt(2, numCarbonsOfNonOxidizedFA);
                prepSt2.setInt(3, doubleBondsOfNonOxidizedFA);
                //prepSt2.setString(4, "");
                rs2 = prepSt2.executeQuery();

                if (rs2.next()) {
                    double mass = rs2.getDouble("mass");
                    String formula = rs2.getString("formula");
                    String name = rs2.getString("compound_name");
                    String cas_id = rs2.getString("cas_id");
                    int charge_type = rs2.getInt("charge_type");
                    int charge_number = rs2.getInt("charge_number");
                    int formula_type_int = rs2.getInt("formula_type_int");
                    int compound_type = rs2.getInt("compound_type");
                    int compound_status = rs2.getInt("compound_status");
                    String lm_id = rs2.getString("lm_id");
                    String kegg_id = rs2.getString("kegg_id");
                    String hmdb_id = rs2.getString("hmdb_id");
                    String agilent_id = rs2.getString("agilent_id");
                    String in_house_id = rs2.getString("in_house_id");
                    String aspergillus_id = rs2.getString("aspergillus_id");
                    String mesh_nomenclature = rs2.getString("mesh_nomenclature");
                    String iupac_classification = rs2.getString("iupac_classification");
                    String aspergillus_web_name = rs2.getString("aspergillus_web_name");
                    int fahfa_id = rs2.getInt("fahfa_id");
                    int oh_position = rs2.getInt("oh_position");

                    Integer pc_id = rs2.getInt("pc_id");
                    Integer chebi_id = rs2.getInt("chebi_id");
                    String knapsack_id = rs2.getString("knapsack_id");
                    Integer npatlas_id = rs2.getInt("npatlas_id");
                    Integer MINE_id = null;
                    LM_Classification lm_classification = msfacade.getLM_ClassificationByCompound_id(compound_id);
                    Lipids_Classification lipids_classification = msfacade.getLipids_classificationByCompound_id(compound_id);
                    List<Classyfire_Classification> classyfire_classifications = new LinkedList();
                    List<Pathway> pathways = new LinkedList();
                    Structure structure = null;
                    CMMCompound nonOxidizedAnnotation = new CMMCompound(compound_id, mass, formula, name, cas_id,
                            formula_type_int, compound_type, compound_status, charge_type, charge_number,
                            lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, chebi_id, MINE_id, knapsack_id, npatlas_id,
                            aspergillus_id, mesh_nomenclature, iupac_classification, aspergillus_web_name,
                            fahfa_id, oh_position,
                            structure, lm_classification, classyfire_classifications, lipids_classification, pathways);
                    oxidizedCompound.addNonOxidizedCompoundsGroupByMass(nonOxidizedAnnotation);
                }

            }
        } catch (SQLException ex) {
            System.out.println("SQL EXCEPTION ADDING NON OXIDIZED ANNOTATIONS");
            Logger
                    .getLogger(MSFacade.class
                            .getName()).log(Level.SEVERE, null, ex);
            //return null;
        }
    }
}
