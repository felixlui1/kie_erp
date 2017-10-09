<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<pushMsgList>
<#list pushMsgList as msgData>
<msgItem>
<transId>${msgData.transId}</transId>
<title>${msgData.title}</title>
<msg>${msgData.msg}</msg>
</msgItem>
</#list>
</pushMsgList>
</pes>