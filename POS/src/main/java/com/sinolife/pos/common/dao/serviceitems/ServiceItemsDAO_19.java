package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 联系方式变更
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("serviceItemsDAO_19")
public class ServiceItemsDAO_19 extends ServiceItemsDAO {
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
		ServiceItemsInputDTO_19 itemDTO = (ServiceItemsInputDTO_19) serviceItemsInputDTO;
		String clientNo = itemDTO.getClientNo();

		Map map = (Map) queryForObject("QUERY_ITEMS_INPUT", clientNo);
		itemDTO.setClientNo((String) map.get("CLIENT_NO"));
		itemDTO.setClientName((String) map.get("CLIENT_NAME"));
		itemDTO.setAcceptChannel(commonQueryDAO.queryPolicyInfoByPolicyNo(
				itemDTO.getPolicyNo()).getChannelType());

		map = new HashMap<String, Object>();
		map.put("clientNo", itemDTO.getClientNo());
		map.put("policyNo", itemDTO.getPolicyNo());
		List<PolicyContactInfoDTO> policyList = queryForList(
				"QUERY_POLICYCONTACTLIST_INPUT", map);
		if (policyList != null && !policyList.isEmpty()) {
			for (PolicyContactInfoDTO policy : policyList) {
				if (!itemDTO.getPolicyNo().equals(policy.getPolicyNo())) {
					policy.setVerifyResult(acceptRuleCheck(itemDTO,
							policy.getPolicyNo()));
				}
			}
		}
		itemDTO.setPolicyContactList(policyList);
		itemDTO.setIsSendLetter("Y");
		
		// 查询已经加挂手机号
		/*WebPosService wps = PlatformContext.getEsbContext().getEsbService(
				WebPosService.class);
		String webMobilePhoneNo = wps.getUserMobileByClientNo(clientNo);
		ClientAddressDTO address = itemDTO.getAddress();
		if (address == null) {
			address = new ClientAddressDTO();
		}
		address.setWebMobilePhoneNo(webMobilePhoneNo);
		itemDTO.setAddress(address);*/
		
