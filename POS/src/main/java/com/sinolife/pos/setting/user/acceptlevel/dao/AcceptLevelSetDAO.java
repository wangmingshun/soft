package com.sinolife.pos.setting.user.acceptlevel.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({"rawtypes","unchecked"})
@Repository("acceptLevelSetDAO")
public class AcceptLevelSetDAO extends AbstractPosDAO{
	
	public List queryAcceptLevelInfo(String level){
		return queryForList("QUERY_GRADE_PRIVS_BY_LEVEL", level);
	}
	
	/**
	 * 更新等级设置表pos_input_grade_privs_set
	 * @param pMap
	 */
	public void setAcceptLevel(Map pMap){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数
		
		//先将原有的数据置为无效
		sqlClient.update(sqlName("UPDATE_INVALID_EXIST_GRADE_PRIVS"),(String)pMap.get("grade"));
		
		String[] items = ((String)pMap.get("items")).split(";");
		//重新写入全部项目
		for (int i = 0; i < items.length && items[i].length()>0; i++) {
			pMap.put("item", items[i]);
			c = sqlClient.update(sqlName("UPDATE_GRADE_PRIVS"), pMap);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_GRADE_PRIVS"), pMap);			
			}
		}
		
	}
	
	/**
	 * 将变化同步到每一个用户身上去
	 * 更新self_input_privs表
	 */
	public void setUserAccept(List<String> cancel,List<String> add, String grade){
		Map pMap = new HashMap();
		pMap.put("grade", grade);

		for (String item:cancel) {
			pMap.put("item", item);
			getSqlMapClientTemplate().update(sqlName("UPDATE_SELF_INPUT_END_DATE"), pMap);
		}
		
		for(String item:add){
			pMap.put("item", item);
			
			getSqlMapClientTemplate().update(sqlName("UPDATE_SELF_INPUT_VALID"), pMap);
			getSqlMapClientTemplate().update(sqlName("INSERT_SELF_INPUT_PRIVS"), pMap);
		}
		
	}

}
