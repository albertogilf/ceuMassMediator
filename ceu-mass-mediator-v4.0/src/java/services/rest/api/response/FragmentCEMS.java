package services.rest.api.response;

import CEMS.CEMSAnnotationFragment;
import java.util.ArrayList;
import java.util.List;
import pathway.Pathway;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class FragmentCEMS {


    private final int precursorIon_compound_id; //precursor.compound_id
    private final String name;
    private final double mz;
    private final int errorPPM; //errorMZ
    protected double standardRMT = 0; //effMob
    protected int errorRMT = 0;//errorEffMob
    private final String precursorIon_cas_id;
    private final String precursorIon_hmdb_id;
    private final String precursorIon_hmdb_uri;
    private final String precursorIon_metlin_id;
    private final String precursorIon_metlin_uri;
    private final String precursorIon_lipidmaps_id;
    private final String precursorIon_lipidmaps_uri;
    private final String precursorIon_kegg_id;
    private final String precursorIon_kegg_uri;
    private final String precursorIon_pubchem_id;
    private final String precursorIon_pubchem_uri;
    private final String precursorIon_chebi_id;
    private final String precursorIon_chebi_uri;
    private final List<PathWay> pathways; //precursorIon pathways



    public FragmentCEMS(CEMSAnnotationFragment fragment) {
        this.precursorIon_compound_id = fragment.getPrecursorIon().getCompound_id();
        this.name = fragment.getName();
        this.mz = fragment.getMz();
        this.errorPPM = fragment.getErrorMZ();
        this.precursorIon_cas_id = fragment.getPrecursorIon().getCas_id();
        this.precursorIon_hmdb_id = fragment.getPrecursorIon().getHmdb_id();
        this.precursorIon_metlin_id = fragment.getPrecursorIon().getMetlin_id();
        this.precursorIon_lipidmaps_id = fragment.getPrecursorIon().getLm_id();
        this.precursorIon_kegg_id = fragment.getPrecursorIon().getKegg_id();
        this.precursorIon_pubchem_id = fragment.getPrecursorIon().getPc_id();
        this.precursorIon_chebi_id = fragment.getPrecursorIon().getChebi_id();
        this.precursorIon_hmdb_uri = fragment.getPrecursorIon().getCompoundHMDBWebPage();
        this.precursorIon_metlin_uri = fragment.getPrecursorIon().getCompoundMetlinWebPage();
        this.precursorIon_lipidmaps_uri = fragment.getPrecursorIon().getCompoundLMWebPage();
        this.precursorIon_kegg_uri = fragment.getPrecursorIon().getCompoundKeggWebPage();
        this.precursorIon_pubchem_uri = fragment.getPrecursorIon().getCompoundPubChemWebPage();
        this.precursorIon_chebi_uri = fragment.getPrecursorIon().getCompoundChebiWebPage();
        this.pathways = new ArrayList<>();
        for (Pathway pathway : fragment.getPrecursorIon().getPathways()) {
            this.pathways.add(new PathWay(pathway.getPathwayName(), pathway.getPathwayWebPage()));
        }
    }

}

