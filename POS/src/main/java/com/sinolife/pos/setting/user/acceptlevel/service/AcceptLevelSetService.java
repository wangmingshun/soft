package com.sinolife.pos.setting.user.acceptlevel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.setting.user.acceptlevel.dao.AcceptLevelSetDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("acceptLevelService")
public class AcceptLevelSetService {

	@Autowired
	AcceptLevelSetDAO acceptLevelDAO;
	
	public List queryAcceptLevelInfo(String level){
		return acceptLevelDAO.queryAcceptLevelInfo(level);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void setAcceptLevel(Map pMap){
		List<String> origin = queryAcceptLevelInfo((String)pMap.get("grade"));
		
		acceptLevelDAO.setAcceptLevel(pMap);
		
		acceptLevelDAO.setUserAccept(canceledItems(origin,(String)pMap.get("items")),addedItems(origin,(String)pMap.get("items")), (String)pMap.get("grade"));
	}
	
	/**
	 * 获取被删除的项目
	 * @param origin
	 * @param newItems
	 * @return
	 */
	private List<String> canceledItems(List<String> origin, String newItems){
		newItems = ";"+newItems;
		
		List<String> canceled = new ArrayList<String>();
		for (String items : origin==null?new ArrayList<String>():origin) {
			if(newItems.indexOf(";"+items+";")<0){
				canceled.add(items);
			}
		}
		return canceled;
	}
	
	/**
	 * 获取新增的项目
	 * @param origin
	 * @param newItems
	 * @return
	 */
	private List<String> addedItems(List<String> origin, String newItems){
		newItems = ";"+newItems;
		List<String> added = new ArrayList<String>();
		
		for (String items : origin==null?new ArrayList<String>():origin) {
			newItems = newItems.replaceAll(";"+items+";", ";");
		}
		
		String s[] = newItems.substring(1, newItems.length()).split(";");
		for (int i = 0; i < s.length; i++) {
			if(StringUtils.isNotBlank(s[i])){
				added.add(s[i]);
			}
		}

		return added;
	}
	
}
