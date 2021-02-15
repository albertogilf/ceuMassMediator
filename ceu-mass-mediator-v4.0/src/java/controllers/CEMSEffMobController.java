package controllers;

import CEMS.CEMSEffMobExperiment;
import CEMS.CEMSFeature;
import exceptions.bufferTemperatureException;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
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
@Named("CEMSEffMobController")
public class CEMSEffMobController extends CEMSControllerAdapter implements Serializable {

    // For Eff Mob search
    private List<Double> inputEffMobs;
    private String inputEffMobTolerance;
    private String inputEffMobModeTolerance;
    private Integer effMobTolerance;

    // FOR THE COLLECTION OF effMobs
    private String queryInputEffMobs;
    

    public CEMSEffMobController() {
        super();
        this.inputEffMobTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputEffMobModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
        this.queryInputEffMobs = "";

    }

    /**
     * Submit search for a CE MS experiment using Effective Mobilities. This
     * method process the data from a ce ms experiment with effective mobilites
     * using the attributes data in the bean.
     */
    public void submitCEMSEffMobSearch() {
        int numInputMZs = readInputMZs();
        List<Double> effMobAux = Cadena.getListOfDoubles(getQueryInputEffMobs(), numInputMZs);
        this.setInputEffMobs(effMobAux);
        super.setMzTolerance(Integer.parseInt(this.getInputmzTolerance()));
        this.effMobTolerance = Integer.parseInt(this.inputEffMobTolerance);
        int chemAlphabetInt = DataFromInterfacesUtilities.getChemAlphabetAsInt(this.getChemAlphabet());

        this.effMobTolerance = Integer.parseInt(this.inputEffMobTolerance);
        int mztoleranceModeAsInt = DataFromInterfacesUtilities.toleranceTypeToInteger(super.getInputmzModeTolerance());

        try {
            this.cemsExperiment = new CEMSEffMobExperiment(super.getMzTolerance(), mztoleranceModeAsInt,
                    super.getIonMode(), super.getPolarity(),
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()),
                    null, null,
                    this.effMobTolerance, this.inputEffMobModeTolerance);

            List<CEMSFeature> cemsFeatures = this.msFacade.getCEAnnotationsFromMassesToleranceAndEffMobs(this.getInputmzs(), this.getMzTolerance(),
                    mztoleranceModeAsInt, this.inputEffMobs, this.effMobTolerance, this.inputEffMobModeTolerance,
                    getBufferFromBufferTempList(super.getBufferTemperatureCode()),
                    getTemperatureFromBufferTempList(super.getBufferTemperatureCode()), 
                    chemAlphabetInt, this.includeDeuterium,
                    this.getMassesMode(), this.getIonMode(), this.getPolarity(), this.getAdducts(), this.allowOppositeESIMode);
            this.cemsExperiment.setCEMSFeatures(cemsFeatures);
        } catch (bufferTemperatureException ex) {

        }

        //System.out.println("CEMSFEATURES: " + cemsFeatures);
    }

    /**
     * Method to clean the input formulary of CEMS EffMob
     */
    @Override
    public void clearForm() {
        super.clearForm();

        this.inputEffMobTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.queryInputEffMobs = "";
        this.inputEffMobModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
    }

    /**
     * Loads the demo Masses for CEMS eff Mob Constants
     */
    public void setCEEffMobDemoMasses() {
        super.setDemoMasses();
        this.queryInputEffMobs = Constants.CEDEMOEFFMOB;
        this.inputEffMobTolerance = Integer.toString(CE_TOLERANCE_DEFAULT_VALUE);
        this.inputEffMobModeTolerance = CE_TOLERANCE_MODE_DEFAULT_VALUE;
    }

    public String getQueryInputEffMobs() {
        return queryInputEffMobs;
    }

    public void setQueryInputEffMobs(String queryInputEffMobs) {
        this.queryInputEffMobs = queryInputEffMobs;
    }

    public List<Double> getInputEffMobs() {
        return inputEffMobs;
    }

    public void setInputEffMobs(List<Double> inputEffMobs) {
        this.inputEffMobs = inputEffMobs;
    }

    public String getInputEffMobTolerance() {
        return inputEffMobTolerance;
    }

    public void setInputEffMobTolerance(String inputEffMobTolerance) {
        this.inputEffMobTolerance = inputEffMobTolerance;
    }

    public String getInputEffMobModeTolerance() {
        return inputEffMobModeTolerance;
    }

    public void setInputEffMobModeTolerance(String inputEffMobModeTolerance) {
        this.inputEffMobModeTolerance = inputEffMobModeTolerance;
    }

    public Integer getEffMobTolerance() {
        return effMobTolerance;
    }

    public void setEffMobTolerance(Integer effMobTolerance) {
        this.effMobTolerance = effMobTolerance;
    }
    

}
