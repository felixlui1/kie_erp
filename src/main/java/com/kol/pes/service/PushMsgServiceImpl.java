/*-----------------------------------------------------------

-- PURPOSE

--    获取推送消息的Service

-- History

--	  19-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.PushMsgDao;
import com.kol.pes.item.DataPushMsgItem;

@Service("pushMsgService")
public class PushMsgServiceImpl extends DataEnableServiceImpl implements PushMsgService {
	
	@Autowired
	@Qualifier("pushMsgDao")
	private PushMsgDao pushMsgDao;

	public List<DataPushMsgItem> getPushMsg(String staffNo, String transId, String isNotice) {
		try {
			return this.pushMsgDao.getPushMsg(staffNo, transId, isNotice);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
