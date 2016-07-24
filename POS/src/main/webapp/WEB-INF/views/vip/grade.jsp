<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">VIP等级管理</sf:override>
<script type="text/javascript">
callback=function(){
	$("form[name=updateForm]").attr("action","${ctx}vip/grade/query");
	$("form[name=updateForm]").submit();
	return false;
}
$(function(){
	$("form[name=trialForm]").validate();
	$("form[name=updateForm]").validate();
	
	$("#queryClientBt").click(function(){
		openClientwindow($("#updateFormClientNo"),callback);
	});
	$("#updateFormClientNo").change(function(){
	
	});

	$("#updateSubmitBt","form[name=updateForm]").live("click", function(){
		$("form[name=updateForm]").attr("action","${ctx}vip/grade/update");
		$("select[name='vipGrade']").attr("disabled", false);
		$("form[name=updateForm]").submit();
		return false;
	});
	
	$("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
	
	$("select[name='operationType']").change(function() {
		var str = '<a class="easyui-linkbutton" id="updateSubmitBt" href="javascript:void(0)" iconCls="icon-ok">确定</a>';
		var type = $("select[name='operationType'] :selected").val();
		var td1 = $("select[name='vipGrade']").closest("td").next("td");
		var td2 = $("input[name='remark']").parent().next();
		if(type == '0') {
			//系统标记
			$(".manualTag").hide();
			$(".systemTag").show();
			td2.html("&nbsp");
			td1.attr("align", "right").html(str).end().end().attr("disabled", true);
			$.parser.parse(td1);
			calcClientVipGrade();//切换到手工标记时可能修改到了vip等级，这里切换回来时重新从服务器取值
		} else {
			//手工标记
			$(".manualTag").show();
			$(".systemTag").hide();
			td1.empty().end().end().attr("disabled", false);
			td2.attr("align", "right").empty();
			$(str).appendTo(td2);
			$.parser.parse(td2);
		}
	});
	
	$("select[name='operationType']").trigger("change");
	
	function calcClientVipGrade() {
		var clientNo = $("#updateFormClientNo").val();
		if(clientNo != "" && clientNo != null) {
			$.ajax({type: "GET",
				url:"${ctx}/vip/calcClientVipGrade",
				data:{"clientNo" : clientNo},
				cache: false,
				async: false,
				dataType:"json",
				success:function(data){
					if(data.flag == "0"){
						$("select[name='vipGrade']").val(data.vipGrade);
					}
				}
			});
		}
	}
});


</script>
</sf:override>

<sf:override name="content">

<form id="tryCal" action="${ctx}vip/grade/trial" name="trialForm">
<div class="easyui-panel">
<table class="layouttable">	      
     <tr>
	     <td width="15%" class="layouttable_td_label">
	                    试算保费，请输入客户号：
	     </td>
	     <td width="50%" class="layouttable_td_widget">
	          <input type="text" name="clientNo" value="${trialInfo.clientNo}" class="{required:true}" />
	     </td>
	     <td align="right">
          <a class='easyui-linkbutton' onclick="$('#tryCal').submit();return false;"  href="javascript:void(0)" iconCls="icon-ok" id="sub">试算</a>
         </td>
    </tr>
</table>
</div>

<c:if test="${not empty trialInfo}">
<div id="p" class="easyui-panel" title="试算结果">
	<table class="layouttable">	
	  <tr>
	    <td width="15%" class="layouttable_td_label">
	        VIP类型：
	    </td>
	    <td width="90%">
	         ${trialInfo.vipType}
	    </td>
	  </tr>
	   <tr>
	    <td width="10%" class="layouttable_td_label">
	        VIP等级：
	    </td>
	    <td width="90%">
	         ${trialInfo.vipGrade}
	    </td>
	  </tr>
	   <tr>
	    <td width="10%" class="layouttable_td_label">
	       VIP机构：
	    </td>
	    <td width="90%" >
	         ${trialInfo.vipBranch}
	    </td>
	  </tr>
	   <tr>
	    <td width="10%" class="layouttable_td_label">
	        VIP保费：
	    </td>
	    <td width="90%">
	         ${trialInfo.vipPrem}
	    </td>
	  </tr>
	</table>
</div>
</c:if>	
<br />
</form>

<form name="updateForm">
<div id="p" class="easyui-panel" title="客户VIP等级修改">
  <table class="layouttable">
	   <tr>
		     <td width="15%" class="layouttable_td_label">
		                      客户号：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="clientNo" value="${vipInfo.clientNo}" class="{required:true}" id="updateFormClientNo" readonly="readonly" />		   
		          <a href="javascript:void(0)" id="queryClientBt">查询客户</a>
		      </td>
		      <td>&nbsp;
		        
		      </td>
	    </tr>
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                        操作类型：
		      </td>
		      <td width="50%" class="layouttable_td_widget">
				    <select name="operationType" class="operationType">
				        <option value="0" selected>系统逐单标记</option>
				        <option value="1">手工标记</option>
				    </select>
		      </td>
		      <td>&nbsp;
		        
		      </td>
	     </tr>
	     <%-- 以下内容手工标记时显示  机构--%>
	     <tr class="manualTag">
		     <td width="10%" class="layouttable_td_label">
		                   机构：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="branchCode" class="{required:function(){return $('.operationType').val() == '1';}} inputPara"/>
		          <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		      </td>
		      <td >&nbsp;
		       
		      </td>
	    </tr>
	     
	     <%-- 以下内容系统逐单标记时显示--%>
	    <tr class="systemTag">
		     <td width="10%" class="layouttable_td_label">
		         VIP客户保费：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <input type="text" class="easyui-numberbox" precision="2" name="vipPremSum" value="${vipInfo.vipPremSum}" readonly="readonly"/>
		     </td>
		     <td>&nbsp;
		        
		      </td>
	    </tr>
	    
	     <%-- 以下内容:
	     		0、系统逐单标记时(此时只读)
				1、手工标记时(此时可操作)--%>
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		         VIP等级：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <select name="vipGrade">
		           <c:forEach items="${CodeTable.posTableMap.POS_VIP_GRADE}" var="item">
			          <option value="${item.code}" <c:if test="${item.code eq vipInfo.vipGrade}">selected</c:if> >${item.description}</option>
			        </c:forEach>
			        <option value="">取消等级</option>
			      </select>
		     </td>
		     <td>&nbsp;
		        
		      </td>
	    </tr>
	     
	     <%-- 以下内容手工标记时显示 --%>
		<tr class="manualTag">
		  <td width="10%" class="layouttable_td_label">
		               备注：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		           <input type="text" name="remark" value="${vipInfo.remark}" class="{byteRangeLength:[0,2000]}"/>
		     </td>
		     <td>&nbsp;
		     
	         </td>
	    </tr>
	</table>
</div>
</form>

<jsp:include page="/WEB-INF/views/include/clientSelect.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>