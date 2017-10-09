/*-----------------------------------------------------------

-- PURPOSE

--    工单的数据封装类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataOsJobItem extends DataItem {

	public int wipEntityId;
	public String wipEntityName;
	
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
	
	public int getWipEntityId() {
		return wipEntityId;
	}
	
	public String getWipEntityName() {
		return wipEntityName;
	}
	
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
}
