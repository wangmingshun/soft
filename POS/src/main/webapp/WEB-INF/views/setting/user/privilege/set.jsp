<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">用户管理&gt;&gt;用户权限设置</sf:override>
<jsp:include page="/WEB-INF/views/include/selectJQuery.jsp"></jsp:include>
<script type="text/javascript">
  $(function(){
	
	$("form[name='queryForm']").validate();
	$("form[name='privilegeForm']").validate();
		  
	 //全选保全项目
	$("#selAll").click(function(){
		$(".servicesCb").attr("checked",this.checked);
	});
	//查询
	$("#queryBt").click(function(){
		$("form[name='queryForm']").submit();
		return false;
	});
	//提交
	$("#setSubBt").click(function(){
		$("#selected option").attr("selected",true);//全选上才能得到值
		$("#selected2 option").attr("selected",true);
		$("form[name='privilegeForm']").submit();
		return false;
	});
	
	//选择柜面
	$("#counterChoseBt").click(function(){
		showCounterWindow($("[name='counterNo']"),'${userUMBranchCode}');
		return false;
	});
	
	//取消上级
	$("#removeUpperBt").click(function(){
		if("${USER_ID}"==""){
			return false;
		}
        $.post("${ctx}setting/user/privilege/removeUpper", 
	          {userId:"${USER_ID}"},
			  function(data) {
				  if(data=="Y"){
					  $.messager.alert("提示","取消成功！");
					  $("#upperUserId").val("");
				  }
		      }, "text");
        return false;
	});
	
	//验证上级用户，如果不为空或自己，则后台去校验必须存在且不是下级
	$("#upperUserId").blur(function(){
		var s = $(this).val();
		if(s!="" && "${USER_ID}"!=""){
			$.post("${ctx}setting/user/privilege/upperCheck",
			{upperId:s, userId:"${USER_ID}"},
			function(data){
				if(data=="1"){
					$.messager.alert("提示", "上级用户"+s+"不存在，请重新录入");
					$("#upperUserId").val("");
				}else if(data=="2"){
					$.messager.alert("提示", s+"已是${USER_ID}下级，请重新录入");
					$("#upperUserId").val("");
				}
			}, "text");
		}
	});
	
	$("#amountDaysChannel").change(function(){
		$.post("${ctx}setting/user/privilege/queryAmount",
		{amountDaysGrade:$("#amountDaysGrade").val(),channel:$(this).val()},
		function(data){
			$("#serviceItemsTb").empty();
			if(data.items.length==0){
				if("${USER_ID}"!=""){
					$.messager.alert("提示","该审批等级和渠道下尚未设置有效的项目信息");
				}
				$("#sourceTransferSumSp").text("");
				$("#notsourceTransferSumSp").text("");
				$("#notselfCashSumSp").text("");
				$("#selfCashSumSp").text("");
				$("#treatyExceedSumSp").text("");
				$("#interestFreeSumSp").text("");
				
				return;
				
			}else{
				var str = '<tr>';
				$.each(data.items,function(i,map){
						str += '<td width="16%"><input type="checkbox" value="'+map["SERVICE_ITEMS"]+'" name="amountDaysPrivsDTO.serviceItemStr" class="servicesCb" />'+map["ITEMS_DESC"]+'</td>';
					if((i+1)%6==0){//换行
						str += '</tr><tr>';
					}
				});
				str += '<tr>';
				$("#serviceItemsTb").append(str);

				$("#sourceTransferSumHid").val(data.SOURCE_TRANSFER_SUM);
				$("#notsourceTransferSumHid").val(data.NOTSOURCE_TRANSFER_SUM);
				$("#notselfCashSumHid").val(data.NOTSELF_CASH_SUM);
				$("#selfCashSumHid").val(data.SELF_CASH_SUM);
				$("#treatyExceedSumHid").val(data.TREATY_EXCEED_SUM);
				$("#interestFreeSumHid").val(data.INTEREST_FREE_SUM);
				
				$("#sourceTransferSumSp").text("等级原值:"+data.SOURCE_TRANSFER_SUM);
				$("#notsourceTransferSumSp").text("等级原值:"+data.NOTSOURCE_TRANSFER_SUM);
				$("#notselfCashSumSp").text("等级原值:"+data.NOTSELF_CASH_SUM);
				$("#selfCashSumSp").text("等级原值:"+data.SELF_CASH_SUM);
				$("#treatyExceedSumSp").text("等级原值:"+data.TREATY_EXCEED_SUM);
				$("#interestFreeSumSp").text("等级原值:"+data.INTEREST_FREE_SUM);
			}
		}, "json");
	});
	
	$("#inputGrade").change(function(){

		$.post("${ctx}setting/user/privilege/inputGradeItems",
		{inputGrade:$("#inputGrade").val()},
		function(data){
			$("#forSelect").empty();
			$("#selected").empty();
			if(data.length==0){
				if("${USER_ID}"!=""){
					$.messager.alert("提示","该录入等级下尚未设置保全项目信息");	
				}
				return;
			}else{
				$.each(data,function(i,map){
					$("#selected").append('<option value="'+map["CODE"]+'">'+map["DESCRIPTION"]+'</option>')
				});
			}
			
			initUserInfo();
			
		}, "json");
	});
	$("#amountDaysGrade").change(function(){
		$("#amountDaysChannel").trigger("change");
	});
	//删除了某保全项目，该保全项目对于的特殊件也要移除
	$("#selected").dblclick(function(){
		var v = $("#forSelect").find("option:selected").val();
		if(v.substr(0,2)!="S_"){
			$("#selected option").each(function(){
				var vs = $(this).val();
				if(vs.substr(0,2)=="S_" && vs.substr(vs.indexOf("_",2)+1)==v){
					$(this).remove().appendTo("#forSelect");
				}
			});
		}
	});
	//已增加了相应的保全项目，才能增加相应特殊件
	$("#forSelect").dblclick(function(){
		var $the = $("#selected").find("option:selected");
		var v = $the.val();
		if(v.substr(0,2)=="S_"){
			$("#forSelect option").each(function(){
				var vs = $(this).val();
				if(vs.substr(0,2)!="S_" && vs==v.substr(v.indexOf("_",2)+1)){
					$the.remove().appendTo("#forSelect");
				}
			});
		}
	});

	init();
	
	//隐藏 退信处理权限 posNoteReturnPrivs
	$("select[name='posNoteReturnPrivs']").closest("tr").hide();

  });

