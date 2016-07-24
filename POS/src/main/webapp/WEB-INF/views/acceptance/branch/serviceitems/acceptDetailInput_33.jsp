<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="headBlock">
<script type="text/javascript">
  $(function(){
	  $("#changePosNoInput").keydown(function(e){
		  if (e.keyCode == 13){
			  $.post("${ctx}acceptance/branch/queryPremInfoByPosNo33", 
			        {posNo:$(this).val(),
					 policyNo:"${acceptance__item_input.policyNo}"},
			         function(data) {
						if(data){
							 $("#serviceItems").text(data.SERVICE_ITEMS);
							 $("#premSum").text(data.PREM_SUM);
							 $("#paymentType").text(data.PAYMENT_TYPE);
							 $("#premFlag").text(data.PREM_FLAG);
							 $("#premName").text(data.PREM_NAME);
							 $("#bankName").text(data.BANK_ABBR);
							 $("#accountNo").text(data.ACCOUNT_NO);
							 $("#premSuccessFlag").text(data.PREM_SUCCESS_FLAG);
							 $("#changePosNo").val(data.POS_NO);
							 if(data.PREM_SUM==0){
								 $.messager.alert("提示", "目前该保全批单没有收付费纪录，无需调整");
								 $("#changePosNo").val("");
							 }
						}else{
							$.messager.alert("提示", "没有数据，请核实批单号或保单号");
							 $("#serviceItems").text("");
							 $("#premSum").text("");
							 $("#paymentType").text("");
							 $("#premFlag").text("");
							 $("#premName").text("");
							 $("#bankName").text("");
							 $("#accountNo").text("");
							 $("#premSuccessFlag").text("");
							 $("#changePosNo").val("");
						}
						 
		            }, "json");
		  }
	  });
	  
  });

</script>
</sf:override>

<!-- 批单收付方式变更 -->
<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="13%">批单号：</td>
    <td class="layouttable_td_widget" width="20%">
      <input type="text" id="changePosNoInput" title="输入后请按回车键" />
      <input type="hidden" id="changePosNo" name="changePosNo" class="{required:true,messages:{required:'请先输入后回车查询到正确的批单号'}}" />
    </td>
    <td class="layouttable_td_label" width="13%">保全项目：</td>
    <td class="layouttable_td_widget" id="serviceItems" width="20%">&nbsp;</td>
    <td class="layouttable_td_label" width="13%">金额：</td>
    <td class="layouttable_td_widget" id="premSum">&nbsp;</td>
  </tr>
  <tr>
    <td class="layouttable_td_label">原收付方式：</td>
    <td class="layouttable_td_widget" id="paymentType"></td>
    <td class="layouttable_td_label">原收付标志：</td>
    <td class="layouttable_td_widget" id="premFlag"></td>
    <td class="layouttable_td_label">原户主：</td>
    <td class="layouttable_td_widget" id="premName"></td>
  </tr>
  <tr>
    <td class="layouttable_td_label">原转账银行：</td>
    <td class="layouttable_td_widget" id="bankName"></td>
    <td class="layouttable_td_label">原转账账户：</td>
    <td class="layouttable_td_widget" id="accountNo"></td>
    <td class="layouttable_td_label">原转账状态：</td>
    <td class="layouttable_td_widget" id="premSuccessFlag"></td>
  </tr>
</table>

</sfform:formEnv>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>