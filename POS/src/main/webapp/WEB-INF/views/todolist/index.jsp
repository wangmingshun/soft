<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">待办任务&gt;&gt;待办任务列表</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function() {
	$("#problemTable").datagrid({
		title:"待处理问题件列表",
		url:"${ctx}todolist/problemList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"problemItemNo", title:"问题号", width:60},
		         {field:"submitter", title:"下发人", width:140},
		         {field:"submitDate", title:"下发时间", formatter:function(value, rowData, rowIndex){
		        	 	return $.format.date(new Date(value), "yyyy-MM-dd");
		         	}, width:60},
		         {field:"problemStatusDesc", title:"问题件状态", width:70},
		         {field:"posNo", title:"保全号", width:100},
		         {field:"policyNo", title:"保单号", width:100, formatter:function(value, rowData, rowIndex){return rowData.posInfo.policyNo;}},
		         {field:"barcodeNo", title:"条形码", width:100, formatter:function(value, rowData, rowIndex){return rowData.posInfo.barcodeNo;}},
		         {field:"acceptor", title:"受理人", width:140, formatter:function(value, rowData, rowIndex){return rowData.posInfo.acceptor;}},
		         {field:"serviceItemsDesc", title:"受理项目", width:100, formatter:function(value, rowData, rowIndex){return rowData.posInfo.serviceItemsDesc;}},
		         {field:"problemItemTypeDesc", title:"问题件类型", width:90},
		         {field:"operation", title:"操作", width:30, formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"${ctx}todolist/problem/" + rowData.problemItemNo + "\">查看</a>";
		        	 }}
		         ]
		]
	});
	$("#acceptEntryTable").datagrid({
		title:"待录入列表",
		url:"${ctx}todolist/acceptEntryList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"posBatchNo", title:"批次号", width:140},
			         {field:"policyNo", title:"保单号", width:140},
			         {field:"clientName", title:"客户姓名", width:100},
			         {field:"serviceItemsDesc", title:"受理项目", width:150},
			         {field:"acceptStatusDesc", title:"受理状态", width:100},
			         {field:"barcodeNo", title:"条形码", width:140},
			         {field:"applyDate", title:"申请日期",formatter:function(value, rowData, rowIndex){
			        	 	return $.format.date(new Date(value), "yyyy-MM-dd");
			         	}, width:80},
			         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
			        	 return "<a href=\"${ctx}acceptance/branch/continueFromBreak.do?posBatchNo=" + rowData.posBatchNo + "\">继续处理</a>";
			         }, width:60}
			         ]]
	});
	$("#approveTable").datagrid({
		title:"待审批列表",
		url:"${ctx}todolist/approveList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"POS_NO", title:"保全号", width:140},
			         {field:"POLICY_NO", title:"保单号", width:140},
			         {field:"SERVICE_ITEMS_DESC", title:"受理项目", width:150},
			         {field:"BARCODE_NO", title:"条形码", width:140},
			         {field:"ACCEPTOR", title:"受理人", width:140},
			         {field:"SUBMIT_TIME", title:"送审日期", width:120},
			         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
			        	 return "<a href=\"${ctx}todolist/approve/" + rowData.POS_NO + "/" + rowData.SUBMIT_NO + "/" + rowData.APPROVE_NO + "/" + rowData.BARCODE_NO + "/" + rowData.POLICY_NO + "/" + rowData.CLIENT_NO + "\">处理</a>";
			         }, width:60}
			         ]]
	});
	$("#publicApproveTable").datagrid({
		title:"公共待审批列表",
		url:"${ctx}todolist/publicApproveList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"POS_NO", title:"保全号", width:140},
			         {field:"POLICY_NO", title:"保单号", width:140},
			         {field:"SERVICE_ITEMS_DESC", title:"受理项目", width:150},
			         {field:"BARCODE_NO", title:"条形码", width:140},
			         {field:"ACCEPTOR", title:"受理人", width:140},
			         {field:"SUBMIT_TIME", title:"送审日期", width:120},
			         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
			        	 return "<a href=\"${ctx}todolist/approve/" + rowData.POS_NO + "/" + rowData.SUBMIT_NO + "/" + rowData.APPROVE_NO + "/" + rowData.BARCODE_NO + "/" + rowData.POLICY_NO + "/" + rowData.CLIENT_NO + "\">处理</a>";
			         }, width:60}
			         ]]
	});
	
	
	$("#undwrPrintTable").datagrid({
		title:"待打印核保函件列表",
		url:"${ctx}todolist/undwrPrintList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"POS_NO", title:"保全号", width:140},
			         {field:"POLICY_NO", title:"保单号", width:140},
			         {field:"SERVICE_ITEMS_DESC", title:"受理项目", width:150},
			         {field:"BARCODE_NO", title:"条形码", width:140},
			         {field:"ACCEPTOR", title:"受理人", width:140},
			         {field:"ACCEPT_DATE", title:"受理日期", width:100},
			         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
			        	 return "<a href=\"javascript:void(0)\" onclick=\"jumpToUws('" + rowData.POS_NO + "', '" + rowData.UW_TYPE + "','" + rowData.APPLY_DATE + "');return false;\">处理</a>";
			         }, width:60}
			         ]]
	});
	
	//edit by wangmingshun goa
	$("#goaApproveTable").datagrid({
		title:"待OA审批任务列表",
		url:"${ctx}todolist/oaApproveList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"POS_NO", title:"保全号", width:140},
			         {field:"POLICY_NO", title:"保单号", width:140},
			         {field:"ITEMS_DESC", title:"受理项目", width:150},
			         {field:"ACCEPTOR", title:"受理人", width:180},
			         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
			        	 return "<a href=\"${ctx}todolist/getApproveHistory?approveId=" + (rowData.OA_APPROVE_ID==null?"":rowData.OA_APPROVE_ID) + " \">详细</a>";
			        	 //return "<a href=\"${ctx}todolist/getApproveHistory?approveId=20150000000000101500\">详细</a>";
			         }, width:60}
			         ]]
	});
});

