<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">维护设置&gt;&gt;银行质押贷款协议信息维护</sf:override>
<sf:override name="head">

<script type="text/javascript">
var prodRes = eval('${products}');//可选险种源json

var $prodCodeInput;//险种选择后的显示域
$(function() {

	$(".bankListCb").live("change",function(){
		if(this.checked==true){
			$("#bankTb tr:eq("+$(".bankListCb").index($(this))+") *").removeAttr("disabled");
		}else{
			$("#bankTb tr:eq("+$(".bankListCb").index($(this))+") :input:not(.bankListCb)").attr("disabled","disabled");
		}
	});
	
	$(".bankChooseBt").live("click",function(){
		if($(this).prev().attr("disabled")==true){return false;};
		var $bankCode = $(this).prev();
		showBankSelectWindow($bankCode, function(code, label){
			$bankCode.parent().next().text(label);
		});
		return false;
	});
	
	$(".branchChooseBt").live("click",function(){
		if($(this).prev().attr("disabled")==true){return false;};
		showBranchTree($(this).prev());
		return false;
	});
	
	$("#queryBt").click(function(){
		if($.trim($("#queryBranch").val())==""){
			$.messager.alert("提示","请先输入机构");
			return false;
		}
		$("form").attr("action","${ctx}setting/mortgageprotocol/query")
		$("form").submit();
		return false;
	});
	
	$(".startDate, .endDate").live("click",function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#productInput").autocomplete({
			source:$.map(prodRes,function(it){
                return it.FULL_NAME;
			}),
			select:function(e,ui){
			var v = ui.item.value;
			v = v.substring(v.lastIndexOf("(")+1,v.lastIndexOf(")"));
			$(this).next().val(v);
		}
	});
	$("#productInputAddBt").click(function(){
		$("#productsInputed").append("<option value='"+$(this).prev().val()+"'>"+$(this).prev().prev().val()+"</option>");
		$(this).prev().val("");
		$(this).prev().prev().val("");
		return false;
	});
	$("#removeInputedBt").click(function(){
		$("#productsInputed option:selected").remove();
		return false;
	});
	$(".productWinBt").live("click",function(){
		if($(this).prev().attr("disabled")==true){return false;};
		$prodCodeInput = $(this).prev();
		$("#pruductWindow").window("open");
		return false;
	});
	
	$(".branchCode").live("change",function(){
		var $the = $(this);
		$.post("${ctx}setting/mortgageprotocol/branchbank", 
		{branchNo:$the.val()},
		function(data){
			if(data.length<1){
				$.messager.alert("提示","该机构下没有相应的银行可以选择，请选择别的机构");
				return;
			}
			$the.parent().parent().find(".bankInput").autocomplete({
				source:$.map(data,function(it){
					return it.BANK_NAME;
				}),
				select:function(e,ui){
				var v = ui.item.value;
				v = v.substring(v.lastIndexOf("（")+1,v.lastIndexOf("）"));
				$(this).next().val(v);
			}
			});
		}, "json");
	});

});

function getProducts(){
	$prodCodeInput.val("");
	var v = "";
	$.each($("#productsInputed option"),function(i,n){
		v += $(this).val();
		v += ",";
	});
	$prodCodeInput.val(v);
	$("#pruductWindow").window("close");
	return false;
}

function addRow(){
	var c = "";
	if(($("#bankTb tr").length+1)%2==0){
		c = "even_column";
	}else{
		c = "odd_column";
	}	
	var str = "";
	str += '<tr class="'+c+'">';
	str += '  <td>';
	str += '    <input type="checkbox" class="bankListCb" />';
	str += '    <input type="hidden" class="protocolCode" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" size="10" disabled="disabled" class="branchCode" />';
	str += '<a class="branchChooseBt" href="javascript:void(0)">选择机构</a>';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" size="25" class="bankInput" disabled="disabled" />';
	str += '    <input type="hidden" class="bankCode" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" size="10" class="Wdate startDate" disabled="disabled" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" size="10" class="Wdate endDate" disabled="disabled" />';
	str += '  </td>';
	str += '  <td>';
	str += '    <input type="text" readonly class="productCode" disabled="disabled" />';
	str += '<a class="productWinBt" href="javascript:void(0)">录入险种</a>';
	str += '  </td>';
	str += '</tr>';	
	$("#bankTb").append(str);
	return false;
}

