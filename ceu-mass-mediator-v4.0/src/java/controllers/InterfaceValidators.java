/*
 * InterfaceValidators.java
 *
 * Created on 22-may-2019, 21:17:19
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package controllers;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * çClass for interface html validators
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.0.0 22-may-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class InterfaceValidators {

    /**
     * Validates the capillary voltage to be an Integer between 0 and 300
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateInputCapillaryVoltage(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        try {
            Integer temperature = (Integer) arg2;
            if (temperature <= 1) {
                throw new ValidatorException(new FacesMessage("The capillary voltage should be a number between 0 and 300"));
            } else if (temperature > 300) {
                throw new ValidatorException(new FacesMessage("The capillary voltage should be a number between 0 and 300"));
            }
        } catch (ClassCastException cce) {
            throw new ValidatorException(new FacesMessage("The capillary voltage should be a number between 0 and 300"));
        }
    }
    
    /**
     * Validates the capillary length to be an Integer between 0 and 10000 mm (10
     * m)
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateMarkerTime(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        try {
            Double markerTime = (Double) arg2;
            if (markerTime <= 0) {
                throw new ValidatorException(new FacesMessage("The marker time should be a number between 0 and 120 min"));
            } else if (markerTime > 10000) {
                throw new ValidatorException(new FacesMessage("The marker time should be a number between 0 and 120 min"));
            }
        } catch (ClassCastException cce) {
            throw new ValidatorException(new FacesMessage("The marker time should be a number between 0 and 120 min"));
        }
    }

    /**
     * Validates the capillary length to be an Integer between 0 and 10000 mm (10
     * m)
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateInputCapillaryLength(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        try {
            Integer temperature = (Integer) arg2;
            if (temperature <= 1) {
                throw new ValidatorException(new FacesMessage("The capillary length should be a number between 0 and 10000"));
            } else if (temperature > 10000) {
                throw new ValidatorException(new FacesMessage("The capillary length should be a number between 0 and 10000"));
            }
        } catch (ClassCastException cce) {
            throw new ValidatorException(new FacesMessage("The capillary length should be a number between 0 and 10000"));
        }
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateInputTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float inputTol = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputTol = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be a number between 0 and 1000"));
        }
        if (inputTol <= 0) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        } else if (inputTol > 1000) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        }
    }

    /**
     * Validates the input single mass to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateInputSingleMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float inputTol = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputTol = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Input mass should be a number between 0 and 10000"));
        }
        if (inputTol <= 0) {
            throw new ValidatorException(new FacesMessage("Input mass should be between 0 and 10000"));
        } else if (inputTol > 10000) {
            throw new ValidatorException(new FacesMessage("Input mass should be between 0 and 10000"));
        }
    }

    /**
     * Validates the retention Time to be a float between 0 and 1000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateSingleRT(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        String RTString = (String) arg2;
        RTString = RTString.replace(",", ".");
        float RT;
        try {
            if (RTString.equals("")) {

            } else {
                RT = Float.parseFloat(RTString);
                if (RT <= 0) {
                    throw new ValidatorException(new FacesMessage("RT should be between 0 and 1000"));
                } else if (RT > 1000) {
                    throw new ValidatorException(new FacesMessage("RT should be between 0 and 1000"));
                }
            }
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("RT should be a number between 0 and 1000"));
        }
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateParentIonMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float parentIonMass = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            parentIonMass = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be a number between 0 and 10000"));
        }
        if (parentIonMass <= 0) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be between 0 and 10000"));
        } else if (parentIonMass > 10000) {
            throw new ValidatorException(new FacesMessage("Parent ion mass should be between 0 and 10000"));
        }
    }

    /**
     * Validates the the Fatty Acid Mass to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateFattyAcidMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float fattyAcidMass = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            fattyAcidMass = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be a number between 0 and 10000"));
        }
        if (fattyAcidMass <= 0) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be between 0 and 10000"));
        } else if (fattyAcidMass > 10000) {
            throw new ValidatorException(new FacesMessage("Fatty acid mass should be between 0 and 10000"));
        }
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateInputRTTolerance(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        float inputRTTol = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputRTTol = Float.parseFloat((String) input);
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The RT window should be a number between 0 and 5"));
        }
        if (inputRTTol <= 0) {
            throw new ValidatorException(new FacesMessage("The RT window should be between 0 and 5"));
        } else if (inputRTTol > 5) {
            throw new ValidatorException(new FacesMessage("The RT window should be between 0 and 5"));
        }
    }

    /**
     * Validates the retention Time to be a float between 0 and 1000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    public static void validateNameAndFormula(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {

        String formula = (String) arg2;
        UIInput uiInputFormula = (UIInput) arg1.getAttributes()
                .get("name");
        String name = uiInputFormula.getSubmittedValue()
                .toString();
        try {
            if (name.length() > 3) {

            } else if (formula.length() > 3) {

            } else {
                throw new ValidatorException(new FacesMessage("Name or formula length should be larger than 4 characters"));
            }

        } catch (NullPointerException npe) {
            throw new ValidatorException(new FacesMessage("Null pointer exception validating name"));
        }
    }

    public static void validatePrecursorIonMass(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {

        float inputPrecursorMZ = -1;
        try {
            String input = (String) arg2;
            input = input.replace(",", ".");
            inputPrecursorMZ = Float.parseFloat((String) input);

        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The precursor ion mass should be a number between 0 and 2000"));
        }
        if (inputPrecursorMZ <= 0) {
            throw new ValidatorException(new FacesMessage("The precursor ion mass should be between 0 and 2000"));
        } else if (inputPrecursorMZ > 2000) {
            throw new ValidatorException(new FacesMessage("The precursor ion mass should be between 0 and 2000"));
        }
    }

    public static void validatePeaks(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {

        String input = (String) arg2;
        String[] p = input.split("\n");

        for (String p1 : p) {
            String[] mzi = p1.split(" ");
            //System.out.println(mzi[0]);
            //System.out.println(mzi[1]);
            if (!isNumeric(mzi[0])) {
                throw new ValidatorException(new FacesMessage("The peaks must be a list of numbers, one per line: m/z [space] intensity"));
            }
            if (!isNumeric(mzi[1])) {
                throw new ValidatorException(new FacesMessage("The peaks must be a list of numbers, one per line: m/z [space] intensity"));
            }
        }
    }

    private static boolean isNumeric(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}
