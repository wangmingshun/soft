package com.sinolife.pos.common.dto;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.sinolife.pos.common.util.PosUtils;

public class BranchInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1180147381463039823L;
	
	/**
	 * 机构号
	 */
	private String id;

	/**
	 * 机构名
	 */
	private String text;

	/**
	 * 级别
	 */
	private String level;
	
	/**
	 * 页面要求的限制级别
	 */
	private String askLevel;

	/**
	 * 在页面数的状态,默认是收拢的
	 */
	private String state = "closed";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLevel() {
		return level;
	}

	public String getSubLevel() {
		return "0" + (Integer.parseInt(level) + 1);
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getState() {
		if((StringUtils.isNotBlank(askLevel) && level.compareTo(askLevel)>=0)||"04".equals(level)){
			state = null;
		}
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String toString() {
		return PosUtils.describBeanAsJSON(this);
	}

	public String getAskLevel() {
		return askLevel;
	}

	public void setAskLevel(String askLevel) {
		this.askLevel = askLevel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
