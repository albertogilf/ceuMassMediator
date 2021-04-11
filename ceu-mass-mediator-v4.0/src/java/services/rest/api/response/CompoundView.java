package services.rest.api.response;

import java.util.List;
import services.rest.api.request.IonMode;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class CompoundView extends View {

    private static final String title = "Compound";

    private final Integer identifier;

    private final String cas_id;

    private final String name;
    private final String formula;
    private final Double mass;

    private final String charge_type;
    private final Integer charge_number;

    private final Double logP;

    private final String kegg_id;
    private final String lm_id;
    private final String hmdb_id;
    private final String metlin_id;
    private final Integer pc_id;
    private final String aspergillus_web_name;
    private final Integer chebi_id;
    private final Integer aspergillus_id;
    private final String knapsack_id;
    private final Integer npatlas_id;

    private final Integer oh_position;

    private final String biological_activity;
    private final String mesh_nomenclature;

    private final String iupac_classification;
    private final String inchi;
    private final String inchi_key;

    private final String smiles;

    // Lipid attributes
    private final Integer num_chains;
    private final Integer number_carbons;
    private final Integer double_bonds;

    // LipidMaps Classification
    private final String category;
    private final String main_class;
    private final String sub_class;
    private final String class_level4;

    private final List<Link> reactions;

    private final List<Link> classyfire_classification;
    private final List<Link> ontology_terms;
    private final List<Link> references;
    private final List<Link> organisms;

    // POR SI ACA.
    /* NUEVA SECCION Electrophoretic information EFF MOBILITY FOR CE. It
     * can have up to 16 different eff mobilities
     * Fichero en DBUpdater create_tables_CE_MS.sql
     * BUFFER UN MAPA -> 1 Formic Acid 1M; 2 -> acetic acid 10% Methanol
     * TEMPERATURE INT IN CELSIUS º
     * IONIZATION MODE 1 -> POSITIVE; 2 -> NEGATIVE
     * ionization_mode 1-> DIRECT; 2 -> INVERSE
     * select buffer, temperature, polarity, ionization_mode, eff_mobility from
     * ce_eff_mob cef inner join
     * ce_experimental_properties cep on cef.ce_exp_prop_id = cep.ce_exp_prop_id
     * where ce_eff_mob.compound_id = @compound_id
     */
    public CompoundView(Integer identifier, String cas_id, String name,
            String formula, Double mass, Integer charge_type,
            Integer charge_number, Double logP, String kegg_id,
            String lm_id, String hmdb_id, String metlin_id,
            Integer pc_id, Integer chebi_id,
            Integer aspergillus_id, Integer oh_position,
            String mesh_nomenclature, String iupac_classification,
            String aspergillus_web_name, String inchi, String inchi_key,
            String smiles, Integer num_chains,
            Integer number_carbons, Integer double_bonds, String category,
            String main_class, String sub_class, String class_level4,
            String created, String last_updated,
            String knapsack_id,
            Integer npatlas_id,
            String biological_activity,
            List<Link> reactions,
            List<Link> classyfire_classification,
            List<Link> ontology_terms,
            List<Link> references,
            List<Link> organisms) {
        super(created, last_updated);
        this.identifier = identifier;
        this.cas_id = cas_id;
        this.name = name;
        this.formula = formula;
        this.mass = mass;
        this.charge_type = (charge_type != null ? IonMode.toString(charge_type) : null);
        this.charge_number = charge_number;
        this.logP = logP;
        this.kegg_id = kegg_id;
        this.lm_id = lm_id;
        this.hmdb_id = hmdb_id;
        this.metlin_id = metlin_id;
        this.pc_id = pc_id;
        this.chebi_id = chebi_id;
        this.aspergillus_id = aspergillus_id;
        this.oh_position = oh_position;
        this.mesh_nomenclature = mesh_nomenclature;
        this.iupac_classification = iupac_classification;
        this.aspergillus_web_name = aspergillus_web_name;
        this.inchi = inchi;
        this.inchi_key = inchi_key;
        this.smiles = smiles;
        this.num_chains = num_chains;
        this.number_carbons = number_carbons;
        this.double_bonds = double_bonds;
        this.category = category;
        this.main_class = main_class;
        this.sub_class = sub_class;
        this.class_level4 = class_level4;
        this.knapsack_id = knapsack_id;
        this.npatlas_id = npatlas_id;
        this.biological_activity = biological_activity;
        this.reactions = reactions;
        this.classyfire_classification = classyfire_classification;
        this.ontology_terms = ontology_terms;
        this.references = references;
        this.organisms = organisms;
    }

    public List<Link> getReferences() {
        return references;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public String getCas_id() {
        return cas_id;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public Double getMass() {
        return mass;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public Integer getCharge_number() {
        return charge_number;
    }

    public Double getLogP() {
        return logP;
    }

    public String getKegg_id() {
        return kegg_id;
    }

    public String getLm_id() {
        return lm_id;
    }

    public String getHmdb_id() {
        return hmdb_id;
    }

    public String getMetlin_id() {
        return metlin_id;
    }

    public Integer getPc_id() {
        return pc_id;
    }

    public Integer getChebi_id() {
        return chebi_id;
    }

    public Integer getAspergillus_id() {
        return aspergillus_id;
    }

    public Integer getOh_position() {
        return oh_position;
    }

    public String getMesh_nomenclature() {
        return mesh_nomenclature;
    }

    public String getIupac_classification() {
        return iupac_classification;
    }

    public String getAspergillus_web_name() {
        return aspergillus_web_name;
    }

    public String getInchi() {
        return inchi;
    }

    public String getInchi_key() {
        return inchi_key;
    }

    public String getSmiles() {
        return smiles;
    }

    public Integer getNum_chains() {
        return num_chains;
    }

    public Integer getNumber_carbons() {
        return number_carbons;
    }

    public Integer getDouble_bonds() {
        return double_bonds;
    }

    public String getCategory() {
        return category;
    }

    public String getMain_class() {
        return main_class;
    }

    public String getSub_class() {
        return sub_class;
    }

    public String getClass_level4() {
        return class_level4;
    }

    public String getKnapsack_id() {
        return knapsack_id;
    }

    public Integer getNpatlas_id() {
        return npatlas_id;
    }

    public String getBiological_activity() {
        return biological_activity;
    }

    public List<Link> getReactions() {
        return reactions;
    }

    public List<Link> getClassyfire_classification() {
        return classyfire_classification;
    }

    public List<Link> getOntology_terms() {
        return ontology_terms;
    }

    public List<Link> getOrganisms() {
        return organisms;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

}
