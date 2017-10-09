<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<assetList>
<#list assetList as assetData>
<assetItem>
<checkId>${assetData.checkId?c}</checkId>
<creationDate>${assetData.creationDate}</creationDate>
<createdBy>${assetData.createdBy?c}</createdBy>
<createdByName>${assetData.createdByName}</createdByName>
<lastUpdateDate>${assetData.lastUpdateDate}</lastUpdateDate>
<lastUpdatedBy>${assetData.lastUpdatedBy?c}</lastUpdatedBy>
<assetId>${assetData.assetId}</assetId>
<assetTagNumber>${assetData.assetTagNumber}</assetTagNumber>
<assetName>${assetData.assetName}</assetName>
<assetOpDscr>${assetData.assetOpDscr}</assetOpDscr>
<checkTime>${assetData.checkTime}</checkTime>
<checkResult>${assetData.checkResult}</checkResult>
<estRepairStart>${assetData.estRepairStart}</estRepairStart>
<estRepairEnd>${assetData.estRepairEnd}</estRepairEnd>
<checkRemarks>${assetData.checkRemarks}</checkRemarks>
<failureCode>${assetData.failureCode?c}</failureCode>
<opCode>${assetData.opCode}</opCode>
</assetItem>
</#list>
</assetList>
</pes>