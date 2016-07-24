package com.sinolife.pos.pubInterface.biz.test;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.pubInterface.biz.esb.impl.MccpPosAcceptInterfaceImpl;

public class TestMccESBInterface {

	public static void main(String args[]) {
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"E:\\zhangzhicheng\\workspace\\SL_POS\\SL_POS\\src\\main\\webapp\\WEB-INF\\spring\\AppContext.xml");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out.println("================开始测试ESB接口数据=================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		MccpPosAcceptInterfaceImpl mccESBImpl = (MccpPosAcceptInterfaceImpl) applicationContext
				.getBean("MccpPosAcceptInterface");
		// 测试根据条件查询可取消转账暂停数据
		List<Map<String, Object>> data1 = mccESBImpl.getCancelTransferStopData(
				"C00003215762", "20110000000002208079", "0146702990596883");
		// 测试连接数据
		// List<PosInfoDTO> list=
		// dao.queryPosInfoByPosNoForUnsuspendBankAccount("20110000000002208079");
		System.out.println("打印数据1：" + data1);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		// 测试根据保单号查保单自垫及借款信息

		Map<String, Object> data2 = mccESBImpl
				.getAplLoanAndAplExtraFeeInfo("0110002060058830");
		System.out.println("打印数据2：" + data2);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试根据保单号查询该保单下可以做收付款方式调整失败的所有保全项目
		List<Map<String, Object>> data3 = mccESBImpl
				.getPayServiceItemsByPolicyNo("P000000000262281");
		System.out.println("打印数据3：" + data3);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试根据保全号查询原收付款信息
		Map<String, Object> data4 = mccESBImpl
				.getOriginalPaymentInfoByPosNo("20110000000002208079");
		System.out.println("打印数据4：" + data4);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试取消转账暂停操作
		Map<String, Object> data5 = mccESBImpl
				.cancelTransferStopOperation("20110000000002208079");
		System.out.println("打印数据5：" + data5);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试 VIP赠险回访 查询客户VIP信息
		Map<String, Object> data6 = mccESBImpl
				.getClientVIPInfoByClientNo("C00004872802");
		System.out.println("查询vip信息 打印数据6：" + data6);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试 收付款失败回访 查询保单保全列表C00001763175 C00001586718
		List<Map<String, Object>> data7 = mccESBImpl
				.getPayFailPolicyInfoByClientNo("C00001763175");
		System.out.println("打印数据7：" + data7);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// 测试 测算还款接口
		ServiceItemsInputDTO objectData = new ServiceItemsInputDTO_9();
		objectData.setPolicyNo("0020002040005283");
		objectData.setPosNo("20110000000000612399");
		ServiceItemsInputDTO_9 data8 = mccESBImpl
				.queryServiceItemsInfoExtraToMccp_9(objectData);
		System.out.println("[[[[[[[------打印数据8：" + data8.toString());
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		// EsbServiceItemsInputDTO esii = new EsbServiceItemsInputDTO_9();
		// esii.setPolicyNo("P000000001755761");
		// esii.setServiceItems("9");
		// // esii.setApplyDate("2012-05-22");
		// esii.setApplyDate("2015-11-18");
		// PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
		// applyInfo.setPolicyNo("P000000001755761");
		// applyInfo.setClientNo("C00005368115");
		//
		// applyInfo.setPaymentType("2");
		// applyInfo.setAcceptChannelCode("4");
		// applyInfo.setAcceptor("lijiang.wb@sino-life.com");
		// SimpleDateFormat sfm = new SimpleDateFormat("yy-MM-dd");
		// Date date = new Date();
		// try {
		// date = sfm.parse("2015-11-18");
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// applyInfo.setApplyDate(date);
		// applyInfo.setApplyType("1");
		// applyInfo.setApprovalServiceType("1");
		// applyInfo.setBankCode("102100000021");
		// applyInfo.setServiceItems("9");
		// applyInfo.setAccountNoType("1");
		// applyInfo.setTransferAccountno("456789");
		// applyInfo.setTransferAccountOwner("徐长山");

		System.out
				.println("===================================================");
		System.out
				.println("===================保单还款测算接口=======================");
		System.out
				.println("===================================================");
		// List<Map<String, Object>> data9 = mccESBImpl.acceptDetailInputSubmit(
		// esii, applyInfo);
		// System.out.println("[[[[[[[------打印数据9：" + data9.toString());
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		String data10 = mccESBImpl.getUserMobileByClientNo("C00003466058");
		System.out.println("======查询手机号码：" + data10);
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");

		System.out
				.println("===================================================");
		System.out.println("=================完成测试ESB接口数据================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
		System.out
				.println("===================================================");
	}

}