		return itemDTO;
	}

	/**
	 * 提供外围系统调用
	 * 
	 * @param serviceItemsInputDTO
	 * @return
	 */
	public ServiceItemsInputDTO_19 queryServiceItemsInfo(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		return (ServiceItemsInputDTO_19) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/**
	 * 提供给退信调用
	 * 
	 * @param serviceItemsInputDTO
	 * @return
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToReturnNote(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_19 itemDTO = (ServiceItemsInputDTO_19) serviceItemsInputDTO;
		String clientNo = itemDTO.getClientNo();

		Map map = (Map) queryForObject("QUERY_ITEMS_INPUT", clientNo);
		itemDTO.setClientNo((String) map.get("CLIENT_NO"));
		itemDTO.setClientName((String) map.get("CLIENT_NAME"));
		itemDTO.setAcceptChannel(commonQueryDAO.queryPolicyInfoByPolicyNo(
				itemDTO.getPolicyNo()).getChannelType());

		map = new HashMap<String, Object>();
		map.put("clientNo", itemDTO.getClientNo());
		map.put("policyNo", itemDTO.getPolicyNo());
		List<PolicyContactInfoDTO> policyList = queryForList(
				"QUERY_POLICYCONTACTLIST_INPUT", map);
		itemDTO.setPolicyContactList(policyList);
		itemDTO.setIsSendLetter("Y");
		// 查询已经加挂手机号

		WebPosService wps = PlatformContext.getEsbContext().getEsbService(
				WebPosService.class);
		String webMobilePhoneNo = wps.getUserMobileByClientNo(clientNo);
		ClientAddressDTO address = itemDTO.getAddress();
		if (address == null) {
			address = new ClientAddressDTO();
		}
		address.setWebMobilePhoneNo(webMobilePhoneNo);
		itemDTO.setAddress(address);
		return itemDTO;
	}
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_19 itemDTO = (ServiceItemsInputDTO_19) serviceItemsInputDTO;
		ServiceItemsInputDTO_19 originDTO = (ServiceItemsInputDTO_19) itemDTO
				.getOriginServiceItemsInputDTO();
		itemDTO.setGroupNo("" + beginGroupNo);

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		// 客户层的变更，若存在，要写入每个保单对应的批单信息里面去
		List<PosAcceptDetailDTO> clientDetail = new ArrayList<PosAcceptDetailDTO>();// getPubDetailList();
		clientDetail.addAll(phoneAcceptDetail(itemDTO));
		clientDetail.addAll(addressAcceptDetail(itemDTO));
		clientDetail.addAll(emailAcceptDetail(itemDTO));
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getClientNo(), "139", "", itemDTO
						.getIsSendLetter()));

		List<PolicyContactInfoDTO> policyContactList = itemDTO
				.getPolicyContactList();
		for (int i = 0; policyContactList != null
				&& i < policyContactList.size(); i++) {
			PolicyContactInfoDTO contactDTO = policyContactList.get(i);
			// 选中的肯定做过修改
			if ("Y".equals(contactDTO.getChecked())) {
				String policyNo = contactDTO.getPolicyNo();
				String posNo = insertPosInfoRecord(itemDTO, policyNo);

				PolicyContactInfoDTO bakContact = getContactByPolicyNO(
						originDTO.getPolicyContactList(), policyNo);
				// 保单联系电话有变化增加
				if (contactDTO.getPhoneSeq() != null)
					if (bakContact.getPhoneSeq() == null
							|| contactDTO.getPhoneSeq() != null
							&& !contactDTO.getPhoneSeq().equals(
									bakContact.getPhoneSeq())) {
						acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
								"1", policyNo, "008", bakContact.getPhoneSeq(),
								contactDTO.getPhoneSeq()));
					}
				// 移动
				if (contactDTO.getMobilePhoneSeq() != null
						&& !contactDTO.getMobilePhoneSeq().equals("")
						&& (bakContact.getMobilePhoneSeq() == null || !bakContact
								.getMobilePhoneSeq().equals(
										contactDTO.getMobilePhoneSeq()))) {

					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "143", bakContact
									.getMobilePhoneSeq(), contactDTO
									.getMobilePhoneSeq()));

				}
				// 家庭
				if (contactDTO.getHomePhoneSeq() != null
						&& !contactDTO.getHomePhoneSeq().equals("")
						&& (bakContact.getHomePhoneSeq() == null || !bakContact
								.getHomePhoneSeq().equals(
										contactDTO.getHomePhoneSeq()))) {

					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "144", bakContact.getHomePhoneSeq(),
							contactDTO.getHomePhoneSeq()));

				}
				// 办公
				if (contactDTO.getOfficePhoneSeq() != null
						&& !contactDTO.getOfficePhoneSeq().equals("")
						&& (bakContact.getOfficePhoneSeq() == null || !bakContact
								.getOfficePhoneSeq().equals(
										contactDTO.getOfficePhoneSeq()))) {

					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "145", bakContact
									.getOfficePhoneSeq(), contactDTO
									.getOfficePhoneSeq()));

				}
				// 有新增地址，保单联系地址就有变
				if (itemDTO.getAddress() != null
						&& "Y".equals(itemDTO.getAddress().getChecked())) {
					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "006", bakContact.getAddressSeq(),
							itemDTO.getAddress().getFakeSeq()));
				}
				// 有新增电邮，保单联系电邮就有变
				if (itemDTO.getEmail() != null
						&& "Y".equals(itemDTO.getEmail().getChecked())) {
					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "007", bakContact.getEmailSeq(),
							itemDTO.getEmail().getFakeSeq()));
				}
				// 保单短信服务有变
				if (contactDTO.getSmsService() != null
						&& !contactDTO.getSmsService().equals("")) {
					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "009", bakContact.getSmsService(),
							contactDTO.getSmsService()));
				}

				// 保单机构码有变
				if (contactDTO.getBranchCode() != null
						&& !same(contactDTO.getBranchCode(),
								bakContact.getBranchCode())) {
					acceptDetailList.add(new PosAcceptDetailDTO(posNo, "1",
							"1", policyNo, "010", bakContact.getBranchCode(),
							contactDTO.getBranchCode()));
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
	 * 根据policyNo在List<PolicyContactInfoDTO>找到一条记录
	 * 
	 * @param policyContactList
	 * @param policyNo
	 * @return
	 */
	private PolicyContactInfoDTO getContactByPolicyNO(
			List<PolicyContactInfoDTO> policyContactList, String policyNo) {
		PolicyContactInfoDTO contactDTO = null;
		for (int i = 0; policyContactList != null
				&& i < policyContactList.size(); i++) {
			if (policyContactList.get(i).getPolicyNo().equals(policyNo)) {
				contactDTO = policyContactList.get(i);
				break;
			}
		}
		return contactDTO;
	}

	/**
	 * 电话信息
	 * 
	 * @return
	 */
	private List<PosAcceptDetailDTO> phoneAcceptDetail(
			ServiceItemsInputDTO_19 itemDTO) {
		List<PosAcceptDetailDTO> list = new ArrayList<PosAcceptDetailDTO>();

		List<ClientPhoneDTO> phoneList = itemDTO.getPhoneList();
		// 业务逻辑只存在新增电话 多个
		for (int i = 0; phoneList != null && i < phoneList.size(); i++) {
			itemDTO.groupNoAddOne();
			ClientPhoneDTO phone = phoneList.get(i);
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "016", null, phone.getPhoneType(),
					itemDTO.getGroupNo()));
			//境外标志
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "098", null, phone.getForeignType(),
					itemDTO.getGroupNo()));
			if (!"".equals(phone.getAreaNo())) {
				list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
						itemDTO.getClientNo(), "017", null, phone.getAreaNo(),
						itemDTO.getGroupNo()));
			}
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "018", null, phone.getPhoneNo(),
					itemDTO.getGroupNo()));
			
			/* endit by wangmingshun start */
			//如果stopOriginalPhoneNo为Y,则将客户层原来对应电话类型的电话号码置为停用状态
			if(!"".equals(phone.getStopOriginalPhoneNo())) {
				list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2", "3",
						phone.getPhoneNo(), "272", null, phone.getStopOriginalPhoneNo(),
						itemDTO.getGroupNo()));
			}
			/* endit by wangmingshun end */
		}

		return list;
	}

	/**
	 * 地址信息
	 * 
	 * @return
	 */
	private List<PosAcceptDetailDTO> addressAcceptDetail(
			ServiceItemsInputDTO_19 itemDTO) {
		List<PosAcceptDetailDTO> list = new ArrayList<PosAcceptDetailDTO>();

		ClientAddressDTO address = itemDTO.getAddress();
		if ("Y".equals(address.getChecked())) {
			itemDTO.groupNoAddOne();
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "025", null, address
							.getProvinceCode(), itemDTO.getGroupNo()));
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "026", null, address.getCityCode(),
					itemDTO.getGroupNo()));
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "027", null, address.getAreaCode(),
					itemDTO.getGroupNo()));
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "028", null, address
							.getDetailAddress(), itemDTO.getGroupNo()));
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "029", null,
					address.getPostalcode(), itemDTO.getGroupNo()));
		}
		if ("Y".equals(address.getWebMobilePhoneChecked())) {
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "096", null, address
							.getWebMobilePhoneNo(), itemDTO.getGroupNo()));
		}
		return list;
	}

	/**
	 * 电邮信息
	 * 
	 * @return
	 */
	private List<PosAcceptDetailDTO> emailAcceptDetail(
			ServiceItemsInputDTO_19 itemDTO) {
		List<PosAcceptDetailDTO> list = new ArrayList<PosAcceptDetailDTO>();

		itemDTO.groupNoAddOne();
		ClientEmailDTO email = itemDTO.getEmail();
		if ("Y".equals(email.getChecked())) {
			list.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "3",
					itemDTO.getClientNo(), "034", null,
					email.getEmailAddress(), itemDTO.getGroupNo()));
		}
		return list;
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
		if (serviceItemsInputDTO instanceof ServiceItemsInputDTO_19) {
			ServiceItemsInputDTO_19 item = (ServiceItemsInputDTO_19) serviceItemsInputDTO;
			List<ClientPhoneDTO> phoneList = item.getPhoneList();
			ClientAddressDTO address = item.getAddress();
			Map pMap = new HashMap();
			Map phoneMap = new HashMap();
			for (int i = 0; phoneList != null && i < phoneList.size(); i++) {
				ClientPhoneDTO phone = phoneList.get(i);
				//境外电话不需要进行验证 DMP-2501
				if("T".equals(phone.getForeignType()))
				{
					continue;
				}
				pMap = new HashMap();
				if ("4".equals(phone.getPhoneType())) {
					pMap.put("checkType", "33");
					pMap.put("checkChar", phone.getPhoneNo());
				} else {

					phoneMap.put("checkChar", phone.getPhoneNo());
					queryForObject("PUB_CHECK_CLIENT_MOBILE", phoneMap);
					// 是手机号码
					if ("Y".equals(phoneMap.get("flag"))) {

						pMap.put("checkType", "33");
						pMap.put("checkChar", phone.getPhoneNo());
					} else {
						pMap.put("checkType", "3");
						pMap.put("checkChar",
								phone.getAreaNo() + "-" + phone.getPhoneNo());
					}
				}

				queryForObject("PUB_CHECK_CLIENT_CONTACT_INFO", pMap);

				if ("N".equals(pMap.get("flag"))) {
					err.reject("phoneNo",
							phone.getAreaNo() + " " + phone.getPhoneNo()
									+ "此电话号码格式不正确");
				}
			}
			// 如果加挂了手机号码则需要校验
			if ("Y".equals(address.getWebMobilePhoneChecked())) {
				Map webMobilePhoneMap = new HashMap();
				webMobilePhoneMap.put("checkType", "33");
				webMobilePhoneMap.put("checkChar",
						address.getWebMobilePhoneNo());
				queryForObject("PUB_CHECK_CLIENT_MOBILE", webMobilePhoneMap);
				if ("N".equals(webMobilePhoneMap.get("flag"))) {
					err.reject("phoneNo",
							"加挂的电话号码" + address.getWebMobilePhoneNo() + "不是手机号");
				}
				String isHave = "N";
				// 判断加挂的手机号码是否是新增的联系电话或者手机号码
				for (int i = 0; phoneList != null && i < phoneList.size(); i++) {
					ClientPhoneDTO phone = phoneList.get(i);
					if ("2".equals(phone.getPhoneType())
							|| "4".equals(phone.getPhoneType())) {
						if (address.getWebMobilePhoneNo().equals(
								phone.getPhoneNo())) {
							isHave = "Y";
						}
					}
				}
				String isClientPhone = commonQueryDAO.isclientphone(
						item.getClientNo(), address.getWebMobilePhoneNo());
				if ("Y".equals(isClientPhone)) {
					isHave = "Y";
				}
				if ("N".equals(isHave)) {
					err.reject("phoneNo",
							"加挂的电话号码" + address.getWebMobilePhoneNo()
									+ "不是客户的电话号码");
				}
				WebPosService wps = PlatformContext.getEsbContext()
						.getEsbService(WebPosService.class);
				Map checkMap = wps.checkClientMobile(item.getClientNo(),
						address.getWebMobilePhoneNo());
				String isCanAdd = (String) checkMap.get("flag");
				if ("N".equals(isCanAdd)) {
					String notAddMsg = (String) checkMap.get("message");
					err.reject("phoneNo", "不符合加挂规则，具体原因为" + notAddMsg);
				}
			}
		}
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
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		final String PHONE_TYPE = "PHONE_TYPE";
		final String AREA_NO = "AREA_NO";
		final String PHONE_NO = "PHONE_NO";
		// 电话类型
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "016");
		posObjectAndItemNoList.add(item);
		// 区号
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "017");
		posObjectAndItemNoList.add(item);
		// 电话号码
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "018");
		posObjectAndItemNoList.add(item);
		// 迁移机构
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "010");
		posObjectAndItemNoList.add(item);

		Map<String, Map<String, String>> phoneMap = new HashMap<String, Map<String, String>>();
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				Map<String, String> phoneRecord = null;
				String groupNo = detail.getInfoGroupNo();
				String posObject = detail.getPosObject();
				String itemNo = detail.getItemNo();
				String newValue = detail.getNewValue();
				if (phoneMap.containsKey(groupNo)) {
					phoneRecord = phoneMap.get(groupNo);
				} else {
					phoneRecord = new HashMap<String, String>();
					phoneMap.put(groupNo, phoneRecord);
				}
				if ("3".equals(posObject) && "016".equals(itemNo)) {
					phoneRecord.put(PHONE_TYPE, newValue);
				} else if ("3".equals(posObject) && "017".equals(itemNo)) {
					phoneRecord.put(AREA_NO, newValue);
				} else if ("3".equals(posObject) && "018".equals(itemNo)) {
					phoneRecord.put(PHONE_NO, newValue);
				} else if ("1".equals(posObject) && "010".equals(itemNo)) {
					// 保单迁移件
					bom.setPolicyTransfer(true);
				}
			}
		}
		Iterator<Entry<String, Map<String, String>>> it = phoneMap.entrySet()
				.iterator();
		List<String> phoneList = new ArrayList<String>();
		while (it.hasNext()) {
			Entry<String, Map<String, String>> entry = it.next();
			Map<String, String> phoneRecord = entry.getValue();
			if (phoneRecord.containsKey(AREA_NO)) {
				phoneList.add(phoneRecord.get(AREA_NO)
						+ phoneRecord.get(PHONE_NO));
			} else {
				phoneList.add(phoneRecord.get(PHONE_NO));
			}
		}
		String[] phoneArr = new String[phoneList.size()];
		for (int i = 0; i < phoneList.size(); i++) {
			phoneArr[i] = phoneList.get(i);
		}
		// 客户电话列表
		bom.setClientPhones(phoneArr);

		super.fillBOMForProcessRuleCheck(bom);
	}

	/**
	 * 根据保单号和投保人id查询客户的联系信息
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @return
	 */
	public List<PolicyContactInfoDTO> queryAppContactInfo(String clientNo,
			String policyNo) {

		Map map = new HashMap<String, Object>();
		map.put("clientNo", clientNo);
		map.put("policyNo", policyNo);
		return queryForList("QUERY_POLICYCONTACTLIST_INPUT", map);

	}
	
	public   Object  pubCheckClientMobile(Map phoneMap)
	{
      	return 	queryForObject("PUB_CHECK_CLIENT_MOBILE", phoneMap);
	
	
	}
	public   Object  pubCheckClientContactInfo(Map pMap)
	{
      	return 	queryForObject("PUB_CHECK_CLIENT_CONTACT_INFO", pMap);
	
	
	}
	
	
	

}
