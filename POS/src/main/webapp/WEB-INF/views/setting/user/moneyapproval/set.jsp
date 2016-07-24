<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">用户管理&gt;&gt;审批金额等级设置</sf:override>
<script type="text/javascript">
var si = null;
  $(function(){
	 //全选保全项目
	$("#selAll").click(function(){
		$(".servicesCb").attr("checked",this.checked);
	});
	
	//提交
	$("#submitBt").click(function(){
		if($(".servicesCb:checked").length<1){
			$.messager.alert("提示","请选择服务项目！");
			return false;
		}
		
		$("#mainForm").submit();
		return false;
	});
	
	$(".servicesCb").change(function(){
		if(this.checked==true){
			si = $(this).val();
			$("#acceptChannel").trigger("change");
		}
	});
	
	$("#mainForm").validate();
	
	$("#acceptChannel").change(function(){
		if($(this).val()=="ALL"){return;}
		if(si==null){return;}
		$.post("${ctx}setting/user/moneyapproval/query", 
		{amountDaysGrade:$("#amountDaysGrade").val(),
		 serviceItem:si,
		 acceptChannel:$("#acceptChannel").val()},
		function(data) {
			$("#sourceTransferSumSp").text("原值:"+data.SOURCE_TRANSFER_SUM);
			$("#notsourceTransferSumSp").text("原值:"+data.NOTSOURCE_TRANSFER_SUM);
			$("#notselfCashSumSp").text("原值:"+data.NOTSELF_CASH_SUM);
			$("#selfCashSumSp").text("原值:"+data.SELF_CASH_SUM);
			$("#treatyExceedSumSp").text("原值:"+data.TREATY_EXCEED_SUM);
			$("#interestFreeSumSp").text("原值:"+data.INTEREST_FREE_SUM);
		}, "json");
		
	});
	
	$("#listFormSubmitBt").click(function(){
		$("[name=listForm]").submit();
	});
	
  });

</script>
</sf:override>
<sf:override name="content">

<form id="mainForm" action="${ctx}setting/user/moneyApprovalSet" method="post">

<div class="easyui-panel">
 <table class="layouttable">
   <tr>
     <td class="layouttable_td_label">请选择金额等级：</td>
     <td class="layouttable_td_widget">
      <select name="amountDaysGrade" id="amountDaysGrade">
        <c:forEach items="${CodeTable.posTableMap.POS_AMOUNT_DAYS_GRADE}" var="item">
          <option value="${item.code}">${item.description}</option>
        </c:forEach>
      </select>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label_multiple">请选择项目：</td>
     <td class="layouttable_td_widget">
       <input type="checkbox" id="selAll" />全选
       <table>
        <tr>
         <c:forEach items="${CodeTable.posTableMap.PRODUCT_SERVICE_ITEMS_LOST_PREM}" var="item" varStatus="status">
           <td><input type="checkbox" name="serviceItemStr" class="servicesCb" value="${item.code}" />${item.description}</td>
           ${status.count % 6 == 0 ? '</tr><tr>' : ''}
         </c:forEach>
        </tr>
       </table>
       
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">请选择受理渠道：</td>
     <td class="layouttable_td_widget">
       <select name="acceptChannel" id="acceptChannel">
         <option value="ALL">全部</option>
         <c:forEach items="${CodeTable.posTableMap.POS_ACCEPT_CHANNEL_CODE}" var="item">
           <option value="${item.code}">${item.description}</option>
         </c:forEach>         
       </select>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">转账金额（原账户）：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="sourceTransferSum" class="easyui-numberbox {required:true}" precision="2" /><span id="sourceTransferSumSp"></span>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">转账金额（非原账户）：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="notsourceTransferSum" class="easyui-numberbox {required:true}" precision="2" /><span id="notsourceTransferSumSp"></span>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">代办现金金额：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="notselfCashSum" class="easyui-numberbox {required:true}" precision="2" /><span id="notselfCashSumSp"></span>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">亲办现金金额：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="selfCashSum" class="easyui-numberbox {required:true}" precision="2" /><span id="selfCashSumSp"></span>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">协议超出金额：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="treatyExceedSum" class="easyui-numberbox {required:true}" precision="2" /><span id="treatyExceedSumSp"></span>
     </td>
   </tr>
   <tr>
     <td class="layouttable_td_label">免息金额：</td>
     <td class="layouttable_td_widget">
       <input type="text" name="interestFreeSum" class="easyui-numberbox {required:true}" precision="2" /><span id="interestFreeSumSp"></span>
     </td>
   </tr>
   <tr>
     <td align="left">金额录入范围0至99999999999999</td>
     <td align="right">
       <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="submitBt">确定</a>
     </td>
   </tr>
 </table>
 
</div>
</form>

<form name="listForm" action="${ctx}report/submit" class="noBlockUI">
	<input type="hidden" name="sql" value="selectPosAmountDaysPrivsSet" />
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="listFormSubmitBt">已设置数据清单</a>
</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>