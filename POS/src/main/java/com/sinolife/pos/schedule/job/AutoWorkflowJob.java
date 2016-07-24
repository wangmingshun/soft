package com.sinolife.pos.schedule.job;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.schedule.dao.ScheduleDAO;

@Component("autoWorkflowJob")
public class AutoWorkflowJob {

	@Autowired
	private ScheduleDAO scheduleDAO;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	private Logger logger = Logger.getLogger(getClass());
	
	public void execute() {
		logger.info("autoWorkflow started at:" + PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss"));
		scheduleDAO.autoWorkflow(commonQueryDAO.getSystemDate());
		logger.info("autoWorkflow complete at:" + PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss"));
	}
}
