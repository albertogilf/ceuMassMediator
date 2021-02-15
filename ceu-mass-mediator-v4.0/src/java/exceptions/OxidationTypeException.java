/*
 * OxidationTypeException.java
 *
 * Created on 14-oct-2019, 20:27:12
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */

package exceptions;

/**
 * Exception for oxidation types not allowed
 * 
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 14-oct-2019
 * 
 * @author Alberto Gil de la Fuente
 */
public class OxidationTypeException extends Exception {

    /**
     * Creates a new instance of oxidationTypeException
     * @param message
     */
    public OxidationTypeException (String message)
    {
        super(message);
    }
}
