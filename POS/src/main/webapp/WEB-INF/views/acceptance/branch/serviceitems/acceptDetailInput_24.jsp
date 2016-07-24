<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 养老 年金给付方式变更 --%>

<sf:override name="headBlock">
<script type="text/javascript">
$(function(){
	
	$("#payTypeInput").change(function(){
		var v = $(this).val();
		
		$("#payPeriodTypePara").empty();
		
		if(v == "1" && "${acceptance__item_input.productFrequency}"=="1"){
			$.messager.alert("提示","趸缴的至祥养老和 颐养天年不能选择一次性给付");
			$(this).val("2");
			$(this).trigger("change");
			return;
			
		}else if(v == "1"){
			$("#payPeriodTypePara").append('<option value="0">一次给付</option>');
			$("#payPeriodType").val("4");
			$("#frequencyOne").show();
			$("#frequencySel").hide();
			
		}else if(v == "2"){
			$("#payPeriodTypePara").append('<option value="20">20年</option>');
			$("#payPeriodType").val("1");
			$("#frequencyOne").hide();
			$("#frequencySel").show();
			
		}else if(v == "3"){
			$("#payPeriodTypePara").append('<option value="80">80岁</option>');
			$("#payPeriodType").val("2");
			$("#frequencyOne").hide();
			$("#frequencySel").show();
			
		}else if(v == "4"){
			$("#payPeriodTypePara").append('<option value="30">30年</option><option value="40">40年</option>');
			$("#payPeriodType").val("1");
			$("#frequencyOne").hide();
			$("#frequencySel").show();
		}
	});

	$("#nextPayDate").click(function(){
		WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-01',minDate:'${acceptance__item_input.minDate}'});
	});
	
	$("#frequencySelect").change(function(){
		if("UBAN_BN1"=="${acceptance__item_input.productCode}" || "UIAN_CN0"=="${acceptance__item_input.productCode}"){
			if($(this).val()!="${acceptance__item_input.frequency}"){
				$("#nextPayDate").val("${acceptance__item_input.dateTransYM}");
			}
		}
	});
	
	init();
	
});

function init(){
	$("span.survival").hide();
	$("span.survival."+$("#productType").val()).show();
	
	$("#payTypeInput").trigger("change");
	
	$("#frequencySelect").trigger("change");
}

//提交前的操作，将隐藏的录入域干掉，以免产生值的干扰
posValidateHandler=function(){
	$("span.survival:hidden").remove();
	return true;
};

</script>
</sf:override>

<sf:override name="serviceItemsInput">

保单${acceptance__item_input.policyNo}年金给付方式信息：

<input type="hidden" id="productType" value="${acceptance__item_input.productType}" />
<input type="hidden" id="oldPayDate" value="${acceptance__item_input.nextPayDate}" />

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">原领取方式：
    </td>
    <td class="layouttable_td_widget" width="19%">${acceptance__item_input.typePeriodDesc}
    </td>
    <td class="layouttable_td_label" width="15%">新领取方式：
    </td>
    <td class="layouttable_td_widget">
     <span class="survival C">
          <select name="payType" id="payTypeInput">
              <option value="2">保证给付年限</option>
              <option value="3">身故返还本金</option>
              <option value="4">保证给付总额</option>
              <option value="1">一次性给付</option>
          </select>
       </span>
      <span class="survival U">
      	开始给付:<input type="text" name="nextPayDate" id="nextPayDate" 
        class="Wdate {required:function(){return '${acceptance__item_input.productType}'=='U'}}" />
       </span>
     <span class="survival C">
          <input type="hidden" name="payPeriodType" id="payPeriodType" />
                 给付:<select name="payPeriodTypePara" id="payPeriodTypePara">
          </select>
      </span>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">原领取频率：
    </td>
    <td class="layouttable_td_widget">
      <c:forEach items="${CodeTable.posTableMap.FREQUENCY}" var="item">
        <c:if test="${item.code eq acceptance__item_input.frequency}">${item.description}领</c:if>
      </c:forEach>
    </td>
    <td class="layouttable_td_label">新领取频率：
    </td>
    <td class="layouttable_td_widget">
      <span class="survival U C" id="frequencySel">
      <select name="frequency" id="frequencySelect">
        <c:if test = "${acceptance__item_input.productCode ne 'CBAN_DN1'}">
          <option value="2">月领</option>
        </c:if>
        <option value="5">年领</option>
      </select>
      </span>
      <span class="survival C" id="frequencyOne">
      <select name="frequency">
        <option value="0">无关</option>
      </select>
      </span>
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>