<#import "macros.ftl" as macros>
<@macros.header component=state />

<#if (success)>

    <@macros.title id=state.getReference_id() name=state.getReference_text()/>

    <fieldset class="attributes"><legend>Attributes</legend>
        <@macros.attlongvalue name="Doi" value=state.getDoi()/>
        <@macros.link name="Link" base="" id=state.getLink()/>
    </fieldset>
    <@macros.linklist l=state.getCompounds() name="Compounds" base=base_compounds />
    <@macros.linklist l=state.getOrganisms() name="Organisms" base=base_organism />
    
<#else>
    <@macros.error />
</#if>

<@macros.footer component=state />