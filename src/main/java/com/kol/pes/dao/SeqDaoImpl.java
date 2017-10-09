/*-----------------------------------------------------------

-- PURPOSE

--    处理工序的数据库操作类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataCodeMessageItem;
import com.kol.pes.item.DataOpFailNotifyItem;
import com.kol.pes.item.DataSeqItem;
import com.kol.pes.item.DataSeqProcedureItem;
import com.kol.pes.item.DataSeqStartedItem;
import com.kol.pes.item.DataTxnSeqItem;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


@Repository("seqDao")
public class SeqDaoImpl implements SeqDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	@Autowired
	@Qualifier("osJobDao")
	private OsJobDao osJobDao;
	 
	//根据commonSequenceId获取工序列表
	public List<DataSeqItem> findSeq(int seqId) throws SQLException {
		
		String sql = "select * from kol_pes_op_seq where routing_sequence_id = "+seqId+"order by operation_seq_num asc";
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataSeqItem>>() {

			public List<DataSeqItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataSeqItem> tempList = new ArrayList<DataSeqItem>();
				
				while(rs.next()) { 
					DataSeqItem data = new DataSeqItem();
					data.routingSeqId = rs.getInt("ROUTING_SEQUENCE_ID");
					data.operationSequenceId = rs.getString("OPERATION_SEQUENCE_ID");
					data.standardSequenceId =  rs.getString("STANDARD_OPERATION_ID");
					data.operationSeqNum = rs.getInt("OPERATION_SEQ_NUM");
					data.standardOperationCode = rs.getString("STANDARD_OPERATION_CODE");
					data.departmentCode = rs.getString("DEPARTMENT_CODE");
					data.departmentId = rs.getString("DEPARTMENT_ID");
					data.operationDescription = rs.getString("OPERATION_DESCRIPTION");
					
					if(data.operationDescription!=null && data.operationDescription.contains("&")) {
						data.operationDescription = data.operationDescription.replace("&", "&amp;");
					}
					
					tempList.add(data);
				}
				
				LogUtil.log("SeqDaoImpl:tempList.size()="+tempList.size());
				
				return tempList;
			}
		});
	}
	
	//生成ID
	public String getTransactionId() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		Object[] resultArray = runner.query("select PES_MOVE_TXN_S.nextval from dual", new ArrayHandler());
		
		//testTransactionId();
		
		if (resultArray != null && resultArray.length > 0) {
            return resultArray[0].toString();
		}
		
		return null;
	}
	
	//测试下trans_id突然变大1000的问题
	private void testTransactionId() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		runner.query("update KOL_PES_SYSTEM_PARAM set profile_value = to_char(to_number(profile_value) + 1), access_level = '(WIP_ENTITY_NAME)' where profile_name = 'DEBUG'", new ArrayHandler());
		LogUtil.log("testTransactionId()");
	}
	
	private String getOpCodeByOpId(int routing_sequence_id, String curOperationId) throws NumberFormatException, SQLException {

		String sql = "select STANDARD_OPERATION_CODE from kol_pes_op_seq where routing_sequence_id="+routing_sequence_id+" and STANDARD_OPERATION_ID="+curOperationId+" order by operation_seq_num asc";
		
		LogUtil.log("getOpCodeByOpId():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) {
					return rs.getString("STANDARD_OPERATION_CODE");
				}

				return null;
			}
		});
	}
	
	//根据工单id获取工单的未完成数量
	private int getWipIncompleteQuantityByWipId(int wipEntityId) throws SQLException {
		String sql = "select incomplete_quantity from kol_pes_os_job where wip_entity_id="+wipEntityId;
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return -1;
		        }
			
				while(rs.next()) { 
					return rs.getInt("incomplete_quantity");
				}
				
				return -1;
			}
		});
	}
	
	//当完成一个工序的最后一个拆分加工时，需要知道这个工序之前所有拆分的坏品数之和。这个函数就是获取这个值的
	public String getTotalScrapQuantityOfOtherPart(String wipId, String opCode) throws SQLException {

		String sql = "select sum(scrap_quantity) as aaa from kol_pes_move_txn_result where wip_entity_id="+wipId+" and fm_operation_code='"+opCode+"' and interfaced=0 and op_end is not null";
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		int scrapTotal = runner.query(sql, new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return 0;
		        }
			
				while(rs.next()) { 
					return rs.getInt("aaa");
				}
				
				return 0;
			}
		});
		
		LogUtil.log("getTotalScrapQuantityOfOtherPart():sql="+sql);
		LogUtil.log("getTotalScrapQuantityOfOtherPart():scrapTotal="+scrapTotal);

		return String.valueOf(scrapTotal);
	}
	
	//开始某道工序之前，需要计算最大可投入数。由于存在未完成工序和工序未全数加工完是不允许跳工序，所以计算最大投入数简单了
	//canJump是判断主管操作时，允许跳工序的情况
	public int getLeftCountForStartOp(String wipName, int wipEntityId, int routing_sequence_id, String opCode, String curOperationId, boolean canJump) throws SQLException {
		
		int incompleteQuan = getWipIncompleteQuantityByWipId(wipEntityId);
		
		String sql = "select sum(trx_quantity) as total from KOL_PES_MOVE_TXN_RESULT where interfaced = 0 and wip_entity_id = "+wipEntityId;
		QueryRunner runner = new QueryRunner(this.dataSource);
		int trxTotal = runner.query(sql, new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return 0;
		        }
			
				while(rs.next()) { 
					return rs.getInt("total");
				}
				
				return 0;
			}
		});
		
		LogUtil.log("getLeftCountForStartOp:opCode="+opCode+", trxTotal="+trxTotal+", incompleteQuan="+incompleteQuan);

		return incompleteQuan-trxTotal;
		
//		if(incompleteQuan >= 0) {
//			List<DataTxnSeqItem> seqListTemp = findTxnSeqList(wipEntityId, null);
//			int trxQuanTotal = getAllTxnSeqTrxQuantity(seqListTemp);
//			int trxQuanNotEnd = getNotEndsTxnSeqTrxQuantity(seqListTemp);
//			
//			LogUtil.log("getLeftCountForStartOp:opCode="+opCode+", trxQuanTotal="+trxQuanTotal+", trxQuanNotEnd="+trxQuanNotEnd+", incompleteQuan="+incompleteQuan);
//			
//			if(trxQuanTotal>incompleteQuan) {
//				return incompleteQuan;
//			}
//			else if(trxQuanTotal==incompleteQuan) {
//				
//				String curOpCode = getOpCodeByOpId(routing_sequence_id, curOperationId);
//				String lastWorkingOpCode = getCurReallyWorkingOpCode(wipEntityId);
//				LogUtil.log("getLeftCountForStartOp:curOpCode="+curOpCode+", lastWorkingOpCode="+lastWorkingOpCode);
//				if(lastWorkingOpCode!=null && lastWorkingOpCode.equals(curOpCode)) {
//					return incompleteQuan-trxQuanTotal;
//				}
//				else if(trxQuanNotEnd>0) {
//					return incompleteQuan-trxQuanTotal;
//				}
//				else {
//					return incompleteQuan;
//				}
//			}
//			else {
//				return incompleteQuan-trxQuanTotal;
//			}
//		}
//		
//		
//		return 0;
	}
	
	//获取工序的加工设备列表
	public List<DataAssetInfoItem> getOpAssetList(String opCode) throws SQLException {
		String sql = "select asset_id, tag_number, description from kol_pes_asset where op_code='"+opCode+"' and asset_id != -1";
		LogUtil.log("getOpAssetList():sql="+sql);
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataAssetInfoItem>>() {

			public List<DataAssetInfoItem> handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
				
				List<DataAssetInfoItem> opAssetList = new ArrayList<DataAssetInfoItem>();
				
				while(rs.next()) { 
					DataAssetInfoItem data = new DataAssetInfoItem();
					data.assetId = rs.getInt("ASSET_ID");
					data.tagNumber = rs.getString("TAG_NUMBER");
					data.description = rs.getString("DESCRIPTION");

					opAssetList.add(data);
				}

				return opAssetList;
			}
		});
	}
	
	//获取输入的txn工序列表的输入数量之和
	private int getAllTxnSeqTrxQuantity(List<DataTxnSeqItem> seqList) {
		int allTrxQ = 0;
		if(seqList!=null && seqList.size()>0) {
			for(DataTxnSeqItem seq : seqList) {
				if(seq!=null && seq.trxQuantity>=0) {
					allTrxQ = allTrxQ + seq.trxQuantity;
				}
			}
		}
		
		return allTrxQ;
	}
	
	//获取输入的txn工序列表的未完成工序的输入数量之和
	private int getNotEndsTxnSeqTrxQuantity(List<DataTxnSeqItem> seqList) {
		int allTrxQ = 0;
		if(seqList!=null && seqList.size()>0) {
			for(DataTxnSeqItem seq : seqList) {
				if(seq!=null && seq.trxQuantity>=0 && (seq.opEnd==null||seq.opEnd.trim().length()==0)) {
					allTrxQ = allTrxQ + seq.trxQuantity;
				}
			}
		}
		
		return allTrxQ;
	}

	//根据已知的工单号和工序号，获取某元件某道工序的加工情况
	private List<DataTxnSeqItem> findTxnSeqList(int wipEntityId, String opCode) throws SQLException {
		
		String sql = "select FM_OPERATION_CODE,TRX_QUANTITY,OP_END from kol_pes_move_txn_result where WIP_ENTITY_ID = "+wipEntityId+" and FM_OPERATION_CODE='"+opCode+"' and (interfaced = 0 or interfaced = 1) order by transaction_id desc";
		if(opCode == null) {//工单号为空时，检索这个工单的所有工序
			sql = "select FM_OPERATION_CODE,TRX_QUANTITY,OP_END from kol_pes_move_txn_result where WIP_ENTITY_ID="+wipEntityId+" and (interfaced = 0 or interfaced = 1) order by transaction_id desc";
		}
		
		LogUtil.log("findTxnSeqList():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataTxnSeqItem>>() {

			public List<DataTxnSeqItem> handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
				
				List<DataTxnSeqItem> tempList = new ArrayList<DataTxnSeqItem>();

				String firstOpCode = null;
				
				while(rs.next()) { 
					DataTxnSeqItem data = new DataTxnSeqItem();
//					data.transactionId = rs.getString("TRANSACTION_ID");
//					data.creationDate = CommonUtil.formatDateTime(rs.getTimestamp("CREATION_DATE"));
//					data.createdBy = rs.getString("CREATED_BY");
//					data.lastUpdateDate = CommonUtil.formatDateTime(rs.getTimestamp("LAST_UPDATE_DATE"));
//					data.lastUpdatedBy = rs.getString("LAST_UPDATED_BY");
//					data.wipEntityId = rs.getString("WIP_ENTITY_ID");
					data.fmOperationCode = rs.getString("FM_OPERATION_CODE");
					data.trxQuantity = rs.getInt("TRX_QUANTITY");
//					data.scrapQuantity = rs.getString("SCRAP_QUANTITY");
//					data.assetId = rs.getString("ASSET_ID");
//					data.opStart = CommonUtil.formatDateTime(rs.getTimestamp("OP_START"));
					data.opEnd = CommonUtil.formatDateTime(rs.getTimestamp("OP_END"));
//					data.interfaced = rs.getInt("INTERFACED");
					
					if(firstOpCode == null) {//记录最顶端的工单code
						firstOpCode = data.fmOperationCode;
					}
					
					LogUtil.log("findTxnSeqList():data.fmOperationCode="+data.fmOperationCode);
					
					if(!firstOpCode.equals(data.fmOperationCode)) {//当一个新的工序出现时，结束统计。因为只有排在最前面的几个工序是我们需要的
						return tempList;
					}
					
					tempList.add(data);
				}

				return tempList;
			}
		});
	}
	
	//在开始一个工序前，判断是否有这个工单的其它工序还没有加工完，如果有，则不能启动一个新的不同工序
	public String isOtherOpNotEndBeforeStartTheNewOne(int WIP_ENTITY_ID, String THIS_OPERATION_CODE) throws SQLException {
		
		String sql = "select FM_OPERATION_CODE from kol_pes_move_txn_result where WIP_ENTITY_ID = "+WIP_ENTITY_ID+" and FM_OPERATION_CODE!='"+THIS_OPERATION_CODE+"' and OP_END is null";
		LogUtil.log("isOtherOpNotEndBeforeStartTheNewOne():sql="+sql);
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
			
				while(rs.next()) { 
					return rs.getString("FM_OPERATION_CODE");
				}
				
				return null;
			}
		});
	}
	
	//在开始一个新的不同工序前，判断当前cur_operation_id对应的工序是否全数完成，如没有则不能开启新工序
	public boolean isCurrentOpCompletedBeforeStartTheNewOne(String wipName, int WIP_ENTITY_ID, int routing_sequence_id, String curOperationId, String opCodeWantStart) throws SQLException {
		
		LogUtil.log("isCurrentOpCompletedBeforeStartTheNewOne()");
		
		String curWorkingOpCode = getCurReallyWorkingOpCode(WIP_ENTITY_ID);
		
		LogUtil.log("isCurrentOpCompletedBeforeStartTheNewOne():curWorkingOpCode="+curWorkingOpCode+", opCodeWantStart="+opCodeWantStart);
		
		if(curWorkingOpCode==null || opCodeWantStart.equals(curWorkingOpCode)) {//如果要开启的工序是cur_operation_id对应的工序，则直接返回true
			return true;
		}
		
		return isCurrentOpCompletedAfterItemSelected(WIP_ENTITY_ID);
	}
	
	//工序开始前运行一个存储过程，确定下能否开启这个工序
	public int runProcedureBeforeStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException {

		Connection con = this.dataSource.getConnection();
		CallableStatement cstmt = con.prepareCall("{call PES.KOL_PES_UTIL_PKG.operationStart(?,?,?)}");
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.setInt(1, WIP_ENTITY_ID);
		cstmt.setString(2, FM_OPERATION_CODE);
		cstmt.execute();
		int resErrCode = cstmt.getInt(3);
		LogUtil.log("runProcedureBeforeStartOp():cstmt.getInt(3)="+resErrCode);
		cstmt.close();
		con.close();
		
		return resErrCode;
	}
	
	//进入完成工序界面时运行一个存储过程，确定下能否完成这个工序
	public DataCodeMessageItem runProcedureBeforeEndOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException {

		DataCodeMessageItem item = new DataCodeMessageItem();
		
		Connection con = this.dataSource.getConnection();
		CallableStatement cstmt = con.prepareCall("{call PES.KOL_PES_UTIL_PKG.operationBeforeComplete(?,?,?,?)}");
		cstmt.setInt(1, WIP_ENTITY_ID);
		cstmt.setString(2, FM_OPERATION_CODE);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
		cstmt.execute();
		int resErrCode = cstmt.getInt(3);
		LogUtil.log("runProcedureBeforeEndOp():WIP_ENTITY_ID="+WIP_ENTITY_ID+", FM_OPERATION_CODE="+FM_OPERATION_CODE+", cstmt.getInt(3)="+resErrCode+", errorMsg="+cstmt.getString(4));
		
		item.code = resErrCode;
		item.message = CommonUtil.noNullString(cstmt.getString(4));
		
		cstmt.close();
		con.close();
		
		return item;
	}
	
	//工序开始前运行一个存储过程，确定下能否开启这个工序
	public int runProcedureAfterStartOp(int WIP_ENTITY_ID, String FM_OPERATION_CODE) throws SQLException {

		Connection con = this.dataSource.getConnection();
		CallableStatement cstmt = con.prepareCall("{call PES.KOL_PES_UTIL_PKG.operationStarted(?,?,?)}");
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
		cstmt.setInt(1, WIP_ENTITY_ID);
		cstmt.setString(2, FM_OPERATION_CODE);
		cstmt.execute();
		int resErrCode = cstmt.getInt(3);
		LogUtil.log("runProcedureAfterStartOp():cstmt.getInt(3)="+resErrCode);
		cstmt.close();
		con.close();
		
		return resErrCode;
	}
	
	//开始工序操作时，插相应数据
	public int insertWhenStartAnOp(String transactionId, Date CREATION_DATE, String CREATED_BY,
									Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, int WIP_ENTITY_ID,
									String FM_OPERATION_CODE, String TRX_QUANTITY, String SCRAP_QUANTITY,
									String assetId1, String assetId2, String assetId3, Date OP_START, int routing_sequence_id, boolean canJump) throws SQLException {
		    
			if(assetId1 == null) {
				assetId1 = String.valueOf(-1);
			}
		
			QueryRunner runner = new QueryRunner(this.dataSource);

			Object[] sqlParam = new Object[] {transactionId.trim(), CommonUtil.formatDateTime(CREATION_DATE), CREATED_BY.trim(), CommonUtil.formatDateTime(LAST_UPDATE_DATE), LAST_UPDATED_BY.trim(),
											  WIP_ENTITY_ID, FM_OPERATION_CODE, TRX_QUANTITY.trim(), SCRAP_QUANTITY.trim(), assetId1, assetId2, assetId3, CommonUtil.formatDateTime(OP_START)};
			
//			///////////寻找move_txn没有正确备份的原因
//			try {
//				runner.update("insert into kol_pes_move_txn_result_log(TRANSACTION_ID, CREATION_DATE, CREATED_BY,"+
//						 "LAST_UPDATE_DATE, LAST_UPDATED_BY, WIP_ENTITY_ID,"+
//						 "FM_OPERATION_CODE, TRX_QUANTITY, SCRAP_QUANTITY,"+
//						 "ASSET_ID,ASSET_ID1,ASSET_ID2, OP_START) values(?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'))", sqlParam);
//				LogUtil.log("insert into kol_pes_move_txn_result_log");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			////////////////////////////////////
			
			return runner.update("insert into kol_pes_move_txn_result(TRANSACTION_ID, CREATION_DATE, CREATED_BY,"+
								 "LAST_UPDATE_DATE, LAST_UPDATED_BY, WIP_ENTITY_ID,"+
								 "FM_OPERATION_CODE, TRX_QUANTITY, SCRAP_QUANTITY,"+
								 "ASSET_ID,ASSET_ID1,ASSET_ID2, OP_START) values(?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'))", sqlParam);
	}
	
	//完成工序时的数据库操作
	public int updateWhenEndAnOp(String transactionId, int SCRAP_QUANTITY, Date OP_END, String LAST_UPDATED_BY, Date LAST_UPDATE_DATE) throws SQLException {
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("update kol_pes_move_txn_result set ");
		sbSql.append("SCRAP_QUANTITY=?,");
		sbSql.append("LAST_UPDATED_BY=?,");
		sbSql.append("LAST_UPDATE_DATE=to_date(?,'yyyy-mm-dd hh24:mi:ss'),");
		sbSql.append("OP_END=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
		sbSql.append(" where TRANSACTION_ID=?");
		
		Object[] params = new Object[] {
										SCRAP_QUANTITY,
										LAST_UPDATED_BY.trim(),
										CommonUtil.formatDateTime(LAST_UPDATE_DATE),
										CommonUtil.formatDateTime(OP_END),
										transactionId.trim()
									   };
		
		LogUtil.log("updateWhenEndAnOp():sql="+sbSql.toString());
		LogUtil.log("updateWhenEndAnOp():SCRAP_QUANTITY="+SCRAP_QUANTITY);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		int resRows = runner.update(sbSql.toString(), params);
		
//		///////////寻找move_txn没有正确备份的原因////
//		try {
//			StringBuilder sbSqlTest = new StringBuilder();
//			sbSql.append("insert into kol_pes_move_txn_result_log(");
//			sbSql.append("SCRAP_QUANTITY,");
//			sbSql.append("LAST_UPDATED_BY,");
//			sbSql.append("LAST_UPDATE_DATE,");
//			sbSql.append("OP_END, TRANSACTION_ID) ");
//			sbSql.append("values(?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?)");
//			
//			runner.update(sbSqlTest.toString(), params);
//			LogUtil.log("update kol_pes_move_txn_result_log set");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		////////////////////////////////////////
		
		return resRows;
	}
	
	//插入需要推送的坏品超标消息
	private void insertOpPushMessage(String transactionId) throws SQLException {
		
		DataSeqStartedItem tempSeq = getOpStartedItemByTransId(transactionId);
		
		List<DataOpFailNotifyItem> opFialList = getOpCodeListNeedPushMsg();
		
		if(tempSeq!=null && opFialList!=null && opFialList.size()>0) {
			
			QueryRunner runner = new QueryRunner(this.dataSource);
			
			for(DataOpFailNotifyItem failItem : opFialList) {
				
				if(failItem!=null && failItem.opCode!=null && failItem.opCode.equals(tempSeq.fmOperationCode) &&
				   tempSeq.scrapQuantity!=null && CommonUtil.isValidNumber(tempSeq.scrapQuantity) &&
				   tempSeq.trxQuantity!=null && CommonUtil.isValidNumber(tempSeq.trxQuantity)) {
					
					float scrapPercent = (Float.valueOf(tempSeq.scrapQuantity)/Float.valueOf(tempSeq.trxQuantity));
					
					LogUtil.log("scrapPercent="+scrapPercent+", failItem.scrapPercent="+failItem.scrapPercent);
					
					if(Float.valueOf(failItem.scrapPercent) < scrapPercent) {//如果坏品比例超出system的上线则需要推送
						
						String insSql = "insert into kol_pes_op_scrap_push_msg "
										+ "(TRANSACTION_ID, WIP_ENTITY_ID, CREATION_DATE, CREATED_BY, OP_CODE, KEEP_PUSH_DAYS, "
										+ "TRX_QUANTITY, SCRAP_QUANTITY)"
										+" values(?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?,?)";
				
						Object[] sqlParam = new Object[] {tempSeq.transactionId,tempSeq.wipEntityId,
														  CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis())),
														  tempSeq.lastUpdateBy,tempSeq.fmOperationCode, 2, 
														  tempSeq.trxQuantity, tempSeq.scrapQuantity};
						runner.update(insSql, sqlParam);
						
						LogUtil.log("insertOpPushMessage:"+insSql);
					}
					break;
				}
			}
		}
	}
	
	//获取transId获取工序的详细信息
	private DataSeqStartedItem getOpStartedItemByTransId(String transactionId) throws SQLException {
		
		String sql  = "select * from kol_pes_move_txn_result where TRANSACTION_ID="+transactionId;
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<DataSeqStartedItem>() {

			public DataSeqStartedItem handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) { 
					DataSeqStartedItem data = new DataSeqStartedItem();
					data.transactionId = String.valueOf(rs.getInt("TRANSACTION_ID"));
					data.wipEntityId = rs.getString("WIP_ENTITY_ID");
					data.lastUpdateBy = rs.getString("LAST_UPDATED_BY");
					data.fmOperationCode = rs.getString("FM_OPERATION_CODE");
					data.trxQuantity = rs.getString("TRX_QUANTITY");
					data.scrapQuantity = rs.getString("SCRAP_QUANTITY");
					data.opStartDate = CommonUtil.formatDateTime(rs.getTimestamp("OP_START"));
					data.opEndDate = CommonUtil.noNullString(CommonUtil.formatDateTime(rs.getTimestamp("OP_END")));
					
					return data;
				}

				return null;
			}
		});
	}
	
	//从system表格中拿到哪些工序需要消息推送
	private List<DataOpFailNotifyItem> getOpCodeListNeedPushMsg() throws SQLException {
		
		String sql = "select * from kol_pes_system_param where SOURCE='OPERATION_FAIL_NOTIFY'";
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<List<DataOpFailNotifyItem>>() {

			public List<DataOpFailNotifyItem> handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
				
				List<DataOpFailNotifyItem> tempList = new ArrayList<DataOpFailNotifyItem>();

				while(rs.next()) { 
					DataOpFailNotifyItem data = new DataOpFailNotifyItem();
					data.opCode = rs.getString("PROFILE_NAME");
					data.scrapPercent = rs.getString("PROFILE_VALUE");
					data.accessLevel = rs.getString("ACCESS_LEVEL");
					
					tempList.add(data);
				}

				return tempList;
			}
		});
	}
	
	//工序完成时运行一个存储过程，告诉系统更新下工单的状态
	public DataSeqProcedureItem runSQLNoticeJobStatus(String transactionId) throws SQLException {
		
		DataSeqStartedItem tempSeq = getOpStartedItemByTransId(transactionId);
		
		if(tempSeq!=null) {
			Connection con = this.dataSource.getConnection();
			CallableStatement cstmt = con.prepareCall("{call PES.KOL_PES_UTIL_PKG.operationComplete(?,?,?,?)}");
			cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(1, Integer.valueOf(tempSeq.wipEntityId.trim()));
			cstmt.setString(2, tempSeq.fmOperationCode);
			cstmt.execute();
			int resErrCode = cstmt.getInt(3);
			String resErrMsg = cstmt.getString(4);
			LogUtil.log("runSQLNoticeJobStatus():resErrCode="+resErrCode+", resErrMsg="+resErrMsg);
			cstmt.close();
			con.close();
			
			if(resErrCode == 0) {
				insertOpPushMessage(transactionId);
			}
			
			return new DataSeqProcedureItem(resErrCode, resErrMsg);
		}
		
		return new DataSeqProcedureItem();
		//declare errorcode number; begin PES.KOL_PES_UTIL_PKG.operationComplete(1077836, 'CD00', errorcode); end;
	}
	
	public int getOpTrxId(String transactionId) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sqlGetOpTrxId = "select count(1) as have from wsm_lot_move_txn_interface where source_line_id = "+transactionId;
	
		return runner.query(sqlGetOpTrxId, new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return 0;
		        }
			
				while(rs.next()) { 
					return rs.getInt("have");
				}
				
				return 0;
			}
		});
	}
	
	//将工序的endDate更新为空
	public int updateResetOpEndedToNull(String transactionId) throws SQLException {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("update kol_pes_move_txn_result set ");
		sbSql.append("SCRAP_QUANTITY=0,");
		sbSql.append("OP_END=null");
		sbSql.append(" where TRANSACTION_ID=?");
		
		Object[] params = new Object[] {transactionId.trim()};
		
		LogUtil.log("updateResetOpEndedToNull():sql="+sbSql.toString());
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		int resRows = runner.update(sbSql.toString(), params);
		LogUtil.log("updateResetOpEndedToNull:resRows="+resRows);
		
		deleteQaReslutWhenOpEndFail(transactionId);
		
		return resRows;
	}
	
	//删除工序结束失败的质量管理计划项
	private int deleteQaReslutWhenOpEndFail(String transactionId) throws SQLException {
		String sql = "delete from kol_pes_qa_result where transaction_id="+transactionId.trim();
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		int resRows = runner.update(sql);
		LogUtil.log("deleteQaReslutWhenOpEndFail:resRows="+resRows+", sql="+sql);
		return resRows;
	}
	
	//判断当前工序是否都结束了
	public boolean isCurrentOpCompletedAfterItemSelected(int WIP_ENTITY_ID) throws SQLException {

		QueryRunner runner = new QueryRunner(this.dataSource);
		String sqlGetOpCode = "select fm_operation_code from kol_pes_move_txn_result where WIP_ENTITY_ID="+WIP_ENTITY_ID+" and interfaced = 0";
	
		String opCode = runner.query(sqlGetOpCode, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
			
				while(rs.next()) { 
					return rs.getString("fm_operation_code");
				}
				
				return null;
			}
		});
		LogUtil.log("isCurrentOpCompletedAfterItemSelected():opCode="+opCode);
		
		return !(opCode!=null && opCode.length()>0);
	}
	
	//考虑到跳工序的情况，判断下到底现在是那个工序正在生产
	public String getCurReallyWorkingOpCode(int wipEntityId) throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sqlGetOpCode = "select fm_operation_code from kol_pes_move_txn_result where WIP_ENTITY_ID="+wipEntityId+" and (interfaced = 0 or interfaced = 1) order by transaction_id desc";
	
		String opCode = runner.query(sqlGetOpCode, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
			
				while(rs.next()) { 
					return rs.getString("fm_operation_code");
				}
				
				return null;
			}
		});
		LogUtil.log("getCurReallyWorkingOpCode():opCode="+opCode);
		return opCode;
	}
	
	
	//判断完成工序时是否加了质量管理计划
	public boolean isQaFilledWhenEndingAnOp(String transId) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String sql = "select count(transaction_id) as aaa from kol_pes_qa_result where transaction_id="+transId;
	
		LogUtil.log("isQaFilledWhenEndingAnOp:sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<Boolean>() {

			public Boolean handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return false;
		        }
				
				while(rs.next()) {
					return rs.getInt("aaa")>0;
				}
				
				return false;
			}
		});
	}
	
	//获取某个工单的某一工序的未完成开启数目，判断是否需要填写质量收集计划用
	public boolean isLastUncompleteOpNumForWip(String wipId, String opCode, boolean canJump) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String sql = "select count(transaction_id)as aaa from kol_pes_move_txn_result where wip_entity_id="+wipId+" and fm_operation_code='"+opCode+"' and op_end is null";
	
		int uncompleteSeqNum = runner.query(sql, new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return 0;
		        }
				
				while(rs.next()) {
					return rs.getInt("aaa");
				}
				
				return 0;
			}
		});
		
		List<String> tempList = getWipNameAndSeqId(wipId);
		
		int leftQuan = getLeftCountForStartOp(tempList.get(0), Integer.valueOf(wipId), Integer.valueOf(tempList.get(1)), opCode, tempList.get(2), canJump);
	
		LogUtil.log("isLastUncompleteOpNumForWip:sql="+sql+"\n uncompleteSeqNum="+uncompleteSeqNum+", leftQuan="+leftQuan);
		
		return uncompleteSeqNum<=1 && leftQuan<=0;
	}
	
	//获取工单名称和工序id
	private List<String> getWipNameAndSeqId(String wipEntityId) throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String sql = "select wip_entity_name, common_routing_sequence_id, cur_operation_id from kol_pes_os_job where wip_entity_id="+wipEntityId;
	
		return runner.query(sql, new ResultSetHandler<List<String>>() {

			public List<String> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<String> tempList = new ArrayList<String>();
				
				while(rs.next()) {
					tempList.add(rs.getString("wip_entity_name"));
					tempList.add(rs.getString("common_routing_sequence_id"));
					tempList.add(rs.getString("cur_operation_id"));
					
					return tempList;
				}
				
				return tempList;
			}
		});
	}
	
	
	
	//获取某个员工开启的所有工序，检索参数为staffNo或AssetTagNumber
	public List<DataSeqStartedItem> getSeqStartedList(String staffNoOrWipNum) throws SQLException {
		
		if(CommonUtil.isStringNotNull(staffNoOrWipNum)) {
		
			String sql = "select r.*, staf.staff_name, akk.*, j.*, s.operation_description, nvl(ts.pct_complete,0) pct_complete from kol_pes_move_txn_result r, " +
						 "(select asset_id, tag_number, description from kol_pes_asset group by asset_id, tag_number, description) akk, " +
						 "kol_pes_os_job j, kol_pes_staffs staf, kol_pes_op_seq s, kol_pes_trx_status ts where r.op_end is null and r.asset_id = akk.asset_id and j.wip_entity_id = r.wip_entity_id " + 
						 "and (r.created_by = "+staffNoOrWipNum+" or r.wip_entity_id = "+staffNoOrWipNum+") and r.fm_operation_code = s.standard_operation_code " +
						 "and j.common_routing_sequence_id = s.routing_sequence_id and staf.staff_no = r.created_by and ts.transaction_id (+) = r.transaction_id order by r.transaction_id desc";
			
			LogUtil.log("getSeqStartedList:sql="+sql);
			QueryRunner runner = new QueryRunner(this.dataSource);
			
			return runner.query(sql, new ResultSetHandler<List<DataSeqStartedItem>>() {
	
				public List<DataSeqStartedItem> handle(ResultSet rs) throws SQLException { 
					if (rs == null) {
			            return null;
			        }
					
					List<DataSeqStartedItem> tempList = new ArrayList<DataSeqStartedItem>();
	
					while(rs.next()) { 
						DataSeqStartedItem data = new DataSeqStartedItem();
						data.transactionId = String.valueOf(rs.getInt("TRANSACTION_ID"));
						data.wipEntityId = rs.getString("WIP_ENTITY_ID");
						data.wipEntityName = rs.getString("WIP_ENTITY_NAME");
						data.creationDate = CommonUtil.formatDateTime(rs.getTimestamp("CREATION_DATE"));
						data.createdBy = rs.getString("CREATED_BY");
						data.lastUpdateDate = CommonUtil.formatDateTime(rs.getTimestamp("LAST_UPDATE_DATE"));
						data.lastUpdateBy = rs.getString("STAFF_NAME");
						data.fmOperationCode = rs.getString("FM_OPERATION_CODE");
						data.opDesc = rs.getString("OPERATION_DESCRIPTION");//OP_DSCR
						data.trxQuantity = rs.getString("TRX_QUANTITY");
						data.assetDesc = rs.getString("DESCRIPTION");
						data.opStartDate = CommonUtil.formatDateTime(rs.getTimestamp("OP_START"));
						data.opEndDate = CommonUtil.noNullString(CommonUtil.formatDateTime(rs.getTimestamp("OP_END")));
						data.assettagNumber = rs.getString("TAG_NUMBER");
						
						//job
						data.saItem = rs.getString("SA_ITEM");
						data.saItemDesc = rs.getString("SA_ITEM_DESC");
						
						data.dffCpnNumber = CommonUtil.noNullString(rs.getString("DFF_CPN_NUMBER"));
						
						data.dffCustomerspec = CommonUtil.noNullString(rs.getString("DFF_CPN_NUMBER"));
						data.dffMfgSpec = CommonUtil.noNullString(rs.getString("DFF_MFG_SPEC"));
						
						data.custNumber = CommonUtil.noNullString(rs.getString("CUST_NUMBER"));
						
						data.incompleteQuantity = rs.getInt("INCOMPLETE_QUANTITY");
						data.startQuantity = rs.getInt("START_QUANTITY");
						data.quantityCompleted = rs.getInt("QUANTITY_COMPLETED");
						data.quantityScrapped = rs.getInt("QUANTITY_SCRAPPED");
						
						data.primaryItemId = rs.getInt("PRIMARY_ITEM_ID");
						data.commonRoutingSequenceId = rs.getString("COMMON_ROUTING_SEQUENCE_ID");
						
						data.curOperationId = rs.getString("CUR_OPERATION_ID");
						data.organizationId = rs.getString("ORGANIZATION_ID");
						
						data.pctComplete = rs.getString("PCT_COMPLETE");
						
						tempList.add(data);
					}
	
					return tempList;
				}
			});
		}
		
		return null;
	}
	
	//获取某个工单的工序加工情况，呼应客户端首界面的“查询工单”功能
	public List<DataSeqStartedItem> getSeqAllListByWipId(String wipId) throws SQLException {
		
		String sql = "select kol_pes_move_txn_result.*, kol_pes_staffs.STAFF_NAME, a.*, kol_pes_os_job.*, kol_pes_op_seq.OPERATION_DESCRIPTION from "+
					 "kol_pes_move_txn_result,(select asset_id, tag_number,DESCRIPTION from kol_pes_asset group by asset_id, tag_number,DESCRIPTION) a,kol_pes_os_job,kol_pes_staffs,kol_pes_op_seq "+
					 "where "+
					 "kol_pes_move_txn_result.asset_id=a.asset_id and "+ 
					 "kol_pes_os_job.wip_entity_id=kol_pes_move_txn_result.wip_entity_id and "+
					 "kol_pes_move_txn_result.wip_entity_id="+wipId+" and "+
					 "kol_pes_move_txn_result.fm_operation_code=kol_pes_op_seq.standard_operation_code and "+
					 "kol_pes_os_job.common_routing_sequence_id=kol_pes_op_seq.routing_sequence_id and "+
					 "kol_pes_staffs.STAFF_NO=kol_pes_move_txn_result.created_by order by transaction_id desc";
		
		LogUtil.log("getSeqAllListByWipId:sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<List<DataSeqStartedItem>>() {

			public List<DataSeqStartedItem> handle(ResultSet rs) throws SQLException { 
				if (rs == null) {
		            return null;
		        }
				
				List<DataSeqStartedItem> tempList = new ArrayList<DataSeqStartedItem>();

				while(rs.next()) { 
					DataSeqStartedItem data = new DataSeqStartedItem();
					data.transactionId = String.valueOf(rs.getInt("TRANSACTION_ID"));
					data.wipEntityId = rs.getString("WIP_ENTITY_ID");
					data.wipEntityName = rs.getString("WIP_ENTITY_NAME");
					data.creationDate = CommonUtil.formatDateTime(rs.getTimestamp("CREATION_DATE"));
					data.createdBy = rs.getString("CREATED_BY");
					data.lastUpdateDate = CommonUtil.formatDateTime(rs.getTimestamp("LAST_UPDATE_DATE"));
					data.lastUpdateBy = rs.getString("STAFF_NAME");
					data.fmOperationCode = rs.getString("FM_OPERATION_CODE");
					data.opDesc = rs.getString("OPERATION_DESCRIPTION");
					data.trxQuantity = rs.getString("TRX_QUANTITY");
					data.scrapQuantity =  CommonUtil.noNullString(rs.getString("SCRAP_QUANTITY"));
					data.assetDesc = rs.getString("DESCRIPTION");
					data.opStartDate = CommonUtil.formatDateTime(rs.getTimestamp("OP_START"));
					data.opEndDate = CommonUtil.noNullString(CommonUtil.formatDateTime(rs.getTimestamp("OP_END")));
					data.assettagNumber = rs.getString("TAG_NUMBER");
					data.interfaced = rs.getString("interfaced");
					
					//job
					data.saItem = rs.getString("SA_ITEM");
					data.saItemDesc = rs.getString("SA_ITEM_DESC");
					
					data.dffCpnNumber = CommonUtil.noNullString(rs.getString("DFF_CPN_NUMBER"));
					
					data.dffCustomerspec = CommonUtil.noNullString(rs.getString("DFF_CPN_NUMBER"));
					data.dffMfgSpec = CommonUtil.noNullString(rs.getString("DFF_MFG_SPEC"));
					
					data.custNumber = CommonUtil.noNullString(rs.getString("CUST_NUMBER"));
					
					data.incompleteQuantity = rs.getInt("INCOMPLETE_QUANTITY");
					data.startQuantity = rs.getInt("START_QUANTITY");
					data.quantityCompleted = rs.getInt("QUANTITY_COMPLETED");
					data.quantityScrapped = rs.getInt("QUANTITY_SCRAPPED");
					
					data.primaryItemId = rs.getInt("PRIMARY_ITEM_ID");
					data.commonRoutingSequenceId = rs.getString("COMMON_ROUTING_SEQUENCE_ID");
					
					data.curOperationId = rs.getString("CUR_OPERATION_ID");
					data.organizationId = rs.getString("ORGANIZATION_ID");
					
					tempList.add(data);
				}

				return tempList;
			}
		});
	}
	
	//删除一个工序的函数 
	public int deleteOpByTransId(String transactionId) throws SQLException {
		
		String sql = "delete kol_pes_move_txn_result where TRANSACTION_ID="+transactionId; 
		
		LogUtil.log("deleteOpByTransId():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		int resRows = runner.update(sql);
		LogUtil.log("deleteOpByTransId="+resRows);
		return resRows;
	}
	
	public String getTimeBufferForOpStart() throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select profile_value from kol_pes_system_param where source='MOVE_TXN_TIME_BUFFER' and profile_name='OP_START'";
		
		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) {
					return rs.getString("profile_value");
				}
				
				return null;
			}
		});
	}
	
	public String getTimeBufferForOpEnd() throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select profile_value from kol_pes_system_param where source='MOVE_TXN_TIME_BUFFER' and profile_name='OP_END'";
		
		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) {
					return rs.getString("profile_value");
				}
				
				return null;
			}
		});
	}
}
