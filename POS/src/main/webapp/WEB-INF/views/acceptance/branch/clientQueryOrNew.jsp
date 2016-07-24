<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 新增或查询客户信息 查不到就是新增  投保人变更项目启用 --%>

<sf:override name="pathString">客户信息查询及新增</sf:override>
<sf:override name="head">
<script type="text/javascript">
var ignoreSexUnmatch = false;		//忽略性别不匹配
var ignoreBirthdayUnmatch = false;	//忽略生日不匹配
var vPolicyPhone = "";    //是否已全新得到过了保单的联系电话了
var vPolicyAddress = "";
var vPolicyEmail = "";
  $(function(){
	// 省 项改变
	$(".inputProvinceCode").live("change",function(){
		var $the = $(this);
		$.post("${ctx}include/queryCityByProvince", 
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
	});
	// 市 项改变
	$(".inputCityCode").live("change",function(){
		var $the = $(this);
		$.post("${ctx}include/queryAreaByCity", 
			{city:$the.val()},
			function(data) {
				$the.next().empty();
                $.each(data,function(i,map){
                    $the.next().append("<option value='"+map["AREA_CODE"]+"'>"+map["AREA_NAME"]+"</option");
				});
		    }, "json");
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
	
	$(".birthdayInput").click(function(){
		WdatePicker({skin:'whyGreen'});
	});
	
	$(".birthdayInput, .sexCodeInput, .idNoInput").change(function(){
		ignoreSexUnmatch = false;
		ignoreBirthdayUnmatch = false;
	});
	
	$(".inputPolicyPhoneSeq").change(function(){
		if($(this).find("option:selected:contains(手机)").length>0){
			$(this).next().removeAttr("disabled");
			$(this).next().attr("checked",true);
		}else{
			$(this).next().removeAttr("checked");
			$(this).next().attr("disabled","true");
		}
		$(".inputPolicySmsService").trigger("change");
	});
	//短信服务选项
	$(".inputPolicySmsService").change(function(){
		if(this.checked==true){
			$(this).next().val("Y");
		}else{
			$(this).next().val("N");
		}
	});

	//如果电话录入域有变化，通知保单联系电话更新
	$("#phoneTb :input").live("change",function(){
		vPolicyPhone = "NEW";
	});
	//选择保单电话时，需实时取上面已经录入的新的电话
	$(".inputPolicyPhoneSeq").click(function(){
		var $sel = $(this);
		if(vPolicyPhone=="GOT" || $("#phoneTb tr").length<1)return;
		$sel.empty();
		$("#phoneTb tr").each(function(){
			var aNo = $(this).find(".inputAreaNo").val();
			var no = $(this).find(".inputPhoneNo").val();
			var	seq = "FAKE_"+$(this).find(".inputPhoneType").val()+'-'+aNo+'-'+no;//虚拟主键记录入数据库accept_detail
			if(aNo!=""){
				aNo += "-";
			}//显示的时候区号加个杠
			var str = '';
			    str += '<option value="'+seq+'">';
				str += $(this).find(".inputPhoneType option:selected").text();
				str += '：'+aNo+no+'</option>'
			$sel.append(str);
		});
		vPolicyPhone = "GOT";
		$sel.trigger("change");
	});
	//如果地址录入域有变化
	$("#addressTb :input").live("change",function(){
		vPolicyAddress = "NEW";
	});
	//选择保单地址时，需实时取上面已经录入的新的地址
	$(".inputPolicyAddressSeq").click(function(){
		var $sel = $(this);
		if(vPolicyAddress=="GOT" || $("#addressTb tr").length<1)return;
		$sel.empty();
		$("#addressTb tr").each(function(){
			var seq = $(this).find(".inputAddressSeq").val();
			var detail = $(this).find(".inputDetailAddress").val();
			
			if(seq==""){
				seq = "FAKE_"+$(this).find(".inputProvinceCode").val()+'-'+
							  $(this).find(".inputCityCode").val()+'-'+
							  $(this).find(".inputAddressAreaCode").val()+'-'+
							  detail;
			}
			
			var str = '';
			    str += '<option value="'+seq+'">';
				str += $(this).find(".inputProvinceCode option:selected").text();
				str += $(this).find(".inputCityCode option:selected").text();
				str += $(this).find(".inputAddressAreaCode option:selected").text();
				str += detail+'</option>';
			$sel.append(str);
		});
		vPolicyAddress = "GOT";
	});
	//如果电邮录入域有变化
	$("#emailTb :input").live("change",function(){
		vPolicyEmail = "NEW";
	});
	//选择保单电邮时，需实时取上面已经录入的新的电邮
	$(".inputPolicyEmailSeq").click(function(){
		var $sel = $(this);
		if(vPolicyEmail=="GOT" || $("#emailTb tr").length<1)return;
		$sel.empty();
		$("#emailTb tr").each(function(){
			var seq = $(this).find(".inputEmailSeq").val();
			if(seq==""){
				seq = "FAKE_"+$(this).find(".inputEmailAddress").val();
			}
			var str = '';
			    str += '<option value="'+seq+'">';
				str += $(this).find(".inputEmailAddress").val()+'</option>';
			$sel.append(str);
		});
		vPolicyEmail = "GOT";
	});
	//输入职业代码查询
	$(".occupationCodeInput").keydown(function(e,k){
		if (e.keyCode == 13 || k=="Y"){
		  $("#occupationGradeDescTd").text("");
		  $.post("${ctx}acceptance/branch/queryOccupationInfo", 
			 {occupationCode:$(this).val()},
			 function(data){
				 if(data){
					 $("#occupationGradeDescTd").text(data.GRADE_DESC);
					 $("#occupationCodeHid").val("Y");
				 }else{
					 $("#occupationGradeDescTd").text("");
					 $("#occupationCodeHid").val("");
				 }
			 },
			 "json"
		  );
		}
	});	
	
	$("#clientNo").change(function(){
		$("form[name='clientInfoQueryForm']").attr("action","${ctx}acceptance/branch/queryClientByClientNo");
		$("form[name='clientInfoQueryForm']").submit();
		return false;
		
	});
	//根据5项信息查询
	$("#clientInfoQueryBt").click(function(){
		$("form[name='clientInfoQueryForm']").attr("action","${ctx}acceptance/branch/queryClientByFive");
		if(idBirthdayMatch()){
			//校验用户名是否和法 edit by wangmingshun start
			if(clientNameValidate()){
				$("form[name='clientInfoQueryForm']").submit();
			}
			//校验用户名是否和法 edit by wangmingshun end
		}
		return false;
	});
	//提交
	$("#clientInfoInputBt").click(function(){
		$("form[name='clientInfoInputForm']").attr("action","${ctx}acceptance/branch/queryAndAddClientSubmit");
		if(beforeSubmit()){
			$("form[name='clientInfoInputForm']").submit();
		}
		return false;
		
		
	});
	//取消
	$("#cancelBt").click(function(){
		$("form[name='cancelForm']").submit();
		return false;
	});
	
	$("form[name='clientInfoQueryForm']").validate();
	$("form[name='clientInfoInputForm']").validate();
	$(".inputPolicyPhoneSeq").trigger("change");
	$(".occupationCodeInput").trigger("keydown","Y");
	
	//隐藏 下拉框中的出生证 edit by wangmingshun
	$("#idType option[value='12']").remove();
	
  });

//增加一行电话信息
function phoneAddRow(){
	var c = "";
	if(($("#phoneTb tr").length+1)%2==0){
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
		str += '  <td><input type="text" size="8" class="inputAreaNo" />';
		str += '  </td>';
		str += '  <td align="left">';
		str += '        <input type="text" size="30" name="'+Math.random()+'" class="inputPhoneNo {required:true}" />';
		str += '  <input type="hidden" class="inputPhoneSeq" /></td>';
		str += '</tr>';
		
	$("#phoneTb").append(str);
}
//删除选中行的电话信息
function phoneDeleteRow(){
	$(".phoneCB:checked").each(function(){
		$(this).parent().parent().remove();
	});	
}
//增加一行地址信息
function addressAddRow(){
	var c = "";
	if(($("#addressTb tr").length+1)%2==0){
		c = "even_column";
	}else{
		c = "odd_column";
	}	
	var $tr = $("#addressComponentTb tr").clone();
	$tr.addClass(c);
	$tr.find("#addressIndexTd").append($("#addressTb tr").length+1);
	$tr.find(".inputDetailAddress").attr("name",Math.random());//加name是为了校验生效，最终name会被重新赋值
	$tr.find(".inputDetailAddress").addClass("{required:true}");
	$tr.find(".inputPostalcode").attr("name",Math.random());
	$tr.find(".inputPostalcode").addClass("{required:true}");
	$("#addressTb").append($tr);
}
//删除一行地址信息
function addressDeleteRow(){
	$(".addressCB:checked").each(function(){
		$(this).parent().parent().remove();
	});	
}
//增加一行电邮信息
function emailAddRow(){
		var c = "";
		if(($("#emailTb tr").length+1)%2==0){
			c = "even_column";
		}else{
			c = "odd_column";
		}
	   var str = '';
		   str += '<tr class="'+c+'">';
           str += '  <td><input type="checkbox" class="emailCB" />'+($("#emailTb tr").length+1);
           str += '  </td>';
           str += '  <td align="left">';
           str += '    <input type="text" name="'+Math.random()+'" class="inputEmailAddress {required:true}" size="40" />';
		   str += '    <input type="hidden" class="inputEmailSeq" />';
           str += '  </td>';
           str += '</tr>';
		   
	  $("#emailTb").append(str);
	}
//删除一行电邮信息
function emailDeleteRow(){
	$(".emailCB:checked").each(function(){
		$(this).parent().parent().remove();
		});	
}

//提交前的操作
function beforeSubmit(){
	/*检查证件有效期是否勾选长期  edit by wangmingshun start*/
	if ($('#isLongidnoValidityDate').attr('checked')==true)
	{
	 if($('#idnoValidityDate').val()!='')
		 {
			$.messager.alert("提示", "身份证的有效期勾选为长期时，请不要再录入日期。");
			return false;
		 }
	}
	/*检查证件有效期是否勾选长期  edit by wangmingshun start*/
	/*验证证件有效期是否大于申请日期 edit by wangmingshun start*/
	var idnoValidityDate = new Date($("#idnoValidityDate").val().replace("-", "/").replace("-", "/"));
	var applyDate=new Date("${acceptance__item_input.applyDate}".replace("-", "/").replace("-", "/"));
	if(idnoValidityDate < applyDate){
		$.messager.alert("校验错误", "录入的证件有效期小于客户保全申请日期，无法完成操作！");
		return false;
	}
	/*验证证件有效期是否大于申请日期 edit by wangmingshun end*/
	if(phoneValidate()){
		return false;
	}
	addName();
	
	return true;
}

function phoneValidate(){
	var b = false;
	$("#phoneTb tr").each(function(idx){
		if(!b){
		 var p1 = $(this).find(".inputPhoneType").val()+$(this).find(".inputAreaNo").val()+$(this).find(".inputPhoneNo").val();
		 var c1 = !$(this).find(".phoneCB").attr("disabled");
		 $("#phoneTb tr").each(function(id){
			 var p2 = $(this).find(".inputPhoneType").val()+$(this).find(".inputAreaNo").val()+$(this).find(".inputPhoneNo").val();
			 var c2 = !$(this).find(".phoneCB").attr("disabled");
			 if(idx!=id && p1==p2 && (c1||c2)){
				 $.messager.alert("提示","第"+(idx+1)+"行与"+(id+1)+"行电话信息相同,请修改！");
				 b = true;
				 return false;
			 }
		 });
		}
	});
	return b;
}

/*function addressValidate(){
	var b = false;
	$("#addressTb tr").each(function(idx){
		if(!b){
		 var a1 = $(this).find(".inputProvinceCode").val()+
				  $(this).find(".inputCityCode").val()+
				  ($(this).find(".inputAddressAreaCode").val()==null?"":$(this).find(".inputAddressAreaCode").val())+
				  $(this).find(".inputDetailAddress").val();
		 var c1 = !$(this).find(".addressCB").attr("disabled");
		 $("#addressTb tr").each(function(id){
			 var a2 = $(this).find(".inputProvinceCode").val()+
		              $(this).find(".inputCityCode").val()+
		              ($(this).find(".inputAddressAreaCode").val()==null?"":$(this).find(".inputAddressAreaCode").val())+
		              $(this).find(".inputDetailAddress").val();
			 var c2 = !$(this).find(".addressCB").attr("disabled");
			 if(idx!=id && a1==a2 && (c1||c2)){
				 $.messager.alert("提示","第"+(idx+1)+"行与"+(id+1)+"行地址信息相同,请修改！");
				 b = true;
				 return false;
			 }
		 });
		}
	});
	return b;
}

function emailValidate(){
	var b = false;
	$("#emailTb tr").each(function(idx){
		if(!b){
		 var e1 = $(this).find(".inputEmailAddress").val();
		 $("#emailTb tr").each(function(id){
			 var e2 = $(this).find(".inputEmailAddress").val();
			 if(idx!=id && e1==e2){
				 $.messager.alert("提示","第"+(idx+1)+"行与"+(id+1)+"行电邮信息相同,请修改！");
				 b = true;
				 return false;
			 }
		 });
		}
	});
	return b;
}*/

/*给动态新增的域赋name*/
function addName(){
	var idx;
	
	//电话信息
	idx = 0;
	$("#phoneTb tr").each(function(){
		$(this).find(".inputPhoneType").attr("name","clientInfo.clientPhoneList["+idx+"].phoneType");
		$(this).find(".inputAreaNo").attr("name","clientInfo.clientPhoneList["+idx+"].areaNo");
		$(this).find(".inputPhoneNo").attr("name","clientInfo.clientPhoneList["+idx+"].phoneNo");
		$(this).find(".inputPhoneSeq").attr("name","clientInfo.clientPhoneList["+idx+"].phoneSeq");
		idx++;
	});
	
	//地址信息
	idx = 0;
	$("#addressTb tr").each(function(){
		$(this).find(".inputProvinceCode").attr("name","clientInfo.clientAddressList["+idx+"].provinceCode");
		$(this).find(".inputCityCode").attr("name","clientInfo.clientAddressList["+idx+"].cityCode");
		$(this).find(".inputAddressAreaCode").attr("name","clientInfo.clientAddressList["+idx+"].areaCode");
		$(this).find(".inputDetailAddress").attr("name","clientInfo.clientAddressList["+idx+"].detailAddress");
		$(this).find(".inputPostalcode").attr("name","clientInfo.clientAddressList["+idx+"].postalcode");
		$(this).find(".inputAddressSeq").attr("name","clientInfo.clientAddressList["+idx+"].addressSeq");
		idx++;
	});
	
	//电邮信息
	idx = 0;
	$("#emailTb tr").each(function(){
		$(this).find(".inputEmailAddress").attr("name","clientInfo.clientEmailList["+idx+"].emailAddress");
		$(this).find(".inputEmailSeq").attr("name","clientInfo.clientEmailList["+idx+"].emailSeq");
		idx++;
	});
}

//校验身份证号和生日值是否匹配，不匹配的话，仅作提醒
function idBirthdayMatch(){
	var idType = $(".idTypeCodeInput option:selected").val();
	var birthday = $(".birthdayInput").val();
	var sexCode = $(".sexCodeInput").val();
	var idNo = $(".idNoInput").val();
	if(idType == "01"){
		if(!ignoreSexUnmatch && !validateIdNoMatchesSexCode(idNo, sexCode)) {
			$.messager.confirm("提示", "身份证号码与性别不匹配，是否继续？", function(yes){
				if(yes) {
					ignoreSexUnmatch = true;
					$("#clientInfoQueryBt").trigger("click");
				}
			});
			return false;
		}
		if(!ignoreBirthdayUnmatch && !validateIdNoMatchesBirthday(idNo, birthday)) {
			$.messager.confirm("提示", "身份证号码与生日不匹配，是否继续？", function(yes){
				if(yes) {
					ignoreBirthdayUnmatch = true;
					$("#clientInfoQueryBt").trigger("click");
				}
			});
			return false;
		}
	}
	return true;
}

//校验客户姓名是否合法 endit by wangmingshun start
function clientNameValidate(){
	var clientName = $("#clientName").val().replace(/\s+/g, "");
	if(clientName.length < 2){
		$.messager.alert("提示", "客户姓名不能少于两个字符！！！");
		return false;
	}
	return true;
}
//校验客户姓名是否合法 endit by wangmingshun end

</script>
</sf:override>
<sf:override name="content">

<sfform:form commandName="acceptance_query_and_add_client" name="clientInfoQueryForm">

<div align="left"><font color="#FF0000">${acceptance_query_and_add_client.message}</font></div>
<div class="easyui-panel" title="查询客户信息">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">客户姓名：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="queryCriteria.clientName" size="18" cssClass="clientNameInput {required:true}" id="clientName"/>
    </td>
    <td class="layouttable_td_label">出生日期：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="queryCriteria.birthday" cssClass="Wdate birthdayInput {required:true}" size="15" />
    </td>
    <td class="layouttable_td_label">性别：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="queryCriteria.sexCode" cssClass="sexCodeInput" items="${CodeTable.posTableMap.SEX}" itemValue="code" itemLabel="description"></sfform:select>
    </td>
    <td class="layouttable_td_label">证件类型：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="queryCriteria.idTypeCode" id="idType" cssClass="idTypeCodeInput" items="${CodeTable.posTableMap.ID_TYPE}" itemValue="code" itemLabel="description"></sfform:select>
    </td>
    <td class="layouttable_td_label">证件号码：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="queryCriteria.idNo" size="20" cssClass="idNoInput {required:true,idNo:{target:'.idTypeCodeInput',value:'01'}}" />
    </td>
    <td>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="clientInfoQueryBt" name="clientInfoQueryBt">查询</a>
    </td>
  </tr>

 
  <c:if test="${not empty acceptance_query_and_add_client.clientNoList}"> 
  <tr>
  <td class="layouttable_td_label"> 客户号：</td>
  <td class="layouttable_td_widget">
  <sfform:select id="clientNo" path="clientNo" cssClass="input_select"  
                    items="${acceptance_query_and_add_client.clientNoList}"/>
  </td>
  
  </tr> 
  </c:if>

</table>
</div>
 </sfform:form>






<sfform:form commandName="acceptance_query_and_add_client" name="clientInfoInputForm">

<input type="hidden" id="clientNameHid" name="clientNameHid" class="{required:true,messages:{required:'请先录入五项信息进行查询'}}" 
 value="${acceptance_query_and_add_client.clientInfo.clientName}" />
<input type="hidden" id="birthdayHid" name="birthdayHid" class="{required:true,messages:{required:''}}" 
 value="<fmt:formatDate pattern='yyyy-MM-dd' value='${acceptance_query_and_add_client.clientInfo.birthday}'/>" />
<input type="hidden" id="sexCodeHid" name="sexCodeHid" class="{required:true,messages:{required:''}}" 
 value="${acceptance_query_and_add_client.clientInfo.sexCode}" />
<input type="hidden" id="idTypeCodeHid" name="idTypeCodeHid" class="{required:true,messages:{required:''}}" 
 value="${acceptance_query_and_add_client.clientInfo.idTypeCode}" />
<input type="hidden" id="idNoHid" name="idNoHid" class="{required:true,messages:{required:''}}" 
 value="${acceptance_query_and_add_client.clientInfo.idNo}" />
<!-- 客户五项信息，不能让用户修改 -->

<br />
<div class="easyui-panel" title="客户信息展示">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">客户姓名：</td>
    <td class="layouttable_td_widget">${acceptance_query_and_add_client.clientInfo.clientName}</td>
    <td class="layouttable_td_label">证件类型：</td>
    <td class="layouttable_td_widget">${acceptance_query_and_add_client.clientInfo.idTypeDesc}</td>
    <td class="layouttable_td_label">证件号码：</td>
    <td class="layouttable_td_widget">${acceptance_query_and_add_client.clientInfo.idNo}</td>
    <td class="layouttable_td_label">证件有效期截止日：</td>
    <!-- 增加证件有效期显示  edit by wangmingshun start -->
	<td>
		<sfform:date path="clientInfo.idnoValidityDate" size="20px" id="idnoValidityDate"
			onfocus="WdatePicker({skin:'whyGreen',startDate:'',minDate:'',isShowToday:false});"
			cssClass="applyDate Wdate {required:function(element){return $('#isLongidnoValidityDate').attr('checked')!=true}}" />
			长期	<sfform:checkbox path="clientInfo.isLongidnoValidityDate"  id="isLongidnoValidityDate" cssClass="isLongidnoValidityDate"/>
	</td>
	<!-- 增加证件有效期显示  edit by wangmingshun end -->
  </tr>
  <tr>
    <td class="layouttable_td_label">职业代码：</td>
    <td class="layouttable_td_widget">
      <c:if test="${not empty acceptance_query_and_add_client.clientInfo.clientNo}">
         ${acceptance_query_and_add_client.clientInfo.occupationCode}
      </c:if>
      <c:if test="${empty acceptance_query_and_add_client.clientInfo.clientNo}">
         <sfform:input path="clientInfo.occupationCode" cssClass="occupationCodeInput" title="输入后请按回车键" />
         <input type="hidden" id="occupationCodeHid" class="{required:true,messages:{required:'请输入并回车查询到正确的职业代码'}}" />
      </c:if>
    </td>
    <td class="layouttable_td_label">国籍：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="clientInfo.countryCode" items="${CodeTable.posTableMap.COUNTRY}" itemValue="code" itemLabel="description"></sfform:select>
    </td>
    <td class="layouttable_td_label">民族：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="clientInfo.nationCode" items="${CodeTable.posTableMap.NATION}" itemValue="code" itemLabel="description"></sfform:select>
    </td>
    <td class="layouttable_td_label">户籍：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="clientInfo.registerPlace" />
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">职业类别：</td>
    <td align="left" id="occupationGradeDescTd">
      ${acceptance_query_and_add_client.clientInfo.occupationGradeDesc}
    </td>
    <td class="layouttable_td_label">工作单位：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="clientInfo.workUnit" />
    </td>
    <td class="layouttable_td_label">职务：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="clientInfo.position" />
    </td>
    <td class="layouttable_td_label">年均收入：</td>
    <td class="layouttable_td_widget">
      <sfform:input path="yearIncome" cssClass="easyui-numberbox required" precision="2" />
    </td>
  </tr>
  <tr>
  	<td class="layouttable_td_label">婚姻状况：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="clientInfo.marriageCode" items="${CodeTable.posTableMap.MARRIAGE}" itemValue="code" itemLabel="description"></sfform:select>
    </td>
  </tr>
</table>
</div>

<br />
<div class="easyui-panel" title="客户联系电话">
<table class="infoDisTab">
 <tr>
  <th width="10%">选择/序号</th>
  <th width="12%">电话类型</th>
  <th width="12%">区号</th>
  <th align="left">电话号码</th>
 </tr>
 <tbody id="phoneTb">
  <c:forEach items="${acceptance_query_and_add_client.clientInfo.clientPhoneList}" var="item" varStatus="s">
    <c:if test="${s.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${s.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td><input type="checkbox" disabled="disabled" class="phoneCB" />${s.index+1}</td>
      <td>
        <select disabled="disabled" class="inputPhoneType">
          <option value="${item.phoneType}">${item.phoneTypeDesc}</option>
        </select>
      </td>
      <td><input type="text" size="8" value="${item.areaNo}" disabled="disabled" class="inputAreaNo" /></td>
      <td align="left">
      <input type="text" size="30" value="${item.phoneNo}" disabled="disabled" class="inputPhoneNo" />
      <input type="hidden" value="${item.phoneSeq}" class="inputPhoneSeq" />
      </td>
    </tr>
  </c:forEach>
 </tbody>
 <tr>
  <td colspan="4" align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="phoneAddBt" onclick="phoneAddRow();return false;">增加</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="phoneDeleteBt" onclick="phoneDeleteRow();return false;">删除</a>
  </td>
 </tr>
</table>
</div>

<br />
<div class="easyui-panel" title="客户联系地址">
<table class="infoDisTab">
 <tr>
  <th width="10%">选择/序号</th>
  <th>联系地址</th>
  <th width="12%">邮编</th>
 </tr>
 <tbody id="addressTb">
  <c:forEach items="${acceptance_query_and_add_client.clientInfo.clientAddressList}" var="item" varStatus="s">
    <c:if test="${s.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${s.index%2!=0}">
        <tr class="even_column">
    </c:if> 
    <c:if test="${s.index < 10 }">
      <td><input type="checkbox" class="addressCB" disabled="disabled" />${s.index+1}</td>
      <td align="left">
        <select disabled="disabled" class="inputProvinceCode">
          <option value="${item.provinceCode}">${item.provinceDesc}</option>
        </select>省/自治区
        <select disabled="disabled" class="inputCityCode">
          <option value="${item.cityCode}">${item.cityDesc}</option>
        </select>市
        <select disabled="disabled" class="inputAddressAreaCode">
          <option value="${item.areaCode}">${item.areaDesc}</option>
        </select>区/县
        <input type="text" value="${item.detailAddress}" size="30" disabled="disabled" class="inputDetailAddress" />
        <input type="hidden" value="${item.addressSeq}" class="inputAddressSeq" />
      </td>
      <td><input type="text" size="10" value="${item.postalcode}" disabled="disabled" /></td>
    </c:if>
    </tr>
  </c:forEach>
 </tbody>
 <tr>
  <td colspan="3" align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addressAddBt" onclick="addressAddRow();return false;">增加</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="addressDeleteBt" onclick="addressDeleteRow();return false;">删除</a>
  </td>
 </tr>
</table>
</div>

<table id="addressComponentTb" style="display:none"><!-- 一个地址组件，用来增加行 -->
<tr>
  <td id="addressIndexTd"><sfform:checkbox path="" cssClass="addressCB" value="N"/>
  </td>
  <td align="left">
         <sfform:select path="" cssClass="inputProvinceCode" items="${CodeTable.posTableMap.PROVINCE}" itemLabel="description" itemValue="code" >
         </sfform:select>
         省/自治区
         <sfform:select path="" cssClass="inputCityCode" ></sfform:select>
         市
         <sfform:select path="" cssClass="inputAddressAreaCode" ></sfform:select>
         区/县
         <sfform:input path="" cssClass="inputDetailAddress" size="40" />
         <sfform:hidden path="" cssClass="inputAddressSeq" />
  </td>
  <td>
    <sfform:input path="" cssClass="inputPostalcode" size="10" maxlength="6" />
  </td>
</tr>
</table>

<br />
<div class="easyui-panel" title="客户联系电邮">
<table class="infoDisTab">
 <tr>
  <th width="10%">选择/序号</th>
  <th align="left">邮箱地址</th>
 </tr>
 <tbody id="emailTb">
  <c:forEach items="${acceptance_query_and_add_client.clientInfo.clientEmailList}" var="item" varStatus="s">
    <c:if test="${s.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${s.index%2!=0}">
        <tr class="even_column">
    </c:if> 
    <c:if test="${s.index < 1 }">
      <td><input type="checkbox" disabled="disabled" />${s.index+1}</td>
      <td align="left">
        <input type="text" size="40" value="${item.emailAddress}" disabled="disabled" class="inputEmailAddress" />
        <input type="hidden" value="${item.emailSeq}" class="inputEmailSeq" />
      </td>
    </c:if>
    </tr>
  </c:forEach>
 </tbody>
 <tr>
  <td colspan="2" align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="emailAddBt" onclick="emailAddRow();return false;">增加</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="emailDeleteBt" onclick="emailDeleteRow();return false;">删除</a>
  </td>
 </tr>
</table>
</div>

<br />

<c:if test="${acceptance_query_and_add_client.displayPolicyContact eq 'Y'}">
	<c:choose>
		<c:when test="${acceptance_query_and_add_client.newPolicyFlag eq 'Y'}">
            <div class="easyui-panel" title="请选择新保单联系信息">
		</c:when>
		<c:otherwise>
            <div class="easyui-panel" title="请选择保单${acceptance_query_and_add_client.contactInfo.policyNo}的联系信息">
		</c:otherwise>
	</c:choose>
<table class="infoDisTab">
 <tr>
  <th width="30%">保单联系电话</th>
  <th>保单联系地址</th>
  <th>保单联系电邮</th>
 </tr>
 <tr>
  <td>
    <sfform:select path="contactInfo.phoneSeq" cssClass="inputPolicyPhoneSeq {required:true}">
    	<c:choose>
        	<c:when test="${empty acceptance_query_and_add_client.clientInfo.clientPhoneList}">
            	<sfform:option value="${acceptance_query_and_add_client.contactInfo.phoneSeq}">${acceptance_query_and_add_client.contactInfo.phoneDesc}</sfform:option>
            </c:when>
            <c:otherwise>
            	<sfform:options items="${acceptance_query_and_add_client.clientInfo.clientPhoneList}" itemLabel="typeAndNo" itemValue="phoneSeq" />
            </c:otherwise>
        </c:choose>
    </sfform:select>
    <input type="checkbox" class="inputPolicySmsService" />
    <input type="hidden" name="contactInfo.smsService" value="${acceptance_query_and_add_client.contactInfo.smsService}" />
    短信服务
  </td>
  <td>
    <sfform:select path="contactInfo.addressSeq" cssClass="inputPolicyAddressSeq {required:true}" >
    	<c:choose>
        	<c:when test="${empty acceptance_query_and_add_client.clientInfo.clientAddressList}">
            	<sfform:option value="${acceptance_query_and_add_client.contactInfo.addressSeq}">${acceptance_query_and_add_client.contactInfo.addressDesc}</sfform:option>
            </c:when>
            <c:otherwise>
            	<sfform:options items="${acceptance_query_and_add_client.clientInfo.clientAddressList}" itemLabel="typeAndAddress" itemValue="addressSeq" />
            </c:otherwise>
        </c:choose>
    </sfform:select>

  </td>
  <td>
    <sfform:select path="contactInfo.emailSeq" cssClass="inputPolicyEmailSeq">
    	<c:choose>
        	<c:when test="${empty acceptance_query_and_add_client.clientInfo.clientEmailList}">
            	<sfform:option value="${acceptance_query_and_add_client.contactInfo.emailSeq}">${acceptance_query_and_add_client.contactInfo.emailDesc}</sfform:option>
            </c:when>
            <c:otherwise>
            	<sfform:options items="${acceptance_query_and_add_client.clientInfo.clientEmailList}" itemLabel="typeAndAddress" itemValue="emailSeq" />
            </c:otherwise>
        </c:choose>       
    </sfform:select>
  </td>
 </tr>
</table>
</div>
</c:if>

<br />
<div align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="clientInfoInputBt" name="clientInfoInputBt" >确定</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="cancelBt">返回</a>
</div>

</sfform:form>

<form action="${ctx}acceptance/branch/queryAndAddClientCancel.do" name="cancelForm"></form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>