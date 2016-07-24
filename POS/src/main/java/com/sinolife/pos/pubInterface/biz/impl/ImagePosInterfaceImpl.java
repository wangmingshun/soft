package com.sinolife.pos.pubInterface.biz.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.pubInterface.biz.service.ImageEventReceive;
import com.sinolife.pos.receipt.dao.ReceiptManageDAO;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("ImageEventReceive")
public class ImagePosInterfaceImpl implements ImageEventReceive {

	@Autowired
	ReceiptManageDAO receiptDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private PosRulesDAO posRulesDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.pubInterface.biz.service.ImagePosInterface#
	 * receiveImageMessage(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	public String receiveImageMessage(String imageType, String imageMainIndex,
			String barCode, String isFailedImage, String scanUserId,
			String branchCode, java.util.Date scanTime)

	{
		String policyNo = commonQueryDAO.queryUWPolicyNo(imageMainIndex);
		String signFlag = posRulesDAO.policySignFlag(policyNo);
		// 对状态为待回销的进行操作
		if ("D".equals(signFlag)) {
			Map pMap = new HashMap();
			pMap.put("policyNo", policyNo);
			pMap.put("signFlag", "S");
			receiptDAO.pubProcUpPolContract(pMap);
			return "回执影像扫描后签收状态修改成功";
		}
		return "回执状态不为D";
	}

}
