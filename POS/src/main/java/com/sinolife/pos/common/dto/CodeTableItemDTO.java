package com.sinolife.pos.common.dto;

import java.io.Serializable;

public class CodeTableItemDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 900568347728014901L;
	private String code;
	private String description;

	public CodeTableItemDTO() {
	}

	public CodeTableItemDTO(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CodeTableItemDTO [code=" + code + ", description="
				+ description + "]";
	}

}
