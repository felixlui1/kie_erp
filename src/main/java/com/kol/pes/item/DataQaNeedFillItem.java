/*-----------------------------------------------------------

-- PURPOSE

--    需要填写的质量管理计划的数据封装类

-- History

--	  09-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

import java.util.ArrayList;
import java.util.List;

public class DataQaNeedFillItem extends DataItem {

	public String charId;
	public String charName;
	public String prompt;
	public String resultColumnName;
	public String datatypeMeaning;
	public String derivedFlag;
	
	public String mandatoryFlag;
	public String readOnlyFlag;
	
	private List<DataQaValueItem> qaValueList;
	
	public String getCharId() {
		return charId;
	}
	
	public String getCharName() {
		return charName;
	}
	
	public String getPrompt() {
		return prompt;
	}
	
	public String getResultColumnName() {
		return resultColumnName;
	}
	
	public String getDatatypeMeaning() {
		return datatypeMeaning;
	}
	
	public String getDerivedFlag() {
		return derivedFlag;
	}
	
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	
	public String getReadOnlyFlag() {
		return readOnlyFlag;
	}
	
	public List<DataQaValueItem> getQaValueList() {
		if(qaValueList == null) {
			return new ArrayList<DataQaValueItem>();
		}
		return qaValueList;
	}
	
	public void addqaValueList(String shortCode, String desc) {
		if(qaValueList == null) {
			qaValueList = new ArrayList<DataQaValueItem>();
		}
		qaValueList.add(new DataQaValueItem(shortCode, desc));
	}
	
}
