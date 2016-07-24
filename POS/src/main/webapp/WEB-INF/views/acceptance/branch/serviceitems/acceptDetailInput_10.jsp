<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 生存保险金领取 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#payDate").click(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
			});
			$("form").validate();
			//针对E启赢投连险及红满堂年金保险,生存金领取方式中增加可选项：年金转账
			var productCodes = '${acceptance__item_input.productCodes}';
			if(!(productCodes.indexOf("CBAN_DN1")>-1||productCodes.indexOf("UEAN_AN1")>-1
				||productCodes.indexOf("UEAN_BN1")>-1||productCodes.indexOf("UEAN_CN1")>-1)){
				$("#payType option[value='7']").remove();
			}
			//应领记录选中状态变化处理
			$(".survivalDue").change(function(){
				var $survivalDueArr = $(".survivalDue");
				var $survivalDue, survBenefSpecialConvention, sum = 0;
				$survivalDueArr.each(function(idx, content){
					$survivalDue = $($survivalDueArr[idx]);
					if($survivalDue.is(":checked")) {
						sum += parseFloat($survivalDue.siblings(".survivalDueSum").val());
					}
				});
				$("#payAmount").html(sum);
			});
			
			//领取方式变化处理
			$("#payType").change(function(){
				$(".payTypeOption").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
				$(".payTypeOption.type" + $(this).val()).show().find(":input").attr("disabled", false).removeClass("ignore");
				$(".benefitTypeSelected").trigger("change");
				$('.benefitTypeSelected').each(function(){ 
					val = $(this).val();
					//edit by wangmingshun 放开“年金受益人”可以“做生存金领取转账授权” type='5'
					/*
					if(val=='3' || val=='5'){
						$(this).attr("disabled", "disabled");
						$(this).css("color", "#B0ABAA");
					}*/
					if(val=='3'){
						$(this).attr("disabled", "disabled");
						$(this).css("color", "#B0ABAA");
					}
				});
				//红满堂添加领取日期
				if(!($(this).val()=='7'&&productCodes.indexOf("CBAN_DN1")>-1)){
					$(".payTypeOption.type7.payDate").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
				}
			});
			
			//受益人类型选择
			$(".benefitTypeSelected").change(function(){
				$(".beneficiaryInfoChecked").trigger("change");
			});
			
			//受益人选中状态变化处理
			$(".beneficiaryInfoChecked").change(function(){
				var $tr = $(this).closest("tr");
				var $transferGrantFlag = $tr.find(".transferGrantFlag");
				var $inputs = $tr.find(":input:not(.beneficiaryInfoChecked)");
				var benefType = $tr.find(".benefitType").val();
				var payType = $("#payType").val();
				if((payType == "2" || payType == "7") && benefType != $(".benefitTypeSelected:checked").val()) {
					$tr.hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
					
				} else {
					$tr.show().find(":input").attr("disabled", false).removeClass("ignore");
					if($(this).is(":checked:visible")) {
						$transferGrantFlag.attr("disabled", false).removeClass("ignore").trigger("change");
					} else {
						$inputs.attr("disabled", true).removeClass("ignore").addClass("ignore");
					}					
					
				}
				var selectY = ($tr.find(".transferGrantFlag:checked").val() == "Y");
				var checkY = $tr.find(".beneficiaryInfoChecked").is(":not(:checked)");
				if(selectY&&!checkY){
					$("#accoutOwner"+benefType).val($tr.find("td[name=beneficiaryName]").text());
				}else{
					$("#accoutOwner"+benefType).val("");
				}
			});
			
			//是否授权标志
			$(".transferGrantFlag").change(function(){
				var $tr = $(this).closest("tr");
				var selectY = ($tr.find(".transferGrantFlag:checked").val() == "Y");
				$tr.find(":input:not(.beneficiaryInfoChecked):not(.transferGrantFlag)")
					.attr("disabled", !selectY).removeClass("ignore")
					.addClass(selectY ? "" : "ignore");
				var payType = $tr.find(".benefitType").val();
				if(selectY){
					$("#accoutOwner"+payType).val($tr.find("td[name=beneficiaryName]").text());
				}else{
					$("#accoutOwner"+payType).val("");
				}
			});
			
			//银行选择链接点击处理
			$(".bankSelectLink").click(function(){
				var $tr = $(this).closest("tr");
				if($tr.find(".beneficiaryInfoChecked").is(":not(:checked)") || $tr.find(".transferGrantFlag:checked").val() != "Y")
					return false;
				
				var $bankCode = $(this).siblings(".bankCode");
				showBankSelectWindow($bankCode, function(code, label){
					$bankCode.siblings("div").remove();
					$bankCode.closest("td").append($("<div class='left'/>").html(label));
				});
				return false;
			});
			
			$(".survivalDue").trigger("change");
			$("#payType").trigger("change");
		});
		
		posValidateHandler = function() {
			$(".survivalDue").trigger("change");
			var payType = $("[name='payType']").val();
			if(payType == "1" || payType == "5" || payType == "6") {
				if($(".survivalDue").length == 0) {
					$.messager.alert("提示", "没有可选的保单生存金应领记录");
					return false;
				}
				if(parseFloat($("#payAmount").html()) == 0) {
					$.messager.alert("提示", "请选择要处理的保单生存金应领记录");
					return false;
				}
			}
			
			
			var payType = $("#payType").val();
			
			if((payType == "2" || payType == "7") && $('.beneficiaryInfoChecked:checked').length<1)
				{
				$.messager.alert("提示", "没有受益人被选中，请选择!");
				return false;
				
				}
			/*校验红满堂领取日期 edit by gaojiaming start*/
			if($("#payType").val()=='7'&&'${acceptance__item_input.productCodes}'.indexOf("CBAN_DN1")>-1){
				var sysDate = new Date();
				var DATE_FORMAT = /^[0-9]{4}-[0-1]?[0-9]{1}-[0-3]?[0-9]{1}$/;
				if(!DATE_FORMAT.test($("#payDate").val())){
			        $.messager.alert("提示", "领取日期格式不正确！", "info", function () {
						return false;					
			        });
			        return false;	
				} 
				var payDate = new Date($("#payDate").val().replace(/-/ig,'/')); 
				if(sysDate-payDate < 0){
			        $.messager.alert("提示", "领取日期不能大于当前日期！", "info", function () {
						return false;					
			        });	
			        return false;	
				}				
			}
			/*校验红满堂领取日期 edit by gaojiaming end*/
			return true;
		};
	</script>
	<style type="text/css">
		.payTypeOption {
			display:none;
		}
	</style>
