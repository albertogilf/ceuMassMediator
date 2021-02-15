package utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class PeriodicTable {

    public static final Map<Element, Double> MAPELEMENTS = new LinkedHashMap<Element, Double>();
    public static final List<Element> LISTELEMENTS = new LinkedList<Element>();

    static {
        MAPELEMENTS.put(Element.H, 1.0078250321d);
        MAPELEMENTS.put(Element.He, 2.014102d);
        MAPELEMENTS.put(Element.He, 4.0026032542d);
        MAPELEMENTS.put(Element.Li, 7.016004558d);
        MAPELEMENTS.put(Element.Be, 9.012182d);
        MAPELEMENTS.put(Element.B, 11.009305d);
        MAPELEMENTS.put(Element.C, 12d);
        MAPELEMENTS.put(Element.N, 14.003074d);
        MAPELEMENTS.put(Element.O, 15.994915d);
        MAPELEMENTS.put(Element.F, 18.998403163d);
        MAPELEMENTS.put(Element.Ne, 19.99244d);
        MAPELEMENTS.put(Element.Na, 22.98976928d);
        MAPELEMENTS.put(Element.Mg, 23.98504d);
        MAPELEMENTS.put(Element.Al, 26.9815385d);
        MAPELEMENTS.put(Element.Si, 27.97693d);
        MAPELEMENTS.put(Element.P, 30.973761632d);
        MAPELEMENTS.put(Element.S, 31.9720710015d);
        MAPELEMENTS.put(Element.Cl, 34.96885d);
        MAPELEMENTS.put(Element.Ar, 39.962383d);
        MAPELEMENTS.put(Element.K, 38.96371d);
        MAPELEMENTS.put(Element.Ca, 39.96259d);
        MAPELEMENTS.put(Element.Sc, 44.95591d);
        MAPELEMENTS.put(Element.Ti, 47.94794d);
        MAPELEMENTS.put(Element.V, 50.94396d);
        MAPELEMENTS.put(Element.Cr, 51.94051d);
        MAPELEMENTS.put(Element.Mn, 54.93804d);
        MAPELEMENTS.put(Element.Fe, 55.93494d);
        MAPELEMENTS.put(Element.Co, 58.93319d);
        MAPELEMENTS.put(Element.Ni, 57.93534d);
        MAPELEMENTS.put(Element.Cu, 62.92960d);
        MAPELEMENTS.put(Element.Zn, 63.92914d);
        MAPELEMENTS.put(Element.Ga, 68.92557d);
        MAPELEMENTS.put(Element.Ge, 73.92118d);
        MAPELEMENTS.put(Element.As, 74.92159d);
        MAPELEMENTS.put(Element.Se, 79.91652d);
        MAPELEMENTS.put(Element.Br, 78.91834d);
        MAPELEMENTS.put(Element.Kr, 83.91150d);
        MAPELEMENTS.put(Element.Rb, 84.91179d);
        MAPELEMENTS.put(Element.Sr, 87.90561d);
        MAPELEMENTS.put(Element.Y, 88.90584d);
        MAPELEMENTS.put(Element.Zr, 89.90470d);
        MAPELEMENTS.put(Element.Nb, 92.90637d);
        MAPELEMENTS.put(Element.Mo, 97.90540d);
        MAPELEMENTS.put(Element.Tc, 98.0d);
        MAPELEMENTS.put(Element.Ru, 101.90434d);
        MAPELEMENTS.put(Element.Rh, 102.90550d);
        MAPELEMENTS.put(Element.Pd, 105.90348d);
        MAPELEMENTS.put(Element.Ag, 106.90509d);
        MAPELEMENTS.put(Element.Cd, 113.90337d);
        MAPELEMENTS.put(Element.In, 114.90388d);
        MAPELEMENTS.put(Element.Sn, 119.90220d);
        MAPELEMENTS.put(Element.Sb, 120.90381d);
        MAPELEMENTS.put(Element.Te, 129.90622d);
        MAPELEMENTS.put(Element.I, 126.90447d);
        MAPELEMENTS.put(Element.Xe, 131.90416d);
        MAPELEMENTS.put(Element.Cs, 132.90545d);
        MAPELEMENTS.put(Element.Ba, 137.90525d);
        MAPELEMENTS.put(Element.La, 138.90636d);
        MAPELEMENTS.put(Element.Ce, 139.90544d);
        MAPELEMENTS.put(Element.Pr, 140.90766d);
        MAPELEMENTS.put(Element.Nd, 141.90773d);
        MAPELEMENTS.put(Element.Pm, 145.0d);
        MAPELEMENTS.put(Element.Sm, 151.91974d);
        MAPELEMENTS.put(Element.Eu, 152.92124d);
        MAPELEMENTS.put(Element.Gd, 157.92411d);
        MAPELEMENTS.put(Element.Tb, 158.92535d);
        MAPELEMENTS.put(Element.Dy, 163.92918d);
        MAPELEMENTS.put(Element.Ho, 164.93033d);
        MAPELEMENTS.put(Element.Er, 165.93030d);
        MAPELEMENTS.put(Element.Tm, 168.93422d);
        MAPELEMENTS.put(Element.Yb, 173.93887d);
        MAPELEMENTS.put(Element.Lu, 174.94078d);
        MAPELEMENTS.put(Element.Hf, 179.94656d);
        MAPELEMENTS.put(Element.Ta, 180.94800d);
        MAPELEMENTS.put(Element.W, 183.95093d);
        MAPELEMENTS.put(Element.Re, 186.95575d);
        MAPELEMENTS.put(Element.Os, 191.96148d);
        MAPELEMENTS.put(Element.Ir, 192.96292d);
        MAPELEMENTS.put(Element.Pt, 194.96479d);
        MAPELEMENTS.put(Element.Au, 196.96657d);
        MAPELEMENTS.put(Element.Hg, 201.97064d);
        MAPELEMENTS.put(Element.Tl, 204.97443d);
        MAPELEMENTS.put(Element.Pb, 207.97665d);
        MAPELEMENTS.put(Element.Bi, 208.98040d);
        MAPELEMENTS.put(Element.Po, 209.98287d);
        MAPELEMENTS.put(Element.At, 210.0d);
        MAPELEMENTS.put(Element.Rn, 222.0d);
        MAPELEMENTS.put(Element.Fr, 223.0d);
        MAPELEMENTS.put(Element.Ra, 226.0d);
        MAPELEMENTS.put(Element.Ac, 227.0d);
        MAPELEMENTS.put(Element.Th, 232.03806d);
        MAPELEMENTS.put(Element.Pa, 231.03588d);
        MAPELEMENTS.put(Element.U, 238.05079d);
        MAPELEMENTS.put(Element.Np, 237.0d);
        MAPELEMENTS.put(Element.Pu, 244.0d);
        MAPELEMENTS.put(Element.Am, 243.0d);
        MAPELEMENTS.put(Element.Cm, 247.0d);
        MAPELEMENTS.put(Element.Bk, 247.0d);
        MAPELEMENTS.put(Element.Cf, 251.0d);
        MAPELEMENTS.put(Element.Es, 252.0d);
        MAPELEMENTS.put(Element.Fm, 257.0d);
        MAPELEMENTS.put(Element.Md, 258.0d);
        MAPELEMENTS.put(Element.No, 259.0d);
        MAPELEMENTS.put(Element.Lr, 266.0d);
        MAPELEMENTS.put(Element.Rf, 267.0d);
        MAPELEMENTS.put(Element.Db, 268.0d);
        MAPELEMENTS.put(Element.Sg, 269.0d);
        MAPELEMENTS.put(Element.Bh, 270.0d);
        MAPELEMENTS.put(Element.Hs, 269.0d);
        MAPELEMENTS.put(Element.Mt, 278.0d);
        MAPELEMENTS.put(Element.Ds, 281.0d);
        MAPELEMENTS.put(Element.Rg, 282.0d);
        MAPELEMENTS.put(Element.Cn, 285.0d);
        MAPELEMENTS.put(Element.Uut, 286.0d);
        MAPELEMENTS.put(Element.Fl, 289.0d);
        MAPELEMENTS.put(Element.Uup, 289.0d);
        MAPELEMENTS.put(Element.Lv, 293.0d);
        MAPELEMENTS.put(Element.Uus, 294.0d);
        MAPELEMENTS.put(Element.Uuo, 294.0d);
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
    public static final Set<String> SETCHNOPSD = new HashSet<String>();

    static {

        SETCHNOPSD.add("C");
        SETCHNOPSD.add("H");
        SETCHNOPSD.add("N");
        SETCHNOPSD.add("O");
        SETCHNOPSD.add("P");
        SETCHNOPSD.add("S");
        SETCHNOPSD.add("D");
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

    public static final Set<String> SETCHNOPSCLD = new HashSet<String>();

    static {
        SETCHNOPSCLD.add("C");
        SETCHNOPSCLD.add("H");
        SETCHNOPSCLD.add("N");
        SETCHNOPSCLD.add("O");
        SETCHNOPSCLD.add("P");
        SETCHNOPSCLD.add("S");
        SETCHNOPSCLD.add("Cl");
        SETCHNOPSCLD.add("D");
    }

}
