
package services.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * POJO for the compounds generated for the Rest Service
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 26/10/2016
 */
@XmlRootElement
public class compoundForAPIRest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    Double mass;
    String name;
    
    public compoundForAPIRest() {
        
    }
    public compoundForAPIRest(Double mass, String name) {
        this.mass = mass;
        this.name = name;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
