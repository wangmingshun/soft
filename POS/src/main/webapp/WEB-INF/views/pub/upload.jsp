<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp" %>
<sf:override name="pathString">投连价格设置</sf:override>
<sf:override name="head">
        <script type="text/javascript">
                  $(function(){
	 				$.extend($.fn.validatebox.defaults.rules, {
	 					reportFileVal: {
	 		        validator: function(value, param){
	 				        var extStart=value.lastIndexOf("."); 
	 				        var ext=value.substring(extStart,value.length).toUpperCase(); 
	 				        if(ext!=".XLS"){				       
	 				        return false; 
	 				        }else{
	 						return true;
	 					}
	 		           
	 		        },
	 		        message: '附件限于XLS格式!'
	 		    }
	 				});	
	 					
	 				   })
			    function btnSub(){
				var form=$("#uploadUlinkPrice");
				var formurl =form.attr("action");
				if( $(form).form('validate')){
				    form.attr("action","${ctx}pub/UlinkPrice/uploadExcel.do");
				    form.submit();
				}
				
				}		
		</script>
</sf:override>

<sf:override name="content">
<sfform:form id="uploadUlinkPrice" enctype="multipart/form-data" method="post">
<div class="layout_div">
   <%-- 
	<div class="navigation_div">
        <font class="font_heading1">产品设置</font>>><a href="#">投连价格设置</a>>><a href="#">上传</a>           
    </div> 
    --%>
 <div id="content" class="easyui-panel" title="上传" collapsible="true">

	 <table class="layouttable">
		<tr>

		<td width="25%" class="layouttable_td_label">
	 			Excel文件名称：
	 	</td>
	 	<td width="25%" class="layouttable_td_widget">
	 		      <input type="file" missingMessage="请上传EXCEL格式的附件" id="content"  required="true" class="easyui-validatebox"  name="content"  validType="reportFileVal"  onblur="" value=""/>
	 	</td>
	 	<td width="25%" class="layouttable_td_widget">
	 		<a class="easyui-linkbutton" name="_eventId_uploadExcel" onclick="btnSub();" iconCls="icon-xls">导入</a>  
	 		 <a class="easyui-linkbutton"  onclick="javascript:history.back();" iconCls="icon-back"  id="cancel" name=cancel">取消</a>		   				 			
	 			
	 	</td>
	   </tr>
   </table>
 </div>
</div>	
</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>