//核保函打印的时候，跳转到uws前，先更新detail的打印次数纪录
function jumpToUws(posNoValue, type, applyDate){
	$.get("${ctx}todolist/uwnotePrint/updateTimes", 
			{posNo:posNoValue},
			function(data) {
				if(data=="Y"){
					window.open("${UW_URL}SL_UW/notePrint/undwrtNote.do?controlNo="+posNoValue+"&undwrtTaskType="+type+"&applyDate="+applyDate,posNoValue,"location=no,resizable=yes");
				}else{
					$.messager.alert("抱歉","发生了异常，请联系技术人员支持！");
				}
		    }, "text");
}

</script>
</sf:override>

<sf:override name="content">
	<table width="100%" id="acceptEntryTable" class="easyui-datagrid"></table>
	<div class="layout_div_top_spance">
		<table width="100%" id="problemTable" class="easyui-datagrid"></table>
	</div>
	<div class="layout_div_top_spance">
		<table width="100%" id="approveTable" class="easyui-datagrid"></table>
	</div>
	
	<div class="layout_div_top_spance">
		<table width="100%" id="publicApproveTable" class="easyui-datagrid"></table>
	</div>
	<div class="layout_div_top_spance">
		<table width="100%" id="undwrPrintTable" class="easyui-datagrid"></table>
	</div>
	<div class="layout_div_top_spance">
		<table width="100%" id="goaApproveTable" class="easyui-datagrid"></table>
	</div>
	
	<%--
	<div class="easyui-panel" title="待处理问题件列表" collapsible="true">
		<table class="infoDisTab">
			<thead>
				<tr>
					<th>问题号</th>
					<th>问题件下发人</th>
					<th>问题件下发时间</th>
					<th>问题件状态</th>
					<th>保全号</th>
					<th>保单号</th>
					<th>条形码</th>
					<th>受理人</th>
					<th>受理项目</th>
					<th>问题件类型</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty problemList}">
						<c:forEach items="${problemList}" var="item" varStatus="status">
							<tr	class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
								<td class=""><a href="${ctx}todolist/problem/${item.problemItemNo}">${item.problemItemNo}</a></td>
								<td class="">${item.submitter}</td>
								<td class=""><fmt:formatDate value="${item.submitDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
								<td class="">${item.problemStatusDesc}</td>
								<td class="">${item.posNo}</td>
								<td class="">${item.posInfo.policyNo}</td>
								<td class="">${item.posInfo.barcodeNo}</td>
								<td class="">${item.posInfo.acceptor}</td>
								<td class="">${item.posInfo.serviceItemsDesc}</td>
								<td class="">${item.problemItemTypeDesc}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr class="odd">
							<td colspan="10" style="text-align:center;">无待处理问题件</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
    </div>
    
    <div class="layout_div_top_spance">
    	<div class="easyui-panel" title="待审批列表" collapsible="true">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>保全号</th>
						<th>保单号</th>
						<th>受理项目</th>
						<th>条形码</th>
						<th>受理人</th>
						<th>送审时间</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty approveList}">
							<c:forEach items="${approveList}" var="item" varStatus="status">
								<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
									<td>
										<a href="${ctx}todolist/approve/${item.POS_NO}/${item.SUBMIT_NO}/${item.APPROVE_NO}/${item.BARCODE_NO}/${item.POLICY_NO}/${item.CLIENT_NO}">${item.POS_NO}</a>
		                            </td>
									<td>${item.POLICY_NO}</td>
									<td>${item.SERVICE_ITEMS_DESC}</td>
									<td>${item.BARCODE_NO}</td>
									<td>${item.ACCEPTOR}</td>
									<td>${item.SUBMIT_TIME}</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr class="odd">
								<td colspan="6" style="text-align:center;">无待审批项目</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
    	</div>
    </div>
    
    <div class="layout_div_top_spance">
    	<div class="easyui-panel" title="待打印核保函件列表" collapsible="true">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>保全号</th>
						<th>保单号</th>
						<th>受理项目</th>
						<th>条形码</th>
						<th>受理人</th>
		                <th>受理日期</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty undwrtList}">
							<c:forEach items="${undwrtList}" var="item" varStatus="status">
								<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
									<td>
		                              <a href="javascript:void(0)" onclick="jumpToUws('${item.POS_NO}','${item.UW_TYPE}');return false;">
		                                ${item.POS_NO}
		                              </a>
		                            </td>
									<td>${item.POLICY_NO}</td>
									<td>${item.SERVICE_ITEMS_DESC}</td>
									<td>${item.BARCODE_NO}</td>
									<td>${item.ACCEPTOR}</td>
		                            <td>${item.ACCEPT_DATE}</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr class="odd">
								<td colspan="6" style="text-align:center;">无待打印核保函</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</div>
	
	<div class="layout_div_top_spance">
    	<div class="easyui-panel" title="待录入列表" collapsible="true">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>批次号</th>
						<th>保单号</th>
						<th>客户姓名</th>
						<th>受理项目</th>
						<th>受理状态</th>
						<th>条形码</th>
						<th dataType="date">申请日期</th>
						<th style="width:60px;" class="td_widget">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty acceptEntryList}">
							<c:forEach items="${acceptEntryList}" var="item" varStatus="status">
								<tr	class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
									<td>${item.posBatchNo}</td>
									<td>${item.policyNo}</td>
									<td>${item.clientName}</td>
									<td>${item.serviceItemsDesc}</td>
									<td>${item.acceptStatusDesc}</td>
									<td>${item.barcodeNo}</td>
									<td><fmt:formatDate value="${item.applyDate}" pattern="yyyy-MM-dd" /></td>
									<td class="td_widget"><a href="${ctx}acceptance/branch/continueFromBreak.do?posBatchNo=${item.posBatchNo}">继续录入</a></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr class="odd">
								<td colspan="8" style="text-align:center;">无待录入保全件</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</div>
	 --%>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
