/*-----------------------------------------------------------

-- PURPOSE

--    p判断哪些已完成工序需要推送坏品数量超标的数据封装类

-- History

--	  19-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.item;

public class DataOpFailNotifyItem extends DataItem {

	public String opCode;
	public String scrapPercent;
	public String accessLevel;
	
	public String getOpCode() {
		return opCode;
	}
	
	public String getScrapPercent() {
		return scrapPercent;
	}
	
	public String getAccessLevel() {
		return accessLevel;
	}
	
}
