/*
 * KBSystemEmpiricalCompound.java
 *
 * Created on 26-dic-2018, 21:04:31
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package model.kbsystem;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 26-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class KBSystemEmpiricalCompound {

    private final Double mass;
    private final Double retentionTime;

    /**
     * Creates a new instance of KBSystemEmpiricalCompound
     *
     * @param mass
     * @param retentionTime
     */
    public KBSystemEmpiricalCompound(Double mass, Double retentionTime) {
        this.mass = mass;
        this.retentionTime = retentionTime;
    }

    public Double getMass() {
        return mass;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    @Override
    public String toString() {
        return "mass: " + this.mass + ", RT: " + this.retentionTime;
    }

}
