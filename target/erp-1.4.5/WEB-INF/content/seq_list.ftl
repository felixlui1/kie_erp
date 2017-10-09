<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<seqList>
<#list seqList as seqData>
<seqItem>
<routingSeqId>${seqData.routingSeqId?c}</routingSeqId>
<operationSequenceId>${seqData.operationSequenceId}</operationSequenceId>
<standardSequenceId>${seqData.standardSequenceId}</standardSequenceId>
<operationSeqNum>${seqData.operationSeqNum?c}</operationSeqNum>
<standardOperationCode>${seqData.standardOperationCode}</standardOperationCode>
<departmentCode>${seqData.departmentCode}</departmentCode>
<departmentId>${seqData.departmentId}</departmentId>
<operationDescription>${seqData.operationDescription}</operationDescription>
</seqItem>
</#list>
</seqList>
</pes>