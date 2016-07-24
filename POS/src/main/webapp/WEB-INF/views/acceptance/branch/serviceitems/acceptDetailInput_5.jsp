<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 新增附加险 页面很复杂--%>

<sf:override name="headBlock">

<script type="text/javascript">
var prodRes = eval('${acceptance__item_input.productOption}');//可选险种源json
var isNeedControl = "N";//判断主险是否是需要控制第一被保险人的险种
$(function(){
	$(".productCodeHad").each(function(){
			if($(this).val()=="CPDD_BN1"){
				isNeedControl = "Y";
			}
	});
	//增加行
	$("#addProductBt").click(function(){
		var c = "";
		if(($("#addProductTb tr").length+1)%2==0){
			c = "even_column";
		}else{
			c = "odd_column";
		}		
		var str = "";
		str += '<tr class="'+c+'">';
		str += '  <td>';
		str += '    <input type="checkbox" class="addProductCb" />';
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" class="productInput" size="45" />';
		str += '    <input type="hidden" name="'+Math.random()+'" class="productCodeInput {required:true}" />';
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" name="'+Math.random()+'" class="annStandardPremInput easyui-numberbox {required:true}" precision="2" size="10" />';
		str += '  </td>';
		str += '  <td>';
		if(isNeedControl =="Y"){
			str += '   <select class="insuredNoInput"> ';
			str += '		<c:forEach items="${acceptance__item_input.clientNoInsList}" var="item">';
			str += '			<c:if test="${item.insuredSeq eq 1}"><option value="${item.insuredNo }" >${item.insuredName}</option></c:if>';
			str += '		</c:forEach>';
			str += '		</select>';
		}else{
			str += '   <select class="insuredNoInput"> ';
			str += '		<c:forEach items="${acceptance__item_input.clientNoInsList}" var="item">';
			str += '			<option value="${item.insuredNo }" >${item.insuredName}</option>';
			str += '		</c:forEach>';
			str += '		</select>';
		}
		str += '    <input type="hidden" name="'+Math.random()+'" class="isAddAppInput {required:true}" value="N"/>';
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" name="'+Math.random()+'" class="baseSumInsInput easyui-numberbox {required:true}" precision="2" size="10" />';
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" name="'+Math.random()+'" class="premTermInput easyui-numberbox {required:true}" size="6" />';
		str += '<select class="premTermInput" style="display:none">';
		str += '    </select>';
		str += '    <select class="premPeriodTypeInput">';
		str += '      <option value="2">年</option>';
		str += '      <option value="3">岁</option>';
		str += '      <option value="1">趸缴</option>';		
		str += '    </select>';		
		str += '  </td>';
		str += '  <td>';
		str += '    <input type="text" name="'+Math.random()+'" class="coveragePeriodInput easyui-numberbox {required:true}" size="6" />';
		str += '<select class="coveragePeriodInput" style="display:none">';
		str += '    </select>';
		str += '    <select class="coverPeriodTypeInput">';
		str += '      <option value="1">年</option>';
		str += '      <option value="2">岁</option>';
		str += '      <option value="6">终身</option>';
		str += '    </select>';
		str += '  </td>';
		str += '  <td>';
		str += '    <select class="dividendInput">';
		str += '    </select>';
		str += '  </td>';
		str += '  <td>';
		str += '    <select class="autoContinueInput">';
		str += '    </select>';
		str += '  </td>';
		str += '</tr>';
		var $s = $(str);
		$.parser.parse($s);//重新渲染，以实现easyui-numberbox效果
		$("#addProductTb").append($s);
		return false;
	});
	//删除行
	$("#removeProductBt").click(function(){
		$(".addProductCb:checked").each(function(){
			$(this).parent().parent().remove();
		});	
		return false;
	});
	
	$(".productInput").live("focus",function(){
		var $the = $(this);
		var $tr = $(this).parent().parent();
		//没找到怎么动态autocomplete的方法，变通实现
		$(".productInput").autocomplete({
			source:$.map(prodRes,function(it){
                return it.FULL_NAME;
			}),
			select:function(e,ui){
				var v = ui.item.value;
				v = v.substring(v.lastIndexOf("(")+1,v.lastIndexOf(")"));
				$the.next().val(v);
				var appInsTheSame = "${acceptance__item_input.appInsTheSame}";
				var isAddApp = $tr.find(".isAddAppInput").val();
				//第一被保人与投保人不一致
				if(appInsTheSame == "false"){
					$.post("${ctx}acceptance/branch/isExemptPlan", 
							{productCode:v},
							function(data) {
								if(data){
									//增加的附加险是豁免险，
									if(data.isExemptPlan=="Y"&&isAddApp=="N"){
										$tr.find(".insuredNoInput").append('<option value="${acceptance__item_input.clientInfoAppList[0].clientNo}">${acceptance__item_input.clientInfoAppList[0].clientName}（投保人）</option>');//增加投保人
										$tr.find(".isAddAppInput").val("Y");
									}
									if(data.isExemptPlan=="N"&&isAddApp=="Y"){
										$tr.find(".insuredNoInput option[value='${acceptance__item_input.clientInfoAppList[0].clientNo}']").remove();
										$tr.find(".isAddAppInput").val("N");
									}
								}
							}, "json");
				}
				longProduct($tr,getFlag(v,"L"));
				dividend($tr,getFlag(v,"D"));
				premSumIns($tr,getFlag(v,"T"));
			}
		});
	});
	//缴费年期 or 保险年期
	$(".premPeriodTypeInput, .coverPeriodTypeInput").live("change",function(){
		var $the = $(this);//防止this变成别的对象
		
		var code = $the.parent().parent().find(".productCodeInput").val();
		if(code==""){return;}
		
		hideIt($the.prev());
		hideIt($the.prev().prev());
		
		//附加安心豁免重疾,特别处理
		if(code=="CIDD_MN0"){
			if($the.hasClass("premPeriodTypeInput")){
				var term = parseInt($("#leftPremTerm").val())-3;
				if(term<2){
					$the.parent().parent().find(".productCodeInput").val("");
					$.messager.alert("提示","主险剩余缴费年期不足5年，不能新增安心豁免");
					return;
				}
				$the.val("2");
				$the.prev().prev().val(term).show().attr("disabled",false).attr("readonly",true);
				
			}else if($the.hasClass("coverPeriodTypeInput")){
				$the.val("1");
				$the.prev().prev().val(parseInt($("#leftPremTerm").val())).show().attr("disabled",false).attr("readonly",true);
			}
			return;
		}
		
		var type = "";
		if($the.hasClass("premPeriodTypeInput")){
			type = "P";
			if($the.hasClass("ReadOnly")){
				$the.val("2");
				$the.prev().prev().val("1").show().attr("disabled",false).attr("readonly",true);//短险缴费期一年
				return;
			}
			if($the.val()=="1"){//趸缴
				$the.prev().prev().val("0").show().attr("disabled",false).attr("readonly",true);
				return;
			}
		}else if($the.hasClass("coverPeriodTypeInput")){
			type = "C";
			if($the.hasClass("ReadOnly")){
				$the.val("1");
				$the.prev().prev().val("1").show().attr("disabled",false).attr("readonly",true);//短险保障期一年
				return;
			}
			if($the.val()=="6"){//终身
				$the.prev().prev().val("999").show().attr("disabled",false).attr("readonly",true);
				return;
			}
		}
		if(getFlag(code,type)=="0"){//与费率因子无关
			showIt($the.prev().prev());
			return;//就不查询可选值了
		}
		
		$.post("${ctx}acceptance/branch/queryProductPremRate", 
		{productCode:code,
		 type:type,
		 typeValue:$the.val()},
		function(data) {
			if(data.length==0){
				showIt($the.prev().prev());
			}else{
				showIt($the.prev());
				$the.prev().empty();
				$.each(data,function(i,value){
					$the.prev().append('<option value="'+value+'">'+value+'</option>')
				});
			}
		}, "json");
	});
		
	//新增附加险的红利选择方式保持一致
	$(".dividendInput").live("change",function(){
		$(".dividendInput").val($(this).val());
	});

});

