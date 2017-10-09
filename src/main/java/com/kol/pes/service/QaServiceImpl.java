/*-----------------------------------------------------------

-- PURPOSE

--    质量收集计划的Service

-- History

--	  09-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.QaDao;
import com.kol.pes.item.DataPlanIdNameItem;
import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.item.DataQaReqItem;

@Service("qaService")
public class QaServiceImpl extends DataEnableServiceImpl implements QaService {
	
	@Autowired
	@Qualifier("qaDao")
	private QaDao qaDao;
	
	public String getPlanIdByOpCode(String opCode, String organizationId, final boolean isChildPlan) {
		try {
			return this.qaDao.getPlanIdByOpCode(opCode, organizationId, false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<DataPlanIdNameItem> getChildPlanIdList(String opCode, String organizationId) {
		try {
			return this.qaDao.getChildPlanIdList(opCode, organizationId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	//获取这个工序的质量收集计划，
	public List<DataQaNeedFillItem> getQaListToFill(String opCode, String organizationId, final boolean isChildPlan) {
		try {
			return this.qaDao.getQaListToFill(opCode, organizationId, isChildPlan);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<DataQaNeedFillItem> getQaListToFillByPlanId(String planIdOrChildPlanId, String opCode) {
		try {
			return this.qaDao.getQaListToFillByPlanId(planIdOrChildPlanId, opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<DataQaNeedFillItem> getQaListByManual(String opCode) {
		try {
			return this.qaDao.getQaListByManual(opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//运行符合条件的触发器
	public List<DataQaReqItem> runTriggersNeedRunForQaItems(String transId, List<DataQaReqItem> qaDataList, String wipId, String opCode, boolean isManualAddedQa) {
		try {
			return this.qaDao.runTriggersNeedRunForQaItems(transId, qaDataList, wipId, opCode, isManualAddedQa);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//插入质量管理数据
	public int insertQaItems(String transactionId, String wipEntityId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, String opCode, boolean isChildPlan,
							 List<DataQaReqItem> qaDataList) {
		try {
			return this.qaDao.insertQaItems(transactionId, wipEntityId, CREATION_DATE, CREATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY, opCode, isChildPlan,qaDataList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int insertQaItems(String transactionId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY,
							 List<DataQaReqItem> qaDataList, String planId) {
		try {
			return this.qaDao.insertQaItems(transactionId, CREATION_DATE, CREATED_BY,
					 					 	LAST_UPDATE_DATE, LAST_UPDATED_BY,
					 					 	qaDataList, planId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<String> getIncompleteQuanStartEndTime(String wipId, String opCode) {
		try {
			return this.qaDao.getIncompleteQuanStartEndTime(wipId, opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
