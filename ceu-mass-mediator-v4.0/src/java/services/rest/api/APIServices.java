package services.rest.api;

import CEMS.*;
import LCMS_FEATURE.*;
import ch.unige.migpred.Predict;
import com.google.gson.Gson;
import controllers.CEMSRMTController;
import exceptions.bufferTemperatureException;
import facades.*;
import freemarker.template.*;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import msms.*;
import pathway.Pathway;
import persistence.NewPathways;
import persistence.oxidizedTheoreticalCompound.OxidizedTheoreticalCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;
import services.rest.api.request.*;
import services.rest.api.response.*;
import utilities.*;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class APIServices {

    private Configuration freemarker = null;

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

        List<Double> inputmzs = new ArrayList<Double>();
        inputmzs.add(query.getMass());

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

        System.out.println("input MZs: " + inputmzs);
        System.out.println("InputModeTolerance: " + toleranceMode);
        System.out.println("InputTolerance: " + tolerance);
        System.out.println("ionMode: " + ionMode);
        System.out.println("massesMode: " + massesMode);
        System.out.println("adducts: " + adducts);
        System.out.println("itemsGrouped: " + itemsGrouped);
        System.out.println("databases: " + databases);
        System.out.println("metabolitesType: " + metabolitesType);
        List<TheoreticalCompounds> compounds = ejbFacade.findCompoundsSimple(
                inputmzs,
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

        List<Double> inputmzs = new ArrayList<Double>();
        inputmzs.add(query.getMass());
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

        System.out.println("input MZs: " + inputmzs);
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
                inputmzs,
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
        List<Double> inputmzs = query.getMasses();

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
        inputmzs.addAll(query.getAll_masses());
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

        System.out.println("input MZs: " + inputmzs);
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
                inputmzs,
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

    @Path("/v3/cesearch/effmob")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSEffMobSearchImpl(String effmobquery) {

        String service = "CE-MS EFF MOB Search";

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + effmobquery);

        EffMobSearchQuery query = gson.fromJson(effmobquery, EffMobSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSEffMobExperiment experiment
                = new CEMSEffMobExperiment(
                        query.getTolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getTolerance_mode().toString()),
                        DataFromInterfacesUtilities.ionizationModeToInteger(query.getIon_mode().toString()),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        null,
                        null,
                        query.getEff_mob_tolerance(),
                        query.getEff_mob_tolerance_mode().toString()
                );

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());
        List<CEMSFeature> cemsFeatures = new ArrayList();

        try {
            cemsFeatures = msFacade.getCEAnnotationsFromMassesToleranceAndEffMobs(
                    query.getMasses(),
                    query.getTolerance(),
                    DataFromInterfacesUtilities.toleranceTypeToInteger(query.getTolerance_mode().toString()),
                    query.getEffective_mobilities(),
                    query.getEff_mob_tolerance(),
                    query.getEff_mob_tolerance_mode().toString(),
                    query.getBuffer().intValue(),
                    query.getBuffer().getTemperature(),
                    DataFromInterfacesUtilities.getChemAlphabetAsInt(query.getChemical_alphabet().toString()),
                    query.isDeuterium(),
                    DataFromInterfacesUtilities.inputMassModeToInteger(query.getMasses_mode().toString()),
                    DataFromInterfacesUtilities.ionizationModeToInteger(query.getIon_mode().toString()),
                    query.getPolarity().intValue(),
                    adducts,
                    true);
        } catch (bufferTemperatureException bte) {
            error(service);
        }
        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        EffMobSearchResults emsr = new EffMobSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzEff groupmz = new CompoundsGroupMzEff(feature.getInput_mz(), feature.getInput_eff_mob());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSEff(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzEff fragmentGroupmz = new FragmentsGroupMzEff(feature.getInput_mz(), feature.getInput_eff_mob());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSEff(fragment));
            }
            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/exprmt")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSExpRMTSearchImpl(String exprmtquery) {

        String service = "CE-MS EXP RMT Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final int CAPILLARYLENGTH = 1000; // mm
        final int CAPILLARYVOLTAGE = 30; // kvoltios
        final boolean ALLOWOPPOSITEESIMODE = true;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + exprmtquery);

        ExpRMTSearchQuery query = gson.fromJson(exprmtquery, ExpRMTSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSRMTExperiment experiment
                = new CEMSRMTExperiment(
                        query.getTolerance(),
                        query.getTolerance_mode().intValue(),
                        query.getIon_mode().intValue(),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        CAPILLARYVOLTAGE,
                        CAPILLARYLENGTH,
                        query.getRmt_tolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getRmt_tolerance_mode().toString()),
                        query.getRmt_Reference().intValue()
                );

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());
        List<CEMSFeature> cemsFeatures = new ArrayList();

        try {
            cemsFeatures = msFacade.getCEAnnotationsFromMassesToleranceAndExpRMTs(query.getMasses(),
                    query.getTolerance(),
                    query.getTolerance_mode().intValue(),
                    query.getRmt(),
                    query.getRmt_tolerance(),
                    query.getRmt_tolerance_mode().toString(),
                    query.getBuffer().intValue(),
                    query.getBuffer().getTemperature(),
                    query.getChemical_alphabet().intValue(),
                    query.isDeuterium(),
                    query.getMasses_mode().intValue(),
                    query.getIon_mode().intValue(),
                    query.getPolarity().intValue(),
                    adducts,
                    query.getRmt_Reference().intValue(),
                    ALLOWOPPOSITEESIMODE);
        } catch (bufferTemperatureException bte) {
            error(service);
        }
        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        ExpRmtSearchResults emsr = new ExpRmtSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzRmt groupmz = new CompoundsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSRmt(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzRmt fragmentGroupmz = new FragmentsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSRmt(fragment));
            }
            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/rmt1marker")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSRMT1MarkerSearchImpl(String rmt1marker) {

        String service = "CE-MS RMT 1 Marker Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final boolean ALLOWOPPOSITEESIMODE = true;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + rmt1marker);

        RMT1MarkerSearchQuery query = gson.fromJson(rmt1marker, RMT1MarkerSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSRMTExperiment experiment
                = new CEMSRMTExperiment(
                        query.getTolerance(),
                        query.getTolerance_mode().intValue(),
                        query.getIon_mode().intValue(),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        query.getCapillary_voltage(),
                        query.getCapillary_length(),
                        query.getRmt_tolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getRmt_tolerance_mode().toString()),
                        query.getRmt_Reference().intValue()
                );

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        Map<Integer, CEMSCompound> dbcandidates = getDbCandidates(query.getBuffer().intValue(),
                query.getBuffer().getTemperature(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                ALLOWOPPOSITEESIMODE);

        float[] databaseMobilities = new float[dbcandidates.size()];
        float[] migrationTimeBuffer = new float[dbcandidates.size()];

        Double[] markerMobilities = Utilities.getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, query.getMarker().intValue(), null);

        float markerMobility = (float) ((double) markerMobilities[0]);
        double markerTimeAux = query.getMarker_time();
        float markerTime = (float) markerTimeAux;

        Predict.singleMarker(databaseMobilities, migrationTimeBuffer, markerMobility, markerTime, query.getCapillary_length(), query.getCapillary_voltage());

        Double bgeMT = Utilities.fillRMTs(dbcandidates, migrationTimeBuffer, query.getRmt_Reference().intValue());

        Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());

        List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndRMTs(
                setDBCandidates,
                query.getMasses(),
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getRmt(),
                query.getRmt_tolerance(),
                query.getRmt_tolerance_mode().toString(),
                query.getChemical_alphabet().intValue(),
                query.isDeuterium(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                adducts,
                query.getRmt_Reference().intValue(),
                bgeMT);

        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        ExpRmtSearchResults emsr = new ExpRmtSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzRmt groupmz = new CompoundsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSRmt(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzRmt fragmentGroupmz = new FragmentsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSRmt(fragment));
            }
            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/rmt2marker")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSRMT2MarkerSearchImpl(String rmt1marker) {

        String service = "CE-MS RMT 2 Marker Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final int CAPILLARYLENGTH = 1000; // mm
        final int CAPILLARYVOLTAGE = 30; // kvoltios
        final boolean ALLOWOPPOSITEESIMODE = true;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + rmt1marker);

        RMT2MarkerSearchQuery query = gson.fromJson(rmt1marker, RMT2MarkerSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSRMTExperiment experiment
                = new CEMSRMTExperiment(
                        query.getTolerance(),
                        query.getTolerance_mode().intValue(),
                        query.getIon_mode().intValue(),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        CAPILLARYVOLTAGE,
                        CAPILLARYLENGTH,
                        query.getRmt_tolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getRmt_tolerance_mode().toString()),
                        query.getRmt_reference().intValue());

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        Map<Integer, CEMSCompound> dbcandidates = getDbCandidates(query.getBuffer().intValue(),
                query.getBuffer().getTemperature(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                ALLOWOPPOSITEESIMODE);
        float[] databaseMobilities = new float[dbcandidates.size()];
        float[] migrationTimeBuffer = new float[dbcandidates.size()];

        Double[] markerMobilities = Utilities.getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, query.getMarker().intValue(), query.getMarker2().intValue());

        float markerMobility1 = (float) ((double) markerMobilities[0]);
        float markerMobility2 = (float) ((double) markerMobilities[1]);
        float markerTime1 = (float) query.getMarker_time();
        float markerTime2 = (float) query.getMarker2_time();

        Predict.doubleMarker(databaseMobilities, migrationTimeBuffer, markerMobility1, markerTime1, markerMobility2, markerTime2);

        Double bgeMT = Utilities.fillRMTs(dbcandidates, migrationTimeBuffer, query.getRmt_reference().intValue());

        Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());

        List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndRMTs(
                setDBCandidates,
                query.getMasses(),
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getRmt(),
                query.getRmt_tolerance(),
                query.getRmt_tolerance_mode().toString(),
                query.getChemical_alphabet().intValue(),
                query.isDeuterium(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                adducts,
                query.getRmt_reference().intValue(),
                bgeMT);

        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        ExpRmtSearchResults emsr = new ExpRmtSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzRmt groupmz = new CompoundsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSRmt(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzRmt fragmentGroupmz = new FragmentsGroupMzRmt(feature.getInput_mz(), feature.getInput_RMT());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSRmt(fragment));
            }

            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/mt1marker")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSMT1MarkerSearchImpl(String rmt1marker) {

        String service = "CE-MS MT 1 Marker Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final boolean ALLOWOPPOSITEESIMODE = true;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + rmt1marker);

        MT1MarkerSearchQuery query = gson.fromJson(rmt1marker, MT1MarkerSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSMTExperiment experiment
                = new CEMSMTExperiment(
                        query.getTolerance(),
                        query.getTolerance_mode().intValue(),
                        query.getIon_mode().intValue(),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        query.getCapillary_voltage(),
                        query.getCapillary_length(),
                        query.getMt_tolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getMt_tolerance_mode().toString()));

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        Map<Integer, CEMSCompound> dbcandidates = getDbCandidates(query.getBuffer().intValue(),
                query.getBuffer().getTemperature(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                ALLOWOPPOSITEESIMODE);

        float[] databaseMobilities = new float[dbcandidates.size()];
        float[] migrationTimeBuffer = new float[dbcandidates.size()];

        Double[] markerMobilities = Utilities.getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, query.getMarker().intValue(), null);

        float markerMobility = (float) ((double) markerMobilities[0]);
        double markerTimeAux = query.getMarker_time();
        float markerTime = (float) markerTimeAux;

        Predict.singleMarker(databaseMobilities, migrationTimeBuffer, markerMobility, markerTime, query.getCapillary_length(), query.getCapillary_voltage());

        Utilities.fillMTs(dbcandidates, migrationTimeBuffer);

        Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());

        List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndMTs(
                setDBCandidates,
                query.getMasses(),
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getMt(),
                query.getMt_tolerance(),
                query.getMt_tolerance_mode().toString(),
                query.getChemical_alphabet().intValue(),
                query.isDeuterium(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                adducts);

        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        ExpMtSearchResults emsr = new ExpMtSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzMt groupmz = new CompoundsGroupMzMt(feature.getInput_mz(), feature.getInput_MT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSMT(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzMt fragmentGroupmz = new FragmentsGroupMzMt(feature.getInput_mz(), feature.getInput_MT());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSMT(fragment));
            }
            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/mt2marker")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSMT2MarkerSearchImpl(String rmt1marker) {

        String service = "CE-MS MT 2 Marker Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final int CAPILLARYLENGTH = 1000; // mm
        final int CAPILLARYVOLTAGE = 30; // kvoltios
        final boolean ALLOWOPPOSITEESIMODE = true;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + rmt1marker);

        MT2MarkerSearchQuery query = gson.fromJson(rmt1marker, MT2MarkerSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        CEMSMTExperiment experiment
                = new CEMSMTExperiment(
                        query.getTolerance(),
                        query.getTolerance_mode().intValue(),
                        query.getIon_mode().intValue(),
                        query.getPolarity().intValue(),
                        query.getBuffer().intValue(),
                        query.getBuffer().getTemperature(),
                        CAPILLARYVOLTAGE,
                        CAPILLARYLENGTH,
                        query.getMt_tolerance(),
                        DataFromInterfacesUtilities.toleranceTypeToInteger(query.getMt_tolerance_mode().toString()));

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        Map<Integer, CEMSCompound> dbcandidates = getDbCandidates(query.getBuffer().intValue(),
                query.getBuffer().getTemperature(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                ALLOWOPPOSITEESIMODE);

        float[] databaseMobilities = new float[dbcandidates.size()];
        float[] migrationTimeBuffer = new float[dbcandidates.size()];

        Double[] markerMobilities = Utilities.getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, query.getMarker().intValue(), query.getMarker2().intValue());

        float markerMobility1 = (float) ((double) markerMobilities[0]);
        float markerMobility2 = (float) ((double) markerMobilities[1]);

        float markerTime1 = (float) query.getMarker_time();
        float markerTime2 = (float) query.getMarker2_time();

        Predict.doubleMarker(databaseMobilities, migrationTimeBuffer, markerMobility1, markerTime1, markerMobility2, markerTime2);

        Utilities.fillMTs(dbcandidates, migrationTimeBuffer);

        Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());

        List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndMTs(
                setDBCandidates,
                query.getMasses(),
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getMt(),
                query.getMt_tolerance(),
                query.getMt_tolerance_mode().toString(),
                query.getChemical_alphabet().intValue(),
                query.isDeuterium(),
                query.getMasses_mode().intValue(),
                query.getIon_mode().intValue(),
                query.getPolarity().intValue(),
                adducts);

        experiment.setCEMSFeatures(cemsFeatures);
        List<CEMSFeature> features = experiment.getCEMSFeatures();

        ExpMtSearchResults emsr = new ExpMtSearchResults();

        for (CEMSFeature feature : features) {
            CompoundsGroupMzMt groupmz = new CompoundsGroupMzMt(feature.getInput_mz(), feature.getInput_MT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSMT(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            FragmentsGroupMzMt fragmentGroupmz = new FragmentsGroupMzMt(feature.getInput_mz(), feature.getInput_MT());
            for (CEMSAnnotationFragment fragment : feature.getAnnotationsFragmentsCEMS()) {
                fragmentGroupmz.addFragment(new FragmentCEMSMT(fragment));
            }
            emsr.addCompoundGroup(groupmz);
            emsr.addFragmentGroup(fragmentGroupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/targeted-effmob")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSTargetedEffMobSearchImpl(String tareff) {

        String service = "Targeted CE-MS Eff Mob Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final int CAPILLARYLENGTH = 1000; // mm
        final int CAPILLARYVOLTAGE = 30; // kvoltios
        final boolean ALLOWOPPOSITEESIMODE = true;
        final int POLARITY = 1; // Direct
        final int CODE_BGE = 180838; // CMM ID of Methionine Sulfone!
        final BufferType BUFFER = BufferType.FORMIC;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + tareff);

        TargetedEffMobSearchQuery query = gson.fromJson(tareff, TargetedEffMobSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        List<Set<CEMSFragment>> inputExperimentalFragments = convert(query.getSignals_grouped(), query.getMasses().size(), query.getIon_source_voltage());

        CEMSRMTExperiment experiment = new CEMSRMTExperiment(
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getIon_mode().intValue(),
                POLARITY,
                BUFFER.intValue(),
                BUFFER.getTemperature(),
                CAPILLARYVOLTAGE,
                CAPILLARYLENGTH,
                query.getEff_mob_tolerance(),
                query.getEff_mob_tolerance_mode().intValue(),
                CODE_BGE);

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        try {
            // TODO QUERIES OVER THE EXP RMT
            List<CEMSFeature> cemsFeatures = msFacade.getCEAnnotationsFromMassesToleranceEffMobsAndFragments(
                    query.getMasses(),
                    query.getTolerance(),
                    query.getTolerance_mode().intValue(),
                    query.getEffective_mobilities(),
                    query.getEff_mob_tolerance(),
                    query.getEff_mob_tolerance_mode().toString(),
                    inputExperimentalFragments,
                    BUFFER.intValue(),
                    BUFFER.getTemperature(),
                    query.getChemical_alphabet().intValue(),
                    query.isDeuterium(),
                    query.getMasses_mode().intValue(),
                    query.getIon_mode().intValue(),
                    POLARITY,
                    adducts,
                    CODE_BGE,
                    query.getIon_source_voltage().intValue(),
                    ALLOWOPPOSITEESIMODE);

            experiment.setCEMSFeatures(cemsFeatures);
        } catch (bufferTemperatureException ex) {
            error(service);
        }

        List<CEMSFeature> features = experiment.getCEMSFeatures();

        TargetedEffMobSearchResults emsr = new TargetedEffMobSearchResults();

        for (CEMSFeature feature : features) {
            List<Signal> input_signals = feature.getExperimentalFragments().stream().map(n -> new Signal(n.getMz(), n.getIntensity())).collect(Collectors.toList());
            CompoundsGroupMzSignalEff groupmz = new CompoundsGroupMzSignalEff(feature.getInput_mz(), input_signals, feature.getInput_eff_mob());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSExtEff(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            emsr.addCompoundGroup(groupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    @Path("/v3/cesearch/targeted-exprmt")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String getCEMSTargetedExpRmtSearchImpl(String tareff) {

        String service = "Targeted CE-MS Exp RMT Search";

        //Estos valores se establecen fijos en CEMSMTControllerAdapter
        final int CAPILLARYLENGTH = 1000; // mm
        final int CAPILLARYVOLTAGE = 30; // kvoltios
        final boolean ALLOWOPPOSITEESIMODE = true;
        final int POLARITY = 1; // Direct
        final int CODE_BGE = 180838; // CMM ID of Methionine Sulfone!
        final BufferType BUFFER = BufferType.FORMIC;

        debug(service, "-> QUERY:" + "\nINPUT JSON:\n" + tareff);

        TargetedExpRMTSearchQuery query = gson.fromJson(tareff, TargetedExpRMTSearchQuery.class);

        MSFacade msFacade = new MSFacade();

        List<Set<CEMSFragment>> inputExperimentalFragments = convert(query.getSignals_grouped(), query.getMasses().size(), query.getIon_source_voltage());

        CEMSRMTExperiment experiment = new CEMSTargetedExperiment(
                query.getTolerance(),
                query.getTolerance_mode().intValue(),
                query.getIon_mode().intValue(),
                POLARITY,
                BUFFER.intValue(),
                BUFFER.getTemperature(),
                CAPILLARYVOLTAGE,
                CAPILLARYLENGTH,
                query.getRmt_tolerance(),
                query.getRmt_tolerance_mode().intValue(),
                CODE_BGE,
                inputExperimentalFragments);

        List<String> adducts = query.getAdducts().stream().map(element -> element.toString()).collect(Collectors.toList());

        // TODO QUERIES OVER THE EXP RMT
        List<CEMSFeature> cemsFeatures;
        try {
            cemsFeatures = msFacade.getCEAnnotationsFromMassesToleranceExpRMTsAndFragments(
                    query.getMasses(),
                    query.getTolerance(),
                    query.getTolerance_mode().intValue(),
                    query.getRmt(),
                    query.getRmt_tolerance(),
                    query.getRmt_tolerance_mode().toString(),
                    inputExperimentalFragments,
                    BUFFER.intValue(),
                    BUFFER.getTemperature(),
                    query.getChemical_alphabet().intValue(),
                    query.isDeuterium(),
                    query.getMasses_mode().intValue(),
                    query.getIon_mode().intValue(),
                    POLARITY,
                    adducts,
                    CODE_BGE,
                    query.getIon_source_voltage().intValue(),
                    ALLOWOPPOSITEESIMODE);

            experiment.setCEMSFeatures(cemsFeatures);
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(APIServices.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<CEMSFeature> features = experiment.getCEMSFeatures();

        TargetedExpRmtSearchResults emsr = new TargetedExpRmtSearchResults();

        for (CEMSFeature feature : features) {
            List<Signal> input_signals = feature.getExperimentalFragments().stream().map(n -> new Signal(n.getMz(), n.getIntensity())).collect(Collectors.toList());
            CompoundsGroupMzSignalRmt groupmz = new CompoundsGroupMzSignalRmt(feature.getInput_mz(), input_signals, feature.getInput_RMT());
            for (CEMSAnnotationsGroupByAdduct annotations : feature.getAnnotationsCEMSGroupByAdduct()) {
                AnnotationsGroupByAdduct groupbyadduct = new AnnotationsGroupByAdduct(annotations.getAdduct());
                for (CEMSAnnotation annotation : annotations.getAnnotationsCEMS()) {
                    groupbyadduct.addCompound(new AnnotationCEMSExtRmt(annotation));
                }
                groupmz.addGroupByAdduct(groupbyadduct);
            }
            emsr.addCompoundGroup(groupmz);
        }

        msFacade.disconnect();

        debug(service, "-> RESULTS:" + "\nOUTPUT JSON:\n" + gson.toJson(emsr));

        return gson.toJson(emsr);
    }

    private CompoundView obtainCompound(Integer compoundId) {
        MSFacade msFacade = new MSFacade();
        CompoundView cv = msFacade.getCompound(compoundId);
        msFacade.disconnect();

        return cv;
    }

    @Path("/v3/compounds/{compoundId}")
    @GET
    @Produces("application/json")
    public String getCompound(@PathParam("compoundId") Integer compoundId) {
        String service = "Compound";
        debug(service, "-> QUERY:" + "\napplication/json\n INPUT id: " + compoundId);

        CompoundView cv = this.obtainCompound(compoundId);

        return (cv != null) ? gson.toJson(cv) : "";
    }

    @Path("/v3/compounds/{compoundId}")
    @GET
    @Produces("text/html")
    public String getCompoundHTML(@PathParam("compoundId") Integer compoundId) {
        String service = "Compound";
        debug(service, "-> QUERY:" + "\ntext/html\n INPUT id: " + compoundId);

        CompoundView cv = this.obtainCompound(compoundId);

        /* Create a data-model */
        ViewDataModel dataModel = new ViewDataModel(cv);
        dataModel.put("base_aspergillus", Constants.WEB_COMPOUND_ASPERGILLUS);
        dataModel.put("base_chebi", Constants.WEB_COMPOUND_CHEBI);
        dataModel.put("base_hmdb", Constants.WEB_COMPOUND_HMDB);
        dataModel.put("base_kegg", Constants.WEB_COMPOUND_KEGG);
        dataModel.put("base_kn", Constants.WEB_COMPOUND_KNAPSACK);
        dataModel.put("base_lm", Constants.WEB_COMPOUND_LM);
        dataModel.put("base_metlin", Constants.WEB_COMPOUND_METLIN);
        dataModel.put("base_pc", Constants.WEB_COMPOUND_PUBCHEMICHAL);
        dataModel.put("base_reaction", Constants.WEB_REACTION_KEGG);
        dataModel.put("base_class", Constants.WEB_CLASSYFIRE);
        dataModel.put("base_ontology", Constants.WEB_ONTOLOGY_LOCAL);
        dataModel.put("base_organism", Constants.WEB_ORGANISMS_LOCAL);
        dataModel.put("base_npatlas", Constants.WEB_COMPOUND_NPATLAS);

        return this.generateHTMLRepresentation("compound_template.ftl", dataModel);
    }

    private ClassyfireNodeView obtainClassyfireNode(String nodeId) {
        MSFacade msFacade = new MSFacade();
        ClassyfireNodeView cn = msFacade.getClassyfireNode(nodeId);
        msFacade.disconnect();

        return cn;
    }

    @Path("/v3/classyfire/{nodeId}")
    @GET
    @Produces("application/json")
    public String getClassyfire(@PathParam("nodeId") String nodeId) {
        String service = "Classyfire Node";
        debug(service, "-> QUERY:" + "\napplication/json\n INPUT id: " + nodeId);

        ClassyfireNodeView cn = obtainClassyfireNode(nodeId);

        return (cn != null) ? gson.toJson(cn) : "";
    }

    @Path("/v3/classyfire/{nodeId}")
    @GET
    @Produces("text/html")
    public String getClassyfireHTML(@PathParam("nodeId") String nodeId) {
        String service = "Classyfire Node";
        debug(service, "-> QUERY:" + "\ntext/html\n INPUT id: " + nodeId);

        ClassyfireNodeView cn = obtainClassyfireNode(nodeId);

        /* Create a data-model */
        ViewDataModel dataModel = new ViewDataModel(cn);
        dataModel.put("classyfire", cn);
        dataModel.put("base_classyfirem", Constants.WEB_CLASSYFIRE);

        return this.generateHTMLRepresentation("classyfire_template.ftl", dataModel);
    }

    private TermView obtainOntologyTerm(Integer termId) {
        MSFacade msFacade = new MSFacade();
        TermView term = msFacade.getOntologyTerm(termId);
        msFacade.disconnect();

        return term;
    }

    @Path("/v3/ontology/{termId}")
    @GET
    @Produces("application/json")
    public String getOntologyTerm(@PathParam("termId") Integer termId) {
        String service = "Ontology Term";
        debug(service, "-> QUERY:" + "\napplication/json\n INPUT id: " + termId);

        TermView term = this.obtainOntologyTerm(termId);

        return (term != null) ? gson.toJson(term) : "";
    }

    @Path("/v3/ontology/{termId}")
    @GET
    @Produces("text/html")
    public String getOntologyTermHTML(@PathParam("termId") Integer termId) {
        String service = "Ontology Term";
        debug(service, "-> QUERY:" + "\ntext/html\n INPUT id: " + termId);

        TermView term = this.obtainOntologyTerm(termId);
        /* Create a data-model */

        ViewDataModel dataModel = new ViewDataModel(term);
        dataModel.put("term", term);
        dataModel.put("base_ontology", Constants.WEB_ONTOLOGY_LOCAL);

        return this.generateHTMLRepresentation("term_template.ftl", dataModel);
    }

    private ReferenceView obtainReference(Integer referenceId) {
        MSFacade msFacade = new MSFacade();
        ReferenceView reference = msFacade.getReference(referenceId);
        msFacade.disconnect();

        return reference;
    }

    @Path("/v3/references/{referenceId}")
    @GET
    @Produces("application/json")
    public String getReference(@PathParam("referenceId") Integer referenceId) {
        String service = "Reference";
        debug(service, "-> QUERY:" + "\napplication/json\n INPUT id: " + referenceId);

        ReferenceView reference = this.obtainReference(referenceId);

        return (reference != null) ? gson.toJson(reference) : "";
    }

    @Path("/v3/references/{referenceId}")
    @GET
    @Produces("text/html")
    public String getReferenceHTML(@PathParam("referenceId") Integer referenceId) {
        String service = "Reference";
        debug(service, "-> QUERY:" + "\ntext/html\n INPUT id: " + referenceId);

        ReferenceView reference = this.obtainReference(referenceId);

        /* Create a data-model */
        ViewDataModel dataModel = new ViewDataModel(reference);
        dataModel.put("base_compounds", Constants.WEB_COMPOUNDS_LOCAL);
        dataModel.put("base_organism", Constants.WEB_ORGANISMS_LOCAL);

        return this.generateHTMLRepresentation("reference_template.ftl", dataModel);
    }

    private OrganismView obtainOrganism(Integer organismId) {
        MSFacade msFacade = new MSFacade();
        OrganismView organism = msFacade.getOrganism(organismId);
        msFacade.disconnect();

        return organism;
    }

    @Path("/v3/organisms/{organismId}")
    @GET
    @Produces("application/json")
    public String getOrganism(@PathParam("organismId") Integer organismId) {
        String service = "Organism";
        debug(service, "-> QUERY:" + "\napplication/json\n INPUT id: " + organismId);

        OrganismView organism = this.obtainOrganism(organismId);

        return (organism != null) ? gson.toJson(organism) : "";
    }

    @Path("/v3/organisms/{organismId}")
    @GET
    @Produces("text/html")
    public String getOrganismHTML(@PathParam("organismId") Integer organismId) {
        String service = "Organism";
        debug(service, "-> QUERY:" + "\ntext/html\n INPUT id: " + organismId);

        OrganismView organism = this.obtainOrganism(organismId);

        /* Create a data-model */
        ViewDataModel dataModel = new ViewDataModel(organism);
        dataModel.put("base_organism", Constants.WEB_ORGANISMS_LOCAL);
        dataModel.put("base_compounds", Constants.WEB_COMPOUNDS_LOCAL);

        return this.generateHTMLRepresentation("organism_template.ftl", dataModel);
    }

    // UTILITY METHODS
    private Map<Integer, CEMSCompound> getDbCandidates(int buffer, int buffer_temperature, int masses_mode, int ion_mode, int polarity, boolean allowOppositeESIMode) {

        Map<Integer, CEMSCompound> dbCandidates = new LinkedHashMap<>();

        Map<Integer, CEMSCompound> dbFormic20CandidatesPositiveDirect = fillDBsByExpCond(1, 20, 1, 1, allowOppositeESIMode);
        Map<Integer, CEMSCompound> dbFormic20CandidatesNegativeInverse = fillDBsByExpCond(1, 20, 2, 2, allowOppositeESIMode);
        Map<Integer, CEMSCompound> dbAcetic25CandidatesPositiveDirect = fillDBsByExpCond(2, 25, 1, 1, allowOppositeESIMode);
        Map<Integer, CEMSCompound> dbAcetic25CandidatesNegativeInverse = fillDBsByExpCond(2, 25, 2, 2, allowOppositeESIMode);

        int provIonMode = (masses_mode == 0) ? 1 : ion_mode;

        if (buffer == 1 && buffer_temperature == 20 && provIonMode == 1 && polarity == 1) {
            dbCandidates = dbFormic20CandidatesPositiveDirect;
        } else if (buffer == 1 && buffer_temperature == 20 && provIonMode == 2 && polarity == 2) {
            dbCandidates = dbFormic20CandidatesNegativeInverse;
        } else if (buffer == 2 && buffer_temperature == 25 && provIonMode == 1 && polarity == 1) {
            dbCandidates = dbAcetic25CandidatesPositiveDirect;
        } else if (buffer == 2 && buffer_temperature == 25 && provIonMode == 2 && polarity == 2) {
            dbCandidates = dbAcetic25CandidatesNegativeInverse;
        }

        return dbCandidates;
    }

    private Map<Integer, CEMSCompound> fillDBsByExpCond(Integer buffer, Integer temperature, Integer ionMode, Integer polarity, boolean allowOppositeESIMode) {
        Map<Integer, CEMSCompound> cemscompounds = new LinkedHashMap<Integer, CEMSCompound>();
        try {
            MSFacade msFacade = new MSFacade();
            cemscompounds = msFacade.getCEMSCompoundsFromExperimentalConditions(buffer, temperature, ionMode, polarity, allowOppositeESIMode);

            msFacade.disconnect();
        } catch (bufferTemperatureException ex) {
            Logger.getLogger(CEMSRMTController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cemscompounds;
    }

    private List<Set<CEMSFragment>> convert(List<List<Signal>> signals_grouped, int inputMasses, IonSourceVoltage isv) {
        List<Set<CEMSFragment>> allExperimentalFragments = new ArrayList<>();
        for (List<Signal> signals : signals_grouped) {
            Set<CEMSFragment> experimentalFragments = new TreeSet();
            for (Signal signal : signals) {
                CEMSFragment experimentalFragment = new CEMSFragment(null, null, null, isv.intValue(),
                        signal.getMz(), signal.getIntensity(), null, null, null);
                experimentalFragments.add(experimentalFragment);
            }
            allExperimentalFragments.add(experimentalFragments);
        }

        //Rellena con conjuntos vacios si hay mas masas que seniales
        int pendientes = inputMasses - allExperimentalFragments.size();
        for (int i = 0; i < pendientes; i++) {
            allExperimentalFragments.add(new TreeSet<CEMSFragment>());
        }
        return allExperimentalFragments;
    }

    private void debug(String service, String message) {
        System.err.println("RESTful Api -" + service + "- DEBUG ~~> " + message);
    }

    private void error(String service) {
        System.err.println("RESTful Api -" + service + "- ERROR ~~> Something wrong happened. Please, review the service");
    }

    private void debug(String name, Double[] doubles) {
        System.out.println(name);
        for (Double d : doubles) {
            System.err.print(d + ",");
        }
        System.err.println("");
    }

    private void debug(String name, double[] doubles) {
        System.out.println(name);
        for (double d : doubles) {
            System.err.print(d + ",");
        }
        System.err.println("");
    }

    private void debug(String name, float[] floats) {
        System.err.println(name);
        for (float f : floats) {
            System.err.print(f + ",");
        }
        System.err.println("");
    }

    private String generateHTMLRepresentation(String template, ViewDataModel dataModel) {

        String output;

        try {
            StringWriter sw = new StringWriter();

            //Rellenamos la plantilla.
            if (freemarker == null) {
                freemarker = this.getFreemarkerConf();
            }
            Template t = freemarker.getTemplate(template);

            t.process(dataModel.getDataModel(), sw);

            output = sw.toString();

        } catch (Exception ex) {
            error("generateHTMLRepresentation method");
            ex.printStackTrace();
            throw new InternalServerErrorException();
        }

        return output;
    }

    private Configuration getFreemarkerConf() {
        //Freemarker lib version
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
        // Specify the source where the template files come from.
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/services/rest/api/templates/");
        //Encoding
        cfg.setDefaultEncoding("UTF-8");

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);
        // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
        cfg.setWrapUncheckedExceptions(true);
        // Do not fall back to higher scopes when reading a null loop variable:
        cfg.setFallbackOnNullLoopVariable(false);
        //Avoid formating numbers with . or , (decimals or thousands). Necessary for integer ids.
        cfg.setNumberFormat("computer");

        return cfg;
    }

}
