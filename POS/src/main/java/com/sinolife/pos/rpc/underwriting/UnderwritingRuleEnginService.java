package com.sinolife.pos.rpc.underwriting;

import java.util.Map;

import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 核保二核的自核和送人工核保接口。
 * 
 */
public interface UnderwritingRuleEnginService {

	/**
	 * 二核规则引擎接口定义
	 * @param clientInfoApp 投保人送核内容:<br/>
	 * <table border="1" cellspacing="0" cellspadding="0">
	 * 	<thead>
	 * 		<tr>
	 * 			<th>KEY值</th><th>含义</th><th>类型</th><th>备注</th>
	 * 		</tr>
	 * 	</thead>
	 * 	<tbody>
	 * 		<tr>
	 * 			<td>ACTORID</td><td>提交人编号</td><td>String</td><td>操作用户</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>PROCESSNAME</td><td>调用系统名称</td><td>String</td><td>系统名称</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>UNDERWRITERNO</td><td>核保员代码</td><td>String</td><td>是否送核需指定核保员，如果无，传入空</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>CLIENTNO</td><td>客户号</td><td>String</td><td>比传，客户代码</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>ISCOMPLEX</td><td>是否复杂件</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>UNWRTINFO</td><td>核保信息</td><td>List&lt;Map&lt;String,Object&gt;&gt;</td><td>送核信息列表【1】</td>
	 * 		</tr>
	 * 	</tbody>
	 * </table>
	 * 【1】	送核信息列表
	 * <table border="1" cellspacing="0" cellspadding="0">
	 * 	<thead>
	 * 		<tr>
	 * 			<th>KEY值</th><th>含义</th><th>类型</th><th>备注</th>
	 * 		</tr>
	 * 	</thead>
	 * 	<tbody>
	 * 		<tr>
	 * 			<td>UNDWRTTASKTYPE</td><td>核保任务类型 </td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>CONTROLNO</td><td>控制号：投保单号/保全号/理赔号</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>PRODSEQ</td><td>产品序号</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>PRODUCTCODE</td><td>产品编号 </td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>POLICYNO</td><td>保单号</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 	</tbody>
	 * </table>
	 * @param clientInfoIns 被保人送核内容 投保人，被保人送核map结构相同，只是客户代码和送核内容由二核准备
	 * @param bom 二核dto
	 * @return Map&lt;String, Object&gt; 
	 * <table border="1" cellspacing="0" cellspadding="0">
	 * 	<thead>
	 * 		<tr>
	 * 			<th>KEY值</th><th>含义</th><th>类型</th><th>备注</th>
	 * 		</tr>
	 * 	</thead>
	 * 	<tbody>
	 * 		<tr>
	 * 			<td>FLAG</td><td>送核成功标识 </td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>MESSAGE</td><td>送核信息描述</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>TASKCODE_APP</td><td>生成的投保人任务编号</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>TASKCODE_INS</td><td>生成的被保人任务编号</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>RULESRESULT</td><td>自核结果（Y自核通过，不需送核，N自核不通过，人工核保，3异常） </td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>UNDERWRITERNO</td><td>人工核保分配的核保员代码</td><td>String</td><td>&nbsp;</td>
	 * 		</tr>
	 * 	</tbody>
	 * </table>
	 */
	Map<String,Object> ruleCheckAndSendToManualWork(Map<String,Object> clientInfoApp, Map<String,Object> clientInfoIns, SUWBOMPolicyDto bom);
}
