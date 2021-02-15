package services.rest.api.request;

import com.google.gson.annotations.SerializedName;
import utilities.DataFromInterfacesUtilities;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum IonModeMS {
    @SerializedName("positive")
    POSITIVE,
    @SerializedName("negative")
    NEGATIVE;



    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case POSITIVE:
                value = "positive";
                break;
            case NEGATIVE:
                value = "negative";
                break;
        }
        return value;
    }



    public int intValue() {
        return DataFromInterfacesUtilities.ionizationModeToInteger(this.toString());
    }

}

