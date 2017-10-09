/*-----------------------------------------------------------

-- PURPOSE

--    封装ActionSupport，作为其它Action的父类。实现XML中的code和message属性的赋值

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.Locale;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ParentAction extends ActionSupport {

	private static final long serialVersionUID = 999888L;
	
	protected final String tag = getClass().getSimpleName();
	
	protected final String CODE_FAIL = "1";
	protected final String CODE_SUCCESS = "0";
	
	protected final int STATUS_OK = 0;
	
	private String code = CODE_SUCCESS;
	private String message = "";
	
	//设置返回XML属性code的值
	public void setCode(String code) {
		this.code = code;
	}
	
	//供XML填值使用
	public String getCode() {
		return this.code;
	}
	
	//设置返回XML属性message的值
	public void setMessage(String message) {
		this.message = message;
	}
	
	//供XML填值使用
	public String getMessage() {
		return this.message;
	}
	
	//设置语言
	public void setLang(String lang) {
		if(lang!=null && lang.equals("en")) {
			ActionContext.getContext().setLocale(new Locale("en", "US"));
		}else {
			ActionContext.getContext().setLocale(new Locale("zh", "CN"));
		}
	}
}
