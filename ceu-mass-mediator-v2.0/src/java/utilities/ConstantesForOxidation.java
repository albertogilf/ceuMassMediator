/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author alberto
 */
public final class ConstantesForOxidation {

    public static final double HCOO_WEIGHT = 44.9982;
    public static final double PROTON_WEIGHT = 1.0073d;
    public static final double H_WEIGHT = 1.0079d;

    public static final double PCHEAD_WEIGHT = 223.09734d;

    public static final int PCHEAD_C = 8;
    public static final int PCHEAD_N = 1;
    public static final int PCHEAD_P = 1;
    public static final int PCHEAD_O = 4;
    public static final int PCHEAD_H = 18;
    public static final int FA_O = 2;

    public static final String LC_PARENTIONDEMOMASS = "842.5572";

    public static final String LC_OX_FATTYACIDDEMOMASS = "319.2285";

    public static final String LC_NONOX_FATTYACIDDEMOMASS = "255.2330";

    public static final String SC_PARENTIONDEMOMASS = "638.3675";

    public static final String SC_NONOX_FATTYACIDDEMOMASS = "255.2330";

    public static final String TOLERANCE_INICITAL_VALUE = "10";

    public static final String OXIDATIONTYPE_FOR_H_ADDUCT = "COOH";

    public static final String NEG_H_ADDUCT = "M-H";

    public static final String HCOO_ADDUCT = "M+FA-H";

    public static final int ITEMS_PER_PAGE_IN_EXCEL = 6500;

    // IF WE INCLUDED LIPIDMAPS. NOW WE ONLY SEARCH INTO IN HOUSE LIBRARY.
    //public static final String FATTY_ACIDS_LM_CLASSIFICATION = "('FA0101','FA0102','FA0103')";
    public static final String FATTY_ACIDS_LM_CLASSIFICATION = "('FA010S','FA010U')";

    public static List<String> FATTY_ACIDS_LM_CLASSIFICATION_LIST;
    static {
        List<String> listFATTY_ACIDS_LM_CLASSIFICATION_LIST_TMP = new LinkedList<>();
        listFATTY_ACIDS_LM_CLASSIFICATION_LIST_TMP.add("FA010S");
        listFATTY_ACIDS_LM_CLASSIFICATION_LIST_TMP.add("FA010U");
        FATTY_ACIDS_LM_CLASSIFICATION_LIST = Collections.unmodifiableList(listFATTY_ACIDS_LM_CLASSIFICATION_LIST_TMP);
    }

    public static final String OXIDIZED_LIPIDS_LM_CLASSIFICATION = "'GP20'";
public static final String OXIDIZED_LIPIDS_LM_CLASSIFICATION_JDBC = "GP20";
    public static final String PC_OXIDIZED_LIPIDS_LM_CLASSIFICATION = "'GP2001'";
    public static final String PC_OXIDIZED_LIPIDS_LM_CLASSIFICATION_JDBC = "GP2001";

    public static final String PC_NON_OXIDIZED_LIPIDS_LM_CLASSIFICATION = "'GP01'";
    public static final String PC_NON_OXIDIZED_LIPIDS_LM_CLASSIFICATION_JDBC = "GP01";

}
