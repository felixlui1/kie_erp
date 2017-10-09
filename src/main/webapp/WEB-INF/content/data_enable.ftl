<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<weekList>
<#list weekList as weekData>
<weekItem week="${weekData.week}" >
<week>${weekData.week}</week>
<startEnd>${weekData.startEnd}</startEnd>
<hourMin>${weekData.hourMin}</hourMin>
</weekItem>
</#list>
</weekList>
</pes>