package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import net.unihub.framework.util.common.MathUtil;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyDividendDTO;


/**
 * 42保单生效日调整
 */
public class ServiceItemsInputDTO_42 extends ServiceItemsInputDTO {
	
	private String oldMaturityDate;//原满期日期
	
	private String oldEffectDate;//原生效日期

	private String newEffectDate;//新生效日起
	
	private String pk_serial;//PK_SERIAL
	
	public String getPk_serial() {
		return pk_serial;
	}


	public void setPk_serial(String pk_serial) {
		this.pk_serial = pk_serial;
	}


	public String getOldMaturityDate() {
		return oldMaturityDate;
	}


	public void setOldMaturityDate(String oldMaturityDate) {
		this.oldMaturityDate = oldMaturityDate;
	}


	public String getOldEffectDate() {
		return oldEffectDate;
	}


	public void setOldEffectDate(String oldEffectDate) {
		this.oldEffectDate = oldEffectDate;
	}


	public String getNewEffectDate() {
		return newEffectDate;
	}


	public void setNewEffectDate(String newEffectDate) {
		this.newEffectDate = newEffectDate;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -6909314174512960727L;

	
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	

}
