<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="headBlock">
	<script type="text/javascript">
		$(function() {
			
			$("[name=appointmentDate]").click(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',minDate:'%y-%M-{%d+1}'});
			  });
			
			//银行选择链接点击处理
			$("#bankSelectLink").live("click", function(){
				var index = $("#bTrIndex").val();
				var bankCode = $("input[name=bankCode]");
				var arr = new Array();
				arr.push(bankCode);
				//arr.push(hideBankCode);
				showBankSelectWindow01(arr, function(code, label){
					$(".bankNameSpan").html(label);
				});
				return false;
			});
			
			$(".add").change(function() {
				if($(".add").attr("checked") == true) {
					$(".addServiceItem").show();
					$(".cancle").attr("checked", false);
					$(".infoDisTab").hide();
					$("input[name='appointmentType']").val("0");
					getPolicyBankAccountNoInfo();
					//getPolicyChannelType();//获取卡类型
				} else {
					$("input[name='appointmentType']").val("");
					$(".addServiceItem").hide();
				}
			});
			$(".cancle").change(function() {
				if($(".cancle").attr("checked") == true) {
					$(".infoDisTab").show();
					$(".add").attr("checked", false);
					$(".addServiceItem").hide();
					$("input[name='appointmentType']").val("1");
				} else {
					$("input[name='appointmentType']").val("");
					$(".infoDisTab").hide();
				}
			});
			
			$(".add").trigger("change");
			$(".cancle").trigger("change");
			$(".addServiceItem input").css("width", "167px");
		});
		
		posValidateHandler = function() {
			if($(".cancle").attr("checked") == true) {
				var $tr = $(".infoDisTab tbody tr");
				var isSubmit = false;
				$tr.each(function(n) {
					if($(this).find(".isCancle").attr("checked") == true) {
						isSubmit = true;
					}
				}); 
				
				if(!isSubmit) {
					$.messager.alert("提示","至少选择一项要取消的项目");
					return false;
				}
			}
			
			if($(".add").attr("checked") == true) {
				var originalAccountNo = $(".originalAccountNo");
				var accountNo = $(".addServiceItem").find("input[name='accountNo']");
				var accountNo1 = $(".addServiceItem").find("input[name='accountNo1']");
				if(accountNo.val() != accountNo1.val()) {
					$.messager.alert("提示","两次输入的银行账号不一致！请重新输入");
					return false;
				}
				
				//规则特殊键处理	勾选之后不做规则检查
				//对于保全项目“预约保全”，目前存在规则限制对该项目不能选择申请类型为“(12)代审”且受理渠道为“(5)银行柜面”操作。希望放开该规则。
				if($("#specialRuleSelected").attr("checked") == false) {
					if(accountNo.val() != originalAccountNo.val()) {
						var applyType = "${acceptance__item_input.applyType}";
						var acceptChannel = "${acceptance__item_input.acceptChannel}";
						if(applyType == '1' || applyType == '5' || (applyType == '12' && acceptChannel == '5')) {
							
						} else {
							$.messager.alert("提示","非原缴费账户只能选择亲办和内部申请");
							return false;
						}
					}
				}
			}
			
			$("select[name='idType']").attr("disabled", false);
			return true;
		};
		
		//获取保单原账户
		function getPolicyBankAccountNoInfo() {
			
			var policyNo = "${acceptance__item_input.policyNo}";
			var accoutNameVal = "${acceptance__item_input.accountName}";
			var accountName = $(".addServiceItem").find("input[name='accountName']");
			var bankCode = $(".addServiceItem").find("input[name='bankCode']");
			var accountNo = $(".addServiceItem").find("input[name='accountNo']");
			var originalAccountNo = $(".originalAccountNo");
			var accountNo1 = $(".addServiceItem").find("input[name='accountNo1']");
			var accountNoType = $(".addServiceItem").find("select[name='accountNoType']");
			
			if(bankCode.val() == "" && accountNo.val() == "") {
				$.post("${ctx}acceptance/branch/policybankacctno", {
					policyNo:policyNo
				}, function(data) {
					if(data.flag == 0) {
						accountName.val(data.acctName);
						bankCode.val(data.acctBankCode);
						accountNo.val(data.acctNo);
						originalAccountNo.val(data.acctNo);
						accountNo1.val(data.acctNo);
						accountNoType.val(data.acctNoType);
						$(".addServiceItem").find(".bankNameSpan").html(data.acctBankName);
						//检查原缴费账户是否和投保人账户同名，不同名的话，可以自行修改信息
						if(accoutNameVal != data.acctName) {
							accountName.attr("readonly", false).css("background-color", "#fff");
							$(".addServiceItem").find("select[name='idType']").attr("disabled", false);
							$(".addServiceItem").find("input[name='idNo']").attr("readonly", false).css("background-color", "#fff");
						}
					} else if(data.flag == 1) {
						$.messager.alert("提示","该保单无原账户信息");
					} else {
						$.messager.alert("对不起","获取保单账户失败，请联系技术人员支持");
					}
				});
			}
		}
		
		/*
		function getPolicyChannelType() {
			var accountNoType = $(".addServiceItem").find("select[name='accountNoType']");
			var policyNo = "${acceptance__item_input.policyNo}";
			$.ajax({type: "GET",
				url:"${ctx}acceptance/branch/queryPolicyChannelType.do",
				data:{"policyNo" : policyNo},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data=="06") {		
						accountNoType.append("<option value='1'>卡</option><option value='3'>信用卡</option>");
					} else {
						accountNoType.append("<option value='1'>卡</option>");
					}
				}});
		}
		*/
	</script>
