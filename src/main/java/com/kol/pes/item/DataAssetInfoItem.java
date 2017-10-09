/*-----------------------------------------------------------

-- PURPOSE

--    设备基本信息的数据封装类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;


public class DataAssetInfoItem extends DataItem {

	public int assetId;
	public String assetNumber;
	public String tagNumber;
	
	public String description;
	public String opCode;
	
	public String opDscr;
	public String location;
	public String attribute7;
	
	
	
	public int getAssetId() {
		return assetId;
	}
	
	public String getAssetNumber() {
		return assetNumber;
	}
	
	public String getTagNumber() {
		return tagNumber;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getOpCode() {
		return opCode;
	}
	
	public String getOpDscr() {
		return opDscr;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getAttribute7() {
		return attribute7;
	}

}
