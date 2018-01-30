package presentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import persistence.Compounds;
import persistence.FACompoundFactories.FACompoundFactory;
import persistence.oxidizedTheoreticalCompound.FACompound;
import persistence.NewCompounds;
import persistence.NewCompoundsGen;
import persistence.compoundsFactories.NewCompoundFactory;
import persistence.compoundsFactories.NewCompoundGenFactory;
import persistence.compoundsFactories.TheoreticalCompoundFactory;
import persistence.FACompoundFactories.OxidizedCompoundFactory;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.CompoundsGroupByMass;
import persistence.oxidizedTheoreticalCompound.OxidizedCompound;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import utilities.TheoreticalCompoundsComparer;
import utilities.AdductsLists;
import utilities.Constantes;
import static utilities.Constantes.ADDUCT_AUTOMATIC_DETECTION_WINDOW;
import utilities.ConstantesForOxidation;
import utilities.OxidationLists;
import static utilities.OxidationLists.MAPOXIDATIONS;
import utilities.TheoreticalOxidizedCompoundsComparer;
import static utilities.Constantes.FILE_FOR_ANALYTICS_PATH;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;
import persistence.FACompoundFactories.OxidizedTheoreticalCompoundFactory;
import static utilities.OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
import static utilities.OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
import utilities.Utilities;
import static utilities.Utilities.calculateFAEMFromPIandOtherFAEM;

/**
 *
 * @author Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente
 */
