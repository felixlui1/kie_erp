/*-----------------------------------------------------------

-- PURPOSE

--    开始工序前检测是否允许开始的Action

-- History

--	  30-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.SeqService;
import com.kol.pes.utils.LogUtil;


public class SeqCheckBeforeStartAction extends ParentAction {

	private static final long serialVersionUID = 8826087712369565098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private int wipEntityId;//工单id
	private String wipEntityName;//工单名称
	private String fmOperationCode;//工序code
	private String curOperationId;//当前正在加工/应该加工的工序

	private int seqId;//工序id
	
	private String timeBufferOpStart;//开始工序时间可以提前的最大分钟数
	
	
	@Override
	@Action(value="/seqCheckBeforeStart", results={
			@Result(name="success", location="check_before_op_start.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		timeBufferOpStart = seqService.getTimeBufferForOpStart();
		
		if("-1".equals(curOperationId)) {
			setCode(CODE_FAIL);
			setMessage(getText("op.thisJobAlreadydone"));//该工单已经完成，要返工就请先在Oracle进行撤销
			return ERROR;
		}
		
		//在开始一个新的不同工序前，判断当前cur_operation_id对应的工序是否全数完成，如没有则不能开启新工序
		if(!seqService.isCurrentOpCompletedBeforeStartTheNewOne(wipEntityName, wipEntityId, seqId, curOperationId, fmOperationCode)) {
			setCode(CODE_FAIL);
			setMessage(getText("op.canNotStartTheNewOpWhenCurOpNotCompleted"));//之前的工序尚未全数加工完成，不能开启新的不同工序
			return ERROR;
		}
		
		//在开始一个工序前，判断是否有这个工单的其它工序还没有加工完，如果有，则不能启动一个新的不同工序
		String notEndedOpCode = seqService.isOtherOpNotEndBeforeStartTheNewOne(wipEntityId, fmOperationCode);
		if(notEndedOpCode != null) {
			setCode(CODE_FAIL);
			setMessage(notEndedOpCode+getText("op.canNotStartTheNewOpWhenOtherOpNotEnded"));//工序尚未加工完成，不能开启新的不同工序
			return ERROR;
		}
		
		int errCodeRes = seqService.runProcedureBeforeStartOp(wipEntityId, fmOperationCode);
		LogUtil.log("runProcedureBeforeStartOp:errCodeRes="+errCodeRes);
		
		if(0 == errCodeRes) {
			return SUCCESS;
		}
		else if(2 == errCodeRes) {
			setCode(CODE_FAIL);
			setMessage(getText("op.endedOpCanNotBeRestarted"));//此工序已完成不能马上重开
			return ERROR;
		}
		else if(3 == errCodeRes) {
			setCode(CODE_FAIL);
			setMessage(getText("op.opCanNotStartWhenUploading"));//上一道工序正在上传，请稍候再试，如超过5分钟仍未成功，请通知IT部门处理
			return ERROR;
		}
		else {
			setCode(CODE_FAIL);
			setMessage(getText("op.opCanNotStartJustNow"));//暂时不能开启工序,请通知IT部门处理
			return ERROR;
		}
	}
	
	public String getTimeBufferOpStart() {
		return timeBufferOpStart;
	}
	
	public void setWipEntityId(String wipEntityId) {
		this.wipEntityId = Integer.valueOf(wipEntityId.trim());
	}
	
	public void setWipEntityName(String wipEntityName) {
		this.wipEntityName = wipEntityName;
	}
	
	public void setFmOperationCode(String fmOperationCode) {
		this.fmOperationCode = fmOperationCode;
	}
	
	public void setCurOperationId(String curOperationId) {
		this.curOperationId = curOperationId;
	}
	
	public void setSeqId(String seqId) {
		this.seqId = Integer.valueOf(seqId);
	}
}
