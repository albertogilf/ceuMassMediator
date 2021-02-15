package services.rest.api.request;

import com.google.gson.annotations.SerializedName;
import utilities.DataFromInterfacesUtilities;

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



    public static String toString(int value) {
        String output = "";

        switch (value) {
            case 0:
                output = IonMode.NEUTRAL.toString();
                break;
            case 1:
                output = IonMode.POSITIVE.toString();
                break;
            case 2:
                output = IonMode.NEGATIVE.toString();
                break;
            default:
                output = IonMode.NEUTRAL.toString();
        }
        return output;
    }



    public int intValue() {
        return DataFromInterfacesUtilities.ionizationModeToInteger(this.toString());
    }

}

