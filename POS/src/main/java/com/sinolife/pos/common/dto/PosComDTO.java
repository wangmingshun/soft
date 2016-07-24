package com.sinolife.pos.common.dto;

import java.io.Serializable;

import com.sinolife.pos.common.util.PosUtils;

/**
 * POS里的上层DTO，目前只添加了两个公共属性
 */
public class PosComDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7873652071615185467L;

	private String flag;//调用存储过程用
	private String message;
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString(){
		return PosUtils.describBeanAsJSON(this);
	}
}
