package utilities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class AdductsLists {

    public static final List<SelectItem> LISTNEUTRALMODESFORCEMS;

    static {
        List<SelectItem> listNeutralModesTMP = new LinkedList<>();
        listNeutralModesTMP.add(new SelectItem(0, "Neutral"));
        LISTNEUTRALMODESFORCEMS = Collections.unmodifiableList(listNeutralModesTMP);
    }
    
    public static final List<SelectItem> LISTNEUTRALMODES;

    static {
        List<SelectItem> listNeutralModesTMP = new LinkedList<>();
        listNeutralModesTMP.add(new SelectItem(0, "Neutral"));
        listNeutralModesTMP.add(new SelectItem(1, "Positive Mode"));
        listNeutralModesTMP.add(new SelectItem(2, "Negative Mode"));
        LISTNEUTRALMODES = Collections.unmodifiableList(listNeutralModesTMP);
    }

    public static final List<SelectItem> LISTIONIZEDMODES;

    static {
        List<SelectItem> listIonizedModesTMP = new LinkedList<>();
        listIonizedModesTMP.add(new SelectItem(1, "Positive Mode"));
        listIonizedModesTMP.add(new SelectItem(2, "Negative Mode"));
        LISTIONIZEDMODES = Collections.unmodifiableList(listIonizedModesTMP);
    }

    public static final Map<String, String> MAPMZPOSITIVEADDUCTS;

    static {
        Map<String, String> mapMZPositiveAdductsTMP = new LinkedHashMap<>();
        mapMZPositiveAdductsTMP.put("M+H", "-1.007276");
        mapMZPositiveAdductsTMP.put("M+2H", "-1.007276");
        mapMZPositiveAdductsTMP.put("M+Na", "-22.989218");
        mapMZPositiveAdductsTMP.put("M+K", "-38.963158");
        mapMZPositiveAdductsTMP.put("M+NH4", "-18.033823");
        mapMZPositiveAdductsTMP.put("M+H-H2O", "17.0032");
        mapMZPositiveAdductsTMP.put("M+H+NH4", "-9.52055");
        mapMZPositiveAdductsTMP.put("2M+H", "-1.007276");
        mapMZPositiveAdductsTMP.put("2M+Na", "-22.989218");
        mapMZPositiveAdductsTMP.put("M+H+HCOONa", "-68.9946");
        mapMZPositiveAdductsTMP.put("2M+H-H2O", "17.0032");
        mapMZPositiveAdductsTMP.put("M+3H", "-1.007276");
        mapMZPositiveAdductsTMP.put("M+2H+Na", "-8.33459");
        mapMZPositiveAdductsTMP.put("M+H+2K", "-26.3112");
        mapMZPositiveAdductsTMP.put("M+H+2Na", "-15.76619");
        mapMZPositiveAdductsTMP.put("M+3Na", "-22.989218");
        mapMZPositiveAdductsTMP.put("M+H+Na", "-11.998247");
        mapMZPositiveAdductsTMP.put("M+H+K", "-19.985217");
        mapMZPositiveAdductsTMP.put("M+ACN+2H", "-21.52055");
        mapMZPositiveAdductsTMP.put("M+2Na", "-22.989218");
        mapMZPositiveAdductsTMP.put("M+2ACN+2H", "-42.033823");
        mapMZPositiveAdductsTMP.put("M+3ACN+2H", "-62.547097");
        mapMZPositiveAdductsTMP.put("M+CH3OH+H", "-33.033489");
        mapMZPositiveAdductsTMP.put("M+ACN+H", "-42.033823");
        mapMZPositiveAdductsTMP.put("M+2Na-H", "-44.97116");
        mapMZPositiveAdductsTMP.put("M+IsoProp+H", "-61.06534");
        mapMZPositiveAdductsTMP.put("M+ACN+Na", "-64.015765");
        mapMZPositiveAdductsTMP.put("M+2K-H", "-76.91904");
        mapMZPositiveAdductsTMP.put("M+DMSO+H", "-79.02122");
        mapMZPositiveAdductsTMP.put("M+2ACN+H", "-83.06037");
        mapMZPositiveAdductsTMP.put("M+IsoProp+Na+H", "-84.05511");
        mapMZPositiveAdductsTMP.put("2M+NH4", "-18.033823");
        mapMZPositiveAdductsTMP.put("2M+K", "-38.963158");
        mapMZPositiveAdductsTMP.put("2M+ACN+H", "-42.033823");
        mapMZPositiveAdductsTMP.put("2M+ACN+Na", "-64.015765");
        mapMZPositiveAdductsTMP.put("3M+H", "-1.007276");
        mapMZPositiveAdductsTMP.put("3M+Na", "-22.989218");
        mapMZPositiveAdductsTMP.put("M+H-2H2O", "35.0127");
        mapMZPositiveAdductsTMP.put("M+NH4-H2O", "-0.0227");
        mapMZPositiveAdductsTMP.put("M+Li", "-7.0160");
        mapMZPositiveAdductsTMP.put("2M+2H+3H2O", "-28.02312");
        mapMZPositiveAdductsTMP.put("M+H+CH3COOH", "-60.021129");
        mapMZPositiveAdductsTMP.put("M+H+CH3COONa", "-82.00307");
        mapMZPositiveAdductsTMP.put("M+F+H", "-20.00623");
        //mapMZPositiveAdductsTMP.put("M+2", "-2.0173");
        MAPMZPOSITIVEADDUCTS = Collections.unmodifiableMap(mapMZPositiveAdductsTMP);
    }
    
    public static final Map<String, String> MAPMZNEGATIVEADDUCTS;

    static {
        Map<String, String> mapMZNegativeAdductsTMP = new LinkedHashMap<>();
        mapMZNegativeAdductsTMP.put("M-H", "1.007276");
        mapMZNegativeAdductsTMP.put("M+Cl", "-34.969402");
        mapMZNegativeAdductsTMP.put("M+FA-H", "-44.998201");
        mapMZNegativeAdductsTMP.put("M-H-H2O", "19.01839");
        mapMZNegativeAdductsTMP.put("M-H+HCOONa", "-66.9802");
        mapMZNegativeAdductsTMP.put("M-H+CH3COONa", "-80.99525");
        mapMZNegativeAdductsTMP.put("2M-H", "1.007276");
        mapMZNegativeAdductsTMP.put("M-3H", "1.007276");
        mapMZNegativeAdductsTMP.put("M-2H", "1.007276");
        mapMZNegativeAdductsTMP.put("M+Na-2H", "-20.974666");
        mapMZNegativeAdductsTMP.put("M+K-2H", "-36.948606");
        mapMZNegativeAdductsTMP.put("M+Hac-H", "-59.013851");
        mapMZNegativeAdductsTMP.put("M+Br", "-78.918885");
        mapMZNegativeAdductsTMP.put("M+TFA-H", "-112.985586");
        mapMZNegativeAdductsTMP.put("2M+FA-H", "-44.998201");
        mapMZNegativeAdductsTMP.put("2M+Hac-H", "-59.013851");
        mapMZNegativeAdductsTMP.put("3M-H", "1.007276");
        mapMZNegativeAdductsTMP.put("M+F", "-18.9984");
        mapMZNegativeAdductsTMP.put("M+F+H", "-20.00623");
        MAPMZNEGATIVEADDUCTS = Collections.unmodifiableMap(mapMZNegativeAdductsTMP);
    }

    public static final Map<String, String> MAPNEUTRALADDUCTS;

    static {
        Map<String, String> mapNeutralAdductsTMP = new LinkedHashMap<>();
        mapNeutralAdductsTMP.put("M", "0");
        MAPNEUTRALADDUCTS = Collections.unmodifiableMap(mapNeutralAdductsTMP);
    }

    public static final List<String> DEFAULT_ADDUCTS_POSITIVE;

    static {
        List<String> listDefaultAdductsPositiveTMP = new LinkedList<>();
        listDefaultAdductsPositiveTMP.add("M+H");
        listDefaultAdductsPositiveTMP.add("M+2H");
        listDefaultAdductsPositiveTMP.add("M+Na");
        listDefaultAdductsPositiveTMP.add("M+K");
        listDefaultAdductsPositiveTMP.add("M+NH4");
        listDefaultAdductsPositiveTMP.add("M+H-H2O");
        DEFAULT_ADDUCTS_POSITIVE = Collections.unmodifiableList(listDefaultAdductsPositiveTMP);
    }

    public static final List<String> DEFAULT_ADDUCTS_NEGATIVE;

    static {
        List<String> listDefaultAdductsNegativeTMP = new LinkedList<>();
        listDefaultAdductsNegativeTMP.add("M-H");
        listDefaultAdductsNegativeTMP.add("M+Cl");
        listDefaultAdductsNegativeTMP.add("M+FA-H");
        listDefaultAdductsNegativeTMP.add("M-H-H2O");
        DEFAULT_ADDUCTS_NEGATIVE = Collections.unmodifiableList(listDefaultAdductsNegativeTMP);
    }

    public static final List<String> CHARGE_2;

    static {
        List<String> CHARGE_2TMP = new LinkedList<>();
        CHARGE_2TMP.add("M+2H");
        CHARGE_2TMP.add("M+H+NH4");
        CHARGE_2TMP.add("M+H+Na");
        CHARGE_2TMP.add("M+H+K");
        CHARGE_2TMP.add("M+ACN+2H");
        CHARGE_2TMP.add("M+2Na");
        CHARGE_2TMP.add("M+2ACN+2H");
        CHARGE_2TMP.add("M+3ACN+2H");
        CHARGE_2TMP.add("M-2H");
        CHARGE_2 = Collections.unmodifiableList(CHARGE_2TMP);
    }

    public static final List<String> CHARGE_3;

    static {
        List<String> CHARGE_3TMP = new LinkedList<>();
        CHARGE_3TMP.add("M+3H");
        CHARGE_3TMP.add("M+2H+Na");
        CHARGE_3TMP.add("M+H+2K");
        CHARGE_3TMP.add("M+H+2Na");
        CHARGE_3TMP.add("M+3Na");
        CHARGE_3TMP.add("M-3H");
        CHARGE_3 = Collections.unmodifiableList(CHARGE_3TMP);
    }

    public static final List<String> DIMER_2;

    static {
        List<String> DIMER_2TMP = new LinkedList<>();
        DIMER_2TMP.add("2M+H");
        DIMER_2TMP.add("2M+NH4");
        DIMER_2TMP.add("2M+Na");
        DIMER_2TMP.add("2M+K");
        DIMER_2TMP.add("2M+ACN+H");
        DIMER_2TMP.add("2M+ACN+Na");
        DIMER_2TMP.add("2M+H-H2O");
        DIMER_2TMP.add("2M-H");
        DIMER_2TMP.add("2M+FA-H");
        DIMER_2TMP.add("2M+Hac-H");
        DIMER_2TMP.add("2M+2H+3H2O");
        DIMER_2 = Collections.unmodifiableList(DIMER_2TMP);
    }

    public static final List<String> TRIMER_3;

    static {
        List<String> DIMER_3TMP = new LinkedList<>();
        DIMER_3TMP.add("3M-H");
        DIMER_3TMP.add("3M+H");
        DIMER_3TMP.add("3M+Na");
        TRIMER_3 = Collections.unmodifiableList(DIMER_3TMP);
    }

    public static final Map<String, List<String>> MAPPOSITIVEADDUCTFRAGMENT;

    static {
        Map<String, List<String>> mapMZPositiveAdductsTMP = new LinkedHashMap<>();

        List<String> possibleParents = new LinkedList<>();
        possibleParents.add("M+H");
        possibleParents.add("M+2H");
        possibleParents.add("M+3H");
        possibleParents.add("M+H-H2O");
        possibleParents.add("M+H+NH4");
        possibleParents.add("M+H+HCOONa");
        possibleParents.add("M+2H+Na");
        possibleParents.add("M+H+2K");
        possibleParents.add("M+H+2Na");
        possibleParents.add("M+H+Na");
        possibleParents.add("M+H+K");
        possibleParents.add("3M+H");
        possibleParents.add("2M+H");
        possibleParents.add("2M+H-H2O");
        possibleParents.add("M+ACN+2H");
        possibleParents.add("M+2ACN+2H");
        possibleParents.add("M+3ACN+2H");
        possibleParents.add("M+CH3OH+H");
        possibleParents.add("M+ACN+H");
        possibleParents.add("M+IsoProp+H");
        possibleParents.add("M+DMSO+H");
        possibleParents.add("M+2ACN+H");
        possibleParents.add("M+IsoProp+Na+H");
        possibleParents.add("2M+ACN+H");
        possibleParents.add("M+H-2H2O");
        possibleParents.add("2M+2H+3H2O");
        possibleParents.add("M+H+CH3COOH");
        possibleParents.add("M+H+CH3COONa");
        mapMZPositiveAdductsTMP.put("M+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+2H");
        possibleParents.add("M+2H+Na");
        possibleParents.add("M+ACN+2H");
        possibleParents.add("M+2ACN+2H");
        possibleParents.add("M+3ACN+2H");
        mapMZPositiveAdductsTMP.put("M+2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Na");
        possibleParents.add("2M+Na");
        possibleParents.add("M+2H+Na");
        possibleParents.add("M+H+2Na");
        possibleParents.add("M+3Na");
        possibleParents.add("M+H+Na");
        possibleParents.add("M+2Na");
        possibleParents.add("M+2Na-H");
        possibleParents.add("M+ACN+Na");
        possibleParents.add("M+IsoProp+Na+H");
        possibleParents.add("2M+ACN+Na");
        mapMZPositiveAdductsTMP.put("M+Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+K");
        possibleParents.add("M+H+2K");
        possibleParents.add("M+H+K");
        possibleParents.add("M+2K-H");
        possibleParents.add("2M+K");
        mapMZPositiveAdductsTMP.put("M+K", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+NH4");
        possibleParents.add("M+H+NH4");
        possibleParents.add("2M+NH4");
        mapMZPositiveAdductsTMP.put("M+NH4", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("2M+H-H2O");
        possibleParents.add("M+H-H2O");
        mapMZPositiveAdductsTMP.put("M+H-H2O", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H+NH4");
        mapMZPositiveAdductsTMP.put("M+H+NH4", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H+HCOONa");
        mapMZPositiveAdductsTMP.put("M+H+HCOONa", possibleParents);

        //mapMZPositiveAdductsTMP.put("2M+H-H2O", "17.0032");
        possibleParents = new LinkedList<>();
        possibleParents.add("M+3H");
        mapMZPositiveAdductsTMP.put("M+3H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+2H+Na");
        mapMZPositiveAdductsTMP.put("M+2H+Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H+2K");
        mapMZPositiveAdductsTMP.put("M+H+2K", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H+2Na");
        mapMZPositiveAdductsTMP.put("M+H+2Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3Na");
        mapMZPositiveAdductsTMP.put("M+3Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+2H+Na");
        possibleParents.add("M+H+2Na");
        possibleParents.add("M+H+Na");
        mapMZPositiveAdductsTMP.put("M+H+Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H+2K");
        possibleParents.add("M+H+K");
        mapMZPositiveAdductsTMP.put("M+H+K", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3ACN+2H");
        possibleParents.add("M+2ACN+2H");
        possibleParents.add("M+ACN+2H");
        mapMZPositiveAdductsTMP.put("M+ACN+2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3Na");
        possibleParents.add("M+2Na");
        possibleParents.add("M+H+2Na");
        possibleParents.add("M+2Na-H");
        mapMZPositiveAdductsTMP.put("M+2Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3ACN+2H");
        possibleParents.add("M+2ACN+2H");
        mapMZPositiveAdductsTMP.put("M+2ACN+2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3ACN+2H");
        mapMZPositiveAdductsTMP.put("M+3ACN+2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+CH3OH+H");
        mapMZPositiveAdductsTMP.put("M+CH3OH+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+ACN+2H");
        possibleParents.add("M+ACN+H");
        possibleParents.add("M+2ACN+2H");
        possibleParents.add("M+3ACN+2H");
        possibleParents.add("M+2ACN+H");
        possibleParents.add("2M+ACN+H");
        mapMZPositiveAdductsTMP.put("M+ACN+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+2Na-H");
        mapMZPositiveAdductsTMP.put("M+2Na-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+IsoProp+Na+H");
        possibleParents.add("M+IsoProp+H");
        mapMZPositiveAdductsTMP.put("M+IsoProp+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("2M+ACN+Na");
        possibleParents.add("M+ACN+Na");
        mapMZPositiveAdductsTMP.put("M+ACN+Na", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+2K-H");
        mapMZPositiveAdductsTMP.put("M+2K-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+DMSO+H");
        mapMZPositiveAdductsTMP.put("M+DMSO+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+3ACN+2H");
        possibleParents.add("M+2ACN+2H");
        possibleParents.add("M+2ACN+H");
        mapMZPositiveAdductsTMP.put("M+2ACN+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+IsoProp+Na+H");
        mapMZPositiveAdductsTMP.put("M+IsoProp+Na+H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+H-2H2O");
        mapMZPositiveAdductsTMP.put("M+H-2H2O", possibleParents);
        
        possibleParents = new LinkedList<>();
        possibleParents.add("M+NH4-H2O");
        mapMZPositiveAdductsTMP.put("M+NH4-H2O", possibleParents);
        
        possibleParents = new LinkedList<>();
        possibleParents.add("M+Li");
        mapMZPositiveAdductsTMP.put("M+Li", possibleParents);
        
        possibleParents = new LinkedList<>();
        possibleParents.add("2M+2H+3H2O");
        mapMZPositiveAdductsTMP.put("2M+2H+3H2O", possibleParents);

        MAPPOSITIVEADDUCTFRAGMENT = Collections.unmodifiableMap(mapMZPositiveAdductsTMP);
    }

    public static final Map<String, List<String>> MAPNEGATIVEADDUCTFRAGMENT;

    static {
        Map<String, List<String>> mapMZNegativeAdductsTMP = new LinkedHashMap<>();

        List<String> possibleParents = new LinkedList<>();
        possibleParents.add("M-H");
        possibleParents.add("M+FA-H");
        possibleParents.add("M-H-H2O");
        possibleParents.add("M-H+HCOONa");
        possibleParents.add("M-H+CH3COONa");
        possibleParents.add("2M-H");
        possibleParents.add("M+Hac-H");
        possibleParents.add("M+TFA-H");
        possibleParents.add("2M+FA-H");
        possibleParents.add("2M+Hac-H");
        possibleParents.add("3M-H");
        possibleParents.add("M-2H");
        possibleParents.add("M+Na-2H");
        possibleParents.add("M+K-2H");
        possibleParents.add("M-3H");
        mapMZNegativeAdductsTMP.put("M-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Cl");
        mapMZNegativeAdductsTMP.put("M+Cl", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("2M+FA-H");
        possibleParents.add("M+FA-H");
        mapMZNegativeAdductsTMP.put("M+FA-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M-H-H2O");
        mapMZNegativeAdductsTMP.put("M-H-H2O", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M-H+HCOONa");
        mapMZNegativeAdductsTMP.put("M-H+HCOONa", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M-3H");
        mapMZNegativeAdductsTMP.put("M-3H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Na-2H");
        possibleParents.add("M-2H");
        possibleParents.add("M+K-2H");
        mapMZNegativeAdductsTMP.put("M-2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Na-2H");
        mapMZNegativeAdductsTMP.put("M+Na-2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+K-2H");
        mapMZNegativeAdductsTMP.put("M+K-2H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Hac-H");
        mapMZNegativeAdductsTMP.put("M+Hac-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+Br");
        mapMZNegativeAdductsTMP.put("M+Br", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+TFA-H");
        mapMZNegativeAdductsTMP.put("M+TFA-H", possibleParents);

        possibleParents = new LinkedList<>();
        possibleParents.add("M+F");
        mapMZNegativeAdductsTMP.put("M+F", possibleParents);
        
        MAPNEGATIVEADDUCTFRAGMENT = Collections.unmodifiableMap(mapMZNegativeAdductsTMP);
    }

}
