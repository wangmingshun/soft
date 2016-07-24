package com.sinolife.pos.setting.user.privilege.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.common.dao.InterestRateDAO;
import com.sinolife.pos.common.include.dao.IncludeDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.setting.user.privilege.dao.UserPrivilegeDAO;
import com.sinolife.pos.setting.user.privilege.dto.UserPrivilegeInfoDTO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("userPrivilegeService")
public class UserPrivilegeService {

	@Autowired
	UserPrivilegeDAO userPrivDAO;

	@Autowired
	IncludeDAO includeDAO;
	
	@Autowired
	
	InterestRateDAO  interestRateDAO;
	
	
	/**
	 * 利率单位
	 * @return
	 */
	public List getInterestUnit()
	{
	return interestRateDAO.getInterestUnit();
	}
	
	
	
	public void addInterestRate(Map pMap)
	{
		interestRateDAO.addInterestRate(pMap);
	}
	
	/**
	 * @param user
	 * @return
	 */
	public Map queryUserPrivsInfo(String user){
		Map map = userPrivDAO.queryUserPrivsInfo(user);
		map.put("userPremPrivs", userPrivDAO.queryUserPremPrivs(user));
		map.put("userUMBranchCode", includeDAO.userBranchCode(user));
		return map;
	}
	/**查询用户受理权限
	 * @param user
	 * @return Y/N
	 */
	public String queryUserPosAcceptPrivs(String user){
		return userPrivDAO.queryUserPosAcceptPrivs(user);
	}
	
	/**
	 * 操作人员currentUserA是分公司用户,对inputUserB设置权限。的控制
	 * B新入司，则判断B必须是A同一个机构下的
	 * B已入司，则判断必须是下级
	 * @param currentUser
	 * @param inputUser
	 * @return true-不能继续设置,
	 */
	public boolean isCurrentUserUpper(String currentUser, String inputUser){
		Map map = userPrivDAO.queryUserPrivsInfo(inputUser);
		boolean b;
		if(currentUser.equals(inputUser)){
			b = false;
			
		}else if("Y".equals(map.get("userExist"))){
			b = "N".equals(userPrivDAO.upperInputCheck(inputUser, currentUser));
			
		}else{
			b = !includeDAO.isParentBranch(includeDAO.userBranchCode(currentUser), includeDAO.userBranchCode(inputUser));
		}
		return b;
	}
	
	public Map queryUserAmount(String amountDaysGrade, String channel){
		return userPrivDAO.queryUserAmount(amountDaysGrade, channel);
	}
	
	public List queryInputGradeItems(String inputGrade){
		return userPrivDAO.queryInputGradeItems(inputGrade);
	}
	
	/**
	 * 用户权限设置页面录入信息提交
	 * 分6种信息，共4张表
	 * @param userPrivsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void submit(UserPrivilegeInfoDTO userPrivsDTO){
		
		userPrivDAO.submitUserPrivs(userPrivsDTO);
		
		userPrivDAO.submitSelfInputPrivs(userPrivsDTO);
		
		userPrivDAO.submitUserPremDaysPrivs(userPrivsDTO);
		
		userPrivDAO.submitUserAprovSpecial(userPrivsDTO);
	}

	public void removeUpper(String userId){
		userPrivDAO.removeUpper(userId);
	}
	
	/**
	 * 录入上级用户的检查
	 * @param upperId
	 * @param userId
	 * @return 0--通过，1--不存在用户，2--已是下级
	 */
	public String upperInputCheck(String upperId, String userId){
		String flag = "0";
		if(upperId.equals(userId)){
			
		}else if("Y".equals(userPrivDAO.userPosExist(upperId))){
			if("Y".equals(userPrivDAO.upperInputCheck(upperId, userId))){
				flag = "2";
			}
		}else{
			flag = "1";
		}
		return flag;
	}
	
	/**
	 * 解析权限文件，返回权限数据相应的list
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public List parsePrivsFile(MultipartFile file) throws IOException, BiffException{
		List list = new ArrayList();
		if (!file.isEmpty()) {
			String filePath = PosUtils.getPosProperty("tmpFilePath") + System.currentTimeMillis() + file.getOriginalFilename();
			File tmpFile = new File(filePath);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			InputStream in = file.getInputStream();
			byte[] buf = new byte[1024];
			int length = in.read(buf);
			while (length != -1) {
				fos.write(buf, 0, length);
				length = in.read(buf);
			}
			fos.flush();
			fos.close();
			in.close();

			Workbook book = Workbook.getWorkbook(tmpFile);
			Sheet sheet = book.getSheet(0);
			for (int i = 0; i < sheet.getRows(); i++) {
				Cell[] cell = sheet.getRow(i);
				if(cell[0]!=null && StringUtils.isNotBlank(cell[0].getContents())){
					Map map = new HashMap();
					map.put("userId", cell[0].getContents());
					map.put("userName", cell[1].getContents());
					map.put("rankCode", cell[2].getContents());
					map.put("upperUser", cell[3].getContents());
					map.put("counter", cell[4].getContents());
					map.put("inputLevel", cell[5].getContents());
					map.put("approvalLevel", cell[6].getContents());
					map.put("diffPlace", cell[7].getContents());
					map.put("ruleSpec", cell[8].getContents());
					map.put("inquiryPrivs", cell[9].getContents());
					
					list.add(map);
				}
			}
			book.close();
			
			tmpFile.delete();//临时文件，删除
		}
		
		return list;
	}
	
	/**
	 * 写入权限数据，逐单
	 * @param map
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int insertSinglePrivsData(Map map){
		
		userPrivDAO.batchInsertPriv(map);
		
		return 0;
	}
	
}