//根据险种代码找到标志
function getFlag(code,type){
	var r = "";
	$.each(prodRes,function(i,map){
		if(map.PRODUCT_CODE==code){
			if("D"==type){//红利
				r = map.DIVIDEND_FLAG;				
			}else if("L"==type){//长险
				r = map.LONG_FLAG;
			}else if("T"==type){//缴费类型
				r = map.CAL_TYPE;
			}else if("P"==type){//缴费年期
				r = map.PREM_PERIOD;
			}else if("C"==type){//保险年期
				r = map.COVER_PERIOD;
			}
			return false;//break的意思
		}
	});
	return r;
}

//长短险的控制
function longProduct($tr,flag){
	$tr.find(".autoContinueInput").empty();
	if(flag=="N"){
		$tr.find(".autoContinueInput").append('<option value="Y">是</option><option value="N">否</option>');//自动续保
		$tr.find(".premPeriodTypeInput").addClass("ReadOnly").trigger("change");//缴费年期类型
		$tr.find(".coverPeriodTypeInput").addClass("ReadOnly").trigger("change");//保险年期类型
	}else{
		$tr.find(".premPeriodTypeInput").removeClass("ReadOnly").trigger("change");//下拉框没有只读属性，disabled掉又不能绑定参数，加个我定义的ReadOnly
		$tr.find(".coverPeriodTypeInput").removeClass("ReadOnly").trigger("change");
	}
}
//红利选择方式域的控制
function dividend($tr,flag){
	$tr.find(".dividendInput").empty();
	//附加险是分红险且主险不是分红险时
	if(flag=="Y" && "${acceptance__item_input.primaryDivFlag}"=="N"){
		var str = "";
		str += '      <c:forEach items="${CodeTable.posTableMap.DIVIDEND_SELECTION}" var="item">';
		str += '        <option value="${item.code}">${item.description}</option>';
		str += '      </c:forEach>';
		$tr.find(".dividendInput").append(str);
		$tr.find(".dividendInput").trigger("change");//保持一致
	}
}
//根据险种是保费算保额还是保额算保费决定屏蔽输入项
function premSumIns($tr,flag){
	hideIt($tr.find(".annStandardPremInput"));
	hideIt($tr.find(".baseSumInsInput"));
	
	if(flag=="1"){//保额算保费
		showIt($tr.find(".baseSumInsInput"));
		
	}else if(flag=="2"){//保费算保额
		showIt($tr.find(".annStandardPremInput"));
		
	}else{//0-保额保费相互独立,3-处于未知状态，防止卡死用户的操作，二者还是都放开录入的好
		showIt($tr.find(".annStandardPremInput"));
		showIt($tr.find(".baseSumInsInput"));
		$tr.find(".annStandardPremInput").removeClass("{required:true}");//两个都他妈的去掉校验吧，豁免险可以啥都不录
		$tr.find(".baseSumInsInput").removeClass("{required:true}");//金管家保额保费相互独立，但仍只录保费
	}
}

