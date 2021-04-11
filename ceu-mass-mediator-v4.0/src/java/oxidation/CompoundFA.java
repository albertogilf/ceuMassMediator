/*
 * FACompound.java
 *
 * Created on 22-may-2018, 18:39:42
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package oxidation;

import compound.Classyfire_Classification;
import compound.CMMCompound;
import compound.LM_Classification;
import compound.Lipids_Classification;
import compound.Structure;
import java.util.List;
import pathway.Pathway;
import utilities.Utilities;

/**
 * CompoundFA describes the Fatty Acids based on the attributes of a normal
 * compound + the EM, the oxidation type (empty string if it is not oxidized)
 * and the mass to Search (usually is the EM - the oxidation type weight)
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 22-may-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class CompoundFA extends CMMCompound {

    private final Double FAEM;
    private final String oxidationType;
    private final Integer ppmIncrement;

    private final int numCarbons;
    private final int numDoubleBonds;
    private final boolean isOxidized;

    /**
     * Constructor of CMMCompound FA. It has all the attributes from the
     * compound
     *
     * @param compound_id
     * @param mass
     * @param formula
     * @param compound_name
     * @param formula_type
     * @param compound_type
     * @param compound_status
     * @param structure
     * @param lm_classification
     * @param classyfire_classification
     * @param lipids_classification
     * @param oxidationType
     * @param pathways
     * @param oxidizedFAEM
     * @param masstoSearchForOxidizedFA
     */
    public CompoundFA(int compound_id, double mass, String formula, String compound_name,
            int formula_type, int compound_type, int compound_status,
            Structure structure,
            LM_Classification lm_classification,
            List<Classyfire_Classification> classyfire_classification,
            Lipids_Classification lipids_classification,
            List<Pathway> pathways,
            Double oxidizedFAEM,
            Double masstoSearchForOxidizedFA,
            String oxidationType) {
        super(compound_id, mass, formula, compound_name,
                "", formula_type, compound_type, compound_status, 0, 0,
                "", "", "", "", "", 0, 0, 0, "", 0, "", "", "", "", 0, 0,
                structure, lm_classification, classyfire_classification, lipids_classification, pathways);
        this.FAEM = oxidizedFAEM;
        this.oxidationType = oxidationType;
        this.isOxidized = !"".equals(oxidationType);

        this.numCarbons = lipids_classification.getNumberCarbons();
        this.numDoubleBonds = lipids_classification.getNumberDoubleBounds();
        this.ppmIncrement = Utilities.calculatePPMIncrement(masstoSearchForOxidizedFA, mass);
    }

    public Double getFAEM() {
        return this.FAEM;
    }

    public String getOxidationType() {
        return this.oxidationType;
    }

    public boolean isIsOxidized() {
        return this.isOxidized;
    }

    public Integer getPpmIncrement() {
        return this.ppmIncrement;
    }

    /**
     * NOT USED
     *
     * @return the name for the presentation of the FA
     */
    @Override
    public String getCompound_name() {
        String name;
        if (super.getCompound_id() == 0) {

            name = "No Fatty Acids found for experimental mass " + this.FAEM + " and oxidation type: "
                    + this.oxidationType;
        } else {
            name = super.getCompound_name();
            //return this.newCompounds.getCompoundName();
        }
        return name;
    }

    public Integer calculateHydrogens() {
        int numHydrogens;
        numHydrogens = this.numCarbons * 2 - 2 * this.numDoubleBonds;
        return numHydrogens;
    }

    public int getNumCarbons() {
        return this.numCarbons;
    }

    public int getNumDoubleBonds() {
        return this.numDoubleBonds;
    }
}
