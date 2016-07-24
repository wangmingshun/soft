<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">维护设置&gt;&gt;省市和机构的对照关系维护</sf:override>
<sf:override name="head">
<script type="text/javascript">

$(function() {
	$(".branchListCb").live("change",function(){
		if(this.checked==true){
			$("#branchTb tr:eq("+$(".branchListCb").index($(this))+") *").removeAttr("disabled");
		}else{
			$("#branchTb tr:eq("+$(".branchListCb").index($(this))+") :input:not(.branchListCb)").attr("disabled","disabled");
		}
	});
	
	$(".branchChooseBt").live("click",function(){
		if($(this).prev().attr("disabled")==true){return false;};
		showBranchTree($(this).prev());
		return false;
	});
	
	$(".provinceInput").live("change",function(){
		var v = $(this).val();
		var $c = $(this).parent().parent().find(".cityInput");
		$.post("${ctx}include/queryCityByProvince", 
			{province:v},
			function(data) {
				$c.empty();
                $.each(data,function(i,map){
                    $c.append("<option value='"+map["CITY_CODE"]+"'>"+map["CITY_NAME"]+"</option");
				});
		    }, "json");		
	});
	$(".cityInput:enabled").live("click",function(){
		if($(this).children().length<=1){
			$(this).parent().parent().find(".provinceInput").trigger("change");
		}
	});

	$("#queryBt").click(function(){
		$("form").attr("action","${ctx}setting/provbranch/provincequery")
		$("form").submit();
		return false;
	});
	
});

function addRow(){
	var c = "";
	if(($("#branchTb tr").length+1)%2==0){
		c = "even_column";
	}else{
		c = "odd_column";
	}	
	var str = "";
		str += '<tr class="'+c+'">';
		str += '  <td>';
		str += '    <input type="checkbox" class="branchListCb" />';
		str += '    <input type="hidden" class="provBranchPK"/>';
		str += '  </td>';
		str += '  <td>';
		str += '    <select class="provinceInput" disabled>';
		str += '      <c:forEach items="${CodeTable.posTableMap.PROVINCE}" var="item">';
		str += '        <option value="${item.code}">${item.description}</option>';
		str += '      </c:forEach>';
		str += '    </select>';
		str += '  </td>';
		str += '  <td>';
		str += '    <select class="cityInput" disabled>';
		str += '    </select>';
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" class="branchInput" disabled />';
		str += '<a class="branchChooseBt" href="javascript:void(0)">选择机构</a>';
		str += '  </td>';
		str += '</tr>';
	$("#branchTb").append(str);
	return false;
}

function saveIt(){
	var valid = false;
	var jsonStr = "[";
	$.each($(".branchListCb:checked"),function(n,v){
		if(valid)return;
		var code = $("#branchTb tr:eq("+$(".branchListCb").index($(this))+") .provBranchPK").val();
		if(code.length<1){//只处理新增的机构信息
		  var rv = rowValues($(".branchListCb").index($(v)));
		  if(rv=="false"){
			  valid = true;
			  return;
		  }else{
			  jsonStr += rv;
		  }
		}
	});
	jsonStr += "]";
	
	if(valid||jsonStr.length<3)return;//校验未通过或没有值
	
	jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号
	
	$.post("${ctx}setting/provincebranch/save", 
		{addInfo:jsonStr},
		function(data) {
			if(data=="Y"){
				$.messager.alert("提示","数据保存成功，若需查看，请重新查询");
				var flag = "";
				$(".branchListCb:checked").each(function(){
				   var code = $("#branchTb tr:eq("+$(".branchListCb").index($(this))+") .provBranchPK").val();
		           if(code.length<1){//只remove新增的柜面信息
			         $(this).parent().parent().remove();
		           }else{
					   flag = "Y";
				   }
		        });
				if(flag == "Y"){
					$.messager.alert("提示","原已存在的信息请通过”修改“按钮操作");
				}
			}else{
				$.messager.alert("提示","抱歉，出错了，请联系技术人员支持！");
			}
	    }, "text");
	return false;
}

