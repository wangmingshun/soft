
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">批量短信发送申请</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	var prodRes = null;//可选险种源json
	var posValidator;
	$(function() {
		$("form").validate();

		$("[name=startDate],[name=endDate]").click(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		$("#branchChooseBt").click(function() {
			showBranchTree($(this).prev(), "03", "${userBranchCode}");
			return false;
		});

		$('#productName').bind('click', function() {
			$.post("${ctx}include/allProducts", function(data) {
				prodRes = data;
				$("#productName").autocomplete({
					source : $.map(prodRes, function(it) {
						return it.FULL_NAME;
					})
				});
			}, "json");
		});

		$("#submitBt").click(function() {
			if ($("[name=sendText]").val() != '') {
				alert("该请求已经在后台提交，请关闭此页面，勿重复提交！");
				$("form").submit();
				this.disabled = true;
				return true;
			} else {
				alert("请输入发送内容！");
				return false;
			}

		});
	});
</script>
</sf:override>
<sf:override name="content">
	<form id=form action="${ctx}others/sendSms" class="noBlockUI"
		method="post">
	<table class="layouttable">
		<tr>
			<td width="10%" class="layouttable_td_label">保单所在机构：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				name="branchCode" class="{required:true}" value="${userBranchCode}" />
			<a href="javascript:void(0)" id="branchChooseBt">选择机构</a></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">保单生效日：</td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				name="startDate" class="Wdate"
				onfocus="WdatePicker({skin:'whyGreen',maxDate:'${CodeTable.posTableMap.SYSDATE}'})" />
			至 <input type="text" name="endDate" class="Wdate"
				onfocus="WdatePicker({skin:'whyGreen',maxDate:'${CodeTable.posTableMap.SYSDATE}'})" />
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">渠道：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="channelType" id="channelType">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>

		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">产品名称：</td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				id="productName" name="productName" size="50" /></td>
			<td>&nbsp;</td>

		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">VIP客户：</td>
			<td width="50%" class="layouttable_td_widget"><select name="vip">
				<option value=''>全部</option>
				<option value='N'>否</option>
				<option value='Y'>是</option>
			</select></td>
			<td>&nbsp;</td>

		</tr>
		<tr>
			<td width="20%" class="layouttable_td_label">是否考虑客户是否愿意接收短信：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="forceSend">
				<option value='N'>否</option>
				<option value='Y'>是</option>
			</select></td>
			<td>&nbsp;</td>

		</tr>

		<tr>
			<td width="10%" class="layouttable_td_label">发送时间：</td>
			<td width="55%" colspan="2" class="layouttable_td_widget"><input
				type="text" id="sendTime" name="sendDay"
				class="easyui-validatebox Wdate"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',skin:'whyGreen',minDate:'%y-%M-%d {%H+1}:%m'})" />
			</td>


		</tr>

        <tr>
			<td width="10%" class="layouttable_td_label">发送号码类型：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="mobileType">
				<option value='1'>短号</option>
				<option value='2'>长号</option>
			</select></td>
			<td>&nbsp;</td>

       </tr>


		<tr>
			<td width="10%" class="layouttable_td_label">短信内容：<span
				class="requred_font">*</span></td>
			<td colspan="2" width="50%" class="layouttable_td_widget"><textarea
				name="sendText" wrap="off" rows='5' cols="80"
				class="multi_text {byteRangeLength:[1,500]}"></textarea></td>


		</tr>
		<tr>
			<td><c:if test="${count>=0}">
				<span id='showNum'> 发送的短信数为：${count}条; </span>
			</c:if></td>
			<td colspan="2" align="right"><input class="btn" type="button"
				id="submitBt" value="提交" /></td>
		</tr>

	</table>
	<hr class="hr_default" />
	</form>
	<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>