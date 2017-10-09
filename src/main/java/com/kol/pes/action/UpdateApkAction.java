/*-----------------------------------------------------------

-- PURPOSE

--    升级APK的Action

-- History

--	  17-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.DataEnableService;
import com.kol.pes.utils.Constants;


public class UpdateApkAction extends ParentAction {

	private static final long serialVersionUID = 8828087712669555098L;
	
	@Autowired
	@Qualifier("qaService")
	private DataEnableService dataEnableService;
	
	private int NEWEST_APK_VERSION = 1;//服务器上的版本号
	private String updateMsg = "";//更新版本的说明
	private String IS_FORCE_UPDATE = "N";//是否强制升级
	
	private int apkPadVersion = 0;//客户端上传的版本号
	
	private String needUpdate = "N";//是否需要升级
	private final String UPDATE_APK_URL = Constants.IS_USE_TEST ? "http://192.168.38.49:8080/kol_pes_apks/kol_pes_latest.apk":"http://192.168.38.50:8080/kol_pes_apks/kol_pes_latest.apk";//升级的连接
	
	
	@Override
	@Action(value="/updateApk", results={
			@Result(name="success", location="update_apk.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		NEWEST_APK_VERSION = dataEnableService.getLatestApkVersionNumber();
		updateMsg = dataEnableService.getApkUpdateMsg();
		IS_FORCE_UPDATE = dataEnableService.getApkIsForceUpdate();
		
		if(apkPadVersion < NEWEST_APK_VERSION) {
			needUpdate = "Y";
			//updateMsg = getText("update.updateApkChanges");//优化部分界面流程
		}
		
		return SUCCESS;
	}
	
	//接收客户端传来的staffNo
	public void setApkPadVersion(String v) {
		this.apkPadVersion = Integer.valueOf(v);
	}
	
	public String getNeedUpdate() {
		return needUpdate;
	}

	public String getUpdateApkUrl() {
		return this.UPDATE_APK_URL;
	}
	
	public String getUpdateMsg() {
		return this.updateMsg;
	}
	
	public String getIsForceUpdate() {
		return IS_FORCE_UPDATE;
	}
	
}
