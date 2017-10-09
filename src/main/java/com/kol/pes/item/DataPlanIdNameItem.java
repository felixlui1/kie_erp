/*-----------------------------------------------------------

-- PURPOSE

--    质量管理计划子计划的名称和id的值的数据封装类

-- History

--	  09-Mar-15  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataPlanIdNameItem extends DataItem {

	public String planId;
	public String planName;
	
	public DataPlanIdNameItem(String planId, String planName) {
		this.planId = planId;
		this.planName = planName;
	}
	
	public String getPlanId() {
		return planId;
	}
	
	public String getPlanName() {
		return planName;
	}
}
