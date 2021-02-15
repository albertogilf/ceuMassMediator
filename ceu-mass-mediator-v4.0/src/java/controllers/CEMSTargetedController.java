package controllers;

import CEMS.CEMSFeature;
import CEMS.CEMSFragment;
import CEMS.CEMSRMTExperiment;
import CEMS.CEMSTargetedExperiment;
import exceptions.bufferTemperatureException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import static utilities.CEMSLists.ION_SOURCE_VOLTAGES_LIST;
import static utilities.CEMSLists.getBufferFromBufferTempList;
import static utilities.CEMSLists.getTemperatureFromBufferTempList;
import utilities.Cadena;
import utilities.DataFromInterfacesUtilities;
import utilities.Constants;
import static utilities.Constants.CE_TOLERANCE_MODE_DEFAULT_VALUE;
import static utilities.Constants.CE_TOLERANCE_DEFAULT_VALUE;

/**
 * Controller (Bean) of the application for CE/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2019
 */
@SessionScoped
@Named("CEMSTargetedController")
public class CEMSTargetedController extends CEMSMTControllerAdapter implements Serializable {

    // For targeted search
    private List<Double> inputRMTs;
    private String inputRMTTolerance;
    private String inputRMTModeTolerance;
    private Integer rmtTolerance;

    // FOR THE COLLECTION OF effMobs
    private String queryInputRMTs;

    private Integer code_bge;
    private Integer ionSourceVoltage;
    private final List<SelectItem> ionSourceVoltageCandidates;

    private String queryInputGroupedSignals;
    private List<Set<CEMSFragment>> inputExperimentalFragments;

    public CEMSTargetedController() {
        super();
        this.queryInputGroupedSignals = "";
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputRMTs = "";
        this.ionSourceVoltageCandidates = ION_SOURCE_VOLTAGES_LIST;
        this.ionSourceVoltage = 100;
        this.code_bge = 180838; // CMM ID of Methionine Sulfone!
    }

