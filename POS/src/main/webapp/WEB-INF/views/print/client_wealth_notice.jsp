<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;投保人名下保单资产证明书打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
	$(function() {
		$("#back").click(function() {
			window.location.href = "${ctx}print/entry";
			return false;
		});
		$("#fm").validate();
		$("#applyDate").val(new Date().Format("yyyy-MM-dd"));
	});
	
	Date.prototype.Format = function(fmt) { 
	  var o = {   
	    "M+" : this.getMonth()+1,                 //月份   
	    "d+" : this.getDate(),                    //日   
	    "h+" : this.getHours(),                   //小时   
	    "m+" : this.getMinutes(),                 //分   
	    "s+" : this.getSeconds(),                 //秒   
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	    "S"  : this.getMilliseconds()             //毫秒   
	  };   
	  if(/(y+)/.test(fmt))   
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	  for(var k in o)   
	    if(new RegExp("("+ k +")").test(fmt))   
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	  return fmt;   
	};  
</script>
</sf:override>

<sf:override name="content">
	<sfform:form id="fm" action="${ctx}print/clientWealthNoticePreview"
		class="noBlockUI">
		<table class="layouttable">
			<tr>
				<td width="10%" class="layouttable_td_label">客户号： <span
					class="requred_font">*</span></td>
				<td width="50%" class="layouttable_td_widget"><input
					type="text" name="clientNo" size="20"
					class="input_text {required:true}" /></td>
				<td>
					<input type="hidden" id="applyDate" name="applyDate"/>
				</td>
				<td align="right"><a class='easyui-linkbutton'
					href="javascript:void(0)" onclick="$('#fm').submit();return false;"
					iconCls="icon-ok" id="confirm">确定</a> <a class='easyui-linkbutton'
					href="javascript:void(0)" iconCls="icon-back" id="back">返回</a></td>
			</tr>
		</table>
		<hr class="hr_default" />
		<div class="left" style="color: red;">${message}</div>

	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