function saveIt(){
	var ex = false;
	var jsonStr = "[";
	$.each($(".bankListCb:checked"),function(n,v){
		if(ex)return;
		var code = $("#bankTb tr:eq("+$(".bankListCb").index($(this))+") .protocolCode").val();
		if(code.length<1){//只处理新增的柜面信息
		  var rv = rowValues($(".bankListCb").index($(v)));
		  if(rv=="false"){
			  ex = true;
			  return;
		  }else{
			  jsonStr += rv;
		  }
		}
	});
	jsonStr += "]";
	
	if(ex||jsonStr.length<3)return;//校验未通过或没有值
	
	jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号
	
	$.post("${ctx}setting/mortgageprotocol/save", 
		{addInfo:jsonStr},
		function(data) {
			if(data=="Y"){
				$.messager.alert("提示","数据保存成功，若需查看，请重新查询");
				var flag = "";
				$(".bankListCb:checked").each(function(){
				   var code = $("#bankTb tr:eq("+$(".bankListCb").index($(this))+") .protocolCode").val();
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
	var protocolCodes = "";//主键
	$(".bankListCb:checked").each(function(){
		protocolCodes += $(this).next().val()+";";
	});
	if(protocolCodes.replace(/;/g,"")==""){
		$(".bankListCb:checked").parent().parent().remove();
	}else{
		if(confirm("删除操作不能撤销，是否继续？")){
			$.post("${ctx}setting/mortgageprotocol/delete", 
			{protocolCodes:protocolCodes},
			function(data) {
				if(data=="Y"){
					$.messager.alert("提示","已删除");
					$(".bankListCb:checked").parent().parent().remove();
				}
		    }, "text");
		}
	}
	return false;
}

function update(){
	var jsonStr = "[";
	$.each($(".bankListCb:checked"),function(n,v){
		var code = $("#bankTb tr:eq("+$(".bankListCb").index($(this))+") .protocolCode").val();
		if(code.length>0){//只处理已存在的柜面信息
		  var rv = endDateValue($(".bankListCb").index($(v)));
		  jsonStr += rv;
		}
	});
	jsonStr += "]";
	if(jsonStr.length<3)return;//没有值
	jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号

	$.post("${ctx}setting/mortgageprotocol/update",
		{updateInfo:jsonStr},
		function(data) {
			if(data=="Y"){
				$.messager.alert("提示","协议截止日期修改成功");
			}
	    }, "text");
	return false;
}

function getExl(){
	if($.trim($("#queryBranch").val())==""){
		$.messager.alert("提示","请先输入机构机构!");
		return;
	}
	$("form").attr("action","${ctx}setting/mortgageprotocol/excel")
	$("form").submit();
	return false;
}

//得到第i行的值
function rowValues(i){
	var tmp = "";
	var str = "{";
	 str += '"protocolCode":"'+$.trim($("#bankTb tr:eq("+i+") .protocolCode").val())+'",';
	 tmp = $.trim($("#bankTb tr:eq("+i+") .branchCode").val());
	 if(tmp==""){$.messager.alert("提示","请录入机构码");return "false";}
	 str += '"branchCode":"'+tmp+'",';
	 
	 tmp = $.trim($("#bankTb tr:eq("+i+") .bankCode").val());
	 if(tmp==""){$.messager.alert("提示","银行信息录入有误，请核实");return "false";}
	 str += '"bankCode":"'+tmp+'",';
	 
	 tmp = $.trim($("#bankTb tr:eq("+i+") .startDate").val());
	 if(tmp==""){$.messager.alert("提示","请录入协议签订日期");return "false";}
	 str += '"startDate":"'+tmp+'",';
	 
	 str += '"endDate":"'+$.trim($("#bankTb tr:eq("+i+") .endDate").val())+'",';
	 
	 tmp = $.trim($("#bankTb tr:eq("+i+") .productCode").val());
	 if(tmp==""){$.messager.alert("提示","请录入险种代码");return "false";}
	 str += '"productCode":"'+tmp+'"';
	 
	 str += '},';
	return str;	
}

//更新时，得到第i行的值
function endDateValue(i){
	var str = "{";
	 str += '"protocolCode":"'+$.trim($("#bankTb tr:eq("+i+") .protocolCode").val())+'",';
	 str += '"endDate":"'+$.trim($("#bankTb tr:eq("+i+") .endDate").val())+'",';
	 str += '},';
	return str;	
}

</script>
</sf:override>

<sf:override name="content">

<form>

<div class="easyui-panel" >
<table class="layouttable">
  <tr>
    <td width="15%" class="layouttable_td_label">协议银行所属机构：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="queryBranch" name="queryBranch" value="${queryBranch}" />
      <a class="branchChooseBt" href="javascript:void(0)">选择机构</a>
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt" name="queryBt">查询</a>
    </td>
  </tr>
</table>
</div>

<br />

<table class="infoDisTab" >
  <tr>
    <th width="5%">选择</th>
    <th>协议机构</th>
    <th width="25%">协议银行名称（编号）</th>
    <th>协议签订日期</th>
    <th>协议截止日期</th>
    <th width="25%">协议险种代码</th>
  </tr>
  <tbody id="bankTb">
  
  <c:forEach items="${protocolList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>
        <input type="checkbox" class="bankListCb" />
        <input type="hidden" class="protocolCode" value="${item.PROTOCOL_CODE}" />
      </td>
      <td>
        <input type="text" size="10" disabled="disabled" value="${item.BRANCH_CODE}" class="branchCode" />
      </td>
      <td>${item.DEPT_FULL_NAME}（${item.DEPT_NO}）</td>
      <td>
        <input type="text" size="10" class="startDate" disabled="disabled" value="${item.START_DATE}" />
      </td>
      <td>
        <input type="text" size="10" class="Wdate endDate" disabled="disabled" value="${item.END_DATE}" />
      </td>
      <td>
        <input type="text" disabled="disabled" class="productCode" value="${item.PRODUCT_CODE}" />
      </td>
    </tr>
  </c:forEach>
  </tbody>
  <tr>
    <td colspan="6" align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" onclick="addRow();return false;">增加</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-save" onclick="saveIt();return false;">保存</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" onclick="delRow();return false;">删除</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-edit" onclick="update();return false;">修改</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-print" onclick="getExl();return false;">导出</a>    
    </td>
  </tr>
</table>

<div id="pruductWindow" class="easyui-window" closed="true" modal="true" title="险种录入" style="width:500px;height:350px;" collapsible="false" minimizable="false" maximizable="false">
	<div class="easyui-layout" fit="true">
		<div align="left" region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
        <table class="layouttable">
          <tr>
            <td>输入险种名称：</td>
            <td>
              <input type="text" id="productInput" size="50" /><input type="hidden" /><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="productInputAddBt">增加</a>
            </td>
          </tr>
          <tr>
            <td valign="top">已录入的险种：</td>
            <td>
                <select multiple="multiple" id="productsInputed" style="width:280px; height:200px;" >
                </select>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
               <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="removeInputedBt" >移除选中的险种</a>
            </td>
          </tr>
        </table>
		</div>
		<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
			<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" onclick="getProducts();return false;">确定</a>
		</div>
	</div>
</div>

</form>

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>