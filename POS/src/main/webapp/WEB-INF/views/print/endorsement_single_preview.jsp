<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;批单逐单打印预览</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#back").click(function(){
				window.location.href="${ctx}print/endorsement_single.do?posNo=${posInfo.POS_NO}";
				return false;
			});
			$("#printEnt").click(function(){
				$("#fm").attr("action", "${ctx}print/endorsement_single_submit_for_ent.do").submit();
				return false;
			});
			$("#printNote").click(function(){
				$("#fm").attr("action", "${ctx}print/endorsement_single_submit_for_note.do").submit();
				return false;
			});
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form id="fm" class="noBlockUI">
	<input type="hidden" name="posNo" value="${posInfo.POS_NO}"/>
	</sfform:form>
	<table class="layouttable">
    <tr>
	    <td width="10%" class="layouttable_td_label">
	                         批单号：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	       ${posInfo.POS_NO}
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
	       ${posInfo.ACCEPTOR_NAME}(${posInfo.ACCEPTOR})
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
	       ${posInfo.ACCEPT_DATE}
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
     
     <tr>
	    <td width="10%" class="layouttable_td_label_multiple">
	                           批文内容：
	    </td>
	    <td width="60%" class="layouttable_td_widget">
	       <pre>${posInfo.APPROVE_TEXT}</pre>
	    </td>
	    <td>
	      &nbsp;
	    </td>
     </tr>
     
    <tr>
	    <td colspan="3" align="right">
	       <a class='easyui-linkbutton' href="${ctx}print/print_application_for_fillform.do?printFlag=Y&posNo=${posInfo.POS_NO}&printType=0&entOrApplication=ent" iconCls="icon-print" >激光打印批单</a>
		   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-print" id="printEnt">针式打印批单</a>
		   <c:if test="${isEndorsementHasNoteOrPlanProvision}">
		       <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-print" id="printNote">打印保证价值表或（及）产品条款</a>
		   </c:if>
		   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-back"  id="back">返回</a>
	     </td>
    </tr>    
	</table>
	<hr class="hr_default"/>
	<div class="left" style="color:red;">${message}</div>	
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
