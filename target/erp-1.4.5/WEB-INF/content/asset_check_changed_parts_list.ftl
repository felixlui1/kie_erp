<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<partsList>
<#list changedPartsList as changedPartData>
<partItem>${changedPartData}</partItem>
</#list>
</partsList>
</pes>