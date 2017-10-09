/*-----------------------------------------------------------

-- PURPOSE

--    开始工序的Action

-- History

--	  22-Oct-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.sql.Date;
import java.util.Calendar;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataSeqProcedureItem;
import com.kol.pes.service.QaService;
import com.kol.pes.service.SeqService;
import com.kol.pes.utils.LogUtil;


public class SeqEndAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	@Autowired
	@Qualifier("qaService")
	private QaService qaService;
	
	private String transactionId;
	private int scrapQuantity;//坏品数量
	private Date opEnd;//完成工序时间
	
	private String lastUpdatedBy;//最后一次更新的员工
	
	private String organizationId;
	private String wipId;//工单id
	private String opCode;//工序code
	
	private boolean canJump;//是否有权限回跳工序
	
	@Override
	@Action(value="/seqEnd", results={
			@Result(name="success", location="seq_end.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		LogUtil.log("SeqEndAction:transactionId="+transactionId);
		
		boolean isQaFilled = seqService.isQaFilledWhenEndingAnOp(transactionId);
		boolean isLastSeq = seqService.isLastUncompleteOpNumForWip(wipId, opCode, canJump);
		String planId = qaService.getPlanIdByOpCode(opCode, organizationId, false);
		
		LogUtil.log("planId="+planId+", isLastSeq="+isLastSeq+", isQaFilled="+isQaFilled);
		
		if(planId!=null && isLastSeq && !isQaFilled) {
			setCode(CODE_FAIL);
			setMessage(getText("op.thisOpIsTheLastNeedFillQaData"));//此工序是最后一个未完成工序,请填写必要的质量收集计划
			return ERROR;
		}
		
		int rows = seqService.updateWhenEndAnOp(transactionId, scrapQuantity, new Date(Calendar.getInstance().getTimeInMillis()), lastUpdatedBy, new Date(Calendar.getInstance().getTimeInMillis()));
		
		if(rows<=0) {
			setCode(CODE_FAIL);
			setMessage(getText("op.endOpFail"));//完成工序失败
			return ERROR;
		}
		else {
			DataSeqProcedureItem resErrData = seqService.runSQLNoticeJobStatus(transactionId);
			if(resErrData.getErrCode() == 0) {
				return SUCCESS;
			}
			else {//需要将完成的工序改成未完成，即 op_end置空

//				if(resErrData.getErrCode() == 2) {
//					setMessage(getText("op.endOpFail")+": "+resErrData.getErrMsg());//完成工序失败
//				}
//				else if(resErrData.getErrCode() == 3) {
//					setMessage(getText("op.endOpFailLastOpNotUpload"));//上一个工序还未完成上传
//				}
//				else if(resErrData.getErrCode() == 4) {
//					setMessage(getText("op.endOpFailThisOpAlreadyInStock"));//此工单已经完工入库,请在Oracle先退仓
//				}
//				else if(resErrData.getErrCode() == 5) {
//					setMessage(getText("op.endOpFailThisOpCanNotEndAgain"));//同一个工序不能重复
//				}
//				else if(resErrData.getErrCode() == 1) {
//					setMessage("工序开始上传还未完成");//工序开始上传还未完成
//				}
//				else {
//					setMessage(getText("op.endOpFail"));//完成工序失败
//				}
//				
//				try {
//					int trxId = seqService.getOpTrxId(transactionId);
//					if(trxId > 0) {
//						setMessage("这个工序已经上传了,网络现在比较慢,不要重复按完成工序键");
//					}
//					else {
//						int rowBackRows = seqService.updateResetOpEndedToNull(transactionId);
//						if(rowBackRows>0) {//如果存储过程运行失败，且回滚end_op成功，则相当于完成工序失败.在此返回0告诉action
//							
//						}
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				int rowBackRows = seqService.updateResetOpEndedToNull(transactionId);
				if(rowBackRows>0) {//如果存储过程运行失败，且回滚end_op成功，则相当于完成工序失败.在此返回0告诉action
					
				}
				
				setMessage("网络不稳定，如果发现工序显示不正常，请通知电脑部检查完成状态");
				
				setCode(CODE_FAIL);
				return ERROR;
			}
		}
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	public void setWipId(String wipId) {
		this.wipId = wipId;
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setScrapQuantity(String scrapQuantity) {
		this.scrapQuantity = Integer.valueOf(scrapQuantity);
	}
	
	public void setOpEnd(String opEnd) {
		this.opEnd = new Date(Long.valueOf(opEnd));
	}
	
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public void setCanJump(String canJump) {
		this.canJump = "Y".equals(canJump)?true:false;
	}
}
