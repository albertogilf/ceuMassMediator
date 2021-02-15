package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum PolarityType {
    @SerializedName("direct")
    DIRECT,
    @SerializedName("reverse")
    REVERSE;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case DIRECT:
                value = "Direct";
                break;
            case REVERSE:
                value = "Reverse";
                break;
        }
        return value;
    }

    public int intValue() {
        int value = -1;
        switch (this) {
            case DIRECT:
                value = 1;
                break;
            case REVERSE:
                value = 2;
                break;
        }
        return value;
    }
}
