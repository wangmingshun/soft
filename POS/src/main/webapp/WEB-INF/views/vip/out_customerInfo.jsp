<%@page import="java.text.SimpleDateFormat;"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
	<script type="text/javascript">
	$(".downloadLink").live(
			"click",
			function() {
				var $this = $(this);
				$.post("${ctx}vip/getDownloadURL.do", {
					fileId : $this.attr("title")
				}, function(data) {
					if (!data || data.flag != "Y") {
						$.messager
								.alert("提示",
										data && data.message ? data.message
												: "获取地址失败。");
					} else {
						window.open(data.url);
					}
				});
				return false;
			});
	
	$(".operation","#productList tbody").live("click", function() {
		var $this = $(this);
		$.messager.confirm("提示", "确认修改?", function(yes) {
			if(yes) {
				var isValid = $this.attr("title");
				var clientNo = "${clientNo}";
				var seqNo = $this.prev("input").val();
				if(isValid == "N") {
					isValid = "Y";
				} else if(isValid == "Y") {
					isValid = "N";
				}
				$.post("${ctx}vip/updateVipServiceInstanceInfo", {
					clientNo : clientNo,
					isValid : isValid,
					seqNo : seqNo
				}, function(data) {
					if(data == "Y") {
						if(isValid == 'N') {
							$this.html("恢复");
							$this.attr("title", "N");
						} else if(isValid == 'Y') {
							$this.html("作废");
							$this.attr("title", "Y");
						}
						$.messager.alert("提示", "修改成功！！");
					} else {
						$.messager.alert("提示", "修改失败！！");
					}
				}, "text");
			}
		});
		return false;
	});
</script>
</sf:override>



