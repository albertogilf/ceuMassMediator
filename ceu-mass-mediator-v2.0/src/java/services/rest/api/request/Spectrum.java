/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.rest.api.request;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class Spectrum {
    
    private double mz;
    private double intensity;
    
    public Spectrum(double mz, double intensity) {
        this.mz = mz;
        this.intensity = intensity;
    }

    public double getMz() {
        return mz;
    }

    public double getIntensity() {
        return intensity;
    }
  
}
