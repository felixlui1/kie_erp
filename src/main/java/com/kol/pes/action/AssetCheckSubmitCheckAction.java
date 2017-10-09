package com.kol.pes.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataAssetCheckCheckItem;
import com.kol.pes.service.AssetCheckService;
import com.kol.pes.utils.LogUtil;

public class AssetCheckSubmitCheckAction extends ParentAction {

	private static final long serialVersionUID = 5526087712866555099L;
	
	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	private String assetId;
	private String userId;

	private List<DataAssetCheckCheckItem> checkValueList;//点检设备界面要显示的点检项目列表
	
	@Override
	@Action(value="/assetCheckSubmitCheck", results={
			@Result(name="success", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		int sucCount = 0;
		
		final String checkId = assetCheckService.assetCheckGetCheckId();

		if(checkValueList!=null && checkValueList.size()>0) {

			for (DataAssetCheckCheckItem checkValueItem : checkValueList) {
				
//				String hadCheckedData = assetCheckService.getAssetCheckResultFromHistory(assetId, checkValueItem.itemSeq, checkValueItem.chkCycle);
//				LogUtil.log("hadCheckedData="+hadCheckedData);
//				if(hadCheckedData != null) {
//					sucCount = sucCount + assetCheckService.assetCheckUpdateCheck(assetId, userId, checkValueItem, checkValueItem.chkCycle);
//				}
//				else {
					sucCount = sucCount + assetCheckService.assetCheckSubmitCheck(assetId, checkId, userId, checkValueItem);
				//}
			}
		}
		
		if(sucCount < checkValueList.size()) {
			setCode(CODE_FAIL);
			setMessage(getText("asset.submitCheckFail"));//提交点检信息失败
			return ERROR;
		}
		
		assetCheckService.runProcedureAfterInsertCheck(checkId);
		
		return SUCCESS;
	}
	
	public void setAssetId(String id) {
		this.assetId = id;
	}
	
//	public void setchkCycle(String id) {
//		this.chkCycle = Integer.valueOf(id);
//	}
	
	public void setUserId(String id) {
		this.userId = id;
	}
	
	public void setCheckValueListString(String value) {
		checkValueList = getCheckListByString(value);
	}
	
	private List<DataAssetCheckCheckItem> getCheckListByString(String listString) {
		
		LogUtil.log("getCheckListByString():listString="+listString);
		List<DataAssetCheckCheckItem> checkResultList = new ArrayList<DataAssetCheckCheckItem>();
		
		if(listString!=null && listString.contains("~")) {
			
			String[] nvListArr = listString.split("@");
			
			for (String nv : nvListArr) {
				String[] nvArr = nv.split("~");
				DataAssetCheckCheckItem checkItem = new DataAssetCheckCheckItem();
				checkItem.itemSetId = nvArr[0];
				checkItem.itemSeq = nvArr[1];
				checkItem.queryText = nvArr[2];
				checkItem.queryType = nvArr[3];
				checkItem.checkResult = nvArr[4];
				checkItem.chkCycle = Integer.valueOf(nvArr[5]);
				
				checkResultList.add(checkItem);
			}
		}
		
		return checkResultList;
	}
	
}
