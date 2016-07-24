package com.sinolife.pos.common.dao.serviceitems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 21 客户资料变更
 */
@Repository("serviceItemsDAO_21")
public class ServiceItemsDAO_21 extends ServiceItemsDAO {

	public static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("clientName", "3|001|1|3|clientNo");
		map.put("idTypeCode", "3|002|1|3|clientNo");
		map.put("idNo", "3|003|1|3|clientNo");
		map.put("countryCode", "3|006|1|3|clientNo");
		map.put("nationCode", "3|007|1|3|clientNo");
		map.put("registerPlace", "3|008|1|3|clientNo");
		map.put("marriageCode", "3|010|1|3|clientNo");
		map.put("workUnit", "3|011|1|3|clientNo");
		map.put("position", "3|012|1|3|clientNo");

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
	public ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		if (!(serviceItemsInputDTO instanceof ServiceItemsInputDTO_21))
			throw new RuntimeException("错误的传入参数类型："
					+ serviceItemsInputDTO.getClass().getName() + ", 预期为:"
					+ ServiceItemsInputDTO_21.class.getName());

		ServiceItemsInputDTO_21 item = (ServiceItemsInputDTO_21) serviceItemsInputDTO;

		// 新的客户信息仅用于录入绑定，全部为空值
		ClientInformationDTO clientInfo = new ClientInformationDTO();
		clientInfo.setClientNo(item.getClientNo());
		item.setClientInformationDTO(clientInfo);

		// 旧的客户信息用于页面校验身份证与生日及性别的匹配性
		List<ClientInformationDTO> clientList = clientInfoDAO
				.selClientinfoForClientno(item.getClientNo());
		if (PosUtils.isNullOrEmpty(clientList)) {
			throw new RuntimeException("找不到客户信息：" + item.getClientNo());
		}
		item.setOriginClientInformationDTO(clientList.get(0));

		// 客户资料变更可选择变更客户对象
		item.setClientSelectEnabled(true);

		return item;
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
		ServiceItemsInputDTO_21 item = (ServiceItemsInputDTO_21) serviceItemsInputDTO;
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, serviceItemsInputDTO.getPosNo(),
				beginGroupNo);
		generator.processSimpleDTO(item.getClientInformationDTO());
		acceptDetailList.addAll(generator.getResult());
		if (item.getClientInformationDTO().getIdnoValidityDate() != null) {
			acceptDetailList
					.add(new PosAcceptDetailDTO(
							item.getPosNo(),
							"3",
							"1",
							item.getClientInformationDTO().getClientNo(),
							"101",
							item.getOriginClientInformationDTO()
									.getIdnoValidityDate() == null ? ""
									: new SimpleDateFormat("yyyy-MM-dd")
											.format(item
													.getOriginClientInformationDTO()
													.getIdnoValidityDate()),
							new SimpleDateFormat("yyyy-MM-dd").format(item
									.getClientInformationDTO()
									.getIdnoValidityDate())));
		} else if (item.getClientInformationDTO().getIsLongidnoValidityDate())// 选择长期有效
		{

			acceptDetailList
					.add(new PosAcceptDetailDTO(
							item.getPosNo(),
							"3",
							"1",
							item.getClientInformationDTO().getClientNo(),
							"101",
							item.getOriginClientInformationDTO()
									.getIdnoValidityDate() == null ? ""
									: new SimpleDateFormat("yyyy-MM-dd")
											.format(item
													.getOriginClientInformationDTO()
													.getIdnoValidityDate()),
							"9999-01-01"));
		}

		return acceptDetailList;
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
	}

	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		// 新投保人号码
		item.put("posObject", "3");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
//		// 新投保人证件号
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "003");
		posObjectAndItemNoList.add(item);
		

		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			
			String clientName = null;
			String idNo = null;
			
			for (PosAcceptDetailDTO detail : detailList) {
				
				if ("3".equals(detail.getPosObject())
						&& "001".equals(detail.getItemNo())) {
					clientName = detail.getNewValue();
				}
				
				if("3".equals(detail.getPosObject())
						&& "003".equals(detail.getItemNo())) {
					idNo = detail.getNewValue();
				}
			}
			
			//只有客户姓名做了变更才处理
			if(PosUtils.isNotNullOrEmpty(clientName)) {
				
				//客户证件号没有做变更,则使用客户信息的证件号
				if(PosUtils.isNullOrEmpty(idNo)) {
					//根据客户号查询客户信息
					Map<String, Object> clientInfo = 
						commonQueryDAO.QueryClientInfoByclientNo(
								bom.getApplicant().getId());
					idNo = (String) clientInfo.get("IDNO");
				}
				
				//变更项是否在黑名单中
				bom.setChangenameInBlacklist(
						posRulesDAO.isChangenameInBlacklist(clientName,idNo));
			}
			
		}
	}
}