@Stateless
public class TheoreticalCompoundsFacade extends AbstractFacade<TheoreticalCompounds> {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "ceuMassMediator")
    /**
     * EntityManager to manage the Java Persistence Api
     */
    public EntityManager em;

    /**
     * HashMap to ?
     */
    // HashMap massIntervalOfCompounds = new HashMap();
    /**
     * Gets the EntityManager
     *
     * @return EntityManager of the class
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /*
    public void setEntityManager(EntityManager em){
        this.em = em;
    }
     */
    /**
     * constructor where it is called the father and the argument passed to the
     * father is the atribute class of TheoreticalCompounds
     */
    public TheoreticalCompoundsFacade() {
        super(TheoreticalCompounds.class);
    }

    /**
     * adds to the list of TheoreticalCompounds tcl all the query results
     * according the mass
     *
     * @param tcl. The list of TheoreticalCompounds
     * @param queryResult. The list of queryResults
     * @param measuredMass. Measured Mass in the Mass Spectrometer
     * @param retentionTime. Retention time in MS
     * @param tcf. The factory
     * @param massToSearch Mass To Search in the database
     * @param adduct adduct to search
     */
    private void addToTheoreticalCompoundList(List<TheoreticalCompounds> tcl,
            List<Compounds> queryResult,
            Double measuredMass,
            Double retentionTime,
            TheoreticalCompoundFactory tcf,
            Double massToSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            TheoreticalCompoundsGroup compoundGroup,
            Boolean isSignificative) {
        Iterator<Compounds> it = queryResult.iterator();
        while (it.hasNext()) {

            //NewCompounds nc = (NewCompounds) it.next();
            //System.out.println("COMPOUND: ID" + nc.getCompoundId() + " KEGG ID: " + nc.getNcKegg().getKeggId() + 
            //        " HMDB ID: " + nc.getNcHMDB().getHmdbId());
            TheoreticalCompounds compound = tcf.construct(it.next(),
                    measuredMass,
                    retentionTime,
                    massToSearch,
                    adduct,
                    isAdductAutoDetected,
                    adductAutoDetected,
                    isSignificative);
            // Add the chemical Alphabet
            tcl.add(compound);
            compoundGroup.addCompound(compound);
        }
    }

    /**
     * returns the TheoreticalFACompound which it is in the List queryResult If
     * there is more than one, it returns the first one and
     *
     * @param queryResult. The list of queryResults
     * @param FAcompoundFactory. The factory for constructing the FA
     * @param FAEM. Measured Mass in the Mass Spectrometer
     * @param massToSearch Mass To Search in the database
     * @param queryParentIonMass queryParentIonMass. It should be bigger than 50
     * Da
     * @param oxidationType Oxidation Type
     */
    private FACompound getFirstFACompoundFromQuery(
            List<Compounds> queryResult,
            FACompoundFactory FAcompoundFactory,
            Double FAEM,
            Double massToSearch,
            String oxidationType) {
        Iterator<Compounds> it = queryResult.iterator();
        if (it.hasNext()) {
            //NewCompounds nc = (NewCompounds) it.next();
            FACompound FAcompound = FAcompoundFactory.construct(it.next(),
                    FAEM,
                    massToSearch,
                    oxidationType);
            return FAcompound;
        } else {
            return null;
        }
    }

    /**
     * adds to the FAoxidized compound oxidizedcompound all the query results
     * according the mass of the precursor. This method works for long and short
     * chain due to the transformation on the FA also occurs in the oxidized
     * compound
     *
     * @param oxidizedCompound. The compound to add the annotations
     * @param queryParentIonMass queryParentIonMass to be searched
     * @param toleranceModeForPI. Tolerance mode for PI (ppm or mDa)
     * @param toleranceForPI. Value of Tolerance for the search of the precursor
     * @param oxidationType Oxidation Type
     */
    private void addOxidizedAnnotationsOverOxidizedPC(OxidizedTheoreticalCompound oxidizedCompound,
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

        TheoreticalCompoundFactory tcf;
        tcf = new NewCompoundFactory();
        Double massToSearch; // Mass to search based on adducts
        // Finish the query with the mass condition
        String startQuery;
        startQuery = "SELECT nc.compoundId FROM NewCompounds nc ";
        String finalQuery;

        startQuery = startQuery + " INNER JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
        startQuery = startQuery + " INNER JOIN NewLipidsClassificationChains nlcc on nlc.compoundId=nlcc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "nlc.compoundId is not null and ";
        startQuery = startQuery + "nlcc.compoundId is not null";
        startQuery = startQuery + ") and ";

// Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlc.subClass = " + ConstantesForOxidation.PC_OXIDIZED_LIPIDS_LM_CLASSIFICATION + " and ";
        startQuery = startQuery + "nlcc.carbons = " + numCarbonsOfOxidizedFA + " and ";
        startQuery = startQuery + "nlcc.doubleBonds = " + doubleBondsOfOxidizedFA + " and ";

        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
        } else {
            massToSearch = null;
            return;
        }

        Double delta;
        switch (toleranceModeForPI) {
            // Case mDa
            case "mDa":
                delta = toleranceForPI * 0.001d;
                break;
            // Case ppm
            case "ppm":
                delta = massToSearch * toleranceForPI * 0.000001f;
                break;
            default:
                delta = massToSearch * toleranceForPI * 0.000001f;
                break;
        }
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;
        Query queryForOxidizedFA;

        // set the mass 
        finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
        finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";
        //System.out.println("FINAL QUERY: " + finalQuery);
        queryForOxidizedFA = getEntityManager().createQuery(finalQuery, Integer.class);
        List resultsForOxidizedFA = queryForOxidizedFA.getResultList();
        CompoundsGroupByMass oxidizedAnnotationOverPC = null;

        boolean firstAnnotation = true;
        if (!resultsForOxidizedFA.isEmpty()) {
// There is a compound with same carbons and double bounds in one FA than the hypothesis. Then look if 
// the hypothesis accomplish also the other FA
            Iterator<Integer> iteratorForCompoundIDsofOxidizedFAs = resultsForOxidizedFA.iterator();
            while (iteratorForCompoundIDsofOxidizedFAs.hasNext()) {
                int compoundId = iteratorForCompoundIDsofOxidizedFAs.next();
                String queryForNonOxidizeFA;
                queryForNonOxidizeFA = "SELECT nc FROM NewCompounds nc ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN NewLipidsClassificationChains nlcc on nc.compoundId=nlcc.compoundId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " WHERE ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nc.compoundId=" + compoundId + " and (";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.compoundId is not null";
                queryForNonOxidizeFA = queryForNonOxidizeFA + ") and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.carbons = " + numCarbonsOfNonOxidizedFA + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.doubleBonds = " + doubleBondsOfNonOxidizedFA;
                Query query2;
                query2 = getEntityManager().createQuery(queryForNonOxidizeFA, NewCompounds.class);
                List resultsForNonOxidizedFA = query2.getResultList();
                Iterator<Compounds> iteratorForOxidizedPCs;
                iteratorForOxidizedPCs = resultsForNonOxidizedFA.iterator();
                if (iteratorForOxidizedPCs.hasNext()) {
                    if (firstAnnotation) {
                        oxidizedAnnotationOverPC = new CompoundsGroupByMass(experimentalMassPI, ConstantesForOxidation.HCOO_ADDUCT);
                        oxidizedCompound.setOxidizedCompoundsGroupByMass(oxidizedAnnotationOverPC);
                        firstAnnotation = false;
                    }
                    TheoreticalCompounds compound;
                    compound = tcf.construct(iteratorForOxidizedPCs.next(),
                            experimentalMassPI,
                            0d,
                            massToSearch,
                            ConstantesForOxidation.HCOO_ADDUCT,
                            false,
                            "",
                            true);
                    // Add the chemical Alphabet
                    oxidizedAnnotationOverPC.addCompound(compound);

                }

            }

        }

    }

    /**
     * adds to the list of TheoreticalCompounds tocl all the query results
     * according the mass. This method works only for long chain oxidation.
     * Short chain
     *
     * @param oxidizedCompound. The compound to add the annotations
     * @param queryParentIonMass queryParentIonMass to be searched
     * @param toleranceModeForPI. Tolerance mode for PI (ppm or mDa)
     * @param toleranceForPI. Value of Tolerance for the search of the precursor
     * @param oxidationType Oxidation Type
     */
    private void addNonOxidizedAnnotationsOverOxidizedPC(OxidizedTheoreticalCompound oxidizedCompound,
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
        TheoreticalCompoundFactory tcf;
        tcf = new NewCompoundFactory();
        Double massToSearch; // Mass to search based on adducts
        // Finish the query with the mass condition
        String startQuery;
        startQuery = "SELECT nc.compoundId FROM NewCompounds nc ";
        String finalQuery;
        startQuery = startQuery + " INNER JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
        startQuery = startQuery + " INNER JOIN NewLipidsClassificationChains nlcc on nlc.compoundId=nlcc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "nlc.compoundId is not null and ";
        startQuery = startQuery + "nlcc.compoundId is not null";
        startQuery = startQuery + ") and ";

// Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlc.mainClass = " + ConstantesForOxidation.PC_NON_OXIDIZED_LIPIDS_LM_CLASSIFICATION + " and ";
        startQuery = startQuery + "nlcc.carbons = " + numCarbonsOfOxidizedFA + " and ";
        startQuery = startQuery + "nlcc.doubleBonds = " + doubleBondsOfOxidizedFA + " and ";
        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
            // Also include the oxidation
            Double oxidationDouble;
            String oxidationValue;
            oxidationValue = MAPOXIDATIONS.get(oxidationType);
            oxidationDouble = Double.parseDouble(oxidationValue);
            massToSearch = massToSearch + oxidationDouble;
        } else {
            massToSearch = null;
            return;
        }

        Double delta;
        switch (toleranceModeForPI) {
            // Case mDa
            case "mDa":
                delta = toleranceForPI * 0.001d;
                break;
            // Case ppm
            case "ppm":
                delta = massToSearch * toleranceForPI * 0.000001f;
                break;
            default:
                delta = massToSearch * toleranceForPI * 0.000001f;
                break;
        }
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;
        Query queryForOxidizedFA;

        // set the mass 
        finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
        finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";
        //System.out.println("FINAL QUERY: " + finalQuery);
        queryForOxidizedFA = getEntityManager().createQuery(finalQuery, Integer.class);
        List resultsForOxidizedFA = queryForOxidizedFA.getResultList();
        CompoundsGroupByMass NonOxidizedAnnotationOverPC = null;

        boolean firstAnnotation = true;
        if (!resultsForOxidizedFA.isEmpty()) {
// There is a compound with same carbons and double bounds in one FA than the hypothesis. Then look if 
// the hypothesis accomplish also the other FA
            Iterator<Integer> iteratorForCompoundIDsofOxidizedFAs = resultsForOxidizedFA.iterator();
            while (iteratorForCompoundIDsofOxidizedFAs.hasNext()) {
                int compoundId = iteratorForCompoundIDsofOxidizedFAs.next();
                String queryForNonOxidizeFA;
                queryForNonOxidizeFA = "SELECT nc FROM NewCompounds nc ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN NewLipidsClassificationChains nlcc on nc.compoundId=nlcc.compoundId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " WHERE ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nc.compoundId=" + compoundId + " and (";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.compoundId is not null";
                queryForNonOxidizeFA = queryForNonOxidizeFA + ") and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.carbons = " + numCarbonsOfNonOxidizedFA + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nlcc.doubleBonds = " + doubleBondsOfNonOxidizedFA;
                Query query2;
                query2 = getEntityManager().createQuery(queryForNonOxidizeFA, NewCompounds.class);
                List resultsForNonOxidizedFA = query2.getResultList();
                Iterator<Compounds> iteratorForOxidizedPCs;
                iteratorForOxidizedPCs = resultsForNonOxidizedFA.iterator();
                if (iteratorForOxidizedPCs.hasNext()) {
                    if (firstAnnotation) {
                        NonOxidizedAnnotationOverPC = new CompoundsGroupByMass(experimentalMassPI, ConstantesForOxidation.HCOO_ADDUCT);
                        oxidizedCompound.setNonOxidizedCompoundsGroupByMass(NonOxidizedAnnotationOverPC);
                        firstAnnotation = false;
                    }
                    TheoreticalCompounds compound;
                    compound = tcf.construct(iteratorForOxidizedPCs.next(),
                            experimentalMassPI,
                            0d,
                            massToSearch,
                            ConstantesForOxidation.HCOO_ADDUCT,
                            false,
                            "",
                            true);
                    // Add the chemical Alphabet
                    NonOxidizedAnnotationOverPC.addCompound(compound);

                }

            }

        }
    }

    private static Double getChargedOriginalMass(double inputMass, double adductValue, int charge) {
        double result = inputMass;

        result = result + adductValue;
        result = result * charge;

        return result;
    }

    private static Double getDimmerOriginalMass(double inputMass, double adductValue, int numberAtoms) {
        double result = inputMass;

        result = result + adductValue;
        result = result / numberAtoms;

        return result;
    }

    private static Double getMassToSearch(Double inputMass, String adduct, Double adductValue) {
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedOriginalMass(inputMass, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedOriginalMass(inputMass, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerOriginalMass(inputMass, adductValue, 2);
        } else if (AdductsLists.DIMER_3.contains(adduct)) {
            massToSearch = getDimmerOriginalMass(inputMass, adductValue, 3);
        } else {
            massToSearch = inputMass + adductValue;
        }
        return massToSearch;
    }

    private static Double getChargedAdductMass(double inputMass, double adductValue, int charge) {
        double result = inputMass;

        result = result / charge;
        result = result - adductValue;

        return result;
    }

    private static Double getDimmerAdductMass(double inputMass, double adductValue, int numberAtoms) {
        double result = inputMass;
        result = result * numberAtoms;
        result = result - adductValue;

        return result;
    }

    private static Double getAdductMass(Double inputMass, String adduct, Double adductValue) {
        Double massToSearch;

        if (AdductsLists.CHARGE_2.contains(adduct)) {
            massToSearch = getChargedAdductMass(inputMass, adductValue, 2);
        } else if (AdductsLists.CHARGE_3.contains(adduct)) {
            massToSearch = getChargedAdductMass(inputMass, adductValue, 3);
        } else if (AdductsLists.DIMER_2.contains(adduct)) {
            massToSearch = getDimmerAdductMass(inputMass, adductValue, 2);
        } else if (AdductsLists.DIMER_3.contains(adduct)) {
            massToSearch = getDimmerAdductMass(inputMass, adductValue, 3);
        } else {
            massToSearch = inputMass - adductValue;
        }
        return massToSearch;
    }

    /**
     * returns a list of compounds from the databases according to the number of
     * the mass and the tolerance .
     *
     * @param masses , List of masses
     * @param toleranceMode tolerance in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @param metabolitesType metabolite types to search (all except peptides 0,
     * only lipids 1 or all including peptides 2)
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findCompoundsSimple(
            List<Double> masses,
            String toleranceMode,
            Double tolerance,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList;

        // If the search is only in MINE, it goes directly to findRangeGeneratedAdvanced
        if (databases.size() == 1 && databases.contains("MINE (Only In Silico Compounds)")) {
            theoreticalCompoundList = findRangeGeneratedSimple(
                    masses,
                    toleranceMode,
                    tolerance,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases);
        } else {
            theoreticalCompoundList = findRangeSimple(
                    masses,
                    toleranceMode,
                    tolerance,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases,
                    metabolitesType);
        }
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds from the databases according to the number of
     * the mass and the tolerance .
     *
     * @param masses , List of masses
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeSimple(
            List<Double> masses,
            String toleranceMode,
            Double tolerance,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;
        //System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Adducts " + adducts);
        //System.out.println("QUERY MASSES -> " + masses);
        //System.out.println("Databases " + databases);

        // Choose the adducts to perform the search
        provisionalMap = chooseprovisionalMapAdducts(massesMode, ionMode, adducts);
        adducts = chooseAdducts(ionMode, provisionalMap, adducts);

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin")))) {
            if (databases.contains("Kegg")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsKegg ck on nc.compoundId=ck.compoundId ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsHMDB ch on nc.compoundId=ch.compoundId ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsLM cl on nc.compoundId=cl.compoundId ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "LEFT JOIN NewCompoundsMetlin cm on nc.compoundId=cm.compoundId ";
            }
            startQuery = startQuery + " WHERE (";
            if (databases.contains("Kegg")) {
                startQuery = startQuery + "ck.keggId is not null or ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + "ch.hmdbId is not null or ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + "cl.lmId is not null or ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "cm.agilentId is not null or ";
            }

            // delete the last or
            startQuery = startQuery.substring(0, startQuery.length() - 4);
            startQuery = startQuery + ") and ";
        } else {
            // add where condition for all databases
            startQuery = startQuery + " WHERE ";
        }

// Add compound type for search
        if (metabolitesType.equals("All including peptides")) {
            // Then search in all compounds in the database
        } else if (metabolitesType.equals("Only lipids")) {
            startQuery = startQuery + "(nc.compoundType = " + "1" + ") and ";
        } else {
            // By default, searches includes all compounds but peptides (2)
            startQuery = startQuery + "(nc.compoundType != " + "2" + ") and ";
        }

        TheoreticalCompoundFactory tcf = new NewCompoundFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = inputMass;
            if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("positive")) {
                mzInputMass = inputMass + Constantes.PROTON_WEIGHT;
            } else if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("negative")) {
                mzInputMass = inputMass - Constantes.PROTON_WEIGHT;
            }
            //System.out.println("\n Compound: " + inputMass + " added simple charged");
            for (String s : adducts) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s);
                Double adductValue;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(s);
                adductValue = Double.parseDouble(sValueAdduct);
                //System.out.println("ADDUCT: " + s);
                massToSearch = getMassToSearch(mzInputMass, s, adductValue);
                // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                Double delta;
                switch (toleranceMode) {
                    // Case mDa
                    case "mDa":
                        delta = tolerance * 0.001d;
                        break;
                    // Case ppm
                    case "ppm":
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                    default:
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                }
                Double low = massToSearch - delta;
                Double high = massToSearch + delta;

                // Insert compounds
                Query q1;
                // Finish the query with the mass condition
                finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";

                // Test for JPA left join
                //finalQuery = "SELECT nc FROM NewCompounds nc WHERE (nc.ncKegg is not null or nc.ncHMDB is not null) and (nc.mass >= 236.08001738701788 and nc.mass <= 236.0941826129821)";
                //finalQuery = "SELECT c FROM NewCompounds c LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
                //System.out.println("Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                results = q1.getResultList();

                /*
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<NewCompounds> q = cb.createQuery(NewCompounds.class);
                    Root<NewCompounds> newCompounds = q.from(NewCompounds.class);
                    q.select(newCompounds).orderBy(cb.desc(newCompounds.get("compoundId")));
                    TypedQuery<NewCompounds> findAllNewCompounds = em.createQuery(q);
                    q1.unwrap(cls).(JpaQuery.class).getDatabaseQuery().getJPQLString();
                 */
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcf.construct(
                            null, inputMass, 0d, massToSearch, s, false, "", true);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            results,
                            inputMass,
                            0d,
                            tcf,
                            massToSearch,
                            s,
                            false,
                            "",
                            compoundGroup,
                            true);

                }
                if (searchInMINE) {
                    String startQueryForGen = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
                    String finalQueryForGen;

                    finalQueryForGen = startQueryForGen + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                    finalQueryForGen = finalQueryForGen + " order by ABS(ncg.mass - " + massToSearch + ")";
                    Query q1forGen;
//                Insert compounds
//                System.out.println("Final query: " + finalQuery);
                    q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                    results = q1forGen.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, 0d, massToSearch, s, false, "", true);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, 0d,
                                tcfForGen, massToSearch, s, false, "", compoundGroup, true);
                    }

                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                // TODO
                // Implement a method to order the compounds group
                // Collections.sort(compoundGroup);
            }
        }
        /*
        System.out.println("\n \n GROUP OF compounds:");
        for (TheoreticalCompoundsGroup group : listCompoundsGroup)
        {
            System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
            System.out.println("Number of elemens: ");
            for(TheoreticalCompounds tc : group.getTheoreticalCompounds())
            {
                System.out.println("Compound: " +tc.getIdentifier());
            }
        }
         */
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds generated, the generated compounds have been
     * extracted from Mine Database.
     *
     * @param masses List of masses
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeGeneratedSimple(
            List<Double> masses,
            String toleranceMode,
            Double tolerance,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;

        // System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        // System.out.println("Adducts " + adducts);
        //System.out.println("Databases " + databases);
        provisionalMap = chooseprovisionalMapAdducts(massesMode, ionMode, adducts);
        adducts = chooseAdducts(ionMode, provisionalMap, adducts);

        /*System.out.println("\nadducts\n" + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
        String finalQuery;

        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = inputMass;
            if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("positive")) {
                mzInputMass = inputMass + Constantes.PROTON_WEIGHT;
            } else if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("negative")) {
                mzInputMass = inputMass - Constantes.PROTON_WEIGHT;
            }
            //System.out.println("\n Compound: " + inputMass + " added simple charged");
            for (String s : adducts) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new ArrayList<TheoreticalCompounds>();
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s);
                Double adductValue;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(s);
                adductValue = Double.parseDouble(sValueAdduct);
                //System.out.println("ADDUCT: " + s);
                massToSearch = getMassToSearch(mzInputMass, s, adductValue);
                // System.out.println("\n \n adduct as parameter: " + adduct + " as string: " + s);

                Double delta;
                switch (toleranceMode) {
                    // Case mDa
                    case "mDa":
                        delta = tolerance * 0.001d;
                        break;
                    // Case ppm
                    case "ppm":
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                    default:
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                }
                Double low = massToSearch - delta;
                Double high = massToSearch + delta;

                // Insert compounds
                Query q1;
                // Finish the query with the mass condition
                finalQuery = startQuery + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                finalQuery = finalQuery + " order by ABS(ncg.mass - " + massToSearch + ")";
                //System.out.println("Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQuery, NewCompoundsGen.class);
                results = q1.getResultList();
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcfForGen.construct(
                            null, inputMass, 0d, massToSearch, s, false, "", true);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, 0d,
                            tcfForGen, massToSearch, s, false, "", compoundGroup, true);
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                // TODO
                // Implement a method to order the compounds group
                // Collections.sort(compoundGroup);
            }

        }
        /*
        System.out.println("\n \n GROUP OF compounds:");
        for (TheoreticalCompoundsGroup group : listCompoundsGroup)
        {
            System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
            System.out.println("Number of elemens: ");
            for(TheoreticalCompounds tc : group.getTheoreticalCompounds())
            {
                System.out.println("Compound: " +tc.getIdentifier());
            }
        }
         */
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds from the databases according to the number of
     * the mass and the tolerance .
     *
     * @param masses , List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectra List of Composite Spectra of every compound
     * @param isSignificativeCompound List of Significative Compounds
     * @param chemAlphabet
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @param metabolitesType metabolite types to search (all except peptides 0,
     * only lipids 1 or all including peptides 2)
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findCompoundsAdvanced(
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList;

        // If the search is only in MINE, it goes directly to findRangeGeneratedAdvanced
        if (databases.size() == 1 && databases.contains("MINE (Only In Silico Compounds)")) {
            theoreticalCompoundList = findRangeGeneratedAdvanced(
                    masses,
                    retentionTimes,
                    compositeSpectra,
                    isSignificativeCompound,
                    toleranceMode,
                    tolerance,
                    chemAlphabet,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases);
        } else {
            theoreticalCompoundList = findRangeAdvanced(
                    masses,
                    retentionTimes,
                    compositeSpectra,
                    isSignificativeCompound,
                    toleranceMode,
                    tolerance,
                    chemAlphabet,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases,
                    metabolitesType);
        }
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds from the databases according to the number of
     * the mass and the tolerance .
     *
     * @param masses , List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectra List of Composite Spectra of every compound
     * @param isSignificativeCompound List of Significative Compounds
     * @param chemAlphabet
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeAdvanced(
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;
        //System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Adducts " + adducts);
        //System.out.println("QUERY MASSES -> " + masses);
        //System.out.println("ISSIGNIFICATIVE COMPOUND -> " + isSignificativeCompound);
        //System.out.println("Databases " + databases);
        //System.out.println("Chemical Alphabet " + chemAlphabet);
        /* TO ENABLE ANALYTICS
        boolean enableAnalytics = true;
        File fileForAnalytics = null;
        int totalNumOfCompoundsByEM;
        if(enableAnalytics)
        {
            String fileName = "test.txt";
            fileForAnalytics = new File(Constantes.FILE_FOR_ANALYTICS_PATH + fileName);
        }
         */
        // Choose the adducts to perform the search
        provisionalMap = chooseprovisionalMapAdducts(massesMode, ionMode, adducts);
        adducts = chooseAdducts(ionMode, provisionalMap, adducts);

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
        /*
// For taking statistics for results
// We want to retrieve number of hits from every database without unifying
        String queryStartKegg;
        String queryStartLM;
        String queryStartHMDB;
        String queryStartMetlin;
        File queriesKegg = new File(FILE_FOR_ANALYTICS_PATH + "unification_jpba/non_lipidic_set_queries_kegg.txt");
        File queriesLM = new File(FILE_FOR_ANALYTICS_PATH + "unification_jpba/non_lipidic_set_queries_lm.txt");
        File queriesHMDB = new File(FILE_FOR_ANALYTICS_PATH + "unification_jpba/non_lipidic_set_queries_hmdb.txt");
        File queriesMetlin = new File(FILE_FOR_ANALYTICS_PATH + "unification_jpba/non_lipidic_set_queries_metlin.txt");
        if (queriesKegg.exists()) {
            queriesKegg.delete();
        } else {
            try {
                queriesKegg.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (queriesLM.exists()) {
            queriesLM.delete();
        } else {
            try {
                queriesLM.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (queriesHMDB.exists()) {
            queriesHMDB.delete();
        } else {
            try {
                queriesHMDB.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (queriesMetlin.exists()) {
            queriesMetlin.delete();
        } else {
            try {
                queriesMetlin.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        FileWriter fileWritterKEGG;
        BufferedWriter bwKEGG = null;
        FileWriter fileWritterLM;
        BufferedWriter bwLM = null;
        FileWriter fileWritterHMDB;
        BufferedWriter bwHMDB = null;
        FileWriter fileWritterMETLIN;
        BufferedWriter bwMETLIN = null;

        try {
            fileWritterKEGG = new FileWriter(queriesKegg, true);
            bwKEGG = new BufferedWriter(fileWritterKEGG);
            fileWritterLM = new FileWriter(queriesLM, true);
            bwLM = new BufferedWriter(fileWritterLM);
            fileWritterHMDB = new FileWriter(queriesHMDB, true);
            bwHMDB = new BufferedWriter(fileWritterHMDB);
            fileWritterMETLIN = new FileWriter(queriesMetlin, true);
            bwMETLIN = new BufferedWriter(fileWritterMETLIN);

        } catch (IOException ex) {
            Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        queryStartKegg = "select count(*) from compounds c inner join compounds_kegg caux on "
                + "c.compound_id=caux.compound_id where "
                + "formula_type = 'CHNOPS' and compound_type !=2 and ";
        queryStartLM = "select count(*) from compounds c inner join compounds_lm  caux on "
                + "c.compound_id=caux.compound_id where "
                + "formula_type = 'CHNOPS' and compound_type !=2 and ";
        queryStartHMDB = "select count(*) from compounds c inner join compounds_hmdb caux on "
                + "c.compound_id=caux.compound_id where "
                + "formula_type = 'CHNOPS' and compound_type !=2 and ";
        queryStartMetlin = "select count(*) from compounds c inner join compounds_agilent caux on "
                + "c.compound_id=caux.compound_id where "
                + "formula_type = 'CHNOPS' and compound_type !=2 and ";
        String queryFinalKEGG;
        String queryFinalLM;
        String queryFinalHMDB;
        String queryFinalMetlin;
         */

// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin")))) {
            if (databases.contains("Kegg")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsKegg ck on nc.compoundId=ck.compoundId ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsHMDB ch on nc.compoundId=ch.compoundId ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsLM cl on nc.compoundId=cl.compoundId ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "LEFT JOIN NewCompoundsMetlin cm on nc.compoundId=cm.compoundId ";
            }
            startQuery = startQuery + " WHERE (";
            if (databases.contains("Kegg")) {
                startQuery = startQuery + "ck.keggId is not null or ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + "ch.hmdbId is not null or ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + "cl.lmId is not null or ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "cm.agilentId is not null or ";
            }

            // delete the last or
            startQuery = startQuery.substring(0, startQuery.length() - 4);
            startQuery = startQuery + ") and ";
        } else {
            // add where condition for all databases
            startQuery = startQuery + " WHERE ";
        }
// Add databases depending on the user selection
        if (chemAlphabet.equals("CHNOPS")) {
            startQuery = startQuery + "nc.formulaType = '" + chemAlphabet + "' and ";
        } else if (chemAlphabet.equals("CHNOPSCL")) {
            startQuery = startQuery + "(nc.formulaType = '" + chemAlphabet + "' or nc.formulaType = 'CHNOPS') and ";
        }

// Add compound type for search
        if (metabolitesType.equals("All including peptides")) {
            // Then search in all compounds in the database
        } else if (metabolitesType.equals("Only lipids")) {
            startQuery = startQuery + "(nc.compoundType = " + "1" + ") and ";
        } else {
            // By default, searches includes all compounds but peptides (2)
            startQuery = startQuery + "(nc.compoundType != " + "2" + ") and ";
        }

        TheoreticalCompoundFactory tcf = new NewCompoundFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = inputMass;
            if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("positive")) {
                mzInputMass = inputMass + Constantes.PROTON_WEIGHT;
            } else if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("negative")) {
                mzInputMass = inputMass - Constantes.PROTON_WEIGHT;
            }
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Integer> inputCompositeSpectrum = compositeSpectra.get(i);
            Double adductValue;
            String sValueAdduct;
            /*
// Taking statistics for results
            queryFinalKEGG = "(";
            queryFinalLM = "(";
            queryFinalHMDB = "(";
            queryFinalMetlin = "(";
             */

            String detectedAdduct = detectAdductBasedOnCompositeSpectrum(ionMode, mzInputMass, adducts, inputCompositeSpectrum);
            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// Check if adduct is detected based on the composite spectrum

                //System.out.println("Automatic adduct detected: " + inputMass);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, detectedAdduct, isSignificative);
