function postParamForm(recordCount, page, pageSize, divPageID, paramFormID,
		dataURL, totalPages, select_page_size)// 提交分页数据
{
	var data = "page=" + page + "&pageSize=" + select_page_size
			+ "&recordCount=" + recordCount + "&select_page_size="
			+ select_page_size + "&fragments=" + divPageID; // 提交分页数据到后台
	var temp = data;
	var data_para = '';
	if (typeof paramFormID != 'undefinded' && paramFormID != '') {
		var o = $("#" + paramFormID);
		var o_str = o.serialize();
		if (typeof o != 'undefinded') {
			if (typeof o_str != 'undefinded' && o_str != '') {
				data = data + "&" + o_str;
				var array_pro = data.split("&");
				for ( var i = 0; i < array_pro.length; i++) {
					var m = array_pro[i].split("=");
					if (data_para.indexOf(m[0]) == -1) {
						data_para = data_para + array_pro[i] + "&";
					}
				}
				data_para = data_para.substring(0, data_para.lastIndexOf("&"));
			}
		}
	}
	if (data_para == '')
		data_para = temp;
	if ((typeof $("#" + divPageID)) != 'undefinded') {
		var ob = $("#" + divPageID);
		var offsettop = ob.offset().top;
		var offsetleft = ob.offset().left;
		var positiontop = ob.height() / 2;
		var positionleft = ob.width() / 2;
		$("#" + divPageID + " table").each(function() {
			$(this).attr("disabled", true);
		});
		$("#" + divPageID + " a").each(function() {
			$(this).get(0).onclick = "";
			$(this).attr("disabled", true);
		});
		$("#" + divPageID + " input").each(function() {
			$(this).attr("disabled", true);
		});
		var showMessageDiv = $("<div id='addPositionImage' class='paginationTableLoadDiv'><br>数据加载中...</div>");
		showMessageDiv.appendTo("#" + divPageID);
		$("#addPositionImage").css({
			'position' : "absolute",
			'top' : offsettop + positiontop,
			'left' : offsetleft + positionleft,
			'z-index' : 9999
		});
		$.ajax({
			type : "GET",
			url : dataURL,
			data : data_para,
			success : function(msg) {
				$("#" + divPageID).html(msg);
			}
		});
	}
}

function pageSizeChange(obj, recordCount, page, pageSize, divPageID,
		paramFormID, dataURL, totalPages)// 修改每页显示页数
{
	var o = $(obj).attr("value");
	var select_page_size = o;
	if (typeof o == 'undefinded')
		select_page_size = 10;
	totalPages = Math.ceil(recordCount / select_page_size);
	if (parseInt(page) >= parseInt(totalPages))
		page = 1;
	postParamForm(recordCount, page, pageSize, divPageID, paramFormID, dataURL,
			totalPages, select_page_size);
}
function get_select_page_size(obj) {
	var rtn = $(obj).parent().parent().parent().find(
			":input[name='select_page_size']").val();
	if (typeof rtn == 'undefinded' || rtn == '')
		rtn = "10";
	return rtn;
}
function firstPage(obj, recordCount, page, pageSize, divPageID, paramFormID,
		dataURL, totalPages)// 第一页
{
	var select_page_size = get_select_page_size(obj);
	page = 1;
	postParamForm(recordCount, page, pageSize, divPageID, paramFormID, dataURL,
			totalPages, select_page_size);
}

function lastPage(obj, recordCount, page, pageSize, divPageID, paramFormID,
		dataURL, totalPages)// 最后一页
{
	var select_page_size = get_select_page_size(obj);
	page = totalPages;
	postParamForm(recordCount, page, pageSize, divPageID, paramFormID, dataURL,
			totalPages, select_page_size);
}

function precedingPage(obj, recordCount, page, pageSize, divPageID,
		paramFormID, dataURL, totalPages)// 上一页
{
	var select_page_size = get_select_page_size(obj);
	if (parseInt(page) - 1 >= 0)
		page = parseInt(page) - 1;
	postParamForm(recordCount, page, pageSize, divPageID, paramFormID, dataURL,
			totalPages, select_page_size);// 下一页
}
function nextPage(obj, recordCount, page, pageSize, divPageID, paramFormID,
		dataURL, totalPages) {
	var select_page_size = get_select_page_size(obj);
	if (parseInt(page) + 1 <= parseInt(totalPages))
		page = parseInt(page) + 1;
	postParamForm(recordCount, page, pageSize, divPageID, paramFormID, dataURL,
			totalPages, select_page_size);// 下一页
}

function goToPage(event, obj, recordCount, page, pageSize, divPageID,
		paramFormID, dataURL, totalPages)// 指定第几页
{
	var myEvent = event || window.event;
	var keyCode;
	if (window.event)
		keyCode = myEvent.keyCode;
	else
		keyCode = event.which;
	var srcElement = myEvent.srcElement;
	if (!srcElement) {
		srcElement = myEvent.target;
	}
	if (typeof totalPages == 'undefined')
		totalPages = 1;
	var toPage = srcElement.value;
	if (keyCode == 13) {
		page = toPage;
		if (typeof toPage == 'undefined' || toPage == '')
			return;
		if (parseInt(toPage) > parseInt(totalPages)) {
			srcElement.value = totalPages;
			page = totalPages;
		}
		if (toPage.substring(0, 1) == '0' || parseInt(toPage) < 1) {
			srcElement.value = 1;
			page = 1;
		}
		var select_page_size = get_select_page_size(obj);
		postParamForm(recordCount, page, pageSize, divPageID, paramFormID,
				dataURL, totalPages, select_page_size);
	}
	if (keyCode == 8) {
		return;
	}
	if (keyCode < 48 || keyCode > 57) {
		if (window.event)
			window.event.returnValue = false;
		else
			event.preventDefault();
	}
}

function FromForSearch(FormName, URL, divPageID) {
	var data = $("form[name=" + FormName + "]").serialize();
	if (typeof data != 'undefined' && data != '') {
		$.ajax({
			type : "GET",
			url : URL,
			data : data,
			success : function(msg) {
				$("#" + divPageID).html(msg);
			}
		});
	} else {
		$.messager.alert('error','请给定有效的表单名!','info');
	}
}