package com.kol.pes.service;

import java.util.List;

import com.kol.pes.item.DataOsJobItem;

public interface OsJobService extends DataEnableService {
	public List<DataOsJobItem> findOsJob(String wipName);//获取工单列表
}
