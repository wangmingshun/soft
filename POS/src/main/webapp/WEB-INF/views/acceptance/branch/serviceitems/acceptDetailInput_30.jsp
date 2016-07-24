<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 保单质押贷款-贷款受理 --%>
<sf:override name="headBlock">
<script type="text/javascript" language="javascript">
$(function(){
	if($("#loanDeptNo :option").length<1){
		$("#noBankNote").text("该保单对应机构下没有可做贷款的银行");
	}
});
</script>

<style type="text/css">

</style>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	
    <br />
    保单${acceptance__item_input.policyNo}险种信息&nbsp;&nbsp;&nbsp;币种：人民币
	<table class="layouttable">
		<tbody>
			<tr>
				<td class="layouttable_td_label">保单冻结状态</td>
				<td class="layouttable_td_widget">${acceptance__item_input.policyMortgageDesc}</td>
				<td class="layouttable_td_label">险种状态</td>
				<td class="layouttable_td_widget">${acceptance__item_input.productStatusDesc}</td>
				<td class="layouttable_td_label">险种名称</td>
				<td class="layouttable_td_widget">${acceptance__item_input.productFullName}</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">投保人姓名</td>
				<td class="layouttable_td_widget">${acceptance__item_input.insuredName}</td>
				<td class="layouttable_td_label">投保人证件类型</td>
				<td class="layouttable_td_widget">${acceptance__item_input.insuredIdTypeName}</td>
				<td class="layouttable_td_label">投保人证件号码</td>
				<td class="layouttable_td_widget">${acceptance__item_input.insuredIdNo}</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">被保人姓名</td>
				<td class="layouttable_td_widget">${acceptance__item_input.applicantName}</td>
				<td class="layouttable_td_label">被保人证件类型</td>
				<td class="layouttable_td_widget">${acceptance__item_input.applicantIdTypeName}</td>
				<td class="layouttable_td_label">被保人证件号码</td>
				<td class="layouttable_td_widget">${acceptance__item_input.applicantIdNo}</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">保单现金价值</td>
				<td class="layouttable_td_widget">${acceptance__item_input.policyCashValue}</td>
				<td class="layouttable_td_label">满期日</td>
				<td class="layouttable_td_widget"><fmt:formatDate value="${acceptance__item_input.maturityDate}" pattern="yyyy-MM-dd"/></td>
				<td class="layouttable_td_label"></td>
				<td class="layouttable_td_widget"></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">贷款利率</td>
				<td class="layouttable_td_widget">
					<sfform:input path="loanInterest" cssClass="textfield {number:true,required:true}"/><font size="3">%</font>
				</td>
				<td class="layouttable_td_label">贷款金额</td>
				<td class="layouttable_td_widget">
					<sfform:input path="loanSum" cssClass="textfield {number:true,required:true}"/>
				</td>
				<td class="layouttable_td_label">贷款年限</td>
				<td class="layouttable_td_widget">
					<sfform:input path="loanLimitDate" cssClass="textfield {number:true}"/>
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">贷款网点</td>
				<td class="layouttable_td_widget" colspan="5">
					<sfform:select path="loanDeptNo" items="${acceptance__item_input.loanDeptList}" itemLabel="description" itemValue="code" cssClass="{required:true}" />
                    <span id="noBankNote"></span>
				</td>
			</tr>
		</tbody>
	</table>
</sfform:formEnv>
</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
