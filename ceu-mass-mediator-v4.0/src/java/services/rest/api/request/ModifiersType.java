package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum ModifiersType {
    @SerializedName("none")
    NONE,
    @SerializedName("NH3")
    NH3,
    @SerializedName("HCOO")
    HCOO,
    @SerializedName("CH3COO")
    CH3COO,
    @SerializedName("HCOONH3")
    HCOONH3,
    @SerializedName("CH3COONH3")
    CH3COONH3;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case NONE:
                value = "None";
                break;
            case NH3:
                value = "NH3";
                break;
            case HCOO:
                value = "HCOO";
                break;
            case CH3COO:
                value = "CH3COO";
                break;
            case HCOONH3:
                value = "HCOONH3";
                break;
            case CH3COONH3:
                value = "CH3COONH3";
                break;
        }
        return value;
    }
}
