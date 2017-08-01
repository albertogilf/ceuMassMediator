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
    
    public static final double H_WEIGHT=1.0073d;

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
        listDBTMP.add("In-house library");
        LISTDB = Collections.unmodifiableList(listDBTMP);
    }
    
    public static final Map<String, String> MAPOXIDATIONS;

    static {
        Map<String, String> mapMZNegativeAdductsTMP = new LinkedHashMap<String, String>();
        mapMZNegativeAdductsTMP.put("OH", "");
        mapMZNegativeAdductsTMP.put("O", "");
        mapMZNegativeAdductsTMP.put("OOH", "");
        mapMZNegativeAdductsTMP.put("COH", "");
        mapMZNegativeAdductsTMP.put("COOH", "");
        MAPOXIDATIONS = Collections.unmodifiableMap(mapMZNegativeAdductsTMP);
    }
    
}
