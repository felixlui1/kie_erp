package com.kol.pes.item;

public class DataAssetCheckSetAssetCheckItem extends DataItem {

	public int assetId;
	
	public String assetTagNum;
	public String assetDescription;
	
	public String scheduledId;
	public String checkedId;
	
	public int getAssetId() {
		return assetId;
	}
	
	public String getAssetTagNum() {
		return assetTagNum;
	}
	
	public String getAssetDescription() {
		return assetDescription;
	}
	
	public String getScheduledId() {
		return scheduledId;
	}
	
	public String getCheckedId() {
		return checkedId;
	}

}
