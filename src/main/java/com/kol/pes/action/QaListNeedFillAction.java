/*-----------------------------------------------------------

-- PURPOSE

--   获取质量收集计划数据项的Action

-- History

--	  07-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.item.DataCodeMessageItem;
import com.kol.pes.item.DataPlanIdNameItem;
import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.service.QaService;
import com.kol.pes.service.SeqService;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.LogUtil;


public class QaListNeedFillAction extends ParentAction {

	private static final long serialVersionUID = 8629087712369555898L;

	@Autowired
	@Qualifier("qaService")
	private QaService qaService;
	
	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	
	private String wipId;//工单id
	private String opCode;//工序code
	private String organizationId;
	private List<DataQaNeedFillItem> qaList;//获取到的质量管理计划列表
	private List<DataPlanIdNameItem> qaChildPlanIdList;//获取的质量管理子计划列表
	private boolean isLastSeq = false;//是否是最后本工序的最后一个结束的加工工序
	
	private boolean canJump;//是否有回跳工序的权限
	
	private List<String> incompleteQuanStartEndTime;//获取的最大可投入数，最早开启的工序时间，最迟结束的工序时间
	
	private String timeBufferOpEnd;//完成工序时间可以提前的最大分钟数
	private String scrapQuanTotal;//当完成一个工序的最后一个拆分加工时，需要知道这个工序之前所有拆分的坏品数之和
	
	@Override
	@Action(value="/qaListNeedFill", results={
			@Result(name="success", location="qa_list_need_fill.ftl", type="freemarker", params={"contentType", "text/xml"}),
			@Result(name="error", location="code_message.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() throws Exception {
		
		if(qaService.dataStatus()!=STATUS_OK) {
			setCode(CODE_FAIL);
			setMessage(getText("common.dataRefreshing"));//服务器正在更新，请稍后操作
			return ERROR;
		}
		
		final DataCodeMessageItem procdureCode = seqService.runProcedureBeforeEndOp(Integer.valueOf(wipId), opCode);
		if(procdureCode != null && procdureCode.code != 0) {
			setCode(CODE_FAIL);
			setMessage(procdureCode.message);//getText("op.endOpCheckExistError") 暂时不能完成该工序，请联系IT
			return ERROR;
		}
		
		timeBufferOpEnd = seqService.getTimeBufferForOpEnd();
		scrapQuanTotal = seqService.getTotalScrapQuantityOfOtherPart(wipId, opCode);
		
		isLastSeq = seqService.isLastUncompleteOpNumForWip(wipId, opCode, canJump);
		qaList = qaService.getQaListToFill(opCode, organizationId, false);
		qaChildPlanIdList = qaService.getChildPlanIdList(opCode, organizationId);
		
		incompleteQuanStartEndTime = qaService.getIncompleteQuanStartEndTime(wipId, opCode);
		
		if((qaList==null || qaList.size()==0) && (qaChildPlanIdList==null || qaChildPlanIdList.size()==0)) {
			setCode(CODE_SUCCESS);
			setMessage(getText("qa.noQaListData"));//没有质量收集计划
			return ERROR;
		}
		
		LogUtil.log("opCode="+opCode);
		
		return SUCCESS;
	}
	
	public String getTimeBufferOpEnd() {
		return timeBufferOpEnd;
	}
	
	public List<DataQaNeedFillItem> getQaList() {
		return qaList;
	}
	
	public List<DataPlanIdNameItem> getQaChildPlanIdList() {
		return qaChildPlanIdList;
	}
	
	public String getIsLastSeq() {
		return isLastSeq?"Y":"N";
	}
	
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	
	public void setWipId(String wipId) {
		this.wipId = wipId;
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public void setCanJump(String canJump) {
		this.canJump = "Y".equals(canJump)?true:false;
	}
	
	public String getIncompleteQuan() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(0));
		}
		return "";
	}
	
	public String getMinStartTime() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(1));
		}
		return "";
	}
	
	public String getMaxEndTime() {
		if(incompleteQuanStartEndTime!=null) {
			return CommonUtil.noNullString(incompleteQuanStartEndTime.get(2));
		}
		return "";
	}
	
	public String getScrapQuanTotal() {
		return scrapQuanTotal;
	}
	
	
}
