package com.sinolife.pos.schedule;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinolife.pos.schedule.thread.NoticePrintThread;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Component
public class NoticePrintBatchProcessManager {

	private Thread workingThread;
	
	@Autowired
	private NoticePrintThread noticePrintThread;
	
	@PostConstruct
	public void init() {
		PlatformContext.getRuntimeConfig();
		
		String runAtThisServer = RuntimeConfig.get("com.sinolife.pos.NoticePrintProcessManager.runAtThisServer", String.class);
		if(workingThread == null && StringUtils.isNotBlank(runAtThisServer) && "true".equalsIgnoreCase(runAtThisServer.trim())) {
			String delayTime = RuntimeConfig.get("com.sinolife.pos.NoticePrintProcessManager.delayTime", String.class);
			String intervalTime = RuntimeConfig.get("com.sinolife.pos.NoticePrintProcessManager.intervalTime", String.class);
			
			long longDelayTime = 10000L;
			long longIntervalTime = 1000L;
			if(StringUtils.isNotBlank(delayTime)) {
				try {
					longDelayTime = Long.parseLong(delayTime);
				} catch(Exception e) {}
			}
			if(StringUtils.isNotBlank(intervalTime)) {
				try {
					longIntervalTime = Long.parseLong(intervalTime);
				} catch(Exception e) {}
			}
			noticePrintThread.setDelayTime(longDelayTime);
			noticePrintThread.setIntervalTime(longIntervalTime);
			workingThread = new Thread(noticePrintThread);
			workingThread.start();
		}
	}
	
	@PreDestroy
	public void destroy() {
		if(workingThread != null && workingThread.isAlive() && !workingThread.isInterrupted()) {
			workingThread.interrupt();
			workingThread = null;
		}
	}
}
