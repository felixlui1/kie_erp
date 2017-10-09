/*-----------------------------------------------------------

-- PURPOSE

--    插入上传图片路径的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.AssetCheckService;
import com.kol.pes.utils.LogUtil;


public class AssetInsertPicPathAction extends ParentAction {

	private static final long serialVersionUID = 4426087712369555099L;
	
	private String checkId;//如果是完成工序传过来的照片，则是transId
	private String picPathDescList;
	private String isEnd = "N";
	private String isSeq = "N";

	@Autowired
	@Qualifier("assetCheckService")
	private AssetCheckService assetCheckService;
	
	@Override
	@Action(value="/assetInsertPicPath", results={
			@Result(name="success", location="asset_insert_pic_path.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(assetCheckService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		LogUtil.log("checkId="+checkId);

		if(picPathDescList!=null && picPathDescList.contains(",")) {//现在上传的照片信息中可以带照片描述，所以代码上还是按这个功能实现
			String[] tempArr = picPathDescList.split("#");
			int picResRow = assetCheckService.insertPicPathDesc(checkId, picPathDescList, "Y".equals(isEnd), "Y".equals(isSeq));
			
			LogUtil.log("picResRow="+picResRow+", tempArr.length="+tempArr.length);
			
			if(picResRow>0 && picResRow>=tempArr.length) {
				setCode(CODE_SUCCESS);
			}else {
				setCode(CODE_FAIL);
				setMessage(getText("asset.addCheckAssetPictureFail"));//添加点检图片失败
				return ERROR;
			}
		}else {
			setCode(CODE_SUCCESS);
		}
		
		return SUCCESS;
	}
	
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	
	public void setPicPathDescList(String picPathDescList) {
		this.picPathDescList = picPathDescList;
	}
	
	public void setIsEnd(String isEnd) {
		this.isEnd = isEnd;
	}
	
	public void setIsSeq(String isSeq) {
		this.isSeq = isSeq;
	}
	
}
