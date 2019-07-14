package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum MetabolitesType {
    @SerializedName("all-except-peptides")
    AWPEPTIDES,
    @SerializedName("only-lipids")
    OLIPIDS,
    @SerializedName("all-including-peptides")
    ALLIPEPTIDES;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case AWPEPTIDES:
                value = "All except peptides";
                break;
            case OLIPIDS:
                value = "Only lipids";
                break;
            case ALLIPEPTIDES:
                value = "All including peptides";
                break;
        }
        return value;
    }
}
