<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">风险管控&gt;&gt;批处理抽样复核结果确认</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form").validate();
	$("#queryBt").click(function(){
		$("#allCb").remove();
		$("[name=confirmResult]").removeClass();
		
		$("form").attr("action","${ctx}riskcontrol/batch/confirmQuery");
		$("form").submit();
		return false;
	});
	
	$(".getDueDate").focus(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	
	$("#confirmBt").click(function(){
		
		var checkSame=true;
		if($("[name=confirmResult]:checked").val()==1)
		{
		   $(".seqCb").each(function()
			{
			  if( $(this).is(":checked")==true) 
			   {				
				 var   policyNo= $(this).siblings(".policyH").val() ;	
				 var   dueSumOld= $(this).siblings(".getDueSumH").val() ;				
				 var   dueSumNew=$(this).closest("tr").find(".reviewSum").val();
				 var   dueDateOld= $(this).siblings(".getDueDateH").val() ;   
				 var   dueDateNew=$(this).closest("tr").find(".reviewDate").val();
				 if(dueSumOld!=dueSumNew)
					 {					 
					   $.messager.alert("提示", policyNo+"应领金额和复核金额不一致，不允许确认意见为通过");
					   checkSame=false;					 
					 }				 
			        
			       if(dueDateOld!=dueDateNew)
				      {				  
				       $.messager.alert("提示", policyNo+"应领日期和复核应领日期不一致，不允许确认意见为通过");
				        checkSame=false;
				      }	
			      }
			   });
		  
		}
		 if(checkSame==false)
			{
			 return false;
			}
		

		$(".seqCb").each(function(){	     
			   
			$(this).next().val(this.checked);
		   });  
		
		$("form").attr("action","${ctx}riskcontrol/batch/confirmSubmit");
		$("form").attr("method","post");
		$("form").submit();
		return false;
	});
	$("#allCb").change(function(){
		$(".seqCb").attr("checked",this.checked);
	});	
});

</script>

</sf:override>

<sf:override name="content">

<form>

<div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">批处理类型：</td>
      <td class="layouttable_td_widget">
      <select name="sampleType">
        <option value="1">生存金</option>
        <!-- option value="2">红利</option -->
      </select>      
      </td>
      <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
</div>

<br />
<c:if test="${not empty confirmList}">
<table class="infoDisTab">
  <tr>
    <th>选择<input type="checkbox" id="allCb" name="seqAllCb" class="{required:function(){return $('.seqCb:checked').length<1}}" />全选</th>
    <th>保单号</th>
    <th>复核结果</th>
    <th>复核人</th>
    <th>应领金额</th>
    <th>应领日期</th>
    <th>复核金额</th>
    <th>复核应领日期</th>
    <th>备注</th>
    </tr>
  <c:forEach items="${confirmList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>
        <input type="checkbox" class="seqCb" name="sampleSeq" value="${item.SAMPLE_SEQ}" />
        <input type="hidden"   name="sampleList[${status.index}].selected" />
        <input type="hidden"   name="sampleList[${status.index}].sampleSeq" value="${item.SAMPLE_SEQ}" />
        <input type ="hidden"  class="policyH" value="${item.POLICY_NO}"/>
        <input type ="hidden"  class="getDueSumH" value="${item.GET_DUE_SUM}"/>
        <input type ="hidden"  class="getDueDateH" value="${item.GET_DUE_DATE}"/>
      </td>
      <td >${item.POLICY_NO}</td>
      <td>${item.REVIEW_RESULT}</td>
      <td>${item.REVIEWER}</td>
      <td>${item.GET_DUE_SUM}</td>
      <td>${item.GET_DUE_DATE}</td>      
      <td><input type="text" size="8" id="reviewSum"  name="sampleList[${status.index}].reviewSum" name="reviewSum"  class=" reviewSum input_text {required:true,number:true}" value="${item.REVIEW_GET_DUE_SUM}" ></input></td>
      <td><input type="text"  size="10"  id="reviewDate" name="sampleList[${status.index}].reviewDate"   class="reviewDate Wdate {required:true} getDueDate" value="${item.REVIEW_GET_DUE_DATE}" ></input></td>
      <td>${item.RESULT_DESC}</td>      
      </tr>
  </c:forEach>
</table>

    <div class="easyui-panel">
    确认意见：
    <input type="radio" name="confirmResult" value="1" class="required" />通过
    <input type="radio" name="confirmResult" value="2" class="required" />不通过
    <input type="radio" name="confirmResult" value="3" class="required" />个单数据异常
    &nbsp;&nbsp;备注：<input type="text" name="resultDesc" size="50" class="{byteRangeLength:[0,4000]}"/>
    <br />
    <div align="right"><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="confirmBt">确定</a></div>
    </div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>