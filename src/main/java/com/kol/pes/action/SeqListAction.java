/*-----------------------------------------------------------

-- PURPOSE

--    工序列表的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataSeqItem;
import com.kol.pes.service.SeqService;
import com.kol.pes.utils.LogUtil;


public class SeqListAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private int id = -1;//上传上来的工单id参数
	private List<DataSeqItem> seqList;//获取的工序列表
	
	@Override
	@Action(value="/seqList", results={
			@Result(name="success", location="seq_list.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(seqService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setSeqList(seqService.findSeq(this.id));//获取工序列表
		
		if(seqList==null || seqList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("op.noThisOpSeqId") + this.id);//没有查到工序号：
			return ERROR;
		}
		
		LogUtil.log("seq_id="+this.id);
		
		return SUCCESS;
	}
	
	public void setSeqId(int id) {
		this.id = id;
	}
	
	private void setSeqList(List<DataSeqItem> seqList) {
		this.seqList = seqList;
	}
	
	//供渲染XML使用
	public List<DataSeqItem> getSeqList() {
		return this.seqList;
	}
}
