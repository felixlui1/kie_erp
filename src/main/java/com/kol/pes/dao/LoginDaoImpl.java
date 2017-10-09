/*-----------------------------------------------------------

-- PURPOSE

--    处理登录的数据库操作类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.kol.pes.item.DataLoginItem;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


@Repository("loginDao")
public class LoginDaoImpl implements LoginDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//登录，返回登录人的信息
	public DataLoginItem login(String userId) throws SQLException {
		if(CommonUtil.isValidNumber(userId)) {
			QueryRunner runner = new QueryRunner(this.dataSource);
			return runner.query("select * from kol_pes_staffs where staff_no = "+userId, new ResultSetHandler<DataLoginItem>() {
	
				public DataLoginItem handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return null;
			        }
					
					while(rs.next()) { 
						DataLoginItem user = new DataLoginItem();
						user.staffNo = rs.getString("STAFF_NO");
						user.staffName = CommonUtil.noNullString(rs.getString("STAFF_NAME"));
						user.cardNo = CommonUtil.noNullString(rs.getString("CARD_NO"));
						user.levelCode = CommonUtil.noNullString(rs.getString("ATTRIBUTE12"));
						
						LogUtil.log("user.staffName="+user.staffName);
						
						return user;
					}
					
					return null;
				}
			});
		}
		return null;
	}

}
