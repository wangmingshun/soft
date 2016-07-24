package com.sinolife.pos.acceptance.branch.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;
import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_15;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_20;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_33;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_5;
import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.util.PosUtils;

@SuppressWarnings("rawtypes")
@Service("serviceItemsOwnService")
public class ServiceItemsOwnService {

	@Autowired
	ServiceItemsDAO_5 dao5;

	@Autowired
	ServiceItemsDAO_15 dao15;

	@Autowired
	ServiceItemsDAO_20 dao20;

	@Autowired
	ServiceItemsDAO_33 dao33;

	@Autowired
	ServiceItemsDAO_2 dao2;

	@Autowired
	ClientInfoDAO clientDAO;

	public Map queryPremInfoByPosNo(Map pMap) {
		return dao33.queryPremInfoByPosNo(pMap);
	}

	/**
	 * 查询一个职业代码对应的描述等信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map queryOccupationInfo(String occupationCode) {
		return dao15.queryOccupationInfo(occupationCode);
	}

	/**
	 * 查询客户信息
	 * 
	 * @param itemsInput
	 * @return
	 */
	public void queryClientInfoForAdd(QueryAndAddClientDTO qaa) {
		// 保单联系信息查询
		if (StringUtils.isNotBlank(qaa.getPolicyNo())) {
			qaa.setContactInfo(dao20.queryPolicyContactInfo(qaa.getPolicyNo()));
		} else {
			qaa.setContactInfo(null);
		}
		qaa.updateOriginContactInfo();

		// 如果输入了新客户号，则接着查客户信息
		if (!StringUtils.isBlank(qaa.getClientNo())) {
			List<ClientInformationDTO> list = clientDAO
					.selClientinfoForClientno(qaa.getClientNo());
			if (list != null && list.size() > 0) {
				ClientInformationDTO clientInfo = list.get(0);
				queryContactInfo(clientInfo);
				qaa.setClientInfo(clientInfo);
				qaa.setMessage(null);
			} else {
				qaa.setClientInfo(null);
				qaa.setMessage("客户信息不存在，请新增！");
			}
		} else {
			qaa.setClientInfo(null);
			qaa.setMessage(null);
		}
		qaa.updateOriginClientInfo();
	}

	/**
	 * 查询客户的联系信息电话地址等
	 * 
	 * @param itemDTO
	 */
	private void queryContactInfo(ClientInformationDTO clientInfo) {
		if (clientInfo != null) {
			String clientNo = clientInfo.getClientNo();
			clientInfo.setClientPhoneList(clientDAO
					.queryPhoneByClientNo(clientNo));
			clientInfo.setClientAddressList(clientDAO
					.queryAddressByClientNo(clientNo));
			clientInfo.setClientEmailList(clientDAO
					.queryEmailByClientNo(clientNo));
		}
	}

	/**
	 * 根据客户号查询客户信息
	 * 
	 * @param clientNo
	 * @param qaa
	 */
	public void queryClientInfoByClientNo(String clientNo,
			QueryAndAddClientDTO qaa) {

		ClientInformationDTO clientInfo = clientDAO
				.queryClientInfoByClientNo(clientNo);

		queryContactInfo(clientInfo);
		qaa.setClientNo(clientNo);
		qaa.setClientInfo(clientInfo);
		qaa.updateOriginClientInfo();

	}

