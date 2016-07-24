<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代办任务&gt;&gt;问题件处理&gt;&gt;问题函件下发</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#ok").click(function(){
				$("#fm").submit();
				return false;
			});
			$("#ret").click(function(){
				window.location.href = "${ctx}todolist/index.do";
				return false;
			});
			$("#back").click(function(){
				window.location.href = "${ctx}todolist/problem/proc/${todolist_problem_info.problemItemNo}";
				return false;
			});
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="todolist_problem_info" id="fm">
		<div class="easyui-panel" title="问题件信息" collapsible="true">
			<table class="infoDisTab" id="benefDataTable">
				<thead>
					<tr>
						<th>问题号</th>
						<th>问题件状态</th>
						<th>问题件类型</th>
						<th>下发人</th>
						<th>问题件内容</th>
						<c:if test="${ not empty todolist_problem_info.transferFailureCause}">
						<th>具体原因</th>
				     	</c:if>
						<th>状态序号</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd_column">
						<td>${todolist_problem_info.problemItemNo}</td>
						<td>${todolist_problem_info.problemStatusDesc}</td>
						<td>${todolist_problem_info.problemItemTypeDesc}</td>
						<td>${todolist_problem_info.submitter}</td>
						<td>${todolist_problem_info.problemContent}</td>
						<c:if test="${ not empty todolist_problem_info.transferFailureCause}">
						<td>${todolist_problem_info.transferFailureCause}</td>
					</c:if>
						<td>${todolist_problem_info.statusChangeNo}</td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<div class="layout_div_top_spance">
			<div class="easyui-panel" title="问题件关联受理信息" collapsible="true">
				<table class="infoDisTab" id="benefDataTable">
					<thead>
						<tr class="table_head_tr">
							<th class="table_head_th">保全号码</th>
							<th class="table_head_th">受理项目</th>
							<th class="table_head_th">受理人</th>
							<th class="table_head_th">受理状态</th>
							<th class="table_head_th">保单号</th>
							<th class="table_head_th">客户姓名</th>
						</tr>
					</thead>
					<tbody>
						<tr class="odd_column">
							<td>${todolist_problem_info.posInfo.posNo}</td>
							<td>${todolist_problem_info.posInfo.serviceItemsDesc}</td>
							<td>${todolist_problem_info.posInfo.acceptor}</td>
							<td>${todolist_problem_info.posInfo.acceptStatusDesc}</td>
							<td>${todolist_problem_info.posInfo.policyNo}</td>
							<td>${todolist_problem_info.posInfo.clientName}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div class="layout_div_top_spance">
			<div class="easyui-panel" title="问题函信息" collapsible="true">
				<table class="layouttable">
					<tbody>
						<tr>
							<td width="25%" class="layouttable_td_label">
								问题事项及描述:
							</td>
							<td width="75%" class="layouttable_td_widget" colspan="3">
								<sfform:textarea path="letterInfo.problemContent" cssClass="multi_text" cols="50" rows="5"/>
							</td>
						</tr>
						<tr>
							<td width="25%" class="layouttable_td_label">
								客户姓名:
							</td>
							<td width="25%" class="layouttable_td_widget">
								<span style="color:blue">${todolist_problem_info.letterInfo.clientName}</span>
							</td>
							<td width="25%" class="layouttable_td_label">
								客户电话:
							</td>
							<td width="25%" class="layouttable_td_widget">
								<sfform:input path="letterInfo.clientPhone" cssClass="input_text"/>
							</td>
						</tr>
						<tr>
							<td width="25%" class="layouttable_td_label">
								业务员电话:
							</td>
							<td width="25%" class="layouttable_td_widget">
								<sfform:input path="letterInfo.empPhone" cssClass="input_text"/>
							</td>
							<td width="25%" class="layouttable_td_label">
								客户联系地址邮编:
							</td>
							<td width="25%" class="layouttable_td_widget">
								<sfform:input path="letterInfo.clientZip" cssClass="input_text"/>
							</td>
						</tr>
						<tr>
							<td width="25%" class="layouttable_td_label">
								客户联系地址:
							</td>
							<td width="75%" class="layouttable_td_widget" colspan="3">
								<sfform:input path="letterInfo.clientAddress" cssClass="input_text" cssStyle="width:250px;"/>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<div style="width:100%;color:red;text-align:left;">${message}</div>
		
		<table width="100%">
			<tr>
				<td style="text-align:right;">
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="ret">返回</a>
					&nbsp;&nbsp;
                	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a>
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="back">取消</a>
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
				</td>
			</tr>
		</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
