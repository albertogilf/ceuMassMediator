package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum IonizationVoltage {
    @SerializedName("low")
    LOW,
    @SerializedName("med")
    MEDIUM,
    @SerializedName("high")
    HIGH,
    @SerializedName("all")
    ALL;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case LOW:
                value = "low";
                break;
            case MEDIUM:
                value = "med";
                break;
            case HIGH:
                value = "high";
                break;
            case ALL:
                value = "all";
                break;
        }
        return value;
    }
}
