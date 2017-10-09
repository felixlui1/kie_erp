<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">

<isLastSeq>${isLastSeq}</isLastSeq>

<incompleteQuan>${incompleteQuan}</incompleteQuan>
<minStartTime>${minStartTime}</minStartTime>
<maxEndTime>${maxEndTime}</maxEndTime>

<qaList>
<#list qaList as qaData>
<qaItem>
<charId>${qaData.charId}</charId>
<charName>${qaData.charName}</charName>
<prompt>${qaData.prompt}</prompt>
<resultColumnName>${qaData.resultColumnName}</resultColumnName>
<datatypeMeaning>${qaData.datatypeMeaning}</datatypeMeaning>
<derivedFlag>${qaData.derivedFlag}</derivedFlag>
<mandatoryFlag>${qaData.mandatoryFlag}</mandatoryFlag>
<readOnlyFlag>${qaData.readOnlyFlag}</readOnlyFlag>
<qaValueList>
<#list qaData.qaValueList as qaValue>
<qaValue shortCode="${qaValue.shortCode}" description="${qaValue.description}"></qaValue>
</#list>
</qaValueList>
</qaItem>
</#list>
</qaList>

<qaChildPlanIdList>
<#list qaChildPlanIdList as qaChildPlan>
<qaChildPlan planId="${qaChildPlan.planId}" planName="${qaChildPlan.planName}"></qaChildPlan>
</#list>
</qaChildPlanIdList>

<timeBufferOpEnd>${timeBufferOpEnd}</timeBufferOpEnd>
<scrapQuanTotal>${scrapQuanTotal}</scrapQuanTotal>

</pes>