<sf:override name="content">


	<div  class="easyui-panel" title="基本资料">

	<table class="infoDisTab">
		<tr>
			<td align="center" width="8%" bgcolor="#E9EEFD">姓名</td>
			<td width="8%">${vipBaseInfo.client_name}</td>
			<td align="center" width="9%" bgcolor="#E9EEFD">性别</td>
			<td width="6%">${vipBaseInfo.client_sex}</td>
			<td align="center" width="10%" bgcolor="#E9EEFD">出生日期</td>
			<td width="8%"><fmt:formatDate
				value="${vipBaseInfo.client_birthday}" type="date"
				dateStyle="default" /></td>
			<td align="center" width="12%" bgcolor="#E9EEFD">证件类型</td>
			<td width="15%">${vipBaseInfo.client_idtype}</td>
			<td align="center" width="11%" bgcolor="#E9EEFD">证件号码</td>
			<td width="13%">${vipBaseInfo.client_idno}</td>
		</tr>
		<tr>

			<td align="center" width="13%" bgcolor="#E9EEFD">工作单位</td>
			<td width="40%" colspan="7">${vipBaseInfo.client_WORK_UNIT}</td>
			<td align="center" width="13%" bgcolor="#E9EEFD">职级/职称</td>
			<td width="20%">${vipBaseInfo.client_POSITION}</td>

		</tr>
		<tr>
			<td align="center" width="13%" bgcolor="#E9EEFD">家庭住址</td>
			<td width="40%" colspan="7">${vipBaseInfo.client_address}</td>
			<td align="center" width="13%" bgcolor="#E9EEFD">联系方式</td>
			<td width="20%">${vipBaseInfo.client_phone}</td>

		</tr>
		<tr>
			<td align="center" width="8%" bgcolor="#E9EEFD">当前VIP等级</td>
			<td width="10%">${vipBaseInfo.vip_grade}</td>
			<td align="center" width="13%" bgcolor="#E9EEFD">当前VIP卡号</td>
			<td width="10%">${vipBaseInfo.card_no}</td>
			<td align="center" width="13%" bgcolor="#E9EEFD">当前服务专员</td>
			<td width="15%">${vipBaseInfo.emp_name}</td>
			<td align="center" width="13%" bgcolor="#E9EEFD">服务专员所属部门</td>
			<td width="15%">${vipBaseInfo.branch_name}</td>
			<td align="center" width="20%" bgcolor="#E9EEFD">联系电话</td>
			<td width="20%">${vipBaseInfo.phone}</td>
		</tr>

	</table>

	</div>

	<div id="policyInfo" class="easyui-panel" title="保单信息">
	<table class="infoDisTab" id="productList">
		<thead>
			<tr>
				<th width="12%" class="td_widget">分支机构</th>
				<th width="12%" class="td_widget">保险合同号</th>
				<th width="8%" class="td_widget">生效日</th>
				<th width="8%" class="td_widget">保单状态</th>
				<th width="16%" class="td_widget">基本计划</th>
				<th width="7%" class="td_widget">投保人</th>
				<th width="7%" class="td_widget">被保险人</th>
				<th width="7%" class="td_widget">保费</th>
				<th width="9%" class="td_widget">缴费周期</th>
				<th width="8%" class="td_widget">VIP保费</th>
				<th width="12%" class="td_widget">代理人/客户经理</th>

			</tr>
		</thead>

		<tbody>
			<c:forEach items="${vipProductInfo.vipProductInfoList}" var="item"
				varStatus="status">
				<tr>
					<td>${item.branchName}</td>
					<td>${item.policyNo}</td>
					<td><fmt:formatDate value="${item.effectDate}" type="date"
						dateStyle="default" /></td>
					<td>${item.dutyDescription}</td>
					<td>${item.productName}</td>
					<td>${item.appName}</td>
					<td>${item.insName}</td>
					<td>${item.premSum}</td>
					<td>${item.periodType}</td>
					<td>${item.vipPremSum}</td>
					<td>${item.empName}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	</div>

	<div id="services" class="easyui-panel" title="VIP信息">

	<table class="infoDisTab">

		<tr>
			<td width="20%">
			<div id="services" class="easyui-panel" title="VIP等级变动信息">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th width="10%" class="td_widget">VIP等级</th>
						<th width="10%" class="td_widget">评定日期</th>
						<th width="10%" class="td_widget">结束日期</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${gradeChangeList}" var="item" varStatus="status">
						<tr>
							<td>${item.vipGrade}</td>
							<td><fmt:formatDate value="${item.vipEffectDate}"
								type="date" dateStyle="default" /></td>
							<td><fmt:formatDate value="${item.vipEndDate}"
								type="both" dateStyle="default" /></td>
						</tr>
					</c:forEach>
				</tbody>

			</table>
			</div>
			</td>

			<td width="40%">

			<div id="services" class="easyui-panel" title="VIP卡号变动信息">
			<table class="infoDisTab" width="40%">

				<thead>
					<tr>
						<th width="10%" class="td_widget">VIP卡号</th>
						<th width="15%" class="td_widget">时间</th>
						<th width="15%" class="td_widget">变更事由</th>
						<th width="10%" class="td_widget">发放人员</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${cardChangeList}" var="item" varStatus="status">
						<tr>
							<td>${item.cardNo}</td>
							<td><fmt:formatDate value="${item.modifiedDate}" type="date"
								dateStyle="default" /></td>
							<td>${item.modifiedReason}</td>
							<td>${item.providedUser}</td>

						</tr>
					</c:forEach>
				</tbody>


			</table>
			</div>


			</td>

			<td width="38%">
			<div id="services" class="easyui-panel" title="VIP服务专员变动信息">
			<table class="infoDisTab" width="25%">
				<thead>
					<tr>
						<th width="8%" class="td_widget">姓名</th>
						<th width="9%" class="td_widget">联系方式</th>
						<th width="20%" class="td_widget">所属部门</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${empChangeList}" var="item" varStatus="status">
						<tr>
							<td>${item.empName}</td>
							<td>${item.empPhone}</td>
							<td>${item.empDept}</td>

						</tr>
					</c:forEach>
				</tbody>

			</table>
			</div>
			</td>
		</tr>
	</table>
	</div>




	<div id="policyInfo" class="easyui-panel" title="享受VIP服务情况
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		服务方案:${clientServicePlan }">
	<table class="infoDisTab" id="productList">
		<thead>
			<tr>
				<th width="6%" class="td_widget">日期</th>
				<th width="8%" class="td_widget">大类</th>
				<th width="10%" class="td_widget">小项</th>
				<th width="25%" class="td_widget">说明</th>
				<th width="3%" class="td_widget">附件下载</th>
				<th width="2%" class="td_widget">操作</th>

			</tr>
		</thead>

		<tbody>
			<c:forEach items="${sharedList}" var="item" varStatus="status">
				<tr>
					<td><fmt:formatDate value="${item.vipServiceDate}" type="date"
						dateStyle="default" /></td>
					<td>${item.bigService}</td>
					<td>${item.smallService}</td>
					<td>${item.seviceDesc}</td>
					<td><c:if test="${not  empty item.fileId}">
						<a href="javascript:void(0)" class="downloadLink"
							title="${item.fileId}">下载</a>
					</c:if></td>
					<td align="center">
						<input type="hidden" value="${item.seqNo }"/>
						<a href="javascript:void(0)" class="operation" title="${item.isValid }">
							<c:if test="${item.isValid eq 'Y' }">作废</c:if>
							<c:if test="${item.isValid eq 'N' }">恢复</c:if>
						</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
</sf:override>
 
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>


