package com.sinolife.pos.common.include.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.StaffInfoDTO;
import com.sinolife.pos.common.include.dao.IncludeDAO;

@SuppressWarnings({"rawtypes","unchecked"})
@Service("includeService")
public class IncludeService {

	@Autowired
	IncludeDAO dao;
	
	/******************
	 * 查询机构基表信息
	 * @param pMap
	 * @return
	 */
	public List queryBranchTree(Map pMap){
		if("Y".equals(pMap.get("root"))){
			return dao.queryBranchTreeRoot(pMap);
		}else{
			return dao.queryBranchTree(pMap);
		}
	}

	/******************
	 * 查询用户权限表信息
	 * @param pMap
	 * @return
	 */
	public List queryUserPrivilege(String user){
		return dao.queryUserPrivilege(user);
	}
	
	public List queryCityByProvince(String province){
		return dao.queryCityByProvince(province);
	}
	
	public List queryAreaByCity(String city){
		return dao.queryAreaByCity(city);
	}
	
	public List queryBranchInProvinceCity(String province,String city,String channel){
		return dao.queryBranchInProvinceCity(province, city,channel);
	}
	
	public List queryCounter(String branchCode){
		return dao.queryCounter(branchCode);
	}
	
	/**
	 * 根据工号查询服务人员
	 * @param empNo
	 * @return
	 */
	public StaffInfoDTO queryStaffByEmpNo(String empNo) {
		return dao.queryStaffByEmpNo(empNo);
	}
	
	/**
	 * 查询业务员是否在staff_info中存在，不判断任何状态及有效性
	 * @param empNo
	 * @return
	 */
	public boolean isStaffExists(String empNo) {
		return dao.isStaffExists(empNo);
	}
	
	/**
	 * 财务接口10：查询银行代码列表接口
	 * @param countryCode 国家代码（非空）
	 * @param provinceCode 省/直辖市代码（非空）
	 * @param cityCode 城市代码（非空）
	 * @param bankCategory 银行大类编码（非空）
	 * @return
	 */
	public List<BankInfoDTO> queryBankList(String countryCode, String provinceCode, String cityCode, String bankCategory) {
		Map<String, Object> retMap = dao.queryBankList(countryCode, provinceCode, cityCode, bankCategory);
		String flag = (String) retMap.get("p_flag");
		String msg = (String) retMap.get("p_mes");
		List<BankInfoDTO> bankList = (List<BankInfoDTO>) retMap.get("p_array_bank_info_items");
		if(!"1".equals(flag))
			throw new RuntimeException("查询银行信息失败：" + msg);
		return bankList;
	}
	
	/**
	 * 查询全部的产品信息，返回用于页面自动完成的json字符串
	 * @return
	 */
	public List productsList(){
		return dao.productsList();
	}
	
	/**
	 * 判断申请书条形码是否已经被使用（在pos_apply_files中存在记录）
	 * @param barcodeNo
	 * @return
	 */
	public Boolean isBarcodeNoUsed(String barcodeNo) {
		return dao.isBarcodeNoUsed(barcodeNo);
	}
	
	/**
	 * 查询用户在um中设置的机构码
	 * @param userId
	 * @return
	 */
	public String userBranchCode(String userId){
		return dao.userBranchCode(userId);
	}

	/**
	 * 根据机构代码尝试查询省市，不保证能查到
	 * @param branchCode
	 * @return
	 */
	public Map<String, Object> queryPrivinceAndCityByBranchCode(String branchCode) {
		return dao.queryPrivinceAndCityByBranchCode(branchCode);
	}
	
	public String checkUserBranchCode(String branchCode,String user) {
		return dao.checkUserBranchCode(branchCode, user);
	}
	
}
