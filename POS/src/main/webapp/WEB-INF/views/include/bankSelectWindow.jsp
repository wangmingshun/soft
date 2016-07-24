<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>
<script type="text/javascript" language="javascript">
	var $bankCodeToSet = null;
	var $array = null;//存放多个jquery对象，将多个jquery对象同时赋值 edit by wangmingshun
	var callBackFuncForBankSelect = null;
	var initProvince = null;
	var initCity = null;
	/**
	 * 显示银行选择窗口
	 * @param $input 银行代码值需要回写的控件引用，必须为JQUERY对象
	 * @param callback 回调函数，签名为：function(bankCode, bankName)
	 */
	function showBankSelectWindow($input, callback) {
		$("#bankCategory option:first").attr("selected", true);
		if(initProvince && initCity) {
			$("#bankProvince option[value='" + initProvince + "']").attr("selected", true).trigger("change");
		} else {
			$("#bankProvince option:first").attr("selected", true).trigger("change");
		}
		
		$bankCodeToSet = $input;
		callBackFuncForBankSelect = callback;
				
		$("#bankSelectWindowDiv").window("open");
	}
	/**
	 * 显示银行选择窗口 edit by wangmingshun
	 * @param $input 银行代码值需要回写的控件引用，必须为JQUERY对象,可回写多个
	 * @param callback 回调函数，签名为：function(bankCode, bankName)
	 */
	 function showBankSelectWindow01($arr, callback) {
			$("#bankCategory option:first").attr("selected", true);
			if(initProvince && initCity) {
				$("#bankProvince option[value='" + initProvince + "']").attr("selected", true).trigger("change");
			} else {
				$("#bankProvince option:first").attr("selected", true).trigger("change");
			}
			
			$array = $arr;
			callBackFuncForBankSelect = callback;
					
			$("#bankSelectWindowDiv").window("open");
		}
	 
	var bankInitflag = false;
	var bankOptions = null;
	$(function() {
		if(bankInitflag) return;//出现原因不明的重复绑定的问题，加个flag控制
		
		//确定按钮
		$("#bankSelectBtnOK").click(function() {
			if(!$("#bankProvince").val()) {
				$.messager.alert("提示","请选择银行所在省/自治区!");
				return false;
			}
			if(!$("#bankCity").val()) {
				$.messager.alert("提示","请选择银行所在市!");
				return false;
			}
			if(!$("#bankCategory").combobox("getValue")) {
				$.messager.alert("提示","请选择银行类型!");
				return false;
			}
			
			var selectedValue = $("#bankSelect").combobox("getValue");
			var selectedBank = null;
			var bank;
			for(var i = 0; i < bankOptions.length; i++) {
				 bank = bankOptions[i];
				if(bank.bankCode == selectedValue) {
					selectedBank = bank;
				}
			}
			if(!selectedBank) {
				$.messager.alert("提示","请从下拉列表中选择银行!");
				return false;
			}
			if($bankCodeToSet) {
				$bankCodeToSet.val(selectedBank.bankCode);
			}
			//edit by wangmingshun 给一组jquery对象赋值
			if($array) {
				for(var i=0; i < $array.length; i++) {
					$array[i].val(selectedBank.bankCode);
				}
			}
			
			if(callBackFuncForBankSelect) {
				callBackFuncForBankSelect.apply(null, [selectedBank.bankCode, selectedBank.description]);
			}
			
			$("#bankSelectWindowDiv").window("close");
			return false;
		});
		
		//取消按钮
		$("#bankSelectBtnCancel").click(function(){
			$("#bankSelectWindowDiv").window("close");
			return false;
		});
		
		//省变化取省的下属市列表
		$("#bankProvince").change(function() {
			var $this = $(this);
			var $bankCity = $("#bankCity");
			$bankCity.find("option").remove();
			$.post("${ctx}include/queryCityByProvince.do",
					{province : $this.val()},
					function(data) {
						for ( var i = 0; data && data.length && i < data.length; i++) {
							$("<option/>").attr("value", data[i]["CITY_CODE"])
										.html(data[i]["CITY_NAME"])
										.appendTo($bankCity);
						}
						if(initCity) {
							$bankCity.find("option[value='" + initCity + "']").attr("selected", true);
						}
						$bankCity.trigger("change");
					}, "json");
		});
		
		//银行类型和市变化则重新取银行列表
		var retriveBankList = function() {
			var $bankSelect = $("#bankSelect");
			$bankSelect.find("option").remove();
			if ($("#bankCategory").combobox("getValue") && $("#bankCity").val()) {
				$.post("${ctx}include/queryBankList", 
						{
							provinceCode : $("#bankProvince").val(),
							cityCode : $("#bankCity").val(),
							bankCategory : $("#bankCategory").combobox("getValue")
						}, function(data) {
							bankOptions = data;
							$bankSelect.combobox({valueField:'bankCode',
								textField:'description',
								width:300,
								filter : function(q, row){
									return row && row.description && row.description.indexOf(q) > -1;
								},"data":data});
							if(bankOptions && bankOptions.length > 0) {
								$bankSelect.combobox("showPanel");
							}
						}, "json");
			}
		};
		
		$("#bankCity").change(retriveBankList);
		
		$("#bankCategory").combobox({
			filter:function(q,row){
				return row.text.indexOf(q) > -1;
			},
			onChange : retriveBankList
		});
		
		$.post("${ctx}include/queryPrivinceAndCityByBranchCode",
				{branchCode : "${LOGIN_USER_INFO.loginUserUMBranchCode}"},
				function(data){
					if(data) {
						initProvince = data["PROVINCE_CODE"];
						initCity = data["CITY_CODE"];
					}
				}, "json");
		
		bankInitflag = true;
	});
</script>
<div style="display:none;">
	<div id="bankSelectWindowDiv" class="easyui-window" modal="true" closed="true" title="银行选择" style="width: 570px; height: 400px;" collapsible="false" minimizable="false" maximizable="false">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;text-align:left;">
				银行所在地区：
				<select id="bankProvince">
					<c:forEach items="${CodeTable.posTableMap.PROVINCE}" var="item" varStatus="status">
						<option value="${item.code}">${item.description}</option>
					</c:forEach>
				</select>
				省/自治区
				<select id="bankCity"></select>
				市
				<br/>
				银行类型：
				<select id="bankCategory" class="easyui-combobox" style="width:250px">
					<c:forEach items="${CodeTable.posTableMap.BANK_CATEGORY}" varStatus="status" var="item">
						<option value="${item.code}">${item.description}</option>
					</c:forEach>
				</select>
				<br/>
				银行选择：
				<select id="bankSelect" class="easyui-combobox"></select>
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="bankSelectBtnOK">确定</a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" id="bankSelectBtnCancel">取消</a>
			</div>
		</div>
	</div>
</div>
