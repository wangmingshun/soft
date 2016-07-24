package com.sinolife.pos.common.util;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * 内存数据翻页支持类. 该类对数据List进行封转，每次只访问其中一个数据页的数据
 * 
 * @param <T>
 *            数据源List中元素的类型
 */

public class PaginationDataWrapper<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7570265251450715219L;
	private List<T> dataProvider; 	// 数据源
	private int pageSize; 			// 每页数据条数
	private int pageStartIndex; 	// 当前页的数据开始索引

	/**
	 * 构造函数
	 * 
	 * @param dataProvider
	 *            数据源List
	 * @param pageSize
	 *            每页数据条数
	 */
	public PaginationDataWrapper(List<T> dataProvider, int pageSize) {
		if (pageSize <= 0)
			throw new RuntimeException("分页大小不能小于0");

		this.dataProvider = (dataProvider == null ? new ArrayList<T>() : dataProvider);
		this.pageSize = pageSize;

		pageStartIndex = 0;
	}

	/**
	 * 获取内部封装的数据源List
	 * @return
	 */
	public List<T> getDataProvider() {
		return this.dataProvider;
	}
	
	/**
	 * 获取分页大小
	 * 
	 * @return
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 获取数据总条数.
	 * 
	 * @return 数据总条数
	 */
	public int getTotalDataSize() {
		return dataProvider == null ? 0 : dataProvider.size();
	}

	/**
	 * 判断是否存在下一页.
	 * 
	 * @return true存在下一页、false不存在
	 */
	public boolean hasNextPage() {
		return getTotalDataSize() > 0 && getTotalDataSize() > pageStartIndex + pageSize;
	}

	/**
	 * 判断是否存在前一页.
	 * 
	 * @return true存在前一页、false不存在
	 */
	public boolean hasPrevPage() {
		return getTotalDataSize() > 0 && pageStartIndex >= pageSize;
	}

	/**
	 * 下一页
	 */
	public void nextPage() {
		if (hasNextPage())
			pageStartIndex += pageSize;
	}

	/**
	 * 前一页
	 */
	public void prevPage() {
		if (hasPrevPage())
			pageStartIndex -= pageSize;
	}

	/**
	 * 跳转到指定页数
	 * @param pageNo
	 */
	public void gotoPage(int pageNo) {
		if(pageNo >= getFirstPageNo() && pageNo <= getLastPageNo())
			pageStartIndex = (pageNo - 1) * pageSize;
	}
	
	/**
	 * 获取当前页数据
	 * 
	 * @return 当前页数据List
	 */
	public List<T> getCurrentPageDataList() {
		int pageEndIndex = pageStartIndex + pageSize;
		if (pageEndIndex > getTotalDataSize() - 1)
			pageEndIndex = getTotalDataSize();
		return dataProvider.subList(pageStartIndex, pageEndIndex);
	}

	/**
	 * 获取当前页码.
	 * 
	 * @return 当前页码.
	 */
	public int getCurrentPageNo() {
		return getTotalDataSize() > 0 ? pageStartIndex / pageSize + 1 : 0;
	}

	/**
	 * 获取第一页页码.
	 * 
	 * @return 第一页页码
	 */
	public int getFirstPageNo() {
		return getTotalDataSize() > 0 ? 1 : 0;
	}

	/**
	 * 获取最后一页页码.
	 * 
	 * @return 最后一页页码
	 */
	public int getLastPageNo() {
		return getTotalDataSize() % pageSize == 0 ? getTotalDataSize() / pageSize
				: getTotalDataSize() / pageSize + 1;
	}

	/**
	 * 判断当前页是否为第一页
	 * 
	 * @return true是第一页,false不是
	 */
	public boolean isFirstPage() {
		return getTotalDataSize() > 0 && getCurrentPageNo() == getFirstPageNo();
	}

	/**
	 * 判断是否为最后一页
	 * 
	 * @return true是最后一页,false不是
	 */
	public boolean isLastPage() {
		return getTotalDataSize() > 0 && getCurrentPageNo() == getLastPageNo();
	}

	/**
	 * 调试使用
	 */
	public void debugStatus() {
		debugStatus(null);
	}
	
	/**
	 * 测试使用
	 * @param printWriter
	 */
	public void debugStatus(PrintWriter printWriter) {
		PrintWriter pw = printWriter;
		if(pw == null)
			pw = new PrintWriter(System.out);
		JSONObject jsonObject = JSONObject.fromObject(this);
		pw.print(this.getClass().getSimpleName());
		pw.print(" properties snapshot: ");
		pw.println(jsonObject.toString(4));
		pw.flush();
	}
	
	/**
	 * 测试类
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> data = new ArrayList<String>();
		data.add(" x ");
		data.add(" y ");
		data.add(" z ");
		data.add(" a ");
		data.add(" b ");
		data.add(" c ");
		PaginationDataWrapper<String> wrapper = new PaginationDataWrapper<String>(
				data, 3);
		wrapper.debugStatus();

		wrapper.nextPage();
		wrapper.debugStatus();

		wrapper.nextPage();
		wrapper.debugStatus();

		wrapper.prevPage();
		wrapper.debugStatus();

		wrapper.prevPage();
		wrapper.debugStatus();

		wrapper.prevPage();
		wrapper.debugStatus();

	}

}
