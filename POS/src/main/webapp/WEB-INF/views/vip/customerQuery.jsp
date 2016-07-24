 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP客户查询</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("#queryBt").click(function(){
		var v = $.trim($("#queryValue").val());
		if(v==""){
			$.messager.alert("提示","请输入要查询的值！");
			return false;
		}		
		var t = $("#queryType").val();
		var type = t.substring(t.indexOf("-")+1,t.indexOf("+"));
		var key = t.substring(t.indexOf("+")+1);
		window.location.href = "${ctx}vip/detailQuery.do?type="+type+"&key="+v;
		return false;
	});	
	$("#queryType").change(function(){
		var t = $("#queryType").val();
		$("#queryTitle").text(t.substring(0,t.indexOf("-")));		
		t = t.substring(t.indexOf("+")+1);		
		if(t=="client_no"){
			$(this).next().show();
		}else{
			$(this).next().hide();
		}	
	});	

});
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
          <option value="保单号-policyNo+policy_no"         >保单资料</option>
          <option value="客户号-clientNo+client_no"         >客户资料</option>
           <option value="vip卡号-vipNo+vip_no"             >VIP卡号</option>      
        </select>
        <span  style="display:none"><a href="javascript:void(0)"  onclick="openClientwindow($('#queryValue'))">查询客户</a></span>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label"><span id="queryTitle">保单号</span>：</td>
    <td class="layouttable_td_widget">
      <input type="text" class="{required:true}" id="queryValue" size="30" />
    </td>
  </tr>
</table>
</div>
<div align="right">	
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
</div>
</form>
<jsp:include page="/WEB-INF/views/include/clientSelect.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>