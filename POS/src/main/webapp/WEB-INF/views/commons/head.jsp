<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<sf:JsCssLib libType="css">
	<sf:JsCssFile file="CacheableResource/css_v1_01/layout.css"/>
	<sf:JsCssFile file="CacheableResource/themes_v1_01/default/easyui.css"/>
	<sf:JsCssFile file="CacheableResource/themes_v1_01/base/jquery.ui.core.css"/>
	<sf:JsCssFile file="CacheableResource/themes_v1_01/base/jquery.ui.autocomplete.css"/>
	<sf:JsCssFile file="CacheableResource/themes_v1_01/base/jquery.ui.theme.css"/>
	<sf:JsCssFile file="CacheableResource/themes_v1_01/icon.css"/>
	<sf:JsCssFile file="CacheableResource/pagination_v1_01/pagination.css"/>
	<sf:JsCssFile file="CacheableResource/sortTable_v1_01/sortTable.css"/>
</sf:JsCssLib>

<sf:JsCssLib libType="js">
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery-1.4.4.min.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.easyui.min.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.bgiframe.js"/>
	<sf:JsCssFile file="sf_home/resources/Zxb.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.ui.core.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.ui.widget.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.ui.position.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.ui.autocomplete.js"/>
	<sf:JsCssFile file="CacheableResource/pagination_v1_01/pagination.js"/>
	<sf:JsCssFile file="CacheableResource/sortTable_v1_01/sortTable.js"/>
	<sf:JsCssFile file="CacheableResource/locale_v1_01/easyui-lang-zh_CN.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.blockUI.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/jquery-validate/jquery.validate.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/jquery-validate/lib/jquery.metadata.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/jquery-validate/lib/jquery.form.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/jquery-validate/additional-methods.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/jquery-validate/localization/messages_cn.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/ajax_file_uploader_v2_1/ajaxfileupload.js"/>
	<sf:JsCssFile file="CacheableResource/commons_v1_01/jquery.dateFormat-1.0.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/pos/pos-validation.js"/>
	<sf:JsCssFile file="CacheableResource/javascript_v1_01/pos/pos-common.js"/>
	<sf:JsCssFile file="sf_home/resources/sf-jquery-patch.js"/>
</sf:JsCssLib>

<script type="text/javascript" src="${ctx}CacheableResource/DatePicker_v1_01/WdatePicker.js"></script>
<script type="text/javascript">
	
//屏蔽各种键的功能,_sf_right_menu_enable的值是设置在综合业务平台里的
if(${_sf_right_menu_enable}==false){
	document.oncontextmenu=function(){return false;};
	document.onkeydown=framweorkKeyDown;
}
function framweorkKeyDown(){ 
	try {
		if ((window.event.altKey)&&((window.event.keyCode==37)||(window.event.keyCode==39))){
			event.returnValue=false;
		}
		if (event.keyCode==8&&((event.srcElement.readOnly==null||event.srcElement.readOnly == true)||(event.srcElement.type!='text'&&event.srcElement.type!='textarea'&&event.srcElement.type!='password'))){ 
			event.keyCode=0;
			event.returnValue=false;
		}
		if (event.keyCode==116){ 
			event.keyCode=0;
			event.returnValue=false;
		}
		if ((event.ctrlKey)&&(event.keyCode==78)){
			event.returnValue=false;
		}
		if ((event.shiftKey)&&(event.keyCode==121)){
			event.returnValue=false;
		}
	} catch(err) {}
}
</script>

<%--
<link rel="stylesheet" type="text/css" href="${ctx}resources/css/layout.css" /> <!-- 新定义的css文件 -->
<link rel="stylesheet" type="text/css" href="${ctx}resources/themes/default/easyui.css" /><!-- easyui的css文件 --> 
<link rel="stylesheet" type="text/css" href="${ctx}resources/themes/base/jquery.ui.all.css" /><!-- jquery ui的css文件，使用blockUI和autocomplete控件才引用此文件，如不使用这俩个控件，则不引用-->
<link rel="stylesheet" type="text/css" href="${ctx}resources/themes/icon.css" /><!--easyui的iocn图标文件-->
<link rel="stylesheet" type="text/css" href="${ctx}sf_home/resources/pagination/pagination.css" /><!--新定义的分页控件的css文件,不使用该功能可以不引用 -->
<link rel="stylesheet" type="text/css" href="${ctx}sf_home/resources/sortTable/sortTable.css" /><!--新定义的分页控件的css文件,不使用该功能可以不引用 -->

<script type="text/javascript" src="${ctx}resources/commons/jquery-1.4.4.min.js"></script><!--jquery库文件 -->
<script type="text/javascript" src="${ctx}resources/commons/jquery.easyui.min.js"></script><!--jquery easyui库文件--> 
<script type="text/javascript" src="${ctx}sf_home/resources/Zxb.js"></script><!-- 自定义的共用js函数库 -->
<script type="text/javascript" src="${ctx}resources/commons/jquery.ui.core.js"></script><!--autocomplete控件， 如不使用这俩个控件，则不引用-->
<script type="text/javascript" src="${ctx}resources/commons/jquery.ui.widget.js"></script><!--autocomplete控件， 如不使用这俩个控件，则不引用-->
<script type="text/javascript" src="${ctx}resources/commons/jquery.ui.position.js"></script><!--autocomplete控件， 如不使用这俩个控件，则不引用-->
<script type="text/javascript" src="${ctx}resources/commons/jquery.ui.autocomplete.js"></script><!--autocomplete控件， 如不使用这俩个控件，则不引用-->
<script type="text/javascript" src="${ctx}sf_home/resources/pagination/pagination.js"></script><!--新定义的分页控件的js文件 ，不使用该功能可以不引用-->
<script type="text/javascript" src="${ctx}sf_home/resources/sortTable/sortTable.js"></script><!--新定义数据表格排序js文件 ，不使用该功能可以不引用-->
<script type="text/javascript" src="${ctx}resources/locale/easyui-lang-zh_CN.js"></script><!--jquery easyui的中文化文件 -->
<script type="text/javascript" src="${ctx}resources/commons/jquery.blockUI.js"></script><!-- 防重复提交的blockUI -->
<script type="text/javascript" src="${ctx}sf_home/resources/sf-jquery-patch.js"></script><!-- session超时后可以重新登录 -->

<script type="text/javascript" src="${ctx}resources/javascript/jquery-validate/jquery.validate.js"></script>
<script type="text/javascript" src="${ctx}resources/javascript/jquery-validate/lib/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx}resources/javascript/jquery-validate/lib/jquery.form.js"></script>
<script type="text/javascript" src="${ctx}resources/javascript/jquery-validate/additional-methods.js"></script>
<script type="text/javascript" src="${ctx}resources/javascript/jquery-validate/localization/messages_cn.js"></script>
<script type="text/javascript" src="${ctx}resources/commons/jquery.dateFormat-1.0.js"></script>

<script type="text/javascript" src="${ctx}resources/javascript/pos/pos-validation.js"></script>
<script type="text/javascript" src="${ctx}resources/javascript/pos/pos-common.js"></script>

<script type="text/javascript" src="${ctx}resources/javascript/DatePicker/WdatePicker.js"></script>
--%>