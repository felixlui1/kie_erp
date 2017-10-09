/*-----------------------------------------------------------

-- PURPOSE

--    登录的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataLoginItem;
import com.kol.pes.service.LoginService;
import com.kol.pes.utils.LogUtil;


public class LoginAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("loginService")
	private LoginService loginService;
	
	private String userId = "";//用户staffNo
	private DataLoginItem loginData;//登陆后的数据
	
	@Override
	@Action(value="/login", results={
			@Result(name="success", location="login.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(loginService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setLoginData(loginService.login(this.userId));
		
		if(loginData==null || loginData.staffName==null) {
			setCode(CODE_FAIL);
			setMessage(getText("common.loginNoThisStaff")+this.userId);//没有此员工的记录:
			return ERROR;
		}
		
		LogUtil.log("userId="+this.userId);
		
		return SUCCESS;
	}
	
	//接收客户端传来的staffNo
	public void setUserId(String id) {
		this.userId = id;
	}
	
	private void setLoginData(DataLoginItem login) {
		this.loginData = login;
	}
	
	public DataLoginItem getLoginData() {
		return this.loginData;
	}
	
}
