/*-----------------------------------------------------------

-- PURPOSE

--    判断服务器数据是否在更新的Action。当返回status为1时，表示数据库正在更新，不能操作

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataWeekItem;
import com.kol.pes.service.DataEnableService;


public class DataEnableAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("dataEnableService")
	private DataEnableService dataEnableService;
	
	private List<DataWeekItem> weekListData;
	
	@Override
	@Action(value="/dataEnable", results={
			@Result(name="success", location="data_enable.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		int status = dataEnableService.dataStatus();
		
		if(status != 0) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
		}
		
		weekListData = dataEnableService.getWeekList();
		
		return SUCCESS;
	}
	
	public List<DataWeekItem> getWeekList() {
		return weekListData;
	}
	
}