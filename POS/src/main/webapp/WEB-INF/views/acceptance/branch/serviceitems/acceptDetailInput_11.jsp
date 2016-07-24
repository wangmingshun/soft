<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 红利领取 --%>

<sf:override name="headBlock">
<script type="text/javascript" language="javascript">

$(function(){
	
	
	$(".divGrant").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
	
	//领取方式变化处理
	$("#drawType").change(function(){


		
		var payType=$("#drawType").val();
	    if(payType=="1"){
	    	$(".divGrant").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
	    	$(".divDrwa").show().find(":input").attr("disabled", false).removeClass("ignore");
	    	$("#policy").css('display','block');
	    	$("#tab").css('display','none');
	    	

	    	$("#bj").css('display','none');
	    	$("#hl").css('display','block');
	    	$("#divsum").css('display','none');
	    	$("#divDrwaSum").css('display','block');
	
	    }
	    else{
	    	
	    	
	    	$(".divGrant").show().find(":input").attr("disabled", false).removeClass("ignore");
	    	
	    	$(".divDrwa").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
	      	$("#policy").css('display','block');
	    	$("#tab").css('display','block');
	    	
	    	
	    	$("#bj").css('display','block');
	    	$("#hl").css('display','none');
	    	$("#divsum").css('display','block');
	    	$("#divDrwaSum").css('display','none');
	    	

	    	
	    }
	});
	
	
	//银行选择链接点击处理
	$(".bankSelectLink").click(function(){
		var $tr = $(this).closest("tr");
		if($tr.find(".beneficiaryInfoChecked").is(":not(:checked)") || $tr.find(".transfer_grant_flag:checked").val() != "Y")
			return false;
		
		var $bankCode = $(this).siblings(".bankCode");
		showBankSelectWindow($bankCode, function(code, label){
			$bankCode.siblings("div").remove();
			$bankCode.closest("td").append($("<div class='left'/>").html(label));
		});
		return false;
	});
	
	//是否授权标志
	$(".transfer_grant_flag").change(function(){
		
	
		
		var $tr = $(this).closest("tr");
	
		var selectY = ($tr.find(".transfer_grant_flag:checked").val() == "Y");
		$tr.find(":input:not(.beneficiaryInfoChecked):not(.transfer_grant_flag)")
		.attr("disabled", !selectY).removeClass("ignore")
		.addClass(selectY ? "" : "ignore");
	
		var name=$tr.find("td[name=clientName]").text();
		
		if(selectY){
			
			$("#accountOwner").val($tr.find("td[name=clientName]").text());
		
		}else{
		
			$("#accountOwner").val("");
		}
	});
	
	//受益人类型选择
	$("#drawType").change(function(){
		$(".beneficiaryInfoChecked").trigger("change");
	});
	
	//受益人选中状态变化处理
	$(".beneficiaryInfoChecked").change(function(){
	
		var $tr = $(this).closest("tr");
		var $transferGrantFlag = $tr.find(".transfer_grant_flag");
		var $inputs = $tr.find(":input:not(.beneficiaryInfoChecked)");
		
		
		if($(this).is(":checked:visible")) 
		{
		
		
			$transferGrantFlag.attr("disabled", false).removeClass("ignore").trigger("change");
		} else {
			
	
			$inputs.attr("disabled", true).removeClass("ignore").addClass("ignore");
			
		}	
		
		var se = ($tr.find(".beneficiaryInfoChecked").val() == "Y");
	
		$tr.find(":input:not(.beneficiaryInfoChecked):not(.transfer_grant_flag)")
		.attr("disabled", !selectY).removeClass("ignore")
		.addClass(selectY ? "" : "ignore");
		
		var selectY = ($tr.find(".transfer_grant_flag:checked").val() == "Y");
		
		
		var checkY = $tr.find(".beneficiaryInfoChecked").is(":not(:checked)");
		if(selectY&&!checkY){
		
			$("#accoutOwner").val($tr.find("td[name=clientName]").text());
			
		}else{
			
			$("#accoutOwner").val("");
		}
	});
});
</script>

</sf:override>


