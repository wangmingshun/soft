<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 投资账户转换 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#add").click(function(){
				//克隆第一行数据
				var $cln = $("#inputTable tbody tr:first").clone("true");
				//清空录入值
				$cln.find(":input:not(:button)").val("");
				//清空校验错误
				$cln.find("em.error").remove();
				$cln.appendTo($("#inputTable tbody"));
				updateIndex();
				return false;
			});
			$(".del").live("click", function(){
				var $this = $(this);
				var $trs = $this.closest("tbody").find("tr");
				var $tr = $this.closest("tr");
				if($trs.length == 1) {
					$tr.find(":input:not(:button)").val("");
					$tr.find("option[value='']").attr("selected", true);
					$tr.find("em.error").remove();
				} else {
					$tr.remove();
				}
				updateIndex();
				return false;
			});
			$(".outFinancialProducts").live("click", function(){
				$(this).closest("tr").find(".outUnits").trigger("focusout");
				$(this).closest("tbody").find(".inFinancialProducts").trigger("focusout");
			});
		});
		function updateIndex() {
			var $trs = $("#inputTable tbody tr");
			var $inputs, $tr, input, index;
			var i = 0;
			$trs.each(function(idx){
				$tr = $($trs[idx]);
				index = $tr.attr("title");
				$inputs = $tr.find(":input:not(:button)");
				$inputs.each(function(idx, content){
					input = $inputs[idx];
					input.name = input.name.replace(index, i);
					input.id = input.id.replace(index, i);
				});
				$tr.attr("title", i);
				i++;
				$(":hidden[name='financialProductsTransListSize']").val(i);
			});
		}
		//转入账户校验函数
		function inAccountValidate(value) {
			//转入账户不能与转出账户相同
			if(value == $(this).closest("tr").find("td:first :input").val())
				return false;
			
			//不能存在已经当前转入账户作为转出账户的记录
			if($(this).closest("tbody").find(":input.outFinancialProducts[value='" + value + "']").length > 0)
				return false;
			
			return true;
		}
		//转出单位数校验函数
		function outUnitsValidate(value) {
			var outFinancialProducts = $(this).closest("tr").find(":input.outFinancialProducts").val();
			var $sameFinancial = $(".outFinancialProducts");
			var sum = 0, val;
			$sameFinancial.each(function(idx){
				if($sameFinancial[idx].value == outFinancialProducts) {
					val = $($sameFinancial[idx]).closest("tr").find(".outUnits").val();
					if(val != "" && parseFloat(val) > 0)
						sum += parseFloat(val);
				}
			});
			if(outFinancialProducts != "" && value != "") {
				var maxValue = parseFloat($("#unitsFor" + outFinancialProducts).html());
				if(maxValue) {
					if(sum == maxValue)
						return true;
					if(sum < 100 || sum > maxValue || value % 10 != 0)
						return false;
				}
			}
			return true;
		}
	</script>
	<style type="text/css">
	</style>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<br/>
	保单${acceptance__item_input.policyNo}投资账户明细信息：
	<table class="infoDisTab">
		<thead>
			<tr>
				<th>投资账户名称</th>
				<th>投资账户累积单位数</th>
				<th>最近卖出价格</th>
				<th>最近卖出价值</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${acceptance__item_input.financialProductsList}" var="item" varStatus="status">
				<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
					<td>${item.finProductsDesc}</td>
					<td><span id="unitsFor${item.financialProducts}">${item.units}</span></td>
					<td>${item.soldPrice}</td>
					<td>${item.amount}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br/>
	本次转换明细：
	<table class="infoDisTab" id="inputTable">
		<thead>
			<tr>
				<th>转出投资账户</th>
				<th>转出单位数</th>
				<th>转入投资账户</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${acceptance__item_input.financialProductsTransList}" var="item" varStatus="status">
				<tr title="${status.index}">
					<td>
						<sfform:select path="financialProductsTransList[${status.index}].outFinancialProducts" cssClass="outFinancialProducts {required:true}">
							<sfform:option value="" label="请选择"/>
							<sfform:options  items="${acceptance__item_input.filteredFinancialProductsList}" itemLabel="finProductsDesc" itemValue="financialProducts"/>
						</sfform:select>
					</td>
					<td>
						<sfform:input path="financialProductsTransList[${status.index}].outUnits" cssClass="textfield outUnits {required:true,number:true,customValidate:'outUnitsValidate',messages:{customValidate:'转出单位数必须大于100，且小于账户剩余单位数，非全部转出时必须为10的倍数'}}"/>
					</td>
					<td>
						<sfform:select path="financialProductsTransList[${status.index}].inFinancialProducts" cssClass="inFinancialProducts {required:true,customValidate:'inAccountValidate',messages:{customValidate:'转出账户不能与转入账户相同，且存在转出记录的账户不能作为转入账户'}}">
							<sfform:option value="" label="请选择"/>
							<sfform:options  items="${acceptance__item_input.financialProductsList}" itemLabel="finProductsDesc" itemValue="financialProducts"/>
						</sfform:select>
					</td>
					<td>
                        <a class="del" href="javascript:void(0)">删除</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<sfform:hidden path="financialProductsTransListSize" />
</sfform:formEnv>
</sf:override>
<sf:override name="buttonBlock">
	<input type="button" value="增加" id="add" class="btn"/>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
