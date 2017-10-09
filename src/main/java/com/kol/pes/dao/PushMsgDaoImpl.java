/*-----------------------------------------------------------

-- PURPOSE

--    获取推送消息的数据库操作类

-- History

--	  19-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.kol.pes.item.DataPushMsgItem;
import com.kol.pes.utils.CommonUtil;


@Repository("pushMsgDao")
public class PushMsgDaoImpl implements PushMsgDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	public List<DataPushMsgItem> getPushMsg(String staffNo, String transId, String isNotice) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
//		String userLevel = runner.query("select * from kol_pes_staffs where staff_no = "+staffNo, new ResultSetHandler<String>() {
//
//			public String handle(ResultSet rs) throws SQLException {
//				if (rs == null) {
//		            return null;
//		        }
//				
//				while(rs.next()) { 
//					return rs.getString("ATTRIBUTE12");
//				}
//				
//				return null;
//			}
//		});
		
		List<String> opCanRevList = runner.query("select * from kol_pes_system_param where SOURCE='OPERATION_FAIL_NOTIFY'", new ResultSetHandler<List<String>>() {

			public List<String> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<String> opCodeList = new ArrayList<String>();
				
				while(rs.next()) { 
					String opCode = rs.getString("PROFILE_NAME");
					opCodeList.add(opCode);
				}
				
				return opCodeList;
			}
		});
		
		if(opCanRevList!=null && opCanRevList.size()>0) {
			
			String time24HoursAgoS = CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis()-CommonUtil.revertDaysToMills(1)));
			//LogUtil.log("time24HoursAgoS="+time24HoursAgoS);
			
			StringBuilder sqlSb = new StringBuilder();
			
			sqlSb.append("select kol_pes_op_scrap_push_msg.*, kol_pes_op_seq.operation_description , kol_pes_os_job.* ");
			sqlSb.append("from kol_pes_op_scrap_push_msg, kol_pes_op_seq, kol_pes_os_job where ");
			sqlSb.append("(kol_pes_op_scrap_push_msg.op_code='").append(opCanRevList.get(0)).append("'");
			for(int i=1; i<opCanRevList.size(); i++) {
				sqlSb.append(" or kol_pes_op_scrap_push_msg.op_code='").append(opCanRevList.get(i)).append("'");
			}
			sqlSb.append(") and kol_pes_op_scrap_push_msg.op_code=kol_pes_op_seq.standard_operation_code and ");
			sqlSb.append("kol_pes_os_job.common_routing_sequence_id=kol_pes_op_seq.routing_sequence_id and ");
			sqlSb.append("kol_pes_os_job.wip_entity_id=kol_pes_op_scrap_push_msg.wip_entity_id");
			if("Y".equals(isNotice)) {
				sqlSb.append(" and kol_pes_op_scrap_push_msg.transaction_id>").append(transId);
			}
			sqlSb.append(" and kol_pes_op_scrap_push_msg.CREATION_DATE > to_date('").append(time24HoursAgoS).append("','yyyy-mm-dd hh24:mi:ss')");
			sqlSb.append(" order by kol_pes_op_scrap_push_msg.transaction_id desc");
			
			List<DataPushMsgItem> pushMsgList = runner.query(sqlSb.toString(), new ResultSetHandler<List<DataPushMsgItem>>() {

				public List<DataPushMsgItem> handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return null;
			        }
					
					List<DataPushMsgItem> tempPushList = new ArrayList<DataPushMsgItem>();
					
					while(rs.next()) { 
						
						DataPushMsgItem data = new DataPushMsgItem(rs.getString("transaction_id"));

						data.wip_entity_name = rs.getString("wip_entity_name");
						data.op_code = rs.getString("op_code");
						data.operation_description = rs.getString("operation_description");
						data.scrap_quantity = rs.getString("scrap_quantity");
						data.trx_quantity = rs.getString("trx_quantity");

						tempPushList.add(data);
					}
					
					return tempPushList;
				}
			});
			
			return pushMsgList;
		}
		
		return null;
	}
}
