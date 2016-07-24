package com.sinolife.pos.common.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dto.CodeTableItemDTO;

@Repository
@SuppressWarnings("unchecked")
public class PosCodeTableDAO extends AbstractPosDAO {

	public List<CodeTableItemDTO> queryCodeTable(String codeTableName) {
		//return (List<CodeTableItemDTO>)getSqlMapClientTemplate().queryForList(PosCodeTableDAO.class.getName() + "." + codeTableName);
		
		List<CodeTableItemDTO> list=getSqlMapClientTemplate().queryForList(PosCodeTableDAO.class.getName() + "." + codeTableName);
		return list;
	}
	
}
