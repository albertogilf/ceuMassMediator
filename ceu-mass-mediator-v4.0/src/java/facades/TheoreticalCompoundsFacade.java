package facades;

import DBManager.QueryConstructor;
import java.util.*;
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
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.CompoundsGroupByMass;
import persistence.oxidizedTheoreticalCompound.OxidizedCompound;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import utilities.TheoreticalCompoundsComparer;
import utilities.ConstantesForOxidation;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;
import utilities.AdductProcessing;
import static utilities.AdductsLists.MAPMZPOSITIVEADDUCTS;
import utilities.DataFromInterfacesUtilities;
import static utilities.OxidationLists.LIST_LONG_CHAIN_OXIDATION_TYPES;
import static utilities.OxidationLists.LIST_SHORT_CHAIN_OXIDATION_TYPES;
import utilities.OxidationProcessing;
import utilities.Utilities;
import static utilities.Utilities.calculateFAEMFromPIandOtherFAEM;
import static utilities.OxidationLists.MAPOXIDATIONMZS;

/**
 *
 * @author Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente. Last
 * Modified: 28/05/2019
 */
@Stateless(name = "TheoreticalCompoundsFacade")
public class TheoreticalCompoundsFacade extends AbstractFacade<TheoreticalCompounds> {

    private static final long serialVersionUID = 1L;

    /**
     * EntityManager to manage the Java Persistence Api
     */
    @PersistenceContext(unitName = "ceuMassMediator")
    private EntityManager em;

    /**
     * Gets the EntityManager
     *
     * @return EntityManager of the class
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * constructor to set the entityClass from the inherited class
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
     * @param experimentalMass. Measured Mass in the Mass Spectrometer
     * @param retentionTime. Retention time in MS
     * @param tcf. The factory
     * @param massToSearch Mass To Search in the database
     * @param adduct adduct to search
     */
    private Integer addToTheoreticalCompoundList(List<TheoreticalCompounds> tcl,
            List<Compounds> queryResult,
            Double experimentalMass,
            Double retentionTime,
            TheoreticalCompoundFactory tcf,
            Double massToSearch,
            String adduct,
            boolean isAdductAutoDetected,
            String adductAutoDetected,
            TheoreticalCompoundsGroup compoundGroup,
            Boolean isSignificative,
            Integer hypothesisId) {
        Iterator<Compounds> it = queryResult.iterator();
        while (it.hasNext()) {
            TheoreticalCompounds compound = tcf.construct(it.next(),
                    experimentalMass,
                    retentionTime,
                    massToSearch,
                    adduct,
                    isAdductAutoDetected,
                    adductAutoDetected,
                    isSignificative,
                    hypothesisId);
            tcl.add(compound);
            compoundGroup.addCompound(compound);
            hypothesisId++;
        }
        return hypothesisId;
    }

