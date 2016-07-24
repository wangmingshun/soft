<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 客户资料变更 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
	var ignoreSexUnmatch = false; //忽略性别不匹配
	var ignoreBirthdayUnmatch = false; //忽略生日不匹配
	var originIdTypeCode = "${acceptance__item_input.originClientInformationDTO.idTypeCode}";
	var originSexCode = "${acceptance__item_input.originClientInformationDTO.sexCode}";
	var originIdNo = "${acceptance__item_input.originClientInformationDTO.idNo}";
	var originBirthDay = "<fmt:formatDate value="${acceptance__item_input.originClientInformationDTO.birthday}" pattern="yyyy-MM-dd"/>";
	posValidateHandler = function() {
		var $inputs = $("#formTable :input");
		var notEmpty = false;
		$inputs.each(function(idx, content) {
			if ($($inputs.get(idx)).val() != "") {
				notEmpty = true;
			}
		});
		if (!notEmpty) {
			$.messager.alert("校验错误", "请输入变更内容");
			return false;
		}
		//校验身份证号码与性别、生日是否匹配
		var idNo = $("#idNo").val() ? $("#idNo").val() : originIdNo;
		var idTypeCode = $("#idTypeCode").val();
		var newIdNo = $("#idNo").val();
		if (idTypeCode == "01") {
			if (!ignoreSexUnmatch
					&& !validateIdNoMatchesSexCode(idNo, originSexCode)) {
				$.messager.confirm("提示", "身份证号码与性别不匹配，是否继续？", function(yes) {
					if (yes) {
						ignoreSexUnmatch = true;
						$("form[name=itemsInputForm]").submit();
					}
				});
				return false;
			}
			if (!ignoreBirthdayUnmatch
					&& !validateIdNoMatchesBirthday(idNo, originBirthDay)) {
				$.messager.confirm("提示", "身份证号码与生日不匹配，是否继续？", function(yes) {
					if (yes) {
						ignoreBirthdayUnmatch = true;
						$("form[name=itemsInputForm]").submit();
					}
				});
				return false;
			}
			var key = newIdNo.substr(newIdNo.length - 1, newIdNo.length);

			if (key == 'x') {//找到输入是小写字母的ascII码的范围
				$.messager.alert("提示", "身份证号码最后一位不可录入为小写x。");
				return false;
			}
		}
		
		if ($('#isLongidnoValidityDate').attr('checked')==true){
			if($('.idnoValidityDate').val()!=''){
				$.messager.alert("提示", "身份证的有效期勾选为长期时，请不要再录入日期。");
				return false;
			}
		}
		
		//校验客户姓名是否合法 endit by wangmingshun start
		var clientName = $("#clientName").val();
		//可能存在姓名不变更，即不录入姓名的时候不做检查
		if(clientName != '') {
			clientName = clientName.replace(/\s+/g, "");
			if(clientName.length < 2){
				$.messager.alert("提示", "客户姓名不能少于两个字符！！！");
				return false;
			}
		}
		//校验客户姓名是否合法 endit by wangmingshun end
		
		return true;
	};
	$(function() {
		//身份证号码变更，重置忽略信息状态
		$("#idNo").change(function() {
			ignoreSexUnmatch = false;
			ignoreBirthdayUnmatch = false;
		});
		$(".idTypeCode").change(function() {
			if ($(".idTypeCode").val()) {
				$("#idTypeCode").val($(".idTypeCode").val());
			} else {
				$("#idTypeCode").val(originIdTypeCode);
			}
		}).trigger("change");

		$("#idNo")
				.blur(
						function() {
							var newIdTypeCode = $(".idTypeCode option:selected")
									.val();
							if (newIdTypeCode == 1) {
								var key = $(this).val().substr(
										$(this).val().length - 1,
										$(this).val().length);

								if (key == 'x') {//找到输入是小写字母的ascII码的范围
									$(this).val(
											$(this).val().substr(0,
													$(this).val().length - 1)
													+ 'X');
								}
							}
						});
		
		//隐藏 下拉框中的出生证 edit by wangmingshun
		$("#idType option[value='12']").remove();
	});
	
	//校验客户姓名是否合法 endit by wangmingshun start
	/*
	function clientNameValidate(){
		var clientName = $("#clientName").val();
		//可能存在客户姓名不变更的情况
		if(clientName != ''){
			var pattern = /^([\u2E80-\uFE4F]{2,1000}|[a-zA-Z]{3,1000})$/g;
			clientName = clientName.replace(/\s+/g, "");
			if(!pattern.test(clientName)){
				$.messager.alert("警告", "客户姓名不能少于2个汉字或者3个字母！！！！请重新输入客户姓名！");
				return false;
			}
		}
		return true;
	}
	*/
	//校验客户姓名是否合法 endit by wangmingshun end
