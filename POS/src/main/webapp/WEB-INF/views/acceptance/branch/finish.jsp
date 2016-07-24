<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">机构受理&gt;&gt;逐单受理&gt;&gt;受理经办结束</sf:override>
<sf:override name="head">
<script type="text/javascript">
  $(function(){
	$("#toEntry").click(function(){
		window.location.href="${ctx}acceptance/branch/entry";
		return false;
	});
	$("#processNext").click(function(){
		window.location.href="${ctx}acceptance/branch/acceptDetailInput/${posBatchNo}";
		return false;
	});
  });
	
</script>
</sf:override>
<sf:override name="content">
	<div class="easyui-panel" title="处理结果（共${acceptance__process_result_list[0].ACCEPT_COUNT}个受理）:" collapsible="true">
		<table class="infoDisTab">
			<tbody>
				<c:forEach items="${acceptance__process_result_list}" var="item">
					<tr class="odd_column">
						<th class="layouttable_td_label" width="10%">保全项目：</th>
						<td class="layouttable_td_widget">第${item.ACCEPT_SEQ}个受理，${item.POLICY_NO}保单，${item.SERVICE_ITEMS_DESC}项目
						</td>
					</tr>
					<tr class="even_column">
						<th class="layouttable_td_label" valign="top">处理结果：</th>
						<td class="layouttable_td_widget" valign="top">
							<pre>${item.RESULT_MSG}</pre>
							<c:if test="${item.RESULT_MSG eq '待收费' or item.RESULT_MSG eq '已生效' or item.RESULT_MSG eq '待保单打印'}">
								<a class="easyui-linkbutton" iconCls="icon-print" href="${ctx}print/print_application_for_fillform.do?printFlag=Y&posNo=${item.POS_NO}&printType=0&entOrApplication=ent">激光打印批单</a>
								&nbsp;&nbsp;
								<a class="easyui-linkbutton" iconCls="icon-print" href="${ctx}print/endorsement_single_submit_for_ent.do?posNo=${item.POS_NO}">针式打印批单</a>
								&nbsp;&nbsp;
								<c:if test="${item.isEndorsementHasNoteOrPlanProvision}">
									<a class="easyui-linkbutton" iconCls="icon-print" href="${ctx}print/endorsement_single_submit_for_note.do?posNo=${item.POS_NO}">查看保证价值表或（及）产品条款PDF</a>
								</c:if>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="toEntry">开始新受理</a>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<c:if test="${hasNext}">
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="processNext">继续处理</a>
				</c:if>
			</td>
		</tr>
	</table>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>