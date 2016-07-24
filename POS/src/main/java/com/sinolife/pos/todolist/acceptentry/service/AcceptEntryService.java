package com.sinolife.pos.todolist.acceptentry.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.todolist.acceptentry.dao.AcceptEntryDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("acceptEntryService")
public class AcceptEntryService {

	@Autowired
	private AcceptEntryDAO acceptEntryDAO;
	
	/**
	 * 查询待受理录入保全件列表
	 * @param acceptor 受理人
	 * @return List&lt;Map&lt;String, Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryAcceptEntryTodolist(String acceptor) {
		List<Map<String, Object>> batchNotCompleteList = acceptEntryDAO.queryBatchNotCompleteList(acceptor);
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		Map<String, Object> processingMap = null;
		String processingBatchNo = null;
		String processingServiceItems = null;
		String processingStatusCode = null;
		for(int i = 0; batchNotCompleteList != null && i < batchNotCompleteList.size(); i++) {
			Map<String, Object> map = batchNotCompleteList.get(i);
			//取Map中的值
			String posBatchNo = (String) map.get("POS_BATCH_NO");
			String serviceItems = (String) map.get("SERVICE_ITEMS");
			String acceptStatusCode = (String) map.get("ACCEPT_STATUS_CODE");
			
			//由于已经在SQL中排序，所以如果跟上条记录的批次号不同则为新批次
			if(!posBatchNo.equals(processingBatchNo)) {
				processingBatchNo = posBatchNo;
				processingServiceItems = serviceItems;
				processingStatusCode = acceptStatusCode;
				
				processingMap = new HashMap<String, Object>();
				processingMap.put("posBatchNo", processingBatchNo);
				processingMap.put("serviceItemsDesc", map.get("SERVICE_ITEMS_DESC"));
				processingMap.put("acceptStatusDesc", map.get("ACCEPT_STATUS_DESC"));
				processingMap.put("clientName", map.get("CLIENT_NAME"));
				processingMap.put("policyNo", map.get("POLICY_NO"));
				processingMap.put("barcodeNo", map.get("BARCODE_NO"));
				processingMap.put("applyDate", map.get("APPLY_DATE"));
				retList.add(processingMap);
			} else if(serviceItems.equals(processingServiceItems) && acceptStatusCode.equals(processingStatusCode) && "A19".equals(acceptStatusCode)) {
				String policyNo = (String) map.get("POLICY_NO");
				policyNo = processingMap.get("policyNo") + "," + policyNo;
				processingMap.put("policyNo", policyNo);
			}
		}
		
		return retList;
		
	}
	
	public String queryPosNoBatchNo(String posNo){
		return acceptEntryDAO.queryPosNoBatchNo(posNo, PlatformContext.getCurrentUser());
	}
	
}
