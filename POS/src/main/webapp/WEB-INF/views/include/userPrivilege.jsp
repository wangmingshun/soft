<%@ page contentType="text/html;charset=UTF-8"%>
<script type="text/javascript">
	function showUserPrivilege(user) {
		$.post("${ctx}include/userPrivilege", 
		    {user:user},
		    function(data) {

                $("#privilegeTb *").remove();//先删除原有数据
				trClass = "odd_column";
                $.each(data,function(i,map){
		          addPrivilegeRow(map["USER_ID"],
					              map["USER_NAME"],
					              map["UPPER_USER_ID"],
					              map["INPUT_GRADE"],
					              map["DIFFERENT_PLACE_PRIVS"],
					              map["COUNTER_NAME"],
								  map["AMOUNT_DAYS_GRADE"]
					      );
					});
					
			$("#userPrivilege").window("open");

		}, "json");
	}
	var trClass = null;
	function addPrivilegeRow(userId,
	                         userName,
	                         upperUserId,
							 rankName,
							 differentPlacePrivs,
							 counterName,
							 amountDaysGrade){

		var str = "<tr class=\"" + trClass + "\"><td>"+userId+"</td><td>"+userName+"</td><td>"+upperUserId+"</td><td>"+rankName
		         +"</td><td>"+differentPlacePrivs+"</td><td>"+counterName+"</td><td>"+amountDaysGrade+"</td></tr>";
			
		$("#privilegeTb").append(str);
		if(trClass == "odd_column") {
			trClass = "even_column";
		} else {
			trClass = "odd_column";
		}
	}
	
</script>
<div style="display:none;">
<div id="userPrivilege" class="easyui-window" closed="true" modal="true" title="用户权限明细" style="width: 800px; height: 200px;" collapsible="false" minimizable="false" maximizable="false">
	<table class="infoDisTab">
		<thead>
           <tr>
			  <th>用户代码</th>
              <th>用户名称</th>
              <th>上级用户</th>
              <th>录入等级</th>
              <th>异地权限</th>
              <th>所属柜面</th>
              <th>审批等级</th>
		   </tr>
		</thead>
		<tbody class="list_td" id="privilegeTb">
		</tbody>
	</table>
</div>
</div>