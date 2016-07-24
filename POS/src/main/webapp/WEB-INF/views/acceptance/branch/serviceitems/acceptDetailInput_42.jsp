<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("form").validate();
			
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
					if(val=='3' || val=='5'){
						$(this).attr("disabled", "disabled");
						$(this).css("color", "#B0ABAA");
					}
				}); 
				
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
				if(payType == "2" && benefType != $(".benefitTypeSelected:checked").val()) {
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
			
			if(payType == "2" && $('.beneficiaryInfoChecked:checked').length<1)
				{
				$.messager.alert("提示", "没有受益人被选中，请选择!");
				return false;
				
				}
			
		
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
		
		   <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               保单原满期日期：
	        </td>
	        <td  class="layouttable_td_widget">
		           <input type="text" id="oldMaturityDate" name="oldMaturityDate"    value="${acceptance__item_input.oldMaturityDate}"  readonly="true" class="Wdate {required:true,messages:{required:' 请输入 保单原满期日期'}}"/>
	                 
	        </td> 
    
	      </tr> 
	      	     <tr>   
	        <td width="10%" class="layouttable_td_label">
	                              保单原生效日期：
	        </td>
	        <td  class="layouttable_td_widget">

		           <input type="text" id="oldEffectDate" name="oldEffectDate" value="${acceptance__item_input.oldEffectDate}"   readonly="true" class="Wdate {required:true,messages:{required:' 请输入保单原生效日期'}}"/>
	                      
	        </td> 
    
	      </tr> 
	      <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               保单新生效日期：<span class="requred_font">*</span>
	        </td>
	        <td  class="layouttable_td_widget">
		   
		          		   
	               <input type="text"  name="newEffectDate" id="newEffectDate"  class="Wdate {required:true,messages:{required:' 请输入保单新生效日期'}}"   onclick="WdatePicker({skin:'whyGreen'})" />   
	                     
	        </td> 
    
	      </tr> 
	</div>
	
	
	
    </sfform:formEnv>

</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
