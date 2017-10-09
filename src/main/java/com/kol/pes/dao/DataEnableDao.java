package com.kol.pes.dao;

import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataWeekItem;

public interface DataEnableDao {
	
	public List<DataWeekItem> getWeekList() throws SQLException;
	
	public int dataStatus() throws SQLException;
	
	public int getLatestApkVersionNumber() throws SQLException;
	
	public String getApkIsForceUpdate() throws SQLException;
	
	public String getApkUpdateMsg() throws SQLException;
}
