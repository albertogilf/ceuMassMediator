package services.rest.api;

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.CompoundsLCMSGroupByAdduct;
import LCMS_FEATURE.Experiment;
import LCMS_FEATURE.Feature;
import com.google.gson.Gson;
import facades.MSFacade;
import facades.MSMSFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import msms.MSMSCompound;
import msms.Msms;
import msms.Peak;
import msms.ScoreComparator;
import pathway.Pathway;
import persistence.NewPathways;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import services.rest.api.request.Adducts;
import services.rest.api.request.AdvancedBatchQuery;
import services.rest.api.request.AdvancedSearchQuery;
import services.rest.api.request.BrowseSearchQuery;
import services.rest.api.request.Database;
import services.rest.api.request.IonModeMS;
import services.rest.api.request.IonizationVoltage;
import services.rest.api.request.MsMsSearchQuery;
import services.rest.api.request.OxidationLongSearchQuery;
import services.rest.api.request.OxidationShortSearchQuery;
import services.rest.api.request.OxidationTypeLong;
import services.rest.api.request.OxidationTypeShort;
import services.rest.api.request.PathwayDisplayerQuery;
import services.rest.api.request.SimpleBatchQuery;
import services.rest.api.request.SimpleSearchQuery;
import services.rest.api.request.SpectraType;
import services.rest.api.request.SpectralQualityControllerQuery;
import services.rest.api.request.Spectrum;
import services.rest.api.request.ToleranceMode;
import services.rest.api.response.AdvancedCompound;
import services.rest.api.response.AdvancedSearchResults;
import services.rest.api.response.Annotation;
import services.rest.api.response.BrowseCompound;
import services.rest.api.response.BrowseSearchResults;
import services.rest.api.response.Compound;
import services.rest.api.response.MsMsSearchCompound;
import services.rest.api.response.MsMsSearchResults;
import services.rest.api.response.OxidationCompound;
import services.rest.api.response.OxidationSearchResults;
import services.rest.api.response.PathWay;
import services.rest.api.response.PathwayDisplayerResults;
import services.rest.api.response.SimpleSearchResults;
import services.rest.api.response.SpectralQualityControllerResults;
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
                                compound.getInChIKey(),
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
        List<Map<Double, Double>> compositeSpectra = new ArrayList<Map<Double, Double>>();
        Map<Double, Double> mapCSpectrum = new TreeMap<Double, Double>();
        List<Spectrum> querySpectra = query.getComposite_spectrum();
        for (Spectrum spec : querySpectra) {
            mapCSpectrum.put(spec.getMz(), (Double) spec.getIntensity());
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
                                compound.getInChIKey(),
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
        List<Map<Double, Double>> compositeSpectra = new ArrayList<Map<Double, Double>>();
        List<Boolean> isSignificativeCompound = new ArrayList<Boolean>();

        List<List<Spectrum>> querySpectra = query.getComposite_spectra();
        for (List<Spectrum> list : querySpectra) {
            Map<Double, Double> mapCspectrum = new TreeMap<Double, Double>();
            for (Spectrum spec : list) {
                mapCspectrum.put(spec.getMz(), (Double) spec.getIntensity());
            }
            compositeSpectra.add(mapCspectrum);
            isSignificativeCompound.add(Boolean.TRUE);
        }

        // Add non-significative compounds
        queryMasses.addAll(query.getAll_masses());
        queryRetentionTimes.addAll(query.getAll_retention_times());
        List<List<Spectrum>> queryAllSpectra = query.getAll_composite_spectra();
        for (List<Spectrum> list : queryAllSpectra) {
            Map<Double, Double> mapCSpectrum = new TreeMap<Double, Double>();
            for (Spectrum spec : list) {
                mapCSpectrum.put(spec.getMz(), (Double) spec.getIntensity());
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
        System.out.println("######### Processing batch advanced search search #########");
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
                                compound.getInChIKey(),
                                pathways
                        );
                asr.addCompound(asrcompound);
            }
        }
        return gson.toJson(asr);
    }

    @Path("/browsesearch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getBrowseSearch(String browsesearchquery) {
        //System.out.println("Browse query: " + browsesearchquery);
        BrowseSearchQuery query = gson.fromJson(browsesearchquery, BrowseSearchQuery.class);

        String name = query.getName().toString();
        boolean exactName = query.isExactName();
        String formula = query.getFormula().toString();
        boolean exactFormula = query.isExactFormula();
        String metabolitesType = query.getMetabolites_type().toString();
        List<String> databases = new LinkedList<String>();
        for (Database database : query.getDatabases()) {
            databases.add(database.toString());
        }
        //databases.add("AllWM");

        //System.out.println("Name: " + name);
        //System.out.println("Is exact name?: " + exactName);
        //System.out.println("Formula: " + formula);
        //System.out.println("Is exact formula?: " + exactFormula);
        //System.out.println("Metabolites Type: " + metabolitesType);
        //System.out.println("Databases: " + databases);
        // Exact Formula not used
        List<TheoreticalCompounds> compounds = ejbFacade.findCompoundsBrowseSearch(name, formula, exactName, databases, metabolitesType);
        //System.out.println("######### Processing browse search #########");
        //System.out.println("## --> Num. Compounds: " + compounds.size());
        //System.out.println("## Expected: 1 ");

        BrowseSearchResults bsr = new BrowseSearchResults();

        if (!compounds.isEmpty()) {
            for (TheoreticalCompounds compound : compounds) {
                List<PathWay> pathways = new ArrayList();
                compound.setBoolShowPathways(true);
                for (Iterator<NewPathways> iterator = compound.getPathways().iterator(); iterator.hasNext();) {
                    NewPathways path = iterator.next();
                    PathWay pathway = new PathWay(path.getPathwayName(), path.obtainPathwayWebPage());
                    pathways.add(pathway);
                }
                BrowseCompound browsecompound
                        = new BrowseCompound(
                                compound.getCompoundId(),
                                compound.getName(),
                                compound.getFormula(),
                                compound.getMolecularWeight(),
                                compound.getCasId(),
                                compound.getHMDBCompound(),
                                compound.getHMDBWebPage(),
                                compound.getMetlinCompound(),
                                compound.getMetlinWebPage(),
                                compound.getLMCompound(),
                                compound.getLMWebPage(),
                                compound.getKeggCompound(),
                                compound.getKeggWebPage(),
                                compound.getPCCompound(),
                                compound.getPCWebPage(),
                                compound.getInChIKey(),
                                pathways);
                bsr.addCompound(browsecompound);
            }
        }
        return gson.toJson(bsr);
    }

    @Path("/msmssearch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getMsMsSearch(String msmssearchquery) {
        //System.out.println("MSMS query: " + msmssearchquery);
        MsMsSearchQuery query = gson.fromJson(msmssearchquery, MsMsSearchQuery.class);

        double ion_mass = query.getIon_mass();
        List<Spectrum> peaks = query.getMs_ms_peaks();
        double ion_tolerance = query.getPrecursor_ion_tolerance();
        ToleranceMode ion_tolerance_mode = query.getPrecursor_ion_tolerance_mode();
        double mz_tolerance = query.getPrecursor_mz_tolerance();
        ToleranceMode mz_tolerance_mode = query.getPrecursor_mz_tolerance_mode();
        IonModeMS ion_mode = query.getIon_mode();
        IonizationVoltage ionization_volt = query.getIonization_voltage();
        List<SpectraType> spectra_types = query.getSpectra_types();

        //System.out.println("Precursor Ion Mass (m/z): " + ion_mass);
        //System.out.println("MS/MS Peak List: " + peaks);
        //System.out.println("Precursor Ion Tolerance: " + ion_tolerance);
        //System.out.println("Precursor Ion Tolerance Mode: " + ion_tolerance_mode);
        //System.out.println("M/Z Tolerance: " + mz_tolerance);
        //System.out.println("M/Z Tolerance Mode: " + mz_tolerance_mode);
        //System.out.println("Ionization Mode: " + ion_mode);
        //System.out.println("Ionization Voltage: " + ionization_volt);
        //System.out.println("Type of spectra: " + spectra_types);
        List<Peak> peaksList = new LinkedList<Peak>();
        for (Spectrum peak : peaks) {
            peaksList.add(new Peak(peak.getMz(), peak.getIntensity()));
        }
        int ion_mode_int = -1;
        switch (ion_mode) {
            case POSITIVE:
                ion_mode_int = 1;
                break;
            case NEGATIVE:
                ion_mode_int = 2;
                break;
            default:
                break;
        }
        int ionization_volt_v = -1;
        switch (ionization_volt) {
            case LOW:
                ionization_volt_v = 10;
                break;
            case MEDIUM:
                ionization_volt_v = 20;
                break;
            case HIGH:
                ionization_volt_v = 40;
                break;
            case ALL:
                ionization_volt_v = 0;
                break;
            default:
                break;
        }
        int spectra_type_int = -1;
        // Predicted and experimental
        if (spectra_types.size() == 2) {
            spectra_type_int = 2;
        } else if (spectra_types.get(0).equals(SpectraType.PREDICTED)) {
            spectra_type_int = 1;
        } else if (spectra_types.get(0).equals(SpectraType.EXPERIMENTAL)) {
            spectra_type_int = 0;
        }
        Msms msms = new Msms(ion_mass, ion_mode_int,
                ionization_volt_v, ionization_volt.toString(),
                peaksList, spectra_type_int);
        MSMSFacade msmsFacade = new MSMSFacade();

        // Set up Tolerance mode and use Da instead of mDa
        String ion_tolerance_mode_MS = "ppm";
        if (ion_tolerance_mode.equals(ToleranceMode.MDA)) {
            ion_tolerance_mode_MS = "Da";
            ion_tolerance = ion_tolerance / 1000;
        }
        String mz_tolerance_mode_MS = "ppm";
        if (mz_tolerance_mode.equals(ToleranceMode.MDA)) {
            mz_tolerance_mode_MS = "Da";
            mz_tolerance = mz_tolerance / 1000;
        }

        List<MSMSCompound> listCandidates = msmsFacade.getMsmsCandidates(msms, ion_tolerance, ion_tolerance_mode_MS);
        List<MSMSCompound> candidatesScored = msmsFacade.scoreMatchedPeaks(msms, listCandidates, mz_tolerance, mz_tolerance_mode_MS);
        List<MSMSCompound> itemsMSCompound = msmsFacade.getTopNMatches(candidatesScored, candidatesScored.size());
        msms.setCompounds(itemsMSCompound);
        Collections.sort(itemsMSCompound, new ScoreComparator());

        System.out.println("######### Processing MS/MS search #########");
        System.out.println("## --> Num. Compounds: " + itemsMSCompound.size());
        System.out.println("## Expected: 6 ");

        MsMsSearchResults mssr = new MsMsSearchResults();

        if (!itemsMSCompound.isEmpty()) {
            for (MSMSCompound msmscompound : itemsMSCompound) {
                MsMsSearchCompound compound = new MsMsSearchCompound(
                        msmscompound.getStringSpectraType(),
                        msmscompound.getCompound_id(),
                        msmscompound.getHMDB_ID(),
                        msmscompound.getCompound_name(),
                        msmscompound.getCompound_formula(),
                        msmscompound.getCompound_mass(),
                        msmscompound.getScore()
                );
                mssr.addCompound(compound);
            }
        }
        msmsFacade.disconnect();
        return gson.toJson(mssr);
    }

    @Path("/oxidationlong")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getOxidationLongSearch(String oxidationlongsearchquery) {
        System.out.println("Oxidation long query: " + oxidationlongsearchquery);
        OxidationLongSearchQuery query = gson.fromJson(oxidationlongsearchquery, OxidationLongSearchQuery.class);

        List<Double> fatty_acids = new LinkedList<Double>();
        fatty_acids.add(query.getFatty_acid_1_mz());
        fatty_acids.add(query.getFatty_acid_2_mz());
        double fatty_tolerance = query.getFatty_acid_tolerance();
        String fatty_tolerance_mode = query.getFatty_acid_tolerance_mode().toString();
        double precursor_mz = query.getPrecursor_mz_negative();
        double precursor_tolerance = query.getPrecursor_tolerance();
        String precursor_tolerance_mode = query.getPrecursor_tolerance_mode().toString();
        List<String> oxidations = new LinkedList<String>();
        for (OxidationTypeLong oxidation : query.getOxidations()) {
            oxidations.add(oxidation.toString());
        }

        System.out.println("Fatty acids m/z: " + fatty_acids);
        System.out.println("Tolerance for fatty acids: " + fatty_tolerance);
        System.out.println("Tolerance mode for fatty acids: " + fatty_tolerance_mode);
        System.out.println("Precursor m/z for negative mode: " + precursor_mz);
        System.out.println("Tolerance for precursor: " + precursor_tolerance);
        System.out.println("Tolerance mode for precursor: " + precursor_tolerance_mode);
        System.out.println("Possible oxidations: " + oxidations);

        List<String> databases = new LinkedList<>();
        databases.add("all");

        List<OxidizedTheoreticalCompound> compounds = ejbFacade.findLCOxidizedFA(precursor_mz,
                fatty_acids, fatty_tolerance_mode, fatty_tolerance,
                precursor_tolerance_mode, precursor_tolerance,
                2, databases, oxidations);

        System.out.println("######### Processing oxidized long search #########");
        System.out.println("## --> Num. Compounds: " + compounds.size());
        System.out.println("## Expected: 4 ");

        OxidationSearchResults osr = new OxidationSearchResults();

        if (!compounds.isEmpty()) {
            for (OxidizedTheoreticalCompound compound : compounds) {

                if (compound.getName() != null && !compound.getName().equals("")) {
                    compound.exchangeBoolShowNonOxidizedAnnotations();

                    Double mz_precursor_positive_M_H = null;
                    List<Double> neutral_losses_positive = new LinkedList<Double>();
                    if (!compound.getNeutralLossesPositiveMode().isEmpty()) {
                        mz_precursor_positive_M_H = compound.getMzPositivePI();
                        neutral_losses_positive = new LinkedList(compound.getNeutralLossesPositiveMode());
                    }
                    Double mz_precursor_negative_M_HCOO = null;
                    List<Double> neutral_losses_negative = new LinkedList<Double>();
                    if (!compound.getNeutralLossesNegativeMode().isEmpty()) {
                        mz_precursor_negative_M_HCOO = compound.getTheoreticalPIMolecularWeight();
                        neutral_losses_negative = new LinkedList(compound.getNeutralLossesNegativeMode());
                    }
                    List<Annotation> putative_annotations_oxidized = new LinkedList<Annotation>();
                    if (compound.getOxidizedCompoundsGroupByMass() != null) {
                        List<TheoreticalCompounds> compoundsList = compound.getOxidizedCompoundsGroupByMass().getListCompounds();
                        for (TheoreticalCompounds theoreticalCompounds : compoundsList) {
                            String name = theoreticalCompounds.getName();
                            String url = "";
                            if (theoreticalCompounds.getLMCompound() != null && !theoreticalCompounds.getLMCompound().equals("")) {
                                url = theoreticalCompounds.getLMWebPage();
                            }
                            putative_annotations_oxidized.add(new Annotation(name, url));
                        }
                    }
                    List<Annotation> putative_annotations_non_oxidized = new LinkedList<Annotation>();
                    if (compound.getNonOxidizedCompoundsGroupByMass() != null) {
                        List<TheoreticalCompounds> compoundsList = compound.getNonOxidizedCompoundsGroupByMass().getListCompounds();
                        for (TheoreticalCompounds theoreticalCompounds : compoundsList) {
                            String name = theoreticalCompounds.getName();
                            String url = "";
                            if (theoreticalCompounds.getLMCompound() != null && !theoreticalCompounds.getLMCompound().equals("")) {
                                url = theoreticalCompounds.getLMWebPage();
                            }
                            putative_annotations_non_oxidized.add(new Annotation(name, url));
                        }
                    }
                    OxidationCompound oxidationcompound
                            = new OxidationCompound(
                                    compound.getOxidizedFAEM(),
                                    compound.getNonOxidizedFAEM(),
                                    compound.getParentIonEM(),
                                    compound.getAdductType(),
                                    compound.getOxidationType(),
                                    compound.getName(),
                                    compound.getFormula(),
                                    compound.getTheoreticalPIMolecularWeight(),
                                    compound.getPpmIncrement(),
                                    mz_precursor_positive_M_H, neutral_losses_positive,
                                    mz_precursor_negative_M_HCOO, neutral_losses_negative,
                                    putative_annotations_oxidized,
                                    putative_annotations_non_oxidized);
                    osr.addCompound(oxidationcompound);
                }
            }
        }

        return gson.toJson(osr);
    }

    @Path("/oxidationshort")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getOxidationShortSearch(String oxidationshortsearchquery) {
        //System.out.println("Oxidation short query: " + oxidationshortsearchquery);
        OxidationShortSearchQuery query = gson.fromJson(oxidationshortsearchquery, OxidationShortSearchQuery.class);

        List<Double> fatty_acids = new LinkedList<Double>();
        fatty_acids.add(query.getFatty_acid_mz());
        double fatty_tolerance = query.getFatty_acid_tolerance();
        String fatty_tolerance_mode = query.getFatty_acid_tolerance_mode().toString();
        double precursor_mz = query.getPrecursor_mz_negative();
        double precursor_tolerance = query.getPrecursor_tolerance();
        String precursor_tolerance_mode = query.getPrecursor_tolerance_mode().toString();
        List<String> oxidations = new LinkedList<String>();
        for (OxidationTypeShort oxidation : query.getOxidations()) {
            oxidations.add(oxidation.toString());
        }

        //System.out.println("Non oxidized fatty acid m/z: " + fatty_acids);
        //System.out.println("Tolerance for fatty acids: " + fatty_tolerance);
        //System.out.println("Tolerance mode for fatty acids: " + fatty_tolerance_mode);
        //System.out.println("Precursor m/z for negative mode: " + precursor_mz);
        //System.out.println("Tolerance for precursor: " + precursor_tolerance);
        //System.out.println("Tolerance mode for precursor: " + precursor_tolerance_mode);
        //System.out.println("Possible oxidations: " + oxidations);
        List<String> databases = new LinkedList<>();
        databases.add("all");

        List<OxidizedTheoreticalCompound> compounds = ejbFacade.findSCOxidizedFA(precursor_mz,
                fatty_acids, fatty_tolerance_mode, fatty_tolerance,
                precursor_tolerance_mode, precursor_tolerance,
                2, databases, oxidations);

        //System.out.println("######### Processing oxidized short search #########");
        //System.out.println("## --> Num. Compounds: " + compounds.size());
        //System.out.println("## Expected: 4 ");
        OxidationSearchResults osr = new OxidationSearchResults();

        if (!compounds.isEmpty()) {
            for (OxidizedTheoreticalCompound compound : compounds) {

                if (compound.getName() != null && !compound.getName().equals("")) {

                    Double mz_precursor_positive_M_H = null;
                    List<Double> neutral_losses_positive = new LinkedList<Double>();
                    if (!compound.getNeutralLossesPositiveMode().isEmpty()) {
                        mz_precursor_positive_M_H = compound.getMzPositivePI();
                        neutral_losses_positive = new LinkedList(compound.getNeutralLossesPositiveMode());
                    }
                    Double mz_precursor_negative_M_HCOO = null;
                    List<Double> neutral_losses_negative = new LinkedList<Double>();
                    if (!compound.getNeutralLossesNegativeMode().isEmpty()) {
                        mz_precursor_negative_M_HCOO = compound.getTheoreticalPIMolecularWeight();
                        neutral_losses_negative = new LinkedList(compound.getNeutralLossesNegativeMode());
                    }
                    List<Annotation> putative_annotations_oxidized = new LinkedList<Annotation>();
                    if (compound.getOxidizedCompoundsGroupByMass() != null) {
                        List<TheoreticalCompounds> compoundsList = compound.getOxidizedCompoundsGroupByMass().getListCompounds();
                        for (TheoreticalCompounds theoreticalCompounds : compoundsList) {
                            String name = theoreticalCompounds.getName();
                            String url = "";
                            if (theoreticalCompounds.getLMCompound() != null && !theoreticalCompounds.getLMCompound().equals("")) {
                                url = theoreticalCompounds.getLMWebPage();
                            }
                            putative_annotations_oxidized.add(new Annotation(name, url));
                        }
                    }
                    List<Annotation> putative_annotations_non_oxidized = new LinkedList<Annotation>();
                    if (compound.getNonOxidizedCompoundsGroupByMass() != null) {
                        List<TheoreticalCompounds> compoundsList = compound.getNonOxidizedCompoundsGroupByMass().getListCompounds();
                        for (TheoreticalCompounds theoreticalCompounds : compoundsList) {
                            String name = theoreticalCompounds.getName();
                            String url = "";
                            if (theoreticalCompounds.getLMCompound() != null && !theoreticalCompounds.getLMCompound().equals("")) {
                                url = theoreticalCompounds.getLMWebPage();
                            }
                            putative_annotations_non_oxidized.add(new Annotation(name, url));
                        }
                    }
                    OxidationCompound oxidationcompound
                            = new OxidationCompound(
                                    compound.getOxidizedFAEM(),
                                    compound.getNonOxidizedFAEM(),
                                    compound.getParentIonEM(),
                                    compound.getAdductType(),
                                    compound.getOxidationType(),
                                    compound.getName(),
                                    compound.getFormula(),
                                    compound.getTheoreticalPIMolecularWeight(),
                                    compound.getPpmIncrement(),
                                    mz_precursor_positive_M_H, neutral_losses_positive,
                                    mz_precursor_negative_M_HCOO, neutral_losses_negative,
                                    putative_annotations_oxidized,
                                    putative_annotations_non_oxidized);
                    osr.addCompound(oxidationcompound);
                }
            }
        }

        return gson.toJson(osr);
    }

    @Path("/pathwaydisplayer")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getPathWayDisplayerImpl(String pathwayDisplayerQuery) {
        System.out.println("Pathway Displayer query: " + pathwayDisplayerQuery);
        PathwayDisplayerQuery query = gson.fromJson(pathwayDisplayerQuery, PathwayDisplayerQuery.class);

        //TODO: Call to the new pathway displayer backend.
        System.out.println("######### Processing Pathway Displayer service #########");

        PathwayDisplayerResults pdr = new PathwayDisplayerResults();

        return gson.toJson(pdr);
    }

    @Path("/spectralquality")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getSpectralQualityControllerImpl(
            String spectralQualityControllerQuery) {

        System.out.println("Spectral Quality Controller query: " + spectralQualityControllerQuery);
        SpectralQualityControllerQuery query = gson.fromJson(spectralQualityControllerQuery, SpectralQualityControllerQuery.class);

        System.out.println("######### Processing Spectral Quality Controller service #########");
        SpectralQualityControllerResults sqcr = SpectralQualityControllerAux.processSpectralQuality(query);

        return gson.toJson(sqcr);
    }

    // ***********************************************************************************************************
    //
    //
    // V3 Services - Services implemented using the new Experiment approach.
    //
    //
    // ***********************************************************************************************************
    @Path("/v3/advancedbatch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getAdvancedBatchExperimentImpl(String advancedbatchquery) {
        System.out.println("Advanced Batch Query: " + advancedbatchquery);
        AdvancedBatchQuery query = gson.fromJson(advancedbatchquery, AdvancedBatchQuery.class);

        MSFacade msFacade = new MSFacade();
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
        List<Map<Double, Double>> compositeSpectra = new ArrayList<Map<Double, Double>>();
        for (List<Spectrum> list : query.getComposite_spectra()) {
            Map<Double, Double> mapCSpectrum = new TreeMap<Double, Double>();
            for (Spectrum spec : list) {
                mapCSpectrum.put(spec.getMz(), (Double) spec.getIntensity());
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
                msFacade
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
            List<Map<Double, Double>> allCompositeSpectra = new ArrayList<Map<Double, Double>>();
            for (List<Spectrum> list : query.getAll_composite_spectra()) {
                Map<Double, Double> mapCSpectrumall = new TreeMap<Double, Double>();
                for (Spectrum spec : list) {
                    mapCSpectrumall.put(spec.getMz(), (Double) spec.getIntensity());
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
                    msFacade);

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
                                        compound.getInChIKey(),
                                        pathways);
                        asr.addCompound(asrcompound);
                        //System.out.println("ASR: " + asrcompound);
                    }
                }
            }
        }
        msFacade.disconnect();
        //System.out.println(gson.toJson(asr));
        return gson.toJson(asr);
    }

    @Path("/v3/batch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getBatchExperimentImpl(String batchquery) {
        System.out.println("Simple Batch Query: " + batchquery);
        SimpleBatchQuery query = gson.fromJson(batchquery, SimpleBatchQuery.class);

        MSFacade msFacade = new MSFacade();
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
        List<Map<Double, Double>> compositeSpectra
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
                msFacade
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
                                        compound.getInChIKey(),
                                        pathways);
                        ssr.addCompound(asrcompound);
                        //System.out.println("ASR: " + asrcompound);
                    }
                }
            }
        }
        msFacade.disconnect();
        //System.out.println(gson.toJson(asr));
        return gson.toJson(ssr);
    }
}
