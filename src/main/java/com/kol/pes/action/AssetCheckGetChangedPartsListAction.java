/*-----------------------------------------------------------

-- PURPOSE

--    获取选择点检设备列表数据的Action

-- History

--	  25-Feb-16  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.AssetCheckService;



public class AssetCheckGetChangedPartsListAction extends ParentAction {

	
	private static final long serialVersionUID = 5526087712866555099L;
	
	private String opCode;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private List<String> changedPartsList;
	
	@Override
	@Action(value="/assetCheckGetChangedPartsList", results={
			@Result(name="success", location="asset_check_changed_parts_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.changedPartsList = assetCheckService.assetCheckGetChangedPartsHistoryList(this.opCode);
		
		if(changedPartsList==null || changedPartsList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.noChangedPartsHistory"));//暂无更换部件记录
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setOpCode(String code) {
		this.opCode = code;
	}
	
	public List<String> getChangedPartsList() {
		return this.changedPartsList;
	}
	
}
