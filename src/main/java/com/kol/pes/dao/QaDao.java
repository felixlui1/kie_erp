package com.kol.pes.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataPlanIdNameItem;
import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.item.DataQaReqItem;

public interface QaDao {
	
	public String getPlanIdByOpCode(String opCode, String organizationId, final boolean isChildPlan) throws SQLException;
	
	public int insertQaItems(String transactionId, Date CREATION_DATE, String CREATED_BY,
			 				 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY,
			 				 List<DataQaReqItem> qaDataList, String planId) throws SQLException;
	
	public List<DataPlanIdNameItem> getChildPlanIdList(String opCode, String organizationId) throws SQLException;
	
	public List<DataQaNeedFillItem> getQaListToFill(String opCode, String organizationId, final boolean isChildPlan) throws SQLException;
	
	public List<DataQaNeedFillItem> getQaListToFillByPlanId(String planIdOrChildPlanId, String opCode) throws SQLException;
	
	public List<DataQaNeedFillItem> getQaListByManual(String opCode) throws SQLException;
	
	public List<DataQaReqItem> runTriggersNeedRunForQaItems(String transId, List<DataQaReqItem> qaDataList, String wipId, String opCode, boolean isManualAddedQa) throws SQLException;
	
	//插入质量管理数据
	public int insertQaItems(String transactionId, String wipEntityId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, String opCode, boolean isChildPlan,
							 List<DataQaReqItem> qaDataList) throws SQLException;
	
	public List<String> getIncompleteQuanStartEndTime(String wipId, String opCode) throws SQLException;
	
	
	
}
