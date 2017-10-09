

package com.kol.pes.item;

public class DataWeekItem extends DataItem {

	public String week;
	public String startEnd;
	public String hourMin;
	
	
	public DataWeekItem(String week, String startEnd, String hourMin) {
		this.week = week;
		this.startEnd = startEnd;
		this.hourMin = hourMin;
	}
	
	public String getWeek() {
		return week;
	}
	
	public String getStartEnd() {
		return startEnd;
	}
	
	public String getHourMin() {
		return hourMin;
	}
}
