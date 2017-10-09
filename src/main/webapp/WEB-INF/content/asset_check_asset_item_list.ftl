<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<assetCheckInfoList>
<#list assetItemList as assetItemData>
<assetItem>
<checkDate>${assetItemData.checkDate}</checkDate>
<assetTotalNum>${assetItemData.assetTotalNum?c}</assetTotalNum>
<assetNeedCheckNumLight>${assetItemData.assetNeedCheckNumLight?c}</assetNeedCheckNumLight>
<assetNeedCheckNumNight>${assetItemData.assetNeedCheckNumNight?c}</assetNeedCheckNumNight>
<assetCheckedNumLight>${assetItemData.assetCheckedNumLight?c}</assetCheckedNumLight>
<assetCheckedNumNight>${assetItemData.assetCheckedNumNight?c}</assetCheckedNumNight>
</assetItem>
</#list>
</assetCheckInfoList>
</pes>