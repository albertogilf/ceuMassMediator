package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum BufferType {
    @SerializedName("formic-acid-1m-20c")
    FORMIC,
    @SerializedName("acetic-acid-1m-25c")
    ACETIC;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case FORMIC:
                value = "formic acid 1M / 20ºC";
                break;
            case ACETIC:
                value = "acetic acid 10% v:v / 25ºC";
                break;
        }
        return value;
    }

    public int intValue() {
        int value = -1;
        switch (this) {
            case FORMIC:
                value = 1;
                break;
            case ACETIC:
                value = 2;
                break;
        }
        return value;
    }

    public int getTemperature() {
        int temperature = -1;
        switch (this) {
            case FORMIC:
                temperature = 20;
                break;
            case ACETIC:
                temperature = 25;
                break;
        }
        return temperature;
    }
}
