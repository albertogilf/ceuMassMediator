package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public enum CrossTalkType {
    @SerializedName("no")
    NOCROSSTALK,
    @SerializedName("soft")
    SOFTCROSSTALK,
    @SerializedName("hard")
    HARDCROSSTALK;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case NOCROSSTALK:
                value = "no";
                break;
            case SOFTCROSSTALK:
                value = "soft";
                break;
            case HARDCROSSTALK:
                value = "hard";
                break;
        }
        return value;
    }

    public double value() {
        double value = 0;
        switch (this) {
            case NOCROSSTALK:
                value = 2;
                break;
            case SOFTCROSSTALK:
                value = 1;
                break;
            case HARDCROSSTALK:
                value = 0;
                break;
        }
        return value;
    }
}