<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">

	<div align="left" id="policy">
	<fieldset class="fieldsetdefault" style="padding:1px;">
	<legend>保单红利信息：&nbsp;&nbsp;&nbsp;&nbsp;币种：人民币</legend>
	<table class="infoDisTab">
		<thead>
			<tr class="table_head_tr">
				<th>保单号</th>
                <th width="30%">险种</th>
				<th>红利分至日</th>
				<th>累积红利余额</th>
				<th>现金红利余额</th>
			</tr>
		</thead>
		<tbody>
          <c:forEach items="${acceptance__item_input.dividendList}" var="item" varStatus="status">
            <c:if test="${status.index%2==0}">
                <tr class="odd_column">
            </c:if>
            <c:if test="${status.index%2!=0}">
                <tr class="even_column">
            </c:if>
				<td>${item.policyNo}</td>
                <td>${item.productFullName}（${item.productCode}）</td>
				<td><fmt:formatDate value="${item.partToDate}" pattern="yyyy-MM-dd"/></td>
				<td>${item.interestDividendBal}</td>
				<td>${item.cashDividendBal}</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</fieldset>
	</div>

     <div class="layout_div_top_spance" id="tab"  style="display:none">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>投保人信息：&nbsp;&nbsp;&nbsp;&nbsp;币种：人民币</legend>
			<table class="infoDisTab">
				<thead>
					<tr >
						
						<th >姓名</th>
						
						<th >证件类型</th>
						<th >证件号码</th>
					
						<th  width="25">选择</th>
						<th >是否授权</th>
						<th  style="width:80px;">账户户主姓名</th>
						<th  style="width:160px;">授权银行</th>
						<th  style="width:155px;">授权账号</th>
						<th  style="width:155px;">再次输入授权账号</th>
					</tr>
				</thead>
				<tbody>
					
				
			 
			
					<c:forEach items="${acceptance__item_input.transList}" var="item" varStatus="status">
							
							<tr  class="divGrant">
					
							<td name="clientName">${acceptance__item_input.clientName}</td>
							<td>${acceptance__item_input.clientIdType}</td>
							<td>${acceptance__item_input.idNo}</td>	
						    <td >
							   <sfform:checkbox path="transList[${status.index}].check" cssClass="beneficiaryInfoChecked"/>
						
							</td>
							<td >
							  
								<sfform:radiobutton  path="transList[${status.index}].transfer_grant_flag" value="Y" label="是" cssClass="transfer_grant_flag"/>
								<sfform:radiobutton path="transList[${status.index}].transfer_grant_flag" value="N" label="否" cssClass="transfer_grant_flag {required:true}"/>
							</td>
							<td >
								<sfform:input  path="transList[${status.index}].account_owner" cssStyle="width:80px;"  id="accountOwner"
									
									cssClass="input_text {required:true}" readonly="true"/>
							</td>
							<td class="payTypeOption type2">
								<sfform:input id="bankcode" path="transList[${status.index}].bank_code" cssStyle="width:100px;" readonly="true"
									cssClass="input_text bankCode {required:true,messages:{required:'请选择银行'}}"/>
								<a href="javascript:void(0);" class="bankSelectLink">选择银行</a>
							</td>
							<td class="payTypeOption type2">
								<sfform:input id="accountNo" path="transList[${status.index}].account_no" 
									
										cssClass="input_text accountNo${status.index} {required:true,messages:{required:'请录入授权账号'}} nopaste"
										cssStyle="width:155px;"/>
							</td>
							<td class="payTypeOption type2">
								<input type="text"  name="transList[${status.index}].accountNoConfirm"
									class="input_text {required:true,equalTo:'.accountNo${status.index}'} nopaste" style="width:155px;" />
							</td>
					    </tr>
			
					</c:forEach>
		
				
				</tbody>
			</table>
		</fieldset>
	</div>
    <div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>红利领取：</legend>
			<table class="infoDisTab" id="type1">
				<thead>
					<tr>
						<th>红利方式</th>
						
						
	                  
	                    <th id="hl" align="left" style="display:block">本次领取红利金额</th>
	                    <th id="bj" style="display:none">领取本金合计</th>
	                  
					</tr>
				</thead>
				<tbody>
					<tr class="odd_column">
						<td>
							<sfform:select  path="drawType" id="drawType" cssClass="input_select {required:true}">
								
								<sfform:option value="1" label="现金领取"/>
								<sfform:option value="2" label="红利转账领取授权"/>
						
							</sfform:select>

						</td>
						
					
					 	<td class="divDrwa" id="divDrwaSum" align="left" style="display:block">     
			                          
					      <sfform:hidden path="dividendBalSum" />
					      <sfform:input name="drawSum" id="drawSum" path="drawSum" cssClass="easyui-numberbox {required:true,notBiggerThan:'#dividendBalSum',messages:{notBiggerThan:'领取金额不能超过保单红利余额总和'}}" precision="2" />
				      
				       </td> 
					
						<td id="divsum" style="display:none">
						   
						   
						    <c:forEach items="${acceptance__item_input.dividendList}" var="item" varStatus="status">
	        
							  <span>${item.interestDividendBal+item.cashDividendBal}</span>
							</c:forEach>
						</td>
						
				  </tr>
	             
				</tbody>
		
			</table>
		</fieldset>
	</div>

</sfform:formEnv>
  <jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
