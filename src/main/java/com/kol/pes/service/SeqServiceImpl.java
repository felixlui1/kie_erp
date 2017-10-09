/*-----------------------------------------------------------

-- PURPOSE

--    工序的Service

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.SeqDao;
import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataCodeMessageItem;
import com.kol.pes.item.DataSeqItem;
import com.kol.pes.item.DataSeqProcedureItem;
import com.kol.pes.item.DataSeqStartedItem;

@Service("seqService")
public class SeqServiceImpl extends DataEnableServiceImpl implements SeqService {
	
	@Autowired
	@Qualifier("seqDao")
	private SeqDao seqDao;
	
	public List<DataSeqItem> findSeq(int seqId) {
		try {
			return this.seqDao.findSeq(seqId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTransactionId() {
		try {
			return this.seqDao.getTransactionId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getLeftCountForStartOp(String wipEntityName, int wipEntityId, int routing_sequence_id, String opCode, String curOperationId, boolean canJump) {
		try {
			return this.seqDao.getLeftCountForStartOp(wipEntityName, wipEntityId, routing_sequence_id, opCode, curOperationId, canJump);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<DataAssetInfoItem> getOpAssetList(String opCode) {
		try {
			return this.seqDao.getOpAssetList(opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//在开始一个工序前，判断是否有这个工单的其它工序还没有加工完，如果有，则不能启动一个新的不同工序
	public String isOtherOpNotEndBeforeStartTheNewOne(int WIP_ENTITY_ID, String THIS_OPERATION_CODE) {
		try {
			return this.seqDao.isOtherOpNotEndBeforeStartTheNewOne(WIP_ENTITY_ID, THIS_OPERATION_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isCurrentOpCompletedBeforeStartTheNewOne(String wipName, int WIP_ENTITY_ID, int routing_sequence_id, String curOperationId, String opCodeWantStart) {
		try {
			return this.seqDao.isCurrentOpCompletedBeforeStartTheNewOne(wipName, WIP_ENTITY_ID, routing_sequence_id, curOperationId, opCodeWantStart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	//工序完成时运行一个存储过程，告诉系统更新下工单的状态
	public int runProcedureBeforeStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) {
		try {
			return this.seqDao.runProcedureBeforeStartOp(WIP_ENTITY_ID, FM_OPERATION_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}
	
	public DataCodeMessageItem runProcedureBeforeEndOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) {
		try {
			return this.seqDao.runProcedureBeforeEndOp(WIP_ENTITY_ID, FM_OPERATION_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//工序完成时运行一个存储过程，告诉系统更新下工单的状态
	public int runProcedureAfterStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) {
		try {
			return this.seqDao.runProcedureAfterStartOp(WIP_ENTITY_ID, FM_OPERATION_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}

	public int insertWhenStartAnOp(String transactionId, Date CREATION_DATE,
									String CREATED_BY, Date LAST_UPDATE_DATE, String LAST_UPDATED_BY,
									int WIP_ENTITY_ID, String FM_OPERATION_CODE,
									String TRX_QUANTITY, String SCRAP_QUANTITY, String assetId1, String assetId2, String assetId3,
									Date OP_START, int seqId, boolean canJump) {
		try {
			return this.seqDao.insertWhenStartAnOp(transactionId, CREATION_DATE,
													 CREATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY,
													 WIP_ENTITY_ID, FM_OPERATION_CODE,
													 TRX_QUANTITY, SCRAP_QUANTITY, assetId1, assetId2, assetId3, OP_START,seqId, canJump);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int updateWhenEndAnOp(String transactionId, int SCRAP_QUANTITY, Date OP_END, String LAST_UPDATED_BY, Date LAST_UPDATE_DAT) {
		try {
			return this.seqDao.updateWhenEndAnOp(transactionId, SCRAP_QUANTITY, OP_END, LAST_UPDATED_BY, LAST_UPDATE_DAT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public DataSeqProcedureItem runSQLNoticeJobStatus(String transactionId) {
		try {
			return this.seqDao.runSQLNoticeJobStatus(transactionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DataSeqProcedureItem();
	}
	
	public int updateResetOpEndedToNull(String transactionId) {
		try {
			return this.seqDao.updateResetOpEndedToNull(transactionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getOpTrxId(String transactionId) {
		try {
			return this.seqDao.getOpTrxId(transactionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//判断完成工序时是否加了质量管理计划
	public boolean isQaFilledWhenEndingAnOp(String transId) {
		try {
			return this.seqDao.isQaFilledWhenEndingAnOp(transId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//获取某个工单的某一工序的未完成开启数目，判断是否需要填写质量收集计划用
	public boolean isLastUncompleteOpNumForWip(String wipId, String opCode, boolean canJump) {
		try {
			return this.seqDao.isLastUncompleteOpNumForWip(wipId, opCode, canJump);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public List<DataSeqStartedItem> getSeqStartedList(String staffNoOrAssetId) {
		try {
			return this.seqDao.getSeqStartedList(staffNoOrAssetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//获取某个工单的工序加工情况，呼应客户端首界面的“查询工单”功能
	public List<DataSeqStartedItem> getSeqAllListByWipId(String wipId) {
		try {
			return this.seqDao.getSeqAllListByWipId(wipId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//删除一个工序的函数 
	public int deleteOpByTransId(String transactionId) {
		try {
			return this.seqDao.deleteOpByTransId(transactionId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getCurReallyWorkingOpCode(int wipEntityId) {
		try {
			return this.seqDao.getCurReallyWorkingOpCode(wipEntityId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isCurrentOpCompletedAfterItemSelected(int WIP_ENTITY_ID) {
		try {
			return this.seqDao.isCurrentOpCompletedAfterItemSelected(WIP_ENTITY_ID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getTimeBufferForOpStart() {
		try {
			return this.seqDao.getTimeBufferForOpStart();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "90";
	}
	
	public String getTimeBufferForOpEnd() {
		try {
			return this.seqDao.getTimeBufferForOpEnd();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "90";
	}
	
	//当完成一个工序的最后一个拆分加工时，需要知道这个工序之前所有拆分的坏品数之和。这个函数就是获取这个值的
	public String getTotalScrapQuantityOfOtherPart(String wipId, String opCode) {
		try {
			return this.seqDao.getTotalScrapQuantityOfOtherPart(wipId, opCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "0";
	}
	
}
