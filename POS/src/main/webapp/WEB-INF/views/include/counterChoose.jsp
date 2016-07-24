<%@ page contentType="text/html;charset=UTF-8"%>
<script type="text/javascript">
	var inputObj;
	$(function() {
		$("#counterWindowChoseBt").click(function() {
			if ($("[name='counterRd']:checked").val() == undefined) {
				alert("请选择柜面！");
				return false;
			}
			inputObj.val($("[name='counterRd']:checked").val());
			$("#counterWindow").window("close");
			return false;
		});
	});
	
	//input将点选结果显示于此域
	function showCounterWindow(input,branchCode) {
		inputObj = input;
		$.get("${ctx}include/counter",
			  {branchCode:branchCode},
		    function(jsdata){
				
				$("#counterWindowTb *").remove();
                $.each(jsdata,function(i,map){
					addRow(map["COUNTER_NO"],
					       map["COUNTER_NAME"]
					      );
					});
					
			$("#counterWindow").window("open");
		}, "json");
	}
    
	function addRow(counterNo, counterName){
		var c = "";
		if(($("#counterWindowTb tr").length+1)%2==0){
			c = "even_column";
		}else{
			c = "odd_column";
		}
	   var str = '<tr class="'+c+'"><td align="right"><input type="radio" name="counterRd" value="'+counterNo+'" /></td><td>'+counterName+'</td></tr>';
	   $("#counterWindowTb").append(str);
	}
	
</script>

<div id="counterWindow" class="easyui-window" closed="true" modal="true" title="柜面信息" style="width: 500px; height: 500px;" align="left" collapsible="false" minimizable="false" maximizable="false">
    <div class="easyui-layout" fit="true">
        <div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc; overflow:scroll">
            <table class="infoDisTab">
             <thead>
              <tr>
                <th align="right" width="50px">选择</th>
                <th>柜面名称</th>
              </tr>
             </thead>
             <tbody id="counterWindowTb">
             </tbody>
            </table>
        </div>
        <div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
            <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="counterWindowChoseBt">确定</a>
        </div>
    </div>
</div>