function init(){
	$("#inputGrade").trigger("change");
	$("#amountDaysGrade").trigger("change");
	initUserInfo();
}

//若页面已有查询到的用户信息，筛选展示
function initUserInfo(){
	
	<c:if test="${userExist eq 'Y'}">
	 $("#deleteAll").trigger("click");
	 $("#deleteAll2").trigger("click");
	 <c:forEach items="${inputItems}" var="item">
		 $("#forSelect option").each(function(){ 
			if($(this).val()=="${item.SERVICE_ITEMS}"){
			 $("#selected").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option");
			 $(this).remove();
			}
		 });
	 </c:forEach>
	 <c:forEach items="${specialPrivs}" var="item">
		 $("#forSelect option").each(function(){
			 var v = $(this).val();
			 v = v.substring(2,v.indexOf("_",2));
			if(v=="${item.SPECIAL_FUNC}"){
			 $("#selected").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option");
			 $(this).remove();
			}
	
		 });
	 </c:forEach>
	 <c:forEach items="${aprovFuncSpecs}" var="item">
		 $("#forSelect2 option").each(function(){ 
			if($(this).val()=="${item.SPECIAL_FUNC}"){
			 $("#selected2").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option");
			 $(this).remove();
			}
	
		 });
	 </c:forEach>
	</c:if>
}

</script>
</sf:override>
<sf:override name="content">
 
<form name="queryForm" action="${ctx}setting/user/privilege/query">
 <div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">请输入用户名:</td>
      <td class="layouttable_td_widget">
        <input name="user" type="text" class="{required:true}" value="${USER_ID}" />
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
 </div>
 
</form>

<form name="privilegeForm" action="${ctx}setting/user/privilege/submit" method="post">

<br />
 
<div class="easyui-panel" title="用户详细信息">
<table class="infoDisTab">
  <thead>
    <tr>
       <th>用户代码</th>
       <th>用户名称</th>
       <th>职级</th>
       <th>异地权限</th>
       <th>上级用户代码</th>
       <th>所属柜面</th>
    </tr>
  </thead>
  <tbody>
    <c:if test="${userExist eq 'Y'}">
    <tr>
       <td>${userInfo.USER_ID}</td>
       <td>${userInfo.USER_NAME}</td>
       <td>
        <c:forEach items="${CodeTable.posTableMap.POS_RANK_CODE}" var="item">
          <c:if test="${userInfo.RANK_CODE eq item.code}">${item.description}</c:if>
        </c:forEach>
       </td>
       <td>${userInfo.DIFFERENT_PLACE_PRIVS eq 'Y'? '是':'否' }</td>
       <td>${userInfo.UPPER_USER_ID}</td>
       <td>${userInfo.COUNTER_NO}</td>
    </tr>    
    </c:if>
  </tbody>
