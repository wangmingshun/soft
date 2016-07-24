package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.util.PosValidateWrapper;

/**
 * 26 红利选择权变更
 * @author wangkui
 *
 */
public class ServiceItemsInputDTO_26 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = 3809436366398409084L;

	/**
	 * 当前红利方式
	 */
	private String oldDividendSelection;
	
	/**
	 * 新的红利方式
	 */
	private String newDividendSelection;

	public String getOldDividendSelection() {
		return oldDividendSelection;
	}

	public void setOldDividendSelection(String oldDividendSelection) {
		this.oldDividendSelection = oldDividendSelection;
	}

	public String getNewDividendSelection() {
		return newDividendSelection;
	}

	public void setNewDividendSelection(String newDividendSelection) {
		this.newDividendSelection = newDividendSelection;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	public void validate(Errors err) {
		super.validate(err);
		
		PosValidateWrapper wrapper = new PosValidateWrapper(err);
		if(oldDividendSelection.equals(newDividendSelection)){
			wrapper.addErrMsg("dividendSelection", "新的红利给付方式不能和原有方式相同");
		}
	}
}
