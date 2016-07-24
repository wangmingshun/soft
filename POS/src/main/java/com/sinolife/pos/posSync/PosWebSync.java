package com.sinolife.pos.posSync;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.posSync.dao.CommonSyncDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
@Service("posWebSync")
public class PosWebSync {
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	CommonSyncDAO commonSyncDAO;
	public Map<String, Object> sendAsynMessage(String pos_no, String businessType,
			String businessNoType, String businessNo){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		WebPosService webPosService = PlatformContext.getEsbContext().getEsbService(WebPosService.class);
		PosInfoDTO posinfo=commonQueryDAO.queryPosInfoRecord(pos_no);
		// 获取当前时间
		Date sysdate = commonQueryDAO.getSystemDate();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		retMap.put("messageType", "3");//消息类型
		retMap.put("policyNo",pos_no);//保全号
		if(posinfo.getEffectiveDate() == null){
			retMap.put("businessDate",posinfo.getEffectiveDate());//业务时间(保全生效日)
		}else{
			retMap.put("businessDate",formatter.format(posinfo.getEffectiveDate()));//业务时间(保全生效日)
		}
		retMap.put("noticeDate",formatter.format(sysdate));//通知时间(当前日期)
		resultMap = webPosService.sendAsynMessage(retMap);
	   if(resultMap.get("resultCode").equals("N")){
		   //失败
		   commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", (String)resultMap.get("resultMsg"));
	   }else{
			//成功
			commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "Y", "");  
	   }
		return resultMap;
	}
}
