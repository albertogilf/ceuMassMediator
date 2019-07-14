package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum SpectraType {
    @SerializedName("experimental")
    EXPERIMENTAL,
    @SerializedName("predicted")
    PREDICTED;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case EXPERIMENTAL:
                value = "experimental";
                break;
            case PREDICTED:
                value = "predicted";
                break;
        }
        return value;
    }
    
}
