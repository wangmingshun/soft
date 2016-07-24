<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<div style="display:none;">
<div id="applyMaterialSelectWindowDiv" class="easyui-window" closed="true" modal="true"
	title="申请材料选择" style="width: 570px; height: 350px;" collapsible="false" minimizable="false" maximizable="false">
	<div class="easyui-layout" fit="true">
		<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
			<script type="text/javascript">
				var applyMaterialSelectWindowCallback = null;
				$(document).ready(function(){
					var $checkboxes = $("#applyMaterialSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					var $selectAllCheckBox = $("#applyMaterialSelectWindowDiv input.selectAllCheckBox");
					$selectAllCheckBox.unbind("click").click(function(event){
						var selectedNum = $checkboxes.filter(":checked").length;
						if(selectedNum == $checkboxes.length) {
							$checkboxes.attr("checked", false);
							event.target.checked = false;
						} else {
							$checkboxes.attr("checked", true);
							event.target.checked = true;
						}
					});
					$checkboxes.unbind("click").click(function(event){
						var selectedNum = $checkboxes.filter(":checked").length;
						if(selectedNum == $checkboxes.length) {
							$selectAllCheckBox.attr("checked", true);
						} else {
							$selectAllCheckBox.attr("checked", false);
						}
					});
					$("#applyMaterialSelectWindowButtonOK").unbind("click").click(function(){
						var valueArray = [];
						var labelArray = [];
						$checkboxes.filter(":checked").each(function(){
							valueArray.push($(this).val());
							labelArray.push($(this).attr("value") + ":" + $(this).attr("alt"));
						});
						if(applyMaterialSelectWindowCallback != null) {
							applyMaterialSelectWindowCallback(labelArray.join(","), valueArray.join(","));
						}
						$("#applyMaterialSelectWindowDiv").window("close");
						return false;
					});
					$("#applyMaterialSelectWindowButtonCancel").unbind("click").click(function(){
						$("#applyMaterialSelectWindowDiv").window("close");
						return false;
					});
				});
				/**
				 * 弹出窗口程序
				 * callback 回调函数 function(labelString, valueString)参数以","分隔
				 * initData 初始状态String类型, 参数以","分隔
				 */
				function openApplyMaterialSelectWindow(callback, initData) {
					applyMaterialSelectWindowCallback = callback;
					var initDataArr = initData.split(",");
					var $checkboxes = $("#applyMaterialSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					$checkboxes.each(function(idx){
						if(initDataArr.indexOf($($checkboxes[idx]).attr("value")) > -1) {
							$($checkboxes[idx]).attr("checked", true);
						} else {
							$($checkboxes[idx]).attr("checked", false);
						}
					});
					$('#applyMaterialSelectWindowDiv').window('open');
				}
				/**
				 * 取得值对应的名称
				 */
				function getApplyMaterialLabels(valueString) {
					var retArr = [];
					var valDataArr = valueString.split(",");
					var $checkboxes = $("#applyMaterialSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					$checkboxes.each(function(idx){
						if(valDataArr.indexOf($($checkboxes[idx]).attr("value")) > -1) {
							retArr.push($($checkboxes[idx]).attr("value") + ":" + $($checkboxes[idx]).attr("alt"));
						}
					});
					return retArr.join(",");
				}
			</script>
			<table cellpadding="0" cellspacing="0" style="text-align:left;">
				<thead>
					<tr>
						<td colspan="2">
							<input type="checkbox" class="checkbox selectAllCheckBox" id="applyMaterialSelectAll"/>
							<label for="applyMaterialSelectAll">全选</label>
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
					<c:forEach items="${CodeTable.posTableMap.POS_APPLY_MATERIAL_CODE}" var="item" varStatus="status">
						<td>
							<span>
								<input id="__materialCode${status.index}" type="checkbox" value="${item.code}" class="checkbox" alt="${item.description}"/>
								<label for="__materialCode${status.index}">${item.code}:${item.description}</label>
							</span>
						</td>
						<c:if test="${status.count mod 2 eq 0}"></tr><tr></c:if>
					</c:forEach>
					</tr>
				</tbody>
			</table>
		</div>
		<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
			<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="applyMaterialSelectWindowButtonOK">确定</a>
			<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" id="applyMaterialSelectWindowButtonCancel">取消</a>
		</div>
	</div>
</div>
</div>