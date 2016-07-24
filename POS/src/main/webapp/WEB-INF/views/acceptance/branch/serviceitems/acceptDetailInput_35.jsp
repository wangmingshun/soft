<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 投资账户转换 --%>
<c:choose>
	<c:when test="${acceptance__item_input.unitLinkedFlag eq 'Y'}">
		<%-- 投连账户 --%>
		<sf:override name="headBlock">
			<script type="text/javascript" language="javascript">
				posValidateHandler = function() {
					var $ratePercent = $(".ratePercent");
					var sum = 0, val;
					$ratePercent.each(function(idx){
						val = $($ratePercent[idx]).val();
						sum += parseInt(val);
					});
					if(sum != 100) {
						$.messager.alert("提示","分配比例之和必须为100");
						return false;
					}
					return true;
				};
				$(function(){
					$(".ratePercent").change(function(){
						var $ratePercent = $(".ratePercent");
						var percent = 0;
						$ratePercent.each(function(idx, content){
							percent += parseInt($($ratePercent[idx]).val());
						});
						$("#percentTotal").html(percent + "%");
					});
					$(".ratePercent").trigger("change");
				});
			</script>
		</sf:override>
		<sf:override name="serviceItemsInput">
		<sfform:formEnv commandName="acceptance__item_input">
			<br/>
			保单${acceptance__item_input.policyNo}投资账户明细信息：
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>投资账户名称</th>
						<th>投资账户累积单位数</th>
						<th>最近卖出价格</th>
						<th>最近卖出价值</th>
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
								<td>${item.amount}</td>
							</tr>
							<c:set var="idx" value="${idx + 1}"/>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
			<br/>
			<div class="left">
				追加保费金额：
				<sfform:input path="addPremAmount" cssClass="textfield {required:true,number:true,min:0}"/>
			</div>
			<br/>
			<div class="font_title left">本次变更账户分配明细：</div>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>投资账户名称</th>
						<th>分配比例（%）</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.financialProductsList}" var="item" varStatus="status">
						<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
							<td>${item.finProductsDesc}</td>
							<td>
								<sfform:select path="financialProductsList[${status.index}].ratePercent" items="${acceptance__item_input.rateList}" itemLabel="description" itemValue="code"
									cssClass="select ratePercent {required:true}"/>
							</td>
						</tr>
						<c:if test="${status.last}">
						<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">even_column</c:when><c:otherwise>odd_column</c:otherwise></c:choose>">
							<td>比例合计</td>
							<td>
								<label id="percentTotal"></label>
							</td>
						</tr>
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
			</script>
		</sf:override>
		<sf:override name="serviceItemsInput">
		<sfform:formEnv commandName="acceptance__item_input">
			保单${acceptance__item_input.policyNo}险种明细信息
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>险种</th>
						<th>险种状态</th>
						<th>被保人</th>
						<th>保额</th>
						<th>缴费频次</th>
						<th>期缴保费</th>
						<th>缴至日</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.policyProductList}" var="item" varStatus="status">
					<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.premInfo.premStatusDesc}</td>
						<td>${item.insuredName}</td>
						<td>${item.baseSumIns}</td>
						<td>${item.premInfo.frequencyDesc}</td>
						<td>${item.premInfo.periodPremSum}</td>
						<td><fmt:formatDate value="${item.premInfo.payToDate}" pattern="yyyy-MM-dd"/></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<br/>
			<div class="left">
				追加保费金额：
				<sfform:input path="addPremAmount" cssClass="textfield {required:true,number:true,min:0}"/>													
			</div>
		</sfform:formEnv>
		</sf:override>
	</c:when>
	<c:otherwise>
		<sf:override name="serviceItemsInput">
			非投连/万能产品，无法操作追加保费
		</sf:override>
	</c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
