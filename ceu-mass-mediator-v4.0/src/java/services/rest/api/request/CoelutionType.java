package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public enum CoelutionType {
    @SerializedName("no-coelution")
    NOCOELUTION,
    @SerializedName("with-known-compound")
    WITHKNOWNCOMPOUND,
    @SerializedName("with-unknown-compound")
    WITHUNKNOWNCOMPOUND;

    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case NOCOELUTION:
                value = "no-coelution";
                break;
            case WITHKNOWNCOMPOUND:
                value = "with-known-compound";
                break;
            case WITHUNKNOWNCOMPOUND:
                value = "with-unknown-compound";
                break;
        }
        return value;
    }

    public double value() {
        double value = 0;
        switch (this) {
            case NOCOELUTION:
                value = 2;
                break;
            case WITHKNOWNCOMPOUND:
                value = 1;
                break;
            case WITHUNKNOWNCOMPOUND:
                value = 0;
                break;
        }
        return value;
    }
}
