package com.sinolife.pos.pub.web;

import java.io.Serializable;

/**
 * 分页参数读取辅助类
 * @author duanpeng
 *
 */
public class PaginatedHelper implements Serializable {
	/**
	 * 是否分页标志
	 */
	private boolean isPageFlag=true;
	public boolean getIsPageFlag() {
		return isPageFlag;
	}
	public void setIsPageFlag(boolean isPageFlag) {
		this.isPageFlag = isPageFlag;
	}
	// 每页记录条数
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int page=1;
	private int rows;

	// 开始位置
	private int startIndex;
	public static final int PAGE_SIZE = 15;
	/**
	 * 获取当前页
	 * @return
	 */
	public int getCurrentPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public void setRows(int rows) {
		if (rows > 0) {
			this.rows = rows;
		} else {
			this.rows = PAGE_SIZE;
		}
	}
	// 计算查询开始位置
	private void calculatestartIndex() {
		if (this.page > 0) {
		} else {
			page = 1;
		}
		this.startIndex = (this.page - 1) * this.rows;
	}
	
	/**
	 * 取得开始位置
	 * @return
	 */
	public int getStartIndex() {
		calculatestartIndex();
		return startIndex;
	}
	public int getPageSize(){
		return this.rows;
	}
	public int getRows() {
		return rows;
	}
}
