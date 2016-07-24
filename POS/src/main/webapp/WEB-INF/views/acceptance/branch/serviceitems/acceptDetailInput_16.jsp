<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!-- 补充告知 -->
<sf:override name="headBlock">
<script type="text/javascript">
  $(function(){
	  /*
	  $("#startDate").focus(function(){
		  WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	  });
	  $(".informMatter").change(function(){
		  if($(".informMatter:checked").val() == "5") {
			  $("#relationshipTR").show().find(":input").removeClass("ignore");
			  $("#relationship").trigger("change");
		  } else {
			  $("#relationshipTR").hide().find(":input").removeClass("ignore").addClass("ignore");
		  }
	  });
	  */
	  
	  $("input[name='informMatterE']").change(function() {
		  if($("input[name='informMatterE']").attr("checked") == true) {
			  $("#relationshipTR").show().find(":input").removeClass("ignore");
			  $("#relationship").trigger("change");
		  } else {
			  $("#relationshipTR").hide().find(":input").removeClass("ignore").addClass("ignore");
		  }
	  });
	  
	  $("#relationship").change(function(){
		  if($("#relationship").val() == "99") {
			  $("#relationshipDesc").show().removeClass("ignore");
		  } else {
			  $("#relationshipDesc").hide().removeClass("ignore").addClass("ignore");
		  }
	  });
	  
	  $("#checkBefore").change(function() {
		  if($(this).attr("checked") == true) {
			    var informObject = $("input[name='informObject']:checked").val();
			    if(informObject == "A") {
					$(this).nextAll(":input").attr("disabled", false);
			    } else {
			    	$(this).nextAll(":input").attr("disabled", true).attr("checked", false);
					$(this).nextAll(":input:not(:input[name='informMatterE'])").attr("disabled", false);
			    }
				$(".matterBeforeDesc").attr("disabled", false);
			} else {
				$(this).nextAll(":input").attr("disabled", true).attr("checked", false);
				$(".matterBeforeDesc").attr("disabled", true).val("");
			}
		    $("input[name='informMatterE']").trigger("change");
	  });
	  
	  $("#checkAfter").change(function() {
		  if($(this).attr("checked") == true) {
				$(".matterAfterDesc").attr("disabled", false);
			} else {
				$(".matterAfterDesc").attr("disabled", true).val("");
			}
	  });
	  
	  $("input[name='informObject']").change(function() {
		  $("#checkBefore").trigger("change");
	  });
	  
	  $("input[name='informMatterE']").trigger("change");
	  $("#checkBefore").trigger("change");
	  $("#checkAfter").trigger("change");

	  posValidateHandler = function(){
		 var before = checkInputBefore();
		 var after = ($("textarea[name='matterAfterDesc']").val() != "") ? true : false;
		 if(before == "0" && !after) {
			 $.messager.alert("提示","告知前后事项必须填写至少一项!");
		 } else if(before == "2") {
			 $.messager.alert("提示","\"补充告知投保前事项\"已经勾选了至少一项，则\"补充告知投保前事项描述\"不能为空，请录入！");
		 } else if(before == "3") {
			 $.messager.alert("提示","\"补充告知投保前事项描述\"已经录入，则\"补充告知投保前事项\"至少勾选一项，请勾选！");
		 }
		 
		 if(before == "2" || before == "3") {
			 //此种情况下，说明录入了但不满足要求。
			 return false;
		 } else if(before == "0" && !after) {
			 //此种情况下是两者都没录入
			 return false;
		 }
		 
		 //通过录入要求后，将勾选项取到，勾选了的不处理，未勾选的设置为disabled，就不提交都后台了
		 $(".informMatters").find(":input").each(function() {
			  if($(this).attr("checked") == false) {
				  $(this).attr("disabled", true);
			  }
		  });
		
		 return true;
	  }
	  
	  //"0" 表示没做任何操作，"1"表示符合要求，"2"表示勾选了没有录入描述，"3"表示录入描述但没有勾选
	  function checkInputBefore() {
		  var flag = "0";
		  $(".informMatters").find("input:not(#checkBefore)").each(function() {
			  if($(this).attr("checked") == true) {
				  //勾选并且，补充告知投保前事项描述不为空，则验证通过
				  if($("textarea[name='matterBeforeDesc']").val() != "") {
					  flag = "1";
				  } else {
					  flag = "2";
				  }
			 	  return false;
			  }
		  });
		  if(flag == "0") {
			  if($("textarea[name='matterBeforeDesc']").val() != "") {
				  flag = "3";
			  }
		  }
		  return flag;
	  }
	  
	  //$(".informMatter").trigger("change");
  });

</script>
</sf:override>

<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<table class="layouttable">
		<tbody>
			<tr>
				<th class="layouttable_td_label">请选择补充告知对象：</th>
				<td class="layouttable_td_widget">
					<sfform:radiobutton path="informObject" value="A" cssClass="{required:true}" label="投保人"/>
					<!--<sfform:radiobutton path="informObject" value="I" cssClass="{required:true}" label=" 被保人"/>-->
					  被保人:
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
						<sfform:radiobutton path="informObject" value="I,${clientNoInsList.insuredNo}" cssClass="{required:true}" label="${clientNoInsList.insuredName}"/>
					</c:forEach>
				</td>
			</tr>
