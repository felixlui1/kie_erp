/*-----------------------------------------------------------

-- PURPOSE

--    已开启的工序的数据封装类

-- History

--	  31-Oct-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataSeqStartedItem extends DataItem {

	public String transactionId;
	public String wipEntityId;
	public String wipEntityName;
	public String creationDate;
	public String createdBy;
	public String lastUpdateDate;
	public String lastUpdateBy;
	public String fmOperationCode;
	public String opDesc;
	public String trxQuantity;
	public String scrapQuantity;
	public String assetDesc;
	public String assettagNumber;
	public String opStartDate;
	public String opEndDate;
	public String interfaced;
	
	//job values
	public String saItem;
	public String saItemDesc;
	
	public String dffCpnNumber;
	
	public String dffCustomerspec;//其它要求
	public String dffMfgSpec;//工序要求
	
	public String custNumber;
	
	public int incompleteQuantity;
	public int startQuantity;
	public int quantityCompleted;
	public int quantityScrapped;
	
	public int primaryItemId;
	public String commonRoutingSequenceId;
	
	public String curOperationId;
	public String organizationId;
	
	public String pctComplete;
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public String getWipEntityId() {
		return wipEntityId;
	}
	
	public String getWipEntityName() {
		return wipEntityName;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public String getLastUpdateBy() {
		return lastUpdateBy;
	}
	
	public String getFmOperationCode() {
		return fmOperationCode;
	}
	
	public String getOpDesc() {
		return opDesc;
	}
	
	public String getTrxQuantity() {
		return trxQuantity;
	}
	
	public String getScrapQuantity() {
		return scrapQuantity;
	}
	
	public String getAssetDesc() {
		return assetDesc;
	}
	
	public String getAssettagNumber() {
		return assettagNumber;
	}
	
	public String getInterfaced() {
		return interfaced;
	}
	
	public String getOpStartDate() {
		return opStartDate;
	}
	
	public String getOpEndDate() {
		return opEndDate;
	}
	
	//job
	public String getSaItem() {
		return saItem;
	}
	
	public String getSaItemDesc() {
		return saItemDesc;
	}
	
	public String getDffCpnNumber() {
		return dffCpnNumber;
	}
	
	public String getDffCustomerspec() {
		return dffCustomerspec;
	}
	
	public String getDffMfgSpec() {
		return dffMfgSpec;
	}
	
	public String getCustNumber() {
		return custNumber;
	}
	
	public int getIncompleteQuantity() {
		return incompleteQuantity;
	}
	
	public int getStartQuantity() {
		return startQuantity;
	}

	public int getQuantityCompleted() {
		return quantityCompleted;
	}
	
	public int getQuantityScrapped() {
		return quantityScrapped;
	}
	
	public int getPrimaryItemId() {
		return primaryItemId;
	}
	
	public String getCommonRoutingSequenceId() {
		return commonRoutingSequenceId;
	}
	
	public String getCurOperationId() {
		return curOperationId;
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public String getPctComplete() {
		return pctComplete;
	}
}
