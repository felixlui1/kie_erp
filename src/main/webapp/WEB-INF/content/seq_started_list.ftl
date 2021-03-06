<?xml version="1.0" encoding="UTF-8" ?>
<pes code="${code}" message="${message}">
<seqStartedList isOpCompleted="${isOpCompleted}" curWorkingOpCode="${curWorkingOpCode}">
<#list seqStartedList as seqData>
<seqItem>
<transactionId>${seqData.transactionId}</transactionId>
<wipEntityId>${seqData.wipEntityId}</wipEntityId>
<wipEntityName>${seqData.wipEntityName}</wipEntityName>
<creationDate>${seqData.creationDate}</creationDate>
<createdBy>${seqData.createdBy}</createdBy>
<lastUpdateDate>${seqData.lastUpdateDate}</lastUpdateDate>
<lastUpdateBy>${seqData.lastUpdateBy}</lastUpdateBy>
<fmOperationCode>${seqData.fmOperationCode}</fmOperationCode>
<opDesc>${seqData.opDesc}</opDesc>
<trxQuantity>${seqData.trxQuantity}</trxQuantity>
<assetDesc>${seqData.assetDesc}</assetDesc>
<assettagNumber>${seqData.assettagNumber}</assettagNumber>
<opStartDate>${seqData.opStartDate}</opStartDate>
<opEndDate>${seqData.opEndDate}</opEndDate>
<saItem>${seqData.saItem}</saItem>
<saItemDesc>${seqData.saItemDesc}</saItemDesc>
<dffCpnNumber>${seqData.dffCpnNumber}</dffCpnNumber>
<dffCustomerspec>${seqData.dffCustomerspec}</dffCustomerspec>
<dffMfgSpec>${seqData.dffMfgSpec}</dffMfgSpec>
<custNumber>${seqData.custNumber}</custNumber>
<incompleteQuantity>${seqData.incompleteQuantity?c}</incompleteQuantity>
<startQuantity>${seqData.startQuantity?c}</startQuantity>
<quantityCompleted>${seqData.quantityCompleted?c}</quantityCompleted>
<quantityScrapped>${seqData.quantityScrapped?c}</quantityScrapped>
<primaryItemId>${seqData.primaryItemId?c}</primaryItemId>
<commonRoutingSequenceId>${seqData.commonRoutingSequenceId}</commonRoutingSequenceId>
<curOperationId>${seqData.curOperationId}</curOperationId>
<organizationId>${seqData.organizationId}</organizationId>
<pctComplete>${seqData.pctComplete}</pctComplete>
</seqItem>
</#list>
</seqStartedList>
</pes>