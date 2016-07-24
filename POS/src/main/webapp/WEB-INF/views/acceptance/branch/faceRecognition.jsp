<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>
		人脸识别功能
	</title>
	<meta name="GENERATOR" content="IPage5 4.0">
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/css_v1_01/layout.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/themes_v1_01/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/themes_v1_01/base/jquery.ui.core.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/themes_v1_01/base/jquery.ui.autocomplete.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/sortTable_v1_01/sortTable.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/themes_v1_01/icon.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}CacheableResource/pagination_v1_01/pagination.css" />
	<script type="text/javascript" src="${ctx}CacheableResource/pagination_v1_01/pagination.js"></script>
	<script type="text/javascript" src="${ctx}resources/commons/jquery-1.4.4.min.js"></script>
</head>

<body>
<div id="recognitionDiv" class="layout_div" style="padding:0;margin:0;">
	  <input type="hidden" id="getImgBase64" name="getImgBase64" />
		<input type="hidden" id="imgBase64" name="imgBase64" />
		<input type="hidden" id="TxtPara" name="TxtPara" value="${acceptance__item_input.faceControlParameter}"/>
		<input type="hidden" id="TxtHint" name="TxtHint" value=""/>
		<input type="hidden" id="check001" name="check001" />
		<input type="hidden" id="check002" name="check002" />
		<input type="hidden" id="controlVersion" name="controlVersion" />
	    <input type="hidden" name="clientNoApp" id="clientNoApp" value="" />
		<input type="hidden" name="serviceItems" id="serviceItems" value="${acceptance__item_input.serviceItems}" />
		<input type="hidden" name="posNo" id="posNo" value="${acceptance__item_input.posNo}" />
    <div class="easyui-layout" fit="true">
    	<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
        	<Table width="800"  border="0" align="center" Valign="Top"
						cellpadding="0" cellspacing="0" id="Table1">
						<tr>
							<td height=10 width=100%></td>
						</tr>
						<tr>
							<td>
								<table>
									<tr>
										<td class="layouttable_td_label">姓名:</td>
										<td class="layouttable_td_widget"><select id="clientNo">
																												<option value="" >--------请选择--------</option>
																												<c:if test="${not acceptance__item_input.appInsTheSame}"><option value="${acceptance__item_input.clientNoApp}" >${acceptance__item_input.clientInfoAppList[0].clientName}</option></c:if>
																												<c:forEach items="${acceptance__item_input.clientNoInsList}" var="item">
																													<option value="${item.insuredNo }" >${item.insuredName}</option>
																											  </c:forEach>
																										  </select></td>
									</tr>	
									<tr>
										<td class="layouttable_td_label">性别:</td>
										<td class="layouttable_td_widget"><input type="text" id="sexDesc" readonly/></td>
									</tr>	
									<tr>
										<td class="layouttable_td_label">证件类型代码:</td>
										<td class="layouttable_td_widget"><input type="text" id="idTypeDesc" readonly/></td>
									</tr>	
									<tr>
										<td class="layouttable_td_label">证件号码:</td>
										<td class="layouttable_td_widget"><input type="text" id="idNo" readonly/></td>
									</tr>	
									<tr>
										<td class="layouttable_td_label">生日:</td>
										<td class="layouttable_td_widget"><input type="text" id="birthday" readonly/></td>
									</tr>
							 	</table>
						 	</td>
							<td >
								<div><OBJECT Name="TFace1" ID="TFace1" CLASSID="CLSID:{FC3899CF-1DDA-4F3D-917C-AA7A7385320A}" CODEBASE = "${acceptance__item_input.irsControlCAB}" width = 500 height = 300> </OBJECT>&nbsp;&nbsp;
						         <OBJECT Name="TFace2" ID="TFace2" CLASSID="CLSID:{FC3899CF-1DDA-4F3D-917C-AA7A7385320A}" CODEBASE = "${acceptance__item_input.irsControlCAB}" width = 500 height = 300> </OBJECT>&nbsp;&nbsp;
						    </div>
							</td>
						</tr>
					</Table>
					<Table width="800"  border="0" align="center" Valign="Top"
						cellpadding="0" cellspacing="0" id="Table2">
						<tr>
							<td align="right" valign="center">
							<div id="MainScr" style="position: relative;">
							<!-- <INPUT TYPE="button" value="手工抓拍人脸" onclick="javascript:Run_Func(2);" id="button1">
							&nbsp;  -->
							<INPUT TYPE="button" value="自动抓拍人脸" onclick="javascript:Run_Func(0);" id="button2"> 
							&nbsp;
							<INPUT TYPE="button" value="进行人脸识别" onclick="javascript:compareInfo();" id="faceRecognitionBt">
							<!--<a href="${ctx}acceptance/branch/downloadTFace.do" id="downloadTFace" iconCls="icon-print" class="easyui-linkbutton">下载插件(控件加载慢)</a>--></div>
							</td>
						</tr>
					</Table>
		</div>
        <!--<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
        		 
        </div>-->
    </div>
