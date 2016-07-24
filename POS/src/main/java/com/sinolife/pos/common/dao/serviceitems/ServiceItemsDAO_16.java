package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_16;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 16 补充告知
 */
@Repository("serviceItemsDAO_16")
public class ServiceItemsDAO_16 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("informObject", "1|011|1|3|policyNo");// 补充告知对象
		map.put("informMatter", "1|012|1|3|policyNo");// 补充告知事项
		map.put("startDate", "1|013|2|4|policyNo");// 补充告知事项起点时间
		map.put("matterDesc", "1|015|1|3|policyNo");// 补充告知事项描述
		map.put("relationship", "1|014|1|3|policyNo");// 补充告知投被保人关系
		map.put("relationshipDesc", "1|085|1|3|policyNo");// 补充告知投被保人其他关系描述

		PROC_PROPERTY_CONFIG_MAP = map;
	}

	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_16 item = (ServiceItemsInputDTO_16) serviceItemsInputDTO;

		PolicyDTO policyInfo = commonQueryDAO.queryPolicyInfoByPolicyNo(item
				.getPolicyNo());
		item.setPolicyInfo(policyInfo);

		// 补充告知已经有客户选择，注释掉这里，使用本身的客户选择项
		// //补充告知可选择变更客户对象
		// item.setClientSelectEnabled(true);

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
		ServiceItemsInputDTO_16 item = (ServiceItemsInputDTO_16) serviceItemsInputDTO;
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		// 更新客户号
			String clientNo = null;
//			if ("A".equals(item.getInformObject())) {
//				clientNo = item.getClientNoApp();
//			} else if ("I".equals(item.getInformObject())) {
//				clientNo = item.getClientNoIns();
//			} else {
//				throw new RuntimeException("无效的变更客户选项："
//						+ item.getInformObject());
//			}
			//家庭单多被保险人 传过来的item.getInformObject()是"I,被保人客户号"  update by  zhangyi.wb
			String [] aa = item.getInformObject().split(",");
			String informObject = aa[0];
			if ("A".equals(informObject)) {
				clientNo = item.getClientNoApp();
			} else if ("I".equals(informObject)) {
				clientNo = aa[1];
			} else {
				throw new RuntimeException("无效的变更客户选项："
						+ item.getInformObject());
			}
			acceptDAO.updatePosInfo(item.getPosNo(), "client_no", clientNo);


		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterA())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterA()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterB())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterB()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterC())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterC()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterD())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterD()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterE())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterE()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterF())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterF()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getInformMatterG())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "167", "", item.getInformMatterG()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getMatterBeforeDesc())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "168", "", item.getMatterBeforeDesc()));
		}
		if(PosUtils.isNotNullOrEmpty(item.getMatterAfterDesc())) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1",
					"1", item.getPolicyNo(), "169", "", item.getMatterAfterDesc()));
		}
		
		acceptDetailList.addAll(generator.getResult());
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
		if (serviceItemsInputDTO instanceof ServiceItemsInputDTO_16) {
			ServiceItemsInputDTO_16 dto = (ServiceItemsInputDTO_16) serviceItemsInputDTO;
			PosValidateWrapper wrapper = new PosValidateWrapper(err);
			if ("5".equals(dto.getInformMatter())
					&& "01".equals(dto.getRelationship())) {
				String policyNo = dto.getPolicyNo();
				String applicantNo = commonQueryDAO
						.getApplicantByPolicyNo(policyNo);
				String insuredNo = commonQueryDAO
						.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
				if (!applicantNo.equals(insuredNo)) {
					wrapper.addErrMsg("relationship", "投被保人不为同一人，不能变更投被保人关系为本人");
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForUnwrtRuleCheck
	 * (com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String,
	 * com.sinolife.pos.common.dto.PersonalNoticeDTO,
	 * com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo,
			PersonalNoticeDTO appPersonalNotice,
			PersonalNoticeDTO insPersonalNotice) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		// 补充告知对象
		item.put("posObject", "1");
		item.put("itemNo", "011");
		posObjectAndItemNoList.add(item);
		// 补充告知事项（原来的方案）
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "012");
		posObjectAndItemNoList.add(item);
		// 补充告知事项（新方案）
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "167");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo,
				posObjectAndItemNoList);
		//存放补充告知的事项数组,最终转换为array
		List<String> list = new ArrayList<String>();
		
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("1".equals(detail.getPosObject())
						&& "011".equals(detail.getItemNo())) {
					if ("A".equals(detail.getNewValue())) {
						bom.getOwner().setIsCurrentPosObj(true);
					} else if ("I".equals(detail.getNewValue())) {
						bom.getInsureds()[0].setIsCurrentPosObj(true);
					}
				} else if ("1".equals(detail.getPosObject())
						&& "012".equals(detail.getItemNo())) {
					String informedMessage = "";
					switch (Integer.parseInt(detail.getNewValue())) {
					case 1:
						informedMessage = "投保时未如实告知";
						break;
					case 2:
						informedMessage = "保全项目未如实告知";
						break;
					case 3:
						informedMessage = "健康状况改变";
						break;
					case 4:
						informedMessage = "变更或取消特别约定";
						break;
					case 5:
						informedMessage = "是被保险人的（关系）";

					bom.setInformedMessage(
							informedMessage);
					}
				}else if ("1".equals(detail.getPosObject())
						&& "167".equals(detail.getItemNo())) {
					//7其他投保前未如实告知情况 不写入,该项目部送核
					if(!"7".equals(detail.getNewValue())) {
						list.add(detail.getNewValue());
						
					}
				}

			}
			String[] s = list.toArray(new String[list.size()]);
			bom.getInsureds()[0].setExceptionDeclaration(s);
		}
	}
}
