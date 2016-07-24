package com.sinolife.pos.common.dto;

import java.io.Serializable;

import javax.persistence.Entity;

/**
 * 免填单信息
 */

@Entity
public class FillFormDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3725882867905597846L;
	private String fillForm;		//免填单
	
	/* 构造函数 */

	public FillFormDTO() {
	}

	/* 属性存取 */

    public String getFillForm() {
        return fillForm;
    }

    public void setFillForm(String fillForm) {
        this.fillForm = fillForm;
    }
}