</sf:override>
<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
	<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>保单受益人信息：&nbsp;&nbsp;&nbsp;&nbsp;币种：人民币</legend>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th width="25">序号</th>
						<th>姓名</th>
						<th>与被保人关系</th>
						<th>受益人类型</th>
						<th>受益顺序</th>
						<th>受益比例</th>
						<th>证件类型</th>
						<th>证件号码</th>
						<th>出生日期</th>
						<th class="payTypeOption type2 type7 td_widget" width="25">选择</th>
						<th class="payTypeOption type2 type7">是否授权</th>
						<th class="payTypeOption type2 type7" style="width:80px;">账户户主姓名</th>
						<th class="payTypeOption type2 type7" style="width:160px;">授权银行</th>
						<th class="payTypeOption type2 type7" style="width:155px;">授权账号</th>
						<th class="payTypeOption type2 type7" style="width:155px;">再次输入授权账号</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.beneficiaryInfoList}" var="item" varStatus="status">
						<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
							<td>${item.benefitSeq}</td>
							<td name="beneficiaryName">${item.benefInfo.beneficiaryName}</td>
							<td>${item.relationshipDesc}</td>
							<td>
								${item.benefitTypeDesc}
								<input type="hidden" class="benefitType" value="${item.benefitType}" />
							</td>
							<td>${item.benefitSeq}</td>
							<td>${item.benefitRatePercent}%</td>
							<td>${item.benefInfo.idTypeDesc}</td>
							<td>${item.benefInfo.idno}</td>
							<td><fmt:formatDate value="${item.benefInfo.birthdate}" pattern="yyyy-MM-dd"/></td>
							<td class="payTypeOption type2 type7 td_widget">
								<sfform:checkbox path="beneficiaryInfoList[${status.index}].checked" cssClass="beneficiaryInfoChecked"/>
							</td>
							<td class="payTypeOption type2 type7" style="width:100px;">
								<sfform:radiobutton path="beneficiaryInfoList[${status.index}].transferGrantFlag" value="Y" label="是" cssClass="transferGrantFlag"/>
								<sfform:radiobutton path="beneficiaryInfoList[${status.index}].transferGrantFlag" value="N" label="否" cssClass="transferGrantFlag {required:true}"/>
							</td>
							<td class="payTypeOption type2 type7">
								<sfform:input path="beneficiaryInfoList[${status.index}].accountOwner" cssStyle="width:80px;" 
									id="accoutOwner${item.benefitType}" 
									cssClass="input_text {required:true}"/>
							</td>
							<td class="payTypeOption type2 type7">
								<sfform:input path="beneficiaryInfoList[${status.index}].bankCode" cssStyle="width:100px;" readonly="true"
									cssClass="input_text bankCode {required:true,messages:{required:'请选择银行'}}"/>
								<a href="javascript:void(0);" class="bankSelectLink">选择银行</a>
							</td>
							<td class="payTypeOption type2 type7">
								<sfform:input path="beneficiaryInfoList[${status.index}].accountNo" 
										cssClass="input_text accountNo${status.index} {required:true,messages:{required:'请录入授权账号'}} nopaste"
										cssStyle="width:155px;"/>
							</td>
							<td class="payTypeOption type2 type7">
								<input type="text" name="beneficiaryInfoList[${status.index}].accountNoConfirm"
									class="input_text {required:true,equalTo:'.accountNo${status.index}'} nopaste" style="width:155px;"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
	<div class="layout_div_top_spance payTypeOption type1 type5 type6">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>保单生存金应领信息：</legend>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>险种</th>
						<th>应领金额</th>
						<th>应领日期</th>
						<th>选择</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.survivalDueList}" var="item" varStatus="status">
						<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
							<td>${item.productFullName}（${item.productCode}）</td>
							<td>${item.payDueSum}</td>
							<td><fmt:formatDate value="${item.payDueDate}" pattern="yyyy-MM-dd"/></td>
							<td>
								<c:choose>
									<c:when test="${item.canBeSelectedFlag eq 'Y'}">
										<sfform:checkbox path="survivalDueList[${status.index}].checked" cssClass="survivalDue"/>
										<input type="hidden" value="${item.payDueSum}" class="survivalDueSum"/>
									</c:when>
									<c:otherwise>
										${item.message}
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
	
	<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>生存保险金领取：</legend>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>生存金领取方式</th>
						<th>领取本金合计</th>
						<th class="payTypeOption type5">转入保单号</th>
						<th class="payTypeOption type6">转入投保单号</th>
						<th class="payTypeOption type7 payDate">年金领取日期</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd_column">
						<td>
							<sfform:select path="payType" id="payType" cssClass="input_select {required:true}">
								<sfform:option value="1" label="现金领取"/>
								<sfform:option value="2" label="生存金转账领取授权"/>
								<sfform:option value="3" label="生存金自动抵交本单续期保费"/>
								<sfform:option value="4" label="生存金累积生息"/>
								<sfform:option value="5" label="生存金转保单暂收余额"/>
								<sfform:option value="6" label="生存金转投保单暂收余额"/>
							</sfform:select>
						</td>
						<td>
							<span id="payAmount">0</span>
						</td>
						<td class="payTypeOption type5">
							<sfform:input path="toPolicyNo" cssClass="input_text {required:true}"/>
						</td>
						<td class="payTypeOption type6">
							<sfform:input path="toApplyBarcode" cssClass="input_text {required:true}"/>
						</td>
						<td class="payTypeOption type7 payDate">
							<sfform:input path="payDate" id="payDate" cssClass="Wdate {required:true,messages:{required:' 请输入领取日期'}}"/>
						</td>						
					</tr>
				</tbody>
				<tfoot class="payTypeOption type2 type7" >
					<tr class="even_column">
						<td colspan="2">
							<c:forEach items="${acceptance__item_input.beneficiaryTypeList}" var="item">
								<sfform:radiobutton path="benefitTypeSelected" value="${item.code}" label="${item.description}" 
									cssClass="benefitTypeSelected {required:function(){return $('#payType').val() == '2';},messages:{required:'请选择受益人类型'}}"/>
							</c:forEach>
						</td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</div>
    </sfform:formEnv>
    <jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
