/*-----------------------------------------------------------

-- PURPOSE

--    获取工单的Action。由于后面可能会做让用户选择工单的业务逻辑，所以，这个工单查询是按查询工单列表的逻辑来的，目前查出来的列表中肯定只有一个节点

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataOsJobItem;
import com.kol.pes.service.OsJobService;
import com.kol.pes.utils.LogUtil;


public class OsJobAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("osJobService")
	private OsJobService osJobService;
	
	private String wipName;//工单名称
	private List<DataOsJobItem> osJobList;//得到的工单列表
	
	@Override
	@Action(value="/osJob", results={
			@Result(name="success", location="os_job.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(osJobService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		this.setOsJobList(osJobService.findOsJob(this.wipName));
		
		if(osJobList==null || osJobList.size()==0) {
			setCode(CODE_FAIL);
			setMessage(getText("job.noThisJobWipName"));//没有查到工单号：
			return ERROR;
		}
		
		LogUtil.log("wip_name="+this.wipName);
		return SUCCESS;
	}
	
	//接收客户端传来的工单ID
	public void setWipName(String wipName) {
		this.wipName = wipName;
	}
	
	private void setOsJobList(List<DataOsJobItem> osJobList) {
		this.osJobList = osJobList;
	}
	
	public List<DataOsJobItem> getOsJobList() {
		return this.osJobList;
	}
}
