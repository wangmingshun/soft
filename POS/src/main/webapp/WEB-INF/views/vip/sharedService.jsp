<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP客户资料查询&gt;&gt;尊荣服务记录</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	posValidateHandler = function() {
		return true;
	};
	
	function resetFun() {
		$("#servcieDate").attr("value",'');	
		$("#serviceReason").attr("value",'');
		return false;
	}
	$(function() {

		$("#vipBigServiceItem").trigger("change");

		$("#servcieDate").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		if ($("#attechmentFileId").val()) {
			$("#attachmentLink").html("删除");
			$("#fileToUpload").hide();
		} else {
			$("#attachmentLink").html("上传");
			$("#fileToUpload").show();
		}
	    $("#attachmentLink1").click(
				function() {

					var $this = $(this);
					if ($this.html() == "删除") {
						$("#loading").show();
						$this.hide();
						$.post("${ctx}vip/clearAttechment.do",
								null, function(data) {
									if (data && data.flag == "Y") {
										$this.html("上传");
										$("#fileName").html("");
										$("#attechmentFileId").val("");
										$("#attechmentFileName").val("");
										$("#fileToUpload").val("").show();
										$.messager.alert("提示", "删除附件成功!");
									} else {
										$.messager.alert("提示", "删除附件失败!");
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
						$
								.ajaxFileUpload({
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

		$("#insertBt").click(function() {
			$("#fm").submit();
			return false;
		});

		$("#applyDate").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});
		$("#fm").validate();

		$("#vipBigServiceItem").change(
				function() {
					var bigServiceItem = $(this).val();
					var $vipBigServiceItem = $("#vipBigServiceItem");
					var $vipSmallServiceItem = $("#vipSmallServiceItem");
					$vipSmallServiceItem.find("option").remove();
					$.post("${ctx}vip/queryVipSamllService.do", {
						"bigServiceItem" : bigServiceItem
					},
							function(data) {
								for ( var i = 0; data && data.length
										&& i < data.length; i++) {

									$("<option/>").attr("value", data[i].code)
											.html(data[i].description)
											.appendTo($vipSmallServiceItem);
								}
							});

				});

	
		
		$("#vipBigServiceItem").trigger("change");
	});
</script>
</sf:override>
<sf:override name="content">

	<sfform:form commandName="vipApplyDetailDto" enctype="multipart/form-data"
		action="${ctx}vip/sharedServiceInsert" id="fm">
		<div class="easyui-panel">
		<table class="layouttable">
			<tr>
				<td class="layouttable_td_label">时间：<span class="requred_font">*</span></td>
				<td class="layouttable_td_widget"><sfform:date id="servcieDate"
					path="servcieDate"
					class="Wdate {required:true,messages:{required:'请输入日期'}}" /></td>
			</tr>

			<tr>
				<td class="layouttable_td_label">服务大项:</td>
				<td class="layouttable_td_widget"><sfform:select
					id="vipBigServiceItem" path="vipBigServiceItem"
					items="${vipBigServiceItemList}" itemLabel="description"
					itemValue="code" cssClass="input_select" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">服务小项:</td>
				<td class="layouttable_td_widget"><select
					id="vipSmallServiceItem" name="vipSmallServiceItem">
					<option>--</option>
				</select></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">附件：</td>
				<td class="layouttable_td_widget">
				  <input type="file" name="attechmentFileName" id="attechmentFileName" class="{required:function(element){if($('#vipBigServiceItem').val()=='2'){return true;}return false;}}" />
				 <!--<sfform:hidden
					path="attechmentFileId" id="attechmentFileId" /> <sfform:hidden
					path="attechmentFileName" id="attechmentFileName" /> <input
					id="fileToUpload" type="file" size="20" name="fileToUpload" /> <span
					id="fileName">${posInfo.attechmentFileName}</span> <img
					id="loading"
					src="${ctx}CacheableResource/javascript_v1_01/ajax_file_uploader_v2_1/loading.gif"
					style="display: none; width: 16px; height: 16px;" /> <a
					href="javascript:void(0)" id="attachmentLink"></a>
					
					function(){if($('vipBigServiceItem').val()=='2'){return true}return false;}
					-->
					</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">备注说明：</td>
				<td><sfform:textarea id="serviceReason" path="serviceReason"
					cssClass="multi_text {byteRangeLength:[10,500]}" cols="30" rows="2" /></td>
			</tr>


			<tr>
				<td></td>
				<td align="right"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search" id="insertBt">新增</a>
				<a class="easyui-linkbutton" onclick="resetFun();"
					iconCls="icon-reset" id="reset">重置</a> <a class="easyui-linkbutton"
					onclick="javascript:history.back();" iconCls="icon-search"
					id="backBt">返回</a></td>
			</tr>
		</table>


		</div>

	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
