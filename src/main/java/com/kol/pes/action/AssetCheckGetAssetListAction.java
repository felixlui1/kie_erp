/*-----------------------------------------------------------

-- PURPOSE

--    获取选择点检设备列表数据的Action

-- History

--	  25-Feb-16  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.sql.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetCheckAssetListItem;
import com.kol.pes.service.AssetCheckService;


public class AssetCheckGetAssetListAction extends ParentAction {

	
	private static final long serialVersionUID = 5526087712866555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private Date startDate;
	private Date endDate;
	private List<DataAssetCheckAssetListItem> assetList;
	
	@Override
	@Action(value="/assetCheckGetAssetList", results={
			@Result(name="success", location="asset_check_asset_item_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.assetList = assetCheckService.assetCheckGetAssetList(startDate, endDate);
		
		if(assetList==null || assetList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.noCheckItem"));//暂无待点检项目
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setStartDate(String date) {
		this.startDate = new Date(Long.valueOf(date));
	}
	
	public void setEndDate(String date) {
		this.endDate = new Date(Long.valueOf(date));
	}
	
	public List<DataAssetCheckAssetListItem> getAssetItemList() {
		return this.assetList;
	}
	
}
