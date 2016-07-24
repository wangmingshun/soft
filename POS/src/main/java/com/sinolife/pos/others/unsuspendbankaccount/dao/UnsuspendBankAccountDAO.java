package com.sinolife.pos.others.unsuspendbankaccount.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;

@SuppressWarnings("unchecked")
@Repository("unsuspendBankAccountDAO")
public class UnsuspendBankAccountDAO extends AbstractPosDAO {

	/**
	 * 根据保全号查询可以做转账暂停取消的保全列表
	 * @param posNo
	 * @return
	 */
	public List<PosInfoDTO> queryPosInfoByPosNoForUnsuspendBankAccount(String posNo) {
		return queryForList("queryPosInfoByPosNoForUnsuspendBankAccount", posNo);
	}
	
	/**
	 * 清空posInfo的转账成功状态
	 * @param posNo
	 * @return
	 */
	public boolean clearPosPremSuccessFlag(String posNo) {
		int effectRows = getSqlMapClientTemplate().update(sqlName("clearPosPremSuccessFlag"), posNo);
		return effectRows == 1;
	}
	
	/**
	 * 根据保全号查询可以做转账暂停取消的保全列表
	 * @param posNo
	 * @return
	 */
	public List<Map<String, Object>>  getPosInfoByPosNoForUnsuspendBankAccountData(String clientNo,String posNo,String policyNo) {
		Map<String,Object> map =new HashMap<String,Object>();
		map.put("posNo", posNo);
		map.put("policyNo", policyNo);
		map.put("clientNo", clientNo);
		return (List<Map<String, Object>>)queryForList("getPosInfoByPosNoForUnsuspendBankData", map);
	}
}
