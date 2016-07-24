 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保全综合查询</sf:override>
<sf:override name="head">

<script type="text/javascript">
var prodRes = null;//可选险种源json
$(function(){
	$("#queryBt").click(function(){
		var v = $.trim($("#queryValue").val());
		if(v==""){
			$.messager.alert("提示","请输入要查询的值！");
			return false;
		}
		if(v.indexOf("(")>0 && v.indexOf(")")>0){
			v = v.substring(v.lastIndexOf("(")+1,v.lastIndexOf(")"));//对于录入险种名字的特殊处理
		}
		
		var t = $("#queryType").val();
		var type = t.substring(t.indexOf("-")+1,t.indexOf("+"));
		var key = t.substring(t.indexOf("+")+1);
		
		//增加E服务信息查询，由于在pos系统，所以url链接到pos系统。
		if(type == "EServiceInfo") {
			window.location.href = "${ctx}pub/PosQuery.do?clientNo=" + v;
		} else {
			window.location.href = "${GQ_URL}SL_GQS/GQQuery.do?type="+type+"&"+key+"="+v+"&isShowBackFlag=N";
		}
		return false;
	});
	
	$("#queryType").change(function(){
		var t = $("#queryType").val();
		var type = t.substring(t.indexOf("-")+1,t.indexOf("+"));
		$("#queryTitle").text(t.substring(0,t.indexOf("-")));
		
		t = t.substring(t.indexOf("+")+1);
		
		if(t=="client_no"){
			$(this).next().show();
		}else{
			$(this).next().hide();
		}

		if(type == "EServiceInfo") {
			$("#clientQueryType").append("<option value=\"byPhoneNo\">按手机号码</option>");
			$("#policyTrialBt").hide();
		} else {
			$("#clientQueryType option[value='byPhoneNo']").remove();
			$("#policyTrialBt").show();
		}
		
		if("agent_no"==t){//业务员信息查询
			$("#agentQueryWindow").window("open");
			return;
		}
		
		if(t=="product_code" && prodRes==null){
		  $.post("${ctx}include/allProducts", 
				 function(data) {
					 prodRes = data;
					$("#queryValue").autocomplete({
						source:$.map(prodRes,function(it){
							return it.FULL_NAME;
						})
					});
				}, "json");
		}
	});

	$("#agentQueryBt").click(function(){
		if(agentValidate()){
			return;
		}
		
		var url = "";
		var type = $(".agentQueryType").val();
		if("byClientNo"==type){
			url = "agent_no="+$(".agentNoInput").val();
		}else if("byName"==type){
			url = "agent_name="+$(".agentNameInput").val()+"&sex_code="+($(".agentSexInput:checked").val()==undefined?"":$(".agentSexInput:checked").val())+"&birthday="+$(".agentBirthdayInput").val();
		}else if("byId"==type){
			url = "id_no="+$(".agentIdNoInput").val()+"&id_type="+$(".agentIdTypeInput").val();
		}
		
		window.location.href = "${GQ_URL}SL_GQS/GQQuery.do?type=agentInfo&"+url;
		return false;
	});
	
	$("#policyTrialBt").click(function(){
		window.location.href = "${GQ_URL}SL_GQS/GQQuery.do?type=policyTrialInfo";
		return false;
	});
	
});

function agentValidate(){
	var flag = false;
	$(".agentQueryTb input:not('.agentQueryType')").each(function(i){
		if($(this).hasClass($(".agentQueryType").val()) && $.trim($(this).val())==""){
			$.messager.alert("提示","请输入"+($(this).parent().prev().text().replace(":","")));
			flag = true;
			return false;
		}
	});
	return flag;
}

</script>
</sf:override>
<sf:override name="content">

