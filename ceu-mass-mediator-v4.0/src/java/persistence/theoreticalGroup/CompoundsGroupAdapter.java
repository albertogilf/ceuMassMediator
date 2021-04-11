/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.theoreticalGroup;

import utilities.Constants;

/**
 *
 * @author alberto
 */
public abstract class CompoundsGroupAdapter implements TheoreticalCompoundsGroup {
    
    protected final Double experimentalMass;
    
    protected final Double retentionTime;
    
    public CompoundsGroupAdapter(Double experimentalMass, Double retentionTime)
    {
        this.experimentalMass = experimentalMass;
        this.retentionTime = retentionTime;
    }
    
    
    /**
     * @return the experimentalMass
     */
    @Override
    public Double getExperimentalMass() {
        return this.experimentalMass;
    }
    
    
    /**
     * @return the retentionTime
     */
    @Override
    public Double getRetentionTime() {
        return this.retentionTime;
    }
    
    
    @Override
    public String getKeggWebPage() {
        return Constants.WEB_KEGG;
    }

    @Override
    public String getHMDBWebPage() {
        return Constants.WEB_HMDB;
    }

    @Override
    public String getMetlinWebPage() {
        return Constants.WEB_METLIN;
    }

    @Override
    public String getLMWebPage() {
        return Constants.WEB_LIPID_MAPS;
    }

    @Override
    public String getPCWebPage() {
        return Constants.WEB_PUBCHEMICHAL;
    }
    
    @Override
    public String getChebiWebPage() {
        return Constants.WEB_CHEBI;
    }
    
    @Override
    public String getKnapsackWebPage() {
        return Constants.WEB_KNAPSACK;
    }
    
    @Override
    public String getNpAtlasWebPage() {
        return Constants.WEB_NPATLAS;
    }
    
    
}
