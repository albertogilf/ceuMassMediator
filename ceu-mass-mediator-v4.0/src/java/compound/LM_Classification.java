/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

/**
 * LM_Classification. Lipid Maps Classification. Several compounds have one
 * LM_Classification (one to many relationship)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class LM_Classification {

    
    private final String category;
    private final String mainClass;
    private final String subClass;
    private final String class_level4;

    public LM_Classification(String category, String mainClass, String subClass, String class_level4) {
        this.category = category == null ? "" : category;
        this.mainClass = mainClass == null ? "" : mainClass;
        this.subClass = subClass == null ? "" : subClass;
        this.class_level4 = class_level4 == null ? "" : class_level4;
    }

    public String getCategory() {
        return category;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getSubClass() {
        return subClass;
    }

    public String getClass_level4() {
        return class_level4;
    }

}
