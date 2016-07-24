<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">用户管理&gt;&gt;岗位变更设置</sf:override>
<sf:override name="head">
<script type="text/javascript">
	$(function() {
		
		//查询下属明细
		$("#detailBt").click(function(){
			if($("#juniorDetailTb tr").length>0){
				$("#juniorDetailDiv").window("open");
				return false;
			}
         $.post("${ctx}setting/user/dutychange/queryJuniorDetail", 
			{userId:$("#userIdHid").val()},
			function(data) {
                $.each(data,function(i,map){
					var c = "";
					if(($("#juniorDetailTb tr").length+1)%2==0){
						c = "even_column";
					}else{
						c = "odd_column";
					}					
					$("#juniorDetailTb").append('<tr class="'+c+'"><td>'+map["USER_ID"]+'</td><td>'+map["USER_NAME"]+'</td></tr>');
				});
									
				$("#juniorDetailDiv").window("open");
		    }, "json");
         return false;

		});

        //设置新上级
		$("#setDirectorBt").click(function(){
		 
		 if($.trim($("#directorIdTxt").val())<1){
			 $.messager.alert("提示","请输入新上级用户名！");
			 return false;
		 }
		 
         $.post("${ctx}setting/user/dutychange/setDirector", 
			{userId:$("#userIdHid").val(),
			 directorId:$("#directorIdTxt").val()},
			function(data){
				if(data=="Y"){
					$.messager.alert("提示","设置成功！");
				}else if(data=="X"){
					$.messager.alert("提示","输入的新上级用户已是当前上级用户的间接下级用户，不能设置");
				}
		    }, "text");
         return false;
		});
		
		$("#queryDutyBt").click(function(){
			$("form").submit();
			return false;
		});
		
		$("form").validate();
	});

</script>
</sf:override>
<sf:override name="content">
<form action="${ctx}setting/user/dutychange/query">

    <div class="easyui-panel">
      <table class="layouttable">
        <tr>
          <td class="layouttable_td_label">请输入用户名:
          </td>
          <td class="layouttable_td_widget">
            <input type="text" name="userIdTxt" value="${userMap.USER_ID}" class="{required:true}" />
            <input type="hidden" id="userIdHid" value="${userMap.USER_ID}" />
          </td>
          <td align="right">
            <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryDutyBt">查询</a>
          </td>
        </tr>
      </table>
    </div>
    <br />

      <div class="easyui-panel" title="用户信息及设置">
        <table class="layouttable">
			<tr>
				<td class="layouttable_td_label" width="25%">角色名称：</td>
				<td class="layouttable_td_widget" colspan="2">${userMap.ROLE_NAME}&nbsp;</td>
			</tr>
            <tr>
                <td class="layouttable_td_label">下属数量：</td>
                <td class="layouttable_td_widget" colspan="2">
                <input type="text" value="${userMap.JUNIOR_COUNT}" readonly="readonly" />
                <a id="detailBt" href="javascript:void(0)">详细信息</a>
                </td>
            </tr>
            <tr>
                <td class="layouttable_td_label">新上级用户名：</td>
                <td class="layouttable_td_widget">
                <input type="text" id="directorIdTxt" />
                </td>
                <td align="right">
                <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="setDirectorBt">设置</a>
                </td>
            </tr>
		</table>
      </div>

</form>

<div id="juniorDetailDiv" class="easyui-window" closed="true" modal="true" title="下属详细列表" style="width: 400px; height: 300px; left:10px; top:200px" collapsible="false" minimizable="false" maximizable="false">
	<table class="infoDisTab">
		<thead>
           <tr>
			  <th>用户代码</th>
              <th>用户名称</th>
		   </tr>
		</thead>
		<tbody id="juniorDetailTb">
		</tbody>
	</table>
</div>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>