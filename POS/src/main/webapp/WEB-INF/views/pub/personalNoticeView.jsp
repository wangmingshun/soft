<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>最新个人资料告知</title>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<style type="text/css">
table.form>tbody>tr>th {
	text-align:right;
	vertical-align:top;
}
table.form>tbody>tr>td {
	text-align:left;
	vertical-align:top;
}
</style>
<script type="text/javascript" language="javascript">
	$(function(){
		$("#ok").click(function(){
			<c:if test="${not empty param.callback}">
				window.parent["${param.callback}"]();
			</c:if>
			<c:if test="${not empty param.windowMode}">
				window.close();
			</c:if>
			return false;
		});
		
		$("#products>tbody>tr:odd").removeClass().addClass("odd_column");
		$("#products>tbody>tr:even").removeClass().addClass("even_column");
		var groupNo = null;
		var cls = "even_column";
		var clsArr, i, tmpGroup;
		$("#mainTable>tbody tr").each(function(idx, content){
			clsArr = $(content).attr("class").split(/\s+/);
			for(i = 0; clsArr && i < clsArr.length; i++) {
				if(clsArr[i].substr(0, 5) == "group") {
					tmpGroup = parseInt(clsArr[i].replace("group", ""));
					if(groupNo == tmpGroup) {
						$(content).addClass(cls);
					} else {
						groupNo = tmpGroup;
						if(cls == "even_column") {
							cls = "odd_column";
						} else {
							cls = "even_column";
						}
						$(content).addClass(cls);
					}
					return;
				}
			}
			$(content).addClass(cls);
		});
		$("#tabContainer").css("display", "").tabs();
	});
