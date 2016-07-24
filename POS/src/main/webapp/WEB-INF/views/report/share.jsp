<%@ page contentType="text/html;charset=UTF-8"%>
<script type="text/javascript">
//清单页面选择机构后，校验机构号码
$(function(){

	$("[name=branchCode]").change(function(){
		if($("[name=branchCode]").val()==""){
			return false;
		}
			$.post("${ctx}include/checkUserBranchCode",
			{branchCode:$("[name=branchCode]").val()},
			function(data){
				if(data=="N"){
					$.messager.alert("提示", "机构码录入不正确");
					$("[name=branchCode]").val("");
				}else{
					$("#branchCodeCheck").val($("[name=branchCode]").val()+"Y");
				}
			}, "text");
    });
	
	$(":input").click(function(){
		$("#sub").removeAttr("disabled");
	});

});

posValidateHandler=function(){
	if(($('[name=branchCode]').val()+'Y')!=$('#branchCodeCheck').val()){
		$.messager.alert("提示", "机构码检测中，请略等数秒再提交");
		return false;
	}

	$("#sub").attr("disabled",true);
	return true;
}
/* 校验同一用户同一清单是否重复提交 edit by gaojiaming start */	
function checkReportTaskStatus(listCode){
	var flag=false;
	 $.ajax({type: "GET",
			url:"${ctx}report/checkReportTaskStatus.do",
			data:{"listCode" :listCode},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if (data!='0'){
					$.messager.alert("提示", "此清单已经提交待处理，不能重复提交！");
					flag =false;
				}else{
					flag =true;						
				}
			}
		});
	 return flag;
}	
/* 校验同一用户同一清单是否重复提交 edit by gaojiaming end */	
</script>

<input type="hidden" id="branchCodeCheck" value="${userBranchCode}Y" />

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>