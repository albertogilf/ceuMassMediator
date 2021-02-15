/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compound;

/**
 * Classyfire_Classification. ClassyFire is a web-based application for
 * automated structural classification of chemical entities. Many compounds have
 * many classyfire_classifications (many to many relaionship)
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 24/04/2018
 */
public class Classyfire_Classification {

    private final String kingdom;
    private final String superClass;
    private final String myclass;
    private final String subClass;
    private final String directParent;

    public Classyfire_Classification(String kingdom, String superClass, String myclass, String subClass, String directParent) {
        this.kingdom = kingdom;
        this.superClass = superClass;
        this.myclass = myclass;
        this.subClass = subClass;
        this.directParent = directParent;
    }

    public String getKingdom() {
        return kingdom;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String getMyclass() {
        return myclass;
    }

    public String getSubClass() {
        return subClass;
    }

    public String getDirectParent() {
        return directParent;
    }

}
