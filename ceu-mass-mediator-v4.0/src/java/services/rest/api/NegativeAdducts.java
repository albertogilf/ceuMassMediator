package services.rest.api;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum NegativeAdducts {
    //NEGATIVE
    @SerializedName("all")
    ALL,
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
            //NEGATIVE
            case ALL:
                value = "All";
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
