<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<setAssetCheckList>
<#list assetList as assetData>
<assetItem>
<assetId>${assetData.assetId?c}</assetId>
<assetTagNum>${assetData.assetTagNum}</assetTagNum>
<assetDescription>${assetData.assetDescription}</assetDescription>
<scheduledId>${assetData.scheduledId}</scheduledId>
<checkedId>${assetData.checkedId}</checkedId>
</assetItem>
</#list>
</setAssetCheckList>
</pes>