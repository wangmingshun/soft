<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<sf:block name="head"></sf:block>
<script type="text/javascript" language="javascript">
	var imageRelateBarcodeNo = "${imageRelateBarcodeNo}";
	var imageBarcodeNo = "${imageBarcodeNo}";
	function switchDisplayImage(barcodeNo) {
		$.post("${ctx}include/getImageByBarcodeNo.do", {"barcodeNo" : barcodeNo, rd : Math.random()}, function(data) {
			$("#fullImageDiv").html(data);
		}, "html");
	}
	
	$(function(){
		var $imageViewLink = $("#imageViewLink");
		var $relateImageViewLink = $("#relateImageViewLink");
		if(imageRelateBarcodeNo == "") {
			$imageViewLink.hide();
			$relateImageViewLink.hide();
		} else {
			$imageViewLink.click(function(){
				$imageViewLink.hide();
				switchDisplayImage(imageBarcodeNo);
				$relateImageViewLink.show();
				return false;
			}).hide();
			$relateImageViewLink.click(function(){
				$relateImageViewLink.hide();
				switchDisplayImage(imageRelateBarcodeNo);
				$imageViewLink.show();
				return false;
			}).show();
		}
	});
</script>
</head>
<body class="easyui-layout" style="width:100%;height:100%;">
	<div region="west" split="true" title="影像资料" style="width:400px" 
		id="fullImageDiv" loadingMessage="正在加载影像......" href="${ctx}include/getImageByBarcodeNo.do?barcodeNo=${imageBarcodeNo}">
		
	</div>
	<div region="center"  style="background:#eee;">
		<div class="easyui-layout" fit="true">
			<div region="center">
				<div class="navigation_div"><span class="font_heading1"><sf:block name="pathString"/></span></div>
				<sf:block name="content" />
			</div>
		</div>
	</div>
</body>
</html>
