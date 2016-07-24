<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 保险金转换年金 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("[name='relationship']").change(function(){
				if($(this).val() == "99"){
					$("#relationOther").show();
				} else {
					$("#relationOther").hide();
				}
			}).trigger("change");
			
			$("[name='withdrawType']").change(function(){
				var withdrawType = $(this).val();
				var $withdrawTimeLimit = $("#withdrawTimeLimit");
				if(withdrawType == "2" || withdrawType == "3") {
					$withdrawTimeLimit.show().removeClass("ignore").attr("disabled", false);
				} else {
					$withdrawTimeLimit.hide().removeClass("ignore").addClass("ignore").attr("disabled", true);
				}
			}).trigger("change");
			<%--
			$("#newApplicantNoQueryLink").click(function(){
				window.location.href = "${ctx}acceptance/branch/queryAndAddClient?newPolicyFlag=Y&policyNo=&clientNo=" + $("[name='newApplicantNo']").val() + 
				"&rewriteClientNoPropName=newApplicantNo&rewriteQAACPropName=qaaForApplicant";
				return false;
			});
			
			$("#newInsuredNoQueryLink").click(function(){
				window.location.href = "${ctx}acceptance/branch/queryAndAddClient?displayPolicyContact=N&policyNo=&clientNo=" + $("[name='newInsuredNo']").val() + 
				"&rewriteClientNoPropName=newInsuredNo&rewriteQAACPropName=qaaForInsured";
				return false;
			});
			--%>
		});
	</script>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<br/>
	<div>
	保单现有保险金信息：
		<c:choose>
			<c:when test="${acceptance__item_input.convertType eq '1'}">
				满期生存保险金
			</c:when>
			<c:when test="${acceptance__item_input.convertType eq '2'}">
				个人账户价值净额
			</c:when>
			<c:when test="${acceptance__item_input.convertType eq '3'}">
				现金价值
			</c:when>
		</c:choose>
		<span style="color: blue;">${acceptance__item_input.convertAmount}</span> 元
	</div>
	<div class="left">
		<c:if test="${acceptance__item_input.convertMode eq '2'}">
			申请转换金额：<sfform:input path="applyAmount" cssClass="textfield {number:true,required:true,min:0.1,max:${acceptance__item_input.convertAmount}}"/>
		</c:if>
		给付方式：
		<sfform:select path="withdrawType" cssClass="select {required:true}">
			<sfform:option value="1">一般终身年金</sfform:option>
			<sfform:option value="2">定期生存年金</sfform:option>
			<sfform:option value="3">保证年限的终身年金</sfform:option>
		</sfform:select>
		<span id="withdrawTimeLimit">
			领取期限：
			<sfform:select path="withdrawTimeLimit" cssClass="select {required:true}">
				<sfform:option value="10">10年</sfform:option>
				<sfform:option value="15">15年</sfform:option>
				<sfform:option value="20">20年</sfform:option>
				<sfform:option value="25">25年</sfform:option>
				<sfform:option value="30">30年</sfform:option>
			</sfform:select>
		</span>
		
		领取频次：<sfform:select path="withdrawFrequency" cssClass="select {required:true}"
					items="${CodeTable.posTableMap.FREQUENCY_MINI}" itemLabel="description" itemValue="code" />
	</div>
	<hr class="hr_default"/>
    
	新保单资料：
	<table class="layouttable">
		<tbody>
			<tr>
				<td class="layouttable_td_label" width="20%">新保单投保人客户号：</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.newApplicantNo}
					<%--
					<sfform:input path="newApplicantNo" cssClass="textfield {required:true}"/>
					<a href="javascript:void(0);" id="newApplicantNoQueryLink">查询投保人资料</a>
					--%>
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">新保单被保人客户号：</td>
				<td class="layouttable_td_widget">
					${acceptance__item_input.newInsuredNo}
					<%--
					<sfform:input path="newInsuredNo" cssClass="textfield {required:true}"/>
					<a href="javascript:void(0);" id="newInsuredNoQueryLink">查询被保人资料</a>
					--%>
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">新保单投保人与被保人的关系：</td>
				<td class="layouttable_td_widget">
					本人
					<%--
					<sfform:select path="relationship" cssClass="select {required:true}"
						items="${CodeTable.posTableMap.RELATIONSHIP}" itemLabel="description" itemValue="code"/>
					<span id="relationOther">
					其他关系描述：
					<sfform:input path="relationDesc" cssClass="textfield {required:function(){return $('[name=relationship]').val()=='99';}}" />
					</span>
					--%>
				</td>
			</tr>
		</tbody>
	</table>
</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
