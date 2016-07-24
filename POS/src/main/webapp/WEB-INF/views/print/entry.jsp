<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#batchPrintTaskTable").datagrid({
				title:"批次打印任务列表",
				url:"${ctx}print/batchPrintTaskList.do",
				pagination:true,
				rownumbers:true,
				singleSelect:true,
				striped:true,
				columns:[[{field:"taskId", title:"任务ID", width:135},
				         {field:"submitDate", title:"提交时间", formatter:function(value, rowData, rowIndex){
				        	 	return $.format.date(new Date(value), "yyyy-MM-dd HH:mm:ss");
				         	}, width:120},
				         {field:"queryNoticeType", title:"任务类型", formatter:function(value, rowData, rowIndex){
					        	return ({"ent":"保全变更批单（针式打印）",
					        			"ent1":"保全变更批单（激光打印）",
					        			"1" : "现金分红红利通知信",
			        		         	"2" : "保额分红红利通知信",
					        		    "3" : "保险合同到期终止通知书",
					        		    "4" : "保险合同失效通知书",
					        		    "5" : "续期追加保险费通知单",
					        		    "6" : "保单贷款清偿提醒函",
					        		    "7" : "保单贷款即将逾期提醒函",
					        		    "8" : "险种满期通知书",
					        		    "9" : "给付通知书",
					        		    "10" : "个人保单年度报告书",
					        		    "11" : "投资连结保险周年报告",
					        		    "51" : "现金分红红利通知信(银代渠道)",
					        		    "52" : "现金分红红利通知信(非银代渠道)",
					        		    "53" : "保额分红红利通知信(银代渠道)",
					        		    "54" : "保额分红红利通知信(非银代渠道)"})[value];
				         	}, width:180},
				         {field:"genRecordNum", title:"批次抽取数量", width:90},
				         {field:"taskStatus", title:"任务状态", width:90, formatter:function(value, rowData, rowIndex){return {"1":"待处理","2":"处理中","3":"处理成功","4":"处理失败"}[value];}},
				         {field:"taskFailReason", title:"失败原因", width:120, formatter:function(value, rowData, rowIndex){return {"1":"PDF生成失败","2":"文件上传至IM失败","3":"邮件发送失败","4":"系统异常"}[value];}},
				         {field:"task", title:"操作", width:90, formatter:function(value, rowData, rowIndex){
				        	var content = "";
				        	if(rowData.imfileId) {
				        		content += "<a href=\"javascript:void{0)\" class=\"downloadLink\" title=\"" + rowData.taskId + "\">下载</a> &nbsp;";
				        	}
				        	content += "<a href=\"javascript:void(0)\" class=\"deleteLink\" title=\"" + rowData.taskId + "\">删除</a>";
				        	return content;
				         }}
				        ]
				]
			});
			$(".downloadLink").live("click", function(){
				var $this = $(this);
				$.post("${ctx}print/getDownloadURL.do", {taskId : $this.attr("title")}, function(data){
					if(!data || data.flag != "Y") {
						$.messager.alert("提示", data&&data.message ? data.message : "获取地址失败。");
					} else {
						window.open(data.url);
					}
				});
				return false;
			});
			$(".deleteLink").live("click", function(){
				var $this = $(this);
				$.post("${ctx}print/deleteBatchPrintTaskById.do", {taskId : $this.attr("title")}, function(data){
					if(!data || data.flag != "Y") {
						$.messager.alert("提示", data&&data.message ? data.message : "删除失败。");
					} else {
						$.messager.alert("提示", "删除成功");
						$("#batchPrintTaskTable").datagrid("reload");
					}
				});
				return false;
			});
		});
	</script>
	<style type="text/css">
	</style>
</sf:override>
<style type="text/css">
#menu_ul li {
	line-height: 30px;
	font-size:15px;
}
</style>
<sf:override name="content">
  <ul id="menu_ul">
    <li><a href="${ctx}print/endorsement_batch.do">批单批次打印</a></li> 
    <li><a href="${ctx}print/endorsement_single.do">批单逐单打印</a></li>
    <li><a href="${ctx}print/notice_batch.do">通知书批次打印</a></li>
    <li><a href="${ctx}print/notice_single.do">通知书逐单打印</a></li>
    <li><a href="${ctx}print/big_refund_application_form.do">业务退费申请单打印</a></li>
    <li><a href="${ctx}print/application.do">免填单申请书打印</a></li>
    <li><a href="${ctx}print/clientWealthNotice.do">投保人名下保单资产证明书打印</a></li>
    <!-- li><a href="${ctx}print/policy_single.do">保单打印</a></li -->
  </ul>
 	<div class="layout_div_top_spance">
		<table width="100%" id="batchPrintTaskTable" class="easyui-datagrid"></table>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
