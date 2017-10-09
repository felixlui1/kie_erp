/*-----------------------------------------------------------

-- PURPOSE

--    是一个Demo，没用

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.DemoService;
import com.opensymphony.xwork2.ActionSupport;


public class DemoAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4424087712369555093L;

	@Autowired
	@Qualifier("demoService")
	private DemoService demoService;
	
	private int id;
	private String message;
	
	@Override
	@Action(value="/demo", results={
			@Result(name="success", location="demo.ftl", type="freemarker", 
					params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		this.setMessage(demoService.find(this.id));
		//this.setMessage("XXX");
		System.out.print(this.id);
		return SUCCESS;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		   
		return this.getText("common.test");
	}
}
