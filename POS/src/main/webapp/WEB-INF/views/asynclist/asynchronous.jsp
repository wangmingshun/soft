 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">异步清单列表</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
  });

</script>
<style type="text/css">
#menu_ul li {
	line-height: 30px;
	font-size:15px;
}

</style>
</sf:override>
<sf:override name="content">

<form>
<ul id="menu_ul">
  <li><a href="${ctx}async/asynchronousList">异步清单任务查询</a></li>
  <li ><span style="color: red;font-size:20px;font-weight:bold;">常用清单</span></li>
  <li><a href="${ctx}async/list_03">保全处理清单</a></li>
  <li><a href="${ctx}async/list_45">线上保全处理清单</a></li>
  <li><a href="${ctx}async/list_34">契撤清单</a></li>
  <li><a href="${ctx}async/list_33">退保清单</a></li>
  <li><a href="${ctx}async/list_35">贷款清单</a></li>
  <!--<li><a href="${ctx}report/survinvest">生存调查数据提取清单</a></li>
  <li><a href="${ctx}report/policymove">保单迁入迁出清单</a></li>
  <li><a href="${ctx}report/uncharged">保全未收费清单</a></li>
  <li><a href="${ctx}report/uncharged.do?policyChannelType=06">保全未收费清单</a></li> -->
  <li ><span style="color: red;font-size:20px;font-weight:bold;">一般清单</span></li>
  <li><a href="${ctx}async/list_05">保全特殊件清单</a></li>
  <li><a href="${ctx}async/list_08">转账结果清单</a></li>
  <li><a href="${ctx}async/list_14">审批任务处理清单</a></li>
  <li><a href="${ctx}async/list_15">问题件跟踪清单</a></li>
  <li><a href="${ctx}async/list_23">保全影像扫描清单</a></li>
  <li><a href="${ctx}async/list_24">保全时效清单</a></li>
  <li><a href="${ctx}async/list_39">保全时效清单（服务评价）</a></li>
  <li><a href="${ctx}async/list_31">重复电话号码停用清单</a></li>
  
  <li ><span style="color: red;font-size:20px;font-weight:bold;">其他清单</span></li>
  <li><a href="${ctx}async/list_01">生存调查数据提取清单</a></li>
  <li><a href="${ctx}async/list_02">保单迁入迁出清单</a></li>
  <li><a href="${ctx}async/list_04">保全未收费清单</a></li>
  <li><a href="${ctx}async/list_06">异地保全受理件清单</a></li>
  <li><a href="${ctx}async/list_07">质押贷款业务清单</a></li>
  <li><a href="${ctx}async/list_09">自动垫交保费清单</a></li>
  <li><a href="${ctx}async/list_10">预计超停清单</a></li>
  <li><a href="${ctx}async/list_11">已终止保单生存金应领清单</a></li>
  <li><a href="${ctx}async/list_12">保全回退/撤销清单</a></li>
  <li><a href="${ctx}async/list_13">生存金预算清单</a></li>
  <li><a href="${ctx}async/list_17">作业环节时效清单</a></li>
  <li><a href="${ctx}async/list_19">客服信函打印清单</a></li>
  <li><a href="${ctx}async/list_20">邮储通知书打印清单</a></li>
  <li><a href="${ctx}async/list_21">受理状态修改清单</a></li>
  <li><a href="${ctx}async/list_27">保单冻结清单</a></li>
  <li><a href="${ctx}async/list_28">协议退费清单</a></li>
  <li><a href="${ctx}async/list_36">银代前置保全处理清单</a></li>
  <li><a href="${ctx}async/list_37">邮储通知书打印清单（月度）</a></li>
  <li><a href="${ctx}async/list_38">保单贷款即将逾期清单</a></li>
  <li><a href="${ctx}async/list_47">POS系统权限清单</a></li>
  <li><a href="${ctx}async/list_48">UM系统权限清单</a></li>
  
 <!-- <li><a href="${ctx}report/difplacepos">异地保全受理件清单</a></li>
  <li><a href="${ctx}report/mortgage">质押贷款业务清单</a></li>
  <li><a href="${ctx}report/transfer">转账结果清单</a></li>
  <li><a href="${ctx}report/autopolicy">自动垫交保费清单</a></li>
  <li><a href="${ctx}report/policylapse">预计超停清单</a></li>
  <li><a href="${ctx}report/survlapsed">已终止保单生存金应领清单</a></li>
  <li><a href="${ctx}report/rollback">保全回退/撤销清单</a></li>
  <li><a href="${ctx}report/survplan">生存金预算清单</a></li>
  <li><a href="${ctx}report/posapprove">审批任务处理清单</a></li>
  <li><a href="${ctx}report/posproblem">问题件跟踪清单</a></li>
   <li><a href="${ctx}report/unscaned">待扫描溢时处理清单</a></li> 
  <li><a href="${ctx}report/timeffect">作业环节时效清单</a></li>

   <li><a href="${ctx}report/chargesuccess">扣费成功生效保单清单</a></li> 
  <li><a href="${ctx}report/clientnotice">客服信函打印清单</a></li>
  <li><a href="${ctx}report/postalnote">邮储通知书打印清单</a></li>
  <li><a href="${ctx}report/statemodify">受理状态修改清单</a></li>
  <li><a href="${ctx}report/posimage">保全影像扫描清单</a></li>
  <li><a href="${ctx}report/poseffective">保全时效清单</a></li>
  <li><a href="${ctx}report/policyfreeze">保单冻结清单</a></li>
  <li><a href="${ctx}report/agreementpos">协议退费清单</a></li>
  <li><a href="${ctx}report/stopphonelist">重复电话号码停用清单</a></li>
    <li><a href="${ctx}report/posprocess_1">契撤清单</a></li>
      <li><a href="${ctx}report/posprocess_2">退保清单</a></li>
        <li><a href="${ctx}report/posprocess_8">贷款清单</a></li>
  --><!-- <li><a href="${ctx}report/surplus">保全贴费业务清单</a></li> -->

</ul>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>