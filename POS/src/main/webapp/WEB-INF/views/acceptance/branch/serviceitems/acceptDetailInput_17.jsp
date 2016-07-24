<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 续期保费退费 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
	$(function() {
		getFinPosPayInfo();
	});
	
	function getFinPosPayInfo() {
		var policyNo = "${acceptance__item_input.policyNo}";
		var prem = "${acceptance__item_input.policyBalance}";
		var periodPrem = "${acceptance__item_input.periodPrem}";
		$.ajax({type: "GET",
			url:"${ctx}acceptance/branch/getFinPosPayInfo.do",
			data:{"policyNo" : policyNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				var flag = data.p_flag;
				var creditFlag = data.creditFlag;
				var accountNo = data.accountNo;
				var premDue = data.premDue;
				//alert(flag+","+creditFlag+","+accountNo+","+premDue+","+prem+","+periodPrem)
				if(flag == 'Y' && creditFlag == 'Y' && prem >= periodPrem) {
					$("input[name='refundAmount']").attr("readonly", "readonly").css("background","#d4d0c8").val(premDue);
				}
			}});
	}
	</script>
	<style type="text/css">
	</style>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<div class="layout_div_top_spance font_heading2">&nbsp;保单余额：RMB${acceptance__item_input.policyBalance}元</div>
	<div class="layout_div_top_spance">
		<table class="layouttable">
			<tbody>
				<tr>
					<td class="layouttable_td_label">退费金额：</td>
					<td class="layouttable_td_widget">
						<sfform:input path="refundAmount" cssClass="input_text {required:true,number:true,min:0.01,max:${acceptance__item_input.policyBalance}}"/>
					</td>
					<td class="layouttable_td_label">退费原因：</td>
					<td class="layouttable_td_widget">
						<sfform:select path="refundCauseCode" items="${CodeTable.posTableMap.POS_RENEW_REFUND_CAUSE_CODE}" itemLabel="description" itemValue="code"
							cssClass="input_select {required:true}"/>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
