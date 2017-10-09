package com.kol.pes.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataPlanIdNameItem;
import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.item.DataQaReqItem;

public interface QaService extends DataEnableService {
	
	public String getPlanIdByOpCode(String opCode, String organizationId, final boolean isChildPlan);
	
	public int insertQaItems(String transactionId, Date CREATION_DATE, String CREATED_BY,
			 				 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY,
			 				 List<DataQaReqItem> qaDataList, String planId);
	
	public List<DataPlanIdNameItem> getChildPlanIdList(String opCode, String organizationId);
	
	//获取这个工序的质量收集计划，
	public List<DataQaNeedFillItem> getQaListToFill(String opCode, String organizationId, final boolean isChildPlan);
	
	public List<DataQaNeedFillItem> getQaListToFillByPlanId(String planIdOrChildPlanId, String opCode);
	
	public List<DataQaNeedFillItem> getQaListByManual(String opCode);
	
	public List<DataQaReqItem> runTriggersNeedRunForQaItems(String transId, List<DataQaReqItem> qaDataList, String wipId, String opCode, boolean isManualAddedQa);
	
	//插入质量管理数据
	public int insertQaItems(String transactionId, String wipEntityId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, String opCode, boolean isChildPlan,
							 List<DataQaReqItem> qaDataList);
	
	public List<String> getIncompleteQuanStartEndTime(String wipId, String opCode);
}
