package com.sinolife.pos.report.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定义各个清单的表头和取值用的key，及文件名
 * 方法名保持与查询sql一致
 * 
 * 由于取决于l_pos_list_query里变量的赋值
 * 这里的key无名字上对应的意义，请看官忽略
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HeadKeyDefine {

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 保单回执延期回销报备清单
	 */
	public void selectReceiptDelayList(Map map){
		List<String> head = new ArrayList<String>();
		head.add("分支机构");
		head.add("销售渠道");
		head.add("合同号");
		head.add("投保人");
		head.add("联系电话");
		head.add("合同打印日期");
		head.add("登记日期");
		head.add("未送达原因");
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_NAME");
		key.add("CHANNEL");
		key.add("POLICY_NO");
		key.add("CLIENT_NAME");
		key.add("CLIENT_PHONE");
		key.add("PROVIDE_DATE");
		key.add("RECORD_DATE");
		key.add("UNARRIVE_CAUSE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", "保单延期回销报备清单"+map.get("dateFrom")+"至"+map.get("dateTo")+".xls");
	}
	
	/**
	 * 生存调查数据提取清单
	 */
	public void selectSurvivalInvestList(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("客户号");
		head.add("客户姓名");
		head.add("调查状态");
		head.add("死亡日期");
		head.add("调查人");
		head.add("回销日期");
		head.add("备注");
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("POLICY_NO");
		key.add("CLIENT_NO");
		key.add("CLIENT_NAME");
		key.add("INVEST_STATE");
		key.add("DEATH_DATE");
		key.add("INVESTGATOR");
		key.add("FEEDBACK_DATE");
		key.add("REMARK");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", "生存调查数据清单"+map.get("yearMonth")+".xls");
	}
	
	/**
	 * 迁入迁出保单清单
	 */
	public void policyMoveQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("迁入机构");
		head.add("迁出机构");
		head.add("保单号");
		head.add("保全号");
		head.add("业务类型");
		head.add("受理日期");
		head.add("受理人");
		head.add("保单来源");
		head.add("保单状态");
		
		List<String> key = new ArrayList<String>();
		key.add("MOVE_IN_BRANCH_NAME");
		key.add("MOVE_OUT_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("BIZ_TYPE");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("POLICY_CHANNEL");
		key.add("POLICY_STATUS");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保单迁入迁出清单.xls");
	}
	
	/**
	 * 保全处理清单
	 */
	public void posProcessListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("柜面代码");
		head.add("保单号");
		head.add("客户号");
		head.add("保全号");
		head.add("保全项目名称");
		head.add("受理日期");
		head.add("受理人");
		head.add("受理状态");
		head.add("客户姓名");
		head.add("补退费类型");
		head.add("补退费金额");
		
		List<String> key = new ArrayList<String>();
		key.add("ACCEPT_BRANCH_NAME");
		key.add("COUNTER_NO");
		key.add("POLICY_NO");
		key.add("CLIENT_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("POS_STATUS");
		key.add("CLIENT_NAME");
		key.add("BIZ_TYPE");
		key.add("FEE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保全处理清单.xls");
	}
	
	/**
	 * 保全特殊件清单
	 */
	public void specialPosListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("保全项目名称");
		head.add("受理日期");
		head.add("受理人");
		head.add("特殊件原因");
		head.add("应追回既得利益");		
		head.add("保全状态");
		head.add("备注");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("BIG_TEXT");
		key.add("FEE");
		key.add("POS_STATUS");
		key.add("MEMO_TEXT");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保全特殊件清单.xls");
	}
	
	/**
	 * 保全未收费清单
	 */
	public void unchargedPosListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("保全项目名称");
		head.add("收费金额");
		head.add("受理日期");
		head.add("受理人");
		head.add("服务人员代码");
		head.add("服务人员姓名");
		head.add("状态");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("FEE");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		key.add("POS_STATUS");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保全未收费清单.xls");		
	}
	
	/**
	 * 质押贷款业务清单
	 */
	public void mortgagePosListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("险种名称");
		head.add("保全号");
		head.add("受理日期");
		head.add("受理人");
		head.add("投保人");
		head.add("贷款金额");
		head.add("贷款期限");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("PRODUCT_NAME");
		key.add("POS_NO");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("CLIENT_NAME");
		key.add("LOAN_SUM");
		key.add("LOAN_DURATION");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"质押贷款业务清单.xls");		
	}
	
	/**
	 * 异地保全受理件清单
	 */
	public void posProcessListOtherQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("业务类型");
		head.add("柜面");
		head.add("保单号");
		head.add("保全号");
		//head.add("受理机构代码");
		head.add("受理机构名称");
		//head.add("保单所在机构代码");
		head.add("保单所在机构名称");
		head.add("受理人");
		head.add("保全项目");
		head.add("受理日期");
		
		List<String> key = new ArrayList<String>();
		key.add("BIZ_TYPE");
		key.add("COUNTER_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		//key.add("ACCEPT_BRANCH_CODE");
		key.add("ACCEPT_BRANCH_NAME");
		//key.add("POLICY_BRANCH_CODE");
		key.add("POLICY_BRANCH_NAME");
		key.add("ACCEPTOR_NAME");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPT_DATE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"异地保全受理件清单.xls");		
	}
	
	/**
	 * 转账结果清单
	 */
	public void transferFailPosListQuery(Map map){
		List<String> head = new ArrayList<String>();
		//head.add("保单所在机构代码");
		head.add("保单所在机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("受理日期");
		head.add("受理人");
		head.add("保全项目");
		head.add("付费人姓名");
		head.add("费用合计");
		head.add("银行代码");
		head.add("银行名称");
		head.add("补退费类型");
		head.add("服务人员代码");
		head.add("服务人员姓名");
		head.add("银行帐号");
		head.add("转账状态");
		head.add("转账不成功原因描述");
	
		
		List<String> key = new ArrayList<String>();
		//key.add("POLICY_BRANCH_CODE");
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("POS_ITEM_DESC");
		key.add("CLIENT_NAME");
		key.add("FEE");
		key.add("BANK_CODE");
		key.add("BANK_NAME");
		key.add("BIZ_TYPE");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		key.add("ACCOUNT_NO");
		key.add("POLICY_STATUS");
		key.add("MEMO_TEXT");	
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"转账结果清单.xls");		
	}
	
	/**
	 * 自动垫交保费清单
	 */
	public void autoPolicyListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("垫交日期");
		head.add("投保人");
		head.add("保单状态");
		head.add("还款状态");
		head.add("本次自垫本金");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("ACCEPT_DATE");
		key.add("CLIENT_NAME");
		key.add("POLICY_STATUS");
		key.add("POS_STATUS");
		key.add("LOAN_SUM");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"自动垫交保费清单.xls");		
	}
	
	/**
	 * 预计超停清单
	 */
	public void policyToLapseListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保单状态");
		head.add("投保人");
		head.add("超停日期");
		head.add("业务类型");
		head.add("自垫/贷款本金");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POLICY_STATUS");
		key.add("CLIENT_NAME");
		key.add("ACCEPT_DATE");
		key.add("BIZ_TYPE");
		key.add("LOAN_SUM");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"预计超停清单.xls");		
	}
	
	/**
	 * 已终止保单生存金应领清单
	 */
	public void survCoverInLapsedPolicy(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保单状态");
		head.add("应领日期");
		head.add("生存金类型");
		head.add("应领金额");
		head.add("被保人姓名");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POLICY_STATUS");
		key.add("ACCEPT_DATE");
		key.add("BIZ_TYPE");
		key.add("LOAN_SUM");
		key.add("CLIENT_NAME");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"已终止保单生存金应领清单.xls");		
	}
	
	/**
	 * 保全回退/撤销清单
	 */
	public void posToRollbackListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("保全项目");
		head.add("受理状态");
		head.add("受理时间");
		head.add("受理人");
		head.add("回退原因描述");
		head.add("回退说明");
		head.add("回退批单号");
		head.add("差错类型");
		head.add("回退/撤销时间");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("POS_STATUS");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("PRODUCT_NAME");
		key.add("MEMO_TEXT");
		key.add("BARCODE");
		key.add("BIZ_TYPE");
		key.add("EXT_DATE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保全回退or撤销清单.xls");		
	}
	
	/**
	 * 生存金预算清单
	 */
	public void planForSurvCoverListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保单来源");
		head.add("险种名称");
		head.add("投保人");
		head.add("被保险人");
		head.add("生存金类型");
		head.add("应领日期");
		head.add("生存金本金");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("CHANNEL");
		key.add("PRODUCT_NAME");
		
		key.add("CLIENT_NAME");
		key.add("BANK_NAME");
		
		
		key.add("BIZ_TYPE");
		key.add("ACCEPT_DATE");
		key.add("LOAN_SUM");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"生存金预算清单.xls");		
	}
	
	/**
	 * 审批任务处理清单
	 */
	public void posApproveListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("审批人");
		head.add("保单号");
		head.add("客户号");
		head.add("保全号");
		head.add("受理项目");
		head.add("规则类特殊件");
		head.add("功能类特殊件");
		head.add("金额送审类");
		head.add("审批时间");
		head.add("审批结果");
		head.add("审批意见");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("AGENT_NAME");
		key.add("POLICY_NO");
		key.add("CLIENT_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("COUNTER_NO");
		key.add("PRODUCT_NAME");
		key.add("MEMO_FLAG");
		key.add("ACCEPT_DATE");
		key.add("BARCODE");
		key.add("MEMO_TEXT");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"审批任务处理清单.xls");		
	}
	
	/**
	 * 问题件跟踪清单
	 */
	public void posProblemListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("条形码");
		head.add("保单号");
		head.add("保全项目");
		head.add("问题号");
		head.add("问题件来源");
		head.add("问题件原因");
		head.add("问题件下发时间");
		head.add("回销时间");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("BARCODE");
		key.add("POLICY_NO");
		key.add("POS_ITEM_DESC");
		key.add("POS_NO");
		key.add("CHANNEL");
		key.add("MEMO_TEXT");
		key.add("ACCEPT_DATE");
		key.add("EXT_DATE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"问题件跟踪清单.xls");		
	}
	
	/**
	 * 待扫描溢时处理清单
	 */
	public void unscanedMaterialListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构代码");
		head.add("柜面代码");
		head.add("保全号");
		head.add("保全项目");
		head.add("受理日期");
		head.add("受理人");
		head.add("受理状态");
		head.add("条形码");
		head.add("未扫描资料类型");
		
		List<String> key = new ArrayList<String>();
		key.add("ACCEPT_BRANCH_CODE");
		key.add("COUNTER_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("POS_STATUS");
		key.add("BARCODE");
		key.add("BIZ_TYPE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"待扫描溢时处理清单.xls");		
	}
	
	/**
	 * 作业环节时效清单
	 */
	public void operationTimeEffectQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保全号");
		head.add("保单号");
		head.add("保全项目");
		head.add("受理状态");
		head.add("受理人");
		head.add("审批人");
		head.add("核保人");
		head.add("受理时效");
		head.add("审批时效");
		head.add("核保时效");
		
		List<String> key = new ArrayList<String>();
		key.add("ACCEPT_BRANCH_NAME");
		key.add("POS_NO");
		key.add("POLICY_NO");
		key.add("POS_ITEM_DESC");
		key.add("POS_STATUS");
		key.add("ACCEPTOR_NAME");
		key.add("AGENT_NAME");
		key.add("CHANNEL");
		key.add("LOAN_SUM");
		key.add("LOAN_DURATION");
		key.add("FEE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"作业环节时效清单.xls");		
	}

	/**
	 * 退信数据清单
	 */
	public void selectReturnNoteList(Map map){
		List<String> head = new ArrayList<String>();
		head.add("条形码号");
		head.add("保单号");
		head.add("客户姓名");
		//head.add("客户地址");
		head.add("邮编");
		head.add("销售渠道");
		head.add("产品类型");
		head.add("退信标识");
		head.add("备注");
		head.add("打印日期");

		List<String> key = new ArrayList<String>();
		key.add("SEQUENCE");
		key.add("POLICY_NO");
		key.add("CLIENT_NAME");
		//key.add("CLIENT_ADDRESS");
		key.add("POSTAL_CODE");
		key.add("CHANNEL");
		key.add("FREQUENCY");
		key.add("RETURN_FLAG");
		key.add("RETURN_REASON");
		key.add("PRINT_DATE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+map.get("dateFrom")+"至"+map.get("dateTo")+"退信清单.xls");
	}

	/**
	 * VIP查询清单
	 */
	public void queryVipClientInfo(Map map){
		List<String> head = new ArrayList<String>();
		head.add("客户号");
		head.add("客户姓名");
		head.add("证件号码");
		head.add("性别");
		head.add("出生日期");
		head.add("所属机构");
		head.add("家庭住址");
		head.add("联式方式");
		head.add("VIP类型");
		head.add("VIP等级");
		head.add("VIP生效日期");
		head.add("VIP结束日期");
		head.add("VIP保费");
		head.add("是否手工标注");		
		head.add("保单号");
		head.add("渠道");
		head.add("主险险种名称");
		head.add("险种代码");
		head.add("缴费年限");
		head.add("保费");
		head.add("保单状态");
		head.add("承保日期");
		head.add("被保人姓名");
		head.add("被保人性别");
		head.add("被保人出生日期");
		head.add("业务员");
		head.add("业务员电话");		
		head.add("服务人员");
		head.add("服务人员电话");
		head.add("服务人员所属机构");
		head.add("保单部门名称");
		
		List<String> key = new ArrayList<String>();
		key.add("CLIENT_NO");
		key.add("CLIENT_NAME");
		key.add("POS_STATUS");
		key.add("MEMO_FLAG");
		key.add("POLICY_BRANCH_CODE");
		key.add("POLICY_BRANCH_NAME");
		key.add("MEMO_TEXT");
		key.add("BARCODE");
		key.add("BANK_CODE");
		key.add("POS_ITEM_DESC");
		key.add("MOVE_IN_BRANCH_CODE");
		key.add("MOVE_OUT_BRANCH_CODE");
		key.add("LOAN_SUM");
		key.add("COUNTER_NO");
		key.add("POLICY_NO");
		key.add("CHANNEL");
		key.add("PRODUCT_NAME");
		key.add("POS_NO");
		key.add("ACCOUNT_NO");
		key.add("LOAN_DURATION");
		key.add("POLICY_STATUS");
		key.add("ACCEPT_BRANCH_CODE");
		key.add("ACCEPTOR_NAME");
		key.add("BIZ_TYPE");
		key.add("BANK_NAME");
		key.add("AGENT_NAME");
		key.add("MOVE_IN_BRANCH_NAME");
		key.add("ACCEPT_BRANCH_NAME");
		key.add("MOVE_OUT_BRANCH_NAME");		
		key.add("BIG_TEXT");
		key.add("COUNTER_NAME");
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"VIP信息清单.xls");		
	}
	
	/**
	 * 信息不真实保单清单
	 */
	public void wrongInfoPolicyQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构代码");
		head.add("机构名称");
		head.add("保单号");
		head.add("保单渠道");
		head.add("险种名称");
		head.add("生效日期");
		head.add("投保人姓名");
		head.add("被保人姓名");
		head.add("投保人联系地址");
		head.add("期缴保费");
		head.add("保单联系电话");
		head.add("投保人手机");
		head.add("投保人家庭电话");
		head.add("投保人办公电话");
		head.add("业务员代码");
		head.add("业务员姓名");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_CODE");
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("CHANNEL");
		key.add("PRODUCT_NAME");
		key.add("ACCEPT_DATE");
		key.add("CLIENT_NAME");
		key.add("ACCEPTOR_NAME");
		key.add("BIG_TEXT");
		key.add("FEE");
		key.add("BARCODE");
		key.add("ACCOUNT_NO");
		key.add("COUNTER_NAME");
		key.add("POLICY_STATUS");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"信息不真实保单清单.xls");		
	}
	
	/**
	 * 扣费成功生效保单清单
	 */
	public void chargeSuccessPolicyQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构代码");
		head.add("机构名称");
		head.add("保单号");
		head.add("险种名称");
		head.add("生效日期");
		head.add("缴费方式");
		head.add("投保人姓名");
		head.add("期缴保费");
		head.add("联系电话");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_CODE");
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("PRODUCT_NAME");
		key.add("ACCEPT_DATE");
		key.add("BIZ_TYPE");
		key.add("CLIENT_NAME");
		key.add("FEE");
		key.add("BARCODE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"扣费成功生效保单清单.xls");		
	}
	
	/**
	 * 客服信函打印清单
	 * @param map
	 */
	public void clientNoticeQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("信函类型");
		head.add("保单号");
		head.add("保单所属分公司");
		head.add("保单所属中支公司");
		head.add("客户号");
		head.add("客户姓名");
		head.add("地址");
		head.add("邮编");
		head.add("打印日期");
		
		List<String> key = new ArrayList<String>();
		key.add("BIZ_TYPE");
		key.add("POLICY_NO");
		key.add("POLICY_BRANCH_NAME");
		key.add("ACCEPT_BRANCH_NAME");
		key.add("CLIENT_NO");
		key.add("CLIENT_NAME");
		key.add("MEMO_TEXT");
		key.add("BARCODE");
		key.add("ACCEPT_DATE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName",map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"客服信函打印清单.xls");		
	}
	
	/**
	 * 回执清单--待回销清单
	 * @param pMap
	 */
	public void selectToConfirmReceipt(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("渠道");
		head.add("保单号");
		head.add("投保人");
//		head.add("投保人性别");
//		head.add("投保人电话");
//		head.add("被保人姓名");
//		head.add("缴费期");
//		head.add("保险期");
//		head.add("保费");
		head.add("险种");
		head.add("保单打印日期");
		head.add("客户签收日期");
		head.add("回执回销日期");
		head.add("回执扫描时间");
		head.add("业务员号");
		head.add("业务员姓名");
		head.add("回执来源");
		head.add("保单来源");
		
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("CHANNEL_TYPE");
		key.add("POLICY_NO");
		key.add("APPLICANT_NAME");
//		key.add("APPLICANT_SEX");
//		key.add("APPLICANT_PHONE");
//		key.add("INSURED_NAME");
//		key.add("PREM_TERM");
//		key.add("COVERAGE_PERIOD");
//		key.add("ANN_STANDARD_PREM");
		key.add("PRODUCT_FULL_NAME");
		key.add("PROVIDE_DATE");
		key.add("CLIENT_SIGN_DATE");
		key.add("CONFIRM_DATE");
		key.add("SCAN_TIME");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		key.add("RECEIPT_BUSINESS_SOURCE");
		key.add("APPLY_SOURCE_DESC");
		
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+map.get("dateFrom")+"至"+map.get("dateTo")+"扫描回执待回销清单.xls");
	}
	
	/**
	 * 回执清单--回执日期修改清单    包括{客户签署日期修改清单，回执录入日期修改清单}
	 * @param pMap
	 */
	public void selectDateChangedReceipt(Map map){
		String title = (String)map.get("listTitle");
		List<String> head = new ArrayList<String>();
		head.add("分公司");
		head.add("支公司");
		head.add("渠道");
		head.add("保单号");
		head.add("回执条码");
		head.add("日期类型");
		head.add("原值");
		head.add("新值");
		head.add("操作用户");
		head.add("操作日期");
		head.add("回执来源");
		head.add("备注");
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH2");
		key.add("BRANCH3");
		key.add("CHANNEL_TYPE");
		key.add("POLICY_NO");
		key.add("BAR_CODE");
		key.add("DATA_TYPE");
		key.add("OLD_VALUE");
		key.add("NEW_VALUE");
		key.add("PROCESSOR");
		key.add("PROCESS_DATE");
		key.add("RECEIPT_BUSINESS_SOURCE");
		key.add("REMARK");

		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+map.get("dateFrom")+"至"+map.get("dateTo")+"回执日期修改清单.xls");
	}

	/**
	 * 回执清单--问题件清单
	 * @param pMap
	 */
	public void selectProblemReceipt(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("渠道");
		head.add("保单号");
		head.add("投保人姓名");
//		head.add("投保人性别");
//		head.add("投保人电话");
//		head.add("被保人姓名");
//		head.add("缴费期");
//		head.add("保险期");
//		head.add("保费");
		head.add("险种名称");
		head.add("保单打印日期");
		head.add("客户签收日期");
		head.add("回执回销日期");
		head.add("回执扫描时间");
		head.add("业务员号");
		head.add("业务员姓名");
		head.add("问题件备注");
		head.add("回执来源");
		head.add("保单来源");
		head.add("问题件下发时间");

		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("CHANNEL_TYPE");
		key.add("POLICY_NO");
		key.add("APPLICANT_NAME");
//		key.add("APPLICANT_SEX");
//		key.add("APPLICANT_PHONE");
//		key.add("INSURED_NAME");
//		key.add("PREM_TERM");
//		key.add("COVERAGE_PERIOD");
//		key.add("ANN_STANDARD_PREM");
		key.add("PRODUCT_FULL_NAME");
		key.add("PROVIDE_DATE");
		key.add("CLIENT_SIGN_DATE");
		key.add("CONFIRM_DATE");
		key.add("SCAN_TIME");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		key.add("NOTE");
		key.add("RECEIPT_BUSINESS_SOURCE");
		key.add("APPLY_SOURCE_DESC");
		key.add("CREATED_DATE");

		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+map.get("dateFrom")+"至"+map.get("dateTo")+"回执回销问题件清单.xls");
	}
	
	/**
	 * 回执清单--报备保单清单
	 * @param pMap
	 */
	public void selectDelayMemoReceipt(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("渠道");
		head.add("保单号");
		head.add("投保人姓名");
//		head.add("投保人性别");
//		head.add("投保人电话");
//		head.add("被保人姓名");
//		head.add("缴费期");
//		head.add("保险期");
//		head.add("保费");
		head.add("险种名称");
		head.add("保单打印日期");
		head.add("业务员号");
		head.add("业务员姓名");
		head.add("报备时间");
		head.add("报备原因");
		head.add("已签收");
		head.add("回执来源");
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("CHANNEL_TYPE");
		key.add("POLICY_NO");
		key.add("APPLICANT_NAME");
//		key.add("APPLICANT_SEX");
//		key.add("APPLICANT_PHONE");
//		key.add("INSURED_NAME");
//		key.add("PREM_TERM");
//		key.add("COVERAGE_PERIOD");
//		key.add("ANN_STANDARD_PREM");
		key.add("PRODUCT_FULL_NAME");
		key.add("PROVIDE_DATE");
		key.add("AGENT_NO");
		key.add("AGENT_NAME");
		key.add("RECORD_DATE");
		key.add("UNARRIVE_CAUSE");
		key.add("SIGN_FLAG");
		key.add("RECEIPT_BUSINESS_SOURCE");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+map.get("dateFrom")+"至"+map.get("dateTo")+"报备保单清单.xls");
	}
	
	/**
	 * 回执清单--统计报表
	 * @param pMap
	 */
	public void queryReceiptFeedbackProportion(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("渠道");
		head.add("部门");
		head.add("总件数");
		head.add("未回销件数");
		head.add("件数未回销率");
		head.add("总保费");
		head.add("未回销保费");
		head.add("保费未回销率");
		head.add("平均未回销率");
		head.add("超期件数");
		head.add("件数超期回销率");
		head.add("超期保费");
		head.add("保费超期回销率");
		head.add("平均超期回销率");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("CHANNEL");
		key.add("BANK_NAME");
		key.add("POLICY_NO");
		key.add("CLIENT_NO");
		key.add("POS_NO");
		key.add("POLICY_BRANCH_CODE");
		key.add("ACCEPT_BRANCH_CODE");
		key.add("POS_ITEM_DESC");
		key.add("BIZ_TYPE");
		key.add("MOVE_IN_BRANCH_CODE");
		key.add("ACCEPTOR_NAME");
		key.add("MOVE_OUT_BRANCH_CODE");
		key.add("POLICY_STATUS");
		key.add("POS_STATUS");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("branchCode")+"机构"+df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"回执回销统计报表.xls");
	}
	
	/**
	 * 待处理抽样数据清单
	 * @param pMap
	 */
	public void selectTodealSampleInfo(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("险种名称");
		head.add("抽档应领日期");
		head.add("抽档应领金额");
		head.add("复核应领日期");
		head.add("复核应领金额");
		head.add("复核结果");
		head.add("复核人");
		head.add("复核确认结果");
		head.add("复核确认人");
		head.add("备注");
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("POLICY_NO");
		key.add("PRODUCT_NAME");
		key.add("GET_DUE_DATE");
		key.add("GET_DUE_SUM");
		key.add("REVIEW_GET_DUE_DATE");
		key.add("REVIEW_GET_DUE_SUM");
		key.add("REVIEW_RESULT");
		key.add("REVIEWER");
		key.add("REVIEW_CONFIRM_RESULT");
		key.add("REVIEW_CONFIRMER");
		key.add("RESULT_DESC");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", "生存金抽样待处理清单.xls");
	}
	
	/**
	 * 审批金额设置数据清单
	 * @param pMap
	 */
	public void selectPosAmountDaysPrivsSet(Map map){
		List<String> head = new ArrayList<String>();
		head.add("审批等级");
		head.add("受理渠道");
		head.add("保全项目");
		head.add("转账金额（原账户）");
		head.add("转账金额（非原账户）");
		head.add("代办现金金额");
		head.add("亲办现金金额");
		head.add("协议超出金额");
		head.add("免息金额");
		
		List<String> key = new ArrayList<String>();
		key.add("AMOUNT_GRADE");
		key.add("ACCEPT_CHANNEL");
		key.add("SERVICE_ITEM");
		key.add("SOURCE_TRANSFER_SUM");
		key.add("NOTSOURCE_TRANSFER_SUM");
		key.add("NOTSELF_CASH_SUM");
		key.add("SELF_CASH_SUM");
		key.add("TREATY_EXCEED_SUM");
		key.add("INTEREST_FREE_SUM");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", "审批等级金额.xls");
	}
	
	/**
	 * 邮储通知书打印清单
	 * @param pMap
	 */
	public void selectPostalNotesGather(Map map){
		List<String> head = new ArrayList<String>();
		head.add("文件目录");
		head.add("文件数量");
		head.add("外地信函份数");
		head.add("本地信函份数");
		
		List<String> key = new ArrayList<String>();
		key.add("DIRECTORY_NAME");
		key.add("FILE_COUNT");
		key.add("OTHER_COUNT_SUM");
		key.add("LOCAL_COUNT_SUM");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("dateFrom")+"至"+map.get("dateTo")+"邮储通知书打印清单.xls");
	}
	

	/**
	 * 受理状态修改 打印清单
	 * @param pMap
	 */
	public void selectStateModifyListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("保全项目名称");
		
		head.add("受理人");
		head.add("受理日期");
		head.add("备注");
	
		
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_FULL_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPTOR_NAME");
		key.add("ACCEPT_DATE");		
		key.add("REMARK");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", map.get("dateFrom")+"至"+map.get("dateTo")+"受理状态修改清单.xls");
	}
	
	/**
	 * 保全贴费业务清单
	 */
	public void surplusListQuery(Map map){
		List<String> head = new ArrayList<String>();
		head.add("机构名称");
		head.add("保单号");
		head.add("保全号");
		head.add("保全项目名称");
		head.add("受理日期");
		head.add("受理人");
		head.add("特殊件原因");
		head.add("应追回既得利益");		
		head.add("保全状态");
		head.add("备注");
		
		List<String> key = new ArrayList<String>();
		key.add("POLICY_BRANCH_NAME");
		key.add("POLICY_NO");
		key.add("POS_NO");
		key.add("POS_ITEM_DESC");
		key.add("ACCEPT_DATE");
		key.add("ACCEPTOR_NAME");
		key.add("BIG_TEXT");
		key.add("FEE");
		key.add("POS_STATUS");
		key.add("MEMO_TEXT");
		
		map.put("head", head);
		map.put("key", key);
		map.put("listName", df.format(map.get("startDate"))+"至"+df.format(map.get("endDate"))+"保全贴费业务清单.xls");
	}
	
}
