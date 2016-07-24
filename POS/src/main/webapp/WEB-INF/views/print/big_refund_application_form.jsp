<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;业务退费申请单打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#back").click(function(){
				window.location.href="${ctx}print/entry";
				return false;
			});
			
			$("#posNo").change(function(){
				var posNo = this.value;
				var $momo = $("#memo");
				if(posNo && !$momo.val()) {
					$.post("${ctx}print/getPosInfoRemark.do", {"posNo":posNo}, function(data){
						if(data && data != "null" && data.flag == "Y" && !$momo.val()) {
							$momo.val(data.remark);
						}
					});
				}
			});
			
			$("#fm").validate();
			$("#posNo").trigger("change");
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form id="fm" class="noBlockUI">
	<div id="p" class="easyui-panel" title="">
	<table class="layouttable">	  		  
		<tbody>
			<tr>
				<th class="layouttable_td_label" width="30%">
					保全号：
					<span class="requred_font">*</span>
		     	</th>
		     	<td class="layouttable_td_widget">
		          <input type="text" name="posNo" id="posNo" value="${param.posNo}" class="input_text {required:true}"/>
				</td>
			</tr>
			<tr>
				<th class="layouttable_td_label" style="vertical-align:top;">
					退费操作备注：
				</th>
		     	<td class="layouttable_td_widget">
		     		<textarea rows="5" cols="50" id="memo" name="memo">${param.memo}</textarea>
		     	</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div id="errorMsg" style="color:red;text-align:left;">
		<c:if test="${not empty message}">
			${message}<br/>
		</c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#fm').submit();return false;"  iconCls="icon-ok" id="confirm">确定</a>
				<a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-back" id="back">返回</a>
			</td>
		</tr>
	</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
