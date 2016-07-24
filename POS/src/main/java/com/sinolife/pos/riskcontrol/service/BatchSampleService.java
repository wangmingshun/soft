package com.sinolife.pos.riskcontrol.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dto.SampleDTO;
import com.sinolife.pos.common.dto.SampleSeqDTO;
import com.sinolife.pos.riskcontrol.dao.BatchSampleDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("batchSampleService")
public class BatchSampleService {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	BatchSampleDAO sampleDAO;

	public void sampleSubmit(Map pMap) {
		sampleDAO.sampleSubmit(pMap);
	}

	public Map verifyNext(String sampleType) {
		return sampleDAO.verifyNext(sampleType,
				PlatformContext.getCurrentUser());
	}

	public void verifySubmit(Map pMap) {
		pMap.put("reviewResult", reviewCheck(pMap));

		sampleDAO.verifySubmit(pMap);
	}

	public List confirmQuery(String sampleType) {
		return sampleDAO.confirmQuery(sampleType,
				PlatformContext.getCurrentUser());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmSubmit(SampleDTO sampleDto) {
		List<SampleSeqDTO> sampleSeqDto = sampleDto.getSampleList();
		Map pMap = null;
		for (int i = 0; sampleSeqDto != null && i < sampleSeqDto.size(); i++) {
			SampleSeqDTO sampleSeq = sampleSeqDto.get(i);
			if (sampleSeq.isSelected()) {
				pMap = new HashMap();
				logger.info("sampleSeq"+sampleSeq.getSampleSeq()+"update start...");
				pMap.put("sampleSeq", sampleSeq.getSampleSeq());
				pMap.put("confirmResult", sampleDto.getConfirmResult());
				pMap.put("reviewResult", sampleDto.getConfirmResult().equals("1")?"Y":"N");
				pMap.put("confirmer", sampleDto.getConfirmer());
				pMap.put("resultDesc", sampleDto.getResultDesc());
				pMap.put("reviewSum", sampleSeq.getReviewSum());
				pMap.put("reviewDate", sampleSeq.getReviewDate());
				sampleDAO.confirmSubmit(pMap);
				logger.info("sampleSeq"+sampleSeq.getSampleSeq()+"update end...");
			}
		}		
	}

	/**
	 * 核对复核结果，如果复核值与原值一致，就是复核通过，否则未通过
	 * 
	 * @return
	 */
	private String reviewCheck(Map map) {
		String flag = "Y";
		// 红利只需比较这一个值
		if ((new BigDecimal("" + map.get("getDueSum"))
				.compareTo(new BigDecimal("" + map.get("reviewGetDueSum")))) != 0) {
			flag = "N";
		}
		// 生存金还要比较日期
		if ("1".equals(map.get("sampleType"))) {
			if (!map.get("getDueDate").equals(map.get("reviewGetDueDate"))) {
				flag = "N";
			}
		}
		return flag;
	}

}
