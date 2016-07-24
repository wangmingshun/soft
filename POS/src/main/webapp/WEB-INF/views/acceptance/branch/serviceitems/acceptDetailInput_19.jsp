<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="headBlock">
<script type="text/javascript">
var vPolicyPhone = "";    //是否已全新得到过了保单的联系电话了

$("#phoneTb").empty();

//增加一行电话信息,这几个预留参数暂时没用
function phoneAddRow(phoneType, areaNo, phoneNo, phoneSeq){
	var c = "";
	var l = $("#phoneTb tr").length+1;
	if(l%2==0){
		c = "even_column";
	}else{
		c = "odd_column";
	}	
	var str = '';
		str += '<tr class="'+c+'">';
		str += '  <td><input type="checkbox" class="phoneCB" />' + ($("#phoneTb tr").length+1);
		str += '  </td>';
		str += '  <td>';
		str += '      <select class="inputPhoneType">';
		str += '        <c:forEach items="${CodeTable.posTableMap.PHONE_TYPE}" var="item">';
		str += '          <option value="${item.code}" >${item.description}</option>';
		str += '        </c:forEach>';
		str += '      </select>';
		str += '  </td>';
		str += '  <td>';
		str += '      <select class="inputForeignType">';
		str += '          <option value="F" >否</option>';
		str += '          <option value="T" >是</option>';
		str += '      </select>';
		str += '  </td>';
		str += '  <td><input type="text" size="5"  maxlength="5"  class="inputAreaNo" />';
		str += '  </td>';
		str += '      <td align="left">';
		str += '        <input type="text" size="14"  maxlength="14"name="'+Math.random()+'" class="inputPhoneNo {required:true}" />';
		str += '        <input type="hidden" class="inputPhoneSeq" />';
		str += '  </td>';
		str += '  <td>';
		str += '      <select class="inputStopOriginalPhoneNo">';
		str += '          <option value="N" >否</option>';
		str += '          <option value="Y" >是</option>';
		str += '      </select>';
		str += '  </td>';
		str += '</tr>';
		
	$("#phoneTb").append(str);
	isStopPhoneNo();//本次变更停用对应的原联系电话下拉框
	phoneInputChange();;
}

/*增加 本次变更停用对应的原联系电话下拉框  edit by wangmingshun start*/
function isStopPhoneNo() {
	var $change = $(".inputStopOriginalPhoneNo");
	var $type = $(".inputPhoneType");
	$change.each(function(index){
		$($change[index]).bind("change", function(){
			var type = $type[index].value;
			$change.each(function(ind){
				if(ind != index && $type[ind].value == type) {
					$change[ind].value = $change[index].value;
				}
			});
		});
		$($type[index]).bind("change", function(){
			var flag = false;
			$change.each(function(ind){
				if(ind != index && $type[ind].value == $type[index].value) {
					$change[index].value = $change[ind].value;
					flag = true;
				}
			});
			if(!flag) {
				$change[index].value = "N";
			}
		});
		$($change[index]).trigger("change");
	});
}
/*增加 本次变更停用对应的原联系电话下拉框  edit by wangmingshun end*/

//删除选中行的电话信息
function phoneDeleteRow(){	
	$(".phoneCB:checked").each(function(){
		$(this).parent().parent().remove();
	});
	phoneInputChange();;
}