function delRow(){
	var pks = "";//主键
	$(".branchListCb:checked").each(function(){
		pks += $(this).next().val()+";";
	});
	if(pks==""){
		$(".branchListCb:checked").parent().parent().remove();
	}else{
		if(confirm("删除操作不能撤销，是否继续？")){
			$.post("${ctx}setting/provincebranch/delete", 
			{pks:pks},
			function(data) {
				if(data=="Y"){
					$.messager.alert("提示","已删除");
					$(".branchListCb:checked").parent().parent().remove();
				}
		    }, "text");
		}
	}
	return false;
}

function update(){
	var valid = false;
	var jsonStr = "[";
	$.each($(".branchListCb:checked"),function(n,v){
		if(valid)return;
		var code = $("#branchTb tr:eq("+$(".branchListCb").index($(this))+") .provBranchPK").val();
		if(code.length>0){//只处理已存在的机构信息
		  var rv = branchValue($(".branchListCb").index($(v)));
		  if(rv=="false"){
			  valid = true;
			  return;
		  }else{
			  jsonStr += rv;
		  }
		}
	});
	jsonStr += "]";
	if(valid||jsonStr.length<3)return;//校验不通过或没有值
	jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号

	$.post("${ctx}setting/provincebranch/update",
		{updateInfo:jsonStr},
		function(data) {
			if(data=="Y"){
				$.messager.alert("提示","机构信息修改成功");
			}
	    }, "text");
	return false;
}

//未实现
function getExl(){
	$("form").attr("action","${ctx}setting/provincebranch/excel")
	$("form").submit();
}

//得到第i行的值
function rowValues(i){
	var tmp = "";
	var str = "{";
	 str += '"provinceCode":"'+$.trim($("#branchTb tr:eq("+i+") .provinceInput").val())+'",';
	 str += '"cityCode":"'+$.trim($("#branchTb tr:eq("+i+") .cityInput").val())+'",';
	 
	 tmp = $.trim($("#branchTb tr:eq("+i+") .branchInput").val());
	 if(tmp==""){$.messager.alert("提示","请录入机构码");return "false";}
	 str += '"branchCode":"'+tmp+'",';
	 
	 str += '},';
	return str;	
}

//更新时，得到第i行的值
function branchValue(i){
	var tmp = $.trim($("#branchTb tr:eq("+i+") .branchInput").val());
	if(tmp==""){$.messager.alert("提示","请录入机构码");return "false";}
	
	var str = "{";
	 str += '"provBranchPK":"'+$.trim($("#branchTb tr:eq("+i+") .provBranchPK").val())+'",';
	 str += '"branchCode":"'+tmp+'",';
	 str += '},';
	return str;	
}

</script>
</sf:override>

<sf:override name="content">

<form>

 <div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">选择省份：</td>
      <td class="layouttable_td_widget">
        <select name="queryProvince">
          <c:forEach items="${CodeTable.posTableMap.PROVINCE}" var="item">
            <option value="${item.code}" <c:if test="${item.code eq queryProvince}">selected</c:if> >${item.description}</option>
          </c:forEach>
        </select>
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt" name="queryBt">查询</a>
      </td>
    </tr>
  </table>
 </div> 
<br />

<table class="infoDisTab">
  <thead>
    <th width="5%">选择</th>
    <th>机构所属省</th>
    <th width="30%">机构所属市</th>
    <th>机构代码</th>
  </thead>
  <tbody id="branchTb">
  
  <c:forEach items="${branchList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>
        <input type="checkbox" class="branchListCb" />
        <input type="hidden" class="provBranchPK" value="${item.BRANCH_IN_PROV_PK}"/>
      </td>
      <td>
        ${item.PROVINCE_NAME}
      </td>
      <td>
        ${item.CITY_NAME}
      </td>
      <td>
        <input type="text" class="branchInput" value="${item.BRANCH_CODE}" disabled /><a class="branchChooseBt" href="javascript:void(0)">选择机构</a>
      </td>
    </tr>
  </c:forEach>
  
  </tbody>
  <tr>
    <td colspan="4" align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" onclick="addRow();return false;">增加</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-save" onclick="saveIt();return false;">保存</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" onclick="delRow();return false;">删除</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-edit" onclick="update();return false;">修改</a>
    </td>
  </tr>
</table>

</form>
<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>