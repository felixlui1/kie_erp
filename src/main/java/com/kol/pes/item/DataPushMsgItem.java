/*-----------------------------------------------------------

-- PURPOSE

--    推送消息的数据封装类

-- History

--	  19-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataPushMsgItem extends DataItem {

	public String transId;
	public String title;
	public String msg;
	
	public String wip_entity_name;
	public String op_code;
	public String operation_description;
	public String scrap_quantity;
	public String trx_quantity;
	
	
	public DataPushMsgItem(String transId) {
		this.transId = transId;
	}
	
	public String getTransId() {
		return transId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getMsg() {
		return msg;
	}
	
}
