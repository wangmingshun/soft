package com.sinolife.pos.posappointmentsync.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("commonAppointmentSyncDAO")
public class CommonAppointmentSyncDAO extends AbstractPosDAO {

	/**
	 * @Description: 查询未处理的保全预约任务
	 * @methodName: getAppointmentSyncList
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public List<Map<String,Object>> getAppointmentSyncList() {
		return queryForList("queryAppointmentSyncList", null);
	}
	
	/**
	 * @Description: 锁定预约任务表的单条记录
	 * @methodName: lockAppointmentDetail
	 * @param posNo
	 * @return
	 * @return boolean
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public boolean lockAppointmentDetail(String posNo) {
		String flag = (String) queryForObject("lockAppointmentDetial", posNo);
		return "Y".equals(flag);
	}
	
	/**
	 * @Description: 更新预约表状态为已处理
	 * @methodName: updateAppointmentDetail
	 * @param posNo
	 * @return void
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public void updateAppointmentStatusCode(String posNo) {
		getSqlMapClientTemplate().update(sqlName("updateAppointmentDetailStatusCode"), posNo);
	}
	
	/**
	 * @Description: 更新预约表的预约保全号
	 * @methodName: updateAppointmentDetailAppointedPosNo
	 * @param posNo
	 * @param appointedPosNo
	 * @return void
	 * @author WangMingShun
	 * @date 2015-9-8
	 * @throws
	 */
	public  void updateAppointmentAppointedPosNo(String posNo, String appointedPosNo) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("posNo", posNo);
		pMap.put("appointedPosNo", appointedPosNo);
		getSqlMapClientTemplate().update(sqlName("updateAppointmentDetailAppointedPosNo"), pMap);
	}
}
