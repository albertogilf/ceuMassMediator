/*
 * bufferTemperatureException.java
 *
 * Created on 12-nov-2019, 0:44:51
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */

package exceptions;

/**
 * Exception for incorrect Buffer Temperature 
 * 
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1.0.0 12-nov-2019
 * 
 * @author Alberto Gil de la Fuente
 */
public class bufferTemperatureException extends Exception {

    /**
     * Creates a new instance of bufferTemperatureException
     * @param message
     */
    public bufferTemperatureException (String message)
    {
        super(message);
    }
}