    /**
     * returns the TheoreticalFACompound which it is in the List queryResult If
     * there is more than one, it returns the first one
     *
     * @param queryResult. The list of queryResults
     * @param FAcompoundFactory. The factory for constructing the FA
     * @param FAEM. Measured Mass in the Mass Spectrometer
     * @param massToSearch Mass To Search in the database
     * @param queryParentIonMass queryParentIonMass. It should be bigger than 50
     * Da
     * @param oxidationType Oxidation Type
     * @return the first FA Compound or null if there is not FA
     */
    private FACompound getFirstFACompoundFromQuery(
            List<Compounds> queryResult,
            FACompoundFactory FAcompoundFactory,
            Double FAEM,
            Double massToSearch,
            String oxidationType) {
        Iterator<Compounds> it = queryResult.iterator();
        if (it.hasNext()) {
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
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + ".compoundId FROM NewCompounds " + aliasTable;
        String finalQuery;

        startQuery = startQuery + " INNER JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
        startQuery = startQuery + " INNER JOIN CompoundChain cc on nc.compoundId=cc.keyCompoundChain.compoundId ";
        startQuery = startQuery + " INNER JOIN Chains chains on cc.keyCompoundChain.chainId=chains.chainId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "nlmc.compoundId is not null and ";
        startQuery = startQuery + "cc.keyCompoundChain.compoundId is not null";
        startQuery = startQuery + ") and ";

// Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlmc.subClass = " + ConstantesForOxidation.PC_OXIDIZED_LIPIDS_LM_CLASSIFICATION + " and ";
// set the number of carbons and double bonds for oxidized FA. The query for Non Oxidized is not needed since the mass already matched
        startQuery = startQuery + "chains.carbons = " + numCarbonsOfOxidizedFA + " and ";
        startQuery = startQuery + "chains.doubleBonds = " + doubleBondsOfOxidizedFA + " and ";

        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
        } else {
            return;
        }

        finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearch, toleranceModeForPI, toleranceForPI);
        //System.out.println("FINAL QUERY OXIDIZED COMPOUND: " + finalQuery);
        Query queryForOxidizedFA;

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
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN CompoundChain cc on nc.compoundId=cc.keyCompoundChain.compoundId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN Chains chains on cc.keyCompoundChain.chainId=chains.chainId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " WHERE (";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nc.compoundId=" + compoundId + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "cc.keyCompoundChain.compoundId is not null";
                queryForNonOxidizeFA = queryForNonOxidizeFA + ") and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "chains.carbons = " + numCarbonsOfNonOxidizedFA + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "chains.doubleBonds = " + doubleBondsOfNonOxidizedFA;
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
                            true,
                            -1);
                    // Add the chemical Alphabet
                    oxidizedAnnotationOverPC.addCompound(compound);
                }
            }
        }
    }

    /**
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
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + ".compoundId FROM NewCompounds " + aliasTable;
        String finalQuery;
        startQuery = startQuery + " INNER JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
        startQuery = startQuery + " INNER JOIN CompoundChain cc on nc.compoundId=cc.keyCompoundChain.compoundId ";
        startQuery = startQuery + " INNER JOIN Chains chains on cc.keyCompoundChain.chainId=chains.chainId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "nlmc.compoundId is not null and ";
        startQuery = startQuery + "cc.keyCompoundChain.compoundId is not null";
        startQuery = startQuery + ") and ";

// Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlmc.mainClass = " + ConstantesForOxidation.PC_NON_OXIDIZED_LIPIDS_LM_CLASSIFICATION + " and ";
// set the number of carbons and double bonds for oxidized FA. The query for Non Oxidized is not needed since the mass already matched
        startQuery = startQuery + "chains.carbons = " + numCarbonsOfOxidizedFA + " and ";
        startQuery = startQuery + "chains.doubleBonds = " + doubleBondsOfOxidizedFA + " and ";
        if (neutralMassPI > 0) {
            massToSearch = neutralMassPI;
            // Also include the oxidation
            Double oxidationDouble;
            String oxidationValue;
            oxidationValue = MAPOXIDATIONMZS.get(oxidationType);
            oxidationDouble = Double.parseDouble(oxidationValue);
            massToSearch = massToSearch + oxidationDouble;
        } else {
            massToSearch = null;
            return;
        }
        finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearch, toleranceModeForPI, toleranceForPI);
        // System.out.println("FINAL QUERY NON OXIDIZED COMPOUND: " + finalQuery);

        Query queryForOxidizedFA;
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
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN CompoundChain cc on nc.compoundId=cc.keyCompoundChain.compoundId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " INNER JOIN Chains chains on cc.keyCompoundChain.chainId=chains.chainId ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + " WHERE (";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "nc.compoundId=" + compoundId + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "cc.keyCompoundChain.compoundId is not null";
                queryForNonOxidizeFA = queryForNonOxidizeFA + ") and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "chains.carbons = " + numCarbonsOfNonOxidizedFA + " and ";
                queryForNonOxidizeFA = queryForNonOxidizeFA + "chains.doubleBonds = " + doubleBondsOfNonOxidizedFA;
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
                            true,
                            -1);
                    // Add the chemical Alphabet
                    NonOxidizedAnnotationOverPC.addCompound(compound);
                }
            }
        }
    }

    /**
     *
     * returns a list of compounds from the databases according to the
     * monoisotopic mass and the tolerance established.
     *
     * @param masses List of masses
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
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        long simpleJPA = 0;
        long start = System.currentTimeMillis();
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
        long end = System.currentTimeMillis();
        simpleJPA = simpleJPA + (end - start);
        System.out.println("simple JPA: " + simpleJPA);
        return theoreticalCompoundList;
    }

    /**
     * returns a list of compounds from the databases according to the
     * monoisotopic mass and the tolerance.
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
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        Integer hypothesisId = 0;
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;
        //System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Adducts " + adducts);
        //System.out.println("QUERY MASSES -> " + masses);
        //System.out.println("Databases " + databases);

        // Choose the adducts to perform the search
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        int massesModeInt = utilities.DataFromInterfacesUtilities.inputMassModeToInteger(massesMode);
        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable;
        String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
// Add filter for databases
        startQuery = QueryConstructor.addFilterDatabasesJPA(startQuery, databases);

// Add compound type for search
        startQuery = QueryConstructor.addFilterMetabolitesTypeJPA(startQuery, metabolitesType);

        TheoreticalCompoundFactory tcf = new NewCompoundFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {

            Double inputMass = masses.get(i);
            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesModeInt, ionMode);
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
                massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, s, adductValue);
                // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearch, toleranceMode, tolerance);

                // Insert compounds
                Query q1;

                // Test for JPA left join
                //finalQuery = "SELECT nc FROM NewCompounds nc WHERE (nc.ncKegg is not null or nc.ncHMDB is not null) 
                // and (nc.mass >= 236.08001738701788 and nc.mass <= 236.0941826129821)";
                //finalQuery = "SELECT c FROM NewCompounds c LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId "
                // + "left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId where (ck.keggId is not null 
                // or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
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
                            null, inputMass, 0d, massToSearch, s, false, "", true, hypothesisId);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                    hypothesisId++;
                } else {
                    hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                            results,
                            inputMass,
                            0d,
                            tcf,
                            massToSearch,
                            s,
                            false,
                            "",
                            compoundGroup,
                            true,
                            hypothesisId);
                }
                if (searchInMINE) {
                    String aliasTableForGen = "ncg";
                    String startQueryForGen = "SELECT " + aliasTableForGen + " FROM NewCompoundsGen "
                            + aliasTableForGen + " WHERE ";
                    String finalQueryForGen;

                    finalQueryForGen = QueryConstructor.addFilterMassesJPA(aliasTableForGen, startQueryForGen, massToSearch, toleranceMode, tolerance);
                    Query q1forGen;
//                Insert compounds
//                System.out.println("Final query: " + finalQuery);
                    q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                    results = q1forGen.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, 0d, massToSearch, s, false, "", true, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, 0d,
                                tcfForGen, massToSearch, s, false, "", compoundGroup, true, hypothesisId);
                    }
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);
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
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases) {
        Integer hypothesisId = 1;
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<>();
        Map<String, String> provisionalMap;

        // System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        // System.out.println("Adducts " + adducts);
        //System.out.println("Databases " + databases);
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        int massesModeInt = utilities.DataFromInterfacesUtilities.inputMassModeToInteger(massesMode);
        /*System.out.println("\nadducts\n" + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "ncg";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompoundsGen " + aliasTable + " WHERE ";
        String finalQuery;

        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesModeInt, ionMode);
            //System.out.println("\n Compound: " + inputMass + " added simple charged");
            for (String s : adducts) {
                listToBeOrdered = new LinkedList<>();
//                    listToBeOrderedForGen = new ArrayList<TheoreticalCompounds>();
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s);
                Double adductValue;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(s);
                adductValue = Double.parseDouble(sValueAdduct);
                //System.out.println("ADDUCT: " + s);
                massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, s, adductValue);
                // System.out.println("\n \n adduct as parameter: " + adduct + " as string: " + s);

                finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearch, toleranceMode, tolerance);
                //System.out.println("Final query: " + finalQuery);

                // Insert compounds
                Query q1;
                q1 = getEntityManager().createQuery(finalQuery, NewCompoundsGen.class);
                results = q1.getResultList();
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcfForGen.construct(
                            null, inputMass, 0d, massToSearch, s, false, "", true, hypothesisId);
                    hypothesisId++;
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, 0d,
                            tcfForGen, massToSearch, s, false, "", compoundGroup, true, hypothesisId);
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);
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
            List<Map<Double, Double>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        long advancedJPA = 0;
        long start = System.currentTimeMillis();
        List<TheoreticalCompounds> theoreticalCompoundList;
        adducts = DataFromInterfacesUtilities.getValueAllAductsBasedOnIonMode(ionMode, adducts);
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
        long end = System.currentTimeMillis();
        advancedJPA = (end - start);
        System.out.println("ADVANCED JPA: " + advancedJPA);
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
            List<Map<Double, Double>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        Integer hypothesisId = 1;
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<>();
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
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        int massesModeInt = utilities.DataFromInterfacesUtilities.inputMassModeToInteger(massesMode);
        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable + " ";
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
        startQuery = QueryConstructor.addFilterDatabasesJPA(startQuery, databases);
// Add chemical alphabet
        startQuery = QueryConstructor.addFilterIntegerFormulaTypeJPA(aliasTable, startQuery, chemAlphabet);

// Add compound type for search
        startQuery = QueryConstructor.addFilterMetabolitesTypeJPA(startQuery, metabolitesType);

        TheoreticalCompoundFactory tcf = new NewCompoundFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesModeInt, ionMode);
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Double> inputCompositeSpectrum = compositeSpectra.get(i);
            Double adductValue;
            String sValueAdduct;
            /*
// Taking statistics for results
            queryFinalKEGG = "(";
            queryFinalLM = "(";
            queryFinalHMDB = "(";
            queryFinalMetlin = "(";
             */

            String detectedAdduct = AdductProcessing.detectAdductBasedOnCompositeSpectrum(
                    ionMode, mzInputMass, adducts, inputCompositeSpectrum);
            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// Check if adduct is detected based on the composite spectrum

                //System.out.println("Automatic adduct detected: " + inputMass);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(
                        inputMass, inputRetentionTime, detectedAdduct, isSignificative);
