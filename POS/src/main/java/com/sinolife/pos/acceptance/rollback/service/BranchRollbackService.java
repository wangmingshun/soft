package com.sinolife.pos.acceptance.rollback.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.acceptance.rollback.dto.RollBackInputDTO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({"rawtypes","unchecked"})
@Service("branchRollbackService")
public class BranchRollbackService {

	@Autowired
	private BranchRollbackDAO rollbackDAO;
	
	@Autowired
	private CommonAcceptDAO acceptDAO;

	@Autowired
	private CommonQueryDAO queryDAO;
	
	public Map queryByPosNo(String posNo){
		Map rstMap = rollbackDAO.rollBackAble(posNo);
		
		Map map = rollbackDAO.queryOriginalPosInfo(posNo);
		if(map!=null && map.size()>0){
			rstMap.putAll(map);	
		}

		return rstMap;
	}
	
	public Map process(String posNo){
		Map rstMap = rollbackDAO.process(posNo);//回退的试算接口
		rstMap.putAll(rollbackDAO.queryOriginalPosInfo(posNo));//原保全的信息
		
		return rstMap;
	}
	
	/**
	 * 提交，返回的是本次撤销产生的项目为40的posNo
	 * @param rollbackDTO
	 * @return
	 */
	public String submit(RollBackInputDTO rollbackDTO){
		PosApplyBatchDTO applyBatch = rollbackDTO.getApplyBatchDTO();
		PosApplyFilesDTO applyFiles = rollbackDTO.getApplyFilesDTO();
		PosInfoDTO posInfo = rollbackDTO.getPosInfoDTO();
		String acptStatus = posInfo.getAcceptStatusCode();//原状态，决定了它是被撤销还是被回退
		String rollbackPosNo = posInfo.getPosNo();//这里开始记录的是将被撤销的保全号，等"写保全信息表"执行后，就被覆盖成了新的保全号
		Date sysdate = queryDAO.getSystemDate();
		
		String user = PlatformContext.getCurrentUser();//当前用户
		
		applyBatch.setAcceptor(user);
		applyBatch.setAcceptChannelCode("1");
		applyBatch.setCounterNo(rollbackDAO.userCounterNo(user));
		acceptDAO.insertPosApplyBatch(applyBatch);//写批次表
		
		applyFiles.setPosBatchNo(applyBatch.getPosBatchNo());
		applyFiles.setFilePages(1l);
		applyFiles.setSignAccordFlag("N");
		applyFiles.setApplyDate(sysdate);
		applyFiles.setPaymentType(posInfo.getPaymentType());
		applyFiles.setTransferAccountno(posInfo.getAccountNo());
		applyFiles.setBankCode(posInfo.getBankCode());
		applyFiles.setTransferAccountOwner(posInfo.getPremName());
		applyFiles.setAccountNoType(posInfo.getAccountNoType());
		applyFiles.setForeignExchangeType(posInfo.getForeignExchangeType());
		applyFiles.setApprovalServiceType("1");
		acceptDAO.insertPosApplyFiles(applyFiles);//写申请书表
		
		posInfo.setAcceptor(user);
		posInfo.setServiceItems("40");
		posInfo.setAcceptDate(sysdate);
		posInfo.setBarcodeNo(applyFiles.getBarcodeNo());
		posInfo.setAcceptSeq(1);
		posInfo.setAcceptStatusCode("A03");
		posInfo.setRemark(applyFiles.getRemark());
		posInfo.setAccountNoType(posInfo.getAccountNoType());
		acceptDAO.insertPosInfo(posInfo);//写保全信息表
		
		PosAcceptDetailDTO acceptDetail = null;
		acceptDetail = new PosAcceptDetailDTO(posInfo.getPosNo(),"5","1",rollbackPosNo,"006",null,rollbackPosNo);
		acceptDAO.insertPosAcceptDetail(acceptDetail);//写保全受理明细表1
		acceptDetail = new PosAcceptDetailDTO(posInfo.getPosNo(),"5","3",rollbackPosNo,"012",null,rollbackDTO.getCauseCode());
		acceptDAO.insertPosAcceptDetail(acceptDetail);//写保全受理明细表2
		
		if("E01".equals(acptStatus)){//回退的才置个暂停
			acceptDAO.doPolicySuspend(posInfo.getPolicyNo(), "5", posInfo.getPosNo(), "40", sysdate);			
		}
		
		return posInfo.getPosNo();
		
	}
}
