package com.kol.pes.item;

public class DataAssetCheckCheckItem extends DataItem {

	public String itemSetId;
	public String itemSeq;
	public String queryText;
	public String queryType;
	
	public int chkCycle;
	
	public String checkResult;
	public String lastChkTime;
	
	
	public String getItemSetId() {
		return itemSetId;
	}
	
	public String getItemSeq() {
		return itemSeq;
	}
	
	public String getQueryText() {
		return queryText;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public int getChkCycle() {
		return chkCycle;
	}
	
	public String getCheckResult() {
		return checkResult;
	}
	
	public String getLastChkTime() {
		return lastChkTime;
	}
}
