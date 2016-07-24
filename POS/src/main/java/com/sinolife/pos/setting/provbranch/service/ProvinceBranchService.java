package com.sinolife.pos.setting.provbranch.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.setting.provbranch.dao.ProvinceBranchDAO;

@SuppressWarnings("rawtypes")
@Service("provinceBranchService")
public class ProvinceBranchService {

	@Autowired
	ProvinceBranchDAO provDAO;
	
	public List query(String province){
		return provDAO.query(province);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAdd(List addList){
		for (int i = 0; addList!=null && i < addList.size(); i++) {
			provDAO.insertProvinceBranch((Map)addList.get(i));
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String pkStr){
		String[] pks = StringUtils.split(pkStr, ";");
		for (int i = 0; pks != null && i < pks.length; i++) {
			provDAO.setProvinceBranchFlag(pks[i]);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(List updateList){
		for (int i = 0; updateList!=null && i < updateList.size(); i++) {
			provDAO.updateProvinceBranch((Map)updateList.get(i));
		}
	}
	
}
