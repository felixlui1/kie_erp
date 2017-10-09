<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<assetLastCheckTime>${assetLastCheckTime}</assetLastCheckTime>
<checkList>
<#list checkItemList as checkItemData>
<checkItem>
<itemSetId>${checkItemData.itemSetId}</itemSetId>
<itemSeq>${checkItemData.itemSeq}</itemSeq>
<queryText>${checkItemData.queryText}</queryText>
<queryType>${checkItemData.queryType}</queryType>
<chkCycle>${checkItemData.chkCycle}</chkCycle>
<checkResult>${checkItemData.checkResult}</checkResult>
<lastChkTime>${checkItemData.lastChkTime}</lastChkTime>
</checkItem>
</#list>
</checkList>
</pes>