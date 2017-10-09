/*-----------------------------------------------------------

-- PURPOSE

--    获取已开启工序的Action

-- History

--	  1-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataSeqStartedItem;
import com.kol.pes.service.SeqService;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


public class SeqStartedListAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private String lastUpdateByOrWipId;//登录员工的id或者是工单的id
	private List<DataSeqStartedItem> seqStartedList;//获取到的未完成工序列表
	
	private String isOpCompleted = "Y";//当前工序是否已经完成
	private String curWorkingOpCode = "";//当前正在加工的工序code
	
	
	@Override
	@Action(value="/seqStartedList", results={
			@Result(name="success", location="seq_started_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		seqStartedList = seqService.getSeqStartedList(lastUpdateByOrWipId);

		if(seqStartedList==null){
			seqStartedList = new ArrayList<DataSeqStartedItem>();
		}
		
		setCode(CODE_SUCCESS);
		setMessage(getText("op.getStartedOpFail"));//暂无相应的已开启工序
		
		if(CommonUtil.isValidNumber(lastUpdateByOrWipId)) {
			isOpCompleted = seqService.isCurrentOpCompletedAfterItemSelected(Integer.valueOf(lastUpdateByOrWipId)) ? "Y":"N";
			curWorkingOpCode = CommonUtil.noNullString(seqService.getCurReallyWorkingOpCode(Integer.valueOf(lastUpdateByOrWipId)));
		}
		
		LogUtil.log("lastUpdateByOrWipId="+lastUpdateByOrWipId+",isOpCompleted="+isOpCompleted+", curWorkingOpCode="+curWorkingOpCode);
		
		return SUCCESS;
	}
	
	public List<DataSeqStartedItem> getSeqStartedList() {
		return seqStartedList;
	}
	
	public void setLastUpdateByOrWipId(String lastUpdateByOrWipId) {
		this.lastUpdateByOrWipId = lastUpdateByOrWipId;
	}
	
	public String getIsOpCompleted() {
		return this.isOpCompleted;
	}
	
	public String getCurWorkingOpCode() {
		return this.curWorkingOpCode;
	}

}
