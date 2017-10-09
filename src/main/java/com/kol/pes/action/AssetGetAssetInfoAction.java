/*-----------------------------------------------------------

-- PURPOSE

--    获取设备故障类型的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetInfoItem;
import com.kol.pes.service.AssetCheckService;


public class AssetGetAssetInfoAction extends ParentAction {

	
	private static final long serialVersionUID = 4426087712869585099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private DataAssetInfoItem assetInfo;//点检设备信息数据类
	private String tagNumber;//设备的tag number
	
	@Override
	@Action(value="/assetGetAssetInfo", results={
			@Result(name="success", location="asset_info.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setAssetInfo(assetCheckService.getAssetInfo(tagNumber));//根据tagNumber获取设备信息数据
		
		if(assetInfo==null) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.getAssetInfoFail"));//获取设备信息失败
			
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setTagNumber(String tagNumber) {
		this.tagNumber = tagNumber;
	}
	
	private void setAssetInfo(DataAssetInfoItem assetInfo) {
		this.assetInfo = assetInfo;
	}
	
	//框架会使用这个封装的数据对象去渲染XML
	public DataAssetInfoItem getAssetInfo() {
		return this.assetInfo;
	}
	
}
