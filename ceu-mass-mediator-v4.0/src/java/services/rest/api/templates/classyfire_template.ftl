<#import "macros.ftl" as macros>
<@macros.header component=state />

<#if (success)>
    <@macros.title id=state.getNode_id() name=state.getNode_name()/>

    <fieldset class="attributes"><legend>Attributes</legend>
        <@macros.link name="Kingdom" base=base_statem id=state.getKingdom()/>
        <@macros.link name="Super Class" base=base_statem id=state.getSuper_class()/>
        <@macros.link name="Main Class" base=base_statem id=state.getMain_class()/>
        <@macros.link name="Sub Class" base=base_statem id=state.getSub_class()/>
        <@macros.link name="Direct Parent" base=base_statem id=state.getDirect_parent()/>
        <@macros.link name="Level 7" base=base_statem id=state.getLevel_7()/>
        <@macros.link name="Level 8" base=base_statem id=state.getLevel_8()/>
        <@macros.link name="Level 9" base=base_statem id=state.getLevel_9()/>
        <@macros.link name="Level 10" base=base_statem id=state.getLevel_10()/>
        <@macros.link name="Level 11" base=base_statem id=state.getLevel_11()/>
    </fieldset>
    
<#else>
    <@macros.error />
</#if>

<@macros.footer component=state />