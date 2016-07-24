package com.sinolife.pos.others.undelivery.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("unDeliveryDAO")
public class UnDeliveryDAO extends AbstractPosDAO {

	public void update(String sequence, String returnReason) {
		Map pMap = new HashMap();
		pMap.put("sequence",sequence);
		pMap.put("returnReason",returnReason);
		getSqlMapClientTemplate().update(sqlName("UPDATE_NOTE_MAIN_RETURN_REASON"), pMap);
	}
	
	public void csfile(String sequence, String user){
		Map pMap = new HashMap();
		pMap.put("p_detail_sequence_no",sequence);
		pMap.put("p_upload_user",user);
		queryForObject("insertPosNoteReturnManage", pMap);
	}
	
    /**
     * 获取通知信息
     * @param detailSequenceNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-11-8
     */
    public String getNoteMainBySequence(String detailSequenceNo) {
        return  (String) queryForObject("getNoteMainBySequence", detailSequenceNo);
    } 	
    
    /**
	 * 信函问题件代办列表
	 * 
	 * @param accessor
	 *            处理人ID
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public List<PosProblemItemsDTO> queryNoteReturnManage(String accessor) {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		List<String> problemStatusList = new ArrayList<String>();
		problemStatusList.add("1");
		problemStatusList.add("2");
		problemStatusList.add("3");
		criteriaMap.put("problemStatusList", problemStatusList);
		criteriaMap.put("accessor", accessor);
		return queryNoteByCriteria(criteriaMap);
	}
	
	public List<PosProblemItemsDTO> queryNoteByCriteria(
			Map<String, Object> criteriaMap) {
		if (StringUtils.isBlank((String) criteriaMap.get("accessor"))
				&& StringUtils.isBlank((String) criteriaMap
						.get("barcodeNo")))
			throw new RuntimeException("问题件处理人和问题件号码不可同时为空");
		
		return queryForList("queryNoteByCriteria", criteriaMap);
	}
	
	/**
	 * 根据条形码查询问题件
	 */
	public PosProblemItemsDTO queryBarcodeNoByID (String barcodeNo,String policyNo,String problemItemNo){
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("barcodeNo", barcodeNo);
		criteriaMap.put("policyNo", policyNo);
		criteriaMap.put("problemItemNo", problemItemNo);
		return (PosProblemItemsDTO) this.getSqlMapClientTemplate()
		.queryForObject(
				UnDeliveryDAO.class.getName() + ".queryBarcodeNoByID", criteriaMap);
	}
	
	/**
	 * 地址正确更改处理意见和状态
	 */
	 public Map<String, Object> updateStatusDesc(String detailSequenceNo, Date sysDate,String problemItemType,String posNo,String dealOpinion){
		 Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("detailSequenceNo", detailSequenceNo);
			paraMap.put("dealDate", sysDate);
			paraMap.put("dealType", problemItemType);
			paraMap.put("posNo", posNo);
			paraMap.put("dealDesc", dealOpinion);
			queryForObject("getDealType", paraMap);
			return paraMap;
	 }
	 
	 /**
	  *  getPolicyNoList
	  */
	 public Map<String, Object> getPolicyNoList(PosProblemItemsDTO posProblemItemsDTO) {
		 String policyNo = posProblemItemsDTO.getPosNo();
		 String problemItemNo  = posProblemItemsDTO.getProblemItemNo();
		 Map<String, Object> paraMap = new HashMap<String, Object>();
		 paraMap.put("policyNo", policyNo);
		 paraMap.put("problemItemNo", problemItemNo);
		 return (Map<String, Object>) queryForObject("getPolicyNoList", paraMap);
	 }
	 
	 /**
	  * 地址错误更改处理意见
	  * @param policyNo
	  * @param calcDate
	  * @return
	  */
	 public Map<String, Object> getDealType(String detailSequenceNo, Date sysDate,String dealType,String posNo,String dealOpinion) {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("detailSequenceNo", detailSequenceNo);
			paraMap.put("dealDate", sysDate);
			paraMap.put("dealType", dealType);
			paraMap.put("posNo", posNo);
			paraMap.put("dealDesc", dealOpinion);
			queryForObject("getDealType", paraMap);
			return paraMap;
	}
	 
	/**
	 * 退信任务交接给新管理员
	 * @param letterManager
	 * @param letterManagerNew
	 *            void
	 * @author zhangyi.wb
	 * @time 2016-6-30
	 */
	public void senderHandoverSubmit(String letterManager,
			String letterManagerNew) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("letterManager", letterManager);
		pMap.put("letterManagerNew", letterManagerNew);
		getSqlMapClientTemplate().update(sqlName("senderHandoverSubmit"), pMap);
	}
	 
	/**
	 * 检查新邢欢管理员是否有权限
	 * @return Integer
	 * @author zhangyi.wb
	 * @time 2016-6-30
	 */
	public Integer checkLetterManager(String letterManagerNew){
		return (Integer) queryForObject("letterManagerNew", letterManagerNew);
	} 
}