</table>
</div>

<br />

<div class="easyui-panel" title="主管详细信息">
<table class="infoDisTab">
  <thead>
    <tr>
       <th>用户代码</th>
       <th>用户名称</th>
       <th>职级</th>
       <th>异地权限</th>
       <th>上级用户代码</th>
       <th>所属柜面</th>
    </tr>
  </thead>
  <tbody>
    <c:if test="${userExist eq 'Y'}">
    <tr>
       <td>${upperInfo.USER_ID}</td>
       <td>${upperInfo.USER_NAME}</td>
       <td>
        <c:forEach items="${CodeTable.posTableMap.POS_RANK_CODE}" var="item">
          <c:if test="${upperInfo.RANK_CODE eq item.code}">${item.description}</c:if>
        </c:forEach>
       </td>
       <td>${upperInfo.DIFFERENT_PLACE_PRIVS eq 'Y'? '是':'否' }</td>
       <td>${upperInfo.UPPER_USER_ID}</td>
       <td>${upperInfo.COUNTER_NO}</td>
    </tr>    
    </c:if>
  </tbody>
</table>
</div>

<br />

<div class="easyui-panel" title="用户信息设置">
    <table class="layouttable">
     <tr>
       <td class="layouttable_td_label">用户代码：</td>
       <td class="layouttable_td_widget">${USER_ID}</td>
       <td class="layouttable_td_label">用户名称：</td>
       <td class="layouttable_td_widget">
        <input type="text" name="userName" value="${userInfo.USER_NAME}" />
        <input type="hidden" name="userId" value="${USER_ID}" />
       </td>
       <td class="layouttable_td_label">用户职级：</td>
       <td class="layouttable_td_widget">
        <select name="rankCode" id="rankCode">
         <c:forEach items="${CodeTable.posTableMap.POS_RANK_CODE}" var="item">
            <c:if test="${isPM eq true || LOGIN_USER_INFO.loginUserRankCode+0 ge item.code+0}">
            	<option value="${item.code}" <c:if test="${userInfo.RANK_CODE eq item.code}">selected</c:if>>${item.description}</option>
            </c:if>
         </c:forEach>
        </select>
       </td>
     </tr>
     <tr>
       <td class="layouttable_td_label">上级主管：</td>
       <td class="layouttable_td_widget">
        <input type="text" name="upperUserId" id="upperUserId" value="${userInfo.UPPER_USER_ID}" />
        <a id="removeUpperBt" href="javascript:void(0)">取消用户上级</a>
       </td>
       <td class="layouttable_td_label">用户所在柜面：</td>
       <td class="layouttable_td_widget">
         <input type="text" name="counterNo" value="${userInfo.COUNTER_NO}" />
         <a id="counterChoseBt" href="javascript:void(0)">选择柜面</a>
       </td>
       <td class="layouttable_td_label">用户录入等级：</td>
       <td class="layouttable_td_widget">
        <select name="inputGrade" id="inputGrade" class="{required:true}">
          <c:forEach items="${CodeTable.posTableMap.POS_INPUT_GRADE_CODE}" var="item">
            <c:if test="${isPM eq true}">
            	<option value="${item.code}" <c:if test="${userInfo.INPUT_GRADE eq item.code}">selected</c:if>>${item.description}
            	</option>
            </c:if>
            <c:if test="${isPM eq false and not empty userInfo and userInfo.INPUT_GRADE eq item.code}">
            	<option value="${item.code}">${item.description}</option>
            </c:if>
          </c:forEach>
        </select>
       </td>
     </tr>
     <tr>
        <td class="layouttable_td_label">用户审批金额等级：</td>
        <td class="layouttable_td_widget">
          <select name="amountDaysGrade" id="amountDaysGrade" class="{required:true}">
            <c:forEach items="${CodeTable.posTableMap.POS_AMOUNT_DAYS_GRADE}" var="item">
	            <c:if test="${isPM eq true}">
	            	<option value="${item.code}" <c:if test="${userInfo.AMOUNT_DAYS_GRADE eq item.code}">selected</c:if>>${item.description}</option>
	            </c:if>
                <c:if test="${isPM eq false and not empty userInfo and userInfo.AMOUNT_DAYS_GRADE eq item.code}">
                    <option value="${item.code}">${item.description}</option>
                </c:if>
            </c:forEach>
          </select>
        </td>
     <c:if test="${isPM eq true}">
       <td class="layouttable_td_label"><span>受理回退审批权限：</span></td>
       <td class="layouttable_td_widget">
         <select name="aprovRollbackPrivs">
          <option value="N" <c:if test="${aprovRollbackPrivs eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${aprovRollbackPrivs eq 'Y'}">selected</c:if>>是</option>
         </select>
       </td>
       <td class="layouttable_td_label">异地操作权限：</td>
       <td class="layouttable_td_widget">
         <select name="differentPlacePrivs">
          <option value="N" <c:if test="${userInfo.DIFFERENT_PLACE_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.DIFFERENT_PLACE_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
       </td>
     </tr>
     <tr>
        <td class="layouttable_td_label">规则特殊件审批权限：</td>
        <td class="layouttable_td_widget">
         <select name="aprovRulePrivs">
          <option value="N" <c:if test="${aprovRulePrivs eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${aprovRulePrivs eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>
        <td class="layouttable_td_label">是否预审员：</td>
        <td class="layouttable_td_widget">
            <select name="inquiryPrivs">
                <option value="N" <c:if test="${userInfo.INQUIRY_PRIVS eq 'N'}">selected</c:if>>否</option>
                <option value="Y" <c:if test="${userInfo.INQUIRY_PRIVS eq 'Y'}">selected</c:if>>是</option>
            </select>
        </td>
        </c:if>
        <td class="layouttable_td_label">移动保全权限：</td>
        <td> 
        <select name="mposPrivs">
          <option value="N" <c:if test="${userInfo.MPOS_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.MPOS_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>     
     </tr>
     <tr>        
        <td class="layouttable_td_label">银保前置保全处理员权限：</td>
        <td> 
        <select name="biaTransFailPrivs">
          <option value="N" <c:if test="${userInfo.BIA_TRANSFAIL_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.BIA_TRANSFAIL_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>     
        
        <td class="layouttable_td_label">自助终端权限：</td>
        <td> 
        <select name="epointPrivs">
          <option value="N" <c:if test="${userInfo.EPOINT_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.EPOINT_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>
       <td class="layouttable_td_label">电销保单续期保费退费权限：</td>
        <td> 
        <select name="returnpremPrivs">
          <option value="N" <c:if test="${userInfo.RETURN_PREM_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.RETURN_PREM_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>
     </tr>
     <tr>
     	<td class="layouttable_td_label">退信处理权限：</td>
        <td> 
        <select name="posNoteReturnPrivs">
          <option value="N" <c:if test="${userInfo.POS_NOTE_RETURN_PRIVS eq 'N'}">selected</c:if>>否</option>
          <option value="Y" <c:if test="${userInfo.POS_NOTE_RETURN_PRIVS eq 'Y'}">selected</c:if>>是</option>
         </select>
        </td>
     </tr>
     
    </table>
