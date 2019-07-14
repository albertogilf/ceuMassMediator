package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public enum ChemAlphabet {
    @SerializedName("all")
    ALL,
    @SerializedName("CHNOPS")
    CHNOPS,
    @SerializedName("CHNOPSCl")
    CHNOPSCL;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case ALL:
                value = "ALL";
                break;
            case CHNOPS:
                value = "CHNOPS";
                break;
            case CHNOPSCL:
                value = "CHNOPSCL";
                break;
        }
        return value;
    }
}