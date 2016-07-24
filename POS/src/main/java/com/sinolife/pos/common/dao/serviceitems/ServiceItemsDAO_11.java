package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyDividendTransAccountDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_11;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 11 红利领取
 */
@SuppressWarnings("unchecked")
@Repository("serviceItemsDAO_11")
public class ServiceItemsDAO_11 extends ServiceItemsDAO {
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_11 itemDTO = (ServiceItemsInputDTO_11)serviceItemsInputDTO;
		itemDTO.setDividendList(queryForList("QUERY_POLICY_DIVIDEND", itemDTO.getPolicyNo()));
		itemDTO.countDividendBalSum();
		
		Map transMap=(Map) queryForObject("QUERY_TRANS_GRANT", itemDTO.getPolicyNo());
		PolicyDividendTransAccountDTO p=new PolicyDividendTransAccountDTO();
		List<PolicyDividendTransAccountDTO> transList=new ArrayList<PolicyDividendTransAccountDTO>();
		if(transMap!=null){			
			if(!"".equals(transMap.get("ACCOUNT_NO"))&&null!=transMap.get("ACCOUNT_NO")){	
				p.setAccount_no(transMap.get("ACCOUNT_NO").toString());			
			}
			else{
				p.setAccount_no("");
			}
			if(!"".equals(transMap.get("ACCOUNT_OWNER"))&&null!=transMap.get("ACCOUNT_OWNER")){
				p.setAccount_owner(transMap.get("ACCOUNT_OWNER").toString());
			}
			else{
				p.setAccount_owner("");
			}
			if(!"".equals(transMap.get("BANK_CODE"))&&null!=transMap.get("BANK_CODE")){
				p.setBank_code(transMap.get("BANK_CODE").toString());
			}
			else{
				p.setBank_code("");
			}
			p.setTransfer_grant_flag("N");
			p.setCheck(false);
		}
		else{			
			p.setAccount_no("");
			p.setAccount_owner("");
			p.setBank_code("");
			p.setTransfer_grant_flag("N");
			p.setCheck(false);
		}

     //   itemDTO.setTransAccountDTO(p);
		transList.add(p);
        String  clientNo=  (String) queryForObject("getApplicantByPolicyNo", itemDTO.getPolicyNo());
        Map  map=(Map) getSqlMapClientTemplate().queryForObject(getClass().getName() + ".QueryClientInfoByclientNo", clientNo);
        itemDTO.setClientName(map.get("CLIENT_NAME").toString());    
        itemDTO.setClientIdType(map.get("ID_TYPE").toString());
        itemDTO.setIdNo(map.get("IDNO").toString());
        itemDTO.setTransList(transList);
		return itemDTO;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_11 itemDTO = (ServiceItemsInputDTO_11)serviceItemsInputDTO;
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"1","1",itemDTO.getPolicyNo(),"146",null,itemDTO.getDrawType()));
		if ("1".equals(itemDTO.getDrawType())){
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"1","1",itemDTO.getPolicyNo(),"037",null,itemDTO.getDrawSum()));
		}else{
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"4","3",itemDTO.getPolicyNo(),"022",null,itemDTO.getTransList().get(0).getAccount_owner()));
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"4","3",itemDTO.getPolicyNo(),"023",null,itemDTO.getTransList().get(0).getBank_code()));
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"4","3",itemDTO.getPolicyNo(),"024",null,itemDTO.getTransList().get(0).getAccount_no()));
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"4","3",itemDTO.getPolicyNo(),"025",null,itemDTO.getTransList().get(0).getTransfer_grant_flag()));
		}	
		return acceptDetailList;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		super.fillBOMForProcessRuleCheck(bom);
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//红利领取选择方式
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "146");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "146".equals(detail.getItemNo())) {
					String drawType = detail.getNewValue();
					bom.setBonusPaymentOption(drawType);
				}
			}
		}
	}

}
