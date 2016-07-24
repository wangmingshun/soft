package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_10;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 10 生存保险金领取
 */
@Repository("serviceItemsDAO_10")
public class ServiceItemsDAO_10 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("payType", "1|120|1|3|policyNo");// 生存金领取方式
		map.put("toPolicyNo", "1|121|1|3|policyNo");// 转入保单号
		map.put("toApplyBarcode", "1|122|1|3|policyNo");// 转入投保单号
		map.put("survivalSeq", "2|021|1|3|prodSeq"); // 生存金给付序号
		map.put("accountOwner", "4|016|1|3|beneficiaryNo");// 账户户主姓名
		map.put("bankCode", "4|017|1|3|beneficiaryNo");// 银行代码
		map.put("accountNo", "4|018|1|3|beneficiaryNo");// 转账账号
		map.put("transferGrantFlag", "4|019|1|3|beneficiaryNo");// 授权状态
		map.put("benefitType", "4|021|1|3|beneficiaryNo");// 受益人类型
		map.put("payDate", "1|151|1|3|policyNo");// 红满堂生存金领取日期
		map.put("payDate", "1|152|1|3|policyNo");// 红满堂生存金领取日期
		PROC_PROPERTY_CONFIG_MAP = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_10 item = (ServiceItemsInputDTO_10) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();

		// 查询受益人信息
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", policyNo);
		criteriaMap.put("benefitStatus", "Y");
		List<PolicyBeneficiaryDTO> beneficiaryInfoList = commonQueryDAO
				.queryBeneficiaryByCriteria(criteriaMap);
		item.setBeneficiaryInfoList(beneficiaryInfoList);

		// 查询生存金应领信息
		criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", policyNo);
		List<PosSurvivalDueDTO> survivalDueList = commonQueryDAO
				.querySurvivalDueByCriteria(criteriaMap);

		// 判断生存金是否可领取
		if (survivalDueList != null && !survivalDueList.isEmpty()) {
			for (Iterator<PosSurvivalDueDTO> it = survivalDueList.iterator(); it
					.hasNext();) {
				PosSurvivalDueDTO posSurvivalDueDTO = it.next();
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("p_pk_serial", posSurvivalDueDTO.getPkSerial());
				paraMap.put("p_draw_type", "1");
				queryForObject("isSurvivalDrawable", paraMap);
				String isDrawable = (String) paraMap.get("p_is_drawable");
				String undrawMessage = (String) paraMap.get("p_undraw_message");
				if ("E".equals(isDrawable)) {
					throw new RuntimeException("查询生存金是否可领取出错：" + undrawMessage);
				}
				posSurvivalDueDTO.setCanBeSelectedFlag(isDrawable);
				posSurvivalDueDTO.setMessage(undrawMessage);
			}
		}
		item.setSurvivalDueList(survivalDueList);

		// 查询转账可选授权受益人类型
		Set<String> benefTypeSet = new HashSet<String>();
		for (PolicyBeneficiaryDTO pb : beneficiaryInfoList) {
			if (pb.getBenefitType() != "2") {
				benefTypeSet.add(pb.getBenefitType());
			}
		}
		/*
		 * for(PolicyProductDTO pp :
		 * commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo()))
		 * { String productCode = pp.getProductCode(); String
		 * survBenefSpecialConvention =
		 * commonQueryDAO.pdSelPrdParameterValuePro(productCode, "99");
		 * if("1".equals(survBenefSpecialConvention)) { benefTypeSet.add("5"); }
		 * else if("2".equals(survBenefSpecialConvention)) {
		 * benefTypeSet.add("4"); benefTypeSet.add("5"); } //保证给付责任
		 * if(commonQueryDAO.hasPromisePayDuty(productCode)) {
		 * benefTypeSet.add("3"); } };
		 */
		List<CodeTableItemDTO> beneficiaryTypeList = new ArrayList<CodeTableItemDTO>();
		if (benefTypeSet.contains("1")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("1", "生存金受益人"));
		}
		if (benefTypeSet.contains("4")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("4", "满期金受益人"));
		}
		if (benefTypeSet.contains("5")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("5", "年金受益人"));
		}
		if (benefTypeSet.contains("3")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("3", "剩余养老金受益人"));
		}
		item.setBeneficiaryTypeList(beneficiaryTypeList);

		// 初始设置为选中生存金受益人
		item.setBenefitTypeSelected("1");
		// 根据保单号获取所有险种信息
		String productCodes = "";
		for (PolicyProductDTO pp : commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo())) {
			productCodes += pp.getProductCode() + ",";
		}
		item.setProductCodes(productCodes);
		return item;
	}

	/**
	 * 提供给银保通满期领取
	 * 
	 * @param serviceItemsInputDTO
	 * @return
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_10(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_10 item = (ServiceItemsInputDTO_10) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();

		// 查询受益人信息
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", policyNo);
		criteriaMap.put("benefitStatus", "Y");
		List<PolicyBeneficiaryDTO> beneficiaryInfoList = commonQueryDAO
				.queryBeneficiaryByCriteria(criteriaMap);
		item.setBeneficiaryInfoList(beneficiaryInfoList);
		String clientNoApp=commonQueryDAO.getApplicantByPolicyNo(policyNo);
		String clientNoIns=commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
		item.setClientNoApp(clientNoApp);
		item.setClientNoIns(clientNoIns);
		// 查询满期金应领信息
		criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", policyNo);
		//criteriaMap.put("survivaltype", "1");// 满期金
		List<PosSurvivalDueDTO> survivalDueList = commonQueryDAO
				.querySurvivalDueByCriteria(criteriaMap);

		// 判断生存金是否可领取
		if (survivalDueList != null && !survivalDueList.isEmpty()) {
			for (Iterator<PosSurvivalDueDTO> it = survivalDueList.iterator(); it
					.hasNext();) {
				PosSurvivalDueDTO posSurvivalDueDTO = it.next();
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("p_pk_serial", posSurvivalDueDTO.getPkSerial());
				paraMap.put("p_draw_type", "1");
				queryForObject("isSurvivalDrawable", paraMap);
				String isDrawable = (String) paraMap.get("p_is_drawable");
				String undrawMessage = (String) paraMap.get("p_undraw_message");
				if ("E".equals(isDrawable)) {
					throw new RuntimeException("查询生存金是否可领取出错：" + undrawMessage);
				}
				posSurvivalDueDTO.setCanBeSelectedFlag(isDrawable);
				posSurvivalDueDTO.setMessage(undrawMessage);
				if ("Y".equals(isDrawable)){
					posSurvivalDueDTO.setChecked(true);
				}	
			}
		}
		item.setSurvivalDueList(survivalDueList);

		// 查询转账可选授权受益人类型
		Set<String> benefTypeSet = new HashSet<String>();
		for (PolicyBeneficiaryDTO pb : beneficiaryInfoList) {
			if (pb.getBenefitType() != "2") {
				benefTypeSet.add(pb.getBenefitType());
			}
		}
		/*
		 * for(PolicyProductDTO pp :
		 * commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo()))
		 * { String productCode = pp.getProductCode(); String
		 * survBenefSpecialConvention =
		 * commonQueryDAO.pdSelPrdParameterValuePro(productCode, "99");
		 * if("1".equals(survBenefSpecialConvention)) { benefTypeSet.add("5"); }
		 * else if("2".equals(survBenefSpecialConvention)) {
		 * benefTypeSet.add("4"); benefTypeSet.add("5"); } //保证给付责任
		 * if(commonQueryDAO.hasPromisePayDuty(productCode)) {
		 * benefTypeSet.add("3"); } };
		 */

		List<CodeTableItemDTO> beneficiaryTypeList = new ArrayList<CodeTableItemDTO>();
		if (benefTypeSet.contains("1")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("1", "生存金受益人"));
		}
		if (benefTypeSet.contains("4")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("4", "满期金受益人"));
		}
		if (benefTypeSet.contains("5")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("5", "年金受益人"));
		}
		if (benefTypeSet.contains("3")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("3", "剩余养老金受益人"));
		}
		item.setBeneficiaryTypeList(beneficiaryTypeList);

		// 初始设置为选中生存金受益人
		item.setBenefitTypeSelected("1");
		item.setPayType("1");

		return item;
	}

	
	
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		final ServiceItemsInputDTO_10 item = (ServiceItemsInputDTO_10) serviceItemsInputDTO;

		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);

		generator.processSimpleDTO(item);

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		String clientNoIns = serviceItemsInputDTO.getClientNoIns();// 被保人客户号
		String applicationNo = serviceItemsInputDTO.getClientNoApp();// 投保人客户号
		// 对在咨诉系统受理的协议满期的生存金列表做特殊处理
		if ("10".equals(item.getSpecialFunc())
				|| "11".equals(item.getSpecialFunc())) {

			// PosAcceptDetailDTO detailDTO = new PosAcceptDetailDTO();

			String agreeMaturitySum = "";// 满期协议金额
			String branchPercent = "";// 分公司承担比例
			String backBenefit = "";// 既得利益
			// 查询满期金应领列表
			Map<String, Object> criteriaMap = new HashMap<String, Object>();
			criteriaMap.put("policyNo", item.getPolicyNo());
			criteriaMap.put("survivaltype", "1");// 满期金
			List<PosSurvivalDueDTO> maturityDueList = commonQueryDAO
					.querySurvivalDueByCriteria(criteriaMap);
			for (int i = 0; item.getSurvivalDueList() != null
					&& i < item.getSurvivalDueList().size(); i++) {
				PosSurvivalDueDTO posSurvivalDueDTO = item.getSurvivalDueList()
						.get(i);
				// 协议满期金领取，需要录入分公司承担比较及既得利益
				if ("1".equals(posSurvivalDueDTO.getSurvivalType())) {

					agreeMaturitySum = posSurvivalDueDTO.getAgreeMaturitySum();
					branchPercent = posSurvivalDueDTO.getBranchPercent();
					backBenefit = posSurvivalDueDTO.getBackBenefit();

				}

			}

			for (int i = 0; maturityDueList != null
					&& i < maturityDueList.size(); i++) {
				PosSurvivalDueDTO posSurvivalDueDTO = maturityDueList.get(i);
				// 协议满期金领取，需要录入分公司承担比较及既得利益
				if ("1".equals(posSurvivalDueDTO.getSurvivalType())) {

					posSurvivalDueDTO.setAgreeMaturitySum(agreeMaturitySum);
					posSurvivalDueDTO.setBranchPercent(branchPercent);
					posSurvivalDueDTO.setBackBenefit(backBenefit);

				}

			}

			PolicyBeneficiaryDTO policyBeneficiaryDTO = null;
			String survClientNo = "";// 生存金收益人客户号
			String matuClientNo = "";// 满期金受益人客户号
			String annuityClientNo = "";// 年金受益人客户号
			List<PolicyBeneficiaryDTO> beneficiaryInfoList = item
					.getBeneficiaryInfoList();

			if (beneficiaryInfoList != null && !beneficiaryInfoList.isEmpty()) {

				for (int i = 0; i < beneficiaryInfoList.size(); i++) {

					policyBeneficiaryDTO = (PolicyBeneficiaryDTO) beneficiaryInfoList
							.get(i);

					if ("1".equals(policyBeneficiaryDTO.getBenefitType())) {// 生存金受益人
						survClientNo = policyBeneficiaryDTO.getBenefInfo()
								.getClientNo();

					} else if ("4"
							.equals(policyBeneficiaryDTO.getBenefitType())) {// 满期金受益人
						matuClientNo = policyBeneficiaryDTO.getBenefInfo()
								.getClientNo();

					} else if ("5"// 年金受益人
					.equals(policyBeneficiaryDTO.getBenefitType())) {// 年金受益人
						annuityClientNo = policyBeneficiaryDTO.getBenefInfo()
								.getClientNo();

					}
				}

			}

			if ((!StringUtils.isBlank(matuClientNo)
					&& matuClientNo.equals(applicationNo))||clientNoIns.equals(applicationNo)) {
				acceptDetailList.add(new PosAcceptDetailDTO(
						serviceItemsInputDTO.getPosNo(), "2", "3", "2", "025",
						"", "Y"));// 投保人和满期金受益人为同一人
			}

			// 生存金受益人和满期金受益人为同一人,则需要将应领未领的生存金写入应领列表中
			if (!StringUtils.isBlank(matuClientNo)
					&& !StringUtils.isBlank(survClientNo)
					&& survClientNo.equals(matuClientNo)) {
				criteriaMap = new HashMap<String, Object>();
				criteriaMap.put("policyNo", item.getPolicyNo());
				criteriaMap.put("survivaltype", "2");// 生存保险金
				List<PosSurvivalDueDTO> survivalDueList = commonQueryDAO
						.querySurvivalDueByCriteria(criteriaMap);
				maturityDueList.addAll(survivalDueList);
			}

			// 生存金受益人和年金受益人为同一人,则需要将应领未领的生存金写入应领列表中
			if (!StringUtils.isBlank(matuClientNo)
					&& !StringUtils.isBlank(annuityClientNo)
					&& annuityClientNo.equals(matuClientNo)) {
				criteriaMap = new HashMap<String, Object>();
				criteriaMap.put("policyNo", item.getPolicyNo());
				criteriaMap.put("survivaltype", "3");// 养老年金
				List<PosSurvivalDueDTO> annuityDueList = commonQueryDAO
						.querySurvivalDueByCriteria(criteriaMap);
				maturityDueList.addAll(annuityDueList);

			}

			// 判断生存金是否可领取
			if (maturityDueList != null && !maturityDueList.isEmpty()) {

				for (Iterator<PosSurvivalDueDTO> it = maturityDueList
						.iterator(); it.hasNext();) {
					PosSurvivalDueDTO posSurvivalDueDTO = it.next();
					Map<String, Object> paraMap = new HashMap<String, Object>();
					paraMap.put("p_pk_serial", posSurvivalDueDTO.getPkSerial());
					paraMap.put("p_draw_type", "1");
					queryForObject("isSurvivalDrawable", paraMap);
					String isDrawable = (String) paraMap.get("p_is_drawable");
					String undrawMessage = (String) paraMap
							.get("p_undraw_message");
					if ("E".equals(isDrawable)) {
						throw new RuntimeException("查询生存金是否可领取出错："
								+ undrawMessage);
					}
					posSurvivalDueDTO.setCanBeSelectedFlag(isDrawable);
					posSurvivalDueDTO.setMessage(undrawMessage);
					posSurvivalDueDTO.setChecked(true);
				}

			}
			item.setSurvivalDueList(maturityDueList);

			for (int i = 0; maturityDueList != null
					&& i < maturityDueList.size(); i++) {
				PosSurvivalDueDTO posSurvivalDueDTO = maturityDueList.get(i);
				// 协议满期金领取，需要录入分公司承担比较及既得利益
				if ("1".equals(posSurvivalDueDTO.getSurvivalType())) {
					if ("10".equals(item.getSpecialFunc())) {
						acceptDetailList.add(new PosAcceptDetailDTO(
								serviceItemsInputDTO.getPosNo(), "2", "1", ""
										+ posSurvivalDueDTO.getProdSeq(),
								"028", null, posSurvivalDueDTO
										.getBranchPercent()));// 分公司承担比例'

						acceptDetailList
								.add(new PosAcceptDetailDTO(
										serviceItemsInputDTO.getPosNo(), "2",
										"1", ""
												+ posSurvivalDueDTO
														.getProdSeq(), "027",
										null, posSurvivalDueDTO
												.getBackBenefit()));// “协议满期-投诉业务”新增“应追回既得利益”
					}

					acceptDetailList.add(new PosAcceptDetailDTO(
							serviceItemsInputDTO.getPosNo(), "2", "1", ""
									+ posSurvivalDueDTO.getProdSeq(), "026",
									posSurvivalDueDTO.getPayDueSum().toString(), posSurvivalDueDTO.getAgreeMaturitySum()));// 协议满期金额
				}

			}

		}else{
			if (clientNoIns.equals(applicationNo)) {
				acceptDetailList.add(new PosAcceptDetailDTO(
						serviceItemsInputDTO.getPosNo(), "2", "3", "2", "025",
						"", "Y"));// 投保人和满期金受益人为同一人
			}
		}
		// 处理应领记录
		generator.processSimpleListDTO(item.getSurvivalDueList(),
				new ItemProcessFilter() {
					@Override
					public boolean needProcess(Map<String, Object> listItem,
							Map<String, Object> oldValueItem) {
						return (Boolean) listItem.get("checked")
								&& "Y".equals(listItem.get("canBeSelectedFlag"));
					}
				});

		// 处理账户授权信息
		final Set<String> benefTypeSet = new HashSet<String>();
		for (CodeTableItemDTO codeTable : item.getBeneficiaryTypeList()) {
			benefTypeSet.add(codeTable.getCode());
		}
		generator.processSimpleListDTO(item.getBeneficiaryInfoList(),
				new ItemProcessFilter() {
					@Override
					public boolean needProcess(Map<String, Object> listItem,
							Map<String, Object> oldValueItem) {
						// 2转账领取只能是1生存金受益人、3身故养老受益人、4满期金受益人、
						// 5年金受益人，并且只处理用户选择的受益人类型
						if ("2".equals(item.getPayType())) {
							String benefitType = (String) listItem
									.get("benefitType");
							if (benefTypeSet.contains(benefitType)
									&& benefitType.equals(item
											.getBenefitTypeSelected())) {
								return (Boolean) listItem.get("checked");
							}
						}
						return false;
					}
				});

		
		// 退保金转新投保单金额
		if (StringUtils.isNotBlank(item.getSurrenderToNewApplyBarCode())
				&& StringUtils.isNotBlank(item.getNewApplyBarCodePrem())) {

			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1", "1",
					item.getPolicyNo(), "154", null, item
							.getSurrenderToNewApplyBarCode()));
			BigDecimal newApplyBarCodePrem=new BigDecimal(item.getNewApplyBarCodePrem());
			
				acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "2", "1",
						item.getPolicyNo(), "270", null, item
								.getNewApplyBarCodePrem()));
			

		}
		
		generator.getResult().addAll(acceptDetailList);
		return generator.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com
	 * .sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
		PosValidateWrapper wrapper = new PosValidateWrapper(err);
		if (serviceItemsInputDTO instanceof ServiceItemsInputDTO_10) {
			ServiceItemsInputDTO_10 item = (ServiceItemsInputDTO_10) serviceItemsInputDTO;
			String payType = item.getPayType();
			if ("5".equals(payType)) {
				// 生存金转保单暂收余额
				String toPolicyNo = item.getToPolicyNo();
				if (!"Y".equals(queryForObject(
						"verifyPolicyNoCanBeTransferedInto", toPolicyNo))) {
					wrapper.addErrMsg("toPolicyNo", "无效的转入保单号");
				}
			} else if ("6".equals(payType)) {
				// 生存金转投保单暂收余额
				String applyBarcode = item.getToApplyBarcode();
				Map<String, Object> retMap = commonQueryDAO
						.uwValidateApplyReceipt(applyBarcode);
				if (!"1".equals(retMap.get("p_flag"))) {
					wrapper.addErrMsg("toApplyNo",
							"无效的转入投保单号:" + retMap.get("p_message"));
				} else if (!"Y".equals(retMap.get("p_receipt_flag"))) {
					wrapper.addErrMsg("toApplyNo", "无效的转入投保单号: 该投保单不可收费");
				}
			} else if ("2".equals(payType)) {
				// 转账领取
				List<PolicyBeneficiaryDTO> benefInfoList = item
						.getBeneficiaryInfoList();
				if (benefInfoList != null && !benefInfoList.isEmpty()) {
					for (int i = 0; i < benefInfoList.size(); i++) {
						// 校验银行信息
						PolicyBeneficiaryDTO benef = benefInfoList.get(i);
						String pathPrefix = "beneficiaryInfoList[" + i + "].";
						if (benef.isChecked()
								&& "Y".equals(benef.getTransferGrantFlag())) {
							Map<String, Object> retMap = commonQueryDAO
									.validateBankAccount(benef.getBankCode(),
											benef.getAccountNo());
							if ("1".equals(retMap.get("p_flag"))
									&& "N".equals(retMap.get("p_validate_flag"))) {
								wrapper.addErrMsg(pathPrefix + "bankNo",
										"账户信息校验不通过：" + retMap.get("p_mes"));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void fillBomPostCreated(POSBOMPolicyDto bom) {
		super.fillBomPostCreated(bom);

		// 存在有效金管家
		bom.setHasEffUian(((String) queryForObject("isHasEffUian",
				bom.getPolicyNo())).equals("Y"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForProcessRuleCheck
	 * (com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		super.fillBOMForProcessRuleCheck(bom);

		// 判定是否是满期金领取
		Map<String, Object> retMap = commonQueryDAO.isMaturitySurvivalDraw(bom
				.getPosNo());
		if (retMap != null) {
			String isMaturitySurvivalDraw = (String) retMap
					.get("is_maturity_draw");

			// 是满期金领取
			bom.setMaturitySurvivalDraw("Y".equals(isMaturitySurvivalDraw));

		}

		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 生存金领取方式
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "120");
		posObjectAndItemNoList.add(item);
		// 账户户主姓名
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "016");
		posObjectAndItemNoList.add(item);
		// 转入保单号
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "121");
		posObjectAndItemNoList.add(item);
		// 转入投保单号
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "122");
		posObjectAndItemNoList.add(item);
		// 选定的受益人类型
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "021");
		posObjectAndItemNoList.add(item);

		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("4".equals(detail.getPosObject())
						&& "021".equals(detail.getItemNo())) {
					String benefitType = detail.getNewValue();
					bom.setBenefitType(benefitType);
				}
				if ("1".equals(detail.getPosObject())
						&& "120".equals(detail.getItemNo())) {
					// 生存金领取方式
					String lifePaymentOption = detail.getNewValue();
					bom.setLifePaymentOption(lifePaymentOption);
					if ("1".equals(lifePaymentOption)) {
						// 1人工领取,取申请书的账号
						PosInfoDTO posInfo = commonQueryDAO
								.queryPosInfoRecord(bom.getPosNo());
						bom.setPaymentAccountant(posInfo.getPremName());
					} else if ("2".equals(lifePaymentOption)) {
						// 2生存金转账领取授权，取detail中记录的账号
						for (PosAcceptDetailDTO detailInternal : detailList) {
							if ("4".equals(detailInternal.getPosObject())
									&& "016".equals(detailInternal.getItemNo())) {
								bom.setPaymentAccountant(detailInternal
										.getNewValue());
							}
						}
					} else if ("5".equals(lifePaymentOption)
							|| "6".equals(lifePaymentOption)) {
						// 转投保单暂收余额和保单暂收余额
						Map<String, Object> paraMap = new HashMap<String, Object>();
						paraMap.put("p_pos_no", bom.getPosNo());
						queryForObject("ruleCheckIsSurvivalDrawable", paraMap);
						String isDrawable = (String) paraMap
								.get("p_is_drawable");
						String undrawableMessage = (String) paraMap
								.get("p_undrawable_message");
						if ("0".equals(paraMap.get("p_flag"))
								|| "E".equals(isDrawable)) {
							if ("Y".equals(isDrawable)) {
								bom.setSurvBenefSameWithAppOrInsured(true);
							} else {
								bom.setSurvBenefSameWithAppOrInsured(false);
								logger.info("p_pos_no:" + bom.getPosNo()
										+ " -> p_undrawable_message:"
										+ undrawableMessage);
							}
						} else {
							throw new RuntimeException("判断受益人是否可领生存金失败："
									+ paraMap.get("p_message") + ", "
									+ undrawableMessage);
						}
					}
				}
			}
		}
	}

}
