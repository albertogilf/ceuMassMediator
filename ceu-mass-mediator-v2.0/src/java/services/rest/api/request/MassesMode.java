package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum MassesMode {
    @SerializedName("neutral")
    NEUTRAL,
    @SerializedName("mz")
    MZ;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case NEUTRAL:
                value = "neutral";
                break;
            case MZ:
                value = "mz";
                break;
        }
        return value;
    }
}
