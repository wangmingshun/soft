package com.sinolife.pos.common.dao.typehandler;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.engine.type.ClobTypeHandlerCallback;

/**
 * 取CLOB数据的handler
 * 其实to_char（CLOB）就可以了
 *
 */
public class ClobTypeHandler extends ClobTypeHandlerCallback {

	/**
	 * 抄的BLOB的set方法，还得改
	 */
	public void setParameter(ParameterSetter ps, Object obj)
			throws SQLException {
		if (obj == null) {
			ps.setBytes(null);
			return;
		}
		byte[] in = (byte[]) obj;
		ByteArrayInputStream bis = new ByteArrayInputStream(in);
		ps.setBinaryStream(bis, in.length);
	}

	public Object getResult(ResultGetter arg0) throws SQLException {
		CharArrayWriter out;
		try {
			Clob clob = arg0.getClob();
			if (clob == null)
				return null;
			Reader r = clob.getCharacterStream();
			out = new CharArrayWriter();
			char[] temp = new char[1024];
			int length;
			while ((length = r.read(temp)) != -1) {
				out.write(temp, 0, length);
				out.write("\n");
			}
			return out.toString();
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public Object valueOf(String arg0) {
		return null;
	}
}
