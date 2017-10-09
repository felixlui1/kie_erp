package com.kol.pes.dao;

import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataPushMsgItem;

public interface PushMsgDao {
	public List<DataPushMsgItem> getPushMsg(String staffNo, String transId, String isNotice) throws SQLException;
}
