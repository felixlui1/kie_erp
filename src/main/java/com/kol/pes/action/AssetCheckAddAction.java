/*-----------------------------------------------------------

-- PURPOSE

--    添加点检记录的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.sql.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.AssetCheckService;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.Constants;
import com.kol.pes.utils.LogUtil;


public class AssetCheckAddAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087712369555099L;
	
	private String checkId;//点检id
	
	private Date creationDate;//点检时间
	private int createdBy;//点检人的staffNO
	
	private Date lastUpdateDate;//上次更新时间
	private int lastUpdatedBy;//最后一次操作的员工号
	
	private String assetId;//设备id
	private Date checkTime;//点检时间
	private int checkResult;//点检结果的代号
	
	private Date estRepairStart;//开始维修时间
	private Date estRepairEnd;//结束维修时间
	private String checkRemarks;//点检备注

	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	@Override
	@Action(value="/assetCheckAdd", results={
			@Result(name="success", location="asset_check_add.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		checkId = assetCheckService.getCheckId();//生成checkId
		
		LogUtil.log("checkId="+checkId);
		
		//插入点检数据
		int resRow = assetCheckService.insertCheck(checkId, creationDate, createdBy, 
												  lastUpdateDate, lastUpdatedBy, 
												  assetId, checkTime, checkResult, estRepairStart, estRepairEnd, 
												  checkRemarks);
		if(resRow>0) {//大于0代表插入成功
			setCode(CODE_SUCCESS);
		}else {
			setCode(CODE_FAIL);
			setMessage(getText("asset.addCheckAssetFail"));
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	//所有类似的函数，都是XML填值用的，框架自动调用
	public String getCheckId() {
		return checkId;
	}
	
	//所有类似的函数，都是接收客户端参数用的，框架自动调用
	public void setCreationDate(String creationDate) {
		this.creationDate = new Date(Long.valueOf(creationDate));
		
		if(Constants.IS_OPEN_LOG) {
			LogUtil.log("setCreationDate():"+CommonUtil.formatDateTime(this.creationDate));
		}
	}
	
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = new Date(Long.valueOf(lastUpdateDate));
	}
	
	public void setLastUpdatedBy(int lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	
	public void setCheckTime(String checkTime) {
		this.checkTime = new Date(Long.valueOf(checkTime));
	}
	
	public void setCheckResult(int checkResult) {
		this.checkResult = checkResult;
	}
	
	public void setEstRepairStart(String estRepairStart) {
		this.estRepairStart = new Date(Long.valueOf(estRepairStart));
	}
	
	public void setEstRepairEnd(String estRepairEnd) {
		this.estRepairEnd = new Date(Long.valueOf(estRepairEnd));
	}
	
	public void setCheckRemarks(String checkRemarks) {
		this.checkRemarks = checkRemarks;
	}
	
}
