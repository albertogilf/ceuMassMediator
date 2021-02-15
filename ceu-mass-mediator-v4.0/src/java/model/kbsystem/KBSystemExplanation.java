/*
 * KBSystemExplanation.java
 *
 * Created on 27-dic-2018, 13:49:50
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package model.kbsystem;

/**
 * Explanation class of the prolog KB system
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 3.0.0 27-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class KBSystemExplanation {

    private final String coreInformation;
    private final String additionalInformation;
    private final String evidenceFor;
    private final String evidenceAgainst;

    /**
     * Creates a new instance of KBSystemExplanation
     *
     * @param coreInformation
     * @param additionalInformation
     * @param evidenceFor
     * @param evidenceAgainst
     */
    public KBSystemExplanation(String coreInformation, String additionalInformation,
            String evidenceFor,
            String evidenceAgainst) {
        this.coreInformation = coreInformation;
        this.additionalInformation = additionalInformation;
        this.evidenceFor = evidenceFor;
        this.evidenceAgainst = evidenceAgainst;
    }

    public String getCoreInformation() {
        return this.coreInformation;
    }

    public String getAdditionalInformation() {
        return this.additionalInformation;
    }

    public String getEvidenceFor() {
        return evidenceFor;
    }

    public String getEvidenceAgainst() {
        return evidenceAgainst;
    }

    public boolean isThereEvidenceFor() {
        if (this.evidenceFor == null || this.evidenceFor.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isThereEvidenceAgainst() {
        if (this.evidenceAgainst == null || this.evidenceAgainst.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "coreInformation: " + this.coreInformation + ", additionalInfo: " + this.additionalInformation
                + ", evidence For: " + this.evidenceFor
                + ", evidence Against: " + this.evidenceAgainst;
    }

}