$(function(){
	// 省 项改变
	$(".inputProvinceCode").change(function(){
		var $the = $(this);
		$.post("${ctx}include/queryCityByProvince.do", 
			{province:$the.val()},
			function(data) {
				$the.next().empty();
                $.each(data,function(i,map){
					var city = map["CITY_CODE"];
					if(city==null){
						city = "";
					}
                    $the.next().append("<option value='"+city+"'>"+map["CITY_NAME"]+"</option");
				});
				$the.siblings(".inputCityCode").trigger("change");
		    }, "json");
		$(".policyMoveCb:checked").trigger("change");
	});
	// 市 项改变
	$(".inputCityCode").change(function(){
		var $the = $(this);
		$.post("${ctx}include/queryAreaByCity.do", 
			{city:$the.val()},
			function(data) {
				$the.next().empty();
                $.each(data,function(i,map){
                    $the.next().append("<option value='"+map["AREA_CODE"]+"'>"+map["AREA_NAME"]+"</option");
				});
		    }, "json");
		$(".policyMoveCb:checked").trigger("change");
	});
	
	$(".inputCityCode:enabled").live("click",function(){
		if($(this).children().length<=1){
			$(this).siblings(".inputProvinceCode").trigger("change");
		}
	});
	$(".inputAddressAreaCode:enabled").live("click",function(){
		if($(this).children().length<=1){
			$(this).siblings(".inputCityCode").trigger("change");
		}
	});
	
	//迁移机构，所有选中的保单一起修改
	$(".inputPolicyBranchCode").change(function(){
		$(".inputPolicyBranchCode:enabled").val($(this).val());
	});
	
	//是否迁移的checkbox
	$(".policyMoveCb").change(function(){
		var str = "";
		var $enabCb = $(".policyMoveCb:enabled");
		if(this.checked==true){
			$enabCb.attr("checked",true);
			if($(".inputCityCode").val()!="" || $(".inputProvinceCode").val()!=""){
				$.post("${ctx}include/queryBranchInProvinceCity.do", 
					{province:$(".inputProvinceCode").val(),city:$(".inputCityCode").val(),channel:"${acceptance__item_input.acceptChannel}"},
					function(data) {
                		$.each(data,function(i,map){
                    		str += "<option value="+map["BRANCH_CODE"]+">"+map["BRANCH_NAME"]+"</option>";
						});
						$enabCb.parent().next().find(".inputPolicyBranchCode").empty().append(str).removeAttr("disabled");
		    	}, "json");
			}
		}else{
			$enabCb.attr("checked",false);
			$enabCb.parent().next().find(".inputPolicyBranchCode").attr("disabled","true");
			$enabCb.parent().next().find(".inputPolicyBranchCode").html('<option value="'+$enabCb.parent().parent().find(".originPolicyBranch").val()+'">'+$enabCb.parent().parent().find(".originPolicyBranch").attr("title")+'</option>');//取回原值
		}
	});
	
	//选择保单电话时，需实时取上面已经录入的新的电话
	$(".inputPolicyPhoneSeq:enabled ,.inputMobilePhoneSeq:enabled ,.inputHomePhoneSeq:enabled,.inputOfficePhoneSeq:enabled" ).live("click",function(){
		var $sel = $(this);
		//if($("#phoneTb tr").length<1){
			
	 if($("#phoneTb").find(".inputPhoneType option:selected:contains("+$sel.attr("title")+")").length==0){
			//var $tmp = $(this).parent().parent().find(".originPolicyPhoneSeq");
			var $tmp =$(this).prev();
			$sel.html('<option value="'+$tmp.val()+'">'+$tmp.attr("title")+'</option>');//取回原值
			return;
		}
		if(vPolicyPhone==("GOT"+$sel.attr("id"))){
			return;
		}
		  var str=''; 
		$("#phoneTb tr").each(function(){
			if($(this).find(".inputPhoneType option:selected:contains("+$sel.attr("title")+")").length>0){
			var aNo = $(this).find(".inputAreaNo").val();
			var no = $(this).find(".inputPhoneNo").val();
			var foreignType=$(this).find(".inputForeignType").val();
			if(foreignType=='T')
				{
				var	seq = "FAKE_"+$(this).find(".inputPhoneType").val()+'-'+aNo+'-'+no+'(境外)';//虚拟主键记录入数据库accept_detail
				}
			else
				{
				var	seq = "FAKE_"+$(this).find(".inputPhoneType").val()+'-'+aNo+'-'+no;//虚拟主键记录入数据库accept_detail
				}
			
			if(aNo!=""){
				aNo += "-";
			}//显示的时候区号加个杠			 
			    str += '<option value="'+seq+'">';
				str += $(this).find(".inputPhoneType option:selected").text();
				str += '：'+aNo+no+'</option>';
			$sel.append(str);
			}
		});
		  if(str!='')
			{
			$sel.empty();
			$sel.append(str);
			}
		vPolicyPhone = "GOT"+$sel.attr("id");//把我自己的id放上去，表明是我亲自得到过的
		$sel.trigger("change");
	});

	$(".inputPolicyPhoneSeq").change(function(){
		smsCbEnable($(this));
		//所有选中的保单的值一起变化
		var $the = $(".inputPolicyPhoneSeq:enabled").not($(this));
		$the.empty();
		$the.append($(this).html());
		//$the.val($(this).val());这样写才简洁但是只有ie支持
		$(".inputPolicyPhoneSeq:enabled option[value="+$(this).val()+"]").attr("selected",true);
		smsCbEnable($the);
	});
	
	$(".inputMobilePhoneSeq").change(function(){	
		//所有选中的保单的值一起变化
		var $the = $(".inputMobilePhoneSeq:enabled").not($(this));
		$the.empty();
		$the.append($(this).html());
		//$the.val($(this).val());这样写才简洁但是只有ie支持
		$(".inputMobilePhoneSeq:enabled option[value="+$(this).val()+"]").attr("selected",true);
		
	});
	
	$(".inputHomePhoneSeq").change(function(){	
		//所有选中的保单的值一起变化
		var $the = $(".inputHomePhoneSeq:enabled").not($(this));
		$the.empty();
		$the.append($(this).html());
		//$the.val($(this).val());这样写才简洁但是只有ie支持
		$(".inputHomePhoneSeq:enabled option[value="+$(this).val()+"]").attr("selected",true);
		
	});
	
	$(".inputOfficePhoneSeq").change(function(){	
		//所有选中的保单的值一起变化
		var $the = $(".inputOfficePhoneSeq:enabled").not($(this));
		$the.empty();
		$the.append($(this).html());
		//$the.val($(this).val());这样写才简洁但是只有ie支持
		$(".inputOfficePhoneSeq:enabled option[value="+$(this).val()+"]").attr("selected",true);
		
	});
	
	
	
	//如果电话录入域有变化，通知保单联系电话更新
	$("#phoneTb :input").live("change",function(){
		phoneInputChange();
	});
	
	//如果地址录入域有变化
	$("#addressTr :input").change(function(){
		if($(".addressCb").val()=='N'){
			
		}else{
			$(".policyAddress").each(function(){
				if($(this).parent().find(".policyContactCb").next().val()=="Y"){
					$(this).find(".newPolicyAddress").text($("#addressTr").find(".inputProvinceCode option:selected").text()+
		            	         $("#addressTr").find(".inputCityCode option:selected").text()+
							     $("#addressTr").find(".inputAddressAreaCode option:selected").text()+
								 $("#addressTr").find(".inputDetailAddress").val());
				}
			});
		}
	});
	
	//如果电邮录入域有变化
	$(".inputEmailAddress").change(function(){
		if($(".emailCb").val()=='N'){
			
		}else{
			$(".policyEmail").each(function(){
				if($(this).parent().find(".policyContactCb").next().val()=="Y"){
					$(this).find(".newPolicyEmail").text($(".inputEmailAddress").val());
				}
			});
		}
	});
	//保单行选中了才能操作修改
	$(".policyContactCb").change(function(){
		if(this.checked==true){
			$(this).next().val("Y");
			$(this).parent().parent().find(":input:not(.inputPolicySmsService,.inputPolicyBranchCode)").removeAttr("disabled");
			smsCbEnable($(this).parent().parent().find("select"));
			$(".inputDetailAddress, .inputEmailAddress").trigger("change");
			$(".policyMoveCb").trigger("change");
			$(".inputPolicyPhoneSeq").trigger("click");
		}else{
			$(this).next().val("N");
			$(this).parent().parent().find(":input").attr("disabled",true);
			$(this).removeAttr("disabled");
		}
	});
	
	$(".inputPolicySmsService").change(function(){
		if(this.checked==true){
	
			$(".inputPolicySmsService:enabled").attr("checked",true).next().val("Y");
			
		}else{
		
		$(".inputPolicySmsService:enabled").attr("checked",false).next().val("N");
			
		}
	});
	
	$(".emailCb, .addressCb").change(function(){
		if(this.checked==true){
			$(this).val("Y");
			$(this).parent().parent().find(":input").not($(this)).removeAttr("disabled");
		}else{
			$(this).val("N");
			$(this).parent().parent().find(":input").not($(this)).attr("disabled",true);
			$(this).parent().parent().find(":input").not($(this)).val("");
			
			//取消的话，清空
			if($(this).attr("name")=="address.checked"){
				$(".policyAddress").each(function(){
					if($(this).parent().find(".policyContactCb").next().val()=="Y"){
						$(this).find(".newPolicyAddress").text("");//$(this).parent().find(".originPolicyAddress").val()
					}
				});
			}
			if($(this).attr("name")=="email.checked"){
				$(".policyEmail").each(function(){
					if($(this).parent().find(".policyContactCb").next().val()=="Y"){
						$(this).find(".newPolicyEmail").text("");//$(this).parent().find(".originPolicyEmail").val()
					}
				});
			}
		}
	});
	$(".webMobilePhoneCb").change(function(){
		if(this.checked==true){
			$(this).val("Y");
			$(this).parent().parent().find(":input").not($(this)).removeAttr("disabled");
		}else{
			$(this).val("N");
			$(this).parent().parent().find(":input").not($(this)).attr("disabled",true);
			$(this).parent().parent().find(":input").not($(this)).val("");
			
		}
	});
	
	
	//初始化,入口保单必选
	$(".policyContactCb").each(function(){
		if($(this).prev().val()=="${acceptance__item_input.policyNo}"){
			$(this).attr("checked",true);
			$(this).trigger("change");
			$(this).attr("disabled",true);
		}
	});
	
	smsService();//edit by wangmignshun 初始化选中保单默认勾选短信服务
});

