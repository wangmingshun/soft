<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;免填单申请书打印预览</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#back").click(function(){
				window.location.href="${ctx}print/application.do?posNo=${POS_NO}";
				return false;
			});
			$("#printEnt").click(function(){
				$("#fm").attr("action", "${ctx}print/print_application_for_fillform.do?printFlag=N&printType=0&entOrApplication=application").submit();
				return false;
			});
			$("#printEnt1").click(function(){
				$("#fm").attr("action", "${ctx}print/print_application_for_fillform.do?printType=1&entOrApplication=application").submit();
				return false;
			});			
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form id="fm" class="noBlockUI">
	<input type="hidden" name="posNo" value="${POS_NO}"/>
	</sfform:form>
	<table class="layouttable">
			<c:forEach items="${posInfoDTOList}" var="posInfo" varStatus="status">	
    <tr>
	    <td width="10%" class="layouttable_td_label">
	                         批单号：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	       ${posInfo.posNo}
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
     <tr>
	    <td width="10%" class="layouttable_td_label">
	                        受理人：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	       ${posInfo.acceptor}
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
     
     <tr>
	    <td width="10%" class="layouttable_td_label">
	                       受理日期：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	       ${posInfo.remark}
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
     
     <tr>
	    <td width="10%" class="layouttable_td_label_multiple">
	                           内容：
	    </td>
	    <td width="60%" class="layouttable_td_widget">
	       <pre>${posInfo.approveText}</pre>
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
			</c:forEach>     
    <tr>
	    <td colspan="3" align="right">
		   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-print" id="printEnt">申请书激光打印</a>
		   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-print" id="printEnt1">申请书针式打印</a>		   
		   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-back"  id="back">返回</a>
	     </td>
    </tr>    
	</table>
	<hr class="hr_default"/>
	<div class="left" style="color:red;">${message}</div>	
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
