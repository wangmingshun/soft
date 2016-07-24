<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<div style="display:none;">
<div id="productServiceItemsSelectWindowDiv" class="easyui-window" closed="true" modal="true" title="保全项目选择" style="width: 570px; height: 400px;" collapsible="false" minimizable="false" maximizable="false">
	<div class="easyui-layout" fit="true">
		<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
			<script type="text/javascript">
				var productServiceItemsSelectWindowCallback = null;
				$(document).ready(function(){
					var $checkboxes = $("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					var $selectAllCheckBox = $("#productServiceItemsSelectWindowDiv input.selectAllCheckBox");
					//全选按钮事件处理
					$selectAllCheckBox.unbind("click").click(function(event){
						var selectedNum = $checkboxes.filter(":enabled:checked").length;
						if(selectedNum == $checkboxes.filter(":enabled").length) {
							$checkboxes.filter(":enabled").attr("checked", false);
							event.target.checked = false;
						} else {
							$checkboxes.filter(":enabled").attr("checked", true);
							event.target.checked = true;
						}
					});
					//项目选择按钮事件处理
					$("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox):enabled").die("click").live("click", function(event){
						var selectedNum = $checkboxes.filter(":enabled:checked").length;
						if(selectedNum == $checkboxes.filter(":enabled").length) {
							$selectAllCheckBox.attr("checked", true);
						} else {
							$selectAllCheckBox.attr("checked", false);
						}
					});
					//确定按钮事件处理
					$("#productServiceItemsSelectWindowButtonOK").unbind("click").click(function(){
						var valueArray = [];
						var labelArray = [];
						$checkboxes.filter(":enabled:checked").each(function(){
							valueArray.push($(this).val());
							labelArray.push($(this).attr("value") + ":" + $(this).attr("alt"));
						});
						if(productServiceItemsSelectWindowCallback != null) {
							productServiceItemsSelectWindowCallback(labelArray.join(","), valueArray.join(","));
						}
						$("#productServiceItemsSelectWindowDiv").window("close");
						return false;
					});
					//取消按钮事件处理
					$("#productServiceItemsSelectWindowButtonCancel").unbind("click").click(function(){
						$("#productServiceItemsSelectWindowDiv").window("close");
						return false;
					});
				});
				/**
				 * 窗口弹出函数
				 * callback 回调函数 function(labelString, valueString)参数以","分隔
				 * initData 初始状态String类型, 参数以","分隔
				 */
				function openProductServiceItemSelectWindow(callback, initData, filterBarcode) {
				
					productServiceItemsSelectWindowCallback = callback;
					var initDataArr = initData.split(",");
					var $checkboxes = $("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					$checkboxes.each(function(idx){
						if(filterServiceItemsByBarcode($($checkboxes[idx]).attr("value"), filterBarcode)) {
							$($checkboxes[idx]).attr("disabled", false).closest("div").show();
							if(initDataArr.indexOf($($checkboxes[idx]).attr("value")) > -1) {
								$($checkboxes[idx]).attr("checked", true);
							} else {
								$($checkboxes[idx]).attr("checked", false);
							}
						} else {
							$($checkboxes[idx]).attr("disabled", true).closest("div").hide();
						}
					});
					var $selectAllCheckBox = $("#productServiceItemsSelectWindowDiv input.selectAllCheckBox");
					var selectedNum = $checkboxes.filter(":enabled:checked").length;
					if(selectedNum == $checkboxes.filter(":enabled").length) {
						$selectAllCheckBox.attr("checked", true);
					} else {
						$selectAllCheckBox.attr("checked", false);
					}
					$('#productServiceItemsSelectWindowDiv').window('open');
				}
				//根据选项值获取名称字符串
				function getPosTypeLabels(valueString) {
					var retArr = [];
					var valDataArr = valueString.split(",");
					var $checkboxes = $("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					$checkboxes.each(function(idx){
						if(valDataArr.indexOf($($checkboxes[idx]).attr("value")) > -1) {
							retArr.push($($checkboxes[idx]).attr("value") + ":" + $($checkboxes[idx]).attr("alt"));
						}
					});
					return retArr.join(",");
				}
				var filter_config = {
					//edit by wangmingshun 项目范围添加44,45
					"1211" : ['19', '23', '21', '18', '28', '7', '26', '25', '24', '30', '31', '32', '29','43','44','45','46'],
					"1212" : ['12', '15', '22', '20', '16', '6', '14', '3', '5', '35','42'],
					"1213" : ['27', '1', '2', '9', '4', '11', '17', '10', '13', '33', '34'],
					"1214" : ['39', '38', '37', '36'],
					"1233" : [ '19','10', '11','1', '2','45'],
					"1235" : [ '19','10', '11','1', '2','45'],
					"1234" : ['19', '23', '21', '18', '28', '7', '26', '25', '24', '30', '31', '32', '29','43','44','45','46'],
					"1215" : ['8']
				};
				function filterServiceItemsByBarcode(serviceItems, filterBarcode) {
					if(!filterBarcode)
						return true;
					
					return filter_config[filterBarcode] && filter_config[filterBarcode].length && filter_config[filterBarcode].indexOf(serviceItems) > -1;
				}
			</script>
			<div>
				<input type="checkbox" class="checkbox selectAllCheckBox" id="productServiceItemsSelectAll"/>
				<label for="productServiceItemsSelectAll">全选</label>
			</div>
			<div style="width:100%">
				<c:forEach items="${CodeTable.posTableMap.PRODUCT_SERVICE_ITEMS}" var="item" varStatus="status">
					<div>
						<input id="__serviceItems${status.index}" type="checkbox" value="${item.code}" class="checkbox" alt="${item.description}"/>
						<span><label for="__serviceItems${status.index}">${item.code}:${item.description}</label></span>
					</div>
				</c:forEach>
			</div>
			
			
		
			
		</div>
		<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
			<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="productServiceItemsSelectWindowButtonOK">确定</a>
			<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" id="productServiceItemsSelectWindowButtonCancel">取消</a>
		</div>
	</div>
</div>
</div>