/*-----------------------------------------------------------

-- PURPOSE

--    工具类

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class CommonUtil {
	
	public static String removeYinhaoInString(String src) {
		if(src != null) {
			return src.replace("'", "").replace("/*", "");
		}
		return src;
	}
	
	public static String getShiftByNowDateTime() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return hour>=8 && hour<20 ? "1" : "2";
	}
	
	public static boolean isStringNotNull(String s) {
		return s!=null && s.length()>0;
	}
	
    public static double revertMillsToDaysDouble(long mills) {
        if(mills<0L) {
            return -1;
        }else {
            return (double) (mills/24d/60d/60d/1000d);
        }
    }
	
	public static long revertDaysToMills(int days) {
        return days*24*60*60*1000L;
    }
	
	public static long revertDaysToMills(double days) {
        return (long) (days*24d*60d*60d*1000d);
    }
	
	public static long revertHoursToMills(int hour) {
		return (long) (hour*60d*60d*1000d);
	}
	
	public static boolean isValidNumber(String pswd) {
		if(pswd != null) {
			String p_s = "^[0-9]+$";
			Pattern pat_s = Pattern.compile(p_s);
	
			return pat_s.matcher(pswd).matches();
		}
		return false;
	}

	public static String noNullString(String s) {//赋值到ftl中的字符串不能为空，否则要报错，所以偶尔需要用“”代替允许为空的string
		if(s!=null) {
			return s;
		}
		
		return "";
	}
	
	public static String noNullInt(String s) {
		if(s!=null) {
			return s;
		}
		
		return "0";
	}
	
	public static boolean isTimeStringFormat(String dateTimeString) {
		if(dateTimeString!=null && dateTimeString.contains(":") && dateTimeString.contains("-")) {
			return true;
		}
		return false;
	}
	
	//根据字符串解析出时间对象
	public static Calendar convertStringToCal(String calString) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = df.parse(calString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	//为文件名格式化一下时间
	public static String formatDateTimeForFileName(Calendar cal) {
		return formatDateForFileName(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
										cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
										cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}
	
	private static String formatDateForFileName(int year, int month, int day, int hour, int min, int seds) {
		return String.format("%4d%02d%02d%02d%02d%02d", year, month, day, hour, min, seds);
	}
	
	public static String formatDateTime(Date date) {
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			return formatDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
							  cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
							  cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		}
		return "";
	}
	
	public static String formatDate(Date date) {
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			return formatDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
							  cal.get(Calendar.DAY_OF_MONTH));
		}
		return "";
	}
	
	public static Date getFormatedDateForDay(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForDayStart(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 1);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForDayMiddle(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 13);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForDayEnd(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForLightStart(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 8);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForLightEnd(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 20);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForNightStart(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 20);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static Date getSettedDateTimeForNightEnd(Date date) {
		Date tmpDate = new Date(date.getTime());
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date.getTime()+revertDaysToMills(1));
			cal.set(Calendar.HOUR_OF_DAY, 8);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			tmpDate.setTime(cal.getTimeInMillis());
		}
		return tmpDate;
	}
	
	public static String formatDateTime(Timestamp stamp) {
		if(stamp != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(stamp);
			
			return formatDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
								cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
								cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		}
		return "";
	}
	
	private static String formatDate(int year, int month, int day, int hour, int min, int seds) {
		return String.format("%4d-%02d-%02d %02d:%02d:%02d", year, month, day,hour, min, seds);
	}
	
	private static String formatDate(int year, int month, int day) {
		return String.format("%4d-%02d-%02d", year, month, day);
	}
}
