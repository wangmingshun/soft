function sortStr(index,dataType) {
	return function(a, b) {
		var aText = $(a).find('td:nth-child(' + index + ')').text();
		var bText = $(b).find('td:nth-child(' + index + ')').text();

		if(dataType=='int_num' ||dataType=='num'||dataType=='date'||dataType=='percent'){
			aText = Parse(aText, dataType)
			bText = Parse(bText, dataType)
			return aText > bText ? -1 : bText > aText ? 1 : 0;	
		}else
			return aText.localeCompare(bText);
	}
}
function Parse(data, dataType) {
	switch (dataType) {
	case 'int_num': 
		return parseInt(data,10)||0 /* 定义为正整数排序 */
	case 'num':
		return parseFloat(data,10) || 0 /* 定义为浮点数排序 */    
	case 'date':
		return Date.parse(data.replace(/\-/g,'/')) || 0  /* 定义为日期类型，转换成整数排序 */
	case 'percent':                                   
		return Number(data.replace("%",""))||0 /* 定义为百分数，转换成浮点数排序 */
	default:                                    
		return data.toUpperCase()/* 字符串类型 */ 
	}
}

function replaceAll(value, regex, replacement) {
	return (typeof value == 'undefined' || typeof regex == 'undefined'
			|| typeof replacement != 'undefined' || typeof value != 'string'
			|| typeof regex != 'string' || typeof replacement != 'string') ? value
			: value.replace(new RegExp(regex, "gm"), replacement);
}
function thClickEvent(table_id, obj) {
	var dataType = $(obj).attr('dataType');
	var index = $("#" + table_id + " th").index(obj) + 1;
	var arr = [];
	var row = $("#" + table_id + " tbody").find("tr");
	$.each(row, function(i) {
		arr[i] = row[i];
	});
	var imageSrc = $(obj).find("#datagrid_sort_" + index).attr("src");
	if (typeof imageSrc != 'undefined'
			&& imageSrc.indexOf('datagrid_sort_asc.gif') != -1) {
		$(obj).parent().find("span").remove();
		$(obj)
				.append(
						"<span><image id='datagrid_sort_"
								+ index
								+ "' src='"
								+ ResouresPath
								+ "sf_home/resources/sortTable/image/datagrid_sort_desc.gif'/></span>");
	}

	if (typeof imageSrc != 'undefined'
			&& imageSrc.indexOf('datagrid_sort_desc.gif') != -1) {
		$(obj).parent().find("span").remove();
		$(obj)
				.append(
						"<span><image id='datagrid_sort_"
								+ index
								+ "' src='"
								+ ResouresPath
								+ "sf_home/resources/sortTable/image/datagrid_sort_asc.gif'/></span>");
	}

	if ($(obj).hasClass('select')) {
		arr.reverse();
	} else {
		arr.sort(sortStr(index, dataType))
		$('.select').removeClass();
		$(obj).addClass('select');
		$(obj).parent().find("span").remove();
		$(obj)
				.append(
						"<span><image id='datagrid_sort_"
								+ index
								+ "' src='"
								+ ResouresPath
								+ "sf_home/resources/sortTable/image/datagrid_sort_desc.gif'/></span>");
	}
	var fragment = document.createDocumentFragment();
	$.each(arr, function(i) {
		var obj_tr  = $(arr[i]);
		obj_tr.removeClass("odd_column");
		obj_tr.removeClass("even_column");
		if(i%2==0){
			obj_tr.addClass("even_column");
		}else{
			obj_tr.addClass("odd_column");		
		}
		fragment.appendChild(arr[i])
	});
	$("#" + table_id + ' tbody').append(fragment);
}