/*-----------------------------------------------------------

-- PURPOSE

--    质量管理计划中需要固定填写的值的数据封装类

-- History

--	  02-Jan-15  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataQaValueItem extends DataItem {

	public String charId;
	public String shortCode;
	public String description;
	
	public DataQaValueItem() {
		
	}
	
	public DataQaValueItem(String shortCode, String description) {
		this.shortCode = shortCode;
		this.description = description;
	}
	
	public DataQaValueItem(String charId, String shortCode, String description) {
		this.charId = charId;
		this.shortCode = shortCode;
		this.description = description;
	}
	
	public String getShortCode() {
		return shortCode;
	}
	
	public String getDescription() {
		return description;
	}
}
