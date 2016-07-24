package com.sinolife.pos.posappointmentsync.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("appointmentSyncDAO")
public class AppointmentSyncDAO extends AbstractPosDAO {

	/**
	 * @Description: 查询预约相关信息明细
	 * @methodName: getAppointmentSyncList
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public Map<String,Object> getAppointmentInfo(String posNo) {
		return (Map<String, Object>) queryForObject("queryAppointmentInfo", posNo);
	}
	
}
