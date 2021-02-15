/*
 * KBSystemResult.java
 *
 * Created on 26-dic-2018, 21:08:53
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package model.kbsystem;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 26-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class KBSystemResult implements Comparable<KBSystemResult> {

    private final String trueValue;
    private final String ruleType;
    private final KBSystemPutativeAnnotation putativeAnnotation;
    private final KBSystemExplanation explanation;

    /**
     * Creates a new instance of ResultKBSystem
     *
     * @param trueValue
     * @param ruleType
     * @param putativeAnnotation
     * @param explanation
     */
    public KBSystemResult(String trueValue, String ruleType,
            KBSystemPutativeAnnotation putativeAnnotation, KBSystemExplanation explanation) {
        this.trueValue = trueValue;
        this.ruleType = ruleType;
        this.putativeAnnotation = putativeAnnotation;
        this.explanation = explanation;
    }

    public String getTrueValue() {
        return this.trueValue;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public KBSystemPutativeAnnotation getPutativeAnnotation() {
        return this.putativeAnnotation;
    }

    public KBSystemExplanation getExplanation() {
        return this.explanation;
    }

    @Override
    public int compareTo(KBSystemResult o) {
        return Integer.compare(this.getPutativeAnnotation().getIdentifier(), o.getPutativeAnnotation().getIdentifier());
    }

    @Override
    public String toString() {
        return "trueValue: " + this.trueValue + ", ruleType: " + this.ruleType
                + "\nhypothesis: " + this.putativeAnnotation + "\nexplanation: " + explanation;
    }
}
