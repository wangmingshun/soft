<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>
<div id="clientSearchWindow" class="easyui-window" closed="true" modal="true" title="客户查询" style="width:580px;height:350px;" collapsible="false" minimizable="false" maximizable="false">
<script type="text/javascript">
var $clientNo;//回写客户号的域
var callbackFn;//回调方法
$(function(){

	$("#clientSearchBt").unbind("click").click(function(){
		if(validate()){
			return;
		}
		
		$("#clientSearchBt").hide();
		$("#loadingImg").show();

		$.post("${ctx}include/clientSearch", 
		{queryType:$("#clientQueryType").val(),
		clientNo:$("#clientNo").val(),
		policyNo:$("#policyNo").val(),
		applicantOrInsured:$("#applicantOrInsured").val(),
		idTypeCode:$("#idTypeCode").val(),
		clientIdNo:$("#clientIdNo").val(),
		clientName:$("#clientName").val(),
		sex:$("[name=sex]:checked").val(),
		birthday:$("#birthday").val(),
		phoneNo:$("#phoneNo").val()
		},
		function(data) {
			
			$("#clientListTb *").remove();
			
			if(data.length==0){
				$.messager.alert("提示","没有查询到对应的客户信息");
				$("#loadingImg").hide();
				$("#clientSearchBt").show();
				return;
			}

			$.each(data,function(i,map){
				var c = "";
				var l = $("#clientListTb tr").size()+1;
				if(l%2==0){
					c = "even_column";
				}else{
					c = "odd_column";
				}
				
				var str = "";
				    str += '<tr class="'+c+'">';
					str += '  <td><input type="radio" name="clientChoose" value="'+map["clientNo"]+'" class="clientChooseRd" /></td>'
					str += '  <td>'+map["clientNo"]+'</td>';
					str += '  <td>'+map["clientName"]+'</td>';
					str += '  <td>'+map["sexDesc"]+'</td>';
					str += '  <td>'+map["birthday"]+'</td>';
					str += '  <td>'+map["idTypeDesc"]+'</td>';
					str += '  <td>'+map["idNo"]+'</td>';
					str += '</tr>';
				$("#clientListTb").append(str);
			});

			$("#loadingImg").hide();
			$("#clientSearchBt").show();
				
		}, "json");
		return false;
	});
	
	$("#birthday").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$(".clientChooseRd").live("click",function(){
		$("#clientSearchWindow").window("close");
		$clientNo.val($(this).val());
		if(callbackFn){
			callbackFn.apply();
		}
	});

	var $form = $("#queryCriteriaTable");
	$("#clientQueryType").change(function(event){
		var $query_option_rows = $form.find("tr:not(:has(#clientQueryType))");
		var selVal = event.target.value;
		$query_option_rows.filter("tr:not(." + selVal + ")").hide();
		$query_option_rows.filter("tr." + selVal).show();
	}).trigger("change");
});

function openClientwindow(input, callback){
	$clientNo = input;
	callbackFn = callback;
	$("#clientSearchWindow").window("open");
}

function validate(){
	var flag = false;
	$("#queryCriteriaTable input:not('#clientQueryType')").each(function(i){
		if($(this).hasClass($("#clientQueryType").val()) && $.trim($(this).val())==""){
			$.messager.alert("提示","请输入"+($(this).parent().prev().text().replace(":","")));
			flag = true;
			return false;
		}
	});
	return flag;
}

</script>
<form id="clientSelectForm">
		<table class="layouttable" id="queryCriteriaTable">
			<tbody>
				<tr>
					<td class="layouttable_td_label">
						客户查询方式:
					</td>
					<td class="layouttable_td_widget">
						<select id="clientQueryType" name="clientQueryType">
							<option value="byPolicy">按保单号</option>
							<option value="byName">按姓名</option>
							<option value="byId">按证件号码</option>
                            <option value="byClientNo">按客户号</option>
						</select>
					</td>
				</tr>
				<tr class="byClientNo" style="display:none;">
					<td class="layouttable_td_label">
						客户号:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" id="clientNo" name="clientNo" class="byClientNo"/>
					</td>
				</tr>
				<tr class="byPolicy" style="display:none;">
					<td class="layouttable_td_label">
						保单号:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" id="policyNo" name="policyNo" class="byPolicy"/>
					</td>
				</tr>
				<tr class="byPolicy" style="display:none;">
					<td class="layouttable_td_label">
						投被保人:
					</td>
					<td class="layouttable_td_widget">
                    	<input type="radio" name="applicantOrInsured" value="A" />投保人
                        <input type="radio" name="applicantOrInsured" value="I" />被保人
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<td class="layouttable_td_label">
						客户证件类型:
					</td>
					<td class="layouttable_td_widget">
                       <select name="idTypeCode" id="idTypeCode">
                        <c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
                          <option value="${item.code}">${item.description}</option>
                        </c:forEach>
                       </select>
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<td class="layouttable_td_label">
						客户证件号码:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" id="clientIdNo" name="clientIdNo" class="byId"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						客户姓名:
					</td>
					<td class="layouttable_td_widget">
						<input id="clientName" name="clientName" class="byName"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						客户性别:
					</td>
					<td class="layouttable_td_widget">
                    	<input type="radio" name="sex" value="1" />男
                        <input type="radio" name="sex" value="2" />女
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						客户生日:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" name="birthday" id="birthday" class="Wdate"/>
					</td>
				</tr>
				<tr class="byPhoneNo" style="display:none;">
					<td class="layouttable_td_label">
						手机号码:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" name="phoneNo" id="phoneNo"/>
					</td>
				</tr>
			</tbody>
		</table>
    <div align="right">
    	<span id="loadingImg" style="display:none"><img src="${ctx}CacheableResource/css_v1_01/images/tree_loading.gif"/>查询中...</span>
    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="clientSearchBt">查询</a>
    </div>
    
    <hr class="hr_default" />
    
    <table class="infoDisTab">
      <tr>
        <th width="8%">选择</th>
        <th>客户号</th>
        <th>姓名</th>
        <th>性别</th>
        <th>生日</th>
        <th>证件类型</th>
        <th width="20%">证件号</th>
      </tr>
      <tbody id="clientListTb">
      
      </tbody>
    </table>
    
</form>
</div>