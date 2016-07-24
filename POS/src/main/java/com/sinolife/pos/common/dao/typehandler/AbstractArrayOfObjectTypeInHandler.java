package com.sinolife.pos.common.dao.typehandler;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.sinolife.sf.framework.comm.ConnectionUtil;

/**
 * 自定义对象数组TypeHandler基类
 */
public abstract class AbstractArrayOfObjectTypeInHandler extends BaseTypeHandler
		implements TypeHandler {

	protected Logger logger = Logger.getLogger(getClass());

	@Override
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		Array array = rs.getArray(columnName);
		return array.getArray();
	}

	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		Array array = rs.getArray(columnIndex);
		return array.getArray();
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		Object obj = cs.getObject(columnIndex);
		logger.debug("getResult:" + obj);
		Array array = cs.getArray(columnIndex);
		return array.getArray();
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,	String jdbcType) throws SQLException {
		// 本连接池连接
		Connection conn = ps.getConnection();
		try {
			// 获取原生Oracle 连接
			conn = ConnectionUtil.getNativeConnection(conn);
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor(getDBListTypeName(), conn);
			StructDescriptor structdesc = new StructDescriptor(getDBListItemTypeName(), conn);
			
			List<?> paramList = (List<?>) parameter;
			
			STRUCT[] structs = null;
			
			if (paramList != null && paramList.size() > 0) {
				structs = new STRUCT[paramList.size()];
				for(int j = 0; j < paramList.size(); j++) {
					Object[] result = getPropertyArrayFromListItem(paramList.get(j));
					structs[j] = new STRUCT(structdesc, conn, result);
				}				
			}
			ARRAY array = new ARRAY(desc, conn, structs);
			ps.setArray(i, array);
			
		} catch (Exception e) {
			throw new RuntimeException("创建自定义数组失败:" + e.getMessage(), e);
		}
	}

	@Override
	public Object valueOf(String arg0) {
		return null;
	}

	/**
	 * 将数组中数据项对象转化为Object[]
	 * @param obj
	 * @return
	 */
	protected abstract Object[] getPropertyArrayFromListItem(Object obj);

	/**
	 * 获取数据库中数组类型名称
	 * @return
	 */
	protected abstract String getDBListTypeName();

	/**
	 * 获取数据库中数组类型的数据项类型名称
	 * @return
	 */
	protected abstract String getDBListItemTypeName();
}