//电话录入域有变化
function phoneInputChange(){
	vPolicyPhone = "NEW";
	$(".inputPolicyPhoneSeq:enabled:first").trigger("click");
}

function smsCbEnable($the){
//选了手机才能勾选短信服务(默认选上)
   $the.each(function(){
		if($(this).attr('title')=='联系电话'){
			$(this).next().removeAttr("disabled");
			$(this).next().trigger("change");
		}
   });		
   }

/*给动态新增的域赋name*/
function addName(){
	var idx;
	//电话信息
	idx = 0;
	$("#phoneTb tr").each(function(){
		$(this).find(".inputPhoneType").attr("name","phoneList["+idx+"].phoneType");
		$(this).find(".inputForeignType").attr("name","phoneList["+idx+"].foreignType");
		$(this).find(".inputAreaNo").attr("name","phoneList["+idx+"].areaNo");
		$(this).find(".inputPhoneNo").attr("name","phoneList["+idx+"].phoneNo");
		$(this).find(".inputPhoneSeq").attr("name","phoneList["+idx+"].phoneSeq");		
		$(this).find(".inputStopOriginalPhoneNo").attr("name","phoneList["+idx+"].stopOriginalPhoneNo");		
		idx++;
	});

}

//提交前的验证和操作
var flagN = true;
posValidateHandler = function() {
	/*当新增联系人时 提醒是否做了保单层联系人变更 edit by wangmingshun start*/
	var policyStr = chenckPolicyNewPhoneChange();
	if(policyStr != "" && flagN) {
		$.messager.confirm("提示", policyStr + "请核实是否继续？", function(yes){
			if(yes) {
				againSubmit();
			} 
		});
		return false;
	}
	/*当新增联系人时 提醒是否做了保单层联系人变更 edit by wangmingshun end*/
	
	if($(".policyContactCb:checked").length<1){
		$.messager.alert("提示", "至少需要选中一个保单进行变更");
		return false;
	}
	
	if(phoneValidate()){
		return false;
	}
	addName();
	
	return true;
}

