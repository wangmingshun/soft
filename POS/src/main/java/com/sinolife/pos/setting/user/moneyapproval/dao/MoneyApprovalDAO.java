package com.sinolife.pos.setting.user.moneyapproval.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.setting.user.moneyapproval.dto.PosAmountDaysPrivsDTO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("moneyApprovalDAO")
public class MoneyApprovalDAO extends SqlMapClientDaoSupport{
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void moneyApprovalSet(PosAmountDaysPrivsDTO amountDaysDTO){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数

		List<String> channel = new ArrayList<String>();
		if("ALL".equals(amountDaysDTO.getAcceptChannel())){//全部渠道
			List<CodeTableItemDTO> list = (List<CodeTableItemDTO>)sqlClient.queryForList(PosCodeTableDAO.class.getName() + ".POS_ACCEPT_CHANNEL_CODE");
			for (int j = 0; list!=null && j < list.size(); j++) {
				channel.add(list.get(j).getCode());
			}
		}else{
			channel.add(amountDaysDTO.getAcceptChannel());
		}
		for (int j = 0; j < channel.size(); j++) {
			String[] serviceItems = amountDaysDTO.getServiceItems();
			amountDaysDTO.setAcceptChannel(channel.get(j));
			for (int i = 0; i < serviceItems.length; i++) {
				amountDaysDTO.setServiceItemOdd(serviceItems[i]);

				//先失效原数据
				sqlClient.update(MoneyApprovalDAO.class.getName()+".UPDATE_AMOUNT_DAYS_END_DATE", amountDaysDTO);

				c = sqlClient.update(MoneyApprovalDAO.class.getName()+".UPDATE_AMOUNT_DAYS_PRIVS", amountDaysDTO);
				if(c<1){
					sqlClient.insert(MoneyApprovalDAO.class.getName()+".INSERT_POS_AMOUNT_DAYS_PRIVS_SET", amountDaysDTO);
				}
			}			
		}
	}
	
	public Map querySet(String amountDaysGrade,String serviceItem, String acceptChannel){
		Map pMap = new HashMap();
		pMap.put("amountDaysGrade", amountDaysGrade);
		pMap.put("serviceItem", serviceItem);
		pMap.put("acceptChannel", acceptChannel);
		
		return (Map)getSqlMapClientTemplate().queryForObject(MoneyApprovalDAO.class.getName()+".QUERY_AMOUNT_DAYS_PRIVS_SET", pMap);
	}
	
}
