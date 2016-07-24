<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 保单质押贷款-还款解冻 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
		});
		
	</script>
    
	<style type="text/css">
	</style>
    
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	
    <br />
    保单${acceptance__item_input.policyNo}险种信息&nbsp;&nbsp;币种：人民币
	<table class="layouttable">
		<tbody>
			<tr>
				<td class="layouttable_td_label">保单冻结状态</td>
				<td class="layouttable_td_widget">
				    ${acceptance__item_input.freezeStatus}
				</td>
				<td class="layouttable_td_label">险种状态</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.productStatus}
				</td>
				<td class="layouttable_td_label">险种名称</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.productName}
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">投保人姓名</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.applicantName}
				</td>
				<td class="layouttable_td_label">投保人证件类型</td>
				<td class="layouttable_td_widget">
                  <c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
					<c:if test="${acceptance__item_input.applicantIdType eq item.code}">${item.description}</c:if>
                  </c:forEach>
				</td>
				<td class="layouttable_td_label">投保人证件号码</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.applicantIdType}
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">被保人姓名</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.insuredName}
				</td>
				<td class="layouttable_td_label">被保人证件类型</td>
				<td class="layouttable_td_widget">
				  <c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
					<c:if test="${acceptance__item_input.insuredIdType eq item.code}">${item.description}</c:if>
                  </c:forEach>
				</td>
				<td class="layouttable_td_label">被保人证件号码</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.insuredIdNo}
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">保单现金价值</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.cashValue}
				</td>
				<td class="layouttable_td_label">最大可贷金额</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.maxLoanSum}
				</td>
				<td class="layouttable_td_label">贷款利率</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.interestRate}
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">满期日</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.maturityDate}
				</td>
				<td class="layouttable_td_label">贷款金额</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.loanSum}
				</td>
				<td class="layouttable_td_label">贷款年限</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.loanDeadline}
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">贷款网点</td>
				<td class="layouttable_td_widget">
                    ${acceptance__item_input.deptNo}
				</td>
				<td class="layouttable_td_label">贷款网点名称</td>
				<td class="layouttable_td_widget">
                    ${acceptance__item_input.deptName}
				</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
			</tr>
		</tbody>
	</table>
    <hr class="hr_default" />

	请选择解冻原因：
	<sfform:radiobuttons path="unfreezeCause" cssClass="{required:true}" items="${CodeTable.posTableMap.POS_POL_MORT_UNFREEZE_CAUSE}" itemLabel="description" itemValue="code" />
        
</sfform:formEnv>

</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
