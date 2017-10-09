<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<maxQuan>${maxQuan}</maxQuan>
<opAssetList>
<#list opAssetList as opAsset>
<opAssetItem assetId="${opAsset.assetId?c}" tagNumber="${opAsset.tagNumber}" assetDesc="${opAsset.description}"></opAssetItem>
</#list>
</opAssetList>
</pes>