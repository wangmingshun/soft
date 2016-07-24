package com.sinolife.pos.others.mucode.service;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.others.mucode.dao.MucodeDAO;
@SuppressWarnings("rawtypes")
@Service("mucodeService")
public class MucodeService {

	
	@Autowired
	MucodeDAO mucodeDAO;
	
	/**检查银代联系人是否存在*/
	public Map  checkName(Map map){
		return mucodeDAO.checkName(map);
	}
	
	/**插入银代联系人*/
	public int  insertNameAndMobile(Map map){
	return	mucodeDAO.insertNameAndMobile(map);
	}
	
	/**修改银代联系人*/
	public int updateNameAndMobile(Map map){
		
		return mucodeDAO.updateNameAndMobile(map);
	}
	/**检查机构是否存在 */
	public Map checkBranchCode(Map map){
		
		return mucodeDAO.checkBranchCode(map);
	}

}
