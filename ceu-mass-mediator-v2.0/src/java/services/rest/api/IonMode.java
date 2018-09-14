package services.rest.api;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum IonMode {
    @SerializedName("neutral")
    NEUTRAL,
    @SerializedName("positive")
    POSITIVE,
    @SerializedName("negative")
    NEGATIVE;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case NEUTRAL:
                value = "neutral";
                break;
            case POSITIVE:
                value = "positive";
                break;
            case NEGATIVE:
                value = "negative";
                break;
        }
        return value;
    }
}
