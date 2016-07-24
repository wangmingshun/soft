package com.sinolife.pos.setting.user.privilege.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.setting.user.moneyapproval.dto.PosAmountDaysPrivsDTO;
import com.sinolife.pos.setting.user.privilege.dto.UserPrivilegeInfoDTO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("userPrivDAO")
public class UserPrivilegeDAO extends AbstractPosDAO{

	public Map queryUserPrivsInfo(String user){
		Map rstMap = new HashMap();
		rstMap.put("userExist", "N");
		Map userInfo = (Map)queryForObject("QUERY_USER_DETAIL_INFO", user);
		if(userInfo!=null){
			rstMap.put("userExist", "Y");
			//用户信息
			rstMap.put("userInfo", userInfo);
			
			//用户上级信息
			rstMap.put("upperInfo", queryForObject("QUERY_USER_DETAIL_INFO", userInfo.get("UPPER_USER_ID")));
			
			//用户个人的录入权限
			rstMap.put("inputItems", queryForList("QUERY_SELF_INPUT_PRIVS", user));
			
			//用户特殊件权限
			rstMap.put("specialPrivs", queryForList("QUERY_SELF_SPECIAL_PRIVS", user));
			
			//用户审批功能特殊件权限
			rstMap.put("aprovFuncSpecs", queryForList("QUERY_APROV_FUNC_SPECIAL_PRIVS", user));
			
			//用户审批规则特殊件权限
			rstMap.put("aprovRulePrivs", queryAprovPrivs(user, "1"));
			
			//用户审批撤销回退的权限
			rstMap.put("aprovRollbackPrivs", queryAprovPrivs(user, "3"));
		}
		return rstMap;
	}
	
	/**
	 * 查询用户单独设置的金额权限
	 * @param user
	 * @return
	 */
	public Map queryUserPremPrivs(String user){
		return (Map)queryForObject("QUERY_USER_PREM_PRIVS", user);
	}
	/**
	 * 查询用户受理权限
	 * @param user
	 * @return
	 */
	public String queryUserPosAcceptPrivs(String user){
		return (String)queryForObject("QUERY_USER_POS_ACCEPT_PRIVS", user);
	}
	/**
	 * 查询用户能审批规则特殊件或撤销回退不
	 * @param user
	 * @param type 1-规则特殊件，3-撤销回退
	 * @return
	 */
	private String queryAprovPrivs(String user,String type){
		Map pMap = new HashMap();
		pMap.put("user", user);
		pMap.put("type", type);
		return (String)queryForObject("QUERY_APROV_SPECIAL_PRIVS", pMap);		
	}
	
	public Map queryUserAmount(String amountDaysGrade, String channel){
		Map pMap = new HashMap();
		pMap.put("amountDaysGrade", amountDaysGrade);
		pMap.put("channel", channel);

		Map rstMap = (Map)queryForObject("QUERY_AMOUNT_CHANNEL_MONEY", pMap);
		rstMap.put("items", queryForList("QUERY_AMOUNT_CHANNEL_ITEMS", pMap));
		
		return rstMap;
	}
	
	/**
	 * 查询录入等级能操作的项目和特殊件
	 * @param rank
	 * @return
	 */
	public List queryInputGradeItems(String inputGrade){
		return queryForList("QUERY_SPECIAL_ITEMS_INPUT_GRADE", inputGrade);
	}
	
	/**
	 * 查询职级能审批的特殊件
	 * @param rank
	 * @return
	 */
	public List queryRankAprov_(String rank){
		return queryForList("QUERY_RANK_APROV_SPECIAL", rank);
	}
	
	/**
	 * 写入用户信息表
	 * @param userPrivsDTO
	 */
	public void submitUserPrivs(UserPrivilegeInfoDTO userPrivsDTO){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		
		//先判断已存在不
		Map map = (Map)queryForObject("QUERY_USER_DETAIL_INFO", userPrivsDTO.getUserId());
		if(map==null){
			sqlClient.insert(sqlName("INSERT_POS_USER_PRIVS"), userPrivsDTO);			
		}else{
			sqlClient.update(sqlName("UPDATE_POS_USER_PRIVS"), userPrivsDTO);
			
			//如果审批等级变化了，失效原有的审批金额权限数据
			if(!userPrivsDTO.getAmountDaysGrade().equals(map.get("AMOUNT_DAYS_GRADE"))){
				sqlClient.update(sqlName("UPDATE_USER_PREM_PRIVS_END"), userPrivsDTO.getUserId());
			}
		}
	}