</div>

<br />

<div class="easyui-panel" title="1、用户录入权限设置<c:if test='${isPM eq true}'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、用户功能特殊件审批权限设置</c:if>">

<table width="100%">
  <tr>
    <td width="50%">

        <table>
          <tr>
            <td>
              <fieldset class="fieldsetdefault"><legend>可增加操作</legend>
              <select multiple="multiple" style="height:220px; width:150px" id="forSelect">
              </select>
              </fieldset>
            </td>
            <td align="center" width="30%">
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add">&nbsp;&nbsp;增加&nbsp;&nbsp;</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="delete">&nbsp;&nbsp;删除&nbsp;&nbsp;</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addAll">全部增加</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="deleteAll">全部删除</a><br /><br />
            </td>
            <td>
              <fieldset class="fieldsetdefault"><legend>已设置操作</legend>
              <select multiple="multiple" style="height:220px; width:150px" id="selected" name="serviceItems" >
              </select>
              </fieldset>
            </td>
          </tr>
        </table>
    
    </td>
    <td>
    
    <c:if test="${isPM eq true}">
        <table>
          <tr>
            <td>
              <fieldset class="fieldsetdefault"><legend>可增加操作</legend>
              <select multiple="multiple" style="height:220px; width:150px" id="forSelect2">
              </select>
              </fieldset>
            </td>
            <td align="center" width="30%">
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add2">&nbsp;&nbsp;增加&nbsp;&nbsp;</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="delete2">&nbsp;&nbsp;删除&nbsp;&nbsp;</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addAll2">全部增加</a><br /><br />
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="deleteAll2">全部删除</a><br /><br />
            </td>
            <td>
              <fieldset class="fieldsetdefault"><legend>已设置操作</legend>
              <select multiple="multiple" style="height:220px; width:150px" id="selected2" name="aprovFuncSpecials" >
                <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_FUNC_CODE}" var="item">
                  <option value="${item.code}">${item.description}</option>
                </c:forEach>
              </select>
              </fieldset>
            </td>
          </tr>
        </table>
    </c:if>
    </td>
  </tr>
  <tr>
    <td></td>
  </tr>
