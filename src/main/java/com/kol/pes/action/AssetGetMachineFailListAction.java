/*-----------------------------------------------------------

-- PURPOSE

--    获取设备故障类型的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetMachineFailItem;
import com.kol.pes.service.AssetCheckService;


public class AssetGetMachineFailListAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087712869585099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private List<DataAssetMachineFailItem> assetMachineFailList;//设备故障状态列表
	private String tag;//客户端上传的设备 tag number
	
	@Override
	@Action(value="/assetMachineFailList", results={
			@Result(name="success", location="asset_machine_fail_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setAssetMachineFailList(assetCheckService.getFailTypeList(tag));
		
		if(assetMachineFailList==null || assetMachineFailList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.getMachineFailTypeFail"));//获取设备故障列表失败
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	private void setAssetMachineFailList(List<DataAssetMachineFailItem> assetList) {
		this.assetMachineFailList = assetList;
	}
	
	public List<DataAssetMachineFailItem> getAssetMachineFailList() {
		return this.assetMachineFailList;
	}
	
}
