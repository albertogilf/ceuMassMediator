package controllers;

import CEMS.CEMSCompound;
import CEMS.CEMSFeature;
import CEMS.CEMSMTExperiment;
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
import static utilities.Utilities.getDBMobilitiesFromCEMSCompounds;

/**
 * Controller (Bean) of the application for CE/MS Searches
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 5.0, 24/04/2019
 */
@SessionScoped
@Named("CEMSMTController")
public class CEMSMTController extends CEMSMTControllerAdapter implements Serializable {

    // For Eff Mob search
    private List<Double> inputMTs;
    private String inputMTTolerance;
    private String inputMTModeTolerance;
    private Integer mtTolerance;

    // FOR THE COLLECTION OF effMobs
    private String queryInputMTs;

    public CEMSMTController() {
        super();
        this.inputMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputMTTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputMTs = "";
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times. This
     * method process the data from a ce ms experiment with absolute Migration
     * Times using 1 marker, capillary voltage and capillary length or 2
     * markers.
     *
     * @param searchMode 1: 1 marker, 2: 2 markers
     */
    public void submitCEMSMTSearch(Integer searchMode) {
        int numInputMZs = readInputMZs();
        List<Double> mtAux = Cadena.getListOfDoubles(getQueryInputMTs(), numInputMZs);
        this.setInputMTs(mtAux);

        super.setMzTolerance(Integer.parseInt(this.getInputmzTolerance()));
        this.mtTolerance = Integer.parseInt(this.inputMTTolerance);
        if (searchMode == 1) {
            submitCEMSMTSearch1Marker();
        } else if (searchMode == 2) {
            submitCEMSMTSearch2Markers();
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times, 1
     * marker and the voltage and capillary length
     */
    public void submitCEMSMTSearch1Marker() {

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSMTExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.mtTolerance, mtToleranceModeAsInt);
            Map<Integer, CEMSCompound> dbcandidates = this.getDbCandidates();
            float[] databaseMobilities = new float[dbcandidates.size()];
            float[] migrationTimeBuffer = new float[dbcandidates.size()];
            Double[] markerMobilities = getDBMobilitiesFromCEMSCompounds(dbcandidates, databaseMobilities, this.getIDMarker1(), null);
            float markerMobility = (float) ((double) markerMobilities[0]);
            double markerTimeAux = this.getMarkerTime1();
            float markerTime = (float) markerTimeAux;

            Predict.singleMarker(databaseMobilities, migrationTimeBuffer, markerMobility, markerTime, this.getCapillary_length(), this.getCapillary_voltage());

            fillMTs(dbcandidates, migrationTimeBuffer);

            Set<CEMSCompound> setDBCandidates = new TreeSet<CEMSCompound>(dbcandidates.values());
            List<CEMSFeature> cemsFeatures = CEMSFacadeInMemory.getCECompoundsFromMassesToleranceAndMTs(setDBCandidates, this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputMTs, this.mtTolerance, this.inputMTModeTolerance,
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts());
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Submit search for a CE MS experiment using absolute Migration Times and 2
     * markers
     */
    public void submitCEMSMTSearch2Markers() {

        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());
        Integer mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(getInputmzModeTolerance());
        Integer mtToleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(this.inputMTModeTolerance);

        try {
            this.cemsExperiment = new CEMSMTExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    super.getCapillary_voltage(), super.getCapillary_length(),
                    this.mtTolerance, mtToleranceModeAsInt);
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
                    mztoleranceModeAsInt, this.inputMTs, this.mtTolerance, this.inputMTModeTolerance,
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts());
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);

        } catch (bufferTemperatureException ex) {
        }
    }

    /**
     * Clear the form of CEMS MT
     */
    @Override
    public void clearForm() {
        super.clearForm();

        this.inputMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.queryInputMTs = "";
        this.inputMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
    }

    /**
     * Loads the demo data of CEMS using MT Constants
     */
    public void setCEMTDemoMasses() {
        super.setDemoMasses();
        this.queryInputMTs = Constants.CEDEMOMT;
        this.inputMTTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputMTModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
    }

    public String getQueryInputMTs() {
        return queryInputMTs;
    }

    public void setQueryInputMTs(String queryInputMTs) {
        this.queryInputMTs = queryInputMTs;
    }

    public List<Double> getInputMTs() {
        return inputMTs;
    }

    public void setInputMTs(List<Double> inputMTs) {
        this.inputMTs = inputMTs;
    }

    public String getInputMTTolerance() {
        return inputMTTolerance;
    }

    public void setInputMTTolerance(String inputMTTolerance) {
        this.inputMTTolerance = inputMTTolerance;
    }

    public String getInputMTModeTolerance() {
        return inputMTModeTolerance;
    }

    public void setInputMTModeTolerance(String inputMTModeTolerance) {
        this.inputMTModeTolerance = inputMTModeTolerance;
    }

    public Integer getMtTolerance() {
        return mtTolerance;
    }

    public void setMtTolerance(Integer mtTolerance) {
        this.mtTolerance = mtTolerance;
    }

}
