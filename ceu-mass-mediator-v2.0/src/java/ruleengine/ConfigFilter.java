/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ruleengine;

/**
 * Bean for configuration filter used by ionization rules.
 * 
 * @author aesteban
 */
public class ConfigFilter {
    
    private String ionMode;
    private String modifier;
    private boolean allCompounds;

    /**
     * @return the ionMode
     */
    public String getIonMode() {
        return ionMode;
    }

    /**
     * @param ionMode the ionMode to set
     */
    public void setIonMode(String ionMode) {
        this.ionMode = ionMode;
    }

    /**
     * @return the modifier
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * @param modifier the modifier to set
     */
    public void setModifier(String modifier) {
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
