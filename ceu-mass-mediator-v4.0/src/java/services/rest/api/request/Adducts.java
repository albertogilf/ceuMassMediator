package services.rest.api.request;

import com.google.gson.annotations.SerializedName;
import utilities.DataFromInterfacesUtilities;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum Adducts {
    //COMMON
    @SerializedName("all")
    ALL,
    //NEUTRAL
    @SerializedName("allNeutral")
    NeuALL,
    @SerializedName("M")
    NM,
    //POSITIVE
    @SerializedName("allPositives")
    PosALL,
    @SerializedName("M+H")
    PMpH,
    @SerializedName("M+2H")
    PMp2H,
    @SerializedName("M+Na")
    PMpNA,
    @SerializedName("M+K")
    PMpK,
    @SerializedName("M+NH4")
    PMpNH4,
    @SerializedName("M+H-H2O")
    PMpHmH2O,
    @SerializedName("M+H+NH4")
    PMpHpNH4,
    @SerializedName("2M+H")
    P2MpH,
    @SerializedName("2M+Na")
    P2MpNA,
    @SerializedName("M+H+HCOONa")
    PMpHpHCOONA,
    @SerializedName("2M+H-H2O")
    P2MpHmH2O,
    @SerializedName("M+3H")
    PMp3H,
    @SerializedName("M+2H+Na")
    PMp2HpNA,
    @SerializedName("M+H+2K")
    PMpHp2K,
    @SerializedName("M+H+2Na")
    PMpHp2NA,
    @SerializedName("M+3Na")
    PMp3NA,
    @SerializedName("M+H+Na")
    PMpHpNA,
    @SerializedName("M+H+K")
    PMpHpK,
    @SerializedName("M+ACN+2H")
    PMpACNp2H,
    @SerializedName("M+2Na")
    PMp2NA,
    @SerializedName("M+2ACN+2H")
    PMp2ACNp2H,
    @SerializedName("M+3ACN+2H")
    PMp3ACNp2H,
    @SerializedName("M+CH3OH+H")
    PMpCH3OHpH,
    @SerializedName("M+ACN+H")
    PMpACNpH,
    @SerializedName("M+2Na-H")
    PMp2NAmH,
    @SerializedName("M+IsoProp+H")
    PMpISOPROPpH,
    @SerializedName("M+ACN+Na")
    PMpACNpNA,
    @SerializedName("M+2K-H")
    PMp2KmH,
    @SerializedName("M+DMSO+H")
    PMpDMSOpH,
    @SerializedName("M+2ACN+H")
    PMp2ACNpH,
    @SerializedName("M+IsoProp+Na+H")
    PMpISOPROPpNApH,
    @SerializedName("2M+NH4")
    P2MpNH4,
    @SerializedName("2M+K")
    P2MpK,
    @SerializedName("2M+ACN+H")
    P2MpACNpH,
    @SerializedName("2M+ACN+Na")
    P2MpACNpNA,
    @SerializedName("M+H-2H2O")
    PMpHm2H2O,
    @SerializedName("M+NH4-H2O")
    PMpNH4mH2O,
    @SerializedName("M+Li")
    PMpLI,
    @SerializedName("2M+2H+3H2O")
    P2Mp2Hp3H2O,
    //NEGATIVE
    @SerializedName("allNegatives")
    NegALL,
    @SerializedName("M-H")
    NMmH,
    @SerializedName("M+Cl")
    NMpCL,
    @SerializedName("M+FA-H")
    NMpFAmH,
    @SerializedName("M-H-H2O")
    NMmHmH2O,
    @SerializedName("M-H+HCOONa")
    NMmHpHCOONA,
    @SerializedName("M-H+HCOONa")
    NMmHpCH3COONA,
    @SerializedName("2M-H")
    N2MmH,
    @SerializedName("M-3H")
    NMm3H,
    @SerializedName("M-2H")
    NMm2H,
    @SerializedName("M+Na-2H")
    NMpNAm2H,
    @SerializedName("M+K-2H")
    NMpKm2H,
    @SerializedName("M+Hac-H")
    NMpHACmH,
    @SerializedName("M+Br")
    NMpBR,
    @SerializedName("M+TFA-H")
    NMpTFAmH,
    @SerializedName("2M+FA-H")
    N2MpFAmH,
    @SerializedName("2M+Hac-H")
    N2MpHACmH,
    @SerializedName("3M-H")
    N3MmH,
    @SerializedName("M+F")
    NMpF;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            //COMMON
            case ALL:
                value = "all";
                break;
            //NEUTRAL
            case NeuALL:
                value = DataFromInterfacesUtilities.ALLADDUCTS_NEUTRAL;
                break;
            case NM:
                value = "M";
                break;
            //POSITIVE
            case PosALL:
                value = DataFromInterfacesUtilities.ALLADDUCTS_POSITIVE;
                break;
            case PMpH:
                value = "M+H";
                break;
            case PMp2H:
                value = "M+2H";
                break;
            case PMpNA:
                value = "M+Na";
                break;
            case PMpK:
                value = "M+K";
                break;
            case PMpNH4:
                value = "M+NH4";
                break;
            case PMpHmH2O:
                value = "M+H-H2O";
                break;
            case PMpHpNH4:
                value = "M+H+NH4";
                break;
            case P2MpH:
                value = "2M+H";
                break;
            case P2MpNA:
                value = "2M+Na";
                break;
            case PMpHpHCOONA:
                value = "M+H+HCOONa";
                break;
            case P2MpHmH2O:
                value = "2M+H-H2O";
                break;
            case PMp3H:
                value = "M+3H";
                break;
            case PMp2HpNA:
                value = "M+2H+Na";
                break;
            case PMpHp2K:
                value = "M+H+2K";
                break;
            case PMpHp2NA:
                value = "M+H+2Na";
                break;
            case PMp3NA:
                value = "M+3Na";
                break;
            case PMpHpNA:
                value = "M+H+Na";
                break;
            case PMpHpK:
                value = "M+H+K";
                break;
            case PMpACNp2H:
                value = "M+ACN+2H";
                break;
            case PMp2NA:
                value = "M+2Na";
                break;
            case PMp2ACNp2H:
                value = "M+2ACN+2H";
                break;
            case PMp3ACNp2H:
                value = "M+3ACN+2H";
                break;
            case PMpCH3OHpH:
                value = "M+CH3OH+H";
                break;
            case PMpACNpH:
                value = "M+ACN+H";
                break;
            case PMp2NAmH:
                value = "M+2Na-H";
                break;
            case PMpISOPROPpH:
                value = "M+IsoProp+H";
                break;
            case PMpACNpNA:
                value = "M+ACN+Na";
                break;
            case PMp2KmH:
                value = "M+2K-H";
                break;
            case PMpDMSOpH:
                value = "M+DMSO+H";
                break;
            case PMp2ACNpH:
                value = "M+2ACN+H";
                break;
            case PMpISOPROPpNApH:
                value = "M+IsoProp+Na+H";
                break;
            case P2MpNH4:
                value = "2M+NH4";
                break;
            case P2MpK:
                value = "2M+K";
                break;
            case P2MpACNpH:
                value = "2M+ACN+H";
                break;
            case P2MpACNpNA:
                value = "2M+ACN+Na";
                break;
            case PMpHm2H2O:
                value = "M+H-2H2O";
                break;
            case PMpNH4mH2O:
                value = "M+NH4-H2O";
                break;
            case PMpLI:
                value = "M+Li";
                break;
            case P2Mp2Hp3H2O:
                value = "2M+2H+3H2O";
                break;
            //NEGATIVE
            case NegALL:
                value = DataFromInterfacesUtilities.ALLADDUCTS_NEGATIVE;
                break;
            case NMmH:
                value = "M-H";
                break;
            case NMpCL:
                value = "M+Cl";
                break;
            case NMpFAmH:
                value = "M+FA-H";
                break;
            case NMmHmH2O:
                value = "M-H-H2O";
                break;
            case NMmHpHCOONA:
                value = "M-H+HCOONa";
                break;
            case NMmHpCH3COONA:
                value = "M-H+HCOONa";
                break;
            case N2MmH:
                value = "2M-H";
                break;
            case NMm3H:
                value = "M-3H";
                break;
            case NMm2H:
                value = "M-2H";
                break;
            case NMpNAm2H:
                value = "M+Na-2H";
                break;
            case NMpKm2H:
                value = "M+K-2H";
                break;
            case NMpHACmH:
                value = "M+Hac-H";
                break;
            case NMpBR:
                value = "M+Br";
                break;
            case NMpTFAmH:
                value = "M+TFA-H";
                break;
            case N2MpFAmH:
                value = "2M+FA-H";
                break;
            case N2MpHACmH:
                value = "2M+Hac-H";
                break;
            case N3MmH:
                value = "3M-H";
                break;
            case NMpF:
                value = "M+F";
                break;
        }
        return value;
    }
}
