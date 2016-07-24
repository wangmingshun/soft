<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>
		<c:choose>
			<c:when test="${personalNoticeDTO.clientOption eq 'A'}">投保人最新个人资料告知</c:when>
			<c:when test="${personalNoticeDTO.clientOption eq 'I'}">被保人${personalNoticeDTO.insuredSeq}最新个人资料告知</c:when>
		</c:choose>
	</title>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<script type="text/javascript" language="javascript">
	function updateInsProductListName() {
		var $row, title, $inputs, $input;
		var $insProductList = $(".insProductList");
		$insProductList.each(function(idx, content){
			$row = $($insProductList[idx]);
			title = $row.attr("title");
			$inputs = $row.find(":input");
			$inputs.each(function(idx1, content1){
				$input = $($inputs[idx1]);
				$input.attr("name", $input.attr("name").replace(title, idx));
			});
			$row.attr("title", idx);
		});
	}
	$(function(){
		//告知选项
		$("[name='itemOption']").change(function(){
			var $this = $(this);
			
			var $item = $("#item tr.item_" + $this.val());
			if($this.is(":checked")) {
				$item.find(":input").attr("disabled", false).removeClass("ignore");
				$item.css("display", "");
				$item.find(".itemAnswer").trigger("change");
				$item.find(".itemAnswer[value='N']").attr("checked", true);
			} else {
				$item.css("display", "none");
				$item.find(":input").removeClass("ignore").addClass("ignore").attr("disabled", true);
			}
			
			//设置全选按钮状态
			var $itemOption = $("[name='itemOption']");
			var $itemOptionChecked = $("[name='itemOption']:checked");
			$("#checkAll").attr("checked", $itemOption.length == $itemOptionChecked.length);
		});
		//告知选项全选按钮
		$("#checkAll").click(function(){
			var $itemOption = $("[name='itemOption']");
			var $itemOptionChecked = $("[name='itemOption']:checked");
			var $itemOptionUnchecked = $("[name='itemOption']:not(:checked)");
			if($itemOption.length == $itemOptionChecked.length) {
				this.checked = false;
				$itemOptionChecked.attr("checked", false);
				$itemOptionChecked.trigger("change");
			} else {
				this.checked = true;
				$itemOptionUnchecked.attr("checked", true);
				$itemOptionUnchecked.trigger("change");
			}
			
		
			
			$(".itemAnswer[value='N']").attr("checked", true);
		});
		//告知1的删除按钮
		$(".del").live("click", function(){
			if($(this).closest("tbody").find("tr").length == 1) {
				$(this).closest("tr").find(":input:not(:button)").val("");
			} else {
				$(this).closest("tr").remove();
			}
			updateInsProductListName();
			return false;
		});
		//告知1的新增按钮
		$("#add").click(function(){
			var $cln = $($(this).closest("table").find("tbody tr")[0]).clone(true);
			$cln.find("em.error").remove();
			$cln.find(":input:not(:button)").val("");
			$cln.appendTo($(this).closest("table").find("tbody"));
			updateInsProductListName();
			return false;
		});
		//选项的答复
		$(".itemAnswer").change(function(){
			var $this = $(this);
			if($this.val() == 'Y' && $this.is(":checked")) {
				if($this.attr("name") == "itemAnswer_1") {
					$this.closest("tbody").find("tr.answerY").css("display", "").find(":input").attr("disabled", false).removeClass("ignore");
				} else {
					$this.closest("tr").find(":input.answerY,.answerY :input").attr("disabled", false).removeClass("ignore");
					$this.closest("tr").find("div.answerY,span.answerY").css("display", "");
				}
			} else if($this.val() == 'N' && $this.is(":checked") || $("[name='" + $this.attr("name") + "']:checked").length == 0){
				if($this.attr("name") == "itemAnswer_1") {
					$this.closest("tbody").find("tr.answerY").css("display", "none").find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
				} else {
					$this.closest("tr").find(":input.answerY,.answerY :input").attr("disabled", true).removeClass("ignore").addClass("ignore");
					$this.closest("tr").find("div.answerY,span.answerY").css("display", "none");
				}
			}
		});
		//确认按钮
		$("#ok").click(function(){
			if($("[name='itemOption']:checked").length == 0) {
				$.messager.alert("提示", "请勾选告知项");
				return false;
			}
			if($("[name='itemOption']:not(:checked):not([value='1']):not([value='12']):not([value='15'])").length > 0) {
				$.messager.alert("提示", "请录入告知项（除1、12、15项之外的项目都必须录入）");
				return false;
			}
			$("[name='notifyForm']").submit();
			return false;
		});
		
		//取消按钮
		$("#cancel").click(function(){
			window.close();
			return false;
		});
		//初始化验证
		$("[name='notifyForm']").validate({
			ignore:".ignore,:input.ignore",
			submitHandler: function(form) {
				$(form).ajaxSubmit(function(data){
					if(data.flag == "N") {
						$.messager.alert("保存失败", "保存失败：" + data.msg);
					} else if(data.flag = "Y") {
						$.messager.confirm("保存成功", "保存成功，是否关闭窗口？", function(t){
							if(t) {
								window.returnValue = "SAVE_SUCCESS";
								window.close();
							}
						});
					}
				});
			}
		});
		//初始化页面状态
		$("[name='itemOption']").trigger("change");
	});
