package com.sinolife.pos.setting.mortgageprotocol.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.setting.mortgageprotocol.dao.MortgageProtocolDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("mortgageProtocolService")
public class MortgageProtocolService {

	@Autowired
	MortgageProtocolDAO protocolDAO;
	
	@Autowired
	CommonQueryDAO queryDAO;
	
	public List query(String branch){
		List protocolList = protocolDAO.query(branch);
		
		//在取数SQL已order by的基础上,把同一协议的险种装在一起
		Map preMap = new HashMap();
		Map curMap;
		for (int i = 0; protocolList!=null && i < protocolList.size(); i++) {
			curMap = (Map)protocolList.get(i);
			if(curMap.get("PROTOCOL_CODE").equals(preMap.get("PROTOCOL_CODE"))){
				preMap.put("PRODUCT_CODE", preMap.get("PRODUCT_CODE")+";"+curMap.get("PRODUCT_CODE"));
				preMap.put("PRODUCT_NAME", preMap.get("PRODUCT_NAME")+";"+curMap.get("PRODUCT_NAME"));
				protocolList.remove(i);
				i--;
			}else{
				preMap = curMap;
			}
		}
		return protocolList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAdd(List addList){
		Map pkMap = new HashMap();
		pkMap.put("table", "pos_mortgage_protocol");
		pkMap.put("column", "protocol_code");
		pkMap.put("owner", "posrule");
		
		for (int i = 0; addList!=null && i < addList.size(); i++) {
			Map pMap = (Map)addList.get(i);
			pMap.put("protocolCode", queryDAO.getSeq(pkMap));
			protocolDAO.insertProtocol(pMap);
			
			String[] products = splitProduct((String)pMap.get("productCode"));
			for (int j = 0; products!=null && j < products.length; j++) {
				pMap.put("productCode", products[j]);
				protocolDAO.insertProduct(pMap);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(String protocolCodeStr){
		String[] protocolCodes = StringUtils.split(protocolCodeStr, ";");
		for (int i = 0; protocolCodes != null && i < protocolCodes.length; i++) {
			protocolDAO.updateProtocolFlag(protocolCodes[i]);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(List updateList){
		for (int i = 0; updateList!=null && i < updateList.size(); i++) {
			protocolDAO.updateProtocol((Map)updateList.get(i));
		}
	}
	
	public List queryBranchBanks(String branchNo){
		return protocolDAO.queryBranchBanks(branchNo);
	}
	
	/**
	 * 支持多种分隔符号,解析用户录入的险种代码
	 */
	private String[] splitProduct(String productStr){
		if(productStr==null)return null;
		
		productStr = productStr.replaceAll("；", ";");
		productStr = productStr.replaceAll(",", ";");
		productStr = productStr.replaceAll("，", ";");
		productStr = productStr.replaceAll(" ", ";");
		productStr = productStr.replaceAll("	", ";");
		return StringUtils.split(productStr,";");
	}
}
