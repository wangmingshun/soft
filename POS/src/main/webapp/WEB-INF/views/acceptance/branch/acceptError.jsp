<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">机构受理&gt;&gt;逐单受理&gt;&gt;受理出错</sf:override>
<sf:override name="head">
<script type="text/javascript">
	$(function(){
		$("#cancelServieBt").click(function() {
			$.post("${ctx}acceptance/branch/cancelService.do", 
					{posNo: "${empty acceptance__item_input.posNo ? posNo : acceptance__item_input.posNo}",
						cancelCause: $("#cancelCause").val(),
						cancelRemark: $("#cancelRemark").val()
					}, function(data) {
						if(data.flag == "Y") {
							$.messager.alert("提示", "受理撤销成功", "info", function(){
								if(data.hasNext) {
									window.location.href="${ctx}acceptance/branch/acceptDetailInput/" +  data.posBatchNo;
								} else {
									window.location.href="${ctx}acceptance/branch/entry";
								}
							});
						} else {
							$.messager.alert("失败", data.message);
						}
					}, "json");
			return false;
		});
		$("#toEntry").click(function(){
			window.location.href="${ctx}acceptance/branch/entry";
			return false;
		});
  });
	
</script>
</sf:override>
<sf:override name="content">
	<div class="easyui-panel" title="受理出错" collapsible="true">
		<table class="infoDisTab">
			<tbody>
				<tr class="odd_column">
					<th class="layouttable_td_label" width="10%">错误信息：</th>
					<td class="layouttable_td_widget">
						${errorMessage}
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-cancel" id="cancelServieBt">受理撤销</a>
				撤销原因：
				<select name="cancelCause" id="cancelCause">
					<c:forEach items="${CodeTable.posTableMap.POS_ROLLBACK_CAUSE_CODE}"	var="item">
						<option value="${item.code}">${item.description}</option>
					</c:forEach>
				</select>
				撤销备注：
				<input type="text" name="cancelRemark" id="cancelRemark" class="{byteRangeLength:[0,2000]}"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="toEntry">开始新受理</a>
			</td>
		</tr>
	</table>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>