/*-----------------------------------------------------------

-- PURPOSE

--    处理工序的数据库接口

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataCodeMessageItem;
import com.kol.pes.item.DataSeqItem;
import com.kol.pes.item.DataSeqProcedureItem;
import com.kol.pes.item.DataSeqStartedItem;

public interface SeqDao {
	
	public List<DataSeqItem> findSeq(int seqId) throws SQLException;
	
	public String getTransactionId() throws SQLException;
	
	public int getLeftCountForStartOp(String wipEntityName, int wipEntityId, int routing_sequence_id, String opCode, String curOperationId, boolean canJump) throws SQLException;
	
	public List<DataAssetInfoItem> getOpAssetList(String opCode) throws SQLException;
	
	//在开始一个工序前，判断是否有这个工单的其它工序还没有加工完，如果有，则不能启动一个新的不同工序
	public String isOtherOpNotEndBeforeStartTheNewOne(int WIP_ENTITY_ID, String THIS_OPERATION_CODE) throws SQLException;
	
	public boolean isCurrentOpCompletedBeforeStartTheNewOne(String wipName, int WIP_ENTITY_ID, int routing_sequence_id, String curOperationId, String opCodeWantStart) throws SQLException;
	
	//工序完成时运行一个存储过程，告诉系统更新下工单的状态
	public int runProcedureBeforeStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException;
	
	public DataCodeMessageItem runProcedureBeforeEndOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException;
	
	public int runProcedureAfterStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException;
	
	public int insertWhenStartAnOp(String transactionId, Date CREATION_DATE, String CREATED_BY,
									Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, int WIP_ENTITY_ID,
									String FM_OPERATION_CODE, String TRX_QUANTITY, String SCRAP_QUANTITY,
									String assetId1, String assetId2, String assetId3, Date OP_START, int seqId, boolean canJump) throws SQLException;
	
	public int updateWhenEndAnOp(String transactionId, int SCRAP_QUANTITY, Date OP_END, String LAST_UPDATED_BY, Date LAST_UPDATE_DAT) throws SQLException;
	
	public DataSeqProcedureItem runSQLNoticeJobStatus(String transactionId) throws SQLException;
	
	public int updateResetOpEndedToNull(String transactionId) throws SQLException;
	
	public int getOpTrxId(String transactionId) throws SQLException;
	
	//判断完成工序时是否加了质量管理计划
	public boolean isQaFilledWhenEndingAnOp(String transId) throws SQLException;
	
	//获取某个工单的某一工序的未完成开启数目，判断是否需要填写质量收集计划用
	public boolean isLastUncompleteOpNumForWip(String wipId, String opCode, boolean canJump) throws SQLException;
	
	public List<DataSeqStartedItem> getSeqStartedList(String staffNoOrAssetId) throws SQLException;
	
	//获取某个工单的工序加工情况，呼应客户端首界面的“查询工单”功能
	public List<DataSeqStartedItem> getSeqAllListByWipId(String wipId) throws SQLException;
	
	//删除一个工序的函数 
	public int deleteOpByTransId(String transactionId) throws SQLException;
	
	public String getCurReallyWorkingOpCode(int wipEntityId) throws SQLException;
	
	public boolean isCurrentOpCompletedAfterItemSelected(int WIP_ENTITY_ID) throws SQLException;
	
	public String getTimeBufferForOpStart() throws SQLException;
	
	public String getTimeBufferForOpEnd() throws SQLException;
	
	//当完成一个工序的最后一个拆分加工时，需要知道这个工序之前所有拆分的坏品数之和。这个函数就是获取这个值的
	public String getTotalScrapQuantityOfOtherPart(String wipId, String opCode) throws SQLException;
	
}
