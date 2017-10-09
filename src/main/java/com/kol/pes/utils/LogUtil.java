package com.kol.pes.utils;

public class LogUtil {

	//在Console输出日志，方便调试
	public static void log(String msg) {
		if(Constants.IS_OPEN_LOG) {
			System.out.print(msg+"\n");
		}
	}
}