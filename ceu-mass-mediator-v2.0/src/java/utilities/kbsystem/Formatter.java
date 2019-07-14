/*
 * Formatter.java
 *
 * Created on 26-dic-2018, 20:40:13
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities.kbsystem;

import java.util.LinkedList;
import java.util.List;
import model.kbsystem.KBSystemPutativeAnnotation;
import model.kbsystem.KBSystemEmpiricalCompound;
import model.kbsystem.KBSystemTheoreticalCompound;
import persistence.theoreticalCompound.TheoreticalCompounds;
import com.google.gson.Gson;
import java.util.Arrays;
import model.kbsystem.KBSystemRequest;
import model.kbsystem.KBSystemResult;
import utilities.DataFromInterfacesUtilities;

/**
 * Class to parse compounds from/to Java DOM from/to Prolog KB system JSON
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 3.0.1 26-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class Formatter {

    /**
     * Creates a new instance of formatter
     */
    public Formatter() {

    }

    public static List<KBSystemPutativeAnnotation> FromDomCompoundToKBSystemCompounds(
            List<TheoreticalCompounds> JavaDomCompounds) {
        List<KBSystemPutativeAnnotation> listKBSystemCompounds = new LinkedList<>();
        for (TheoreticalCompounds TC : JavaDomCompounds) {
            listKBSystemCompounds.add(FromDomCompoundToKBSystemCompound(TC));
        }
        return listKBSystemCompounds;
    }

    public static KBSystemPutativeAnnotation FromDomCompoundToKBSystemCompound(
            TheoreticalCompounds JavaDomCompound) {
        Integer hypothesisId = JavaDomCompound.getHypothesisId();
        String adduct = JavaDomCompound.getAdduct();
        //System.out.println("Parsing compound: " + JavaDomCompound);
        KBSystemPutativeAnnotation kbsystemCompound = new KBSystemPutativeAnnotation(hypothesisId,
                new KBSystemEmpiricalCompound(JavaDomCompound.getExperimentalMass(),
                        JavaDomCompound.getRetentionTime()),
                new KBSystemTheoreticalCompound(JavaDomCompound.getCompoundId(), JavaDomCompound.getName()),
                adduct);
        //System.out.println("KBSYSTEMCOMPOUND: " + kbsystemCompound);
        return kbsystemCompound;
    }
    
    public static KBSystemRequest FromDomCompoundToKBSystemRequest(int ionMode,
            List<TheoreticalCompounds> JavaDomCompounds) {
        String ionizationMode = DataFromInterfacesUtilities.ionizationModeToString(ionMode);
        List<KBSystemPutativeAnnotation> listKBSystemPutativeAnnotations
                = FromDomCompoundToKBSystemCompounds(JavaDomCompounds);
        KBSystemRequest kbSystemRequest = new KBSystemRequest(1, ionizationMode, listKBSystemPutativeAnnotations);
        return kbSystemRequest;
    }

    public static String FromDomCompoundToKBSystemRequestStringJSON(int ionMode,
            List<TheoreticalCompounds> JavaDomCompounds) {
        String ionizationMode = DataFromInterfacesUtilities.ionizationModeToString(ionMode);
        List<KBSystemPutativeAnnotation> listKBSystemPutativeAnnotations
                = FromDomCompoundToKBSystemCompounds(JavaDomCompounds);
        KBSystemRequest kbSystemRequest = new KBSystemRequest(1, ionizationMode, listKBSystemPutativeAnnotations);
        return FromKBSystemCompoundToJSON(kbSystemRequest);
    }

    public static String FromKBSystemCompoundToJSON(KBSystemRequest kbSystemRequest) {
        //System.out.println("Parsing compound: " + id + " \n " + kbSystemRequest);

        Gson gson = new Gson();
        String KBSystemCompoundsJSON = gson.toJson(kbSystemRequest);
        //System.out.println("kbSystemRequest: " + KBSystemCompoundsJSON);
        return KBSystemCompoundsJSON;
    }

    public static void assignResultsToTheoreticalCompounds(
            List<TheoreticalCompounds> JavaDomCompounds,
            KBSystemResult[] kbsystemResults) {
        Arrays.stream(kbsystemResults).forEach(res -> {
            //System.out.println(res);
            Integer hypothesisId = res.getPutativeAnnotation().getIdentifier();
            TheoreticalCompounds TC = JavaDomCompounds.get(hypothesisId - 1);
            TC.addKbSystemResult(res);
            //System.out.println("TC: " + TC);
        });
    }
}