//提交前的验证和操作
posValidateHandler=function(){
	if(!productCheck()){
		return false;
	}
	if(!premTermCheck()){
		return false;
	}
	addName();
	
	return true;
};

//险种录入检查，防止用户瞎录不存在的险种
function productCheck(){
	var had = "kui-";
	$(".productCodeHad").each(function(){
		had += $(this).val();
	});
	
	var b = true;
	$(".productCodeInput").each(function(){
		var code = $(this).val();
		var name = $(this).prev().val();
		if(name.indexOf(code)<1){
			$.messager.alert("提示","请核实录入的险种，或从险种列表重新选择");
			b = false;
			return false;
		}

	//if((code=="UIAN_AN0" || code=="UIAN_BN0") && (had.indexOf("UIAN_AN0")>0 || had.indexOf("UIAN_BN0")>0)){
	//		$.messager.alert("提示","主险已有金管家险种，不能再新增");
	//		b = false;
	//		return false;
	//	}

	});
	return b;	
}

//短险缴费年期需小于主险的剩余缴费年期（长险的规则引擎有控制，短险本也该其控制的）
function premTermCheck(){
	var b = true;
	$(".premPeriodTypeInput").each(function(){
		if($(this).hasClass("ReadOnly") && b){
			if(parseInt($(this).prev().prev().val())>parseInt($("#leftPremTerm").val())){
				$.messager.alert("提示","新增附加险缴费年期不能大于主险剩余缴费年期"+$("#leftPremTerm").val()+"年");
				b = false;
				return false;
			}
		}
	});
	return b;
}

