package services.rest.api;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum NeutralAdducts {
    @SerializedName("all")
    ALL,
    @SerializedName("M")
    NM;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case ALL:
                value = "All";
                break;
            case NM:
                value = "M";
                break;
        }
        return value;
    }
}
