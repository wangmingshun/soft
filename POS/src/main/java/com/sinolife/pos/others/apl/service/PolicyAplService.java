package com.sinolife.pos.others.apl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.others.apl.dao.PolicyAplDAO;

@Service
public class PolicyAplService {

	@Autowired
	PolicyAplDAO aplDAO;
	
	public String policySubmit(String policyNo){
		return aplDAO.policySubmit(policyNo);
	}
	
}
