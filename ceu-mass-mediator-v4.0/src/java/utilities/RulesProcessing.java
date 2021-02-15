/*
 * RulesProcessing.java
 *
 * Created on 03-jun-2018, 18:28:47
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package utilities;

import LCMS_FEATURE.CompoundLCMS;
import LCMS_FEATURE.CompoundsLCMSGroupByAdduct;
import LCMS_FEATURE.Feature;
import LCMS_FEATURE.FeaturesGroupByRT;
import java.util.List;
import ruleengine.ConfigFilter;

/**
 * Class to apply knowledge to the annotations based on expert knowledge
 *
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 4.0.1 03-jun-2018
 *
 * @author Alberto Gil de la Fuente
 */
public final class RulesProcessing {

    /**
     * Creates a new instance of RulesProcessing
     */
    private RulesProcessing() {
    }

    /**
     * Process the rules over the features GroupedByRT TODO WITH NEW DATA MODEL,
     * IT WILL RECEIVE A LIST OF FEATURES TO ANNOTATE. CREATE ALSO METHOD FOR A
     * LIST OF FEATURES GROUPED BY RT
     */
    private void processAllRules(List<Feature> features, String modifier, String ionMode, boolean allCompounds) {
        // Drools.
        // Creates configFilter with ionization mode.
        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(modifier);
        configFilter.setIonMode(ionMode);
        configFilter.setAllCompounds(allCompounds);
        // Execute rules.
        // TODO ALBERTO APPLY RULES TO FEATURES
        // this.items = RuleProcessor.processCompounds(this.items, configFilter);

    }

    /**
     * Process only the ionisation rules (based on the possible adducts and the
     * target annotation).
     *
     *
     */
    private void processIonizationRules(List<Feature> features, String modifier, String ionMode, boolean allCompounds) {
        // Drools.
        // Creates configFilter with ionization mode.
        ConfigFilter configFilter = new ConfigFilter();
        configFilter.setModifier(modifier);
        configFilter.setIonMode(ionMode);
        configFilter.setAllCompounds(allCompounds);
        // Execute rules.
        // TODO ALBERTO APPLY RULES TO FEATURES
        // this.items = RuleProcessor.processSimpleSearch(this.items, configFilter);
        // Write tags for colors
        /*
        for (TheoreticalCompounds tc : this.items) {
            tc.createColorIonizationScore();
        }
         */
    }

    /**
     * GET THE MAX NUMBER OF RT RULES APPLIED WITHIN ALL THE ANNOTATIONS AND CALCULATE
     * THE RT SCORE FOR ALL THE ANNOTATIONS
     *
     * @param features the features annotated and after processing of the rules
     * @return the max number of RT rules applied over a single annotation
     */
    public static int calculateMaxNumberOfRTScoresFeatures(List<Feature> features) {
        int maxNumberOfRTScoresApplied = 0;
        for (Feature feature : features) {
            for (CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct : feature.getAnnotationsGroupedByAdduct()) {
                for (CompoundLCMS compoundLCMS : compoundsLCMSGroupByAdduct.getCompounds()) {
                    compoundLCMS.calculateRTScore();
                    if (compoundLCMS.getTotalNumberRTScores() > maxNumberOfRTScoresApplied) {
                        maxNumberOfRTScoresApplied = compoundLCMS.getTotalNumberRTScores();
                    }
                }
            }
        }
        return maxNumberOfRTScoresApplied;
        //System.out.println("MAX NUMBER OF RT APPLIED: " + this.maxNumberOfRTScoresApplied);
    }

    /**
     * GET THE MAX NUMBER OF RT RULES APPLIED WITHIN ALL THE ANNOTATIONS AND CALCULATE
     * THE RT SCORE FOR ALL THE ANNOTATIONS
     * 
     * @param featuresGroupByRT the features annotated and after processing of the rules
     * @return the max number of RT rules applied over a single annotation
     */
    public static int calculateMaxNumberOfRTScoresFeaturesGroupByRT(List<FeaturesGroupByRT> featuresGroupByRT) {
        int maxNumberOfRTScoresApplied = 0;
        for (FeaturesGroupByRT featureGroupByRT : featuresGroupByRT) {
            for (Feature feature : featureGroupByRT.getFeatures()) {
                for (CompoundsLCMSGroupByAdduct compoundsLCMSGroupByAdduct : feature.getAnnotationsGroupedByAdduct()) {
                    for (CompoundLCMS compoundLCMS : compoundsLCMSGroupByAdduct.getCompounds()) {
                        compoundLCMS.calculateRTScore();
                        if (compoundLCMS.getTotalNumberRTScores() > maxNumberOfRTScoresApplied) {
                            maxNumberOfRTScoresApplied = compoundLCMS.getTotalNumberRTScores();
                        }
                    }
                }
            }
        }
        return maxNumberOfRTScoresApplied;
        //System.out.println("MAX NUMBER OF RT APPLIED: " + this.maxNumberOfRTScoresApplied);
    }

}
