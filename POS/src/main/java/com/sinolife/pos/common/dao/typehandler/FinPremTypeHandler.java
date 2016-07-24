package com.sinolife.pos.common.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.sinolife.pos.common.dto.FinPremDTO;
import com.sinolife.sf.framework.comm.ConnectionUtil;

public class FinPremTypeHandler extends BaseTypeHandler implements TypeHandler {
	@Override
	public Object getResult(ResultSet arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getResult(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getResult(CallableStatement arg0, int arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			String jdbcType) throws SQLException {
		Connection con = ps.getConnection();
		con = ConnectionUtil.getNativeConnection(con);
		
		FinPremDTO finPremDTO =(FinPremDTO)parameter;
		Object[] result = new Object[4];
		result[0] = finPremDTO.getPolicyNo();
		result[1] = finPremDTO.getPolicyPremChgType();
		result[2] = finPremDTO.getPayToDate();
		result[3] = finPremDTO.getFrequency();
		StructDescriptor structdesc = new StructDescriptor(
				"POSCDE.REC_FIN_PREM", con);
		STRUCT struct = new STRUCT(structdesc, con, result);

		ps.setObject(i, struct);
		
	}

	@Override
	public Object valueOf(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
