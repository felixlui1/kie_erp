/*-----------------------------------------------------------

-- PURPOSE

--    取消设备点检的Action

-- History

--	  11-Aug-16  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.AssetCheckService;


public class AssetCheckCancelAssetCheckAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087762869585099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private String assetId;//设备的id
	
	@Override
	@Action(value="/assetCheckCancelAssetCheck", results={
			@Result(name="success", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		final boolean isDelete = assetCheckService.assetCheckCancelAssetCheck(assetId);

		if(isDelete) {
			setMessage(getText("asset.assetCheckCancelAssetCheckSuccessPart1")+assetId+getText("asset.assetCheckCancelAssetCheckSuccessPart2"));//已取消[扫描取得的号]在本班次的检点安排
			return SUCCESS;
		}
		else {
			setCode(CODE_FAIL);
			setMessage(getText("asset.assetCheckCancelAssetCheckFail"));//此设备在本次不需点检或无效点检号
			return ERROR;
		}
	}
	
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
}
