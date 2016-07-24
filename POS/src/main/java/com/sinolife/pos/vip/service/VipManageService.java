package com.sinolife.pos.vip.service;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.VipApplyDetailDto;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.vip.dao.VipManageDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.store.TempFileFactory;

@SuppressWarnings({ "rawtypes" })
@Service("vipManageService")
@Transactional(propagation = Propagation.REQUIRED)
public class VipManageService {
	
	@Autowired
	PosFileDAO PosFileDAO;

	@Autowired
	VipManageDAO vipDAO;
	
	private static TempFileFactory fileFactory = TempFileFactory.getInstance(VipManageService.class);
	

	/**
	 * 客户最后一次投保地点作为其VIP所属机构
	 * 
	 * @param clientNo
	 * @return
	 */
	public String clientApplyBranch(String clientNo) {
		return vipDAO.clientApplyBranch(clientNo);
	}

	public String judgeClientNoVip(String clientNo) {
		return vipDAO.judgeClientNoVip(clientNo);
	}

	/**
	 * 调用l_pos_vip_manage的接口 vip管理都是以pkg接口形式提供的
	 * 
	 * @param sql
	 * @param pMap
	 * @return
	 */
	public Map exeVipProcedure(String sql, Map pMap) {
		vipDAO.exeVipProcedure(sql, pMap);
		return pMap;
	}

	public List exeVipProcedureList(String sql, Map<String, Object> pMap) {

		return vipDAO.exeVipProcedureList(sql, pMap);

	}

	public void insertVipDetailInfo(String sql, Map<String, Object> pMap) {

		vipDAO.insertVipDetailInfo(sql, pMap);

	}

	public void updateVipDetailInfo(String sql, Map<String, Object> pMap) {

		vipDAO.updateVipDetailInfo(sql, pMap);

	}

	public List getDesc(String sql) {
		return vipDAO.getDesc(sql);
	}

	public String queryTypeGradePremsum(Map pMap) {
		return vipDAO.queryTypeGradePremsum(pMap);
	}

	public void typeGradePremsumSet(Map pMap) {
		if ("NUL".equals(vipDAO.queryTypeGradePremsum(pMap))) {
			vipDAO.insertTypeGradePremsum(pMap);

		} else {
			vipDAO.updateTypeGradePremsum(pMap);
		}
	}

	public Map queryVipProductInfo(Map pMap) {
		return vipDAO.queryVipProductInfo(pMap);
	}

	/*
	 * 当前客户的服务专员改变
	 */
	public void vipEmpChange(String clientNo,
			VipApplyDetailDto vipApplyDetailDto) {

		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("clientNo", clientNo);
		vipDAO.updatePosVipServiceInfo(pMap);
		pMap = new HashMap<String, Object>();
		pMap.put("clientNo", clientNo);
		pMap.put("empNo", vipApplyDetailDto.getEmpNo());
		pMap.put("empPhone", vipApplyDetailDto.getEmpPhone());
		pMap.put("empChangeReason", vipApplyDetailDto.getEmpChangeReason());

		vipDAO.insertVipDetailInfo("INSERT_POS_VIP_SERVICE_INFO", pMap);

	}

