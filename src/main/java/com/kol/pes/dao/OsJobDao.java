/*-----------------------------------------------------------

-- PURPOSE

--    处理工单的数据库操作接口

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataOsJobItem;

public interface OsJobDao {
	public List<DataOsJobItem> findOsJob(String wipName) throws SQLException;
	public List<DataOsJobItem> findOsJob(int wipId) throws SQLException;
}
