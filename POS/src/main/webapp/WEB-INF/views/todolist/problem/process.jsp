<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代办任务&gt;&gt;问题件处理</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
	$(function() {
		$("#back").click(function() {
			window.location.href = "${ctx}todolist/index.do";
			return false;
		});

		$("#ok").click(function() {
			$("#fm").submit();
			return false;
		});

		$("#attachmentLink")
				.click(
						function() {
							var $this = $(this);
							if ($this.html() == "删除") {
								$("#loading").show();
								$this.hide();
								$
										.post(
												"${ctx}/todolist/problem/proc/clearAttechment.do",
												null,
												function(data) {
													if (data
															&& data.flag == "Y") {
														$this.html("上传");
														$("#fileName").html("");
														$("#attechmentFileId")
																.val("");
														$("#attechmentFileName")
																.val("");
														$("#fileToUpload")
																.val().show();
														$.messager.alert("提示",
																"删除附件成功!");
													} else {
														$.messager.alert("提示",
																"删除附件失败!");
													}
													$("#loading").hide();
													$this.show();
												});
							} else {
								if (!$("#fileToUpload").val()) {
									$.messager.alert("提示", "请选择要上传的文件!");
									return false;
								}
								$("#loading").show();
								$this.hide();
								$.ajaxFileUpload({
									url : '${ctx}include/uploadFile.do',
									secureuri : false,
									fileElementId : "fileToUpload",
									dataType : "json",
									data : {
										filePath : "acceptance/recording"
									},
									success : function(data, status) {
										$("#loading").hide();
										$this.show();
										if (data.errorMessage) {
											$.messager
													.alert("失败", errorMessage);
										} else {
											$("#attechmentFileId").val(
													data.fileId);
											$("#attechmentFileName").val(
													data.fileName);
											$("#fileName").html(data.fileName);
											$this.html("删除");
											$("#fileToUpload").hide();
										}
									},
									error : function(data, status, e) {
										$.messager.alert("失败", e);
										$("#loading").hide();
										$this.show();
									}
								});
							}
							return false;
						});

		$("#fm").validate();

		if ("${flag}" == "success") {
			alert("${message}");
			window.location.href = "${ctx}todolist/index.do";
		}
		if ($("#attechmentFileId").val()) {
			$("#attachmentLink").html("删除");
			$("#fileToUpload").hide();
		} else {
			$("#attachmentLink").html("上传");
			$("#fileToUpload").show();
		}
	});
</script>
	<style type="text/css">
</style>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="todolist_problem_info" id="fm">
		<div class="easyui-panel" title="问题件信息" collapsible="true">
		<table class="infoDisTab" id="benefDataTable">
			<thead>
				<tr>
					<th width="12%">问题号</th>
					<th width="12%">问题件状态</th>
					<th width="12%">问题件类型</th>					
					<th width="8%">下发人</th>
					<th width="20%">问题件内容</th>
					<c:if test="${ not empty todolist_problem_info.transferFailureCause}">
						<th  width="30%">具体原因</th>
					</c:if>
					<th width="8%">状态序号</th>
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
				<tr>
					<th>保全号码</th>
					<th>受理项目</th>
					<th>受理人</th>
					<th>受理状态</th>
					<th>保单号</th>
					<th>客户姓名</th>
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

		<c:if test="${empty readOnly}">
			<div class="layout_div_top_spance">
			<div class="easyui-panel" title="问题件处理" collapsible="true">
			<table class="layouttable">
				<tbody>
					<tr>
						<td width="25%" class="layouttable_td_label">处理结果：</td>
						<td width="75%" class="layouttable_td_widget"><sfform:select
							id="dealResult" path="dealResult"
							items="${todolist_problem_info.problemDealResultList}"
							itemLabel="description" itemValue="code" cssClass="input_select" />
						</td>
					</tr>
					<c:if test="${todolist_problem_info.problemItemType eq '2'}">
						<tr>
							<td width="25%" class="layouttable_td_label">电话回访录音：</td>
							<td width="75%" class="layouttable_td_widget"><sfform:hidden
								path="attechmentFileId" id="attechmentFileId" /> <sfform:hidden
								path="attechmentFileName" id="attechmentFileName" /> <input
								id="fileToUpload" type="file" size="20" name="fileToUpload" /> <span
								id="fileName">${acceptance__item_input.attechmentFileName}</span>
							<img id="loading"
								src="${ctx}CacheableResource/javascript_v1_01/ajax_file_uploader_v2_1/loading.gif"
								style="display: none; width: 16px; height: 16px;" /> <a
								href="javascript:void(0)" id="attachmentLink"></a></td>
						</tr>
					</c:if>
					<tr>
						<td width="25%" class="layouttable_td_label">处理意见：</td>
						<td width="75%" class="layouttable_td_widget"><sfform:textarea
							path="dealOpinion"
							cssClass="multi_text {required:function(){return $('#dealResult').val()=='02';}}"
							cols="20" rows="5" /></td>
					</tr>
				</tbody>
			</table>
			</div>
			</div>
			<div class="layout_div_top_spance">
			<div class="easyui-panel" title="问题件处理提示" collapsible="true">
			<ol style="margin: 0;">
				<c:choose>
					<c:when test="${todolist_problem_info.problemItemType eq '1'}">
						<li>请核对账户持有人姓名、账号是否正确</li>
						<li>请核对账户是否冻结</li>
						<li>请核实客户账户余额是否足够缴纳本次补费</li>
					</c:when>
					<c:when test="${todolist_problem_info.problemItemType eq '2'}">
						<li>请重新核对申请人签名是否与签名样本相符</li>
						<li>请重新核对影像扫描资料是否齐全</li>
						<li>请重新核对保全申请单填写是否正确</li>
						<li>请提供回访录音</li>
					</c:when>
				</c:choose>
			</ol>
			</div>
			</div>
		</c:if>


		<div style="font-size: 16px; color: red; text-align: left;"><c:if
			test="${not empty VALIDATE_MSG }">
          ${VALIDATE_MSG}
          </c:if> <sfform:errors path="*"></sfform:errors></div>

		<br />

		<table width="100%">
			<tr>
				<td style="text-align: right;"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search"
					onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a>
				<a class="easyui-linkbutton" href="javascript:void(0)"
					iconCls="icon-search" id="imageViewLink">查看原始影像</a> <a
					class="easyui-linkbutton" href="javascript:void(0)"
					iconCls="icon-search" id="relateImageViewLink">查看关联影像</a> <a
					class="easyui-linkbutton" href="javascript:void(0)"
					iconCls="icon-back" id="back">返回</a> <c:if test="${empty readOnly}">
					<a class="easyui-linkbutton" href="javascript:void(0)"
						iconCls="icon-ok" id="ok">确定</a>
				</c:if></td>
			</tr>
		</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosSimpleImageViewLayout.jsp"></jsp:include>
