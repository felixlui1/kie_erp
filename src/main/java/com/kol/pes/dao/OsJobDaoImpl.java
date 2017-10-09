/*-----------------------------------------------------------

-- PURPOSE

--    处理工单的数据库操作类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.kol.pes.item.DataOsJobItem;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


@Repository("osJobDao")
public class OsJobDaoImpl implements OsJobDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//根据工单名称获取工单列表
	public List<DataOsJobItem> findOsJob(String wipName) throws SQLException {
		
		final String sql = "select * from kol_pes_os_job where upper(WIP_ENTITY_NAME) like upper('%"+wipName+"%')";
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataOsJobItem>>() {

			public List<DataOsJobItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataOsJobItem> tempList = new ArrayList<DataOsJobItem>();

				while(rs.next()) { 
					DataOsJobItem data = new DataOsJobItem();
					data.wipEntityId = rs.getInt("WIP_ENTITY_ID");
					data.wipEntityName = rs.getString("WIP_ENTITY_NAME");
					
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
					
					data.curOperationId = CommonUtil.noNullString(rs.getString("CUR_OPERATION_ID"));
					
					data.organizationId = rs.getString("ORGANIZATION_ID");
					
					tempList.add(data);
				}
				
				LogUtil.log("OsJobDaoImpl:tempList.size()="+tempList.size()+"sql="+sql);
				
				return tempList;
			}
		});
	}
	
	//根据工单id获取工单列表
	public List<DataOsJobItem> findOsJob(int wipId) throws SQLException {
		
		String sql = "select * from kol_pes_os_job where WIP_ENTITY_ID="+wipId;
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataOsJobItem>>() {

			public List<DataOsJobItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataOsJobItem> tempList = new ArrayList<DataOsJobItem>();
				
				while(rs.next()) { 
					DataOsJobItem data = new DataOsJobItem();
					data.wipEntityId = rs.getInt("WIP_ENTITY_ID");
					data.wipEntityName = rs.getString("WIP_ENTITY_NAME");
					
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
					
					data.curOperationId = CommonUtil.noNullString(rs.getString("CUR_OPERATION_ID"));
					
					tempList.add(data);
				}
				
				LogUtil.log("OsJobDaoImpl:tempList.size()="+tempList.size());
				
				return tempList;
			}
		});
	}
}
