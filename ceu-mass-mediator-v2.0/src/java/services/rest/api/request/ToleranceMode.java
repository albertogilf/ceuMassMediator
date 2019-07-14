package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum ToleranceMode {
    @SerializedName("ppm")
    PPM,
    @SerializedName("mDa")
    MDA;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case PPM:
                value = "ppm";
                break;
            case MDA:
                value = "mDa";
                break;
        }
        return value;
    }
}
