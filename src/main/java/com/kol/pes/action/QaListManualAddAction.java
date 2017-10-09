/*-----------------------------------------------------------

-- PURPOSE

--   获取人工添加的质量收集计划数据项的Action

-- History

--	  03-Dec-14  LiZheng  Created.

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


public class QaListManualAddAction extends ParentAction {

	private static final long serialVersionUID = 8629087702389555898L;

	@Autowired
	@Qualifier("qaService")
	private QaService qaService;
	
	private String wipId;//工单id
	private String opCode;//工序code
	
	private List<DataQaNeedFillItem> qaList;//返回的质量管理计划
	private List<String> incompleteQuanStartEndTime;//获取的最大可投入数、最早工序开始时间、最迟工序结束时间
	
	@Override
	@Action(value="/qaListManual", results={
			@Result(name="success", location="qa_list_manual_add.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(qaService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		qaList = qaService.getQaListByManual(opCode);
		incompleteQuanStartEndTime = qaService.getIncompleteQuanStartEndTime(wipId, opCode);
		
		if(qaList==null || qaList.size()==0) {
			setCode(CODE_SUCCESS);
			setMessage(getText("qa.noQaListData"));//没有质量收集计划
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public List<DataQaNeedFillItem> getQaList() {
		return qaList;
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
