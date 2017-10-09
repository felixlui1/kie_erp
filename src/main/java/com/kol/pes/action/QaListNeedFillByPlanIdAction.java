/*-----------------------------------------------------------

-- PURPOSE

--    登录的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.service.QaService;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


public class QaListNeedFillByPlanIdAction extends ParentAction {

	private static final long serialVersionUID = 2124087712369555098L;

	@Autowired
	@Qualifier("qaService")
	private QaService qaService;
	private String planId;//质量管理计划的id
	private List<DataQaNeedFillItem> qaList;//根据id获取到的质量管理计划
	
	private String wipId;//工单id
	private String opCode;//工序code
	private List<String> incompleteQuanStartEndTime;//最大可投入数，未完成工序的最早开启时间，最晚完成时间
	
	@Override
	@Action(value="/qaListByPlanId", results={
			@Result(name="success", location="qa_list_by_plan_id.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(qaService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		qaList = qaService.getQaListToFillByPlanId(this.planId, opCode);
		incompleteQuanStartEndTime = qaService.getIncompleteQuanStartEndTime(wipId, opCode);

		if(qaList==null || qaList.size()==0) {
			setCode(CODE_SUCCESS);
			setMessage(getText("qa.noQaListData"));//没有质量收集计划
			return ERROR;
		}
		
		LogUtil.log("planId="+planId);
		
		return SUCCESS;
	}
	
	public List<DataQaNeedFillItem> getQaList() {
		return qaList;
	}
	
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setWipId(String wipId) {
		this.wipId = wipId;
	}
	
	public String getIncompleteQuan() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(0));
		}
		return "";
	}
	
	public String getMinStartTime() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(1));
		}
		return "";
	}
	
	public String getMaxEndTime() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(2));
		}
		return "";
	}
}