// System.out.println("\n Compound: " + inputMass + " Adduct automatically detected");
                // As we detect automatically the adduct, go directly for the inputMass

                sValueAdduct = provisionalMap.get(detectedAdduct);
                adductValue = Double.parseDouble(sValueAdduct);

//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                massToSearch = utilities.AdductProcessing.getMassToSearch(
                        mzInputMass, detectedAdduct, adductValue);
                finalQuery = QueryConstructor.addFilterMassesJPA(
                        aliasTable, startQuery, massToSearch, toleranceMode, tolerance);
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
                            null, inputMass, inputRetentionTime, massToSearch,
                            detectedAdduct, false, "", isSignificative, hypothesisId);
                    hypothesisId++;
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, inputRetentionTime,
                            tcf, massToSearch, detectedAdduct, false, "",
                            compoundGroup, isSignificative, hypothesisId);
                }
                if (searchInMINE) {
// It could be done in a method, but the number of parameters is too long, so we will keep code here
                    String aliasTableForGen = "ncg";
                    String startQueryForGen = "SELECT " + aliasTableForGen
                            + " FROM NewCompoundsGen " + aliasTableForGen
                            + " WHERE ";
                    String finalQueryForGen;
                    startQueryForGen = QueryConstructor.addFilterIntegerFormulaTypeJPA(aliasTableForGen,
                            startQueryForGen, chemAlphabet);

                    finalQueryForGen = QueryConstructor.addFilterMassesJPA(
                            aliasTableForGen, startQueryForGen, massToSearch, toleranceMode, tolerance);

                    // Insert compounds
                    Query q1forGen;
//                System.out.println("Final query: " + finalQuery);
                    q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                    results = q1forGen.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, massToSearch,
                                detectedAdduct, false, "", isSignificative, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime, tcfForGen, massToSearch,
                                detectedAdduct, false, "", compoundGroup, isSignificative, hypothesisId);
                    }
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                for (String s : adducts) {
                    // Include all adducts if we detect a double charge for 
                    // frontend presentation
                    compoundGroup = new CompoundsGroupByMass(inputMass, inputRetentionTime, s, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    sValueAdduct = provisionalMap.get(s);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    //System.out.println("ADDUCT: " + s);
                    if (!s.equals(detectedAdduct)) {
                        TheoreticalCompounds compound = tcf.construct(
                                null, inputMass, inputRetentionTime, null, s, true,
                                detectedAdduct, isSignificative, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);

                        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                        theoreticalCompoundList.addAll(listToBeOrdered);
                        listCompoundsGroup.add(compoundGroup);
                    }
                }
            } else {
                //System.out.println("\n Compound: " + inputMass + " added simple charged");
                for (String adduct : adducts) {
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(
                            inputMass, inputRetentionTime, adduct, isSignificative);
                    sValueAdduct = provisionalMap.get(adduct);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, adduct, adductValue);
                    //System.out.println("ADDUCT: " + s);

                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                    finalQuery = QueryConstructor.addFilterMassesJPA(
                            aliasTable, startQuery, massToSearch, toleranceMode, tolerance);
                    /*
// Take statistics about compounds without unifying                
                    queryFinalKEGG = queryFinalKEGG + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalLM = queryFinalLM + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalHMDB = queryFinalHMDB + "(mass >= " + low + " and mass <= " + high + ") or ";
                    queryFinalMetlin = queryFinalMetlin + "(mass >= " + low + " and mass <= " + high + ") or ";
                     */
                    Query q1;
                    // Test for JPA left join
                    //finalQuery = "SELECT nc FROM NewCompounds nc WHERE (nc.ncKegg is not null or nc.ncHMDB is not null) 
                    // and (nc.mass >= 236.08001738701788 and nc.mass <= 236.0941826129821)";
                    //finalQuery = "SELECT c FROM NewCompounds c LEFT JOIN NewCompoundsKegg ck on 
                    // c.compoundId=ck.compoundId left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId where 
                    //  + "(ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 
                    // and c.mass <= 236.0941826129821)";
                    //System.out.println("Final query: " + finalQuery);
                    q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                    results = q1.getResultList();

                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcf.construct(
                                null, inputMass, inputRetentionTime,
                                massToSearch, adduct, false, "", isSignificative, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime, tcf, massToSearch,
                                adduct, false, "", compoundGroup, isSignificative, hypothesisId);
                    }
                    if (searchInMINE) {
                        String aliasTableForGen = "ncg";
                        String startQueryForGen = "SELECT " + aliasTableForGen
                                + " FROM NewCompoundsGen " + aliasTableForGen
                                + " WHERE ";
                        String finalQueryForGen;
                        startQueryForGen = QueryConstructor.addFilterIntegerFormulaTypeJPA(aliasTableForGen,
                                startQueryForGen, chemAlphabet);
                        finalQueryForGen = QueryConstructor.addFilterMassesJPA(aliasTableForGen,
                                startQueryForGen, massToSearch, toleranceMode, tolerance);

                        // Insert compounds
                        Query q1forGen;
//                System.out.println("Final query: " + finalQuery);
                        q1forGen = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                        results = q1forGen.getResultList();
                        if (results.isEmpty()) {
                            TheoreticalCompounds compound = tcfForGen.construct(
                                    null, inputMass, inputRetentionTime, massToSearch,
                                    adduct, false, "", isSignificative, hypothesisId);
                            hypothesisId++;
                            listToBeOrdered.add(compound);
                            compoundGroup.addCompound(compound);
                        } else {
                            hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                                    results, inputMass, inputRetentionTime, tcfForGen, massToSearch,
                                    adduct, false, "", compoundGroup, isSignificative, hypothesisId);
                        }
                    }
                    Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                    theoreticalCompoundList.addAll(listToBeOrdered);
                    listCompoundsGroup.add(compoundGroup);
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
            List<Map<Double, Double>> compositeSpectra,
            List<Boolean> isSignificativeCompound,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            int ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases) {
        Integer hypothesisId = 1;
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;

        // System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        // System.out.println("Adducts " + adducts);
        //System.out.println("Databases " + databases);
        //System.out.println("Chemical Alphabet " + chemAlphabet);
        provisionalMap = AdductProcessing.chooseprovisionalMapAdducts(ionMode);
        adducts = AdductProcessing.chooseAdducts(ionMode, provisionalMap, adducts);

        int massesModeInt = utilities.DataFromInterfacesUtilities.inputMassModeToInteger(massesMode);
        /*System.out.println("\nadducts\n" + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTableForGen = "ncg";
        String startQueryForGen = "SELECT " + aliasTableForGen
                + " FROM NewCompoundsGen " + aliasTableForGen
                + " WHERE ";
        String finalQueryForGen;
        startQueryForGen = QueryConstructor.addFilterIntegerFormulaTypeJPA(aliasTableForGen,
                startQueryForGen, chemAlphabet);

        TheoreticalCompoundFactory tcfForGen = new NewCompoundGenFactory();
// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesModeInt, ionMode);
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Double> inputCompositeSpectrum = compositeSpectra.get(i);
            String detectedAdduct = AdductProcessing.detectAdductBasedOnCompositeSpectrum(ionMode,
                    mzInputMass, adducts, inputCompositeSpectrum);
            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// At this moment only one adduct possible.

                // System.out.println("Automaticall adduct detected: " + masses.get(i) + " Compos: " + inputCompositeSpectrum);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(
                        inputMass, inputRetentionTime, detectedAdduct, isSignificative);

                Double adductValue;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(detectedAdduct);
                adductValue = Double.parseDouble(sValueAdduct);
                //System.out.println("ADDUCT: " + s);
                massToSearch = utilities.AdductProcessing.getMassToSearch(
                        mzInputMass, detectedAdduct, adductValue);
//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                finalQueryForGen = QueryConstructor.addFilterMassesJPA(
                        aliasTableForGen, startQueryForGen, massToSearch, toleranceMode, tolerance);
                Query q1;
// Insert compounds
//                System.out.println("Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                results = q1.getResultList();
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcfForGen.construct(
                            null, inputMass, inputRetentionTime, massToSearch, detectedAdduct,
                            false, "", isSignificative, hypothesisId);
                    hypothesisId++;
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                            results, inputMass, inputRetentionTime, tcfForGen, massToSearch,
                            detectedAdduct, false, "", compoundGroup, isSignificative, hypothesisId);
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                for (String adduct : adducts) {
                    // Include all adducts if we detect a double charge for 
                    // frontend presentation
                    compoundGroup = new CompoundsGroupByMass(inputMass,
                            inputRetentionTime, adduct, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    //System.out.println("ADDUCT: " + s);
                    if (!adduct.equals(detectedAdduct)) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, null, adduct,
                                true, detectedAdduct, isSignificative, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);

                        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                        theoreticalCompoundList.addAll(listToBeOrdered);
                        listCompoundsGroup.add(compoundGroup);
                    }
                }
            } else {
                //System.out.println("\n Compound: " + inputMass + " added simple charged"
                //        + inputCompositeSpectrum);
                for (String adduct : adducts) {
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new ArrayList<TheoreticalCompounds>();
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(
                            inputMass, inputRetentionTime, adduct, isSignificative);
                    Double adductValue = 0.0d;
                    String sValueAdduct;
                    sValueAdduct = provisionalMap.get(adduct);
                    adductValue = Double.parseDouble(sValueAdduct);
                    //System.out.println("ADDUCT: " + s);
                    massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, adduct, adductValue);
                    finalQueryForGen = QueryConstructor.addFilterMassesJPA(aliasTableForGen,
                            startQueryForGen, massToSearch, toleranceMode, tolerance);

                    // Insert compounds
                    Query q1;
                    //System.out.println("Final query: " + finalQuery);
                    q1 = getEntityManager().createQuery(finalQueryForGen, NewCompoundsGen.class);
                    results = q1.getResultList();
                    if (results.isEmpty()) {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, massToSearch,
                                adduct, false, "", isSignificative, hypothesisId);
                        hypothesisId++;
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                                results, inputMass, inputRetentionTime, tcfForGen, massToSearch,
                                adduct, false, "", compoundGroup, isSignificative, hypothesisId);
                    }
                    Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                    theoreticalCompoundList.addAll(listToBeOrdered);
                    listCompoundsGroup.add(compoundGroup);
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
     * returns the FDR calculated based on the F+H adduct
     *
     * @param masses , List of masses
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public Double getFDR(
            List<Double> masses,
            String toleranceMode,
            Double tolerance,
            int ionMode,
            String massesMode,
            List<String> databases,
            String metabolitesType) {
        return this.getFDR(masses, toleranceMode, tolerance, "ALL", ionMode, massesMode, databases, metabolitesType);

    }

    /**
     * returns the FDR calculated based on the F+H adduct
     *
     * @param masses , List of masses
     * @param chemAlphabet
     * @param toleranceMode tolerance set in ppm or mDa
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public Double getFDR(
            List<Double> masses,
            String toleranceMode,
            Double tolerance,
            String chemAlphabet,
            int ionMode,
            String massesMode,
            List<String> databases,
            String metabolitesType) {

        int massesModeInt = utilities.DataFromInterfacesUtilities.inputMassModeToInteger(massesMode);
        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable + " ";
        String finalQuery;
        startQuery = QueryConstructor.addFilterDatabasesJPA(startQuery, databases);
// Add chemical alphabet
        startQuery = QueryConstructor.addFilterIntegerFormulaTypeJPA(aliasTable, startQuery, chemAlphabet);

// Add compound type for search
        startQuery = QueryConstructor.addFilterMetabolitesTypeJPA(startQuery, metabolitesType);
        double fdr = 0;
        int totalHits = 0;
        for (int i = 0; i < masses.size(); i++) {
            Double inputMass = masses.get(i);
            Double mzInputMass = Utilities.calculateMZFromNeutralMass(inputMass, massesModeInt, ionMode);
            String adduct = "M+F+H";
            Double adductValue;
            String sValueAdduct;

            sValueAdduct = MAPMZPOSITIVEADDUCTS.get(adduct);
            adductValue = Double.parseDouble(sValueAdduct);
            //System.out.println("ADDUCT: " + s);
            massToSearch = utilities.AdductProcessing.getMassToSearch(mzInputMass, adduct, adductValue);
            finalQuery = QueryConstructor.addFilterMassesJPA(
                    aliasTable, startQuery, massToSearch, toleranceMode, tolerance);
            Query q1;
            q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
            results = q1.getResultList();
            totalHits += results.size();
        }
        fdr = (double) totalHits / (double) masses.size();
        return fdr;
    }

    // Methods for browse search
    /**
     * returns a list of compounds from the databases based on the name and/or
     * formula
     *
     * @param name
     * @param formula
     * @param exactName
     * @param databases Databases to search
     * @param metabolitesType metabolites Type to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findCompoundsBrowseSearch(
            String name,
            String formula,
            boolean exactName,
            List<String> databases,
            String metabolitesType) {
        Integer hypothesisId = 1;
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<>();
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Databases " + databases);
        //System.out.println("Chemicaal Alphabet " + chemAlphabet);

        // Choose the adducts to perform the search

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
         */
        List results;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT nc FROM NewCompounds nc ";
        String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";
        startQuery = QueryConstructor.addFilterDatabasesJPA(startQuery, databases);

// Add compound type for search
        startQuery = QueryConstructor.addFilterMetabolitesTypeJPA(startQuery, metabolitesType);

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
                    null, 0d, 0d, null, "", false, "", true, hypothesisId);
            listToBeOrdered.add(compound);
            compoundGroup.addCompound(compound);
        } else {
            hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                    results, 0d, 0d,
                    tcf, null, "", false, "", compoundGroup, true, hypothesisId);
        }
        Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
        theoreticalCompoundList.addAll(listToBeOrdered);

        if (searchInMINE) {

            listToBeOrdered = new LinkedList<TheoreticalCompounds>();
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
                        null, 0d, 0d, null, "", false, "", true, hypothesisId);
                hypothesisId++;
                listToBeOrdered.add(compound);
                compoundGroup.addCompound(compound);
            } else {
                hypothesisId = addToTheoreticalCompoundList(listToBeOrdered,
                        results, 0d, 0d,
                        tcfForGen, null, "", false, "", compoundGroup, true, hypothesisId);
            }
            Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
            theoreticalCompoundList.addAll(listToBeOrdered);
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

    // Methods for Oxidized compounds
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
            int ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<OxidizedTheoreticalCompound> oxidizedPCsList = new LinkedList<OxidizedTheoreticalCompound>();

        FACompound nonOxidizedFA = null;
        FACompound oxidizedFA;
        List<String> oxidations = OxidationProcessing.chooseLCOxidations(possibleOxidations);
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
            String aliasTable = "nc";
            String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable;
            String finalQuery;

            startQuery = startQuery + " INNER JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
            startQuery = startQuery + " INNER JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
            startQuery = startQuery + " WHERE (";
            startQuery = startQuery + "ncih.inHouseId is not null";
            startQuery = startQuery + ") and ";

            // Stablish the classification of Fatty Acids
            startQuery = startQuery + "nlmc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
