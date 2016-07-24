<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">节假日设置</sf:override>
<script type="text/javascript">
$(function(){
	$("#yearQueryBt").click(function(){
		$(".vacationDate").removeClass("{required:true}");
		if($("[name=year]").val().length!=4){
			$.messager.alert("提示","请正确输入年份！");
			return false;
		}
		$("form[name=queryForm]").attr("action","${ctx}setting/holiday/query");
		$("form[name=queryForm]").submit();
		return false;
	});
	
	$("#saveBt").click(function(){
		getValues();
		$("form[name=queryForm]").attr("action","${ctx}setting/holiday/save");
		$("form[name=queryForm]").submit();
		return false;
	});

	$("[name=year]").click(function(){
		WdatePicker({skin:'whyGreen',dateFmt:'yyyy'});
	});
	$(".vacationDate").live("click",function(){
		WdatePicker({skin:'whyGreen'});
	});
	
	$("#ckAllCb").change(function(){
		$(".dateCb").attr("checked",this.checked);
	});
	
	$("form[name=queryForm]").validate();
	
});

function addRow(){
	var c = "";
	if(($("#dateTb tr").length+1)%2==0){
		c = "even_column";
	}else{
		c = "odd_column";
	}
   var str = '';
	str += '<tr class="'+c+'">';
	str += '  <td align="center">';
	str += '    <input type="checkbox" class="dateCb" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" name="'+Math.random()+'" class="Wdate vacationDate {required:true}" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <select name="${status.index}" class="dateType">';
	str += '    	<option value="1" selected>节假日</option>';
	str += '    	<option value="2">工作日</option>';
	str += '    <select/">';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" class="vacationDesc" maxlength="25" />';
	str += '  </td>';
	str += '</tr>';	   
  $("#dateTb").append(str);
  return false;
}
//删除一行信息
function delRow(){
	if($(".dateCb:checked").length>0){
		$.messager.alert("提醒","该操作仅从页面上删除行，实际已设置的数据不能被删除！");
	}
	$(".dateCb:checked").each(function(){
		$(this).closest("tr").remove();
	});
	return false;
}

function getValues(){
	var str = "[";
	$("#dateTb tr").each(function(){
		str += '{"vacationDate":"';
		str += $(this).find(".vacationDate").val();
		str += '",';
		str += '"dateType":"';
		str += $(this).find(".dateType").val();
		str += '",';
		str += '"vacationDesc":"';
		str += $(this).find(".vacationDesc").val();
		str += '"},';
	});
	str += ']';
	str = str.replace("},]","}]");//去掉后面一个逗号
	$("#setResult").val(str);
}

</script>
</sf:override>
<sf:override name="content">

<form name="queryForm" method="post">

 <div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">请选择年份：</td>
      <td class="layouttable_td_widget">
        <input type="text" name="year" id="year" class="Wdate" />
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="yearQueryBt">查询</a>
      </td>
    </tr>
  </table>
 </div> 
<br />

<div class="easyui-panel" title="已设置特殊的节假日">
<table class="infoDisTab">
  <tr>
    <th width="8%">选择<input type="checkbox" id="ckAllCb" />全选</th>
    <th>日期</th>
    <th>节假日/工作日</th>
    <th>备注</th>
  </tr>
  <tbody id="dateTb">
  <c:forEach items="${vacationList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td align="center">
        <input type="checkbox" class="dateCb" />
      </td>
      <td>
        <input type="text" value="${item.VACATION_DATE}" name="${status.index}" class="Wdate vacationDate {required:true}" />
      </td>
      <td>
        <select name="${status.index}" class="dateType">
	        <c:if test="${item.DATE_TYPE eq 1}">
		        <option value="1" selected>节假日</option>
		        <option value="2">工作日</option>
		    </c:if>
		    <c:if test="${item.DATE_TYPE eq 2}">
		        <option value="1">节假日</option>
		        <option value="2" selected>工作日</option>
		    </c:if>
	    </select>
      </td>
      <td>
        <input type="text" value="${item.VACATION_DESCRIPTION}" class="vacationDesc {byteRangeLength:[0,50]}"/>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
  <div align="right">
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" onclick="addRow();return false;">增加</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" onclick="delRow();return false;">删除</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-save" id="saveBt">保存</a>
  </div>
</div>

<input type="hidden" name="setResult" id="setResult" />
</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>