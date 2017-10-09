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

import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.service.AssetCheckService;


public class AssetCheckGetSetAssetCheckAction extends ParentAction {

	private static final long serialVersionUID = 5526087712866555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private Date date;
	private int shift;
	private String staffNo;
	private List<DataAssetCheckSetAssetCheckItem> assetList;
	
	@Override
	@Action(value="/assetCheckGetSetAssetCheckList", results={
			@Result(name="success", location="asset_check_set_asset_check_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus() != STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setAssetList(assetCheckService.assetCheckGetSetAssetCheckList(date, shift, staffNo));
		
		if(assetList==null || assetList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.noCheckItem"));//暂无待点检项目
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setDate(String date) {
		this.date = new Date(Long.valueOf(date));;
	}
	
	public void setShift(String s) {
		this.shift = Integer.valueOf(s);
	}
	
	public void setStaffNo(String s) {
		this.staffNo = s;
	}
	
	private void setAssetList(List<DataAssetCheckSetAssetCheckItem> assetList) {
		this.assetList = assetList;
	}
	
	public List<DataAssetCheckSetAssetCheckItem> getAssetList() {
		return this.assetList;
	}
	
}