</script>
</sf:override>
<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<table class="layouttable" id="formTable">
			<tbody>
				<tr>
					<th class="layouttable_td_label">原客户姓名：</th>
					<td class="layouttable_td_widget">
					${acceptance__item_input.originClientInformationDTO.clientName}</td>
					<th class="layouttable_td_label">原证件类型：</th>
					<td class="layouttable_td_widget"><c:forEach var="item"
						items="${CodeTable.posTableMap.ID_TYPE}">
						<c:if
							test="${item.code eq acceptance__item_input.originClientInformationDTO.idTypeCode}">
								${item.description}
							</c:if>
					</c:forEach></td>
					<th class="layouttable_td_label">原证件号码：</th>
					<td class="table_column">
					${acceptance__item_input.originClientInformationDTO.idNo}</td>
				</tr>
				<tr>
					<th class="layouttable_td_label">变更客户姓名：</th>
					<td class="layouttable_td_widget"><sfform:input
						path="clientInformationDTO.clientName"
						cssClass="input_text ${empty acceptance__item_input.originClientInformationDTO.clientName ? 'required' : ''}" id="clientName"/>
					</td>
					<th class="layouttable_td_label">变更证件类型：</th>
					<td class="layouttable_td_widget"><sfform:select
						path="clientInformationDTO.idTypeCode" id="idType"
						cssClass="input_select idTypeCode ${empty acceptance__item_input.originClientInformationDTO.idTypeCode ? 'required' : ''}">
						<sfform:option value="">请选择</sfform:option>
						<sfform:options items="${CodeTable.posTableMap.ID_TYPE}"
							itemLabel="description" itemValue="code" />
					</sfform:select> <input type="hidden" id="idTypeCode" /></td>
					<th class="layouttable_td_label">变更证件号码：</th>
					<td class="table_column"><sfform:input id="idNo"
						path="clientInformationDTO.idNo"
						cssClass="input_text {idNo:{target:'#idTypeCode',value:'01'}${empty acceptance__item_input.originClientInformationDTO.idNo ? ',required:true' : ''}}" />
					</td>
				</tr>
				<tr>
					<th class="layouttable_td_label">原婚姻状况：</th>
					<td class="table_column"><c:forEach var="item"
						items="${CodeTable.posTableMap.MARRIAGE}">
						<c:if
							test="${item.code eq acceptance__item_input.originClientInformationDTO.marriageCode}">
								${item.description}
							</c:if>
					</c:forEach></td>
					<th class="layouttable_td_label">原户籍：</th>
					<td class="table_column">
					${acceptance__item_input.originClientInformationDTO.registerPlace}
					</td>
					<th class="layouttable_td_label">原国籍：</th>
					<td class="table_column"><c:forEach var="item"
						items="${CodeTable.posTableMap.COUNTRY}">
						<c:if
							test="${item.code eq acceptance__item_input.originClientInformationDTO.countryCode}">
								${item.description}
							</c:if>
					</c:forEach></td>
				</tr>
				<tr>
					<th class="layouttable_td_label">变更婚姻状况：</th>
					<td class="table_column"><sfform:select
						path="clientInformationDTO.marriageCode" cssClass="input_select">
						<sfform:option value="">请选择</sfform:option>
						<sfform:options items="${CodeTable.posTableMap.MARRIAGE}"
							itemValue="code" itemLabel="description" />
					</sfform:select></td>
					<th class="layouttable_td_label">变更户籍：</th>
					<td class="table_column"><sfform:input
						path="clientInformationDTO.registerPlace" cssClass="input_text" />
					</td>
					<th class="layouttable_td_label">变更国籍：</th>
					<td class="table_column"><sfform:select
						path="clientInformationDTO.countryCode" cssClass="input_select">
						<sfform:option value="">请选择</sfform:option>
						<sfform:options items="${CodeTable.posTableMap.COUNTRY}"
							itemLabel="description" itemValue="code" />
					</sfform:select></td>
				</tr>
				<tr>
					<th class="layouttable_td_label">原民族：</th>
					<td class="table_column"><c:forEach var="item"
						items="${CodeTable.posTableMap.NATION}">
						<c:if
							test="${item.code eq acceptance__item_input.originClientInformationDTO.nationCode}">
								${item.description}
							</c:if>
					</c:forEach></td>
					<th class="layouttable_td_label">原工作单位：</th>
					<td class="table_column" colspan="3">
					${acceptance__item_input.originClientInformationDTO.workUnit}</td>
				</tr>
				<tr>
					<th class="layouttable_td_label">变更民族：</th>
					<td class="table_column"><sfform:select
						path="clientInformationDTO.nationCode" cssClass="input_select">
						<sfform:option value="">请选择</sfform:option>
						<sfform:options items="${CodeTable.posTableMap.NATION}"
							itemValue="code" itemLabel="description" />
					</sfform:select></td>
					<th class="layouttable_td_label">变更工作单位：</th>
					<td class="table_column" colspan="3"><sfform:input
						path="clientInformationDTO.workUnit" cssClass="input_text"
						size="65" /></td>
				</tr>
				<tr>
				  	<th class="layouttable_td_label">证件有效期截止日：</th>
					<td class="layouttable_td_widget">
					<sfform:input
						path="clientInformationDTO.idnoValidityDate"
						cssClass="input_text idnoValidityDate"
						cssStyle="width:80px;"
						onfocus="WdatePicker({skin:'whyGreen',startDate:'${acceptance__item_input.applyDate}}',minDate:'${acceptance__item_input.applyDate}}',isShowToday:false});" />
						长期							
						<sfform:checkbox path="clientInformationDTO.isLongidnoValidityDate"  id="isLongidnoValidityDate" cssClass="isLongidnoValidityDate"/>
						
					</td>
				</tr>
			</tbody>
		</table>
	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>