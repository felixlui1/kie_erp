/*-----------------------------------------------------------

-- PURPOSE

--    开始、完成结果中的工序的数据封装类

-- History

--	  22-Oct-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataTxnSeqItem extends DataItem {

	public String transactionId;
	public String creationDate;
	public String createdBy;
	public String lastUpdateDate;
	public String lastUpdatedBy;
	public String wipEntityId;
	public String fmOperationCode;
	public int trxQuantity;
	public String scrapQuantity;
	public String assetId;
	public String opStart;
	public String opEnd;
	public int interfaced;
	
	
	public String getTransactionId() {
		return transactionId;
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
	
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	
	public String getWipEntityId() {
		return wipEntityId;
	}
	
	public String getFmOperationCode() {
		return fmOperationCode;
	}
	
	public int getTrxQuantity() {
		return trxQuantity;
	}
	
	public String getScrapQuantity() {
		return scrapQuantity;
	}
	
	public String getAssetId() {
		return assetId;
	}
	
	public String getOpStart() {
		return opStart;
	}
	
	public String getOpEnd() {
		return opEnd;
	}
	
	public int getInterfaced() {
		return interfaced;
	}
	
}
