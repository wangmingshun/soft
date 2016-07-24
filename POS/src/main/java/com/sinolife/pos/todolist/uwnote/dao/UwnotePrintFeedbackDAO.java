package com.sinolife.pos.todolist.uwnote.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({"rawtypes","unchecked"})
@Repository("undwrtPrintFeedbackDAO")
public class UwnotePrintFeedbackDAO extends AbstractPosDAO{

	public List queryTodoPrintList(String userId){
		List list = queryForList("QUERY_UWNOTE_TO_PRINT_LIST", userId);
		CollectionUtils.filter(list, new Predicate(){
			@Override
			public boolean evaluate(Object obj) {
				Map<String, String> map = (Map<String, String>)obj;
				return "Y".equals(queryForObject("UW_QUERY_UNDWRT_IS_DECI", map.get("POS_NO")));
			}});
		return list;
	}
	
	/**
	 * 查看detail表的记录，看是否打印过核保函了
	 * @param posNo
	 * @return
	 */
	public boolean printed(String posNo){
		List list = queryForList("QUERY_UWNOTE_PRINT_DETAIL", posNo);
		if(list==null||list.size()<1){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 核保函打印次数加1
	 * @param posNo
	 */
	public void printTimeAddOne(String posNo){
		getSqlMapClientTemplate().update(sqlName("UPDATE_DETAIL_PRINT_TIME"), posNo);
	}
	
	public List queryForPrintList(String startDate, String endDate, String userId){
		Map pMap = new HashMap();
		pMap.put("startDate", startDate);
		pMap.put("endDate", endDate);
		pMap.put("userId", userId);
		return queryForList("QUERY_UWNOTE_FOR_PRINT_LIST", pMap);
	}
	
	public List feedbackQueryPosInfo(String posNo,String userId){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("userId", userId);
		return queryForList("QUERY_FEEDBACK_POSINFO", pMap);
	}
	
	public void undwrtNoteWriteOff(Map pMap){
		queryForObject("UW_UNDWRT_NOTE_WRITE_OFF", pMap);
		if("2".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}

}
