<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>

<sf:override name="pathString">投连价格设置</sf:override>
<sf:override name="head">
<script type="text/javascript" language="javascript">
//加载分页列表
 $(function(){  
	$('#ulinkPriceTable').datagrid({
		width : 1035,
		title : '投连价格清单',
		iconCls : 'icon-search',
		striped : true,
	    columns:[[
	        {field:'financialProducts',title:'账户代码',align:'center',width:110},
	        {field:'description',title:'账户描述',align:'center',width:240},
	        {field:'priceDate',title:'评估日期',align:'center',width:250},
	        {field:'soldPrice',title:'卖出价',align:'center',width:210},
	        {field:'buyPrice',title:'买入价',align:'center',width:210}
	    ]],
	    pagination :true
	});
	
    //查询按钮，绑定onClick事件 
    $("#search").bind("click", function(){
    	var data = $("#ulinkPrice").serialize();
    	var send_url = "<c:url value='/pub/UlinkPrice/getUlinkPriceList.do'/>?" + data;
		if($("#ulinkPrice").form('validate')) {
			$('#ulinkPriceTable').datagrid({url:send_url});
		}
		return false;
    });
    
    //增加按钮，绑定onClick事件 
    $("#add").bind("click",function(){
        window.location.href="${ctx}pub/UlinkPrice/uploadExcel/main.do";
        return false;
    });
 });
</script>
</sf:override>

<sf:override name="content">
<form id="ulinkPrice" enctype="application/x-www-form-urlencoded; charset=UTF-8">
<div class="layout_div">
	 
	<%--<div class="navigation_div">--%>
        <%--<font class="font_heading1">产品设置</font>>><a href="#">投连价格设置</a>  --%>      
     <%-- </div> --%>  
    
    <div id="ulinkPrice" class="easyui-panel" title="查询条件" collapsible="true">
    <table class="layouttable">
		<tr>
	                      <td width="25%" class="layouttable_td_label">开始日期:
	                          <span style="color: red;">*</span>
						  </td>
						   <td width="25%" class="layouttable_td_widget">
                              <input  id="beginDate"  name="beginDate"  class="easyui-validatebox"  required="true" readonly="true"/>
		                      <img onclick="WdatePicker({skin:'whyGreen',el:'beginDate'})"  style="cursor:pointer" src="${ctx}resources/DatePicker/skin/datePicker.gif"  width="16" height="22" align="middle"/>
	                      </td>
	                      <td width="25%" class="layouttable_td_label">结束日期:
	                          <span style="color: red;">*</span>
						  </td>
						   <td width="25%" class="layouttable_td_widget">
                              <input  id="finishDate"  name="finishDate"   class="easyui-validatebox"  required="true" readonly="true"/>
		                      <img onclick="WdatePicker({skin:'whyGreen',el:'finishDate'})"  style="cursor:pointer" src="${ctx}resources/DatePicker/skin/datePicker.gif"  width="16" height="22" align="middle"/>
	                      </td>
	   </tr>
    </table>
  <div align="right">
    <a class="easyui-linkbutton" iconCls="icon-search" id="search" href="javascript:void(0)">查询</a>
    <a class="easyui-linkbutton" iconCls="icon-add" id="add" href="javascript:void(0)">增加</a>  
   </div>
</div>
</div>
</form>
<br/>
<table id="ulinkPriceTable"></table>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>