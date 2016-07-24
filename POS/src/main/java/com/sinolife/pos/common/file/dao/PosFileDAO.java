package com.sinolife.pos.common.file.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("posFileDAO")
public class PosFileDAO extends AbstractPosDAO{

	public void insertPosFileInfo(String fileId, String fileName, String policyNo, String posNo, String fileType, String userId){
		if(StringUtils.isBlank(fileId)||
		   StringUtils.isBlank(fileName)||
		   (StringUtils.isBlank(policyNo)&&StringUtils.isBlank(posNo))||
		   StringUtils.isBlank(userId)){
			throw new RuntimeException("记录文件信息的必要参数不完整");
		}
		Map pMap = new HashMap();
		pMap.put("fileId", fileId);
		pMap.put("policyNo", policyNo);
		pMap.put("fileType", fileType);
		pMap.put("posNo", posNo);
		pMap.put("fileName", fileName);
		pMap.put("userId", userId);
		
		getSqlMapClientTemplate().insert(sqlName("INSERT_POS_FILE_INFO"), pMap);
	}
	
	public void insertPosFileInfo(String fileId, String fileName,  String fileType, String userId){
		if(StringUtils.isBlank(fileId)||
		   StringUtils.isBlank(fileName)||		 
		   StringUtils.isBlank(userId)){
			throw new RuntimeException("记录文件信息的必要参数不完整");
		}
		Map pMap = new HashMap();
		pMap.put("fileId", fileId);		
		pMap.put("fileType", fileType);		
		pMap.put("fileName", fileName);
		pMap.put("userId", userId);		
		getSqlMapClientTemplate().insert(sqlName("INSERT_POS_FILE_INFO"), pMap);
	}
	
	
	
	
	
	public List<Map<String,String>> queryPosFileInfo(String policyNo,String posNo,String fileType){
		if(StringUtils.isBlank(policyNo) && StringUtils.isBlank(posNo)){
			throw new RuntimeException("查询文件信息，保单号和保全号不能都为空");
		}
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("posNo", posNo);
		pMap.put("fileType", fileType);
		
		return queryForList("QUERY_POS_FILE_INFO", pMap);
	}
	
}
