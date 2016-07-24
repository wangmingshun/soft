package com.sinolife.pos.report.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * 专门处理保全清单数据类型poscde.array_pos_query_result的handler
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPosQueryResultHandler extends BaseTypeHandler implements TypeHandler {

	@Override
	public Object getResult(ResultSet arg0, String arg1) throws SQLException {
		return null;
	}

	@Override
	public Object getResult(ResultSet arg0, int arg1) throws SQLException {
		return null;
	}

	@Override
	public Object getResult(CallableStatement cs, int idx) throws SQLException {
		List list = new ArrayList();

		ARRAY ar = (ARRAY) cs.getArray(idx);
		if (ar != null && ar.length() > 0) {
			Object[] obj = (Object[]) ar.getArray();
			
			if(obj!=null && obj.length > 5000){
				throw new RuntimeException("查询结果过大"+obj.length+"，请缩小查询范围");
			}
			
			STRUCT st = null;
			for (int i = 0; i < obj.length; i++) {
				st = (STRUCT) obj[i];
				Object[] tmp = (Object[]) st.getAttributes();

				//此map对应db层的type poscde.pos_query_result_row
				Map map = new HashMap();
				map.put("POS_NO", tmp[0]);
				map.put("POLICY_NO", tmp[1]);
				map.put("POS_ITEM_DESC", tmp[2]);
				map.put("BIZ_TYPE", tmp[3]);
				map.put("ACCEPT_DATE", tmp[4]==null?"":DateFormat.getDateTimeInstance().format(tmp[4]));
				map.put("ACCEPTOR_NAME", tmp[5]);
				map.put("CHANNEL", tmp[6]);
				map.put("POLICY_STATUS", tmp[7]);
				map.put("POS_STATUS", tmp[8]);
				map.put("AGENT_NO", tmp[9]);
				map.put("AGENT_NAME", tmp[10]);
				map.put("CLIENT_NO", tmp[11]);
				map.put("CLIENT_NAME", tmp[12]);
				map.put("POLICY_BRANCH_CODE", tmp[13]);
				map.put("POLICY_BRANCH_NAME", tmp[14]);
				map.put("ACCEPT_BRANCH_CODE", tmp[15]);
				map.put("ACCEPT_BRANCH_NAME", tmp[16]);
				map.put("COUNTER_NO", tmp[17]);
				map.put("COUNTER_NAME", tmp[18]);
				map.put("PRODUCT_NAME", tmp[19]);
				map.put("BANK_NAME", tmp[20]);
				map.put("LOAN_SUM", tmp[21]);
				map.put("LOAN_DURATION", tmp[22]);
				map.put("MOVE_IN_BRANCH_CODE", tmp[23]);
				map.put("MOVE_IN_BRANCH_NAME", tmp[24]);
				map.put("MOVE_OUT_BRANCH_CODE", tmp[25]);
				map.put("MOVE_OUT_BRANCH_NAME", tmp[26]);
				map.put("FEE", tmp[27]);
				map.put("BIG_TEXT", tmp[28]);
				map.put("MEMO_TEXT", tmp[29]);
				map.put("MEMO_FLAG", tmp[30]);
				map.put("BANK_CODE", tmp[31]);
				map.put("BARCODE", tmp[32]);
				map.put("ACCOUNT_NO", tmp[33]);
				map.put("EXT_DATE", tmp[34]==null?"":DateFormat.getDateTimeInstance().format(tmp[34]));

				list.add(map);
			}
		}
		return list;
	}

	@Override
	public void setParameter(PreparedStatement arg0, int arg1, Object arg2,
			String arg3) throws SQLException {

	}

	@Override
	public Object valueOf(String arg0) {
		return null;
	}
}
