<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">用户管理&gt;&gt;工作交接确认</sf:override>

<script type="text/javascript">
  $(function(){
	//任务细项的全选
    $("#taskAllCb").click(function(){
		$("[name=taskPK]").attr("checked",this.checked);
	});
	//工作类型的全选
	$("#workAllCb").click(function(){
		$("[name=workPK]").attr("checked",this.checked);
	});
	//提交
	$(".confirmBt").click(function(){
		$("#handoverStatus").val($(this).attr("name"));
		$("form[name=f]").submit();
		return false;
	});
	
	$("form[name=f]").validate();
	
  })
	
</script>
</sf:override>
<sf:override name="content">

<form name="f" action="${ctx}jobswitch/confirm" method="post">

 <div class="easyui-panel" collapsible="true" title="用户${USER}待确认交接任务明细">
   <table class="infoDisTab">
     <thead>
        <tr>
		    <th>任务类型</th>
			<th>保单号</th>
			<th>条形码</th>
			<th>批单号</th>
			<th>任务步骤</th>
            <th>工作原处理人</th>
			<th>选择<input type="checkbox" id="taskAllCb" name="taskAllCb" class="{required:function(){return $('.cb:checked').length<1}}" />全选</th>
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
				    <td>${task.TASK_TYPE_DESC}</td>
                    <td>${task.POLICY_NO}</td>
                    <td>${task.BARCODE_NO}</td>
                    <td>${task.POS_NO}</td>
                    <td>${task.STEP}</td>
                    <td>${task.HANDOVER}</td>
                    <td>
                    <input type="checkbox" name="taskPK" value="${task.TASK_TYPE}+${task.PK_SERIAL}" class="cb" />
                    </td>
				</tr>
			</c:forEach>
            <c:if test="${empty taskList}">
                <tr><td align="center" colspan="7">没有待确认任务明细</td></tr>
            </c:if>
     </tbody>
   </table>
 </div>
 <br />
   
 <div class="easyui-panel" collapsible="true" title="用户${USER}待确认交接工作类型">
   <table class="infoDisTab">
     <thead>
        <tr>
		    <th>工作原处理人</th>
			<th>交接截止日期</th>
			<th>工作类型</th>
			<th>
            选择<input type="checkbox" id="workAllCb" name="workAllCb" class="{required:function(){return $('.cb:checked').length<1}}" />全选</th>
		</tr>
     </thead>
     <tbody>
			<c:forEach var="work" items="${workList}" varStatus="status">
                <c:if test="${status.index%2==0}">
                    <tr class="odd_column">
                </c:if>
                <c:if test="${status.index%2!=0}">
                    <tr class="even_column">
                </c:if>
                    <td>${work.HANDOVER}</td>
                    <td>${work.HANDOVER_END_DATE}</td>
                    <td>${work.TYPE_DESC}</td>
                    <td>
                    <input type="checkbox" name="workPK" value="${work.PK_SERIAL}" class="cb" />
                    </td>
				</tr>
			</c:forEach>
            <c:if test="${empty workList}">
                <tr><td align="center" colspan="4">没有待确认工作类型</td></tr>
            </c:if>
     </tbody>
   </table>
 </div>
 
   <div align="right">
     <a class="easyui-linkbutton confirmBt" href="javascript:void(0)" iconCls="icon-ok" name="2">接受</a>
     <a class="easyui-linkbutton confirmBt" href="javascript:void(0)" iconCls="icon-cancel" name="3">拒绝</a>
     <input type="hidden" id="handoverStatus" name="handoverStatus" />
   </div>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>

<c:if test="${not empty RETURN_MESSAGE}">
  <script type="text/javascript">
	alert("${RETURN_MESSAGE}");
	$("form").attr("action","${ctx}setting/user/jobswitch/confirm");
	$("form").submit();
  </script>
</c:if>