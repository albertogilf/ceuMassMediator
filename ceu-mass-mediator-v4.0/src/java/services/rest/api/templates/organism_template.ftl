<#import "macros.ftl" as macros>
<@macros.header component=state />

<#if (success)>
    
    <@macros.title id=state.getOrganism_id() name=state.getOrganism_name()/>

    <fieldset class="attributes"><legend>Basic attributes</legend>
        <@macros.attvalue name="Organism Level" value=state.getOrganism_level()/>
        <@macros.link name="Parent id" base=base_organism id=state.getParent_id()/>
    </fieldset>

    <@macros.linknumlist l=state.getOrganism_hierarchy() name="Organism Hierarchy" base=base_organism sep=" --> "/>
    <@macros.linklist l=state.getCompounds() name="Compounds" base=base_compounds />
    <@macros.linklist l=state.getReferences() name="References" base="" sep="<br/>" />
    
<#else>
    <@macros.error />
</#if>

<@macros.footer component=state />