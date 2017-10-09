/*-----------------------------------------------------------

-- PURPOSE

--    工序的数据封装类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataSeqItem extends DataItem {

	public int routingSeqId;
	public String operationSequenceId;
	public String standardSequenceId;
	public int operationSeqNum;
	public String standardOperationCode;
	public String departmentCode;
	public String departmentId;
	public String operationDescription;
	
	
	
	public int getRoutingSeqId() {
		return routingSeqId;
	}
	
	public String getOperationSequenceId() {
		return operationSequenceId;
	}
	
	public String getStandardSequenceId() {
		return standardSequenceId;
	}
	
	public int getOperationSeqNum() {
		return operationSeqNum;
	}
	
	public String getStandardOperationCode() {
		return standardOperationCode;
	}
	
	public String getDepartmentCode() {
		return departmentCode;
	}
	
	public String getDepartmentId() {
		return departmentId;
	}
	
	public String getOperationDescription() {
		return operationDescription;
	}
}
