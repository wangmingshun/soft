package com.sinolife.pos.common.dao.serviceitems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.InsuredClientDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_20;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.rpc.ilogjrules.posrules.POSJrulesCheck;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

@SuppressWarnings({ "unchecked" })
@Repository("serviceItemsDAO")
public abstract class ServiceItemsDAO extends AbstractPosDAO {

	@Autowired
	protected CommonQueryDAO commonQueryDAO;

	@Autowired
	protected ClientInfoDAO clientInfoDAO;

	@Autowired
	protected PosRulesDAO posRulesDAO;

	@Autowired
	protected POSJrulesCheck jruleCheck;

	@Autowired
	protected CommonAcceptDAO acceptDAO;

	private static final Map<String, String> ACCEPT_DETAIL_CONFIG_MAP;
	public static final Map<String, String> STATUS_RESTRICT_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("specialRuleType", "5|002|1|3|clientNo"); // 规则特殊件
		map.put("specialFunc", "5|003|1|3|clientNo"); // 功能特殊件
		map.put("callConfirm", "5|004|1|3|clientNo"); // 电话回访确认件
		map.put("applyDate", "5|005|1|3|clientNo"); // 申请日期
		// map.put("specialRuleType", "5|005|1|3|clientNo"); // 规则特殊件类型
		// map.put("specialRetreatReason", "5|013|1|3|clientNo"); //协议退费原因

		ACCEPT_DETAIL_CONFIG_MAP = map;
		

