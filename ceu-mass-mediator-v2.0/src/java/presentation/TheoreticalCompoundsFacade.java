package presentation;

import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import persistence.Compounds;
import persistence.NewCompounds;
import persistence.NewCompoundsGen;
import persistence.adapterFactories.NewCompoundsAdapterFactory;
import persistence.adapterFactories.NewCompoundsGenAdapterFactory;
import persistence.adapterFactories.TheoreticalCompoundFactory;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.CompoundsGroupByMass;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import utilities.TheoreticalCompoundsComparer;
import utilities.AdductsLists;
import utilities.Constantes;
import static utilities.Constantes.ADDUCT_AUTOMATIC_DETECTION_WINDOW;

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
    private EntityManager em;

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
                    adductAutoDetected,
                    isSignificative);
            // Add the chemical Alphabet
            tcl.add(compound);
            compoundGroup.addCompound(compound);
        }
    }

    private static Double getDoubleChargeOriginalMass(double inputMass, String massesMode) {
        double result = inputMass;
        if (massesMode.equals("mz")) {
            result = result - 1.0072;
            result = result * 2;
        } else if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED)) {
            // It should have detect the double charge in function of composite Spectrum
            // If composite Spectrum has wrong values, search as usual
            // Then, if the compositeSpectrum has errors, compounds are treated by default
            result = result - 1.0072;
            result = result * 2;
        }

        return result;
    }

    /**
     * returns a list of compounds from the databases according to the number of
     * the mass and the tolerance .
     *
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses , List of masses
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
            int[] range,
            List<Double> masses,
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
                    range,
                    masses,
                    tolerance,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases);
        } else {
            theoreticalCompoundList = findRangeSimple(
                    range,
                    masses,
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
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses , List of masses
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
            int[] range,
            List<Double> masses,
            Double tolerance,
            String ionMode,
            String massesMode,
            List<String> adducts,
            List<TheoreticalCompoundsGroup> listCompoundsGroup,
            List<String> databases,
            String metabolitesType) {
        List<TheoreticalCompounds> theoreticalCompoundList = new LinkedList<TheoreticalCompounds>();
        Map<String, String> provisionalMap;
        System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
        boolean searchInMINE = databases.contains("All") || databases.contains("MINE (Only In Silico Compounds)");
        //System.out.println("Adducts " + adducts);
        //System.out.println("QUERY MASSES -> " + masses);
        //System.out.println("Databases " + databases);

        // Choose the adducts to perform the search
        provisionalMap = chooseprovisionalMap(massesMode, ionMode, adducts);
        adducts = chooseAdducts(massesMode, ionMode, provisionalMap, adducts);

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
        System.out.println("\nrange[0] \n" + range[0]);
         */
        // range[0] is the index of the first mass (0) and range[1] is the index of the last mass
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

        TheoreticalCompoundFactory tcf = new NewCompoundsAdapterFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundsGenAdapterFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = range[0]; i <= range[1]; i++) {
            Double inputMass = masses.get(i);

            //System.out.println("\n Compound: " + inputMass + " added simple charged");
            for (String s : adducts) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s);
                Double adduct;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(s);
                //System.out.println("ADDUCT: " + s);
                if (sValueAdduct.equals("DoubleCharged")) {
                    massToSearch = getDoubleChargeOriginalMass(inputMass, massesMode);
                } else if (s.contains("2M")) {
                    adduct = Double.parseDouble(sValueAdduct);
                    massToSearch = (inputMass + adduct) / 2;

                } else {
                    adduct = Double.parseDouble(sValueAdduct);
                    massToSearch = inputMass + adduct;
                }
                // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                Double delta = massToSearch * tolerance * 0.000001f;
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
                            null, inputMass, 0d, massToSearch, s, "", true);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            q1.getResultList(),
                            inputMass,
                            0d,
                            tcf,
                            massToSearch,
                            s,
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
                                null, inputMass, 0d, massToSearch, s, "", true);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                q1forGen.getResultList(), inputMass, 0d,
                                tcfForGen, massToSearch, s, "", compoundGroup, true);
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
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses List of masses
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeGeneratedSimple(
            int[] range,
            List<Double> masses,
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
        provisionalMap = chooseprovisionalMap(massesMode, ionMode, adducts);
        adducts = chooseAdducts(massesMode, ionMode, provisionalMap, adducts);

        /*System.out.println("\nadducts\n" + adducts);
        System.out.println("\nrange[0] \n" + range[0]);
         */
        // range[0] is the index of the first mass (0) and range[1] is the index of the last mass
        List results;
        Double massToSearch;

// Create the common part of the query (Databases, Chemical Alphabet. Only the mass changes.
        String startQuery = "SELECT ncg FROM NewCompoundsGen ncg WHERE ";
        String finalQuery;

        TheoreticalCompoundFactory tcfForGen = new NewCompoundsGenAdapterFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
        for (int i = range[0]; i <= range[1]; i++) {
            Double inputMass = masses.get(i);
            //System.out.println("\n Compound: " + inputMass + " added simple charged");
            for (String s : adducts) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                    listToBeOrderedForGen = new ArrayList<TheoreticalCompounds>();
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s);
                Double adduct = 0.0d;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(s);
                if (sValueAdduct.equals("DoubleCharged")) {
                    massToSearch = getDoubleChargeOriginalMass(masses.get(i), massesMode);

                } else if (sValueAdduct.contains("2M")) {
                    adduct = Double.parseDouble(sValueAdduct);
                    massToSearch = (inputMass + adduct) / 2;
                } else {
                    adduct = Double.parseDouble(sValueAdduct);
                    massToSearch = inputMass + adduct;
                }
                // System.out.println("\n \n adduct as parameter: " + adduct + " as string: " + s);

                Double delta = massToSearch * tolerance * 0.000001f;
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
                            null, inputMass, 0d, massToSearch, s, "", true);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            q1.getResultList(), inputMass, 0d,
                            tcfForGen, massToSearch, s, "", compoundGroup, true);
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
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses , List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectrum List of Composite Spectrums of every compound
     * @param isSignificativeCompound List of Significative Compounds
     * @param chemAlphabet
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
            int[] range,
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectrum,
            List<Boolean> isSignificativeCompound,
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
                    range,
                    masses,
                    retentionTimes,
                    compositeSpectrum,
                    isSignificativeCompound,
                    tolerance,
                    chemAlphabet,
                    ionMode,
                    massesMode,
                    adducts,
                    listCompoundsGroup,
                    databases);
        } else {
            theoreticalCompoundList = findRangeAdvanced(
                    range,
                    masses,
                    retentionTimes,
                    compositeSpectrum,
                    isSignificativeCompound,
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
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses , List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectrum List of Composite Spectrums of every compound
     * @param isSignificativeCompound List of Significative Compounds
     * @param chemAlphabet
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
            int[] range,
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectrum,
            List<Boolean> isSignificativeCompound,
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
        System.out.println("\nION MODE " + ionMode + " masses Mode: " + massesMode + "\n");
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
        provisionalMap = chooseprovisionalMap(massesMode, ionMode, adducts);
        adducts = chooseAdducts(massesMode, ionMode, provisionalMap, adducts);

        /*
        System.out.println("PROVISIONALMAP IN MAIN METHOD: " + provisionalMap);
        System.out.println("ADDUCTS IN MAIN METHOD: " + adducts);
        System.out.println("\nrange[0] \n" + range[0]);
         */
        // range[0] is the index of the first mass and range[1] is the index of the last mass
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

        TheoreticalCompoundFactory tcf = new NewCompoundsAdapterFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundsGenAdapterFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = range[0]; i <= range[1]; i++) {
            Double inputMass = masses.get(i);
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Integer> inputCompositeSpectrum = compositeSpectrum.get(i);
            Double adduct;
            String sValueAdduct;
            String detectedAdduct = detectAdductBasedOnCompositeSpectrum(massesMode, ionMode, inputMass, adducts, inputCompositeSpectrum);
            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// Check if adduct is detected based on the composite spectrum

                //System.out.println("Automatic adduct detected: " + inputMass);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, detectedAdduct, isSignificative);
// System.out.println("\n Compound: " + inputMass + " Adduct automatically detected");
                // As we detect automatically the adduct, go directly for the inputMass

                sValueAdduct = provisionalMap.get(detectedAdduct);
                adduct = Double.parseDouble(sValueAdduct);

//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                massToSearch = inputMass + adduct;
                Double delta = massToSearch * tolerance * 0.000001f;
                Double low = massToSearch - delta;
                Double high = massToSearch + delta;

// Finish the query with the mass condition
                finalQuery = startQuery + "(nc.mass >= " + low + " and nc.mass <= " + high + ")";
                finalQuery = finalQuery + " order by ABS(nc.mass - " + massToSearch + ")";
                Query q1;
// Insert compounds
                //System.out.println("Double Charge detected: Final query: " + finalQuery);
                q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
                results = q1.getResultList();
                //System.out.println("RESULTS: " + results);
                if (results.isEmpty()) {
                    TheoreticalCompounds compound = tcf.construct(
                            null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, "", isSignificative);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {
                    addToTheoreticalCompoundList(listToBeOrdered,
                            q1.getResultList(), inputMass, inputRetentionTime,
                            tcf, massToSearch, detectedAdduct, "", compoundGroup, isSignificative);
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
                                null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {

                        addToTheoreticalCompoundList(listToBeOrdered,
                                q1forGen.getResultList(), inputMass, inputRetentionTime,
                                tcfForGen, massToSearch, detectedAdduct, "", compoundGroup, isSignificative);
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
                    compoundGroup = new CompoundsGroupByMass(inputMass, s, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    sValueAdduct = provisionalMap.get(s);
                    //System.out.println("ADDUCT: " + s);
                    if (s.equals(detectedAdduct)) {

                    } else {
                        TheoreticalCompounds compound = tcf.construct(
                                null, inputMass, inputRetentionTime, null, s, detectedAdduct, isSignificative);
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
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s, isSignificative);
                    Double adductDouble;
                    sValueAdduct = provisionalMap.get(s);
                    //System.out.println("ADDUCT: " + s);
                    if (sValueAdduct.equals("DoubleCharged")) {
                        massToSearch = getDoubleChargeOriginalMass(inputMass, massesMode);

                    } else if (s.contains("2M")) {
                        adductDouble = Double.parseDouble(sValueAdduct);
                        massToSearch = (inputMass + adductDouble) / 2;

                    } else {
                        adductDouble = Double.parseDouble(sValueAdduct);
                        massToSearch = inputMass + adductDouble;
                    }
                    // System.out.println("ADDUCT: " + s + " MASS TO SEARCH: " + massToSearch);

                    Double delta = massToSearch * tolerance * 0.000001f;
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
                                null, inputMass, inputRetentionTime, massToSearch, s, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                q1.getResultList(), inputMass, inputRetentionTime,
                                tcf, massToSearch, s, "", compoundGroup, isSignificative);

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
                                    null, inputMass, inputRetentionTime, massToSearch, s, "", isSignificative);
                            listToBeOrdered.add(compound);
                            compoundGroup.addCompound(compound);
                        } else {

                            addToTheoreticalCompoundList(listToBeOrdered,
                                    q1forGen.getResultList(), inputMass, inputRetentionTime,
                                    tcfForGen, massToSearch, s, "", compoundGroup, isSignificative);
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
     * @param range, array of integer. The first position is the starting point
     * and the second is the final point. This range is useful for paginating
     * the result list of compounds depending of the number of masses
     * @param masses List of masses
     * @param retentionTimes List of retention times of every compound
     * @param compositeSpectrum List of Composite Spectrums of every compound
     * @param isSignificativeCompound List of significative Compounds
     * @param chemAlphabet
     * @param tolerance, the tolerance which user wants to find compounds for
     * @param ionMode Ionization mode
     * @param massesMode
     * @param adducts Possible adducts
     * @param listCompoundsGroup
     * @param databases Databases to search
     * @return a List of theoreticalCompounds (SuperClass)
     */
    public List<TheoreticalCompounds> findRangeGeneratedAdvanced(
            int[] range,
            List<Double> masses,
            List<Double> retentionTimes,
            List<Map<Double, Integer>> compositeSpectrum,
            List<Boolean> isSignificativeCompound,
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
        provisionalMap = chooseprovisionalMap(massesMode, ionMode, adducts);
        adducts = chooseAdducts(massesMode, ionMode, provisionalMap, adducts);

        /*System.out.println("\nadducts\n" + adducts);
        System.out.println("\nrange[0] \n" + range[0]);
         */
        // range[0] is the index of the first mass and range[1] is the index of the last mass
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

        TheoreticalCompoundFactory tcfForGen = new NewCompoundsGenAdapterFactory();

// Create the list for ordering the compounds
        List<TheoreticalCompounds> listToBeOrdered;
//        List<TheoreticalCompounds> listToBeOrderedForGen;
        for (int i = range[0]; i <= range[1]; i++) {
            Double inputMass = masses.get(i);
            Double inputRetentionTime = retentionTimes.get(i);
            Boolean isSignificative = isSignificativeCompound.get(i);
            Map<Double, Integer> inputCompositeSpectrum = compositeSpectrum.get(i);
            String detectedAdduct = detectAdductBasedOnCompositeSpectrum(massesMode, ionMode, inputMass, adducts, inputCompositeSpectrum);
            if (!detectedAdduct.equals("")) {
                listToBeOrdered = new LinkedList<TheoreticalCompounds>();
//                listToBeOrderedForGen = new LinkedList<TheoreticalCompounds>();
// Check if the adduct is double charged.
// At this moment only one adduct possible.

                // System.out.println("Automaticall adduct detected: " + masses.get(i) + " Compos: " + inputCompositeSpectrum);
                // Create the new group of compounds for the view
                CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, detectedAdduct, isSignificative);

                Double adduct;
                String sValueAdduct;
                sValueAdduct = provisionalMap.get(detectedAdduct);

                adduct = Double.parseDouble(sValueAdduct);
//                System.out.println("\n Compound: " + inputMass + " detected automatically adduct: " + detectedAdduct
//                        + " value: " + sValueAdduct);
                massToSearch = inputMass + adduct;
                Double delta = massToSearch * tolerance * 0.000001f;
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
                            null, inputMass, inputRetentionTime, massToSearch, detectedAdduct, "", isSignificative);
                    listToBeOrdered.add(compound);
                    compoundGroup.addCompound(compound);
                } else {

                    addToTheoreticalCompoundList(listToBeOrdered,
                            q1.getResultList(), inputMass, inputRetentionTime,
                            tcfForGen, massToSearch, detectedAdduct, "", compoundGroup, isSignificative);
                }
                Collections.sort(listToBeOrdered, new TheoreticalCompoundsComparer());
                theoreticalCompoundList.addAll(listToBeOrdered);
                listCompoundsGroup.add(compoundGroup);

                for (String s : adducts) {
                    // Include all adducts if we detect a double charge for 
                    // frontend presentation
                    compoundGroup = new CompoundsGroupByMass(inputMass, s, isSignificative);
                    listToBeOrdered = new LinkedList<TheoreticalCompounds>();
                    //System.out.println("ADDUCT: " + s);
                    if (s.equals(detectedAdduct)) {

                    } else {
                        TheoreticalCompounds compound = tcfForGen.construct(
                                null, inputMass, inputRetentionTime, null, s, detectedAdduct, isSignificative);
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
                    CompoundsGroupByMass compoundGroup = new CompoundsGroupByMass(inputMass, s, isSignificative);
                    Double adduct = 0.0d;
                    String sValueAdduct;
                    sValueAdduct = provisionalMap.get(s);
                    if (sValueAdduct.equals("DoubleCharged")) {
                        massToSearch = getDoubleChargeOriginalMass(masses.get(i), massesMode);

                    } else if (sValueAdduct.contains("2M")) {
                        adduct = Double.parseDouble(sValueAdduct);
                        massToSearch = (inputMass + adduct) / 2;
                    } else {
                        adduct = Double.parseDouble(sValueAdduct);
                        massToSearch = inputMass + adduct;
                    }
                    // System.out.println("\n \n adduct as parameter: " + adduct + " as string: " + s);

                    Double delta = massToSearch * tolerance * 0.000001f;
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
                                null, inputMass, inputRetentionTime, massToSearch, s, "", isSignificative);
                        listToBeOrdered.add(compound);
                        compoundGroup.addCompound(compound);
                    } else {
                        addToTheoreticalCompoundList(listToBeOrdered,
                                q1.getResultList(), inputMass, inputRetentionTime,
                                tcfForGen, massToSearch, s, "", compoundGroup, isSignificative);
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
    private Map<String, String> chooseprovisionalMap(String massesMode, String ionMode, List<String> adducts) {
        Map<String, String> provisionalMap = null;
        switch (massesMode) {
            case "mz":
                if (ionMode.equals("positive")) {
                    provisionalMap = AdductsLists.MAPMZPOSITIVEADDUCTS;
                } else if (ionMode.equals("negative")) {
                    provisionalMap = AdductsLists.MAPMZNEGATIVEADDUCTS;
                }
                //System.out.println("MZ -> PROVISIONALMAP: " + provisionalMap);
                return provisionalMap;
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
            default:
                if (ionMode.equals("positive")) {
                    provisionalMap = AdductsLists.MAPRECALCULATEDPOSITIVEADDUCTS;
                } else if (ionMode.equals("negative")) {
                    provisionalMap = AdductsLists.MAPRECALCULATEDNEGATIVEADDUCTS;
                } else {
                    provisionalMap = AdductsLists.MAPNEUTRALADDUCTS;
                }
                //System.out.println("DEFAULT -> PROVISIONALMAP: " + provisionalMap);
                return provisionalMap;
        }
    }

    private String detectAdductBasedOnCompositeSpectrum(String massesMode,
            String ionMode,
            Double inputMass,
            List<String> adducts,
            Map<Double, Integer> compositeSpectrum) {
//        System.out.println("DETECTING ADDUCT BASED ON COMPOSITE SPECTRUM. Masses Mode: " + massesMode + " ionMode: " + ionMode);
//        System.out.println("FOR INPUT MASS: " + inputMass);
// TODO MAYBE MANAGE ISOTOPES?
        String adductDetected = "";
        Map<String, String> mapAdducts;
        Map<String, String> mapAdductsToSearchPeaks;
        double adductDouble;
        double adductDoubleForCheckRelation;
        double massToSearchInCompositeSpectrum;
        double massToSearchInCompositeSpectrumForCheckRelation;
        double differenceMassAndPeak;
        double mzCorrection = 0;
        List<String> allAdductsForCheckRelation;
        boolean search = false;
        switch (ionMode) {
            case "positive": {
                mapAdducts = AdductsLists.MAPRECALCULATEDPOSITIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<String>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPRECALCULATEDPOSITIVEADDUCTS.keySet());
                mzCorrection = AdductsLists.H_WEIGHT;
                if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED)) {
                    search = true;
                } else if (massesMode.equals("mz")) {
                    inputMass = inputMass - AdductsLists.H_WEIGHT;
                    search = true;
                } else {
                    System.out.println("MASSES MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + massesMode);
                    return adductDetected;
                }
                break;
            }
            case "negative": {
                mapAdducts = AdductsLists.MAPRECALCULATEDNEGATIVEADDUCTS;
                allAdductsForCheckRelation = new LinkedList<String>();
                allAdductsForCheckRelation.addAll(AdductsLists.MAPRECALCULATEDNEGATIVEADDUCTS.keySet());
                mzCorrection = -(AdductsLists.H_WEIGHT);
                if (massesMode.equals(Constantes.NAME_FOR_RECALCULATED)) {
                    search = true;
                } else if (massesMode.equals("mz")) {
                    inputMass = inputMass + AdductsLists.H_WEIGHT;
                    search = true;
                } else {
                    System.out.println("MASSES MODE BAD SPECIFIED DETECTING AUTOMATIC ADDUCT BASED ON "
                            + "COMPOSITE SPECTRUM: " + massesMode);
                }
                break;
            }
            default: {
                mapAdducts = null;
                allAdductsForCheckRelation = null;
            }
        }
        if (search) {
            boolean validHypothesis;
            for (String adductName : adducts) {
                // Hypothesis -> Adduct is adductName
                // There are Hypothesis not valid due to the previous statistical analysis
                // as all the dymers and adducts with double charge
                // They can apper, but with the composite spectrum it is not possible to
                // detect automatically
                String adductValue;
                adductValue = mapAdducts.get(adductName);
                if (adductName.equals("M+2H") || adductName.contains("2M")) {
                    adductDouble = 0d;
                    validHypothesis = false;
                } else {
                    adductDouble = Double.parseDouble(adductValue);
                    validHypothesis = true;
                }
//                System.out.println("HYPOTHESIS -> ADDUCT = " + adductName + " adduct value: " + adductValue);

                // Hypothesis -> Peak is adductName
                // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                // So now is time to loop the composite spectrum searching the peak
                if (validHypothesis) {
                    for (String adductNameForCheckRelation : allAdductsForCheckRelation) {
                        String adductValueForCheckRelation = mapAdducts.get(adductNameForCheckRelation);
                        if (!adductName.equals(adductNameForCheckRelation)) {
                            boolean validRelationForHypothesis;
                            if (adductName.equals("M+H") || adductName.equals("M-H")) {
                                // Only detect dymers and double charged adducts when
                                // working with hypothesis M+H
                                validRelationForHypothesis = true;
                                if (adductNameForCheckRelation.equals("M+2H")) {
                                    massToSearchInCompositeSpectrumForCheckRelation = (inputMass / 2);
                                    massToSearchInCompositeSpectrumForCheckRelation = massToSearchInCompositeSpectrumForCheckRelation + mzCorrection;
                                } else if (adductNameForCheckRelation.contains("2M+")) {
                                    adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                                    massToSearchInCompositeSpectrumForCheckRelation = (inputMass * 2) - adductDoubleForCheckRelation;
                                    massToSearchInCompositeSpectrumForCheckRelation = massToSearchInCompositeSpectrumForCheckRelation + mzCorrection;
                                } else if (adductNameForCheckRelation.contains("2M-")) {
                                    adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                                    massToSearchInCompositeSpectrumForCheckRelation = (inputMass * 2) - adductDoubleForCheckRelation;
                                    massToSearchInCompositeSpectrumForCheckRelation = massToSearchInCompositeSpectrumForCheckRelation + mzCorrection;
                                } else {
                                    adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                                    massToSearchInCompositeSpectrumForCheckRelation = inputMass - adductDoubleForCheckRelation;
                                    massToSearchInCompositeSpectrumForCheckRelation = massToSearchInCompositeSpectrumForCheckRelation + mzCorrection;
                                }
                            } else if (adductNameForCheckRelation.equals("M-H")
                                    || adductNameForCheckRelation.equals("M+2H")
                                    || adductNameForCheckRelation.contains("2M")) {
                                validRelationForHypothesis = false;
                                massToSearchInCompositeSpectrumForCheckRelation = 0d;
                            } else {
                                validRelationForHypothesis = true;
                                adductDoubleForCheckRelation = Double.parseDouble(adductValueForCheckRelation);
                                massToSearchInCompositeSpectrumForCheckRelation = inputMass - adductDoubleForCheckRelation + adductDouble;
                                massToSearchInCompositeSpectrumForCheckRelation = massToSearchInCompositeSpectrumForCheckRelation + mzCorrection;
                            }
                            // Peak to search in Composite Spectrum is now in massToSearchInCompositeSpectrum
                            // So now is time to loop the composite spectrum searching the peak
                            if (validRelationForHypothesis) {
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
        Map<String, String> provisionalMap = chooseprovisionalMap(massesMode, ionMode, adducts);
        adducts = chooseAdducts(massesMode, ionMode, provisionalMap, adducts);

        String adductDetected = detectAdductBasedOnCompositeSpectrum(massesMode,
                ionMode, inputMass, adducts, compositeSpectrum);
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
    private List<String> chooseAdducts(String massesMode, String ionMode, Map<String, String> provisionalMap, List<String> adducts) {
        switch (massesMode) {
            case "mz":
                if (ionMode.equals("positive")) {
                    if (adducts.isEmpty() || adducts.get(0).equals("allPositives")) {
                        Set set = provisionalMap.keySet();
                        adducts = new LinkedList<String>(set);
                    }
                }
                if (ionMode.equals("negative")) {
                    if (adducts.isEmpty() || adducts.get(0).equals("allNegatives")) {
                        Set set = provisionalMap.keySet();
                        adducts = new LinkedList<String>(set);
                    }
                }
                //System.out.println("MZ -> ADDUCTS: " + adducts);
                return adducts;
            case Constantes.NAME_FOR_RECALCULATED:
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
            default:
                if (adducts.isEmpty() || adducts.get(0).equals("allNeutral")) {
                    Set set = provisionalMap.keySet();
                    adducts = new LinkedList<String>(set);
                }
                //System.out.println("DEFAULT -> ADDUCTS: " + adducts);
                return adducts;
        }
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
        System.out.println("\nrange[0] \n" + range[0]);
         */
        // range[0] is the index of the first mass (0) and range[1] is the index of the last mass
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

        TheoreticalCompoundFactory tcf = new NewCompoundsAdapterFactory();
        TheoreticalCompoundFactory tcfForGen = new NewCompoundsGenAdapterFactory();

        List<TheoreticalCompounds> listToBeOrdered;
        listToBeOrdered = new LinkedList<TheoreticalCompounds>();
        CompoundsGroupByMass compoundGroup;
        compoundGroup = new CompoundsGroupByMass(0d, "", true);
        //System.out.println("Final query: " + finalQuery);
        q1 = getEntityManager().createQuery(finalQuery, NewCompounds.class);
        q1.setMaxResults(500);
        results = q1.getResultList();

        if (results.isEmpty()) {
            TheoreticalCompounds compound = tcf.construct(
                    null, 0d, 0d, 0d, "", "", true);
            listToBeOrdered.add(compound);
            compoundGroup.addCompound(compound);
        } else {
            addToTheoreticalCompoundList(listToBeOrdered,
                    q1.getResultList(), 0d, 0d,
                    tcf, 0d, "", "", compoundGroup, true);
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
                        null, 0d, 0d, 0d, "", "", true);
                listToBeOrdered.add(compound);
                compoundGroup.addCompound(compound);
            } else {

                addToTheoreticalCompoundList(listToBeOrdered,
                        q1forGen.getResultList(), 0d, 0d,
                        tcfForGen, 0d, "", "", compoundGroup, true);
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
}
