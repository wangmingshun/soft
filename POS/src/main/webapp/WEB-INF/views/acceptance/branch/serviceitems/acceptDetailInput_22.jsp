<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 受益人变更 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		var hasDelElements = {"1":"N", "2":"N", "3":"N"};
		$(function(){
			//新增受益人
			$("#add").click(function(){
				//克隆第一个模板元素
				var $cln = $("#tr0").clone(true);
				$cln.find("em.error").remove();
				$cln.find(":input").val("").attr("readOnly", false);
				$cln.css("display", "");
				$cln.removeClass("ignore");
				$cln.find(".benefitType").val($("[name='benefitType']:checked").val());
				$cln.attr("title", "new");
				$cln.find(".selectRow").attr("checked", false);
				$cln.find(".origin").remove();
				$cln.appendTo($("#benefDataTable tbody"));
				updateIndex();
				initBene("add");//edit by wangmingshun
				return false;
			});
			//删除受益人
			$("#remove").click(function(){
				var $checked = $("#benefDataTable :checkbox:checked:visible");
				if($checked.length == 0){
					$.messager.alert("提示", "请选择要删除的对象");
					return false;
				}
				var $chkbox, $tr;
				$checked.each(function(idx, content){
					$chkbox = $($checked[idx]);
					$tr = $chkbox.closest("tr");
					if($tr.attr("title") != "new") {
						//删除一行原本就存在的，认为做了变更
						hasDelElements[$tr.find(".benefitType").val()] = "Y";
					}
					if($("#benefDataTable tbody tr").length == 1) {
						$tr.find(":input").val("");
						$tr.css("display", "");
						$tr.attr("title", "new");
						$tr.removeClass("ignore");
						$tr.find(".benefitType").val($("[name='benefitType']:not(:checked)").val());
						$tr.find(".selectRow").attr("checked", false);
					} else {
						$tr.remove();
					}
				});
				updateIndex();
				initBene();//edit by wangmingshun
				return false;
			});
			$("[name='benefitType']").change(function(){
				updateIndex();
			});
			$(".relationship").live("focusout click", function(){
				//这里貌似jquery的live绑定select控件的change事件有BUG，在IE下会报错，用focusout和click两个事件先凑合着用
				var $this = $(this);
				var $relationDesc = $this.closest("tr").find(".relationDesc");
				if($this.val() == "99") {
					$relationDesc.attr("disabled", false);
				} else {
					$relationDesc.attr("disabled", true);
				}
			});
			pageInitialize();
		});
		//更新元素索引，包括绑定名称等
		function updateIndex() {
			var $trArr = $("#benefDataTable tbody tr");
			var $tr, $iptArr, $ipt, index, name, id, length;
			var benefitType = $("[name='benefitType']:checked").val();
			var trLabel = 1;
			$trArr.each(function(idx, content){
				var $tr = $($trArr[idx]);
				index = $tr.attr("id").replace(/tr/g, "");
				if(index != idx) {
					$iptArr = $tr.find(":input");
					$iptArr.each(function(idx1, conent){
						$ipt = $($iptArr[idx1]);
						name = $ipt.attr("name");
						id = $ipt.attr("id");						
						var  vidx=idx+"''";	
						$ipt.attr("name", name.replace(/\d/, idx));
						$ipt.attr("id", id.replace(/\d/, idx));						
					});
					var cls = $tr.find(".idno").attr("class");
					var idxBegin = cls.indexOf("benefInfoIdType") + "benefInfoIdType".length;
					var idxEnd = cls.indexOf("'", idxBegin);
					cls = cls.substring(0, idxBegin) + idx + cls.substr(idxEnd);
					
					$tr.find(".idno").attr("class", cls);
					$tr.attr("id", "tr" + idx);
					/*为新增的行添加onclick事件 edit by wangmingshun start*/
					$tr.removeAttr("onclick");
					if($tr.find(".benefitType").val() == '2') {
						/*
						var onclick = document.createAttribute("onclick");
						var fn = "beneficiaryInfo('" + idx + "')";
						$tr.get(0).attributes.setNamedItem(onclick);
						$tr.get(0).setAttribute("onclick", fn);
						*/
						//ie不兼容问题，通过setAttribute设置的onclick无效，所以通过以下方法
						//jquery的click事件在这会报错，所以转换为js对象进行处理
						$tr.get(0).onclick = function() {
							beneficiaryInfo(idx);
						};
					}
					/*为新增的行添加onclick事件 edit by wangmingshun end*/
				}
				if($tr.find(".benefitType").val() == benefitType) {
					$tr.find("td:eq(1)").html(trLabel++);
					$tr.find(":input").removeClass("ignore");
					$tr.find(".relationship").trigger("focusout");
					$tr.show();
				} else {
					$tr.find(":input").removeClass("ignore").addClass("ignore");
					$tr.hide();
				}
				if($tr.find(".beneficiaryNo").val() == "B00000000001") {
					$tr.find(":input:not(.selectRow)").attr("readOnly", true).removeClass("ignore").addClass("ignore").unbind("click").unbind("change").unbind("focus").end().find("select");
				}
			});
			$("#beneficiaryInfoListSize").val($trArr.length);
		}
		function pageInitialize() {
			$("[name='benefitType']").trigger("change");
			$(".relationship").trigger("focusout");
			initBene("init");//edit by wangmingshun
			$("#fuck").val("2");//默页面载入时，受益人状态为2
			$("#isPwm").trigger("change");//edit by wangmingshun
			pwmBlurInit();//页面加载完成后绑定onblur事件
			isShowPwmCheckBox();//控制是否显示类信托checkbox  
		}
		posValidateHandler = function(){
			/*判断填入的证件有效期是否符合要求 edit by wangmingshun start*/
			var applyDate="${acceptance__item_input.applyDate}";
			var valid = true;
			var isLong = true;
			var nameValid = true;
			var start=new Date(applyDate.replace("-", "/").replace("-", "/")); 
			var $idValidDate = $(".idValidDate");
			var $isLongDate = $(".isLongDate");
			var $beneficiaryName = $(".beneficiaryName");
			$idValidDate.each(function(index){
				if($isLongDate[index].checked){
					if($idValidDate[index].value != ''){
						isLong = false;
					}
				}
				var end=new Date($idValidDate[index].value.replace("-", "/").replace("-", "/"));
				if(start > end){
					valid = false;
				}
				var name = $beneficiaryName[index].value;
				if(name != ''){
					if(!nameValidate(name)){
						nameValid = false;  
					}
				}
			});
			if(!isLong){
				$.messager.alert("提示", "存在身份证的有效期勾选为长期，请不要再录入日期。");
				return false;
			}
			if(!valid){
				$.messager.alert("校验错误", "存在证件有效期小于客户保全申请日期的记录，无法完成操作！");
				return false;
			}
			if(!nameValid){
				$.messager.alert("提示", "客户姓名不能少于两个字符！！！");
				return false;
			}
			/*
			var nameValid = true;
			var $beneficiaryName = $(".beneficiaryName");
			$beneficiaryName.each(function(index){
				var name = $beneficiaryName[index].value;
				if(name != ''){
					if(!nameValidate(name)){
						nameValid = false;  
					}
				}
			});
			if(!nameValid){
				$.messager.alert("提示", "客户姓名不能少于两个字符！！！");
				return false;
			}
			*/
			/*判断填入的证件有效期是否符合要求 edit by wangmingshun end*/
			
			/*提交的时候当类信托业务没有勾选，则将相关的dto属性设置为空  edit by wangmingshun start*/
			var bf = $("#isPwm").attr("checked");
			var $trArr = $("#benefDataTable tbody tr");
			//当勾选长期后，提交的时候将有效期自动设置为9999-1-1
			$trArr.each(function(idx){
				var $tr = $($trArr[idx]);
				if($tr.find(".isLongDate").attr("checked") == true) {
					$tr.find(".idValidDate").val("9999-1-1");
					$tr.find(".isLongDate").attr("checked", false);
				}
			});
			
			var pwmValid = true;
			//var accountInfo = true;
			var pwmStr = "";
			if(bf == false) {
				$trArr.each(function(idx){
					$("#claimPremDrawType" + idx).val("");
					$("#promiseRate" + idx).val("");
					$("#promiseAge" + idx).val("");
					$("#accountOwner" + idx).val("");
					$("#accountNo" + idx).val("");
					$("#bankCode" + idx).val("");
					$("#bankName" + idx).val("");
				});
			} else { //当勾选了之后循环校验是否存在未录入项，存在则给出提示，并且终止提交操作
				$trArr.each(function(idx){
					var $tr = $($trArr[idx]);
					var index = 0;
					if($tr.find(".benefitType").val() == '2') {
						var name = $tr.find(".beneficiaryName").val();
						var claimPremDrawType = $("#claimPremDrawType" + idx).val();
						if(claimPremDrawType == "") {
							pwmStr += "[" + name + "]身故保险金领取方式未选择！";
							pwmValid = false;
						} else {
							var promiseRate = $("#promiseRate" + idx).val();
							var promiseAge = $("#promiseAge" + idx).val();
							if(promiseRate == "") {
								index++;
								pwmStr += "[" + name + "]约定比例未填写！";
								pwmValid = false;
							} else {
								if(promiseRate > 100 || promiseRate < 0) {
									index++;
									pwmStr += "[" + name + "]约定比例范围必须大于0且不能超过100！";
									pwmValid = false;
								}
							}
							if(promiseAge == "") {
								if(claimPremDrawType != '03') {
									if(index == 0) {
										index++;
										pwmStr += "[" + name + "]约定年龄未填写！";
									} else {
										index++;
										pwmStr += "约定年龄未填写！";
									}
									pwmValid = false;
								}
							}
						}
						//提示银行信息
						//勾选就必须校验
						if($("#accountChecked" + idx).val() == "Y") {
							var accountOwner = $("#accountOwner" + idx).val();
							var accountNo = $("#accountNo" + idx).val();
							var bankCode = $("#bankCode" + idx).val();
							if(accountNo == "" || bankCode == "") {
								if(index == 0) {
									pwmStr += "[" + name + "]";
								}
								pwmStr += "<br/>授权信息(注：一旦勾选,就必须录入.取消勾选,提交后,原来存在的授权信息会被清空.)：<br/>";
								pwmValid = false;
							}
							if(accountNo == "") {
								pwmStr += "账号未填写！";
							}
							if(bankCode == "") {
								pwmStr += "银行没有选择！";
							}
						} else {
							//没有勾选，清空授权信息
							$("#accountOwner" + idx).val("");
							$("#accountNo" + idx).val("");
							$("#bankCode" + idx).val("");
							$("#bankName" + idx).val("");
						}
					}
				});
			}
			if(!pwmValid) {
				pwmStr += "<br/>请录入或者取消类信托，方可进行下一步操作！";
				$.messager.alert("提示", pwmStr);
				return false;
			}
			/*提交的时候当类信托业务没有勾选，则将相关的dto属性设置为空  edit by wangmingshun end*/
			
			var $trs = $("#benefDataTable tbody tr:visible");
			var productCode = $("#productCode").val();
			if($("[name='benefitType']:checked").val() == "1") {
				if($trs.length > 1) {
					$.messager.alert("提示", "生存金受益人仅允许存在一个.");
					return false;
				}
				//下面是险种必须是投保人
				var productCodeApp = 'CBAN_BR1,CBAN_CR1,CIAN_JR1,CBAN_GN1,CIAN_KN1';
				//录入的生存金受益人信息
				var beneficiaryName=$($trs[0]).find(".beneficiaryName").val();
				var sexCode=$($trs[0]).find(".sexCode").val();
				var idType=$($trs[0]).find(".idType").val();
				var idno=$($trs[0]).find(".idno").val();
				var birthdate=$($trs[0]).find(".birthdate").val();
				//投保人
				var clientNameApp='${acceptance__item_input.clientInfoApp.clientName}';
				var sexCodeApp='${acceptance__item_input.clientInfoApp.sexCode}';
				var idTypeCodeApp='${acceptance__item_input.clientInfoApp.idTypeCode}';
				var idNoApp='${acceptance__item_input.clientInfoApp.idNo}';
				var birthdayApp='<fmt:formatDate value="${acceptance__item_input.clientInfoApp.birthday}" pattern="yyyy-MM-dd"/>';	
				//被保险人
				var clientNameIns='${acceptance__item_input.clientInfoIns.clientName}';
				var sexCodeIns='${acceptance__item_input.clientInfoIns.sexCode}';
				var idTypeCodeIns='${acceptance__item_input.clientInfoIns.idTypeCode}';
				var idNoIns='${acceptance__item_input.clientInfoIns.idNo}';
				var birthdayIns='<fmt:formatDate value="${acceptance__item_input.clientInfoIns.birthday}" pattern="yyyy-MM-dd"/>';
				if(productCodeApp.indexOf(productCode)>-1){				
					if(beneficiaryName!=clientNameApp||sexCode!=sexCodeApp||idType!=idTypeCodeApp||idno!=idNoApp||birthdate!=birthdayApp){
						$.messager.alert("提示", "此险种生存金受益人必须与投保人一致");
						return false;						
					}					
				}else if(productCode=='CEAN_AN1'||productCode=='CIAN_LR1'||productCode=='CIAN_GR1'||productCode=='CIED_PR1'){
					if(!((beneficiaryName==clientNameApp&&sexCode==sexCodeApp&&idType==idTypeCodeApp&&idno==idNoApp&&birthdate==birthdayApp)
							||(beneficiaryName==clientNameIns&&sexCode==sexCodeIns&&idType==idTypeCodeIns&&idno==idNoIns&&birthdate==birthdayIns))){
						$.messager.alert("提示", "此险种生存金受益人必须与投保人或被保险人一致");
						return false;						
					}						
				}else{
					if(beneficiaryName!=clientNameIns||sexCode!=sexCodeIns||idType!=idTypeCodeIns||idno!=idNoIns||birthdate!=birthdayIns){
						$.messager.alert("提示", "此险种生存金受益人必须与被保险人一致");
						return false;						
					}					
				}
			}
			var sum, tmpSeq, i, j, $item;
			var benefSeqArr = [];
			$trs.each(function(idx, content) {
				tmpSeq = $($trs[idx]).find(".benefitSeq").val();
				if(benefSeqArr.indexOf(tmpSeq) == -1) {
					benefSeqArr.push(tmpSeq);
				}
			});
			//对收益顺序进行排序
			for(i = 0; i < benefSeqArr.length; i++) {
				for(j = i; j < benefSeqArr.length; j++) {
					if(benefSeqArr[i] > benefSeqArr[j]) {
						tmpSeq = benefSeqArr[i];
						benefSeqArr[i] = benefSeqArr[j];
						benefSeqArr[j] = tmpSeq;
					}
				}
			}
			//排序后的收益顺序应该与其所在的索引一致，否则就是受益顺序存在缺位的情况
			for(i = 0; i < benefSeqArr.length; i++) {
				if(benefSeqArr[i] != (i + 1)) {
					$.messager.alert("提示", "受益顺序必须连续，必须存在受益顺序为" + (benefSeqArr[i] - 1) + "的受益人才能录入受益顺序为" + benefSeqArr[i] + "的受益人。");
					return false;
				}
			}
			for(i = 0; i < benefSeqArr.length; i++) {
				tmpSeq = benefSeqArr[i];
				sum = 0;
				$trs.each(function(idx, content) {
					$item = $($trs[idx]);
					if(tmpSeq == $item.find(".benefitSeq").val()) {
						sum += parseInt($item.find(".benefitRatePercent").val());
					}
				});
				if(sum != 100) {
					$.messager.alert("提示", "受益顺序为[" + tmpSeq + "]受益人的受益比例之和必须为100%，目前为：" + sum + "%。");
					return false;
				}
			}
			//校验必须至少进行了一类变更，删除、新增或修改
			if(hasDelElements[$("[name='benefitType']:checked").val()] == "N") { //未进行删除
				var hasAddElements = false;
				$trs.each(function(idx, content) {
					if(!hasAddElements && $($trs[idx]).attr("title") == "new" && $($trs[idx]).find(".benefitType").val() == $("[name='benefitType']:checked").val()) {
						hasAddElements = true;
					}
					/*类信托状态是否改变*/
					var isPwmOrigin = null;
					if($("#isPwmOrigin").val() == 'true') {
						isPwmOrigin = true;
					} else if($("#isPwmOrigin").val() == 'false') {
						isPwmOrigin = false;
					}
					if($("#isPwm").attr("checked") != isPwmOrigin) {
						hasAddElements = true;
					}
				});
				if(!hasAddElements) {	//未新增,类信托状态也没改变
					//判断是否修改
					var $tr;
					var hasModifiedElements = false;
					$trs.each(function(idx, content) {
						$tr = $($trs[idx]);
						if(!hasModifiedElements && $tr.find(".beneficiaryNameOrigin").length > 0) {
							hasModifiedElements |= ($tr.find(".beneficiaryName").val() != $tr.find(".beneficiaryNameOrigin").val());
							hasModifiedElements |= ($tr.find(".sexCode").val() != $tr.find(".sexCodeOrigin").val());
							hasModifiedElements |= ($tr.find(".idType").val() != $tr.find(".idTypeOrigin").val());
							hasModifiedElements |= ($tr.find(".idno").val() != $tr.find(".idnoOrigin").val());
							//edit by wangmingshun 
							hasModifiedElements |= ($tr.find(".claimPremDrawType").val() != $tr.find(".claimPremDrawTypeOrigin").val());
							hasModifiedElements |= ($tr.find(".promiseRate").val() != $tr.find(".promiseRateOrigin").val());
							hasModifiedElements |= ($tr.find(".promiseAge").val() != $tr.find(".promiseAgeOrigin").val());
							hasModifiedElements |= ($tr.find(".idValidDate").val() != $tr.find(".idValidDateOrigin").val());
							hasModifiedElements |= ($tr.find(".isLongDate").attr("checked") == true);
							hasModifiedElements |= ($tr.find(".countryCode").val() != $tr.find(".countryCodeOrigin").val());
							hasModifiedElements |= ($tr.find(".accountOwner").val() != $tr.find(".accountOwnerOrigin").val());
							hasModifiedElements |= ($tr.find(".accountNo").val() != $tr.find(".accountNoOrigin").val());
							hasModifiedElements |= ($tr.find(".bankCode").val() != $tr.find(".bankCodeOrigin").val());
							
							hasModifiedElements |= ($tr.find(".birthdate").val() != $tr.find(".birthdateOrigin").val());
							hasModifiedElements |= ($tr.find(".relationship").val() != $tr.find(".relationshipOrigin").val());
							if(!hasModifiedElements && $tr.find(".relationship").val() == "99") {
								hasModifiedElements |= ($tr.find(".relationDesc").val() != $tr.find(".relationDescOrigin").val());
							}
							hasModifiedElements |= ($tr.find(".benefitRatePercent").val() != $tr.find(".benefitRatePercentOrigin").val());
							hasModifiedElements |= ($tr.find(".benefitSeq").val() != $tr.find(".benefitSeqOrigin").val());
							hasModifiedElements |= ($tr.find(".phone").val() != $tr.find(".phoneOrigin").val());
							hasModifiedElements |= ($tr.find(".address").val() != $tr.find(".addressOrigin").val());
						}
					});
					if(!hasModifiedElements) {
						$.messager.alert("提示", "没有任何信息存在变更！");
						return false;
					}
				}
			}
			return true;
		};
		
		//姓名校验
		function nameValidate(name){
			name = name.replace(/\s+/g, "");
			if(name.length < 2){
				return false;
			}
			return true;
		}
		 
		/* 类信托业务处理  edit by wangmingshun start */
		//设置类信托业务  调用隐藏域中的值来初始化div层 
		function beneficiaryInfo(index, type) {
			/*
			var arr = [
				<c:forEach items="${acceptance__item_input.beneficiaryInfoList}" var="item" varStatus="status">
					<c:if test="${status.count > 1}">, </c:if>
					'${item.benefInfo.beneficiaryName}'
				</c:forEach>
			]; */
			//alert(index)
			//alert(arr[index])
			var name = $("#beneficiaryName" + index).val(); //获取对应行的受益人姓名
			var claimPremDrawType = $("#claimPremDrawType" + index).val(); //理赔方式
			var promiseRate = $("#promiseRate" + index).val();	//约定比例
			var promiseAge = $("#promiseAge" + index).val();	//约定年龄
			var accountOwner = $("#accountOwner" + index).val(); //银行卡户主
			var accountNo = $("#accountNo" + index).val();	//银行卡账号
			var bankCode = $("#bankCode" + index).val();	//银行编号
			var bankName = $("#bankName" + index).val();	//银行名称
			var accountChecked = $("#accountChecked" + index).val(); //授权信息是否被选中
			//var transferGrantFlag = $("#transferGrantFlag" + index).val();	//转账授权标志 Y/N
			$("#beneName").html(name);	//设置div受益人
			$("#bTrIndex").val(index);	//div隐藏属性，记录当前操作的受益人所在行索引
			initIsPwmDiv();	//将类信托div初始化
			//初始化公用div层银行信息
			$("#accountOwner").val("");
			$("#accountNo").val("");
			$("#bankCode").val("");
			$("#bankName").html("");
			$("#showAccountInfoDiv").attr("checked", false);
			$("#accountInfoDiv").hide();
			
			//重新赋值到对应的公用div
			if(type == "init") {
				if(accountOwner != null && accountOwner != "" && accountNo != null && accountNo != "" && bankCode != null && bankCode != "") {
					$("#showAccountInfoDiv").attr("checked", true);
					$("#accountInfoDiv").show();
					$("#accountOwner" + index).val(name);
					$("#accountOwner").val(name);	//受益人账户姓名默认为受益人姓名且不可修改
					$("#accountChecked" + index).val("Y");
					$("#accountNo").val(accountNo);
					$("#bankCode").val(bankCode);
					$("#bankName").html(bankName);
				}
			} else {
				if(accountChecked == "Y") {
					$("#showAccountInfoDiv").attr("checked", true);
					$("#accountInfoDiv").show();
					$("#accountOwner" + index).val(name);
					$("#accountOwner").val(name);	//受益人账户姓名默认为受益人姓名且不可修改
					$("#accountNo").val(accountNo);
					$("#bankCode").val(bankCode);
					$("#bankName").html(bankName);
				}
			}
			
			if(claimPremDrawType == '02') {
				$("#bOne").attr("checked", true);
				$("#oneDiv").show();
				$("#bOneAge").val(promiseAge);
				$("#bOnePercent").val(promiseRate);
				if(promiseRate == "" || promiseAge == "") {
					$("#bOne").trigger("change");
				}
				//历史记录保存到隐藏域中
				if(promiseRate != "") {
					$("#onePercent" + index).val(promiseRate);
				}
				if(promiseAge != "") {
					$("#oneAge" + index).val(promiseAge);
				}
			} 
			if(claimPremDrawType == '03') {
				$("#bTwo").attr("checked", true);
				$("#twoDiv").show();
				$("#bTwoPercent").val(promiseRate);
				if(promiseRate == "") {
					$("#bTwo").trigger("change");
				}
				//历史记录保存到隐藏域中
				if(promiseRate != "") {
					$("#twoPercent" + index).val(promiseRate);
				}
			} 
			if(claimPremDrawType == '04') {
				$("#bThree").attr("checked", true);
				$("#threeDiv").show();
				$("#bThreeAge").val(promiseAge);
				$("#bThreePercent").val(promiseRate);
				if(promiseRate == "" || promiseAge == "") {
					$("#bThree").trigger("change");
				}
				//历史记录保存到隐藏域中
				if(promiseRate != "") {
					$("#threePercent" + index).val(promiseRate);
				}
				if(promiseAge != "") {
					$("#threeAge" + index).val(promiseAge);
				}
			} 
		}
		
		//初始化类信托div层
		function initIsPwmDiv(type) {
			if("one" == type) {
				$("#oneDiv").show();
				
				$("#bTwo").attr("checked", false);
				$("#bTwoPercent").val("");
				$("#twoDiv").hide();
				
				$("#bThree").attr("checked", false);
				$("#bThreeAge").val("");
				$("#bThreePercent").val("");
				$("#threeDiv").hide();
			} else if("two" == type) {
				$("#bOne").attr("checked", false);
				$("#bOneAge").val("");
				$("#bOnePercent").val("");
				$("#oneDiv").hide();
				
				$("#twoDiv").show();
				
				$("#bThree").attr("checked", false);
				$("#bThreeAge").val("");
				$("#bThreePercent").val("");
				$("#threeDiv").hide();
				
			} else if("three" == type) {
				$("#bOne").attr("checked", false);
				$("#bOneAge").val("");
				$("#bOnePercent").val("");
				$("#oneDiv").hide();
				
				$("#bTwo").attr("checked", false);
				$("#bTwoPercent").val("");
				$("#twoDiv").hide();
				
				$("#threeDiv").show();
			} else {
				$("#bOne").attr("checked", false);
				$("#bOneAge").val("");
				$("#bOnePercent").val("");
				$("#oneDiv").hide();
				
				$("#bTwo").attr("checked", false);
				$("#bTwoPercent").val("");
				$("#twoDiv").hide();
				
				$("#bThree").attr("checked", false);
				$("#bThreeAge").val("");
				$("#bThreePercent").val("");
				$("#threeDiv").hide();
			}
		}
		
		/*
		 * 类信托 div层
		 * 1、页面载入和删除某个受益人， 显示第一个身故受益人的信息
		 * 2、添加的时候 ，显示添加的受益人信息
		 */
		function initBene(type) {
			//判断添加的时候是否是类信托业务
			if($("#isPwm").attr("checked") == true) {
				var $trArr = $("#benefDataTable tbody tr");
				$trArr.each(function(idx){
					var $tr = $($trArr[idx]);
					var beneType = $tr.find(".benefitType").val();
					if(beneType == '2' && type == "init"){
						//页面载入的时候初始化方法
						beneficiaryInfo(idx, type);
					} else if(beneType == '2' && type == null){
						//删除用户的方法
						beneficiaryInfo(idx);
						return false;	//找到第一个受益人即结束循环
					} else if(beneType == '2' && type == "add"){
						beneficiaryInfo(idx);	//添加的时候 ，循环到最后一个，即为新添加的
					} else {
						$("#beneName").html("");
						initIsPwmDiv();
					}
				});
			}
		}
		
		//受益人性质改变 radio change事件处理
		function beneChange(type){
			if($(type).val() == '2' || $(type).val() == "") {
				$("#fuck").val("2");//记录当前受益人性质
				$("#pwmSpan").show();
				if($("#isPwm").attr("checked") == true) {
					$("#benefitDiv").show();
				} else {
					$("#benefitDiv").hide();
				}
			} else {
				$("#benefitDiv").hide();
				$("#fuck").val("fuck");
				$("#pwmSpan").hide();
			}
		}
		
		//类信托业务checkbox change事件处理
		$("#isPwm").live("change", function(){
			if($("#fuck").val() == '2') {
				if($("#isPwm").attr("checked") == true) {
					$("#benefitDiv").show();
					initBene();
				} else {
					$("#benefitDiv").hide();
				}
			} else {
				$("#benefitDiv").hide();
			}
		});
		
		//由于是3选一，所以一旦选中其中一个另外两个必须取消选择，并将其对应的div隐藏
		//分期领取至约定年龄 checkbox change事件
		$("#bOne").live("change", function(){
			var index = $("#bTrIndex").val();
			initIsPwmDiv("one");	//首先初始化为空值
			//dto中的值首先设置为null
			initDTO(index);
			if($("#bOne").attr("checked") == true) {
				//查看隐藏域中是否存在值，如果存在则显示到对应的input中，并更新dto的属性值
				var onePercent = $("#onePercent" + index).val();
				var oneAge = $("#oneAge" + index).val();
				if(onePercent != "" && onePercent != null) {
					$("#bOnePercent").val(onePercent);
					$("#promiseRate" + index).val(onePercent);
				}
				if(oneAge != "" && oneAge != null) {
					$("#bOneAge").val(oneAge);
					$("#promiseAge" + index).val(oneAge);
				}
				//对应的隐藏域的  身故金领取方式设置为"02"
				$("#claimPremDrawType" + index).val("02");
			} else {
				$("#oneDiv").hide();
			}
		});
		
		//分期领取至身故保险金本息领取完 checkbox change事件
		$("#bTwo").live("change", function(){
			var index = $("#bTrIndex").val();
			initIsPwmDiv("two");	//首先初始化为空值
			//dto中的值首先设置为null
			initDTO(index);
			if($("#bTwo").attr("checked") == true) {
				//查看隐藏域中是否存在值，如果存在则显示到对应的input中，并更新dto的属性值
				var twoPercent = $("#twoPercent" + index).val();
				if(twoPercent != "" && twoPercent != null) {
					$("#bTwoPercent").val(twoPercent);
					$("#promiseRate" + index).val(twoPercent);
				}
				//对应的隐藏域的  身故金领取方式设置为"03"
				$("#claimPremDrawType" + index).val("03");
			} else {
				$("#twoDiv").hide();
			}
		});
		
		//自约定年龄起分期领取 checkbox change事件
		$("#bThree").live("change", function(){
			var index = $("#bTrIndex").val();
			initIsPwmDiv("three");	//首先初始化为空值
			//dto中的值首先设置为null
			initDTO(index);
			if($("#bThree").attr("checked") == true) {
				//查看隐藏域中是否存在值，如果存在则显示到对应的input中，并更新dto的属性值
				var threePercent = $("#threePercent" + index).val();
				var threeAge = $("#threeAge" + index).val();
				if(threePercent != "" && threePercent != null) {
					$("#bThreePercent").val(threePercent);
					$("#promiseRate" + index).val(threePercent);
				}
				if(threeAge != "" && threeAge != null) {
					$("#bThreeAge").val(threeAge);
					$("#promiseAge" + index).val(threeAge);
				}
				//对应的隐藏域的 身故金领取方式设置为"04"
				$("#claimPremDrawType" + index).val("04");
			} else {
				$("#threeDiv").hide();
			}
		});
		
		function initDTO(index) {
			$("#claimPremDrawType" + index).val("");
			$("#promiseRate" + index).val("");
			$("#promiseAge" + index).val("");
		}
		
		function pwmBlurInit() {
			//通过div层的信息设置对应受益人的 类信托业务信息 通过onblur事件来处理
			$("#bOnePercent").blur(function(){
				var index = $("#bTrIndex").val();
				var one = $("#bOnePercent").val();
				$("#promiseRate" + index).val(one);	//设置隐藏域dto的约定比例
				//隐藏域历史记录
				$("#onePercent" + index).val(one);
			});
			$("#bOneAge").blur(function(){
				var index = $("#bTrIndex").val();
				var one = $("#bOneAge").val();
				$("#promiseAge" + index).val(one);	//设置隐藏域dto的约定年龄
				//隐藏域历史记录
				$("#oneAge" + index).val(one);
			});
			$("#bTwoPercent").blur(function(){
				var index = $("#bTrIndex").val();
				var two = $("#bTwoPercent").val();
				$("#promiseRate" + index).val(two);	//设置隐藏域dto的约定比例
				//隐藏域历史记录
				$("#twoPercent" + index).val(two);
			});
			$("#bThreePercent").blur(function(){
				var index = $("#bTrIndex").val();
				var three = $("#bThreePercent").val();
				$("#promiseRate" + index).val(three); //设置隐藏域dto的约定比例
				//隐藏域历史记录
				$("#threePercent" + index).val(three);
			});
			$("#bThreeAge").blur(function(){
				var index = $("#bTrIndex").val();
				var three = $("#bThreeAge").val();
				$("#promiseAge" + index).val(three); //设置隐藏域dto的约定年龄
				//隐藏域历史记录
				$("#threeAge" + index).val(three);
			});
			/*
			$("#accountOwner").blur(function(){
				var index = $("#bTrIndex").val();
				var accountOwner = $("#accountOwner").val();
				$("#accountOwner" + index).val(accountOwner); //设置隐藏域dto的约定年龄
				//隐藏域历史记录
				//$("#hideAccountOwner" + index).val(accountOwner);
			});
			*/
			$("#accountNo").blur(function(){
				var index = $("#bTrIndex").val();
				var accountNo = $("#accountNo").val();
				$("#accountNo" + index).val(accountNo); //设置隐藏域dto的约定年龄
				//隐藏域历史记录
				//$("#hideAccountNo" + index).val(accountNo);
			});
			
		}
		
		/* 类信托业务处理  edit by wangmingshun start */
		//银行选择链接点击处理
		$("#bankSelectLink").live("click", function(){
			var index = $("#bTrIndex").val();
			var divBankCode = $(this).siblings(".bankCode");
			var dtoBankCode = $("#bankCode" + index);
			//var hideBankCode = $("#hideBankCode" + index);
			var arr = new Array();
			arr.push(divBankCode);
			arr.push(dtoBankCode);
			//arr.push(hideBankCode);
			showBankSelectWindow01(arr, function(code, label){
				$("#bankName").html(label);
				$("#bankName" + index).val(label);
			});
			return false;
		});
		//当保单为PWM渠道并且保费在趸交100万（含）以上；期交首年保费为20万（含）以上系统才显示类信托业务勾选项及其录入界面。
		function isShowPwmCheckBox() {
			var policyNo = "${acceptance__item_input.policyNo}";
			var isPwm = "${acceptance__item_input.isPwm}";
			if(isPwm != 'true') {
				//如果以前已经有了类信托的信息，则按照满足条件的设置
				$.ajax({type: "GET",
					url:"${ctx}acceptance/branch/serviceItem_22/policyCanDoPwm.do",
					data:{"policyNo" : policyNo},
					cache: false,
					async: false,
					dataType:"text",
					success:function(data){
						if(data != 'Y'){
							//移除受益人性质radio的change事件，并且隐藏pwm类信托checkbox
							$(".beneChange").removeAttr("onchange");
							$("#pwmSpan").hide();
						}
					}
				});
			}
		}
		//授权信息checkbox change 事件
		$("#showAccountInfoDiv").live("change", function() {
			var isChecked = $("#showAccountInfoDiv").attr("checked");
			var index = $("#bTrIndex").val();
			if(isChecked == true) {
				var name = $("#beneficiaryName" + index).val(); //获取对应行的受益人姓名
				$("#accountOwner").val(name);	//受益人账户姓名默认为受益人姓名且不可修改
				$("#accountOwner" + index).val(name); //设置隐藏域dto的受益人账户
				$("#accountChecked" + index).val("Y");
				$("#accountInfoDiv").show();
			} else {
				$("#accountChecked" + index).val("NN");
				$("#accountInfoDiv").hide();
			}
		});
		
		function initAccountInfo() {
			
		}
	</script>
