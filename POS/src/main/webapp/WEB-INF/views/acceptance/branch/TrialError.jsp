<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="content">
	<div class="easyui-panel" title="试算不通过" collapsible="true">
		<table class="infoDisTab">
			<tbody>
				<tr class="odd_column">
					<td class="layouttable_td_widget">
						${errorMessage}
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:center;">
			  <a class="easyui-linkbutton"href="javascript:void(0)" 
			   onclick="javascript:window.external.FromExit()">关闭</a>
			</td>
		</tr>
	</table>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>