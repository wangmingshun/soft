<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP客户资料查询&gt;&gt;服务专员变更</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	posValidateHandler = function() {
		return true;
	};	
	
	$(function() {
		
		$("#insertBt").click(function() {
			$("#fm").submit();
			return false;
		});

		$("#applyDate").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});
		
		$("#fm").validate();
	});
	
	
	
</script>
</sf:override>
<sf:override name="content">

	<sfform:form commandName="vipApplyDetailDto"
		action="${ctx}vip/empChangeInsert" id="fm">		
		<div class="easyui-panel">
		<table class="layouttable">
			<tr>
				<td class="layouttable_td_label">服务专员编号：</td>
				<td class="layouttable_td_widget">
					<sfform:input path="empNo" cssClass="input_text {required:true}"   size="15"/>
					
					</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">联系方式：</td>
				<td class="layouttable_td_widget">
				
				<sfform:input path="empPhone" cssClass="input_text {required:true}"  maxlength="13" size="15"/>
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">变更事由：</td>
				<td>
				<sfform:textarea id="empChangeReason" path="empChangeReason"
					cssClass="multi_text {required:true,byteRangeLength:[10,500]}" cols="30" rows="2" />
				</td>
			</tr>
			
			<tr>
			   <td>
               </td>				
				<td align="right"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search" id="insertBt">新增</a>
				<a class="easyui-linkbutton" onclick="javascript:history.back();"
					iconCls="icon-search" id="backBt">返回</a></td>
			</tr>
		</table>


		</div>

	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
