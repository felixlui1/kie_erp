<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<osJobList>
<#list osJobList as osJob>
<osJob>
<wipEntityId>${osJob.wipEntityId?c}</wipEntityId>
<wipEntityName>${osJob.wipEntityName}</wipEntityName>
<saItem>${osJob.saItem}</saItem>
<saItemDesc>${osJob.saItemDesc}</saItemDesc>
<dffCpnNumber>${osJob.dffCpnNumber}</dffCpnNumber>
<dffCustomerspec>${osJob.dffCustomerspec}</dffCustomerspec>
<dffMfgSpec>${osJob.dffMfgSpec}</dffMfgSpec>
<custNumber>${osJob.custNumber}</custNumber>
<incompleteQuantity>${osJob.incompleteQuantity?c}</incompleteQuantity>
<startQuantity>${osJob.startQuantity?c}</startQuantity>
<quantityCompleted>${osJob.quantityCompleted?c}</quantityCompleted>
<quantityScrapped>${osJob.quantityScrapped?c}</quantityScrapped>
<primaryItemId>${osJob.primaryItemId?c}</primaryItemId>
<commonRoutingSequenceId>${osJob.commonRoutingSequenceId}</commonRoutingSequenceId>
<curOperationId>${osJob.curOperationId}</curOperationId>
<organizationId>${osJob.organizationId}</organizationId>
</osJob>
</#list>
</osJobList>
</pes>