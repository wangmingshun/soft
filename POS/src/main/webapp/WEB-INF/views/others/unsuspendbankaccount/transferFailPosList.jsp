<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">辅助功能&gt;&gt;转账暂停取消</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#search").click(function(){
				$("#fm").submit();
				return false;
			});
			$(".doCancel").click(function(){
				var $this = $(this);
				var posNo = $this.attr("title");
				$.post("${ctx}others/unsuspendbankaccount/unsuspendBankAccount",
					{posNo:posNo},
					function(data){
						if(data.flag == "Y") {
							$.messager.alert("成功", "取消转账暂停成功");
							$this.closest("tr").remove();
							if($("#dataTable tbody tr").length == 0) {
								window.location.href = "${ctx}others/unsuspendbankaccount/entry";
							}
						} else {
							$.messager.alert("失败", "取消转账暂停失败：" + data.msg);
						}
					});
				return false;
			});
			
			$("#fm").validate();
		});
	</script>
</sf:override>
<sf:override name="content">
<form action="${ctx}others/unsuspendbankaccount/transferFailPosList" method="post" id="fm">
	<div class="easyui-panel" title="" collapsible="true">
		<table class="layouttable">
			<tbody>
				<tr>
					<td class="layouttable_td_label">
						保全号:
					</td>
					<td class="layouttable_td_widget">
						<input type="text" name="posNo" class="input_text required" value="${posNo}"/>
					</td>
					<td align="right">
						<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="search">查询</a>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</form>
	<div class="layout_div_vspance">
		<table class="infoDisTab" id="dataTable">
			<thead>
				<tr>
					<th>保单号</th>
					<th>保全号</th>
					<th class="td_data">转账金额</th>
					<th>转账户主</th>
					<th>转账银行</th>
					<th>转账账号</th>
					<th>转账失败原因</th>
					<th class="td_widget">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${empty transferFailPosList}">
						<tr class="odd_column">
							<td colspan="8" style="text-align:center;">没有数据</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${transferFailPosList}" var="item" varStatus="status">
							<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
								<td>${item.policyNo}</td>
								<td>${item.posNo}</td>
								<td class="td_data">${item.premSum}</td>
								<td>${item.premName}</td>
								<td>${item.bankName}</td>
								<td>${item.accountNo}</td>
								<td>${item.transferResultDesc}</td>
								<td class="td_widget">
									<c:choose>
										<c:when test="${item.transferSuspStatus eq 'Y'}">
											<a href="javascript:void(0);" class="doCancel" title="${item.posNo}">取消转账暂停</a>
										</c:when>
										<c:otherwise>
											无法取消转账暂停
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>