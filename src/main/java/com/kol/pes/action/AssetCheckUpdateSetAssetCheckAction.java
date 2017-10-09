/*-----------------------------------------------------------

-- PURPOSE

--    获取选择点检设备列表数据的Action

-- History

--	  25-Feb-16  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;


import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetCheckSetAssetCheckItem;
import com.kol.pes.service.AssetCheckService;


public class AssetCheckUpdateSetAssetCheckAction extends ParentAction {

	
	private static final long serialVersionUID = 5526087712866555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private Date checkDate;
	private int shift;
	private String staffNo;
	private List<DataAssetCheckSetAssetCheckItem> assetList;
	
	@Override
	@Action(value="/assetCheckUpdateSetAssetCheckList", results={
			@Result(name="success", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		assetCheckService.assetCheckUpdateAssetCheckList(checkDate, shift, staffNo, assetList);
		
		if(assetList==null || assetList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.noCheckItem"));//暂无待点检项目
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public void setDate(String date) {
		checkDate = new Date(Long.valueOf(date));
	}
	
	public void setShift(String shift) {
		this.shift = Integer.valueOf(shift);
	}
	
	public void setStaffNo(String no) {
		this.staffNo = no;
	}
	
	public void setAssetList(String listString) {
		this.assetList = getAssetCheckListByString(listString);
	}
	
	private List<DataAssetCheckSetAssetCheckItem> getAssetCheckListByString(String listString) {
		
		List<DataAssetCheckSetAssetCheckItem> checkResultList = new LinkedList<DataAssetCheckSetAssetCheckItem>();
		
		if(listString!=null && listString.contains(",")) {

			String[] nvListArr = listString.split("@");
			
			for (String nv : nvListArr) {
				String[] nvArr = nv.split(",");
				
				DataAssetCheckSetAssetCheckItem checkItem = new DataAssetCheckSetAssetCheckItem();
				
				checkItem.assetId = Integer.valueOf(nvArr[0]);
				checkItem.assetTagNum = nvArr[1];
				checkItem.assetDescription = nvArr[2];
				checkItem.scheduledId = nvArr[3];
				checkItem.checkedId = nvArr[4];
				
				checkResultList.add(checkItem);
			}
		}
		
		return checkResultList;
	}
	
}
