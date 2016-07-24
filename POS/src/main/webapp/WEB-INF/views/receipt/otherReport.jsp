<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保单回执查询清单&gt;&gt;其他追踪报表</sf:override>
<script type="text/javascript">
$(function(){
	$("form").validate();
	
	$("[name=startDate],[name=endDate]").focus(function(){
	    WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	  
	$("#branchChooseBt").click(function(){		
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	});
	
	$("#listType").change(function(){
		$("span.sp").hide();
		$("span.sp"+$(this).val()).show();
		$("#channel").trigger("change");
		if($(this).val()=="3"){
			var t = new Date();
			
			
			var m = t.getMonth()+1;
			if(m<10){m = "0"+m;}
			var d = t.getDate();
			if(d<10){d = "0"+d;}
			
			var date = "${CodeTable.posTableMap.SYSDATE}";
			var y = parseInt(date.substr(0,4));
			var m = parseInt(date.substr(5,2),10);
			var d=  parseInt(date.substr(8,2),10);
			
			var nows = y+"-"+m+"-"+d;
			$("[name=startDate],[name=endDate]").val(nows);
			$("#dateTitleSp").text("回销录入");
		}else{
			$("[name=startDate]").val("");
			$("#dateTitleSp").text("承保");
		}
	});
	$("#channel").change(function(){
		$(this).next().empty();
		var $op = $("option.op"+$(this).parent().prev().val()+$(this).val()).clone();//不复制的话他还把原位置的元素搞不见了
		$(this).next().append($op);
		$(".subSp01").hide();
		$(".subSp"+$(this).val()).show();
	});
	
	$(".inputPara").click(function(){
		$("#submitBt").attr("disabled",false);
	});
	$("#submitBt").click(function(){
		$(this).attr("disabled",true);
		$("form").submit();
		return false;
	});
	
});

</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}receipt/list/otherReport/submit" class="noBlockUI">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">清单类型：</td>
    <td class="layouttable_td_widget">
      <select name="listType" id="listType" class="inputPara">
        <option value="1">已回销回执清单</option>
        <option value="2">未回销回执清单</option>
        <option value="3">回执录入清单</option>
      </select>
      <span class="sp sp1 sp2">
        <select name="channel" id="channel" class="inputPara">
          <option value="">全部</option>
          <option value="00">银保通</option>
          <option value="01">个险</option>
          <option value="02">一般银行险</option>
          <option value="03">经代</option>
          <option value="07">收展</option>
        </select>
        <%--
        <select name="channel" class="inputPara">
        <option value="">全部</option>
        <c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}" var="item">
          <option value="${item.code}">${item.description}</option>
        </c:forEach>
      	</select>
         --%>
        <select name="timeConsume" style="display:none" class="subSp01 subSp02 subSp03 subSp00 subSp07">
        </select>
      </span>
      <span class="sp sp3" style="display:none">操作员：<input type="text" name="confirmUser" class="inputPara" /></span>
      <div style="display:none" title="option写在select里不能切换隐藏">
	      <select>
	       <option value="" class="op100 op101 op102 op103 op107 op200 op201 op202 op203 op207">全部</option>
	       <option value="10" class="op100 op107">超10日回销清单</option>
	       <option value="20" class="op101 op102">超20日回销清单</option>
	       <option value="30" class="op103">超30日回销清单</option>
	       <option value="10" class="op200 op207">超10日未回销清单</option>
	       <option value="60" class="op200 op201 op203 op207">超60日未回销清单</option>
	       <option value="15" class="op201 op202">超15日未回销清单</option>
	       <option value="20" class="op201 op202">超20日未回销清单</option>
	       <option value="90" class="op202">超90日未回销清单</option>
	       <option value="30" class="op203">超30日未回销清单</option>
	      </select>
      </div>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">保单机构：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="branchCode" class="{required:true} inputPara" />
      <a id="branchChooseBt" href="javascript:void(0)">选择机构</a>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label"><span id="dateTitleSp">承保</span>日期：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}} inputPara" title="已回销和未回销清单，按保单承保日期算，录入清单按录入日期算" />
      至
      <input type="text" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}} inputPara" />
    </td>
  </tr>
</table>
</div>
<div align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="submitBt">查询</a>
</div>

</form>

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>