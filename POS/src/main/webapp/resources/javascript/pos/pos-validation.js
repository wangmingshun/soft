/* 常用JS校验类型定义 */

// 中文字符字节长度
jQuery.validator.addMethod("byteRangeLength",
		function(value, element, param) {
			var length = value.length;
			for ( var i = 0; i < value.length; i++) {
				if (value.charCodeAt(i) > 127) {
					length++;
				}
			}
			return this.optional(element)
					|| (length >= param[0] && length <= param[1]);
		}, $.validator.format("请确保输入的值在{0}-{1}个字节之间(一个中文字算2个字节)"));

//条形码
jQuery.validator.addMethod("barcodeNo", function(value, element, param) {
	var length = value.length;
	return this.optional(element) || true;
}, $.validator.format("无效的条码号"));

// 保单号
jQuery.validator.addMethod("policyNo", function(value, element, param) {
	var length = value.length;
	return this.optional(element) || true;
}, $.validator.format("无效的保单号"));

// 客户号
jQuery.validator.addMethod("barcodeNo", function(value, element, param) {
	var length = value.length;
	return this.optional(element) || true;
}, $.validator.format("无效的条码号"));

// 业务员代码
jQuery.validator.addMethod("empNo", function(value, element, param) {
	var length = value.length;
	return this.optional(element) || true;
}, $.validator.format("无效的条码号"));

// 日期yyyy-MM-dd
jQuery.validator.addMethod("commonDateFormat", function(value, element, param) {
	var length = value.length;
	return this.optional(element) || true;
}, $.validator.format("无效的日期"));

// 邮政编码
jQuery.validator.addMethod("postalCode", function(value, element, param) {
	var t = /^[0-9]{6}$/;
    return this.optional(element) || (t.test(value));
}, $.validator.format("无效的邮编"));

// 证件号码-身份证
jQuery.validator.addMethod("idNo", function(value, element, param) {
	var valid = true;
	if(param) {
		var paramObj = eval(param);
		var target = paramObj.target;
		var val = paramObj.value;
		if(target && val) {
			if($(target).val() == val) {
				valid = value && (Zxb.check.checkCnId(value.toUpperCase()).split(",")[0] == "0");
				if(!valid) {
					if(/^\d{15}(\d{2}(\d|x|X))?$/.test(value)) {
						this.showLabel(element, "该身份证校验位不正确");
						return true;
					}
				}
			}
		}
	}
	return this.optional(element) || valid;
}, $.validator.format("无效的证件号码"));

//自定义
jQuery.validator.addMethod("customValidate", function(value, element, param) {
	return this.optional(element) || eval(param).apply(element, [value]);
}, $.validator.format("无效的值"));

//不能与原值相等
jQuery.validator.addMethod("notEqualTo", function(value, element,param) {
	  return value != $(param).val();
	}, "不能等于原值");

//只能小于原值,这里仅限于数字比较 新旧值任意一个是空就随便填
jQuery.validator.addMethod("smallerThan", function(value, element,param) {
	  return value=="" || $(param).val()=="" || parseFloat(value) < parseFloat($(param).val());
}, "只能小于原值");

//不能大于原值,这里仅限于数字比较
jQuery.validator.addMethod("notBiggerThan", function(value, element,param) {
	  return value=="" || $(param).val()=="" || parseFloat(value) <= parseFloat($(param).val());
}, "不能大于原值");

var posValidateHandler = null;
var _posValidateHandler = function(form) {
	if(!posValidateHandler || posValidateHandler()) {
		
		if(!$(form).hasClass("noBlockUI")){
			$.blockUI({ 
				message:'正在提交中，请稍后...',
				css: { 
				  padding:0,
				  margin:0,
				  width:"30%",
				  top:"40%",
				  left:"35%",
				  textAlign:"center",
				  color:"#000",
				  border:"1px solid #aaa",
				  backgroundColor:"#CCFFFF",
				  cursor:"wait"
				}, 
				overlayCSS:
				{
				  backgroundColor:"#fff",
				  opacity:0.6
				  }
				});			
		}
		
		form.submit();
	}
};
/* 设置校验默认值 */
$.validator.setDefaults({
	debug : false, // 调试模式
	errorElement : "em",
//	wrapper: "li",
	focusInvalid: false,
	submitHandler: _posValidateHandler,
	errorLabelContainer: "#formMessageBox",
	errorContainer:"#formHasErrorMessage",
	errorPlacement : function(error, element) {
		error.css("color", "red").appendTo(element.parent());
	}
});
$(function(){
	/* 阻止回车键提交 */
	var $form = $("form");
	$form.each(function(idx){
		$("<input type='password' style='display:none;'/>").appendTo($form[idx]);
	});
});