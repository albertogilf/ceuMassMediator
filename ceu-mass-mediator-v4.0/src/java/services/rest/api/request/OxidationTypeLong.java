package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum OxidationTypeLong {
    @SerializedName("allOxidations")
    ALL,
    @SerializedName("O")
    O,
    @SerializedName("OH")
    OH,
    @SerializedName("OH-OH")
    OHOH,
    @SerializedName("OOH")
    OOH;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case ALL:
                value = "allOxidations";
                break;
            case O:
                value = "O";
                break;
            case OH:
                value = "OH";
                break;
            case OHOH:
                value = "OH-OH";
                break;
            case OOH:
                value = "OOH";
                break;
        }
        return value;
    }
}
