/*-----------------------------------------------------------

-- PURPOSE

--    根据工序的id获取相应工序加工状况的Action

-- History

--	  15-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataSeqStartedItem;
import com.kol.pes.service.SeqService;


public class SeqMoveListByWipIdAction extends ParentAction {

	private static final long serialVersionUID = 4424088888869555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private String wipEntityId;//工单id
	private List<DataSeqStartedItem> seqMoveAllList;//根据工单id获取未完成工序列表
	
	
	@Override
	@Action(value="/seqMoveAllList", results={
			@Result(name="success", location="seq_move_all_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		seqMoveAllList = seqService.getSeqAllListByWipId(wipEntityId);

		if(seqMoveAllList==null || seqMoveAllList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("op.getMoveAllOpFail"));//暂无相应的工序
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	public List<DataSeqStartedItem> getSeqMoveAllList() {
		return seqMoveAllList;
	}
	
	public void setWipEntityId(String wipEntityId) {
		this.wipEntityId = wipEntityId;
	}

}
