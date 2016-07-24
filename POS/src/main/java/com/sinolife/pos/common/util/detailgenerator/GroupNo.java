package com.sinolife.pos.common.util.detailgenerator;

/**
 * 组号类，由该类管理组号的增减
 */
public class GroupNo {
	
	private int groupNo = 0;
	
	public GroupNo(int beginGroupNo) {
		this.groupNo = beginGroupNo;
	}
	
	public int inc() {
		return ++groupNo;
	}
	
	public int dec() {
		return --groupNo;
	}
	
	public int intValue() {
		return groupNo;
	}
	
	public String get() {
		return String.valueOf(groupNo);
	}
	
	public void set(int value) {
		this.groupNo = value;
	}
	
	@Override
	public String toString() {
		return get();
	}
}
