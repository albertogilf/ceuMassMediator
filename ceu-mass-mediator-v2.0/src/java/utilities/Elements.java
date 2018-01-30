package utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class Elements {

    public static final Map<String, Double> MAPELEMENTS = new HashMap<String, Double>();
    public static final List<Element> LISTELEMENTS = new LinkedList<Element>();

    static {
        // TODO UPDATE ELEMENTS MONOISOTOPIC WEIGHTS IN BOTH PROJECTS!!
        MAPELEMENTS.put("H", 1.0078250321d);
        MAPELEMENTS.put("He", 4.0026032542d);
        MAPELEMENTS.put("Li", 7.016004558d);
        MAPELEMENTS.put("Be", 9.012182d);
        MAPELEMENTS.put("B", 11.009305d);
        MAPELEMENTS.put("C", 12d);
        MAPELEMENTS.put("N", 14.003074d);
        MAPELEMENTS.put("O", 15.994915d);
        MAPELEMENTS.put("F", 18.998403163d);
        MAPELEMENTS.put("Ne", 20.1797d);
        MAPELEMENTS.put("Na", 22.98976928d);
        MAPELEMENTS.put("Mg", 24.305d);
        MAPELEMENTS.put("Al", 26.9815385d);
        MAPELEMENTS.put("Si", 28.085d);
        MAPELEMENTS.put("P", 30.973761632d);
        MAPELEMENTS.put("S", 31.9720710015d);
        MAPELEMENTS.put("Cl", 35.45d);
        MAPELEMENTS.put("Ar", 39.948d);
        MAPELEMENTS.put("K", 39.0983d);
        MAPELEMENTS.put("Ca", 40.078d);
        MAPELEMENTS.put("Sc", 44.955908d);
        MAPELEMENTS.put("Ti", 47.867d);
        MAPELEMENTS.put("V", 50.9415d);
        MAPELEMENTS.put("Cr", 51.9961d);
        MAPELEMENTS.put("Mn", 51.9961d);
        MAPELEMENTS.put("Fe", 55.845d);
        MAPELEMENTS.put("Co", 58.933194d);
        MAPELEMENTS.put("Ni", 58.6934d);
        MAPELEMENTS.put("Cu", 63.546d);
        MAPELEMENTS.put("Zn", 65.38d);
        MAPELEMENTS.put("Ga", 69.723d);
        MAPELEMENTS.put("Ge", 72.630d);
        MAPELEMENTS.put("As", 74.921595d);
        MAPELEMENTS.put("Se", 78.971d);
        MAPELEMENTS.put("Br", 79.904d);
        MAPELEMENTS.put("Kr", 83.798d);
        MAPELEMENTS.put("Rb", 85.4678d);
        MAPELEMENTS.put("Sr", 87.62d);
        MAPELEMENTS.put("Y", 88.90584d);
        MAPELEMENTS.put("Zr", 91.224d);
        MAPELEMENTS.put("Nb", 92.90637d);
        MAPELEMENTS.put("Mo", 95.95d);
        MAPELEMENTS.put("Tc", 98.0d);
        MAPELEMENTS.put("Ru", 101.07d);
        MAPELEMENTS.put("Rh", 102.90550d);
        MAPELEMENTS.put("Pd", 106.42d);
        MAPELEMENTS.put("Ag", 107.8682d);
        MAPELEMENTS.put("Cd", 112.414d);
        MAPELEMENTS.put("In", 114.818d);
        MAPELEMENTS.put("Sn", 118.710d);
        MAPELEMENTS.put("Sb", 121.760d);
        MAPELEMENTS.put("Te", 127.60d);
        MAPELEMENTS.put("I", 126.90447d);
        MAPELEMENTS.put("Xe", 131.293d);
        MAPELEMENTS.put("Cs", 132.90545196d);
        MAPELEMENTS.put("Ba", 137.327d);
        MAPELEMENTS.put("La", 138.90547d);
        MAPELEMENTS.put("Ce", 140.116d);
        MAPELEMENTS.put("Pr", 140.90766d);
        MAPELEMENTS.put("Nd", 144.242d);
        MAPELEMENTS.put("Pm", 145.0d);
        MAPELEMENTS.put("Sm", 150.36d);
        MAPELEMENTS.put("Eu", 151.964d);
        MAPELEMENTS.put("Gd", 157.25d);
        MAPELEMENTS.put("Tb", 158.92535d);
        MAPELEMENTS.put("Dy", 162.500d);
        MAPELEMENTS.put("Ho", 164.93033d);
        MAPELEMENTS.put("Er", 167.259d);
        MAPELEMENTS.put("Tm", 168.93422d);
        MAPELEMENTS.put("Yb", 173.045d);
        MAPELEMENTS.put("Lu", 173.045d);
        MAPELEMENTS.put("Hf", 178.49d);
        MAPELEMENTS.put("Ta", 180.94788d);
        MAPELEMENTS.put("W", 183.84d);
        MAPELEMENTS.put("Re", 186.207d);
        MAPELEMENTS.put("Os", 190.23d);
        MAPELEMENTS.put("Ir", 192.217d);
        MAPELEMENTS.put("Pt", 195.084d);
        MAPELEMENTS.put("Au", 196.966569d);
        MAPELEMENTS.put("Hg", 200.592d);
        MAPELEMENTS.put("Tl", 204.38d);
        MAPELEMENTS.put("Pb", 207.2d);
        MAPELEMENTS.put("Bi", 208.98040d);
        MAPELEMENTS.put("Po", 209.0d);
        MAPELEMENTS.put("At", 210.0d);
        MAPELEMENTS.put("Rn", 222.0d);
        MAPELEMENTS.put("Fr", 223.0d);
        MAPELEMENTS.put("Ra", 226.0d);
        MAPELEMENTS.put("Ac", 227.0d);
        MAPELEMENTS.put("Th", 232.0377d);
        MAPELEMENTS.put("Pa", 231.03588d);
        MAPELEMENTS.put("U", 238.02891d);
        MAPELEMENTS.put("Np", 237.0d);
        MAPELEMENTS.put("Pu", 244.0d);
        MAPELEMENTS.put("Am", 243.0d);
        MAPELEMENTS.put("Cm", 247.0d);
        MAPELEMENTS.put("Bk", 247.0d);
        MAPELEMENTS.put("Cf", 251.0d);
        MAPELEMENTS.put("Es", 252.0d);
        MAPELEMENTS.put("Fm", 257.0d);
        MAPELEMENTS.put("Md", 258.0d);
        MAPELEMENTS.put("No", 259.0d);
        MAPELEMENTS.put("Lr", 266.0d);
        MAPELEMENTS.put("Rf", 267.0d);
        MAPELEMENTS.put("Db", 268.0d);
        MAPELEMENTS.put("Sg", 269.0d);
        MAPELEMENTS.put("Bh", 270.0d);
        MAPELEMENTS.put("Hs", 269.0d);
        MAPELEMENTS.put("Mt", 278.0d);
        MAPELEMENTS.put("Ds", 281.0d);
        MAPELEMENTS.put("Rg", 282.0d);
        MAPELEMENTS.put("Cn", 285.0d);
        MAPELEMENTS.put("Uut", 286.0d);
        MAPELEMENTS.put("Fl", 289.0d);
        MAPELEMENTS.put("Uup", 289.0d);
        MAPELEMENTS.put("Lv", 293.0d);
        MAPELEMENTS.put("Uus", 294.0d);
        MAPELEMENTS.put("Uuo", 294.0d);
    }

    public static final Set<String> SETCHNOPS = new HashSet<String>();
    static {
        
        SETCHNOPS.add("C");
        SETCHNOPS.add("H");
        SETCHNOPS.add("N");
        SETCHNOPS.add("O");
        SETCHNOPS.add("P");
        SETCHNOPS.add("S");
    }
    public static final Set<String> SETCHNOPSCL = new HashSet<String>();
    static {
        SETCHNOPSCL.add("C");
        SETCHNOPSCL.add("H");
        SETCHNOPSCL.add("N");
        SETCHNOPSCL.add("O");
        SETCHNOPSCL.add("P");
        SETCHNOPSCL.add("S");
        SETCHNOPSCL.add("Cl");
    }

}
