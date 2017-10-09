/*-----------------------------------------------------------

-- PURPOSE

--   接收质量收集计划的数据项并运行触发器的Action

-- History

--	  10-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataQaReqItem;
import com.kol.pes.service.QaService;
import com.kol.pes.utils.LogUtil;


public class QaInsertDataAction extends ParentAction {

	private static final long serialVersionUID = 8629087712669555898L;

	@Autowired
	@Qualifier("qaService")
	private QaService qaService;
	
	private String createStaffNo;//创建质量管理计划的员工
	private String wipEntityId;//工单id
	private String opCode;//工序的code
	private String transactionId;//
	private String qaNvList;//由质量管理计划键值对组成的字符串
	private String qaChildNvList;//质量管理计划子计划键值对组成的字符串
	private String isManualAddedQa = "N";//是否是手动添加的质量管理计划
	private String childPlanId = null;//子计划的id
	
	@Override
	@Action(value="/qaInsertData", results={
			@Result(name="success", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(qaService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		List<DataQaReqItem> qaList = getNvListByString(qaNvList);
		List<DataQaReqItem> qaChildList = getNvListByString(qaChildNvList);
		
		List<DataQaReqItem> res = qaService.runTriggersNeedRunForQaItems(transactionId, qaList, wipEntityId, opCode, "Y".equals(isManualAddedQa));//运行触发器
		List<DataQaReqItem> resChild = qaService.runTriggersNeedRunForQaItems(transactionId, qaChildList, wipEntityId, opCode, "Y".equals(isManualAddedQa));//运行触发器

		if(res!=null && resChild!=null) {//如果触发器运行无误，插入质量管理数据
			
			int inserRows = qaService.insertQaItems(transactionId, wipEntityId,
													new Date(Calendar.getInstance().getTimeInMillis()), 
													createStaffNo, 
													new Date(Calendar.getInstance().getTimeInMillis()), 
													createStaffNo, opCode, false,
													getTriResAndInputQaListTogether(res,qaList));
			
		    int inserChildRows = qaService.insertQaItems(transactionId,
														new Date(Calendar.getInstance().getTimeInMillis()), 
														createStaffNo, 
														new Date(Calendar.getInstance().getTimeInMillis()), 
														createStaffNo,
														getTriResAndInputQaListTogether(resChild,qaChildList), childPlanId);
			if(inserRows>0 && inserChildRows>0) {
				setCode(CODE_SUCCESS);
				return SUCCESS;
			}
			else {
				setCode(CODE_FAIL);
				setMessage(getText("qa.insertQaDataFail"));//插入质量管理数据失败
				return ERROR;
			}
		}
		else {
			setCode(CODE_FAIL);
			setMessage(getText("qa.runQaTriggersFail"));//运行质量管理数据触发条件失败
			return ERROR;
		}
	}
	
	//把运行触发器得到的结果和输入的质量管理计划数据合到一起
	private List<DataQaReqItem> getTriResAndInputQaListTogether(List<DataQaReqItem> triResList, List<DataQaReqItem> reqList) {
		List<DataQaReqItem> tempList = new ArrayList<DataQaReqItem>();
		
		tempList.addAll(reqList);
		
		if(triResList!=null && triResList.size()>0) {
			for(DataQaReqItem item : triResList) {
				if(!isReqResContainsTheTriItem(tempList, item)) {//在数据数据中不包含才加入
					tempList.add(item);
				}
			}
		}
		
		return tempList;
	}
	
	//判断触发器得到质量管理数据是否在输入中已经包含
	private boolean isReqResContainsTheTriItem(List<DataQaReqItem> reqResList, DataQaReqItem item) {
		if(reqResList!=null && item!=null) {
			for(DataQaReqItem d : reqResList) {
				if(d!=null && d.resultColumnName!=null && d.resultColumnName.equals(item.resultColumnName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setChildPlanId(String childPlanId) {
		this.childPlanId = childPlanId;
	}
	
	public void setIsManualAddedQa(String isManualAddedQa) {
		this.isManualAddedQa = isManualAddedQa;
	}
	
	public void setCreateStaffNo(String createStaffNo) {
		this.createStaffNo = createStaffNo;
	}
	
	public void setWipEntityId(String wipEntityId) {
		this.wipEntityId = wipEntityId;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public void setQaNvList(String qaNvList) {
		this.qaNvList = qaNvList;
	}
	
	public void setQaChildNvList(String qaChildNvList) {
		this.qaChildNvList = qaChildNvList;
	}
	
	private List<DataQaReqItem> getNvListByString(String qaNvListString) {
		
		LogUtil.log("qaNvListString="+qaNvListString);
		
		List<DataQaReqItem> qaReqList = new ArrayList<DataQaReqItem>();
		
		if(qaNvListString!=null && qaNvListString.contains(",")) {
			String[] nvListArr = qaNvListString.split("@");
			for(String nv : nvListArr) {
				String[] nvArr = nv.split(",");
				DataQaReqItem qaItem = new DataQaReqItem();
				qaItem.charId = nvArr[0];
				qaItem.resultColumnName = nvArr[1];
				if(nvArr.length >=3) {
					qaItem.value = nvArr[2];
				}
				else {
					qaItem.value = "";
				}
				qaReqList.add(qaItem);
			}
		}
		
		return qaReqList;
	}

}