</script>
</head> 
<body>
<sfform:form name="notifyForm" commandName="personalNoticeDTO">
<div class="layout_div" style="padding:0;margin:0;">
<table class="infoDisTab">
<thead>
	<tr>
		<th colspan="5">
			告知对象：
			<c:choose>
				<c:when test="${personalNoticeDTO.clientOption eq 'A'}">投保人</c:when>
				<c:when test="${personalNoticeDTO.clientOption eq 'I'}">被保人${personalNoticeDTO.insuredSeq}</c:when>
			</c:choose>
			<sfform:hidden path="clientOption" />
		</th>
	</tr>
	<tr>
		<th colspan="5">
			请勾选告知序号：
			<input type="checkbox" id="checkAll" /><label for="checkAll">全选</label>
			<sfform:checkbox path="itemOption" value="1" label="1"/>
			<sfform:checkbox path="itemOption" value="2" label="2"/>
			<sfform:checkbox path="itemOption" value="3" label="3"/>
			<sfform:checkbox path="itemOption" value="4" label="4"/>
			<sfform:checkbox path="itemOption" value="5" label="5"/>
			<sfform:checkbox path="itemOption" value="6" label="6"/>
			<sfform:checkbox path="itemOption" value="7" label="7"/>
			<sfform:checkbox path="itemOption" value="8" label="8"/>
			<sfform:checkbox path="itemOption" value="9" label="9"/>
			<sfform:checkbox path="itemOption" value="10" label="10"/>
			<sfform:checkbox path="itemOption" value="11" label="11"/>
			<sfform:checkbox path="itemOption" value="12" label="12"/>
			<sfform:checkbox path="itemOption" value="13" label="13"/>
			<sfform:checkbox path="itemOption" value="14" label="14"/>
			<sfform:checkbox path="itemOption" value="15" label="15"/>
			<sfform:checkbox path="itemOption" value="16" label="16"/>
			<sfform:checkbox path="itemOption" value="17" label="17"/>
		</th>
	</tr>