function againSubmit() {
	flagN = false;
	$("#itemsInputFormSubmitBt").trigger("click");
}

/*校验录入是否存在完全相同的电话*/
function phoneValidate(){
	var b = false;
	$("#phoneTb tr").each(function(idx){
		if(!b){
		 var p1 = $(this).find(".inputPhoneType").val()+$(this).find(".inputAreaNo").val()+$(this).find(".inputPhoneNo").val();
		 $("#phoneTb tr").each(function(id){
			 var p2 = $(this).find(".inputPhoneType").val()+$(this).find(".inputAreaNo").val()+$(this).find(".inputPhoneNo").val();
			 if(idx!=id && p1==p2){
				 alert("第"+(idx+1)+"行与"+(id+1)+"行电话信息相同,请修改！");
				 b = true;
				 return;
			 }
		 });
		}
	});
	return b;
}

/* policyContactCb change事件处理  edit by wangmingshun start */
function smsService(index) {
	if(index != null) {
		if($("#policyContactCb" + index).attr("checked") == true) {
			$("[id='policyContactList["+index+"].smsService']").attr("checked", true);
			$(".inputPolicySmsService:enabled").attr("checked",true).next().val("Y");
		} else {
			$("[id='policyContactList["+index+"].smsService']").attr("checked", false);
			$(".inputPolicySmsService:enabled").attr("checked",false).next().val("N");
		}
	} else {
		var $policyContactCb = $(".policyContactCb");
		$policyContactCb.each(function(index){
			if(this.checked == true) {
				$("[id='policyContactList["+index+"].smsService']").attr("checked", true);
				$(".inputPolicySmsService:enabled").attr("checked",true).next().val("Y");
			}
		});
	}
	//chenckPolicyNewPhoneChange();
}