</div>
</body>
<script type="text/javascript">
	
	$(function(){
		$("#clientNo").change(function(event) {
			var clientNoApp = $("#clientNo").val();
			$("#clientNoApp").val(clientNoApp);
			if(clientNoApp!=null&&clientNoApp!="")
			{
				$.ajax({
						type : "post",
						dataType : "json",
						async : true,
						data : {
							clientNo : clientNoApp
						},
						url : "${ctx}acceptance/branch/getClientInfoByClientNo.do",
						success : function(data) {
							$("#sexDesc").val(data.sexDesc);
							$("#idTypeDesc").val(data.idTypeDesc);
							$("#idNo").val(data.idNo);
							$("#birthday").val(data.birthday);
						}
					});
				}else{
							$("#sexDesc").val("");
							$("#idTypeDesc").val("");
							$("#idNo").val("");
							$("#birthday").val("");
				}
		});
	});
	var sRet = null;
	var sPara = null;
	// TFace1视频框做采集用 TFace2视频框做显示图像用      先把TFace2隐藏
	document.getElementById("TFace1").style.display = "";
	document.getElementById("TFace2").style.display = "none";
	//打开摄像头
	function openDev(iIdx) {
		document.getElementById("TFace1").style.display = "";
		document.getElementById("TFace2").style.display = "none";
		document.getElementById("TxtHint").value = "";
		sPara = document.getElementById("TxtPara").value;
		sRet = TFace1.openDevice(sPara);
		//解析返回xml串，如成功自动显示抓拍图像
		//解析xml
		try {//IE
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async = "false";
			xmlDoc.loadXML(sRet);
		} catch (e) {
			try //Firefox, Mozilla, Opera, etc.
			{
				parser = new DOMParser();
				xmlDoc = parser.parseFromString(sRet, "text/xml");
			} catch (e) {
				alert(e.message);
			}
		}
		//解析出resultCode ，如果resultCode=0  则关闭摄像头,并显示图像  否则不关闭
		var res = xmlDoc.getElementsByTagName("resultCode")[0].childNodes[0].nodeValue;
		var resultMsg = xmlDoc.getElementsByTagName("resultMsg")[0].childNodes[0].nodeValue;
		if(res == 0){
			$("#check002").val("Y");
		}else{
			$("#check002").val("N");
		}
	}

	//关闭摄像头
	function closeDev(iIdx) {
		
		document.getElementById("TFace1").style.display = "";
		document.getElementById("TFace2").style.display = "none";
		sRet = TFace1.closeDevice();
	}

	//检测摄像头状态
	function checkDev(iIdx) {
		
		document.getElementById("TFace1").style.display = "";
		document.getElementById("TFace2").style.display = "none";
		document.getElementById("TxtHint").value = "";
		sPara = document.getElementById("TxtPara").value;
		sRet = TFace2.checkDevice(sPara);
		//解析返回xml串，如成功自动显示抓拍图像
		//解析xml
		try {//IE
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async = "false";
			xmlDoc.loadXML(sRet);
		} catch (e) {
			try //Firefox, Mozilla, Opera, etc.
			{
				parser = new DOMParser();
				xmlDoc = parser.parseFromString(sRet, "text/xml");
			} catch (e) {
				alert(e.message);
			}
		}
		//解析出resultCode ，如果resultCode=0  则关闭摄像头,并显示图像  否则不关闭
		var res = xmlDoc.getElementsByTagName("resultCode")[0].childNodes[0].nodeValue;
		var resultMsg = xmlDoc.getElementsByTagName("resultMsg")[0].childNodes[0].nodeValue;
		if(res == 0){
			$("#check001").val("Y");
		}else{
			$("#check001").val("N");
		}
		//解析控件参数xml
		try {//IE
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async = "false";
			xmlDoc.loadXML(sPara);
		} catch (e) {
			try //Firefox, Mozilla, Opera, etc.
			{
				parser = new DOMParser();
				xmlDoc = parser.parseFromString(sPara, "text/xml");
			} catch (e) {
				alert(e.message);
			}
		}
		//解析出控件版本
		var version = xmlDoc.getElementsByTagName("version")[0].childNodes[0].nodeValue;
		$("#controlVersion").val(version);
	}
	function Run_Func(iIdx) {
		document.getElementById("TFace1").style.display = "";
		document.getElementById("TFace2").style.display = "none";
		sRet = TFace1.closeDevice();
		//document.getElementById("button1").disabled=true;
		document.getElementById("button2").disabled=true;
		//检查摄像头是否成功 Y成功 N失败
		checkDev();
		var check001 = $("#check001").val();
		if(check001=="N"){
			alert("打开摄像头失败！");
			//document.getElementById("button1").disabled=false;
			document.getElementById("button2").disabled=false;
			return false;
		}
		
		//document.getElementById("button1").disabled=false;
		document.getElementById("button2").disabled=false;
		if (iIdx == 0) // 自动抓拍人脸图像
		{

			document.getElementById("TFace1").style.display = "";
			document.getElementById("TFace2").style.display = "none";
			document.getElementById("TxtHint").value = "";
			sPara = document.getElementById("TxtPara").value;
			//sRet = TFace1.getFaceB64(sPara);
			//20140605 TCCZY 身份证号和流水号由单独的参数传入
			sRet = TFace1.getFaceB64A(sPara, "123456789012345678",
					"12345678901234567890123456789012");
			document.getElementById("TxtHint").value = sRet;
			//解析返回xml串，如成功自动显示抓拍图像
			//解析xml
			try {//IE
				xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
				xmlDoc.async = "false";
				xmlDoc.loadXML(sRet);
			} catch (e) {
				try //Firefox, Mozilla, Opera, etc.
				{
					parser = new DOMParser();
					xmlDoc = parser.parseFromString(sRet, "text/xml");
				} catch (e) {
					alert(e.message);
				}
			}

			//解析出resultCode ，如果resultCode=0  则关闭摄像头,并显示图像  否则不关闭
			var res = xmlDoc.getElementsByTagName("resultCode")[0].childNodes[0].nodeValue;

			if (res == 0) {
				TFace1.closeDevice();//关闭摄像头
				document.getElementById("TFace1").style.display = "none";
				document.getElementById("TFace2").style.display = "";
				$("#getImgBase64").val(sRet);
				//显示图像
				TFace2.showImgA("123456789012345678",
						"12345678901234567890123456789012", sRet);
			}
			return true;
		} else if (iIdx == 2) // 手动抓拍人脸图像
		{

			document.getElementById("TFace1").style.display = "";
			document.getElementById("TFace2").style.display = "none";
			document.getElementById("TxtHint").value = "";
			sPara = document.getElementById("TxtPara").value;
			//sRet = TFace1.getFaceB64(sPara);
			//20140605 TCCZY 身份证号和流水号由单独的参数传入
			var sRet2 = TFace1.getFaceB64B(sPara, "123456789012345678",
					"12345678901234567890123456789012");
			document.getElementById("TxtHint").value = sRet2;

			//解析返回xml串，手工抓取成功后自动显示图像
			try {//IE
				xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
				xmlDoc.async = "false";
				xmlDoc.loadXML(sRet2);

			} catch (e) {
				try //Firefox, Mozilla, Opera, etc.
				{
					parser = new DOMParser();
					xmlDoc = parser.parseFromString(sRet, "text/xml");
				} catch (e) {
					alert(e.message);
				}
			}

			//解析出resultCode ，如果resultCode=0  则关闭摄像头,并显示图像  否则不关闭
			var res = xmlDoc.getElementsByTagName("resultCode")[0].childNodes[0].nodeValue;

			if (res == 0) {
				TFace1.closeDevice();//关闭摄像头
				document.getElementById("TFace1").style.display = "none";
				document.getElementById("TFace2").style.display = "";
				$("#getImgBase64").val(sRet2);
				//显示图像
				TFace2.showImgA("123456789012345678",
						"12345678901234567890123456789012", sRet2);
			}
			return true;
		}
	}
	
	function compareInfo() {
		var clientNoApp = $("#clientNo").val();
		if($("#clientNo").val()==null || $("#clientNo").val()==""){
				alert("请选择需要进行人脸识别的人！");	
				return ;
		}
		$("#getImgBase64").val(document.getElementById("TxtHint").value);
		if ($("#getImgBase64").val() == null || $("#getImgBase64").val() == "") {
			alert("请先获取图片！！！");
			return ;
		}
		var getImgBase64 = $("#getImgBase64").val();
		getImgBase64 =getImgBase64.substring(getImgBase64.indexOf("<imgBase>")+9);
		var arr = new Array();
		arr = getImgBase64.split("</imgBase>");
		var imgBase64 = arr[0];
		$.ajax({
			type : "post",
			dataType : "json",
			async : true,
			data : {
				clientNoApp : $("#clientNoApp").val(),
				serviceItems : $("#serviceItems").val(),
				posNo : $("#posNo").val(),
				imgBase64 : imgBase64,
				controlVersion : $("#controlVersion").val()
			},
			url : "${ctx}acceptance/branch/faceRecognition.do",
			success : function(data) {
				if(data.flag =="Y"){
							window.returnValue = "Y,"+data.message;
							window.close();										
				}else{
					window.returnValue = "N,"+data.message;
					window.close();
					
				}
				/*$("#flag").val(data.flag);
				$("#message").val(data.message);
				$("#compareNo").val(data.compareNo);
				if(data.compareSystem=="1"){
					$("#compareSystemDesc").val("公民身份系统");
				}else if(data.compareSystem=="2"){
					$("#compareSystemDesc").val("smartBIos系统");
				}
				$("#compareTimesNCIIC").val(data.compareTimesNCIIC);*/
			}
		});
		
	}
</script>
</html>