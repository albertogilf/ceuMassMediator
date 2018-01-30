package utilities;

// import java.util.HashMap;
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
    

    public static final List<SelectItem> LISTNEUTRALMODES;

    static {
        List<SelectItem> listNeutralModesTMP = new LinkedList<SelectItem>();
        listNeutralModesTMP.add(new SelectItem("neutral", "Neutral"));
        listNeutralModesTMP.add(new SelectItem("positive", "Positive Mode"));
        listNeutralModesTMP.add(new SelectItem("negative", "Negative Mode"));
        LISTNEUTRALMODES = Collections.unmodifiableList(listNeutralModesTMP);
    }

    public static final List<SelectItem> LISTIONIZEDMODES;

    static {
        List<SelectItem> listIonizedModesTMP = new LinkedList<SelectItem>();
        listIonizedModesTMP.add(new SelectItem("positive", "Positive Mode"));
        listIonizedModesTMP.add(new SelectItem("negative", "Negative Mode"));
        LISTIONIZEDMODES = Collections.unmodifiableList(listIonizedModesTMP);
    }

    /*
    public static final Map<String, String> MAPRECALCULATEDPOSITIVEADDUCTS;

    static {
        Map<String, String> mapRecalculatedPositiveAdductsTMP = new LinkedHashMap<String, String>();
        mapRecalculatedPositiveAdductsTMP.put("M+H", "0");
        mapRecalculatedPositiveAdductsTMP.put("M+2H", "0");
        mapRecalculatedPositiveAdductsTMP.put("M+Na", "-21.9819");
        mapRecalculatedPositiveAdductsTMP.put("M+K", "-37.9558");
        mapRecalculatedPositiveAdductsTMP.put("M+NH4", "-17.0265");
        mapRecalculatedPositiveAdductsTMP.put("M+H-H2O", "18.0105");
        mapRecalculatedPositiveAdductsTMP.put("M+H+HCOONa", "-67.9873");
        
        mapRecalculatedPositiveAdductsTMP.put("2M+H", "0");
        mapRecalculatedPositiveAdductsTMP.put("2M+Na", "-21.9819");
        mapRecalculatedPositiveAdductsTMP.put("2M+H-H2O", "18.0105");
        MAPRECALCULATEDPOSITIVEADDUCTS = Collections.unmodifiableMap(mapRecalculatedPositiveAdductsTMP);
    }
    public static final Map<String, String> MAPRECALCULATEDNEGATIVEADDUCTS;

    static {
        Map<String, String> mapRecalculatedNegativeAdductsTMP = new LinkedHashMap<String, String>();
        mapRecalculatedNegativeAdductsTMP.put("M-H", "0");
        mapRecalculatedNegativeAdductsTMP.put("M+Cl", "-35.9767");
        mapRecalculatedNegativeAdductsTMP.put("M+HCOO", "-46.0055");
        mapRecalculatedNegativeAdductsTMP.put("M-H-H2O", "18.0105");
        mapRecalculatedNegativeAdductsTMP.put("M-H+HCOONa", "-67.9875");
        mapRecalculatedNegativeAdductsTMP.put("2M-H", "0");
        MAPRECALCULATEDNEGATIVEADDUCTS = Collections.unmodifiableMap(mapRecalculatedNegativeAdductsTMP);
    }
*/
    public static final Map<String, String> MAPMZPOSITIVEADDUCTS;

    static {
        Map<String, String> mapMZPositiveAdductsTMP = new LinkedHashMap<String, String>();
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
        MAPMZPOSITIVEADDUCTS = Collections.unmodifiableMap(mapMZPositiveAdductsTMP);
    }
    public static final Map<String, String> MAPMZNEGATIVEADDUCTS;

    static {
        Map<String, String> mapMZNegativeAdductsTMP = new LinkedHashMap<String, String>();
        mapMZNegativeAdductsTMP.put("M-H", "1.007276");
        mapMZNegativeAdductsTMP.put("M+Cl", "-34.969402");
        mapMZNegativeAdductsTMP.put("M+FA-H", "-44.998201");
        mapMZNegativeAdductsTMP.put("M-H-H2O", "19.01839");
        mapMZNegativeAdductsTMP.put("M-H+HCOONa", "-66.9802");
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
        MAPMZNEGATIVEADDUCTS = Collections.unmodifiableMap(mapMZNegativeAdductsTMP);
    }

    public static final Map<String, String> MAPNEUTRALADDUCTS;

    static {
        Map<String, String> mapNeutralAdductsTMP = new LinkedHashMap<String, String>();
        mapNeutralAdductsTMP.put("M", "0");
        MAPNEUTRALADDUCTS = Collections.unmodifiableMap(mapNeutralAdductsTMP);
    }
    
    
    
    public static final List<String> CHARGE_2;

    static {
        List<String> CHARGE_2TMP = new LinkedList<String>();
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
        List<String> CHARGE_3TMP = new LinkedList<String>();
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
        List<String> DIMER_2TMP = new LinkedList<String>();
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
        DIMER_2 = Collections.unmodifiableList(DIMER_2TMP);
    }
    
    public static final List<String> DIMER_3;

    static {
        List<String> DIMER_3TMP = new LinkedList<String>();
        DIMER_3TMP.add("3M-H");
        DIMER_3 = Collections.unmodifiableList(DIMER_3TMP);
    }

    public static final List<String> LISTDB;

    static {
        List<String> listDBTMP = new LinkedList<String>();
        listDBTMP.add("Kegg");
        listDBTMP.add("HMDB");
        listDBTMP.add("LipidMaps");
        listDBTMP.add("Metlin");
        // For generated compounds
        listDBTMP.add("MINE (Only In Silico Compounds)");
        LISTDB = Collections.unmodifiableList(listDBTMP);
    }

    public static final List<String> MODIFIERS;

    static {
        List<String> modifiersTMP = new LinkedList<String>();
        modifiersTMP.add("NH3");
        modifiersTMP.add("HCOO");
        modifiersTMP.add("CH3COO");
        modifiersTMP.add("HCOONH3");
        modifiersTMP.add("CH3COONH3");
        MODIFIERS = Collections.unmodifiableList(modifiersTMP);
    }

    public static final List<String> METABOLITESTYPES;

    static {
        List<String> metabolitesTypesTMP = new LinkedList<String>();
        metabolitesTypesTMP.add("All except peptides"); //0
        metabolitesTypesTMP.add("Only lipids"); // 1
        metabolitesTypesTMP.add("All including peptides"); // 2
        METABOLITESTYPES = Collections.unmodifiableList(metabolitesTypesTMP);
    }
}
