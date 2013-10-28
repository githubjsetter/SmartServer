package com.smart.bi.client.design;

public class Splitpageinfo {
	/**
	 * 数据开始行
	 */
	public int startrow;
	/**
	 * 数据结束行
	 */
	public int endrow;

	public int getRowcount() {
		return endrow - startrow + 1;
	}
}
