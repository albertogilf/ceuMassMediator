package services.rest.api.request;

import com.google.gson.annotations.SerializedName;
import utilities.DataFromInterfacesUtilities;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum CEMSToleranceMode {
    @SerializedName("percentage")
    PERCENTAGE;



    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case PERCENTAGE:
                value = "percentage";
                break;
        }
        return value;
    }



    public int intValue() {
        return DataFromInterfacesUtilities.toleranceTypeToInteger(this.toString());
    }

}