	/**
	 * 写入用户录入权限表
	 * @param userPrivsDTO
	 */
	public void submitSelfInputPrivs(UserPrivilegeInfoDTO userPrivsDTO){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数
		//先将原有数据置失效
		sqlClient.update(sqlName("SET_SELF_INPUT_PRIVS_END_DATE"), userPrivsDTO.getUserId());//录入权限表
		sqlClient.update(sqlName("SET_SELF_SPECIAL_PRIVS_END_DATE"), userPrivsDTO.getUserId());//特殊件权限表
		
		//循环插入
		String[] serviceItems = userPrivsDTO.getServiceItemArr();
		for (int i = 0; serviceItems!=null && i < serviceItems.length; i++) {
			if(serviceItems[i].startsWith("S_")){
				serviceItems[i] = serviceItems[i].substring(serviceItems[i].indexOf("_")+1, serviceItems[i].indexOf("_", 2));
				userPrivsDTO.setServiceItem(serviceItems[i]);
				c = sqlClient.update(sqlName("UPDATE_SELF_SPECIAL_PRIVS"), userPrivsDTO);
				if(c<1){
					sqlClient.insert(sqlName("INSERT_SELF_SPECIAL_PRIVS"), userPrivsDTO);//特殊件权限表
				}
			}else{
				userPrivsDTO.setServiceItem(serviceItems[i]);
				c = sqlClient.update(sqlName("UPDATE_SELF_INPUT_PRIVS"), userPrivsDTO);
				if(c<1){
					sqlClient.insert(sqlName("INSERT_SELF_INPUT_PRIVS"), userPrivsDTO);//录入权限表
				}
			}
		}
	}
	
