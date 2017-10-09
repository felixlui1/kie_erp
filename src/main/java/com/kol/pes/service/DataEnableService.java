package com.kol.pes.service;

import java.util.List;

import com.kol.pes.item.DataWeekItem;



public interface DataEnableService {
	
	public List<DataWeekItem> getWeekList();
	
	//判断数据库状态，看是否可访问
	public int dataStatus();
	
	public int getLatestApkVersionNumber();
	
	public String getApkIsForceUpdate();
	
	public String getApkUpdateMsg();
}
