<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">用户管理&gt;&gt;工作交接设置</sf:override>

<script type="text/javascript">
  $(function(){

	//任务的全选
    $("#taskAllCb").click(function(){
		$(".taskCb").attr("checked",this.checked);
	});
	//工作类型的全选
	$("#workAllCb").click(function(){
		$(".workCb").attr("checked",this.checked);
	});

    //查询权限明细
    $(".privilegeBt").click(function(){
		var v = $.trim($(this).prev().val());
		if(v==""){
			$.messager.alert("提示","请输入用户名!");
			return false;
		}
		showUserPrivilege(v);
		return false;
	});
    
    $("#endDate").click(function(){
		WdatePicker({skin:'whyGreen',minDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt").click(function(){
		$("form[name=queryForm]").attr("action","${ctx}jobswitch/queryjobsinfo");
		$("form[name=queryForm]").submit();
		return false;
	});
	$("#cancelBt").click(function(){
		$("form[name=queryForm]").attr("action","${ctx}jobswitch/workcancel");
		$("form[name=queryForm]").submit();
		return false;
	});
	
	$("#switchTaskBt").click(function(){
		if($("#taskUndertaker").val()==""){
			$.messager.alert("提示","请先输入承接人");
			return false;
		}
		if(sameUser($("#taskUndertaker").val(),$("#taskHandover").val())){
			$.messager.alert("提示","承接人与交接人不能相同");
			return false;
		}
		$.post("${ctx}jobswitch/amountDaysGradeCheck", 
		{handover:"${USER_ID}",
		 undertaker:$("#taskUndertaker").val()},
		function(data) {
			if(data=="Y"){
				$("form[name=taskForm]").submit();
			}else{
				$.messager.alert("提示","该承接人的审批等级小于工作交接人，不能做交接!");
			}
		}, "text");
		return false;
	});
	$("#switchWorkBt").click(function(){
		if($("#workUndertaker").val()==""){
			$.messager.alert("提示","请先输入承接人!");
			return false;
		}
		if(sameUser($("#workUndertaker").val(),$("#workhandover").val())){
			$.messager.alert("提示","承接人与交接人不能相同");
			return false;
		}
		$.post("${ctx}jobswitch/amountDaysGradeCheck", 
		{handover:"${USER_ID}",
		 undertaker:$("#workUndertaker").val()},
		function(data) {
			if(data=="Y"){
				$("form[name=workForm]").submit();
			}else{
				$.messager.alert("提示","该承接人的审批等级小于工作交接人，不能做交接!");
			}
		}, "text");
		return false;
	});
	
	$("form[name=queryForm]").validate();
	$("form[name=taskForm]").validate();
	$("form[name=workForm]").validate();
  });

//两个id是否是同一个人，即除去@后缀进行比较
function sameUser(id1,id2){
	if(id1.indexOf("@")>0){
		id1 = id1.substring(0, id1.indexOf("@"));
	}
	if(id2.indexOf("@")>0){
		id2 = id2.substring(0, id2.indexOf("@"));
	}
	if(id1==id2){
		return true;
	}else{
		return false;
	}
}

</script>
</sf:override>
<sf:override name="content">

<form name="queryForm">

<div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">请输入用户名:
      </td>
      <td class="layouttable_td_widget"><input type="text" id="userIdInput" name="userIdInput" value="${USER_ID}" class="{required:true}" /></td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="cancelBt">取消工作交接</a>
      </td>
    </tr>
  </table>
</div>
<br />

</form>

<form name="taskForm" action="${ctx}jobswitch/tasktosomeguy">
    <div class="easyui-panel" collapsible="true" title="用户${USER_ID}现有任务明细">
    <input type="hidden" name="handover" id="taskHandover" value="${USER_ID}" class="{required:true,messages:{required:'请先输入用户进行查询'}}" />
	<table class="infoDisTab">
		<thead>
            <tr>
			    <th>任务类型</th>
				<th>保单号</th>
				<th>条形码</th>
				<th>批单号</th>
				<th>任务步骤</th>
				<th>是否交接<input type="checkbox" name="taskAllCb" id="taskAllCb" class="{required:function(){return $('.taskCb:checked').length<1}}" />全选</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="task" items="${taskList}" varStatus="status">
                <c:if test="${status.index%2==0}">
                    <tr class="odd_column">
                </c:if>
                <c:if test="${status.index%2!=0}">
                    <tr class="even_column">
                </c:if>
				    <td>${task.JOB_TYPE_DESC}</td>
                    <td>${task.POLICY_NO}</td>
                    <td>${task.BARCODE_NO}</td>
                    <td>${task.POS_NO}</td>
                    <td>${task.STATUS}</td>
                    <td>
                    <c:choose>
                      <c:when test="${task.JOB_TYPE eq '1'}">
                        <input type="checkbox" class="taskCb" value="${task.POS_NO}+${task.PK}" name="pkPosNo" />
                      </c:when>
                      <c:when test="${task.JOB_TYPE eq '2'}">
                        <input type="checkbox" class="taskCb" value="${task.POS_NO}+${task.PK}" name="pkApprove" />
                      </c:when>
                      <c:when test="${task.JOB_TYPE eq '3'}">
                        <input type="checkbox" class="taskCb" value="${task.POS_NO}+${task.PK}" name="pkProblem" />
                      </c:when>
                    </c:choose>
                    </td>
				</tr>
			</c:forEach>
			<c:if test="${not empty USER_ID && empty taskList}">
			  <tr>
			    <td colspan="6" align="center">没有待交接任务</td>
			  </tr>
            </c:if>
		</tbody>
	</table>
    <table width="100%">
        <tr>
          <td colspan="3">请分配给用户：
            <input type="text" name="undertaker" id="taskUndertaker" />
            <a class="privilegeBt" href="javascript:void(0)">查询该用户权限明细</a>
          </td>
          <td colspan="3" align="right"><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="switchTaskBt">确定</a></td>
        </tr>
    </table>
  </div>
<br />
    
</form>

<div class="easyui-panel" collapsible="true" title="用户${USER_ID}已交接待确认任务明细">
	<table class="infoDisTab">
		<thead>
            <tr>
			    <th>任务类型</th>
				<th>保单号</th>
				<th>条形码</th>
				<th>批单号</th>
				<th>任务步骤</th>
                <th>交接时间</th>
                <th>承接人</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="task" items="${confirmList}" varStatus="status">
                <c:if test="${status.index%2==0}">
                    <tr class="odd_column">
                </c:if>
                <c:if test="${status.index%2!=0}">
                    <tr class="even_column">
                </c:if>
				    <td>${task.JOB_TYPE_DESC}</td>
                    <td>${task.POLICY_NO}</td>
                    <td>${task.BARCODE_NO}</td>
                    <td>${task.POS_NO}</td>
                    <td>${task.STATUS}</td>
                    <td>${task.HANDOVER_DATE}</td>
                    <td>${task.UNDERTAKER}</td>
				</tr>
			</c:forEach>
			<c:if test="${not empty USER_ID && empty confirmList}">
			  <tr>
			    <td colspan="7" align="center">没有已交接待确认任务</td>
			  </tr>
            </c:if>
		</tbody>
	</table>
</div>
<br />

<form name="workForm" action="${ctx}jobswitch/worktosomeguy">
  <div class="easyui-panel" collapsible="true" title="用户${USER_ID}待交接工作类型">
    <input type="hidden" name="handover" id="workhandover" value="${USER_ID}" class="{required:true,messages:{required:'请先输入用户进行查询'}}" />
	<table class="infoDisTab">
		<thead>
            <tr>
			    <th width="10%">任务类型</th>
                <th width="20%">当前交接状态</th>
				<th width="15%">
                是否交接<input type="checkbox" id="workAllCb" name="workAllCb" class="{required:function(){return $('.workCb:checked').length<1}}" />全选</th>
                <th></th>
			</tr>
		</thead>
		<tbody>
			<tr>
			   <td>受理件</td>
               <td>
                 <c:if test="${not empty USER_ID}">
                 ${empty workStatus.POS_UNDERTAKER?"未交接":"已交接给："}${workStatus.POS_UNDERTAKER}
                 </c:if>
               </td>
			   <td>
               <input type="checkbox" name="workType" class="workCb" value="1" />
               </td>
               <td>
               </td>
			</tr>
            <tr class="even_column">
			   <td>审批件</td>
			   <td>
			     <c:if test="${not empty USER_ID}">
                 ${empty workStatus.APPROVE_UNDERTAKER?"未交接":"已交接给："}${workStatus.APPROVE_UNDERTAKER}
                 </c:if>
               </td>
			   <td>
               <input type="checkbox" name="workType" class="workCb" value="2" />
               </td>
               <td>
               </td>
			</tr>
            <tr>
			   <td>问题件</td>
			   <td>
			     <c:if test="${not empty USER_ID}">
                 ${empty workStatus.PROBLEM_UNDERTAKER?"未交接":"已交接给："}${workStatus.PROBLEM_UNDERTAKER}
                 </c:if>
               </td>
			   <td>
               <input type="checkbox" name="workType" class="workCb" value="3" />
               </td>
               <td align="left">
               请分配给用户：<input type="text" name="undertaker" id="workUndertaker" />
                 <a class="privilegeBt" href="javascript:void(0)">查询该用户权限明细</a>
               </td>
			</tr>
            <tr class="even_column">
			   <td>核保函待打印件</td>
			   <td>
			     <c:if test="${not empty USER_ID}">
                 ${empty workStatus.UWPRINT_UNDERTAKER?"未交接":"已交接给："}${workStatus.UWPRINT_UNDERTAKER}
                 </c:if>
               </td>
			   <td>
               <input type="checkbox" name="workType" class="workCb" value="4" />
               </td>
               <td align="left">
               工作交接原因：<input type="text" size="40" class="{required:true}" name="cause" />
               </td>
			</tr>
            <tr>
			   <td>核保函待回销件</td>
			   <td>
			     <c:if test="${not empty USER_ID}">
                 ${empty workStatus.UWFEEDBACK_UNDERTAKER?"未交接":"已交接给："}${workStatus.UWFEEDBACK_UNDERTAKER}
                 </c:if>
               </td>
			   <td>
               <input type="checkbox" name="workType" class="workCb" value="5" />
               </td>
               <td align="left">
                 交接截止时间：<input type="text" id="endDate" name="endDate" class="Wdate {required:true}" />
               </td>
			</tr>
		</tbody>
	</table>
  </div>
  <div align="right"><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="switchWorkBt">确定</a></div>
</form>

<jsp:include page="/WEB-INF/views/include/userPrivilege.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>