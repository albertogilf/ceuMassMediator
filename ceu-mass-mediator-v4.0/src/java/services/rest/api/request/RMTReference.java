package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum RMTReference {
    @SerializedName("paracetamol")
    PARACETAMOL,
    @SerializedName("l-methionine-sulfone")
    LMETHIONINESULFONE;



    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case PARACETAMOL:
                value = "Paracetamol";
                break;
            case LMETHIONINESULFONE:
                value = "L-Methionine sulfone";
                break;
        }
        return value;
    }



    public int intValue() {
        int value = 0;
        switch (this) {
            case PARACETAMOL:
                value = 73414;
                break;
            case LMETHIONINESULFONE:
                value = 180838;
                break;
        }
        return value;
    }

}

