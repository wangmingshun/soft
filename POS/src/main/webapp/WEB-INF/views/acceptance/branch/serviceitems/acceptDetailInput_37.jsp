<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 部分领取 --%>
<c:choose>
	<c:when test="${acceptance__item_input.unitLinkedFlag eq 'Y'}">
		<%-- 投连账户 --%>
		<sf:override name="headBlock">
			<script type="text/javascript" language="javascript">
				posValidateHandler = function() {
					var $withdrawUnits = $(".withdrawUnits");
					var changed = false;
					var val;
					$withdrawUnits.each(function(idx, content){
						val = $($withdrawUnits[idx]).val();
						if(val && parseFloat(val) > 0)
							changed = true;
					});
					if(!changed) {
						$.messager.alert("提示","请输入领取单位数");
						return false;
					}
					return true;
				};
			</script>
		</sf:override>
		<sf:override name="serviceItemsInput">
		<sfform:formEnv commandName="acceptance__item_input">
			<br/>
			<div class="font_title left">保单${acceptance__item_input.policyNo}投资账户明细信息：</div>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>投资账户名称</th>
						<th>投资账户累积单位数</th>
						<th>最近卖出价格</th>
						<th>本次领取单位数</th>
					</tr>
				</thead>
				<tbody>
					<c:set var="idx" value="0"/>
					<c:forEach items="${acceptance__item_input.financialProductsList}" var="item" varStatus="status">
						<c:if test="${item.amount gt 0}">
							<tr class="<c:choose><c:when test="${idx mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
								<td>${item.finProductsDesc}</td>
								<td>${item.units}</td>
								<td>${item.soldPrice}</td>
								<td>
									<sfform:input path="financialProductsList[${status.index}].withdrawUnits" cssClass="textfield withdrawUnits {number:true,max:${empty item.units ? 0 : item.units}}"/>
								</td>
							</tr>
							<c:set var="idx" value="${idx + 1}"/>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</sfform:formEnv>
		</sf:override>
	</c:when>
	<c:when test="${acceptance__item_input.universalFlag eq 'Y'}">
		<%-- 万能账户 --%>
		<sf:override name="headBlock">
			<script type="text/javascript" language="javascript">
			$(function() {
				/* 控制抵交续期保费的保单号 edit by yanghanguang start */
				$("#payPolicyTr").hide();
				$("#payType1").click(function() {
					$("#payPolicyTr").hide();
					$("#payPolicy").val("");			
				});
				$("#payType2").click(function() {
					$("#payPolicyTr").show();			
				});
				$("#payPolicy").blur(function() {
					var payPolicy = $("#payPolicy").val();
					var applyPolicy = '${acceptance__item_input.policyNo}';
					if(payPolicy==''||payPolicy==null){
						return false;
					}
					$.ajax({type: "GET",
						url:"${ctx}/acceptance/branch/acceptDetailInput/checkPaypolicyValid.do?applyPolicy="+applyPolicy,
						data:{"payPolicy" :payPolicy},
						cache: false,
						async: false,
						dataType:"text",
						success:function(data){
							if(data!='Y'){
						        $.messager.alert("校验错误", "保单号"+payPolicy+"无效，请核实后重新录入！", "info", function () {
									$("#payPolicy").val("");
									$("#payPolicy").focus();
									return false;						
						        });									
								return false;
							}
							
						}
					});				
				});				
				/* edit by gaojiaming end */
			});			
			</script>
		</sf:override>
		<sf:override name="serviceItemsInput">
		<sfform:formEnv commandName="acceptance__item_input">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>保单账户价值</th>
						<th>保单本年度领取次数</th>
						<th>本次领取现金价值</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd_column">
						<td>${acceptance__item_input.policyValue}</td>
						<td>${acceptance__item_input.withdrawTime}</td>
						<td>
							<sfform:input path="withdrawAmount" cssClass="textfield {required:true,number:true,min:0,max:${acceptance__item_input.policyValue}}"/>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="left">
				<table class="layouttable">
					<tr>
						<th class="layouttable_td_label" width="20%" >领取方式：
						 </th>
						<td class="layouttable_td_widget">
						<input type="radio" name="payType" id="payType1" checked="checked" />现金领取
						<input type="radio" name="payType" id="payType2" />抵交续期保费
						</td>							 				
					</tr>
					<tr id="payPolicyTr">
						<th class="layouttable_td_label" width="20%" >抵交续期保费的保单号：
						<span class="requred_font">*</span>
						 </th>						 
						<td class="layouttable_td_widget">
						<sfform:input path="payPolicy" cssClass="input_text {required:function(element){return $('#payType2').attr('checked')==true;}}"  id="payPolicy"/>
						</td>					
					</tr>															
				</table>													
			</div>					
		</sfform:formEnv>
		</sf:override>
	</c:when>
	<c:otherwise>
		<sf:override name="serviceItemsInput">
		非投连/万能产品，无法操作部分领取
		</sf:override>
	</c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
