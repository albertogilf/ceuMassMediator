package controllers;

import CEMS.CEMSCompound;
import CEMS.CEMSFeature;
import CEMS.CEMSRMTExperiment;
import ch.unige.migpred.Predict;
import exceptions.bufferTemperatureException;
import facades.CEMSFacadeInMemory;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import static utilities.CEMSLists.getBufferFromBufferTempList;
import static utilities.CEMSLists.getTemperatureFromBufferTempList;
import utilities.Cadena;
import utilities.DataFromInterfacesUtilities;
import utilities.Constants;
import static utilities.Constants.CE_TOLERANCE_MODE_DEFAULT_VALUE;
import static utilities.Constants.CE_TOLERANCE_DEFAULT_VALUE;
import static utilities.Utilities.fillMTs;
import static utilities.Utilities.fillRMTs;
import static utilities.Utilities.getDBMobilitiesFromCEMSCompounds;

/**
 * Controller (Bean) of the application for CE/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2019
 */
@SessionScoped
@Named("CEMSRMTController")
public class CEMSRMTController extends CEMSMTControllerAdapter implements Serializable {

    // For RMT search
    private List<Double> inputRMTs;
    private String inputRMTTolerance;
    private String inputRMTModeTolerance;
    private Integer rmtTolerance;

    // FOR THE COLLECTION OF RMTs
    private String queryInputRMTs;

    private Integer code_bge;

    public CEMSRMTController() {
        super();
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputRMTs = "";
        this.code_bge = 180838; // CMM ID of Methionine Sulfone!

    }

    /**
     * Submit search for a CE MS experiment using Relative migration times. This
     * method process the data from a ce ms experiment with relative migration
     * times using the attributes data in the bean.
     *
     * @param searchMode: 1: 1 marker; 2: 2 markers; 3: experimental MTs
     * (methionine sulfone)
     */
    public void submitCEMSRMTSearch(Integer searchMode) {
        int numInputMZs = readInputMZs();
        List<Double> rmtAux = Cadena.getListOfDoubles(getQueryInputRMTs(), numInputMZs);
        this.setInputRMTs(rmtAux);

        super.setMzTolerance(Integer.parseInt(this.getInputmzTolerance()));
        this.rmtTolerance = Integer.parseInt(this.inputRMTTolerance);
        if (searchMode == 1) {
            submitCEMSRMTSearch1Marker();
        } else if (searchMode == 2) {
            submitCEMSRMTSearch2Markers();
        } else if (searchMode == 3) {
            submitCEMSEXPRMTSearch();
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times, 1
     * marker and the voltage and capillary length
     */
    private void submitCEMSRMTSearch1Marker() {

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

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times and 2
     * markers
     */
    private void submitCEMSRMTSearch2Markers() {

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

            Double bgeMT = fillRMTs(dbcandidates, migrationTimeBuffer, this.code_bge);

            Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());
            List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndRMTs(setDBCandidates, this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance,
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.code_bge, bgeMT);
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times and 2
     * markers
     */
    private void submitCEMSEXPRMTSearch() {

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

            List<CEMSFeature> cemsFeatures = this.msFacade.getCEAnnotationsFromMassesToleranceAndExpRMTs(this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputRMTs, this.rmtTolerance, this.inputRMTModeTolerance,
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.code_bge, this.allowOppositeESIMode);
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Clears the formulary data of CEMS using RMT
     */
    @Override
    public void clearForm() {
        super.clearForm();
        this.queryInputRMTs = "";
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
    }

    /**
     * Loads the demo data of CEMS using RMT
     */
    public void setCERMTDemoMasses() {
        super.setDemoMasses();
        this.queryInputRMTs = Constants.CEDEMORMT;
        this.inputRMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputRMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;

        if (super.getBufferTemperatureCode().equals(1)) {
            this.code_bge = 180838; // CMM ID of Methionine Sulfone!
        } else if (super.getBufferTemperatureCode().equals(2)) {
            this.code_bge = 73414; // CMM ID of Paracetamol!
        }
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

    public String getInputRMTTolerance() {
        return inputRMTTolerance;
    }

}
