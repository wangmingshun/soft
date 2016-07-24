<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 保单还款 --%>
<sf:override name="headBlock">
<script type="text/javascript" language="javascript">
	$(function() {
		
		//对于自垫本金的还是采用以前的方案
		var isApl = false;
		var totalAmount = 0;
		
		<c:forEach items="${acceptance__item_input.loanPayBackList }" var="item">
			totalAmount = totalAmount + eval("${item.totalSum}");
		</c:forEach>
		//值大于0表示是新方案
		isApl = totalAmount > 0 ? true : false;

		if(!isApl) {
			//自垫采用老方案，隐藏明细div
			$(".detailDiv").find("input").attr("disabled", true).end().hide();
		} else {
			//非自垫，采用新方案，隐藏老div
			$(".totalDiv").find("input").attr("disabled", true).end().hide();
			
			//设置应还合计
			$(".totalAmount").html(totalAmount.toFixed(2));
			
			//默认本次还款金额录入框只读，只有选择还款类型后才能操作,不可做任何操作
			$("input[name='loanPayBackSum']").attr("readonly", true).css("background-color", "#d4d0c8");
			$(".total").attr("disabled", true);
			$(".part").attr("disabled", true);
			
			//还款类型类型改变事件
			$("input[name='loanPayBackType']").change(function() {
				//选中全额还款，本次还款金额设置为应还总金额
				if($("input[name='loanPayBackType']:checked").val() == '1') {
					$("input[name='loanPayBackSum']").val($(".totalAmount").html()).attr("readonly", true).css("background-color", "#d4d0c8");
					//明细中的每条记录默认设置为全额还款
					$(".total").attr("checked", true).attr("disabled", true);
					$(".part").attr("disabled", true).attr("checked", false);
					$(".loanPayBackTypeDetail").val("1");
					$(".checkCash").html("0");
					initTrTotalSum("1");
					$(".originLoanPayBackSum").val($(".totalAmount").html());
				} else {
					$("input[name='loanPayBackSum']").val("").attr("readonly", false).css("background-color", "#eeeeee");
					$(".total").attr("checked", false).attr("disabled", false);
					$(".part").attr("disabled", false).attr("checked", false);
					$(".loanPayBackTypeDetail").val("0");
					initTrTotalSum("2");
				}
			});
			
			//选择部分还款，部分还款金额<应还金额
			$("input[name='loanPayBackSum']").blur(function() {
				if($("input[name='loanPayBackType']:checked").val() == '2') {
					var loanPayBackSum = $(".loanPayBackSum").val();
					var totalSum = $(".totalAmount").html();
					//验证输入的必须是整数或者小数
					
					var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
					if(!reg.test(loanPayBackSum)) {
						$(".loanPayBackSum").val("");
						$(".checkCash").html("");
						$.messager.alert("提示", "请输入正确的数字！！");
						return false;
					}
					
					
					if(eval(loanPayBackSum) >= eval(totalSum) || loanPayBackSum <= 0) {
						$.messager.alert("提示", "选择部分还款时，\"本次还款金额\"录入的金额必须小于全部应还款金额并且大于0！请重新录入！");
						$("input[name='loanPayBackSum']").val("");
						$(".checkCash").html("0");
						$(".total").attr("checked", false).attr("disabled", false);
						$(".part").attr("checked", false).attr("disabled", false);
						initTrTotalSum("2");
						$(".originLoanPayBackSum").val("");
						return false;
					}
					//首先获取上一次录入的"本次还款金额"
					var originLoanPayBackSum = $(".originLoanPayBackSum").val();
					if(originLoanPayBackSum != "") {
						//说明之前有录入记录，对比这次和上次的结果，如果有变化，则初始化明细记录，”剩余金额“也要初始化
						if(loanPayBackSum != originLoanPayBackSum) {
							$(".total").attr("checked", false).attr("disabled", false);
							$(".part").attr("checked", false).attr("disabled", false);
							initTrTotalSum("2");
							$(".checkCash").html(loanPayBackSum);
						}
					} else {
						//第一次录入，则肯定要初始化”剩余金额“
						$(".checkCash").html(loanPayBackSum);
					}
					//记录本次录入的“本次还款金额 ”供下次使用
					$(".originLoanPayBackSum").val(loanPayBackSum);
				}
			});
			
			//为每条保全记录的还款类型绑定change事件
			var $posInfo = $(".infoDisTab tbody tr");
			$posInfo.each(function(index) {
				var $tr = $(this);
				//全部还款checkbox
				$tr.find(".total").change(function() {
					var checkCash = $(".checkCash").html() == "" ? 0 : $(".checkCash").html();
					if($tr.find(".total").attr("checked") == true) {
						if(eval(checkCash) < eval($tr.find(".totalSum").html())) {
							if(checkCash != '0') {
								$.messager.alert("提示", "剩余可还金额小于本条本息合计，只能选择部分还款，请重新选择！");
							} else {
								$.messager.alert("提示", "剩余可还金额为0，无法继续还款！");
							}
							//ie9以下的版本,IE在等待失去焦点,当失去焦点的时候才会触发相应事件,必须在改变复选框之前，
							//将该复选框的焦点转移走，否则当复选框失去焦点的时候还会再次触发事件，这种不确定的触发是不需要的
							this.blur();
							$tr.find(".total").attr("checked", false);
							return false;
						}
						$tr.find(".loanPayBackTypeDetail").val("1");
						$tr.find(".part").attr("disabled", true);
						checkCash = (checkCash - $tr.find(".totalSum").html()).toFixed(2);
						$(".checkCash").html(eval(checkCash));
						$tr.find(".trTotalSum").html($tr.find(".totalSum").html());
						$tr.find(".loanPayBackSumDetail").val($tr.find(".totalSum").html());
					} else {
						/*
						$tr.find(".loanPayBackTypeDetail").val("0");
						$tr.find(".part").attr("disabled", false);
						$(".checkCash").html(eval(checkCash) + eval($tr.find(".totalSum").html()));
						$tr.find(".trTotalSum").html("");
						$tr.find(".loanPayBackSumDetail").val("");
						*/
						//只要有一个取消掉，就全部取消，初始化录入
						inintDetail();
					}
				});
				//部分还款checkbox
				$tr.find(".part").live("change", function() {
					var checkCash = $(".checkCash").html() == "" ? 0 : $(".checkCash").html();
					if($tr.find(".part").attr("checked") == true) {
						if(eval(checkCash) >= eval($tr.find(".totalSum").html())) {
							$.messager.alert("提示", "剩余可还金额大于或者等于本条本息合计，只能选择全部还款，请重新选择！");
							this.blur();
							$tr.find(".part").attr("checked", false);
							return false;
						}
						if(checkCash == '0') {
							$.messager.alert("提示", "剩余可还金额为0，无法继续还款！");
							this.blur();
							$tr.find(".part").attr("checked", false);
							return false;
						}
						$tr.find(".loanPayBackTypeDetail").val("2");
						$tr.find(".total").attr("disabled", true);
						$(".checkCash").html("0");
						checkCash = eval(checkCash).toFixed(2);
						$tr.find(".trTotalSum").html(checkCash);
						$tr.find(".loanPayBackSumDetail").val(checkCash);
					} else {
						/*
						$tr.find(".loanPayBackTypeDetail").val("0");
						$tr.find(".total").attr("disabled", false);
						$(".checkCash").html(eval($tr.find(".trTotalSum").html()) + eval(checkCash));
						$tr.find(".trTotalSum").html("");
						$tr.find(".loanPayBackSumDetail").val("");
						*/
						//只要有一个取消掉，就全部取消，初始化录入
						inintDetail();
					}
				});
			});
		}
		
		
		//提交前校验
		posValidateHandler = function() {
			var loanPayBackType = $("input[name='loanPayBackType']:checked").val();
			//新方案才验证
			if(isApl) {
				if(loanPayBackType == "1" || loanPayBackType == "2") {
				} else {
					$.messager.alert("提示", "至少选择一种\"还款类型\"！");
					return false;
				}
				var loanPayBackSum = $("input[name='loanPayBackSum']").val();
				if(loanPayBackSum == "") {
					$.messager.alert("提示", "\"本次还款金额\"必须录入！");
					return false;
				}
				var checkCash = eval($(".checkCash").html());
				if(checkCash != '0') {
					$.messager.alert("提示", "\"剩余可还款金额\"必须全部使用，请选择某条保全记录来抵消！");
					return false;
				}
			}
			return true;
		};
		
		//全额还款和部分还款radio使用，loanPayBackType只有为1的时候才表示全额还款
		function initTrTotalSum(loanPayBackType) {
			var $posInfo = $(".infoDisTab tbody tr");
			$posInfo.each(function(index) {
				$tr = $(this);
				if(loanPayBackType == '1') {
					$tr.find(".trTotalSum").html($tr.find(".totalSum").html());
					$tr.find(".loanPayBackSumDetail").val($tr.find(".totalSum").html());
				} else {
					$tr.find(".trTotalSum").html("");
					$tr.find(".loanPayBackSumDetail").val("");
				}
			});
		}
		
		//一旦明细中勾选了某一项之后，在取消勾选则强制初始化，防止通过不同的组合，之后出现多笔部分还款，理论上只能出现一笔部分还款
		function inintDetail() {
			$(".part").attr("disabled", false).attr("checked", false);
			$(".total").attr("disabled", false).attr("checked", false);
			$(".loanPayBackSumDetail").val("");
			$(".trTotalSum").html("");
			$(".checkCash").html($(".loanPayBackSum").val());
			$(".loanPayBackTypeDetail").val("0");
		}
	});
