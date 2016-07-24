package com.sinolife.pos.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.sf.kts.CodeTableResource;
import com.sinolife.sf.kts.ReloadableResource;
import com.sinolife.sf.kts.SchedulePolicy;

/**
 * 缓存代码表，一般用于下拉框数据源
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Component("posCodeTableBean")
public class PosCodeTableBean extends ReloadableResource implements CodeTableResource{

	private Logger logger = Logger.getLogger(PosCodeTableBean.class);

	private ConcurrentHashMap posTableMap = new ConcurrentHashMap();
	
	private static final List<String> CODE_TABLE_SQL_NAMES;
	static {
		List<String> codeTableSqlNames = new ArrayList<String>();
		codeTableSqlNames.add(CodeTableNames.SEX);
		codeTableSqlNames.add(CodeTableNames.ID_TYPE);
		codeTableSqlNames.add(CodeTableNames.POS_APPLY_TYPE_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_ACCEPT_CHANNEL_CODE);
		codeTableSqlNames.add(CodeTableNames.ALL_POS_ACCEPT_CHANNEL_CODE);		
		codeTableSqlNames.add(CodeTableNames.PRODUCT_SERVICE_ITEMS);
		codeTableSqlNames.add(CodeTableNames.PRODUCT_SERVICE_ITEMS_LOST_PREM);
		codeTableSqlNames.add(CodeTableNames.POS_INPUT_GRADE_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_RANK_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_AMOUNT_DAYS_GRADE);
		codeTableSqlNames.add(CodeTableNames.POS_SPECIAL_FUNC_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_SPECIAL_RULE_CODE);
		codeTableSqlNames.add(CodeTableNames.FIN_FOREIGN_EXCHANGE_TYPE);	
		codeTableSqlNames.add(CodeTableNames.POS_APPLY_MATERIAL_CODE);		
		codeTableSqlNames.add(CodeTableNames.DIVIDEND_SELECTION);
		codeTableSqlNames.add(CodeTableNames.COUNTRY);
		codeTableSqlNames.add(CodeTableNames.NATION);
		codeTableSqlNames.add(CodeTableNames.EDUCATION);
		codeTableSqlNames.add(CodeTableNames.MARRIAGE);
		codeTableSqlNames.add(CodeTableNames.PHONE_TYPE);
		codeTableSqlNames.add(CodeTableNames.PROVINCE);
		codeTableSqlNames.add(CodeTableNames.POS_REPLACE_SIGN_CAUSE_CODE);
		codeTableSqlNames.add(CodeTableNames.RELATIONSHIP);
		codeTableSqlNames.add(CodeTableNames.APL_OPTION);
		codeTableSqlNames.add(CodeTableNames.POS_ROLLBACK_CAUSE_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_ACCEPT_STATUS_CODE);
		codeTableSqlNames.add(CodeTableNames.EMAIL_TYPE);
		codeTableSqlNames.add(CodeTableNames.ADDRESS_TYPE);
		codeTableSqlNames.add(CodeTableNames.BENEFIT_TYPE);
		codeTableSqlNames.add(CodeTableNames.BUSINESS_NO_TYPE);
		codeTableSqlNames.add(CodeTableNames.CHARGING_METHOD);
		codeTableSqlNames.add(CodeTableNames.POS_POL_MORT_UNFREEZE_CAUSE);
		codeTableSqlNames.add(CodeTableNames.CHANNEL_TYPE_TBL);
		codeTableSqlNames.add(CodeTableNames.FREQUENCY);
		codeTableSqlNames.add(CodeTableNames.POS_SURRENDER_CAUSE_CODE);
		codeTableSqlNames.add(CodeTableNames.FREQUENCY_MINI);
		codeTableSqlNames.add(CodeTableNames.POS_RENEW_REFUND_CAUSE_CODE);
		codeTableSqlNames.add(CodeTableNames.POS_TASK_TYPE);
		codeTableSqlNames.add(CodeTableNames.BANK_CATEGORY);
		codeTableSqlNames.add(CodeTableNames.APPROVAL_SERVICE_TYPE);
		codeTableSqlNames.add(CodeTableNames.POS_PROBLEM_DEAL_RESULT);
		codeTableSqlNames.add(CodeTableNames.POS_VIP_TYPE);
		codeTableSqlNames.add(CodeTableNames.POS_VIP_GRADE);
		codeTableSqlNames.add(CodeTableNames.POS_NOTE_TYPE);
		codeTableSqlNames.add(CodeTableNames.POS_NOTE_TYPE_BATCH);
		codeTableSqlNames.add(CodeTableNames.POS_BRANCH_CLASSIFY);
		codeTableSqlNames.add(CodeTableNames.FIN_ACCOUNT_NO_TYPE);
		codeTableSqlNames.add(CodeTableNames.DEPARTMENT_INFO);
		codeTableSqlNames.add(CodeTableNames.POS_EXAM_TYPE);
		codeTableSqlNames.add(CodeTableNames.POS_BANK_CATEGORY);
		CODE_TABLE_SQL_NAMES = codeTableSqlNames;
	}
	
	@Autowired
	private PosCodeTableDAO codeTableDAO;
	
	@Autowired
	private CommonQueryDAO queryDAO;
	
	private int times = 1;//控制不同资源不同调度频率
	
	protected ConcurrentHashMap loadResource() {
		//48个半小时，新的一天
		if(times>48){
			times = 1;
			posTableMap.clear();
		}
		
		//db系统时间，给WdatePicker设当前时间用，页面上其他地方也可以/可能用到，该时间需要及时更新，已避免跨天的问题，为此引入了times变量来控制
		//半小时更新一次也不能完全避免跨天的问题，只能是几乎避免
		posTableMap.put("SYSDATE", new SimpleDateFormat("yyyy-MM-dd").format(queryDAO.getSystemDate()));
		
		if(times == 1){
			for(String codeTableSqlName : CODE_TABLE_SQL_NAMES) {
				try {
					posTableMap.put(codeTableSqlName, Collections.synchronizedList(codeTableDAO.queryCodeTable(codeTableSqlName)));
				} catch(Exception e) {
					logger.error("Init CodeTable failed: error to load code table -> " + codeTableSqlName, e);
				}
			}	
		}
		
		times++;
		return posTableMap;
	}

	public String getResourceName() {
		return "posTableMap";
	}

	protected void configSchedulePolicy(SchedulePolicy schedulePolicy) {
        schedulePolicy.setPeroid(30); 				 //这里设置更新调度周期
        schedulePolicy.setTimeUnit(TimeUnit.MINUTES);//这里是调度周期的单位
	}

	public Map getPosTableMap() {
		return posTableMap;
	}
}
