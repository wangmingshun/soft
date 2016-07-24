<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代办任务&gt;&gt;问题件处理</sf:override>
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

function showOrHide(){
	//1.获取下拉表的值
	var type = $("#problemItemType").val();
	//2.判断下拉框的类型
	if(type==2){	
		//3.如果(为2 是错误的地址)显示 联系地址变更
		$("#divId").show();
	}else{
		//隐藏
		$("#divId").hide();
	}
};

$(function() {
	$('#box').change(function(){
		if($("#box").attr("checked") == true) {
			$("input[name='postalcode']").attr("disabled", false);
			$("input[name='detailAddress']").attr("disabled", false);
			$("select[name='cityCode']").attr("disabled", false);
			$("select[name='provinceCode']").attr("disabled", false);
		} else {
			$("input[name='postalcode']").attr("disabled", true);
			$("input[name='detailAddress']").attr("disabled", true);
			$("select[name='cityCode']").attr("disabled", true);
			$("select[name='provinceCode']").attr("disabled", true);
		}
	});
	$("#box").trigger("change");
	$('#divId').hide();
	$("#back").click(function() {
		window.location.href = "${ctx}others/index.do";
		return false;
	});

	$("#ok").click(function() {
		var type = $("#problemItemType").val();
		if(type ==1){
			var data = $("#fm").serialize();
			//var detailSequenceNo = $("#detailSequenceNo").val();
			//window.location.href = "${ctx}others/update.do?detailSequenceNo=" + detailSequenceNo + "";
			$("#fm").attr("action", "${ctx}others/update.do")+data;
			$("#fm").submit();	
		
		}else if(type == 2){
			var data = $("#fm").serialize();
			//alert(data);
			//return false;
			//var policyNo = $("#policyNo").val();
			//window.location.href = "${ctx}others/getPolicyNoList.do?policyNo=" + policyNo + "";
			$("#fm").attr("action", "${ctx}others/getPolicyNoList.do")+data;
			$("#fm").submit();
		}
		return false;
	});


});
</script>
	<style type="text/css">
</style>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="todolist_problem_info" id="fm">
		<input name="detailSequenceNo"  value="${todolist_problem_info.detailSequenceNo}"type="hidden"/>
		<input name="posNo"  value="${todolist_problem_info.posNo}"type="hidden"/>
		<input name="problemItemNo"  value="${todolist_problem_info.problemItemNo}"type="hidden"/>
		<div class="easyui-panel" title="问题件信息">
		<table class="infoDisTab" id="benefDataTable">
			<thead>
				<tr>
					<th width="12%">问题号</th>
					<th width="12%">问题件状态</th>
					<th width="12%">问题件类型</th>					
					<th width="12%">下发人</th>
					<th width="20%">问题件内容</th>
				</tr>
			</thead>
			<tbody>
				<tr class="odd_column">
					<td>${todolist_problem_info.problemItemNo}</td>
					<td>${todolist_problem_info.problemStatusDesc}</td>
					<td>${todolist_problem_info.problemItemTypeDesc}</td>					
					<td>${todolist_problem_info.submitter}</td>
					<td>${todolist_problem_info.problemContent}</td>
				</tr>
			</tbody>
		</table>
		</div>

		<div class="layout_div_top_spance">
		<div class="easyui-panel" title="问题件关联受理信息">
		<table class="infoDisTab" id="benefDataTable">
			<thead>
				<tr>
					<th>保单号</th>
					<th>条形码</th>
					<th>受理人</th>
					<th>信函类型</th>
					<th>客户姓名</th>
					<th>联系地址</th>
					<th>联系电话</th>
				</tr>
			</thead>
			<tbody>
				<tr class="odd_column">
					<td>${todolist_problem_info.posNo}</td>
					<td>${todolist_problem_info.detailSequenceNo}</td>
					<td>${todolist_problem_info.dealUser}</td>
					<td>${todolist_problem_info.noteType}</td>
					<td>${todolist_problem_info.clientName}</td>
					<td>${todolist_problem_info.clientAddress}</td>
					<td>${todolist_problem_info.phoneNo}</td>
					
				</tr>
			</tbody>
		</table>
		</div>
		</div>

			<div class="layout_div_top_spance">
			<div class="easyui-panel" title="问题件处理">
			<table class="layouttable">
				<tbody>
					<tr>
						<td width="25%" class="layouttable_td_label">处理结果：</td>
						<td width="75%" class="layouttable_td_widget">
							<select name="problemItemType" size="1" id="problemItemType" onchange="showOrHide()">
								<option value="1">地址正确</option>
								<option value="2">地址错误</option>
							</select>
						</td>
					</tr>
					
					<tr>
						<td width="25%" class="layouttable_td_label">处理意见：</td>
						<td align="left">
							<textarea class="easyui-validatebox" id="dealOpinion" name="dealOpinion" rows="5" cols="15" style="width:200px; height:100px; resize: none;"></textarea>
					 	</td>
					</tr>
				</tbody>
			</table>
			</div>
			</div>
			
			<div class="easyui-panel" title="联系地址变更" id="divId">
				<input type="checkbox" name="box" id="box"></input>录入联系地址:
					<td align="left" id="box1">
				         <sfform:select path="" cssClass="inputProvinceCode" items="${CodeTable.posTableMap.PROVINCE}" itemLabel="description" itemValue="code" name ="provinceCode">
				         </sfform:select>
				         省/自治区
				         <sfform:select path="" cssClass="inputCityCode" id="inputCityCode" name ="cityCode"></sfform:select>
				         市
				         <sfform:select path="" cssClass="inputAddressAreaCode" name ="areaCode"></sfform:select>
				         区/县
				         <sfform:input path="" cssClass="inputDetailAddress" size="30" name ="detailAddress"/>
				         <sfform:hidden path="" cssClass="inputAddressSeq" />
				         邮编：<input type="text"  name ="postalcode"/>
				  </td>
			</div>
			<tr>
				<li>1.请核对客户的保单联系地址是否正确</li>
				<li>2.如客户地址正确，处理结果选择"地址正确"</li>
				<li>3.如客户地址错误，处理结果选择"地址错误",修改联系地址.</li>
			</tr>
		<table width="100%">
			<tr>
				<td style="text-align: right;"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search"
					onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a>
				 <a
					class="easyui-linkbutton" href="javascript:void(0)"iconCls="icon-back" id="back">返回</a> 
					<a class="easyui-linkbutton" href="javascript:void(0)"iconCls="icon-ok" id="ok">确定</a>
				</td>
			</tr>
		</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>