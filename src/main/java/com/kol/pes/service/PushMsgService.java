package com.kol.pes.service;

import java.util.List;

import com.kol.pes.item.DataPushMsgItem;

public interface PushMsgService extends DataEnableService {
	public List<DataPushMsgItem> getPushMsg(String staffNo, String transId, String isNotice);
}