</thead>
<tbody id="item">
	<tr class="odd_column item_1">
		<td colspan="3">1.告知人是否正在申请或已拥有任何保险公司或保险机构的保险合同?如是,请填下表:</td>
		<td colspan="2">
			<sfform:radiobutton path="itemAnswer_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
	</tr>
	<tr class="odd_column item_1 answerY">
		<td colspan="5">
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>承保公司/机构</th>
						<th>保险品种</th>
						<th>保险金额总数</th>
						<th>每日住院现金给付</th>
						<th>保险合同生效日期</th>
						<th class="td_widget" style="width:40px;">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${personalNoticeDTO.insProductList}" varStatus="status" var="item">
					<tr class="insProductList" title="${status.index}">
						<td class=""><sfform:input path="insProductList[${status.index}].company" cssClass="input_text {required:true}"/></td>
						<td class=""><sfform:input path="insProductList[${status.index}].insName" cssClass="input_text {required:true}"/></td>
						<td class=""><sfform:input path="insProductList[${status.index}].insSum" cssClass="input_text {required:true,number:true}"/></td>
						<td class=""><sfform:input path="insProductList[${status.index}].hospitalizationPremPerDay" cssClass="input_text {required:true,number:true}"/></td>
						<td class=""><sfform:input path="insProductList[${status.index}].effDate" cssClass="input_date {required:true} Wdate" onfocus="WdatePicker({skin:'whyGreen'});"/></td>
						<td class="td_widget">
							<a href="javascript:void(0);" class="del">删除</a>
						</td>
					</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr class="even_column">
						<td colspan="6" style="text-align:right;">
							<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add">新增</a>
						</td>
					</tr>
				</tfoot>
			</table>
		</td>
	</tr>
	<tr class="even_column item_2">
		<td>2.告知人目前的身高、体重？</td>
		<td>
			<div>身高<sfform:input path="height" cssClass="input_text {required:true,number:true}" cssStyle="width:60px"/>厘米</div>
			<div>体重<sfform:input path="weight" cssClass="input_text {required:true,number:true}" cssStyle="width:60px"/>公斤</div>
		</td>
		<td>过去的一年中体重是否有明显增减（半年内体重增减达到5公斤以上）？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_3">
		<td colspan="3">3. 告知人的人寿保险、人身意外或健康保险的投保申请是否曾被拒保、推迟、加费或作限制保障权益？是否有解除保险合同？是否曾向任何保险公司提出索赔申请？若“是”，请说明。</td>
		<td>
			<sfform:radiobutton path="itemAnswer_3" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_3" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_3" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_4">
		<td colspan="3">4. 是否计划出国或改变居住地或工作地点？正在或试图参加私人性质飞行，或携带氧气瓶潜水、或登山、或从事危险性的运动？若“是”，请填妥相关问卷，连同此告知书一并交回本公司。</td>
		<td>
			<sfform:radiobutton path="itemAnswer_4" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_4" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_4" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_5">
		<td colspan="3">5.是否持有有效摩托车驾照？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_5" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_5" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_5" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_6">
		<td colspan="3">6.&nbsp;a.是否吸烟?
			<span class="answerY">吸烟 
				<sfform:input path="smokeYear" cssClass="input_text {required:true,number:true}" cssStyle="width:50px;"/>年
				<sfform:input path="smokePerDay" cssClass="input_text {required:true,number:true}" cssStyle="width:50px;"/>支/天；
			</span>
			若现已停止吸烟，停止吸烟原因及时间
			<sfform:input path="smokeQuitReason" cssClass="input_text" cssStyle="width:150px;"/>。
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_6_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_6_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_6_1" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_6">
		<td colspan="3">
			&nbsp;&nbsp;&nbsp;b.是否饮酒?
			<span class="answerY">饮酒
				<sfform:input path="drinkYear" cssClass="input_text {required:true,number:true}" cssStyle="width:50px;"/>年，种类
				<sfform:input path="drinkType" cssClass="input_text {required:true}" cssStyle="width:80px;"/>，数量
				<sfform:input path="drinkWeightPerWeek" cssClass="input_text {required:true,number:true}" cssStyle="width:50px;"/>两/ 周；
			</span>
			若现已停止饮酒，停止饮酒原因及时间
			<sfform:input path="drinkQuitReason" cssClass="input_text" cssStyle="width:150px;"/>。
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_6_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_6_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_6_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_7">
		<td colspan="3">7.是否曾经或正在使用镇静安眠剂、可成瘾药物、麻醉剂，或接受戒毒、戒酒治疗？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_7" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_7" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_7" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_8">
		<td colspan="3">8.最近六个月内是否有医生建议您服药、住院、接受诊疗、手术或其它医疗方案？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_8" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_8" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_8" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_9">
		<td colspan="3">9.最近五年内，是否曾经作下列之一的检查，有无异常？核磁共振(MRI)、心电图、胃镜、纤维结肠镜、气管镜、CT、超声波、X光、眼底检查、脑电图、肝功能、肾功能、病理活检及其它特殊检查。</td>
		<td>
			<sfform:radiobutton path="itemAnswer_9" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_9" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_9" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">10.有否以下所列身体残障状况？<br/>
			&nbsp;&nbsp;&nbsp;a.脊柱、胸廓、四肢、手指或手掌、足趾或足部缺损畸形、两上肢或两下肢长度不等、跛行？
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_1" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;b.眼、耳、鼻、舌或其它颜面部软组织缺损畸形？牙齿脱落、上下颌骨缺失、颞下颌关节强直？肋骨骨折或缺失？颈部或腰部活动受限？肢体肌力下降？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;c.睾丸萎缩或缺失？阴茎缺失？输精管闭锁或缺失？（男性） </td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_3" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_3" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_3" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;d.子宫切除？阴道闭锁？乳房切除？（女性）</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_4" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_4" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_4" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;e.视力、听力、语言、咀嚼、吞咽、嗅觉、触觉等功能障碍或中枢神经系统障碍？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_5" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_5" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_5" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;f.精神、智能障碍或性格行为异常？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_6" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_6" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_6" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_10">
		<td colspan="3">&nbsp;&nbsp;&nbsp;g.脾、肺、胃、小肠、结肠、直肠、胰腺、肝、肾、膀胱切除？心脏的结构损伤或功能障碍？输尿管闭锁或缺失？其它内脏或身体器官缺损、摘除或移植？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_10_7" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_10_7" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_10_7" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_11">
		<td colspan="3">11.a.您及您的配偶曾接受或试图接受与艾滋病（AIDS）有关的医疗咨询、检验或治疗,或艾滋病病毒（HIV）呈阳性反应？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_11_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_11_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_11_1" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_11">
		<td colspan="3">&nbsp;&nbsp;&nbsp;b.是否曾经验血而得知为乙肝表面抗原(HbsAg)阳性反应或不宜献血？	</td>
		<td>
			<sfform:radiobutton path="itemAnswer_11_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_11_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_11_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_12">
		<td colspan="3">12.若为16周岁以上女性，请告知：<br/>
			&nbsp;&nbsp;&nbsp;a.目前是否怀孕？<span class="answerY">已怀孕<sfform:input path="fetationWeeks" cssClass="input_text {required:true,number:true}" cssStyle="width:50px;"/>周？</span>
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_12_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_12_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_12_1" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_12">
		<td colspan="3">&nbsp;&nbsp;&nbsp;b.(曾)患子宫、卵巢、乳房或其它生殖器官疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_12_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_12_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_12_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_12">
		<td colspan="3">&nbsp;&nbsp;&nbsp;c.(曾)异常妊娠、阴道异常出血或接受下腹部手术？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_12_3" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_12_3" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_12_3" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_12">
		<td colspan="3">&nbsp;&nbsp;&nbsp;d.母亲、姐妹中是否有人(曾)患乳腺、子宫、卵巢等生殖器官恶性肿瘤？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_12_4" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_12_4" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_12_4" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">13.是否患有或曾经患有以下疾病：<br/>
			&nbsp;&nbsp;&nbsp;a.最近六个月内，是否有下列疾患或自觉症状？
			<ul>
				<li>不明原因皮肤出血点或淤斑、鼻衄、反复齿龈出血？</li>
				<li>不明原因的声嘶、关节红肿酸痛、难以愈合的舌、皮肤溃疡，持续低热，体重显著减轻（短期内5公斤以上），痣的形态、大小或颜色改变、黄疸？</li>
				<li>咳嗽、痰中有血块或血丝？眼睛胀痛、视力或听力明显下降、视物不清？</li>
				<li>持续一周以上的吞咽困难、食欲不振、盗汗、腹泻、腹部不适？</li>
				<li>紫绀、胸闷、心慌、气急、心前区疼痛、反复头痛、头晕？</li>
				<li>小便困难、蛋白尿、血尿、便血、黑便、粘液便？</li>
			</ul>
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_1" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_1" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_1" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;b.视神经病变、白内障、青光眼、视网膜出血或剥离、近视800度以上？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_2" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_2" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_2" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;c. 脑血管意外及后遗症、蛛网膜下腔出血、癫痫病、帕金森氏综合症、精神病、神经麻痹、心脏病、高血压、高脂血症、血管瘤、血管疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_3" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_3" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_3" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;d.胸膜炎、肺炎、哮喘、肺结核、慢性支气管炎、支气管扩张症、肺气肿、气胸、尘肺、矽肺？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_4" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_4" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_4" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;e.慢性胃肠炎、结肠炎、消化性溃疡、消化道出血穿孔、胰腺炎、肝炎、脂肪肝、肝硬化、肝脓肿、胆道结石、胆囊炎、腹膜炎、脾肿大、肛肠疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_5" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_5" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_5" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;f.肾炎、肾病综合症、尿毒症、急性肾功能衰竭、尿路结石、肾囊肿、肾下垂、反复尿路感染、性病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_6" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_6" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_6" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;g.糖尿病、垂体、甲状腺、肾上腺疾病等内分泌系统疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_7" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_7" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_7" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;h.贫血、再生障碍性贫血、白血病、紫癜症、血友病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_8" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_8" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_8" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;i.风湿热、关节炎、类风湿性关节炎、痛风、椎间盘突出症、红斑狼疮、硬皮病、皮肌炎、重症肌无力、肌肉萎缩症、其他结缔组织疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_9" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_9" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_9" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;j.肿瘤(包括任何良性、恶性或尚未定性的肿瘤)、息肉、囊肿或增生物？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_10" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_10" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_10" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;k.先天性疾病、遗传性疾病？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_11" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_11" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_11" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;l.身体是否有瘢痕？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_12" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_12" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_12" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_13">
		<td colspan="3">&nbsp;&nbsp;&nbsp;m.除上述以外的其它疾病、症状或意外受伤史？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_13_13" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_13_13" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_13_13" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_14">
		<td colspan="3">14.直系亲属中，是否患有或曾经患有高血压、肾病、心脏病、肝炎、肝肾囊肿、肝硬化、糖尿病、精神病、癌症或早于60周岁因病身故者？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_14" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_14" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_14" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_15">
		<td colspan="3">15.若为2周岁以下婴儿，请告知：<br/>
			&nbsp;&nbsp;&nbsp;a.被保险人出生时身长
				<sfform:input path="birthHeight" cssClass="input_text {number:true,required:true} answerY" cssStyle="width:50px;"/>厘米，体重
				<sfform:input path="birthWeight" cssClass="input_text {number:true,required:true} answerY" cssStyle="width:50px;"/>公斤，出生医院
				<sfform:input path="birthHospital" cssClass="input_text {required:true} answerY" cssStyle="width:50px;"/>出生时留院天数
				<sfform:input path="birthStayHispitalDays" cssClass="input_text {number:true,required:true} answerY" cssStyle="width:50px;"/>天，如超过7天,请详细说明。<br/>
			&nbsp;&nbsp;&nbsp;b.出生时是否有早产、难产、窒息等情况？是否使用产钳等辅助器械？<br/>
			&nbsp;&nbsp;&nbsp;c.出生时是否有抢救史？<br/>
			&nbsp;&nbsp;&nbsp;d.是否未按要求接受预防接种？<br/>
			&nbsp;&nbsp;&nbsp;e.是否曾进行婴幼儿体检且结果异常？<br/>
			&nbsp;&nbsp;&nbsp;f.是否经常患腹痛、婴幼儿腹泻等消化系统疾病？<br/>
			&nbsp;&nbsp;&nbsp;g.是否曾患哮喘、肺炎、扁桃体炎等呼吸系统疾病？<br/>
			&nbsp;&nbsp;&nbsp;h.是否曾患疝气？<br/>
			&nbsp;&nbsp;&nbsp;i.是否曾出现“高热惊厥”？
		</td>
		<td>
			<sfform:radiobutton path="itemAnswer_15" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_15" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_15" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="even_column item_16">
		<td colspan="3">16.是否已参加公费医疗或社会医疗保险？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_16" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_16" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_16" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
	<tr class="odd_column item_17">
		<td colspan="3">17.您是否有其他事项告知本公司？</td>
		<td>
			<sfform:radiobutton path="itemAnswer_17" value="Y" label="是" cssClass="itemAnswer required"/>
			<sfform:radiobutton path="itemAnswer_17" value="N" label="否" cssClass="itemAnswer required"/>
		</td>
		<td>
			<sfform:textarea path="itemRemark_17" rows="2" cols="20" cssClass="multi_text answerY required" />
		</td>
	</tr>
</tbody>
</table>
<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-cancel" id="cancel">取消</a>
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
			</td>
		</tr>
</table>
</div>
</sfform:form>
</body>
</html>