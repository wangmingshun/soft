package com.sinolife.pos.common.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.ClientQualityDTO;
import com.sinolife.pos.common.dto.ClientTouchHistoryDTO;

@Repository("clientInfoDAO")
@SuppressWarnings({ "unchecked" })
public class ClientInfoDAO extends AbstractPosDAO {

	public ClientInformationDTO queryClientInfoByClientNo(String clientNo) {

		return (ClientInformationDTO) queryForObject("queryClientInfoByClientNo",
				clientNo);

	}

	/**
	 * 根据客户五项信息查询客户
	 * 
	 * @param clientName
	 * @param sexCode
	 * @param birthday
	 * @param idType
	 * @param idNo
	 * @return
	 */

	public List<ClientInformationDTO> selClientinfoPro(String clientName,
			String sexCode, Date birthday, String idType, String idNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_name", clientName);
		paraMap.put("p_sex_code", sexCode);
		paraMap.put("p_birthday", birthday);
		paraMap.put("p_id_type", idType);
		paraMap.put("p_idno", idNo);
		paraMap.put("p_message", "");
		getSqlMapClientTemplate().queryForObject(
				this.getClass().getName() + ".selClientinfoPro", paraMap);
		String flag = (String) paraMap.get("v_sign");
		String msg = (String) paraMap.get("v_message");
		if (!"Y".equals(flag))
			throw new RuntimeException(msg);

		List<ClientInformationDTO> retList = (List<ClientInformationDTO>) paraMap
				.get("v_cursor");
		return retList;
	}

	/**
	 * 根据客户号查询客户信息
	 * 
	 * @param clientNo
	 * @return List<ClientAddressDTO>
	 */
	public List<ClientInformationDTO> selClientinfoForClientno(String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_no", clientNo);
		getSqlMapClientTemplate().queryForObject(
				this.getClass().getName() + ".selClientinfoForClientno",
				paraMap);
		String flag = (String) paraMap.get("v_sign");
		String msg = (String) paraMap.get("v_message");
		if (!"Y".equals(flag))
			throw new RuntimeException(msg);

		List<ClientInformationDTO> retList = (List<ClientInformationDTO>) paraMap
				.get("t_cursor");
		return retList;
	}

	/**
	 * 根据客户号查询客户地址明细
	 * 
	 * @param clientNo
	 * @return List<ClientAddressDTO>
	 */
	public List<ClientAddressDTO> queryAddressByClientNo(String clientNo) {
		return getSqlMapClientTemplate().queryForList(
				getClass().getName() + ".queryAddressByClientNo", clientNo);
	}

	/**
	 * 根据客户号查询客户电话明细
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<ClientPhoneDTO> queryPhoneByClientNo(String clientNo) {
		return getSqlMapClientTemplate().queryForList(
				getClass().getName() + ".queryPhoneByClientNo", clientNo);
	}

	/**
	 * 根据客户号查询客户电邮明细
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<ClientEmailDTO> queryEmailByClientNo(String clientNo) {
		return getSqlMapClientTemplate().queryForList(
				getClass().getName() + ".queryEmailByClientNo", clientNo);
	}

	/**
	 * 查询客户接触历史记录
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<ClientTouchHistoryDTO> selClientTouchHistoryPro(String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		queryForObject("selClientTouchHistoryPro", paraMap);
		return (List<ClientTouchHistoryDTO>) paraMap.get("touchInfo");
	}

	/**
	 * 查询客户品质信息
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<ClientQualityDTO> selClientQualBehaPro(String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		queryForObject("selClientQualBehaPro", paraMap);
		return (List<ClientQualityDTO>) paraMap.get("qualityInfo");
	}

	/**
	 * 新增一条客户基本信息
	 * 
	 * @param clientInfoDTO
	 * @return
	 */
	public ClientInformationDTO insClientInformationPro(
			ClientInformationDTO clientInfoDTO) {
		queryForObject("insClientInformationPro", clientInfoDTO);
		List<ClientInformationDTO> list = clientInfoDTO.getClientsReturn();
		if ("Y".equals(clientInfoDTO.getFlag())) {
			clientInfoDTO.setClientNo(list.get(0).getClientNo());
		}
		return clientInfoDTO;
	}

	/**
	 * 新增客户电话信息
	 * 
	 * @param ClientPhoneDTO
	 * @return
	 */
	public ClientPhoneDTO insClientPhonePro(ClientPhoneDTO clientPhone) {
		queryForObject("insClientPhonePro", clientPhone);
		return clientPhone;
	}

	/**
	 * 新增客户地址信息
	 * 
	 * @param ClientAddressDTO
	 * @return
	 */
	public ClientAddressDTO insClientAddressPro(ClientAddressDTO clientAddress) {
		queryForObject("insClientAddressPro", clientAddress);
		return clientAddress;
	}

	/**
	 * 新增客户电邮信息
	 * 
	 * @param ClientEmailDTO
	 * @return
	 */
	public ClientEmailDTO insClientEmailPro(ClientEmailDTO clientEmail) {
		queryForObject("insClientEmailPro", clientEmail);
		return clientEmail;
	}

	/**
	 * 修改客户信息
	 * 
	 * @param clientInfo
	 * @return
	 */
	public ClientInformationDTO updateClientInformationPro(
			ClientInformationDTO clientInfo) {
		queryForObject("updateClientInformationPro", clientInfo);
		return clientInfo;
	}

	

	/**
	 * @param clientNo 客户号
	 * @param clientWill 是否愿意接收短信：Y/N
	 * @return
	 */
	public String getClientMobile(
			String clientNo,String clientWill ) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		paraMap.put("clientWill", clientNo);
		queryForObject("getClientMobile", paraMap);
		return paraMap.get("mobilePhone")!=null?(String)paraMap.get("mobilePhone"):"";
	}
	
	
	
	
	/** 成长红包操作了退保、契撤后，调用下面的接口通知微信端 
	 * @param posNo
	 * @return
	 */
	public String queryWebNeedNotify(String posNo) {
		return (String) this.queryForObject("queryWebNeedNotify",
				posNo);
	}

	/**
	 * 根据电话号码查询客户号
	 * @methodName: getClientNoByPhoneNo
	 * @param phoneNo
	 * @return
	 * @return list
	 * @author WangMingShun
	 * @date 2016-1-21
	 * @throws
	 */
	public List getClientNoByPhoneNo(String phoneNo) {
		return this.queryForList("getClientNoByPhoneNo", phoneNo);
	}
}