</script>
<style type="text/css">
</style>
</sf:override>
<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<div class="layout_div_top_spance detailDiv">
		<fieldset class="fieldsetdefault" style="padding: 1px;"><legend>保单自垫及借款信息：&nbsp;&nbsp;&nbsp;&nbsp;币种：人民币</legend>
		<div class="layout_div_top_spance">
			<fieldset style="border:1px solid #95a7bf">
				<span class="font_heading2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应还合计：<span class="totalAmount">0</span>元</span>
				<span class="font_heading2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;还款类型：</span>
				<input type="radio" name="loanPayBackType" value="1" />全额还款
				<input type="radio" name="loanPayBackType" value="2" />部分还款
				<br/>
				<br/>
				<%-- 记录下上一次输入的 “本次还款金额” --%>
				<input type="hidden" class="originLoanPayBackSum"/>
				<span class="font_heading2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本次还款金额：</span>
				<input type="text" name="loanPayBackSum" style="border:1px solid #95a7bf" class="loanPayBackSum"/>
				<span class="font_heading2">&nbsp;&nbsp;&nbsp;剩余可还款金额：<span class="checkCash">0</span></span>
				<div>&nbsp;</div>
			</fieldset>
		</div>
		<br/>
		<table class="infoDisTab">
			<thead>
				<tr>
					<th style="width:30px;">序<br/>号</th>
					<th style="width:120px;">保全号</th>
					<th style="width:80px;">生效日期</th>
					<th style="width:80px;">借款本金</th>
					<th style="width:80px;">借款利息</th>
					<th style="width:80px;">本息合计</th>
					<th style="width:50px;">贷款利率</th>
					<th style="width:130px;">还款类型</th>
					<th style="width:80px;">还款金额</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.loanPayBackList }" var="item" varStatus="status">
					<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td>${status.index + 1 }</td>
						<td>${item.posNo }</td>
						<td><fmt:formatDate type="date" value="${item.effectDate }"/></td>
						<td>${item.loanCash }</td>
						<td>${item.loanInterest }</td>
						<td><span class="totalSum">${item.totalSum }</span></td>
						<td><c:choose>
							<c:when test="${item.loanRateType eq '1'}">
									同银行5年期贷款利率
							</c:when>
							<c:otherwise>${item.loanRate }%</c:otherwise>
						</c:choose></td>
						<td>
							<sfform:hidden path="loanPayBackList[${status.index }].loanPayBackTypeDetail" cssClass="loanPayBackTypeDetail" />
							<input type="checkbox" class="total" value="1"/>全额还款
							<input type="checkbox" class="part" value="2"/>部分还款
						</td>
						<td>
							<sfform:hidden path="loanPayBackList[${status.index }].loanPayBackSumDetail" cssClass="loanPayBackSumDetail"/>
							<span class="trTotalSum"></span>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
		</div>
		<%-- -----------------------------------------分界线--------------------------------------------- --%>
		<div class="layout_div_top_spance totalDiv">
		<fieldset class="fieldsetdefault" style="padding: 1px;"><legend>保单自垫及借款信息：&nbsp;&nbsp;&nbsp;&nbsp;币种：人民币</legend>
		<table class="infoDisTab">
			<thead>
				<tr>
					<th class="td_data">自垫本金</th>
					<th class="td_data">自垫利息</th>
					<th class="td_data">应补差额保费</th>
					<th class="td_data">借款本金</th>
					<th class="td_data">借款利息</th>
				</tr>
			</thead>
			<tbody>
				<tr
					class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
					<td class="td_data"><c:choose>
						<c:when test="${empty acceptance__item_input.aplLoanSum}">
									0
								</c:when>
						<c:otherwise>${acceptance__item_input.aplLoanSum}</c:otherwise>
					</c:choose></td>
					<td class="td_data"><c:choose>
						<c:when test="${empty acceptance__item_input.aplInterestSum}">
									0
								</c:when>
						<c:otherwise>${acceptance__item_input.aplInterestSum}</c:otherwise>
					</c:choose></td>
					<td class="td_data"><c:choose>
						<c:when test="${empty acceptance__item_input.aplExtraFee}">
									0
								</c:when>
						<c:otherwise>${acceptance__item_input.aplExtraFee}</c:otherwise>
					</c:choose></td>
					<td class="td_data"><c:choose>
						<c:when test="${empty acceptance__item_input.loanSum}">
									0
								</c:when>
						<c:otherwise>${acceptance__item_input.loanSum}</c:otherwise>
					</c:choose></td>
					<td class="td_data"><c:choose>
						<c:when test="${empty acceptance__item_input.interestSum}">
									0
								</c:when>
						<c:otherwise>${acceptance__item_input.interestSum}</c:otherwise>
					</c:choose></td>
				</tr>
				<c:if test="${not empty acceptance__item_input.loanSum}">
					<tr>

						<td class="layouttable_td_label" width="20%">还款金额:</td>
						<td colspan="4"><input type="text" name="loanPayBackSum"
							value="${acceptance__item_input.loanAllSum}"
							id="loanPayBackSum"
							class="input_text {required:function(){return ${not empty acceptance__item_input.loanSum}},number:true,max:${acceptance__item_input.loanAllSum}}" /></td>
					</tr>

				</c:if>
			</tbody>
		</table>
		</fieldset>
		</div>
	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
