package services.rest.api.request;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public enum Database {
    @SerializedName("all-except-mine")
    ALLWMINE,
    @SerializedName("all")
    ALL,
    @SerializedName("kegg")
    KEGG,
    @SerializedName("hmdb")
    HMDB,
    @SerializedName("lipidmaps")
    LIPIDMAPS,
    @SerializedName("metlin")
    METLIN,
    @SerializedName("mine")
    MINE,
    @SerializedName("in-house")
    INHOUSE,
    @SerializedName("aspergillus")
    ASPERGILLUS,
    @SerializedName("fahfa")
    FAHFA;



    @Override
    public String toString() {
        String value = "";
        switch (this) {
            case ALLWMINE:
                value = "AllWM";
                break;
            case ALL:
                value = "All";
                break;
            case KEGG:
                value = "Kegg";
                break;
            case HMDB:
                value = "HMDB";
                break;
            case LIPIDMAPS:
                value = "LipidMaps";
                break;
            case METLIN:
                value = "Metlin";
                break;
            case MINE:
                value = "MINE (Only In Silico Compounds)";
                break;
            case INHOUSE:
                value = "in-house";
                break;
            case ASPERGILLUS:
                value = "Aspergillus";
                break;
            case FAHFA:
                value = "FAHFA";
                break;
        }
        return value;
    }

}

