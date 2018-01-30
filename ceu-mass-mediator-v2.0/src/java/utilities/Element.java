package utilities;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class Element {

    private final int atomicNumber;
    private final String element;
    // private final String fullName;
    private final double atomicMass;

    public Element(int atomicNumber, String element, double atomicMass) {
        this.atomicNumber = atomicNumber;
        this.element = element;
        this.atomicMass = atomicMass;
    }

    /*
    public Element(int atomicNumber, String element, String fullName, double atomicMass) {
        this.atomicNumber = atomicNumber;
        this.element = element;
        this.fullName = fullName;
        this.atomicMass = atomicMass;
    }
     */
}
