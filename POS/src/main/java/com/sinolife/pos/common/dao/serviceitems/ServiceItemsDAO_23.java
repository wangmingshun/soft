package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dto.ClientAccountDTO;
import com.sinolife.pos.common.dto.PolicyPremInfoDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

@SuppressWarnings("unchecked")
@Repository("serviceItemsDAO_23")
public class ServiceItemsDAO_23 extends ServiceItemsDAO {

	@Autowired
	PosCodeTableDAO posCodeTableDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

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
		ServiceItemsInputDTO_23 itemDTO = (ServiceItemsInputDTO_23) serviceItemsInputDTO;

		itemDTO.setApplicantName((String) queryForObject("QUERY_CLIENT_NAME",
				itemDTO.getClientNo()));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("applicantNo", itemDTO.getClientNo());
		map.put("policyNo", itemDTO.getPolicyNo());
		List<PolicyPremInfoDTO> policyList = queryForList(
				"QUERY_CLIENT_POLICYS", map);
		if (policyList != null && !policyList.isEmpty()) {
			for (PolicyPremInfoDTO policy : policyList) {
				if (!itemDTO.getPolicyNo().equals(policy.getPolicyNo())) {
					policy.setVerifyResult(acceptRuleCheck(itemDTO,
							policy.getPolicyNo()));
				}
			}
		}
		itemDTO.setPolicyPremList(policyList);
		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(itemDTO.getPolicyNo());
		itemDTO.setPolicyChannelType(policy_channel_type);

		// Map<String,Object> map=new HashMap<String,Object>();
		// map.put("posNo", itemDTO.getPosNo());
		// queryForObject("accountTypeRule1",map);
		// itemDTO.setCheckedCreditCard(((String)map.get("flag")).equals("0")?true:false);

