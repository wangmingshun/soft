package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.life.foundation.common.lang.StringUtils;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosAppointmentInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_45;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_5;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.sf.framework.excel.thirdparty.edu.npu.fastexcel.common.util.StringUtil;

@Repository("serviceItemsDAO_45")
public class ServiceItemsDAO_45 extends ServiceItemsDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_45 item = (ServiceItemsInputDTO_45) serviceItemsInputDTO;
		item.setPosAppointmentInfoList(
				queryForList("queryAppointmentInfo", item.getPolicyNo()));
		//查询投保人信息
		Map<String, Object> applicantInfo = 
			(Map<String, Object>) queryForObject("queryApplicantInfo", item.getClientNo());
		item.setAccountName((String)applicantInfo.get("clientName"));
		item.setIdNo((String)applicantInfo.get("idNo"));
		item.setIdType((String)applicantInfo.get("idType"));
		return item;
	}
	
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO_45 queryServiceItemsInfoExtra_45(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_45) queryServiceItemsInfoExtra(serviceItemsInputDTO);
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
		ServiceItemsInputDTO_45 items = (ServiceItemsInputDTO_45) serviceItemsInputDTO;
		ServiceItemsInputDTO_45 snapshot = (ServiceItemsInputDTO_45) items
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		//判断操作类型 0保全预约 1取消预约
		if("0".equals(items.getAppointmentType())) {
			//服务项目
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					"Y", "160", "", items.getAppointmentServiceItem()));
			//预约日期
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "2", 
					items.getPolicyNo(), "156", "", 
					PosUtils.formatDate(items.getAppointmentDate(), "yyyy-MM-dd")));
			//领款人
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "157", "", items.getAccountName()));
			//银行代码
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "158", "", items.getBankCode()));
			//银行账户
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "159", "", items.getAccountNo()));
			//银行卡类型
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "161", "", items.getAccountNoType()));
			//证件号码
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "162", "", items.getIdNo()));
			//证件类型
			acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
					items.getPolicyNo(), "163", "", items.getIdType()));
			
		} else if("1".equals(items.getAppointmentType())){
			//获得可取消的保全项目
			List<PosAppointmentInfoDTO> li = items.getPosAppointmentInfoList();
			for(PosAppointmentInfoDTO p : li) {
				//该值为true表示取消页面勾选了
				if(p.getIsCancle()) {
					//要取消的保全
					acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "1", "1", 
							"N", "160", "", p.getPosNo()));
				}
			}
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
		if(serviceItemsInputDTO instanceof ServiceItemsInputDTO_45) {
			ServiceItemsInputDTO_45 sii = (ServiceItemsInputDTO_45) serviceItemsInputDTO;
			PosValidateWrapper wrapper = new PosValidateWrapper(err);
			
			//保全预约才做如下校验
			if(sii.getAppointmentServiceItem() != null && 
					"0".equals(sii.getAppointmentType())) {
				Map<String, Object> pMap = new HashMap<String, Object>();
				pMap.put("serviceItem", sii.getAppointmentServiceItem());
				pMap.put("policyNo", sii.getPolicyNo());
				String serviceItemDesc = (String) queryForObject("checkServiceItemsIsExist", pMap);
				//保单已经预约该项目后，不可重复预约
				if(!StringUtils.isNullOrEmpty(serviceItemDesc)) {
					wrapper.addErrMsg("appointmentServiceItem", "该保单已预约(" 
							+ serviceItemDesc +"),不能再次预约");
					return;
				}
				//保单预约退保，仅支持理财一号险种(UBAN_BN1)
				if("2".equals(sii.getAppointmentServiceItem())) {
					String flag = (String) queryForObject("checkPolicyProductCode", sii.getPolicyNo());
					if("0".equals(flag)) {
						wrapper.addErrMsg("appointmentServiceItem", "保单预约退保，仅支持有效的理财一号产品");
					}
				}
				//预约还款，保单必须有效，并且有有效贷款记录的
				if("9".equals(sii.getAppointmentServiceItem())) {
					String flag = (String) queryForObject("checkLoanPayBack", sii.getPolicyNo());
					if("0".equals(flag)) {
						wrapper.addErrMsg("appointmentServiceItem", "保单预约还款，保单必须有效，并且有有效贷款记录");
					}
				}
			}
		}
	}
}
