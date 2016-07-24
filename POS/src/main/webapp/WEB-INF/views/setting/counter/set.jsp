<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">维护设置&gt;&gt;柜面信息维护</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	$(function() {
		$(".chooseBranchBt").live("click",function(){
			if($(this).prev().attr("disabled")==true){return;};
			showBranchTree($(this).prev(),null,"${userBranchCode}");
			return false;
		});
		
		$(".counterListCb").live("click",function(){

			if($(this).attr("checked")==true){
				$("#counterTb tr:eq("+$(".counterListCb").index($(this))+") *").removeAttr("disabled");
			}else{
				$("#counterTb tr:eq("+$(".counterListCb").index($(this))+") :input:not(.counterListCb)").attr("disabled","disabled");
			}
			
		});
		
		$("#queryCounterBt").click(function(){
			if($.trim($("#branchTxt0").val())==""){
				$.messager.alert("提示","请先选择机构!");
				return false;
			}
			
			$.post("${ctx}setting/counter/query", 
			{branchNo:$("#branchTxt0").val(),
			 sonBranch:$("#sonBranch").val()},
			function(data) {
				
				$("#counterTb *").remove();
				
				if(data.length==0){
					$.messager.alert("提示","该机构尚没有柜面信息");
					return;
				}
				
                $.each(data,function(i,map){
					addRow(map["COUNTER_NO"],
					       map["COUNTER_BRANCH"],
					       map["COUNTER_NAME"],
					       map["COUNTER_TYPE"],
					       map["COUNTER_DIRECTOR"],
					       map["COUNTER_PHONE"],
					       map["COUNTER_FAX"],
					       map["COUNTER_ADDR"],
					       map["COUNTER_POST"],
						   map["OPEN_TIME"]
					      );
					});
		    }, "json");
			return false;
		});
		
		$("#sonBranch").change(function(){
			if(this.checked == true){
				$(this).val("Y");
			}else{
				$(this).val("N");
			}
		});
		
		$(".counterTypeSelect").live("change",function(){
			if("${userBranchCode}"!="86" && "${userBranchCode}"!=""){
				$(this).val("A");
			}
		});
				
	});

	//增加一行
	function addRow(counter_no,
	                counter_branch,
					counter_name,
					counter_type,
					counter_director,
					counter_phone,
					counter_fax,
					counter_addr,
					counter_post,
					counter_time){
		var c = "";
		var l = $("#counterTb tr").length+1;
		if(l%2==0){
			c = "even_column";
		}else{
			c = "odd_column";
		}
		var str = '<tr class="'+c+'"><td><input type="checkbox" class="counterListCb" /><input type="hidden" name="counterNoHid" value="'+counter_no+'" /></td>';
		    str += '<td><input disabled type="text" size="6" id="branchTxt' + l;
			str += '" value="' + counter_branch;
			str += '" /><a class="chooseBranchBt" href="javascript:void(0)">选择机构</a>';
			str += '</td><td><input disabled type="text" name="counterNameTxt" value="' + counter_name;
			str += '" /></td><td><select disabled class="counterTypeSelect" name="counterTypeSel">';
			str += '<option value="A"';
			if(counter_type=="A"){
				str += ' selected ';
			}
			str += '>客户服务中心</option>';
			str += '<option value="B"';
			if(counter_type=="B"){
				str += ' selected ';
			}
			str += '>销售服务支持中心</option>';
			str += '<option value="C"';
			if(counter_type=="C"){
				str += ' selected ';
			}
			str += '>派驻点</option>';
			str += '<option value="D"';
			if(counter_type=="D"){
				str += ' selected ';
			}
			str += '>受理网点</option>';
			str += '</select></td><td><input disabled type="text" size="8" name="counterDirectorTxt" value="' + counter_director;
			str += '" /></td><td><input disabled type="text" name="counterPhoneTxt" value="' + counter_phone;
			str += '" /></td><td><input disabled type="text" name="counterFaxTxt" value="' + counter_fax;
			str += '" /></td><td><input disabled type="text" name="counterAddrTxt" value="' + counter_addr;
			str += '" /></td><td><input disabled type="text" name="counterPostTxt" size="6" maxlength="6" value="' + counter_post;
			str += '" /></td><td><input disabled type="text" name="counterTimeTxt" size="10" value="' + counter_time;
			str += '" /></td>';
			str = str.replace(/undefined/g,"");
			str = str.replace(/null/g,"");
			
		$("#counterTb").append(str);
		return false;
	}
	
