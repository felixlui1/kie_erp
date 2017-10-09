/*-----------------------------------------------------------

-- PURPOSE

--    开始工序的Action

-- History

--	  22-Oct-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.SeqService;
import com.kol.pes.utils.LogUtil;


public class SeqStartAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private String transactionId;
	private String createdBy;//创建工序的员工
	private String lastUpdatedBy;//最后一次操作的员工
	private int wipEntityId;//工单id
	private String wipEntityName;//工单名称
	private String fmOperationCode;//工序code
	private String curOperationId;//工序id
	private String trxQuantity;//投入数
	private String assetId1;//加工设备1
	private String assetId2;
	private String assetId3;
	private Date opStart;//工序开始时间
	
	private int seqId;//工序id
	private boolean canJump;//是否可以回跳工序
	
	
	@Override
	@Action(value="/seqStart", results={
			@Result(name="success", location="seq_start.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
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
		
		//由于最大可投入数时可能随时变化的，所以每次开始一个工序时，都临时获取一次最新的最大可投入数
		int maxQuan = seqService.getLeftCountForStartOp(wipEntityName, wipEntityId, seqId, fmOperationCode, curOperationId, canJump);
		
		if(Integer.valueOf(trxQuantity.trim()) > maxQuan) {//当输入的投入数大于最大可投入数时
			setCode(CODE_FAIL);
			setMessage(getText("op.inputQuanLargerThanMaxQuan"));//投入数大于剩余数
			return ERROR;
		}
		
		transactionId = seqService.getTransactionId();//生成transactionId
		
		int errCodeRes = seqService.runProcedureBeforeStartOp(wipEntityId, fmOperationCode);
		LogUtil.log("runProcedureBeforeStartOp:errCodeRes="+errCodeRes);
		
		if(0 == errCodeRes) {
			int rows = seqService.insertWhenStartAnOp(transactionId, new Date(Calendar.getInstance().getTimeInMillis()-1000*60), createdBy,
														new Date(Calendar.getInstance().getTimeInMillis()-1000*60), lastUpdatedBy, 
														wipEntityId, fmOperationCode, 
														trxQuantity, "0", 
														assetId1,assetId2,assetId3, opStart, seqId, canJump);
			if(rows<=0) {
				setCode(CODE_FAIL);
				setMessage(getText("op.startOpFail"));//开启工序失败
				return ERROR;
			}
			
			//int startedRes = seqService.runProcedureAfterStartOp(wipEntityId, fmOperationCode);
			//LogUtil.log("transactionId="+transactionId+", startedRes="+startedRes);
			
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
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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
	
	public void setTrxQuantity(String trxQuantity) {
		this.trxQuantity = trxQuantity;
	}
	
	public void setAssetId1(String assetId1) {
		this.assetId1 = assetId1;
	}
	
	public void setAssetId2(String assetId2) {
		this.assetId2 = assetId2;
	}
	
	public void setAssetId3(String assetId3) {
		this.assetId3 = assetId3;
	}
	
	public void setOpStart(String opStart) {
		this.opStart = new Date(Long.valueOf(opStart));
	}
	
	public void setSeqId(String seqId) {
		this.seqId = Integer.valueOf(seqId);
	}

	public void setCanJump(String canJump) {
		this.canJump = "Y".equals(canJump)?true:false;
	}
}
