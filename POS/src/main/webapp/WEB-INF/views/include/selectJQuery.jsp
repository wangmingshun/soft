<%@ page contentType="text/html;charset=UTF-8"%>
<script type="text/javascript">
//适用于左右选择框互选的公用代码
$(function(){

     $("#add").click(function(){
		  $("#forSelect").trigger("dblclick");
		  return false;
       });

     $("#delete").click(function(){ 
		  $("#selected").trigger("dblclick");
		  return false;
      });
	  
	  $("#addAll").click(function(){ 
          $("#forSelect option").each(function(){ 
              $("#selected").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option");
              $(this).remove();  
          });
          return false;
       });

     $("#deleteAll").click(function(){ 
          $("#selected option").each(function(){ 
              $("#forSelect").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option"); 
              $(this).remove();  
          });
          return false;
      });

     $("#forSelect").bind("dblclick",function(){
		 $("#selected option").attr("selected",false);
		 $(this).find("option:selected").remove().appendTo("#selected");
		 return false;
	 });
	 
	 $("#selected").bind("dblclick",function(){
		 $("#forSelect option").attr("selected", false);
		 $(this).find("option:selected").remove().appendTo("#forSelect");
		 return false;
	 });	

	 //第2组互选框控件
     $("#add2").click(function(){
		 $("#forSelect2").trigger("dblclick");
		 return false;
	 });
     $("#delete2").click(function(){ 
		 $("#selected2").trigger("dblclick");
		 return false;
     });
	 $("#addAll2").click(function(){ 
          $("#forSelect2 option").each(function(){ 
              $("#selected2").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option");
              $(this).remove();  
          });
          return false;
      });
     $("#deleteAll2").click(function(){ 
          $("#selected2 option").each(function(){ 
              $("#forSelect2").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option"); 
              $(this).remove();  
          });
          return false;
      });
     $("#forSelect2").dblclick(function(){
		 $("#selected2 option").attr("selected",false);
		 $(this).find("option:selected").remove().appendTo("#selected2");
		 return false;
	 });
	 $("#selected2").dblclick(function(){
		 $("#forSelect2 option").attr("selected", false);
		 $(this).find("option:selected").remove().appendTo("#forSelect2");
		 return false;
	 });
	 
});
</script>