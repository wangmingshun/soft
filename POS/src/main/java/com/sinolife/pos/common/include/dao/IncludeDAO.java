package com.sinolife.pos.common.include.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.StaffInfoDTO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("includeDAO")
public class IncludeDAO extends AbstractPosDAO {

	public List queryBranchTreeRoot(Map pMap){
		return queryForList("QUERY_BRANCH_ROOT", pMap);
	}
	
	public List queryBranchTree(Map pMap){
		return queryForList("QUERY_BRANCH_SONS", pMap);
	}
	
	public List queryUserPrivilege(String user){
		return queryForList("QUERY_USER_PRIVILEGE", user);
	}
	
	public List queryCityByProvince(String province){
		return queryForList("QUERY_CITY_BY_PROVINCE", province);
	}
	
	public List queryAreaByCity(String city){
		return queryForList("QUERY_AREA_BY_CITY", city);
	}
	
	public List queryBranchInProvinceCity(String province,String city,String channel){
		Map pMap = new HashMap();
		pMap.put("province", province);
		pMap.put("city", city);
		pMap.put("channel", channel);
		return queryForList("QUERY_BRANCH_IN_PROVINCE_CITY", pMap);
	}
	
	public List queryCounter(String branchCode){
		return queryForList("QUERY_COUNTER_INFO", branchCode);
	}
	
	/**
	 * 根据工号查询服务人员
	 * @param empNo
	 * @return
	 */
	public StaffInfoDTO queryStaffByEmpNo(String empNo) {
		return (StaffInfoDTO) getSqlMapClientTemplate().queryForObject(getClass().getName() + ".queryStaffByEmpNo", empNo);
	}
	
	/**
	 * 财务接口10：查询银行代码列表接口
	 * @param countryCode 国家代码（非空）
	 * @param provinceCode 省/直辖市代码（非空）
	 * @param cityCode 城市代码（非空）
	 * @param bankCategory 银行大类编码（非空）
	 * @return
	 */
	public Map<String, Object> queryBankList(String countryCode, String provinceCode, String cityCode, String bankCategory) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_country_code", countryCode);
		paraMap.put("p_province_code", provinceCode);
		paraMap.put("p_city_code", cityCode);
		paraMap.put("p_bank_category", bankCategory);
		queryForObject("queryBankList", paraMap);
		String flag = (String) paraMap.get("p_flag");
		if("1".equals(flag)) {
			ArrayTypeResultHandler handler = new ArrayTypeResultHandler() {
				@Override
				public Map handleResult(Map map) {
					ARRAY array = (ARRAY) map.get("p_array_bank_info_items");
					List<BankInfoDTO> result = new ArrayList<BankInfoDTO>();
					BankInfoDTO bi = null;// 根据返回数据的基本类型确定Dto中的数据类型
					try {
						Object[] obj = (Object[]) array.getArray();// mydata可以取出来
						STRUCT struct = null;
						for (int i = 0; i < obj.length; i++) {
							struct = (STRUCT) obj[i];
							Object[] tmp = (Object[]) struct.getAttributes();
							bi = new BankInfoDTO();
							bi.setBankCode((String) tmp[0]);
							bi.setDescription((String) tmp[1]);
							bi.setBankAbbr((String) tmp[2]);
							bi.setBankCategory((String) tmp[3]);
							bi.setCountryCode((String) tmp[4]);
							bi.setProvinceCode((String) tmp[5]);
							bi.setCityCode((String) tmp[6]);
							result.add(bi);
						}
						map.put("p_array_bank_info_items", result);
					} catch (SQLException e) {
						String errMsg = "ArrayTypeResultHandler error:" + e.getMessage();
						logger.error(errMsg, e);
						throw new RuntimeException(errMsg);
					}
					return map;
				}
			};
			handler.handleResult(paraMap);
		}
		return paraMap;
	}

	public List productsList(){
		return queryForList("SELECT_ALL_PRODUCTS", null);
	}
	
	/**
	 * 判断申请书条形码是否已经被使用（在pos_apply_files中存在记录）
	 * @param barcodeNo
	 * @return
	 */
	public boolean isBarcodeNoUsed(String barcodeNo) {
		return "Y".equals(queryForObject("isBarcodeNoUsed", barcodeNo));
	}
	
	/**
	 * 查询用户在um中设置的机构码
	 * @param userId
	 * @return
	 */
	public String userBranchCode(String userId){
		return (String)queryForObject("QUERY_UM_USER_BRANCH_CODE", userId);
	}
	
	/**
	 * branchCode是否是parentBranch的下级机构，包括是自己也返回true
	 * @param parentBranch
	 * @param branchCode
	 * @return
	 */
	public boolean isParentBranch(String parentBranch, String branchCode){
		Map pMap = new HashMap();
		pMap.put("parentBranch", parentBranch);
		pMap.put("branchCode", branchCode);
		return "Y".equals(queryForObject("QUERY_IS_PARENT_BRANCH", pMap));
	}

	/**
	 * 查询业务员是否在staff_info中存在，不判断任何状态及有效性
	 * @param empNo
	 * @return
	 */
	public boolean isStaffExists(String empNo) {
		return "Y".equals(queryForObject("isStaffExists", empNo));
	}
	
	/**
	 * 根据机构代码尝试查询省市，不保证能查到
	 * @param branchCode
	 * @return
	 */
	public Map<String, Object> queryPrivinceAndCityByBranchCode(String branchCode) {
		return (Map<String, Object>) queryForObject("queryPrivinceAndCityByBranchCode", branchCode);
	}
	
	public String checkUserBranchCode(String branchCode,String user) {
		Map pMap = new HashMap();
		pMap.put("branchCode", branchCode);
		pMap.put("user", user);
		return (String)queryForObject("QUERY_USER_BRANCH_CODE_CHECK", pMap);
	}
	
}
