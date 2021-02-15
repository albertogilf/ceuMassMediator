
package utilities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 * Controller (Bean) of the application for the oxidation feature
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 4.0, 5/12/2017
 */
public class ConstantsForQA {
    public static final List<SelectItem> LISTMSMSINT_10E3;

    static {
        List<SelectItem> LISTMSMSINT_10E3_TMP = new LinkedList<SelectItem>();
        LISTMSMSINT_10E3_TMP.add(new SelectItem("2", "> 10E3"));
        LISTMSMSINT_10E3_TMP.add(new SelectItem("1", "10E3-10E2"));
        LISTMSMSINT_10E3_TMP.add(new SelectItem("0", "< 10E2"));
        LISTMSMSINT_10E3 = Collections.unmodifiableList(LISTMSMSINT_10E3_TMP);
    }
    
    public static final List<SelectItem> LISTMSMSINT_10E4;

    static {
        List<SelectItem> LISTMSMSINT_10E4_TMP = new LinkedList<SelectItem>();
        LISTMSMSINT_10E4_TMP.add(new SelectItem("2", "> 10E4"));
        LISTMSMSINT_10E4_TMP.add(new SelectItem("1", "10E4-10E3"));
        LISTMSMSINT_10E4_TMP.add(new SelectItem("0", "< 10E3"));
        LISTMSMSINT_10E4 = Collections.unmodifiableList(LISTMSMSINT_10E4_TMP);
    }
    
    public static final List<SelectItem> LISTMSMSINT_10E5;

    static {
        List<SelectItem> LISTMSMSINT_10E5_TMP = new LinkedList<SelectItem>();
        LISTMSMSINT_10E5_TMP.add(new SelectItem("2", "> 10E4"));
        LISTMSMSINT_10E5_TMP.add(new SelectItem("1", "10E4-10E3"));
        LISTMSMSINT_10E5_TMP.add(new SelectItem("0", "< 10E3"));
        LISTMSMSINT_10E5 = Collections.unmodifiableList(LISTMSMSINT_10E5_TMP);
    }
    
    public static final List<SelectItem> LISTMSMSINT_10E6;

    static {
        List<SelectItem> LISTMSMSINT_10E6_TMP = new LinkedList<SelectItem>();
        LISTMSMSINT_10E6_TMP.add(new SelectItem("2", "> 10E5"));
        LISTMSMSINT_10E6_TMP.add(new SelectItem("1", "10E5-10E4"));
        LISTMSMSINT_10E6_TMP.add(new SelectItem("0", "< 10E4"));
        LISTMSMSINT_10E6 = Collections.unmodifiableList(LISTMSMSINT_10E6_TMP);
    }
    
    public static final List<SelectItem> LISTMSMSINT_10E7;

    static {
        List<SelectItem> LISTMSMSINT_10E7_TMP = new LinkedList<SelectItem>();
        LISTMSMSINT_10E7_TMP.add(new SelectItem("2", "> 10E6"));
        LISTMSMSINT_10E7_TMP.add(new SelectItem("1", "10E6-10E5"));
        LISTMSMSINT_10E7_TMP.add(new SelectItem("0", "< 10E5"));
        LISTMSMSINT_10E7 = Collections.unmodifiableList(LISTMSMSINT_10E7_TMP);
    }
    
}
