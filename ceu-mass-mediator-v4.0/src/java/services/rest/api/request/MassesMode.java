package services.rest.api.request;

import com.google.gson.annotations.SerializedName;
import utilities.DataFromInterfacesUtilities;

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



    public int intValue() {
        return DataFromInterfacesUtilities.inputMassModeToInteger(this.toString());
    }

}

