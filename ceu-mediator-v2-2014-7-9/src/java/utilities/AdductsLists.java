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
    
    public static final double H_WEIGHT=1.0073d;

    public static final List<SelectItem> LISTNEUTRALMODES;

    static {
        List<SelectItem> listNeutralModesTMP = new LinkedList<SelectItem>();
        listNeutralModesTMP.add(new SelectItem("neutral", "neutral"));
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

    public static final Map<String, String> MAPRECALCULATEDPOSITIVEADDUCTS;

    static {
        Map<String, String> mapRecalculatedPositiveAdductsTMP = new LinkedHashMap<String, String>();
        mapRecalculatedPositiveAdductsTMP.put("M+H", "0");
        mapRecalculatedPositiveAdductsTMP.put("M+Na", "-21.9819");
        mapRecalculatedPositiveAdductsTMP.put("M+K", "-37.9558");
        mapRecalculatedPositiveAdductsTMP.put("M+NH4", "-17.0265");
        mapRecalculatedPositiveAdductsTMP.put("M+H-H2O", "18.0105");
        mapRecalculatedPositiveAdductsTMP.put("M+H+HCOONa", "-67.9873");
        mapRecalculatedPositiveAdductsTMP.put("M+2H", "DoubleCharged");
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

    public static final Map<String, String> MAPMZPOSITIVEADDUCTS;

    static {
        Map<String, String> mapMZPositiveAdductsTMP = new LinkedHashMap<String, String>();
        mapMZPositiveAdductsTMP.put("M+H", "-1.0073");
        mapMZPositiveAdductsTMP.put("M+Na", "-22.9892");
        mapMZPositiveAdductsTMP.put("M+K", "-38.9631");
        mapMZPositiveAdductsTMP.put("M+NH4", "-18.0338");
        mapMZPositiveAdductsTMP.put("M+H-H2O", "17.0032");
        mapMZPositiveAdductsTMP.put("M+H+HCOONa", "-68.9946");
        mapMZPositiveAdductsTMP.put("M+2H", "DoubleCharged");
        mapMZPositiveAdductsTMP.put("2M+H", "-1.0073");
        mapMZPositiveAdductsTMP.put("2M+Na", "-22.9892");
        mapMZPositiveAdductsTMP.put("2M+H-H2O", "17.0032");
        MAPMZPOSITIVEADDUCTS = Collections.unmodifiableMap(mapMZPositiveAdductsTMP);
    }
    public static final Map<String, String> MAPMZNEGATIVEADDUCTS;

    static {
        Map<String, String> mapMZNegativeAdductsTMP = new LinkedHashMap<String, String>();
        mapMZNegativeAdductsTMP.put("M-H", "1.0073");
        mapMZNegativeAdductsTMP.put("M+Cl", "-34.9694");
        mapMZNegativeAdductsTMP.put("M+HCOO", "-44.9982");
        mapMZNegativeAdductsTMP.put("M-H-H2O", "19.0178");
        mapMZNegativeAdductsTMP.put("M-H+HCOONa", "-66.9802");
        mapMZNegativeAdductsTMP.put("2M-H", "1.0073");
        MAPMZNEGATIVEADDUCTS = Collections.unmodifiableMap(mapMZNegativeAdductsTMP);
    }

    public static final Map<String, String> MAPNEUTRALADDUCTS;

    static {
        Map<String, String> mapNeutralAdductsTMP = new LinkedHashMap<String, String>();
        mapNeutralAdductsTMP.put("M", "0");
        MAPNEUTRALADDUCTS = Collections.unmodifiableMap(mapNeutralAdductsTMP);
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
