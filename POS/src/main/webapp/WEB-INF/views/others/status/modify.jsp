<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保全受理状态修改</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	$("#queryBt").click(function(){
		$("form").attr("action","${ctx}others/statusModify/query");
		$("form").submit();
		return false;
	});
	$("#modifyBt").click(function(){
		$("[name=posNoInput]").removeClass();
		$("form").attr("action","${ctx}others/statusModify/submit");
		$("form").submit();
		return false;
	});

	if($("#attechmentFileId").val()) {
		$("#attachmentLink").html("删除");
		$("#fileToUpload").hide();
	} else {
		$("#attachmentLink").html("上传");
		$("#fileToUpload").show();
	}
	$("#attachmentLink").click(function(){
		
	
		var $this = $(this);
		if($this.html() == "删除") {
			$("#loading").show();
			$this.hide();
			$.post("${ctx}acceptance/branch/clearAttechment.do", null, function(data){
				if(data && data.flag == "Y") {
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
			if(!$("#fileToUpload").val()) {
				$.messager.alert("提示", "请选择要上传的文件!");
				return false;
			}
			$("#loading").show();
			$this.hide();
			$.ajaxFileUpload({
				url :'${ctx}include/uploadFile.do',
				secureuri : false,
				fileElementId : "fileToUpload",
				dataType : "json",
				data : {filePath : "acceptance/recording"},
				success : function(data, status) {
					$("#loading").hide();
					$this.show();
					if(data.errorMessage){
						$.messager.alert("失败", errorMessage);
					} else {
						$("#attechmentFileId").val(data.fileId);
						$("#attechmentFileName").val(data.fileName);
						$("#fileName").html(data.fileName);
						$this.html("删除");
						$("#fileToUpload").hide();
					}
				}, error: function (data, status, e){
					$.messager.alert("失败", e);
					$("#loading").hide();
					$this.show();
				}
			});
		}
		return false;
	});
	
  });


</script>
</sf:override>
<sf:override name="content">

<sfform:form name="mainForm" commandName="acceptance__item_input">
<div id="p1" class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">保全批单号：</td>
      <td class="layouttable_td_widget">
      <sfform:input path="posNo" cssClass="input_text {required:true}" />     
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
</div>

<br />

<c:if test="${not empty posInfo}">
<div id="p2" class="easyui-panel" title="保全信息">
    <table class="layouttable">
      <tr>
      	<td class="layouttable_td_label">保全批单号：</td>
        <td class="layouttable_td_widget">
        	${posInfo.POS_NO}
            <!--<input type="hidden" name="posNo" value="${posInfo.POS_NO}" class="{required:true}" />
        --></td>
        <td class="layouttable_td_label">保单号：</td>
        <td class="layouttable_td_widget">${posInfo.POLICY_NO}</td>
        <td class="layouttable_td_label">保全项目：</td>
        <td class="layouttable_td_widget">
            <c:choose>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'E01'}">
						         保单补发
						</c:when>
						<c:otherwise>
						    ${posInfo.DESCRIPTION}
						</c:otherwise>
				</c:choose>
						</td>
      </tr>
      <tr>
        <td class="layouttable_td_label">现状态：</td>
        <td class="layouttable_td_widget">
         <c:choose>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'E01'}">
						        已生效
						</c:when>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'C01'}">
						   处理规则不通过
						</c:when>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'C05'}">
						      受理规则不通过
						</c:when>
				</c:choose>      
    
       </td>
        <td class="layouttable_td_label">修改后状态：</td>
        <td class="layouttable_td_widget" >
         <c:choose>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'E01'}">
						        待保单打印
						</c:when>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'C01'}">
						       待预处理
						</c:when>
						<c:when test="${posInfo.ACCEPT_STATUS_CODE eq 'C05'}">
						       批次受理
						</c:when>
				</c:choose>      
        
      </td> 
      <td class="layouttable_td_label">附件:</td>
						<td class="layouttable_td_widget">
						<sfform:hidden	path="attechmentFileId" id="attechmentFileId" /> 
						<sfform:hidden 	path="attechmentFileName" id="attechmentFileName" /> 
						<input id="fileToUpload" type="file" size="20" name="fileToUpload" /> 
					<span id="fileName">${posInfo.attechmentFileName}</span>
						<img id="loading"
							src="${ctx}CacheableResource/javascript_v1_01/ajax_file_uploader_v2_1/loading.gif"
							style="display: none; width: 16px; height: 16px;" /> <a
							href="javascript:void(0)" id="attachmentLink"></a></td>
      
      </tr>
      <tr>     
       <td class="layouttable_td_label">业务审批号：</td>
        <td class="layouttable_td_widget"  >
        <sfform:input path="approveNo"   />
         </td> 
        <td class="layouttable_td_label">调整原因：</td>
        <td class="layouttable_td_widget"  colspan="4">
        <sfform:textarea path="remark"  wrap="off" rows="1" cols="50" cssClass="multi_text {required:true,byteRangeLength:[0,200]}" />
         </td>      
      </tr>
    </table>
</div>
<div align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="modifyBt">修改</a>
</div>
</c:if>

</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