<%--
			<tr>
				<th class="layouttable_td_label">请选择告知事项：</th>
				<td class="layouttable_td_widget">
					<sfform:radiobutton path="informMatter" value="1" cssClass="{required:true} informMatter" label="投保时未如实告知"/>
					<sfform:radiobutton path="informMatter" value="2" cssClass="{required:true} informMatter" label="保全项目未如实告知"/>
					<sfform:radiobutton path="informMatter" value="3" cssClass="{required:true} informMatter" label="健康状况改变"/>
					<sfform:radiobutton path="informMatter" value="4" cssClass="{required:true} informMatter" label="变更或取消特别约定"/>
					<sfform:radiobutton path="informMatter" value="5" cssClass="{required:true} informMatter" label="是被保险人的（关系）"/>
				</td>
			</tr>
			<tr id="relationshipTR">
				<th class="layouttable_td_label">是被保险人的（关系）：</th>
				<td class="layouttable_td_widget">
					<sfform:select path="relationship" items="${CodeTable.posTableMap.RELATIONSHIP}" itemLabel="description" itemValue="code"
						cssClass="input_select {required:true}" id="relationship"/>
					<sfform:input path="relationshipDesc" id="relationshipDesc" cssClass="input_text {required:true}" />
				</td>
			</tr>
			<tr>
				<th class="layouttable_td_label">告知事项起点时间：</th>
				<td class="layouttable_td_widget">
					<sfform:input path="startDate" cssClass="Wdate {required:true}" />
				</td>
			</tr>
			<tr>
				<th class="layouttable_td_label" style="vertical-align:top;">告知事项描述：</th>
				<td class="layouttable_td_widget">
					<sfform:textarea path="matterDesc" rows="3" cols="40" cssClass="multi_text {required:true}" />
				</td>
			</tr>
 --%>
			<tr>
				<th class="layouttable_td_label" style="vertical-align:top;">补充告知投保前事项：</th>
				<td class="layouttable_td_widget informMatters">
					<input type="checkbox" id="checkBefore"/><span style="font-weight: bold;font-size: 14px;">补充告知投保前事项</span><br/>
					<span style="color: red;">注：勾选a-f项将送人工核保</span><br/>
					<input type="checkbox" name="informMatterA" value="1" />a.未如实告知健康情况
					<input type="checkbox" name="informMatterB" value="2" />b.保全项目未如实告知
					<input type="checkbox" name="informMatterC" value="3" />c.变更或取消特别约定
					<input type="checkbox" name="informMatterD" value="4" />d.职业未如实告知
					<input type="checkbox" name="informMatterE" value="5" />e.投被保人关系告知
					<input type="checkbox" name="informMatterF" value="6" />f.新年龄超投保范围
					<input type="checkbox" name="informMatterG" value="7" />g.其他投保前未如实告知情况
				</td>
			</tr>
			<tr id="relationshipTR">
				<th class="layouttable_td_label">是被保险人的（关系）：</th>
				<td class="layouttable_td_widget">
					<sfform:select path="relationship" items="${CodeTable.posTableMap.RELATIONSHIP}" itemLabel="description" itemValue="code"
						cssClass="input_select {required:true}" id="relationship"/>
					<sfform:input path="relationshipDesc" id="relationshipDesc" cssClass="input_text {required:true}" />
				</td>
			</tr>
			<tr>
				<th class="layouttable_td_label" style="vertical-align:top;">补充告知投保前事项描述：</th>
				<td class="layouttable_td_widget">
					<textarea rows="3" cols="40" class="matterBeforeDesc {required:function() {return $('#checkBefore').attr('checked');}}" name="matterBeforeDesc"></textarea></span>
				</td>
			</tr>
			<tr>
				<th class="layouttable_td_label" style="vertical-align:top;">补充告知投保后事项描述：</th>
				<td class="layouttable_td_widget">
					<input type="checkbox" id="checkAfter"/><span style="font-weight: bold;font-size: 14px;">补充告知投保后事项</span><br/>
					<textarea rows="3" cols="40" class="matterAfterDesc {required:function() {return $('#checkAfter').attr('checked');}}" name="matterAfterDesc"></textarea></span>
				</td>
			</tr>
		</tbody>
	</table>
	<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>保单层信息：</legend>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>保单号</th>
						<th>被保人</th>
						<th>投保人</th>
						<th>生效日期</th>
						<th>交至日</th>
						<th>保单状态</th>
					</tr>
				</thead>
				<tbody>
					<tr class="odd_column">
						<td>${acceptance__item_input.policyInfo.policyNo}</td>
						<td>${acceptance__item_input.policyInfo.insuredName}</td>
						<td>${acceptance__item_input.policyInfo.applicantName}</td>
						<td><fmt:formatDate value="${acceptance__item_input.policyInfo.effectDate}" pattern="yyyy-MM-dd"/></td>
						<td><fmt:formatDate value="${acceptance__item_input.policyInfo.payToDate}" pattern="yyyy-MM-dd"/></td>
						<td>${acceptance__item_input.policyInfo.dutyStatusDesc}</td>
					</tr>
				</tbody>
			</table>
		</fieldset>
	</div>
</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
