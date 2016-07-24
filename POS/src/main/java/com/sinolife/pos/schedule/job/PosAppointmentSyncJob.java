package com.sinolife.pos.schedule.job;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.posappointmentsync.PosAppointmentSyncImpl;
import com.sinolife.pos.posappointmentsync.dao.CommonAppointmentSyncDAO;

@Component("posAppointmentSyncJob")
public class PosAppointmentSyncJob {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private TransactionTemplate txTmpl;
	@Autowired
	private CommonAppointmentSyncDAO commonAppointmentSyncDAO; 
	@Autowired
	private PosAppointmentSyncImpl posAppointmentSyncImpl;
	
	public void execute() {
		logger.info("posAppointmentSyncJob "
				+ Thread.currentThread().getId()
				+ " started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		List<Map<String, Object>> appointmentList = commonAppointmentSyncDAO.getAppointmentSyncList();
		if (appointmentList != null && !appointmentList.isEmpty()) {
			for (final Map<String, Object> map : appointmentList) {
                txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						String posNo = (String) map.get("POSNO");
						String serviceItems = (String) map.get("SERVICEITEM"); 
						String applyDate = (String) map.get("APPLYDATE");
						//锁定单条任务  locked为true表示该记录没有被锁定
						boolean locked = commonAppointmentSyncDAO.lockAppointmentDetail(posNo);
						if(!locked) {
							return false;
						}
						
						//不管处理结果如何，都需要将该任务设置为已处理
						commonAppointmentSyncDAO.updateAppointmentStatusCode(posNo);
						logger.info("更新pos_appointment表状态完成。posNo:" + posNo);
						
						//此处不做保全项目判断，具体逻辑判断放到handleOperation方法中处理
						posAppointmentSyncImpl.handleOperation(posNo, applyDate, serviceItems);
						logger.info("预约保全项目serviceItems：" + serviceItems + ",处理完毕。");
						
//						if("2".equals(serviceItem)) {
//							//预约任务为退保
//							//调用对应的接口处理
//							posAppointmentSyncImpl.surrenderOperation(posNo, applyDate);
//						}

						return true;
					}
				});
			}
		}


		logger.info("posAppointmentSyncJob "
				+ Thread.currentThread().getId()
				+ " ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
	}
}