</sf:override>
<sf:override name="serviceItemsInput">
	<input type="hidden"  id="productCode" value="${acceptance__item_input.productCode}"/>
	<sfform:formEnv commandName="acceptance__item_input">
	<div class="layout_div_top_spance">
		<span class="font_heading2">&nbsp;受益人性质：</span>
		<sfform:radiobuttons path="benefitType" items="${acceptance__item_input.beneficiaryTypeList}" itemLabel="description" itemValue="code"
				cssClass="beneChange required" onchange="beneChange(this)"/>
		<input type="hidden" id="fuck"/>
		<span id="pwmSpan"><sfform:checkbox path="isPwm" id="isPwm"/>类信托业务</span>
		<%-- 记录类信托的原始状态 --%>
		<input type="hidden" value="${acceptance__item_input.isPwm}" id="isPwmOrigin"/>
	</div>
	<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>被保人姓名：${acceptance__item_input.clientInfoIns.clientName}</legend>
			<table class="infoDisTab" id="benefDataTable">
				<thead>
					<tr>
						<th style="width:18px;" class="td_widget">选<br/>择</th>
						<th style="width:18px;">序<br/>号</th>
						<th style="width:50px;">受益人<br/>姓名</th>
						<th style="width:35px;">性别</th>
						<th style="width:80px;">证件类型</th>
						<th style="width:110px;">证件号码</th>
						<th style="width:77px;">出生日期</th>
						<%--edit by wangmingshun --%>
						<th style="width:77px;">证件有效期</th>
						<th style="width:60px;">国籍</th>
						<th style="width:60px;">是被保险<br/>人的(关系)</th>
						<th style="width:50px;">其他关系说明</th>
						<th style="width:35px;">受益份额</th>
						<th style="width:26px;">受益<br/>顺序</th>
						<%--
						<th style="width:100px;">联系电话</th>
						<th style="width:150px;">联系地址</th>
						--%>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.beneficiaryInfoList}" var="item" varStatus="status">
						<c:set var="title" value="new" scope="page"/>
						<c:set var="hiddenInputs" value="" scope="page" />
						<c:forEach items="${acceptance__item_input.originServiceItemsInputDTO.beneficiaryInfoList}" var="itemInternal" varStatus="statusInternal">
							<c:if test="${not empty item.beneficiaryNo and item.beneficiaryNo eq itemInternal.beneficiaryNo}">
								<c:set var="hiddenInputs" scope="page">
									<input type="hidden" class="beneficiaryNameOrigin origin" value="${itemInternal.benefInfo.beneficiaryName}"/>
									<input type="hidden" class="sexCodeOrigin origin" value="${itemInternal.benefInfo.sexCode}"/>
									<input type="hidden" class="idTypeOrigin origin" value="${itemInternal.benefInfo.idType}"/>
									<input type="hidden" class="idnoOrigin origin" value="${itemInternal.benefInfo.idno}"/>
									<%--edit by wangmingshun --%>
									<input type="hidden" class="claimPremDrawTypeOrigin origin" value="${itemInternal.claimPremDrawType}"/>
									<input type="hidden" class="promiseRateOrigin origin" value="${itemInternal.promiseRate}"/>
									<input type="hidden" class="promiseAgeOrigin origin" value="${itemInternal.promiseAge}"/>
									<input type="hidden" class="idValidDateOrigin origin" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${itemInternal.benefInfo.idValidDate}"/>"/>
									<input type="hidden" class="countryCodeOrigin origin" value="${itemInternal.benefInfo.countryCode}"/>
									<input type="hidden" class="accountOwnerOrigin origin" value="${itemInternal.accountOwner}"/>
									<input type="hidden" class="accountNoOrigin origin" value="${itemInternal.accountNo}"/>
									<input type="hidden" class="bankCodeOrigin origin" value="${itemInternal.bankCode}"/>
									
									<input type="hidden" class="birthdateOrigin origin" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${itemInternal.benefInfo.birthdate}"/>"/>
									<input type="hidden" class="relationshipOrigin origin" value="${itemInternal.relationship}"/>
									<input type="hidden" class="relationDescOrigin origin" value="${itemInternal.relationDesc}"/>
									<input type="hidden" class="benefitRatePercentOrigin origin" value="${itemInternal.benefitRatePercent}"/>
									<input type="hidden" class="benefitSeqOrigin origin" value="${itemInternal.benefitSeq}"/>
									<%--
									<input type="hidden" class="phoneOrigin origin" value="${itemInternal.benefInfo.phone}"/>
									<input type="hidden" class="addressOrigin origin" value="${itemInternal.benefInfo.address}"/>
									--%>
								</c:set>
								<c:set var="title" value="old" scope="page"/>
							</c:if>
						</c:forEach>
						<tr id="tr${status.index}" title="${title}" onclick="beneficiaryInfo('${status.index}');">
							<td class="td_widget">
								<input type="checkbox" class="selectRow"/>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].beneficiaryNo" cssClass="beneficiaryNo" />
								<sfform:hidden path="beneficiaryInfoList[${status.index}].benefitType" cssClass="benefitType"/>
								<!-- 如下隐藏域是防止有删除元素时，元素位置改变，新增的元素无法覆盖原有位置DTO的原始值的情况  -->
								<sfform:hidden path="beneficiaryInfoList[${status.index}].applyNo" />
								<sfform:hidden path="beneficiaryInfoList[${status.index}].policyNo" />
								<sfform:hidden path="beneficiaryInfoList[${status.index}].benefInfo.beneficiaryNo" />
								<sfform:hidden path="beneficiaryInfoList[${status.index}].benefInfo.clientNo" />
								${hiddenInputs}
								<%-- edit by wangmingshun start --%>
								<%-- 身故保险金领取方式：
										02、分期领取至约定年龄（含有比例和周岁）
										03、分期领取至身故保险金本息领取完（只有周岁）
										04、自约定年龄起分期领取（含有比例和周岁）  --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].claimPremDrawType" id="claimPremDrawType${status.index}" class="claimPremDrawType"/>
								<%-- 约定比例 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].promiseRate" id="promiseRate${status.index}" class="promiseRate"/>
								<%-- 约定周岁 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].promiseAge" id="promiseAge${status.index}" class="promiseAge"/>
								<%-- 账户名 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].accountOwner" id="accountOwner${status.index}" class="accountOwner"/>
								<%-- 银行编号 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].bankCode" id="bankCode${status.index}" class="bankCode" value=""/>
								<%-- 银行名称 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].bankName" id="bankName${status.index}" class="bankName" value=""/>
								<%-- 账号 --%>
								<sfform:hidden path="beneficiaryInfoList[${status.index}].accountNo" id="accountNo${status.index}" class="accountNo"/>
								<%-- 以下隐藏属性是保存类信托div层的历史录入记录 --%>
								<input type="hidden" id="onePercent${status.index }" value=""/>
								<input type="hidden" id="oneAge${status.index }" value=""/>
								<input type="hidden" id="twoPercent${status.index }" value=""/>
								<input type="hidden" id="threePercent${status.index }" value=""/>
								<input type="hidden" id="threeAge${status.index }" value=""/>
								<input type="hidden" id="accountChecked${status.index }" value="N"/>
								<%--
								<input type="hidden" id="hideBankCode${status.index }" value=""/>
								<input type="hidden" id="hideBankName${status.index }" value=""/>
								<input type="hidden" id="hideAccountOwner${status.index }" value=""/>
								<input type="hidden" id="hideAccountNo${status.index }" value=""/>
								 --%>
								<%-- edit by wangmingshun end --%>
							</td>
							<td>${status.index}</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.beneficiaryName" 
									style="width:50px;" cssClass="input_text beneficiaryName required" id="beneficiaryName${status.index}"/>
							</td>
							<td>
								<sfform:select path="beneficiaryInfoList[${status.index}].benefInfo.sexCode" items="${CodeTable.posTableMap.SEX}" itemLabel="description" itemValue="code"
									cssClass="input_select sexCode required" style="width:50px"/>
							</td>
							<td>
								<sfform:select path="beneficiaryInfoList[${status.index}].benefInfo.idType" id="benefInfoIdType${status.index}" items="${CodeTable.posTableMap.ID_TYPE}" itemLabel="description" itemValue="code"
									style="width:100px" cssClass="input_select idType required"/>
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.idno" 
									style="width:135px;" cssClass="input_text idno {required:true,idNo:{target:'#benefInfoIdType${status.index}',value:'01'}}" />
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.birthdate" onfocus="WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});"
									cssClass="input_text birthdate Wdate {required:true}" style="width:85px;"/>
							</td>
							<%--edit by wangmingshun --%>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.idValidDate" onfocus="WdatePicker({skin:'whyGreen',startDate:'',minDate:'',isShowToday:false});"
									style="width:90px;" cssClass="input_text idValidDate Wdate {required: function(element){return $(element).closest('td').find('.isLongDate').attr('checked') == false;}}"/>
								<%--
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.idValidDate" onfocus="WdatePicker({skin:'whyGreen',startDate:'',minDate:'',isShowToday:false});"
									style="width:90px;" cssClass="input_text idValidDate Wdate {required: false}"/>
								长期<sfform:checkbox path="beneficiaryInfoList[${status.index}].benefInfo.isLongDate"/>
								 --%>
								长期	<input type="checkbox" class="isLongDate"/>
							</td>
							<td>
								<sfform:select path="beneficiaryInfoList[${status.index}].benefInfo.countryCode" items="${CodeTable.posTableMap.COUNTRY}" itemLabel="description" itemValue="code"
									cssClass="input_select countryCode required" style="width:80px"/>
							</td>
							<td>
								<sfform:select path="beneficiaryInfoList[${status.index}].relationship" items="${CodeTable.posTableMap.RELATIONSHIP}" itemLabel="description" itemValue="code"
									cssClass="input_select relationship required" style="width:80px"/>
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].relationDesc"
									style="width:48px;" maxlength="40" cssClass="input_text relationDesc required"/>
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefitRatePercent" 
									cssClass="input_text benefitRatePercent {required:true,number:true,min:1,max:100}" style="width:35px;"/>%
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefitSeq"
									style="width:20px;" cssClass="input_text benefitSeq {required:true,number:true,min:1}"/>
							</td>
							<%--
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.phone"
									style="width:98px;" cssClass="input_text phone"/>
							</td>
							<td>
								<sfform:input path="beneficiaryInfoList[${status.index}].benefInfo.address"
									style="width:148px;" cssClass="input_text address"/>
							</td>
							--%>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div style="border:1px solid blue;background: rgb(227,238,227);" id="benefitDiv">
				[<span id="beneName" style="color:red;font-size:15px;"></span>]身故保险金领取方式选择：
				<input type="hidden" id="bTrIndex"/>	
				<input type="checkbox" value="N" id="bOne"/><span>分期领取至约定年龄</span>		
				<input type="checkbox" value="N" id="bTwo"/><span>分期领取至身故保险金本息领取完</span>		
				<input type="checkbox" value="N" id="bThree"/><span>自约定年龄起分期领取</span>
				<br/>
				<br/>
				<div id="oneDiv">
				&nbsp;&nbsp;被保险人身故周年日领取该受益人项下的身故保险金的
				<input type="text" id="bOnePercent" style="width:50px;border:1px solid rgb(149,167,191);"/>%,年满
				<input type="text" id="bOneAge" style="width:30px;border:1px solid rgb(149,167,191);"/>周岁后的领取日，一次性领取余额.
				</div>
				<div id="twoDiv">
				&nbsp;&nbsp;被保险人身故周年日领取该受益人项下的身故保险金的
				<input type="text" id="bTwoPercent" style="width:50px;border:1px solid rgb(149,167,191);"/>%,直至身故保险金本息领取完.</span>		
				</div>
				<div id="threeDiv">
				&nbsp;&nbsp;自受益人年满
				<input type="text" id="bThreeAge" style="width:30px;border:1px solid rgb(149,167,191);"/>周岁后的被保险人身故周年日，每年领取该受益人项下的身故保险金的
				<input type="text" id="bThreePercent" style="width:50px;border:1px solid rgb(149,167,191);"/>%,直至身故保险金本息领取完.	
				</div>
				<div>
				<br/>
				授权信息：
				<input type="checkbox" value="N" id="showAccountInfoDiv"/>
				<div id="accountInfoDiv">
					<table>
						<tr>
							<td style="width:135px;">账户名：<input 
								type="text" id="accountOwner" style="width:77px;border:1px solid rgb(149,167,191);" readonly="readonly"/></td>
							<td style="width:210px;">银行：<input 
								type="text" id="bankCode" style="width:100px;border:1px solid rgb(149,167,191);" readonly="readonly" class="bankCode"/>
								<a href="javascript:void(0);" id="bankSelectLink">选择银行</a><div id="bankName"></div></td>
							<td>账号：<input 
								type="text" id="accountNo" style="width:188px;border:1px solid rgb(149,167,191);"/></td>
						</tr>
					</table>
				</div>
				</div>
				<br/>
			</div>
		</fieldset>
	</div>
	<sfform:hidden path="beneficiaryInfoListSize" />
    </sfform:formEnv>
    <jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
</sf:override>
<sf:override name="buttonBlock">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add">新增</a>
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="remove">删除</a>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
