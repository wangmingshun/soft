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
import com.sinolife.pos.posSync.PosBiaSyncImpl;
import com.sinolife.pos.posSync.PosWebSync;
import com.sinolife.pos.posSync.dao.CommonSyncDAO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;

@Component("posSyncJob")
public class posSyncJob {
	@Autowired
	private ScheduleDAO scheduleDAO;
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private TransactionTemplate txTmpl;
	@Autowired
	PosBiaSyncImpl posBiaSyncImpl;
	@Autowired
	CommonSyncDAO commonSyncDAO;
	@Autowired
	PosWebSync posWebSync;
	
	public void execute() {
		logger.info("posSyncJob "
				+ Thread.currentThread().getId()
				+ " started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		List<Map<String, Object>> notSyncList = commonSyncDAO.queryNotSyncList();
		if (notSyncList != null && !notSyncList.isEmpty()) {
			for (final Map<String, Object> map : notSyncList) {
                txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						String businessType =(String) map.get("BUSINESS_TYPE");
						String businessNo=(String) map.get("BUSINESS_NO"); 
						String businessNoType=(String) map.get("BUSINESS_NO_TYPE"); 
						String syncStatus =(String) map.get("SYNC_STATUS"); 
						String controlPk =(String) map.get("CONTROL_PK");
						String policyNo = (String) map.get("POLICYNO");
						//锁定单条任务  得到结果为Y说明没有被锁住，不是Y说明被锁住了，就不执行该条记录了
						String lock = commonSyncDAO.lockPosSyncJobDetial(controlPk);
						if(!"Y".equals(lock)) {
							return false;
						}
						//根据不同的business_type进行不同处理
						if("01".equals(businessType)){
							//处理同步事务
							String posNo=businessNo;
							//首先基表中查询中介机构查询码是否存在了，存在则不再调用查询码接口去获取了
							String queryCode = commonSyncDAO.getAgencyQueryCode(businessType, businessNo, 
									businessNoType, "queryCode");
							if(queryCode == null || "".equals(queryCode)) {
								Map<String, Object> branchMap = 
									posBiaSyncImpl.queryBranchCheckCode(posNo, businessType, 
											businessNoType,businessNo);
								String resultCode = (String) branchMap.get("SL_RSLT_CODE");
								if(resultCode != null && "999999".equals(resultCode)) {
									//调用接口获取中介机构查询码成功
									queryCode = (String) branchMap.get("queryCode");
								}
							}
							//再次判断queryCode是否存在了，如果不为空则继续下一步处理
							if(queryCode != null && !"".equals(queryCode)) {
								//调用批单等级接口
								posBiaSyncImpl.posRegister(posNo, queryCode, businessType, 
										businessNoType,businessNo);
							}
						} if ("02".equals(businessType)) {
							String posNo=businessNo;
							Map<String, Object> webSyncMap = posWebSync.sendAsynMessage(posNo, businessType, 
									businessNoType,businessNo);
							
						} if ("10".equals(businessType)) {
							String posNo = businessNo;
							posBiaSyncImpl.toPartnerEsbHandle(policyNo, businessNo, businessType, businessNoType);
						}
						return true;
					}
				});
			}
		}


		logger.info("posSyncJob "
				+ Thread.currentThread().getId()
				+ " ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
	}
}
