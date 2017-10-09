/*-----------------------------------------------------------

-- PURPOSE

--    工序的存储过程运行结果数据封装类

-- History

--	  23-Dec-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataSeqProcedureItem extends DataItem {

	private int errCode = 2;
	private String errMsg = "";
	
	public DataSeqProcedureItem() {
		
	}
	
	public DataSeqProcedureItem(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}
	
	public int getErrCode() {
		return errCode;
	}
	
	public String getErrMsg() {
		if(errMsg !=null) {
			return errMsg.replaceAll("\"", "");
		}
		return "";
	}
}
