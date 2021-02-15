/*
 * KBSystemPutativeAnnotation.java
 *
 * Created on 26-dic-2018, 21:03:25
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package model.kbsystem;

/**
 * Input compound for the KB System.
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 26-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class KBSystemPutativeAnnotation {

    private final Integer identifier;
    private final KBSystemEmpiricalCompound empiricalCompound;
    private final KBSystemTheoreticalCompound theoreticalCompound;
    private final String adduct;

    /**
     * Creates a new instance of KBSystemCompound
     *
     * @param identifier
     * @param empiricalCompound
     * @param theoreticalCompound
     * @param adduct
     */
    public KBSystemPutativeAnnotation(Integer identifier, KBSystemEmpiricalCompound empiricalCompound,
            KBSystemTheoreticalCompound theoreticalCompound, String adduct) {
        this.identifier = identifier;
        this.empiricalCompound = empiricalCompound;
        this.theoreticalCompound = theoreticalCompound;
        this.adduct = adduct;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public KBSystemEmpiricalCompound getEmpiricalCompound() {
        return empiricalCompound;
    }

    public KBSystemTheoreticalCompound getTheoreticalCompound() {
        return theoreticalCompound;
    }

    public String getAdduct() {
        return adduct;
    }

    @Override
    public String toString() {
        return "id: " + this.identifier + ", EmpCom: " + this.empiricalCompound 
                + ", TheoComp: " + this.theoreticalCompound
                + ", adduct: " + this.adduct;
    }
}