//name赋值
function addName(){
	var idx = $("#productHadListTb tr").length;
	$("#addProductTb tr").each(function(){
		$(this).find(".productCodeInput").attr("name","productList["+idx+"].productCode");
		$(this).find(".annStandardPremInput").attr("name","productList["+idx+"].annStandardPrem");
		$(this).find(".insuredNoInput").attr("name","productList["+idx+"].insuredNo");
		$(this).find(".baseSumInsInput").attr("name","productList["+idx+"].baseSumIns");
		$(this).find(".premTermInput:visible").attr("name","productList["+idx+"].premTerm");
		$(this).find(".premPeriodTypeInput").attr("name","productList["+idx+"].premPeriodType");
		$(this).find(".coveragePeriodInput:visible").attr("name","productList["+idx+"].coveragePeriod");
		$(this).find(".coverPeriodTypeInput").attr("name","productList["+idx+"].coverPeriodType");
		$(this).find(".dividendInput").attr("name","productList["+idx+"].dividend");
		$(this).find(".autoContinueInput").attr("name","productList["+idx+"].autoContinue");
		idx++;
	});
	$("#productSize").val(idx-$("#productHadListTb tr").length);
}

//隐藏并disabled对象，主要是为了disabled后可以躲过校验
function hideIt($obj){
	$obj.hide();
	$obj.attr("disabled",true);
	$obj.val("");
}
//显示并UNdisabled对象
function showIt($obj){
	$obj.show();
	$obj.attr("disabled",false);
	$obj.removeAttr("readonly");
}

</script>

</sf:override>

<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">

<br />
保单${acceptance__item_input.policyNo}险种明细：&nbsp;&nbsp;币种：人民币
<input type="hidden" value="${acceptance__item_input.leftPremTerm}" id="leftPremTerm" />
<input type="hidden" value="${acceptance__item_input.leftCoveragePeriod}" id="leftCoveragePeriod" />
<input type="hidden" value="${acceptance__item_input.primaryDivFlag}" id="primaryDivFlag" />
<table class="infoDisTab">
  <tr>
    <th>序号</th>
    <th width="25%">险种（代码）</th>
    <th>险种状态</th>
    <th>是否主险</th>
    <th>被保人</th>
    <th>份数</th>
    <th>保额</th>
    <th>生效日期</th>
    <th>交至日期</th>
    <th>缴费年期</th>
    <th>期交保费</th>
    <th>缴费频次</th>
  </tr>
  <tbody id="productHadListTb">
  <c:forEach items="${acceptance__item_input.productList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>${item.prodSeq}</td>
      <td>${item.productFullName}（${item.productCode}）
      	<input type="hidden" class="productCodeHad" value="${item.productCode}" />
      </td>
      <td>${item.dutyStatusDesc}</td>
      <td>${item.isPrimaryPlan}</td>
      <td>${item.insuredName}</td>
      <td>${item.units}</td>
      <td>${item.baseSumIns}</td>
      <td>
        <fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"></fmt:formatDate>
      </td>
      <td>
        <fmt:formatDate value="${item.premInfo.payToDate}" pattern="yyyy-MM-dd"></fmt:formatDate>
      </td>
      <td>${item.premInfo.premTerm}</td>
      <td>${item.premInfo.periodStandardPrem}</td>
      <td>${item.premInfo.frequencyDesc}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<br />
新增附加险列表：
<table class="infoDisTab">
  <tr>
    <th width="5%">选择</th>
    <th width="29%">险种名称</th>
    <th width="9%">保费</th>
    <th width="8%">被保人</th>
    <th width="9%">保额</th>
    <th width="13%">交费年期</th>
    <th width="13%">保险年期</th>
    <th>红利分配方式</th>
    <th>自动续保</th>
  </tr>
  <tbody id="addProductTb">
  </tbody>
  <tr>
    <td colspan="8" align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addProductBt">增加</a>
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="removeProductBt">删除</a>      
      <input type="hidden" name="productSize" id="productSize"/>
      <input type="hidden" name="oneAtleast" class="{required:function(){return $('#addProductTb tr').length<1},messages:{required:'请增加附加险'}}"/>
    </td>
  </tr>
</table>

</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