//删除选中的行
function delRow(){
	 var $cked = $(".counterListCb:checked");//选中的行
     if($cked.length<1){
		 return;
	 }

	 var nos = "";
	 $.each($cked,function(n,v){
		 var no = $("#counterTb tr:eq("+$(".counterListCb").index($(v))+") :input[name='counterNoHid']").val();
		 if(no.length>0){
			 nos += no;
			 nos += ";";
		 }
	 });
	 
	 if(nos!=""){
		 if(!confirm("删除操作不能撤销，是否继续？")){
		   return;
	     }
		 $.post("${ctx}setting/counter/delete", 
			{counterNos:nos},
			function(data) {
				if(data=="Y"){
					$.messager.alert("提示","所选原有柜面已删除！");
				}
		    }, "text");
	 }
	 $cked.each(function(){
		$(this).closest("tr").remove();
	 });
	 return false;
}
	
	//更新选中的行
	function update(){
	  var jsonStr = "[";
      $.each($(".counterListCb:checked"),function(n,v){
		 var i = $(".counterListCb").index($(v));
		 if($("#counterTb tr:eq("+i+") :input[name='counterNoHid']").val().length>0){
		   jsonStr += getValues(i);
		 			 
		 }
      });
	  jsonStr += "]";
	  jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号
	  
	  if(jsonStr.length<3){
		 $.messager.alert("提示","请先查询信息并修改");
		 return;
	  }
        $.post("${ctx}setting/counter/update", 
			{counterNewInfo:jsonStr},
			function(data) {

                $(".counterListCb:checked").each(function(){
				   if($("#counterTb tr:eq("+$(".counterListCb").index($(this))+") :input[name='counterNoHid']").val().length>0){
				     $(this).attr("checked",false);
				     $("#counterTb tr:eq("+$(".counterListCb").index($(this))+") :input:not(.counterListCb)").attr("disabled","disabled");
				   }//只灰掉原有的是做修改的
		        });

				if(data=="Y"){
					$.messager.alert("提示","所选柜面信息更新成功！");
				}
		    }, "text");
        return false;
	}
	
	//保存增加的，填写了的，选中了的 行 信息
	function saveIt(){
	 var jsonStr = "[";
	 $.each($(".counterListCb:checked"),function(n,v){
		 var i = $(".counterListCb").index($(v));
		 
		 if($("#counterTb tr:eq("+i+") :input[name='counterNoHid']").val().length>0){
			 $.messager.alert("提示",$("#counterTb tr:eq("+i+") :input[name='counterNameTxt']").val()+"为已有柜面，请通过修改进行操作");
			 return true;
		 }

		 jsonStr += getValues(i);

	 });
	 jsonStr += "]";
	 jsonStr = jsonStr.replace("},]","}]");//去掉后面一个逗号
	 
	 if(jsonStr.length<3){
		 $.messager.alert("提示","请增加信息");
		 return;
	 }
	 
     $.post("${ctx}setting/counter/add", 
			{newCounterInfo:jsonStr},
			function(data) {
				if(data=="Y"){
					$.messager.alert("提示","柜面信息已成功添加,若需查看,请重新查询！");
				}
                $(".counterListCb:checked").each(function(){
				   var no = $("#counterTb tr:eq("+$(".counterListCb").index($(this))+") :input[name='counterNoHid']").val();
		           if(no.length<1){
			         $(this).parent().parent().remove();
				     //只remove新增的柜面信息
		           }
		        });

		    }, "text");
     return false;
	}
	
	//获取第i行的各个录入域的值
	function getValues(i){
		var str = "";
		 str += '{';
		 str += '"counterNo":"'+$("#counterTb tr:eq("+i+") :input[name='counterNoHid']").val()+'",';
		 str += '"counterBranch":"'+$("#branchTxt"+(i+1)).val()+'",';
		 str += '"counterName":"'+$("#counterTb tr:eq("+i+") :input[name='counterNameTxt']").val()+'",';
		 str += '"counterType":"'+$("#counterTb tr:eq("+i+") :input[name='counterTypeSel']").val()+'",';
		 str += '"counterDirector":"'+$("#counterTb tr:eq("+i+") :input[name='counterDirectorTxt']").val()+'",';
		 str += '"counterPhone":"'+$("#counterTb tr:eq("+i+") :input[name='counterPhoneTxt']").val()+'",';
		 str += '"counterFax":"'+$("#counterTb tr:eq("+i+") :input[name='counterFaxTxt']").val()+'",';
		 str += '"counterAddr":"'+$("#counterTb tr:eq("+i+") :input[name='counterAddrTxt']").val()+'",';
		 str += '"counterPost":"'+$("#counterTb tr:eq("+i+") :input[name='counterPostTxt']").val()+'",';
		 str += '"counterTime":"'+$("#counterTb tr:eq("+i+") :input[name='counterTimeTxt']").val()+'"';
		 str += '},';
	    return str;
	}
	
	//导出
	function getExl(){
		if($.trim($("#branchTxt0").val())==""){
				$.messager.alert("提示","请先选择机构!");
				return;
		}
		
		$("form").attr("action","${ctx}setting/counter/export");
		$("form").submit();
		return false;
	}
	
    </script>
</sf:override>
<sf:override name="content">
	<form>
      <div id="p1" class="easyui-panel" >
        <table class="layouttable">
          <tr>
            <td width="15%" class="layouttable_td_label">柜面所属机构：</td>
            <td>
              <input type="text" id="branchTxt0" name="branchTxt0" />
              <a class="chooseBranchBt" href="javascript:void(0)">选择机构</a>
              <input type="checkbox" id="sonBranch" name="sonBranch" value="Y" checked />包含下级
            </td>
            <td align="right">
              <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryCounterBt">查询</a>
            </td>
          </tr>
        </table>
      </div>
      
      <br />
      
      <div id="p2" class="easyui-panel" title="柜面信息列表" collapsible="true">
		<table class="infoDisTab">
			<thead>
				<tr>
					<th width="3%">选择</th>
					<th width="12%">柜面所属机构</th>
					<th>柜面名称</th>
					<th width="9%">柜面类型</th>
					<th width="9%">柜面负责人</th>
					<th>柜面电话</th>
					<th>柜面传真</th>
					<th>柜面地址</th>
					<th width="7%">邮编</th>
					<th width="8%">营业时间</th>
				</tr>
			</thead>
			<tbody id="counterTb">
			</tbody>
		</table>
    
		<p align="right">
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" onclick="addRow();return false;">增加</a>
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-save" onclick="saveIt();return false;">保存</a>
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" onclick="delRow();return false;">删除</a>
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-edit" onclick="update();return false;">修改</a>
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-print" onclick="getExl();return false;">导出</a>
		</p>
      </div>
      
	</form>

    <jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
        
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>