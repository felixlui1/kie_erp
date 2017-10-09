/*-----------------------------------------------------------

-- PURPOSE

--    点检设备列表的数据封装类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;


public class DataAssetCheckItem extends DataItem {

	public int checkId;
	public String creationDate;
	public int createdBy;
	public String createdByName;
	
	public String lastUpdateDate;
	public int lastUpdatedBy;
	
	public String assetId;
	public String assetTagNumber;
	public String assetName="";
	public String assetOpDscr="";
	public String checkTime;
	public String checkResult;
	
	public String estRepairStart;
	public String estRepairEnd;
	public String checkRemarks;
	
	public String status;
	
	public int failureCode;
	
	public String opCode;
	
	public int getCheckId() {
		return checkId;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public int getCreatedBy() {
		return createdBy;
	}
	
	public String getCreatedByName() {
		return createdByName;
	}
	
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public int getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	
	public String getAssetId() {
		return assetId;
	}
	
	public String getAssetTagNumber() {
		return assetTagNumber;
	}
	
	public String getAssetName() {
		return assetName;
	}
	
	public String getAssetOpDscr() {
		return assetOpDscr;
	}
	
	public String getCheckTime() {
		return checkTime;
	}
	
	public String getCheckResult() {
		return checkResult;
	}
	
	public String getEstRepairStart() {
		return estRepairStart;
	}
	
	public String getEstRepairEnd() {
		return estRepairEnd;
	}
	
	public String getCheckRemarks() {
		if(checkRemarks==null) {
			checkRemarks = "";
		}
		return checkRemarks;
	}

	public int getFailureCode() {
		return failureCode;
	}
	
	public String getOpCode() {
		return opCode;
	}

}