//任务提交前检查 假设新增加了客户联系电话，如果被勾选的保单没有做出和新增类型相同的电话变更，则给出提示，但是不做控制
function chenckPolicyNewPhoneChange() {
	var $phoneTr = $("#phoneTb tr");
	if($phoneTr.length > 0) {
		//存在新增的做提醒，否则不做处理
		//取得所有保单对象
		var str = "";//记录保单是否做了联系方式变更
		var $policy = $(".infoDisTab tr");
		$policy.each(function(index){
			//保单被选中
			var policyTr = $($policy[index]).find(".policyNoSpan").text();
			if(policyTr.length > 0) {
				if($($policy[index]).find(".policyContactCb").attr("checked") == true) {
					var desc = "";
					var indx = "保单号[" + policyTr + "]存在如下信息未作变动：";
					//循环新增电话列表中的每条记录
					$phoneTr.each(function(idx){
						//获取每一条记录和保单中的电话信息进行对比，存在相同的则不做提示，否则做出提示
						var phoneType = $($phoneTr[idx]).find(".inputPhoneType").val();
						//1家庭电话、 2联系电话(对应保单)、3办公电话、4手机
						if(phoneType == '1') {
							var inputHomePhoneSeq = $($policy[index]).find(".inputHomePhoneSeq").text();
							if(inputHomePhoneSeq.indexOf("：") < 0)
								desc += "家庭电话、";
						}
						if(phoneType == '2') {
							var inputPolicyPhoneSeq = $($policy[index]).find(".inputPolicyPhoneSeq").text();
							if(inputPolicyPhoneSeq.indexOf("：") < 0)
								desc += "联系(保单)电话、";
						}
						if(phoneType == '3') {
							var inputOfficePhoneSeq = $($policy[index]).find(".inputOfficePhoneSeq").text();
							if(inputOfficePhoneSeq.indexOf("：") < 0)
								desc += "办公电话、";
						}
						if(phoneType == '4') {
							var inputMobilePhoneSeq = $($policy[index]).find(".inputMobilePhoneSeq").text();
							if(inputMobilePhoneSeq.indexOf("：") < 0)
								desc += "手机、";
						}
					});
					if(desc != "")
						str += indx + desc + "<br/>";
				}
			}
		});
		if(str != "") {
			return str;
		}
	}
	return "";
}
/* policyContactCb change事件处理  edit by wangmingshun end */

</script>
</sf:override>

