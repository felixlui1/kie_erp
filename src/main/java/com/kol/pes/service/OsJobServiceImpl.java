/*-----------------------------------------------------------

-- PURPOSE

--    查询工单相关的Service

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.OsJobDao;
import com.kol.pes.item.DataOsJobItem;

@Service("osJobService")
public class OsJobServiceImpl extends DataEnableServiceImpl implements OsJobService {
	
	@Autowired
	@Qualifier("osJobDao")
	private OsJobDao osJobDao;
	
	public List<DataOsJobItem> findOsJob(String wipName) {
		try {
			return this.osJobDao.findOsJob(wipName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
