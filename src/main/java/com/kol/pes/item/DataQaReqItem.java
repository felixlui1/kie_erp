/*-----------------------------------------------------------

-- PURPOSE

--    服务器接收到的质量管理计划的数据封装类

-- History

--	  09-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

import com.kol.pes.utils.LogUtil;

public class DataQaReqItem extends DataItem {

	public String charId;
	public String resultColumnName;
	public String value;
	
	public DataQaReqItem() {
		
	}
	
	public DataQaReqItem(String resultColumnName, String value, String charId) {
		this.resultColumnName = resultColumnName;
		this.value = value;
		this.charId = charId;
		LogUtil.log("DataQaReqItem:resultColumnName="+resultColumnName+", value="+value+", charId="+charId);
	}
	
	public String getCharId() {
		return charId;
	}
	
	public String getResultColumnName() {
		return resultColumnName;
	}
	
	public String getValue() {
		return value;
	}
}
