<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 复效 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		var cofirmed = false;
		posValidateHandler = function() {
			if($(".product").length == 0) {
				$.messager.alert("校验失败", "没有可复效的险种");
				return false;
			}
			if($(".product.primaryPlan:checked").length == 0) {
				$.messager.alert("校验失败", "复效必须选择主险");
				return false;
			}
			if($(".renewType:checked").val() == "1") {
				if(!cofirmed) {
					var selectedProduct = "";
					$(".product:checked").each(function(idx,content){
						selectedProduct += $($(".product:checked")[idx]).closest("tr").find(".product_name").html() + "<br/>";
					});
					$.messager.confirm("提示", "您选择了整单复效，即将复效以下险种，是否继续？<br/>" + selectedProduct, function(yes){
						if(yes) {
							cofirmed = true;
							$("[name='itemsInputForm']").submit();
						}
					});
					return false;
				}
			} else if($(".renewType:checked").val() == "3") {
				if($(".product:not(.primaryPlan):checked").length == 0) {
					$.messager.alert("校验失败", "您选择了“主险与部分附加险同时复效”，除主险外，请至少再选择一个险种");
					return false;
				}
			}
			return true;
		};
		initPage = function() {
			$("#specialFuncSelected,#specialFunc").change(function(){
				if($("#specialFuncSelected").is(":checked") && $("#specialFunc").is(":enabled") && $("#specialFunc option:selected").val() == "1") {
					$(".renewInterestInput").show();
					//选中了功能特殊件并且选择了复效免利息，则需要输入实收利息
					$("[name='renewInterest']").removeClass("ignore").attr("disabled", false).show();
				} else {
					$(".renewInterest").removeClass("ignore").addClass("ignore").attr("disabled", true).hide();
					$(".renewInterestInput").hide();
				}
				//选择复效免责任
			
				if($("#specialFuncSelected").is(":checked") && $("#specialFunc").is(":enabled") && $("#specialFunc option:selected").val() == "4")
				{					
					$(".noComeBack:checkbox").removeClass("ignore").show();
					$("span.noComeBack").hide();
				} else {
					$(".noComeBack:checkbox").removeClass("ignore").addClass("ignore").hide();
					$("span.noComeBack").show();
				}
			});
			
			$(".product").click(function(){
				cofirmed = false;
				var renewType = $(".renewType:checked").val();
				var $this = $(this);
				if(renewType == "1") {
					$this.attr("checked", true);
					return false;
				} else if(renewType == "2") {
					$this.attr("checked", $this.is(".primaryPlan"));
				} else if(renewType == "3") {
					if($this.is(".primaryPlan")) {
						$this.attr("checked", true);
					} else {
						var sign = this.checked;
						var productCode = $(this).prev().val();
						$(".product").not(this).each(
										function() {										
											if($(this).prev().val() ==productCode){
												$(this).attr("checked", sign);
											}
						});
						return true;
					}
				}
				return false;
			});
			
			$(".renewType").change(function(){
				$(".product").trigger("click");
			});
			
			$(".renewType").trigger("change");
		
			//在这里初始化是为了确保让代码在specialFuncSelected的trigger事件之前执行
			$(".renewInterest").removeClass("ignore").addClass("ignore").attr("disabled", true).hide();
			$(".renewInterestInput").hide();
		};
	</script>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<div class="layout_div_vspance">
		<table class="layouttable">
			<tbody>
				<tr>
					<th class="layouttable_td_label">
						复效类型:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:radiobutton path="renewType" value="1" label="整单复效" cssClass="renewType {required:true}"/>
						<sfform:radiobutton path="renewType" value="2" label="仅复效主险" cssClass="renewType {required:true}"/>
						<sfform:radiobutton path="renewType" value="3" label="主险与部分附加险同时复效" cssClass="renewType {required:true}"/>
					</td>
					<th class="layouttable_td_label renewInterestInput">
						复效实付利息:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget renewInterestInput">
						<sfform:input path="renewInterest" cssClass="renewInterest {required:true,number:true,min:0}"/>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
		
	<fieldset class="fieldsetdefault" style="padding:1px;">
		<legend>保单${acceptance__item_input.policyNo}险种明细：&nbsp;&nbsp;&nbsp;币种：人民币</legend>
		<table class="infoDisTab">
			<thead>
				<tr>
					<th width="15%">险种（代码）</th>
					<th>险种状态</th>
					<th>被保人</th>
					<th class="td_data" style="width:50px;">份数</th>
					<th class="td_data">保额</th>
					<th dataType="date">生效日期</th>
					<th dataType="date">交至日期</th>
					<th>缴费年期</th>
					<th>期缴保费</th>
					<th>缴费频次</th>
					<th>选择需复<br/>效险种</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.policyProductList}" var="item" varStatus="status">
				<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
					<td class="product_name">${item.productFullName}（${item.productCode}）</td>
					<td>${item.dutyStatusDesc}</td>					
					<td>${item.insuredName}</td>
					<td class="td_data" dataType="num">${item.units}</td>
					<td class="td_data" dataType="num">${item.baseSumIns}</td>
					<td><fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"/></td>
					<td><fmt:formatDate value="${item.premInfo.payToDate}" pattern="yyyy-MM-dd"/></td>
					<td>${item.premInfo.premTerm}</td>
					<td>${item.premInfo.periodPremSum}</td>
					<td>${item.premInfo.frequencyDesc}</td>			
				    <td>
				    	<input type="hidden" class="productCode" value="${item.productCode}"/>
					    <sfform:checkbox  path="policyProductList[${status.index}].selected"   cssClass="product ${item.isPrimaryPlan eq 'Y' ? 'primaryPlan' : ''} ${item.canBeSelectedFlag eq 'N'?'noComeBack':''}"/>
					    <c:if test="${item.canBeSelectedFlag eq 'N'}">
						     <span class="noComeBack"> 不可复效，${item.message}</span>
						</c:if>
			      </td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</fieldset>
	
	<c:if test="${acceptance__item_input.hasValidLoanOrApl}">
		<div class="layout_div_top_spance">
			<fieldset class="fieldsetdefault" style="padding:1px;">
				<legend>保单自垫及借款信息</legend>
				<table class="infoDisTab">
					<thead>
						<tr>
							<th class="td_data">自垫本金</th>
							<th class="td_data">自垫利息</th>
							<th class="td_data">借款本金</th>
							<th class="td_data">借款利息</th>
						</tr>
					</thead>
					<tbody>
						<tr class="odd_column">
							<td class="td_data">${empty acceptance__item_input.aplLoanSum ? 0 : acceptance__item_input.aplLoanSum}</td>
							<td class="td_data">${empty acceptance__item_input.aplInterestSum ? 0 : acceptance__item_input.aplInterestSum}</td>
							<td class="td_data">${empty acceptance__item_input.loanSum ? 0 : acceptance__item_input.loanSum}</td>
							<td class="td_data">${empty acceptance__item_input.interestSum ? 0 : acceptance__item_input.interestSum}</td>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
	</c:if>
</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
