/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruleengine;

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.CompoundsLCMSGroupByAdduct;
import LCMS_FEATURE.Feature;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import persistence.NewCompounds;
import persistence.NewLMClassification;
import persistence.theoreticalCompound.NewCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;

/**
 * Processor for ionization, precedence and retention time rules.
 *
 * @author aesteban
 */
public class RuleProcessor {

    private static final boolean ACTIVE = true;

    /**
     * Method for creating a container for drools. There are different
     * containers for each module (web and REST)
     *
     * @param containerId
     * @return a KieContainer
     */
    public static KieContainer getContainer(String containerId) {
        if (ACTIVE) {
            try {

                KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();

                return kContainer;
            } catch (RuntimeException e) {
                System.out.println("EXCEPTION: " + e.toString());
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.getContainer");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.getContainer");
            }
        }
        return null;
    }

    /**
     * Method for rules execution over a theoretical compound list.
     *
     * @param compounds
     * @param configFilter
     */
    public static void processRulesTC(List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter) {
        if (ACTIVE) {
            try {

                long time = System.currentTimeMillis();
                KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
                KieSession kSession = null;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");

                processIonizationRulesTC(compounds, configFilter, kContainer);

                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                // -------------------------------------
                // ADDUCTS RELATIONS RULES
                // -------------------------------------
                System.out.println("#########--> ADDUCTS RELATIONS rules. START.");
                processAdductRelationRulesTC(compounds, configFilter, kContainer);

                System.out.println("#########--> ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));

                if (configFilter.getAllCompounds()) {
                    time = System.currentTimeMillis();
                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS rules. START.");
                    processLackAdductRelationRulesTC(compounds, configFilter, kContainer);

                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));
                }

                // -------------------------------------
                // PRECEDENCE RULES
                // -------------------------------------
                // Deleted temporaly
                /*
                 * System.out.println("#########--> Precedence rules. START.");
                 *
                 * compounds = processPrecedenceRules(compounds, configFilter,
                 * kContainer); *
                 * System.out.println("#########--> Precedence rules. END. Time:
                 * " + (System.currentTimeMillis() - time));
                 * time = System.currentTimeMillis();
                 */
                // -------------------------------------
                // RETENTION TIME RULES
                // -------------------------------------
                time = System.currentTimeMillis();
                System.out.println("#########--> Retention Time rules. START.");
                processRTRulesTC(compounds, configFilter, kContainer);

                System.out.println("#########--> Retention Time. END. Time: " + (System.currentTimeMillis() - time));

                // Release the kie container 
                kContainer.dispose();
                // -------------------------------------
                // TRACE
                // -------------------------------------
                for (TheoreticalCompounds compo : compounds) {
                    if (compo.getCategory() != null && !"".equals(compo.getCategory().trim())) {
                        /*
                         * System.out.println(" --> ["
                         * + compo.getCompoundId() + "]["
                         * + compo.getCategory() + "]["
                         * + compo.getMainClass() + "]["
                         * + compo.getSubClass() + "]["
                         * + compo.getCarbons() + "]["
                         * + compo.getDoubleBonds() + "]["
                         * + compo.getRetentionTime() + "]["
                         * + compo.isSignificativeCompound() + "] -> ["
                         * + compo.getAdduct() + "] :::> ION_SCORE: "
                         * + compo.getIonizationScore() + " | AR_SCORE: "
                         * + compo.getAdductRelationScore() + " | RT_SCORE: "
                         * // Deleted temporaly
                         * //+ compo.getPrecedenceScore() + " | PRE_SCORE: "
                         * + compo.getRetentionTimeScore());
                        System.out.println(" --> ["
                                + compo.getCompoundId() + "]["
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
                         */
                    }
                }
            } catch (RuntimeException e) {
                System.out.println("EXCEPTION: " + e.toString());
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRulesTC");
            }
        }
    }

    /**
     * Process the rules for compoundLCMS. It contains Features
     *
     * @param features
     * @param configFilter
     */
    public static void processRulesFeatures(List<Feature> features,
            ConfigFilter configFilter) {
        List<CompoundLCMS> compoundsLCMS = new LinkedList();
        features.forEach((feature) -> {
            for (CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct : feature.getAnnotationsGroupedByAdduct()) {
                for (CompoundLCMS annotation : compoundsLCMSGroupByAdduct.getCompounds()) {
                    compoundsLCMS.add(annotation);
                }
            }
        });
        processRulesCompoundsLCMS(compoundsLCMS, configFilter);
    }

    /**
     * Method for rules execution over a list of CompoundsLCMS
     *
     * @param compoundsLCMS
     * @param configFilter
     */
    public static void processRulesCompoundsLCMS(
            List<CompoundLCMS> compoundsLCMS, ConfigFilter configFilter) {
        if (ACTIVE) {
            try {

                // TEST.
                //compounds = getPrecedenceRulesTest();
                //compounds = getRetentionTimeRulesTest();
                // ----------------------------------------------------
                System.out.println("######### Processing advanced search #########");
                System.out.println("#########--> Num. Compounds: " + compoundsLCMS.size());
                System.out.println("#########--> Ionization: " + configFilter.getIonMode());
                System.out.println("#########--> Modifier: " + configFilter.getModifier());
                System.out.println("#########--> All Compounds: " + configFilter.getAllCompounds());

                long time = System.currentTimeMillis();
                KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
                KieSession kSession = null;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");

                processIonizationRulesCompoundsLCMS(compoundsLCMS, configFilter, kContainer);
                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();

                // -------------------------------------
                // ADDUCTS RELATIONS RULES
                // -------------------------------------
                System.out.println("#########--> ADDUCTS RELATIONS rules. START.");
                processAdductRelationRulesCompoundsLCMS(compoundsLCMS, configFilter, kContainer);

                System.out.println("#########--> ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));

                if (configFilter.getAllCompounds()) {
                    time = System.currentTimeMillis();
                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS rules. START.");
                    processLackAdductRelationRulesCompoundsLCMS(compoundsLCMS, configFilter, kContainer);

                    System.out.println("#########--> LACK OF ADDUCTS RELATIONS. END. Time: " + (System.currentTimeMillis() - time));
                }

                // -------------------------------------
                // PRECEDENCE RULES
                // -------------------------------------
                // Deleted temporaly
                /*
                 * System.out.println("#########--> Precedence rules. START.");
                 *
                 * processPrecedenceRulesCompoundsLCMS(compoundsLCMS,
                 * configFilter, kContainer); *
                 * System.out.println("#########--> Precedence rules. END. Time:
                 * " + (System.currentTimeMillis() - time));
                 * time = System.currentTimeMillis();
                 */
                // -------------------------------------
                // RETENTION TIME RULES
                // -------------------------------------
                time = System.currentTimeMillis();
                System.out.println("#########--> Retention Time rules. START.");
                processRTRulesCompoundsLCMS(compoundsLCMS, configFilter, kContainer);

                System.out.println("#########--> Retention Time. END. Time: " + (System.currentTimeMillis() - time));

                // -------------------------------------
                // TRACE
                // -------------------------------------
                /*
                for (CompoundLCMS compo : compoundsLCMS) {
                    if (compo.getCategory() != null && !"".equals(compo.getCategory().trim())) {

                        System.out.println(" --> ["
                                + compo.getCompound_id() + "]["
                                + compo.getEM() + "]["
                                + compo.getCategory() + "]["
                                + compo.getMainClass() + "]["
                                + compo.getSubClass() + "]["
                                + compo.getCarbons() + "]["
                                + compo.getDoubleBonds() + "]["
                                + compo.getRT() + "]["
                                + compo.isIsSignificative() + "] -> ["
                                + compo.getAdduct() + "] :::> ION_SCORE: "
                                + compo.getIonizationScore() + " | AR_SCORE: "
                                + compo.getAdductRelationScore() + " | RT_SCORE: "
                                // Deleted temporaly
                                //+ compo.getPrecedenceScore() + " | PRE_SCORE: "
                                + compo.getRTscore()
                                + compo.getFinalScore());
                    }
                }
*/
                // Release the kie container 
                kContainer.dispose();
            } catch (RuntimeException e) {
                System.out.println("EXCEPTION: " + e.toString());
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRulesCompoundsLCMS");
            }
        }
    }

    /**
     * Process the ionization rules for a simple search. It contains compounds
     * with only EM and adducts from an experiment with a modifier.
     *
     * @param compounds
     * @param configFilter
     */
    public static void processSimpleSearchTC(
            List<TheoreticalCompounds> compounds, ConfigFilter configFilter) {
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
                KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");
                processIonizationRulesTC(compounds, configFilter, kContainer);

                // Release the kie container 
                kContainer.dispose();
                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processSimpleSearchTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processSimpleSearchTC");
            }
        }
    }

    /**
     * Process the ionization rules for a simple search. It contains Features
     *
     * @param features
     * @param configFilter
     */
    public static void processSimpleSearchFeatures(List<Feature> features,
            ConfigFilter configFilter) {
        List<CompoundLCMS> compoundsLCMS = new LinkedList();
        features.forEach((feature) -> {
            for (CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct : feature.getAnnotationsGroupedByAdduct()) {
                for (CompoundLCMS annotation : compoundsLCMSGroupByAdduct.getCompounds()) {
                    compoundsLCMS.add(annotation);
                }
            }
        });
        processSimpleSearchCompoundsLCMS(compoundsLCMS, configFilter);
    }

    /**
     * Process the ionization rules for a simple search. It contains a list of
     * compoundLCMS
     *
     * @param compoundsLCMS
     * @param configFilter
     */
    public static void processSimpleSearchCompoundsLCMS(
            List<CompoundLCMS> compoundsLCMS, ConfigFilter configFilter) {
        if (ACTIVE) {
            try {

                // TEST.
                //compounds = getPrecedenceRulesTest();
                //compounds = getRetentionTimeRulesTest();
                // ----------------------------------------------------
                System.out.println("######### Processing simple search #########");
                System.out.println("#########--> Num. Compounds: " + compoundsLCMS.size());
                System.out.println("#########--> Ionization: " + configFilter.getIonMode());
                System.out.println("#########--> Modifier: " + configFilter.getModifier());
                System.out.println("#########--> All Compounds: " + configFilter.getAllCompounds());

                long time = System.currentTimeMillis();
                KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                System.out.println("#########--> Ionization rules. START.");
                processIonizationRulesCompoundsLCMS(compoundsLCMS, configFilter, kContainer);

                kContainer.dispose();
                System.out.println("#########--> Ionization rules. END. Time: " + (System.currentTimeMillis() - time));

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processSimpleSearchCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processSimpleSearchCompoundsLCMS");
            }
        }
    }

    private static void processIonizationRulesTC(
            List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsTypeIonRulesTCKSession");
                kSession.insert(configFilter);
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processIonizationRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processIonizationRulesTC");
            }
        }
    }

    private static void processIonizationRulesCompoundsLCMS(
            List<CompoundLCMS> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // IONIZATION RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsTypeIonRulesCompoundsKSession");
                kSession.insert(configFilter);
                for (CompoundLCMS compo : compounds) {

                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processIonizationRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processIonizationRulesCompoundsLCMS");
            }
        }
    }

    private static void processAdductRelationRulesTC(
            List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // ADDUCT RELATION RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("AdductRelationRulesTCKSession");
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processAdductRelationRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processAdductRelationRulesTC");
            }
        }
    }

    private static void processAdductRelationRulesCompoundsLCMS(
            List<CompoundLCMS> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // ADDUCT RELATION RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("AdductRelationRulesCompoundsKSession");
                for (CompoundLCMS compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processAdductRelationRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processAdductRelationRulesCompoundsLCMS");
            }
        }
    }

    private static void processLackAdductRelationRulesCompoundsLCMS(
            List<CompoundLCMS> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // LACK ADDUCT RELATION RULES
                // -------------------------------------
                if (configFilter.getAllCompounds()) {
                    kSession = kContainer.newKieSession("LackAdductRelationRulesCompoundsKSession");
                    kSession.insert(configFilter);
                    for (CompoundLCMS compo : compounds) {
                        kSession.insert(compo);
                    }
                    kSession.fireAllRules();
                    kSession.dispose();
                }

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processLackAdductRelationRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processLackAdductRelationRulesCompoundsLCMS");
            }
        }
    }

    private static void processLackAdductRelationRulesTC(
            List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // LACK ADDUCT RELATION RULES
                // -------------------------------------
                if (configFilter.getAllCompounds()) {
                    kSession = kContainer.newKieSession("LackAdductRelationRulesTCKSession");
                    kSession.insert(configFilter);
                    for (TheoreticalCompounds compo : compounds) {
                        kSession.insert(compo);
                    }
                    kSession.fireAllRules();
                    kSession.dispose();
                }

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processLackAdductRelationRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processLackAdductRelationRulesTC");
            }
        }
    }

    private static void processPrecedenceRulesCompoundsLCMS(
            List<CompoundLCMS> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // Precedence RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsPrecedenceRulesCompoundsKSession");
                for (CompoundLCMS compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processPrecedenceRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processPrecedenceRulesCompoundsLCMS");
            }
        }
    }

    private static void processPrecedenceRulesTC(
            List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // Precedence RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsPrecedenceRulesTCKSession");
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processPrecedenceRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processPrecedenceRulesTC");
            }
        }
    }

    private static void processRTRulesCompoundsLCMS(
            List<CompoundLCMS> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // RT RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsRTRulesCompoundsKSession");
                for (CompoundLCMS compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRTRulesCompoundsLCMS");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRTRulesCompoundsLCMS");
            }
        }
    }

    private static void processRTRulesTC(
            List<TheoreticalCompounds> compounds,
            ConfigFilter configFilter,
            KieContainer kContainer) {
        if (ACTIVE) {
            try {

                KieSession kSession;

                // -------------------------------------
                // RT RULES
                // -------------------------------------
                kSession = kContainer.newKieSession("LipidsRTRulesTCKSession");
                for (TheoreticalCompounds compo : compounds) {
                    kSession.insert(compo);
                }
                kSession.fireAllRules();
                kSession.dispose();

            } catch (RuntimeException e) {
                System.out.println("RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRTRulesTC");
            } catch (Exception e) {
                System.out.println("NON RUNTIME EXCEPTION. FAILED APPLYING RULES IN RuleProcessor.processRTRulesTC");
            }
        }
    }

    /**
     * Test set for precedence rules. Deleted temporaly
     *
     * @return
     */
    public static List<TheoreticalCompounds> getPrecedenceRulesTest() {
        // ----------------------------------------------------
        // PG < PE < PI < PA < PS << PC
        // 

        List<TheoreticalCompounds> compounds = new ArrayList();

        NewCompounds comp = null;
        NewCompound newComp = null;

        comp = new NewCompounds();
        comp.setCompoundId(41757);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompound(comp, 750.54108526, 1D, 750.54108526, "M+H", false, "", true, 1);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLMclassification(new NewLMClassification());
        comp.setCompoundId(24561);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP02");
        comp.setSubClass("GP0201"); // PE
        newComp = new NewCompound(comp, 719.546504989, 4D, 719.546504989, "M+H", false, "", true, 2);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLMclassification(new NewLMClassification());
        comp.setCompoundId(17985);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP06");
        comp.setSubClass("GP0601"); // PI
        newComp = new NewCompound(comp, 838.557129254, 2D, 838.557129254, "M+H", false, "", true, 3);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setCompoundId(62871);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP10");
        comp.setSubClass("GP1001"); // PA
        newComp = new NewCompound(comp, 676.504309, 3D, 676.504309, "M+H", false, "", true, 4);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLMclassification(new NewLMClassification());
        comp.setCompoundId(33614);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP03");
        comp.setSubClass("GP0301"); // PS
        newComp = new NewCompound(comp, 763.536334233, 5D, 763.536334233, "M+H", false, "", true, 5);
        compounds.add(newComp);

        comp = new NewCompounds();
        comp.setLMclassification(new NewLMClassification());
        comp.setCompoundId(39009);
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP01");
        comp.setSubClass("GP0101"); // PC
        newComp = new NewCompound(comp, 761.593455181, 6D, 761.593455181, "M+H", false, "", true, 6);
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
        NewCompound newComp = null;

        // 34	0	1
        comp = new NewCompounds();
        comp.setCompoundId(41757);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401");
        newComp = new NewCompound(comp, 750.54108526, 1D, 750.54108526, "M+H", false, "", true, 1);
        compounds.add(newComp);

        // 34	1	3
        comp = new NewCompounds();
        comp.setCompoundId(41758);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(34);
        comp.setDoubleBonds(1);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401");
        newComp = new NewCompound(comp, 750.54108526, 3D, 750.54108526, "M+H", false, "", true, 2);
        compounds.add(newComp);

        // 32	0	4
        comp = new NewCompounds();
        comp.setCompoundId(41759);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(32);
        comp.setDoubleBonds(0);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompound(comp, 750.54108526, 4D, 750.54108526, "M+H", false, "", true, 3);
        compounds.add(newComp);

        // 32	1	2
        comp = new NewCompounds();
        comp.setCompoundId(41760);
        comp.setLMclassification(new NewLMClassification());
        comp.setCarbons(32);
        comp.setDoubleBonds(1);
        comp.setCategory("GP");
        comp.setMainClass("GP04");
        comp.setSubClass("GP0401"); // PG
        newComp = new NewCompound(comp, 750.54108526, 2D, 750.54108526, "M+H", false, "", true, 4);
        compounds.add(newComp);

        return compounds;
    }

}
