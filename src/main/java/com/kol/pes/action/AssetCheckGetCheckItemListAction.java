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

import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.service.AssetCheckService;
import com.kol.pes.utils.LogUtil;


public class AssetCheckGetCheckItemListAction extends ParentAction {
	
	private static final long serialVersionUID = 5526087712866555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private String assetId;
	private List<DataAssetCheckCheckItem> checkItemList;//点检设备界面要显示的点检项目列表
	
	private String assetLastCheckTime;
	
	@Override
	@Action(value="/assetCheckGetCheckItemList", results={
			@Result(name="success", location="asset_check_check_item_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		LogUtil.log("assetCheckGetCheckItemList:this.assetId="+this.assetId);
		this.setCheckItemList(assetCheckService.getAssetCheckCheckItemList(this.assetId));
		
		this.assetLastCheckTime = assetCheckService.getAssetCheckLastCheckTimeOfAsset(this.assetId);
		
		if(checkItemList==null || checkItemList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.noCheckItem"));//暂无待点检项目
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setAssetId(String id) {
		this.assetId = id;
	}
	
	private void setCheckItemList(List<DataAssetCheckCheckItem> assetList) {
		this.checkItemList = assetList;
	}
	
	public List<DataAssetCheckCheckItem> getCheckItemList() {
		return this.checkItemList;
	}
	
	public String getAssetLastCheckTime() {
		return this.assetLastCheckTime;
	}
	
}