</sf:override>

<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">操作类型：
    </td>
    <td class="layouttable_td_widget">
    	<input type="checkbox" class="add"/>保全预约
    	<input type="checkbox" class="cancle"/>取消预约
    	<input type="hidden" name="appointmentType" class="{required:true}"/>
    </td>
  </tr>
</table>

<table class="layouttable addServiceItem">
  <tr>
    <td class="layouttable_td_label" width="15%">服务项目：
    </td>
    <td>
	    <select name="appointmentServiceItem" style="width:169px;">
			<option value="2">退保</option>
			<option value="9">还款</option>
		</select>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">预约日期：
    </td>
    <td>
    	<input type="text" name="appointmentDate" class="Wdate {required:function(){return $('.add').attr('checked')},messages:{required:' 请输入预约日期'}} inputPara" />
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">账户名：
    </td>
    <td>
    	<input type="text" name="accountName" value="${acceptance__item_input.accountName }" class="{required:function(){return $('.add').attr('checked');}}" readonly="readonly"/>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">银行代码：
    </td>
    <td>
    	<input type="text" name="bankCode" readOnly="readonly" class="{required:function(){return $('.add').attr('checked');}}"/>
    	<a href="javascript:void(0);" id="bankSelectLink">选择银行</a><br/>
    	<span class="bankNameSpan"></span>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">银行卡类型：
    </td>
    <td>
    	<select name="accountNoType" class="{required:function(){return $('.add').attr('checked');}}">
    		<option value='1'>卡</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">银行账号：
    </td>
    <td>
    	<!-- 记录下保单原缴费账户  -->
    	<input type="hidden" class="originalAccountNo" value=""/>
    	<input type="text" name="accountNo" class="{required:function(){return $('.add').attr('checked');}}"/>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">再次输入银行账号：
    </td>
    <td>
    	<input type="text" name="accountNo1" class="{required:function(){return $('.add').attr('checked');}}"/>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">证件类型：
    </td>
    <td>
    	<select name="idType" disabled="disabled">
    		<c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
    			<option value="${item.code }" <c:if test="${item.code eq acceptance__item_input.idType }">selected</c:if> >${item.description }</option>
    		</c:forEach>
    	</select>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label" width="15%">证件号码：
    </td>
    <td>
    	<input name="idNo" value="${acceptance__item_input.idNo }" class="{required:function(){return $('.add').attr('checked');}}" readonly="readonly"/>
    </td>
  </tr>
</table>

<fieldset class="fieldsetdefault " style="padding:1px;">
<table class="infoDisTab">
	<thead>
		<tr>
			<th style="width:37px;" class="td_widget">选择</th>
			<th style="width:50px;">保全项目</th>
			<th style="width:37px;">预约日期</th>
			<th style="width:110px;">受理人</th>
			<th style="width:50px;">领款人</th>
			<th style="width:180px;">银行代码</th>
			<th style="width:120px;">银行账号</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${acceptance__item_input.posAppointmentInfoList }" var="item" varStatus="status">
			<tr>
				<td>
					<sfform:checkbox path="posAppointmentInfoList[${status.index}].isCancle" class="isCancle"/>
				</td>
				<td>${item.serviceItem }.${item.serviceItemDesc }</td>
				<td><fmt:formatDate value="${item.appointmentDate }" type="date"/></td>
				<td>${item.acceptUser }</td>
				<td>${item.premName }</td>
				<td>${item.bankCode }<div>${item.bankName }</div></td>
				<td>${item.accountNo }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</fieldset>
</sfform:formEnv>

<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>