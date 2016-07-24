<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">信函管理&gt;&gt;退信任务交接</sf:override>
<sf:override name="head">
<script type="text/javascript">

		$(function() {
			$("#submitBt").click(function() {
				var letterManager = $.trim($("#letterManager").val());
				var letterManagerNew = $.trim($("#letterManagerNew").val());
				if(letterManager==''){
					$.messager.alert("失败", "请填写需要交接的原信函管理员");
					$("#letterManager").focus();
					return ;
				}
				if(letterManagerNew==''){
					$.messager.alert("失败", "请填写需要交接给的新信函管理员");
					$("#letterManagerNew").focus();
					return ;
				}
				$.ajax({
					type : "post",
					dataType : "json",
					async : true,
					data : {
						letterManager:letterManager,letterManagerNew:letterManagerNew
					},
					url : "${ctx}others/senderHandoverSubmit.do",
					success : function(data) {
						if(data.checkPass=="N"){
								$.messager.alert("失败", "填写的新信函管理员没有权限，请查看新信函管理员是否填写正确！");
								$("#letterManagerNew").focus();
								return ;
						}else{
							if(data.isChanged=="Y"){
								$.messager.alert("提示", "退信任务交接成功！");
								return ;
							}
						}
					}
				});
				});
	});
</script>
</sf:override>

<sf:override name="content">
<fieldset class="fieldsetdefault" style="padding:1px;">
	<form name="mainForm"  method="post">
	<table class="layouttable">	
	   <tr>
		     <td width="15%" class="layouttable_td_label">
		                    原信函管理员：
		       <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">		 
		     	   <input type="text" name="letterManager" id="letterManager" class="input_text" />
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	    	
	    <tr>
		      <td width="15%" class="layouttable_td_label">
		                   新信函管理员：
		          <span class="requred_font">*</span>
		      </td>
		      <td width="50%" class="layouttable_td_widget">
		      	<input type="text" name="letterManagerNew" id="letterManagerNew" class="input_text" />
		      </td>
		      <td>
		        &nbsp;
		      </td>
		      <td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-ok" id="submitBt">确定</a>
	         </td>
	     </tr>
	</table>
	</form>
</fieldset>	
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
