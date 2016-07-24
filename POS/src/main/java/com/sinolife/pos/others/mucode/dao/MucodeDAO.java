package com.sinolife.pos.others.mucode.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("mucodeDAO")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MucodeDAO extends AbstractPosDAO{

	/**检查银代联系人是否存在*/
	public Map  checkName(Map map){
		
		return (Map) queryForObject("QUERY_NAME", map);		
	
	}
	
	/**检查机构是否存在 */
	public Map checkBranchCode(Map map){
		
		return (Map) queryForObject("QUERY_BRANCH_CODE", map);	
	}
	/**插入银代联系人*/
	public int insertNameAndMobile(Map map){
		
		Map pMap = new HashMap();
				
	    int flag=0;
		
		pMap.put("checkChar", map.get("MOBILE_NO"));
		queryForObject("PUB_CHECK_CLIENT_MOBILE", pMap);
		//是手机号码
		if ("Y".equals(pMap.get("flag")))
		 {				
			
			getSqlMapClientTemplate().insert(sqlName("INSERT_NAME"), map);

		 }
		else
		{
			 flag=2;
		}
		
      return flag;
		
	}
	/**修改银代联系人*/
	public int updateNameAndMobile(Map map){		
	
	    Map pMap = new HashMap();		
		
	    int flag=0;
		pMap.put("checkChar", map.get("MOBILE_NO"));
	    queryForObject("PUB_CHECK_CLIENT_MOBILE", pMap);
	   
		//是手机号码
		if ("Y".equals(pMap.get("flag")))
		 {				
			
			  flag=getSqlMapClientTemplate().update(sqlName("UPDATE_NAME"), map);

		 }
		else
		{
			  flag=2;

		}
		
	 
	  return flag;
		
	}
}
