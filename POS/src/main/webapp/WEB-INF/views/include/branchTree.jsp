<%@ page contentType="text/html;charset=UTF-8"%>
<script type="text/javascript">
	var $input;
	$(function(){
		$("#branchChoseBt").click(function(){
			if ($("#branchUl").tree("getSelected") == null) {
				$.messager.alert("提示","请选择机构！");
				return;
			}
			$input.val($("#branchUl").tree("getSelected").id);
			$input.trigger("change");
			$("#brachTree").window("close");
			return false;
		});
	});
	//input将点选结果显示于此域
	function showBranchTree(input, level, code) {
		$input = input;
		$("#branchUl").empty();
		$("#branchUl").html("<li>机构信息加载中，请稍后...</li>");
		$("#brachTree").window("open");
		
		if(level==undefined){
			level="";
		}
		if(code==undefined){
			code="";
		}
		
		$("#branchUl").tree({
			url:"${ctx}include/branchTree?branchCode=" + code + "&branchLevel=" + level + "&root=Y",
			onDblClick:function(){
				$("#branchChoseBt").trigger("click");
			},
			onBeforeExpand:function(node,param) {
				$('#branchUl').tree('options').url =  "${ctx}include/branchTree?branchCode=" + node.id + "&branchLevel=" + level + "&root=N";
			}
		});
	}

</script>
<div id="brachTree" class="easyui-window" closed="true" modal="true" title="机构信息" style="width: 320px; height: 400px;" align="left" collapsible="false" minimizable="false" maximizable="false">
    <div class="easyui-layout" fit="true">
        <div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
            <ul id="branchUl">
            </ul>
        </div>
        <div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
            <a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="branchChoseBt">确定</a>
        </div>
    </div>
</div>