		map = new ConcurrentHashMap<String, String>();
		// 保全项目->允许的保单状态,允许的保单缴费状态,允许的险种状态,允许的险种失效原因,允许的险种缴费状态,允许的险种缴费来源,保单层变更以主险为险种层变更对象
		map.put("1", "1,NA,1,NA,NA,NA,N"); // 犹豫期退保
		map.put("2", "NA,NA,NA,8/9/10/11/12/13,NA,NA,N");// 退保
		map.put("3", "1,0/1,1,NA,0/1,1,N"); // 加保
		map.put("4", "NA,NA,NA,8,NA,1/4,N"); // 减保
		map.put("5", "1,1,1,NA,1,1,Y"); // 新增附加险
		map.put("6", "NA,0/1/2,2,8/11/12,0/2,1/3/6,N"); // 复效
		map.put("7", "NA,1/2,NA,8,1/2,1,N"); // 减额缴清
		map.put("8", "1,0/1,1,NA,0/1,1,Y"); // 保单贷款
		map.put("9", "1,0/1,1,NA,0/1,1/3,Y"); // 保单还款
		map.put("10", "NA,NA,NA,5/6/7/8/9/10/11/12/13,NA,NA,N"); // 生存保险金领取
		map.put("11", "NA,NA,NA,7/8/10/11/12,NA,NA,Y"); // 红利领取
		map.put("12", "NA,NA,NA,7,NA,1,Y"); // 年龄性别错误更正
		map.put("13", "1,1,1,NA,1,1,Y"); // 缴费频次变更
		map.put("14", "1,1,1,NA,1,1,N"); // 缴费年期变更
		map.put("15", "NA,NA,NA,NA,NA,1,Y"); // 职业等级变更
		map.put("16", "NA,NA,NA,NA,NA,1,Y"); // 补充告知
		map.put("17", "NA,NA,NA,7/8/10/11/12,0/1,NA,N"); // 续期保费退费
		map.put("18", "NA,NA,NA,5/6/7/8/9/10/11/12/13,NA,NA,Y"); // 签名变更
		map.put("19", "NA,NA,NA,7/8/9/10/11/12/13,NA,NA,Y"); // 联系方式变更
		map.put("20", "1,0/1,1,NA,0/1,1,Y"); // 投保人变更
		map.put("21", "NA,NA,NA,5/6/7/8/9/10/11/12/13,NA,NA,Y"); // 客户资料变更
		map.put("22", "1,NA,1,5/6/8/9/10/11/12/13,NA,NA,Y"); // 受益人变更
		map.put("23", "1,1,1,NA,1,1,Y"); // 续期交费方式变更
		map.put("24", "1,NA,1,NA,NA,NA,N"); // 年金给付方式选择
		map.put("25", "1,1,1,NA,1,NA,Y"); // 自垫选择方式变更
		map.put("26", "1,NA,1,8/9/11/12,0/1,NA,Y"); // 红利给付方式变更
		map.put("27", "1,0/1,1,8/9/10/11/12/13,0/1,NA,Y");// 保单补发
		map.put("28", "NA,NA,NA,7/8/9/10/11/12/13,NA,NA,Y");// 保单挂失和挂失解除
		map.put("29", "1,1,1,NA,1,NA,N"); // 预约终止附加险
		map.put("30", "1,0/1,1,NA,0/1,1,Y"); // 保单质押贷款-贷款受理
		map.put("31", "NA,NA,NA,7/8/9/10/11/12/13,0/1,NA,Y"); // 保单质押贷款-还款解冻
		map.put("32", "1,0/1,1,NA,0/1,NA,Y"); // 提前满期申请
		map.put("33", "NA,NA,NA,NA,NA,NA,N"); // 保全收付款方式调整
		map.put("34", "NA,NA,NA,NA,NA,NA,Y"); // 保险金转换年金
		map.put("35", "1,NA,1,NA,NA,NA,Y"); // 追加保费
		map.put("36", "1,NA,1,NA,NA,NA,Y"); // 基本保费变更
		map.put("37", "1,NA,1,NA,NA,NA,N"); // 账户部分领取
		map.put("38", "1,NA,1,NA,NA,NA,Y"); // 账户分配比例变更
		map.put("39", "1,NA,1,NA,NA,NA,Y"); // 投资账户转换
		STATUS_RESTRICT_CONFIG_MAP = map;
	}

	/******************************************
	 * 根据保全号查询公共受理录入信息
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemsInputDTO queryCommonInputByPosNo(String posNo) {
		ServiceItemsInputDTO sii = (ServiceItemsInputDTO) getSqlMapClientTemplate()
				.queryForObject(
						ServiceItemsDAO.class.getName()
								+ ".QUERY_COMMON_ACCEPT_DETAIL_INPUT", posNo);
		String policyNo = sii.getPolicyNo();
		String serviceItems = sii.getServiceItems();

		// 查询保单补发次数
		sii.setPolicyProvideTime(String.valueOf(commonQueryDAO
				.getPolicyProvideTimeByPolicyNo(policyNo)));

		// 保单补发次数，一般保全项目该域为显示域，特别保全项目为录入域
		// 这里默认设置可编辑标志为false，由子类决定保单补发次数是否可编辑
		sii.setPolicyProvideTimeEditable(false);

		// 查询功能特殊件选项
		List<CodeTableItemDTO> specialFuncList = getSqlMapClientTemplate()
				.queryForList(
						ServiceItemsDAO.class.getName()
								+ ".querySpecialFuncListByServiceItems",
						serviceItems);
		sii.setSpecialFuncList(specialFuncList);
		// 当查询出可选的特殊件选项时，才允许显示特殊功能件录入域
		sii.setSpecialFuncEnabled(specialFuncList != null
				&& specialFuncList.size() > 0);

		// 协议退费原因
		// List<CodeTableItemDTO> specialRetreatReasonList =new
		// ArrayList<CodeTableItemDTO> ();
		// specialRetreatReasonList.add(new CodeTableItemDTO("0","--"));
		// specialRetreatReasonList.addAll(getSqlMapClientTemplate().queryForList(ServiceItemsDAO.class.getName()
		// + ".querySpecialRetreatReason"));

		// sii.setSpecialRetreatReasonList(specialRetreatReasonList);
		// 查询用户能操作的功能特殊件
		sii.setFuncSpecPriv(getSqlMapClientTemplate().queryForList(
				ServiceItemsDAO.class.getName() + ".queryUserFuncSpecPrivs",
				PlatformContext.getCurrentUser()));

		// 查询规则特殊件选项
		List<CodeTableItemDTO> specialRuleList = getSqlMapClientTemplate()
				.queryForList(
						ServiceItemsDAO.class.getName()
								+ ".querySpecialRuleList");
		sii.setSpecialRuleList(specialRuleList);

		// 查询规则特殊件原因选项
		List<CodeTableItemDTO> specialRuleReasonList = getSqlMapClientTemplate()
				.queryForList(
						ServiceItemsDAO.class.getName()
								+ ".querySpecialRuleReasonList");
		sii.setSpecialRuleReasonList(specialRuleReasonList);

		// 查询申请保单是否为业务员自保件
		sii.setSelfInsured(posRulesDAO.isSelfInsured(policyNo));

		// 查询投被保人是否为同一人
		sii.setAppInsTheSame(commonQueryDAO.isAppInsTheSame(policyNo));

		// 查询投保人客户号
		sii.setClientNoApp(commonQueryDAO.getApplicantByPolicyNo(policyNo));

		// 查询被保人客户号
		//家庭单多被保险人 查询
		sii.setClientNoIns(commonQueryDAO
				.getInsuredOfPrimaryPlanByPolicyNoNew(policyNo,"1"));
		sii.setClientNoInsList(commonQueryDAO.getInsuredClientList(policyNo));
//		//测试使用
//		List<InsuredClientDTO> insuredClientList= new ArrayList<InsuredClientDTO>();
//		InsuredClientDTO test1 = new InsuredClientDTO();
//		test1.setInsuredName("赵维双");
//		test1.setInsuredNo("C00000000006");
//		test1.setInsuredSeq("1");
//		InsuredClientDTO test2 = new InsuredClientDTO();
//		test2.setInsuredName("钟民发");
//		test2.setInsuredNo("C00000000010");
//		test2.setInsuredSeq("2");
//		InsuredClientDTO test3 = new InsuredClientDTO();
//		test3.setInsuredName("周远华");
//		test3.setInsuredNo("C00000000017");
//		test3.setInsuredSeq("3");
//		insuredClientList.add(test1);
//		insuredClientList.add(test2);
//		insuredClientList.add(test3);
//		sii.setClientNoInsList(insuredClientList);
		List<ClientInformationDTO> clientInfoAppList = clientInfoDAO.selClientinfoForClientno(commonQueryDAO.getApplicantByPolicyNo(policyNo));
		sii.setClientInfoAppList(clientInfoAppList);
		//查询保单投保单号
		sii.setPolicyApplyBarCode(commonQueryDAO.queryPolicyApplyBarcode(policyNo));

		// 将父类对象转化成子类对象
		try {
			Class<?> clz = Class
					.forName("com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_"
							+ serviceItems);
			ServiceItemsInputDTO sii2 = (ServiceItemsInputDTO) clz
					.getDeclaredConstructor().newInstance();
			BeanUtils.copyProperties(sii, sii2);
			sii = sii2;
		} catch (Exception e) {
			logger.error(
					"create instance of com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_"
							+ serviceItems + " error", e);
		}

		// 查询子类项目所需要的信息
		return queryServiceItemsInfoExtra(sii);
	}

	/**
	 * 处理规则检查时填充BOM对象 注意此父类是在子类执行后才执行的 for override
	 * 
	 * @param bom
	 */
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		if (ArrayUtils.contains(new String[] { "1", "2", "3", "4", "7", "8",
				"14", "29", "32", "35", "36", "37", "38", "39" }, bom
				.getServiceItems().getServiceItem())) {
			StringBuilder productCode = new StringBuilder();

			boolean productHesitated = false; // 险种是否在犹豫期
			String doPolicyCancelSign; // 不能操作犹豫期撤销的原因
			// boolean policyScrupled = false; // 保单在犹豫期截止时间内
			for (POSUWBOMPlanDto plan : bom.getPlansList()) {
				if (plan.isCurrentPosPlan()) {
					productCode.append(plan.getProductCode()).append(";");
				}
			}
			productHesitated = posRulesDAO.isHesitatedProduct(
					bom.getPolicyNo(), productCode.toString(),
					bom.getApplyDate());

			// // 判断保单是否在犹豫期内
			// Map<String, Object> retMap = posRulesDAO.scruplePeriodPolicy(bom
			// .getPolicyNo());
			// if (retMap != null) {
			//
			// Date policySignBackDate = (Date) retMap.get("p_scruple_date");
			// policyScrupled = bom.getApplyDate().compareTo(
			// policySignBackDate) < 0;
			// }
			// 判断保单能否做犹豫期撤销
			Map<String, Object> retMap = posRulesDAO.isHesitateCondition(
					bom.getPolicyNo(), bom.getApplyDate());
			if (retMap != null) {
				doPolicyCancelSign = (String) retMap.get("p_reason");
				// 整单撤销不能操作犹豫期撤销的原因
				bom.setDoPolicyCancelSign(doPolicyCancelSign);

			}
			// 附件险撤销是否在犹豫期内
			bom.setHesitated(productHesitated);

		}

		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 功能特殊件
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "5");
		item.put("itemNo", "003");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("5".equals(detail.getPosObject())
						&& "003".equals(detail.getItemNo())) {
					// 功能特殊件
					bom.setSpecialFunc(detail.getNewValue());
				}
			}
		}
	}

	/**
	 * 创建保全BOM之后执行，无论将进行处理规则检查还是受理规则检查
	 * 
	 * @param bom
	 */
	public void fillBomPostCreated(POSBOMPolicyDto bom) {
		// do nothing
	}

	/**
	 * 自核规则检查及送人工核保时填充BOM对象 for override
	 * 
	 * @param bom
	 * @param posNo
	 * @param appPersonalNotice
	 * @param insPersonalNotice
	 */
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo,
			PersonalNoticeDTO appPersonalNotice,
			PersonalNoticeDTO insPersonalNotice) {
		// do nothing
	}

	private ThreadLocal<List<PosAcceptDetailDTO>> pubDetailList = new ThreadLocal<List<PosAcceptDetailDTO>>();

	/**
	 * 获取公共录入项的acceptDetail
	 * 
	 * @return
	 */
	protected List<PosAcceptDetailDTO> getPubDetailList() {
		return pubDetailList.get();
	}

	/**
	 * 转换pos_accept_detail记录， 公共录入项也需记录detail
	 * 
	 * @param serviceItemsInputDTO
	 * @return
	 */
	public List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				ACCEPT_DETAIL_CONFIG_MAP, serviceItemsInputDTO.getPosNo(), 0);
		generator.processSimpleDTO(serviceItemsInputDTO);
		// 电话回访确认件，并且上传了附件，则保存附件的文件名及ID
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getAttechmentFileId())
				&& "Y".equals(serviceItemsInputDTO.getCallConfirm())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"009", serviceItemsInputDTO.getClientNo(), "1",
					serviceItemsInputDTO.getAttechmentFileId(),
					serviceItemsInputDTO.getAttechmentFileName());
		}
		// 规则特殊件原因
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getSpecialRuleReason())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"018", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getSpecialRuleReason());
		}
		// 规则特殊件其它原因描述
		if (StringUtils.isNotBlank(serviceItemsInputDTO
				.getOtherSpecialRuleReason())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"020", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getOtherSpecialRuleReason());
		}
		// 规则特殊件其它类型描述
		if (StringUtils.isNotBlank(serviceItemsInputDTO
				.getOtherSpecialRuleType())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"019", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getOtherSpecialRuleType());
		}

		// 官网加挂的手机号
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getMobile())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"021", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getMobile());
		}
		// 官网注册邮箱
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getWebEmail())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"024", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getWebEmail());
		}
		/* edit by gaojiaming start */
		// 终端机编码	
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getTerminalCode())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"025", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getTerminalCode());
		}
		if (StringUtils.isNotBlank(serviceItemsInputDTO.getIs_wechat_attention())) {
			generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
					"027", serviceItemsInputDTO.getClientNo(), "1", "",
					serviceItemsInputDTO.getIs_wechat_attention());
		}
		 /* edit by gaojiaming end */
		/* edit by wangmingshun start */
		//获取保全信息
		PosInfoDTO posInfoDTO = commonQueryDAO.queryPosInfoRecord(serviceItemsInputDTO.getPosNo());
		//只有转账类型的才进行处理
		if(StringUtils.isNotBlank(posInfoDTO.getPaymentType()) && 
				"2".equals(posInfoDTO.getPaymentType())) {
			//是否原缴费账户
			boolean isPolicyAcctCb = posRulesDAO.currentOriginalAccountSame(serviceItemsInputDTO.getPosNo());
			if(isPolicyAcctCb) {
				generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
						"030", serviceItemsInputDTO.getClientNo(), "3", "", 
						"Y");
			} else {
				generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), "5",
						"030", serviceItemsInputDTO.getClientNo(), "3", "", 
						"N");
			}
		}
		/* edit by wangmingshun end */
		/*
		 * 代审信息或免填单标志写入公共项,此改动为解决保全明细勾选多张保单生成的其它保全号不能把免填单和代审信息的明细项写入的问题
		 * edit by gaojiaming start
		 */
		List<PosAcceptDetailDTO> posAcceptDetailDTOList = acceptDAO.getPosAcceptDetailDTOList(serviceItemsInputDTO
				.getPosNo());
		if (posAcceptDetailDTOList != null && posAcceptDetailDTOList.size() > 0) {
			for (PosAcceptDetailDTO posAcceptDetailDTO : posAcceptDetailDTOList) {
				generator.addAcceptDetail(serviceItemsInputDTO.getPosNo(), posAcceptDetailDTO.getPosObject(),
						posAcceptDetailDTO.getItemNo(), posAcceptDetailDTO.getObjectValue(),
						posAcceptDetailDTO.getProcessType(), "", posAcceptDetailDTO.getNewValue());
			}
		}
		/* 代审信息或免填单标志写入公共项 edit by gaojiaming end */	
		List<PosAcceptDetailDTO> list = generator.getResult();
		

		pubDetailList.set(list);

		List<PosAcceptDetailDTO> subList = generateAcceptDetailDTOList(
				serviceItemsInputDTO, generator.getGroupNo().intValue());
		if (subList != null && !subList.isEmpty()) {
			list.addAll(subList);
		}

		pubDetailList.remove();

		return list;
	}

	/**
	 * 数据库校验
	 * 
	 * @param serviceItemsInputDTO
	 * @param err
	 */
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		PosValidateWrapper wrapper = new PosValidateWrapper(err);
		// 校验保全状态，应该为A01/A02/A03中的其中一个，控制不能通过后退键重复受理
		String posNo = serviceItemsInputDTO.getPosNo();
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		String acceptStatusCode = posInfo.getAcceptStatusCode();
		wrapper.rejectIfNotInEnum("acceptStatusCode", acceptStatusCode,
				new String[] { "A01", "A02", "A03" }, "无效的受理状态："
						+ acceptStatusCode);

		if (serviceItemsInputDTO.getAppPersonalNotice() != null
				&& serviceItemsInputDTO.getInsPersonalNotice() != null) {
			String clientNoIns = commonQueryDAO
					.getInsuredOfPrimaryPlanByPolicyNo(serviceItemsInputDTO
							.getPolicyNo());
			String clientNoApp = null;
			if ("20".equals(serviceItemsInputDTO.getServiceItems())
					&& serviceItemsInputDTO instanceof ServiceItemsInputDTO_20) {
				clientNoApp = ((ServiceItemsInputDTO_20) serviceItemsInputDTO)
						.getApplicantNo();
			} else {
				clientNoApp = commonQueryDAO
						.getApplicantByPolicyNo(serviceItemsInputDTO
								.getPolicyNo());
			}
			if (StringUtils.isNotBlank(clientNoApp)
					&& clientNoApp.equals(clientNoIns)) {
				wrapper.addErrMsg("insPersonalNotice", "投被保人相同，只允许录入一份个人资料告知!");
			}
		}
	}

	/**
	 * 保全项目生成受理明细列表
	 * 
	 * @param serviceItemsInputDTO
	 * @param beginGroupNo
	 * @return
	 */
	protected abstract List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo);

	/*************************************
	 * 根据保全号查询具体项目的受理录入信息
	 * 
	 * @param posNo
	 * @return
	 */
	protected abstract ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 两个值是否相同，null和""视为相同 DTO里面多处有用于比较
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	protected boolean same(Object s1, Object s2) {
		return ((s1 == null || "".equals(s1)) && (s2 == null || "".equals(s2)))
				|| ((s1 != null && s2 != null) && s1.equals(s2));
	}

	/**
	 * 查询该posNo在受理明细表里最大的分组号 部分保全项目用到该值
	 * 
	 * @param posNo
	 * @return
	 */
	protected String maxGroupNo(String posNo) {
		return (String) getSqlMapClientTemplate().queryForObject(
				ServiceItemsDAO.class.getName() + ".QUERY_DETAIL_MAX_GROUPNO",
				posNo);
	}

	/**
	 * 公共项和客户层变更信息生成后，复制几份到各个保单层，只有posNo不一样 多个保全项目有这种需求
	 * 
	 * @param list
	 * @param posNo
	 */
	protected List<PosAcceptDetailDTO> replaceDetailListPosNo(
			List<PosAcceptDetailDTO> detailList, String posNo) {
		List<String> keys = new ArrayList<String>();
		keys.add("5002");// 公共项的object和itemNo的拼值，参考本类的ACCEPT_DETAIL_CONFIG_MAP
		keys.add("5003");
		keys.add("5004");
		keys.add("5005");

		List<PosAcceptDetailDTO> list = (List<PosAcceptDetailDTO>) PosUtils
				.deepCopy(detailList);
		PosAcceptDetailDTO detail;
		for (int i = 0; list != null && i < list.size(); i++) {
			detail = list.get(i);
			detail.setPosNo(posNo);
			if (keys.contains(detail.getPosObject() + detail.getItemNo())
					&& detail.getObjectValue().startsWith("E")) {
				detail.setObjectValue(posNo);// 公共项的objectValue如果写的保全号，就替换掉，有的不是，如12
			}
		}
		return list;
	}

	/**
	 * 保全项目中途生成pos_info 多个保全项目有用此逻辑
	 * 
	 * @param itemInput
	 * @param policyNo
	 * @return posNo
	 */
	protected String insertPosInfoRecord(ServiceItemsInputDTO itemDTO,
			String policyNo) {
		String posNo = itemDTO.getPosNo();

		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();

		// 若是申请保单，则没啥要做的
		if (itemDTO.getPolicyNo().equals(policyNo)) {

			// 若不是，对新的policyNo需新插入pos_info记录
		} else {

			// 移动保全所用，查询保全是否是移动保全所做，找出受理时录入的客户发短信所用手机号码
			String mposMobile = null;
			List<String> phoneList = commonQueryDAO.queryPosAcceptDetailInfo(
					posNo, "5", "017");
			if (phoneList != null && phoneList.size() > 0) {
				mposMobile = phoneList.get(0);
			}

			// 更新捆绑顺序：同批次and不同于当前保单号对应的保全的项目and受理序号比本保全记录更大
			sqlClient.update(ServiceItemsDAO.class.getName()
					+ ".UPDATE_POS_INFO_ACCEPT_SEQ", posNo);

			PosInfoDTO posInfo = (PosInfoDTO) sqlClient.queryForObject(
					CommonQueryDAO.class.getName() + ".queryPosInfoRecord",
					posNo);
			posInfo.setPolicyNo(policyNo);
			String seq = (String) sqlClient.queryForObject(
					ServiceItemsDAO.class.getName()
							+ ".QUERY_ITEMS_MAX_ACCEPT_SEQ", posInfo);// 本项目最大序号+1
			posInfo.setAcceptSeq(Integer.valueOf(seq));

			// 然后插入一条pos_info记录
			sqlClient.queryForObject(CommonAcceptDAO.class.getName()
					+ ".INSERT_POS_INFO", posInfo);

			posNo = posInfo.getPosNo();// 返回新生成的posNo
			itemDTO.addPosNo(posNo);

			// 保单置暂停
			acceptDAO.doPolicySuspend(policyNo, "5", posNo,
					posInfo.getServiceItems(), commonQueryDAO.getSystemDate());

			// 增加移动保全受理信息
			if (mposMobile != null && !"".equals(mposMobile)) {
				PosAcceptDetailDTO detail;
				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posNo);
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("016");
				detail.setObjectValue(posNo);
				detail.setNewValue(posInfo.getAcceptor());// 移动保全受理人员
				detail.setOldValue(posInfo.getAcceptor());
				acceptDAO.insertPosAcceptDetail(detail);

				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posNo);
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("017");
				detail.setObjectValue(mposMobile);
				detail.setNewValue(mposMobile);// 移动保全通知电话
				detail.setOldValue(mposMobile);
				acceptDAO.insertPosAcceptDetail(detail);
			}

		}
		return posNo;
	}

	/**
	 * 查询指定的procType和itemNo的录入数据
	 * 
	 * @param posNo
	 * @param procTypeAndItemNoList
	 * @return
	 */
	protected List<PosAcceptDetailDTO> queryPosAcceptDetail(String posNo,
			List<Map<String, String>> procTypeAndItemNoList) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("procTypeAndItemNoList", procTypeAndItemNoList);
		return getSqlMapClientTemplate().queryForList(
				ServiceItemsDAO.class.getName() + ".queryPosAcceptDetail",
				paraMap);
	}

	/**
	 * 检查保单的受理规则
	 * 
	 * @param dto
	 * @param policyNo
	 * @return
	 */
	protected POSVerifyResultDto acceptRuleCheck(ServiceItemsInputDTO dto,
			String policyNo) {
		// 对于试算，取消规则检查
		if (!dto.getBarcodeNo().startsWith("trial")) {
			POSBOMPolicyDto bom;
			PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(dto
					.getPosNo());
			try {
				bom = (POSBOMPolicyDto) jruleCheck.createBOMForAcceptRuleCheck(
						policyNo, posInfo.getClientNo(), posInfo
								.getServiceItems(), new SimpleDateFormat(
								"yyyy-MM-dd").parse(dto.getApplyDate()),
						posInfo.getAcceptDate(), posInfo.getAcceptor(), dto
								.getApplyType(), dto.getAcceptChannel(),
					    null,
						posRulesDAO.currentServiceItemsList(dto.getPosNo()));
			} catch (ParseException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			POSVerifyResultDto verifyResult = jruleCheck.excuteRules(bom);
			logger.info("RULE_ENGINE verifyResult:"
					+ PosUtils.describBeanAsJSON(verifyResult));
			return verifyResult;
		} else {
			logger.info("试算忽略受理规则检查：" + policyNo);
			return null;
		}
	}

	protected void setCanBeSelectedFlag(List<PolicyProductDTO> policyProductList) {
		String serviceItems = getClass().getName()
				.substring(getClass().getName().length() - 2)
				.replaceAll("\\D", "");
		String configStr = STATUS_RESTRICT_CONFIG_MAP.get(serviceItems);
		if (StringUtils.isNotBlank(configStr) && policyProductList != null
				&& !policyProductList.isEmpty()) {
			String[] configArr = configStr.toUpperCase().split(",");
			if (configArr.length == 7) {
				Set<String> permitPolicyStatus = new HashSet<String>(
						Arrays.asList(configArr[0].split("\\/"))); // 允许的保单状态
				Set<String> permitPolicyPremStatus = new HashSet<String>(
						Arrays.asList(configArr[1].split("\\/"))); // 允许的保单缴费状态
				Set<String> permitProductStatus = new HashSet<String>(
						Arrays.asList(configArr[2].split("\\/"))); // 允许的险种状态
				Set<String> permitProductLapseReason = new HashSet<String>(
						Arrays.asList(configArr[3].split("\\/")));// 允许的险种失效原因
				Set<String> permitProductPremStatus = new HashSet<String>(
						Arrays.asList(configArr[4].split("\\/")));// 允许的险种缴费状态
				Set<String> permitProductPremSource = new HashSet<String>(
						Arrays.asList(configArr[5].split("\\/")));// 允许的险种缴费来源
				// String altPrimaryProduct = configArr[6]; //保单层变更以主险为险种层变更对象
				PolicyDTO policyInfo = commonQueryDAO
						.queryPolicyInfoByPolicyNo(policyProductList.get(0)
								.getPolicyNo());
				for (PolicyProductDTO policyProduct : policyProductList) {
					StringBuilder sb = new StringBuilder("保单：")
							.append(policyProduct.getPolicyNo()).append("，序号：")
							.append(policyProduct.getProdSeq()).append(":");
					boolean canBeSelected = true;
					String message = null;
					if (canBeSelected && !permitPolicyStatus.isEmpty()
							&& !permitPolicyStatus.contains("NA")) {
						canBeSelected = permitPolicyStatus.contains(policyInfo
								.getDutyStatus());
						if (!canBeSelected) {
							sb.append("保单状态为：")
									.append(policyInfo.getDutyStatus())
									.append("，允许的保单状态为：")
									.append(permitPolicyStatus.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "保单状态不为" + permitPolicyStatus.toString();
						}
					}
					if (canBeSelected && !permitPolicyPremStatus.isEmpty()
							&& !permitPolicyPremStatus.contains("NA")) {
						canBeSelected = permitPolicyPremStatus
								.contains(policyInfo.getPremInfo()
										.getPremStatus());
						if (!canBeSelected) {
							sb.append("保单缴费状态：")
									.append(policyInfo.getPremInfo()
											.getPremStatus())
									.append("，允许的保单缴费状态为：")
									.append(permitPolicyPremStatus.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "保单缴费状态不为"
									+ permitPolicyPremStatus.toString();
						}
					}
					if (canBeSelected && !permitProductStatus.isEmpty()
							&& !permitProductStatus.contains("NA")) {
						canBeSelected = permitProductStatus
								.contains(policyProduct.getDutyStatus());
						if (!canBeSelected) {
							sb.append("险种状态：")
									.append(policyProduct.getDutyStatus())
									.append("，允许的险种状态为：")
									.append(permitProductStatus.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "险种状态不为" + permitProductStatus.toString();

						}
					}
					if (canBeSelected && !permitProductLapseReason.isEmpty()
							&& !permitProductLapseReason.contains("NA")
							&& "2".equals(policyProduct.getDutyStatus())) {
						canBeSelected = permitProductLapseReason
								.contains(policyProduct.getLapseReason());
						if (!canBeSelected) {
							sb.append("险种失效原因：")
									.append(policyProduct.getLapseReason())
									.append("，允许的险种失效原因为：")
									.append(permitProductLapseReason.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "险种失效原因不为"
									+ permitProductLapseReason.toString();
							if (serviceItems.equals("2")
									&& policyProduct.getProdSeq().intValue() == 1)// 如果是退保操作保单主险为‘满期’，可以进行选择
							{
								permitProductLapseReason.add("7");
								canBeSelected = permitProductLapseReason
										.contains(policyProduct
												.getLapseReason());
								message = "主险失效原因不为"
										+ permitProductLapseReason.toString();
								permitProductLapseReason.remove("7");
							}
						}
					}
					if (canBeSelected && !permitProductPremStatus.isEmpty()
							&& !permitProductPremStatus.contains("NA")) {
						canBeSelected = permitProductPremStatus
								.contains(policyProduct.getPremInfo()
										.getPremStatus());
						if (!canBeSelected) {
							sb.append("险种缴费状态：")
									.append(policyProduct.getPremInfo()
											.getPremStatus())
									.append("，允许的险种缴费状态为：")
									.append(permitProductPremStatus.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "险种缴费状态不为"
									+ permitProductPremStatus.toString();
						}
					}
					if (canBeSelected && !permitProductPremSource.isEmpty()
							&& !permitProductPremSource.contains("NA")) {
						canBeSelected = permitProductPremSource
								.contains(policyProduct.getPremInfo()
										.getPremSource());
						if (!canBeSelected) {
							sb.append("险种缴费来源：")
									.append(policyProduct.getPremInfo()
											.getPremSource())
									.append("，允许的险种缴费来源为：")
									.append(permitProductPremSource.toString())
									.append("，判断可选择：").append(canBeSelected);
							message = "险种缴费来源不为"
									+ permitProductPremSource.toString();
						}
					}
					if (!canBeSelected) {
						policyProduct.setCanBeSelectedFlag("N");
						policyProduct.setMessage(message);
					} else if (StringUtils.isBlank(policyProduct
							.getCanBeSelectedFlag())) {
						sb.append("，判断可选择：").append(canBeSelected);
						policyProduct.setCanBeSelectedFlag("Y");
					}
					logger.info(sb.toString());
				}
			}
		}
	}

	/**
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemsInputDTO queryCommonAcceptDetailInput(String posNo) {

		return (ServiceItemsInputDTO) getSqlMapClientTemplate().queryForObject(
				ServiceItemsDAO.class.getName()
						+ ".QUERY_COMMON_ACCEPT_DETAIL_INPUT", posNo);

	}

}