</script>
</head> 
<body style="margin:0; padding: 1px 1px 1px 1px;">
<div class="layout_div" style="padding:0;margin:0;">
<c:if test="${not empty message}">${message}</c:if>
<div id="tabContainer" style="display:none;">
	<c:forEach varStatus="status" items="${personalNoticeList}" var="personalNotice">
		<div title="${personalNotice.clientOption eq 'A' ? '投保人' : '被保人'}最新个人资料告知" style="padding:0;" class="tabDiv">
			<div class="navigation_div">
				<span class="font_heading1">
					客户号：<span style="color:blue;">${personalNotice.clientInfo.clientNo}</span>&nbsp;&nbsp;
					客户姓名：<span style="color:blue;">${personalNotice.clientInfo.clientName}</span>&nbsp;&nbsp;
					保全项目：<span style="color:blue;">${personalNotice.posInfo.posNo}(${personalNotice.posInfo.serviceItemsDesc})</span>
				</span>
			</div>
			<table class="infoDisTab" id="mainTable">
			<thead>
				<tr>
					<th colspan="3">告知项</th>
					<th style="width:50px;">客户回答</th>
					<th style="width:200px;">补充说明</th>
				</tr>
			</thead>
			<tbody>
			<c:if test="${personalNotice.itemOptionList[1]}">
				<tr class="group1">
					<td colspan="3">1.告知人是否正在申请或已拥有任何保险公司或保险机构的保险合同?</td>
					<td colspan="2">${personalNotice.itemAnswer_1 eq "Y" ? "是" : "否"}</td>
				</tr>
				<c:if test="${personalNotice.itemAnswer_1 eq 'Y'}">
					<tr class="group1">
						<td colspan="5">
							<table class="infoDisTab" id="products">
								<thead>
									<tr>
										<th>承保公司/机构</th>
										<th>保险品种</th>
										<th>保险金额总数</th>
										<th>每日住院现金给付</th>
										<th>保险合同生效日期</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${personalNotice.insProductList}" varStatus="status" var="item">
									<tr>
										<td>${item.company}</td>
										<td>${item.insName}</td>
										<td>${item.insSum}</td>
										<td>${item.hospitalizationPremPerDay}</td>
										<td><fmt:formatDate value="${item.effDate}" pattern="yyyy-MM-dd"/></td>
									</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[2]}">
				<tr class="group2">
					<td>2.告知人目前的身高、体重？</td>
					<td>
						<div>身高<span style="color:blue;">${personalNotice.height}</span>厘米</div>
						<div>体重<span style="color:blue;">${personalNotice.weight}</span>公斤</div>
					</td>
					<td>过去的一年中体重是否有明显增减（半年内体重增减达到5公斤以上）？</td>
					<td>${personalNotice.itemAnswer_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_2}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[3]}">
				<tr class="group3">
					<td colspan="3">3. 告知人的人寿保险、人身意外或健康保险的投保申请是否曾被拒保、推迟、加费或作限制保障权益？是否有解除保险合同？是否曾向任何保险公司提出索赔申请？</td>
					<td>${personalNotice.itemAnswer_3 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_3}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[4]}">
				<tr class="group4">
					<td colspan="3">4. 是否计划出国或改变居住地或工作地点？正在或试图参加私人性质飞行，或携带氧气瓶潜水、或登山、或从事危险性的运动？若“是”，请填妥相关问卷，连同此告知书一并交回本公司。</td>
					<td>${personalNotice.itemAnswer_4 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_4}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[5]}">
				<tr class="group5">
					<td colspan="3">5.是否持有有效摩托车驾照？</td>
					<td>${personalNotice.itemAnswer_5 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_5}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[6]}">
				<tr class="group6">
					<td colspan="3">6.&nbsp;a.是否吸烟?
						<c:if test="${personalNotice.itemAnswer_6_1 eq 'Y'}">
							吸烟 <span style="color:blue;">${personalNotice.smokeYear}</span>年
							<span style="color:blue;">${personalNotice.smokePerDay}</span>支/天；
						</c:if>
						若现已停止吸烟，停止吸烟原因及时间
						<span style="color:blue;">${personalNotice.smokeQuitReason}</span>。
					</td>
					<td>${personalNotice.itemAnswer_6_1 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_6_1}</td>
				</tr>
				<tr class="group6">
					<td colspan="3">
						&nbsp;&nbsp;&nbsp;b.是否饮酒?
						<c:if test="${personalNotice.itemAnswer_6_2 eq 'Y'}">
							饮酒<span style="color:blue;">${personalNotice.drinkYear}</span>年，种类
							<span style="color:blue;">${personalNotice.drinkType}</span>，数量
							<span style="color:blue;">${personalNotice.drinkWeightPerWeek}</span>两/ 周；
						</c:if>
						若现已停止饮酒，停止饮酒原因及时间
						<span style="color:blue;">${personalNotice.drinkQuitReason}</span>。
					</td>
					<td>${personalNotice.itemAnswer_6_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_6_2}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[7]}">
				<tr class="group7">
					<td colspan="3">7.是否曾经或正在使用镇静安眠剂、可成瘾药物、麻醉剂，或接受戒毒、戒酒治疗？</td>
					<td>${personalNotice.itemAnswer_7 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_7}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[8]}">
				<tr class="group8">
					<td colspan="3">8.最近六个月内是否有医生建议您服药、住院、接受诊疗、手术或其它医疗方案？</td>
					<td>${personalNotice.itemAnswer_8 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_8}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[9]}">
				<tr class="group9">
					<td colspan="3">9.最近五年内，是否曾经作下列之一的检查，有无异常？核磁共振(MRI)、心电图、胃镜、纤维结肠镜、气管镜、CT、超声波、X光、眼底检查、脑电图、肝功能、肾功能、病理活检及其它特殊检查。</td>
					<td>${personalNotice.itemAnswer_9 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_9}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[10]}">
				<tr class="group10">
					<td colspan="3">10.有否以下所列身体残障状况？<br/>
						&nbsp;&nbsp;&nbsp;a.脊柱、胸廓、四肢、五官、手指或足趾缺损畸形、跛行
					</td>
					<td>${personalNotice.itemAnswer_10_1 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_10_1}</td>
				</tr>
				<tr class="group10">
					<td colspan="3">&nbsp;&nbsp;&nbsp;b.视力、听力、语言、咀嚼、嗅觉、触觉等功能障碍或中枢神经系统障碍</td>
					<td>${personalNotice.itemAnswer_10_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_10_2}</td>
				</tr>
				<tr class="group10">
					<td colspan="3">&nbsp;&nbsp;&nbsp;c.精神、智能障碍或性格行为异常</td>
					<td>${personalNotice.itemAnswer_10_3 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_10_3}</td>
				</tr>
				<tr class="group10">
					<td colspan="3">&nbsp;&nbsp;&nbsp;d.内脏或其它身体器官缺损、摘除或移植？</td>
					<td>${personalNotice.itemAnswer_10_4 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_10_4}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[11]}">
				<tr class="group11">
					<td colspan="3">11.a.您及您的配偶曾接受或试图接受与艾滋病（AIDS）有关的医疗咨询、检验或治疗,或艾滋病病毒（HIV）呈阳性反应？</td>
					<td>${personalNotice.itemAnswer_11_1 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_11_1}</td>
				</tr>
				<tr class="group11">
					<td colspan="3">&nbsp;&nbsp;&nbsp;b.是否曾经验血而得知为乙肝表面抗原(HbsAg)阳性反应或不宜献血？	</td>
					<td>${personalNotice.itemAnswer_11_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_11_2}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[12]}">
				<tr class="group12">
					<td colspan="3">12.若为16周岁以上女性，请告知：<br/>
						&nbsp;&nbsp;&nbsp;a.目前是否怀孕？
							<c:if test="${personalNotice.itemAnswer_12_1 eq 'Y'}">
								已怀孕<span style="color:blue;">${personalNotice.fetationWeeks}</span>周？
							</c:if>
					</td>
					<td>${personalNotice.itemAnswer_12_1 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_12_1}</td>
				</tr>
				<tr class="group12">
					<td colspan="3">&nbsp;&nbsp;&nbsp;b.(曾)患子宫、卵巢、乳房或其它生殖器官疾病？</td>
					<td>${personalNotice.itemAnswer_12_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_12_2}</td>
				</tr>
				<tr class="group12">
					<td colspan="3">&nbsp;&nbsp;&nbsp;c.(曾)异常妊娠、阴道异常出血或接受下腹部手术？</td>
					<td>${personalNotice.itemAnswer_12_3 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_12_3}</td>
				</tr>
				<tr class="group12">
					<td colspan="3">&nbsp;&nbsp;&nbsp;d.母亲、姐妹中是否有人(曾)患乳腺、子宫、卵巢等生殖器官恶性肿瘤？</td>
					<td>${personalNotice.itemAnswer_12_4 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_12_4}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[13]}">
				<tr class="group13">
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
					<td>${personalNotice.itemAnswer_13_1 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_1}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;b.视神经病变、白内障、青光眼、视网膜出血或剥离、近视800度以上？</td>
					<td>${personalNotice.itemAnswer_13_2 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_2}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;c. 脑血管意外及后遗症、蛛网膜下腔出血、癫痫病、帕金森氏综合症、精神病、神经麻痹、心脏病、高血压、高脂血症、血管瘤、血管疾病？</td>
					<td>${personalNotice.itemAnswer_13_3 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_3}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;d.胸膜炎、肺炎、哮喘、肺结核、慢性支气管炎、支气管扩张症、肺气肿、气胸、尘肺、矽肺？</td>
					<td>${personalNotice.itemAnswer_13_4 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_4}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;e.慢性胃肠炎、结肠炎、消化性溃疡、消化道出血穿孔、胰腺炎、肝炎、脂肪肝、肝硬化、肝脓肿、胆道结石、胆囊炎、腹膜炎、脾肿大、肛肠疾病？</td>
					<td>${personalNotice.itemAnswer_13_5 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_5}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;f.肾炎、肾病综合症、尿毒症、急性肾功能衰竭、尿路结石、肾囊肿、肾下垂、反复尿路感染、性病？</td>
					<td>${personalNotice.itemAnswer_13_6 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_6}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;g.糖尿病、垂体、甲状腺、肾上腺疾病等内分泌系统疾病？</td>
					<td>${personalNotice.itemAnswer_13_7 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_7}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;h.贫血、再生障碍性贫血、白血病、紫癜症、血友病？</td>
					<td>${personalNotice.itemAnswer_13_8 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_8}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;i.风湿热、关节炎、类风湿性关节炎、痛风、椎间盘突出症、红斑狼疮、硬皮病、皮肌炎、重症肌无力、肌肉萎缩症、其他结缔组织疾病？</td>
					<td>${personalNotice.itemAnswer_13_9 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_9}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;j.肿瘤(包括任何良性、恶性或尚未定性的肿瘤)、息肉、囊肿或增生物？</td>
					<td>${personalNotice.itemAnswer_13_10 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_10}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;k.先天性疾病、遗传性疾病？</td>
					<td>${personalNotice.itemAnswer_13_11 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_11}</td>
				</tr>
				<tr class="group13">
					<td colspan="3">&nbsp;&nbsp;&nbsp;l.除上述以外的其它疾病、症状或意外受伤史？</td>
					<td>${personalNotice.itemAnswer_13_12 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_13_12}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[14]}">
				<tr class="group14">
					<td colspan="3">14.直系亲属中，是否患有或曾经患有高血压、肾病、心脏病、肝炎、肝肾囊肿、肝硬化、糖尿病、精神病、癌症或早于60周岁因病身故者？</td>
					<td>${personalNotice.itemAnswer_14 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_14}</td>
				</tr>
			</c:if>
			<c:if test="${personalNotice.itemOptionList[15]}">
				<tr class="group15">
					<td colspan="3">15.若为2周岁以下婴儿，请告知：<br/>
						&nbsp;&nbsp;&nbsp;a.被保险人出生时身长<span style="color:blue;">${personalNotice.birthHeight}</span>厘米，体重
							<span style="color:blue;">${personalNotice.birthWeight}</span>公斤，出生医院
							<span style="color:blue;">${personalNotice.birthHospital}</span>出生时留院天数
							<span style="color:blue;">${personalNotice.birthStayHispitalDays}</span>天，如超过7天,请详细说明。<br/>
						&nbsp;&nbsp;&nbsp;b.出生时是否有早产、难产、窒息等情况？是否使用产钳等辅助器械？<br/>
						&nbsp;&nbsp;&nbsp;c.出生时是否有抢救史？<br/>
						&nbsp;&nbsp;&nbsp;d.是否未按要求接受预防接种？<br/>
						&nbsp;&nbsp;&nbsp;e.是否曾进行婴幼儿体检且结果异常？<br/>
						&nbsp;&nbsp;&nbsp;f.是否经常患腹痛、婴幼儿腹泻等消化系统疾病？<br/>
						&nbsp;&nbsp;&nbsp;g.是否曾患哮喘、肺炎、扁桃体炎等呼吸系统疾病？<br/>
						&nbsp;&nbsp;&nbsp;h.是否曾患疝气？<br/>
						&nbsp;&nbsp;&nbsp;i.是否曾出现“高热惊厥”？
					</td>
					<td>${personalNotice.itemAnswer_15 eq "Y" ? "是" : "否"}</td>
					<td>${personalNotice.itemRemark_15}</td>
				</tr>
			</c:if>
			</tbody>
			</table>
		</div>
	</c:forEach>
</div>
<c:if test="${not empty param.callback or not empty param.windowMode}">
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
			</td>
		</tr>
	</table>
</c:if>
</div>
</body>
</html>