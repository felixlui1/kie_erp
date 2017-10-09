/*-----------------------------------------------------------

-- PURPOSE

--    质量收集计划的数据库操作类

-- History

--	  07-Nov-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.kol.pes.item.DataPlanIdNameItem;
import com.kol.pes.item.DataQaCharActionItem;
import com.kol.pes.item.DataQaNeedFillItem;
import com.kol.pes.item.DataQaReqItem;
import com.kol.pes.item.DataQaTriggerItem;
import com.kol.pes.item.DataQaValueItem;
import com.kol.pes.utils.CommonUtil;
import com.kol.pes.utils.ExpressionUtil;
import com.kol.pes.utils.LogUtil;


@Repository("qaDao")
public class QaDaoImpl implements QaDao {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	//根据工单的code找出它有没有对应的plan_id和child_plan_id.由于可能有child_plan_id，所以我们返回一个List,其实这个list最多有两个数据结点
	public String getPlanIdByOpCode(String opCode, String organizationId, final boolean isChildPlan) throws SQLException {
		
		String sql = "select plan_id from kol_pes_op_plan where standard_operation_code='"+opCode+"'"+" and ORGANIZATION_ID="+organizationId+" and operator_meaning='equals'";
		if(isChildPlan) {
			sql = "select child_plan_id from kol_pes_op_plan where standard_operation_code='"+opCode+"'"+" and ORGANIZATION_ID="+organizationId+" and operator_meaning='equals'";
		}
		
		LogUtil.log("getPlanIdByOpCode()equals:sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);

		String plan_id = runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					if(isChildPlan) {
						return rs.getString("child_plan_id");
					}else {
						return rs.getString("plan_id");
					}
				}
				
				return null;
			}
		});
		
		if(plan_id == null) {
			String sql2 = "select plan_id from kol_pes_op_plan where ('"+opCode+"' between low_value and high_value) and ORGANIZATION_ID="+organizationId+" and operator_meaning='between'";
			if(isChildPlan) {
				sql2 = "select child_plan_id from kol_pes_op_plan where ('"+opCode+"' between low_value and high_value) and ORGANIZATION_ID="+organizationId+" and operator_meaning='between'";
			}
			
			LogUtil.log("getPlanIdByOpCode() between:sql2="+sql2);
			
			plan_id = runner.query(sql2, new ResultSetHandler<String>() {

					public String handle(ResultSet rs) throws SQLException {
						if (rs == null) {
				            return null;
				        }
						
						while(rs.next()) { 
							if(isChildPlan) {
								return rs.getString("child_plan_id");
							}else {
								return rs.getString("plan_id");
							}
						}
						
						return null;
					}
			});
		}
		
		return plan_id;
	}
	
	//根据最新逻辑需求，获取子质量收集计划的id
	public List<DataPlanIdNameItem> getChildPlanIdList(String opCode, String organizationId) throws SQLException {
		
		String planId = getPlanIdByOpCode(opCode, organizationId, false);
		if(planId==null || planId.length()==0) {//如果不存在基本质量收集计划，说明是手动添加的类型，则以260为id获取子计划
			planId = getManualQaPlanId();
		}
			
		String sql = "select plan_id, child_plan_id, child_plan from KOL_PES_OP_PLAN where plan_id = "+planId+" and child_plan_id is not null group by plan_id, child_plan_id, child_plan";
		
		LogUtil.log("getChildPlanIdList():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<List<DataPlanIdNameItem>>() {

			public List<DataPlanIdNameItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataPlanIdNameItem> tempList = new ArrayList<DataPlanIdNameItem>();
				
				while(rs.next()) { 
					tempList.add(new DataPlanIdNameItem(rs.getString("CHILD_PLAN_ID"), rs.getString("CHILD_PLAN")));
				}
				
				return tempList;
			}
		});
	}
	
	//获取这个工序的质量收集计划，
	public List<DataQaNeedFillItem> getQaListToFill(String opCode, String organizationId, final boolean isChildPlan) throws SQLException {
		String planIdOrChildPlanId = getPlanIdByOpCode(opCode, organizationId, isChildPlan);
		return getQaListToFillByPlanId(planIdOrChildPlanId, opCode);
	}
	
	public List<DataQaNeedFillItem> getQaListToFillByPlanId(String planIdOrChildPlanId, String opCode) throws SQLException {
		StringBuilder sqlSb = new StringBuilder();
		
		//String sql = "select c.plan_id,c.char_id,c.char_name,c.result_column_name,c.prompt,nvl(o.mandatory_flag, c.mandatory_flag) mandatory_flag,c.read_only_flag,nvl(o.displayed_flag, c.displayed_flag) displayed_flag,c.datatype_meaning,c.display_length,c.decimal_precision,c.derived_flag from kol_pes_plan_char c,kol_pes_plan_char_op o where c.plan_id = "+planIdOrChildPlanId+" and c.plan_id = o.plan_id (+) and c.char_id = o.char_id (+) and o.op_code (+) = '"+opCode+"' order by c.prompt_sequence asc"; 
		
		String sql2 = "select * from (select c.plan_id, c.char_id, c.char_name, c.result_column_name, c.PROMPT_SEQUENCE, c.PROMPT, nvl(o.mandatory_flag, c.mandatory_flag) mandatory_flag, c.read_only_flag, nvl(o.displayed_flag, c.displayed_flag) displayed_flag, c.datatype_meaning, c.display_length, c.decimal_precision, c.derived_flag from kol_pes_plan_char c, kol_pes_plan_char_op o  where c.plan_id = " + planIdOrChildPlanId + " and c.plan_id = o.plan_id (+) and c.char_id = o.char_id (+) and o.op_code (+) = '" + opCode +"') where displayed_flag = 1 and char_id not in (select assigned_char_id from kol_pes_char_action where kol_pes_char_action.plan_id = " + planIdOrChildPlanId + " and (message like '%[%' or assign_type='S')) order by PROMPT_SEQUENCE";
		
		if(planIdOrChildPlanId!=null && planIdOrChildPlanId.length()>0) {
			sqlSb.append("select * from kol_pes_plan_char where plan_id=").append(planIdOrChildPlanId);
			sqlSb.append(" and kol_pes_plan_char.char_id not in");
			sqlSb.append("(select assigned_char_id from kol_pes_char_action where kol_pes_char_action.plan_id=").append(planIdOrChildPlanId);
			sqlSb.append(" and (kol_pes_char_action.message like '%[%' or kol_pes_char_action.assign_type='S'))");
			sqlSb.append(" and DISPLAYED_FLAG=1 order by prompt_sequence asc");
		}
		else {
			return new ArrayList<DataQaNeedFillItem>();
		}
		
		LogUtil.log("getQaListToFill():sql2="+sql2);
		
		final List<DataQaValueItem> qaValueList = getQaValueListByPlanId(planIdOrChildPlanId);

		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql2, new ResultSetHandler<List<DataQaNeedFillItem>>() {

			public List<DataQaNeedFillItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataQaNeedFillItem> tempList = new ArrayList<DataQaNeedFillItem>();
				
				while(rs.next()) { 
					DataQaNeedFillItem qa = new DataQaNeedFillItem();
					qa.charId = rs.getString("CHAR_ID");
					qa.charName = rs.getString("CHAR_NAME");
					qa.prompt = rs.getString("PROMPT");
					qa.resultColumnName = rs.getString("RESULT_COLUMN_NAME");
					qa.datatypeMeaning = rs.getString("DATATYPE_MEANING");
					qa.derivedFlag = rs.getString("DERIVED_FLAG");
					qa.mandatoryFlag = rs.getString("MANDATORY_FLAG");
					qa.readOnlyFlag = rs.getString("READ_ONLY_FLAG");
					
					if(qaValueList!=null && !"250".equals(qa.charId) && !"268".equals(qa.charId)) {//机器编号和操作员编号不使用可选数据，特殊处理一下
						for(DataQaValueItem qaValue : qaValueList) {
							if(qaValue!=null && qa.charId.equals(qaValue.charId)) {
								qa.addqaValueList(qaValue.shortCode, qaValue.description);
							}
						}
					}
					
					LogUtil.log("qa.prompt="+qa.prompt);
					
					tempList.add(qa);
				}
				
				return tempList;
			}
		});
	}
	
	//获取有固定值可供填写的质量管理计划项的值
	private List<DataQaValueItem> getQaValueListByPlanId(String planId) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sql = "select * from kol_pes_plan_char_value where plan_id="+planId;
		
		LogUtil.log("getQaValueListByPlanId():sql="+sql);
		
		return runner.query(sql, new ResultSetHandler<List<DataQaValueItem>>() {

			public List<DataQaValueItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataQaValueItem> qaValueListTemp = new ArrayList<DataQaValueItem>();
				
				while(rs.next()) {
					qaValueListTemp.add(new DataQaValueItem(rs.getString("char_id"), 
															rs.getString("short_code"), 
															rs.getString("description")));
				}
				
				return qaValueListTemp;
			}
		});
	}
	
	//获取手动添加的质量管理计划
	private String getManualQaPlanId() throws SQLException {
		QueryRunner runner = new QueryRunner(this.dataSource);
		String sqlGetPlanName = "select profile_value from kol_pes_system_param where source='QA PLAN' and profile_name='DEFAULT QA PLAN'";
		
		String planName = runner.query(sqlGetPlanName, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) {
					return rs.getString("profile_value");
				}
				
				return null;
			}
		});
		
		if(planName!=null) { 
			String sqlPlanId = "select plan_id from kol_pes_op_plan where plan_name='"+planName+"'";
		
			String planId = runner.query(sqlPlanId, new ResultSetHandler<String>() {

				public String handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return null;
			        }
					
					while(rs.next()) {
						return rs.getString("plan_id");//child_plan_id
					}
					
					return null;
				}
			});
			
			return planId;
		}
		return null;
	}
	
	//根据plan——name获取质量收集计划
	public List<DataQaNeedFillItem> getQaListByManual(String opCode) throws SQLException {
		
		String planId = getManualQaPlanId();
			
		if(planId!=null) {
			String sql2 = "select * from (select c.plan_id, c.char_id, c.char_name, c.result_column_name, c.prompt, c.prompt_sequence, nvl(o.mandatory_flag, c.mandatory_flag) mandatory_flag, c.read_only_flag, nvl(o.displayed_flag, c.displayed_flag) displayed_flag, c.datatype_meaning, c.display_length, c.decimal_precision, c.derived_flag from kol_pes_plan_char c, kol_pes_plan_char_op o  where c.plan_id = " + planId + " and c.plan_id = o.plan_id (+) and c.char_id = o.char_id (+) and o.op_code (+) = '" + opCode +"') where displayed_flag = 1 and char_id not in (select assigned_char_id from kol_pes_char_action where kol_pes_char_action.plan_id = " + planId + " and (message like '%[%' or assign_type='S')) order by prompt_sequence";

			StringBuilder sqlSb = new StringBuilder();
			sqlSb.append("select * from kol_pes_plan_char where plan_id=").append(planId);
			sqlSb.append(" and kol_pes_plan_char.char_id not in");
			sqlSb.append("(select assigned_char_id from kol_pes_char_action where kol_pes_char_action.plan_id=").append(planId);
			sqlSb.append(" and (kol_pes_char_action.message like '%[%' or kol_pes_char_action.assign_type='S'))");
			sqlSb.append(" and displayed_flag=1 order by prompt_sequence asc");
			
			LogUtil.log("getQaListByManual():sqlSb="+sql2);
			
			final List<DataQaValueItem> qaValueList = getQaValueListByPlanId(planId);
			
			QueryRunner runner = new QueryRunner(this.dataSource);
			return runner.query(sql2, new ResultSetHandler<List<DataQaNeedFillItem>>() {

				public List<DataQaNeedFillItem> handle(ResultSet rs) throws SQLException {
					if (rs == null) {
			            return null;
			        }
					
					List<DataQaNeedFillItem> tempList = new ArrayList<DataQaNeedFillItem>();
					
					while(rs.next()) { 
						DataQaNeedFillItem qa = new DataQaNeedFillItem();
						qa.charId = rs.getString("CHAR_ID");
						qa.charName = rs.getString("CHAR_NAME");
						qa.prompt = rs.getString("PROMPT");
						qa.resultColumnName = rs.getString("RESULT_COLUMN_NAME");
						qa.datatypeMeaning = rs.getString("DATATYPE_MEANING");
						qa.derivedFlag = rs.getString("DERIVED_FLAG");
						qa.mandatoryFlag = rs.getString("MANDATORY_FLAG");
						qa.readOnlyFlag = rs.getString("READ_ONLY_FLAG");
						
						if(qaValueList!=null) {
							for(DataQaValueItem qaValue : qaValueList) {
								if(qaValue!=null && qa.charId.equals(qaValue.charId)) {
									qa.addqaValueList(qaValue.shortCode, qaValue.description);
								}
							}
						}
						
						LogUtil.log("qa.prompt="+qa.prompt);
						
						tempList.add(qa);
					}
					
					return tempList;
				}
			});
		}

		return null;
	}
	
	//根据plan_id获取触发条件
	private List<DataQaTriggerItem> getTriggerListByPlanId(List<String> planIdList) throws SQLException {
		
		StringBuilder sqlSb = new StringBuilder();
		
		if(planIdList!=null && planIdList.size()>0) {
			sqlSb.append("select * from KOL_PES_PLAN_ACTION_TRIGGERS where plan_id=").append(planIdList.get(0));
			StringBuilder orSb = new StringBuilder();
			if(planIdList.size()>1) {
				for(int i=1; i<planIdList.size(); i++) {
					String op = planIdList.get(i);
					orSb.append(" or plan_id=").append(op);
				}
				sqlSb.append(orSb.toString());
			}
		}
		else {
			return null;
		}
		
		LogUtil.log("getTriggerList:"+sqlSb.toString());
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sqlSb.toString(), new ResultSetHandler<List<DataQaTriggerItem>>() {

			public List<DataQaTriggerItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataQaTriggerItem> tempList = new ArrayList<DataQaTriggerItem>();
				
				while(rs.next()) {
					DataQaTriggerItem qa = new DataQaTriggerItem();
					qa.PLAN_CHAR_ACTION_TRIGGER_ID = rs.getString("PLAN_CHAR_ACTION_TRIGGER_ID");
					qa.PLAN_ID = rs.getString("PLAN_ID");
					qa.SQL_STRING_FLAG = rs.getString("SQL_STRING_FLAG");
					qa.DATATYPE = rs.getString("DATATYPE");
					qa.CHAR_ID = rs.getString("CHAR_ID");
					qa.OPERATOR = rs.getString("OPERATOR");
					qa.LOW_VALUE_LOOKUP = rs.getString("LOW_VALUE_LOOKUP");
					qa.HIGH_VALUE_LOOKUP = rs.getString("HIGH_VALUE_LOOKUP");
					qa.LOW_VALUE_OTHER = rs.getString("LOW_VALUE_OTHER");
					qa.HIGH_VALUE_OTHER = rs.getString("HIGH_VALUE_OTHER");
					
					LogUtil.log("qa.CHAR_ID="+qa.CHAR_ID);
					
					tempList.add(qa);
				}
				
				return tempList;
			}
		});
	}
	
	//判断一个message表达式中的变量是否都在输入项目数据中
	private boolean isAllCharNameInExpInReqList(String message, List<DataQaReqItem> qaDataList, List<DataQaNeedFillItem> charNameIdList) throws SQLException {
		Pattern p = Pattern.compile("\\[.*?\\]");
		Matcher m1 = p.matcher(message);
		while (m1.find()) {
			String charName1 = m1.group(0);
			String charId = findCharIdByCharName(charName1.replace("[", "").replace("]", ""), charNameIdList);
			if(!isCharIdContainsInReqList(qaDataList, charId)) {
				LogUtil.log("isAllCharNameInExpInReqList:false");
				return false;
			}
		}
		LogUtil.log("isAllCharNameInExpInReqList:true");
		return true;
	}
	
	//用 planId 获取   action char 
	private List<DataQaCharActionItem> getActionCharListByPlanId(List<String> planIdList) throws SQLException {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select * from KOL_PES_CHAR_ACTION where plan_id=").append(planIdList.get(0));
		if(planIdList.size()>1) {
			for(int i=1; i<planIdList.size(); i++) {
				String op = planIdList.get(i);
				sqlSb.append(" or plan_id=").append(op);
			}
		}
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sqlSb.toString(), new ResultSetHandler<List<DataQaCharActionItem>>() {

			public List<DataQaCharActionItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				List<DataQaCharActionItem> tempList = new ArrayList<DataQaCharActionItem>();
				while(rs.next()) {
					DataQaCharActionItem qa = new DataQaCharActionItem();
					qa.ACTION_ID = rs.getString("ACTION_ID");
					qa.PLAN_ID = rs.getString("PLAN_ID");
					qa.PLAN_CHAR_ACTION_TRIGGER_ID = rs.getString("PLAN_CHAR_ACTION_TRIGGER_ID");
					qa.PLAN_CHAR_ACTION_ID = rs.getString("PLAN_CHAR_ACTION_ID");
					qa.ASSIGN_TYPE = rs.getString("ASSIGN_TYPE");
					qa.ASSIGNED_CHAR_ID = rs.getString("ASSIGNED_CHAR_ID");
					qa.MESSAGE = rs.getString("MESSAGE");
					
					tempList.add(qa);
				}
				return tempList;
			}
		});
	}
	
	//获取所有的charId和charName对应集合，方便后面的逻辑判断使用。避免每次循环都查一次数据表
	private List<DataQaNeedFillItem> getCharNameCharIdByPlanId(List<String> planIdList) throws SQLException {
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("select * from kol_pes_plan_char where plan_id=").append(planIdList.get(0));
		if(planIdList.size()>1) {
			for(int i=1; i<planIdList.size(); i++) {
				String op = planIdList.get(i);
				sqlSb.append(" or plan_id=").append(op);
			}
		}
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sqlSb.toString(), new ResultSetHandler<List<DataQaNeedFillItem>>() {

			public List<DataQaNeedFillItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataQaNeedFillItem> tempList = new ArrayList<DataQaNeedFillItem>();
				
				while(rs.next()) { 
					DataQaNeedFillItem qa = new DataQaNeedFillItem();
					qa.charId = rs.getString("CHAR_ID");
					qa.charName = rs.getString("CHAR_NAME");
					qa.prompt = rs.getString("PROMPT");
					qa.resultColumnName = rs.getString("RESULT_COLUMN_NAME");
					qa.datatypeMeaning = rs.getString("DATATYPE_MEANING");
					qa.derivedFlag = rs.getString("DERIVED_FLAG");
					
					LogUtil.log("qa.prompt="+qa.prompt);
					
					tempList.add(qa);
				}
				
				return tempList;
			}
		});
	}
	
	private String getOrgIdByWipId(String wipId) throws SQLException {
		
		String sql = "select organization_id from kol_pes_os_job where wip_entity_id="+wipId;

		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("organization_id");
				}
				
				return null;
			}
		});
	}
	
	//根据接收到的数据管理计划数据项判断触发了那些操作
	public List<DataQaReqItem> runTriggersNeedRunForQaItems(String transId, List<DataQaReqItem> qaDataList, String wipId, String opCode, boolean isManualAddedQa) throws SQLException {
		
		if(qaDataList==null || qaDataList.size()==0) {
			return new ArrayList<DataQaReqItem>();
		}
		
		List<DataQaTriggerItem> triggersNeedRunList = new ArrayList<DataQaTriggerItem>();
		
		List<String> planIdList = new ArrayList<String>();
		
		if(isManualAddedQa) {
			planIdList.add(getManualQaPlanId());
		}
		else {
			String orgId = getOrgIdByWipId(wipId);
			String planId = getPlanIdByOpCode(opCode, orgId, false);
			if(planId!=null) {
				planIdList.add(planId);
			}
			
			String childPlanId = getPlanIdByOpCode(opCode, orgId, true);
			if(childPlanId!=null) {
				planIdList.add(childPlanId);
			}
			
			LogUtil.log("runTriggersNeedRunForQaItems():planId="+planId+", childPlanId="+childPlanId);
		}
		
		List<DataQaTriggerItem> triList = getTriggerListByPlanId(planIdList);
		List<DataQaCharActionItem> actionCharList = getActionCharListByPlanId(planIdList);//获取message的列表，方便后面替换的时候检索，只要差一次数据库就够了
		List<DataQaNeedFillItem> charNameIdList = getCharNameCharIdByPlanId(planIdList);//获取name和id的对应列表，方便后面替换的时候检索，只要差一次数据库就够了
		
		if(qaDataList!=null && triList!=null && qaDataList.size()>0 && triList.size()>0) {
			
			for(DataQaTriggerItem triItem : triList) {
				
				if ("7".equals(triItem.OPERATOR)) {// is entered
					if(isCharIdContainsInReqList(qaDataList, triItem.CHAR_ID)) {
						triggersNeedRunList.add(triItem);
					}
					else {//如果没能直接找到，则考虑是否通过表达式替换寻找
						String message = getExpectionToChangeCharId(triItem.CHAR_ID, actionCharList);
						LogUtil.log("runTriggersNeedRunForQaItems:message1="+message+",triItem.CHAR_ID="+triItem.CHAR_ID);
						if(message != null) {
							message = replaceCharNameToExpInMessage(qaDataList, message, actionCharList, charNameIdList);
							LogUtil.log("runTriggersNeedRunForQaItems:message2="+message+",triItem.CHAR_ID="+triItem.CHAR_ID);
							if(isAllCharNameInExpInReqList(message, qaDataList, charNameIdList)) {
								triggersNeedRunList.add(triItem);
							}
						}
					}
				}
				else {
					for(DataQaReqItem qaReq : qaDataList) {
						if(qaReq!=null && triItem!=null && qaReq.charId!=null && qaReq.charId.equals(triItem.CHAR_ID)) {
							//根据OPERATOR标志判断数据是否符合触发条件
							//1	equals		=
							//6	less than	<
							//4	at most		<=
							//3	at least	>=
							//8	is empty	is null
							//5	greater than >
							//7	is entered
	
							if("1".equals(triItem.OPERATOR) && qaReq.value.equals(triItem.LOW_VALUE_OTHER)) {
								triggersNeedRunList.add(triItem);
							}
							else if("6".equals(triItem.OPERATOR) && CommonUtil.isValidNumber(qaReq.value) && CommonUtil.isValidNumber(triItem.LOW_VALUE_OTHER)) {
								if(Integer.valueOf(qaReq.value)<Integer.valueOf(triItem.LOW_VALUE_OTHER)) {
									triggersNeedRunList.add(triItem);
								}
							}
							else if ("4".equals(triItem.OPERATOR) && CommonUtil.isValidNumber(qaReq.value) && CommonUtil.isValidNumber(triItem.LOW_VALUE_OTHER)) {
								if(Integer.valueOf(qaReq.value)<=Integer.valueOf(triItem.LOW_VALUE_OTHER)) {
									triggersNeedRunList.add(triItem);
								}
							} 
							else if ("3".equals(triItem.OPERATOR) && CommonUtil.isValidNumber(qaReq.value) && CommonUtil.isValidNumber(triItem.LOW_VALUE_OTHER)) {
								if(Integer.valueOf(qaReq.value)>=Integer.valueOf(triItem.LOW_VALUE_OTHER)) {
									triggersNeedRunList.add(triItem);
								}
							} 
							else if ("8".equals(triItem.OPERATOR) && (qaReq.value==null||qaReq.value.length()==0)) {
								triggersNeedRunList.add(triItem);
							}
							else if ("5".equals(triItem.OPERATOR) && CommonUtil.isValidNumber(qaReq.value) && CommonUtil.isValidNumber(triItem.LOW_VALUE_OTHER)) {
								if(Integer.valueOf(qaReq.value)>Integer.valueOf(triItem.LOW_VALUE_OTHER)) {
									triggersNeedRunList.add(triItem);
								}
							}
						}
					}
				}
			}
		}
		
		return runTriggersGetMessageById(transId, qaDataList, triggersNeedRunList, wipId, planIdList, actionCharList, charNameIdList);
	}
	
	//根据triggers id 找出对应的message，并根据message的类型，分类运行
	private List<DataQaReqItem> runTriggersGetMessageById(String transId, List<DataQaReqItem> qaDataList, List<DataQaTriggerItem> triggersNeedRunList, String wipId, 
														  List<String> planIdList, List<DataQaCharActionItem> actionCharList, List<DataQaNeedFillItem> charNameIdList) throws SQLException {
		
		List<DataQaReqItem> resQaDataList = new ArrayList<DataQaReqItem>();
		
		StringBuilder sqlSb = new StringBuilder();
		
		if(triggersNeedRunList!=null && triggersNeedRunList.size()>0) {
			
			sqlSb.append("select * from KOL_PES_CHAR_ACTION where plan_char_action_trigger_id=").append(triggersNeedRunList.get(0).PLAN_CHAR_ACTION_TRIGGER_ID);
			
			if(triggersNeedRunList.size()>1) {
				for(int i=1; i<triggersNeedRunList.size(); i++) {
					DataQaTriggerItem triItem = triggersNeedRunList.get(i);
					sqlSb.append(" or plan_char_action_trigger_id=").append(triItem.PLAN_CHAR_ACTION_TRIGGER_ID);
				}
			}
			sqlSb.append(" order by assign_type desc");
			LogUtil.log("runTriggersGetMessageById():sql="+sqlSb);
		}
		else {
			return resQaDataList;
		}
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		List<DataQaCharActionItem> triggerMsgList = runner.query(sqlSb.toString(), new ResultSetHandler<List<DataQaCharActionItem>>() {

			public List<DataQaCharActionItem> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<DataQaCharActionItem> tempList = new ArrayList<DataQaCharActionItem>();
				
				while(rs.next()) {
					DataQaCharActionItem qa = new DataQaCharActionItem();
					qa.ACTION_ID = rs.getString("ACTION_ID");
					qa.PLAN_ID = rs.getString("PLAN_ID");
					qa.PLAN_CHAR_ACTION_TRIGGER_ID = rs.getString("PLAN_CHAR_ACTION_TRIGGER_ID");
					qa.PLAN_CHAR_ACTION_ID = rs.getString("PLAN_CHAR_ACTION_ID");
					qa.ASSIGN_TYPE = rs.getString("ASSIGN_TYPE");
					qa.ASSIGNED_CHAR_ID = rs.getString("ASSIGNED_CHAR_ID");
					qa.MESSAGE = rs.getString("MESSAGE");
					
					tempList.add(qa);
				}
				
				return tempList;
			}
		});
		
		if(triggerMsgList==null || triggerMsgList.size()==0) {
			return resQaDataList;
		}
		else {
			try {
				String trxQuan = getOpTrxQuanByTransId(transId);
				String incompleteQuan = getIncompleteQuantityByWipEntityId(wipId);
				String organizeId = getOrganizationIdByPlanId(planIdList.get(0));
				String itemId = getItemIdByWipEntityId(wipId);
				
				List<DataQaCharActionItem> tempNotRunTriggerMsgList = new ArrayList<DataQaCharActionItem>();
				
				for(int i=0; i<triggerMsgList.size(); i++) {
					
					DataQaCharActionItem tri = triggerMsgList.get(i);
					LogUtil.log("tri.MESSAGE="+tri.MESSAGE);
					
					if("S".equals(tri.ASSIGN_TYPE)) {//如果是SQL
						String sql = replaceCharNameToValueInMessage(qaDataList, resQaDataList, tri.MESSAGE, actionCharList, charNameIdList, trxQuan, incompleteQuan, organizeId, itemId);
						
						String resS = null;
						
						if(tri.MESSAGE!=null && tri.MESSAGE.toLowerCase().contains("elect sysdate from dua")) {//select sysdate from dual;
							resS = CommonUtil.formatDateTime(new Date(Calendar.getInstance().getTimeInMillis()));
						}
						else if(tri.MESSAGE!=null && tri.MESSAGE.toLowerCase().contains("elect :move_transactions.available from dual")) {//select :move_transactions.available from dual;
							resS = getIncompleteByWipEntityId(wipId);
						}
						else if(tri.MESSAGE!=null && tri.MESSAGE.toLowerCase().equals(":move_transactions.move_quantity")) {//:move_transactions.MOVE_QUANTITY
							resS = getOpTrxQuanByTransId(transId);
						}
						else if(tri.MESSAGE!=null && tri.MESSAGE.toLowerCase().contains(":move_transactions.available from dual")) {//:move_transactions.MOVE_QUANTITY
							resS = incompleteQuan;
						}
						else {
							if (sql.contains("]")) {
								tempNotRunTriggerMsgList.add(tri);
							} 
							else {
								resS = runSqlInMessage(sql);
							}
						}
						
						LogUtil.log("resS="+resS);
						
						if(resS != null) {//有一些sql运行结果为空
							if(resS.length()>80)  {
								resS = resS.substring(0, 80);
							}
							resQaDataList.add(new DataQaReqItem(findResultColumnNameByChaId(charNameIdList, tri.ASSIGNED_CHAR_ID,tri.PLAN_ID), resS, tri.ASSIGNED_CHAR_ID));
						}
					}
					else {
						if(tri.MESSAGE!=null && (tri.MESSAGE.contains("]")||tri.MESSAGE.toLowerCase().contains("move_transactions")) && !tri.MESSAGE.contains("DECODE(")) {//如果是表达式
							String exp = replaceCharNameToValueInMessage(qaDataList, resQaDataList, tri.MESSAGE, actionCharList, charNameIdList, trxQuan, incompleteQuan, organizeId,itemId);
							double resD = getValueOfExpression(exp);
							//String resS = String.valueOf(resD);//.substring(0, String.valueOf(resD).indexOf("."));
							DecimalFormat df = new DecimalFormat("0.00");
							String resS = df.format(resD);
							LogUtil.log("resS="+resS);
							resQaDataList.add(new DataQaReqItem(findResultColumnNameByChaId(charNameIdList, tri.ASSIGNED_CHAR_ID,tri.PLAN_ID), resS, tri.ASSIGNED_CHAR_ID));
						}
						else if(tri.MESSAGE!=null && tri.MESSAGE.contains("DECODE(")) {
							String sql = replaceCharNameToValueInMessage(qaDataList, resQaDataList, tri.MESSAGE, actionCharList, charNameIdList, trxQuan, incompleteQuan, organizeId, itemId);
							sql = "select "+sql+" from dual";
							String resS2 = runSqlInMessage(sql);
							resQaDataList.add(new DataQaReqItem(findResultColumnNameByChaId(charNameIdList, tri.ASSIGNED_CHAR_ID,tri.PLAN_ID), resS2, tri.ASSIGNED_CHAR_ID));
							LogUtil.log("resS2="+resS2);
						}
						else {//如果是值
							resQaDataList.add(new DataQaReqItem(findResultColumnNameByChaId(charNameIdList, tri.ASSIGNED_CHAR_ID,tri.PLAN_ID), tri.MESSAGE,tri.ASSIGNED_CHAR_ID));
						}
					}
					
					if (i==triggerMsgList.size()-1 && tempNotRunTriggerMsgList.size()>0) {
						triggerMsgList = tempNotRunTriggerMsgList;
						i=0;
						LogUtil.log("tempNotRunTriggerMsgList continued");
					}
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return resQaDataList;
		}
	}
	
	//将SQL或者表达式中的变量（charName）替换成客户端提交上来的值
	private String replaceCharNameToValueInMessage(List<DataQaReqItem> qaDataList, List<DataQaReqItem> resQaDataList, 
												   String sql, 
												   List<DataQaCharActionItem> actionCharList, List<DataQaNeedFillItem> charNameIdList,
												   String trxQuan, String incompleteQuan, String organizeId, String itemId) throws SQLException {
		
		List<DataQaReqItem> tempQaReqAndResList = new LinkedList<DataQaReqItem>();
		tempQaReqAndResList.addAll(qaDataList);
		tempQaReqAndResList.addAll(resQaDataList);
		
		sql = replaceCharNameToExpInMessage(tempQaReqAndResList, sql, actionCharList, charNameIdList);
		
		sql = sql.toUpperCase();
		sql = sql.replace(":MOVE_TRANSACTIONS.MOVE_QUANTITY", trxQuan);
		sql = sql.replace(":MOVE_TRANSACTIONS.AVAILABLE", incompleteQuan);
		sql = sql.replace(":MOVE_TRANSACTIONS.ORGANIZATION_ID", organizeId);
		sql = sql.replace(":MOVE_TRANSACTIONS.PRIMARY_ITEM_ID", itemId);
		
		LogUtil.log("replaceCharNameToValueInMessage:sql after exp replace="+sql);
		
		Pattern p = Pattern.compile("\\[.*?\\]");
		Matcher m2 = p.matcher(sql);
		
		while (m2.find()) {
			String charName2 = m2.group(0);//例如[Job]
			String charId = findCharIdByCharName(charName2.replace("[", "").replace("]", ""), charNameIdList);

			for(DataQaReqItem qaReq : tempQaReqAndResList) {
				if(qaReq!=null && qaReq.charId!=null && qaReq.charId.equals(charId) && qaReq.value!=null) {
					
					if (CommonUtil.isValidNumber(qaReq.value.replace(".", ""))) {
						sql = sql.replace(charName2, qaReq.value);
					}
					else if (CommonUtil.isTimeStringFormat(qaReq.value)) {
						sql = sql.replace(charName2, String.valueOf(CommonUtil.revertMillsToDaysDouble(CommonUtil.convertStringToCal(qaReq.value).getTimeInMillis())));	
					}						
					else {
						sql = sql.replace(charName2, "'"+qaReq.value+"'");
					}

					LogUtil.log("replaceCharNameToValueInMessage:qaReq.value="+qaReq.value+", charName2="+charName2);
					break;
				}
			}
		}
		
		sql = sql.toUpperCase();
		sql = sql.replace(":MOVE_TRANSACTIONS.MOVE_QUANTITY", trxQuan);
		sql = sql.replace(":MOVE_TRANSACTIONS.AVAILABLE", incompleteQuan);
		sql = sql.replace(":MOVE_TRANSACTIONS.ORGANIZATION_ID", organizeId);
		sql = sql.replace(":MOVE_TRANSACTIONS.PRIMARY_ITEM_ID", itemId);
		
		LogUtil.log("replaceCharNameToValueInMessage:sql after exp replace all="+sql);
		
		return sql.replace(";", "");
	}
	
	//将由表达式得出的变量直接替换成相应表达式
	private String replaceCharNameToExpInMessage(List<DataQaReqItem> qaDataList, String sql, List<DataQaCharActionItem> actionCharList, List<DataQaNeedFillItem> charNameIdList) throws SQLException {
		Pattern p = Pattern.compile("\\[.*?\\]");
		
		for(int i=0; i<5; i++) {//默认循环3次吧，用while太危险 。已知的表达式变量由其它表达式替换的循环也就三层，所以循环五次是足够的.
			Matcher m1 = p.matcher(sql);
			boolean isEveryCharInReqList = true;
			while (m1.find()) {//先将需要由其它表达式计算的变量替换为对应的表达式，以期望新的表达式的每一个变量都能在输入的质量参数里找到
				
				String charName1 = m1.group(0);//例如[Job]
				String charId = findCharIdByCharName(charName1.replace("[", "").replace("]", ""), charNameIdList);
				String exp = getExpectionToChangeCharId(charId, actionCharList);
				
				if(!isCharIdContainsInReqList(qaDataList, charId) && exp!=null) {
					isEveryCharInReqList = false;
					sql = sql.replace(charName1, exp);
					LogUtil.log("replaceCharNameToExpInMessage:charName1="+charName1+", is replaced by exp="+exp);
				}
			}
			
			if(isEveryCharInReqList) {
				break;
			}
		}
		
		return sql;
	}
	
	private String getOpTrxQuanByTransId(String transId) throws SQLException {
		String sql = "select trx_quantity from kol_pes_move_txn_result where transaction_id="+transId;
		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("trx_quantity");
				}
				
				return null;
			}
		});
	}
	
	//判断message中的变量是否在输入的值里边有包含，如果没有，则在表中找形成这个变量的表达式
	private boolean isCharIdContainsInReqList(List<DataQaReqItem> qaDataList, String charId) {
		for(DataQaReqItem qaReq : qaDataList) {
			if(qaReq!=null && qaReq.charId!=null && qaReq.charId.equals(charId) && qaReq.value!=null) {
				return true;
			}
		}
		return false;
	}
	
	//根据planId和assigned_char_name找到某个变量的基础表达式
	private String getExpectionToChangeCharId(String charIdInExp, List<DataQaCharActionItem> actionCharList) throws SQLException {
		if(charIdInExp!=null && actionCharList!=null) {
			for(DataQaCharActionItem actionItem : actionCharList) {
				if(actionItem!=null && actionItem.ASSIGNED_CHAR_ID!=null && actionItem.ASSIGNED_CHAR_ID.equals(charIdInExp) && 
				   actionItem.MESSAGE!=null && actionItem.MESSAGE.contains("[")) {
					return "("+actionItem.MESSAGE+")";
				}
			}
		}
		return null;
	}
	
	//根据planId找出它有没有对应的OrganizationId,观察数据表得知，planId相同基本OrganizationId也一定相同
	private String getOrganizationIdByPlanId(String planId) throws SQLException {
		
		String sql = "select ORGANIZATION_ID from kol_pes_op_plan where plan_id='"+planId+"'";
		
		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("ORGANIZATION_ID");
				}
				
				return null;
			}
		});
	}
	
	//根据工单的WIP_ENTITY_ID找对应的INCOMPLETE_QUANTITY
	private String getIncompleteQuantityByWipEntityId(String wip_entity_id) throws SQLException {
		
		String sql = "select INCOMPLETE_QUANTITY from kol_pes_os_job where wip_entity_id="+wip_entity_id;
		
		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("INCOMPLETE_QUANTITY");
				}
				
				return null;
			}
		});
	}
	
	//根据工单的WIP_ENTITY_ID找对应的item_id
	private String getItemIdByWipEntityId(String wip_entity_id) throws SQLException {
		
		String sql = "select PRIMARY_ITEM_ID from kol_pes_os_job where wip_entity_id="+wip_entity_id;
		
		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("PRIMARY_ITEM_ID");
				}
				
				return null;
			}
		});
	}
	
	//根据工单的WIP_ENTITY_ID找对应的incomplete_quantity
	private String getIncompleteByWipEntityId(String wip_entity_id) throws SQLException {
		
		String sql = "select INCOMPLETE_QUANTITY from kol_pes_os_job where wip_entity_id="+wip_entity_id;
		
		QueryRunner runner = new QueryRunner(this.dataSource);

		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) { 
					return rs.getString("INCOMPLETE_QUANTITY");
				}
				
				return null;
			}
		});
	}
	
	//根据charName检索出对应的charId
	private String findCharIdByCharName(String charName, List<DataQaNeedFillItem> charNameIdList) throws SQLException {
		if(charName!=null && charNameIdList!=null) {
			for(DataQaNeedFillItem charNameIdItem : charNameIdList) {
				if(charNameIdItem!=null && charNameIdItem.charName!=null && charNameIdItem.charName.toUpperCase().equals(charName.toUpperCase())) {
					return charNameIdItem.charId;
				}
			}
		}
		
		return null;
	}
	
	//根据charId检索出对应的ResultColumnName
	private String findResultColumnNameByChaId(List<DataQaNeedFillItem> charNameIdList, String charId, String planId) throws SQLException {
		if(charNameIdList!=null && charId!=null) {
			for(DataQaNeedFillItem charNameId : charNameIdList) {
				if(charNameId!=null && charNameId.charId!=null && charNameId.charId.equals(charId)) {
					return charNameId.resultColumnName;
				}
			}
		}
		return null;
	}
	
	//用SQL运行得出一个值
	private String runSqlInMessage(String sql) throws SQLException {
		sql = sql.replace(";", "");//语句末尾的;号保留会报错，故去掉
		LogUtil.log("runSqlInMessage:replaced="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		return runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }

				while(rs.next()) {
					return rs.getString(1);
				}
				
				return null;
			}
		});
	}
	
	//用整理好计算公式进行简单计算，得出一个值
	private double getValueOfExpression(String expression) {
		expression = expression.replace("[", "").replace("]", "").replace("'", "");
		LogUtil.log("getValueOfExpression="+expression);
		try {
			double resExp = ExpressionUtil.instance().computeWithStack(expression);
			if("Infinity".equals(String.valueOf(resExp))) {
				return 0d;
			}
			return resExp;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0d;
	}

	//插入质量管理数据
	public int insertQaItems(String transactionId, String wipEntityId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY, String opCode, boolean isChildPlan,
							 List<DataQaReqItem> qaDataList) throws SQLException {
		
		if(qaDataList==null || qaDataList.size()==0) {
			return 1;//如果没有质量管理数据，则不需要插表。为了迎合Action中的判断，返回1
		}
		
		String planId = getPlanIdByOpCode(opCode, getOrgIdByWipId(wipEntityId), isChildPlan);
		
		LogUtil.log("insertQaItems:planId1="+planId);
		
		if(planId==null || planId.length()==0) {
			planId = getManualQaPlanId();
		}
		
		LogUtil.log("insertQaItems:planId2="+planId);
		
		return insertQaItems(transactionId, CREATION_DATE, CREATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY, qaDataList, planId);
	}
	
	//插入质量管理数据
	public int insertQaItems(String transactionId, Date CREATION_DATE, String CREATED_BY,
							 Date LAST_UPDATE_DATE, String LAST_UPDATED_BY,
							 List<DataQaReqItem> qaDataList, String planId) throws SQLException {
		if(planId==null || planId.length()==0) {
			return 1;//如果没有质量管理数据，则不需要插表。为了迎合Action中的判断，返回1
		}
		
		if(isQaDataExist(transactionId, planId)) {
			deleteQaReslutWhenOpEndFail(transactionId, planId);
		}
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		StringBuilder sqlSb = new StringBuilder();
		
		sqlSb.append("insert into kol_pes_qa_result(TRANSACTION_ID, CREATION_DATE, CREATED_BY, LAST_UPDATE_DATE, LAST_UPDATED_BY, PLAN_ID");
		
		for(DataQaReqItem qa : qaDataList) {
			if(qa.resultColumnName!=null) {
				if ("19".equals(qa.charId)) {
					qa.resultColumnName = "WIP_ENTITY_NAME";//特殊处理一下
				}
				sqlSb.append(", ").append(qa.resultColumnName);
			}
		}
		
		sqlSb.append(") values(");
		sqlSb.append(transactionId).
		append(", to_date('").append(CommonUtil.formatDateTime(CREATION_DATE)).append("','yyyy-mm-dd hh24:mi:ss')").
		append(", ").append(CREATED_BY).
		append(", to_date('").append(CommonUtil.formatDateTime(LAST_UPDATE_DATE)).append("','yyyy-mm-dd hh24:mi:ss')").
		append(", ").append(LAST_UPDATED_BY).
		append(", ").append(planId);
		
		for(DataQaReqItem qa : qaDataList) {
			if(qa.resultColumnName!=null) {
				sqlSb.append(", '").append(CommonUtil.removeYinhaoInString(qa.value)).append("'");
			}
		}
		
		sqlSb.append(")");
		
		LogUtil.log("insertQaItems():"+sqlSb.toString());

		return runner.update(sqlSb.toString());
	}
	
	private boolean isQaDataExist(String transId, String planId) throws SQLException {
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		String sql = "select TRANSACTION_ID from kol_pes_qa_result where TRANSACTION_ID="+transId+" and PLAN_ID="+planId;
		
		String res = runner.query(sql, new ResultSetHandler<String>() {

			public String handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				while(rs.next()) {
					return (rs.getString("TRANSACTION_ID"));
				}
				
				return null;
			}
		});
		
		return res!=null && res.length()>0;
	}
	
	//删除工序结束失败的质量管理计划项
	private int deleteQaReslutWhenOpEndFail(String transactionId, String PLAN_ID) throws SQLException {
		String sql = "delete from kol_pes_qa_result where transaction_id="+transactionId.trim()+" and PLAN_ID="+PLAN_ID;
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		int resRows = runner.update(sql);
		LogUtil.log("deleteQaReslutWhenOpEndFail:resRows="+resRows+", sql="+sql);
		return resRows;
	}
	
	//获取未完成工序的总投入数，最早开启时间，最晚完成时间
	public List<String> getIncompleteQuanStartEndTime(String wipId, String opCode) throws SQLException {
		
		String sql = "select sum(trx_quantity) as trx, min(op_start)as ops,  max(op_end) as ope"+
					 " from kol_pes_move_txn_result"+
					 " where interfaced = 0 and wip_entity_id="+ wipId +" and fm_operation_code='"+opCode+"'";
		
		LogUtil.log("getIncompleteQuanStartEndTime():sql="+sql);
		
		QueryRunner runner = new QueryRunner(this.dataSource);
		
		return runner.query(sql, new ResultSetHandler<List<String>>() {

			public List<String> handle(ResultSet rs) throws SQLException {
				if (rs == null) {
		            return null;
		        }
				
				List<String> tempList = new ArrayList<String>();
				
				while(rs.next()) {
					tempList.add(rs.getString("trx"));
					tempList.add(CommonUtil.formatDateTime(rs.getTimestamp("ops")));
					tempList.add(CommonUtil.formatDateTime(rs.getTimestamp("ope")));
					
					return tempList;
				}
				
				return null;
			}
		});
	}
	

}