// TODO TAKE INTO ACCOUNT THAT MORE THAN ONE HYPOTHESIS CAN OCCUR!
                if (LIST_LONG_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    OxidizedTheoreticalCompound oxidizedPC = null;
// Possible oxidation in long chain
                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONMZS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.PROTON_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                    finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery,
                            massToSearchForOxFA, toleranceModeForFAs, toleranceForFAs);

                    // Insert compounds
                    List results;
                    Query q1;

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
            int ionMode,
            List<String> databases,
            List<String> possibleOxidations) {
        List<OxidizedTheoreticalCompound> oxidizedPCsList = new LinkedList<>();

        FACompound nonOxidizedFA = null;
        FACompound oxidizedFA;
        List<String> oxidations = OxidationProcessing.chooseSCOxidations(possibleOxidations);
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
            nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 0);

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
            String aliasTable = "nc";
            String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable;
            String finalQuery;
// Add databases depending on the user selection
// finalQuery = "SELECT c FROM NewCompounds c 
// LEFT JOIN NewCompoundsKegg ck on c.compoundId=ck.compoundId 
// left join NewCompoundsHMDB ch on c.compoundId=ch.compoundId 
// where (ck.keggId is not null or ch.hmdbId is not null) and (c.mass >= 236.08001738701788 and c.mass <= 236.0941826129821)";

            startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
            startQuery = startQuery + " LEFT JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
            startQuery = startQuery + " WHERE (";
            startQuery = startQuery + "ncih.inHouseId is not null";
            startQuery = startQuery + ") and ";

            // Stablish the classification of Fatty Acids
            startQuery = startQuery + "nlmc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

            String oxidationValue;

            //System.out.println("\n FA: " + fattyAcylMass);
            for (String oxid : oxidations) {
                if (LIST_SHORT_CHAIN_OXIDATION_TYPES.contains(oxid)) {
                    OxidizedTheoreticalCompound oxidizedPC = null;
// Possible oxidation in long chain

                    Double oxidationDouble;
                    oxidationValue = MAPOXIDATIONMZS.get(oxid);
                    oxidationDouble = Double.parseDouble(oxidationValue);
                    massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                    massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                    finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearchForOxFA,
                            toleranceModeForFA, toleranceForFA);

                    // Insert compounds
                    List results;
                    Query q1;

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
                        nonOxidizedFA = getNonOxidizedFA(mzNONOxidizedFAMass, 0);

                        oxidationValue = MAPOXIDATIONMZS.get(oxid);
                        oxidationDouble = Double.parseDouble(oxidationValue);
                        massToSearchForOxFA = mzOxidizedFAMass + ConstantesForOxidation.H_WEIGHT;
                        massToSearchForOxFA = massToSearchForOxFA + oxidationDouble;
// Statistics
//                    massToSearch = mzFattyAcidMass + oxidationDouble;
                        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
                        finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearchForOxFA,
                                toleranceModeForFA, toleranceForFA);

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

        Double massToSearchForOxFA;

        if (modeQuerymzFattyAcidMass == 0) {
            massToSearchForOxFA = querymzFattyAcidMass + ConstantesForOxidation.PROTON_WEIGHT;
        } else {
            massToSearchForOxFA = querymzFattyAcidMass + ConstantesForOxidation.H_WEIGHT;
        }
// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable;
        String finalQuery;
        startQuery = startQuery + " LEFT JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
        startQuery = startQuery + " LEFT JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "ncih.inHouseId is not null";
        startQuery = startQuery + ") and ";
        // Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlmc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
        finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearchForOxFA,
                toleranceModeForFA, toleranceForFA);

        // Insert compounds
        Query q1;
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
    private FACompound getNonOxidizedFA(Double querymzFattyAcidMass, int modeQuerymzFattyAcidMass) {
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
        Double massToSearchForOxFA;

        if (modeQuerymzFattyAcidMass == 0) {
            massToSearchForOxFA = querymzFattyAcidMass + ConstantesForOxidation.PROTON_WEIGHT;
        } else {
            massToSearchForOxFA = querymzFattyAcidMass + ConstantesForOxidation.H_WEIGHT;
        }
// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String aliasTable = "nc";
        String startQuery = "SELECT " + aliasTable + " FROM NewCompounds " + aliasTable;
        String finalQuery;
        startQuery = startQuery + " INNER JOIN NewCompoundsInHouse ncih on nc.compoundId=ncih.compoundId ";
        startQuery = startQuery + " INNER JOIN NewLMClassification nlmc on nc.compoundId=nlmc.compoundId ";
        startQuery = startQuery + " WHERE (";
        startQuery = startQuery + "ncih.inHouseId is not null";
        startQuery = startQuery + ") and ";

        // Stablish the classification of Fatty Acids
        startQuery = startQuery + "nlmc.subClass in " + ConstantesForOxidation.FATTY_ACIDS_LM_CLASSIFICATION + " and ";

        FACompoundFactory FACompoundFactory = new FACompoundFactory();

        // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);
        finalQuery = QueryConstructor.addFilterMassesJPA(aliasTable, startQuery, massToSearchForOxFA,
                toleranceModeForFA, toleranceForFA);

// Insert compounds
        Query q1;
        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
        List results;
        results = q1.getResultList();

        if (results.isEmpty()) {
            FAcompound = null;
        } else {
            FAcompound = getFirstFACompoundFromQuery(results,
                    FACompoundFactory, querymzFattyAcidMass, massToSearchForOxFA, "");
        }
        return FAcompound;
    }

}
