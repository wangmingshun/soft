package com.sinolife.pos.common.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import com.ibatis.sqlmap.engine.type.BaseTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.sinolife.pos.common.dto.ClientInformationDTO;

/**
 * 处理pubcde.t_client_info_arr
 *
 */
public class TClientInfoArrHandler extends BaseTypeHandler implements TypeHandler{

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
		List<ClientInformationDTO> list = new ArrayList<ClientInformationDTO>();
		ARRAY ar = (ARRAY) cs.getArray(idx);
		if (ar != null && ar.length() > 0) {
			Object[] obj = (Object[]) ar.getArray();

			STRUCT st = null;
			for (int i = 0; i < obj.length; i++) {
				st = (STRUCT) obj[i];
				Object[] tmp = (Object[]) st.getAttributes();

				//此client对应db层的type pubcde.rec_client_info
				ClientInformationDTO client = new ClientInformationDTO();
				client.setClientNo(""+tmp[0]);
				client.setClientName(""+tmp[1]);
				client.setBirthday(tmp[2]==null?null:(Date)tmp[2]);
				client.setSexCode(""+tmp[3]);
				client.setSexDesc(""+tmp[4]);
				client.setIdTypeCode(""+tmp[5]);
				client.setIdTypeDesc(""+tmp[6]);
				client.setIdNo(""+tmp[7]);
				client.setIdnoValidityDate(tmp[8]==null?null:(Date)tmp[8]);
				client.setNationCode(""+tmp[9]);
				client.setNationDesc(""+tmp[10]);
				client.setCountryCode(""+tmp[11]);
				client.setCountryDesc(""+tmp[12]);
				client.setEducationCode(""+tmp[13]);
				client.setEducationDesc(""+tmp[14]);
				client.setMarriageCode(""+tmp[15]);
				client.setMarriageDesc(""+tmp[16]);
				client.setOccupationCode(""+tmp[17]);
				client.setOccupationDesc(""+tmp[18]);
				client.setWorkUnit(""+tmp[19]);
				client.setPosition(""+tmp[20]);
				client.setDeathDate(tmp[21]==null?null:(Date)tmp[21]);
				client.setRegisterPlace(""+tmp[22]);
				client.setPhoneticizeLastName(""+tmp[23]);
				client.setPhoneticizeFirstName(""+tmp[24]);
				
				list.add(client);
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
