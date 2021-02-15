<#import "macros.ftl" as macros>
<@macros.header component=state />

<#if (success)>

    <@macros.title id=state.getOntology_term_id() name=state.getTerm()/>

    <fieldset class="attributes"><legend>Attributes</legend>
        <@macros.attlongvalue name="Definition" value=state.getDefinition()/>   
        <@macros.attlongvalue name="Ontology Comment" value=state.getOntology_comment()/> 
        <@macros.attvalue name="External id" value=state.getExternal_id()/> 
        <@macros.attvalue name="External source" value=state.getExternal_source()/> 
        <@macros.attvalue name="Curator" value=state.getCurator()/> 
        <@macros.link name="Parent id" base=base_ontology id=state.getParent_id()/>
        <@macros.attvalue name="Ontology level" value=state.getOntology_level()/> 
    </fieldset>

<#else>
    <@macros.error />
</#if>

<@macros.footer component=state />