	/**
	 * 写入用户金额天数权限限制表
	 * @param userPrivsDTO
	 */
	public void submitUserPremDaysPrivs(UserPrivilegeInfoDTO userPrivsDTO){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数
		
		PosAmountDaysPrivsDTO amountDaysPrivsDTO = userPrivsDTO.getAmountDaysPrivsDTO();
		String[] serviceItems = amountDaysPrivsDTO.getServiceItems();
		for (int i = 0; serviceItems!=null && i < serviceItems.length; i++) {
			userPrivsDTO.getAmountDaysPrivsDTO().setServiceItemOdd(serviceItems[i]);
			getGradeValue(userPrivsDTO);
			
			//失效原数据
			sqlClient.update(sqlName("UPDATE_USER_PREM_DAYS_PRIVS_END_DATE"), userPrivsDTO);
			
			c = sqlClient.update(sqlName("UPDATE_POS_USER_PREM_DAYS_PRIVS"), userPrivsDTO);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_POS_USER_PREM_DAYS_PRIVS"), userPrivsDTO);
			}
		}
		
	}
	
	/**
	 * 如果用户某一项金额的值没输入，取原值
	 * @param userPrivsDTO
	 */
	private void getGradeValue(UserPrivilegeInfoDTO userPrivsDTO){
		Map map = null;
		PosAmountDaysPrivsDTO amountDays = userPrivsDTO.getAmountDaysPrivsDTO();
		if(StringUtils.isBlank(amountDays.getSourceTransferSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setSourceTransferSum(String.valueOf(map.get("SOURCE_TRANSFER_SUM")));
		}
		if(StringUtils.isBlank(amountDays.getNotsourceTransferSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setNotsourceTransferSum(String.valueOf(map.get("NOTSOURCE_TRANSFER_SUM")));
		}
		if(StringUtils.isBlank(amountDays.getNotselfCashSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setNotselfCashSum(String.valueOf(map.get("NOTSELF_CASH_SUM")));
		}
		if(StringUtils.isBlank(amountDays.getSelfCashSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setSelfCashSum(String.valueOf(map.get("SELF_CASH_SUM")));
		}
		if(StringUtils.isBlank(amountDays.getTreatyExceedSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setTreatyExceedSum(String.valueOf(map.get("TREATY_EXCEED_SUM")));
		}
		if(StringUtils.isBlank(amountDays.getInterestFreeSum())){
			if(map==null){
				map = (Map)queryForObject("QUERY_AMOUNT_DAYS_GRADE_VALUE", userPrivsDTO);
			}
			amountDays.setInterestFreeSum(String.valueOf(map.get("INTEREST_FREE_SUM")));
		}
	}

	/**
	 * 写入用户审批特殊件权限数据
	 * @param userPrivsDTO
	 */
	public void submitUserAprovSpecial(UserPrivilegeInfoDTO userPrivs){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数
		
		//先将原有数据置失效
		sqlClient.update(sqlName("UPDATE_USER_APROV_PRIVS_END_DATE"),userPrivs.getUserId());
		
		userPrivs.setAprovFuncSpecial("1");//搞个缺省值防止下面两条写的时候报错
		//规则特殊件
		if("Y".equals(userPrivs.getAprovRulePrivs())){
			userPrivs.setSpecType("1");
			c = sqlClient.update(sqlName("UPDATE_USER_APROV_SPECIAL"), userPrivs);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_USER_APROV_SPECIAL"), userPrivs);
			}
		}
		//撤销回退
		if("Y".equals(userPrivs.getAprovRollbackPrivs())){
			userPrivs.setSpecType("3");
			c = sqlClient.update(sqlName("UPDATE_USER_APROV_SPECIAL"), userPrivs);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_USER_APROV_SPECIAL"), userPrivs);
			}
		}
		
		//功能特特殊件
		String[] aprovFuncSpecs = userPrivs.getAprovFuncSpecialArr();
		userPrivs.setSpecType("2");
		for (int i = 0; aprovFuncSpecs!=null && i < aprovFuncSpecs.length; i++) {
			userPrivs.setAprovFuncSpecial(aprovFuncSpecs[i]);
			c = sqlClient.update(sqlName("UPDATE_USER_APROV_SPECIAL"), userPrivs);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_USER_APROV_SPECIAL"), userPrivs);
			}
		}
	}
	
	public void removeUpper(String userId){
		getSqlMapClientTemplate().update(sqlName("REMOVE_UPPER_POS_USER_PRIVS"), userId);
	}
	
	/**
	 * 输入的上级是否本是下级，不包含自己
	 * @param upperId
	 * @param userId
	 * @return Y--前者是后者下级（直接或间接）  N--不是
	 */
	public String upperInputCheck(String upperId, String userId){
		Map pMap = new HashMap();
		pMap.put("upperId", upperId);
		pMap.put("userId", userId);
		
		return (String)queryForObject("QUERY_INPUT_UPPER_ID_CHECK", pMap);
	}
	
	/**
	 * 输入用户是否在pos_user_privs表中已存在
	 * @param userId
	 * @return Y N
	 */
	public String userPosExist(String userId){
		return (String)queryForObject("QUERY_USER_POS_EXISTS", userId);
	}
	
	/**
	 * 批次导入时，某一个用户的权限数据写入
	 * @param map
	 */
	public void batchInsertPriv(Map map){
		getSqlMapClientTemplate().insert(sqlName("BATCH_INSERT_POS_USER_PRIVS"), map);
		getSqlMapClientTemplate().insert(sqlName("BATCH_INSERT_SELF_INPUT_PRIVS"), map.get("userId"));
		getSqlMapClientTemplate().insert(sqlName("BATCH_INSERT_SELF_SPECIAL_PRIVS"), map.get("userId"));
		if("Y".equals(map.get("ruleSpec"))){
			getSqlMapClientTemplate().insert(sqlName("BATCH_INSERT_USER_APROV_SPECIAL"), map.get("userId"));
		}
	}
	
}
