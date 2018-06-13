/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testFeatures;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.Feature;
import LCMS.FeaturesGroupByRT;
import compound.Classyfire_Classification;
import compound.Compound;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import facades.MSFacade;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pathway.Pathway;
import persistence.theoreticalGroup.CompoundsGroupAdapter;

/**
 *
 * @author María 606888798
 */
public class FeaturesAndCompounds {

    public static List<Feature> generateFeaturesForTest() {
        //CS as null since we don't test hem by the moment
        List<Feature> features = new LinkedList<>();
        List<String> adducts = new LinkedList<>();
        adducts.add("M+");
        adducts.add("M-");
        adducts.add("M+H");
        adducts.add("M+H");
        adducts.add("M+Na");
        adducts.add("M+K");
        double tolerance = 0.1;
        Map<Double, Integer> CS = null;

        // TODO ALBERTO DETECT ADDUCT BASED ON CS IN FEATURE CONSTRUCTOR
        double EM, RT;
        String adductAutoDetected = "";
        EM = 399.3367;
        RT = 18.842525;
        List<CompoundsLCMSGroupByAdduct> annotationsGroupedByAdduct = new LinkedList<>();
        /*
        CompoundsLCMSGroupByAdduct compoundsLCMSMH = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts,
                true, EM, RT, CS, true, "M+H");
        CompoundsLCMSGroupByAdduct compoundsLCMSMNa = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts,
                true, EM, RT, CS, true, "M+Na");
        CompoundsLCMSGroupByAdduct compoundsLCMSMHH2O = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts,
                true, EM, RT, CS, true, "M+H-H2O");
        
        annotationsGroupedByAdduct.add(compoundsLCMSMH);
        annotationsGroupedByAdduct.add(compoundsLCMSMNa);
        annotationsGroupedByAdduct.add(compoundsLCMSMHH2O);
         */

        Feature f1 = new Feature(EM, RT, CS, true, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        adductAutoDetected = "";
        EM = 421.31686;
        RT = 18.842525;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f2 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        // TODO COMPLETE WITH REAL DATA
        adductAutoDetected = "";
        EM = 66.76;
        RT = 18.842525;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f3 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        adductAutoDetected = "M+K";
        EM = 315.2424;
        RT = 8.144917;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f3_1 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        adductAutoDetected = "";
        EM = 337.2234;
        RT = 8.144917;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f4 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        adductAutoDetected = "M-";
        EM = 280.2402;
        RT = 28.269503;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f5 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        adductAutoDetected = "";
        EM = 67.987;
        RT = 0;
        annotationsGroupedByAdduct = getCompoundsByExperimentalMassAndAdduct(tolerance, adducts, EM, RT, CS, true, adductAutoDetected);
        Feature f6 = new Feature(EM, RT, CS, false, adductAutoDetected, true, annotationsGroupedByAdduct, 1);

        features.add(f1);
        features.add(f2);
        features.add(f3);
        features.add(f4);
        features.add(f5);
        features.add(f6);
        return features;
    }

    //My "database"
    public static List<Compound> generateCompoundsLCMSForTest() {
        String inchi = "";
        String inchiKey = "";
        String smiles = "";
        Structure s1 = new Structure(inchi, inchiKey, smiles);
        String category = "";
        String mainClass = "";
        String subClass = "";
        String class_level4 = "";
        LM_Classification lm_classification = new LM_Classification(category, mainClass, subClass, class_level4);
        List<Classyfire_Classification> classyfire_classification = new LinkedList();
        String lipidType = "";
        Lipids_Classification lipids_classification = new Lipids_Classification(lipidType, 0, 0, 0);
        List<Pathway> pathways = new LinkedList();
        String lm_id = "";
        String kegg_id = "";
        String hmdb_id = "";
        String agilent_id = "";
        String in_house_id = "";
        String pc_id = "";
        String MINE_id = "";
        Compound c1 = new Compound(1, 66.97972, "a", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id,
                s1, lm_classification, classyfire_classification, lipids_classification, pathways);
        Compound c1_1 = new Compound(1, 280.23965, "b", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c1_2 = new Compound(1, 337.22395, "c", "a", "a", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c2 = new Compound(1, 314.23418, "d", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c2_1 = new Compound(1, 44.99778, "e", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c2_2 = new Compound(1, 0.0, "f", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c2_3 = new Compound(1, 67.98645, "g", "b", "b", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c3 = new Compound(1, 68.99428, "h", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c3_1 = new Compound(1, 67.98755, "i", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c3_2 = new Compound(1, 29.02384, "j", "c", "c", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c4 = new Compound(1, 398.32764, "k", "d", "d", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c5 = new Compound(1, 276.27924, "", "", "", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c6 = new Compound(1, 398.32942, "l", "e", "e", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);
        Compound c7 = new Compound(1, 338.23068, "m", "f", "f", 0, 0, 0, 0, 0, lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id, null, null, null, null, null);

        List<Compound> compounds = new LinkedList<>();
        compounds.add(c1);
        compounds.add(c2);
        compounds.add(c3);
        compounds.add(c4);
        compounds.add(c5);
        compounds.add(c6);
        compounds.add(c7);
        compounds.add(c1_1);
        compounds.add(c2_1);
        compounds.add(c2_3);
        compounds.add(c1_2);
        compounds.add(c3_1);
        compounds.add(c3_2);
        compounds.add(c2_2);

        return compounds;

    }

    private static Double getNeutralMassByEMAndAdduct(String adduct, double EM) {
        double mass = 0;
        switch (adduct) {
            case "M+":
                mass = -5.5E-4;
                break;
            case "M-":
                mass = +5.5E-4;
                break;
            case "M+H":
                mass = -1.0073;
                break;
            case "M-H":
                mass = +1.0073;
                break;
            case "M+K":
                mass = -38.9632;
                break;
            case "M+Na":
                mass = -22.9892;
                break;
            default:
                break;

        }
        return (EM + mass);
    }

    private static List<CompoundsLCMSGroupByAdduct> getCompoundsByExperimentalMassAndAdduct(
            double tolerance, List<String> adducts, 
            double EM, double RT,
            Map<Double, Integer> CS, Boolean isSignificative, String adduct) {

        List<CompoundsLCMSGroupByAdduct> compoundsLCMSGroupByAdduct = new LinkedList<>();

        if (!adduct.equals("")) {
            Double massToSearch = getNeutralMassByEMAndAdduct(adduct, EM);
            // Select in database compound.mass beteen masstosearch-tolerance and masstosearch+tolerance
            List<Compound> compounds = generateCompoundsLCMSForTest(); // MY DDBB
            List<CompoundLCMS> matchedCompounds = new LinkedList<>();

            for (Compound compound : compounds) {

                double mass = compound.getMass();

                if (mass >= massToSearch - tolerance && mass <= massToSearch + tolerance) {
                    int compound_id = compound.getCompound_id();
                    double compound_mass = mass;
                    String formula = compound.getFormula();
                    String name = compound.getCompound_name();
                    String cas_id = compound.getCas_id();
                    int formulatype = compound.getFormula_type();
                    int compoundtype = compound.getCompound_type();
                    int compoundstatus = compound.getCompound_status();
                    int chargeType = compound.getCharge_type();
                    int chargeNumber = compound.getCharge_number();
                    String lm_id = compound.getLm_id();
                    String kegg_id = compound.getKegg_id();
                    String hmdb_id = compound.getHmdb_id();
                    String agilent_id = compound.getMetlin_id();
                    String in_house_id = compound.getIn_house_id();
                    String pc_id = compound.getPc_id();
                    String MINE_id = compound.getMINE_id();
                    Structure structure = compound.getStructure();
                    LM_Classification lm_classification = compound.getLm_classification();
                    List<Classyfire_Classification> classyfire_classification = compound.getClasssyfire_classification();
                    Lipids_Classification lipids_classification = compound.getLipids_classification();
                    List<Pathway> pathways = compound.getPathways();

                    CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, adduct, isSignificative,
                            compound_id, compound_mass, formula, name, cas_id, formulatype,
                            compoundtype, compoundstatus, chargeType, chargeNumber,
                            lm_id, kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id,
                            structure, lm_classification, classyfire_classification, lipids_classification, pathways);
                    compoundLCMS.setShowPathways(true);
                    matchedCompounds.add(compoundLCMS);
                }
            }
            CompoundsLCMSGroupByAdduct compoundgroupByadduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS, 
                    adduct, matchedCompounds);
            compoundsLCMSGroupByAdduct.add(compoundgroupByadduct);
            return compoundsLCMSGroupByAdduct;

        } else {

            for (String an_adduct : adducts) {

                Double massToSearch = getNeutralMassByEMAndAdduct(an_adduct, EM);
                // Select in database compound.mass beteen masstosearch-tolerance and masstosearch+tolerance
                List<Compound> compounds = generateCompoundsLCMSForTest(); // MY DDBB
                List<CompoundLCMS> matchedCompounds = new LinkedList<>();

                for (Compound compound : compounds) {
                    double mass = compound.getMass();
                    if (mass >= massToSearch - tolerance && mass <= massToSearch + tolerance) {
                        int compound_id = compound.getCompound_id();
                        double compound_mass = mass;
                        String formula = compound.getFormula();
                        String name = compound.getCompound_name();
                        String cas_id = compound.getCas_id();
                        int formulatype = compound.getFormula_type();
                        int compoundtype = compound.getCompound_type();
                        int compoundstatus = compound.getCompound_status();
                        int chargeType = compound.getCharge_type();
                        int chargeNumber = compound.getCharge_number();
                        String lm_id = compound.getLm_id();
                        String kegg_id = compound.getKegg_id();
                        String hmdb_id = compound.getHmdb_id();
                        String agilent_id = compound.getMetlin_id();
                        String in_house_id = compound.getIn_house_id();
                        String pc_id = compound.getPc_id();
                        String MINE_id = compound.getMINE_id();
                        Structure structure = compound.getStructure();
                        LM_Classification lm_classification = compound.getLm_classification();
                        List<Classyfire_Classification> classyfire_classification = compound.getClasssyfire_classification();
                        Lipids_Classification lipids_classification = compound.getLipids_classification();
                        List<Pathway> pathways = compound.getPathways();

                        CompoundLCMS compoundLCMS = new CompoundLCMS(EM, RT, CS, an_adduct, isSignificative,
                                compound_id, compound_mass, formula, name, cas_id, formulatype, compoundtype,
                                compoundstatus, chargeType, chargeNumber,
                                lm_id,kegg_id, hmdb_id, agilent_id, in_house_id, pc_id, MINE_id,
                                structure, lm_classification, classyfire_classification, lipids_classification, pathways);
                        compoundLCMS.setShowPathways(true);
                        matchedCompounds.add(compoundLCMS);
                    }
                }

                CompoundsLCMSGroupByAdduct compoundgroupByadduct = new CompoundsLCMSGroupByAdduct(EM, RT, CS, an_adduct, matchedCompounds);
                compoundsLCMSGroupByAdduct.add(compoundgroupByadduct);
            }
            return compoundsLCMSGroupByAdduct;
        }
    }

    public static void main(String[] args) {

        List<Feature> features = generateFeaturesForTest();
        for (Feature feature : features) {
            System.out.println("FEATURE DATA => EM: " + feature.getEM() + " RT: " + feature.getRT() + " ADDUCT: " + feature.getAdductAutoDetected());
            List<CompoundsLCMSGroupByAdduct> c_LCMSGroupByAdduct = feature.getAnnotationsGroupedByAdduct();
            int c1 = 0;
            for (CompoundsLCMSGroupByAdduct c_group : c_LCMSGroupByAdduct) {
                c1++;
                System.out.println("  " + c1 + ". Compounds Group By Adduct " + c_group.getAdduct() + " with EM " + c_group.getEM() + " and RT " + c_group.getRT());
                List<CompoundLCMS> compounds = c_group.getCompounds();
                int c2 = 0;
                for (CompoundLCMS c : compounds) {
                    c2++;
                    System.out.println("    " + c2 + ". Compound with mass= " + c.getMass());
                }
            }
        }

    }
}
