package com.kol.pes.item;

public class DataAssetCheckAssetListItem extends DataItem {

	public String checkDate;
	
	public int assetTotalNum;
	
	public int assetNeedCheckNumLight;
	public int assetNeedCheckNumNight;
	
	public int assetCheckedNumLight;
	public int assetCheckedNumNight;
	
	public String getCheckDate() {
		return checkDate;
	}
	
	public int getAssetTotalNum() {
		return assetTotalNum;
	}
	
	public int getAssetNeedCheckNumLight() {
		return assetNeedCheckNumLight;
	}
	
	public int getAssetNeedCheckNumNight() {
		return assetNeedCheckNumNight;
	}
	
	public int getAssetCheckedNumLight() {
		return assetCheckedNumLight;
	}
	
	public int getAssetCheckedNumNight() {
		return assetCheckedNumNight;
	}
}
