package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum IonSourceVoltage {
    @SerializedName("100V")
    _100V,
    @SerializedName("200V")
    _200V;



    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case _100V:
                value = "100V";
                break;
            case _200V:
                value = "200V";
                break;
        }
        return value;
    }



    public int intValue() {
        int value = 100;
        switch (this) {
            case _100V:
                value = 100;
                break;
            case _200V:
                value = 200;
                break;
        }
        return value;
    }

}

