/*
 * CEMSLists.java
 *
 * Created on 11-nov-2019, 23:39:12
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities;

import exceptions.bufferTemperatureException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 * Class containing static lists for CEMS Interfaces
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.1.0.0 11-nov-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CEMSLists {

    public static final List<SelectItem> BGEEXPCANDIDATES;

    static {
        List<SelectItem> BGEEXPCANDIDATESTMP = new LinkedList<>();
        BGEEXPCANDIDATESTMP.add(new SelectItem(180838, "L-Methionine sulfone"));
        BGEEXPCANDIDATESTMP.add(new SelectItem(73414, "Paracetamol"));
        /*
        BUFFERLISTTMP.add(new SelectItem(3, "formic acid 1M / 20ºC"));
        BUFFERLISTTMP.add(new SelectItem(4, "formic acid 1M / 20ºC"));
         */
        BGEEXPCANDIDATES = Collections.unmodifiableList(BGEEXPCANDIDATESTMP);
    }

    public static final List<SelectItem> BUFFERTEMPERATURELIST;

    static {
        List<SelectItem> BUFFERLISTTMP = new LinkedList<>();
        BUFFERLISTTMP.add(new SelectItem(1, "formic acid 1M / 20ºC"));
        BUFFERLISTTMP.add(new SelectItem(2, "acetic acid 10% v:v / 25ºC"));
        /*
        BUFFERLISTTMP.add(new SelectItem(3, "formic acid 1M / 20ºC"));
        BUFFERLISTTMP.add(new SelectItem(4, "formic acid 1M / 20ºC"));
         */
        BUFFERTEMPERATURELIST = Collections.unmodifiableList(BUFFERLISTTMP);
    }
    
    public static final List<SelectItem> POLARITYLIST;

    static {
        List<SelectItem> POLARITYLISTTMP = new LinkedList<>();
        POLARITYLISTTMP.add(new SelectItem(1, "Direct"));
        POLARITYLISTTMP.add(new SelectItem(2, "Reverse"));
        /*
        BUFFERLISTTMP.add(new SelectItem(3, "formic acid 1M / 20ºC"));
        BUFFERLISTTMP.add(new SelectItem(4, "formic acid 1M / 20ºC"));
         */
        POLARITYLIST = Collections.unmodifiableList(POLARITYLISTTMP);
    }

    public static final List<SelectItem> ION_SOURCE_VOLTAGES_LIST;

    static {
        List<SelectItem> ION_SOURCE_VOLTAGES_LISTTMP = new LinkedList<>();
        ION_SOURCE_VOLTAGES_LISTTMP.add(new SelectItem(100, "100V"));
        ION_SOURCE_VOLTAGES_LISTTMP.add(new SelectItem(200, "200V"));
        /*
        BUFFERLISTTMP.add(new SelectItem(3, "formic acid 1M / 20ºC"));
        BUFFERLISTTMP.add(new SelectItem(4, "formic acid 1M / 20ºC"));
         */
        ION_SOURCE_VOLTAGES_LIST = Collections.unmodifiableList(ION_SOURCE_VOLTAGES_LISTTMP);
    }

    /**
     *
     * @param bufferTemperature 0 for formic acid 1M / 20ºC, 1 for acetic acid
     * 1M / 25ºC
     * @return the buffer from the bufferTemperature Option
     * @throws bufferTemperatureException
     */
    public static Integer getBufferFromBufferTempList(Integer bufferTemperature)
            throws bufferTemperatureException {
        if (null == bufferTemperature) {
            throw new bufferTemperatureException("Buffer or temperature incorrect. "
                    + "Call the method with 0 for formic acid 1M / 20ºC, 1 for acetic acid 10% v:v / 25ºC");
        } else {
            switch (bufferTemperature) {
                case 1:
                    return 1; // Formic acid code / 20ºC
                case 2:
                    return 2; // acetic acid code / 25ºC
                default:
                    throw new bufferTemperatureException("Buffer or temperature incorrect. "
                            + "Call the method with 0 for formic acid 1M / 20ºC, 1 for acetic acid 1M / 25ºC");
            }
        }
    }

    /**
     *
     * @param bufferTemperature 0 for formic acid 1M / 20ºC, 1 for acetic acid
     * 1M / 25ºC
     * @return the temperature from the bufferTemperature Option
     * @throws bufferTemperatureException
     */
    public static Integer getTemperatureFromBufferTempList(Integer bufferTemperature)
            throws bufferTemperatureException {
        if (null == bufferTemperature) {
            throw new bufferTemperatureException("Buffer or temperature incorrect. "
                    + "Call the method with 1 for formic acid 1M / 20ºC, 2 for acetic acid 10% v:v / 25ºC");
        } else {
            switch (bufferTemperature) {
                case 1:
                    return 20; // Formic acid code / 20ºC
                case 2:
                    return 25; // acetic acid code / 25ºC
                default:
                    throw new bufferTemperatureException("Buffer or temperature incorrect. "
                            + "Call the method with 0 for formic acid 1M / 20ºC, 1 for acetic acid 10% v:v / 25ºC");
            }
        }
    }

}
