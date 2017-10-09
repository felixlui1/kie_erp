/*-----------------------------------------------------------

-- PURPOSE

--    获取点检设备列表的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetCheckItem;
import com.kol.pes.service.AssetCheckService;


public class AssetGetCheckListAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087712869555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private List<DataAssetCheckItem> assetList;//点检的设备列表
	
	@Override
	@Action(value="/assetGetCheckList", results={
			@Result(name="success", location="asset_check_error_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setAssetList(assetCheckService.findAssetWithError());
		
		if(assetList==null || assetList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.getAssetListNone"));//暂无存在故障的设备
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	private void setAssetList(List<DataAssetCheckItem> assetList) {
		this.assetList = assetList;
	}
	
	public List<DataAssetCheckItem> getAssetList() {
		return this.assetList;
	}
	
}
