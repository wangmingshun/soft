var ResouresPath = "${ctx}";
var muteFunction = function(){
	return false;
};
$(function(){
    $(":input[readonly]").addClass("readOnly_input");
    
    /* 让panel能自适应窗口的的变化 */
    $(window).resize(function() {
    	var rwidth = $(".navigation_div").width();
    	$(".easyui-panel").panel("resize", {
    		width : rwidth
    	});
    	$(".tabs-container").tabs("resize");

    	if($.browser.mozilla){
    		$(".easyui-datagrid").datagrid({width:rwidth,height:'auto'});    		
    	}
    });
    
    /*屏蔽粘贴功能*/
    $(".nopaste").live("focus", function(){
    	this.onpaste = muteFunction;
    	this.ondragenter = muteFunction;
    	return true;
    });
    
    /*录入域自动去除首尾空格*/
    $(":text:not(.noTrimBlank):not([readonly])").live("focusout", function(){
    	this.value = $.trim(this.value);
    	return true;
    });
});
/*
 * 校验身份证是否与性别匹配
 */
function validateIdNoMatchesSexCode(idNo, sexCode) {
	if(idNo && idNo.length && (idNo.length == 15 || idNo.length == 18)) {
		var sexChar = null;
		if(idNo.length == 15) {
			sexChar = idNo.charAt(14);
		} else if(idNo.length == 18) {
			sexChar = idNo.charAt(16);
		}
		sexChar = parseInt(sexChar) % 2 == 0 ? "2" : "1";
		return sexChar == sexCode;
	}
	return true;
}
/*
 * 校验身份证是否与生日匹配
 */
function validateIdNoMatchesBirthday(idNo, birthday) {
	if(idNo && idNo.length && (idNo.length == 15 || idNo.length == 18)) {
		var birthYear = birthday.substring(0, 4);
		var birthMonth = birthday.substring(birthday.indexOf("-") + 1, birthday.lastIndexOf("-"));
		var birthDay = birthday.substring(birthday.lastIndexOf("-") + 1);
		var year = null;
		var month = null;
		var day = null;
		if(idNo.length == 15) {
			year = idNo.substring(6, 8);
			month = idNo.substring(8, 10);
			day = idNo.substring(10, 12);
			return parseInt(year) == parseInt(birthYear.substring(2)) && parseInt(month) == parseInt(birthMonth) && parseInt(day) == parseInt(birthDay);
		} else if(idNo.length == 18) {
			year = idNo.substring(6, 10);
			month = idNo.substring(10, 12);
			day = idNo.substring(12, 14);
			return parseInt(year) == parseInt(birthYear) && parseInt(month) == parseInt(birthMonth) && parseInt(day) == parseInt(birthDay);
		}
	}
	return true;
}