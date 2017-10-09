/*-----------------------------------------------------------

-- PURPOSE

--    获取推送消息的Action

-- History

--	  19-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataPushMsgItem;
import com.kol.pes.service.PushMsgService;


public class PushMsgAction extends ParentAction {

	private static final long serialVersionUID = 8888087716669555098L;

	@Autowired
	@Qualifier("pushMsgService")
	private PushMsgService pushMsgService;
	
	private String staffNo;
	private String transId;
	private String isNotice;
	
	private List<DataPushMsgItem> pushMsgList;
	
	@Override
	@Action(value="/pushMsg", results={
			@Result(name="success", location="push_msg_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(pushMsgService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		pushMsgList = pushMsgService.getPushMsg(staffNo, transId, isNotice);
		
		if(pushMsgList==null || pushMsgList.size()==0) {
			setMessage(getText("push.noMessage"));//暂无推送消息
			return ERROR;
		}
		
		converPushMsgList(pushMsgList);
		
		return SUCCESS;
	}
	
	private void converPushMsgList(List<DataPushMsgItem> pushMsgList) {
		
		if(pushMsgList!=null) {
			for(DataPushMsgItem pushItem : pushMsgList) {
				
				pushItem.title = pushItem.wip_entity_name+"  "+pushItem.op_code+" "+pushItem.operation_description;
				
				float percent = Float.valueOf(pushItem.scrap_quantity)/Float.valueOf(pushItem.trx_quantity);
				
				pushItem.msg = getText("push.trxQuan")+pushItem.trx_quantity+"   "+
							   getText("push.scrapQuan")+pushItem.scrap_quantity+"   "+
							   getText("push.scrapPercent")+String.valueOf(percent);
			}
			
		}
	}
	
	public List<DataPushMsgItem> getPushMsgList() {
		return this.pushMsgList;
	}
	
	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}
	
	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	public void setIsNotice(String isNotice) {
		this.isNotice = isNotice;
	}
	
}
