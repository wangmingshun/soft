<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;批单批次打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		posValidateHandler = function() {
			return true;
		};
		
		function asc(x,y) {
			if (x > y)
				return 1;
			else
				return -1;
		}
		
		/* 格式化保全项目录入值，并去除掉无效录入值  */
		function formatServiceItemsInput(val) {
			var v = val.replace(/\D/g, " ");
			v = $.trim(v);
			v = v.replace(/\s+/g, ",");
			var arr = v.split(",");
			var arr1 = [];
			var num;
			$.each(arr, function(idx, content){
				num = parseInt(content);
				if(num >= 1 && num <= 40 && arr1.indexOf(num) == -1) {
					arr1.push(num);
				}
			});
			arr1.sort(asc);
			return arr1.join(",");
		}
		
		$(function(){
			
			$("[name='effectDateStart'],[name='effectDateEnd']").focus(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
			});
			
			$("#acceptBranchCodeLink").click(function(){
				showBranchTree($("[name='acceptBranchCode']"), "03", "${LOGIN_USER_INFO.loginUserUMBranchCode}");
				return false;
			});
			
			/* 保全项目选择按钮click事件处理，弹出保全项目选择窗口 */
			$("#serviceItemsLink").click(function(event){
				var $link = $(this);
				openProductServiceItemSelectWindow(function(labelData, valueData){
					//给文本框赋值
					$link.siblings(":text").val(valueData).trigger("change");
				}, $link.siblings(":text").val());
				return false;
			});
			
			/* 保全项目录入框change事件处理，更新保全项目名称显示 */
			$("[name='serviceItems']").change(function(event){
				var $input = $(this);
				var val = formatServiceItemsInput($input.val());
				$input.val(val);
				var label = getPosTypeLabels(val);
				var $ul = $("<ul class='itemList'/>");
				var hasContent = false;
				$.each(label.split(","), function(idx, content) {
					if(content != "") {
						$("<li/>").text(content).appendTo($ul);
						hasContent = true;
					}
				});
				$input.siblings("ul").remove().end();
				if(hasContent) {
					$input.closest("td").append($ul);
				}
			});
			
			$("#back").click(function(){
				window.location.href="${ctx}print/entry.do";
				return false;
			});
			
			$("#print").click(function(){
				$("#fm").attr("action", "${ctx}print/endorsement_batch.do?printType=0&entOrApplication=logo").submit();
				return false;
			});
			
			$("#fm").validate();
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="print_endorsement_criteria" id="fm" class="noBlockUI">
	<table class="layouttable">
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                      生效时间：
		        <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <sfform:input path="effectDateStart" cssClass="Wdate {required:true}" id="effectDateStart"/>
					至
				   <sfform:input path="effectDateEnd" cssClass="Wdate {required:true}" id="effectDateEnd"/>
		      </td>
		      <td>
		        &nbsp;
		      </td>
	    </tr>
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                           受理渠道：
		      </td>
		      <td width="50%" class="layouttable_td_widget">
				    <sfform:select path="acceptChannelCode" id="acceptChannelCode">					
					    <sfform:option value="">全部</sfform:option>
					    <sfform:option value="1">柜面受理</sfform:option>
					    <sfform:option value="12">银代前置受理</sfform:option>
					    <sfform:option value="11">移动保全受理</sfform:option>
					    <sfform:option value="7">网络保全受理</sfform:option>
					    <sfform:option value="4">电话中心受理</sfform:option>
					    <sfform:option value="10">续期受理</sfform:option>
					    <sfform:option value="13">电销受理</sfform:option>
					</sfform:select>
		      </td>
		      <td>
		        &nbsp;
		      </td>
	     </tr>
	      <tr>
			<td width="10%" class="layouttable_td_label">保单渠道：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="policyChannelCode" id="policyChannelCode">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>
             <td>&nbsp;		        
		      </td>
		</tr>
	     
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                    机构：
		        <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <sfform:input path="acceptBranchCode" cssClass="input_text required" readonly="true"/>
				 <a href="javascript:void(0);" id="acceptBranchCodeLink">选择</a>
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	    
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                    保全项目：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <sfform:input path="serviceItems" cssClass="input_text"/>
				  <a href="javascript:void(0);" id="serviceItemsLink">选择</a>
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	    
	    
	        
	     <tr>
		     <td width="10%" class="layouttable_td_label">
		                    受理人：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <sfform:input path="acceptor" cssClass="input_text" id="acceptor"/>
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	    
	      <tr>
		     <td width="10%" class="layouttable_td_label">
		                    寄送方式：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <sfform:select path="approvalServiceType" items="${CodeTable.posTableMap.APPROVAL_SERVICE_TYPE}" 
		         itemLabel="description" itemValue="code" id="approvalServiceType"/>
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	      <tr>
		     <td width="10%" class="layouttable_td_label">
		                   打印选项：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		     	<sfform:radiobutton path="printOptions" value="Y" label="全部" />
		     	<sfform:radiobutton path="printOptions" value="N" label="未打印" checked="true" />
		     </td>
	         <td colspan="3" align="right">
		       <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-print" id="print">激光打印批单</a>
			   <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#fm').submit();return false;"  iconCls="icon-print" id="printEnt">针式打印批单</a>
			   <a class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-back"  id="back">返回</a>
		     </td>
	      </tr>
	    
	</table>
	<hr class="hr_default"/>
	<div style="color:red;text-align:left;">
		${message}
		<c:if test="${not empty message}"><br/></c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	</sfform:form>
	<jsp:include page="/WEB-INF/views/include/branchTree.jsp" />
	<jsp:include page="/WEB-INF/views/include/productServiceItemsSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
