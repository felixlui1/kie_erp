/*-----------------------------------------------------------

-- PURPOSE

--    登录的Service

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.DataEnableDao;
import com.kol.pes.item.DataWeekItem;

@Service("dataEnableService")
public class DataEnableServiceImpl implements DataEnableService {
	
	@Autowired
	@Qualifier("dataEnableDao")
	private DataEnableDao dataEnableDao;
	
	public List<DataWeekItem> getWeekList() {
		try {
			return this.dataEnableDao.getWeekList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int dataStatus() {
		try {
			return this.dataEnableDao.dataStatus();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	public int getLatestApkVersionNumber() {
		try {
			return this.dataEnableDao.getLatestApkVersionNumber();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	public String getApkIsForceUpdate() {
		try {
			return this.dataEnableDao.getApkIsForceUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "N";
	}
	
	public String getApkUpdateMsg() {
		try {
			return this.dataEnableDao.getApkUpdateMsg();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
