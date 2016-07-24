<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>

<sf:override name="pathString">消息提示</sf:override>
<sf:override name="head">
<script type="text/javascript" language="javascript">
var secs = 3;
window.setTimeout('auto()',1000);

function auto() {		
	if(secs>0){
	document.getElementById("calcuTimeID").innerHTML=""+secs;
	}
	secs--;
	if(secs==0){
		go_newPage();
	}
	window.setTimeout('auto()',1000);
	
}
function go_newPage(){
	//window.location.href="${ctx}${forwardUrl}";
	window.history.go(-1);
}
</script>
</sf:override>

<sf:override name="content">
	 
	<div id="w" class="easyui-window" resizable="false" collapsible="false" minimizable="false" maximizable="false" closable="false" title="系统消息" iconCls="icon-info" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
				${msgs}
				
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				该页面三秒钟[<span id="calcuTimeID">3</span>]自动跳转<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="go_newPage();">跳转</a>
			</div>
		</div>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>




