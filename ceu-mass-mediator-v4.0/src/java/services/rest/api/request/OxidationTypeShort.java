package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum OxidationTypeShort {
    @SerializedName("allOxidations")
    ALL,
    @SerializedName("CO")
    CO,
    @SerializedName("COOH")
    COOH;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case ALL:
                value = "allOxidations";
                break;
            case CO:
                value = "CO";
                break;
            case COOH:
                value = "COOH";
                break;
        }
        return value;
    }
}
