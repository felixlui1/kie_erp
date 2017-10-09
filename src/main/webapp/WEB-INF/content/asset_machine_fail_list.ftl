<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<machineFailList>
<#list assetMachineFailList as machineFail>
<failItem>
<lookupCode>${machineFail.lookupCode}</lookupCode>
<tag>${machineFail.tag}</tag>
<meaning>${machineFail.meaning}</meaning>
</failItem>
</#list>
</machineFailList>
</pes>