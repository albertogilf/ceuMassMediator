<#import "macros.ftl" as macros>
<@macros.header component=state />

<#if (success)>

    <@macros.title id=state.getIdentifier() name=state.getName()/>

    <fieldset class="attributes"><legend>Basic attributes</legend>
        <@macros.attvalue name="Formula" value=state.getFormula()/>
        <@macros.attvalue name="Mass" value=state.getMass()/>
        <@macros.attvalue name="Charge type" value=state.getCharge_type()/>
        <@macros.attvalue name="Charge number" value=state.getCharge_number()/>
        <@macros.attlongvalue name="Inchi" value=state.getInchi()/>
        <@macros.attvalue name="Inchi key" value=state.getInchi_key()/>
        <@macros.attvalue name="IUPAC Classification" value=state.getIupac_classification()/>
        <@macros.attlongvalue name="Smiles" value=state.getSmiles()/>
    </fieldset>

    <fieldset class="attributes"><legend>Identifiers</legend>
        <@macros.link name="Aspergillus" base=base_aspergillus id=state.getAspergillus_web_name()/>
        <@macros.attvalue name="Aspergillus id" value=state.getAspergillus_id()/>
        <@macros.attvalue name="CAS" value=state.getCas_id()/>
        <@macros.link name="Chebi" base=base_chebi id=state.getChebi_id()/>
        <@macros.link name="HMDB" base=base_hmdb id=state.getHmdb_id()/>
        <@macros.link name="KEGG" base=base_kegg id=state.getKegg_id()/>
        <@macros.link name="Knapsack" base=base_kn id=state.getKnapsack_id()/>
        <@macros.link name="LipidMaps" base=base_lm id=state.getLm_id()/>
        <@macros.link name="Metlin" base=base_metlin id=state.getMetlin_id()/>
        <@macros.link name="PubChem" base=base_pc id=state.getPc_id()/>
        <@macros.link name="NpAtlas" base=base_npatlas id=state.getNpatlas_id()/>
    </fieldset>

    <#if (state.getNum_chains()?has_content)>
    <fieldset class="attributes"><legend>Lipid attributes</legend>
        <@macros.attvalue name="Number of Chains" value=state.getNum_chains()/>
        <@macros.attvalue name="Number of Carbons" value=state.getNumber_carbons()/>
        <@macros.attvalue name="Double bonds" value=state.getDouble_bonds()/>
    </fieldset>
    </#if>

    <#if (state.getCategory()?has_content)>
    <fieldset class="attributes"><legend>LipidMaps Classification</legend>
        <@macros.attvalue name="Category" value=state.getCategory()/>
        <@macros.attvalue name="Main Class" value=state.getMain_class()/>
        <@macros.attvalue name="Sub Class" value=state.getSub_class()/>
        <@macros.attvalue name="Class Level 4" value=state.getClass_level4()/>
    </fieldset>
    </#if>

    <#if (state.getBiological_activity()?has_content || state.getLogP()?has_content || state.getMesh_nomenclature()?has_content || state.getOh_position()?has_content)>
    <fieldset class="attributes"><legend>Other attributes</legend>
        <@macros.attvalue name="Biological activity" value=state.getBiological_activity()/>
        <@macros.attvalue name="LogP" value=state.getLogP()/>
        <@macros.attvalue name="MESH Nomenclature" value=state.getMesh_nomenclature()/>
        <@macros.attvalue name="OH Position" value=state.getOh_position()/>
    </fieldset>
    </#if>

    <@macros.linklist l=state.getClassyfire_classification() name="Classyfire Classification" base=base_class />
    <@macros.linklist l=state.getOrganisms() name="Organisms" base=base_organism />
    <@macros.linklist l=state.getReactions() name="Reactions" base=base_reaction sep=" "/>
    <@macros.linklist l=state.getOntology_terms() name="Ontology" base=base_ontology />
    <@macros.linklist l=state.getReferences() name="References" base="" sep="<br/>" />


<#else>
    <@macros.error />
</#if>

<@macros.footer component=state/>