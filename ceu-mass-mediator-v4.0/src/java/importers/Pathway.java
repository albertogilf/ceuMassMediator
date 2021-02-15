package importers;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.ss.usermodel.Hyperlink;
import utilities.CompoundsComparer;
import static utilities.Constants.*;
import utilities.Constants;

/**
 *
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class Pathway implements Serializable {

    private static final long serialVersionUID = 1L;

    public String code;
    public String name;
    public Hyperlink hyperl;
    public List<CompoundForPathway> compounds;
    // maximum exlusivity of any compound in the pathway
    private double maxExclusivity;
    // percentage of compounds present in the pathway respecting total compounds in it.
    private double percentage;

    public Pathway(String code, String name) {
        this.code = code;
        this.name = name;
        compounds = new LinkedList();
        this.maxExclusivity = 0;
        this.percentage = 0;
    }

    public Pathway(String code, String name, Hyperlink hyperl) {
        this.code = code;
        this.name = name;
        this.hyperl = hyperl;
        compounds = new LinkedList();
        this.maxExclusivity = 0;
        this.percentage = 0;
    }

    /**
     * Deprecated since CMM uses a comparator for sorts a set, but method is
     * here in case it is needed in the future. In this case, class should
     * implement Comparable
     *
     * method for sort function.
     *
     * @param t
     * @return
     */
    /*
    @Override
    public int compareTo(Object t) {
        Integer a = this.compounds.size();
        Pathway p = (Pathway) t;
        return a.compareTo(p.compounds.size());
    }
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public List<CompoundForPathway> getCompounds() {
        return compounds;
    }

    public void setCompounds(List<CompoundForPathway> Compounds) {
        this.compounds = Compounds;
    }

    public Hyperlink getHyperl() {
        return hyperl;
    }

    public void setHyperl(Hyperlink hyperl) {
        this.hyperl = hyperl;
    }

    /**
     * Add the compound to the group of compounds
     *
     * @param compound
     */
    public void addCompound(CompoundForPathway compound) {
        this.compounds.add(compound);
    }

    /**
     *
     * @return the web page of the compound
     */
    public String getWebPage() {
        return Constants.WEB_PATHWAY_KEGG + this.code;
    }

    /**
     * Creates the structure of the pathways in html format Less job on
     * application server
     *
     * @return column pathway
     */
    public String toHtml() {
        String s = "";
        try {
            //String a= hyperl.getURL().toString().substring(0, 51);
            String a = hyperl.getAddress();
            s = "<a href='" + a + "'>"
                    + this.name + "</a>";
        } catch (NullPointerException e) {
//            s=hyperl.toString();
        }
        return s;
    }

    public String keggWebPage() {
        return WEB_KEGG;
    }

    public String HMDBWebPage() {
        return WEB_HMDB;
    }

    public String LMWebPage() {
        return WEB_LIPID_MAPS;
    }

    public String metlinWebPage() {
        return WEB_METLIN;
    }

    public String PCWebPage() {
        return WEB_PUBCHEMICHAL;
    }

    public String getChebiWebPage() {
        return Constants.WEB_CHEBI;
    }

    public void sortCompounds() {
        Collections.sort(compounds, new CompoundsComparer());
    }

    public double getMaxExclusivity() {
        return maxExclusivity;
    }

    public void setMaxExclusivity(double maxExclusivity) {
        this.maxExclusivity = maxExclusivity;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

}