	/*
	 * 
	 * 当前客户的VIP卡号变动
	 */
	public void vipCardChange(String clientNo,
			VipApplyDetailDto vipApplyDetailDto) {

		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("clientNo", clientNo);
		vipDAO.updatePosVipCardInfo(pMap);

		pMap = new HashMap<String, Object>();
		pMap.put("clientNo", clientNo);
		pMap.put("cardNo", vipApplyDetailDto.getCardNo());
		pMap.put("applyDate", vipApplyDetailDto.getApplyDate());
		pMap.put("modifiedReason", vipApplyDetailDto.getModifiedReason());
		pMap.put("providedUser", vipApplyDetailDto.getProvidedUser());

		vipDAO.insertVipDetailInfo("INSERT_POS_VIP_CARD_INFO", pMap);

		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		Map<String, Object> vcMap = new HashMap<String, Object>();
	
		vcMap.put("p_voucher_flow", vipApplyDetailDto.getCardNo());
		vcMap.put("p_business_series", "L");
		vcMap.put("p_branch_code", "");
		vcMap.put("p_voucher_code", "");
		vcMap.put("p_related_businessno", clientNo);
		vcMap.put("p_confirm_date", DateUtil.stringToDate(vipApplyDetailDto.getApplyDate()));
		vcMap.put("p_confirm_userno", userInfo.getLoginUserID());

		exeVipProcedure("voucherflowConfirm", vcMap);
		
	}

	
	/**
	 * 插入pos_file_info表一条记录(vip尊荣服务)
	 * @param fileId
	 * @param fileName
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @param userId
	 */
	public String insertPosFileInfo(String fileId, String fileName, String fileType, String userId){
		if(StringUtils.isBlank(fileId)||StringUtils.isBlank(fileName)||StringUtils.isBlank(userId)){

		}else{
			PosFileDAO.insertPosFileInfo(fileId, fileName,fileType, userId);
		}
		
		return "Y";
	}
	/**
	 * 上传单个文件
	 * @param file    文件
	 * @param subPath 子路径（若需要）
	 * @return 文件在im系统的ID，唯一ID
	 */
	public String upload(MultipartFile file, String subPath){
		String fileId = null;
		
		byte[] content = null;
		SFFile tempFile = null;
		OutputStream os = null;
		try {
			if(file != null) {
				content = file.getBytes();
				tempFile = fileFactory.createTempFile();
				os = tempFile.openOutputStream();
				os.write(content);
				os.flush();
				os.close();
				
				if(StringUtils.isBlank(subPath)){
					subPath = "receipt";
				}
				
				SFFilePath filePath = new SFFilePath();
				filePath.setModule("pos");
				filePath.setModuleSubPath(subPath.split("\\/"));
				filePath.setStogeType(StogeType.WEEK);
				fileId = PlatformContext.getIMFileService().putFile(tempFile, filePath);
			}
		} catch (Exception e) {
			throw new RuntimeException("文件"+file.getOriginalFilename()+"上传异常：",e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch(Exception e) {}
			}
			if(tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
		}
		return fileId;
	}

	/*
	 * 
	 * vip客户享受尊荣服务
	 */
	public void vipSharedService(String clientNo,
			VipApplyDetailDto vipApplyDetailDto) {

		Map<String, Object> pMap = new HashMap<String, Object>();
		
		
		String fileId = upload(vipApplyDetailDto.getAttechmentFileName(), null);
		String originalFilename = vipApplyDetailDto.getAttechmentFileName().getOriginalFilename();
		insertPosFileInfo(fileId, originalFilename, "6", PlatformContext.getCurrentUser());
		
		pMap = new HashMap<String, Object>();
		pMap.put("clientNo", clientNo);
		pMap.put("serviceDate", vipApplyDetailDto.getServcieDate());
		pMap.put("vipSmallServiceItem",
				vipApplyDetailDto.getVipSmallServiceItem());
		pMap.put("serviceReason", vipApplyDetailDto.getServiceReason());
		if(!StringUtils.isBlank(originalFilename)) {
			pMap.put("fileId", fileId);
		}

		vipDAO.insertServiceInstanceInfo(pMap);

	}
	
	/**
	 * @methodName: getclientServicePlanInfo
	 * @Description: 获取客户的服务方案 
	 * @param map
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-6-1
	 * @throws
	 */
	public Map getclientServicePlanInfo(Map map) {
		return vipDAO.getclientServicePlanInfo(map);
	}
	
	/**
	 * @Description: 更新VIP服务情况表的服务信息状态
	 * @methodName: updateVipServiceInstanceInfo
	 * @param clientNo
	 * @param isValid
	 * @param seqNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-20
	 * @throws
	 */
	public String updateVipServiceInstanceInfo(String clientNo, String isValid, String seqNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		paraMap.put("isValid", isValid);
		paraMap.put("seqNo", seqNo);
		int count = vipDAO.updateVipServiceInstanceInfo(paraMap);
		if(count > 0) {
			return "Y";
		}
		return "N";
	}
	
	public Map<String,Object> queryPayFailPolicyInfoByClientNo(String clientNo) {
		return  vipDAO.queryPayFailPolicyInfoByClientNo(clientNo);
	}
}