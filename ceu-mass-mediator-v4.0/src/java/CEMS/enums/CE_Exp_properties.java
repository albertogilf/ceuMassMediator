/*
 * CE_Exp_properties.java
 *
 * Created on 30-dic-2019, 10:10:02
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS.enums;

import exceptions.bufferTemperatureException;
import java.util.Objects;
import utilities.Constants;

/**
 * Class to represent the possible experimental properties from CE MS. The eff
 * mob only depends on the temperature and the buffer
 *
 * @version $Revision: 1.1.1.1 $
 * @since 4.1.2 18-dic-2019
 *
 * @author Alberto Gil de la Fuente
 */
public class CE_Exp_properties {

    public enum CE_BUFFER_ENUM {
        FORMIC(1) {
            @Override
            public String toString() {
                return "formic";
            }
        },
        ACETIC(2) {
            @Override
            public String toString() {
                return "acetic";
            }
        };

        private final Integer bufferCode;

        private CE_BUFFER_ENUM(Integer buffer_code) {
            this.bufferCode = buffer_code;
        }

        public Integer getBufferCode() {
            return this.bufferCode;
        }

        /**
         *
         * @param buffer_code
         * @return
         * @throws exceptions.bufferTemperatureException
         */
        public static CE_BUFFER_ENUM fromBufferCode(Integer buffer_code)
                throws bufferTemperatureException {
            for (CE_BUFFER_ENUM ce_buffer_enum : CE_BUFFER_ENUM.values()) {
                if (Objects.equals(ce_buffer_enum.bufferCode, buffer_code)) {
                    return ce_buffer_enum;
                }
            }
            throw new bufferTemperatureException("Incorrect buffer. Only acetic and buffer are accepted");
        }
    }

    /**
     * EXP Properties in CE contains the buffer used(CE_BUFFER_ENUM.FORMIC or
     * CE_BUFFER_ENUM.ACETIC, the temperature (20,25ºC) and the polarity (1,2)
     */
    public enum CE_EXP_PROP_ENUM {
        FORMIC_20_POS_DIR(CE_BUFFER_ENUM.FORMIC, 20, 1, 1),
        FORMIC_20_POS_INV(CE_BUFFER_ENUM.FORMIC, 20, 1, 2),
        FORMIC_20_NEG_DIR(CE_BUFFER_ENUM.FORMIC, 20, 2, 1),
        FORMIC_20_NEG_INV(CE_BUFFER_ENUM.FORMIC, 20, 2, 2),
        FORMIC_25_POS_DIR(CE_BUFFER_ENUM.FORMIC, 25, 1, 1),
        FORMIC_25_POS_INV(CE_BUFFER_ENUM.FORMIC, 25, 1, 2),
        FORMIC_25_NEG_DIR(CE_BUFFER_ENUM.FORMIC, 25, 2, 1),
        FORMIC_25_NEG_INV(CE_BUFFER_ENUM.FORMIC, 25, 2, 2),
        ACETIC_20_POS_DIR(CE_BUFFER_ENUM.ACETIC, 20, 1, 1),
        ACETIC_20_POS_INV(CE_BUFFER_ENUM.ACETIC, 20, 1, 2),
        ACETIC_20_NEG_DIR(CE_BUFFER_ENUM.ACETIC, 20, 2, 1),
        ACETIC_20_NEG_INV(CE_BUFFER_ENUM.ACETIC, 20, 2, 2),
        ACETIC_25_POS_DIR(CE_BUFFER_ENUM.ACETIC, 25, 1, 1),
        ACETIC_25_POS_INV(CE_BUFFER_ENUM.ACETIC, 25, 1, 2),
        ACETIC_25_NEG_DIR(CE_BUFFER_ENUM.ACETIC, 25, 2, 1),
        ACETIC_25_NEG_INV(CE_BUFFER_ENUM.ACETIC, 25, 2, 2);

        private final CE_BUFFER_ENUM buffer;
        private final Integer temperature;
        private final Integer ionization_mode;
        private final Integer polarity;

        private CE_EXP_PROP_ENUM(CE_BUFFER_ENUM buffer, Integer temperature, Integer ionization_mode, Integer polarity) {
            this.buffer = buffer;
            this.temperature = temperature;
            this.ionization_mode = ionization_mode;
            this.polarity = polarity;
        }

        public String getBuffer() {
            return this.buffer.toString();
        }

        public Integer getTemperature() {
            return this.temperature;
        }

        public Integer getIonization_mode() {
            return ionization_mode;
        }

        public Integer getPolarity() {
            return polarity;
        }

        @Override
        public String toString() {
            return this.buffer.toString() + " " + Integer.toString(this.temperature) + "ºC"
                    + " IONIZATION_MODE: " + Integer.toString(this.ionization_mode)
                    + " POLARITY: " + Integer.toString(this.polarity);
        }