// System.out.println("\n Compound: " + inputMass + " Adduct automatically detected");
                // As we detect automatically the adduct, go directly for the inputMass

                sValueAdduct = provisionalMap.get(detectedAdduct);
                adductValue = Double.parseDouble(sValueAdduct);

//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                massToSearch = getMassToSearch(mzInputMass, detectedAdduct, adductValue);
                Double delta;
                switch (toleranceMode) {
                    // Case mDa
                    case "mDa":
                        delta = tolerance * 0.001d;
                        break;
                    // Case ppm
                    case "ppm":
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                    default:
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                }
                Double low = massToSearch - delta;
                Double high = massToSearch + delta;

// Finish the query with the mass condition
                finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";
                /*
// Take statistics about compounds without unifying                
                queryFinalKEGG = queryFinalKEGG + "(mass >= " + low + " and mass <= " + high + ") or ";
                queryFinalLM = queryFinalLM + "(mass >= " + low + " and mass <= " + high + ") or ";
                queryFinalHMDB = queryFinalHMDB + "(mass >= " + low + " and mass <= " + high + ") or ";
                queryFinalMetlin = queryFinalMetlin + "(mass >= " + low + " and mass <= " + high + ") or ";
                 */
                Query q1;
// Insert compounds
                //System.out.println("Double Charge detected: Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                results = q1.getResultList();
                //System.out.println("RESULTS: " + results);
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcf.construct(
                            null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, false, "", isSignificative);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, inputRetentionTime,
                            tcf, massToSearch, detectedAdduct, false, "", compoundGroup, isSignificative);
                }
                if (searchInMINE) {

// It could be done in a method, but the number of parameters is too long, so we will keep code here
                    String startQueryForGen = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
                    String finalQueryForGen;
                    if (chemAlphabet.equals("CHNOPS")) {
                        startQueryForGen = startQueryForGen + "ncg.formulaType = '" + chemAlphabet + "' and ";
                    } else if (chemAlphabet.equals("CHNOPSCL")) {
                        startQueryForGen = startQueryForGen + "(ncg.formulaType = '" + chemAlphabet + "' or ncg.formulaType = 'CHNOPS') and ";
                    }

                    finalQueryForGen = startQueryForGen + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                    finalQueryForGen = finalQueryForGen + " order by ABS(ncg.mass - " + massToSearch + ")";
                    Query q1forGen;
// Insert compounds
//                System.out.println("Final query: " + finalQuery);
                    q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                    results = q1forGen.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, false, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {

                        addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime,
                                tcfForGen, massToSearch, detectedAdduct, false, "", compoundGroup, isSignificative);
                    }

                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                // TODO
                // Implement a method to order the compounds group
                // Collections.sort(compoundGroup);
                for (String s : adducts) {
                    // Include all adducts if we detect a double charge for 
                    // frontend presentation
                    compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, s, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    sValueAdduct = provisionalMap.get(s);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    //System.out.println("ADDUCT: " + s);
                    if (s.equals(detectedAdduct)) {
// IT WAS ALREADY INCLUDED IN THE FIRST POSITION
                    } else {
                        TheoreticalCompounds compound = tcf.construct(
                                null, inputMass, inputRetentionTime, null, s, true, detectedAdduct, isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);

                        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                        theoreticalCompoundList.addAll(listToBeOrdered);
                        listCompoundsGroup.add(compoundGroup);
                    }

                    // TODO
                    // Implement a method to order the compounds group
                    // Collections.sort(compoundGroup);
                }

            } else {
                //System.out.println("\n Compound: " + inputMass + " added simple charged");

                for (String s : adducts) {
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, s, isSignificative);
                    sValueAdduct = provisionalMap.get(s);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    massToSearch = getMassToSearch(mzInputMass, s, adductValue);
                    //System.out.println("ADDUCT: " + s);

                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                    Double delta;
                    switch (toleranceMode) {
                        // Case mDa
                        case "mDa":
                            delta = tolerance * 0.001d;
                            break;
                        // Case ppm
                        case "ppm":
                            delta = massToSearch * tolerance * 0.000001f;
                            break;
                        default:
                            delta = massToSearch * tolerance * 0.000001f;
                            break;
                    }
                    Double low = massToSearch - delta;
                    Double high = massToSearch + delta;

// Finish the query with the mass condition
                    finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                    finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";
                    /*
// Take statistics about compounds without unifying                
                    queryFinalKEGG = queryFinalKEGG + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalLM = queryFinalLM + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalHMDB = queryFinalHMDB + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalMetlin = queryFinalMetlin + "(mass >= " + low + " and mass <= " + high + ") or ";
                     */
                    Query q1;

                    // Test for JPA left join
                    //finalQuery = "SELECT nc FROM NewCompounds nc WHERE (nc.ncKegg is not null or nc.ncHMDB is not null) and (nc.mass >= 236.08001738701788 and nc.mass <= 236.0941826129821)";
                    //finalQuery = "SELECT c FROM NewCompounds c LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
                    //System.out.println("Final query: " + finalQuery);
                    q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                    results = q1.getResultList();

                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcf.construct(
                                null, inputMass, inputRetentionTime, massToSearch, s, false, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime,
                                tcf, massToSearch, s, false, "", compoundGroup, isSignificative);

                    }
                    if (searchInMINE) {
                        String startQueryForGen = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
                        String finalQueryForGen;
                        if (chemAlphabet.equals("CHNOPS")) {
                            startQueryForGen = startQueryForGen + "ncg.formulaType = '" + chemAlphabet + "' and ";
                        } else if (chemAlphabet.equals("CHNOPSCL")) {
                            startQueryForGen = startQueryForGen + "(ncg.formulaType = '" + chemAlphabet + "' or ncg.formulaType = 'CHNOPS') and ";
                        }

                        finalQueryForGen = startQueryForGen + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                        finalQueryForGen = finalQueryForGen + " order by ABS(ncg.mass - " + massToSearch + ")";
                        Query q1forGen;
//                Insert compounds
//                System.out.println("Final query: " + finalQuery);
                        q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                        results = q1forGen.getResultList();
                        if (results.isEmpty()) {
                            TheoreticalCompounds compound = tcfForGen.construct(
                                    null, inputMass, inputRetentionTime, massToSearch, s, false, "", isSignificative);
                            listToBeOrdered.add(compound);
                            compoundGroup.addCompound(compound);
                        } else {

                            addToTheoreticalCompoundList(listToBeOrdered,
                                    results, inputMass, inputRetentionTime,
                                    tcfForGen, massToSearch, s, false, "", compoundGroup, isSignificative);
                        }

                    }
                    Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                    theoreticalCompoundList.addAll(listToBeOrdered);
                    listCompoundsGroup.add(compoundGroup);

                    // TODO
                    // Implement a method to order the compounds group
                    // Collections.sort(compoundGroup);
                }

            }
            /*
// Write queries for statistics
            try {
                bwKEGG.write(queryStartKegg + queryFinalKEGG.substring(0, queryFinalKEGG.length() - 4) + ");");
                bwKEGG.write("\n");
                bwLM.write(queryStartLM + queryFinalLM.substring(0, queryFinalLM.length() - 4) + ");");
                bwLM.write("\n");
                bwHMDB.write(queryStartHMDB + queryFinalHMDB.substring(0, queryFinalHMDB.length() - 4) + ");");
                bwHMDB.write("\n");
                bwMETLIN.write(queryStartMetlin + queryFinalMetlin.substring(0, queryFinalMetlin.length() - 4) + ");");
                bwMETLIN.write("\n");
            } catch (IOException ex) {
                Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
             */
        }
        /*
        try {
            // Close bufferedWritters for statistics and files
            bwKEGG.close();
            bwLM.close();
            bwHMDB.close();
            bwMETLIN.close();
        } catch (IOException ex) {
            Logger.getLogger(TheoreticalCompoundsFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
 /*
        System.out.println("\n \n GROUP OF compounds:");
        for (TheoreticalCompoundsGroup group : listCompoundsGroup)
        {
            System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
            System.out.println("Number of elemens: ");
            for(TheoreticalCompounds tc : group.getTheoreticalCompounds())
            {
                System.out.println("Compound: " +tc.getIdentifier());
            }
        }
         */
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds generated, the generated compounds have been
     * extracted from Mine Database.
     *
     * @param masses List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectra List of Composite Spectra of every compound
     * @param isSignificativeCompound List of significative Compounds
     * @param chemAlphabet
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeGeneratedAdvanced(
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;

        // System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        // System.out.println("Adducts " + adducts);
        //System.out.println("Databases " + databases);
        //System.out.println("Chemical Alphabet " + chemAlphabet);
        provisionalMap = chooseprovisionalMapAdducts(massesMode, ionMode, adducts);
        adducts = chooseAdducts(ionMode, provisionalMap, adducts);

        /*System.out.println("\nadducts\n" + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
        String finalQuery;
        if (chemAlphabet.equals("CHNOPS")) {
            startQuery = startQuery + "ncg.formulaType = '" + chemAlphabet + "' and ";
        } else if (chemAlphabet.equals("CHNOPSCL")) {
            startQuery = startQuery + "(ncg.formulaType = '" + chemAlphabet + "' or ncg.formulaType = 'CHNOPS') and ";
        }

        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = inputMass;
            if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("positive")) {
                mzInputMass = inputMass + Constantes.PROTON_WEIGHT;
            } else if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED) && ionMode.equals("negative")) {
                mzInputMass = inputMass - Constantes.PROTON_WEIGHT;
            }
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Integer> inputCompositeSpectrum = compositeSpectra.get(i);
            String detectedAdduct = detectAdductBasedOnCompositeSpectrum(ionMode, mzInputMass, adducts, inputCompositeSpectrum);

            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// Check if the adduct is double charged.
// At this moment only one adduct possible.

                // System.out.println("Automaticall adduct detected: " + masses.get(i) + " Compos: " + inputCompositeSpectrum);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, detectedAdduct, isSignificative);

                Double adductValue;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(detectedAdduct);
                adductValue = Double.parseDouble(sValueAdduct);
                //System.out.println("ADDUCT: " + s);
                massToSearch = getMassToSearch(mzInputMass, detectedAdduct, adductValue);
//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                Double delta;
                switch (toleranceMode) {
                    // Case mDa
                    case "mDa":
                        delta = tolerance * 0.001d;
                        break;
                    // Case ppm
                    case "ppm":
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                    default:
                        delta = massToSearch * tolerance * 0.000001f;
                        break;
                }
                Double low = massToSearch - delta;
                Double high = massToSearch + delta;
// Finish the query with the mass condition
                finalQuery = startQuery + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                finalQuery = finalQuery + " order by ABS(ncg.mass - " + massToSearch + ")";
                Query q1;
// Insert compounds
//                System.out.println("Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQuery, NewCompoundsGen.class);
                results = q1.getResultList();
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcfForGen.construct(
                            null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, false, "", isSignificative);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {

                    addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, inputRetentionTime,
                            tcfForGen, massToSearch, detectedAdduct, false, "", compoundGroup, isSignificative);
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                for (String s : adducts) {
                    // Include all adducts if we detect a double charge for 
                    // frontend presentation
                    compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, s, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    //System.out.println("ADDUCT: " + s);
                    if (s.equals(detectedAdduct)) {

                    } else {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, null, s, true, detectedAdduct, isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);

                        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                        theoreticalCompoundList.addAll(listToBeOrdered);
                        listCompoundsGroup.add(compoundGroup);
                    }

                    // TODO
                    // Implement a method to order the compounds group
                    // Collections.sort(compoundGroup);
                }
            } else {
                //System.out.println("\n Compound: " + inputMass + " added simple charged"
                //        + inputCompositeSpectrum);
                for (String s : adducts) {
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new ArrayList<TheoreticalCompounds>();
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, s, isSignificative);
                    Double adductValue = 0.0d;
                    String sValueAdduct;
                    sValueAdduct = provisionalMap.get(s);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    massToSearch = getMassToSearch(mzInputMass, s, adductValue);

                    Double delta;
                    switch (toleranceMode) {
                        // Case mDa
                        case "mDa":
                            delta = tolerance * 0.001d;
                            break;
                        // Case ppm
                        case "ppm":
                            delta = massToSearch * tolerance * 0.000001f;
                            break;
                        default:
                            delta = massToSearch * tolerance * 0.000001f;
                            break;
                    }
                    Double low = massToSearch - delta;
                    Double high = massToSearch + delta;

                    // Insert compounds
                    Query q1;
                    // Finish the query with the mass condition
                    finalQuery = startQuery + "(ncg.mass >= " + low + " and ncg.mass <= " + high + ")";
                    finalQuery = finalQuery + " order by ABS(ncg.mass - " + massToSearch + ")";
                    //System.out.println("Final query: " + finalQuery);
                    q1 = getEntityManager().createQuery(finalQuery, NewCompoundsGen.class);
                    results = q1.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, massToSearch, s, false, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime,
                                tcfForGen, massToSearch, s, false, "", compoundGroup, isSignificative);
                    }
                    Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                    theoreticalCompoundList.addAll(listToBeOrdered);
                    listCompoundsGroup.add(compoundGroup);

                    // TODO
                    // Implement a method to order the compounds group
                    // Collections.sort(compoundGroup);
                }
            }

        }
        /*
        System.out.println("\n \n GROUP OF compounds:");
        for (TheoreticalCompoundsGroup group : listCompoundsGroup)
        {
            System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
            System.out.println("Number of elemens: ");
            for(TheoreticalCompounds tc : group.getTheoreticalCompounds())
            {
                System.out.println("Compound: " +tc.getIdentifier());
            }
        }
         */
        return theoreticalCompoundList;
    }

    /**
     * process the provisional map to perform the search. Return provisional Map
     * of adducts
     *
     * @param massesMode
     * @param ionMode Ionization mode
     * @param provisionalMap Map to get the value of adducts
     * @param adducts Possible adducts
     */
    private Map<String, String> chooseprovisionalMapAdducts(String massesMode, String ionMode, List<String> adducts) {
        Map<String, String> provisionalMap = null;
        switch (massesMode) {
            case "mz":
                if (ionMode.equals("positive")) {
                    provisionalMap = AdductsLists.MAPMZPOSITIVEADDUCTS;
                } else if (ionMode.equals("negative")) {
                    provisionalMap = AdductsLists.MAPMZNEGATIVEADDUCTS;
                } else {
                    provisionalMap = AdductsLists.MAPNEUTRALADDUCTS;
                }
                //System.out.println("MZ -> PROVISIONALMAP: " + provisionalMap);
                return provisionalMap;
            /*
            case Constantes.NAME_FOR_RECALCULATED:
                if (ionMode.equals("positive")) {
                    provisionalMap = AdductsLists.MAPRECALCULATEDPOSITIVEADDUCTS;
                } else if (ionMode.equals("negative")) {
                    provisionalMap = AdductsLists.MAPRECALCULATEDNEGATIVEADDUCTS;
                } else {
                    provisionalMap = AdductsLists.MAPNEUTRALADDUCTS;
                }
                //System.out.println("NEUTRAL -> PROVISIONALMAP: " + provisionalMap);
                return provisionalMap;
             */
            default:
                if (ionMode.equals("positive")) {
                    provisionalMap = AdductsLists.MAPMZPOSITIVEADDUCTS;
                } else if (ionMode.equals("negative")) {
                    provisionalMap = AdductsLists.MAPMZNEGATIVEADDUCTS;
                } else {
                    provisionalMap = AdductsLists.MAPNEUTRALADDUCTS;
                }
                //System.out.println("DEFAULT -> PROVISIONALMAP: " + provisionalMap);
                return provisionalMap;
        }
    }

    /**
     * Detect the ionization adduct based on the relationships in the Composite
     * Spectrum. The inputMass is handled as m/z
     *
     * @param ionMode positive or negative
     * @param inputMass m/z
     * @param adducts possible adducts to be formed
     * @param compositeSpectrum Signals of CS
     * @return
     */
    private String detectAdductBasedOnCompositeSpectrum(
            String ionMode,
            Double inputMass,
            List<String> adducts,
            Map<Double, Integer> compositeSpectrum) {
//        System.out.println("DETECTING ADDUCT BASED ON COMPOSITE SPECTRUM. Masses Mode: " + massesMode + " ionMode: " + ionMode);
//        System.out.println("FOR INPUT MASS: " + inputMass);
// TODO MAYBE MANAGE ISOTOPES?
        String adductDetected = "";
        Map<String, String> mapAdducts;
        double adductDouble;
        double adductDoubleForCheckRelation;
        double massToSearchInCompositeSpectrumForCheckRelation;
        double differenceMassAndPeak;
        List<String> allAdductsForCheckRelation;
        boolean search = false;
        switch (ionMode) {
            case "positive": {
                mapAdducts = AdductsLists.MAPMZPOSITIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<String>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZPOSITIVEADDUCTS.keySet());
                if (ionMode.equals("positive")) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + ionMode);
                    return adductDetected;
                }
                break;
            }
            case "negative": {
                mapAdducts = AdductsLists.MAPMZNEGATIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<String>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPMZNEGATIVEADDUCTS.keySet());
                if (ionMode.equals("negative")) {
                    search = true;
                } else {
                    System.out.println("ION MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + ionMode);
                }
                break;
            }
            default: {
                mapAdducts = null;
                allAdductsForCheckRelation = null;
            }
        }
        if (search) {

            for (String adductName : adducts) {
                // Hypothesis -> Adduct is adductName
                String adductValue;

                adductValue = mapAdducts.get(adductName);
                adductDouble = Double.parseDouble(adductValue);
                Double neutralMassBasedOnAdduct;
                neutralMassBasedOnAdduct = getMassToSearch(inputMass, adductName, adductDouble);

                //System.out.println("HYPOTHESIS -> ADDUCT = " + adductName + " value: " + neutralMassBasedOnAdduct);
                // Hypothesis -> Peak is adductName
                // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                // So now is time to loop the composite spectrum searching the peak
                for (String adductNameForCheckRelation : allAdductsForCheckRelation) {
                    String adductValueForCheckRelation = mapAdducts.get(adductNameForCheckRelation);
                    adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                    if (!adductName.equals(adductNameForCheckRelation)) {

                        massToSearchInCompositeSpectrumForCheckRelation
                                = getAdductMass(neutralMassBasedOnAdduct, adductNameForCheckRelation, adductDoubleForCheckRelation);
                        //System.out.println("  ADDUCT TO SEARCH IN CS: " + adductNameForCheckRelation
                        //        + " VALUE:" + massToSearchInCompositeSpectrumForCheckRelation);
                        // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                        // So now is time to loop the composite spectrum searching the peak
                        for (Double peakInCompositeSpectrum : compositeSpectrum.keySet()) {
//                                    System.out.println("AdductName: " + adductNameForCheckRelation 
//                                            + " value: " + adductValueForCheckRelation + 
//                                            " PEAK COMPOSITE: " + peakInCompositeSpectrum + 
//                                            " SEARCHING PEAK: " + massToSearchInCompositeSpectrumForCheckRelation);
                            differenceMassAndPeak = Math.abs(peakInCompositeSpectrum - massToSearchInCompositeSpectrumForCheckRelation);
//                                    System.out.println("PEAK IN COMPOSITE SPECTRUM: " + peakInCompositeSpectrum + 
//                                            " DIFFERENCE: " + differenceMassAndPeak);
                            if (differenceMassAndPeak < ADDUCT_AUTOMATIC_DETECTION_WINDOW) {
                                adductDetected = adductName;
                                //System.out.println("ADDUCT DETECTED: " + adductDetected + " RELATED TO: " + adductNameForCheckRelation);
                                return adductDetected;
                            }
                        }
                    }
                }

            }
        }
        return adductDetected;

    }

    /**
     * Test for method detectAdductBasedOnCompositeSpectrum
     *
     * @param massesMode
     * @param ionMode
     * @param inputMass
     * @param adducts
     * @param compositeSpectrum
     * @return
     */
    public String testDetectAdductBasedOnCompositeSpectrum(String massesMode,
            String ionMode,
            double inputMass,
            List<String> adducts,
            Map<Double, Integer> compositeSpectrum) {
        Map<String, String> provisionalMap = chooseprovisionalMapAdducts(massesMode, ionMode, adducts);
        adducts = chooseAdducts(ionMode, provisionalMap, adducts);

        String adductDetected = detectAdductBasedOnCompositeSpectrum(ionMode, inputMass, adducts, compositeSpectrum);
        return adductDetected;
    }

    /**
     * process the list of adducts to perform the search. Return adducts to
     * search
     *
     * @param massesMode
     * @param ionMode Ionization mode
     * @param provisionalMap Map to get the value of adducts
     * @param adducts Possible adducts
     */
    private List<String> chooseAdducts(String ionMode,
            Map<String, String> provisionalMap,
            List<String> adducts) {

        if (ionMode.equals("positive")) {
            if (adducts.isEmpty() || adducts.get(0).equals("allPositives")) {
                Set set = provisionalMap.keySet();
                adducts = new LinkedList<String>(set);
            }
        } else if (ionMode.equals("negative")) {
            if (adducts.isEmpty() || adducts.get(0).equals("allNegatives")) {
                Set set = provisionalMap.keySet();
                adducts = new LinkedList<String>(set);
            }
        } else {
            Set set = provisionalMap.keySet();
            adducts = new LinkedList<String>(set);
        }
        //System.out.println(" NEUTRAL -> ADDUCTS: " + adducts);
        return adducts;

    }

    // Methods for browse search
    /**
     * returns a list of compounds from the databases based on the name and/or
     * formula
     *
     * @param name
     * @param formula
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findCompoundsBrowseSearch(
            String name,
            String formula,
            boolean exactName,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Databases " + databases);
        //System.out.println("Chemicaal Alphabet " + chemAlphabet);

        // Choose the adducts to perform the search

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
        if (!(databases.contains("AllWM") || databases.contains("All")
                || (databases.contains("Kegg") && databases.contains("HMDB")
                && databases.contains("LipidMaps") && databases.contains("Metlin")))) {
            if (databases.contains("Kegg")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsKegg ck on nc.compoundId=ck.compoundId ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsHMDB ch on nc.compoundId=ch.compoundId ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + " LEFT JOIN NewCompoundsLM cl on nc.compoundId=cl.compoundId ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "LEFT JOIN NewCompoundsMetlin cm on nc.compoundId=cm.compoundId ";
            }
            startQuery = startQuery + " WHERE (";
            if (databases.contains("Kegg")) {
                startQuery = startQuery + "ck.keggId is not null or ";
            }
            if (databases.contains("HMDB")) {
                startQuery = startQuery + "ch.hmdbId is not null or ";
            }
            if (databases.contains("LipidMaps")) {
                startQuery = startQuery + "cl.lmId is not null or ";
            }
            if (databases.contains("Metlin")) {
                startQuery = startQuery + "cm.agilentId is not null or ";
            }

            // delete the last or
            startQuery = startQuery.substring(0, startQuery.length() - 4);
            startQuery = startQuery + ") and ";
        } else {
            // add where condition for all databases
            startQuery = startQuery + " WHERE ";
        }

// Add compound type for search
        if (metabolitesType.equals("All including peptides")) {
            // Then search in all compounds in the database
        } else if (metabolitesType.equals("Only lipids")) {
            startQuery = startQuery + "(nc.compoundType = " + "1" + ") and ";
        } else {
            // By default, searches includes all compounds but peptides (2)
            startQuery = startQuery + "(nc.compoundType != " + "2" + ") and ";
        }

//        List<TheoreticalCompounds> listToBeOrderedForGen;
        // Insert compounds
        Query q1;
        // Finish the query with the mass condition
        if (!name.equals("")) {
            if (exactName) {
                finalQuery = startQuery + "nc.compoundName = '" + name + "'";
            } else {
                finalQuery = startQuery + "nc.compoundName like '%" + name + "%'";
            }
            if (!formula.equals("")) {
                finalQuery = finalQuery + " and nc.formula = '" + formula + "'";
            }
            //finalQuery = finalQuery + " limit 500";
        } else if (!formula.equals("")) {
            finalQuery = startQuery + " nc.formula = '" + formula + "'";
            //finalQuery = finalQuery + " limit 500";
        } else {
            return theoreticalCompoundList;
        }

        TheoreticalCompoundFactory tcf = new NewCompoundFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

        List<TheoreticalCompounds> listToBeOrdered;
        listToBeOrdered = new LinkedList<TheoreticalCompounds>();
        CompoundsGroupByMass compoundGroup;
        compoundGroup = new CompoundsGroupByMass(0d, 0d, "", true);
        //System.out.println("Final query: " + finalQuery);
        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
        q1.setMaxResults(500);
        results = q1.getResultList();

        if (results.isEmpty()) {
            TheoreticalCompounds compound = tcf.construct(
                    null, 0d, 0d, null, "", false, "", true);
            listToBeOrdered.add(compound);
            compoundGroup.addCompound(compound);
        } else {
            addToTheoreticalCompoundList(listToBeOrdered,
                    results, 0d, 0d,
                    tcf, null, "", false, "", compoundGroup, true);
        }
        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
        theoreticalCompoundList.addAll(listToBeOrdered);
        listCompoundsGroup.add(compoundGroup);

        if (searchInMINE) {
            String startQueryForGen = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
            String finalQueryForGen;

            if (!name.equals("")) {
                if (exactName) {
                    finalQueryForGen = startQueryForGen + "ncg.compoundName = '" + name + "'";
                } else {
                    finalQueryForGen = startQueryForGen + "ncg.compoundName like '%" + name + "%'";
                }
                if (!formula.equals("")) {
                    finalQueryForGen = finalQueryForGen + " and ncg.formula = '" + formula + "'";
                }
                //finalQueryForGen = finalQueryForGen + " limit 500";
            } else if (!formula.equals("")) {
                finalQueryForGen = startQueryForGen + " ncg.formula = '" + formula + "'";
                //finalQueryForGen = finalQueryForGen + " limit 500";
            } else {
                return theoreticalCompoundList;
            }

            Query q1forGen;
//                Insert compounds
//                System.out.println("Final query: " + finalQuery);
            q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
            q1forGen.setMaxResults(500);
            results = q1forGen.getResultList();
            if (results.isEmpty()) {
                TheoreticalCompounds compound = tcfForGen.construct(
                        null, 0d, 0d, null, "", false, "", true);
                listToBeOrdered.add(compound);
                compoundGroup.addCompound(compound);
            } else {

                addToTheoreticalCompoundList(listToBeOrdered,
                        results, 0d, 0d,
                        tcfForGen, null, "", false, "", compoundGroup, true);
            }
            Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
            theoreticalCompoundList.addAll(listToBeOrdered);
            listCompoundsGroup.add(compoundGroup);
        }

        // TODO
        // Implement a method to order the compounds group
        // Collections.sort(compoundGroup);
        /*
        System.out.println("\n \n GROUP OF compounds:");
        for (TheoreticalCompoundsGroup group : listCompoundsGroup)
        {
            System.out.println("GROUP OF: " + group.getExperimentalMass() + " Adduct: " + group.getAdduct());
            System.out.println("Number of elemens: ");
            for(TheoreticalCompounds tc : group.getTheoreticalCompounds())
            {
                System.out.println("Compound: " +tc.getIdentifier());
            }
        }
         */
        return theoreticalCompoundList;
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
    public List<OxidizedTheoreticalCompound> findLCOxidizedFA(
            Double parentIonEM,
            List<Double> FAsEM,
            String toleranceModeForFAs,
            Double toleranceForFAs,
            String toleranceModeForPI,
            Double toleranceForPI,
            String ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<OxidizedTheoreticalCompound> oxidizedPCsList = new LinkedList<OxidizedTheoreticalCompound>();

        FACompound nonOxidizedFA = null;
        FACompound oxidizedFA;
        List<String> oxidations = chooseLCOxidations(possibleOxidations);
        FACompoundFactory FAcFactory = new FACompoundFactory();

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

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
            String startQuery;
            startQuery = "SELECT nc FROM NewCompounds nc ";
            String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";

            startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
            startQuery = startQuery + " LEFT JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
            startQuery = startQuery + " WHERE (";
            startQuery = startQuery + "ncih.inHouseId is not null";
            startQuery = startQuery + ") and ";

            // Stablish the classification of Fatty Acids
            startQuery = startQuery + "nlc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
// TODO TAKE INTO ACCOUNT THAT MORE THAN ONE HYPOTHESIS CAN OCCUR!
                if (LIST_LONG_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    OxidizedTheoreticalCompound oxidizedPC = null;
// Possible oxidation in long chain
                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.PROTON_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                    Double delta;
                    switch (toleranceModeForFAs) {
                        // Case mDa
                        case "mDa":
                            delta = toleranceForFAs * 0.001d;
                            break;
                        // Case ppm
                        case "ppm":
                            delta = massToSearchForOxFA * toleranceForFAs * 0.000001f;
                            break;
                        default:
                            delta = massToSearchForOxFA * toleranceForFAs * 0.000001f;
                            break;
                    }
                    Double low = massToSearchForOxFA - delta;
                    Double high = massToSearchForOxFA + delta;

                    // Insert compounds
                    List results;
                    Query q1;
                    // Finish the query with the mass condition
                    finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                    finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearchForOxFA + ")";

                    q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                    results = q1.getResultList();

                    if (results.isEmpty()) {
                        oxidizedFA = FAcFactory.construct(null, mzOxidizedFAMass, massToSearchForOxFA, oxid);
                    } else {
                        oxidizedFA = getFirstFACompoundFromQuery(results,
                                FAcFactory, mzOxidizedFAMass, massToSearchForOxFA,
                                oxid);
                    }

// TODO CREATE PC WITH OXIDIZED AND NON OXIDIZED FA, AND ADD ANNOTATIONS OF 
// OXIDIZED AND NON OXIDIZED COMOPOUNDS IN THIS PC! 
                    oxidizedPC = new OxidizedCompound(mzOxidizedFAMass,
                            mzNONOxidizedFAMass,
                            parentIonEM,
                            oxid,
                            ConstantesForOxidation.HCOO_ADDUCT,
                            oxidizedFA,
                            nonOxidizedFA);
                    addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                    addNonOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                    oxidizedPCsList.add(oxidizedPC);
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
    public List<OxidizedTheoreticalCompound> findSCOxidizedFA(
            Double parentIonEM,
            List<Double> FAsEM,
            String toleranceModeForFA,
            Double toleranceForFA,
            String toleranceModeForPI,
            Double toleranceForPI,
            String ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<OxidizedTheoreticalCompound> oxidizedPCsList = new LinkedList<>();

        FACompound nonOxidizedFA = null;
        FACompound oxidizedFA;
        List<String> oxidations = chooseSCOxidations(possibleOxidations);
        FACompoundFactory FAcFactory = new FACompoundFactory();

        int nonOxidizedFAPosition;
        Double mzOxidizedFAMass;
        double mzNONOxidizedFAMass = 0;
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
            nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 1);

        } else if (FAsEM.size() == 2) {
// Write in log. 
// In short chain oxidation, the SC oxidized FA appears in the low region, which is difficult to see
// Because of the fragments that there are in this region of the cromatogram.
            // TODO WRITE IN LOG 
            // AND RETURN AN EMPTY LIST
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

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
            String startQuery;
            startQuery = "SELECT nc FROM NewCompounds nc ";
            String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";

            startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
            startQuery = startQuery + " LEFT JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
            startQuery = startQuery + " WHERE (";
            startQuery = startQuery + "ncih.inHouseId is not null";
            startQuery = startQuery + ") and ";

            // Stablish the classification of Fatty Acids
            startQuery = startQuery + "nlc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
                if (LIST_SHORT_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    OxidizedTheoreticalCompound oxidizedPC = null;
// Possible oxidation in long chain

                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                    Double delta;
                    switch (toleranceModeForFA) {
                        // Case mDa
                        case "mDa":
                            delta = toleranceForFA * 0.001d;
                            break;
                        // Case ppm
                        case "ppm":
                            delta = massToSearchForOxFA * toleranceForFA * 0.000001f;
                            break;
                        default:
                            delta = massToSearchForOxFA * toleranceForFA * 0.000001f;
                            break;
                    }
                    Double low = massToSearchForOxFA - delta;
                    Double high = massToSearchForOxFA + delta;

                    // Insert compounds
                    List results;
                    Query q1;
                    // Finish the query with the mass condition
                    finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                    finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearchForOxFA + ")";

                    q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                    results = q1.getResultList();

                    if (results.isEmpty()) {
                        oxidizedFA = FAcFactory.construct(null, mzOxidizedFAMass, massToSearchForOxFA, oxid);
                    } else {
                        oxidizedFA = getFirstFACompoundFromQuery(results,
                                FAcFactory, mzOxidizedFAMass, massToSearchForOxFA,
                                oxid);
                    }
// CREATE PC WITH OXIDIZED AND NON OXIDIZED FA, AND ADD ANNOTATIONS OF 
// OXIDIZED AND NON OXIDIZED COMOPOUNDS IN THIS PC! 
                    oxidizedPC = new OxidizedCompound(mzOxidizedFAMass,
                            mzNONOxidizedFAMass,
                            parentIonEM,
                            oxid,
                            ConstantesForOxidation.HCOO_ADDUCT,
                            oxidizedFA,
                            nonOxidizedFA);
                    addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                    oxidizedPCsList.add(oxidizedPC);

// If the oxidation hypothesis is COOH, then the adduct formed may have been M+HCOO or M-H
// Add the hypothesis of M-H
                    if (oxid.equals(ConstantesForOxidation.OXIDATIONTYPE_FOR_H_ADDUCT)) {

                        parentIonNeutralMass = parentIonEM + ConstantesForOxidation.PROTON_WEIGHT;
                        mzOxidizedFAMass = calculateFAEMFromPIandOtherFAEM(parentIonNeutralMass, mzNONOxidizedFAMass);
                        nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 1);

                        oxidationValue = MAPOXIDATIONS.get(oxid);
                        oxidationDouble = Double.parseDouble(oxidationValue);
                        massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                        massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                        switch (toleranceModeForFA) {
                            // Case mDa
                            case "mDa":
                                delta = toleranceForFA * 0.001d;
                                break;
                            // Case ppm
                            case "ppm":
                                delta = massToSearchForOxFA * toleranceForFA * 0.000001f;
                                break;
                            default:
                                delta = massToSearchForOxFA * toleranceForFA * 0.000001f;
                                break;
                        }
                        low = massToSearchForOxFA - delta;
                        high = massToSearchForOxFA + delta;

                        // Finish the query with the mass condition
                        finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                        finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearchForOxFA + ")";

                        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                        results = q1.getResultList();

                        if (results.isEmpty()) {
                            oxidizedFA = FAcFactory.construct(null, mzOxidizedFAMass, massToSearchForOxFA, oxid);
                        } else {
                            oxidizedFA = getFirstFACompoundFromQuery(results,
                                    FAcFactory, mzOxidizedFAMass, massToSearchForOxFA,
                                    oxid);
                        }

                        oxidizedPC = new OxidizedCompound(mzOxidizedFAMass,
                                mzNONOxidizedFAMass,
                                parentIonEM,
                                oxid,
                                ConstantesForOxidation.NEG_H_ADDUCT,
                                oxidizedFA,
                                nonOxidizedFA);
                        addOxidizedAnnotationsOverOxidizedPC(oxidizedPC, toleranceModeForPI, toleranceForPI);
                        oxidizedPCsList.add(oxidizedPC);
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
     * @param querymzFattyAcidMasses
     * @param modeQuerymzFattyAcidMass. 0 for experimental mass. 1 for
     * calculated mass
     * @return if mass querymzFattyAcidMass corresponds to a Fatty Acid
     */
    private Boolean isNonOxidizedFA(
            Double querymzFattyAcidMass,
            int modeQuerymzFattyAcidMass) {

        double toleranceForFA = 10;
        String toleranceModeForFA = "mDa";
        return isNonOxidizedFA(querymzFattyAcidMass, modeQuerymzFattyAcidMass, toleranceForFA, toleranceModeForFA);
    }

    /**
     * returns if mass querymzFattyAcidMass corresponds to a Fatty Acid based on
     * the tolerance
     *
     * @param querymzFattyAcidMass
     * @param modeQuerymzFattyAcidMass. 0 for experimental mass. 1 for
     * calculated mass
     * @param toleranceModeForFA
     * @param toleranceForFA
     * @return if mass querymzFattyAcidMass corresponds to a Fatty Acid
     */
    private Boolean isNonOxidizedFA(
            Double querymzFattyAcidMass,
            int modeQuerymzFattyAcidMass,
            Double toleranceForFA,
            String toleranceModeForFA) {

        Double massToSearch;
// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery;
        startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
        startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
        startQuery = startQuery + " LEFT JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "ncih.inHouseId is not null";
        startQuery = startQuery + ") and ";
        // Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

        if (modeQuerymzFattyAcidMass == 0) {
            massToSearch = querymzFattyAcidMass + ConstantesForOxidation.PROTON_WEIGHT;
        } else {
            massToSearch = querymzFattyAcidMass + ConstantesForOxidation.H_WEIGHT;
        }
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

        Double delta;
        switch (toleranceModeForFA) {
            // Case mDa
            case "mDa":
                delta = toleranceForFA * 0.001d;
                break;
            // Case ppm
            case "ppm":
                delta = massToSearch * toleranceForFA * 0.000001f;
                break;
            default:
                delta = massToSearch * toleranceForFA * 0.000001f;
                break;
        }
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;

        // Insert compounds
        Query q1;
        // Finish the query with the mass condition
        finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
        finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";

        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
        List results;
        results = q1.getResultList();

        if (results.isEmpty()) {
            return false;
        } else {
            return true;
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
    private FACompound getNonOxidizedFA(
            Double querymzFattyAcidMass,
            int modeQuerymzFattyAcidMass) {
        double toleranceForFA = 10;
        String toleranceModeForFA = "mDa";
        return getNonOxidizedFA(querymzFattyAcidMass, modeQuerymzFattyAcidMass, toleranceForFA, toleranceModeForFA);
    }

    /**
     * returns if mass querymzFattyAcidMass corresponds to a Fatty Acid based on
     * the tolerance
     *
     * @param querymzFattyAcidMass
     * @param modeQuerymzFattyAcidMass. 0 for experimental mass. 1 for
     * calculated mass
     * @param toleranceModeForFA
     * @param toleranceForFA
     * @return if mass querymzFattyAcidMass corresponds to a Fatty Acid
     */
    private FACompound getNonOxidizedFA(
            Double querymzFattyAcidMass,
            int modeQuerymzFattyAcidMass,
            Double toleranceForFA,
            String toleranceModeForFA) {
        FACompound FAcompound;
        Double massToSearch;
// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery;
        startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";

        startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
        startQuery = startQuery + " LEFT JOIN NewLipidsClassification nlc on nc.compoundId=nlc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "ncih.inHouseId is not null";
        startQuery = startQuery + ") and ";

        // Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

        FACompoundFactory FACompoundFactory = new FACompoundFactory();

        if (modeQuerymzFattyAcidMass == 0) {
            massToSearch = querymzFattyAcidMass + ConstantesForOxidation.PROTON_WEIGHT;
        } else {
            massToSearch = querymzFattyAcidMass + ConstantesForOxidation.H_WEIGHT;
        }
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

        Double delta;
        switch (toleranceModeForFA) {
            // Case mDa
            case "mDa":
                delta = toleranceForFA * 0.001d;
                break;
            // Case ppm
            case "ppm":
                delta = massToSearch * toleranceForFA * 0.000001f;
                break;
            default:
                delta = massToSearch * toleranceForFA * 0.000001f;
                break;
        }
        Double low = massToSearch - delta;
        Double high = massToSearch + delta;

        // Insert compounds
        Query q1;
        // Finish the query with the mass condition
        finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
        finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";

        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
        List results;
        results = q1.getResultList();

        if (results.isEmpty()) {
            FAcompound = null;
        } else {
            FAcompound = getFirstFACompoundFromQuery(results, FACompoundFactory, querymzFattyAcidMass, massToSearch, "");
        }
        return FAcompound;
    }

    /**
     * process the list of LC oxidations to perform the search. Return LC
     * oxidations to search
     *
     * @param oxidations Possible oxidations
     */
    private List<String> chooseLCOxidations(List<String> oxidations) {
        if (oxidations.isEmpty() || oxidations.get(0).equals("allOxidations")) {
            oxidations = OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
        }
        //System.out.println("DEFAULT -> ADDUCTS: " + adducts);
        return oxidations;
    }

    /**
     * process the list of SC oxidations to perform the search. Return SC
     * oxidations to search
     *
     * @param oxidations Possible oxidations
     */
    private List<String> chooseSCOxidations(List<String> oxidations) {
        if (oxidations.isEmpty() || oxidations.get(0).equals("allOxidations")) {
            oxidations = OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
        }
        //System.out.println("DEFAULT -> ADDUCTS: " + adducts);
        return oxidations;
    }
}
