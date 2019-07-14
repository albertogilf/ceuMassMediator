/*
 * KBSystemTheoreticalCompound.java
 *
 * Created on 26-dic-2018, 21:04:50
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
public class KBSystemTheoreticalCompound {

    private final Integer identifier;
    private final String name;

    /**
     * Creates a new instance of KBSystemTheoreticalCompound
     *
     * @param identifier
     * @param name
     */
    public KBSystemTheoreticalCompound(int identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "identifier: " + this.identifier + ", name: " + this.name;
    }
}