        /**
         *
         * @param buffer
         * @param temperature
         * @param ionization_mode
         * @param polarity
         * @return
         * @throws bufferTemperatureException
         */
        public static CE_EXP_PROP_ENUM fromBufferTemperatureIonModeAndPolarity(CE_BUFFER_ENUM buffer,
                Integer temperature, Integer ionization_mode, Integer polarity)
                throws bufferTemperatureException {
            // INCLUDE NEW TEMPERATURES WHEN NEEDED
            if (!temperature.equals(25) && !temperature.equals(20)) {
                throw new bufferTemperatureException("Incorrect temperature. Only 20 and 25ºC are accepted");
            }
            if (!ionization_mode.equals(1) && !ionization_mode.equals(2)) {
                throw new bufferTemperatureException("Incorrect Ionization Mode. 1: positive, 2: negative");
            }
            if (!polarity.equals(1) && !polarity.equals(2)) {
                throw new bufferTemperatureException("Incorrect Polarity. 1: direct, 2: reverse");
            }
            for (CE_EXP_PROP_ENUM exp_prop_enum : CE_EXP_PROP_ENUM.values()) {
                if (Objects.equals(exp_prop_enum.temperature, temperature) && exp_prop_enum.buffer == buffer
                        && exp_prop_enum.ionization_mode == ionization_mode && exp_prop_enum.polarity == polarity) {

                    return exp_prop_enum;
                }
            }

            throw new bufferTemperatureException("Incorrect buffer. Only acetic and buffer are accepted");
        }

        /**
         *
         * @param code
         * @return
         * @throws bufferTemperatureException
         */
        public static CE_EXP_PROP_ENUM fromIntegerCode(Integer code)
                throws bufferTemperatureException {
            boolean found = false;
            for (CE_EXP_PROP_ENUM exp_prop_enum : CE_EXP_PROP_ENUM.values()) {
                if (Objects.equals(code, exp_prop_enum.getCode())) {
                    return exp_prop_enum;
                }
            }
            if (!found) {
                throw new bufferTemperatureException("Incorrect buffer. Only acetic and buffer are accepted");
            }
            return null;
        }

        /**
         *
         * @return the code of the enum: 1: buffer Formic acid 20ºC 2: buffer
         * Acetic acid 20ºC 3: buffer Formic acid 25ºC 4: buffer Acetic acid
         * 25ºC and null otherwise
         */
        public Integer getCode() {
            if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 20
                    && this.ionization_mode == 1 && this.polarity == 1) {
                return 1;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 20
                    && this.ionization_mode == 1 && this.polarity == 2) {
                return 2;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 20
                    && this.ionization_mode == 2 && this.polarity == 1) {
                return 3;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 20
                    && this.ionization_mode == 2 && this.polarity == 2) {
                return 4;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 25
                    && this.ionization_mode == 1 && this.polarity == 1) {
                return 5;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 25
                    && this.ionization_mode == 1 && this.polarity == 2) {
                return 6;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 25
                    && this.ionization_mode == 2 && this.polarity == 1) {
                return 7;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC && this.temperature == 25
                    && this.ionization_mode == 2 && this.polarity == 2) {
                return 8;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 20
                    && this.ionization_mode == 1 && this.polarity == 1) {
                return 9;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 20
                    && this.ionization_mode == 1 && this.polarity == 2) {
                return 10;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 20
                    && this.ionization_mode == 2 && this.polarity == 1) {
                return 11;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 20
                    && this.ionization_mode == 2 && this.polarity == 2) {
                return 12;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 25
                    && this.ionization_mode == 1 && this.polarity == 1) {
                return 13;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 25
                    && this.ionization_mode == 1 && this.polarity == 2) {
                return 14;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 25
                    && this.ionization_mode == 2 && this.polarity == 1) {
                return 15;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC && this.temperature == 25
                    && this.ionization_mode == 2 && this.polarity == 2) {
                return 16;
            }
            return null;
        }

        /**
         *
         * @param allowOppositeESIMode
         * @return the name of the mysql view containing the eff mob from both
         * ion modes and polarities
         */
        public String getViewName(Boolean allowOppositeESIMode) {
            if(!allowOppositeESIMode)
            {
                return Constants.CEMS_TABLE_NAME;
            }
            if (this.buffer == CE_BUFFER_ENUM.FORMIC
                    && this.ionization_mode == 1) {
                return Constants.CEMS_VIEW_FORMIC_POS;
            } else if (this.buffer == CE_BUFFER_ENUM.FORMIC
                    && this.ionization_mode == 2) {
                return Constants.CEMS_VIEW_FORMIC_NEG;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC
                    && this.ionization_mode == 1) {
                return Constants.CEMS_VIEW_ACETIC_POS;
            } else if (this.buffer == CE_BUFFER_ENUM.ACETIC
                    && this.ionization_mode == 2) {
                return Constants.CEMS_VIEW_ACETIC_POS;
            }
            return Constants.CEMS_TABLE_NAME;
        }

    }

}
