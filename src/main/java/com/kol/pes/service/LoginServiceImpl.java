/*-----------------------------------------------------------

-- PURPOSE

--    登录的Service

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kol.pes.dao.LoginDao;
import com.kol.pes.item.DataLoginItem;

@Service("loginService")
public class LoginServiceImpl extends DataEnableServiceImpl implements LoginService {
	
	@Autowired
	@Qualifier("loginDao")
	private LoginDao loginDao;

	public DataLoginItem login(String userId) {
		try {
			return this.loginDao.login(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
