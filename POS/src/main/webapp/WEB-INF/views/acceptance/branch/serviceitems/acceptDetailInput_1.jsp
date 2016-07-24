<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 犹豫期退保 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#wholePolicy").change(function(){
				$(".product.canBeSelected").attr("checked", this.checked);
			});
			$(".product").change(function(){
				$("#wholePolicy").attr("checked", ($(".product.canBeSelected").length == $(".product.canBeSelected:checked").length));
				if(!$(this).is(".canBeSelected")) {
					$(this).attr("checked", false).attr("disabled", true);
				}
				//DMP-13411家庭单修改
				if ($(this).prev().val() == "CPDD_CN1") {
						$("#wholePolicy").attr("checked", this.checked);
						$(".product").attr("checked", this.checked);
				}else{
					var sign = this.checked;
					var productCode = $(this).prev().val();
					$(".product").not(this).each(
									function() {										
										if($(this).prev().val() ==productCode){
											$(this).attr("checked", sign);
										}
					});
				}
				
				/*else{
					//DMP-13411家庭单修改
					var productCode = $(this).val();
					var productCode = $(this).val();
					var sign = this.checked;
					if(productCode=="CPDD_CN1"){
						$("#wholePolicy").attr("checked", this.checked);
						$(".product").not(this).attr("disabled", this.checked);
						$(".product").attr("checked", this.checked);
					}
					$(".product").not(this).each(
								function() {
									if($(this).val() ==productCode){
										$(this).attr("checked", sign);
									}
					});*/
					
			});
			$("#is_doubt").change(function(){
				
				if($("#is_doubt").is(":checked"))
					{
					$("#doubt").show();
					}
				else
			        {
					 $("#doubt").attr("style","display:none");
					}
			});
			$(".primaryPlan").change(function(){
				if(this.checked) {
					$(".product.canBeSelected:not(.primaryPlan)").attr("checked", true).trigger("change");
				}
			});

			$(".product").trigger("change");

		});
		posValidateHandler = function() {		
			var b = true;
			if($("#is_doubt").is(":checked"))
			{
			if($(".dbdoubtList:checked").length==0)
			{
				$.messager.alert("提示", "如果是可疑交易必须选择可疑交易类型!");  
				return false;
			}
			}
			return b;
		};
	</script>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
 <c:if test="${TRIAL_FLAG ne 'Y'}">
        是否可疑交易：
<input type="checkbox"  id="is_doubt" name="douBt"  />
    <br />
    <table class="infoDisTab"    id="doubt" style="display:none">
    <thead class="odd_column">
    <tr>
         <th width="9%">选择</th>
         <th>可疑交易类型</th>
    </tr>
    </thead>
    <tbody  class="even_column">
    <tr>
         <th><input type="checkbox" name="doubtList" value="1304" class="dbdoubtList" /></th>
         <th> 犹豫期退保时称大额发票丢失的，或者同一投保人短期内多次退保遗失发票总额达到大额的</th>
    </tr>
     <tr >
         <th><input type="checkbox" name="doubtList" value="1308" class="dbdoubtList" /></th>
         <th>大额保费保单犹豫期退保、保险合同生效日后短期内退保或者提取现金价值，并要求退保金转入第三方账户或者非缴费账户的</th>
    </tr>
     <tr>
         <th><input type="checkbox" name="doubtList" value="1309" class="dbdoubtList" /></th>
         <th> 不关注退保可能带来的较大金钱损失，而坚决要求退保，且不能合理解释退保原因的</th>
    </tr>
    </tbody>
    </table>
	<br/>
	</c:if>
	<div class="font_title left">
		<c:if test="${not empty acceptance__item_input.receiptPutoffDate}">
			保单报备日期：
			<fmt:formatDate value="${acceptance__item_input.receiptPutoffDate}" pattern="yyyy-MM-dd"/>
			&nbsp;&nbsp;&nbsp;
		</c:if>
		<span>
			<input type="checkbox" id="wholePolicy"/>
			<label for="wholePolicy">整单契撤</label>
		</span>
		&nbsp;&nbsp;&nbsp;
		币种：人民币
        
	</div>
    <div>
      <input type="hidden" name="checkOneAtLeast" class="{required:function(){return $('.product:checked').length<1},messages:{required:'请至少选择一个产品'}}" />
    </div>
	<table class="infoDisTab">
		<thead>
			<tr>
				<th style="width:30px;">选择</th>
				<th>险种</th>
				<th style="width:65px;">险种状态</th>
				<th style="width:100px;">被保人</th>
				<th style="width:75px;">生效日期</th>
				<th style="width:80px;">期缴保费</th>
				<th style="width:65px;">是否主险</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${acceptance__item_input.policyProductList}" var="item" varStatus="status">
			<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
				<td>
					<input type="hidden" class="productCode" value="${item.productCode}"/>
					<sfform:checkbox path="policyProductList[${status.index}].selected" cssClass="product ${item.isPrimaryPlan eq 'Y' ? 'primaryPlan' : ''} ${item.canBeSelectedFlag eq 'Y' ? 'canBeSelected' : ''} "/>
				</td>
				<td>${item.productFullName}（${item.productCode}）</td>
				<td>${item.dutyStatusDesc}</td>
				<td>${item.insuredName}</td>
				<td><fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"/></td>
				<td>${item.premInfo.periodPremSum}</td>
				<td>${item.isPrimaryPlan eq 'Y' ? '是' : '否'}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
    <br />
	契撤原因：<sfform:select path="surrenderCauseCode" items="${CodeTable.posTableMap.POS_SURRENDER_CAUSE_CODE}" itemLabel="description" itemValue="code"/>

</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