		return itemDTO;
	}

	/* 提供给外围系统部调用 */
	public ServiceItemsInputDTO_23 queryServiceItemsInfo(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_23) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}

	/* 提供给银保通 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_23(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_23 itemDTO = (ServiceItemsInputDTO_23) serviceItemsInputDTO;

		itemDTO.setApplicantName((String) queryForObject("QUERY_CLIENT_NAME",
				itemDTO.getClientNo()));

		List<PolicyPremInfoDTO> policyList = queryForList("QUERY_POL_POLICYS",
				itemDTO.getPolicyNo());
		if (policyList != null && !policyList.isEmpty()) {
			for (PolicyPremInfoDTO policy : policyList) {
				if (itemDTO.getPolicyNo().equals(policy.getPolicyNo())) {
					policy.setChecked("Y");
				}
			}
		}
		itemDTO.setPolicyPremList(policyList);
		return itemDTO;
	}

	/* 提供给CMP使用 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToCMP_23(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_23 itemDTO = (ServiceItemsInputDTO_23) serviceItemsInputDTO;
		itemDTO.setAccountNoTypeList(posCodeTableDAO
				.queryCodeTable(CodeTableNames.FIN_ACCOUNT_NO_TYPE));
		itemDTO.setBankProvinceList(posCodeTableDAO
				.queryCodeTable(CodeTableNames.PROVINCE));
		itemDTO.setBankCategoryList(posCodeTableDAO
				.queryCodeTable(CodeTableNames.BANK_CATEGORY));
		return itemDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_23 itemDTO = (ServiceItemsInputDTO_23) serviceItemsInputDTO;
		ServiceItemsInputDTO_23 originDTO = (ServiceItemsInputDTO_23) itemDTO
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		// 客户层的变更，若存在，要写入每个保单对应的批单信息里面去
		List<PosAcceptDetailDTO> clientDetail = new ArrayList<PosAcceptDetailDTO>();
		ClientAccountDTO account = itemDTO.getAccount();
		if (StringUtils.isNotBlank(account.getAccountNo())) {
			itemDTO.groupNoAddOne();
			clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3",
					"3", itemDTO.getClientNo(), "038", null, account
							.getBankCode(), itemDTO.getGroupNo()));
			// clientDetail.add(new
			// PosAcceptDetailDTO(itemDTO.getPosNo(),"3","3",itemDTO.getClientNo(),"040",null,account.getClientAccount(),itemDTO.getGroupNo()));
			clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3",
					"3", itemDTO.getClientNo(), "041", null, account
							.getAccountNo(), itemDTO.getGroupNo()));
			if(account.getAccountNoType()==null)
			{
				account.setAccountNoType("1");
				
			}
			
			clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3",
					"3", itemDTO.getClientNo(), "095", null, account
							.getAccountNoType(), itemDTO.getGroupNo()));// 选择账号类型

		}
		// 保单层的变更信息
		List<PolicyPremInfoDTO> policyPremList = itemDTO.getPolicyPremList();
		for (int i = 0; policyPremList != null && i < policyPremList.size(); i++) {
			PolicyPremInfoDTO premDTO = policyPremList.get(i);
			// 选中的保单
			if ("Y".equals(premDTO.getChecked())) {
				String policyNo = premDTO.getPolicyNo();
				String posNo = insertPosInfoRecord(itemDTO, policyNo);

				PolicyPremInfoDTO bakPrem = getPremByPolicyNo(
						originDTO.getPolicyPremList(), policyNo);
				// 续期缴费方式
				acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1", "1",
						policyNo, "017", bakPrem.getChargingMethod(), itemDTO
								.getChargingMethod()));

				// 续期缴费账号发生变更,选择方式为转账的情况下
				if ("2".equals(itemDTO.getChargingMethod())) {
					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "016", bakPrem.getAccountSeq(),
							account.getAccountNo()));
				}
				// 客户层的添加进来
				acceptDetailList.addAll(replaceDetailListPosNo(clientDetail,
						posNo));
				if (!itemDTO.getPolicyNo().equals(policyNo)) {
					// 当且仅当是新的保全号(即不是第一个保单)，还有公共部分的明细
					acceptDetailList.addAll(replaceDetailListPosNo(
							getPubDetailList(), posNo));
				}
			}
		}
		return acceptDetailList;
	}

	/**
	 * 从List<PolicyPremInfoDTO>根据policyNo找到一条记录
	 * 
	 * @param policyPremList
	 * @param policyNo
	 * @return
	 */
	private PolicyPremInfoDTO getPremByPolicyNo(
			List<PolicyPremInfoDTO> policyPremList, String policyNo) {
		PolicyPremInfoDTO premDTO = null;
		for (int i = 0; policyPremList != null && i < policyPremList.size(); i++) {
			if (policyPremList.get(i).getPolicyNo().equals(policyNo)) {
				premDTO = policyPremList.get(i);
				break;
			}
		}
		return premDTO;
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
		if (serviceItemsInputDTO instanceof ServiceItemsInputDTO_23) {
			ServiceItemsInputDTO_23 item = (ServiceItemsInputDTO_23) serviceItemsInputDTO;
			List<PolicyPremInfoDTO> policyPremList = item.getPolicyPremList();
			for (int i = 0; policyPremList != null && i < policyPremList.size(); i++) {
				PolicyPremInfoDTO prem = policyPremList.get(i);
				if ("Y".equals(prem.getChecked())
						&& "1".equals(item.getChargingMethod())
						&& "1".equals(prem.getChargingMethod())) {
					err.reject("policyNo", prem.getPolicyNo() + "变更前后均为现金，无需选择");
				}
			}
		}
	}

	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {

		String chargingMethod = "";
		List<String> list = commonQueryDAO.queryPosAcceptDetailInfo(
				bom.getPosNo(), "1", "017");
		if (list != null && list.size() > 0) {
			chargingMethod = (String) list.get(0);
		}
		if ("2".equals(chargingMethod) && "06".equals(bom.getChannelType())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("policyNo", bom.getPolicyNo());
			queryForObject("QUERY_TSS_POLICY_PROJECT", map);

			if ("2".equals((String) map.get("flag"))) {
				throw new RuntimeException("调用query_tss_policy_project接口出错");
			} else {

				boolean depenProject = false;

				if (((String) map.get("projectType")).equals("1")) {
					depenProject = true;

				}

				boolean conProject = false;

				if (!((String) map.get("projectType")).equals("1")) {
					conProject = true;

				}

				bom.setDepenProject(depenProject);
				bom.setConProject(conProject);
				bom.setProjectCode((String) map.get("projectCode"));

				List<Map<String, Object>> accountList = commonQueryDAO
						.queryAccountByPolicyNo(bom.getPolicyNo());
				Map<String, Object> accMap = new HashMap<String, Object>();
				if (accountList.size() > 0) {

					accMap = (Map<String, Object>) accountList.get(0);
				}

				bom.setOriginalAccountCategory((String) accMap
						.get("ACCOUNT_NO_TYPE"));
				bom.setOriginalBankCategory((String) accMap
						.get("BANK_CATEGORY"));

				List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
				Map<String, String> item = new HashMap<String, String>();
				item.put("posObject", "3");
				item.put("itemNo", "095");
				posObjectAndItemNoList.add(item);
				List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
						bom.getPosNo(), posObjectAndItemNoList);
				if (detailList != null && !detailList.isEmpty()) {
					for (PosAcceptDetailDTO detail : detailList) {
						if ("3".equals(detail.getPosObject())
								&& "095".equals(detail.getItemNo())) {
							// 续期缴费账号类型
							bom.setAccountCategory(detail.getNewValue());
						}
					}
				}

				posObjectAndItemNoList = new ArrayList<Map<String, String>>();
				item = new HashMap<String, String>();
				item.put("posObject", "3");
				item.put("itemNo", "038");
				posObjectAndItemNoList.add(item);
				detailList = queryPosAcceptDetail(bom.getPosNo(),
						posObjectAndItemNoList);
				if (detailList != null && !detailList.isEmpty()) {
					for (PosAcceptDetailDTO detail : detailList) {
						if ("3".equals(detail.getPosObject())
								&& "038".equals(detail.getItemNo())) {
							// 续期缴费银行大类

							bom.setBankCategory(commonQueryDAO
									.getBankCategoryByBankCode(detail
											.getNewValue()));
						}
					}
				}

			}

		}
	}
}