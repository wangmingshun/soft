<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代审权限管理&gt;&gt;新增不良记录</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form").validate();		
	$("#save").click(function(){
		if($("#objectNo").val()==''||$("#objectName").val()==''||$("#policyNo").val()==''||$("#descrpiton").val()==''){
			$.messager.alert("提示", "请录入*号必录项！");
			return;			
		}
		$.messager.confirm('确认保存', '您确认要保存此不良记录吗？', function(r){
			if (r){
				$.ajax({type: "GET",
					url:"${ctx}approve/insertBadBehavior.do",
					data:$(form).formSerialize(),
					cache: false,
					async: false,
					dataType:"text",
					success:function(data){
						if(data=="Y"){
					        $.messager.alert("提示", "保存成功！", "info", function () {
								window.location.href="${ctx}approve/queryBadBehavior";
								return false;							
					        });	
							return false;						
						}else if(data=="E"){
							$.messager.alert("提示", "是否需要送审判断出错！");													
						}else{
							$.messager.alert("提示", "保存失败！");													
						}
					}
				});	
			}
		});
		return false;			
	});	
	$("#objectNo").blur(function(){
		var objectNo=$("#objectNo").val();
		var objectType=$("#objectType").val();
		$.ajax({type: "GET",
			url:"${ctx}approve/getObjectInfoByObjectNo.do?needCheckPrivs=N&objectType="+objectType,
			data:{"objectNo" :objectNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.objectName==""||data.objectName==null){
					$.messager.alert("提示", "获取对象信息失败,请核查对象号码！");	
					$("#objectName").val("");	
					return false;
				}else{
					$("#objectName").val(data.objectName);													
				}
			}
		});		
	});	
	$("#objectType").change(function(){
		$("#objectNo").val("");
		$("#objectName").val("");	
	});	
});

</script>
</sf:override>
<sf:override name="content">

<form id="form">

<div id="p1" class="easyui-panel">
<table class="layouttable">
  <tr>
    <td width="15%" class="layouttable_td_label">类别：</td>
    <td class="layouttable_td_widget">
	    <select id="objectType" name="objectType">					
		    <option value="1">网点</option>
		    <option value="2">银代经理</option>
		</select>
    </td>
  </tr>
  <tr>    
    <td width="15%" class="layouttable_td_label">对象号码：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
		<input type="text"  id="objectNo" name="objectNo" value="" class="{required:true}"/>
    </td>    
  </tr>
  <tr>
    <td width="15%" class="layouttable_td_label">对象名称：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
		<input type="text"  readonly id="objectName" name="objectName" value="" class="{required:true}"/>
    </td>
  </tr>
  <tr>    
    <td width="15%" class="layouttable_td_label">保单号：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
		<input type="text"  name="policyNo" id="policyNo" value=""/>
    </td>    
  </tr>
  <tr>
    <td width="15%" class="layouttable_td_label">行为类别：</td>
    <td class="layouttable_td_widget">
	    <select id="behaviorType" name="behaviorType">					
		    <option value="1">有效投诉件数</option>
		    <option value="2">疑似销售误导</option>
		    <option value="3">疑似代签名</option>
		    <option value="4">伪造保全资料</option>
		    <option value="5">其它</option>		    
		</select>		
    </td>
  </tr>
  <tr>    
    <td width="15%" class="layouttable_td_label">备注：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
		<textarea name="descrpiton" id="descrpiton" wrap="off" cols="40" value="" ></textarea>		
    </td>    
  </tr>    
</table>
</div>
<br />
<div align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="save" >保存</a>
</div>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>