<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">
  <br/>
  客户号：${acceptance__item_input.clientNo}&nbsp;&nbsp;
  客户姓名：${acceptance__item_input.clientName}
  
  <br />
  <br />
    
  客户联系电话
   <table class="infoDisTab">
     <tr>
      <th width="8%">选择/序号</th>
      <th width="12%">电话类型</th>
      <th width="12%">境外</th>
      <th width="12%">区号</th>
      <th align="20%">电话号码</th>
      <th align="left">本次变更停用对应的原联系电话</th>
     </tr>
     <tbody id="phoneTb">
     
     </tbody>
     <tr>
      <td colspan="6" align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="phoneAddBt" onclick="phoneAddRow();return false;">增加</a>
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="phoneDeleteBt" onclick="phoneDeleteRow();return false;">删除</a>
      </td>
     </tr>
   </table>  
  <br />
  
   <!-- <c:if test="${not empty acceptance__item_input.address.webMobilePhoneNo}" >
    保单加挂手机号码
    <table class="infoDisTab">
      <tr>
         <td align="right" width="10%"><input type="checkbox"  class="webMobilePhoneCb" name="address.webMobilePhoneChecked" />录入手机：</td>
         <td align="left" >
           <sfform:input path="address.webMobilePhoneNo" cssClass="inputMobilePhone" size="20" /> 
         </td>  
     </tr>
     <tr>
     </tr>
   </table>
   </c:if> 
   <br /> -->
   
   客户地址和电邮
   <table class="infoDisTab">
        <tr id="addressTr" >
         <td align="right" width="12%"><input type="checkbox" class="addressCb" value="N" name="address.checked" />录入联系地址：</td>
         <td align="left">
         <sfform:select path="address.provinceCode" cssClass="inputProvinceCode" disabled="true"
           items="${CodeTable.posTableMap.PROVINCE}" itemLabel="description" itemValue="code" >
         </sfform:select>
         省/自治区
         <sfform:select path="address.cityCode" cssClass="inputCityCode" disabled="true" ></sfform:select>
         市
         <sfform:select path="address.areaCode" cssClass="inputAddressAreaCode" disabled="true" ></sfform:select>
         区/县
         <sfform:input path="address.detailAddress" cssClass="inputDetailAddress {required:true}" size="40" disabled="true" />
         </td>
         <td width="20%">邮编：
           <sfform:input path="address.postalcode" cssClass="inputPostalcode {required:true,postalCode:true}" size="10" disabled="true" maxlength="6" />
         </td>
        </tr>
        <tr>
         <td align="right"><input type="checkbox" class="emailCb" value="N" name="email.checked" />录入联系电邮：</td>
         <td align="left" >
           <sfform:input path="email.emailAddress" cssClass="inputEmailAddress {required:true,email:true}" size="40" disabled="true" />
        
         </td>       
         <td class="layouttable_td_widget">
			 客服信函寄送:
			<sfform:radiobutton path="isSendLetter"  value="Y" label="是"/>
			<sfform:radiobutton path="isSendLetter" value="N" label="否"/>
		 </td>
        </tr>
       
   </table>

    <br />

   客户保单联系信息
   <table class="infoDisTab">
     <tr>
       <th width="8%">选择/序号</th>
       <th width="10%">保单号</th>
       <th width="32%">联系电话</th>
       <th width="10%">保单联系地址</th>
       <th width="12%">保单联系电邮</th>
       <th width="3%">是否迁移</th>
       <th width="15%">迁入机构</th>
     </tr>
     <c:forEach varStatus="status" items="${acceptance__item_input.policyContactList}" var="item">
        <c:if test="${status.index%2==0}">
            <tr class="odd_column">
        </c:if>
        <c:if test="${status.index%2!=0}">
            <tr class="even_column">
        </c:if>
       <td>
           	
    	<c:choose>
    		<c:when test="${empty item.verifyResult.ruleInfos}">
		         <sfform:hidden path="policyContactList[${status.index}].policyNo" />
		         <input type="checkbox" class="policyContactCb" onclick="smsService('${status.index}')" id="policyContactCb${status.index}" />${status.index+1}
		         <sfform:hidden path="policyContactList[${status.index}].checked" />
    		</c:when>
    		<c:otherwise>
    			<a href="javascript:void(0)" onclick="$('#unpassRuleDetailWindow${status.index}').window('open');return false;">规则检查不通过</a>
    			<div id="unpassRuleDetailWindow${status.index}" class="easyui-window" title="规则检查不通过详细信息"
				style="width:500px;height:300px;padding:5px;background: #fafafa;" closed="true" modal="true" collapsible="false" minimizable="false" maximizable="false">
					<div class="easyui-layout" fit="true">
						<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
							<ol style="padding:0;margin:0 0 0 28px;">
								<c:forEach items="${item.verifyResult.ruleInfos}" var="verifyResultItem">
									<li>${verifyResultItem.description}</li>
								</c:forEach>
							</ol>
						</div>
						<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
							<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="$('#unpassRuleDetailWindow${status.index}').window('close');return false;">关闭</a>
						</div>
					</div>
				</div>
    		</c:otherwise>
    	</c:choose>
    	
       </td>
       <td>
         <span class="policyNoSpan">${item.policyNo}</span>
         <input type="hidden" class="originPolicyBranch" value="${item.branchCode}" title="${item.branchName}" />
       </td>
       <td>
         <input type="hidden" class="originPolicyPhoneSeq" value="${item.phoneSeq}" title="${item.phoneDesc}" />
        保单： <sfform:select  title="联系电话"   path="policyContactList[${status.index}].phoneSeq" cssClass="inputPolicyPhoneSeq {required:true}" disabled="true" >
                   <sfform:option value="${item.phoneSeq}">${item.phoneDesc}</sfform:option>
         </sfform:select>
        <input type="checkbox" id="policyContactList[${status.index}].smsService" class="inputPolicySmsService" value="N"
           disabled="true" <c:if test="${item.smsService eq 'Y'}">checked="true"</c:if> />短信服务         
      	<input type="hidden" name="policyContactList[${status.index}].smsService"  value="${item.smsService}"  />         
    <br>
     <input type="hidden" class="originMobilePhoneSeq" value="${item.mobilePhoneSeq}" title="${item.mobilePhoneDesc}" />
         手机： <sfform:select  title="手机"   path="policyContactList[${status.index}].mobilePhoneSeq" cssClass="inputMobilePhoneSeq" disabled="true" >
           <sfform:option value="${item.mobilePhoneSeq}">${item.mobilePhoneDesc}</sfform:option>
         </sfform:select>
    
      
    <br>
    家庭：
     <input type="hidden"   class="originHomePhoneSeq" value="${item.homePhoneSeq}" title="${item.homePhoneDesc}" />
     <sfform:select   title="家庭"  path="policyContactList[${status.index}].homePhoneSeq" cssClass="inputHomePhoneSeq" disabled="true" >
           <sfform:option value="${item.homePhoneSeq}">${item.homePhoneDesc}</sfform:option>
         </sfform:select>
   
     <br>
     <input type="hidden"   class="originOfficePhoneSeq" value="${item.officePhoneSeq}" title="${item.officePhoneDesc}" />
     办公： <sfform:select  title="办公"   path="policyContactList[${status.index}].officePhoneSeq" cssClass="inputOfficePhoneSeq" disabled="true" >
           <sfform:option value="${item.officePhoneSeq}">${item.officePhoneDesc}</sfform:option>
         </sfform:select>
       
       </td>
       <td class="policyAddress">
       原值:${item.addressDesc}
       <input type="hidden" name="originAddrSeq${status.index}" value="${item.addressSeq}" " />
       <br />
       新值:<span class="newPolicyAddress"></span>
       </td>
       <td class="policyEmail">
       原值:${item.emailDesc}
       <br />
       新值:<span class="newPolicyEmail"></span>
       </td>
       <td>
         <input type="checkbox" class="policyMoveCb" disabled="true" />
       </td>
       <td valign="middle">
         <sfform:select path="policyContactList[${status.index}].branchCode" cssClass="inputPolicyBranchCode {required:true}" disabled="true" >
           <sfform:option value="${item.branchCode}">${item.branchName}</sfform:option>
         </sfform:select>
       </td>
     </tr>
     </c:forEach>     
   </table>

</sfform:formEnv>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>