    /**
     * Submit search for a CE MS experiment using Relative migration times and
     * the fragmentation. This method process the data from a ce ms experiment
     * with relative migration times using the attributes data in the bean.
     *
     * @param searchMode: 1: 1 marker; 2: 2 markers; 3: experimental MTs 4: Eff
     * mobs (methionine sulfone)
     */
    public void submitTargetedCEMSSearch(Integer searchMode) {
        int numInputMZs = readInputMZs();
        List<Double> rmtAux = Cadena.getListOfDoubles(getQueryInputRMTs(), numInputMZs);
        this.setInputRMTs(rmtAux);
        super.setMzTolerance(Integer.parseInt(this.getInputmzTolerance()));
        this.rmtTolerance = Integer.parseInt(this.inputRMTTolerance);

        List<Set<CEMSFragment>> groupedSignalsAux
                = Cadena.getListOfExperimentalProductIons(this.queryInputGroupedSignals, numInputMZs, this.ionSourceVoltage);
        this.inputExperimentalFragments = groupedSignalsAux;

        if (searchMode == 1) {
            submitCEMSTargetedSearch1Marker();
        } else if (searchMode == 2) {
            submitCEMSTargetedSearch2Markers();
        } else if (searchMode == 3) {
            submitCEMSEXPRMTTargetedSearch();
        } else if (searchMode == 4) {
            submitCEMSeffMobTargetedSearch();
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times, the
     * in-source experimental fragments, 1 marker and the voltage and capillary
     * length
     */
    private void submitCEMSTargetedSearch1Marker() {

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputRMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSTargetedExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.rmtTolerance, mtToleranceModeAsInt, this.code_bge, this.inputExperimentalFragments);
            /*
            Map<Integer, CEMSCompound> dbcandidates = this.getDbCandidates();
            float[] databaseMobilities = new float[dbcandidates.size()];
            float[] migrationTimeBuffer = new float[dbcandidates.size()];
            Double[] markerMobilities = getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, this.getIDMarker1(), null);
            float markerMobility = (float) ((double) markerMobilities[0]);
            double markerTimeAux = this.getMarkerTime1();
            float markerTime = (float) markerTimeAux;

            Predict.singleMarker(databaseMobilities, migrationTimeBuffer, markerMobility, markerTime, this.getCapillary_length(), this.getCapillary_voltage());

            Double bgeMT = fillRMTs(dbcandidates, migrationTimeBuffer, this.code_bge);
            
            Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());
            List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndRMTs(setDBCandidates, this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance,
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.code_bge, bgeMT);
            
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);
             */
        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * TODO! Submit search for a CE MS experiment using absolute Migration Times
     * and 2 markers
     */
    private void submitCEMSTargetedSearch2Markers() {
        // TODO!
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputRMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSTargetedExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.rmtTolerance, mtToleranceModeAsInt, this.code_bge, this.inputExperimentalFragments);
            /*
            Map<Integer, CEMSCompound> dbcandidates = this.getDbCandidates();
            float[] databaseMobilities = new float[dbcandidates.size()];
            float[] migrationTimeBuffer = new float[dbcandidates.size()];
            Double[] markerMobilities = getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, this.getIDMarker1(), this.getIDMarker2());
            float markerMobility1 = (float) ((double) markerMobilities[0]);
            float markerMobility2 = (float) ((double) markerMobilities[1]);
            double markerTimeAux1 = this.getMarkerTime1();
            float markerTime1 = (float) markerTimeAux1;
            double markerTimeAux2 = this.getMarkerTime2();
            float markerTime2 = (float) markerTimeAux2;

            Predict.doubleMarker(databaseMobilities, migrationTimeBuffer, markerMobility1, markerTime1, markerMobility2, markerTime2);

            fillMTs(dbcandidates, migrationTimeBuffer);

            Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());
            
            List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndMTs(setDBCandidates, this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance,
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts());
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);
             */

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times and 2
     * markers
     */
    private void submitCEMSEXPRMTTargetedSearch() {

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputRMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSTargetedExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.rmtTolerance, mtToleranceModeAsInt, this.code_bge, this.inputExperimentalFragments);

            // TODO QUERIES OVER THE EXP RMT
            List<CEMSFeature> cemsFeatures = this.msFacade.getCEAnnotationsFromMassesToleranceExpRMTsAndFragments(this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance, this.inputExperimentalFragments,
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.code_bge, this.ionSourceVoltage, this.allowOppositeESIMode);
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times and
     * effective mobilities
     */
    private void submitCEMSeffMobTargetedSearch() {

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputRMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSRMTExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.rmtTolerance, mtToleranceModeAsInt, this.code_bge);

            // TODO QUERIES OVER THE EXP RMT
            List<CEMSFeature> cemsFeatures = this.msFacade.getCEAnnotationsFromMassesToleranceEffMobsAndFragments(this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance, this.inputExperimentalFragments,
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.code_bge, this.ionSourceVoltage, this.allowOppositeESIMode);
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Clears the form data of targeted CEMS
     */
    @Override
    public void clearForm() {
        super.clearForm();

        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.queryInputRMTs = "";
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;

        this.queryInputGroupedSignals = "";
    }

    /**
     * Loads the demo data of targeted CEMS
     */
    public void setRMTTargetedCEDemoMasses() {
        super.setDemoMasses();
        this.setQueryInputMasses(Constants.CEDEMOMASSFORTARGETED);

        this.queryInputRMTs = Constants.CEDEMORMTFORTARGETED;
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputGroupedSignals = Constants.CEDEMOGROUPEDSIGNALSFORTARGETED;
        this.ionSourceVoltage = 100;
    }

    /**
     * Loads the demo data of targeted CEMS
     */
    public void seteffMobTargetedCEDemoMasses() {
        super.setDemoMasses();
        this.setQueryInputMasses(Constants.CEDEMOMASSFORTARGETED);

        this.queryInputRMTs = Constants.CEDEMOEFFMOBFORTARGETED;
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputGroupedSignals = Constants.CEDEMOGROUPEDSIGNALSFORTARGETED;
        this.ionSourceVoltage = 100;
    }

    public String getQueryInputRMTs() {
        return queryInputRMTs;
    }

    public void setQueryInputRMTs(String queryInputRMTs) {
        this.queryInputRMTs = queryInputRMTs;
    }

    public List<Double> getInputRMTs() {
        return inputRMTs;
    }

    public void setInputRMTs(List<Double> inputRMTs) {
        this.inputRMTs = inputRMTs;
    }

    public String getInputRMTTolerance() {
        return inputRMTTolerance;
    }

    public void setInputRMTTolerance(String inputRMTTolerance) {
        this.inputRMTTolerance = inputRMTTolerance;
    }

    public String getInputRMTModeTolerance() {
        return inputRMTModeTolerance;
    }

    public void setInputRMTModeTolerance(String inputRMTModeTolerance) {
        this.inputRMTModeTolerance = inputRMTModeTolerance;
    }

    public Integer getRmtTolerance() {
        return rmtTolerance;
    }

    public void setRmtTolerance(Integer rmtTolerance) {
        this.rmtTolerance = rmtTolerance;
    }

    public Integer getCode_bge() {
        return code_bge;
    }

    public void setCode_bge(Integer code_bge) {
        this.code_bge = code_bge;
    }

    public Integer getIonSourceVoltage() {
        return ionSourceVoltage;
    }

    public void setIonSourceVoltage(Integer ionSourceVoltage) {
        this.ionSourceVoltage = ionSourceVoltage;
    }

    public List<SelectItem> getIonSourceVoltageCandidates() {
        return ionSourceVoltageCandidates;
    }

    public String getQueryInputGroupedSignals() {
        return queryInputGroupedSignals;
    }

    public void setQueryInputGroupedSignals(String queryInputGroupedSignals) {
        this.queryInputGroupedSignals = queryInputGroupedSignals;
    }

    public List<Set<CEMSFragment>> getInputExperimentalFragments() {
        return inputExperimentalFragments;
    }

    public void setInputExperimentalFragments(List<Set<CEMSFragment>> inputExperimentalFragments) {
        this.inputExperimentalFragments = inputExperimentalFragments;
    }

}
