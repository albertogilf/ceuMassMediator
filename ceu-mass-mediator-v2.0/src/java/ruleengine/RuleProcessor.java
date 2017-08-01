/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruleengine;

import java.util.ArrayList;
import java.util.List;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import persistence.NewCompounds;
import persistence.NewLipidsClassification;
import persistence.theoreticalCompound.NewCompoundsAdapter;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 * Processor for ionization, precedence and retention time rules.
 *
 * @author aesteban
 */
public class RuleProcessor {

    private static final boolean ACTIVE = true;

    /**
     * Method for rules execution over a theoretical compound list.
     *
     * @param compounds
     * @param configFilter
     * @return
     */
    public static List<TheoreticalCompounds> processCompounds(List<TheoreticalCompounds> compounds, ConfigFilter configFilter) {
        if (ACTIVE) {
            try {

                // TEST.
                //compounds = getPrecedenceRulesTest();
                //compounds = getRetentionTimeRulesTest();
                // ----------------------------------------------------
                
                System.out.println("######### Processing advanced search #########");
                System.out.println("#########--> Num. Compounds: " + compounds.size());
                System.out.println("#########--> Ionization: " + configFilter.getIonMode());
                System.out.println("#########--> Modifier: " + configFilter.getModifier());
                System.out.println("#########--> All Compounds: " + configFilter.getAllCompounds());
                
                long time = System.currentTimeMillis();

                KieContainer kContainer = KieServices.Factory.get().getKieClasspathContainer();
                KieSession kSession = null;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");

                kSession = kContainer.newKieSession("LipidsTypeIonRulesKSession");
                kSession.insert(configFilter);
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                // -------------------------------------
                // ADDUCTS RELATIONS RULES
                // -------------------------------------
                System.out.println("#########--> ADDUCTS RELATIONS rules. START.");

                kSession = kContainer.newKieSession("AdductRelationRulesKSession");
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

                System.out.println("#########--> ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                if (configFilter.getAllCompounds()) {

                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS rules. START.");

                    kSession = kContainer.newKieSession("LackAdductRelationRulesKSession");
                    kSession.insert(configFilter);
                    for (TheoreticalCompounds compo : compounds) {
                        kSession.insert(compo);
                    }
                    kSession.fireAllRules();
                    kSession.dispose();

                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));
                    time = System.currentTimeMillis();
                }

                // -------------------------------------
                // PRECEDENCE RULES
                // -------------------------------------
                // Deleted temporaly
                /*
                System.out.println("#########--> Precedence rules. START.");
                
                kSession = kContainer.newKieSession("LipidsPrecedenceRulesKSession");
                for (TheoreticalCompounds compo: compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();               

                System.out.println("#########--> Precedence rules. END. Time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis(); 
                 */
                // -------------------------------------
                // RETENTION TIME RULES
                // -------------------------------------
                System.out.println("#########--> Retention Time rules. START.");

                kSession = kContainer.newKieSession("LipidsRetentionTimeKSession");
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

                System.out.println("#########--> Retention Time. END. Time: " + (System.currentTimeMillis() - time));

                // -------------------------------------
                // TRACE
                // -------------------------------------
                for (TheoreticalCompounds compo : compounds) {
                    if (compo.getCategory() != null && !"".equals(compo.getCategory().trim())) {
                        System.out.println(" --> ["
                                + compo.getIdentifier() + "]["
                                + compo.getCategory() + "]["
                                + compo.getMainClass() + "]["
                                + compo.getSubClass() + "]["
                                + compo.getCarbons() + "]["
                                + compo.getDoubleBonds() + "]["
                                + compo.getRetentionTime() + "]["
                                + compo.isSignificativeCompound() + "] -> ["
                                + compo.getAdduct() + "] :::> ION_SCORE: "
                                + compo.getIonizationScore() + " | AR_SCORE: "
                                + compo.getAdductRelationScore() + " | RT_SCORE:  "
                                // Deleted temporaly
                                //+ compo.getPrecedenceScore() + " | PRE_SCORE: " 
                                + compo.getRetentionTimeScore());
                    }
                }

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processCompounds");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processCompounds");
            }
        }
        return compounds;
    }

    public static List<TheoreticalCompounds> processIonizationRules(List<TheoreticalCompounds> compounds, ConfigFilter configFilter) {
        if (ACTIVE) {
            try {

                // TEST.
                //compounds = getPrecedenceRulesTest();
                //compounds = getRetentionTimeRulesTest();
                // ----------------------------------------------------
                System.out.println("######### Processing simple search #########");
                System.out.println("#########--> Num. Compounds: " + compounds.size());
                System.out.println("#########--> Ionization: " + configFilter.getIonMode());
                System.out.println("#########--> Modifier: " + configFilter.getModifier());
                System.out.println("#########--> All Compounds: " + configFilter.getAllCompounds());
                
                long time = System.currentTimeMillis();
                KieContainer kContainer = KieServices.Factory.get().getKieClasspathContainer();
                KieSession kSession = null;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");

                kSession = kContainer.newKieSession("LipidsTypeIonRulesKSession");
                kSession.insert(configFilter);
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processCompounds");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processCompounds");
            }
        }
        return compounds;
    }

    /**
     * Test set for precedence rules. Deleted temporaly
     *
     * @return
     */
    private static List<TheoreticalCompounds> getPrecedenceRulesTest() {
        // ----------------------------------------------------
        // PG < PE < PI < PA < PS << PC
        // 

        List<TheoreticalCompounds> compounds = new ArrayList();

        NewCompounds comp = null;
        NewCompoundsAdapter newComp = null;

        comp = new NewCompounds();
        comp.setCompoundId(41757);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompoundsAdapter(comp, 750.54108526, 1D, 750.54108526, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCompoundId(24561);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP02");
        comp.setSubClass("GP0201"); // PE
        newComp = new NewCompoundsAdapter(comp, 719.546504989, 4D, 719.546504989, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCompoundId(17985);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP06");
        comp.setSubClass("GP0601"); // PI
        newComp = new NewCompoundsAdapter(comp, 838.557129254, 2D, 838.557129254, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setCompoundId(62871);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP10");
        comp.setSubClass("GP1001"); // PA
        newComp = new NewCompoundsAdapter(comp, 676.504309, 3D, 676.504309, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCompoundId(33614);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP03");
        comp.setSubClass("GP0301"); // PS
        newComp = new NewCompoundsAdapter(comp, 763.536334233, 5D, 763.536334233, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCompoundId(39009);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP01");
        comp.setSubClass("GP0101"); // PC
        newComp = new NewCompoundsAdapter(comp, 761.593455181, 6D, 761.593455181, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        return compounds;
    }

    /**
     * Test set for retention time rules.
     *
     * @return
     */
    private static List<TheoreticalCompounds> getRetentionTimeRulesTest() {
        // ----------------------------------------------------
        // PG < PE < PI < PA < PS << PC
        // 

        List<TheoreticalCompounds> compounds = new ArrayList();

        NewCompounds comp = null;
        NewCompoundsAdapter newComp = null;

        // 34	0	1
        comp = new NewCompounds();
        comp.setCompoundId(41757);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401");
        newComp = new NewCompoundsAdapter(comp, 750.54108526, 1D, 750.54108526, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        // 34	1	3
        comp = new NewCompounds();
        comp.setCompoundId(41758);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(1);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401");
        newComp = new NewCompoundsAdapter(comp, 750.54108526, 3D, 750.54108526, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        // 32	0	4
        comp = new NewCompounds();
        comp.setCompoundId(41759);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(32);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompoundsAdapter(comp, 750.54108526, 4D, 750.54108526, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        // 32	1	2
        comp = new NewCompounds();
        comp.setCompoundId(41760);
        comp.setLipidClass(new NewLipidsClassification());
        comp.setCarbons(32);
        comp.setDoubleBonds(1);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompoundsAdapter(comp, 750.54108526, 2D, 750.54108526, "M+H", "");
        newComp.setSignificativeCompound(true);
        compounds.add(newComp);

        return compounds;
    }

}
