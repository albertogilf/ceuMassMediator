/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @version: 5.0, 28/06/2017
 */
public class OxidationLists {

    public static final List<SelectItem> LISTIONMODES;

    static {
        List<SelectItem> listIonizedModesTMP = new LinkedList<SelectItem>();
        listIonizedModesTMP.add(new SelectItem("positive", "Positive Mode"));
        listIonizedModesTMP.add(new SelectItem("negative", "Negative Mode"));
        LISTIONMODES = Collections.unmodifiableList(listIonizedModesTMP);
    }

    public static final List<String> LISTDB;

    static {
        List<String> listDBTMP = new LinkedList<String>();
        listDBTMP.add("HMDB");
        listDBTMP.add("LipidMaps");
        listDBTMP.add("Metlin");
        listDBTMP.add("In-House-Library");
        LISTDB = Collections.unmodifiableList(listDBTMP);
    }
    
    public static final List<String> LIST_LONG_CHAIN_OXIDATION_TYPES;

    static {
        List<String> listLONGCHAINOXIDATIONTMP = new LinkedList<String>();
        listLONGCHAINOXIDATIONTMP.add("O");
        listLONGCHAINOXIDATIONTMP.add("OH");
        listLONGCHAINOXIDATIONTMP.add("OH-OH");
        listLONGCHAINOXIDATIONTMP.add("OOH");
        LIST_LONG_CHAIN_OXIDATION_TYPES = Collections.unmodifiableList(listLONGCHAINOXIDATIONTMP);
    }
    
    public static final List<String> LIST_SHORT_CHAIN_OXIDATION_TYPES;

    static {
        List<String> listSHORTCHAINOXIDATIONTMP = new LinkedList<String>();
        listSHORTCHAINOXIDATIONTMP.add("COH");
        listSHORTCHAINOXIDATIONTMP.add("COOH");
        LIST_SHORT_CHAIN_OXIDATION_TYPES = Collections.unmodifiableList(listSHORTCHAINOXIDATIONTMP);
    }

    public static final Map<String, String> MAPOXIDATIONMZS;

    static {
        Map<String, String> mapOXIDATIONSTMP = new LinkedHashMap<String, String>();
        mapOXIDATIONSTMP.put("O", "-13.9793");
        mapOXIDATIONSTMP.put("OH", "-15.9949");
        mapOXIDATIONSTMP.put("OH-OH", "-31.9898");
        mapOXIDATIONSTMP.put("OOH", "-31.9898");
        mapOXIDATIONSTMP.put("COH", "-13.9793");
        mapOXIDATIONSTMP.put("COOH", "-29.9742");
        MAPOXIDATIONMZS = Collections.unmodifiableMap(mapOXIDATIONSTMP);
    }
    
    public static final Map<String, Integer> MAPOXIDATIONHYDROGENS;

    static {
        Map<String, Integer> mapOXIDATIONSTMP = new LinkedHashMap<String, Integer>();
        mapOXIDATIONSTMP.put("O", -2);
        mapOXIDATIONSTMP.put("OH", 0);
        mapOXIDATIONSTMP.put("OH-OH", 0);
        mapOXIDATIONSTMP.put("OOH", 0);
        mapOXIDATIONSTMP.put("COH", -2);
        mapOXIDATIONSTMP.put("COOH", -2);
        MAPOXIDATIONHYDROGENS = Collections.unmodifiableMap(mapOXIDATIONSTMP);
    }
    
    public static final Map<String, Integer> MAPOXIDATIONOXIGENS;

    static {
        Map<String, Integer> mapOXIDATIONSTMP = new LinkedHashMap<String, Integer>();
        mapOXIDATIONSTMP.put("O", 1);
        mapOXIDATIONSTMP.put("OH", 1);
        mapOXIDATIONSTMP.put("OH-OH", 2);
        mapOXIDATIONSTMP.put("OOH", 2);
        mapOXIDATIONSTMP.put("COH", 1);
        mapOXIDATIONSTMP.put("COOH", 2);
        MAPOXIDATIONOXIGENS = Collections.unmodifiableMap(mapOXIDATIONSTMP);
    }
    
    

    public static final List<Double> LISTNLPOS_O;

    static {
        List<Double> listNLPTMP_O = new LinkedList<Double>();
        LISTNLPOS_O = Collections.unmodifiableList(listNLPTMP_O);
    }

    public static final List<Double> LISTNLPOS_OH;

    static {
        List<Double> listNLPTMP_OH = new LinkedList<Double>();
        listNLPTMP_OH.add(-18.0108d);
        LISTNLPOS_OH = Collections.unmodifiableList(listNLPTMP_OH);
    }
    
    public static final List<Double> LISTNLPOS_OH_OH;

    static {
        List<Double> listNLPTMP_OH_OH = new LinkedList<Double>();
        listNLPTMP_OH_OH.add(-18.0108d);
        listNLPTMP_OH_OH.add(-36.0216d);
        LISTNLPOS_OH_OH = Collections.unmodifiableList(listNLPTMP_OH_OH);
    }
    
    public static final List<Double> LISTNLPOS_OOH;

    static {
        List<Double> listNLPTMP_OOH = new LinkedList<Double>();
        listNLPTMP_OOH.add(-18.0108d);
        listNLPTMP_OOH.add(-34.0049d);
        LISTNLPOS_OOH = Collections.unmodifiableList(listNLPTMP_OOH);
    }
    
    public static final List<Double> LISTNLPOS_COH;

    static {
        List<Double> listNLPTMP_COH = new LinkedList<Double>();
        LISTNLPOS_COH = Collections.unmodifiableList(listNLPTMP_COH);
    }
    
    public static final List<Double> LISTNLPOS_COOH;

    static {
        List<Double> listNLPTMP_COOH = new LinkedList<Double>();
        LISTNLPOS_COOH = Collections.unmodifiableList(listNLPTMP_COOH);
    }
    
    public static final List<Double> LISTNLNEG_O;

    static {
        List<Double> listNLNTMP_O = new LinkedList<Double>();
        LISTNLNEG_O = Collections.unmodifiableList(listNLNTMP_O);
    }
    
    public static final List<Double> LISTNLNEG_OH;

    static {
        List<Double> listNLNTMP_OH = new LinkedList<Double>();
       LISTNLNEG_OH = Collections.unmodifiableList(listNLNTMP_OH);
    }
    
    public static final List<Double> LISTNLNEG_OH_OH;

    static {
        List<Double> listNLNTMP_OH_OH = new LinkedList<Double>();
       LISTNLNEG_OH_OH = Collections.unmodifiableList(listNLNTMP_OH_OH);
    }
    
    public static final List<Double> LISTNLNEG_OOH;

    static {
        List<Double> listNLNTMP_O = new LinkedList<Double>();
      LISTNLNEG_OOH = Collections.unmodifiableList(listNLNTMP_O);
    }
    
    public static final List<Double> LISTNLNEG_COH;

    static {
        List<Double> listNLNTMP_COH = new LinkedList<Double>();
        listNLNTMP_COH.add(-59.0371d);
        LISTNLNEG_COH = Collections.unmodifiableList(listNLNTMP_COH);
    }
    
    public static final List<Double> LISTNLNEG_COOH;

    static {
        List<Double> listNLNTMP_COOH = new LinkedList<Double>();
        listNLNTMP_COOH.add(-59.0371d);
        LISTNLNEG_COOH = Collections.unmodifiableList(listNLNTMP_COOH);
    }
}