<form>

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">查询类型：</td>
    <td class="layouttable_td_widget">
        <select id="queryType">
          <option value="保单号-policyInfo+policy_no"           >保单资料</option>
          <option value="客户号-clientInfo+client_no"           >客户资料</option>
          <option value="保全批单号-posChangeInfo+pos_no"       >保全变更明细信息</option>
          <option value="产品名称-productInfo+product_code"     >产品条款</option>          
          <option value="保单号-beneficiaryInfo+policy_no"      >保单受益人信息</option>
          <option value="保单号-imageInfo+policy_no"            >保单影像信息</option>
          <option value="保单号-posInfo+policy_no"              >保单保全资料</option>
          <option value="保单号-posSurvivalDueInfo+policy_no"   >生存领取信息</option>
          <option value="保单号-posLoanInfo+policy_no"          >保单贷款信息</option>
          <option value="保单号-posDividendsDueInfo+policy_no"  >红利资料信息</option>
          <option value="保单号-posAutoPolicyLoanInfo+policy_no">自垫资料信息</option>
          <option value="保单号-personAccountInfo+policy_no"    >万能账户信息</option>
          <option value="保单号-touLianInfo+policy_no"          >投连账户信息</option>
          <option value="保全批单号-underWriteInfo+apply_no"     >核保信息</option>
          <option value="投保单号-policyInfo+apply_barcode"     >投保单对应保单信息</option>
          <option value="保单号-productSuminsChangeInfo+policy_no">保单险种的保额保费变化</option>
          <option value="业务员号-agentInfo+agent_no"             >业务员资料</option>
          <option value="条形码-posInfo+barcode_no"               >条码相关保全信息</option>
          <option value="客户号-EServiceInfo+client_no"               >E服务平台信息</option>
        </select>
        <span  style="display:none"><a href="javascript:void(0)"  onclick="openClientwindow($('#queryValue'))">查询客户</a></span>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label"><span id="queryTitle">保单号</span>：</td>
    <td class="layouttable_td_widget">
      <input type="text" class="{required:true}" id="queryValue" size="50" />
    </td>
  </tr>
</table>
</div>
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-edit" id="policyTrialBt">保单试算</a>
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
</div>

</form>

<jsp:include page="/WEB-INF/views/include/clientSelect.jsp"></jsp:include>

<div id="agentQueryWindow" class="easyui-window" closed="true" modal="true" title="业务员查询" style="width:580px;height:200px;" collapsible="false" minimizable="false" maximizable="false">
		<table class="layouttable agentQueryTb" id="queryCriteriaTable">
			<tbody>
				<tr>
					<td class="layouttable_td_label">
						查询方式:
					</td>
					<td class="layouttable_td_widget">
						<select id="clientQueryType" name="clientQueryType" class="agentQueryType">
                        	<option value="byClientNo">按业务员号</option>
							<option value="byName">按业务员姓名</option>
							<option value="byId">按证件号码</option>
						</select>
					</td>
				</tr>
				<tr class="byClientNo" style="display:none;">
					<td class="layouttable_td_label">
						业务员号:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" id="clientNo" name="clientNo" class="byClientNo agentNoInput"/>
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<td class="layouttable_td_label">
						证件类型:
					</td>
					<td class="layouttable_td_widget">
                       <select name="idTypeCode" id="idTypeCode" class="agentIdTypeInput">
                        <c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
                          <option value="${item.code}">${item.description}</option>
                        </c:forEach>
                       </select>
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<td class="layouttable_td_label">
						证件号码:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" id="clientIdNo" name="clientIdNo" class="byId agentIdNoInput"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						业务员姓名:
					</td>
					<td class="layouttable_td_widget">
						<input id="clientName" name="clientName" class="byName agentNameInput"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						业务员性别:
					</td>
					<td class="layouttable_td_widget">
                    	<input type="radio" name="sex" value="1" class="agentSexInput" />男
                        <input type="radio" name="sex" value="2" class="agentSexInput" />女
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<td class="layouttable_td_label">
						业务员生日:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" name="birthday" id="birthday" class="Wdate agentBirthdayInput"/>
					</td>
				</tr>
			</tbody>
		</table>
        <div align="right">
        	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="agentQueryBt">查询</a>
        </div>
</div>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>