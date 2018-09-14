package services.rest.api;

import LCMS.CompoundLCMS;
import LCMS.CompoundsLCMSGroupByAdduct;
import LCMS.Experiment;
import LCMS.Feature;
import com.google.gson.Gson;
import facades.MSFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import pathway.Pathway;
import persistence.NewPathways;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import utilities.*;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class APIServices {

    Gson gson;

    @EJB
    private facades.TheoreticalCompoundsFacade ejbFacade;

    public APIServices() {
        gson = new Gson();
    }

    @Path("/test")
    @GET
    public String getTest() {
        return "{\"debug\": \"on\"}";
    }

    @Path("/simplesearch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getSimpleSearch(String simplequery) {
        System.out.println("Simple query: " + simplequery);
        SimpleSearchQuery query = gson.fromJson(simplequery, SimpleSearchQuery.class);

        List<Double> queryMasses = new ArrayList<Double>();
        queryMasses.add(query.getMass());

        String toleranceMode = query.getTolerance_mode().toString();

        Double tolerance = query.getTolerance();

        String ionMode = query.getIon_mode().toString();

        String massesMode = query.getMasses_mode().toString();

        List<String> adducts = new LinkedList<String>();
        for (Adducts pa : query.getAdducts()) {
            adducts.add(pa.toString());
        }

        List<TheoreticalCompoundsGroup> itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();

        List<String> databases = new LinkedList<String>();

        for (Database database : query.getDatabases()) {
            databases.add(database.toString());
        }
        //databases.add("AllWM");

        String metabolitesType = query.getMetabolites_type().toString();

        System.out.println("QueryMasses: " + queryMasses);
        System.out.println("InputModeTolerance: " + toleranceMode);
        System.out.println("InputTolerance: " + tolerance);
        System.out.println("ionMode: " + ionMode);
        System.out.println("massesMode: " + massesMode);
        System.out.println("adducts: " + adducts);
        System.out.println("itemsGrouped: " + itemsGrouped);
        System.out.println("databases: " + databases);
        System.out.println("metabolitesType: " + metabolitesType);
        List<TheoreticalCompounds> compounds = ejbFacade.findCompoundsSimple(
                queryMasses,
                toleranceMode,
                tolerance,
                DataFromInterfacesUtilities.ionizationModeToInteger(ionMode),
                massesMode,
                adducts,
                itemsGrouped,
                databases,
                metabolitesType
        );
        System.out.println("######### Processing simple search #########");
        System.out.println("## --> Num. Compounds: " + compounds.size());
        System.out.println("## Expected: 92 ");

        //TODO: Llamada a las reglas de ionizado -> Se obvia de momento porque no sera necesario con la nueva version de EXPERIMENTO.
        SimpleSearchResults ssr = new SimpleSearchResults();

        if (!compounds.isEmpty()) {
            for (TheoreticalCompounds compound : compounds) {
                List<PathWay> pathways = new ArrayList();
                for (NewPathways path : compound.getPathways()) {
                    PathWay pathway = new PathWay(path.getPathwayName(), path.obtainPathwayWebPage());
                }
                Compound ssrcompound
                        = new Compound(
                                compound.getCompoundId(),
                                compound.getExperimentalMass(),
                                compound.getName(),
                                compound.getFormula(),
                                compound.getAdduct(),
                                compound.getMolecularWeight(),
                                compound.getPPMIncrement(),
                                compound.getIonizationScore(),
                                compound.getFinalScore(),
                                compound.getCasId(),
                                compound.getKeggCompound(),
                                compound.getKeggWebPage(),
                                compound.getHMDBCompound(),
                                compound.getHMDBWebPage(),
                                compound.getLMCompound(),
                                compound.getLMWebPage(),
                                compound.getMetlinCompound(),
                                compound.getMetlinWebPage(),
                                compound.getPCCompound(),
                                compound.getPCWebPage(),
                                pathways);
                ssr.addCompound(ssrcompound);
            }
        }
        return gson.toJson(ssr);
    }

    @Path("/advancedsearch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getAdvancedSearch(String advancedquery) {
        System.out.println("Advanced query: " + advancedquery);
        AdvancedSearchQuery query = gson.fromJson(advancedquery, AdvancedSearchQuery.class);

        List<Double> queryMasses = new ArrayList<Double>();
        queryMasses.add(query.getMass());
        List<Double> queryRetentionTimes = new ArrayList<Double>();
        queryRetentionTimes.add(query.getRetention_time());
        List<Map<Double, Integer>> compositeSpectra = new ArrayList<Map<Double, Integer>>();
        Map<Double, Integer> mapCSpectrum = new TreeMap<Double, Integer>();
        List<Spectrum> querySpectra = query.getComposite_spectrum();
        for (Spectrum spec : querySpectra) {
            mapCSpectrum.put(spec.getMz(), (int) spec.getIntensity());
        }
        compositeSpectra.add(mapCSpectrum);

        // Only one mass, retention time and spectra: it's significative
        List<Boolean> isSignificativeCompound = new ArrayList<Boolean>();
        isSignificativeCompound.add(Boolean.TRUE);

        String toleranceMode = query.getTolerance_mode().toString();
        Double tolerance = query.getTolerance();
        String chemAlphabet = query.getChemical_alphabet().toString();
        if (query.getDeuterium()) {
            chemAlphabet = chemAlphabet + "D";
        }
        String ionMode = query.getIon_mode().toString();
        String massesMode = query.getMasses_mode().toString();
        List<String> adducts = new LinkedList<String>();
        for (Adducts pa : query.getAdducts()) {
            adducts.add(pa.toString());
        }
        List<TheoreticalCompoundsGroup> itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        List<String> databases = new LinkedList<String>();
        for (Database database : query.getDatabases()) {
            databases.add(database.toString());
        }
        String metabolitesType = query.getMetabolites_type().toString();

        System.out.println("QueryMasses: " + queryMasses);
        System.out.println("QueryRetentionTimes: " + queryRetentionTimes);
        System.out.println("CompositeSpectra: " + compositeSpectra);
        System.out.println("IsSignificativeCompound: " + isSignificativeCompound);
        System.out.println("InputModeTolerance: " + toleranceMode);
        System.out.println("InputTolerance: " + tolerance);
        System.out.println("ChemAlphabet: " + chemAlphabet);
        System.out.println("ionMode: " + ionMode);
        System.out.println("massesMode: " + massesMode);
        System.out.println("adducts: " + adducts);
        System.out.println("itemsGrouped: " + itemsGrouped);
        System.out.println("databases: " + databases);
        System.out.println("metabolitesType: " + metabolitesType);

        List<TheoreticalCompounds> compounds = ejbFacade.findCompoundsAdvanced(
                queryMasses,
                queryRetentionTimes,
                compositeSpectra,
                isSignificativeCompound,
                toleranceMode,
                tolerance,
                chemAlphabet,
                DataFromInterfacesUtilities.ionizationModeToInteger(ionMode),
                massesMode,
                adducts,
                itemsGrouped,
                databases,
                metabolitesType);
        System.out.println("######### Processing simple search #########");
        System.out.println("## --> Num. Compounds: " + compounds.size());
        System.out.println("## Expected: 76 ");

//        // PARTE DE REGLAS, NO FUNCIONA
//        // PASAR A EXPERIMENT
//        ConfigFilter configFilter = new ConfigFilter();
//        configFilter.setModifier(query.getModifiers_type().toString());
//        configFilter.setIonMode(query.getIon_mode().toString());
//        configFilter.setAllCompounds(true);
//        // Execute rules.
//        RuleProcessor.processRulesTC(compounds, configFilter);
        //TODO: Llamada a las reglas de ionizado -> Se obvia de momento porque no sera necesario con la nueva version de EXPERIMENTO.
        AdvancedSearchResults asr = new AdvancedSearchResults();

        if (!compounds.isEmpty()) {
            for (TheoreticalCompounds compound : compounds) {
                List<PathWay> pathways = new ArrayList();
                for (NewPathways path : compound.getPathways()) {
                    PathWay pathway = new PathWay(path.getPathwayName(), path.obtainPathwayWebPage());
                }
                AdvancedCompound asrcompound
                        = new AdvancedCompound(
                                compound.getCompoundId(),
                                compound.getExperimentalMass(),
                                compound.getRetentionTime(),
                                compound.getName(),
                                compound.getFormula(),
                                compound.getAdduct(),
                                compound.getMolecularWeight(),
                                compound.getPPMIncrement(),
                                compound.getIonizationScore(),
                                compound.getAdductRelationScore(),
                                compound.getRetentionTimeScore(),
                                compound.getFinalScore(),
                                compound.getCasId(),
                                compound.getKeggCompound(),
                                compound.getKeggWebPage(),
                                compound.getHMDBCompound(),
                                compound.getHMDBWebPage(),
                                compound.getLMCompound(),
                                compound.getLMWebPage(),
                                compound.getMetlinCompound(),
                                compound.getMetlinWebPage(),
                                compound.getPCCompound(),
                                compound.getPCWebPage(),
                                pathways);
                asr.addCompound(asrcompound);
            }
        }
        return gson.toJson(asr);
    }

    @Path("/advancedbatch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getAdvancedBatch(String advancedbatchquery) {
        System.out.println("Advanced query: " + advancedbatchquery);
        AdvancedBatchQuery query = gson.fromJson(advancedbatchquery, AdvancedBatchQuery.class);

        // Add significative compounds
        List<Double> queryMasses = query.getMasses();

        List<Double> queryRetentionTimes = query.getRetention_times();
        List<Map<Double, Integer>> compositeSpectra = new ArrayList<Map<Double, Integer>>();
        List<Boolean> isSignificativeCompound = new ArrayList<Boolean>();

        List<List<Spectrum>> querySpectra = query.getComposite_spectra();
        for (List<Spectrum> list : querySpectra) {
            Map<Double, Integer> mapCspectrum = new TreeMap<Double, Integer>();
            for (Spectrum spec : list) {
                mapCspectrum.put(spec.getMz(), (int) spec.getIntensity());
            }
            compositeSpectra.add(mapCspectrum);
            isSignificativeCompound.add(Boolean.TRUE);
        }

        // Add non-significative compounds
        queryMasses.addAll(query.getAll_masses());
        queryRetentionTimes.addAll(query.getAll_retention_times());
        List<List<Spectrum>> queryAllSpectra = query.getAll_composite_spectra();
        for (List<Spectrum> list : queryAllSpectra) {
            Map<Double, Integer> mapCSpectrum = new TreeMap<Double, Integer>();
            for (Spectrum spec : list) {
                mapCSpectrum.put(spec.getMz(), (int) spec.getIntensity());
            }
            compositeSpectra.add(mapCSpectrum);
            isSignificativeCompound.add(Boolean.FALSE);
        }

        String toleranceMode = query.getTolerance_mode().toString();
        Double tolerance = query.getTolerance();
        String chemAlphabet = query.getChemical_alphabet().toString();
        if (query.getDeuterium()) {
            chemAlphabet = chemAlphabet + "D";
        }
        String ionMode = query.getIon_mode().toString();
        String massesMode = query.getMasses_mode().toString();
        List<String> adducts = new LinkedList<String>();
        for (Adducts pa : query.getAdducts()) {
            adducts.add(pa.toString());
        }
        List<TheoreticalCompoundsGroup> itemsGrouped = new LinkedList<TheoreticalCompoundsGroup>();
        List<String> databases = new LinkedList<String>();
        for (Database database : query.getDatabases()) {
            databases.add(database.toString());
        }
        String metabolitesType = query.getMetabolites_type().toString();

        System.out.println("QueryMasses: " + queryMasses);
        System.out.println("QueryRetentionTimes: " + queryRetentionTimes);
        System.out.println("CompositeSpectra: " + compositeSpectra);
        System.out.println("IsSignificativeCompound: " + isSignificativeCompound);
        System.out.println("InputModeTolerance: " + toleranceMode);
        System.out.println("InputTolerance: " + tolerance);
        System.out.println("ChemAlphabet: " + chemAlphabet);
        System.out.println("ionMode: " + ionMode);
        System.out.println("massesMode: " + massesMode);
        System.out.println("adducts: " + adducts);
        System.out.println("itemsGrouped: " + itemsGrouped);
        System.out.println("databases: " + databases);
        System.out.println("metabolitesType: " + metabolitesType);

        List<TheoreticalCompounds> compounds = ejbFacade.findCompoundsAdvanced(
                queryMasses,
                queryRetentionTimes,
                compositeSpectra,
                isSignificativeCompound,
                toleranceMode,
                tolerance,
                chemAlphabet,
                DataFromInterfacesUtilities.ionizationModeToInteger(ionMode),
                massesMode,
                adducts,
                itemsGrouped,
                databases,
                metabolitesType);
        System.out.println("######### Processing simple search #########");
        System.out.println("## --> Num. Compounds: " + compounds.size());
        System.out.println("## Expected: 1146 ");

//        // PARTE DE REGLAS, NO FUNCIONA
//        // PASAR A EXPERIMENT
//        ConfigFilter configFilter = new ConfigFilter();
//        configFilter.setModifier(query.getModifiers_type().toString());
//        configFilter.setIonMode(query.getIon_mode().toString());
//        configFilter.setAllCompounds(true);
//        // Execute rules.
//        RuleProcessor.processRulesTC(compounds, configFilter);
        //TODO: Llamada a las reglas de ionizado -> Se obvia de momento porque no sera necesario con la nueva version de EXPERIMENTO.
        AdvancedSearchResults asr = new AdvancedSearchResults();

        if (!compounds.isEmpty()) {
            for (TheoreticalCompounds compound : compounds) {
                List<PathWay> pathways = new ArrayList();
                for (NewPathways path : compound.getPathways()) {
                    PathWay pathway = new PathWay(path.getPathwayName(), path.obtainPathwayWebPage());
                }
                AdvancedCompound asrcompound
                        = new AdvancedCompound(
                                compound.getCompoundId(),
                                compound.getExperimentalMass(),
                                compound.getRetentionTime(),
                                compound.getName(),
                                compound.getFormula(),
                                compound.getAdduct(),
                                compound.getMolecularWeight(),
                                compound.getPPMIncrement(),
                                compound.getIonizationScore(),
                                compound.getAdductRelationScore(),
                                compound.getRetentionTimeScore(),
                                compound.getFinalScore(),
                                compound.getCasId(),
                                compound.getKeggCompound(),
                                compound.getKeggWebPage(),
                                compound.getHMDBCompound(),
                                compound.getHMDBWebPage(),
                                compound.getLMCompound(),
                                compound.getLMWebPage(),
                                compound.getMetlinCompound(),
                                compound.getMetlinWebPage(),
                                compound.getPCCompound(),
                                compound.getPCWebPage(),
                                pathways);
                asr.addCompound(asrcompound);
            }
        }
        return gson.toJson(asr);
    }

    @Path("/v3/advancedbatch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getAdvancedBatchExperimentImpl(String advancedbatchquery) {
        System.out.println("Advanced Batch Query: " + advancedbatchquery);
        AdvancedBatchQuery query = gson.fromJson(advancedbatchquery, AdvancedBatchQuery.class);

        //SIGNIFICATIVE COMPOUNDS
        //Masses Mode
        String masses_mode = query.getMasses_mode().toString();

        //Ion Mode
        String ion_mode = query.getIon_mode().toString();

        // Modifiers type
        String modifiers_type = query.getModifiers_type().toString();

        // Tolerance
        Double tolerance = query.getTolerance();

        // Tolerance Mode
        String tolerance_mode = query.getTolerance_mode().toString();

        // Databases
        List<String> databases = new LinkedList<String>();
        for (Database db : query.getDatabases()) {
            databases.add(db.toString());
        }

        // Adducts
        List<String> adducts = new LinkedList<String>();
        for (Adducts posAdduct : query.getAdducts()) {
            adducts.add(posAdduct.toString());
        }

        // Metabolites Type
        String metabolites_type = query.getMetabolites_type().toString();

        // Chemical Alphabet
        String chemical_alphabet = query.getChemical_alphabet().toString();

        // Create features from input data
        List<String> adductsFiltered = AdductProcessing.FilterAdductsFromInterface(adducts,
                DataFromInterfacesUtilities.ionizationModeToInteger(ion_mode));

        // Rewrite values according to Database integers.
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(chemical_alphabet);
        List<Integer> databasesInt = DataFromInterfacesUtilities.getDatabasesAsInt(databases);
        int ionizationModeInt = DataFromInterfacesUtilities.ionizationModeToInteger(ion_mode);
        int massesModeInt = DataFromInterfacesUtilities.inputMassModeToInteger(masses_mode);
        Integer metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(metabolites_type);
        int modifiersInt = DataFromInterfacesUtilities.modifierToInteger(modifiers_type);
        Integer toleranceModeInt = DataFromInterfacesUtilities.toleranceTypeToInteger(tolerance_mode);

        // InputMasses (Experimental masses)
        List<Double> significantMasses = query.getMasses();
        List<Double> allMasses = query.getAll_masses();
        List<Feature> significantFeatures;
        List<Feature> allFeatures = null;
        boolean isAllFeatures = false;
        List<Double> massesMZFromNeutral = new LinkedList<Double>();
        for (double mass : significantMasses) {
            massesMZFromNeutral.add(
                    utilities.Utilities.calculateMZFromNeutralMass(
                            mass,
                            massesModeInt,
                            ionizationModeInt)
            );
        }

        // Retention times
        List<Double> retentionTimes = query.getRetention_times();

        // Composite Spectra
        List<Map<Double, Integer>> compositeSpectra = new ArrayList<Map<Double, Integer>>();
        for (List<Spectrum> list : query.getComposite_spectra()) {
            Map<Double, Integer> mapCSpectrum = new TreeMap<Double, Integer>();
            for (Spectrum spec : list) {
                mapCSpectrum.put(spec.getMz(), (int) spec.getIntensity());
            }
            compositeSpectra.add(mapCSpectrum);
        }

        // Load Significant Features
        significantFeatures = FeaturesRTProcessing.loadFeatures(
                massesMZFromNeutral,
                retentionTimes,
                compositeSpectra,
                true,
                massesModeInt,
                ionizationModeInt,
                adductsFiltered,
                tolerance,
                toleranceModeInt,
                databasesInt,
                metabolitesTypeInt,
                chemAlphabetInt,
                new MSFacade()
        );
        if (allMasses != null && !allMasses.isEmpty()) {
            isAllFeatures = true;
            // All Experimental Masses
            List<Double> allMassesMZFromNeutral = new LinkedList<Double>();
            for (double mass : query.getAll_masses()) {
                allMassesMZFromNeutral.add(
                        utilities.Utilities.calculateMZFromNeutralMass(
                                mass,
                                massesModeInt,
                                ionizationModeInt)
                );
            }

            // All Retention times
            List<Double> allRetentionTimes = query.getAll_retention_times();

            // All Composite Spectra
            List<Map<Double, Integer>> allCompositeSpectra = new ArrayList<Map<Double, Integer>>();
            for (List<Spectrum> list : query.getAll_composite_spectra()) {
                Map<Double, Integer> mapCSpectrumall = new TreeMap<Double, Integer>();
                for (Spectrum spec : list) {
                    mapCSpectrumall.put(spec.getMz(), (int) spec.getIntensity());
                }
                allCompositeSpectra.add(mapCSpectrumall);
            }

            // Load NON Significant Features
            allFeatures = FeaturesRTProcessing.loadFeatures(
                    allMassesMZFromNeutral,
                    allRetentionTimes,
                    allCompositeSpectra,
                    true,
                    massesModeInt,
                    ionizationModeInt,
                    adductsFiltered,
                    tolerance,
                    toleranceModeInt,
                    databasesInt,
                    metabolitesTypeInt,
                    chemAlphabetInt,
                    new MSFacade());

            // Set significant features
            FeaturesRTProcessing.setSignificantFeatures(significantFeatures, allFeatures);
        }

        if (allFeatures == null) {
            // If allFeatures is null, then there were not non significant compounds in the query
            allFeatures = significantFeatures;
            isAllFeatures = false;
        }

        // Create experiment with Features
        Experiment experiment = new Experiment(
                significantFeatures,
                allFeatures,
                isAllFeatures,
                tolerance.intValue(),
                toleranceModeInt,
                chemAlphabetInt,
                modifiersInt,
                metabolitesTypeInt,
                databasesInt,
                massesModeInt,
                ionizationModeInt,
                adducts);

        experiment.processCompoundsAdvanced();
        significantFeatures = experiment.getSignificantFeatures();
        allFeatures = experiment.getAllFeatures();

        System.out.println("Features: " + significantFeatures.size());

        AdvancedSearchResults asr = new AdvancedSearchResults();

        for (Feature feature : significantFeatures) {
            for (CompoundsLCMSGroupByAdduct compGroup : feature.getAnnotationsGroupedByAdduct()) {
                //System.out.println("EM: " + compGroup.getEM() + " RT: " + compGroup.getRT() + " NUM ANNOTATION: " +  compGroup.getNumberAnnotations());
                if (!compGroup.getCompounds().isEmpty()) {
                    for (CompoundLCMS compound : compGroup.getCompounds()) {
                        List<PathWay> pathways = new ArrayList();
                        for (Pathway path : compound.getPathways()) {
                            PathWay pathway = new PathWay(path.getPathwayName(), path.getPathwayWebPage());
                        }
                        AdvancedCompound asrcompound
                                = new AdvancedCompound(
                                        compound.getCompound_id(),
                                        compound.getEM(),
                                        compound.getRT(),
                                        compound.getCompound_name(),
                                        compound.getFormula(),
                                        compound.getAdduct(),
                                        compound.getMass(),
                                        compound.getIncrementPPM(),
                                        compound.getIonizationScore(),
                                        compound.getAdductRelationScore(),
                                        compound.getRTscore(),
                                        compound.getFinalScore(),
                                        compound.getCas_id(),
                                        compound.getKegg_id(),
                                        compound.getCompoundKeggWebPage(),
                                        compound.getHmdb_id(),
                                        compound.getCompoundHMDBWebPage(),
                                        compound.getLm_id(),
                                        compound.getCompoundLMWebPage(),
                                        compound.getMetlin_id(),
                                        compound.getCompoundMetlinWebPage(),
                                        compound.getPc_id(),
                                        compound.getCompoundPubChemWebPage(),
                                        pathways);
                        asr.addCompound(asrcompound);
                        //System.out.println("ASR: " + asrcompound);
                    }
                }
            }
        }
        //System.out.println(gson.toJson(asr));
        return gson.toJson(asr);
    }

    @Path("/v3/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getBatchExperimentImpl(String batchquery) {
        System.out.println("Simple Batch Query: " + batchquery);
        SimpleBatchQuery query = gson.fromJson(batchquery, SimpleBatchQuery.class);

        //SIGNIFICATIVE COMPOUNDS. There is only significative compounds
        //Masses Mode
        String masses_mode = query.getMasses_mode().toString();

        //Ion Mode
        String ion_mode = query.getIon_mode().toString();

        // Tolerance
        Double tolerance = query.getTolerance();

        // Tolerance Mode
        String tolerance_mode = query.getTolerance_mode().toString();

        // Databases
        List<String> databases = new LinkedList<String>();
        for (Database db : query.getDatabases()) {
            databases.add(db.toString());
        }

        // Adducts
        List<String> adducts = new LinkedList<String>();
        for (Adducts posAdduct : query.getAdducts()) {
            adducts.add(posAdduct.toString());
        }

        // Metabolites Type
        String metabolites_type = query.getMetabolites_type().toString();

        // Create features from input data
        List<String> adductsFiltered = AdductProcessing.FilterAdductsFromInterface(adducts,
                DataFromInterfacesUtilities.ionizationModeToInteger(ion_mode));

        // Rewrite values according to Database integers.
        List<Integer> databasesInt = DataFromInterfacesUtilities.getDatabasesAsInt(databases);
        int ionizationModeInt = DataFromInterfacesUtilities.ionizationModeToInteger(ion_mode);
        int massesModeInt = DataFromInterfacesUtilities.inputMassModeToInteger(masses_mode);
        Integer metabolitesTypeInt = DataFromInterfacesUtilities.metabolitesTypeToInteger(metabolites_type);
        Integer toleranceModeInt = DataFromInterfacesUtilities.toleranceTypeToInteger(tolerance_mode);

        // InputMasses (Experimental masses)
        List<Double> significantMasses = query.getMasses();
        List<Feature> significantFeatures;
        List<Feature> allFeatures = null;
        boolean isAllFeatures = false;
        List<Double> massesMZFromNeutral = new LinkedList<Double>();
        for (double mass : significantMasses) {
            massesMZFromNeutral.add(
                    utilities.Utilities.calculateMZFromNeutralMass(
                            mass,
                            massesModeInt,
                            ionizationModeInt)
            );
        }

        // Default values
        List<Double> retentionTimes = new ArrayList(Collections.nCopies(significantMasses.size(), 0.0));
        List<Map<Double, Integer>> compositeSpectra
                = new ArrayList(Collections.nCopies(significantMasses.size(), new HashMap<>()));
        int chemAlphabetInt = 4; // ALL = 4
        int modifiersInt = 0; // NONE = 0

        // Load Significant Features
        significantFeatures = FeaturesRTProcessing.loadFeatures(
                massesMZFromNeutral,
                retentionTimes,
                compositeSpectra,
                true,
                massesModeInt,
                ionizationModeInt,
                adductsFiltered,
                tolerance,
                toleranceModeInt,
                databasesInt,
                metabolitesTypeInt,
                chemAlphabetInt,
                new MSFacade()
        );

        if (allFeatures == null) {
            // If allFeatures is null, then there were not non significant compounds in the query
            allFeatures = significantFeatures;
            isAllFeatures = false;
        }

        // Create experiment with Features
        Experiment experiment = new Experiment(
                significantFeatures,
                allFeatures,
                isAllFeatures,
                tolerance.intValue(),
                toleranceModeInt,
                chemAlphabetInt,
                modifiersInt,
                metabolitesTypeInt,
                databasesInt,
                massesModeInt,
                ionizationModeInt,
                adducts);

        // I do not want the rules to be processed in batch search
        //experiment.processCompoundsSimple();
        significantFeatures = experiment.getSignificantFeatures();

        System.out.println("Features: " + significantFeatures.size());

        SimpleSearchResults ssr = new SimpleSearchResults();

        for (Feature feature : significantFeatures) {
            for (CompoundsLCMSGroupByAdduct compGroup : feature.getAnnotationsGroupedByAdduct()) {
                //System.out.println("EM: " + compGroup.getEM() + " RT: " + compGroup.getRT() + " NUM ANNOTATION: " +  compGroup.getNumberAnnotations());
                if (!compGroup.getCompounds().isEmpty()) {
                    for (CompoundLCMS compound : compGroup.getCompounds()) {
                        List<PathWay> pathways = new ArrayList();
                        for (Pathway path : compound.getPathways()) {
                            PathWay pathway = new PathWay(path.getPathwayName(), path.getPathwayWebPage());
                        }
                        Compound asrcompound
                                = new Compound(
                                        compound.getCompound_id(),
                                        compound.getEM(),
                                        compound.getCompound_name(),
                                        compound.getFormula(),
                                        compound.getAdduct(),
                                        compound.getMass(),
                                        compound.getIncrementPPM(),
                                        compound.getIonizationScore(),
                                        compound.getFinalScore(),
                                        compound.getCas_id(),
                                        compound.getKegg_id(),
                                        compound.getCompoundKeggWebPage(),
                                        compound.getHmdb_id(),
                                        compound.getCompoundHMDBWebPage(),
                                        compound.getLm_id(),
                                        compound.getCompoundLMWebPage(),
                                        compound.getMetlin_id(),
                                        compound.getCompoundMetlinWebPage(),
                                        compound.getPc_id(),
                                        compound.getCompoundPubChemWebPage(),
                                        pathways);
                        ssr.addCompound(asrcompound);
                        //System.out.println("ASR: " + asrcompound);
                    }
                }
            }
        }
        //System.out.println(gson.toJson(asr));
        return gson.toJson(ssr);
    }
}
