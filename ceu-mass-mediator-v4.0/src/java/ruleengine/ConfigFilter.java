/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruleengine;

import utilities.DataFromInterfacesUtilities;

/**
 * Bean for configuration filter used by ionization rules.
 * 
 * @author aesteban
 */
public class ConfigFilter {
    
    private int ionMode;
    private int modifier;
    private boolean allCompounds;

    /**
     * @return the ionMode
     */
    public int getIonMode() {
        return this.ionMode;
    }

    /** 
     * @param ionMode the ionMode to set. Neutral, positive or Negative
     */
    public void setIonMode(String ionMode) {
        int ionizationModeInt = DataFromInterfacesUtilities.ionizationModeToInteger(ionMode);
        this.ionMode = ionizationModeInt;
    }
    
    /** 
     * @param ionMode the ionMode to set. 1 positive 0 negative
     */
    public void setIonMode(int ionMode) {
        this.ionMode = ionMode;
    }

    /**
     * @return the modifier
     */
    public int getModifier() {
        return this.modifier;
    }

    /**
     * @param modifier the modifier to set
     */
    public void setModifier(String modifier) {
        int modifierAsInt = DataFromInterfacesUtilities.modifierToInteger(modifier);
        this.modifier = modifierAsInt;
    }
    
    /**
     * @param modifier the modifier to set
     */
    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    /**
     * @return if user introduced significant and non-significant compounds
     */
    public boolean isAllCompounds() {
        return this.allCompounds;
    }
    
    public boolean getAllCompounds() {
        return this.allCompounds;
    }

    /**
     * @param allCompounds user introduced significant and non significant compounds
     */
    public void setAllCompounds(boolean allCompounds) {
        this.allCompounds = allCompounds;
    }
    
    
    
}
