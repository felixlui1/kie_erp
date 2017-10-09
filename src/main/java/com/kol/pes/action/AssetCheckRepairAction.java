/*-----------------------------------------------------------

-- PURPOSE

--    更新维修信息的Action

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


public class AssetCheckRepairAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087718869555099L;
	
	private String checkId;//点检id
	
	private Date lastUpdateDate;//上次更新时间
	private int lastUpdatedBy;//上次操作员工

	private Date actRepairStart;//实际开始修理时间
	private Date actRepairEnd;//实际结束修理时间
	private String repairRemarks;//备注
	
	private int failureCode;//故障代码
	private int downTime;//停机时长
	
	private String opCode;//故障代码
	
	private String[] changedParts;//更换的部件

	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	@Override
	@Action(value="/assetCheckRepair", results={
			@Result(name="success", location="asset_check_add.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		//更新点检表格，加入维修字段的数据
		int resRow = assetCheckService.updateCheckRepair(checkId, lastUpdateDate, lastUpdatedBy, 
														actRepairStart, actRepairEnd, downTime, 
														repairRemarks, failureCode);
		
		if(changedParts!=null && changedParts.length>0) {
			for(String part : changedParts) {
				assetCheckService.assetCheckInsertChangedParts(checkId, part, String.valueOf(lastUpdatedBy), opCode);
			}
		}
		
		if(resRow>0) {
			setCode(CODE_SUCCESS);
		}else {
			setCode(CODE_FAIL);
			setMessage(getText("asset.saveRepairInfoFail"));//保存维修信息失败
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setCheckId(String id) {
		checkId = id;
	}
	
	public String getCheckId() {
		return checkId;
	}
	
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = new Date(Long.valueOf(lastUpdateDate));
	}
	
	public void setLastUpdatedBy(int lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public void setActRepairStart(String estRepairStart) {
		this.actRepairStart = new Date(Long.valueOf(estRepairStart));
	}
	
	public void setActRepairEnd(String estRepairEnd) {
		this.actRepairEnd = new Date(Long.valueOf(estRepairEnd));
	}
	
	public void setRepairRemarks(String repairRemarks) {
		this.repairRemarks = repairRemarks;
	}
	
	public void setFailureCode(int failureCode) {
		this.failureCode = failureCode;
	}
	
	public void setDownTime(int downTime) {
		this.downTime = downTime;
	}
	
	public void setChangedParts(String parts) {
		this.changedParts = (parts!=null && parts.length()>0) ? parts.split("@") : null;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
}
