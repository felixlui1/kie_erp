/*-----------------------------------------------------------

-- PURPOSE

--    删除一个已经开始的工序的Action

-- History

--	  30-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.SeqService;


public class SeqDeleteStartedAction extends ParentAction {

	private static final long serialVersionUID = 8826086812369565098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private String transactionId;
	
	
	@Override
	@Action(value="/seqDeleteOp", results={
			@Result(name="success", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		int trxId = seqService.getOpTrxId(transactionId);
		if(trxId > 0) {
			setMessage("这个工序已经上传了,网络现在比较慢,不要删除这个工序");
			return ERROR;
		}
		else {
			if(seqService.deleteOpByTransId(transactionId)==1) {
				return SUCCESS;
			}else {
				setCode(CODE_FAIL);
				setMessage(getText("op.deleteOpFail"));//删除工序失败
				return ERROR;
			}
		}
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
