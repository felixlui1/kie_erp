/*-----------------------------------------------------------

-- PURPOSE

--    处理登录的数据库操作类

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

import com.kol.pes.item.DataWeekItem;


@Repository("dataEnableDao")
public class DataEnableDaoImpl implements DataEnableDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//读取数据库状态
	public int dataStatus() throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select PROFILE_VALUE from kol_pes_system_param where SOURCE='SYSTEM' and PROFILE_NAME='REFRESH_STATUS'", new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					int status = rs.getInt("PROFILE_VALUE");
					return status;
				}
				
				return 1;
			}
		});
	}
	
	public List<DataWeekItem> getWeekList() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select * from kol_pes_system_param where source='BACKUP_SCHEDULE_S' or source='BACKUP_SCHEDULE_E'", new ResultSetHandler<List<DataWeekItem>>() {

			public List<DataWeekItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				List<DataWeekItem> tempList = new ArrayList<DataWeekItem>();
				while(rs.next()) { 
					tempList.add(new DataWeekItem(rs.getString("profile_name"), rs.getString("source"), rs.getString("profile_value")));
				}
				return tempList;
			}
		});
	}
	
	//获取最新的apk版本号
	public int getLatestApkVersionNumber() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select profile_value from kol_pes_system_param where source='APK' and profile_name='LATEST_VERSION'", new ResultSetHandler<Integer>() {

			public Integer handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return 1;
		        }
				
				while(rs.next()) { 
					int status = rs.getInt("PROFILE_VALUE");
					return status;
				}
				
				return 1;
			}
		});
	}
	
	//是否强制升级apk
	public String getApkIsForceUpdate() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select profile_value from kol_pes_system_param where source='APK' and profile_name='IS_FORCE_UPDATE'", new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("PROFILE_VALUE");
				}
				
				return null;
			}
		});
	}
	
	//apk升级时的提示信息
	public String getApkUpdateMsg() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query("select profile_value from kol_pes_system_param where source='APK' and profile_name='UPDATE_MSG'", new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("PROFILE_VALUE");
				}
				
				return null;
			}
		});
	}

}