</table>
</div>


<br />

<div class="easyui-panel" title="用户审批金额权限设置">
<table class="layouttable">
   <tr>
     <td class="layouttable_td_label_multiple" width="15%">请选择项目：<input type="hidden" name="serviceItemStr" /></td>
     <td class="layouttable_td_widget">
       <input name="selAll" type="checkbox" id="selAll" />全选
       <table width="100%" id="serviceItemsTb">
       </table>
       
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">请选择受理渠道：</td>
     <td class="layouttable_td_widget">
       <select name="amountDaysPrivsDTO.acceptChannel" id="amountDaysChannel">
         <c:forEach items="${CodeTable.posTableMap.POS_ACCEPT_CHANNEL_CODE}" var="item">
           <option value="${item.code}">${item.description}</option>
         </c:forEach>
       </select>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">转账金额（原账户）：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="sourceTransferSumHid" />
       <input name="amountDaysPrivsDTO.sourceTransferSum" class="easyui-numberbox {notBiggerThan:'#sourceTransferSumHid'}" precision="2" />
       <span id="sourceTransferSumSp"></span>
       <c:if test="${not empty userPremPrivs.SOURCE_TRANSFER_SUM}">&nbsp;用户原值:${userPremPrivs.SOURCE_TRANSFER_SUM}</c:if>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">转账金额（非原账户）：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="notsourceTransferSumHid" />
       <input name="amountDaysPrivsDTO.notsourceTransferSum" class="easyui-numberbox {notBiggerThan:'#notsourceTransferSumHid'}" precision="2" />
       <span id="notsourceTransferSumSp"></span>
       <c:if test="${not empty userPremPrivs.NOTSOURCE_TRANSFER_SUM}">&nbsp;用户原值:${userPremPrivs.NOTSOURCE_TRANSFER_SUM}</c:if>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">代办现金金额：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="notselfCashSumHid" />
       <input name="amountDaysPrivsDTO.notselfCashSum" class="easyui-numberbox {notBiggerThan:'#notselfCashSumHid'}" precision="2" />
       <span id="notselfCashSumSp"></span>
       <c:if test="${not empty userPremPrivs.SELF_CASH_SUM}">&nbsp;用户原值:${userPremPrivs.SELF_CASH_SUM}</c:if>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">亲办现金金额：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="selfCashSumHid" />
       <input name="amountDaysPrivsDTO.selfCashSum" class="easyui-numberbox {notBiggerThan:'#selfCashSumHid'}" precision="2" />
       <span id="selfCashSumSp"></span>
       <c:if test="${not empty userPremPrivs.NOTSELF_CASH_SUM}">&nbsp;用户原值:${userPremPrivs.NOTSELF_CASH_SUM}</c:if>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">协议超出金额：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="treatyExceedSumHid" />
       <input name="amountDaysPrivsDTO.treatyExceedSum" class="easyui-numberbox {notBiggerThan:'#treatyExceedSumHid'}" precision="2" />
       <span id="treatyExceedSumSp"></span>
       <c:if test="${not empty userPremPrivs.TREATY_EXCEED_SUM}">&nbsp;用户原值:${userPremPrivs.TREATY_EXCEED_SUM}</c:if>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">免息金额：</td>
     <td class="layouttable_td_widget">
       <input type="hidden" id="interestFreeSumHid" />
       <input name="amountDaysPrivsDTO.interestFreeSum" class="easyui-numberbox {notBiggerThan:'#interestFreeSumHid'}" precision="2" />
       <span id="interestFreeSumSp"></span>
       <c:if test="${not empty userPremPrivs.INTEREST_FREE_SUM}">&nbsp;用户原值:${userPremPrivs.INTEREST_FREE_SUM}</c:if>
     </td>
   </tr>
 </table>
</div>
<p align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="setSubBt">确定</a>
    <input type="hidden" name="umcheck" id="umcheck" value="${UM_CHECK_FLAG}" class="{required:function(){return $('#umcheck').val()!='Y'},messages:{required:'请先查询用户在UM中的POS系统权限，通过后再操作设置'}}" />
</p>
<br />

</form>

<jsp:include page="/WEB-INF/views/include/counterChoose.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>