/*-----------------------------------------------------------

-- PURPOSE

--    处理点检信息的数据库操作类

-- History

--	  29-Sep-14  LiZheng  Created.

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

import com.kol.pes.item.DataAssetCheckAssetListItem;
import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.item.DataAssetCheckItem;
import com.kol.pes.item.DataAssetCheckSdtItem;
import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.item.DataAssetMachineFailItem;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;

@Repository("assetCheckDao")
public class AssetCheckDaoImpl implements AssetCheckDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//获取有故障的点检设备列表
	public List<DataAssetCheckItem> findAssetWithError() throws SQLException {
		
		String sql = "select nvl(s.staff_name, '已辞职') staff_name, a.*, r.* "+
					"from kol_pes_staffs s, (select distinct ASSET_ID,TAG_NUMBER,DESCRIPTION,OP_DSCR,OP_CODE from kol_pes_asset) a, "
					+ "kol_pes_asset_check_result r "+
					"where r.asset_id=a.asset_id "+
					"and s.staff_no(+)=r.created_by "+
					"and r.status=2 "+
					"order by r.check_id desc";
		
		LogUtil.log("findAssetWithError():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataAssetCheckItem>>() {

			public List<DataAssetCheckItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataAssetCheckItem> tempList = new ArrayList<DataAssetCheckItem>();
				
				while(rs.next()) { 
					DataAssetCheckItem data = new DataAssetCheckItem();
					
					data.checkId = rs.getInt("CHECK_ID");
					data.creationDate = CommonUtil.formatDateTime(rs.getTimestamp("CREATION_DATE"));
					
					data.createdBy = rs.getInt("CREATED_BY");
					data.createdByName = rs.getString("STAFF_NAME");
					data.lastUpdateDate = CommonUtil.formatDateTime(rs.getTimestamp("LAST_UPDATE_DATE"));
					
					data.lastUpdatedBy = rs.getInt("LAST_UPDATED_BY");
					data.assetId = rs.getString("ASSET_ID");
					data.assetTagNumber = rs.getString("TAG_NUMBER");
					data.assetName = rs.getString("DESCRIPTION");
					data.assetOpDscr = rs.getString("OP_DSCR");
					
					data.checkTime = CommonUtil.formatDateTime(rs.getTimestamp("CHECK_TIME"));
					data.checkResult = rs.getString("CHECK_RESULT");
					data.status = rs.getString("STATUS");
					data.estRepairStart = CommonUtil.formatDateTime(rs.getTimestamp("EST_REPAIR_START"));
					data.estRepairEnd = CommonUtil.formatDateTime(rs.getTimestamp("EST_REPAIR_END"));
					data.checkRemarks = rs.getString("CHECK_REMARKS");
					
					if(CommonUtil.isStringNotNull(data.checkRemarks)) {
						data.checkRemarks = data.checkRemarks.replace("<", "小于").replace(">", "大于");
					}
					
					data.failureCode = rs.getInt("FAILURE_CODE");
					data.opCode = rs.getString("OP_CODE");
					
					tempList.add(data);
				}
				
				LogUtil.log("DataAssetCheckItem::tempList.size()="+tempList.size());
				rs.close();
				return tempList;
			}
		});
	}
	
	//生成点检ID
	public String getCheckId() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		Object[] resultArray = runner.query("select PES_ASSET_CHECK_S.nextval from dual", new ArrayHandler());
		
		if (resultArray != null && resultArray.length > 0) {
            return resultArray[0].toString();
		}
		
		return null;
	}
	
	//根据tagNumber或许某一设备的其它信息
	public DataAssetInfoItem getAssetInfo(String tagNum) throws SQLException {
		
		LogUtil.log("getAssetInfo():tagNum="+tagNum);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select * from kol_pes_asset where TAG_NUMBER = '"+tagNum+"'";
		return runner.query(sql, new ResultSetHandler<DataAssetInfoItem>() {

			public DataAssetInfoItem handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) { 
					DataAssetInfoItem data = new DataAssetInfoItem();
					
					data.assetId = rs.getInt("ASSET_ID");
					data.assetNumber = rs.getString("ASSET_NUMBER");
					data.tagNumber = rs.getString("TAG_NUMBER");
					data.description = rs.getString("DESCRIPTION");
					data.opCode = rs.getString("OP_CODE");
					data.opDscr = rs.getString("OP_DSCR");
					data.location = rs.getString("LOCATION");
					data.attribute7 = rs.getString("ATTRIBUTE7");
					
					return data;
				}
				
				return null;
			}
		});
	}
	
	//插入新的点检记录
	public int insertCheck(String checkId, Date CREATION_DATE, int CREATED_BY, 
						   Date LAST_UPDATE_DATE, int LAST_UPDATED_BY,
						   String ASSET_ID, Date CHECK_TIME, int CHECK_RESULT, 
						   Date EST_REPAIR_START, Date EST_REPAIR_END, 
						   String CHECK_REMARKS) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		final String sbSql1 = "insert into kol_pes_asset_check_result(CHECK_ID, CREATION_DATE, CREATED_BY, "
						    + "LAST_UPDATE_DATE, LAST_UPDATED_BY,ASSET_ID, CHECK_TIME, CHECK_RESULT,"
						    + "EST_REPAIR_START, EST_REPAIR_END, CHECK_REMARKS, status) "
						    + "values(?,"
						    + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),"//
						    + "?,"
						    + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),"//
						    + "?,?,"
						    + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),"//
						    + "?,"
						    + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),"//
						    + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),"//
						    + "?,?)";
		
		final String sbSql2 = "insert into kol_pes_asset_check_result(CHECK_ID, CREATION_DATE, CREATED_BY, "
				            + "LAST_UPDATE_DATE, LAST_UPDATED_BY, ASSET_ID, CHECK_TIME, CHECK_RESULT, status) "
				            + "values(?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?)";
		
        Object[] params1 = new Object[] {
        								checkId,CommonUtil.formatDateTime(CREATION_DATE),CREATED_BY,
        								CommonUtil.formatDateTime(LAST_UPDATE_DATE),LAST_UPDATED_BY,
					            		ASSET_ID.trim(),CommonUtil.formatDateTime(CHECK_TIME),CHECK_RESULT,
					            		CommonUtil.formatDateTime(EST_REPAIR_START),
					            		CommonUtil.formatDateTime(EST_REPAIR_END),
					            		CHECK_REMARKS,2//2代表设备存在故障
					            	    };
        
        Object[] params2 = new Object[] {
					            		checkId,CommonUtil.formatDateTime(CREATION_DATE),CREATED_BY,
					            		CommonUtil.formatDateTime(LAST_UPDATE_DATE),LAST_UPDATED_BY,
					            		ASSET_ID.trim(),CommonUtil.formatDateTime(CHECK_TIME),CHECK_RESULT,1//1代表设备正常
					            	    };
        if(CHECK_RESULT == 2) {
            LogUtil.log("insertCheck():sbSql1="+sbSql1.toString());
            int res1 = runner.update(sbSql1.toString(), params1);
            runProcedureAfterInsertManual(checkId);
            return res1;
        }
        else if(CHECK_RESULT == 1) {
            LogUtil.log("insertCheck():sbSql2="+sbSql2.toString());
            int res2 = runner.update(sbSql2.toString(), params2);
            runProcedureAfterInsertManual(checkId);
            return res2;
        }
        
		return 0;
	}
	
	private int runProcedureAfterInsertManual(String checkId) throws SQLException {

		//kol_pes_util_pkg.checkComplete(20010, 'MANUAL', errcode, errmsg)
		
		Connection con = this.dataSource.getConnection();
		CallableStatement cstmt = con.prepareCall("{call kol_pes_util_pkg.checkComplete(?, 'MANUAL', ?, ?)}");
		
		cstmt.setString(1, checkId);
		cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);

		cstmt.execute();
		int resErrCode = cstmt.getInt(2);
		String resErrMsg = cstmt.getString(3);
		LogUtil.log("runProcedureAfterInsertManual():resErrCode="+resErrCode+", resErrMsg="+resErrMsg);
		cstmt.close();
		con.close();
		
		return resErrCode;
	}
	
	//此处传入的照片描述信息暂时没有查表，后面可能会更改
	public int insertPicPathDesc(String checkId, String pathDescList, boolean isRepair, boolean isSeq) throws SQLException {
		
		if(checkId!=null && pathDescList!=null && pathDescList.contains(",")) {
			
			QueryRunner runner = new QueryRunner(this.dataSource);
			String[] pathDescArr = pathDescList.split("#");
			int resAll = 0;
			
			for(int i=0; i<pathDescArr.length; i++) {
				String pd = pathDescArr[i];
				if(pd!=null) {
					String[] pdArr = pd.split(",");
					Object[] sqlParam = new Object[] {checkId, pdArr[0], isSeq?"kol_pes_move_txn_result":"kol_pes_asset_check_result", isRepair?"2":"1"};
					
					resAll = resAll + runner.update("insert into kol_pes_photo_store(SOURCE_ID, PHOTO_FILE_NAME, SOURCE_TABLE, STAGE) values(?,?,?,?)", sqlParam);
				}
			}
				
			return resAll;
		}
		
		return 0;
	}
	
	//获取设备错误类型列表
	public List<DataAssetMachineFailItem> getFailTypeList(String tag) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select * from kol_pes_machine_failure_type";
		if(tag!=null && tag.length()>0) {
			sql = "select * from kol_pes_machine_failure_type "
					+ "where tag in (select op_name from kol_pes_op_master "
					+ "where operation_code = (select op_code from kol_pes_asset "
					+ "where tag_number = '"+tag+"' and rownum=1))";
		}
		
		LogUtil.log("getFailTypeList():sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<List<DataAssetMachineFailItem>>() {

			public List<DataAssetMachineFailItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataAssetMachineFailItem> tempList = new ArrayList<DataAssetMachineFailItem>();

				while(rs.next()) { 
					DataAssetMachineFailItem data = new DataAssetMachineFailItem();
					
					data.lookupCode = rs.getString("LOOKUP_CODE");
					data.tag = rs.getString("TAG");
					data.meaning = rs.getString("MEANING");

					tempList.add(data);
				}
				
				return tempList;
			}
		});
	}
	
	//更新插入维修信息
	public int updateCheckRepair(String CHECK_ID, Date LAST_UPDATE_DATE, int LAST_UPDATED_BY, 
								 Date ACT_REPAIR_START, Date ACT_REPAIR_END, int DOWN_TIME, String REPAIR_REMARKS, int FAILURE_CODE) throws SQLException {
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("update kol_pes_asset_check_result set ");
		
		sbSql.append("LAST_UPDATE_DATE=to_date(?,'yyyy-mm-dd hh24:mi:ss'),");
		sbSql.append("LAST_UPDATED_BY=?,");
		
		sbSql.append("ACT_REPAIR_START=to_date(?,'yyyy-mm-dd hh24:mi:ss'),");
		sbSql.append("ACT_REPAIR_END=to_date(?,'yyyy-mm-dd hh24:mi:ss'),");
		sbSql.append("DOWN_TIME=?,");
		sbSql.append("FAILURE_CODE=lpad(?, 5, '0'),");
		sbSql.append("REPAIR_REMARKS=?,");
		sbSql.append("status=3");
		
		sbSql.append(" where CHECK_ID=?");
		
		Object[] params = new Object[] {
										CommonUtil.formatDateTime(LAST_UPDATE_DATE),
										LAST_UPDATED_BY,
										CommonUtil.formatDateTime(ACT_REPAIR_START),
										CommonUtil.formatDateTime(ACT_REPAIR_END),
										DOWN_TIME,
										FAILURE_CODE,
										REPAIR_REMARKS,
										CHECK_ID
										};
		
		LogUtil.log("updateCheckRepair():sql="+sbSql.toString());
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.update(sbSql.toString(), params);
	}
	
	public List<DataAssetCheckCheckItem> getAssetCheckCheckItemList(final String assetId) throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		final String sql = "select it.ITEM_SET_ID,it.ITEM_SEQ,it.QUERY_TEXT,it.QUERY_TYPE,it.chk_cycle, lk.ITEM_SET_ID from "+
							"kol_pes_machine_check_items it, kol_pes_machine_check_link lk "+
							"where it.ITEM_SET_ID=lk.ITEM_SET_ID "+
							"and lk.asset_id='"+assetId+"'";
		LogUtil.log("getAssetCheckCheckItemList():sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<List<DataAssetCheckCheckItem>>() {
			public List<DataAssetCheckCheckItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				List<DataAssetCheckCheckItem> tempList = new ArrayList<DataAssetCheckCheckItem>();
				while(rs.next()) { 
					DataAssetCheckCheckItem data = new DataAssetCheckCheckItem();
					data.itemSetId = rs.getString("ITEM_SET_ID");
					data.itemSeq = rs.getString("ITEM_SEQ");
					data.queryText = rs.getString("QUERY_TEXT").replace("<", "小于").replace(">", "大于");
					data.queryType = rs.getString("QUERY_TYPE");
					data.chkCycle = rs.getInt("chk_cycle");
					data.checkResult = "";
					data.lastChkTime = CommonUtil.noNullString(getAssetCheckLastCheckTimeFromHistory(assetId, data.itemSeq, data.chkCycle));
					
					//if(isCheckItemShouldBeCheck(nowTime, data.lastChkTime, data.chkCycle)) {
						tempList.add(data);
					//}
				}
				return tempList;
			}
		});
	}
	
	public String getAssetCheckLastCheckTimeOfAsset(String assetId) throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select max(creation_date) as max_date from kol_pes_mach_check_result where asset_id = "+assetId+" and KOL_PES_UTIL_PKG.dayShift(creation_date) = KOL_PES_UTIL_PKG.currShift";
		LogUtil.log("getAssetCheckLastCheckTimeOfAsset():sql="+sql);
		return runner.query(sql, new ResultSetHandler<String>() {
			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				while(rs.next()) { 
					return CommonUtil.noNullString(rs.getString("max_date"));
				}
				return null;
			}
		});
	}
	
	private boolean isCheckItemShouldBeCheck(Calendar nowTime, String lastChecktime, int chkCycle) {
		
		int nowHour = nowTime.get(Calendar.HOUR_OF_DAY);
		Calendar lastChk = CommonUtil.convertStringToCal(lastChecktime);
		
		if(lastChk != null && (nowTime.getTimeInMillis()-lastChk.getTimeInMillis()<CommonUtil.revertHoursToMills(12)) && chkCycle==12) {
			
			int lastChkHour = lastChk.get(Calendar.HOUR_OF_DAY);
			
			if(nowHour>=8 && nowHour<20) {
				if(lastChkHour<=nowHour && lastChkHour>=8) {
					LogUtil.log("isCheckItemShouldBeCheck():false1");
					return false;
				}
			}
			else {
				if(lastChkHour>=20 || lastChkHour<8) {
					LogUtil.log("isCheckItemShouldBeCheck():false2");
					return false;
				}
			}
		}
		LogUtil.log("isCheckItemShouldBeCheck():true");
		return true;
	}
	
	//取出未超出点检周期的之前点检过的设备信息
	private String getAssetCheckLastCheckTimeFromHistory(String assetId, String itemSeq, int chkCycle) throws SQLException {

		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select * from (select query_value,last_update_date from kol_pes_mach_check_result where asset_id='"+assetId+"' and item_seq='"+itemSeq+"' order by LAST_UPDATE_DATE desc) where rownum=1";
		LogUtil.log("getAssetCheckResultFromHistory():sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<String>() {
			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				while(rs.next()) { 
					return CommonUtil.formatDateTime(rs.getTimestamp("last_update_date"));
				}
				return null;
			}
		});
	}
	
	//根据assetId获取某一设备的其它信息
	public DataAssetInfoItem getAssetCheckAssetInfo(String tagNum) throws SQLException {
		
		LogUtil.log("getAssetCheckAssetInfo():tagNum="+tagNum);
		
//		String shift = CommonUtil.getShiftByNowDateTime();
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
//		String sql = "select a.* "+
//				"from kol_pes_mach_check_schedule s "+
//				", kol_pes_machine_check_link l "+
//				", (select distinct asset_id, asset_number, tag_number, description, op_code, OP_DSCR, location, attribute7 from kol_pes_asset) a "+
//				"where 1=1  "+
//				"and s.check_date=to_date('"+CommonUtil.formatDate(getCheckDateByNow())+"','yyyy-mm-dd') "+
//				"and s.shift="+shift+" "+
//				"and s.asset_id = l.asset_id "+
//				"and a.tag_number = '"+tagNum+"' "+
//				"and l.asset_id = a.asset_id";
		
		String sql = "select a.* "+
					"from "+
					" kol_pes_machine_check_link l "+
					", (select distinct asset_id, asset_number, tag_number, description, op_code, OP_DSCR, location, attribute7 from kol_pes_asset) a "+
					"where 1=1  "+
					"and a.tag_number = '"+tagNum+"' "+
					"and l.asset_id = a.asset_id";
		
		LogUtil.log("getAssetCheckAssetInfo():sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<DataAssetInfoItem>() {

			public DataAssetInfoItem handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				while(rs.next()) { 
					DataAssetInfoItem data = new DataAssetInfoItem();
					data.assetId = rs.getInt("ASSET_ID");
					data.assetNumber = rs.getString("ASSET_NUMBER");
					data.tagNumber = rs.getString("TAG_NUMBER");
					data.description = rs.getString("DESCRIPTION");
					data.opCode = rs.getString("OP_CODE");
					data.opDscr = rs.getString("OP_DSCR");
					data.location = rs.getString("LOCATION");
					data.attribute7 = rs.getString("ATTRIBUTE7");
					return data;
				}
				return null;
			}
		});
	}
	
	private Date getCheckDateByNow() {
		Calendar nowDate = Calendar.getInstance();
		int hour = nowDate.get(Calendar.HOUR_OF_DAY);
		if(hour < 8) {
			nowDate.setTimeInMillis(nowDate.getTimeInMillis()-CommonUtil.revertDaysToMills(1));
		}
		return new Date(nowDate.getTimeInMillis());
	}
	
	//生成ID
	public String assetCheckGetCheckId() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		Object[] resultArray = runner.query("select pes_machine_check_s.nextval from dual", new ArrayHandler());
		
		if (resultArray != null && resultArray.length > 0) {
            return resultArray[0].toString();
		}
		
		return null;
	}
	
	//提交点检信息
	public int assetCheckSubmitCheck(String assetId, String checkId, String userId, DataAssetCheckCheckItem checkValue) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		final String sbSql = "insert into kol_pes_mach_check_result(asset_id, CHECK_ID, "
							+ "item_set_id, item_seq, query_value, query_result,"
							+ "CREATION_DATE, CREATED_BY, "
				            + "LAST_UPDATE_DATE, LAST_UPDATED_BY) "
				            + "values(?,?,?,?,?,?,"
				            + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,"
				            + "to_date(?,'yyyy-mm-dd hh24:mi:ss'),?)";
		
		Object[] params = new Object[] {
				assetId, checkId, checkValue.itemSetId, checkValue.itemSeq, 
				checkValue.checkResult, "Y",//assetCheckIsCheckOk(checkValue)?"Y":"N"
				CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis())), userId,
        		CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis())), userId
        	    };
		LogUtil.log("assetCheckSubmitCheck():sbSql="+sbSql);
		int res = runner.update(sbSql.toString(), params);
		
		return res;
	}
	
	public int runProcedureAfterInsertCheck(String checkId) throws SQLException {

		//kol_pes_util_pkg.checkComplete(20010, 'CHECK', errcode, errmsg)
		
		Connection con = this.dataSource.getConnection();
		CallableStatement cstmt = con.prepareCall("{call kol_pes_util_pkg.checkComplete(?, 'CHECK', ?, ?)}");
		
		cstmt.setString(1, checkId);
		cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
		cstmt.registerOutParameter(3, java.sql.Types.INTEGER);

		cstmt.execute();
		int resErrCode = cstmt.getInt(2);
		String resErrMsg = cstmt.getString(3);
		LogUtil.log("runProcedureAfterInsertCheck():resErrCode="+resErrCode+", resErrMsg="+resErrMsg);
		cstmt.close();
		con.close();
		
		return resErrCode;
	}
	
	//更新点检信息
	public int assetCheckUpdateCheck(String assetId, String userId, DataAssetCheckCheckItem checkValue, int chkCycle) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String timeS = CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis()-CommonUtil.revertHoursToMills(chkCycle)));
		
		final String updateSql = "update kol_pes_mach_check_result set query_value=?, query_result = ?, "
								+ "LAST_UPDATE_DATE=to_date(?,'yyyy-mm-dd hh24:mi:ss'), LAST_UPDATED_BY=?"
								+ " where item_seq=? and asset_id=? and "
								+ "LAST_UPDATE_DATE>to_date(?,'yyyy-mm-dd hh24:mi:ss')";
		
		Object[] updateParams = new Object[] {
								checkValue.checkResult, assetCheckIsCheckOk(checkValue)?"Y":"N",
								CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis())), 
								userId,
								checkValue.itemSeq,
								assetId,
								timeS};
		
		LogUtil.log("assetCheckUpdateCheck():updateSql="+updateSql);
		
		return runner.update(updateSql.toString(), updateParams);
	}
	
	private boolean assetCheckIsCheckOk(DataAssetCheckCheckItem checkValue) throws SQLException {
		if(checkValue != null) {
			
			if("numeric".equals(checkValue.queryType.toLowerCase())) {
				
				QueryRunner runner = new QueryRunner(this.dataSource);
				String sql  = "select * from kol_pes_check_items_std where item_set_id="+checkValue.itemSetId+" and item_seq='"+checkValue.itemSeq+"'";
				LogUtil.log("assetCheckIsCheckOk():sql="+sql);
				
				DataAssetCheckSdtItem sdtData = runner.query(sql, new ResultSetHandler<DataAssetCheckSdtItem>() {

					public DataAssetCheckSdtItem handle(ResultSet rs) throws SQLException {
						if (rs == null) {
				            return null;
				        }
						
						while(rs.next()) { 
							DataAssetCheckSdtItem data = new DataAssetCheckSdtItem();
							data.rangeLow = rs.getString("range_low");
							data.rangeHigh = rs.getString("range_high");
							data.equalSign = rs.getString("equal_sign");
							return data;
						}
						
						return null;
					}
				});
				
				if(sdtData != null) {
//					if("Y".equals(sdtData.equalSign)) {
//						if(CommonUtil.isStringNotNull(sdtData.rangeLow) && CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
//							return Float.valueOf(checkValue.checkResult) > Float.valueOf(sdtData.rangeLow) && Float.valueOf(checkValue.checkResult) < Float.valueOf(sdtData.rangeHigh);
//						}
//						else if(CommonUtil.isStringNotNull(sdtData.rangeLow) && !CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
//							return Float.valueOf(checkValue.checkResult) > Float.valueOf(sdtData.rangeLow);
//						}
//						else if(!CommonUtil.isStringNotNull(sdtData.rangeLow) && CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
//							return Float.valueOf(checkValue.checkResult) < Float.valueOf(sdtData.rangeHigh);
//						}
//					}
//					else {
						if(CommonUtil.isStringNotNull(sdtData.rangeLow) && CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
							return Float.valueOf(checkValue.checkResult) >= Float.valueOf(sdtData.rangeLow) && Float.valueOf(checkValue.checkResult) <= Float.valueOf(sdtData.rangeHigh);
						}
						else if(CommonUtil.isStringNotNull(sdtData.rangeLow) && !CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
							return Float.valueOf(checkValue.checkResult) >= Float.valueOf(sdtData.rangeLow);
						}
						else if(!CommonUtil.isStringNotNull(sdtData.rangeLow) && CommonUtil.isStringNotNull(sdtData.rangeHigh)) {
							return Float.valueOf(checkValue.checkResult) <= Float.valueOf(sdtData.rangeHigh);
						}
//					}
				}
				else {
					return true;
				}
			}
			else if("clean_level".equals(checkValue.queryType.toLowerCase())) {
				return !"1".equals(checkValue.checkResult);
			}
			else if("anomaly".equals(checkValue.queryType.toLowerCase())) {
				return "1".equals(checkValue.checkResult);
			}
			else if("boolean".equals(checkValue.queryType.toLowerCase())) {
				return "Y".equals(checkValue.checkResult);
			}
			else {
				return true;
			}
		}
		
		return true;
	}
	
	public List<DataAssetCheckAssetListItem> assetCheckGetAssetList(Date startDateS, Date endDateS) throws SQLException {
		
		Date startDate = CommonUtil.getFormatedDateForDay(new Date(startDateS.getTime()));
		Date endDate = CommonUtil.getFormatedDateForDay(new Date(endDateS.getTime()));
		
		Date cursorDate = new Date(startDate.getTime());
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		List<DataAssetCheckAssetListItem> tmpAssetList = new ArrayList<DataAssetCheckAssetListItem>();
		
		while(cursorDate.getTime() <= endDate.getTime()) {
			
			String sqlAssetTotalNum = "select count(1) as total_num from kol_pes_machine_check_link";
			
			String sqlAssetNeedCheckNumLight = "select count(1) as need_check_num from (select asset_id from kol_pes_mach_check_schedule where trunc(check_date)=trunc(to_date('"+CommonUtil.formatDate(cursorDate)+"','yyyy-mm-dd')) and shift=1 group by asset_id)";
			String sqlAssetNeedCheckNumNight = "select count(1) as need_check_num from (select asset_id from kol_pes_mach_check_schedule where trunc(check_date)=trunc(to_date('"+CommonUtil.formatDate(cursorDate)+"','yyyy-mm-dd')) and shift=2 group by asset_id)";
			
			String sqlAssetCheckedNumLight = "select count(1) as checked_num from (select distinct asset_id from kol_pes_mach_check_result where kol_pes_util_pkg.dayshift(creation_date) = '"+CommonUtil.formatDate(cursorDate)+"-1')";
			String sqlAssetCheckedNumNight = "select count(1) as checked_num from (select distinct asset_id from kol_pes_mach_check_result where kol_pes_util_pkg.dayshift(creation_date) = '"+CommonUtil.formatDate(cursorDate)+"-2')";;
			
			LogUtil.log("sqlAssetNeedCheckNumLight="+sqlAssetNeedCheckNumLight);
			LogUtil.log("sqlAssetNeedCheckNumNight="+sqlAssetNeedCheckNumNight);
			
			int assetTotalNum = runner.query(sqlAssetTotalNum, new ResultSetHandler<Integer>() {

				public Integer handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return 0;
			        }
					while(rs.next()) { 
						return rs.getInt("total_num");
					}
					return 0;
				}
			});
			
			int assetNeedCheckNumLight = runner.query(sqlAssetNeedCheckNumLight, new ResultSetHandler<Integer>() {

				public Integer handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return 0;
			        }
					while(rs.next()) { 
						return rs.getInt("need_check_num");
					}
					return 0;
				}
			});
			
			int assetNeedCheckNumNight = runner.query(sqlAssetNeedCheckNumNight, new ResultSetHandler<Integer>() {

				public Integer handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return 0;
			        }
					while(rs.next()) { 
						return rs.getInt("need_check_num");
					}
					return 0;
				}
			});
			
			int assetCheckedNumLight = runner.query(sqlAssetCheckedNumLight, new ResultSetHandler<Integer>() {

				public Integer handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return 0;
			        }
					while(rs.next()) { 
						return rs.getInt("checked_num");
					}
					return 0;
				}
			});
			
			int assetCheckedNumNight = runner.query(sqlAssetCheckedNumNight, new ResultSetHandler<Integer>() {

				public Integer handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return 0;
			        }
					while(rs.next()) { 
						return rs.getInt("checked_num");
					}
					return 0;
				}
			});
			
			DataAssetCheckAssetListItem data = new DataAssetCheckAssetListItem();
			
			data.checkDate = CommonUtil.formatDate(cursorDate);
			data.assetTotalNum = assetTotalNum;
			data.assetNeedCheckNumLight = assetNeedCheckNumLight;
			data.assetNeedCheckNumNight = assetNeedCheckNumNight;
			data.assetCheckedNumLight = assetCheckedNumLight;
			data.assetCheckedNumNight = assetCheckedNumNight;
			
			tmpAssetList.add(data);
			
			cursorDate.setTime(cursorDate.getTime()+CommonUtil.revertDaysToMills(1));
		}//while
		
		return tmpAssetList;
	}
	
	public List<DataAssetCheckSetAssetCheckItem> assetCheckGetSetAssetCheckList(Date date, int shift, String staffNo) throws SQLException {
		
		Date lightStart = CommonUtil.getSettedDateTimeForLightStart(date);
		Date lightEnd = CommonUtil.getSettedDateTimeForLightEnd(date);
		
		Date nightStart = CommonUtil.getSettedDateTimeForNightStart(date);
		Date nightEnd = CommonUtil.getSettedDateTimeForNightEnd(date);
		
		Date day = CommonUtil.getFormatedDateForDay(date);
		
		String sql = "select asset_id, "
				+ "(select tag_number from kol_pes_asset where kol_pes_asset.asset_id = kol_pes_machine_check_link.asset_id and rownum<=1) as tag_num,"
				+ "(select DESCRIPTION from kol_pes_asset where kol_pes_asset.asset_id = kol_pes_machine_check_link.asset_id and rownum<=1) as description,"
				+ "(select asset_id from kol_pes_mach_check_schedule where kol_pes_mach_check_schedule.asset_id = kol_pes_machine_check_link.asset_id and trunc(kol_pes_mach_check_schedule.check_date)=trunc(to_date('"+CommonUtil.formatDate(day)+"','yyyy-mm-dd')) and kol_pes_mach_check_schedule.shift="+shift+" and rownum<=1) as scheduled_id,"
				+ (shift==1 ? "(select asset_id from kol_pes_mach_check_result where kol_pes_mach_check_result.asset_id=kol_pes_machine_check_link.asset_id and last_update_date>=to_date('"+CommonUtil.formatDateTime(lightStart)+"','yyyy-mm-dd hh24:mi:ss') and last_update_date<to_date('"+CommonUtil.formatDateTime(lightEnd)+"','yyyy-mm-dd hh24:mi:ss') and rownum<=1) as checked_id":
							  "(select asset_id from kol_pes_mach_check_result where kol_pes_mach_check_result.asset_id=kol_pes_machine_check_link.asset_id and last_update_date>=to_date('"+CommonUtil.formatDateTime(nightStart)+"','yyyy-mm-dd hh24:mi:ss') and last_update_date<to_date('"+CommonUtil.formatDateTime(nightEnd)+"','yyyy-mm-dd hh24:mi:ss') and rownum<=1) as checked_id")
				+ " from kol_pes_machine_check_link where asset_id in"+
					"  (select a.asset_id from kol_pes_machine_check_link l, "+
					"(select distinct pa.asset_id, pa.tag_number, pa.description, pa.location from "+
					"kol_pes_asset pa, kol_pes_mach_sched_rights r "+
					"where pa.asset_id = nvl(r.asset_id, pa.asset_id)"+
					"and r.op_code = pa.op_code and r.staff_no = "+staffNo+") a "+
					"where a.asset_id = l.asset_id) order by tag_num";
		
		LogUtil.log("assetCheckGetSetAssetCheckList():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<List<DataAssetCheckSetAssetCheckItem>>() {

			public List<DataAssetCheckSetAssetCheckItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataAssetCheckSetAssetCheckItem> tmpAssetList = new ArrayList<DataAssetCheckSetAssetCheckItem>();

				while(rs.next()) { 
					DataAssetCheckSetAssetCheckItem data = new DataAssetCheckSetAssetCheckItem();
					
					data.assetId = rs.getInt("asset_id");
					
					data.assetTagNum = rs.getString("tag_num");
					data.assetDescription = rs.getString("description");
					
					data.scheduledId = rs.getString("scheduled_id")==null ? "0":rs.getString("scheduled_id");
					data.checkedId = rs.getString("checked_id")==null ? "0":rs.getString("checked_id");
					
					tmpAssetList.add(data);
				}
				
				return tmpAssetList;
			}
		});
	}
	
	public int assetCheckUpdateAssetCheckList(Date date, int shift, String userId, List<DataAssetCheckSetAssetCheckItem> assetList) throws SQLException {

		Date nowDate = new Date(Calendar.getInstance().getTimeInMillis());
		Date dayDate = CommonUtil.getFormatedDateForDay(new Date(Calendar.getInstance().getTimeInMillis()));
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		for(int i=0; i<assetList.size(); i++) {
		
			DataAssetCheckSetAssetCheckItem assetDataItem = assetList.get(i);
			LogUtil.log("assetCheckUpdateAssetCheckList():asset name="+assetDataItem.assetDescription);
			
			if(CommonUtil.isStringNotNull(assetDataItem.scheduledId) && !"0".equals(assetDataItem.scheduledId)) {
				
				String insertedAssetId = assetCheckGetAssetIdFromSchedule(assetDataItem.assetId, date, shift);
				
				if(insertedAssetId == null) {
					String sqlInsertAsset = "insert into kol_pes_mach_check_schedule(asset_id, check_date, shift, creation_date, created_by, last_update_date, last_updated_by) values(?, trunc(to_date(?,'yyyy-mm-dd')), ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?)";
				
					Object[] paramsInsert = new Object[] {assetDataItem.assetId,
														  CommonUtil.formatDate(date),
														  shift, CommonUtil.formatDateTime(nowDate), userId, 
														  CommonUtil.formatDateTime(nowDate), userId};
					
					runner.update(sqlInsertAsset, paramsInsert);
					LogUtil.log("assetCheckUpdateAssetCheckList():sqlInsertAsset="+sqlInsertAsset);
				}
				else {
					String sqlUpdateAsset = "update kol_pes_mach_check_schedule set last_update_date=to_date(?,'yyyy-mm-dd hh24:mi:ss'),last_updated_by=? where asset_id=? and trunc(check_date)=trunc(to_date(?,'yyyy-mm-dd')) and shift=?";
					Object[] paramsUpdate = new Object[] {
														CommonUtil.formatDateTime(nowDate),
														userId,
														assetDataItem.assetId,
														CommonUtil.formatDate(dayDate),
														shift};
					
					runner.update(sqlUpdateAsset, paramsUpdate);
					LogUtil.log("assetCheckUpdateAssetCheckList():sqlUpdateAsset="+sqlUpdateAsset);
				}
			}
			else {
				String sqlDeleteAsset = "delete from kol_pes_mach_check_schedule where asset_id=? and trunc(check_date)=trunc(to_date(?,'yyyy-mm-dd')) and shift=?";
				Object[] paramsDelete = new Object[] {
													assetDataItem.assetId,
													CommonUtil.formatDate(date),
													shift};

				runner.update(sqlDeleteAsset, paramsDelete);
				LogUtil.log("assetCheckUpdateAssetCheckList():sqlDeleteAsset="+sqlDeleteAsset);
			}
		}//for
		
		return 0;
	}
	
	private String assetCheckGetAssetIdFromSchedule(int assetId, Date day, int shift) throws SQLException {
		
		String sqlGetInsertedAsset = "select asset_id from kol_pes_mach_check_schedule where asset_id="+assetId+" and trunc(check_date)=trunc(to_date('"+CommonUtil.formatDate(day)+"','yyyy-mm-dd')) and shift="+shift;
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sqlGetInsertedAsset, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) { 
					return rs.getString("asset_id");
				}
				
				return null;
			}
		});
	}
	
	public void assetCheckInsertChangedParts(String checkId, String changedParts, String staffNo, String opCode) throws SQLException {
		
		Date nowDate = new Date(Calendar.getInstance().getTimeInMillis());

		String sqlInsertParts = "insert into kol_pes_changed_parts(check_id, parts_name, creation_date, created_by, last_update_date, last_updated_by) values(?, ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?)";
		
		Object[] paramsInsert = new Object[] {checkId, changedParts,
											  CommonUtil.formatDateTime(nowDate), staffNo, 
											  CommonUtil.formatDateTime(nowDate), staffNo};
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		runner.update(sqlInsertParts, paramsInsert);
		LogUtil.log("assetCheckInsertChangedParts():sqlInsertParts="+sqlInsertParts);
			
		if(!assetCheckIsChangedPartsNameExist(changedParts, opCode)) {
			String sqlInsertPartsHistory = "insert into kol_pes_machine_parts(op_code, parts_name, creation_date, created_by, last_update_date, last_updated_by) values(?, ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?, to_date(?,'yyyy-mm-dd hh24:mi:ss'), ?)";
			Object[] paramsInsertHistory = new Object[] {opCode, changedParts,
														CommonUtil.formatDateTime(nowDate), staffNo, 
														CommonUtil.formatDateTime(nowDate), staffNo};
			runner.update(sqlInsertPartsHistory, paramsInsertHistory);
		}
	}
	
	private boolean assetCheckIsChangedPartsNameExist(String changedParts, String opCode) throws SQLException {
		String sql = "select parts_name from kol_pes_machine_parts where parts_name='"+changedParts+"' and op_code='"+opCode+"'";
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String changedPartsFromDb = runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) { 
					return rs.getString("parts_name");
				}
				
				return null;
			}
		});
		
		return CommonUtil.isStringNotNull(changedPartsFromDb);
	}
	
	public List<String> assetCheckGetChangedPartsHistoryList(String opCode) throws SQLException {
		
		String sql = "select parts_name from kol_pes_machine_parts where op_code='"+opCode+"'";
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<List<String>>() {

			public List<String> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				List<String> tmpPartsList = new ArrayList<String>();

				while(rs.next()) { 
					tmpPartsList.add(rs.getString("parts_name"));
				}
				return tmpPartsList;
			}
		});
	}
	
	public boolean assetCheckCancelAssetCheck(String tagNum) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String sqlDeleteAsset = "delete from kol_pes_mach_check_schedule where to_char(check_date, 'yyyy-mm-dd') || '-' || shift = kol_pes_util_pkg.currshift and asset_id in (select asset_id from kol_pes_asset where tag_number = '"+tagNum+"')";
		Object[] paramsDelete = new Object[] {tagNum};
		LogUtil.log("assetCheckCancelAssetCheck():sql="+sqlDeleteAsset);
		
		int resRow = runner.update(sqlDeleteAsset);
		LogUtil.log("assetCheckCancelAssetCheck():tagNum="+tagNum+", resRow="+resRow);
		return resRow > 0;
	}
	
}