	/**
	 * 根据5项信息查询客户信息 若客户存在还调用queryContactInfo方法查客户联系信息
	 * 
	 * @param itemsInput
	 * @return
	 */
	public void queryClientByFive(QueryAndAddClientDTO qaa) {
		ClientInformationDTO criteria = qaa.getQueryCriteria();
		List<ClientInformationDTO> list = clientDAO.selClientinfoPro(
				criteria.getClientName(), criteria.getSexCode(),
				criteria.getBirthday(), criteria.getIdTypeCode(),
				criteria.getIdNo());
		qaa.setClientNoList(new ArrayList<String>());
		if (list != null && list.size() == 1) {
			ClientInformationDTO clientInfo = list.get(0);
			String clientNo = clientInfo.getClientNo();
			queryContactInfo(clientInfo);
			qaa.setClientNo(clientNo);
			qaa.setClientInfo(clientInfo);
			qaa.setMessage("该客户信息已存在，客户号" + clientNo);
		} else if (list != null && list.size() > 1) {
			qaa.getClientNoList().add("--");
			for (int i = 0; i < list.size(); i++) {
				ClientInformationDTO clientInfo = list.get(i);
				String clientNo = clientInfo.getClientNo();
				queryContactInfo(clientInfo);
				qaa.setClientNo(clientNo);
				qaa.setClientInfo(clientInfo);
				qaa.setMessage("请选择客户号!");
				qaa.getClientNoList().add(clientNo);
			}

		}

		else {
			ClientInformationDTO clientInfo = (ClientInformationDTO) PosUtils
					.deepCopy(criteria);
			clientInfo.setIdTypeDesc(PosUtils.getDescByCodeFromCodeTable(
					CodeTableNames.ID_TYPE, clientInfo.getIdTypeCode()));
			qaa.setClientInfo(clientInfo);
			qaa.setClientNo(null);
			qaa.setMessage(new StringBuffer(" 姓名:")
					.append(criteria.getClientName())
					.append(" 出生日期:")
					.append(new SimpleDateFormat("yyyy-MM-dd").format(criteria
							.getBirthday())).append(" 性别:")
					.append("1".equals(criteria.getSexCode()) ? "男" : "女")
					.append(" 证件号:").append(criteria.getIdNo())
					.append(" 对应客户信息不存在，请调整查询条件或确认新增！").toString());
		}
		qaa.updateOriginClientInfo();
	}

	/**
	 * 插入新增的客户信息，包括基本信息，联系信息等
	 * 
	 * @param itemDTO
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertNewClientInfo(QueryAndAddClientDTO qaa) {
		ClientInformationDTO clientInfo = qaa.getClientInfo();
		clientDAO.insClientInformationPro(clientInfo);
		if ("Y".equals(clientInfo.getFlag())) {
			String clientNo = clientInfo.getClientNo();
			qaa.setClientNo(clientNo);
			qaa.getClientInfo().setClientNo(clientNo);
			qaa.setMessage("新增客户成功，客户号：" + clientNo);
			qaa.setNewClientAdded(true);

			// 更新保单联系信息的客户号
			qaa.updateClientNo();

			List<ClientPhoneDTO> phoneList = clientInfo.getClientPhoneList();
			for (int i = 0; phoneList != null && i < phoneList.size(); i++) {
				ClientPhoneDTO phone = phoneList.get(i);
				clientDAO.insClientPhonePro(phone);
				if (!"Y".equals(phone.getFlag())) {
					qaa.appendMessage("\n电话" + phoneList.get(i).getTypeAndNo()
							+ "新增失败" + phone.getMessage());
				}
			}
			List<ClientAddressDTO> addressList = clientInfo
					.getClientAddressList();
			for (int i = 0; addressList != null && i < addressList.size(); i++) {
				ClientAddressDTO address = addressList.get(i);
				clientDAO.insClientAddressPro(address);
				if (!"Y".equals(address.getFlag())) {
					qaa.appendMessage("\n地址"
							+ addressList.get(i).getDetailAddress() + "新增失败"
							+ address.getMessage());
				}
			}
			List<ClientEmailDTO> emailList = clientInfo.getClientEmailList();
			for (int i = 0; emailList != null && i < emailList.size(); i++) {
				ClientEmailDTO email = emailList.get(i);
				clientDAO.insClientEmailPro(email);
				if (!"Y".equals(email.getFlag())) {
					qaa.appendMessage("\n邮箱"
							+ emailList.get(i).getTypeAndAddress() + "新增失败"
							+ email.getMessage());
				}
			}

			// 重新查询一遍联系信息，以用于页面显示效果,同时也省了在插入联系信息返回时获取返回的Seq
			queryContactInfo(clientInfo);
			qaa.updateContactSeq();
			qaa.updateOriginClientInfo();
		} else {
			qaa.setMessage("新增客户失败:" + clientInfo.getMessage());
		}
	}

	/**
	 * 查询产品费率因子
	 * 
	 * @param productCode
	 * @param type
	 *            P-缴费期，C-保险期
	 * @param typeValue
	 *            类型的值，如，交几年，保障几岁等
	 * @return
	 */
	public List queryProductPremRate(String productCode, String type,
			String typeValue) {
		return dao5.queryProductPremRate(productCode, type, typeValue);
	}

	/**
	 * 转保健相关校验，并返回保费等信息
	 * 
	 * @param oldApplyBarCode
	 * @param newApplyBarCode
	 * @return
	 */
	public Map<String, Object> getTranslateApplyInfo(String oldApplyBarCode,
			String newApplyBarCode) {
		return dao2.getTranslateApplyInfo(oldApplyBarCode, newApplyBarCode);
	}

}
